//本月按天和去年同期值的对比
var option1 = {
    // title: {
    //     text: '本月和去年同期GMV值对比'
    // },
    tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    legend: {
        data: ['本月', '去年同期'],
        align: 'right',
        right: 10
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: [{
        type: 'category',
        data: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']
    }],
    yAxis: [{
        type: 'value',
        name: 'GMV值',
        axisLabel: {
            formatter: '{value}'
        }
    }],
    series: [{
        name: '本月',
        type: 'bar',
        data: [1200, 1215, 1400, 1120, 1920, 1200, 1335, 1400, 1420, 1520, 2326, 2211]
    }, {
        name: '去年同期',
        type: 'bar',
        data: [625, 1035, 3400, 1220, 5520, 1326, 1200, 1215, 1400, 1120, 1920, 2000]
    }]
};

// 利润率
var option2 = {
    tooltip: {
        formatter: "{a} <br/>{b} : {c}%"
    },
    series: [{
        name: '',
        type: 'gauge',
        center: ['50%', '55%'], // 默认全局居中
        radius: '80%',
        axisLine: {
            show: false,
            lineStyle: { // 属性lineStyle控制线条样式
                color: [
                    [0.8, '#15c377'],
                    [1, '#CAE1FF']
                ],
                width: 10
            }
        },
        splitLine: {
            show: false
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            show: false
        },
        pointer: {
            show: false,
            length: '0',
            width: '0'
        },
        detail: {
            formatter: '{value}%',
            offsetCenter: [0, '5%']
        },
        data: [{
            value: 4,
            label: {
                textStyle: {
                    fontSize: 12
                }
            }
        }]
    }]
};

var option3 = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            lineStyle: {
                color: '#ddd'
            }
        },
        padding: [5, 10],
        extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'
    },
    legend: {
        right: 20,
        orient: 'vertical',
        data: ['本年各月GMV值','去年各月GMV值']
    },
    xAxis: {
        type: 'category',
        data: ['01','02','03','04','05','06','07','08','09','10','11', '12'],
        boundaryGap: false,
        splitLine: {
            show: false,
            interval: 'auto',
            lineStyle: {
                color: ['#D4DFF5']
            }
        },
        axisTick: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#609ee9'
            }
        },
        axisLabel: {
            margin: 10,
            textStyle: {
                fontSize: 14
            }
        }
    },
    yAxis: {
        type: 'value',
        name: 'GMV值',
        splitLine: {
            // show: false,
            lineStyle: {
                color: ['#D4DFF5']
            }
        },
        axisTick: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#609ee9'
            }
        },
        axisLabel: {
            margin: 10,
            textStyle: {
                fontSize: 14
            }
        }
    },
    series: [{
        name: '本年各月GMV值',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: ['1200', '1400', '1008', '1411', '1026', '', '', '', '', '', '', ''],
        areaStyle: {
            normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: 'rgba(199, 237, 250,0.5)'
                }, {
                    offset: 1,
                    color: 'rgba(199, 237, 250,0.2)'
                }], false)
            }
        },
        itemStyle: {
            normal: {
                color: '#f7b851'
            }
        },
        lineStyle: {
            normal: {
                width: 3
            }
        }
    }, {
        name: '去年各月GMV值',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: ['1200', '1400', '1208', '1211', '1326', '1288', '1100', '1100', '1500', '1300', '1498', '1822'],
        areaStyle: {
            normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: 'rgba(216, 244, 247,1)'
                }, {
                    offset: 1,
                    color: 'rgba(216, 244, 247,1)'
                }], false)
            }
        },
        itemStyle: {
            normal: {
                color: '#58c8da'
            }
        },
        lineStyle: {
            normal: {
                width: 3
            }
        }
    }]
};

