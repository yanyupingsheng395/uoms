var sms_validator;
$(function () {
    validate();
    statInputNum($("#smsContent"),$("#word"));
    statInputNum($("#smsContent1"),$("#word1"));
});
// 新增文案
function add() {
    $("#msg_modal").modal('hide');
    setUserGroupChecked();
    $('#smsCode').val("");
    $('#smsContent').val("");
    $('#smsName').val("");
    $('#remark').val("");
    $("input[name='isCoupon']:radio").removeAttr("checked").removeAttr("disabled");
    $("input[name='isProductName']:radio").removeAttr("checked").removeAttr("disabled");
    $("input[name='isProductUrl']:radio").removeAttr("checked").removeAttr("disabled");
    $("#word").text("0");
    $("#fontNum").val('');
    $("#myLargeModalLabel3").text("新增文案");
    $("#btn_save_sms").attr("name", "save");
    $('#add_modal').modal('show');
    $("#smsTemplateAddForm").validate().resetForm();
}

// 新增文案的时候，给群组信息设置默认选中和只读
function setUserGroupChecked() {
    var groupInfo = $("#currentGroupInfo").val();
    var groupInfoArr = groupInfo.split("|");
    groupInfoArr.forEach((v,k)=>{
        if(k===0) {
            $("input[name='userValue']:checkbox[value='" + v + "']").prop("checked", true).attr('disabled', 'disabled');
        }
        if(k===1) {
            $("input[name='lifeCycle']:checkbox[value='" + v + "']").prop("checked", true).attr('disabled', 'disabled');
        }
        if(k===2) {
            $("input[name='pathActive']:checkbox[value='" + v + "']").prop("checked", true).attr('disabled', 'disabled');
        }
    });
}

function clearUserGroupDisabled() {
    $("input[name='userValue']").removeAttr("disabled");
    $("input[name='lifeCycle']").removeAttr("disabled");
    $("input[name='pathActive']").removeAttr("disabled");
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
        $MB.n_warning('请选择需要测试的文案！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];

    //根据获取到的数据查询
    $.getJSON("/smsTemplate/getSmsTemplateNotValid?smsCode="+smsCode,function (resp) {
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

$("#send_modal").on('hidden.bs.modal', function () {
    $('#msg_modal').modal('show');
});

function sendMessage()
{
    //验证
    var smsContent= $('#smsContent1').val();

    if($('input[name="phoneNum"]').eq(0).val() === '' && $('input[name="phoneNum"]').eq(1).val() === ''&& $('input[name="phoneNum"]').eq(2).val()=== '')
    {
        $MB.n_warning("手机号不能为空！");
        return;
    }
    var phoneNum=[];
    if($('input[name="phoneNum"]').eq(0).val() !== '') {
        phoneNum.push($('input[name="phoneNum"]').eq(0).val());
    }
    if($('input[name="phoneNum"]').eq(1).val() !== '') {
        phoneNum.push($('input[name="phoneNum"]').eq(1).val());
    }
    if($('input[name="phoneNum"]').eq(2).val() !== '') {
        phoneNum.push($('input[name="phoneNum"]').eq(2).val());
    }
    phoneNum = phoneNum.join(',');
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
                var groupId = $("#currentGroupId").val();
                smsTemplateTable(groupId);
            }else {
                $MB.n_danger(resp.msg);
            }
        })
    });
}

// 字数统计
$("#btn_cal").click(function () {
    var data = getSmsContentFontCount();
    $("#fontNum").val(data.count + "个字");
});

