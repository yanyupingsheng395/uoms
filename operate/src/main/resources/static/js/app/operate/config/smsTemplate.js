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
        }, {
            field: 'isCoupon',
            title: '是否有券',
            formatter: function (value, row, index) {
                let res = "-";
                if(value == '1') {
                    res = "是";
                }
                if(value == '0') {
                    res = "否";
                }
                return res;
            }
        }]
    };
    $MB.initTable('smsTemplateTable', settings);
    //为刷新按钮绑定事件
    $("#btn_refresh").on("click",function () {
        $('#smsTemplateTable').bootstrapTable('refresh');
    });

    statInputNum($("#smsContent"),$("#word"));
    statInputNum($("#smsContent1"),$("#word1"));
});

/**
 * 字数统计
 * @param textArea
 * @param numItem
 */
function statInputNum(textArea,numItem) {
    curLength = textArea.val().length;
    numItem.text(curLength);
    textArea.on('input propertychange', function () {
        var _value = $(this).val().replace(/\n/gi,"");
        numItem.text(_value.length);
    });
}


/**
 * 测试推送
 * @param smsCode
 */
function testSend()
{
    var selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择要删除的模板！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];

    //根据获取到的数据查询
    $.getJSON("/smsTemplate/getSmsTemplate?smsCode="+smsCode,function (resp) {
        if (resp.code === 200){
            //更新测试面板
            $("#smsCode1").val(resp.data.smsCode);
            $("#smsContent1").val(resp.data.smsContent);

            var _value = $("#smsContent1").val().replace(/\n/gi,"");
            $("#word1").text(_value.length);

            $('#send_modal').modal('show');
        }
    })

}

function sendMessage()
{
    //验证
    var phoneNum=$('#phoneNum').val();
    var smsContent= $('#smsContent1').val();

    if(null==phoneNum||phoneNum=='')
    {
        $MB.n_warning("手机号不能为空！");
        return;
    }

    if(null==smsContent||smsContent=='')
    {
        $MB.n_warning("模板内容不能为空！");
        return;
    }

    //判断是否含有变量
    if(smsContent.indexOf("$") >= 0 ) {
        $MB.n_warning("模板内容的变量请替换为实际值！");
        return;
    }

    //提交后端进行发送
    lightyear.loading('show');

    var param = new Object();
    param.phoneNum=phoneNum;
    param.smsContent=smsContent;

    $.ajax({
        url: "/smsTemplate/testSend",
        data: JSON.stringify(param),
        type: 'POST',
        contentType: "application/json;charset=utf-8",
        success: function (r) {
            lightyear.loading('hide');
            if(r.code==200)
            {
                $MB.n_success(r.msg);
            }else
            {
                $MB.n_danger(r.msg);
            }

        }
    });


}

function del() {
    var selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择要删除的模板！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];

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
                                        //提示成功
                                        $MB.n_success('删除成功！');
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