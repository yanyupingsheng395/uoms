function nodeClick() {
    var opDataType = $("#opDataType").val();
    var url = "";
    if(opDataType == "edit") {
        url = "/progress/generateDiagDataOfEdit";
    }
    if(opDataType == "add" || opDataType == "view") {
        url = "/progress/generateDiagData";
    }
    var kpiLevelId = jm.get_selected_node().data.kpiLevelId;
    $.get(url, {diagId: diagId, kpiLevelId: kpiLevelId}, function (r) {
        viewChart(r.data);
    });
}

function getRootDiagInfo() {
    var code = "";
    $.ajax({
        url: "/diag/getDimByDiagId",
        data: {diagId: diagId},
        async: false,
        success: function (r) {
            $("#_diagName").html("").html("<p>名称: " + r.data['DIAG_NAME'] + "</p>");
            $("#diagKpiCode").html("").html("<p>指标: " + r.data['KPI_NAME'] + "</p>");
            var dimInfos = r.data['DIM_DISPLAY_NAME'] == null ? null:r.data['DIM_DISPLAY_NAME'].split(";");
            if(dimInfos != null) {
                $.each(dimInfos, function (k, v) {
                    code += "<li>"+v+"</li>";
                });
            }
        }
    });
    return code;
}
var o = new Object();
function viewChart(obj) {
    o = obj;
    $("#modal").modal('show');
}

$('#modal').on('hidden.bs.modal', function () {
    $.fn.zTree.destroy('tree');
});

$('#modal').on('shown.bs.modal', function () {
    lightyear.loading('show');
    var obj = o;
    var operateType = obj.periodType;
    if (operateType == "M") {
        operateType = "月";
    } else {
        operateType = "天";
    }
    $("#handleDesc").html("").html("<p class='h4'>" + obj.handleDesc + "</p>");
    $("#operateType").html("").html("<p>周期: " + operateType + "</p>");
    $("#timePeriod").html("").html("<p>时间: " + obj.beginDt + "&nbsp;到&nbsp;" + obj.endDt + "</p>");
    var isRoot = jm.get_selected_node().isroot;

    var treeArr = [];
    // 条件
    $.each(obj.whereinfo, function (k, v) {
        var o = new Object();
        if(v["inheritFlag"] == "Y") {
            o.id = k + 1;
            o.name = v.dimName + ":" + v.dimValueDisplay + "（继承至父节点）";
        }else if(v["inheritFlag"] == "A"){
            o.id = k + 1;
            o.name = v.dimName + ":" + v.dimValueDisplay + "（加法拆分的维度）";
        }else {
            o.id = k + 1;
            o.name = v.dimName + ":" + v.dimValueDisplay;
        }
        o.pId = 0;
        treeArr.push(o);
    });
    treeArr.push({id:0, pId:-1, name:'过滤条件'});
    createWhereInfoTree("tree", treeArr);

    if (isRoot) { // 根节点
        $("#opdesc").html("").html("<p>该周期内GMV为：" + obj.kpiValue + "元</p>");
        $("#template1").attr("style", "display:block;");
        $("#template2").attr("style", "display:none;");
        $("#template3").attr("style", "display:none;");
        var code = getRootDiagInfo();
        if(code != "") {
            $("#_conditions").html("").html("<ol>"+code+"</ol>");
        }
    } else {
        if (obj.handleType == "M") { // 乘法
            $("#template1").attr("style", "display:none;");
            $("#template2").attr("style", "display:block;");
            $("#template3").attr("style", "display:none;");

            // 指标趋势图
            t2charts(obj);
            t2Cov1(obj);
            t2Relate(obj);
        } else if (obj.handleType == "A") { // 加法
            $("#template1").attr("style", "display:none;");
            $("#template2").attr("style", "display:none;");
            $("#template3").attr("style", "display:block;");
            t3chart1(obj, 't3chart1');

            $("#t3chart").attr("style","display:block;");
            t3chart4(obj, "t3chart4");

            //  判断是否为目标指标
            if(obj.lineData != null && obj.lineData.length != 0) {
                $("#t2chart").attr("style","display:block;");
                $("#t2chart_target").attr("style","display:none;");
                t3chart2(obj, 't3chart2');
                t3chart4(obj, 't3chart42');
                t3chart6(obj, 't3chart6');
            }else {

                $("#t2chart").attr("style","display:none;");
                $("#t2chart_target").attr("style","display:block;");
            }
            t3Cov(obj);
            t3Relate(obj);
        } else if (obj.handleType == "F") { // 条件过滤
            $("#opdesc").html("").html("<p class='h5'>该周期内"+obj.kpiName+"为：" + obj.kpiValue + "</p>");
            $("#template1").attr("style", "display:block;");
            $("#template2").attr("style", "display:none;");
            $("#template3").attr("style", "display:none;");
        }
    }

    lightyear.loading('hide');
});

