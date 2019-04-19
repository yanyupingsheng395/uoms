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

// 模板2 面积图demo
function template2(chartId) {
    var legendData = ["首购", "非首购"];
    var xAxisData = ["201901", "201902", "201903", "201904", "201905"];
    var xAxisName = "月份";
    var yAxisName = "GMV值（元）";
    var seriesData = [{name: '首购', stack: '总量',type: 'line',areaStyle: {normal: {}}, data:[500254, 452545, 352545, 652545, 552545]},
        {name: '非首购', stack: '总量',type: 'line', areaStyle: {normal: {}},data:[500254, 452545, 352545, 652545, 552545]}];
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
}

// 柱状图demo
function template3_1(chartId) {
    var legendData = ["客单价"];
    var xAxisData = ["201901", "201902", "201903", "201904"];
    var xAxisName = "月份";
    var yAxisName = "客单价（元）";
    var seriesData = [{name: '客单价', type: 'bar', data:[100, 200, 400, 300]}];
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
}

function template3_2(chartId) {
    var legendData = ["子品类一","子品类二","子品类三","子品类一均线","子品类二均线","子品类三均线"];
    var xAxisData = ["201901", "201902", "201903", "201904"];
    var xAxisName = "品类";
    var yAxisName = "GMV值（元）";
    var seriesData = [{name: '子品类一', type: 'line', data:[100, 200, 400, 320, 500]},
        {name: '子品类二', type: 'line', data:[300, 200, 500, 230, 430]},
        {name: '子品类三', type: 'line', data:[200, 400, 100, 330, 350]},
        {name: '子品类一均线', type: 'line', data:[200, 200, 200, 200, 200]},
        {name: '子品类二均线', type: 'line', data:[400, 400, 400, 400, 400]}];
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
}

function template4_1(chartId) {
    var legendData = ["GMV值", "均线-5%", "均线", "均线+5%"];
    var xAxisData = ["200901","200902","200903","200904","200905"];
    var xAxisName = "月份";
    var yAxisName = "GMV值（元）";
    var seriesData = [{name: 'GMV值', type: 'line', data:[1000, 800, 1400, 500, 1300]},
        {name: '均线-5%', type: 'line', data:[950, 950, 950, 950, 950]},
        {name: '均线', type: 'line', data:[1000, 1000, 1000, 1000, 1000]},
        {name: '均线+5%', type: 'line', data:[1050, 1050, 1050, 1050, 1050]}];
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
}

function template4_2(chartId) {
    var legendData = ["用户数","均线-5%", "均线", "均线+5%"];
    var xAxisData = ["200901","200902","200903","200904","200905"];
    var xAxisName = "月份";
    var yAxisName = "用户数";
    var seriesData = [{name: 'GMV值', type: 'line', data:[200, 100, 140, 350, 200]},
        {name: '均线-5%', type: 'line', data:[120, 120, 120, 120, 120]},
        {name: '均线', type: 'line', data:[150, 150, 150, 150, 150]},
        {name: '均线+5%', type: 'line', data:[180, 180, 180, 180, 180]}];
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
}

// 区域上色demo
function template4_3(chartId) {
    var legendData = ["客单价", "均线"];
    var xAxisData = ["200901","200902","200903","200904","200905"];
    var xAxisName = "月份";
    var yAxisName = "客单价（元）";
    var seriesData = [{name: '客单价', type: 'line', data:[100, 120, 140, 350, 200]},
        {name: '均线', type: 'line', markArea: {
                itemStyle: {
                    color: '#48b0f7',
                    opacity: 0.2
                },
                silent: true,
                label: {
                    normal: {
                        position: ['10%', '50%']
                    }
                },
                data: [
                    [{
                        name: '',
                        yAxis: 121,
                        x: '0%',
                        itemStyle: {
                            normal: {
                                color: '#ff0000'
                            }
                        },
                    }, {
                        yAxis: 260,
                        x: '100%'
                    }]
                ]
            },data:[180, 180, 180, 180, 180]}];
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
}