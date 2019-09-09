$(function () {
    let settings = {
        url: "/activity/gePageOfHead",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit )+ 1,  //页码
                param: {name: $("input[name='actName']").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'headId',
            title: 'ID',
            visible: false
        }, {
            field: 'activityName',
            title: '活动名称'
        }, {
            field: 'activityType',
            title: '活动类型',
            formatter: function (value, row, index) {
                if(value == "own") {
                    return "自主";
                }else if(value == "plat"){
                    return "平台";
                }else {
                    return "";
                }
            }
        }, {
            field: 'startDate',
            title: '活动开始时间'
        },  {
            field: 'endDate',
            title: '活动结束时间'
        }, {
            field: 'beforeDate',
            title: '活动影响开始时间'
        },  {
            field: 'afterDate',
            title: '活动影响结束时间'
        }, {
            field: 'coverNum',
            title: '预计覆盖人数（人）'
        }, {
            field: 'convertNum',
            title: '活动转化人数（人）'
        }, {
            field: 'numYearPercent',
            title: '年同比（%）'
        }, {
            field: 'convertAmount',
            title: '活动转化金额（元）'
        }, {
            field: 'amountYearPercent',
            title: '年同比（%）'
        }, {
            field: 'status',
            title: '状态',
            formatter: function (value, row, index) {
                // if(value == 'todo') {
                //     return "待编辑";
                // }
                return "-";
            }
        }]
    };
    $MB.initTable('activityTable', settings);
});

function searchActivity() {
    $MB.refreshTable('activityTable');
}

function resetActivity() {
    $("input[name='actName']").val('');
    $MB.refreshTable('activityTable');
}

$("#btn_edit").click(function () {
    let selected = $("#activityTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要查看的活动！');
        return;
    }
    // let status = selected[0].status;
    // if(status != "todo") {
    //     $MB.n_warning('当前记录不可编辑，请选择待编辑状态的记录！');
    //     return;
    // }
    let headId = selected[0].headId;
    window.location.href = "/page/activity/edit?id=" + headId;
});

$("#btn_add").click(function () {
    window.location.href = "/page/activity/add";
});

$("#btn_catch").click(function () {
    let selected = $("#activityTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要评估的活动！');
        return;
    }
    $MB.n_warning("活动未执行,暂无法查看效果数据。");
});