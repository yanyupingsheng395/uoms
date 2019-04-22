// 获取维度
function getDimensionAndVal() {
    $.get("/progress/getDiagDimList", null, function (r) {
        var code = "<option value=''>请选择</option>";
        $.each(r.data, function(k, v) {
            code += "<option value='" + k + "'> " + v + " </option>";
        });
        $("#reasonDimension").html("").html(code);
        $("#reasonDimension").selectpicker('refresh');
    });
}

// 获取值
$("#reasonDimension").change(function() {
    var val = $(this).find("option:selected").val();
    getValueList(val, "reasonDesVal");
});

var dimensions = new Array();
// 添加维度及值
function addDimensionAndVal() {
    var op1 = "reasonDimension";
    var op2 = "reasonDesVal";

    if($("#" + op1).find("option:selected").val() == "" || $("#" + op2).find("option:selected").val() == "") {
        toastr.warning("请选择维度或值！");
    }else {
        var arr = $("#" + op2).selectpicker('val');
        var condition = $("#" + op1).find("option:selected").text();
        var val = $("#" + op1).find("option:selected").val();
        var code = "<tr><td style='text-align: left;'>" + condition.trim() + ":";
        var conditionCodes = "";
        for(var i=0;i<arr.length;i++) {
            var t = $("#" + op2).find("option[value='"+arr[i]+"']").text();
            code += t + ",";
            conditionCodes += arr[i] + ","
        }
        code = code.substring(0, code.length - 1);
        conditionCodes = conditionCodes.substring(0, conditionCodes.length - 1);
        code += "<input name='dimValues' type='hidden' value='"+conditionCodes+"'><input name='dimKey' type='hidden' value='" + val + "'/></td><td><a style='color:#000000;cursor: pointer;' onclick='remove(\""+val+"\", this)'><i class='mdi mdi-close'></i></a></td></tr>";

        if($("#selectedTable").find("tr td").text().indexOf("暂无数据") > -1) {
            $("#selectedTable").html("").html(code);
        }else {
            $("#selectedTable").append(code);
        }

        // 列表中删除
        $("#" + op1).find("option[value='"+val+"']").remove();
        $("#" + op1).selectpicker('refresh');

        $("#" + op2).html("");
        $("#" + op2).selectpicker('refresh');

        dimensions.push(val);
    }
}

// 删除已选的维度及值
function remove(val, dom) {
    $(dom).parent().parent().remove();

    dimensions.forEach(function(item, index, arr){
        if(item == val) {
            dimensions.splice(index, 1);
        }
    });

    if(dimensions.length == 0) {
        $("#selectedTable").html("").html("<tr><td><i class=\"mdi mdi-alert-circle-outline\"></i>暂无数据！</td></tr>");
    }

    $.get("/progress/getDiagDimList", null, function (r) {
        var code = "";
        $.each(r.data, function(k, v) {
            if(dimensions.length != 0) {
                dimensions.forEach(function(item, index, arr){
                    if(item != k) {
                        code += "<option value='" + k + "'> " + v + " </option>";
                    }
                });
            }else {
                code += "<option value='" + k + "'> " + v + " </option>";
            }
        });
        $("#reasonDimension").html("").html("<option value=''>请选择</option>" + code);
        $("#reasonDimension").selectpicker('refresh');
    });
}

reasonModalInit();
function reasonModalInit() {
    // 获取维度和值
    getDimensionAndVal();

    // 初始化时间
    $("#reasonPeriod").val($("#periodType").find("option:selected").text());
    $("#reasonTimeBegin").val($("#beginDt").val());
    $("#reasonTimeEnd").val($("#endDt").val());

    // 提交分析
    $("#submitAnalysis").on("click",submit_analysis);
}

function submit_analysis(){
    var alert_str="";

    //进行校验
    if($("#selectedTable").find("tr td").text().indexOf("暂无数据") > -1) {
        alert_str+='请至少选择一组维度及其值！';
    }

    if(null!=alert_str&&alert_str!='')
    {
        toastr.warning(alert_str);
        return;
    }

    var dim=[];
    //遍历已选择的维度
    $("#selectedTable").find("tr").each(function (i) {
        var temp = $(this).find("td").find("input[name='dimKey']").val() + "^" + $(this).find("td").find("input[name='dimValues']").val() + "^" + $(this).find("td").text();
        dim.push(temp);
    });
    var start_dt = $("#beginDt").val();
    var end_dt = $("#endDt").val();
    var datas={
        kpi:$("#reasonKpiCode").val(),
        startDt:start_dt,
        endDt:end_dt,
        period:$("#periodType").find("option:selected").val(),
        source: '运营诊断',
        dims:dim
    };

    //遮罩层打开
    lightyear.loading('show');

    $("#submitAnalysis").attr('disabled',true);
    //向服务端提交数据
    $.ajax({
        type: "post",
        url: "/reason/submitAnalysisManual",
        data: JSON.stringify(datas),
        dataType: "json",
        headers: {
            'Content-Type': 'application/json'
        },
        success: function (r) {
            if (r.code === 200) {
                lightyear.loading('hide');
                $.confirm({
                    title: '成功',
                    content: "您提交的编号为【"+r.msg+"】的分析已在进行中，在列表页待完成后通过【查看】功能查看详情！",
                    type: 'green',
                    buttons: {
                        omg: {
                            text: '确定',
                            btnClass: 'btn-green',
                            action: function(){
                                //
                                $("#nodeReasonAddModal").modal('hide');
                            }
                        }
                    }
                });
            }
        }
    });
}