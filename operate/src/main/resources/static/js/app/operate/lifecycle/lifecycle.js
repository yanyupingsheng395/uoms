function tab2Init() {
    retention_time();
    getUnitPriceChart();
    getDtPeriodChart();
    getRateChart();
    getCateChart();
    getStageNode();
    getSampleDate();
}

function getSampleDate() {
    var spuId = selectId;
    $.getJSON("/spuLifeCycle/getSampleDate", {spuId: spuId}, function (r) {
        var data = r.data;
        if(data == null || data == undefined) {
            data = '--';
        }
        $("#sampleDate").html("").append(data);
    });
}

function getStageNode() {
    var spuId = selectId;
    var yAxisTitle = new Array();
    $.get("/spuLifeCycle/getStageNode", {spuId: spuId}, function (r) {
        $.each(r.data, function (k, v) {
            var content = "";
            if(v == "0" || v == 0) {
                if(k == 0) {
                    content = "该品类购买人数过少，尚未形成购买规律";
                    yAxisTitle.push("首购到复购[1次-]");
                }
                if(k == 1) {
                    content = "该品类尚未形成忠诚购买规律";
                    yAxisTitle.push("成长期["+r.data[0]+"次-]");
                }
                if(k == 2) {
                    content = "该品类尚未出现衰退趋势";
                    yAxisTitle.push("成熟期["+r.data[1]+"次-]");
                }
                v = "-";
            }else {
                if(k == 0) {
                    content = "从该点开始，出现复购行为";
                    yAxisTitle.push("首购到复购[1次-" + v + "次]");
                }
                if(k == 1) {
                    content = "从该点开始，留存率变化趋于平缓";
                    yAxisTitle.push("成长期["+r.data[0]+"次-" + v + "次]");
                }
                if(k == 2) {
                    content = "从该点开始，留存率变化呈明显下降的趋势";
                    yAxisTitle.push("成熟期["+r.data[1]+"次-" + v + "次]");
                }
            }
            $("#stage").find("tbody tr:eq("+k+")").find("td:eq(1)").text(v);
            $("#stage").find("tbody tr:eq("+k+")").find("td:eq(2)").text(content);
        });

        freq_time("freq1", yAxisTitle[0], "puchtimes_gap_repurch");
        freq_time("freq2", yAxisTitle[1], "puchtimes_gap_loyal");
        freq_time("freq3", yAxisTitle[2],"puchtimes_gap_decline");
    });
}

function getRetentionByMethod(purchTimes, type) {
    var data = null;
    $.ajax({
        url: "/fitdata/generateFittingData",
        type: 'post',
        data:{spuId: selectId, purchCount: purchTimes, type: type},
        async: false,
        success: function (r) {
            data = r.data;
        }
    });
    return data;
}

// 留存率随购买次数变化
function retention_time() {
    var spuId = selectId;
    $.get("/spuLifeCycle/retentionPurchaseTimes", {spuId: spuId}, function (r) {
        var chart = r.data;
        var seriesDatas = new Array();
        var series0 = chart.seriesData[0];
        series0.type = 'line';
        series0.smooth = true;
        var legend = ["实际值"];
        if(series0.markPoint != undefined) {
            series0.markPoint.tooltip = {
                show: true, // 是否显示
                formatter: '从该点开始购买次数过少', // 内容格式器 a（系列名称），b（类目值），c（数值）, d（无）
                trigger: 'item' // 触发类型，默认数据触发，见下图，可选为：'item' | 'axis'
            };
            series0.markPoint.symbolSize = 30;
            series0.name = "实际值";

            var x = chart.xAxisData[parseInt(series0.markPoint.data[0].xAxis)];
            var xdata = new Array();
            $.each(chart.xAxisData, function (k, v) {
                xdata.push(v);
            });
            var series1Data = getRetentionByMethod(xdata.join(","), "formula");
            if(series1Data != null) {
                series1Data = series1Data.filter(function(value, index, arr) {
                    return  value >= 0;
                });
            }
            var flag = checkIfFitting(series1Data);
            if(flag) {
                var series1 = new Object();
                series1.data = series1Data;
                series1.type = 'line';
                series1.smooth = true;
                series1.name = "拟合值";
                series1.itemStyle = {
                    normal: {
                        color: '#F9A589',
                        borderWidth: 3,
                        borderColor: "#F9A589"
                    }
                };
                seriesDatas.push(series1);
                legend.push("拟合值");
            }
        }
        seriesDatas.push(series0);
        var option = getOption(legend, chart.xAxisData, chart.xAxisName, chart.yAxisName, seriesDatas);
        option.grid = {right:'22%'};
        if(flag) {
            option.legend.selected = {'实际值':true, '拟合值':false};
            $("#fitRemark").attr("style", "display:none;");
        }else {
            $("#fitRemark").attr("style", "display:block;");
        }
        var freqChart = echarts.init(document.getElementById('retention_freq'), 'macarons');
        freqChart.setOption(option, true);
    });
}

