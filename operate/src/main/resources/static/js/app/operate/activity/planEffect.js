$(function () {
    getPlanEffectInfo(0);
     makePushChart();
    getEffectPersonalPage();
});

// 获取页面的汇总信息
function getPlanEffectInfo(kpiType) {
    $.get("/activityPlan/getPlanEffectInfo", {planId: planId,kpiType:kpiType}, function (r) {
        let data = r.data;
        $("#planDt").html('').append('<i class="mdi mdi-alert-circle-outline"></i>' + data["planDt"] + '对'+data['userCount']+'个用户进行个性化推送培养。');

        //更新累计数据
        $("#userCount").text(data.activitPf.userCount);
        $("#successCount").text(data.activitPf.successCount);
        $("#pushCost").text(data.activitPf.pushCost);
        $("#covUserCount").text(data.activitPf.covUserCount);
        $("#covAmount").text(data.activitPf.covAmount);
        $("#covRate").text(data.activitPf.covRate);
        $("#pushPerIncome").text(data.activitPf.pushPerIncome);
    });
}

$("#kpiType").change(function () {
    //判断计划阶段 如果是通知阶段，则不允许切换 否则刷新数据

    if(planType==='NOTIFY')
    {
        $("#kpiType").val("0");
        $MB.n_warning("通知阶段无法按推送并转化并成长查看效果！");
    }else
    {
        getPlanEffectInfo($("#kpiType").val());
    }

});

/**
 * 获取推送结果变化图
 */
function makePushChart() {
    $.get("/activityPlan/getPlanEffectTrend", {planId: planId}, function (r) {
        let data = r.data;
        let xdata = data['xdata'];
        let ydata1 = data['ydata1'];
        let ydata2 = data['ydata2'];
        let ydata3 = data['ydata3'];
        let ydata4 = data['ydata4'];
        let option1 = getChartOption(xdata, ydata1, ydata2, '转化人数/在推荐类目转化人数随时间变化图');
        let option2 = getRateChartOption(xdata, ydata3, ydata4, '转化率/在推荐类目转化率随时间变化图');
        let chart1 = echarts.init(document.getElementById("chart1"), 'macarons');
        chart1.setOption(option1);
        let chart2 = echarts.init(document.getElementById("chart2"), 'macarons');
        chart2.setOption(option2);
    });
}

// 获取echart数据
function getChartOption(xdata, yData1, yData2, title) {
    return {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                crossStyle: {
                    color: '#999'
                }
            }
        },
        legend: {
            data: ['转化人数', '在推荐类目转化人数'],
            align: 'right',
            right: 10
        },
        title: {
            text: title,
            x: 'center',
            y: 'bottom',
            textStyle: {
                //文字颜色
                color: '#000',
                //字体风格,'normal','italic','oblique'
                fontStyle: 'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight: 'normal',
                //字体系列
                fontFamily: 'sans-serif',
                //字体大小
                fontSize: 12
            }
        },
        xAxis: [
            {
                type: 'category',
                data: xdata,
                axisPointer: {
                    type: 'shadow'
                }
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '人数(人)',
                splitLine: {show: false},
                axisTick: {show: false},
                splitArea: {show: false},
            }
        ],
        series: [
            {
                name: '转化人数',
                type: 'line',
                data: yData1
            },
            {
                name: '在推荐类目转化人数',
                type: 'line',
                data: yData2
            }
        ]
    };
}

// 获取echart数据
function getRateChartOption(xdata, yData1, yData2, title) {
    return {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                crossStyle: {
                    color: '#999'
                }
            }
        },
        legend: {
            data: ['转化率(%)', '在推荐类目转化率(%)'],
            align: 'right',
            right: 10
        },
        title: {
            text: title,
            x: 'center',
            y: 'bottom',
            textStyle: {
                //文字颜色
                color: '#000',
                //字体风格,'normal','italic','oblique'
                fontStyle: 'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight: 'normal',
                //字体系列
                fontFamily: 'sans-serif',
                //字体大小
                fontSize: 12
            }
        },
        xAxis: [
            {
                type: 'category',
                data: xdata,
                axisPointer: {
                    type: 'shadow'
                }
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '人数(人)',
                splitLine: {show: false},
                axisTick: {show: false},
                splitArea: {show: false},
            }
        ],
        series: [
            {
                name: '转化率(%)',
                type: 'line',
                data: yData1
            },
            {
                name: '在推荐类目转化率(%)',
                type: 'line',
                data: yData2
            }
        ]
    };
}

// 获取个体效果分页数据
function getEffectPersonalPage() {
    var settings = {
        url: '/activityPlan/getPersonalPlanEffect',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    planId: planId
                }
            };
        },
        columns: [[
            {
                field: 'userId',
                title: '用户ID',
                align: "center",
                valign: "middle",
                rowspan: 2
            },
            {
                title: "推送与结果",
                align: "center",
                colspan: 5
            },
            {
                title: "推送时用户状态",
                align: "center",
                colspan: 2
            }
        ], [{
            field: 'pushPeriod',
            title: '推送时段'
        }, {
            field: 'convertPeriod',
            title: '转化时段'
        }, {
            field: 'convertInterval',
            title: '转化间隔（天）'
        }, {
            field: 'pushSpu',
            title: '推送SPU'
        }, {
            field: "spuIsConvert",
            title: "推送SPU是否转化",
            formatter: function (value, row, index) {
                let res = "-";
                if(value == 'Y') {
                    res = '是';
                }
                if(value == 'N') {
                    res = '否';
                }
                return res;
            }
        }, {
            field: "userValue",
            title: "用户价值",
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
        }, {
            field: "pathActive",
            title: "用户活跃度",
            formatter: function (value, row, index) {
                let res = "";
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
        }]]
    };
    $MB.initTable('effectPersonalTable', settings);
}

// 导出个体结果
// $("#btn_download").click(function () {
//     $MB.confirm({
//         title: "<i class='mdi mdi-alert-outline'></i>提示：",
//         content: "确定导出记录?"
//     }, function () {
//         $("#btn_download").text("下载中...").attr("disabled", true);
//         $.post("/daily/downloadExcel", {headId: headId}, function (r) {
//             if (r.code === 200) {
//                 window.location.href = "/common/download?fileName=" + r.msg + "&delete=" + true;
//             } else {
//                 $MB.n_warning(r.msg);
//             }
//             $("#btn_download").html("").append("<i class=\"fa fa-download\"></i> 导出").removeAttr("disabled");
//         });
//     });
// });


//返回
$("#backBtn").click(function () {
    window.location.href = "/page/activity/backToPlanList?planId=" + planId;
});
