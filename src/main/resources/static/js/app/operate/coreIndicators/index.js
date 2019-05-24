// 获取监控目标
getTargetList();
function getTargetList() {
    $.ajax({
        url: "/target/getTargetList",
        data: {},
        async: false,
        type: 'GET',
        success: function (r) {
            var code = "";
            $.each(r.data, function (k, v) {
                if(v.endDt == null) {
                    code += "<option value='"+v.id+"' data-code='"+v.startDt+"'>"+v.name+"</option>";
                }else {
                    code += "<option value='"+v.id+"' data-code='"+v.startDt+"~"+v.endDt+"'>"+v.name+"</option>";
                }
            });
            $("#tgtList").html("").html(code);
            $("#tgtList").selectpicker('refresh');
            $("#tgtDate").html("").html($("#tgtList").find("option:eq(0)").attr("data-code"));
        }
    });
}

var tgtId = $("#tgtList").find("option:selected").val();
getMonitorVal();
function getMonitorVal() {
    if(tgtId != "") {
        $.get("/tgtKpiMonitor/getMonitorVal", {id: tgtId}, function (r) {
            var unit = r.data["KPI_UNIT"] == undefined ? "" : r.data["KPI_UNIT"];
            var targetVal = r.data["TARGET_VAL"] == undefined ? 0.00 + unit:r.data["TARGET_VAL"] + unit;
            var actualVal = r.data["ACTUAL_VAL"] == undefined ? 0.00 + unit:r.data["ACTUAL_VAL"] + unit;
            var actualValRate = r.data["ACTUAL_VAL_RATE"] == undefined ? 0.00:r.data["ACTUAL_VAL_RATE"];
            var actualValLast = r.data["ACTUAL_VAL_LAST"] == undefined ? 0.00 + unit:r.data["ACTUAL_VAL_LAST"] + unit;
            var finishRate = r.data["FINISH_RATE"] == undefined ? 0.00 + "%":r.data["TARGET_VAL"] + "%";
            var finishRateDiffer = r.data["FINISH_RATE_DIFFER"] == undefined ? 0.00 + "%":r.data["TARGET_VAL"] + "%";
            var finishRateLast = r.data["FINISH_RATE_LAST"] == undefined ? 0.00 + "%":r.data["FINISH_RATE_LAST"] + "%";

            $("#targetVal").html("").html(targetVal);
            $("#actualVal").html("").html(actualVal);
            $("#actualValRate").html("").html(actualValRate > 0 ? "同比<span style=\"color:green;\"><i class=\"mdi mdi-menu-up mdi-18px\"></i>"+actualValRate+"%</span>":"同比<span style=\"color:red;\"><i class=\"mdi mdi-menu-down mdi-18px\"></i>"+actualValRate+"%</span>");
            $("#actualValLast").html("").html(actualValLast);
            $("#finishRate").html("").html(finishRate);
            $("#finishRateDiffer").html("").html(finishRateDiffer);
            $("#finishRateLast").html("").html(finishRateLast);
            console.log(r);
        });
    }
}
getCharts();
function getCharts() {
    $.get("/tgtKpiMonitor/getCharts", {id:tgtId}, function (r) {
        console.log(r)
    });
}


$("#tgtList").change(function () {
    $("#tgtDate").html("").html($(this).find("option:selected").attr("data-code"));
});

var option1 = {
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
        "data": ['实际值', '目标值']
    },
    "calculable": false,
    "xAxis": [{
        name: "日期",
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
        name: "指标值",
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
        "name": "目标值",
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
            "name": "实际值",
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

var option2 = {
    legend: {
        right: 20,
        orient: 'vertical',
        data: ['本年指标值','去年指标值','本年平均指标值','去年平均指标值'],
        selected: {'本年指标值': true, '去年指标值': true,'本年平均指标值': false,'去年平均指标值': false}
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            lineStyle: {
                color: '#ddd'
            }
        },
        padding: [15, 20],
        extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'
    },
    xAxis: {
        name:"月份",
        data: ['1月份', '2月份', '3月份', '4月份', '5月份'],
        splitLine: {
            show: false
        },
        boundaryGap: false
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
        name: '本年指标值',
        type: 'line',
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: getPeriodRandom(800, 2000,5)
    }, {
        name: '去年指标值',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: getPeriodRandom(800, 2000, 5)
    },{
        name: '本年平均指标值',
        type: 'line',
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: getPeriodRandom(1200, 1200,5)
    }, {
        name: '去年平均指标值',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbol: 'circle',
        symbolSize: 6,
        data: getPeriodRandom(1400, 1400,5)
    }]
};

var option3 = {
    tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    legend: {
        data: ['指标值','环比增长率'],
        align: 'right',
        right: 10,
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
        splitLine:{show: false},
        data: ['201901','201902','201903','201904','201905']
    }],
    yAxis: [{
        type: 'value',
        name: '指标值',
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
        name: '指标值',
        type: 'bar',
        yAxisIndex: 0,
        data: getPeriodRandom(0, 1000, 5)
    }]
};


function data_init() {
    // 实际值和目标值
    var chart1 = echarts.init(document.getElementById('chart1'), 'macarons');
    chart1.setOption(option1);

    var chart2 = echarts.init(document.getElementById('chart2'), 'macarons');
    chart2.setOption(option2);

    var chart3 = echarts.init(document.getElementById('chart3'), 'macarons');
    chart3.setOption(option3);
}
data_init();

function getPeriodRandom(m,n,k) {
    var data = new Array();
    for(var i=0; i<k; i++) {
        data.push(getRandom(m,n));
    }
    return data;
}

function getRandom (m,n){
    var num = Math.floor(Math.random()*(m - n) + n);
    return num;
}

// 获取目标的维度和值
$("#dimensionVal").on('shown.bs.modal', function () {
    var id = $("#tgtList").find("option:selected").val();
    if(id != "") {
        $.get("/target/getDimensionsById", {id: id}, function (r) {
            var code = "";
            $.each(r.data, function (k, v) {
                code += "<li>" + v["DIMENSION_NAME"] + ":" + v["DIMENSION_VAL_NAME"] +  "</li>";
            });
            $("#dimensionList").html("").html(code);
        });
    }else {
        toastr.warning("请选择目标！");
    }
});