// 判断是否有拟合曲线
function checkIfFitting(data) {
    var flag = false;
    if(data != null && data != undefined) {
        $.each(data, function (k, v) {
            if(parseInt(v) != 0) {
                flag = true;
            }
        });
    }
    return flag;
}

// 每个阶段购买间隔柱状图
function freq_time(chartId, title, type) {
    var spuId = selectId;
    $.get("/spuLifeCycle/getStagePeriodData", {spuId: spuId, type: type}, function (r) {
        var series = r.data.seriesData[0];
        // series.symbol = "none";
        series.type = 'line';
        series.smooth = true;
        var option = getOption(null, r.data.xAxisData, "间隔（天）", "人次", series);
        option.grid = {right: '19%', left: '7%'};
        option.tooltip = {formatter:'人次：{c}次<br/>&nbsp;&nbsp;间隔：{b}天'};
        var freqChart = echarts.init(document.getElementById(chartId), 'macarons');
        option.title = {
            text: title,
            x:'center',
            y: 'bottom',
            textStyle:{
                //文字颜色
                color:'#000',
                //字体风格,'normal','italic','oblique'
                fontStyle:'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight:'normal',
                //字体系列
                fontFamily:'sans-serif',
                //字体大小
                fontSize:12
            }
        };
        freqChart.setOption(option);
    });
}

function getUnitPriceChart() {
    var spuId = selectId;
    $.get("/spuLifeCycle/getUnitPriceChart", {spuId: spuId}, function (r) {
        var chart = r.data;
        chart = getFittingDataOfKpi(chart, "uprice_purch_formula");
        var option = getOption(chart.legend, chart.xAxisData, chart.xAxisName, chart.yAxisName, chart.seriesDatas);
        option.tooltip = {formatter:'购买次数：{b}<br/>&nbsp;&nbsp;件单价：{c}'};
        option.grid = {left: '20%',right:'25%'};
        if(chart.flag) {
            option.legend.selected = {'实际值':true, '拟合值':false};
            $("#kpi1Tip").attr("style", "display:none;");
        }else {
            $("#kpi1Tip").attr("style", "display:block;");
        }
        option.title = {
            text: '件单价随购买次数变化趋势图',
            x:'center',
            y: 'bottom',
            textStyle:{
                //文字颜色
                color:'#000',
                //字体风格,'normal','italic','oblique'
                fontStyle:'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight:'normal',
                //字体系列
                fontFamily:'sans-serif',
                //字体大小
                fontSize:12
            }
        };
        var chart = echarts.init(document.getElementById('kpi1'), 'macarons');
        chart.setOption(option);
    });
}

function getDtPeriodChart() {
    var spuId = selectId;
    $.get("/spuLifeCycle/getDtPeriodChart", {spuId: spuId}, function (r) {
        var chart = r.data;
        chart = getFittingDataOfKpi(chart, "date_purch_formula");
        var option = getOption(chart.legend, chart.xAxisData, chart.xAxisName, chart.yAxisName, chart.seriesDatas);
        option.tooltip = {formatter:'&nbsp;&nbsp;购买次数：{b}<br/>&nbsp;&nbsp;时间间隔：{c}'};
        option.grid = {left: '20%',right:'25%'};
        if(chart.flag) {
            option.legend.selected = {'实际值':true, '拟合值':false};
            $("#kpi2Tip").attr("style", "display:none;");
        }else {
            $("#kpi2Tip").attr("style", "display:block;");
        }
        var chart = echarts.init(document.getElementById('kpi2'), 'macarons');
        option.title = {
            text: '时间间隔随购买次数变化趋势图',
            x:'center',
            y: 'bottom',
            textStyle:{
                //文字颜色
                color:'#000',
                //字体风格,'normal','italic','oblique'
                fontStyle:'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight:'normal',
                //字体系列
                fontFamily:'sans-serif',
                //字体大小
                fontSize:12
            }
        };
        chart.setOption(option);
    });
}


