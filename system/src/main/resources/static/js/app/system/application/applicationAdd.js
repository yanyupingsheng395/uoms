var validator;
var $applicationAddForm = $("#application-add-form");
var ctx = "/";
$(function () {
    validateRule();
    $("#application-add .btn-save").click(function () {
        var name = $(this).attr("name");
        var validator = $applicationAddForm.validate();
        var flag = validator.form();
        if (flag) {
            if (name === "save") {
                $.post(ctx + "application/add", $applicationAddForm.serialize(), function (r) {
                    if (r.code === 0) {
                        closeModal();
                        $MB.n_success(r.msg);
                        $MB.refreshTable("applicationTable");
                    } else $MB.n_danger(r.msg);
                });
            }
            if (name === "update") {
                $.post(ctx + "application/update", $applicationAddForm.serialize(), function (r) {
                    if (r.code === 0) {
                        closeModal();
                        $MB.n_success(r.msg);
                        $MB.refreshTable("applicationTable");
                    } else $MB.n_danger(r.msg);
                });
            }
        }
    });

    $("#application-add .btn-close").click(function () {
        closeModal();
    });

});

function clearReset() {
    $("#application-add-form")[0].reset();
}
$('#application-add').on('hidden.bs.modal', function () {
    clearReset();
});

function closeModal() {
    $("#application-add-button").attr("name", "save");
    $("#application-add-modal-title").html('新增应用');
    validator.resetForm();
    $MB.closeAndRestModal("application-add");
}

function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $applicationAddForm.validate({
        rules: {
            name: {
                required: true,
                minlength: 3,
                maxlength: 10,
                remote: {
                    url: "application/checkApplicationName",
                    type: "get",
                    dataType: "json",
                    data: {
                        name: function () {
                            return $("input[name='applicationName']").val().trim();
                        },
                        oldName: function () {
                            return $("input[name='oldApplicationName']").val().trim();
                        }
                    }
                }
            },
            remark: {
                maxlength: 50
            },
            domain: {
                required: true
            },
            enableFlag: {
                required: true
            }
        },
        messages: {
            name: {
                required: icon + "请输入应用名称",
                minlength: icon + "应用名称长度3到10个字符",
                remote: icon + "该应用名已经存在"
            },
            remark: icon + "应用logo不能超过50个字符",
            domain: icon + "请输入应用地址"
        }
    });
}