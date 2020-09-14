$(function () {
    getTaskDt();
    makePushChart();
    //加载所有的企业微信成员
    getAllQywxUserList();
    getEffectPersonalPage();

    $("#qywxUserIdSelect").change(function() {
        getUserStatics($(this).find("option:selected").val());
    });
});

function getUserStatics(qywxUserId) {
    if(qywxUserId !== '') {
        $.get("/qywxDaily/getUserStatics", {headId: headId, qywxUserId:qywxUserId}, function (r) {
            console.log(r);
            var data = r.data;
            $("#msgNum").text(data['msgNum']);
            $("#executeMsgNum").text(data['executeMsgNum']);
            $("#coverNum").text(data['coverNum']);
            $("#executeCoverNum").text(data['executeCoverNum']);
            $("#convertNum").text(data['convertNum']);
            $("#convertAmount").text(data['convertAmount']);
            $("#convertRate").text(data['convertRate']);
            $("#convertSpuNum").text(data['convertSpuNum']);
            $("#convertSpuAmount").text(data['convertSpuAmount']);
            $("#convertSpuRate").text(data['convertSpuRate']);
        });
    }
}

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