// 创建过滤条件树
function createWhereInfoTree(treeId, nodes) {
    if(nodes.length > 1) {
        var setting = {
            view: {
                showIcon: false
            },
            data: {
                simpleData: {
                    enable: true
                }
            }
        };
        $.fn.zTree.init($("#" + treeId), setting, nodes);
    }
}

// 乘法变异系数图
function covChart(chartId, obj) {
    var legendData = new Array();
    var xAxisData = obj.xdata;
    var seriesData = new Array();
    $.each(obj.covNames, function (k, v) {
        legendData.push(v);
        var t = new Object();
        t.name = v;
        t.type = 'line';
        t.data = obj.covValues[k];
        seriesData.push(t);
    });
    var xAxisName = obj.xName;
    var yAxisName = "变异系数";
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option, true);
}
function t3Cov(obj) {
    var code1 = "";
    var code2 = "";
    var tmp = null;
    $.each(obj.covData, function (k, v) {
        if(v.name == "总体") {
            if(v.data != null && v.data != "null") {
                tmp = v.data + "%";
            }else {
                tmp = "--";
            }
        }else {
            code1 += "<td>"+v.name+"</td>";
            if(v.data == "") {
                code2 += "<td>--</td>";
            }else {
                code2 += "<td>"+v.data+"%</td>";
            }
        }
    });
    code1 = "<tr class='active'><td>总体</td>" + code1 + "</tr>";
    code2 = "<tr><td>"+tmp+"</td>" + code2 + "</tr>";
    $("#covTable").html("").html(code1 + code2);
}

function t3Relate(obj) {
    var code1 = "";
    var code2 = "";
    $.each(obj.relateData, function (k, v) {
        code1 += "<td>"+v.name+"</td>";
        if(v.data == "") {
            code2 += "<td>--</td>";
        }else {
            code2 += "<td>"+v.data+"</td>";
        }
    });
    code1 = "<tr class='active'><td></td>" + code1 + "</tr>";
    code2 = "<tr><td>总体</td>" + code2 + "</tr>";
    $("#relateTable").html("").html(code1 + code2);
}

function t2Cov1(obj) {
    var code1 = "<tr class='active'>";
    var code2 = "<tr>";
    $.each(obj.covValues, function (k, v) {
        if(v != "") {
            code2 += "<td>"+v+"%</td>";
        }else {
            code2 += "<td>--</td>";
        }
        code1 += "<td>"+k+"</td>";
    });
    code1 += "</tr>";
    code2 += "</tr>";
    $("#cov1Table").html("").html(code1 + code2);
}

function t2Relate(obj) {
    var code1 = "<tr class='active'><td style='width: 100px;'></td>";
    var code2 = "<tr><td style='width: 100px;'>"+obj.relate.name+"</td>";
    $.each(obj.relate.data, function (k, v) {
        code1 += "<td> " + v.name + " </td>";
        code2 += "<td>" + v.data + "</td>";
    });
    code1 += "</tr>";
    code2 += "</tr>";
    $("#relate1Table").html("").html(code1 + code2);
}

