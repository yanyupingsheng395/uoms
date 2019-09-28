$(function () {
    initTable();
});
function initTable() {
    var settings = {
        url: '/push/getPushInfoListPage',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    sourceCode: $("#sourceCode").find("option:selected").val(),
                    pushStatus: $("#pushStatus").find("option:selected").val()
                }
            };
        },
        columns: [{
            checkbox: true
        },{
            field: 'pushId',
            title: 'ID',
            visible: false
        },{
            field: 'userPhone',
            title: '手机号'
        },{
            field: 'pushContent',
            title: '推送内容'
        },{
            field: 'pushPeriod',
            title: '推送时段（时）'
        },{
            field: 'pushStatus',
            title: '推送状态',
            formatter: function (value, row, index) {
                let code = "-";
                switch (value) {
                    case "P":
                        code = "计划中";
                        break;
                    case "S":
                        code = "成功";
                        break;
                    case "F":
                        code = "失败";
                        break;
                    case "R":
                        code = "已推送,待回馈状态";
                        break;
                    case "C":
                        code = "被防骚扰拦截";
                        break;
                }
                return code;
            }
        }]
    };
    $MB.initTable('pushInfoTable', settings);
}

function resetPush() {
    $("#sourceCode").find("option:selected").removeAttr("selected");
    $("#pushStatus").find("option:selected").removeAttr("selected");
    $MB.refreshTable("pushInfoTable");
}

$("#btn_query").click(function () {
    $MB.refreshTable("pushInfoTable");
});


