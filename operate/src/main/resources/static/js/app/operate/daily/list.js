$(function () {
    init_date('touchDt', 'yyyy-mm-dd', 0, 2, 0);
    $("#touchDt").datepicker('setEndDate', new Date());
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
        }, {
            field: 'headId',
            title: 'ID',
            visible: false
        }, {
            field: 'touchDtStr',
            title: '日期'
        }, {
            field: 'totalNum',
            title: '建议推送人数（人）'
        }, {
            field: 'successNum',
            title: '实际成功推送人数（人）'
        }, {
            field: 'convertNum',
            title: '推送转化人数（人）'
        }, {
            field: 'convertRate',
            title: '推送转化率（%）'
        }, {
            field: 'convertAmount',
            title: '推送转化金额（元）'
        }, {
            field: 'validStatus',
            title: '配置校验状态',
            formatter: function (value, row, indx) {
                var res = "-";
                if(value === '1') {
                    res = "通过";
                }
                if(value === '0') {
                    res = "<a onclick='gotoConfig()' style='color: #48b0f7;text-decoration: underline;cursor: pointer;'>不通过</a>";
                }
                return res;
            }
        },{
            field: 'status',
            title: '任务执行状态',
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
    if (status != "todo") {
        $MB.n_warning('当前记录已被执行，请选择待执行状态的记录！');
        return;
    }

    var headId = selected[0].headId;

    $.get("/daily/validUserGroup", {}, function(r) {
        if(r.code == 200) {
            if(r.data) {
                $MB.n_warning("成长组配置验证未通过！");
                return false;
            }
            window.location.href = "/page/daily/edit?id=" + headId;
        }else {
            $MB.n_danger("未知异常！");
            return false;
        }
    });
});

$("#btn_catch").click(function () {
    var selected = $("#dailyTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要查看的任务！');
        return;
    }
    var status = selected[0].status;
    if (status != 'done' && status != 'finished') {
        $MB.n_warning("只有已执行，已结束状态可查看任务效果！");
        return;
    }
    var headId = selected[0].headId;
    window.location.href = "/page/daily/effect?id=" + headId;
});

function gotoConfig() {
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: "去完成配置？"
    }, function () {
        location.href = "/page/daily/config";
    });
}