$( function () {
    getMessageList();
    validCoupon();
});

function getMessageList() {
    var settings = {
        url: "/qywxChatMsg/getMeList",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [ {
            checkbox: true,
        },{
            field: 'batchMsgId',
            title: 'ID',
            visible: false
        },{
            field: 'batchMsgName',
            title: '群发消息名称',
            align: 'center'
        }, {
            field: 'status',
            title: '状态',
            align: 'center',
            formatter: function (value, row, index) {
                if (value == 'todo') {
                    return "草稿";
                } else if (value == 'done') {
                    return "已执行";
                }else {
                    return "-";
                }
            }
        }, {
            field: 'insertBy',
            title: '创建人',
            align: 'center'
        }, {
            field: 'insertDt',
            title: '创建时间',
            align: 'center'
        }]
    };
    $MB.initTable( 'baseTable', settings );
}

function showSendTime() {
    $("#sendtime").show();
}
function closeSendTime() {
    $("#sendtime").hide();
    $("#sendDT").val("");
}

layui.use('laydate', function(){
    var laydate = layui.laydate;
    //日期时间选择器
    laydate.render({
        elem: '#test5'
        ,type: 'datetime'
    });
});

/**
 *键盘键入事件
 */
function smsContentValid() {
    $('#smsContentInput').val($('#textContent').val());
    if($('#smsContentInput').val() !== '') {
        $('#smsContentInput').removeClass('error');
        $("#smsContentInput-error").remove();
    }
    var content = $('#textContent').val() === "" ? "请输入短信内容": $('#textContent').val();
    $("#article").html('').append(content);
}

/**
 *进入新增消息界面
 */
$("#btn_save").click(function () {
    window.location.href="/page/goBaseMsg";
});

var validator_coupon;
function validCoupon() {
    var icon = "<i class='mdi mdi-close-circle'></i> ";
    var rule = {
        rules: {
            batchMsgName: {
                required: true
            },
            textContent: {
                required: true
            }
        },
        messages: {
            batchMsgName: {
                required: icon + "请输入群发名称"
            },
            textContent: {
                required: icon + "请输入群发消息内容"
            }
        }
    };
    validator_coupon = $("#smsTemplateAddForm").validate(rule);
}

function saveCouponData() {
    var flag = validator_coupon.form();
    if(flag) {
        $.post("/qywxChatMsg/saveData", $("#smsTemplateAddForm").serialize(), function (r) {
            if(r.code == 200) {
                $MB.n_success("保存成功！");
                window.location.href="/page/goChatMsgList";
            }else {
                $MB.n_danger("保存失败！");
            }
        });
    }
}

//删除
$("#btn_delete").click(function () {
    var selected = $( "#baseTable" ).bootstrapTable( 'getSelections' );
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        $MB.confirm( {
            title: "<i class='mdi mdi-alert-outline'></i>提示：",
            content: "确定删除选中的记录?"
        }, function () {
            $.post( "/qywxChatMsg/deleteById", {id: selected[0]['batchMsgId']}, function (r) {
                if (r.code == 200) {
                    $MB.n_success( "删除成功！" );
                    $MB.refreshTable( 'baseTable' );
                }
            } );
        } );
    }

});