function validCouponSendType(isCoupon) {
    let couponSendType = getCouponSendType();
    let smsContent = $('#smsContent').val();
    let isProductUrl = $("input[name='isProductUrl']:checked").val();
    let isProductName = $("input[name='isProductName']:checked").val();
    if(smsContent === '') {
        $MB.n_warning("文案内容不能为空！");
        return false;
    }
    if(couponSendType === 'A') { // 包含${COUPON_URL}
        // 不能包含url
        if(isCoupon === '0') {
            if(smsContent.indexOf("${COUPON_URL}") > -1) {
                $MB.n_warning("选择'不体现补贴链接与商品'，文案内容不能出现${COUPON_URL}");
                return false;
            }
            if(smsContent.indexOf("${COUPON_NAME}") > -1) {
                $MB.n_warning("选择'不体现补贴链接与商品'，文案内容不能出现${COUPON_NAME}");
                return false;
            }
        }

        // 不能包含url
        if(isCoupon === '1') {
            if(smsContent.indexOf("${COUPON_URL}") === -1) {
                $MB.n_warning("补贴发放方式为自行领取，模板内容未发现${COUPON_URL}");
                return false;
            }
            if(smsContent.indexOf("${COUPON_NAME}") === -1) {
                $MB.n_warning("补贴发放方式为自行领取，模板内容未发现${COUPON_NAME}");
                return false;
            }
        }
    }
    if(couponSendType === 'B') { // 不包含${COUPON_URL}
        if(smsContent.indexOf("${COUPON_URL}") > -1) {
            $MB.n_warning("补贴发放方式为系统发送，模板内容不能出现${COUPON_URL}");
            return false;
        }
        if(smsContent.indexOf("${COUPON_NAME}") > -1) {
            $MB.n_warning("补贴发放方式为系统发送，模板内容不能出现${COUPON_NAME}");
            return false;
        }
        if(isCoupon === '1') {
            $MB.n_warning("补贴发放方式为系统发送，'体现补贴链接与名称'不能为'是'");
            return;
        }
    }

    if(isProductUrl === '1') {
        if(smsContent.indexOf("${PROD_URL}") === -1) {
            $MB.n_warning("选择'体现推荐商品的详情页'，文案内容未发现${PROD_URL}");
            return false;
        }
    }
    if(isProductUrl === '0') {
        if(smsContent.indexOf("${PROD_URL}") > -1) {
            $MB.n_warning("选择'不体现推荐商品的详情页'，文案内容不能出现${PROD_URL}");
            return false;
        }
    }
    if(isProductName === '1') {
        if(smsContent.indexOf("${PROD_NAME}") === -1) {
            $MB.n_warning("选择'体现推荐商品名称'，文案内容未发现${PROD_NAME}");
            return false;
        }
    }
    if(isProductName === '0') {
        if(smsContent.indexOf("${PROD_NAME}") > -1) {
            $MB.n_warning("选择'不体现推荐商品名称'，文案内容不能出现${PROD_NAME}");
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
$("#btn_save_sms").click(function () {
    let flag = sms_validator.form();
    if(flag) {
        var alert_str = "";
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
            content: sure_msg
        }, function () {
            clearUserGroupDisabled();
            var data = $("#smsTemplateAddForm").serialize();
            setUserGroupChecked();
            $.post(url, data, function (r) {
                if(r.code === 200) {
                    $MB.n_success(success_msg);
                }else {
                    $MB.n_danger(r.msg);
                }
                $('#add_modal').modal('hide');
                $('#smsTemplateTable').bootstrapTable('refresh');
                $("#msg_modal").modal('show');
            });
        });
    }
});
// 表单验证
function validate() {
    let icon = "<i class='fa fa-times-circle'></i> ";
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
    sms_validator = $("#smsTemplateAddForm").validate(rule);
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
    $('#smsContentInput').val("");
    $("input[name='userValue']").removeAttr("disabled");
    $("input[name='userValue']:checked").removeAttr("checked");
    $("input[name='lifeCycle']").removeAttr("disabled");
    $("input[name='lifeCycle']:checked").removeAttr("checked");
    $("input[name='pathActive']").removeAttr("disabled");
    $("input[name='pathActive']:checked").removeAttr("checked");
    $("#word").val('0');
    $("#fontNum").val('');
    $("#remark").val('');
    $("#smsTemplateAddForm").validate().resetForm();
    $("#msg_modal").modal('show');
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
        $MB.n_warning('请选择需要编辑的文案！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];
    $.getJSON("/smsTemplate/getSmsTemplate?smsCode="+smsCode,function (resp) {
        if (resp.code === 200){
            $("#msg_modal").modal('hide');
            var data = resp.data;
            $("#smsCode").val(data.smsCode);
            $("#smsName").val(data.smsName);
            $("#smsContent").val(data.smsContent);
            $("#smsContentInput").val(data.smsContent);
            statInputNum($("#smsContent"),$("#word"));
            $("input[name='isCoupon']:radio[value='" + data.isCoupon + "']").prop("checked", true);
            $("input[name='isProductName']:radio[value='" + data.isProductName + "']").prop("checked", true);
            $("input[name='isProductUrl']:radio[value='" + data.isProductUrl + "']").prop("checked", true);
            $("#remark").val(data.remark);
            $("#myLargeModalLabel3").text("修改文案");
            $("#btn_save_sms").attr("name", "update");
            $("#add_modal").modal('show');

            if(data.userValue !== null) {
                data.userValue.split(',').forEach((v,k)=>{
                    $("input[name='userValue']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }
            if(data.lifeCycle !== null) {
                data.lifeCycle.split(',').forEach((v,k)=>{
                    $("input[name='lifeCycle']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }
            if(data.pathActive !== null) {
                data.pathActive.split(',').forEach((v,k)=>{
                    $("input[name='pathActive']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }

            var groupInfo = $("#currentGroupInfo").val();
            var groupInfoArr = groupInfo.split("|");
            groupInfoArr.forEach((v,k)=>{
                if(k===0) {
                    $("input[name='userValue']:checkbox[value='" + v + "']").prop("checked", true).attr('disabled', 'disabled');
                }
                if(k===1) {
                    $("input[name='lifeCycle']:checkbox[value='" + v + "']").prop("checked", true).attr('disabled', 'disabled');
                }
                if(k===2) {
                    $("input[name='pathActive']:checkbox[value='" + v + "']").prop("checked", true).attr('disabled', 'disabled');
                }
            });

        }else {
            $MB.n_danger(resp.msg);
        }
    });
});