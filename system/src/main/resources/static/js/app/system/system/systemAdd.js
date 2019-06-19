var validator;
var $systemAddForm = $("#system-add-form");
var ctx = "/";
$(function () {
    validateRule();
    $("#system-add .btn-save").click(function () {
        var name = $(this).attr("name");
        var validator = $systemAddForm.validate();
        var flag = validator.form();
        if (flag) {
            if (name === "save") {
                $.post(ctx + "system/add", $systemAddForm.serialize(), function (r) {
                    if (r.code === 200) {
                        closeModal();
                        $MB.n_success(r.msg);
                        $MB.refreshTable("systemTable");
                    } else $MB.n_danger(r.msg);
                });
            }
            if (name === "update") {
                $.post(ctx + "system/update", $systemAddForm.serialize(), function (r) {
                    if (r.code === 200) {
                        closeModal();
                        $MB.n_success(r.msg);
                        $MB.refreshTable("systemTable");
                    } else $MB.n_danger(r.msg);
                });
            }
        }
    });

    $("#system-add .btn-close").click(function () {
        closeModal();
    });

});

function clearReset() {
    $("#system-add-form")[0].reset();
}
$('#system-add').on('hidden.bs.modal', function () {
    clearReset();
});

function closeModal() {
    $("#system-add-button").attr("name", "save");
    $("#system-add-modal-title").html('新增系统');
    validator.resetForm();
    $MB.closeAndRestModal("system-add");
}

function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $systemAddForm.validate({
        rules: {
            name: {
                required: true,
                minlength: 3,
                maxlength: 10,
                remote: {
                    url: "system/checkSystemName",
                    type: "get",
                    dataType: "json",
                    data: {
                        name: function () {
                            return $("input[name='name']").val().trim();
                        },
                        oldName: function () {
                            return $("input[name='oldName']").val().trim();
                        }
                    }
                }
            },
            domain: {
                required: true
            },
            logo: {
                required: true
            },
            sortNum: {
                required: true,
                digits: true
            },
            remark: {
                maxlength: 50
            },
            domain: {
                required: true
            }
        },
        messages: {
            name: {
                required: icon + "请输入系统名称",
                minlength: icon + "系统名称长度3到10个字符",
                remote: icon + "该系统名已经存在"
            },
            logo: icon + "请输入系统Logo",
            sortNum: {
                required: "请输入系统序号",
                digits: "只能是非负整数"

            },
            remark: icon + "系统描述不能超过50个字符",
            domain: icon + "请输入系统域名"
        }
    });
}