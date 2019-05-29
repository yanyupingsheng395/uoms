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
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit )+ 1  //页码
            };
        },
        columns: [{
            field: 'reasonName',
            title: '编号'
        }, {
            field: 'progress',
            title: '进度',
            formatter: function (value, row, index) {
                return value == null ? "" : value + "%"
            }
        } ,{
            // R表示计算中 F表示计算完成
            field: 'status',
            title: '状态',
            formatter: function (value, row, index) {
                if (value == "R") {
                    return "<span class='label label-primary'>计算中</span>";
                }else if (value == "F") {
                    return "<span class='label label-primary'>完成</span>";
                }else if (value == "E") {
                    return "<span class='label label-danger'>错误</span>";
                }
            }
        },  {
            field: 'kpiName',
            title: '指标'
        },{
            field: 'periodName',
            title: '周期',
            formatter: function (value, row, index) {
               return row.beginDt+"至"+row.endDt;
            }
        },{
            field: 'source',
            title: '来源'
        }, {
            field: 'dimDisplayName',
            title: '维度&值',
            formatter: function (value, row, index) {
                if(null == value || value == "" || value == undefined) {
                    return "-";
                } else  if(value.indexOf("暂无数据") > -1){
                    return "-";
                }
               else  if(value.length >= 10) {
                    var newVal = value.substr(0, 10) + "...";
                    var title = value.replace(";", ";&nbsp;&nbsp;");
                    return "<a style='color: #000000;border-bottom: 1px solid' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-placement=\"bottom\" data-original-title=\""+title+"\">"+newVal+"</a>";
                }else {
                    return value;
                }
            }
        },{
            field: 'createDt',
            title: '创建时间'
        },{
            filed: 'button',
            title: '操作',
            formatter: function (value, row, index) {
                var reasonId=row.reasonId;
                if(row.status=='R')
                {
                    return "<div class='btn btn-info btn-sm' onclick='updatedata("+reasonId+")'><i class='mdi mdi-redo'></i>更新</div>";
                    // return "";
                }else
                {
                    return "<div class='btn btn-primary btn-sm' onclick='view("+reasonId+")'><i class='mdi mdi-eye'></i>查看</div>&nbsp;<div class='btn btn-danger btn-sm' onclick='del("+reasonId+")'><i class='mdi mdi-window-close'></i>删除</div>";
                }
            }
        }],onLoadSuccess: function(data){
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $('#reasonTable').bootstrapTable(settings);

    //为刷新按钮绑定事件
    $("#btn_refresh").on("click",function () {
        $('#reasonTable').bootstrapTable('refresh');
    });
});

function view(reasonId){
    //进入查看界面 todo 更改为post方式
    location.href = "/reason/viewReason?reasonId=" + reasonId;

}

function del(reasonId) {

    //遮罩层打开
    lightyear.loading('show');

    //进行删除提示
        $.confirm({
            title: '确认',
            content: '是否删除数据？',
            theme: 'bootstrap',
            type: 'orange',
            buttons: {
                confirm: {
                    text: '确认',
                    btnClass: 'btn-blue',
                    action: function(){
                             $.getJSON("/reason/deleteReasonById?reasonId="+reasonId,function (resp) {
                                    if (resp.code === 200){
                                        lightyear.loading('hide');
                                        //提示成功
                                        toastr.success('删除成功!');
                                        //刷新表格
                                        //todo 如果在后面某个页上删除数据后，刷新后还停在当前页
                                        //var pageNum=$('#reasonTable').bootstrapTable('getOptions').pageNumber;
                                        $('#reasonTable').bootstrapTable('refresh');
                                    }
                                })
                    }
                },
                cancel: {
                    text: '取消',
                    action: function () {
                        lightyear.loading('hide');
                    }
                }
            }
        });
}

// function del(reasonId) {
//     //进行删除提示
//     $.confirm({
//         title: '确认',
//         content: '演示环境，数据不可删除！',
//         theme: 'bootstrap',
//         type: 'orange',
//         buttons: {
//             confirm: {
//                 text: '确认',
//                 btnClass: 'btn-blue'
//             },
//             cancel: {
//                 text: '取消'
//             }
//         }
//     });
// }

function updatedata(reasonId)
{
    lightyear.loading('show');
    $.getJSON("/reason/updateProgressById?reasonId="+reasonId,function (resp) {
        if (resp.code === 200){
            lightyear.loading('hide');
            //提示成功
            toastr.success('更新成功!');
            //刷新表格
            $('#reasonTable').bootstrapTable('refresh');
        }
    });
}