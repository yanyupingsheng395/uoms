Date.prototype.format=function (){
    var s='';
    s+=this.getFullYear()+'/';          // 获取年份。
    s+=(this.getMonth()+1)+"/";         // 获取月份。
    s+= this.getDate();                 // 获取日。
    return(s);                          // 返回日期。
};

//按日查询
function getDayAll(begin,end){
    var dateAllArr = new Array();
    var ab = begin.split("-");
    var ae = end.split("-");
    var db = new Date();
    db.setUTCFullYear(ab[0], ab[1]-1, ab[2]);
    var de = new Date();
    de.setUTCFullYear(ae[0], ae[1]-1, ae[2]);
    var unixDb=db.getTime();
    var unixDe=de.getTime();
    for(var k=unixDb;k<=unixDe;){
        dateAllArr.push((new Date(parseInt(k))).format().toString());
        k=k+24*60*60*1000;
    }
    return dateAllArr;
}

var xData = function() {
    return getDayAll('2019-05-01', '2019-05-15');
}();

//按周查询
function getWeekAll(begin,end){
    var dateAllArr = new Array();
    var ab = begin.split("-");
    var ae = end.split("-");
    var db = new Date();
    db.setUTCFullYear(ab[0], ab[1]-1, ab[2]);
    var de = new Date();
    de.setUTCFullYear(ae[0], ae[1]-1, ae[2]);
    var unixDb=db.getTime();
    var unixDe=de.getTime();
    for(var k=unixDb;k<=unixDe;){
        dateAllArr.push((new Date(parseInt(k))).format().toString());
        k=k+7*24*60*60*1000;
    }
    return dateAllArr;
}

var option1 = {
    tooltip: {
        trigger: "axis",
        axisPointer: {
            type: "shadow",
            textStyle: {
                color: "#fff"
            }

        },
    },
    grid: {
        borderWidth: 0,
        top: 110,
        bottom: 95,
        textStyle: {
            color: "#fff"
        }
    },
    legend: {
        right: 20,
        orient: 'vertical',
        textStyle: {
            color: '#90979c',
        },
        data: ['首购GMV', '复购GMV']

    },
    calculable: true,
    xAxis: [{
        name: "日期",
        boundaryGap: false,
        type: "category",
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        splitLine: {
            "show": false
        },
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            interval: 0,
            // rotate: 40
        },
        data: xData
    }],
    yAxis: [{
        type: "value",
        name: "GMV值（元）",
        splitLine: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            interval: 0,

        },
        splitArea: {
            show: false
        },

    }],
    series: [{
        name: "首购GMV",
        type: "line",
        stack: "总量",
        areaStyle: {normal: {}},
        data: getMonthRandom(1000, 2000),
    }, {
        name: "复购GMV",
        type: "line",
        areaStyle: {normal: {}},
        stack: "总量",
        data: getMonthRandom(1000, 2000)
    }]
};

function getRandom (m,n){
    var num = Math.floor(Math.random()*(m - n) + n);
    return num;
}

function getMonthRandom(m,n) {
    var data = new Array();
    for(var i=0; i<15; i++) {
        data.push(getRandom(m,n));
    }
    return data;
}
initChart();
function initChart() {
    var chart1 = echarts.init(document.getElementById('chart1'), 'macarons');
    chart1.setOption(option1);
}

