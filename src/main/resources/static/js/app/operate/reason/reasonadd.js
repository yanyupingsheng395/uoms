$(function () {
    //为提交分析按钮绑定事件
    $("#submitAnalysis").on("click",submit_analysis);

    
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