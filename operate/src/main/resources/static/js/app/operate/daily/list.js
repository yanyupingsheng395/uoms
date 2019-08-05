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
            field: 'optNum',
            title: '实际选择（人）'
        },{
            field: 'status',
            title: '状态',
            formatter: function (value, row, indx) {
                var res;
                switch (value) {
                    case "todo":
                        res = "草稿、待推送";
                        break;
                    case "pre_push":
                        res = "生成推送名单中";
                        break;
                    case "ready_push":
                        res = "已生成名单，待推送";
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
        }, {
                title: '推送名单',
                formatter: function (value, row, indx) {
                    if(row.status == 'ready_push') {
                        return "<a style='text-decoration: underline;color: #000;cursor: pointer;' onclick='getPushList(" + row.headId + ")'>推送名单</a>";
                    }else {
                        return '-';
                    }
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
        $MB.n_warning('当前记录不可编辑，请选择草稿状态的记录！');
        return;
    }
    var headId = selected[0].headId;
    window.location.href = "/page/daily/edit?id=" + headId;
});

$("#btn_view").click(function () {
    var selected = $("#dailyTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要查看的任务！');
        return;
    }
    var headId = selected[0].headId;
    window.location.href = "/page/daily/view?id=" + headId;
});

$("#btn_push").click(function () {
    var selected = $("#dailyTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要推送的记录！');
        return;
    }
    var status = selected[0].status;
    if(status != "ready_push") {
        $MB.n_warning('请勾选待推送的记录！');
        return;
    }
    var headId = selected[0].headId;
    $.get("/daily/pushList", {headId: headId}, function (r) {
        if(r.code == 200) {
            $MB.n_success("推送成功！");
        }else {
            $MB.n_success("推送失败！未知异常！");
        }
        $MB.refreshTable('dailyTable');
    });
});

/**
 * 查看推送名单
 */
function getPushList(headId) {
    $("#push_modal").modal('show');
    var settings = {
        url: '/daily/getPushList',
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
                param: {headId: headId}
            };
        },
        columns: [{
            field: 'userId',
            title: '用户ID'
        },{
            field: 'smsContent',
            title: '短信内容'
        }]
    };
    $('#pushTable').bootstrapTable('destroy');
    $MB.initTable('pushTable', settings);
}
