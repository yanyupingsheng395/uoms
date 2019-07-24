var validator;
var $roleAddForm = $("#role-add-form");
var ctx = "/";
$(function () {
    validateRule();
    createMenuTree();
    $("#role-add .btn-save").click(function () {
        var name = $(this).attr("name");
        getMenu();
        var validator = $roleAddForm.validate();
        var flag = validator.form();
        if (flag) {
            if (name === "save") {
                $.post(ctx + "role/add", $roleAddForm.serialize(), function (r) {
                    if (r.code === 200) {
                        closeModal();
                        $MB.n_success(r.msg);
                        $MB.refreshTable("roleTable");
                    } else $MB.n_danger(r.msg);
                });
            }
            if (name === "update") {
                $.post(ctx + "role/update", $roleAddForm.serialize(), function (r) {
                    if (r.code === 200) {
                        closeModal();
                        $MB.n_success(r.msg);
                        $MB.refreshTable("roleTable");
                    } else $MB.n_danger(r.msg);
                });
            }
        }
    });

    $("#role-add .btn-close").click(function () {
        closeModal();
    });

});

function closeModal() {
    roleEditModalReset();
}

$('#role-add').on('hidden.bs.modal', function () {
    roleEditModalReset();
});

// 取消系统的checkbox
$('#role-add').on('show.bs.modal', function () {
    $('#menuTree').find("ul li a[class='jstree-anchor']").each(function() {$(this).addClass("jstree-no-checkboxes")});
});

function roleEditModalReset() {
    $("#role-add-form")[0].reset();
    $("#sysId").selectpicker('val', '');
    $("input[name='system']").val("");

    $("#role-add-button").attr("name", "save");
    $("#role-add-modal-title").html('新增角色');
    validator.resetForm();
    $MB.resetJsTree("menuTree");
    $MB.closeAndRestModal("role-add");
}

function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $roleAddForm.validate({
        rules: {
            roleName: {
                required: true,
                minlength: 3,
                maxlength: 10,
                remote: {
                    url: "role/checkRoleName",
                    type: "get",
                    dataType: "json",
                    data: {
                        roleName: function () {
                            return $("input[name='roleName']").val().trim();
                        },
                        oldRoleName: function () {
                            return $("input[name='oldRoleName']").val().trim();
                        }
                    }
                }
            },
            remark: {
                maxlength: 50
            },
            menuId: {
                required: true
            }
        },
        messages: {
            roleName: {
                required: icon + "请输入角色名称",
                minlength: icon + "角色名称长度3到10个字符",
                remote: icon + "该角色名已经存在"
            },
            remark: icon + "角色描述不能超过50个字符",
            menuId: icon + "请选择相应菜单权限"
        }
    });
}

var ids = new Array();
function createMenuTree() {
    $.post(ctx + "menu/menuButtonTree", {}, function (r) {
        var data = r.msg;
        if (r.code === 200) {
            $('#menuTree').jstree({
                "core": {
                    'data': data.children
                },
                "state": {
                    "disabled": true
                },
                "checkbox": {
                    "three_state": false
                },
                "plugins": ["wholerow", "checkbox"]
            });
        } else {
            $MB.n_danger(r.msg);
        }
        data.children.forEach((item, index, data) => {
            ids.push(item.id);
        });
    });
}


function getMenu() {
    var $menuTree = $('#menuTree');
    var ref = $menuTree.jstree(true);
    var menuIds = ref.get_checked();
    $menuTree.find(".jstree-undetermined").each(function (i, element) {
        menuIds.push($(element).closest('.jstree-node').attr("id"));
    });
    $("[name='menuId']").val(menuIds);
}

$("#role-add").on('shown.bs.modal', function () {
    ids.forEach((item, index, ids) => {
        $("#menuTree").jstree('disable_node', item);
    });
});