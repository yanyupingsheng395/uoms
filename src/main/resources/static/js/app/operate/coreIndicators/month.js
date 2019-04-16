var xData = function() {
    var data = [];
    for (var i = 1; i < 13; i++) {
        data.push(i + "月份");
    }
    return data;
}();

function getDay() {
    var day = new Array();
    var i = 0;
    while (i < 30) {
        day.push(i + 1);
        i++;
    }
    return day;
}

function getRandom (m,n){
    var num = Math.floor(Math.random()*(m - n) + n);
    return num;
}

function getPeriodRandom(m,n,k) {
    var data = new Array();
    for(var i=0; i<k; i++) {
        data.push(getRandom(m,n));
    }
    return data;
}

//本月按天和去年同期值的对比
var option1 = {
    tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    legend: {
        data: ['本月GMV', '去年同期月GMV', '本月GMV日均', '上月GMV日均', '去年同期月GMV日均'],
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
        name: '日期',
        splitLine:{show: false},
        data: getDay()
    }],
    yAxis: [{
        type: 'value',
        name: 'GMV值（元）',
        splitLine:{show: false},
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            formatter: '{value}'
        }
    }],
    series: [{
        name: '本月GMV',
        type: 'bar',
        data: getPeriodRandom(5000, 10000, 15)
    }, {
        name: '去年同期月GMV',
        type: 'bar',
        data: getPeriodRandom(5000, 10000, 30)
    }, {
        name: '本月GMV日均',
        type: 'line',
        data: getPeriodRandom(4000, 4000, 15)
    }, {
        name: '上月GMV日均',
        type: 'line',
        data: getPeriodRandom(6000, 6000, 15)
    }, {
        name: '去年同期月GMV日均',
        type: 'line',
        data: getPeriodRandom(8000, 8000, 15)
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
        data: ['本年各月GMV值','去年各月GMV值','本年月平均GMV值','去年月平均GMV值']
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
        data: ['1月份', '2月份', '3月份', '4月份', '5月份'],
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
        data: getPeriodRandom(800, 2000,5)
    }, {
        name: '去年各月GMV值',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: getPeriodRandom(800, 2000, 5)
    },{
        name: '本年月平均GMV值',
        type: 'line',
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: getPeriodRandom(1200, 1200,5)
    }, {
        name: '去年月平均GMV值',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: getPeriodRandom(1400, 1400,5)
    }]
};


var option4 = {
    tooltip: {
        show: "true",
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
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
        "data": ['实际GMV', '目标GMV']
    },
    "calculable": false,
    "xAxis": [{
        name: "月份",
        type: 'category',
        axisTick: {
            show: false
        },
        axisLine: {
            show: true,
        },
        data: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12']
    }, {
        type: 'category',
        axisLine: {
            show: false
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            show: false
        },
        splitArea: {
            show: false
        },
        splitLine: {
            show: false
        },
        axisPointer: {
            type: 'none'
        },
        data: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12']
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
        "name": "目标GMV",
        xAxisIndex: 1,
        color: "#ccc",
        "type": "bar",
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
        "data": [300,600,500,800,900,800,870,900,700,800,700,880]
    },

        {
            "name": "实际GMV",
            xAxisIndex: 0,
            "type": "bar",
            "barMaxWidth": 35,
            "barGap": "10%",
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
            "data": [100,200,100,300,500,400,670,800,600,700,600,780]
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
            "data": getMonthRandom(0, 100)
        }
    ]
};


var option7 = {
    tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    legend: {
        data: ['GMV值', '环比增长率'],
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
        name: '日期',
        splitLine:{show: false},
        data: ['20190501','20190502','20190503','20190504','20190505','20190506','20190507','20190508','20190509','20190510','201905011']
    }],
    yAxis: [{
        type: 'value',
        name: 'GMV值（元）',
        splitLine:{show: false},
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            formatter: '{value}'
        }
    }, {
        type: 'value',
        name: '环比增长率（%）',
        splitLine:{show: false},
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            formatter: '{value}'
        }
    }],
    series: [{
        name: '环比增长率',
        type: 'line',
        yAxisIndex: 1,
        data: getPeriodRandom(0, 100, 11)
    },{
        name: 'GMV值',
        type: 'bar',
        yAxisIndex: 0,
        data: getPeriodRandom(0, 1000, 11)
    }]
};

getChangeIndex();
// 变异系数
function getChangeIndex() {
    var code = "<tr style='background-color: #f5f6fa'><td>日期</td>";
    var day = getDay();
    $.each(day, function (k, v) {
        code += "<td> " + v + " </td>";
    });
    code += "</tr>";

    var t1 = getPeriodRandom(0, 100,30);
    var t2 = getPeriodRandom(0, 100,30);
    var t3 = getPeriodRandom(0, 100,30);

    code += "<tr><td>本月</td>";
    $.each(t1, function (k, v) {
        code += "<td>0." + v + " </td>";
    });
    code += "</tr>";

    code += "<tr><td>去年</td>";
    $.each(t1, function (k, v) {
        code += "<td>0." + v + " </td>";
    });
    code += "</tr>";

    code += "<tr><td>上月</td>";
    $.each(t1, function (k, v) {
        code += "<td>0." + v + " </td>";
    });
    code += "</tr>";

    $("#table1").html("").html(code);
}

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

    var chart_period = echarts.init(document.getElementById('chart_period'), 'macarons');
    chart_period.setOption(option7);
}

function year_init() {
    var chart0 = echarts.init(document.getElementById('chart0'), 'macarons');
    chart0.setOption(option0);

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


var option0 = {
    tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    legend: {
        data: ['GMV值','环比增长率'],
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
        name: '年份',
        splitLine:{show: false},
        data: ['201901','201902','201903','201904','201905']
    }],
    yAxis: [{
        type: 'value',
        name: 'GMV值（元）',
        splitLine:{show: false},
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            formatter: '{value}'
        }
    },{
        type: 'value',
        name: '环比增长率（%）',
        splitLine:{show: false},
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            formatter: '{value}'
        }
    }],
    series: [{
        name: '环比增长率',
        type: 'line',
        yAxisIndex: 1,
        data: getPeriodRandom(0, 100, 5)
    },{
        name: 'GMV值',
        type: 'bar',
        yAxisIndex: 0,
        data: getPeriodRandom(0, 1000, 5)
    }]
};