var option2 = {
    tooltip: {
        trigger: "axis",
        axisPointer: {
            type: "shadow",
            textStyle: {
                color: "#fff"
            }

        },
    },
    grid: {
        borderWidth: 0,
        top: 110,
        bottom: 95,
        textStyle: {
            color: "#fff"
        }
    },
    legend: {
        right: 20,
        orient: 'vertical',
        textStyle: {
            color: '#90979c',
        },
        data: ['首购用户数', '复购用户数']

    },
    calculable: true,
    xAxis: [{
        name: "日期",
        boundaryGap: false,
        type: "category",
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        splitLine: {
            "show": false
        },
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            interval: 0,
            rotate: 40

        },
        grid: {
            left: '10%',
            bottom:'35%'
        },
        data: xData
    }],
    yAxis: [{
        type: "value",
        name: "交易用户数",
        splitLine: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            interval: 0,

        },
        splitArea: {
            show: false
        },

    }],
    series: [{
        name: "首购用户数",
        type: "line",
        stack: "总量",
        areaStyle: {normal: {}},
        data: getMonthRandom(1000, 2000),
    }, {
        name: "复购用户数",
        type: "line",
        areaStyle: {normal: {}},
        stack: "总量",
        data: getMonthRandom(1000, 2000)
    }]
};

var chart2 = echarts.init(document.getElementById('chart2'), 'macarons');
chart2.setOption(option2);


var option3 = {
    tooltip: {
        trigger: "axis",
        axisPointer: {
            type: "shadow",
            textStyle: {
                color: "#fff"
            }

        },
    },
    grid: {
        borderWidth: 0,
        top: 110,
        bottom: 95,
        textStyle: {
            color: "#fff"
        }
    },
    legend: {
        right: 20,
        orient: 'vertical',
        textStyle: {
            color: '#90979c',
        },
        data: ['首购平均客单价', '复购平均客单价']

    },
    calculable: true,
    xAxis: [{
        name: "日期",
        boundaryGap: false,
        type: "category",
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        splitLine: {
            "show": false
        },
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            interval: 0,
            rotate: 40

        },
        grid: {
            left: '10%',
            bottom:'35%'
        },
        data: xData
    }],
    yAxis: [{
        type: "value",
        name: "平均客单价（元）",
        splitLine: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            interval: 0,

        },
        splitArea: {
            show: false
        },

    }],
    series: [{
        name: "首购平均客单价",
        type: "line",
        data: getMonthRandom(1000, 2000),
    }, {
        name: "复购平均客单价",
        type: "line",
        data: getMonthRandom(1000, 2000)
    }]
};

var chart3 = echarts.init(document.getElementById('chart3'), 'macarons');
chart3.setOption(option3);

var option4 = {
    tooltip: {
        trigger: "axis",
        axisPointer: {
            type: "shadow",
            textStyle: {
                color: "#fff"
            }

        },
    },
    grid: {
        borderWidth: 0,
        top: 110,
        bottom: 95,
        textStyle: {
            color: "#fff"
        }
    },
    legend: {
        right: 20,
        orient: 'vertical',
        textStyle: {
            color: '#90979c',
        },
        data: ['首购平均订单价', '复购平均订单价']

    },
    calculable: true,
    xAxis: [{
        name: "日期",
        boundaryGap: false,
        type: "category",
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        splitLine: {
            "show": false
        },
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            interval: 0,
            rotate: 40

        },
        grid: {
            left: '10%',
            bottom:'35%'
        },
        data: xData
    }],
    yAxis: [{
        type: "value",
        name: "平均订单价（元）",
        splitLine: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            interval: 0,

        },
        splitArea: {
            show: false
        },

    }],
    series: [{
        name: "首购平均订单价",
        type: "line",
        data: getMonthRandom(1000, 2000),
    }, {
        name: "复购平均订单价",
        type: "line",
        data: getMonthRandom(1000, 2000)
    }]
};
var chart4 = echarts.init(document.getElementById('chart4'), 'macarons');
chart4.setOption(option4);

