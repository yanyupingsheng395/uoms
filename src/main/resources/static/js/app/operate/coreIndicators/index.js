// 获取监控目标
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
                    code += "<option value='"+v.id+"' data-code='"+v.startDt+"' data-period='"+v.periodType+"'>"+v.name+"</option>";
                }else {
                    code += "<option value='"+v.id+"' data-code='"+v.startDt+"~"+v.endDt+"' data-period='"+v.periodType+"'>"+v.name+"</option>";
                }
            });
            $("#tgtList").html("").html(code);
            $("#tgtList").selectpicker('refresh');
            if(r.data.length != 0) {
                $("#tgtDate").html("").html($("#tgtList").find("option:eq(0)").attr("data-code").replace("~", "至"));
            }
        }
    });
}

init();
function init() {
    getTargetList();
}

var tgtId = $("#tgtList").find("option:selected").val();
getMonitorVal();
function getMonitorVal() {
    var tgtId = $("#tgtList").find("option:selected").val();
    if(tgtId != "" && tgtId != null) {
        $.get("/tgtKpiMonitor/getMonitorVal", {id: tgtId}, function (r) {
            var unit = r.data["KPI_UNIT"] == undefined ? "" : r.data["KPI_UNIT"];
            var targetVal = r.data["TARGET_VAL"] == undefined ? 0.00 + unit:accounting.formatNumber(r.data["TARGET_VAL"]) + unit;
            var actualVal = r.data["ACTUAL_VAL"] == undefined ? 0.00 + unit:accounting.formatNumber(r.data["ACTUAL_VAL"]) + unit;
            var actualValRate = r.data["ACTUAL_VAL_RATE"] == undefined ? '--':r.data["ACTUAL_VAL_RATE"] + '%';
            var actualValLast = r.data["ACTUAL_VAL_LAST"] == undefined ? 0.00 + unit:accounting.formatNumber(r.data["ACTUAL_VAL_LAST"] )+ unit;
            var finishRate = r.data["FINISH_RATE"] == undefined ? 0.00 + "%":r.data["FINISH_RATE"] + "%";

            var remainTgt = r.data["REMAIN_TGT"] == undefined ? 0.00 + unit:r.data["REMAIN_TGT"] + unit;
            var finishDiffer = r.data["FINISH_DIFFER"] == undefined ? 0.00 + unit:r.data["FINISH_DIFFER"] + unit;
            var remainCount = r.data["REMAIN_COUNT"] == undefined ? 0:r.data["REMAIN_COUNT"];
            var varyIdx = r.data["VARY_IDX"] == undefined ? 0.00:r.data["VARY_IDX"];
            var varyIdxLast = r.data["VARY_IDX_LAST"] == undefined ? '--':r.data["VARY_IDX_LAST"];


            $("#targetVal").html("").html(targetVal);
            $("#actualVal").html("").html(actualVal);
            if(actualValRate == '--') {
                $("#actualValRate").html("").html("<span style='line-height: 30px;'>同比:"+actualValRate+"</span>");
            }else {
                $("#actualValRate").html("").html(actualValRate > 0 ? "同比<span style=\"color:green;\"><i class=\"mdi mdi-menu-up mdi-18px\"></i>"+actualValRate+"</span>":"同比<span style=\"color:red;\"><i class=\"mdi mdi-menu-down mdi-18px\"></i>"+actualValRate+"</span>");
            }
            $("#actualValLast").html("").html(actualValLast);
            $("#finishRate").html("").html(finishRate);
            // 完成度
            $("#finishRate2").html("").html(finishRate);
            $("#remainTgt").html("").html(remainTgt);
            $("#finishDiffer").html("").html(finishDiffer);
            $("#remainCount").html("").html(remainCount);
            $("#varyIdx").html("").html(varyIdx);
            $("#varIdxLast").html("").html(varyIdxLast);
        });
    }
}

/**
 * 获取三个echart图
 * periodType：时间周期
 * dt：时间
 */
var dt = $("#tgtList").find("option:selected").attr("data-code");
var periodType = $("#tgtList").find("option:selected").attr("data-period");
getCharts(periodType, dt);
function getCharts(periodType, dt) {
    if(tgtId != "" && tgtId != null) {
        $.get("/tgtKpiMonitor/getCharts", {id:tgtId, periodType:periodType, dt: dt}, function (r) {
            chartInit(r.data[0], "chart1");
            chartInit(r.data[1], "chart2");
            chartInit(r.data[2], "chart3");
        });
    }
}

