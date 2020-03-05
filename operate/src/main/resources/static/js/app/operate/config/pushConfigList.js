$(function () {
    getTableData();
});

function getTableData() {
    var settings = {
        url: "/push/getPushProperties",
        sortable: true,
        sortOrder: "asc",
        pagination: false,
        columns: [{
            field: 'orderNum',
            title: '序号',
            sortable: true
        }, {
            field: 'name',
            title: '配置项名'
        }, {
            field: 'value',
            title: '配置项值'
        }, {
            field: 'comments',
            title: '备注'
        }]
    };
    $MB.initTable('configTable', settings);
}



