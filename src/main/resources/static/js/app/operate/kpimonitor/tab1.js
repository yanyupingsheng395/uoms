initData();
function initData() {
    getTotalGmv();
    getTotalTradeUser();
    getTotalAvgPrice();

    gmvChart();
    tradeUserChart();
    avgCsPriceChart();

    getOrderAvgPrice();
    getAvgOrderQuantity();

}

getSource();
function getSource() {
    $.get("/kpiMonitor/getSource", {}, function (r) {
        var arr = Object.keys(r.data);
        var len = arr.length;
        var code = "<option value=''>所有</option>";
        if(len != 0) {
            $.each(r.data, function (k, v) {
                code += "<option value='"+k+"'>"+v+"</option>";
            });
        }
        $("#source").html("").html(code);
        $("#source").selectpicker('refresh');
    });
}

// 查询所有
function searchTotal() {
    getTotalGmv();
    getTotalTradeUser();
    getTotalAvgPrice();
}

function searchGmv() {
    gmvChart();
}

function searchTradeAndPrice() {
    tradeUserChart();
    avgCsPriceChart();
}

function searchOrderPrice() {
    getOrderAvgPrice();
    getAvgOrderQuantity();
}

function gmvChart() {
    var startDt = $("#startDate1").val();
    var endDt = $("#endDate1").val();
    $.get("/kpiMonitor/getGMV", {startDt: startDt, endDt: endDt}, function (r) {
        var legendData = r.data.legendData;
        var seriesData = new Array();
        $.each(r.data.seriesData, function (k, v) {
            v.stack = '总量';
            v.type = 'line';
            v.areaStyle = {normal: {}};
            seriesData.push(v);
        });
        var xAxisName = r.data.xAxisName;
        var yAxisName = r.data.yAxisName;
        var xAxisData = r.data.xAxisData;
        var option = getOption(legendData, xAxisData, xAxisName, yAxisName, seriesData);
        var chart = echarts.init(document.getElementById("chart1"), 'macarons');
        chart.setOption(option);
    });
}

function tradeUserChart() {
    var startDt = $("#startDate2").val();
    var endDt = $("#endDate2").val();
    $.get("/kpiMonitor/getTradeUser", {startDt: startDt, endDt: endDt}, function (r) {
        var legendData = r.data.legendData;
        var seriesData = new Array();
        $.each(r.data.seriesData, function (k, v) {
            v.stack = '总量';
            v.type = 'line';
            v.areaStyle = {normal: {}};
            seriesData.push(v);
        });
        var xAxisName = r.data.xAxisName;
        var yAxisName = r.data.yAxisName;
        var xAxisData = r.data.xAxisData;

        var option = getOption(legendData, xAxisData, xAxisName, yAxisName, seriesData);
        var chart = echarts.init(document.getElementById("chart2"), 'macarons');
        chart.setOption(option);
    });
}

function avgCsPriceChart() {
    var startDt = $("#startDate2").val();
    var endDt = $("#endDate2").val();
    var chart = echarts.init(document.getElementById("chart3"), 'macarons');
    chart.showLoading();
    $.get("/kpiMonitor/getAvgCsPrice", {startDt: startDt, endDt: endDt}, function (r) {
        var legendData = r.data.legendData;
        var seriesData = new Array();
        $.each(r.data.seriesData, function (k, v) {
            v.stack = '总量';
            v.type = 'line';
            seriesData.push(v);
        });
        var xAxisName = r.data.xAxisName;
        var yAxisName = r.data.yAxisName;
        var xAxisData = r.data.xAxisData;
        var option = getOption(legendData, xAxisData, xAxisName, yAxisName, seriesData);
        chart.setOption(option);
        chart.hideLoading();
    });
}


// 获取总销售额
function getTotalGmv() {
    var startDt = $("#dateItem1").val();
    var endDt = $("#dateItem2").val();
    $.get("/kpiMonitor/getTotalGmv", {startDt: startDt, endDt: endDt}, function (r) {
        var gmv = r.data.gmv;
        var yny = r.data.yny;
        var numYny = yny.replace("%", "");
        if(Number(numYny) > 0) {
            $("#totalGmvYny").html("").html("同比<span style=\"color:green;\"><i class=\"mdi mdi-menu-up mdi-18px\"></i>" + yny + "</span>");
        }else {
            $("#totalGmvYny").html("").html("同比<span style=\"color:red;\"><i class=\"mdi mdi-menu-down mdi-18px\"></i>" + yny + "</span>");
        }
        $("#totalGmv").html("").html(gmv + "元");
    });
}
function getTotalTradeUser() {
    var startDt = $("#dateItem1").val();
    var endDt = $("#dateItem2").val();
    $.get("/kpiMonitor/getTotalTradeUser", {startDt: startDt, endDt: endDt}, function (r) {
        var gmv = r.data.tradeUser;
        var yny = r.data.yny;
        var numYny = yny.replace("%", "");
        if(Number(numYny) > 0) {
            $("#tradeUserYny").html("").html("同比<span style=\"color:green;\"><i class=\"mdi mdi-menu-up mdi-18px\"></i>" + yny + "</span>");
        }else {
            $("#tradeUserYny").html("").html("同比<span style=\"color:red;\"><i class=\"mdi mdi-menu-down mdi-18px\"></i>" + yny + "</span>");
        }
        $("#tradeUser").html("").html(gmv + "人");
    });
}

