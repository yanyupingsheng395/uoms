function tab2Init() {
    retention_time();
    freq_time("freq1", "购买次数（复购）", "puchtimes_gap_repurch");
    freq_time("freq2", "购买次数（忠诚）", "puchtimes_gap_loyal");
    freq_time("freq3", "购买次数（衰退）","puchtimes_gap_decline");

    getUnitPriceChart();
    getDtPeriodChart();
    getRateChart();
    getCateChart();
    userCount();
    saleVolume();

    getStageNode();
}

function getStageNode() {
    var spuId = 1;
    $.get("/spuLifeCycle/getStageNode", {spuId: spuId}, function (r) {
        $.each(r.data, function (k, v) {
            $("#stage").find("tbody tr:eq("+k+")").find("td:eq(1)").text(v);
        });
    });
}

function retention_time() {
    var spuId = selectId;
    $.get("/spuLifeCycle/retentionPurchaseTimes", {spuId: spuId}, function (r) {
        var series = r.data.seriesData[0];
        series.type = 'line';
        series.smooth = true;
        var option = getOption(null, r.data.xAxisData, r.data.xAxisName, r.data.yAxisName, series);
        option.tooltip = {formatter:'购买次数：{b}<br/>留存率：{c}%'};
        option.grid = {right:'22%'};
        var freqChart = echarts.init(document.getElementById('retention_freq'), 'macarons');
        freqChart.setOption(option);
    });
}

// 购买次数随时间间隔的变化
function freq_time(chartId, yName, type) {
    var spuId = selectId;
    $.get("/spuLifeCycle/getPurchDateChart", {spuId: spuId, type: type}, function (r) {
        console.log(r);
        var series = r.data.seriesData[0];
        series.type = 'line';
        series.smooth = true;
        var option = getOption(null, r.data.xAxisData, "时间间隔（天）", yName, series);
        option.grid = {right: '26%', left: '14%'};
        option.tooltip = {formatter:'人次：{b}<br/>间隔：{c}'};
        var freqChart = echarts.init(document.getElementById(chartId), 'macarons');
        freqChart.setOption(option);
    });
}

function getUnitPriceChart() {
    var spuId = selectId;
    $.get("/spuLifeCycle/getUnitPriceChart", {spuId: spuId}, function (r) {
        var series = r.data.seriesData[0];
        series.type = 'line';
        series.smooth = true;
        var option = getOption(null, r.data.xAxisData, r.data.xAxisName, r.data.yAxisName, series);
        option.tooltip = {formatter:'购买次数：{b}<br/>&nbsp;&nbsp;件单价：{c}'};
        option.grid = {left: '20%',right:'25%'};
        var chart = echarts.init(document.getElementById('kpi1'), 'macarons');
        chart.setOption(option);
    });
}

function getDtPeriodChart() {
    var spuId = selectId;
    $.get("/spuLifeCycle/getDtPeriodChart", {spuId: spuId}, function (r) {
        var series = r.data.seriesData[0];
        series.type = 'line';
        series.smooth = true;
        var option = getOption(null, r.data.xAxisData, r.data.xAxisName, r.data.yAxisName, series);
        option.tooltip = {formatter:'&nbsp;&nbsp;购买次数：{b}<br/>&nbsp;&nbsp;时间间隔：{c}'};
        option.grid = {left: '20%',right:'25%'};
        var chart = echarts.init(document.getElementById('kpi2'), 'macarons');
        chart.setOption(option);
    });
}


function getRateChart() {
    var spuId = selectId;
    $.get("/spuLifeCycle/getRateChart", {spuId: spuId}, function (r) {
        var series = r.data.seriesData[0];
        series.type = 'line';
        series.smooth = true;
        var option = getOption(null, r.data.xAxisData, r.data.xAxisName, r.data.yAxisName, series);
        option.tooltip = {formatter:'&nbsp;&nbsp;购买次数：{b}<br/>&nbsp;&nbsp;连带率：{c}%'};
        option.grid = {left: '20%',right:'25%'};
        var chart = echarts.init(document.getElementById('kpi3'), 'macarons');
        chart.setOption(option);
    });
}


function getCateChart() {
    var spuId = selectId;
    $.get("/spuLifeCycle/getCateChart", {spuId: spuId}, function (r) {
        var series = r.data.seriesData[0];
        series.type = 'line';
        series.smooth = true;
        var option = getOption(null, r.data.xAxisData, r.data.xAxisName, r.data.yAxisName, series);
        option.tooltip = {formatter:'&nbsp;&nbsp;购买次数：{b}<br/>&nbsp;&nbsp;品类种数：{c}'};
        option.grid = {left: '20%',right:'25%'};
        var chart = echarts.init(document.getElementById('kpi4'), 'macarons');
        chart.setOption(option);
    });
}

function userCount() {
    var spuId = selectId;
    $.get("/spuLifeCycle/getUserCountChart", {spuId: spuId}, function (r) {
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
        var chart = echarts.init(document.getElementById("userCount"), 'macarons');
        chart.setOption(option);
    });

}

function saleVolume() {
    var spuId = selectId;
    $.get("/spuLifeCycle/getSaleVolumeChart", {spuId: spuId}, function (r) {
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
        var chart = echarts.init(document.getElementById("saleVolume"), 'macarons');
        chart.setOption(option);
    });
}