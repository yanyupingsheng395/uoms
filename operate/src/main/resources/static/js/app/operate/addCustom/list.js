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
            checkbox: true
        },{
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
    var data = [
        {sendRange: '0', sendCount:28799, applySuccess: 9881, applySuccessRate: '24%', applyTriggerRule: '用户发生购买后短信推送申请',
        taskStartDt: '20200514', taskStatus: 'doing'},
        {sendRange: '0', sendCount:140, applySuccess: 42, applySuccessRate: '30%', applyTriggerRule: '通过短信推送带有二维码的页面',
            taskStartDt: '20200531', taskStatus: 'done'},
        {sendRange: '0', sendCount:213, applySuccess: 21, applySuccessRate: '10%', applyTriggerRule: '通过短信推送带有二维码的页面',
            taskStartDt: '20200530', taskStatus: 'done'},
        {sendRange: '0', sendCount:429, applySuccess: 55, applySuccessRate: '13%', applyTriggerRule: '通过短信推送带有二维码的页面',
            taskStartDt: '20200529', taskStatus: 'done'},
        {sendRange: '0', sendCount:224, applySuccess: 38, applySuccessRate: '17%', applyTriggerRule: '通过短信推送带有二维码的页面',
            taskStartDt: '20200528', taskStatus: 'done'},
        {sendRange: '0', sendCount:2862, applySuccess: 429, applySuccessRate: '15%', applyTriggerRule: '通过短信推送带有二维码的页面',
            taskStartDt: '20200527', taskStatus: 'done'},
        {sendRange: '0', sendCount:674, applySuccess: 127, applySuccessRate: '19%', applyTriggerRule: '通过短信推送带有二维码的页面',
            taskStartDt: '20200526', taskStatus: 'done'},
        {sendRange: '0', sendCount:358, applySuccess: 78, applySuccessRate: '21%', applyTriggerRule: '通过短信推送带有二维码的页面',
            taskStartDt: '20200525', taskStatus: 'done'},
        {sendRange: '0', sendCount:351, applySuccess: 91, applySuccessRate: '26%', applyTriggerRule: '通过短信推送带有二维码的页面',
            taskStartDt: '20200524', taskStatus: 'done'},
        {sendRange: '0', sendCount:730, applySuccess: 226, applySuccessRate: '31%', applyTriggerRule: '通过短信推送带有二维码的页面',
            taskStartDt: '20200523', taskStatus: 'done'},
        {sendRange: '1', sendCount:184841, applySuccess: 38816, applySuccessRate: '21%', applyTriggerRule: '通过企业微信自动添加好友',
            taskStartDt: '20200516', taskStatus: 'done'}];
    $("#dataTable").bootstrapTable(settings).bootstrapTable('load', data);
}

$("#btn_effect").click(function () {
    let selected = $("#dataTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选要查看效果的任务！');
        return;
    }
    let status = selected[0].taskStatus;
    window.location.href = "/page/addCustom/effect";
});