$(function () {
    var settings = {
        url: "/smsTemplate/list",
        method: 'post',
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit )+ 1,  //页码
                param: {smsCode: $("input[name='smsCode']").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'smsCode',
            title: '模板编码'
        }, {
            field: 'smsContent',
            title: '模板内容'
        }]
    };
    $MB.initTable('smsTemplateTable', settings);
    //为刷新按钮绑定事件
    $("#btn_refresh").on("click",function () {
        $('#smsTemplateTable').bootstrapTable('refresh');
    });

});

function del() {
    var smsCode = $("#smsTemplateTable").bootstrapTable('getSelections')[0]["smsCode"];

    if(null==smsCode||''==smsCode)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择要删除的模板！');
        return;
    }

    //遮罩层打开
    lightyear.loading('show');

    //进行删除提示
        $.confirm({
            title: '提示：',
            content: '是否删除数据？',
            buttons: {
                confirm: {
                    text: '确认',
                    btnClass: 'btn-blue',
                    action: function(){
                             $.getJSON("/smsTemplate/deleteSmsTemplate?smsCode="+smsCode,function (resp) {
                                    if (resp.code === 200){
                                        lightyear.loading('hide');
                                        //提示成功
                                        //刷新表格
                                        $('#smsTemplateTable').bootstrapTable('refresh');
                                    }
                                })
                    }
                },
                cancel: {
                    text: '取消'
                }
            }
        });

}




function add() {
    $('#smsCode').val("");
    $('#smsContent').val("");
    $('#add_modal').modal('show');

}

function saveSmsTemplate()
{
    var alert_str="";

    //验证
    var smsCode= $('#smsCode').val();
    var smsContent= $('#smsContent').val();

    if(null==smsCode||smsCode=='')
    {
        alert_str+='模板编码不能为空！';
    }

    if(null==smsContent||smsContent=='')
    {
        alert_str+='模板内容不能为空！';
    }

    if(null!=alert_str&&alert_str!='')
    {
        $MB.n_warning(alert_str);
        return;
    }

    //提示是否要保存
        $.confirm({
            title: '确认',
            content: '确定要保存数据？',
            theme: 'bootstrap',
            type: 'orange',
            buttons: {
                confirm: {
                    text: '确认',
                    btnClass: 'btn-blue',
                    action: function(){
                        var param = new Object();
                        param.smsCode=smsCode;
                        param.smsContent=smsContent;

                        $.ajax({
                            url: "/smsTemplate/addSmsTemplate",
                            data: JSON.stringify(param),
                            type: 'POST',
                            contentType: "application/json;charset=utf-8",
                            success: function (r) {
                                $('#add_modal').modal('hide');
                                $MB.n_success("保存成功!");
                                $('#smsTemplateTable').bootstrapTable('refresh');
                            }
                        });

                    }
                },
                cancel: {
                    text: '取消'
                }
            }
        });
}

$("#add_modal").on('hidden.bs.modal', function () {
    $('#smsCode').val("");
    $('#smsContent').val("");
});


function searchSmsTemplate() {
    $MB.refreshTable('smsTemplateTable');
}

function resetSmsTemplate() {
    $("input[name='smsCode']").val("");
    $MB.refreshTable('smsTemplateTable')
}