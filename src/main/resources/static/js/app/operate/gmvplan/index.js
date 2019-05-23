
$(function () {
    var settings = {
        url: "/target/getPageList",
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
            field: 'ID',
            title: 'ID',
            visible: false
        },{
            field: 'NAME',
            title: '目标名称'
        },{
            field: 'START_DT',
            title: '开始时间'
        }, {
            field: 'END_DT',
            title: '结束时间'
        }, {
            field: 'KPI_NAME',
            title: '指标'
        },{
            field: 'TARGET_VAL',
            title: '目标值'
        },{
            field: 'DIMENSIONS',
            title: '维度&值',
            formatter: function (value, row, index) {
                if(value.length >= 10) {
                    var newVal = value.substr(0, 10) + "...";
                    var title = value.split("|").join("<br/>");
                    return "<a style='color: #000000;border-bottom: 1px solid' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-original-title=\""+title+"\">"+newVal+"</a>";
                }else {
                    return value;
                }
            }
        },{
            title: '操作',
            formatter: function (values, row,index) {
                return "<a class='btn btn-primary btn-sm' href='/target/detail?id="+row.ID+"'><i class='mdi mdi-eye'></i>目标详情</a>" +
                    "&nbsp;&nbsp;<a class='btn btn-danger btn-sm' onclick='deleteDatas()'><i class='mdi mdi-close'></i>删除</a>";
            }
        }],
        onLoadSuccess: function(data){
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $('#gmvPlanTable').bootstrapTable(settings);
});
function deleteDatas(){
    $.confirm({
        title: '提示',
        content: '演示环境，暂不支持删除数据！',
        theme: 'bootstrap',
        type: 'orange',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'btn-blue'
            }
        }
    });
}
function executeData(id){
    $.confirm({
        title: '提示：',
        content: '确定要执行此运营目标?执行状态的运营目标将会滚动计算，不允许再删除！',
        type: 'orange',
        theme: 'bootstrap',
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
        type: 'orange',
        theme: 'bootstrap',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'btn-danger',
                action: function(){
                    $.post("/gmvplan/deleteData", {year: year},function (r) {
                        if(r.code == 200) {
                            toastr.success('删除成功!');
                        }else {
                            toastr.error('删除失败!');
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
