$(function () {
    init_date('touchDt', 'yyyy-mm-dd', 0,2,0);
    $("#touchDt").datepicker('setEndDate',new Date());
    initTable();
});
function initTable() {
    var settings = {
        url: '/daily/getTouchPageList',
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
            field: 'actualNum',
            title: '总推送（人）'
        },{
            field: 'planNum',
            title: '计划中（人）'
        },{
            field: 'successNum',
            title: '推送成功（人）'
        },{
            field: 'faildNum',
            title: '推送失败（人）'
        }, {
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
                return "<a style='text-decoration: underline;color: #000;cursor: pointer;' onclick='getPushList(" + row.headId + ")'>推送名单</a>";
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

var HEAD_ID;
/**
 * 查看推送名单
 */
function getPushList(headId) {
    HEAD_ID = headId;
    $("#push_modal").modal('show');
}

$("#push_modal").on('shown.bs.modal', function () {
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
                param: {headId: HEAD_ID}
            };
        },
        columns: [{
            field: 'userId',
            title: '用户ID'
        },{
            field: 'smsContent',
            title: '短信内容'
        }, {
            field: 'status',
            title: '状态',
            formatter: function (value, row, index) {
                var res;
                switch (value) {
                    case "P":
                        res = "计划中";
                        break;
                    case "D":
                        res = "已发送，待反馈结果";
                        break;
                    case "S":
                        res = "成功";
                        break;
                    case "F":
                        res = "失败";
                        break;
                    default:
                        res = "-";
                        break;
                }
                return res;
            }
        }]
    };
    $('#pushTable').bootstrapTable('destroy');
    $MB.initTable('pushTable', settings);
});