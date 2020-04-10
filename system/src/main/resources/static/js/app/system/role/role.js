var ctx = "/";
$(function () {
    var settings = {
        url: ctx + "role/list",
        pageSize: 10,
        singleSelect: true,
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: params.offset / params.limit + 1,
                roleName: $(".role-table-form").find("input[name='qroleName']").val() == null ? null: $(".role-table-form").find("input[name='qroleName']").val().trim()
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'roleName',
            title: '角色'
        }, {
            field: 'remark',
            title: '描述'
        }, {
            field: 'createDt',
            title: '创建时间'
        }]
    };

    $MB.initTable('roleTable', settings);
});

function searchRole() {
    $MB.refreshTable('roleTable');
}

function refreshRole() {
    $("input[name='roleName']").val("");
    $MB.refreshTable('roleTable');
}

function deleteRoles() {
    var selected = $("#roleTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要删除的角色！');
        return;
    }
    var ids = "";
    for (var i = 0; i < selected_length; i++) {
        ids += selected[i].roleId;
        if (i !== (selected_length - 1)) ids += ",";
    }

    $MB.confirm({
        content: "删除选中角色将导致该角色对应账户失去相应的权限，确定删除？",
        title: "确定删除"
    }, function () {
        $.post(ctx + 'role/delete', {"ids": ids}, function (r) {
            if (r.code === 200) {
                $MB.n_success(r.msg);
                refreshRole();
            } else {
                $MB.n_danger(r.msg);
            }
        });
    });
}