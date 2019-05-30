/**
 * 适用于'单轴' 柱状图和线图
 *
 * @param legendData 图例名称  Array
 * @param xAxisData  X轴数据  Array
 * @param xAxisData  X轴名称  string
 * @param yAxisData  Y轴名称  string
 * @param seriesData 数据  Array {name:'', type: bar/line', data: []}
 */
function getOption(legendData, xAxisData, xAxisName, yAxisName, seriesData) {
    return {
        tooltip: {
            trigger: 'axis',
            padding: [15, 20],
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: legendData,
            align: 'right',
            right: 10
        },
        xAxis: [{
            type: 'category',
            boundaryGap : false,
            name: xAxisName,
            splitLine:{show: false},
            data: xAxisData
        }],
        yAxis: [{
            type: 'value',
            name: yAxisName,
            splitLine: {show: false},
            axisTick:  {show: false},
            splitArea: {show: false},
            axisLabel: {formatter: '{value}'}
        }],
        series: seriesData
    };
}

/**
 * 柱状图模版
 * @param legendData
 * @param xAxisData
 * @param xAxisName
 * @param yAxisName
 * @param seriesData
 * @returns {{yAxis: {axisLabel: {formatter: string}, splitArea: {show: boolean}, name: *, splitLine: {show: boolean}, axisTick: {show: boolean}, type: string}[], xAxis: {axisLabel: {interval: number}, data: *, name: *, splitLine: {show: boolean}, type: string, boundaryGap: boolean}[], legend: {data: *, right: number, align: string}, series: *, tooltip: {padding: number[], axisPointer: {type: string}, trigger: string}}}
 */
function getBarOption2(legendData, xAxisData, xAxisName, yAxisName, seriesData) {
    return {
        tooltip: {
            trigger: 'axis',
            padding: [15, 20],
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: legendData,
            align: 'right',
            right: 10
        },
        xAxis: [{
            type: 'category',
            boundaryGap : true,
            name: xAxisName,
            splitLine:{show: false},
            data: xAxisData,
            axisLabel:{
                interval: 0
            }
        }],
        yAxis: [{
            type: 'value',
            name: yAxisName,
            splitLine: {show: false},
            axisTick:  {show: false},
            splitArea: {show: false},
            axisLabel: {formatter: '{value}'}
        }],
        series: seriesData
    };
}

// 柱状图
function getBarOption (xAxisData, xAxisName, yAxisName, seriesData, toolXname, toolYname) {
    return {
        xAxis: {
            name: xAxisName,
            type: 'category',
            data: xAxisData,
            axisLabel:{
                interval: 0
            },
            splitLine:{show: false},
            splitArea : {show : false}
        },
        grid: {right:'13%'},
        yAxis: {
            name: yAxisName,
            type: 'value',
            splitLine:{show: false},
            splitArea : {show : false}
        },
        series: [{
            data: seriesData,
            type: 'bar'
        }],
        tooltip : {
            trigger: 'axis',
            axisPointer : {
                type : 'shadow'
            },
            padding: [15, 20],
            extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)',
            formatter:function (params, ticket) {
                var htmlStr="<p style='color: #ffffff'>"+toolXname+"："+params[0].axisValueLabel+"</p>"
                htmlStr+="<p style='color: #ffffff'>"+toolYname+"："+params[0].value+"</p>"
                return htmlStr;
            }
        }
    };
}

/**
 * 获取散点图
 */
function getScatterOption(obj) {
    var series = getScatterSeries(obj, true, max, min);
    var option = {
        tooltip: {},
        grid: {
            right: '12%'
        },
        legend: {data:legend},
        xAxis: {
            name: xname,
            type: "category",
            boundaryGap: false,
            splitArea: {show:false},
            splitLine: {show:false},
            data: xdata
        },
        yAxis: {
            name: yname,
            splitArea: {show:false},
            splitLine: {show:false},
        },
        series: series
    };

    var chart = echarts.init(document.getElementById("t3chart3"), 'macarons');
    chart.setOption(option, true);
    setTimeout(function () {
        chart.resize();
    }, 200);
}

/**
 *
 * @param obj
 * @param flag true:目标指标，false：上层指标
 * @returns {Array}
 */
function getScatterSeries(obj, flag, max, min) {
    var series =  [];
    var data = flag ? obj["areaData"] : obj["lineData"];
    var xdata = obj["xdata"];
    for(var i=0; i<data.length; i++) {
        var o = new Object();
        o.name = data[i].name;
        o.type = 'scatter';
        o.symbol = 'circle';
        var odata = [];
        for(var j=0; j<xdata.length; j++) {
            odata.push([xdata[j], data[i]["data"][j], data[i]["data"][j]]);
        }
        o.data = odata;
        o.symbolSize = function (data) {
            return ((data[2]-min)/(max-min)) * 80; // 归一法
        };
        series.push(o);
    }
    return series;
}


