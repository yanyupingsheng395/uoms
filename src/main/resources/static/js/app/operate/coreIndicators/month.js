var xData = function() {
    var data = [];
    for (var i = 1; i < 13; i++) {
        data.push(i + "月份");
    }
    return data;
}();
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
        name: '月份',
        data: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']
    }],
    yAxis: [{
        type: 'value',
        name: 'GMV值（元）',
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
    legend: {
        right: 20,
        orient: 'vertical',
        data: ['本年各月GMV值','去年各月GMV值']
    },
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
    xAxis: {
        name:"月份",
        data: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
        splitLine: {
            show: false
        }
    },
    yAxis: {
        type: 'value',
        name: "GMV值（元）",
        splitLine: {
            show: false
        },
        splitArea : {show : false}
    },
    series: [{
        name: '本年各月GMV值',
        type: 'line',
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: getMonthRandom(800, 2000)
    }, {
        name: '去年各月GMV值',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: getMonthRandom(800, 2000)
    }]
};


var option4 = {
    "tooltip": {
        "trigger": "axis",
        "axisPointer": {
            "type": "shadow",
            textStyle: {
                color: "#fff"
            }

        },
    },
    "grid": {
        "borderWidth": 0,
        "top": 110,
        "bottom": 95,
        textStyle: {
            color: "#fff"
        }
    },
    "legend": {
        right: 20,
        orient: 'vertical',
        textStyle: {
            color: '#90979c',
        },
        "data": ['本年实际GMV', '本年目标GMV']
    },
    "calculable": false,
    "xAxis": [{
        "type": "category",
        name: "月份",
        "axisLine": {
            lineStyle: {
                color: '#90979c'
            }
        },
        "splitLine": {
            "show": false
        },
        "axisTick": {
            "show": false
        },
        "splitArea": {
            "show": false
        },
        "axisLabel": {
            "interval": 0,

        },
        "data": ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
    }],
    "yAxis": [{
        "type": "value",
        name: "GMV值（元）",
        "splitLine": {
            "show": false
        },
        "axisLine": {
            lineStyle: {
                color: '#90979c'
            }
        },
        "axisTick": {
            "show": false
        },
        "axisLabel": {
            "interval": 0,

        },
        "splitArea": {
            "show": false
        },

    }],
    "series": [{
        "name": "本年目标GMV",
        "type": "bar",
        "stack": "总量1",
        "barMaxWidth": 35,
        "barGap": "10%",
        "itemStyle": {
            "normal": {
                "label": {
                    "show": true,
                    "textStyle": {
                        "color": "#fff"
                    },
                    "position": "insideTop",
                    formatter: function(p) {
                        return p.value > 0 ? (p.value) : '';
                    }
                }
            }
        },
        "data": getMonthRandom(0, 100),
    },

        {
            "name": "本年实际GMV",
            "type": "bar",
            "stack": "总量2",
            "itemStyle": {
                "normal": {
                    "barBorderRadius": 0,
                    "label": {
                        "show": true,
                        "position": "top",
                        formatter: function(p) {
                            return p.value > 0 ? (p.value) : '';
                        }
                    }
                }
            },
            "data": getMonthRandom(0, 100)
        }
    ]
};

function getRandom (m,n){
    var num = Math.floor(Math.random()*(m - n) + n);
    return num;
}

function getMonthRandom(m,n) {
    var data = new Array();
    for(var i=0; i<12; i++) {
        data.push(getRandom(m,n));
    }
    return data;
}
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
    "tooltip": {
        "trigger": "axis",
        "axisPointer": {
            "type": "shadow",
            textStyle: {
                color: "#fff"
            }

        },
    },
    "grid": {
        "borderWidth": 0,
        "top": 110,
        "bottom": 95,
        textStyle: {
            color: "#fff"
        }
    },
    "legend": {
        right: 20,
        orient: 'vertical',
        textStyle: {
            color: '#90979c',
        },
        "data": ['本年实际GMV', '本年目标GMV']
    },
    "calculable": false,
    "xAxis": [{
        "type": "category",
        name: "月份",
        "axisLine": {
            lineStyle: {
                color: '#90979c'
            }
        },
        "splitLine": {
            "show": false
        },
        "axisTick": {
            "show": false
        },
        "splitArea": {
            "show": false
        },
        "axisLabel": {
            "interval": 0,

        },
        "data": ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
    }],
    "yAxis": [{
        "type": "value",
        name: "利润率（%）",
        "splitLine": {
            "show": false
        },
        "axisLine": {
            lineStyle: {
                color: '#90979c'
            }
        },
        "axisTick": {
            "show": false
        },
        "axisLabel": {
            "interval": 0,

        },
        "splitArea": {
            "show": false
        },

    }],
    "series": [{
            "name": "本年各月利润率",
            "type": "line",
            "itemStyle": {
                "normal": {
                    "barBorderRadius": 0,
                    "label": {
                        "show": true,
                        "position": "top",
                        formatter: function(p) {
                            return p.value > 0 ? (p.value) : '';
                        }
                    }
                }
            },
            "data": getMonthRandom(0, 100)
        }
    ]
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