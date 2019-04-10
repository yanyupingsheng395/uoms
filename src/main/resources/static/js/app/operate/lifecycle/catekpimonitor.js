var xData = function() {
    var data = [];
    for (var i = 1; i < 13; i++) {
        data.push(i + "月份");
    }
    return data;
}();

var option1 = {
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
        "data": ['新客比例', '老客比例', 'GMV']

    },
    "calculable": true,
    "xAxis": [{
        "type": "category",
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
        "data": xData,
    }],
    "yAxis": [{
        "type": "value",
        name: "占比（%）",
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

    }, {
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
        "name": "新客比例",
        "type": "bar",
        "stack": "总量",
        yAxisIndex:0,
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
            "name": "老客比例",
            "type": "bar",
            "stack": "总量",
            yAxisIndex:0,
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
        }, {
            "name": "GMV",
            "type": "line",
            "stack": "GMV",
            yAxisIndex:1,
            symbolSize:10,
            symbol:'circle',
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
            "data": getMonthRandom(1000, 2000)
        },
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

var chart1 = echarts.init(document.getElementById('chart1'), 'macarons');
chart1.setOption(option1);

var option2 = {
    legend: {
        right: 20,
        orient: 'vertical',
        data: ['老用户数','新用户数', '交易用户数']
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
        data: xData,
        splitLine: {
            show: false
        }
    },
    yAxis: {
        type: 'value',
        splitLine: {
            show: false
        },
        splitArea : {show : false}
    },
    series: [{
        name: '老用户数',
        type: 'line',
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: ['1200', '1400', '1008', '1411', '1026', '1288', '1300', '800', '1100', '1000', '1118', '1322']
    }, {
        name: '新用户数',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
            normal: {
                color: '#00BFFF'
            }
        },
        data: ['1200', '1400', '808', '811', '626', '488', '1600', '1100', '500', '300', '1998', '822']
    }, {
        name: '交易用户数',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        itemStyle: {
            normal: {
                color: '#f7b851'
            }
        },
        symbolSize: 6,
        data: ['', '', '', '1225', '1126', '1388', '1100', '1010', '1300', '1230', '1218', '1122']
    }]
};

var chart2 = echarts.init(document.getElementById('chart2'), 'macarons');
chart2.setOption(option2);

var option3 = {
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
        "data": ['平均件单价', '平均购买件数']

    },
    "calculable": true,
    "xAxis": [{
        "type": "category",
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
        "data": xData,
    }],
    "yAxis": [{
        "type": "value",
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
        "name": "平均购买件数",
        "type": "bar",
        "stack": "平均购买件数",
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
        "data": [
            578,
            1069,
            678,
            788,
            452,
            607,
            878,
            656,
            787,
            487,
            987,
            787
        ],
    }, {
        "name": "平均件单价",
        "type": "line",
        "stack": "平均件单价",
        symbolSize:10,
        symbol:'circle',
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
        "itemStyle": {
            "normal": {
                "color": '#f7b851'
            }
        },
        "data": [
            540,
            1024,
            1356,
            1267,
            1368,
            1027,
            1125,
            1136,
            1264,
            1056,
            1149,
            1087
        ]
    }]
};

var chart3 = echarts.init(document.getElementById('chart3'), 'macarons');
chart3.setOption(option3);