init_date('touchDate', 'yyyy-mm-dd', 0,2,0);
$("#touchDate").datepicker("setDate", new Date());
$("#touchDate").datepicker("setEndDate", new Date());

$(function () {
    $('#cdrq').text($('#touchDate').val());
    //获取当日需要运营的总人数
    // getTableData();

    //为刷新按钮绑定事件
    $("#btn_query").on("click",function () {
        refreshOpDataTable();
    });

    $("#btn_downLoad").on("click",function () {
        $MB.n_warning("演示环境，暂不支持数据下载！");
    });

    // 统计图表
    $("#btn_statistics").on("click",function () {
        $("#statis_modal").modal('show');
    });

    // 效果统计表
    $("#btn_effect").on("click",function () {
        // $("#effect_modal").modal('show');
        $MB.n_warning("尚未进行触达，无法查看效果统计。");
    });

    var settings = {
        url: '/op/getOpDayDetailAllList',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,  //页码
                sort: params.sort,      //排序列名
                sortOrder: params.order,
                param: {daywid: $('#touchDate').val(), userActiv: $("#pathActiv").find("option:selected").val(), userValue: $("#userValue").find("option:selected").val()}
            };
        },
        columns: [[{
            field: 'USER_ID',
            title: '用户ID',
            rowspan: 2,
            valign:"middle"
        },{
            title: '用户状态',
            colspan: 2
        },{
            title: '用户目标',
            colspan: 1
        },{
            title: '用户策略',
            colspan: 5
        }], [{
            field: 'PATH_ACTIV',
            title: '用户活跃度',
            sortable: true,
            formatter:function (value, row, index) {
                var res = "";
                switch (value) {
                    case "UAC_01":
                        res = "高度活跃";
                        break;
                    case "UAC_02":
                        res = "中度活跃";
                        break;
                    case "UAC_03":
                        res = "流失预警";
                        break;
                    case "UAC_04":
                        res = "弱流失";
                        break;
                    case "UAC_05":
                        res = "强流失";
                        break;
                    case "UAC_06":
                        res = "沉睡";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        }
            ,{
                field: 'USER_VALUE',
                title: '用户价值',
                sortable: true,
                formatter: function (value, row, index) {
                    var res = "";
                    switch (value) {
                        case "ULC_01":
                            res = "重要";
                            break;
                        case "ULC_02":
                            res = "主要";
                            break;
                        case "ULC_03":
                            res = "普通";
                            break;
                        case "ULC_04":
                            res = "长尾";
                            break;
                        default:
                            res = "-";
                    }
                    return res;
                }
            },
            {
                field: 'PIECE_PRICE',
                title: '件单价'
            },
            {
                field: 'SPU_NAME',
                title: '留存SPU'
            },{
                field: 'RECOM_SPU',
                title: '推荐SPU'
            },{
                field: 'DISCOUNT_LEVEL',
                title: '折扣率'
            },{
                field: 'ORDER_PERIOD',
                title: '平均订单时段'
            },{
                field: 'REFER_DENO',
                title: '优惠面额'
            }
        ]]
    };
    $MB.initTable('opdayTable', settings);
});

function refreshOpDataTable() {
    $MB.refreshTable('opdayTable');
}

// 获取表格数据
// function getTableData() {
//     //获取当日需要运营的总人数
//     $.getJSON("/op/getOpDayHeadInfo", {daywid: dayWid}, function (resp) {
//         if (resp.code == 200) {
//             $('#cdrq').text($('#touchDate').val());
//             $('#cdrs').text(resp.msg);
//         }
//     });
//     // //加载明细数据
//     // $.getJSON("/op/getOpDayDetailAllList", {daywid: dayWid, userActiv: userActiv, userValue: userValue}, function (resp) {
//     //     if (resp.code == 200) {
//     //         $('#opdayTable').bootstrapTable('load', resp.data);
//     //         var data = $('#opdayTable').bootstrapTable('getData', true);
//     //         //合并单元格
//     //         var merge =$(":radio[name='merge']:checked").val();
//     //         mergeCells(data, merge, 1);
//     //     }
//     // });
// }

function getStatisSpu() {
    var touchDt = $("#touchDate").val();
    $.get("/op/getSpuStatis", {touchDt: touchDt}, function (r) {
        var c1 = "<tr class='active'><td>推荐品类</td>";
        var c2 = "<tr><td>推荐次数</td>";
        $.each(r.data, function (k, v) {
            c1 += "<td>" + v.SPU_NAME + "</td>";
            c2 += "<td>" + v.RECOMEND_TIMES + "</td>";
        });
        c1 += "</tr>";
        c2 += "</tr>";
        $("#recomDataTable").html("").html(c1 + c2);
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
    chart.setOption(option, true);
    chart.hideLoading();
}

$('#statis_modal').on('shown.bs.modal', function () {
    getChartData("unit_price", "piece_price_chart", "目标件单价", "推荐次数");
    getChartData("discount_rate", "discount_chart", "折扣率", "推荐次数");
    getChartData("buying_time", "time_chart", "购买时间（小时）", "订单量");
    getChartData("refer_deno", "preferential_chart", "优惠面额", "推荐次数");
    getStatisSpu();
});



function mergeCells(data,fieldName,colspan) {
    //声明一个map计算相同属性值在data对象出现的次数和
    var sortMap = {};
    for(var i = 0 ; i < data.length ; i++){
        for(var prop in data[i]){
            if(prop == fieldName){
                var key = data[i][prop]
                if(sortMap.hasOwnProperty(key)){
                    sortMap[key] = sortMap[key] * 1 + 1;
                } else {
                    sortMap[key] = 1;
                }
                break;
            }
        }
    }
    for(var prop in sortMap){
    }
    var index = 0;
    for(var prop in sortMap){
        var count = sortMap[prop] * 1;
        $("#opdayTable").bootstrapTable('mergeCells',{index:index, field:fieldName, colspan: colspan, rowspan: count});
        index += count;
    }
}

function resetOpday() {
    $("#touchDate").val('');
    $("#pathActiv").find("option:selected").attr("selected", false);
    $("#userValue").find("option:selected").attr("selected", false);
    $MB.refreshTable('opdayTable');
}
