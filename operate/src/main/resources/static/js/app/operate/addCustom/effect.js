init();
function init() {
    if(sendAndApplyData.length == 0) {
        chart1([], [], []);
    }else {
        var xdata = sendAndApplyData.map((v, k)=>v['date']);
        var ydata1 = sendAndApplyData.map((v, k)=>v['applynum']);
        var ydata2 = sendAndApplyData.map((v, k)=>v['applypassnum']);
        var scheduleid = sendAndApplyData.map((v, k)=>v['scheduleid']);
        chart1(xdata, ydata1, ydata2, scheduleid);
    }
    if(statisApplyData.length == 0) {
        chart2([], []);
        chart3([], []);
    }else {
        var xdata = statisApplyData.map((v, k)=>v['statisday']);
        var ydata1 = statisApplyData.map((v, k)=>v['num']);
        var ydata2 = statisApplyData.map((v, k)=>v['rate']);
        chart2(xdata, ydata1);
        chart3(xdata, ydata2);
    }
}

function chart1(xdata, ydata1, ydata2, scheduleid) {
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
            name: "任务日期"
        }],
        yAxis: [{
            type: "value",
            axisLabel: {
                formatter: "{value}"
            },
            name: "发送申请数量",
            splitArea: {show: false},
            splitLine: {show: false}
        }, {
            type: "value",
            axisLabel: {
                formatter: "{value}"
            },
            name: "通过申请数量",
            splitArea: {show: false},
            splitLine: {show: false}
        }],
        series: [{
            name:'发送申请数量',
            type: 'bar',
            barWidth: "12",
            data: ydata1
        },
            {
                yAxisIndex: 1,
                name:'通过申请数量',
                type: 'bar',
                barWidth: "12",
                data: ydata2
            }
        ]
    };
    let chart = echarts.init(document.getElementById("chart1"), 'macarons');
    chart.on('click', function(params) {
        var scheduleId = scheduleid[params['dataIndex']];
        $.get("/addUser/getStatisApplyData", {headId: headId, scheduleId: scheduleId}, function (r) {
            if(r.code == 200) {
                var data = r.data;
                var xdata = data.map((v, k)=>v['statisday']);
                var ydata1 = data.map((v, k)=>v['num']);
                var ydata2 = data.map((v, k)=>v['rate']);
                chart2(xdata, ydata1);
                chart3(xdata, ydata2);
            }
        });
    });
    chart.setOption(option);
}

function chart2(xdata, ydata) {
    var option ={
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        title: {
            text: '发送申请后申请通过数随时间变化图',
            x: 'center',
            y: 'bottom',
            textStyle: {
                color: '#000',
                fontStyle: 'normal',
                fontWeight: 'normal',
                fontFamily: 'sans-serif',
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
            data: xdata,
            axisTick: {
                alignWithLabel: true
            },
            axisLabel: {
                margin: 20
            },
            name: "效果天数"
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
            barWidth: "12",
            data: ydata
        }]
    };
    let chart = echarts.init(document.getElementById("chart2"), 'macarons');
    chart.setOption(option);
}

function chart3(xdata, ydata) {
    var option =  {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                crossStyle: {
                    color: '#999'
                }
            }
        },
        grid: {
            top: "15%",
            left: "32",
            bottom: "13%",
            right: "15%",
            containLabel: true
        },
        title: {
            text: '发送申请后转化率随时间变化图',
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
                name: "效果天数",
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
                name: '推送转化率',
                axisTick: {show: false},
                splitArea: {show: false},
                splitLine: {show: false}
            }
        ],
        series: [
            {
                name: '推送转化率(%)',
                type: 'line',
                data: ydata
            }
        ]
    };
    let chart = echarts.init(document.getElementById("chart3"), 'macarons');
    chart.setOption(option);
}