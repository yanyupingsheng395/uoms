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
            field: 'domain',
            title: '访问地址'
        }, {
            field: 'logo',
            title: '系统Logo'
        },{
            field: 'sortNum',
            title: '序号'
        },{
            field: 'enableFlag',
            title: '状态',
            formatter: function (value, row, index) {
                var res = "";
                value == '1' ? res = "<span class=\"badge bg-success\">启用</span>" : res = "<span class=\"badge bg-warning\">禁用</span>";
                return res;
            }
        },{
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
            if (r.code === 200) {
                $MB.n_success(r.msg);
                refreshSystem();
            } else {
                $MB.n_danger(r.msg);
            }
        });
    });
}