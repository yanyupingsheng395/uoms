$(function () {
    getTaskDt();
    makePushChart();
    //加载所有的企业微信成员
    getAllQywxUserList();
    getEffectPersonalPage();

    $("#qywxUserIdSelect").change(function() {
        //重新加载数据
        $MB.refreshTable('effectPersonalTable');
    });
});

// 获取页面头的当前日期和任务日期
function getTaskDt() {
    $.get("/qywxDaily/getTaskInfo", {headId: headId}, function (r) {
        let data = r.data;
        $("#taskInfo").html('').append('<i class="mdi mdi-alert-circle-outline"></i>任务日期：' + data["taskDateStr"] + '，成功触达：'+data['successNum']+'人，效果累计天数：'+data['effectDays']);
    });
}

function getAllQywxUserList() {
    $.get("/qywxDaily/getQywxUserList", {headId: headId}, function (r) {
        $.each(r.data,function (index,value) {
            $("#qywxUserIdSelect").append("<option id="+value.qywxUserId+">"+value.qywxUserName+"</option>");
        })
    });
}

/**
 * 获取推送结果变化图
 */
function makePushChart() {
    $.get("/qywxDaily/getPushEffectChange", {headId: headId}, function (r) {
        let data = r.data;
        let xdata = data['xdata'];
        let ydata1 = data['ydata1'];
        let ydata2 = data['ydata2'];
        let ydata3 = data['ydata3'];
        let ydata4 = data['ydata4'];
        let option1 = getChartOption(xdata, ydata1, ydata2, "转化人数(人)", "转化率(%)", '推送转化人数，推送转化率随时间变化图');
        let option2 = getChartOption(xdata, ydata3, ydata4, "转化人数(人)", "转化率(%)", '推送且购买推荐SPU转化人数，推送且购买推荐SPU转化率随时间变化图');
        let chart1 = echarts.init(document.getElementById("chart1"), 'macarons');
        chart1.setOption(option1);
        let chart2 = echarts.init(document.getElementById("chart2"), 'macarons');
        chart2.setOption(option2);
    });
}

// 获取echart数据
function getChartOption(xdata, yData1, yData2, yName1, yName2, title) {
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
            data: ['转化人数(人)', '转化率(%)'],
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
                name: yName1,
                splitLine: {show: false},
                axisTick: {show: false},
                splitArea: {show: false},
            },
            {
                type: 'value',
                name: yName2,
                splitLine: {show: false},
                axisTick: {show: false},
                splitArea: {show: false}
            }
        ],
        series: [
            {
                name: yName1,
                type: 'line',
                data: yData1
            },
            {
                name: yName2,
                type: 'line',
                yAxisIndex: 1,
                data: yData2
            }
        ]
    };
}

// 获取个体效果分页数据
function getEffectPersonalPage() {
    var settings = {
        url: '/qywxDaily/getConversionList',
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
                    headId: headId,
                    qywxUserId: $("#qywxUserIdSelect").find("option:selected").val(),
                }
            };
        },
        columns: [
            {
                field: 'userId',
                title: '用户ID'
            },{
                field: 'qywxUserName',
                title: '推送成员'
            }, {
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
            }]
    };
    $("#effectPersonalTable").bootstrapTable('destroy').bootstrapTable(settings);
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

