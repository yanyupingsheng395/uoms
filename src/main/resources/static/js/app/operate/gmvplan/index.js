$(function () {
    var settings = {
        url: "/gmvplan/list",
        method: 'post',
        cache: false,
        pagination: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [{
            field: 'yearId',
            title: '年份'
        }, {
            field: 'gmvTarget',
            title: 'GMV目标值'
        }, {
            field: 'targetRate',
            title: '相比上年增长率',
            formatter: function (value, row, index) {
                return value == null ? "" : value + "%"
            }
        }, {
            // D表示草稿  C表示更新数据中 E表示已下达执行
            field: 'status',
            title: '状态',
            formatter: function (value, row, index) {
                if (value == "D") {
                    return "<span class='label label-info'>草稿</span>";
                }else if (value == "C") {
                    return "<span class='label label-primary'>更新数据中</span>";
                }else if (value == "E") {
                    return "<span class='label label-warning'>执行</span>";
                }
            }
        }, {
            filed: 'button',
            title: '操作',
            formatter: function (value, row, index) {
                var year = row.yearId;
                if (row.status == "D") {
                    return "<div class='btn btn-success btn-sm' onclick='executeData("+year+")'><i class='mdi mdi-check'></i>执行</div>&nbsp;<div class='btn btn-warning btn-sm' onclick='modifyData("+year+")'><i class='mdi mdi-pencil'></i>修改</div>&nbsp;<div class='btn btn-danger btn-sm' onclick='deleteData("+year+")'><i class='mdi mdi-window-close'></i>删除</div>";
                }else if (row.status == "C") {
                    return "<a class='btn btn-primary btn-sm' onclick='viewData("+year+")'><i class='mdi mdi-eye'></i>查看</a>&nbsp;<div class='btn btn-info btn-sm' onclick='changeData("+year+")'><i class='mdi mdi-redo'></i>变更</div>";
                }
            }
        }]
    };
    $('#gmvPlanTable').bootstrapTable(settings);
});

function executeData(year){
    $.confirm({
        title: '提示：',
        content: '确定要执行此运营目标?执行状态的运营目标将会滚动计算，不允许再删除！',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'btn-blue'
            },
            cancel: {
                text: '取消'
            }
        }
    });
}

function viewData(year){
    location.href = "/gmvplan/view?year=" + year
}

function changeData(year){
    location.href = "/gmvplan/change?year=" + year
}

function deleteData(year){
    $.confirm({
        title: '提示：',
        content: '是否删除目标数据？',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'btn-blue',
            },
            cancel: {
                text: '取消'
            }
        }
    });
}

function modifyData(year){
    location.href = "/gmvplan/edit?year=" + year
}
