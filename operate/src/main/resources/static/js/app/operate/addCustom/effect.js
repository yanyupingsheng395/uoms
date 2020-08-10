$(function () {
    var xdata=['第1天', '第2天', '第3天', '第4天', '第5天'];
    var ydata=[561,199,548,184,199];
    let chart1 = echarts.init(document.getElementById("chart3"), 'macarons');
    let option1 = getChartOption(xdata, ydata, "推送转化率");
    chart1.setOption(option1);
});

// 获取echart数据
function getChartOption(xdata, yData, yName) {
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
        title: {
            text: '发送申请日期推送后转化率图',
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
                boundaryGap: false,
                axisPointer: {
                    type: 'shadow'
                }
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: yName,
                axisTick: {show: false},
                splitArea: {show: false},
                splitLine: {show: false}
            }
        ],
        series: [
            {
                name: '推送转化率(%)',
                type: 'line',
                data: yData
            }
        ]
    };
}

init();
function init() {
    if(sendAndApplyData.length == 0) {
        chart1([], [], []);
    }else {
        var xdata = sendAndApplyData.map((v, k)=>v['date']);
        var ydata1 = sendAndApplyData.map((v, k)=>v['applyNum']);
        var ydata2 = sendAndApplyData.map((v, k)=>v['applyPassNum']);
        chart1(xdata, ydata1, ydata2);
    }
}

chart1();
function chart1(xdata, ydata1, ydata2) {
    var option ={
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        title: {
            text: '发送申请数&申请通过数随时间变化图',
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
        animation: false,
        grid: {
            top: "15%",
            left: "32",
            bottom: "13%",
            right: "11%",
            containLabel: true
        },
        legend:{
            show:true,
            data:['发送申请数量','通过申请数量'],
        },
        xAxis: [{
            type: "category",
            data: xdata,
            axisTick: {
                alignWithLabel: true
            },
            axisLabel: {
                margin: 20
            },
            name: "自然日"
        }],
        yAxis: [{
            type: "value",
            axisLabel: {
                formatter: "{value}"
            },
            name: "人数",
            splitArea: {show: false},
            splitLine: {show: false}
        }],
        series: [{
            name:'发送申请数量',
            type: 'bar',
            barWidth: "12",
            data: [{
                "value": 3
            }, {
                "value": 6
            },{
                "value": 10
            }, {
                "value": 6
            },{
                "value": 1
            }]
        },
            {
                name:'通过申请数量',
                type: 'bar',
                //silent: true,
                "barWidth": "12",
                //barGap: '-100%', // Make series be overlap
                "data": [{
                    "value": 8
                }, {
                    "value": 4
                },{
                    "value": 8
                }, {
                    "value": 4
                },{
                    "value": 8
                }]
            }
        ]
    };
    let chart = echarts.init(document.getElementById("chart1"), 'macarons');
    chart.setOption(option);
}

chart2();
function chart2() {
    var option ={
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        title: {
            text: '申请通过数随申请日期变化图',
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
        animation: false,
        grid: {
            top: "15%",
            left: "32",
            bottom: "13%",
            right: "15%",
            containLabel: true
        },
        xAxis: [{
            type: "category",
            data: ["20200801", "20200802","20200803","20200804","20200805"],
            axisTick: {
                alignWithLabel: true
            },
            axisLabel: {
                margin: 20
            },
            name: "发送申请日期"
        }],
        yAxis: [{
            type: "value",
            axisLabel: {
                formatter: "{value}"
            },
            name: "通过申请数量",
            splitArea: {show: false},
            splitLine: {show: false}
        }],
        series: [{
            name: '通过申请数量',
            type: 'bar',
            //silent: true,
            "barWidth": "12",
            //barGap: '-100%', // Make series be overlap
            "data": [{
                "value": 3
            }, {
                "value": 6
            },{
                "value": 10
            }, {
                "value": 6
            },{
                "value": 1
            }]
        }]
    };
    let chart = echarts.init(document.getElementById("chart2"), 'macarons');
    chart.setOption(option);
}