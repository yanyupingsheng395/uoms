$(function () {

    $('#touchDate').datepicker({
        format: 'yyyy-mm-dd',
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true
    });

    $("#touchDate").datepicker("setDate", new Date());
    $('#cdrq').text($('#touchDate').val());

    //获取当日需要运营的总人数
    $.getJSON("/op/getOpDayHeadInfo?daywid="+$('#touchDate').val(), function (resp) {
        if (resp.code==200){
            $('#cdrq').text($('#touchDate').val());
            $('#cdrs').text(resp.msg);
        }
    });

    //加载明细数据
    $.getJSON("/op/getOpDayDetailAllList?daywid="+$('#touchDate').val(), function (resp) {
        if (resp.code == 200) {
            $('#opdayTable').bootstrapTable('load', resp.data);
            var data = $('#opdayTable').bootstrapTable('getData', true);
            var merge =$(":radio[name='merge']:checked").val();
            //合并单元格
            mergeCells(data, merge, 1);
        }
    });

    //为刷新按钮绑定事件
    $("#btn_query").on("click",function () {

        //获取当日需要运营的总人数
        $.getJSON("/op/getOpDayHeadInfo?daywid=" + $('#touchDate').val(), function (resp) {
            if (resp.code == 200) {
                $('#cdrq').text($('#touchDate').val());
                $('#cdrs').text(resp.msg);
            }
        });

        //加载明细数据
        $.getJSON("/op/getOpDayDetailAllList?daywid=" + $('#touchDate').val(), function (resp) {
            if (resp.code == 200) {
                $('#opdayTable').bootstrapTable('load', resp.data);
                var data = $('#opdayTable').bootstrapTable('getData', true);
                //合并单元格
                var merge =$(":radio[name='merge']:checked").val();
                mergeCells(data, merge, 1);
            }
        });
    });

    $("#btn_downLoad").on("click",function () {
        $.alert({
            title: '提示：',
            content: '演示环境，不支持导出！',
            confirm: {
                text: '确认',
                btnClass: 'btn-primary'
            }
        });
    });

    // 统计图表
    $("#btn_statistics").on("click",function () {
        $("#statis_modal").modal('show');
    });

});

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

$('#opdayTable').bootstrapTable({
        datatype: 'json',
        pagination: true,
        sidePagination: "client",
        pageList: [10, 25, 50, 100],
        columns: [[{
            field: 'USER_ID',
            title: '用户ID',
            rowspan: 2,
            valign:"middle"
        },{
            title: '具体目标',
            colspan: 9
        },{
            title: '策略',
            colspan: 4
        }],[{
                field: 'PATH_ACTIV',
                title: '路径活跃度'
            },
            {
                field: 'USER_VALUE',
                title: '用户价值'
            },
            {
                field: 'SPU_NAME',
                title: 'SPU/品类'
            },{
                field: 'PIECE_PRICE',
                title: '件单价'
            },{
                field: 'JOIN_RATE',
                title: '连带率'
            },{
                field: 'PURCH_TYPE_NUM',
                title: '购买品类种数'
            },{
                field: 'TAR_IDEAL_DT',
                title: '达成目标的理想时间'
            },{
                field: 'TAR_GENERAL_DT',
                title: '达成目标的一般时间'
            },{
                field: 'TAR_DEADLINE',
                title: '达成目标的最后期限'
            },{
                field: 'RECOM_SPU',
                title: '推荐品类'
            },{
                field: 'DISCOUNT_RATE',
                title: '折扣率'
            },{
                field: 'ORDER_PERIOD',
                title: '平均订单时段'
            },{
                field: 'REFER_DENO',
                title: '优惠面额'
            }
        ]]
    });



function mergeCells(data,fieldName,colspan)
{
    //声明一个map计算相同属性值在data对象出现的次数和
    var sortMap = {};
    for(var i = 0 ; i < data.length ; i++){
        for(var prop in data[i]){
            if(prop == fieldName){
                var key = data[i][prop]
                if(sortMap.hasOwnProperty(key)){
                    sortMap[key] = sortMap[key] * 1 + 1;
                } else {
                    sortMap[key] = 1;
                }
                break;
            }
        }
    }
    for(var prop in sortMap){
    }
    var index = 0;
    for(var prop in sortMap){
        var count = sortMap[prop] * 1;
        $("#opdayTable").bootstrapTable('mergeCells',{index:index, field:fieldName, colspan: colspan, rowspan: count});
        index += count;
    }
}

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
        data: [10, 20, 30, 21, 22, 24, 12, 14, 30, 32],
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
        data: ['10', '30', '50', '70', '80', '100', '200', '300', '400', '500'],
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
        data: [34, 10, 50, 55, 90, 124, 42, 74, 60, 32],
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
        data: ['100', '600', '1100', '1600', '2100', '2600', '3200', '3700', '4200', '4700'],
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