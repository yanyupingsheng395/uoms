let validator;
$(function () {
    validate();
    initTable();
    statInputNum($("#smsContent"),$("#word"));
    statInputNum($("#smsContent1"),$("#word1"));
});

//为刷新按钮绑定事件
$("#btn_refresh").on("click",function () {
    $('#smsTemplateTable').bootstrapTable('refresh');
});

function initTable() {
    var settings = {
        url: "/smsTemplate/list",
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
                pageNum: (params.offset / params.limit )+ 1  //页码
            };
        },
        columns: [[{
            checkbox: true,
            rowspan: 2
        }, {
            field: 'smsName',
            title: '文案名称',
            rowspan: 2,
            valign: "middle"
        }, {
            title: '适用人群',
            colspan: 4
        },  {
            field: 'smsContent',
            title: '文案内容',
            rowspan: 2,
            valign: "middle"
        }], [{
            field: 'userValue',
            title: '用户在类目的价值',
        }, {
            field: 'userLifeCycle',
            title: '用户在类目上的生命周期阶段',
        }, {
            field: 'userValue',
            title: '用户在类目特定购买次序的活跃度',
        }, {
            field: 'isCoupon',
            title: '有无补贴',
            formatter: function (value, row, index) {
                let res = "-";
                if(value == '1') {
                    res = "有";
                }
                if(value == '0') {
                    res = "无";
                }
                return res;
            }
        }]]
    };
    $MB.initTable('smsTemplateTable', settings);
}

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
            $("#smsName1").val(resp.data.smsName);
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
    var selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择要删除的文案！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];

    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除当前文案？'
    }, function () {
        $.getJSON("/smsTemplate/deleteSmsTemplate?smsCode="+smsCode,function (resp) {
            if (resp.code === 200){
                //提示成功
                $MB.n_success('删除成功！');
                //刷新表格
                $('#smsTemplateTable').bootstrapTable('refresh');
            }else {
                $MB.n_danger(resp.msg);
            }
        })
    });
}

function add() {
    $('#smsCode').val("");
    $('#smsContent').val("");
    $('#smsName').val("");
    $('#remark').val("");
    $("input[name='isCoupon']:radio").removeAttr("checked");
    $("input[name='isProductName']:radio").removeAttr("checked");
    $("input[name='isProductUrl']:radio").removeAttr("checked");
    $("#word").text("0");
    $("#fontNum").val('');
    $("#myLargeModalLabel3").text("新增文案");
    $("#btn_save").attr("name", "save");
    $('#add_modal').modal('show');
    $("#smsTemplateAddForm").validate().resetForm();
}

// 字数统计
$("#btn_cal").click(function () {
    var data = getSmsContentFontCount();
    $("#fontNum").val(data.count + "个字");
});

function validCouponSendType(isCoupon) {
    let couponSendType = getCouponSendType();
    let smsContent = $('#smsContent').val();
    if(smsContent === '') {
        $MB.n_warning("模板内容不能为空！");
        return false;
    }
    if(couponSendType === 'A') { // 包含${COUPON_URL}
        // 不能包含url
        if(isCoupon === '0') {
            if(smsContent.indexOf("${COUPON_URL}") > -1) {
                $MB.n_warning("选择不包含券，模板内容不能出现${COUPON_URL}");
                return false;
            }
        }

        // 不能包含url
        if(isCoupon === '1') {
            if(smsContent.indexOf("${COUPON_URL}") === -1) {
                $MB.n_warning("优惠券发放方式为自行领取，模板内容未发现${COUPON_URL}");
                return false;
            }
        }
    }
    if(couponSendType === 'B') { // 不包含${COUPON_URL}
        if(smsContent.indexOf("${COUPON_URL}") > -1) {
            $MB.n_warning("优惠券发放方式为系统发送，模板内容发现${COUPON_URL}");
            return false;
        }
    }
    return true;
}

// 获取优惠券发送方式
function getCouponSendType() {
    var res;
    $.ajax({
        url: "/smsTemplate/getCouponSendType",
        method: "get",
        async: false,
        success: function (r) {
            res = r.data;
        }
    });
    return res;
}

function getSmsContentFontCount() {
    let smsContent = $('#smsContent').val();
    var res;
    $.ajax({
        url: "/smsTemplate/calFontNum",
        data: {smsContent: smsContent},
        method: "get",
        async: false,
        success: function (r) {
            res = r.data;
        }
    });
    return res;
}

