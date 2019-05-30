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
                if (value == "D") {
                    return "日";
                }
            }
        }, {
            title: '周期时间',
            formatter: function (value, row, index) {
                return row.beginDt + "至" + row.endDt;
            }
        }, {
            field: 'dimDisplayName',
            title: '维度&值',
            formatter: function (value, row, index) {
                if(value == null) {
                    return "-";
                }else {
                    if(value.length >= 10) {
                        var newVal = value.substr(0, 10) + "...";
                        var title = value.replace(";", ";&nbsp;&nbsp;");
                        return "<a style='color: #000000;border-bottom: 1px solid' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-placement=\"bottom\" data-original-title=\""+title+"\">"+newVal+"</a>";
                    }else if(value.length < 10 && value.length > 0) {
                        return value;
                    }
                    if(value == "" || value == undefined) {
                        return "-";
                    }
                }
            }
        }, {
            field: 'createDt',
            title: '创建时间'
        }, {
            filed: '#',
            title: '操作',
            formatter: function (value, row, index) {
                    return "<a class='btn btn-primary btn-sm' href='/page/diagnosis/view?id="+row.diagId+"'><i class='mdi mdi-eye'></i>查看</a>&nbsp;<div class='btn btn-warning btn-sm' onclick='editDiag("+row.diagId+")'><i class='mdi mdi-pencil'></i>编辑</div>&nbsp;<div class='btn btn-danger btn-sm' onclick='deleteConfirm("+row.diagId+")'><i class='mdi mdi-window-close'></i>删除</div>";
            }
        }],onLoadSuccess: function(data){
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $('#diagTable').bootstrapTable(settings);
});

function editDiag(diagId) {
    location.href = "/diag/edit?id=" + diagId;
}

// function deleteConfirm(id) {
//     $.confirm({
//         title: '提示：',
//         content: '确认删除这条记录？',
//         type: 'orange',
//         theme: 'bootstrap',
//         buttons: {
//             confirm: {
//                 text: '确认',
//                 btnClass: 'btn-danger',
//                 action: function(){
//                     deleteData(id);
//                 }
//             },
//             cancel: {
//                 text: '取消'
//             }
//         }
//     });
// }
function deleteConfirm(id) {
    $.confirm({
        title: '提示：',
        content: '演示环境，数据不可删除！',
        type: 'orange',
        theme: 'bootstrap',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'btn-danger'
            },
            cancel: {
                text: '取消'
            }
        }
    });
}

function deleteData(id) {
    $.post("/diag/deleteById", {id: id}, function (r) {
        if(r.code == 200) {
            toastr.success(r.msg);
        }else {
            toastr.error(r.msg);
        }
        setTimeout(function () {
            $('#diagTable').bootstrapTable('refresh');
        }, 1500)
    });
}