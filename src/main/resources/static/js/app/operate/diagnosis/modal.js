function nodeClick() {
    var kpiLevelId = jm.get_selected_node().data.KPI_LEVEL_ID;
    $.get("/progress/generateDiagData", {diagId: diagId, kpiLevelId: kpiLevelId}, function (r) {
        viewChart(r.data);
    });
}
function viewChart(obj) {
    $("#modal").modal('show');
    var operateType = obj.periodType;
    if (operateType == "M") {
        operateType = "月";
    } else {
        operateType = "天";
    }
    $("#handleDesc").html("").html("<p class='h4'>" + obj.handleDesc + "</p>");
    $("#operateType").html("").html("<p class='h5'>周期: " + operateType + "</p>");
    $("#timePeriod").html("").html("<p class='h5'>时间: " + obj.beginDt + "&nbsp;到&nbsp;" + obj.endDt + "</p>");

    var isRoot = jm.get_selected_node().isroot;
    if (isRoot) { // 根节点
        $("#opdesc").html("").html("<p class='h5'>该周期内GMV为：" + obj.kpiValue + "元</p>");
        $("#template1").attr("style", "display:block;");
        $("#template2").attr("style", "display:none;");
        $("#template3").attr("style", "display:none;");

        var whereinfo = "<div class=\"col-md-12\">\n" +
            "<table class=\"table table-sm\">\n" +
            "<n></n><tr><td><i class=\"mdi mdi-alert-circle-outline\"></i>无过滤条件！</td></tr>\n" +
            "</table>\n" +
            "</div>";
        $("#whereinfo").html("").html(whereinfo);
    } else {
        // 条件
        var whereinfo = "<div class=\"col-md-12\"><table class='table table-sm'>";
        $.each(obj.whereinfo, function (k, v) {
            if(v.inheritFlag == "Y") {
                whereinfo += "<tr><td class='text-left'>" + v.dimName + ":" + v.dimValueDisplay + "（继承至父节点）</td></tr>";
            }else {
                whereinfo += "<tr><td class='text-left'>" + v.dimName + ":" + v.dimValueDisplay + "</td></tr>";
            }
        });
        whereinfo += "</table></div>";
        if (obj.whereinfo != null && obj.whereinfo.length == 0) {
            whereinfo = "<div class=\"col-md-12\">\n" +
                "<table class=\"table table-sm\">\n" +
                "<n></n><tr><td><i class=\"mdi mdi-alert-circle-outline\"></i>无过滤条件！</td></tr>\n" +
                "</table>\n" +
                "</div>";
        }
        $("#whereinfo").html("").html(whereinfo);

        if (obj.handleType == "M") { // 乘法
            $("#template1").attr("style", "display:none;");
            $("#template2").attr("style", "display:block;");
            $("#template3").attr("style", "display:none;");

            // 变异系数
            var chartId = "covChart";
            covChart(chartId, obj);

            // 指标趋势图
            t2charts(obj);
            t2Cov(obj);
        } else if (obj.handleType == "A") { // 加法
            $("#template1").attr("style", "display:none;");
            $("#template2").attr("style", "display:none;");
            $("#template3").attr("style", "display:block;");
            t3chart1(obj, 't3chart1');
            if(obj.lineData.length != 0 && obj.lineAvgData.length != 0) {
                $("#t2chart").attr("style","display:block;");
                t3chart2(obj, 't3chart2');
            }else {
                $("#t2chart").attr("style","display:none;");
            }
            t3Cov(obj);
        } else if (obj.handleType == "F") { // 条件过滤
            $("#opdesc").html("").html("<p class='h5'>该周期内"+obj.kpiName+"为：" + obj.kpiValue + "</p>");
            $("#template1").attr("style", "display:block;");
            $("#template2").attr("style", "display:none;");
            $("#template3").attr("style", "display:none;");
        }
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
    chart.setOption(option);
    setTimeout(function () {
        chart.resize();
    }, 200);
}
function t3Cov(obj) {
    var code1 = "<tr class='active'><td></td>";
    var code2 = "<tr><td>变异系数</td>";
    var code3 = "<tr><td>相关系数</td>";
    $.each(obj.covData, function (k, v) {
        code1 += "<td> " + v.name + " </td>";
        code2 += "<td> "+ v.data +" </td>";
        code3 += "<td>" + obj.relateData[k].data + "</td>";
    });
    code1 += "</tr>";
    code2 += "</tr>";
    code3 += "</tr>";
    $("#covTable").html("").html(code1 + code2 + code3);
}

function t2Cov(obj) {
    var code1 = "<tr class='active'>";
    var code2 = "<tr>";
    $.each(obj.relate.data, function (k, v) {
        code1 += "<td> " + v.name + " </td>";
        code2 += "<td>" + v.data + "</td>";
    });
    code1 += "</tr>";
    code2 += "</tr>";
    $("#cov2Table").html("").html(code1 + code2);
}

function t2charts(obj) {
    $("#rate1").html("").html(obj.firYName + "末期比基期的变化率:" + obj.firChangeRate);
    $("#rate2").html("").html(obj.secYName + "末期比基期的变化率:" + obj.secChangeRate);
    $("#rate3").html("").html(obj.thirdYName + "末期比基期的变化率:" + obj.thirdChangeRate);
    makeT2Chart(obj,"t2chart1", obj.firYName,obj.firData, obj.firAvg, obj.firUp, obj.firDown);
    makeT2Chart(obj,"t2chart2", obj.secYName,obj.secData, obj.secAvg, obj.secUp, obj.secDown);
    makeT2Chart(obj,"t2chart3", obj.thirdYName,obj.thirdData, obj.thirdAvg, obj.thirdUp, obj.thirdDown);
}

function t3chart1(obj, chartId){

    var legendData = obj.legendData;
    var xAxisData = obj.xdata;
    var xAxisName = obj.xname;
    var yAxisName = "GMV值（元）";
    var seriesData = new Array();
    $.each(obj.areaData, function (k, v) {
        var obj = new Object();
        obj.name = v.name;
        obj.data = v.data;
        obj.type = 'line';
        obj.stack = '总量';
        obj.areaStyle = {normal: {}};
        seriesData.push(obj);
    });
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
    setTimeout(function () {
        chart.resize();
    }, 200);
}
function t3chart2(obj, chartId){
    var legendData = obj.legendData;
    var xAxisData = obj.xdata;
    var xAxisName = obj.xname;
    var yAxisName = "GMV值（元）";
    var seriesData = new Array();
    $.each(obj.lineData, function (k, v) {
        var obj = new Object();
        obj.name = v.name;
        obj.data = v.data;
        obj.type = 'line';
        seriesData.push(obj);
    });
    var len = xAxisData.length;
    $.each(obj.lineAvgData, function (k, v) {
        var obj = new Object();
        var name = v.name + "均线";
        obj.name = name;
        legendData.push(name);
        var data = new Array();
        for(var i=0; i<len;i++) {
            data.push(v.data);
        }
        obj.data = data;
        obj.type = 'line';
        seriesData.push(obj);
    });

    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
    setTimeout(function () {
        chart.resize();
    }, 200);
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
    chart.setOption(option);
    setTimeout(function () {
        chart.resize();
    }, 200);
}