function getXBarOption(obj, chartId) {
    var mainKpi = obj["mainKpiBarData"];
    var xdata = [];
    var ydata = [];
    $.each(mainKpi, function (k, v) {
        xdata.push(v["value"]);
        ydata.push(v["name"]);
    });
    var series = [{type:'bar', data:xdata}];
    var xname = "GMV";

    var option = {
        grid: {
            left: '3%',
            right: '7%',
            bottom: '10%',
            containLabel: true
        },

        tooltip: {
            show:"true",
            trigger: 'axis',
            axisPointer: { // 坐标轴指示器，坐标轴触发有效
                type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        xAxis:  {
            name: xname,
            type: 'value',
            axisTick : {show: false},
            splitLine: {show: false},
            splitArea: {show: false}
        },
        yAxis: [
            {
                type: 'category',
                axisLine: {show:true},
                axisTick: {show:false},
                axisLabel: {show:true},
                splitArea: {show:false},
                splitLine: {show:false},
                data: ydata
            }
        ],
        series: series
    };
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option, true);
    setTimeout(function () {
        chart.resize();
    }, 200);
}

// // 模板2 面积图demo
// function template2(chartId) {
//     var legendData = ["首购", "非首购"];
//     var xAxisData = ["201901", "201902", "201903", "201904", "201905"];
//     var xAxisName = "月份";
//     var yAxisName = "GMV值（元）";
//     var seriesData = [{name: '首购', stack: '总量',type: 'line',areaStyle: {normal: {}}, data:[500254, 452545, 352545, 652545, 552545]},
//         {name: '非首购', stack: '总量',type: 'line', areaStyle: {normal: {}},data:[500254, 452545, 352545, 652545, 552545]}];
//     var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
//     var chart = echarts.init(document.getElementById(chartId), 'macarons');
//     chart.setOption(option);
// }
//
// // 柱状图demo
// function template3_1(chartId) {
//     var legendData = ["客单价"];
//     var xAxisData = ["201901", "201902", "201903", "201904"];
//     var xAxisName = "月份";
//     var yAxisName = "客单价（元）";
//     var seriesData = [{name: '客单价', type: 'bar', data:[100, 200, 400, 300]}];
//     var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
//     var chart = echarts.init(document.getElementById(chartId), 'macarons');
//     chart.setOption(option);
// }
//
// function template3_2(chartId) {
//     var legendData = ["子品类一","子品类二","子品类三","子品类一均线","子品类二均线","子品类三均线"];
//     var xAxisData = ["201901", "201902", "201903", "201904"];
//     var xAxisName = "品类";
//     var yAxisName = "GMV值（元）";
//     var seriesData = [{name: '子品类一', type: 'line', data:[100, 200, 400, 320, 500]},
//         {name: '子品类二', type: 'line', data:[300, 200, 500, 230, 430]},
//         {name: '子品类三', type: 'line', data:[200, 400, 100, 330, 350]},
//         {name: '子品类一均线', type: 'line', data:[200, 200, 200, 200, 200]},
//         {name: '子品类二均线', type: 'line', data:[400, 400, 400, 400, 400]}];
//     var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
//     var chart = echarts.init(document.getElementById(chartId), 'macarons');
//     chart.setOption(option);
// }
//
// function template4_1(chartId) {
//     var legendData = ["GMV值", "均线-5%", "均线", "均线+5%"];
//     var xAxisData = ["200901","200902","200903","200904","200905"];
//     var xAxisName = "月份";
//     var yAxisName = "GMV值（元）";
//     var seriesData = [{name: 'GMV值', type: 'line', data:[1000, 800, 1400, 500, 1300]},
//         {name: '均线-5%', type: 'line', data:[950, 950, 950, 950, 950]},
//         {name: '均线', type: 'line', data:[1000, 1000, 1000, 1000, 1000]},
//         {name: '均线+5%', type: 'line', data:[1050, 1050, 1050, 1050, 1050]}];
//     var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
//     var chart = echarts.init(document.getElementById(chartId), 'macarons');
//     chart.setOption(option);
// }
//
// function template4_2(chartId) {
//     var legendData = ["用户数","均线-5%", "均线", "均线+5%"];
//     var xAxisData = ["200901","200902","200903","200904","200905"];
//     var xAxisName = "月份";
//     var yAxisName = "用户数";
//     var seriesData = [{name: 'GMV值', type: 'line', data:[200, 100, 140, 350, 200]},
//         {name: '均线-5%', type: 'line', data:[120, 120, 120, 120, 120]},
//         {name: '均线', type: 'line', data:[150, 150, 150, 150, 150]},
//         {name: '均线+5%', type: 'line', data:[180, 180, 180, 180, 180]}];
//     var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
//     var chart = echarts.init(document.getElementById(chartId), 'macarons');
//     chart.setOption(option);
// }
//
// // 区域上色demo
// function template4_3(chartId) {
//     var legendData = ["客单价", "均线"];
//     var xAxisData = ["200901","200902","200903","200904","200905"];
//     var xAxisName = "月份";
//     var yAxisName = "客单价（元）";
//     var seriesData = [{name: '客单价', type: 'line', data:[100, 120, 140, 350, 200]},
//         {name: '均线', type: 'line', markArea: {
//                 itemStyle: {
//                     color: '#48b0f7',
//                     opacity: 0.2
//                 },
//                 silent: true,
//                 label: {
//                     normal: {
//                         position: ['10%', '50%']
//                     }
//                 },
//                 data: [
//                     [{
//                         name: '',
//                         yAxis: 121,
//                         x: '0%',
//                         itemStyle: {
//                             normal: {
//                                 color: '#ff0000'
//                             }
//                         },
//                     }, {
//                         yAxis: 260,
//                         x: '100%'
//                     }]
//                 ]
//             },data:[180, 180, 180, 180, 180]}];
//     var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
//     var chart = echarts.init(document.getElementById(chartId), 'macarons');
//     chart.setOption(option);
// }