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
            0.25,
            0.45,
            0.33,
            0.41,
            0.35,
            0.26,
            0.39,
            0.21,
            0.42,
            0.32,
            0.41,
            0.25
        ],
    },

        {
            "name": "老客比例",
            "type": "bar",
            "stack": "总量",
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
            "data": [
                0.33,
                0.28,
                0.56,
                0.34,
                0.21,
                0.12,
                0.45,
                0.36,
                0.42,
                0.26,
                0.53,
                0.11
            ]
        }, {
            "name": "GMV",
            "type": "line",
            "stack": "GMV",
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
            "data": [
                0.58,
                0.83,
                0.89,
                0.75,
                0.56,
                0.38,
                0.74,
                0.85,
                0.64,
                0.53,
                0.49,
                0.87
            ]
        },
    ]
};

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