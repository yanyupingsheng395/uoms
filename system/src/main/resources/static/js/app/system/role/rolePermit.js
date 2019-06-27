// 给用户授权
var roleId = null;
function setUserRoles() {
    var selected = $("#roleTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要授权的角色！');
        return;
    }
    if (selected_length > 1) {
        $MB.n_warning('一次只能授权一个角色！');
        return;
    }
    roleId = selected[0].roleId;
    $.ajax({
        url:ctx + "role/getUserRoleTree",
        data:{roleId: roleId},
        async: false,
        success: function (r) {
            if (r.code === 200) {
                var data = r.data;
                $('#userRoleTree').jstree({
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
                }).on("loaded.jstree", function (event, data) {
                    $('#userRoleTree').find("ul li a[class='jstree-anchor']").each(function() {$(this).addClass("jstree-no-checkboxes")});
                    $("#userRoleTree").jstree('disable_node', "-1");
                    $(r.data.children[0].children).each(function (k, v) {
                        if(v.checked) {
                            $("#userRoleTree").jstree('select_node', v.id, true);
                        }
                    });
                });
            } else {
                $MB.n_danger(r.msg);
            }
        }
    });
    $("#role-permit").modal('show');
}

function saveUserRole() {
    var ref = $('#userRoleTree').jstree(true);
    var userIds = ref.get_selected(false);
    userIds = userIds.join(",");
    $.post("/role/updateUserRole", {userIds: userIds, roleId: roleId}, function (r) {
        if(r.code === 200) {
            $MB.n_success(r.msg);
        }else {
            $MB.n_danger(r.msg);
        }
        $("#role-permit").modal('hide');
    });
}

$("#role-permit").on('hidden.bs.modal', function () {
    $('#userRoleTree').jstree("destroy");
});

