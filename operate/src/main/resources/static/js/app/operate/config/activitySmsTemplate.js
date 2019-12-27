$(function () {
    var settings = {
        url: "/activityTemplate/list",
        method: 'post',
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 25,            //每页的记录行数（*）
        pageList: [25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit )+ 1,  //页码
                param: {code: $("input[name='code']").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'code',
            title: '模板编码',
            valign: 'middle',
            align: 'center'
        }, {
            field: 'content',
            title: '活动模板'
        }, {
            field: 'contentNormal',
            title: '常规模版'
        }]
    };
    $MB.initTable('templateTable', settings);
    //为刷新按钮绑定事件
    $("#btn_refresh").on("click",function () {
        $('#templateTable').bootstrapTable('refresh');
    });
    statInputNum($("#content"),$("#word_activity"));
    statInputNum($("#contentNormal"),$("#word_normal"));
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
    let selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择需要测试的模板！');
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

    let param = new Object();
    param.phoneNum=phoneNum;
    param.smsContent=smsContent;

    $.ajax({
        url: "/smsTemplate/testSend",
        data: param,
        type: 'POST',
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
    var selectRows=$("#templateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择要删除的模板！');
        return;
    }

    var code =selectRows[0]["code"];

    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除短信模板？'
    }, function () {
        $.getJSON("/activityTemplate/deleteTemplate?code="+code,function (resp) {
            if (resp.code === 200){
                //提示成功
                $MB.n_success('删除成功！');
                //刷新表格
                $('#templateTable').bootstrapTable('refresh');
            }else {
                $MB.n_danger(resp.msg);
            }
        })
    });
}

function add() {
    $('#code').val("").removeAttr("readOnly");
    $('#content').val("");
    $('#contentNormal').val("");
    $("#word_activity").text("0");
    $("#word_normal").text("0");
    $("#myLargeModalLabel3").text("新增模板");
    $("#btn_save").attr("name", "save");
    $('#add_modal').modal('show');
}

// 新增&修改
$("#btn_save").click(function () {
    var alert_str="";

    //验证
    var code= $('#code').val();
    var content= $('#content').val();
    var contentNormal= $('#contentNormal').val();

    if(null==code||code=='')
    {
        alert_str+='模板编码不能为空！';
    }

    if(null==content||content=='')
    {
        alert_str+='活动模板内容不能为空！';
    }

    if(null==contentNormal||contentNormal==' ')
    {
        alert_str+='常规模版内容不能为空！';
    }

    if(null!=alert_str&&alert_str!='')
    {
        $MB.n_warning(alert_str);
        return;
    }

    var url = "";
    var success_msg = "";
    var sure_msg = "";
    var flag = false;
    if($(this).attr("name") == "save") {
        $.ajax({
            url:"/activityTemplate/checkCode",
            data:{code: code},
            async: false,
            success: function (r) {
                if(r.code === 200) {
                    if(r.data != '0') {
                        $MB.n_warning("模板编码已存在！");
                        flag = true;
                    }
                }
            }
        });
        if(flag) return;
        url = "/activityTemplate/addTemplate";
        success_msg = "新增成功！";
        sure_msg = "确定要保存数据？";
    }else {
        url = "/activityTemplate/updateTemplate";
        success_msg = "修改成功！";
        sure_msg = "确定要修改数据？";
    }

    //提示是否要保存
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认？'
    }, function () {
        var param = new Object();
        param.code=code;
        param.content=content;
        param.contentNormal = contentNormal;

        $.ajax({
            url: url,
            data: JSON.stringify(param),
            type: 'POST',
            contentType: "application/json;charset=utf-8",
            success: function (r) {
                if(r.code === 200) {
                    $MB.n_success(success_msg);
                }else {
                    $MB.n_danger("未知错误发生，请检查！");
                }
                $('#add_modal').modal('hide');
                $('#templateTable').bootstrapTable('refresh');
            }
        });
    });

});

$("#add_modal").on('hidden.bs.modal', function () {
    $('#code').val("");
    $('#content').val("");
    $('#contentNormal').val("");
});


function searchSmsTemplate() {
    $MB.refreshTable('templateTable');
}

function resetSmsTemplate() {
    $("input[name='code']").val("");
    $MB.refreshTable('templateTable')
}

$("#btn_edit").click(function () {
    var selectRows=$("#templateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择要修改的模板！');
        return;
    }

    var code =selectRows[0]["code"];
    $.getJSON("/activityTemplate/getTemplate?code="+code,function (resp) {
        if (resp.code === 200){
            var data = resp.data;
            $("#code").val(data.code).attr("readOnly", true);
            $("#content").val(data.content);
            $("#contentNormal").val(data.contentNormal);
            statInputNum($("#content"),$("#word_activity"));
            statInputNum($("#contentNormal"),$("#word_normal"));
            $("#myLargeModalLabel3").text("修改模板");
            $("#btn_save").attr("name", "update");
            $("#add_modal").modal('show');
        }
    });
});