function t2charts(obj) {
    var frate = obj.firChangeRate == ""?"--":obj.firChangeRate;
    var srate = obj.secChangeRate == ""?"--":obj.secChangeRate;
    var trate = obj.thirdChangeRate == ""?"--":obj.thirdChangeRate;
    $("#rate1").html("").html(obj.firYName + "末期比基期的变化率:" + frate);
    $("#rate2").html("").html(obj.secYName + "末期比基期的变化率:" + srate);
    $("#rate3").html("").html(obj.thirdYName + "末期比基期的变化率:" + trate);
    makeT2Chart(obj,"t2chart1", obj.firYName,obj.firData, obj.firAvg, obj.firUp, obj.firDown);
    makeT2Chart(obj,"t2chart2", obj.secYName,obj.secData, obj.secAvg, obj.secUp, obj.secDown);
    makeT2Chart(obj,"t2chart3", obj.thirdYName,obj.thirdData, obj.thirdAvg, obj.thirdUp, obj.thirdDown);
}

/**
 * 不同维度对目标指标的影响效果
 * 首购/非首购：面积图
 * 其他维度：条形图
 * @param obj
 * @param chartId
 */
function t3chart1(obj, chartId){
    var legendData = obj.legendData;
    var xAxisData = obj.xdata;
    var xAxisName = obj.xname;
    var yAxisName = "GMV";
    var seriesData = new Array();
    var option = null;
    $.each(obj.areaData, function (k, v) {
        var obj = new Object();
        obj.name = v.name;
        obj.data = v.data;
        obj.type = 'line';
        seriesData.push(obj);
    });
    option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option, true);
}
function t3chart2(obj, chartId){
    var legendData = obj.legendData;
    var xAxisData = obj.xdata;
    var xAxisName = obj.xname;
    var yAxisName = obj.kpiName;
    var seriesData = new Array();
    $.each(obj.lineData, function (k, v) {
        var obj = new Object();
        obj.name = v.name;
        obj.data = v.data;
        obj.type = 'line';
        seriesData.push(obj);
    });
    var len = xAxisData.length;
    var tmp = {};
    $.each(obj.lineAvgData, function (k, v) {
        var obj = new Object();
        var name = v.name + "均线";
        obj.name = name;
        legendData.push(name);
        tmp[""+name+""] = false;
        tmp[""+v.name+""] = true;
        var data = new Array();
        for(var i=0; i<len;i++) {
            data.push(v.data);
        }
        obj.data = data;
        obj.type = 'line';
        seriesData.push(obj);
    });

    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    option.legend.selected = tmp;
    option.grid = {top:'28%'};
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option, true);
}


// 条形图
function t3chart4(obj, chartId) {
    var mainKpi = obj["mainKpiBarData"];
    var xdata = [];
    var ydata = [];
    $.each(mainKpi, function (k, v) {
        xdata.push(v["name"]);
        ydata.push(v["value"]);
    });
    var series = [{type:'bar', data:ydata}];
    var yname = "GMV";

    var option = {
        grid: {
            left: '3%',
            right: '7%',
            bottom: '10%',
            containLabel: true
        },

        tooltip: {
            show:"true",
            trigger: 'axis',
            axisPointer: { // 坐标轴指示器，坐标轴触发有效
                type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        xAxis:  {
            type: 'category',
            axisTick : {show: false},
            splitLine: {show: false},
            splitArea: {show: false},
            data: xdata
        },
        yAxis: [
            {
                name: yname,
                type: 'value',
                axisLine: {show:true},
                axisTick: {show:false},
                axisLabel: {show:true},
                splitArea: {show:false},
                splitLine: {show:false}
            }
        ],
        series: series
    };
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option, true);
}

/**
 *
 * @param obj
 * @param flag true:目标指标，false：上层指标
 * @returns {Array}
 */
function getScatterSeries(obj, flag, max, min) {
    var series =  [];
    var data = flag ? obj["areaData"] : obj["lineData"];
    var xdata = obj["xdata"];
    for(var i=0; i<data.length; i++) {
        var o = new Object();
        o.name = data[i].name;
        o.type = 'scatter';
        o.symbol = 'circle';
        var odata = [];
        for(var j=0; j<xdata.length; j++) {
            odata.push([xdata[j], data[i]["data"][j], data[i]["data"][j]]);
        }
        o.data = odata;
        o.symbolSize = function (data) {
            return ((data[2]-min)/(max-min)) * 80;
        };
        series.push(o);
    }
    return series;
}
function t3chart5(obj, chartId){
    var xdata = obj["xdata"];
    var xname = obj["xname"];
    var yname = obj["kpiName"];
    var legend = obj["legendData"];
    var allData = [];
    $.each(obj["lineData"], function (k, v) {
        allData = allData.concat(v["data"]);
    });
    var max = Math.max.apply(null, allData);
    var min = Math.min.apply(null, allData);
    var series = getScatterSeries(obj, false, max, min);
    var option = {
        tooltip: {},
        grid: {
            right: '12%'
        },
        legend: {data:legend},
        xAxis: {
            name: xname,
            type: "category",
            boundaryGap: false,
            splitArea: {show:false},
            splitLine: {show:false},
            data: xdata
        },
        yAxis: {
            name: yname,
            splitArea: {show:false},
            splitLine: {show:false},
        },
        series: series
    };

    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option, true);
}

