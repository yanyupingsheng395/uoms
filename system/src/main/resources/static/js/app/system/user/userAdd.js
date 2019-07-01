var validator;
var $userAddForm = $("#user-add-form");
var $rolesSelect = $userAddForm.find("select[name='rolesSelect']");
var $roles = $userAddForm.find("input[name='roles']");
var ctx = "/";

$(function () {
    init_date("expire", "yyyy-mm-dd", 0, 2, 0, new Date(), "");
    validateRule();
    initRole();

    $("#user-add .btn-save").click(function () {
        var name = $(this).attr("name");
        var validator = $userAddForm.validate();
        var flag = validator.form();
        if (flag) {
            if (name === "save") {
                $.post(ctx + "user/add", $userAddForm.serialize(), function (r) {
                    if (r.code === 200) {
                        closeModal();
                        $MB.n_success(r.msg);
                        $MB.refreshTable("userTable");
                    } else $MB.n_danger(r.msg);
                });
            }
            if (name === "update") {
                $.post(ctx + "user/update", $userAddForm.serialize(), function (r) {
                    if (r.code === 200) {
                        closeModal();
                        $MB.n_success(r.msg);
                        $MB.refreshTable("userTable");
                    } else $MB.n_danger(r.msg);
                });
            }
        }
    });

    $("#user-add .btn-close").click(function () {
        closeModal();
    });

});

function clearReset() {
    $("#user-add-button").attr("name", "save");
    validator.resetForm();
    $rolesSelect.selectpicker('refresh');
    $userAddForm.find("input[name='username']").removeAttr("readonly");
    $userAddForm.find("input[name='username']").val("");
    $userAddForm.find("input[name='email']").val("");
    $userAddForm.find("input[name='mobile']").val("");
    $userAddForm.find("input[name='expire']").val("");
    $userAddForm.find("select[name='rolesSelect']").selectpicker('val', "");
    $userAddForm.find(".user_password").show();
    $userAddForm.find("input[name='status']:checked").removeAttr("checked");
    $userAddForm.find("input[name='ssex']:checked").removeAttr("checked");

    $("input[name='status']:radio[value='1']").prop("checked", true);
    $("input[name='ssex']:radio[value='0']").prop("checked", true);

    $("#user-add-modal-title").html('新增用户');
}

$('#user-add').on('hidden.bs.modal', function () {
    clearReset();
});

function closeModal() {
    $MB.closeAndRestModal("user-add");
}

function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $userAddForm.validate({
        rules: {
            username: {
                required: true,
                minlength: 3,
                maxlength: 10,
                remote: {
                    url: "user/checkUserName",
                    type: "get",
                    dataType: "json",
                    data: {
                        username: function () {
                            return $("input[name='username']").val().trim();
                        },
                        oldusername: function () {
                            return $("input[name='oldusername']").val().trim();
                        }
                    }
                }
            },
            email: {
                email: true
            },
            roles: {
                required: true
            },
            mobile: {
                checkPhone: true
            },
            status: {
                required: true
            },
            ssex: {
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
            username: {
                required: icon + "请输入用户名",
                minlength: icon + "用户名长度3到10个字符",
                remote: icon + "用户名已经存在"
            },
            roles: icon + "请选择用户角色",
            email: icon + "邮箱格式不正确",
            status: icon + "请选择状态",
            ssex: icon + "请选择性别"
        }
    });
}

function initRole() {
    $.post(ctx + "role/list2", {}, function (r) {
        var data = r.data;
        var option = "";
        $.each(data, function(key, val) {
            option += "<option value='" + key + "'>" + val + "</option>"
        });

        $rolesSelect.html("").append(option);
        $rolesSelect.selectpicker('refresh');
    });
}

$rolesSelect.on('changed.bs.select',function(){
    $roles.val($rolesSelect.selectpicker('val'));
});

