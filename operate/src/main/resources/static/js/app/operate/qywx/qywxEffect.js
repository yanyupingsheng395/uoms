$(function () {
    getOverAllInfo();
    makePushChart();
    //加载所有的企业微信成员
    getAllFollowUserList();
    //加载转化情况
    getConvertDetailData();

    $("#followUserIdSelect").change(function() {
        getUserStatics($(this).find("option:selected").val());
    });
});

function getUserStatics(followUserId) {
    if(followUserId !== '') {
        $.get("/qywxDaily/getUserStatics", {headId: headId, followUserId:followUserId}, function (r) {
            var data = r.data;
            $("#msgNum").text(data['msgNum']);
            $("#executeMsgNum").text(data['executeMsgNum']);
            $("#coverNum").text(data['coverNum']);
            $("#executeCoverNum").text(data['executeCoverNum']);
            $("#convertNum1").text(data['convertNum']);
            $("#convertAmount1").text(data['convertAmount']);
            $("#convertRate1").text(data['convertRate']);
            $("#convertSpuNum1").text(data['convertSpuNum']);
            $("#convertSpuAmount1").text(data['convertSpuAmount']);
            $("#convertSpuRate1").text(data['convertSpuRate']);
        });
    }
}

// 获取页面头的当前日期和任务日期
function getOverAllInfo() {
    $.get("/qywxDaily/getOverAllInfo", {headId: headId}, function (r) {
        let data = r.data;
        $("#taskInfo").html('').append('<i class="mdi mdi-alert-circle-outline"></i>任务日期：' + data.taskDateStr + '，成功触达：'+data.successNum+'人，效果累计天数：'+data.effectDays);

        //填充其它数据
        $("#totalNum").text(data.totalNum);
        $("#successNum").text(data.successNum);
        $("#pushSuccessRate").text(data.pushSuccessRate);
        $("#convertNum").text(data.convertNum);
        $("#convertRate").text(data.convertRate);
        $("#convertAmount").text(data.convertAmount);

        $("#convertSpuNum").text(data.convertSpuNum);
        $("#convertSpuRate").text(data.convertSpuRate);
        $("#convertSpuAmount").text(data.convertSpuAmount);
    });
}

function getAllFollowUserList() {
    $.get("/qywxDaily/getFollowUserList", {headId: headId}, function (r) {
        let followUserId="";
        $.each(r.data,function (index,value) {
            if(index===0)
            {
                followUserId=value.followUserId;
                $("#followUserIdSelect").append("<option selected ='selected' value="+value.followUserId+">"+value.followUserName+"</option>");
            }else
            {
                $("#followUserIdSelect").append("<option value="+value.followUserId+">"+value.followUserName+"</option>");
            }
        })
        //加载一次数据
        getUserStatics(followUserId);
    });
}

/**
 * 加载转化情况
 */
function getConvertDetailData()
{
    var settings = {
        url: '/qywxDaily/getConvertDetailData',
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
                    headId: headId
                }
            };
        },
        columns: [[
            {
                title: '用户',
                align: "center",
                colspan: 2
            },
            {
                title: "推送与结果",
                align: "center",
                colspan: 3
            },
            {
                title: "推送时用户状态",
                align: "center",
                colspan: 2
            }
        ], [
            {
                field: 'followUserName',
                title: '员工名称',
                align: "center"
            },
            {
                field: 'qywxContactName',
                title: '客户名称',
                align: "center"
            },
            {
            field: 'convertInterval',
            align: "center",
            title: '转化间隔（天）'
        }, {
            field: 'pushSpu',
            align: "center",
            title: '推送SPU'
        }, {
            field: "spuIsConvert",
            align: "center",
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
            align: "center",
            title: "用户价值",
            formatter: function (value, row, index) {
                var res = "";
                switch (value) {
                    case "ULC_01":
                        res = "高价值低敏感";
                        break;
                    case "ULC_02":
                        res = "高价值较敏感";
                        break;
                    case "ULC_03":
                        res = "中价值高敏感";
                        break;
                    case "ULC_04":
                        res = "低价值低敏感";
                        break;
                    case "ULC_05":
                        res = "低价值高敏感";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        }, {
            field: "pathActive",
            align: "center",
            title: "用户活跃度",
            formatter: function (value, row, index) {
                let res = "";
                switch (value) {
                    case "UAC_01":
                        res = "促活节点";
                        break;
                    case "UAC_02":
                        res = "留存节点";
                        break;
                    case "UAC_03":
                        res = "弱流失预警";
                        break;
                    case "UAC_04":
                        res = "强流失预警";
                        break;
                    case "UAC_05":
                        res = "沉睡预警";
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
    $MB.initTable('qywxDailyConvertTable', settings);
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

