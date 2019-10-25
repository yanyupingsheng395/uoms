$(function () {
    getTaskDt();
    makePushChart();
});

// 获取页面头的当前日期和任务日期
function getTaskDt() {
    $.get("/daily/getCurrentAndTaskDate", {headId:headId}, function (r) {
        let data = r.data;
        $("#taskDt").html('').append('<i class="mdi mdi-alert-circle-outline"></i>当前日期：'+data['currentDt']+'，任务：'+data['taskDt']+'');
    });
}

/**
 * 获取推送结果变化图
 */
function makePushChart() {
    $.get("/daily/getPushData", {headId: headId}, function (r) {
        let data = r.data;
        let xdata = data['xdata'];
        let ydata1 = data['ydata1'];
        let ydata2 = data['ydata2'];
        let ydata3 = data['ydata3'];
        let ydata4 = data['ydata4'];
        let option1 = getChartOption(xdata, ydata1, ydata2, "转化人数(人)", "转化率(%)", '推送转化人数，推送转化率随时间变化图');
        let option2 = getChartOption(xdata, ydata3, ydata4, "转化人数(人)", "转化率(%)", '推送且购买推荐SPU转化人数，推送且购买推荐SPU转化率随时间变化图');
        let chart1 = echarts.init(document.getElementById("chart1"), 'macarons');
        chart1.setOption(option1);
        let chart2 = echarts.init(document.getElementById("chart2"), 'macarons');
        chart2.setOption(option2);
    });
}

function getChartOption(xdata, yData1, yData2, yName1, yName2, title) {
    return {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                crossStyle: {
                    color: '#999'
                }
            }
        },
        grid: {top:'10%'},
        title:{
            text: title,
            x: 'center',
            y: 'bottom',
            textStyle: {
                //文字颜色
                color: '#000',
                //字体风格,'normal','italic','oblique'
                fontStyle: 'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight: 'normal',
                //字体系列
                fontFamily: 'sans-serif',
                //字体大小
                fontSize: 12
            }
        },
        xAxis: [
            {
                type: 'category',
                data: xdata,
                axisPointer: {
                    type: 'shadow'
                }
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: yName1,
                min: 0,
                max: 250,
                interval: 50,
                splitLine: {show: false},
                axisTick:  {show: false},
                splitArea: {show: false},
            },
            {
                type: 'value',
                name: yName2,
                min: 0,
                max: 25,
                interval: 5,
                splitLine: {show: false},
                axisTick:  {show: false},
                splitArea: {show: false}
            }
        ],
        series: [
            {
                name:yName1,
                type:'line',
                data:yData1
            },
            {
                name:yName2,
                type:'line',
                yAxisIndex: 1,
                data:yData2
            }
        ]
    };
}