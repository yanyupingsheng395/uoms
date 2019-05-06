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
                return "<div class='btn btn-primary btn-sm' onclick='view("+headId+")'><i class='mdi mdi-eye'></i>查看名单</div>&nbsp;<div class='btn btn-primary btn-sm' onclick='operation_category(\"+headId+\")'><i class='mdi mdi-webpack'></i>运营品类</div>&nbsp;<div class='btn btn-primary btn-sm' onclick='viewstatis("+headId+")'><i class='mdi mdi-chart-bar-stacked'></i>统计信息</div>&nbsp;<div class='btn btn-primary btn-sm' onclick='vieweffect(\"+headId+\")'><i class='mdi mdi-google-analytics'></i>效果统计</div>&nbsp;" +
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
}

$('#statis_modal').on('shown.bs.modal', function () {
    var timeChart = echarts.init(document.getElementById('time_chart'), 'macarons');
    timeChart.setOption(time_option);

    var discountChart = echarts.init(document.getElementById('discount_chart'), 'macarons');
    discountChart.setOption(discount_option);

    var preferentialChart = echarts.init(document.getElementById('preferential_chart'), 'macarons');
    preferentialChart.setOption(preferential_option);

    var piecePriceChart = echarts.init(document.getElementById('piece_price_chart'), 'macarons');
    piecePriceChart.setOption(piece_price_option);
});

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

// 运营品类
function operation_category() {
    $("#operate_modal").modal('show');
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
            title: '确认',
            content: '确定要保存数据？',
            theme: 'bootstrap',
            type: 'orange',
            buttons: {
                confirm: {
                    text: '确认',
                    btnClass: 'btn-blue',
                    action: function(){
                             $.getJSON("/op/savePeriodHeaderInfo?periodName="+periodName+"&startDt="+startDt+"&endDt="+endDt,function (resp) {
                                    if (resp.code === 200){
                                        //提示成功
                                        $('#add_modal').modal('hide');
                                        toastr.success("保存成功!");
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
// 购买时间分布图
var time_option = {
    xAxis: {
        name: '时间',
        type: 'category',
        data: ['1:00', '2:00', '3:00', '4:00', '5:00', '6:00',
            '7:00','8:00', '9:00', '10:00', '11:00', '12:00', '13:00', '14:00',
            '15:00', '16:00', '17:00', '18:00', '19:00', '20:00', '21:00', '22:00', '23:00', '24:00'],
        axisLabel:{
            interval: 1
        },
        splitLine:{show: false},
        splitArea : {show : false}
    },
    yAxis: {
        name: '订单量',
        type: 'value',
        splitLine:{show: false},
        splitArea : {show : false}
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

// 折扣率分布图
var discount_option = {
    xAxis: {
        name: '折扣率（%）',
        type: 'category',
        data: ['10', '20', '30', '40', '50', '60', '70', '80', '90', '100'],
        axisLabel:{
            interval: 0
        },
        splitLine:{show: false},
        splitArea : {show : false}
    },
    yAxis: {
        name: '推荐次数',
        type: 'value',
        splitLine:{show: false},
        splitArea : {show : false}
    },
    series: [{
        data: [0, 2, 5, 8, 10, 19, 54, 64, 90, 0],
        type: 'bar'
    }],
    tooltip : {
        trigger: 'axis',
        axisPointer : {
            type : 'shadow'
        },
        formatter:function (params, ticket) {
            var htmlStr="<p style='color: #ffffff'>折扣率："+params[0].axisValueLabel+"</p>"
            htmlStr+="<p style='color: #ffffff'>推荐次数："+params[0].value+"</p>"
            return htmlStr;
        }
    }
};

// 优惠面额分布图
var preferential_option = {
    xAxis: {
        name: '优惠面额（元）',
        type: 'category',
        data: ['2', '5', '8', '10', '15', '20', '30', '40', '50'],
        axisLabel:{
            interval: 0
        },
        splitLine:{show: false},
        splitArea : {show : false}
    },
    yAxis: {
        name: '推荐次数',
        type: 'value',
        splitLine:{show: false},
        splitArea : {show : false}
    },
    series: [{
        data: [94, 98, 92, 88, 80, 30, 22, 14, 4],
        type: 'bar'
    }],
    tooltip : {
        trigger: 'axis',
        axisPointer : {
            type : 'shadow'
        },
        formatter:function (params, ticket) {
            var htmlStr="<p style='color: #ffffff'>优惠面额："+params[0].axisValueLabel+"</p>"
            htmlStr+="<p style='color: #ffffff'>推荐次数："+params[0].value+"</p>"
            return htmlStr;
        }
    }
};


// 目标件单价分布图
var piece_price_option = {
    xAxis: {
        name: '目标件单价',
        type: 'category',
        data: ['99', '129', '149', '199', '249', '299', '349', '399', '449', '499'],
        axisLabel:{
            interval: 0
        },
        splitLine:{show: false},
        splitArea : {show : false}
    },
    yAxis: {
        name: '推荐次数',
        type: 'value',
        splitLine:{show: false},
        splitArea : {show : false}
    },
    series: [{
        data: [44, 50, 56, 35, 40, 12, 42, 74, 60, 32],
        type: 'bar'
    }],
    tooltip : {
        trigger: 'axis',
        axisPointer : {
            type : 'shadow'
        },
        formatter:function (params, ticket) {
            var htmlStr="<p style='color: #ffffff'>目标件单价："+params[0].axisValueLabel+"</p>"
            htmlStr+="<p style='color: #ffffff'>推荐次数："+params[0].value+"</p>"
            return htmlStr;
        }
    }
};

function vieweffect() {
    $("#effect_modal").modal('show');
}