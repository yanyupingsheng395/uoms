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
            title: '目标周期',
            formatter: function (values, row, index) {
                if(row["END_DT"] == null) {
                    return row["START_DT"];
                }else {
                    return row["START_DT"] + "至" + row["END_DT"];
                }
            }
        },{
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
                }else if(value != "" && value != null){
                    return value;
                }else {
                    return "-";
                }
            }
        },{
            field: 'STATUS',
            title: '状态',
            formatter: function (value, row, index) {
                switch (value) {
                    case "0":
                        return "<span class=\"label label-default\">待拆解</span>";
                        break;
                    case "1":
                        return "<span class=\"label label-primary\">拆解计算中</span>";
                        break;
                    case "2":
                        return "<span class=\"label label-success\">执行中</span>";
                        break;
                    case "3":
                        return "<span class=\"label label-success\">停止</span>";
                        break;
                    case "4":
                        return "<span class=\"label label-warning\">失效</span>";
                        break;
                    case "-1":
                        return "<span class=\"label label-danger\">异常</span>";
                        break;
                    default:
                        return "-";
                }
            }
        }, {
            title: '操作',
            formatter: function (values, row,index) {
                var res = "";
                switch (row['STATUS']) {
                    case "1":
                        res =  "<a class='btn btn-primary btn-sm' href='/target/detail?id="+row.ID+"'><i class='mdi mdi-eye'></i>目标详情</a>";
                        break;
                    case "2":
                        res =  "<a class='btn btn-primary btn-sm' href='/target/detail?id="+row.ID+"'><i class='mdi mdi-eye'></i>目标详情</a>&nbsp;&nbsp;<a class='btn btn-danger btn-sm' onclick='deleteDatas("+row.ID+")'><i class='mdi mdi-close'></i>删除</a>";
                        break;
                    case "3":
                        res =  "<a class='btn btn-primary btn-sm' href='/target/detail?id="+row.ID+"'><i class='mdi mdi-eye'></i>目标详情</a>&nbsp;&nbsp;<a class='btn btn-danger btn-sm' onclick='deleteDatas("+row.ID+")'><i class='mdi mdi-close'></i>删除</a>";
                        break;
                    case "4":
                        res =  "<a class='btn btn-primary btn-sm' href='/target/detail?id="+row.ID+"'><i class='mdi mdi-eye'></i>目标详情</a>&nbsp;&nbsp;<a class='btn btn-danger btn-sm' onclick='deleteDatas("+row.ID+")'><i class='mdi mdi-close'></i>删除</a>";
                        break;
                    case "-1":
                        res =  "<a class='btn btn-danger btn-sm' onclick='deleteDatas("+row.ID+")'><i class='mdi mdi-close'></i>删除</a>";
                        break;
                    default:
                        res =  "-";
                        break;
                }
                return res;
            }
        }],
        onLoadSuccess: function(data){
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $('#gmvPlanTable').bootstrapTable(settings);
});
function deleteDatas(id){
    $.confirm({
        title: '提示',
        content: '确认删除这条记录？',
        theme: 'bootstrap',
        type: 'orange',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'btn-blue',
                action: function() {
                    delData(id);
                }
            },
            cancel: {
                text: '取消'
            }
        }
    });
}

// 点击删除按钮，将数据状态更新为-2
function delData(id) {
    $.get("/target/deleteDataById", {id: id}, function (r) {
        if(r.code == 200) {
            toastr.success("删除成功！");
            $('#gmvPlanTable').bootstrapTable('refresh');
        }else {
            toastr.error("未知异常！");
        }
    });
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
                    $.post("/target/deleteData", {year: year},function (r) {
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