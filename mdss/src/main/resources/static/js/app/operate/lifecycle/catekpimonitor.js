function prefixInteger(num, length) {
    return (num/Math.pow(10,length)).toFixed(length).substr(2);
}

// 格式化日期
Date.prototype.format=function (){
    var s='';
    s+=this.getFullYear()+'-';          // 获取年份。
    s+=prefixInteger((this.getMonth()+1), 2)+"-";         // 获取月份。
    s+= prefixInteger(this.getDate(),2);                 // 获取日。
    return(s);                          // 返回日期。
};

$('.bootstrap-select').on('click', function() {
    var style = "border-color: #33cabb !important;outline: 0;-webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(51, 202, 187, .6) !important;box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(51, 202, 187, .6) !important;";
    $(this).attr("style", style);
});

// 初始化日期插件
init_date_begin("startDate1", "endDate1", "yyyy-mm-dd", 0,2,0);
init_date_end("startDate1", "endDate1", "yyyy-mm-dd", 0,2,0);
init_date_begin("startDate2","endDate2", "yyyy-mm-dd", 0,2,0);
init_date_end("startDate2","endDate2", "yyyy-mm-dd", 0,2,0);
init_date_begin("startDate3","endDate3", "yyyy-mm-dd", 0,2,0);
init_date_end("startDate3","endDate3", "yyyy-mm-dd", 0,2,0);

// 昨天往前3月
dateValInint();
function dateValInint() {
    var startDt = new Date();
    startDt.setDate(startDt.getDate()-1);
    startDt.setMonth(startDt.getMonth()-3);
    var endDt = new Date();
    endDt.setDate(endDt.getDate()-1);
    $("#startDate1").val(startDt.format());
    $("#endDate1").val(endDt.format());
    $("#startDate2").val(startDt.format());
    $("#endDate2").val(endDt.format());
    $("#startDate3").val(startDt.format());
    $("#endDate3").val(endDt.format());
}
function gmvChart() {
    var spuId = selectId;
    var startDt = $("#startDate1").val();
    var endDt = $("#endDate1").val();
    $.get("/kpiMonitor/getGMV", {startDt: startDt, endDt: endDt, spuId: spuId}, function (r) {
        var legendData = r.data.legendData;
        var seriesData = new Array();
        $.each(r.data.seriesData, function (k, v) {
            v.stack = '总量';
            v.type = 'line';
            v.areaStyle = {normal: {}};
            seriesData.push(v);
        });
        var xAxisName = r.data.xAxisName;
        var yAxisName = r.data.yAxisName;
        var xAxisData = r.data.xAxisData;

        var option = getOption(legendData, xAxisData, xAxisName, yAxisName, seriesData);
        var chart = echarts.init(document.getElementById("chart1"), 'macarons');
        chart.setOption(option);
    });
}
function tradeUserChart() {
    var spuId = selectId;
    var startDt = $("#startDate2").val();
    var endDt = $("#endDate2").val();
    $.get("/kpiMonitor/getTradeUser", {startDt: startDt, endDt: endDt, spuId: spuId}, function (r) {
        var legendData = r.data.legendData;
        var seriesData = new Array();
        $.each(r.data.seriesData, function (k, v) {
            v.stack = '总量';
            v.type = 'line';
            v.areaStyle = {normal: {}};
            seriesData.push(v);
        });
        var xAxisName = r.data.xAxisName;
        var yAxisName = r.data.yAxisName;
        var xAxisData = r.data.xAxisData;

        var option = getOption(legendData, xAxisData, xAxisName, yAxisName, seriesData);
        var chart = echarts.init(document.getElementById("chart2"), 'macarons');
        chart.setOption(option);
    });
}
function avgCsPriceChart() {
    var spuId = selectId;
    var startDt = $("#startDate3").val();
    var endDt = $("#endDate3").val();
    var chart = echarts.init(document.getElementById("chart3"), 'macarons');
    chart.showLoading();
    $.get("/kpiMonitor/getAvgCsPrice", {startDt: startDt, endDt: endDt, spuId: spuId}, function (r) {
        var legendData = r.data.legendData;
        var seriesData = new Array();
        $.each(r.data.seriesData, function (k, v) {
            v.stack = '总量';
            v.type = 'line';
            seriesData.push(v);
        });
        var xAxisName = r.data.xAxisName;
        var yAxisName = r.data.yAxisName;
        var xAxisData = r.data.xAxisData;
        var option = getOption(legendData, xAxisData, xAxisName, yAxisName, seriesData);
        chart.setOption(option);
        chart.hideLoading();
    });
}
