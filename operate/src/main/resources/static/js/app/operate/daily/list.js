$(function () {
    init_date('touchDt', 'yyyy-mm-dd', 0,2,0);
    $("#touchDt").datepicker('setEndDate',new Date());
    initTable();
});
function initTable() {
    var settings = {
        url: '/daily/getPageList',
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
                param: {touchDt: $("#touchDt").val()}
            };
        },
        columns: [{
            checkbox: true
        },{
            field: 'headId',
            title: 'ID',
            visible: false
        },{
            field: 'touchDt',
            title: '日期'
        },{
            field: 'totalNum',
            title: '任务建议（人）'
        },{
            field: 'actualNum',
            title: '实际推送（人）'
        },{
            field: 'touchRate',
            title: '触达率（%）'
        },{
            field: 'convertCount',
            title: '转化人数（人）'
        },{
            field: 'convertRate',
            title: '转化率（%）'
        },{
            field: 'convertAmount',
            title: '转化金额（元）'
        },{
            field: 'status',
            title: '状态',
            formatter: function (value, row, indx) {
                var res;
                switch (value) {
                    case "todo":
                        res = "草稿、待推送";
                        break;
                    case "doing":
                        res = "推送中";
                        break;
                    case "done":
                        res = "推送结束、效果统计中";
                        break;
                    case "finished":
                        res = "结束";
                        break;
                    default:
                        res = "-";
                        break;
                }
                return res;
            }
        }]
    };
    $MB.initTable('dailyTable', settings);
}

function resetDaily() {
    $("#touchDt").val("");
    $MB.refreshTable("dailyTable");
}

$("#btn_query").click(function () {
    $MB.refreshTable("dailyTable");
});

$("#btn_edit").click(function () {
    var selected = $("#dailyTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要编辑的任务！');
        return;
    }
    var headId = selected[0].headId;
    window.location.href = "/page/daily/edit?id=" + headId;
});