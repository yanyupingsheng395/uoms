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
        "data": getMonthRandom(0, 100),
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
            "data": getMonthRandom(0, 100)
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
            "data": getMonthRandom(0, 100)
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
        name:"月份",
        data: xData,
        splitLine: {
            show: false
        }
    },
    yAxis: {
        type: 'value',
        name: "用户数",
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
        data: getMonthRandom(1200, 2000)
    }, {
        name: '新用户数',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: getMonthRandom(1400, 2000)
    }, {
        name: '交易用户数',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: getMonthRandom(1500, 2000)
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
        "data": ['客单价', '订单价', '订单量']

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
        "name": "订单量",
        "type": "bar",
        "stack": "订单量",
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
        "name": "订单价",
        "type": "line",
        "stack": "订单价",
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
        "lineStyle": {
            "normal": {
                "width": 2
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
    },{
        "name": "客单价",
        "type": "line",
        "stack": "客单价",
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
            1540,
            1229,
            1356,
            1078,
            1368,
            1124,
            1234,
            1348,
            1264,
            1256,
            1249,
            1387
        ]
    }]
};

var chart3 = echarts.init(document.getElementById('chart3'), 'macarons');
chart3.setOption(option3);

chart3.on('click', function (params) {
    if(params.seriesName == "订单价") {
        $("#chartModal").modal('show');
        modal3();
    }
});

function modal3() {
    var option = {
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
            "data": ['平均连带率', '平均件单价']

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
            "name": "平均连带率",
            "type": "bar",
            "stack": "平均连带率",
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
                572,
                1014,
                1024,
                992,
                1084,
                658,
                1224,
                743,
                1124,
                1034,
                999,
                1124
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

    var chart = echarts.init(document.getElementById('chart8'), 'macarons');
    chart.setOption(option);
    setTimeout(function () {
        chart.resize();
    }, 200);
}