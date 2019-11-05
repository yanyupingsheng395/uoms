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
                checkbox: true,
                valign: "middle",
                rowspan: 2
            },
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
                        res = "<span class=\"badge bg-info\">待计划</span>";
                        break;
                    case "todo":
                        res = "<span class=\"badge bg-color-default\">待执行</span>";
                        break;
                    case "doing":
                        res = "<span class=\"badge bg-warning\">执行中</span>";
                        break;
                    case "done":
                        res = "<span class=\"badge bg-success\">执行完</span>";
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
                        res = "<span class=\"badge bg-info\">待计划</span>";
                        break;
                    case "todo":
                        res = "<span class=\"badge bg-color-default\">待执行</span>";
                        break;
                    case "doing":
                        res = "<span class=\"badge bg-warning\">执行中</span>";
                        break;
                    case "done":
                        res = "<span class=\"badge bg-success\">执行完</span>";
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

$("#btn_edit").click(function () {
    let selected = $("#activityTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要编辑的活动！');
        return;
    }
    let headId = selected[0].headId;
    window.location.href = "/page/activity/edit?headId=" + headId;
});

$("#btn_plan").click(function () {
    let selected = $("#activityTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要编辑的活动！');
        return;
    }
    let headId = selected[0].headId;
    let preheatStatus = selected[0]['preheatStatus'];
    let formalStatus = selected[0]['formalStatus'];
    let flag = preheatStatus === 'todo' || preheatStatus === 'doing' || formalStatus === 'todo' || formalStatus === 'doing';
    if(flag) {
        window.location.href = "/page/activity/plan?id=" + headId;
    }else {
        $MB.n_warning("该活动的当前状态不允许执行计划！");
    }
});