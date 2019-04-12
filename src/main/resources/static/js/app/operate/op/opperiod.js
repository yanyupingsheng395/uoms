$(function () {

    $('#startDate').datepicker({
        format: 'yyyy-mm-dd',
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true
    });

    $('#endDate').datepicker({
        format: 'yyyy-mm-dd',
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true
    });

    var settings = {
        url: "/op/periodHeaderList",
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
            field: 'PERIOD_TASK_NAME',
            title: '活动名称'
        }, {
            field: 'PERIOD_START_DT',
            title: '活动开始时间'
        }, {
            field: 'PERIOD_END_DT',
            title: '活动结束时间'
        },  {
            field: 'CURRENT_USER_COUNT',
            title: '用户数量'
        }, {
            field: 'CREATE_DT',
            title: '创建时间'
        }, {
            filed: 'button',
            title: '操作',
            formatter: function (value, row, index) {
                var headId=row.PERIOD_HEADER_ID;
                return "<div class='btn btn-primary btn-sm' onclick='view("+headId+")'><i class='mdi mdi-eye'></i>查看名单</div>&nbsp;<div class='btn btn-primary btn-sm' onclick='viewstatis("+headId+")'><i class='mdi mdi-chart-bar-stacked'></i>查看统计信息</div>&nbsp;" +
                    "<div class='btn btn-primary btn-sm' onclick='download("+headId+")'><i class='mdi mdi-download'></i>导出</div>&nbsp;<div class='btn btn-danger btn-sm' onclick='del(\"+headId+\")'><i class='mdi mdi-window-close'></i>删除</div>";
            }
        }]
    };
    $('#periodTable').bootstrapTable(settings);

    //为刷新按钮绑定事件
    $("#btn_refresh").on("click",function () {
        $('#periodTable').bootstrapTable('refresh');
    });

    $('#periodUserListTable').bootstrapTable(
        {
            datatype: 'json',
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
            columns: [
                {
                    field: 'PRODUCT_NAME',
                    title: '产品名称'
                },
                {
                    field: 'COUPON_DISPLAY',
                    title: '优惠券'
                },
                {
                    field: 'USER_ID',
                    title: '用户ID'
                },
                {
                    field: 'DISCOUNT_EFFORT',
                    title: '优惠力度',
                    formatter: function (value, row, index) {
                        if (value == 'H') {
                            return "高";
                        } else if (value == 'M') {
                            return "中";
                        } else {
                            return "低";
                        }
                    }
                }]
        });

});

function del(reasonId) {

    $.alert({
        title: '提示：',
        content: '演示环境，不支持删除！',
        confirm: {
            text: '确认',
            btnClass: 'btn-primary'
        }
    });
    // toastr.options = {
    //     "progressBar": true,
    //     "positionClass": "toast-top-center",
    //     "preventDuplicates": true,
    //     "timeOut": 5000,
    //     "showMethod": "fadeIn",
    //     "hideMethod": "fadeOut"
    // }

    //遮罩层打开
    //lightyear.loading('show');

    //进行删除提示
    //     $.confirm({
    //         title: '提示：',
    //         content: '是否删除数据？',
    //         buttons: {
    //             confirm: {
    //                 text: '确认',
    //                 btnClass: 'btn-blue',
    //                 action: function(){
    //                          $.getJSON("/op/deletePeriodId?reasonId="+reasonId,function (resp) {
    //                                 if (resp.code === 200){
    //                                     lightyear.loading('hide');
    //                                     //提示成功
    //                                     toastr.success("删除成功！");
    //                                     //刷新表格
    //                                     $('#reasonTable').bootstrapTable('refresh');
    //                                 }
    //                             })
    //                 }
    //             },
    //             cancel: {
    //                 text: '取消'
    //             }
    //         }
    //     });
}

function viewstatis(headerId)
{
    $('#statis_modal').modal('show');

    var catRankChart = echarts.init(document.getElementById('cat_rank_chart'), 'macarons');
    catRankChart.setOption(cat_rank_option);

    var timeChart = echarts.init(document.getElementById('time_chart'), 'macarons');
    timeChart.setOption(time_option);

    $('#history_price').text('100');
}

function view(headerId)
{
    var opt={
        url: "/op/getPeriodUserList?headerId="+headerId
    };
    $('#periodUserListTable').bootstrapTable("refresh",opt);
    $('#userlist_modal').modal('show');
}

function download(headerId) {
    $.alert({
        title: '提示：',
        content: '演示环境，不支持下载！',
        confirm: {
            text: '确认',
            btnClass: 'btn-primary'
        }
    });
}

function add() {
    $('#startDt').val("");
    $('#endDt').val("");
    $('#periodName').val("");
    $('#add_modal').modal('show');

}

function savePeriod()
{
    var alert_str="";
    toastr.options = {
        "closeButton": true,
        "progressBar": true,
        "positionClass": "toast-top-center",
        "preventDuplicates": true,
        "timeOut": 5000,
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }

    //验证
    var startDt= $('#startDt').val();
    var endDt= $('#endDt').val();
    var periodName= $('#periodName').val();

    if(null==periodName||periodName=='')
    {
        alert_str+='</br>周期活动名称不能为空！';
    }

    if(null==startDt||startDt=='')
    {
        alert_str+='</br>周期活动开始日期不能为空！';
    }

    if(null==endDt||endDt=='')
    {
        alert_str+='</br>周期活动结束日期不能为空！';
    }

    if(null!=alert_str&&alert_str!='')
    {
        toastr.warning(alert_str);
        return;
    }

    //提示是否要保存
        $.confirm({
            title: '提示：',
            content: '确定要保存数据？',
            buttons: {
                confirm: {
                    text: '确认',
                    btnClass: 'btn-blue',
                    action: function(){
                             $.getJSON("/op/savePeriodHeaderInfo?periodName="+periodName+"&startDt="+startDt+"&endDt="+endDt,function (resp) {
                                    if (resp.code === 200){
                                        //提示成功
                                        $('#add_modal').modal('hide');

                                        $('#periodTable').bootstrapTable('refresh');
                                    }
                                })
                    }
                },
                cancel: {
                    text: '取消'
                }
            }
        });
}

//////////////////////////////////////////////////////////////////////////////////////////
var cat_rank_option = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        }
    },
    xAxis: {
        name: '推荐次数',
        type: 'value'
    },
    yAxis: {
        name: '品类名称',
        type: 'category',
        data: ['品类1','品类2','品类3','品类4','品类5']
    },
    series: [
        {
            type: 'bar',
            data: [15, 12, 5, 3, 1]
        }
    ]
};

var time_option = {
    xAxis: {
        type: 'category',
        data: ['1点', '2点', '3点', '3点', '5点', '6点',
            '7点','8点', '9点', '10点', '11点', '12点', '13点', '14点',
            '15点', '16点', '17点', '18点', '19点', '20点', '21点', '22点', '23点', '24点'],
        axisLabel:{
            interval: 0
        }
    },
    yAxis: {
        name: '订单量',
        type: 'value'
    },
    series: [{
        data: [2, 0, 3, 1, 2, 4, 1,1,0,3,5,7,10,1,2,4,3,1,4,3,7,8,1,2],
        type: 'bar'
    }],
    tooltip : {
        trigger: 'axis',
        axisPointer : {
            type : 'shadow'
        },
        formatter:function (params, ticket) {
            var htmlStr="<h4 style='color: #ffffff'>"+params[0].axisValueLabel+"</h4>"
            htmlStr+="<p style='color: #ffffff'>订单量："+params[0].value+"</p>"
            return htmlStr;
        }
    }
};