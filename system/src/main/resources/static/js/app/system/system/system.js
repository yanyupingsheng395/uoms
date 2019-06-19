var ctx = "/";
$(function () {
    var settings = {
        url: ctx + "system/list",
        pageSize: 10,
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: params.offset / params.limit + 1,
                name: $(".system-table-form").find("input[name='name']").val() == null ? null: $(".system-table-form").find("input[name='name']").val().trim()
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'name',
            title: '名称'
        }, {
            field: 'remark',
            title: '描述'
        }, {
            field: 'createDt',
            title: '创建时间'
        }]
    };

    $MB.initTable('systemTable', settings);
});

function searchSystem() {
    $MB.refreshTable('systemTable');
}

function refreshSystem() {
    $(".system-table-form")[0].reset();
    searchSystem();
}

function deleteSystem() {
    var selected = $("#systemTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要删除的系统！');
        return;
    }
    var ids = "";
    for (var i = 0; i < selected_length; i++) {
        ids += selected[i].id;
        if (i !== (selected_length - 1)) ids += ",";
    }

    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定删除系统信息？"
    }, function () {
        $.post(ctx + 'system/delete', {"ids": ids}, function (r) {
            if (r.code === 0) {
                $MB.n_success(r.msg);
                refresh();
            } else {
                $MB.n_danger(r.msg);
            }
        });
    });
}