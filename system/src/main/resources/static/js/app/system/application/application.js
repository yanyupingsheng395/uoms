var ctx = "/";
$(function () {
    var settings = {
        url: ctx + "application/list",
        pageSize: 10,
        singleSelect: true,
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: params.offset / params.limit + 1,
                name: $(".application-table-form").find("input[name='name']").val() == null ? null: $(".application-table-form").find("input[name='name']").val().trim()
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'applicationName',
            title: '应用名称'
        }, {
            field: 'domain',
            title: '地址'
        }, {
            field: 'remark',
            title: '应用logo'
        }, {
            field: 'enableFlag',
            title: '是否启用',
            formatter: function (value, row, index) {
                if (value === 'Y') return '<span class="badge bg-success">启用</span>';
                if (value === 'N') return '<span class="badge bg-warning">未启用</span>';
            }
        }]
    };

    $MB.initTable('applicationTable', settings);
});

function searchApplication() {
    $MB.refreshTable('applicationTable');
}

function refreshApplication() {
    $(".application-table-form")[0].reset();
    searchApplication();
}

function deleteApplication() {
    var selected = $("#applicationTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要删除的应用！');
        return;
    }
    var ids = "";
    for (var i = 0; i < selected_length; i++) {
        ids += selected[i].applicationId;
        if (i !== (selected_length - 1)) ids += ",";
    }

    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定删除应用信息？"
    }, function () {
        $.post(ctx + 'application/delete', {"ids": ids}, function (r) {
            if (r.code === 0) {
                $MB.n_success(r.msg);
                refresh();
            } else {
                $MB.n_danger(r.msg);
            }
        });
    });
}