var option5 = {
    tooltip: {
        trigger: "axis",
        axisPointer: {
            type: "shadow",
            textStyle: {
                color: "#fff"
            }

        },
    },
    grid: {
        borderWidth: 0,
        top: 110,
        bottom: 95,
        textStyle: {
            color: "#fff"
        }
    },
    legend: {
        right: 20,
        orient: 'vertical',
        textStyle: {
            color: '#90979c',
        },
        data: ['首购平均订单数', '复购平均订单数']

    },
    calculable: true,
    xAxis: [{
        name: "日期",
        boundaryGap: false,
        type: "category",
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        splitLine: {
            "show": false
        },
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            interval: 0,
            rotate: 40

        },
        grid: {
            left: '10%',
            bottom:'35%'
        },
        data: xData
    }],
    yAxis: [{
        type: "value",
        name: "平均订单数",
        splitLine: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            interval: 0,

        },
        splitArea: {
            show: false
        },

    }],
    series: [{
        name: "首购平均订单数",
        type: "line",
        data: getMonthRandom(1000, 2000),
    }, {
        name: "复购平均订单数",
        type: "line",
        data: getMonthRandom(1000, 2000)
    }]
};
var chart5 = echarts.init(document.getElementById('chart5'), 'macarons');
chart5.setOption(option5);

chart4.on('click', function (params) {
    $("#chartModal").modal('show');
});
$('#chartModal').on('shown.bs.modal', function () {
    modal3();
});

var option80 = {
    tooltip: {
        trigger: "axis",
        axisPointer: {
            type: "shadow",
            textStyle: {
                color: "#fff"
            }

        },
    },
    grid: {
        borderWidth: 0,
        top: 110,
        bottom: 95,
        textStyle: {
            color: "#fff"
        }
    },
    legend: {
        right: 20,
        orient: 'vertical',
        textStyle: {
            color: '#90979c',
        },
        data: ['首购平均件单价', '复购平均件单价']

    },
    calculable: true,
    xAxis: [{
        name: "日期",
        boundaryGap: false,
        type: "category",
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        splitLine: {
            "show": false
        },
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            interval: 0,
            rotate: 40

        },
        grid: {
            left: '10%',
            bottom:'35%'
        },
        data: xData
    }],
    yAxis: [{
        type: "value",
        name: "平均件单价",
        splitLine: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            interval: 0,

        },
        splitArea: {
            show: false
        },

    }],
    series: [{
        name: "首购平均件单价",
        type: "line",
        data: getMonthRandom(1000, 2000),
    }, {
        name: "复购平均件单价",
        type: "line",
        data: getMonthRandom(1000, 2000)
    }]
};
var option81 = {
    tooltip: {
        trigger: "axis",
        axisPointer: {
            type: "shadow",
            textStyle: {
                color: "#fff"
            }

        },
    },
    grid: {
        borderWidth: 0,
        top: 110,
        bottom: 95,
        textStyle: {
            color: "#fff"
        }
    },
    legend: {
        right: 20,
        orient: 'vertical',
        textStyle: {
            color: '#90979c',
        },
        data: ['首购平均连带率', '复购平均连带率']

    },
    calculable: true,
    xAxis: [{
        name: "日期",
        boundaryGap: false,
        type: "category",
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        splitLine: {
            "show": false
        },
        axisTick: {
            show: false
        },
        splitArea: {
            show: false
        },
        axisLabel: {
            interval: 0,
            rotate: 40

        },
        grid: {
            left: '10%',
            bottom:'35%'
        },
        data: xData
    }],
    yAxis: [{
        type: "value",
        name: "平均连带率",
        splitLine: {
            show: false
        },
        axisLine: {
            lineStyle: {
                color: '#90979c'
            }
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            interval: 0,

        },
        splitArea: {
            show: false
        },

    }],
    series: [{
        name: "首购平均连带率",
        type: "line",
        data: getMonthRandom(1000, 2000),
    }, {
        name: "复购平均连带率",
        type: "line",
        data: getMonthRandom(1000, 2000)
    }]
};
function modal3() {
    var chart80 = echarts.init(document.getElementById('chart80'), 'macarons');
    chart80.setOption(option80);

    var chart81 = echarts.init(document.getElementById('chart81'), 'macarons');
    chart81.setOption(option81);
}