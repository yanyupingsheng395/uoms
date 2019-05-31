reasonModalInit();
function reasonModalInit() {
    // 初始化时间
    $("#reasonPeriod").val(getPeriodType());
    $("#reasonTimeBegin").val($("#beginDt").val());
    $("#reasonTimeEnd").val($("#endDt").val());

    // 提交分析
    $("#submitAnalysis").on("click",submit_analysis);
}

function getPeriodType() {
    var opDataType = $("#opDataType").val();
    var period = "";
    if(opDataType == "edit") {
        period = $("#periodType").val() == "M" ? "月":"日";
    }else {
        period = $("#periodType").find("option:selected").text();
    }
    return period;
}

function getPeriodTypeVal() {
    var opDataType = $("#opDataType").val();
    var period = "";
    if(opDataType == "edit") {
        period = $("#periodType").val();
    }else {
        period = $("#periodType").find("option:selected").val();
    }
    return period;
}

function submit_analysis(){
    var alert_str="";

    //进行校验
    // if($("#selectedTable").find("tr td").text().indexOf("暂无数据") > -1) {
    //     alert_str+='请至少选择一组维度及其值！';
    // }

    if(null!=alert_str&&alert_str!='')
    {
        toastr.warning(alert_str);
        return;
    }

    var dim=[];
    //遍历已选择的维度
    if($("#selectedTable").find("tr").find("td").text().indexOf("暂无数据") == -1) {
        $("#selectedTable").find("tr").each(function (i) {
            var temp = $(this).find("td").find("input[name='dimKey']").val() + "^" + $(this).find("td").find("input[name='dimValues']").val() + "^" + $(this).find("td").text();
            dim.push(temp);
        });
    }
    var start_dt = $("#beginDt").val();
    var end_dt = $("#endDt").val();
    var datas={
        kpi:$("#reasonKpiCode").val(),
        beginDt:start_dt,
        endDt:end_dt,
        period:getPeriodTypeVal(),
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
                    theme: 'bootstrap',
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