function t3chart6(obj, chartId){
    var mainKpi = obj["currKpiBarData"];
    var xdata = [];
    var ydata = [];
    $.each(mainKpi, function (k, v) {
        xdata.push(v["name"]);
        ydata.push(v["value"]);
    });
    var series = [{type:'bar', data:ydata}];
    var yname = obj["kpiName"];

    var option = {
        grid: {
            left: '3%',
            right: '7%',
            bottom: '10%',
            containLabel: true
        },

        tooltip: {
            show:"true",
            trigger: 'axis',
            axisPointer: { // 坐标轴指示器，坐标轴触发有效
                type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        xAxis:  {
            type: 'category',
            axisTick : {show: false},
            splitLine: {show: false},
            splitArea: {show: false},
            data: xdata
        },
        yAxis: [
            {
                name: yname,
                type: 'value',
                axisLine: {show:true},
                axisTick: {show:false},
                axisLabel: {show:true},
                splitArea: {show:false},
                splitLine: {show:false}
            }
        ],
        series: series
    };
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option, true);
}
// 目标指标的散点图
function t3chart3(obj) {
    var xdata = obj["xdata"];
    var xname = obj["xname"];
    var yname = $("#targetKpi").find("option:selected").text();
    var legend = obj["legendData"];
    var allData = [];
    $.each(obj["areaData"], function (k, v) {
        allData = allData.concat(v["data"]);
    });

    var max = Math.max.apply(null, allData);
    var min = Math.min.apply(null, allData);
    var series = getScatterSeries(obj, true, max, min);
    var option = {
        tooltip: {},
        grid: {
            right: '12%'
        },
        legend: {data:legend},
        xAxis: {
            name: xname,
            type: "category",
            boundaryGap: false,
            splitArea: {show:false},
            splitLine: {show:false},
            data: xdata
        },
        yAxis: {
            name: yname,
            splitArea: {show:false},
            splitLine: {show:false},
        },
        series: series
    };

    var chart = echarts.init(document.getElementById("t3chart3"), 'macarons');
    chart.setOption(option, true);
}

function makeT2Chart(obj,chartId,yName, data, avg, up, down) {
    var legendData = [yName, "均线"];
    var xAxisData = obj.xdata;
    var xAxisName = obj.xName;
    var yAxisName = yName;
    var seriesData = new Array();
    var avgData = new Array();
    $.each(data, function () {
        avgData.push(avg);
    });
    var t1 = new Object();
    t1.name = yName;
    t1.type = 'line';
    t1.data = data;
    seriesData.push(t1);

    var t2 = new Object();
    t2.name = legendData[1];
    t2.type = 'line';
    t2.data = avgData;
    t2.markArea = {itemStyle:{
            color: '#48b0f7',
            opacity: 0.2
        },
        silent: true,
        label: {
            normal: {
                position: ['10%', '50%']
            }
        },
        data: [
            [{
                name: '',
                yAxis: up,
                x: '10%',
                itemStyle: {
                    normal: {
                        color: '#ff0000'
                    }
                },
            }, {
                yAxis: down,
                x: '90%'
            }]
        ]};
    seriesData.push(t2);
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option, true);
}
