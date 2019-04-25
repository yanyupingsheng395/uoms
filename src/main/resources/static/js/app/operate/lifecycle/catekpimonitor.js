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
        data: ['首购GMV', '非首购GMV']

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
        name: "非首购GMV",
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

var chart1 = echarts.init(document.getElementById('chart1'), 'macarons');
chart1.setOption(option1);

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
        data: ['首购用户数', '非首购用户数']

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
        name: "非首购用户数",
        type: "line",
        areaStyle: {normal: {}},
        stack: "总量",
        data: getMonthRandom(1000, 2000)
    }]
};

var chart2 = echarts.init(document.getElementById('chart2'), 'macarons');
chart2.setOption(option2);


var data_1 = getMonthRandom(1000, 2000);
var data_2 = getMonthRandom(1000, 2000);
var data_3 = arrPlus(data_1, data_2);

function arrPlus(arr1, arr2) {
    var arr = new Array();
    $.each(arr1, function (k, v) {
        arr.push(v + arr2[k]);
    });
    return arr;
}
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
        data: ['首购平均客单价', '非首购平均客单价', '平均客单价']

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
        data: data_1,
    }, {
        name: "非首购平均客单价",
        type: "line",
        data: data_2
    }, {
        name: "平均客单价",
        type: "line",
        data: data_3
    }]
};

var chart3 = echarts.init(document.getElementById('chart3'), 'macarons');
chart3.setOption(option3);