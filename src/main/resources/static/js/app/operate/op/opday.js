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
        //  toastr.warning("暂无统计，稍后再试。");
    });

    // 效果统计表
    $("#btn_effect").on("click",function () {
        // $("#effect_modal").modal('show');
        toastr.warning("尚未进行触达，无法查看效果统计。");
    });

});

function getStatisSpu() {
    var touchDt = $("#touchDate").val();
    $.get("/op/getSpuStatis", {touchDt: touchDt}, function (r) {
        var c1 = "<tr class='active'><td>推荐品类</td>";
        var c2 = "<tr><td>推荐次数</td>";
        $.each(r.data, function (k, v) {
            c1 += "<td>" + v.SPU_NAME + "</td>";
            c2 += "<td>" + v.RECOMEND_TIMES + "</td>";
        });
        c1 += "</tr>";
        c2 += "</tr>";
        $("#recomDataTable").html("").html(c1 + c2);
    });
}

// 购买时段分布图
function getChartData(type, id, toolXname, toolYname) {
    var touchDt = $("#touchDate").val();
    $.get("/op/getChartData", {touchDt: touchDt, type: type}, function (r) {
        makeChart(r.data, id, toolXname, toolYname);
    });
}

function makeChart(data, id, toolXname, toolYname) {
    var chart = echarts.init(document.getElementById(id), 'macarons');
    chart.showLoading();
    var seriesData = data.yAxisData;
    var xAxisName = data.xAxisName;
    var yAxisName = data.yAxisName;
    var xAxisData = data.xAxisData;
    var option = getBarOption(xAxisData, xAxisName, yAxisName, seriesData, toolXname, toolYname);
    console.log(option)
    chart.setOption(option, true);
    chart.hideLoading();
}

$('#statis_modal').on('shown.bs.modal', function () {
    getChartData("unit_price", "piece_price_chart", "目标件单价", "推荐次数");
    getChartData("discount_rate", "discount_chart", "折扣率", "推荐次数");
    getChartData("buying_time", "time_chart", "购买时间（小时）", "订单量");
    getChartData("refer_deno", "preferential_chart", "优惠面额", "推荐次数");
    getStatisSpu();
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
                field: 'DISCOUNT_LEVEL',
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
