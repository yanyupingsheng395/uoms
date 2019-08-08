$(function () {
    getTaskDt();
    getKpiVal();
    getKpiStatis();
    getKpiTrend();
});
// 获取日期
function getTaskDt() {
    $.get("/daily/getCurrentAndTaskDate", {headId:headId}, function (r) {
        var data = r.data;
        $("#taskDt").html('').append('<i class="mdi mdi-alert-circle-outline"></i>当前日期：'+data['currentDt']+'，任务：'+data['taskDt']+'');
    });
}

// 获取指标值
function getKpiVal() {
    $.get("/daily/getKpiVal", {headId:headId}, function (r) {
        var data = r.data;
        var data1 = new Array();
        data1.push({value: data['totalNum'] == null ? '-' : data['totalNum'], name: '建议推送人数'});
        data1.push({value: data['optNum']== null ? '-' : data['optNum'], name: '实际选择人数'});
        data1.push({value: data['successNum']== null ? '-' : data['successNum'], name: '成功触达人数'});
        data1.push({value: data['convertCount']== null ? '-' : data['convertCount'], name: '任务转化人数'});
        var data2 = "执行率 " + (data['executeRate'] == null ? '-' : data['executeRate']) + "%";
        var data3 = "触达率 " + (data['touchRate'] == null ? '-' : data['touchRate']) + "%";
        var data4 = "转化率 " + (data['convertRate'] == null ? '-' : data['convertRate']) + "%";
        createFunnelChart('chart1', data1, data2, data3, data4);
    });
}

function createFunnelChart(chartId, data1, data2, data3, data4) {
    var colors=['#f36119','#ff9921','#20c8ff','#2cb7ff','#1785ef'];
    var option = {
        backgroundColor:'#ffffff',
        color:colors,
        "tooltip": {
            "trigger": "axis",
            "axisPointer": {
                "type": "cross",
                "label": {
                    "backgroundColor": "red"
                },
                "lineStyle": {
                    "color": "#9eb2cb"
                }
            }
        },
        grid: {
            top: 20,
            left: "2%",
            right: 20,
            bottom: 30,
            containLabel: true,
            borderWidth: 0.5
        },

        series: [
            {
                top:0,
                type: 'funnel',
                left: '10%',
                width: '80%',
                gap: 16,
                minSize: 150,
                maxSize: 410,
                label: {
                    show: true,
                    position: 'inside',
                    formatter: '{b}  ({c}人)'
                },
                data:data1
            },

            {
                top:0,
                type: 'funnel',
                left: '10%',
                width: '80%',
                gap: 100,
                z:1,
                minSize: 150,
                maxSize: 150,
                label: {
                    normal: {
                        color: '#000000',
                        position: 'right',
                        backgroundColor:'#f6f7fc',
                        borderColor:'green',
                        borderWidth :1,
                        borderRadius :4,
                        padding :[11,25],
                        width:100
                    }
                },
                //右侧的百分比显示的地方
                labelLine: {
                    show:true,
                    normal: {
                        length: 200,
                        position: 'center',
                        lineStyle: {
                            width: 1,
                            color:'green',
                            type:'solid'

                        }
                    }
                },
                //主体是透明的
                itemStyle: {
                    normal: {
                        color: 'transparent',
                        borderWidth:4,
                        opacity: 1
                    }
                },
                data: [
                    {value: 100, name: '',labelLine:{show:false},label:{show:false}},
                    {value: 80, name: data2},
                    {value: 60, name: data3},
                    {value: 40, name: data4},
                    {value: 20, name: '',labelLine:{show:false},label:{show:false}},
                ]
            }
        ]
    };
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
}

function getKpiStatis() {
    $.get("/daily/getKpiStatis", {headId:headId}, function (r) {
        var data = r.data;
        var code = "";
        var convertAmout = data['convertAmount']==null?'-':data['convertAmount'];
        var subsidyAmount = data['subsidyAmount']==null?'-':data['subsidyAmount'];
        var subsidyPerUnit = data['subsidyPerUnit']==null?'-':data['subsidyPerUnit'];
        var subsidyCount = data['subsidyCount']==null?'-':data['subsidyCount'];
        var cancleCount = data['cancleCount']==null?'-':data['cancleCount'];
        var cancleRate = data['cancleRate']==null?'-':data['cancleRate'];

        var convertAmout = data['convertAmount']==null?'-':data['convertAmount'];
        code += "<tr class='text-center'><td>任务转化金额（元）</td><td>"+convertAmout+"</td><td>补贴发放数（个）</td><td>"+subsidyCount+"</td></tr>";
        code += "<tr class='text-center'><td>任务补贴金额（元）</td><td>"+subsidyAmount+"</td><td>补贴核销数（个）</td><td>"+cancleCount+"</td></tr>";
        code += "<tr class='text-center'><td>每元补贴创收（元）</td><td>"+subsidyPerUnit+"</td><td>核销率（%）</td><td>"+cancleRate+"</td></tr>";
        $("#statisData").html('').append(code);
    });
}

function getKpiTrend() {
    $.get("/daily/getKpiTrend", {headId:headId}, function (r) {
        var data = r.data;
        createTrendChart('chart4', data['convertAmount']);
        createTrendChart('chart5', data['convertCnt']);
        createTrendChart('chart6', data['sellCnt']);
    });
}

function createTrendChart(chartId, data) {
    var xAxisData = data['xAxisData'];
    var xAxisName = data['xAxisName'];
    var yAxisName = data['yAxisName'];
    var yAxisData = data['yAxisData'];
    var seriesData = [{type: 'line', data:yAxisData}];
    var option = getOption(null,xAxisData,xAxisName,yAxisName,seriesData);
    option.grid = {
        left: '5%',
        right: '14%',
        top:'15%',
        bottom: '2%',
        containLabel: true
    };
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
}