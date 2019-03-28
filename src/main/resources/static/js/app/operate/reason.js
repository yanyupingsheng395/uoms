$(function () {
    var settings = {
        url: "/reason/list",
        method: 'post',
        cache: false,
        pagination: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: params.offset / params.limit + 1
            };
        },
        columns: [{
            field: 'reasonName',
            title: '原因探究名称'
        }, {
            field: 'beginDate',
            title: '探究开始日期'
        },{
            field: 'endDate',
            title: '探究结束日期'
        }, {
            field: 'KPI_NAME',
            title: '指标名称'
        }, {
            field: 'progress',
            title: '进度',
            formatter: function (value, row, index) {
                return value == null ? "" : value + "%"
            }
        }, {
            // A表示草稿 R表示计算中 F表示计算完成
            field: 'status',
            title: '状态',
            formatter: function (value, row, index) {
                if (value == "A") {
                    return "<span class='label label-info'>草稿</span>";
                }else if (value == "R") {
                    return "<span class='label label-primary'>计算中</span>";
                }else if (value == "F") {
                    return "<span class='label label-warning'>完成</span>";
                }
            }
        }, {
            field: 'createDt',
            title: '创建时间'
        }, {
            filed: 'button',
            title: '操作',
            formatter: function (value, row, index) {
                if (row.status == "D") {
                    return "<div class='btn btn-success' onclick='view()'>查看</div>&nbsp;<div class='btn btn-warning' onclick='modify()'>修改</div>&nbsp;<div class='btn btn-danger' onclick='del()'>删除</div>";
                }else if (row.status == "C") {
                    return "<div class='btn btn-primary' onclick='del()'>查看</div>&nbsp;<div class='btn btn-info' onclick='change()'>变更</div>";
                }
            }
        }]
    };
    $('#reasonTable').bootstrapTable(settings);
});