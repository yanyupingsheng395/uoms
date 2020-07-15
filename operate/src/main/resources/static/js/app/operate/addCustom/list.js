getTableData();
function getTableData() {
    var settings = {
        // url: "",
        // cache: false,
        // pagination: true,
        // singleSelect: true,
        // sidePagination: "server",
        // pageNumber: 1,            //初始化加载第一页，默认第一页
        // pageSize: 10,            //每页的记录行数（*）
        // pageList: [10, 25, 50, 100],
        // queryParams: function (params) {
        //     return {
        //         pageSize: params.limit,  ////页面大小
        //         pageNum: (params.offset / params.limit) + 1
        //     };
        // },
        columns: [{
            field: 'sendRange',
            align: "center",
            title: '发送申请范围',
            formatter: function (value, row, index) {
                let res = "-";
                switch (value) {
                    case "0":
                        res = "筛选";
                        break;
                    case "1":
                        res = "全部";
                        break;
                }
                return res;
            }
        },{
            field: 'sendCount',
            align: "center",
            title: '发送申请数量（人）'
        }, {
            field: 'applySuccess',
            align: "center",
            title: '通过申请数量（人）'
        }, {
            field: 'applySuccessRate',
            align: "center",
            title: '申请通过率（%）'
        }, {
            field: 'applyTriggerRule',
            align: "center",
            title: '申请触发机制'
        },{
            field: 'taskStartDt',
            align: "center",
            title: '开始时间'
        }, {
            field: 'taskStatus',
            align: "center",
            title: '任务状态',
            formatter: function (value, row, index) {
                let res = "-";
                switch (value) {
                    case "edit":
                        res = "<span class=\"badge bg-info\">待计划</span>";
                        break;
                    case "todo":
                        res = "<span class=\"badge bg-primary\">待执行</span>";
                        break;
                    case "doing":
                        res = "<span class=\"badge bg-warning\">执行中</span>";
                        break;
                    case "done":
                        res = "<span class=\"badge bg-success\">执行完</span>";
                        break;
                    case "timeout":
                        res = "<span class=\"badge bg-gray\">过期未执行</span>";
                        break;
                }
                return res;
            }
        }]
    };
    var data = [{sendRange: '1', sendCount:55400, applySuccess: 500, applySuccessRate: '9%', applyTriggerRule: '通过短信推送带有二维码的页面',
        taskStartDt: '20200709', taskStatus: 'done'}];
    $("#dataTable").bootstrapTable(settings).bootstrapTable('load', data);
}