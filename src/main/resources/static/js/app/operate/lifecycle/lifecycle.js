function tab2Init() {
    retention_time();
    freq_time("freq1", "次数（1-2次）");
    freq_time("freq2", "次数（2-10次）");
    freq_time("freq3", "次数（>=10次）");
    kpi_count("kpi1", "件单价（元）", 1);
    kpi_count("kpi2", "时间间隔（天）", 2);
    kpi_count("kpi3", "连带率（%）", 3);
    kpi_count("kpi4", "品类种数", 4);
    userCount();
    saleVolume();
}

function retention_time() {
    // 留存率与购买间隔
    var retention_option1= {
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',
            data: [],
            name: '',
            nameTextStyle: 'oblique',
            boundaryGap: false,
            splitArea : {show : false},
            splitLine:{show: false}
        },
        grid: [{
            height:'50%',
            left: '10%',
            right: '13%',
        }],
        yAxis: {
            type: 'value',
            name: '',
            max: 20,
            splitArea : {show : false},
            splitLine:{show: false},
        },
        series: [{
            data: [],
            type: 'line',
            smooth: true
        }]
    };

    //构造留存率与其它指标关系的三个图
    retention_option1.xAxis.name="购买次数";
    retention_option1.xAxis.data=['1','2','3','4','5','6','7','8','9','10','11+'];
    retention_option1.yAxis.name="留存率(%)";
    retention_option1.series[0].data=['3','5','5.4','6','12','14','12','5.5','5.6','4.7','3.8'];

    var freqChart = echarts.init(document.getElementById('retention_freq'), 'macarons');
    freqChart.setOption(retention_option1);
}

// 购买次数随时间间隔的变化
function freq_time(chartId, yName) {
    var option= {
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',
            data: [],
            name: '',
            nameTextStyle: 'oblique',
            boundaryGap: false,
            splitArea : {show : false},
            splitLine:{show: false}
        },
        grid: [{
            height:'50%',
            left: '19%',
            right: '29%',
        }],
        yAxis: {
            type: 'value',
            name: '',
            splitArea : {show : false},
            splitLine:{show: false},
        },
        series: [{
            data: [],
            type: 'line',
            smooth: true
        }]
    };

    //构造留存率与其它指标关系的三个图
    option.xAxis.name="时间间隔（天）";
    option.xAxis.data=['2','4','8','16','32','64', '128'];
    option.yAxis.name=yName;
    option.series[0].data=['3','5','10','15','10','5', '3'];

    var freqChart = echarts.init(document.getElementById(chartId), 'macarons');
    freqChart.setOption(option);
}

// 指标随购买次数变化
function kpi_count(chartId, yName, type) {
    var option= {
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',
            data: [],
            name: '',
            nameTextStyle: 'oblique',
            boundaryGap: false,
            splitArea : {show : false},
            splitLine:{show: false}
        },
        grid: [{
            height:'50%',
            left: '19%',
            right: '19%',
        }],
        yAxis: {
            type: 'value',
            name: '',
            splitArea : {show : false},
            splitLine:{show: false},
        },
        series: [{
            data: [],
            type: 'line',
            smooth: true
        }]
    };

    //构造留存率与其它指标关系的三个图
    option.xAxis.name="次数";
    option.xAxis.data=['1','5','10','15', '20'];
    option.yAxis.name=yName;
    if(type == 1) {
        option.series[0].data=['225','300','380','300','225'];
    }
    if(type == 2) {
        option.series[0].data=['3','6','9','6','3'];
    }
    if(type == 3) {
        option.series[0].data=['0.35','0.54','0.8','0.54','0.35'];
    }
    if(type == 4) {
        option.series[0].data=['5','10','20','10','5'];
    }

    var freqChart = echarts.init(document.getElementById(chartId), 'macarons');
    freqChart.setOption(option);
}

function userCount() {
    var option= {
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['新客期','成长期','成熟期','衰退期']
        },
        xAxis: {
            type: 'category',
            data: [],
            name: '',
            nameTextStyle: 'oblique',
            boundaryGap: false,
            splitArea : {show : false},
            splitLine:{show: false}
        },
        grid: [{
            height:'50%',
            left: '19%',
            right: '19%',
        }],
        yAxis: {
            type: 'value',
            name: '',
            splitArea : {show : false},
            splitLine:{show: false},
        },
        series: [{
            name: '',
            stack: '总量',
            data: [],
            type: 'line',
            smooth: true,
            areaStyle: {normal: {}}
        },{
            name: '',
            stack: '总量',
            data: [],
            type: 'line',
            smooth: true,
            areaStyle: {normal: {}}
        },{
            name: '',
            stack: '总量',
            data: [],
            type: 'line',
            smooth: true,
            areaStyle: {normal: {}}
        },{
            name: '',
            stack: '总量',
            data: [],
            type: 'line',
            smooth: true,
            areaStyle: {normal: {}}
        }]
    };

    //构造留存率与其它指标关系的三个图
    option.xAxis.name="时间";
    option.xAxis.data=['201901','201902','201903','201904','201905'];
    option.yAxis.name='用户数';
    option.series[0].data=['203','250','574','161','621','141'];
    option.series[1].data=['303','450','474','361','421','241'];
    option.series[2].data=['103','350','674','561','221','541'];
    option.series[3].data=['213','150','474','261','321','341'];
    option.series[0].name = '新客期';
    option.series[1].name = '成长期';
    option.series[2].name = '成熟期';
    option.series[3].name = '衰退期';

    var chart = echarts.init(document.getElementById("userCount"), 'macarons');
    chart.setOption(option);
}

function saleVolume() {
    var option= {
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['新客期','成长期','成熟期','衰退期']
        },
        xAxis: {
            type: 'category',
            data: [],
            name: '',
            nameTextStyle: 'oblique',
            boundaryGap: false,
            splitArea : {show : false},
            splitLine:{show: false}
        },
        grid: [{
            height:'50%',
            left: '19%',
            right: '19%',
        }],
        yAxis: {
            type: 'value',
            name: '',
            splitArea : {show : false},
            splitLine:{show: false},
        },
        series: [{
            name: '',
            stack: '总量',
            data: [],
            type: 'line',
            smooth: true,
            areaStyle: {normal: {}}
        },{
            name: '',
            stack: '总量',
            data: [],
            type: 'line',
            smooth: true,
            areaStyle: {normal: {}}
        },{
            name: '',
            stack: '总量',
            data: [],
            type: 'line',
            smooth: true,
            areaStyle: {normal: {}}
        },{
            name: '',
            stack: '总量',
            data: [],
            type: 'line',
            smooth: true,
            areaStyle: {normal: {}}
        }]
    };

    //构造留存率与其它指标关系的三个图
    option.xAxis.name="时间";
    option.xAxis.data=['201901','201902','201903','201904','201905'];
    option.yAxis.name='销售额（元）';
    option.series[0].data=['1030','1150','1274','1611','1321','1341'];
    option.series[1].data=['830','250','714','611','821','441'];
    option.series[2].data=['1030','250','1474','1611','1221','1341'];
    option.series[3].data=['630','1250','6174','3611','4221','1241'];
    option.series[0].name = '新客期';
    option.series[1].name = '成长期';
    option.series[2].name = '成熟期';
    option.series[3].name = '衰退期';

    var chart = echarts.init(document.getElementById("saleVolume"), 'macarons');
    chart.setOption(option);
}