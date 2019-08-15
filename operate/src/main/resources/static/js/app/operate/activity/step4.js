var headerId = "110";
function step4Inint() {
    getChartData("unit_price", "piece_price_chart", "目标件单价", "推荐次数");
    getChartData("discount_rate", "discount_chart", "折扣率", "推荐次数");
    getChartData("buying_time", "time_chart", "购买时间（小时）", "订单量");
    getChartData("refer_deno", "preferential_chart", "优惠面额", "推荐次数");
    getStatisSpu();
}

// 购买时段分布图
function getChartData(type, id, toolXname, toolYname) {
    $.get("/op/getPeriodChartData", {headerId: headerId, type: type}, function (r) {
        makeChart(r.data, id, toolXname, toolYname);
    });
}

function makeChart(data, id, toolXname, toolYname) {
    var chart = echarts.init(document.getElementById(id), 'macarons');
    chart.showLoading();
    var seriesData = data.yAxisData;
    var xAxisName = data.xAxisName;
    var yAxisName = data.yAxisName;
    var xAxisData = data.xAxisData;
    var option = getBarOption(xAxisData, xAxisName, yAxisName, seriesData, toolXname, toolYname);
    option.grid = {
        right: '20%'
    };
    if(id == 'piece_price_chart') {
        option.xAxis.axisLabel = {
            interval:0,
            rotate:40
        };
    }

    chart.setOption(option, true);
    chart.hideLoading();
}

// function getStatisSpu() {
//     $.get("/op/getPeriodSpuStatis", {headerId: headerId}, function (r) {
//         var code = "";
//         $.each(r.data, function (k, v) {
//             code += "<tr><td>" + v.SPU_NAME + "</td><td>" + v.RECOMEND_TIMES + "</td></tr>";
//         });
//         if(code != "") {
//             $("#recomDataTableBody").html("").html(code);
//         }
//     });
// }

function getStatisSpu() {
    let settings = {
        url: "/op/getPeriodSpuStatis",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "client",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        columns: [{
            field: 'SPU_NAME',
            title: '推荐品类',
        }, {
            field: 'RECOMEND_TIMES',
            title: '购买次数（次）'
        }]
    };
    $MB.initTable('recomDataTable', settings);
}