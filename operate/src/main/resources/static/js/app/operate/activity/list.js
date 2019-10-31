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
                pageNum: (params.offset / params.limit) + 1,  //页码
                param: {name: $("input[name='actName']").val()}
            };
        },
        columns: [[
            {
                field: 'activityName',
                title: '活动名称',
                align: "center",
                valign: "middle",
                rowspan: 2
            },
            {
                title: "预热",
                align: "center",
                colspan: 3
            },
            {
                title: "正式",
                align: "center",
                colspan: 3
            }
        ], [{
            field: 'preheatStartDt',
            title: '活动开始时间'
        }, {
            field: 'preheatEndDt',
            title: '活动结束时间'
        }, {
            field: 'preheatStatus',
            title: '活动状态',
            formatter: function (value, row, index) {
                let res = "-";
                switch (value) {
                    case "edit":
                        res = "待计划";
                        break;
                    case "todo":
                        res = "待执行";
                        break;
                    case "doing":
                        res = "执行中";
                        break;
                    case "done":
                        res = "执行完";
                        break;
                }
                return res;
            }
        }, {
            field: 'formalStartDt',
            title: '活动开始时间'
        }, {
            field: 'formalEndDt',
            title: '活动结束时间'
        }, {
            field: 'formalStatus',
            title: '活动状态',
            formatter: function (value, row, index) {
                let res = "-";
                switch (value) {
                    case "edit":
                        res = "待计划";
                        break;
                    case "todo":
                        res = "待执行";
                        break;
                    case "doing":
                        res = "执行中";
                        break;
                    case "done":
                        res = "执行完";
                        break;
                }
                return res;
            }
        }]]
    };
    $MB.initTable('activityTable', settings);
});

// 查询列表
function searchActivity() {
    $MB.refreshTable('activityTable');
}

// 重置查询条件
function resetActivity() {
    $("input[name='actName']").val('');
    $MB.refreshTable('activityTable');
}

// 创建计划
$("#btn_add").click(function () {
    window.location.href = "/page/activity/add";
});