var option4 = {
    tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    legend: {
        data: ['本年实际GMV', '本年目标GMV'],
        align: 'right',
        right: 10
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: [{
        type: 'category',
        data: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']
    }],
    yAxis: [{
        type: 'value',
        name: 'GMV值',
        axisLabel: {
            formatter: '{value}'
        }
    }],
    series: [{
        name: '本年实际GMV',
        type: 'bar',
        data: [12000, 12500, 14000, 22000, 0, 0, 0, 0, 0, 0, 0, 0]
    }, {
        name: '本年目标GMV',
        type: 'bar',
        data: [12500, 10500, 24000, 22200, 25200, 13200, 12000, 12150, 14000, 12000, 19200, 10250]
    }]
};

// 利润率
var option5 = {
    tooltip: {
        formatter: "{a} <br/>{b} : {c}%"
    },
    series: [{
        name: '',
        type: 'gauge',
        center: ['50%', '55%'], // 默认全局居中
        radius: '80%',
        axisLine: {
            show: false,
            lineStyle: { // 属性lineStyle控制线条样式
                color: [
                    [0.8, '#15c377'],
                    [1, '#CAE1FF']
                ],
                width: 10
            }
        },
        splitLine: {
            show: false
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            show: false
        },
        pointer: {
            show: false,
            length: '0',
            width: '0'
        },
        detail: {
            formatter: '{value}%',
            offsetCenter: [0, '5%']
        },
        data: [{
            value: 3,
            label: {
                textStyle: {
                    fontSize: 12
                }
            }
        }]
    }]
};


var option6 = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            lineStyle: {
                color: '#fff'
            }
        },

        padding: [5, 10],
        textStyle: {
            color: '#fff',
        },
        extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'
    },
    legend: {
        right: 20,
        orient: 'vertical',
        data: ['本年各月利润率']
    },
    xAxis: {
        type: 'category',
        data: ['01','02','03','04','05','06','07','08','09','10','11', '12'],
        boundaryGap: false,
        splitLine: {
            show: false
        },
        axisTick: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#609ee9'
            }
        },
        axisLabel: {
            margin: 10,
            textStyle: {
                fontSize: 14
            }
        }
    },
    yAxis: {
        type: 'value',
        name: '利润率',
        splitLine: {
            lineStyle: {
                color: ['#D4DFF5']
            }
        },
        axisTick: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#609ee9'
            }
        },
        axisLabel: {
            margin: 10,
            textStyle: {
                fontSize: 14
            }
        }
    },
    series: [{
        name: '本年各月利润率',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: ['25', '33.21', '42', '12', '44', '', '', '', '', '', '', ''],
        areaStyle: {
            normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: 'rgba(199, 237, 250,0.5)'
                }, {
                    offset: 1,
                    color: 'rgba(199, 237, 250,0.2)'
                }], false)
            }
        },
        itemStyle: {
            normal: {
                color: '#f7b851'
            }
        },
        lineStyle: {
            normal: {
                width: 3
            }
        }
    }]
};
init();
function init() {
    var gmvChart = echarts.init(document.getElementById('gmvChart'), 'macarons');
    gmvChart.setOption(option1);

    var profitChart = echarts.init(document.getElementById('profitChart'), 'macarons');
    profitChart.setOption(option2);

    $("#tabs").find("li").click(function() {
        $(this).addClass("active");
        $(this).siblings().removeClass("active");

        if($(this).index() == 0) {
            $("#tab_month").attr("style", "display:block");
            $("#tab_year").attr("style", "display:none");
        }else {
            $("#tab_month").attr("style", "display:none");
            $("#tab_year").attr("style", "display:block");
            year_init();
        }
    });
}

// $("#tabs li").each(function(k, v) {
//     $(this).click(function () {
//         alert(k);
//     });
// });

function year_init() {
    var chart1 = echarts.init(document.getElementById('chart1'), 'macarons');
    chart1.setOption(option3);

    var chart2 = echarts.init(document.getElementById('chart2'), 'macarons');
    chart2.setOption(option4);

    var chart3 = echarts.init(document.getElementById('chart3'), 'macarons');
    chart3.setOption(option5);

    var chart4 = echarts.init(document.getElementById('chart4'), 'macarons');
    chart4.setOption(option6);
}

function selectOnchang(obj) {
    var index = obj.selectedIndex;
    if(index == 0) {
        $("#gmv").attr("style", "display:block;");
        $("#profit").attr("style", "display:none;");
    }else {
        $("#gmv").attr("style", "display:none;");
        $("#profit").attr("style", "display:block;");
    }

}