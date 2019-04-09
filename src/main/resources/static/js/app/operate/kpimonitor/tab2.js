var xData = function() {
    var data = [];
    for (var i = 1; i < 13; i++) {
        data.push(i + "月份");
    }
    return data;
}();


function initChart() {
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
            "data": ['获取成本回报']

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
            "name": "获取成本回报",
            "type": "line",
            "stack": "获取成本回报",
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
        }]
    };

    var chart4 = echarts.init(document.getElementById('chart4'), 'macarons');
    chart4.setOption(option4);

    var option5 = {
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
            "data": ['毛利回收期']

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
            "name": "毛利回收期",
            "type": "line",
            "stack": "毛利回收期",
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
                19,
                11,
                15,
                14,
                28,
                22,
                14,
                30,
                22,
                6,
                14,
                21
            ]
        }]
    };

    var chart5 = echarts.init(document.getElementById('chart5'), 'macarons');
    chart5.setOption(option5);

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
            "data": ['全生命周期价值', '月客单价', '预估的生命周期长度']
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
            "name": "全生命周期价值",
            "type": "line",
            "stack": "全生命周期价值",
            "data": getMonthRandom()
        },{
            "name": "月客单价",
            "type": "line",
            "stack": "月客单价",
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
            "data": getMonthRandom()
        },{
            "name": "预估的生命周期长度",
            "type": "line",
            "stack": "预估的生命周期长度",
            "data": getMonthRandom()
        }]
    };

    var chart6 = echarts.init(document.getElementById('chart6'), 'macarons');
    chart6.setOption(option6);

    var option7 = {
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
            "data": ['用户平均成本', '平均获客成本', '平均培养成本']
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
            "name": "用户平均成本",
            "type": "line",
            "stack": "用户平均成本",
            "data": getMonthRandom()
        },{
            "name": "平均获客成本",
            "type": "line",
            "stack": "平均获客成本",
            "data": getMonthRandom()
        },{
            "name": "平均培养成本",
            "type": "line",
            "stack": "平均培养成本",
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
            "data": getMonthRandom()
        }]
    };

    var chart7 = echarts.init(document.getElementById('chart7'), 'macarons');
    chart7.setOption(option7);

    setTimeout(function () {
        chart4.resize();
        chart5.resize();
        chart6.resize();
        chart7.resize();
    }, 200)

    chart7.on('click', function (params) {
        if(params.seriesName == "平均培养成本") {
            $("#chartModal").modal('show');
            modal1();
        }
    });

    chart6.on('click', function (params) {
        if(params.seriesName == "月客单价") {
            $("#chartModal").modal('show');
            modal2();
        }
    });
}

function modal1() {
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
            "data": ['商品折扣成本', '优惠促销成本']
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
            "name": "商品折扣成本",
            "type": "line",
            "stack": "商品折扣成本",
            "data": getMonthRandom()
        },{
            "name": "优惠促销成本",
            "type": "line",
            "stack": "优惠促销成本",
            "data": getMonthRandom()
        }]
    };

    var chart = echarts.init(document.getElementById('chart8'), 'macarons');
    chart.setOption(option);
    setTimeout(function () {
        chart.resize();
    }, 200);
}

function modal2() {
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
            "data": ['月购买频次', '月订单价']
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
            "name": "月购买频次",
            "type": "line",
            "stack": "月购买频次",
            "data": getMonthRandom()
        },{
            "name": "月订单价",
            "type": "line",
            "stack": "月订单价",
            "data": getMonthRandom()
        }]
    };

    var chart = echarts.init(document.getElementById('chart8'), 'macarons');
    chart.setOption(option);
    setTimeout(function () {
        chart.resize();
    }, 200);
}

function getRandom (m,n){
    var num = Math.floor(Math.random()*(m - n) + n);
    return num;
}

function getMonthRandom() {
    var data = new Array();
    for(var i=0; i<12; i++) {
        data.push(getRandom(100,900));
    }
    return data;
}

