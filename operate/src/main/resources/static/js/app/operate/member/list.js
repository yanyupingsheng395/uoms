$(function () {
    initTable();
});
function initTable() {
    var settings = {
        url: '/member/getHeadListPage',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1,
                param: {memberDate: $("#memberDate").val()}
            };
        },
        columns: [{
            checkbox: true
        },{
            field: 'headId',
            title: 'ID',
            visible: false
        },{
            field: 'memberDateStr',
            title: '日期'
        },{
            field: 'userCount',
            title: '定向用户数（人）'
        },{
            field: 'convertUserCount',
            title: '转化用户数（人）'
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
    $MB.initTable('memberTable', settings);
}

// 查询
$("#btn_query").click(function () {
    $MB.refreshTable('memberTable');
});

// 重置
$("#btn_reset").click(function () {
    $("#memberDate").val('');
    $MB.refreshTable('memberTable');
});

$("#btn_edit").click(function () {
    let selected = $("#memberTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要编辑的会员日！');
        return;
    }
    let status = selected[0].status;
    if(status != "todo") {
        $MB.n_warning('当前记录已被执行，请选择待执行状态的记录！');
        return;
    }
    let headId = selected[0].headId;
    window.location.href = "/page/member/edit?id=" + headId;
});