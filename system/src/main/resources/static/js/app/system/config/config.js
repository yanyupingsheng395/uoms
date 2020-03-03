$(function () {
    getTableData();
});
function getTableData() {
    var $configTableForm = $("#config-form");
    var settings = {
        url: "/config/getList",
        sortable: true,
        sortOrder: "asc",
        pagination: false,
        queryParams: function (params) {
            return {
                sort: params.sort,
                order: params.order,
                name: $("input[name]").val(),
                typeCode1: $("select[name='typeCode1']").val()
            };
        },
        columns: [{
            field: 'typeCode2',
            title: '参数类别',
            sortable: true
        }, {
            field: 'name',
            title: '参数名'
        }, {
            field: 'value',
            title: '参数值'
        }, {
            field: 'comments',
            title: '备注'
        }, {
            field: 'orderNum',
            title: '排序号',
            sortable: true
        }]
    };
    $MB.initTable('configTable', settings);
    // $("#configTable").bootstrapTable('destroy').bootstrapTable(settings);
}

function searchConfig() {
    $MB.refreshTable('configTable');
}

// 重置条件
function refreshConfig() {
    $("input[name='name']").val("");
    $("select[name='typeCode1']").find("option:selected").removeAttr("selected");
    $MB.refreshTable('configTable');
}

// 刷新数据
function reload() {
    $.get("/config/reloadData", {}, function (r) {
        if(r.code === 200) {
            $MB.n_success("刷新成功！");
            refreshConfig();
        }
    });
}