function getTotalAvgPrice() {
    var startDt = $("#dateItem1").val();
    var endDt = $("#dateItem2").val();
    $.get("/kpiMonitor/getTotalAvgPrice", {startDt: startDt, endDt: endDt}, function (r) {
        var gmv = r.data.avgPrice;
        var yny = r.data.yny;
        var numYny = yny.replace("%", "");
        if(Number(numYny) > 0) {
            $("#avgPriceYny").html("").html("同比<span style=\"color:green;\"><i class=\"mdi mdi-menu-up mdi-18px\"></i>" + yny + "</span>");
        }else {
            $("#avgPriceYny").html("").html("同比<span style=\"color:red;\"><i class=\"mdi mdi-menu-down mdi-18px\"></i>" + yny + "</span>");
        }
        $("#avgPrice").html("").html(gmv + "元");
    });
}

function getOrderAvgPrice() {
    var startDt = $("#startDate3").val();
    var endDt = $("#endDate3").val();
    $.get("/kpiMonitor/getOrderAvgPrice", {startDt: startDt, endDt: endDt}, function (r) {
        var chart = echarts.init(document.getElementById("chart4"), 'macarons');
        chart.showLoading();
        $.get("/kpiMonitor/getAvgCsPrice", {startDt: startDt, endDt: endDt}, function (r) {
            var legendData = r.data.legendData;
            var seriesData = new Array();
            $.each(r.data.seriesData, function (k, v) {
                v.stack = '总量';
                v.type = 'line';
                seriesData.push(v);
            });
            var xAxisName = r.data.xAxisName;
            var yAxisName = r.data.yAxisName;
            var xAxisData = r.data.xAxisData;
            var option = getOption(legendData, xAxisData, xAxisName, yAxisName, seriesData);
            chart.setOption(option);
            chart.hideLoading();
            chart.on('click', function (params) {
                $("#chartModal").modal('show');
            });
        });
    });


}

function getAvgOrderQuantity() {
    var startDt = $("#startDate3").val();
    var endDt = $("#endDate3").val();
    $.get("/kpiMonitor/getAvgOrderQuantity", {startDt: startDt, endDt: endDt}, function (r) {
        var chart = echarts.init(document.getElementById("chart5"), 'macarons');
        chart.showLoading();
        $.get("/kpiMonitor/getAvgOrderQuantity", {startDt: startDt, endDt: endDt}, function (r) {
            var legendData = r.data.legendData;
            var seriesData = new Array();
            $.each(r.data.seriesData, function (k, v) {
                v.stack = '总量';
                v.type = 'line';
                seriesData.push(v);
            });
            var xAxisName = r.data.xAxisName;
            var yAxisName = r.data.yAxisName;
            var xAxisData = r.data.xAxisData;
            var option = getOption(legendData, xAxisData, xAxisName, yAxisName, seriesData);
            chart.setOption(option);
            chart.hideLoading();
        });
    });
}
function getAvgPiecePrice() {
    var startDt = $("#startDate8").val();
    var endDt = $("#endDate8").val();
    $.get("/kpiMonitor/getAvgPiecePrice", {startDt: startDt, endDt: endDt}, function (r) {
        var chart = echarts.init(document.getElementById("chart80"), 'macarons');
        chart.showLoading();
        var legendData = r.data.legendData;
        var seriesData = new Array();
        $.each(r.data.seriesData, function (k, v) {
            v.stack = '总量';
            v.type = 'line';
            seriesData.push(v);
        });
        var xAxisName = r.data.xAxisName;
        var yAxisName = r.data.yAxisName;
        var xAxisData = r.data.xAxisData;
        var option = getOption(legendData, xAxisData, xAxisName, yAxisName, seriesData);
        chart.setOption(option);
        chart.hideLoading();
    });
}

function getAvgJoinRate() {
    var startDt = $("#startDate8").val();
    var endDt = $("#endDate8").val();
    $.get("/kpiMonitor/getAvgJoinRate", {startDt: startDt, endDt: endDt}, function (r) {
        var chart = echarts.init(document.getElementById("chart81"), 'macarons');
        chart.showLoading();
        var legendData = r.data.legendData;
        var seriesData = new Array();
        $.each(r.data.seriesData, function (k, v) {
            v.stack = '总量';
            v.type = 'line';
            seriesData.push(v);
        });
        var xAxisName = r.data.xAxisName;
        var yAxisName = r.data.yAxisName;
        var xAxisData = r.data.xAxisData;
        var option = getOption(legendData, xAxisData, xAxisName, yAxisName, seriesData);
        chart.setOption(option);
        chart.hideLoading();
    });
}

$('#chartModal').on('shown.bs.modal', function () {
    getAvgPiecePrice();
    getAvgJoinRate();
});


function rateAndPiece() {
    getAvgPiecePrice();
    getAvgJoinRate();
}
