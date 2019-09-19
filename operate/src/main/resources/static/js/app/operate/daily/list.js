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
            title: '建议推送人数（人）'
        },{
            field: 'convertCount',
            title: '任务转化人数（人）'
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
                        res = "<span class=\"badge bg-info\">待执行</span>";
                        break;
                    case "done":
                        res = "<span class=\"badge bg-success\">已执行</span>";
                        break;
                    case "finished":
                        res = "<span class=\"badge bg-primary\">已结束</span>";
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
    var status = selected[0].status;
    if(status != "todo") {
        $MB.n_warning('当前记录不可编辑，请选择待编辑状态的记录！');
        return;
    }
    var headId = selected[0].headId;
    window.location.href = "/page/daily/edit?id=" + headId;
});


$("#btn_catch").click(function () {
    var selected = $("#dailyTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要查看的任务！');
        return;
    }
    var status = selected[0].status;
    if(status != 'done' && status != 'finished' && status != 'doing') {
        $MB.n_warning("执行中，已执行和已结束状态的可检查评估！");
        return;
    }
    var headId = selected[0].headId;
    window.location.href = "/page/daily/effect?id=" + headId;
});