// 各月目标和实际值对比图
function chartInit(chartData, chartId) {
    var legend = chartData['legendData'];
    var xname = chartData['xAxisName'];
    var yname = chartData['yAxisName'];
    var xdata = chartData['xAxisData'];
    var series = chartData['seriesData'];
    var option;
    if(chartId == "chart1") {
        option = getOptionAcc(legend, xdata, xname, yname, series);
    }else if(chartId == "chart2") {
        option = currLastValOption(legend, xdata, xname, yname, series);
        option.series[1].smooth = false;
        option.series[1].itemStyle = {
            normal:{
                lineStyle:{
                    width:2,
                    type:'dotted'
                }
            }
        };
        option.series[3].smooth = false;
        option.series[3].itemStyle = {
            normal:{
                lineStyle:{
                    width:2,
                    type:'dotted'
                }
            }
        };
    }else if(chartId == "chart3") {
        option = getValRateOption(legend, xdata, xname, yname, series);
        option.series[0].yAxisIndex = 0;
        option.series[1].yAxisIndex = 1;
    }
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
}

$("#tgtList").change(function () {
    $("#tgtDate").html("").html($(this).find("option:selected").attr("data-code").replace("~", "至"));
    // 请求面板数据
    var periodType = $(this).find("option:selected").attr("data-period");
    var dt = $(this).find("option:selected").attr("data-code");
    getCharts(periodType, dt);
    getMonitorVal();
});


function getOptionAcc(legend, xdata, xAxis, yAxis, series) {
    return {
        tooltip: {
            trigger: 'axis',
            padding: [15, 20],
            extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)',
            formatter: function (params) {
                return "日期：" + params[0].name + "<br/>" + params[0].seriesName + ":" + params[0].value + "<br/>" + params[1].seriesName + ":" + params[1].value;
            }
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
            data: legend
        },
        calculable: false,
        xAxis: [{
            name: xAxis,
            type: 'category',
            axisTick: {
                show: false
            },
            axisLine: {
                show: true,
            },
            data: xdata,
            boundaryGap: true
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
            data: xdata
        }],
        yAxis: [{
            type: "value",
            name: yAxis,
            splitLine: {
                show: false
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
        series:series
    };
}
function currLastValOption(legend, xdata, xAxis, yAxis, series) {
    return {
        legend: {
            right: 20,
            orient: 'vertical',
            data: legend,
            selected: {'本周期指标值': true, '去年同期指标值': true,'本周期平均指标值': false,'去年同期平均指标值': false}
        },
        tooltip: {
            trigger: 'axis',
            padding: [15, 20],
            extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'
        },
        xAxis: {
            name: xAxis,
            data: xdata,
            splitLine: {
                show: false
            },
            boundaryGap: false
        },
        yAxis: {
            type: 'value',
            name: yAxis,
            splitLine: {
                show: false
            },
            splitArea : {show : false}
        },
        series: series
    };
}

function getValRateOption(legend, xdata, xAxis, yAxis, series) {
    return {tooltip: {
            trigger: 'axis',
            axisPointer: { // 坐标轴指示器，坐标轴触发有效
                type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        legend: {
            data: legend,
            align: 'right',
            right: 10,
        },
        xAxis: [{
            type: 'category',
            // name: xAxis,
            splitLine:{show: false},
            data: xdata,
            boundaryGap: true
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
        series: series};
}

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

function getDimensionList() {
    var id = $("#tgtList").find("option:selected").val();
    if(id != "") {
        $.get("/target/getDimensionsById", {id: id}, function (r) {
            var code = "";
            $.each(r.data, function (k, v) {
                code += "<li>" + v["DIMENSION_NAME"] + ":" + v["DIMENSION_VAL_NAME"] +  "</li>";
            });

            if(code == "") {
                toastr.warning("该目标没有维度和值！");
            }else {
                $("#dimensionVal").modal('show');
                $("#dimensionList").html("").html(code);
            }
        });
    }else {
        toastr.warning("请选择目标！");
    }
}