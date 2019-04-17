String.prototype.replaceAll = function(s1,s2){
    return this.replace(new RegExp(s1,"gm"),s2);
}

$(function () {
    //为提交分析按钮绑定事件
    $("#submitAnalysis").on("click",submit_analysis);

    //为删除维度绑定事件
    $("#dimlist").on("click",'.dim-remove',function () {
        $(this).closest('li').remove();
    });

    $("#dimselectlist").change(function() {
        var dimCode = $(this).find("option:selected").val();
        getValueList(dimCode);
    });
});

function submit_analysis(){
    var alert_str="";
    toastr.options = {
        "closeButton": true,
        "progressBar": true,
        "positionClass": "toast-top-center",
        "preventDuplicates": true,
        "timeOut": 5000,
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }

    //进行校验
    var zb=$("#zhibiao").val();
    if(zb=='0')
    {
        alert_str+='请先选择一个指标！';
    }

    //校验至少选择了一个维度
    var dimlist=$("#dimlist").find("li");
    if(null==dimlist||dimlist.length==0)
    {
        alert_str+='</br>请至少选择一组维度及其值！';
    }

    //校验选择了时间区间
    var start_dt=$("#start_dt").val();
    var end_dt=$("#end_dt").val();

    if(null==start_dt||start_dt==''||null==end_dt||end_dt=='')
    {
        alert_str+='</br>请选择发生时间区间！';
    }else
    {
        if(start_dt>=end_dt)
        {
            alert_str+='</br>结束时间必须大于开始时间！';
        }
    }

    //校验选择了模板
    var ck=$("#template_select").find(":checkbox:checked");

    if(null==ck||ck.length==0)
    {

        alert_str+='</br>请至少选择一个模板！';
    }

    if(null!=alert_str&&alert_str!='')
    {
        toastr.warning(alert_str);
        return;
    }

    var array_template=[];
    ck.each(function (i) {
        array_template.push($(this).val());
    });

    var dim=[];

     //遍历已选择的维度
    dimlist.each(function (i) {
        var temp=$(this).children(".dimKey").get(0).value+"^"+$(this).children(".dimValues").get(0).value+"^"+$(this).children(".dimDispaly").get(0).value;
        dim.push(temp);

    });

    var datas={
        kpi:zb,
        startDt:start_dt,
        endDt:end_dt,
        period:$("#period").val(),
        templates:array_template,
        dims:dim
    };

    //遮罩层打开
    lightyear.loading('show');

    $("#submitAnalysis").attr('disabled',true);
    //向服务端提交数据
    $.ajax({
        type: "post",
        url: "/reason/submitAnalysis",
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
                                location.replace("/reason/gotoIndex");
                            }
                        }
                    }
                });


            }
        }
    });

}

function selectDim()
{
    //获取当前页面已经选择值
    var dimlist=$("#dimlist").find("li");
    var dim=[];

    //遍历已选择的维度
    dimlist.each(function (i) {
        var temp=$(this).children(".dimKey").get(0).value;
        dim.push(temp);
    });

    var code='';
    //为维度值选择框动态绑定值
    $.getJSON("/reason/getReasonDimList", function (resp) {
        if (resp.code == 200) {
            //遍历值 然后排除掉已经选择的值
            $.each(resp.data,function (k,v) {
                //判断当前元素是否在已选择列表中
                 if($.inArray(k,dim)==-1)  //找到
                 {
                     code += "<option value='" + k + "'> " + v + " </option>";
                 }
            });

            $("#dimselectlist").html("").html(code);
            $("#dimselectlist").selectpicker('refresh');


            getValueList($("#dimselectlist").find("option:selected").val());
        }
    });

    //弹开对话框
    $("#dim_modal").modal('show');

}

function getValueList(dimCode) {
    $.get("/reason/getReasonDimValuesList", {dimCode: dimCode}, function(r) {
        var code = "";
        $.each(r.data, function (k, v) {
            code += "<option value='" + k + "'>" + v + "</option>";
        });
        $("#dimvalueselectlist").html("").html(code);
        $("#dimvalueselectlist").selectpicker('refresh');
    });
}

function saveDim() {
    var alert_str='';
    toastr.options = {
        "closeButton": true,
        "progressBar": true,
        "positionClass": "toast-top-center",
        "preventDuplicates": true,
        "timeOut": 5000,
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }

    //不能不空
    var selectDim=$("#dimselectlist").find("option:selected").val();

    var dimvaluelist=$("#dimvalueselectlist").find("option:selected");

    if(null==selectDim||selectDim=='')
    {
        alert_str+='</br>请选择维度！';
    }
    if(null==dimvaluelist||dimvaluelist.length==0)
    {
        alert_str+='</br>请选择维度值！';
    }

    if(null!=alert_str&&alert_str!='')
    {
        toastr.warning(alert_str);
        return;
    }

    var selectDimLabel=$("#dimselectlist").find("option:selected").text();
    var selectDimValues=[];
    var selectDimValuesLabel=[];
    //保存所选维度
    //格式： <li class="list-group-item"><input  class="col-xs-11 dimDispaly" value="维度 : 新/用户 - 值:新|旧" style="border:0px" disabled="true" maxlength="150"/><input type="hidden" class="dimKey" value="neworold"/><input type="hidden" class="dimValues" value="new|old" /><span class="mdi mdi-delete" style="color: #006cfa;"></span></li>
    $.each(dimvaluelist,function (index,value) {
        selectDimValues.push(value.value);
        selectDimValuesLabel.push(value.text);
    });

    var inputValue='维度 : '+selectDimLabel+' - 值 : '+selectDimValuesLabel.join("|");
    var hiddenValue=selectDimValues.join("|");

    console.log(inputValue);
    var template="<li class='list-group-item'><input  class='col-xs-11 dimDispaly' value=\""+inputValue+"\"  title=\""+inputValue+"\" style='border:0px' disabled='true'/>" +
        "<input type='hidden' class='dimKey' value='"+selectDim+"'/><input type='hidden' class='dimValues' value='"+hiddenValue+"' /><span class='mdi mdi-delete dim-remove' style='color: #006cfa;'></span></li>";
    $("#dimlist").append(template);

    $("#dim_modal").modal('hide');
}