// 新增&修改
$("#btn_save").click(function () {
    let flag = validator.form();
    if(flag) {
        var alert_str;
        var isCoupon = $("input[name='isCoupon']:checked").val();
        if(!validCouponSendType(isCoupon)) {
            return;
        }

        var data = getSmsContentFontCount();
        if(!data.valid) {
            alert_str+='文案字数超出最大限制！';
        }

        if(null!=alert_str&&alert_str!='')
        {
            $MB.n_warning(alert_str);
            return;
        }
        var url = "";
        var success_msg = "";
        var sure_msg = "";
        if($(this).attr("name") == "save") {
            url = "/smsTemplate/addSmsTemplate";
            success_msg = "新增成功！";
            sure_msg = "确定要保存数据？";
        }else {
            url = "/smsTemplate/updateSmsTemplate";
            success_msg = "修改成功！";
            sure_msg = "确定要修改数据？";
        }


        //提示是否要保存
        $MB.confirm({
            title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
            content: '确认提交数据？'
        }, function () {

            $.post(url, $("#smsTemplateAddForm").serialize(), function (r) {
                if(r.code === 200) {
                    $MB.n_success(success_msg);
                }else {
                    $MB.n_danger("未知错误发生，请检查！");
                }
                $('#add_modal').modal('hide');
                $('#smsTemplateTable').bootstrapTable('refresh');
            });
        });
    }
});
// 表单验证
function validate() {
    let icon = "<i class='fa fa-close'></i> ";
    let rule = {
        rules: {
            smsName: {
                required: true
            },
            smsContentInput: {
                required: true
            },
            isCoupon: {
                required: true
            },
            isProductName: {
                required: true
            },
            isProductUrl: {
                required: true
            }
        },
        errorPlacement: function (error, element) {
            if (element.is(":checkbox") || element.is(":radio")) {
                error.appendTo(element.parent().parent());
            } else {
                error.insertAfter(element);
            }
        },
        messages: {
            smsName: {
                required: icon + "请输入文案名称"
            },
            smsContentInput: {
                required: icon + "请输入文案内容"
            },
            isCoupon: {
                required: icon + "请选择"
            },
            isProductName: {
                required: icon + "请选择"
            },
            isProductUrl: {
                required: icon + "请选择"
            }
        }
    };
    validator = $("#smsTemplateAddForm").validate(rule);
}

//
function smsContentValid() {
    $('#smsContentInput').val($('#smsContent').val());
    if($('#smsContentInput').val() !== '') {
        $('#smsContentInput').removeClass('error');
        $("#smsContentInput-error").remove();
    }
}

$("#add_modal").on('hidden.bs.modal', function () {
    $('#smsCode').val("");
    $('#smsContent').val("");
    $("input[name='isCoupon']").removeAttr("disabled");
});

// 文案检索
function searchSmsTemplate() {
    $MB.refreshTable('smsTemplateTable');
}

function resetSmsTemplate() {
    $("input[name='smsCode']").val("");
    $MB.refreshTable('smsTemplateTable')
}

$("#btn_edit").click(function () {
    var selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择要修改的文案！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];
    $.getJSON("/smsTemplate/getSmsTemplate?smsCode="+smsCode,function (resp) {
        if (resp.code === 200){
            var data = resp.data;
            $("#smsCode").val(data.smsCode);
            $("#smsName").val(data.smsName);
            $("#smsContent").val(data.smsContent);
            statInputNum($("#smsContent"),$("#word"));
            $("input[name='isCoupon']:radio[value='" + data.isCoupon + "']").prop("checked", true);
            $("input[name='isCoupon']").attr("disabled", "disabled");
            $("input[name='isProductName']:radio[value='" + data.isProductName + "']").prop("checked", true);
            $("input[name='isProductName']").attr("disabled", "disabled");
            $("input[name='isProductUrl']:radio[value='" + data.isProductUrl + "']").prop("checked", true);
            $("input[name='isProductUrl']").attr("disabled", "disabled");
            $("#remark").val(data.remark);
            $("#myLargeModalLabel3").text("修改文案");
            $("#btn_save").attr("name", "update");
            $("#add_modal").modal('show');
        }
    });
});