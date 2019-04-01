$(function () {
    var settings = {
        url: "/diag/list",
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
            field: 'diagName',
            title: '名称'
        }, {
            field: 'periodType',
            title: '周期',
            formatter: function (value, row, index) {
                if (value == "M") {
                    return "月";
                }
                if (value == "Y") {
                    return "天";
                }
            }
        }, {
            field: 'beginDt',
            title: '周期开始时间'
        }, {
            field: 'endDt',
            title: '周期结束时间'
        }, {
            filed: '#',
            title: '操作',
            formatter: function (value, row, index) {
                    return "<a class='btn btn-primary btn-sm' onclick=''><i class='mdi mdi-eye'></i>查看</a>&nbsp;<div class='btn btn-danger btn-sm' onclick=''><i class='mdi mdi-window-close'></i>删除</div>";
            }
        }]
    };
    $('#diagTable').bootstrapTable(settings);
});
