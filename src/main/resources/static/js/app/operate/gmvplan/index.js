toastr.options = {
    "closeButton": true,
    "progressBar": true,
    "positionClass": "toast-top-center",
    "preventDuplicates": true,
    "timeOut": 1500,
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};
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
            field: 'planId',
            title: 'ID',
            visible: false
        },{
            field: 'yearId',
            title: '年份'
        }, {
            field: 'gmvTarget',
            title: 'GMV目标值（元）'
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
                var id = row.planId;
                if (row.status == "D") {
                    return "<div class='btn btn-success btn-sm' onclick='executeData("+id+")'><i class='mdi mdi-check'></i>执行</div>&nbsp;<a class='btn btn-primary btn-sm' onclick='viewData("+year+")'><i class='mdi mdi-eye'></i>查看</a>&nbsp;<div class='btn btn-warning btn-sm' onclick='modifyData("+year+")'><i class='mdi mdi-pencil'></i>修改</div>&nbsp;<div class='btn btn-danger btn-sm' onclick='deleteData("+year+")'><i class='mdi mdi-window-close'></i>删除</div>";
                }else if (row.status == "C") {
                    return "<a class='btn btn-primary btn-sm' onclick='viewData("+year+")'><i class='mdi mdi-eye'></i>查看</a>";
                } else if (row.status == "E") {
                    return "<div class='btn btn-info btn-sm' onclick='changeData("+year+")'><i class='mdi mdi-redo'></i>变更</div>";
                }
            }
        }]
    };
    $('#gmvPlanTable').bootstrapTable(settings);
});

function executeData(id){
    $.confirm({
        title: '提示：',
        content: '确定要执行此运营目标?执行状态的运营目标将会滚动计算，不允许再删除！',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'btn-blue',
                action: function(){
                    $.post("/gmvplan/execute", {id: id}, function (r) {
                        $('#gmvPlanTable').bootstrapTable('refresh');
                    });
                }
            },
            cancel: {
                text: '取消'
            }
        }
    });
}

function viewData(year){
    location.href = "/page/gmvplan/view?year=" + year
}

function changeData(year){
    location.href = "/page/gmvplan/change?year=" + year
}

function deleteData(year){
    $.confirm({
        title: '提示：',
        content: '确认删除该记录？',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'btn-danger',
                action: function(){
                    $.post("/gmvplan/deleteData", {year: year},function (r) {
                        if(r.code == 200) {
                            toastr.success("删除成功！");
                        }else {
                            toastr.error("删除失败！");
                        }
                        setTimeout(function () {
                            $('#gmvPlanTable').bootstrapTable('refresh');
                        }, 1000)
                    });
                }
            },
            cancel: {
                text: '取消'
            }
        }
    });
}

function modifyData(year){
    location.href = "/page/gmvplan/edit?year=" + year
}
