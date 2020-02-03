var validator;
var $couponForm = $("#coupon_edit");
init_date('validEnd', 'yyyy-mm-dd', 0,2,0);
$("#validEnd").datepicker('setStartDate', new Date());
$(function () {
    validateRule();
});
// 新增补贴
function couponAdd() {
    $('#coupon_modal').modal('hide');
    $('#coupon_add_modal').modal('show');
}

$("#coupon_add_modal").on('hidden.bs.modal', function () {
    closeModal();
});

// 关闭新增补贴
function closeModal() {
    var $form = $('#coupon_edit');
    $form.find("input[name='couponName']").val("").removeAttr("readOnly");
    $form.find("input[name='couponDenom']").val("").removeAttr("readOnly");
    $form.find("input[name='couponThreshold']").val("").removeAttr("readOnly");
    $form.find("input[name='couponInfo2']").val("").removeAttr("readOnly");
    $form.find("input[name='couponUrl']").val("").removeAttr("readOnly");
    $form.find("input[name='couponNum']").val("").removeAttr("readOnly");
    $form.find("input[name='couponDisplayName']").val("").removeAttr("readOnly");
    $form.find("input[name='validEnd']").val("").removeAttr("readOnly");
    $("input[name='validStatus']:radio[value='Y']").prop("checked", true);
    $("input[name='userValue']").removeAttr("disabled");
    $("input[name='userValue']:checked").removeAttr("checked");
    $("input[name='lifeCycle']").removeAttr("disabled");
    $("input[name='lifeCycle']:checked").removeAttr("checked");
    $("input[name='pathActive']").removeAttr("disabled");
    $("input[name='pathActive']:checked").removeAttr("checked");
    $MB.closeAndRestModal("coupon_add_modal");
    $("#couponValid").hide();
    $("#coupon_edit").validate().resetForm();
    $("#coupon_modal").modal('show');
    $("#btn_save_coupon").attr('name', 'save');
}

// 表单验证规则
function validateRule() {
    // 编辑补贴：不进行补贴名称的验证
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $couponForm.validate({
        rules: {
            couponDenom: {
                required: true,
                digits: true
            },
            couponThreshold: {
                required: true,
                digits: true
            },
            couponInfo2: {
                required: $("#validUrl").val() === 'A'
            },
            couponUrl: {
                required: $("#validUrl").val() === 'A'
            },
            couponDisplayName: {
                required: true,
                maxlength: couponNameLen,
                remote: {
                    url: "/coupon/checkCouponName",
                    type: "get",
                    dataType: "json",
                    data: {
                        couponDisplayName: function () {
                            return $("#couponDisplayName").val();
                        },
                        operate: function () {
                            return $("#btn_save_coupon").attr("name");
                        }
                    }
                }
            },
            validEnd: {
                required: true
            },
            couponNum: {
                required: true,
                digits: true
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
            couponDenom: {
                required: icon + "请输入面额",
                digits: icon + "只能是整数"
            },
            couponThreshold: {
                required: icon + "请输入门槛",
                digits: icon + "只能是整数"
            },
            couponInfo2: icon + "请输入长链",
            couponUrl: icon + "请输入短链",
            couponDisplayName: {
                required: icon + "请输入引用名",
                maxlength: icon + "最大长度不能超过"+couponNameLen+"个字符",
                remote: icon + "补贴名称已存在"
            },
            validEnd: icon + "请输入截止日期",
            couponNum: {
                required: icon + "请输入数量",
                digits: icon + "只能是整数"
            }
        }
    });
}

// 券保存
$("#btn_save_coupon").click(function () {
    var name = $(this).attr("name");
    var validator = $couponForm.validate();
    var flag = validator.form();
    if (flag) {
        clearUserGroupDisabled();
        var formData = $("#coupon_edit").serialize();
        setUserGroupChecked();
        if (name === "save") {
            $.post("/coupon/save", formData, function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success(r.msg);
                    $MB.refreshTable("couponTable");
                } else $MB.n_danger(r.msg);
            });
        }
        if (name === "update") {
            if(!validCoupon()) {
                $MB.n_warning("当前日期大于有效截止日期，'券是否有效'的更改无效！");
                return;
            }
            $.post("/coupon/update", formData, function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success(r.msg);
                    $MB.refreshTable("couponTable");
                } else $MB.n_danger(r.msg);
            });
        }
    }
});