function getRateChart() {
    var spuId = selectId;
    $.get("/spuLifeCycle/getRateChart", {spuId: spuId}, function (r) {
        var chart = r.data;
        chart = getFittingDataOfKpi(chart, "rate_purch_formula");
        var option = getOption(chart.legend, chart.xAxisData, chart.xAxisName, chart.yAxisName, chart.seriesDatas);
        option.tooltip = {formatter:'&nbsp;&nbsp;购买次数：{b}<br/>&nbsp;&nbsp;连带率：{c}'};
        option.grid = {left: '20%',right:'25%'};
        if(chart.flag) {
            option.legend.selected = {'实际值':true, '拟合值':false};
            $("#kpi3Tip").attr("style", "display:none;");
        }else {
            $("#kpi3Tip").attr("style", "display:block;");
        }
        var chart = echarts.init(document.getElementById('kpi3'), 'macarons');
        option.title = {
            text: '连带率随购买次数变化趋势图',
            x:'center',
            y: 'bottom',
            textStyle:{
                //文字颜色
                color:'#000',
                //字体风格,'normal','italic','oblique'
                fontStyle:'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight:'normal',
                //字体系列
                fontFamily:'sans-serif',
                //字体大小
                fontSize:12
            }
        };
        chart.setOption(option);
    });
}

// 封装KPI拟合曲线
function getFittingDataOfKpi(chart, type) {
    var seriesDatas = new Array();
    var series0 = chart.seriesData[0];
    series0.type = 'line';
    series0.smooth = true;
    series0.name = "实际值";
    var legend = ["实际值"];

    var xdata = new Array();
    $.each(chart.xAxisData, function (k, v) {
        xdata.push(v);
    });

    var series1Data = getRetentionByMethod(xdata.join(","), type);
    if(series1Data != null) {
        series1Data = series1Data.filter(function(value, index, arr) {
            return  value >= 0;
        });
    }
    var flag = checkIfFitting(series1Data);
    if(flag) {
        var series1 = new Object();
        series1.data = series1Data;
        series1.type = 'line';
        series1.smooth = true;
        series1.name = "拟合值";
        series1.itemStyle = {
            normal: {
                color: '#F9A589',
                    borderWidth: 3,
                    borderColor: "#F9A589"
            }
        };
        seriesDatas.push(series1);
        legend.push("拟合值");
    }
    seriesDatas.push(series0);
    chart.seriesDatas = seriesDatas;
    chart.legend = legend;
    chart.flag = flag;
    return chart;
}


function getCateChart() {
    var spuId = selectId;
    $.get("/spuLifeCycle/getCateChart", {spuId: spuId}, function (r) {
        var chart = r.data;
        chart = getFittingDataOfKpi(chart, "cate_purch_formula");
        var option = getOption(chart.legend, chart.xAxisData, chart.xAxisName, chart.yAxisName, chart.seriesDatas);
        option.tooltip = {formatter:'&nbsp;&nbsp;购买次数：{b}<br/>&nbsp;&nbsp;品类种数：{c}'};
        option.grid = {left: '20%',right:'25%'};
        if(chart.flag) {
            option.legend.selected = {'实际值':true, '拟合值':false};
            $("#kpi4Tip").attr("style", "display:none;");
        }else {
            $("#kpi4Tip").attr("style", "display:block;");
        }
        var chart = echarts.init(document.getElementById('kpi4'), 'macarons');
        option.title = {
            text: '品类种数随购买次数变化趋势图',
            x:'center',
            y: 'bottom',
            textStyle:{
                //文字颜色
                color:'#000',
                //字体风格,'normal','italic','oblique'
                fontStyle:'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight:'normal',
                //字体系列
                fontFamily:'sans-serif',
                //字体大小
                fontSize:12
            }
        };
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
        option.yAxis[0].axisLabel = {
            margin: 2,
            formatter: function (value, index) {
                if (value >= 10000 && value < 10000000) {
                    value = value / 10000 + "万";
                } else if (value >= 10000000) {
                    value = value / 10000000 + "千万";
                }
                return value;
            }
        };
        var chart = echarts.init(document.getElementById("saleVolume"), 'macarons');
        chart.setOption(option);
    });
}