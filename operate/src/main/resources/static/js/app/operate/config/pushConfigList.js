$(function () {
    let settings = {
        url: "/push/getPushConfig",
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
    $('#configTable').bootstrapTable('destroy').bootstrapTable(settings);
    $.get("/push/getPushConfig", {}, function (r) {
        let dataList = r.data;
        $("#vendor").text(r.msg.vendorName);
        $("#configTable").bootstrapTable('load', dataList);
    });
});


