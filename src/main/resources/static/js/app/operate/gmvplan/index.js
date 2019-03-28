$(function () {
    var settings = {
        url: "/gmvplan/list",
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
                if (row.status == "D") {
                    return "<div class='btn btn-success' onclick='executeData('+row.yearId+')'>执行</div>&nbsp;<div class='btn btn-warning' onclick='modifyData('+row.yearId+')'>修改</div>&nbsp;<div class='btn btn-danger' onclick='deleteData('+row.yearId+')'>删除</div>";
                }else if (row.status == "C") {
                    return "<a class='btn btn-primary' onclick='viewData('+row.yearId+')'>查看</a>&nbsp;<div class='btn btn-info' onclick='changeData('+row.yearId+')'>变更</div>";
                }
            }
        }]
    };
    $('#gmvPlanTable').bootstrapTable(settings);
});

function executeData(year){

}

function viewData(year){
    $.post("/gmvplan/view", {year: year});
}

function changeData(year){

}

function deleteData(year){

}

function modifyData(year){

}