/**
 * 获取短链
 */
function getShortUrl() {
    var url = $("#couponInfo2").val();
    if(url.trim() == "") {
        $MB.n_warning("长链不能为空！");
        return;
    }
    $.get("/coupon/getShortUrl", {url: url}, function(r) {
        if(r.code === 200) {
            $("#couponUrl").val(r.data);
        }else {
            $MB.n_danger(r['msg']);
        }
    });
}

// 去除日期的验证
function removeValid() {
    var validEnd = $("#validEnd").val();
    if(validEnd !== '') {
        $("#validEnd").removeClass('error');
        $("#validEnd-error").remove();
    }
}

// 编辑补贴
function updateCoupon() {
    $("#btn_save_coupon").attr("name", "update");
    var selected = $("#couponTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要修改的补贴！');
        return;
    }
    if(selected_length > 1) {
        $MB.n_warning("一次只能编辑一条记录！");
        return;
    }
    var couponId = selected[0].couponId;
    $("#couponValid").show();
    $.post("/coupon/getByCouponId", {"couponId": couponId}, function (r) {
        if (r.code === 200) {
            var $form = $('#coupon_edit');
            $("#coupon_add_modal").modal('show');
            $("#coupon_modal").modal('hide');
            var coupon = r.data;
            $("#myLargeModalLabel").html('修改补贴');
            $form.find("input[name='couponId']").val(coupon.couponId);
            $form.find("input[name='couponDenom']").val(coupon.couponDenom).attr("readonly", true);
            $form.find("input[name='couponThreshold']").val(coupon.couponThreshold).attr("readonly", true);
            $form.find("input[name='couponInfo2']").val(coupon.couponInfo2);
            $form.find("input[name='couponUrl']").val(coupon.couponUrl);
            $form.find("input[name='couponNum']").val(coupon.couponNum);
            $form.find("input[name='couponDisplayName']").val(coupon.couponDisplayName).attr("readonly", true);
            $form.find("input[name='validEnd']").val(coupon.validEnd);
            $("input[name='validStatus']:radio[value='"+coupon.validStatus+"']").prop("checked", true);

            if(coupon.userValue !== null) {
                coupon.userValue.split(',').forEach((v,k)=>{
                    $("input[name='userValue']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }
            if(coupon.lifeCycle !== null) {
                coupon.lifeCycle.split(',').forEach((v,k)=>{
                    $("input[name='lifeCycle']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }
            if(coupon.pathActive !== null) {
                coupon.pathActive.split(',').forEach((v,k)=>{
                    $("input[name='pathActive']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }
        } else {
            $MB.n_danger(r['msg']);
        }
    });
}

// 验证券是否有效
function validCoupon() {
    let validStatus = $("input[name='validStatus']:checked").val();
    if(validStatus === 'Y') {
        let validEnd = $("#validEnd").val();
        let date = new Date();
        let now = String(date.getFullYear()) + (date.getMonth()+1).toString().padStart(2,'0') + (date.getDate().toString().padStart(2,'0'));
        return Number(now) <= Number(validEnd);
    }
    return true;
}

$("#btn_delete_coupon").click(function () {
    var selected = $("#couponTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要删除的补贴！');
        return;
    }
    var couponId = [];
    selected.forEach((v, k) => {
        couponId.push(v['couponId']);
    });

    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除选中的补贴？'
    }, function () {
        $.post("/coupon/deleteCoupon", {"couponId": couponId.join(",")}, function (r) {
            if(r.code == 200) {
                $MB.n_success(r.msg);
            }else {
                $MB.n_danger(r.msg);
            }
            couponTable($("#currentGroupId").val());
        });
    });
});

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

// 保存或更新之前取消disabled效果
function clearUserGroupDisabled() {
    $("input[name='userValue']").removeAttr("disabled");
    $("input[name='lifeCycle']").removeAttr("disabled");
    $("input[name='pathActive']").removeAttr("disabled");
}