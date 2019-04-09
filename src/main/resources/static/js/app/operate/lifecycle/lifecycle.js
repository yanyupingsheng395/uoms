var retention_option= {
    tooltip: {
        trigger: 'axis'
    },
    xAxis: {
        type: 'category',
        data: [],
        name: '',
        nameTextStyle: 'oblique',
        boundaryGap: false,
        nameRotate: 45
    },
    axisLabel:{
        interval: 0,
        rotate: 45
    },
    yAxis: {
        type: 'value',
        name: '',
        max: 50
    },
    series: [{
        data: [],
        type: 'line',
        smooth: true
    }]
};

var radar_option = {
    tooltip: {},
    legend: {
        data: ['组合1', '组合2','组合3']
    },
    radar: {
        indicator: [
            { name: '购买次数', max: 50},
            { name: '客单价', max: 1000},
            { name: '购买间隔', max: 365},
            { name: '购买品类指数', max: 10}
        ],
        center: ['50%','50%'],
        radius: 80
    },
    series: [{
        type: 'radar',
      //  itemStyle: {normal: {areaStyle: {type: 'default'}}},
        data : [
            {
                value : [30, 256, 25, 2],
                name : '组合1'
            },
            {
                value : [5, 999, 20, 1],
                name : '组合2'
            },
            {
                value : [40, 500, 45, 5],
                name : '组合3'
            }
        ]
    }]
};

var area_option = {
    tooltip : {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#6a7985'
            }
        }
    },
    xAxis: {
        type: 'category',
        boundaryGap: false,
        data: []
    },
    yAxis: {
        type: 'value'
    },
    series: [{
        data: [],
        type: 'line',
        areaStyle: {}
    }]
};
$(function () {

     //构造留存率与其它指标关系的四个图

    retention_option.xAxis.name="购买次数";
    retention_option.xAxis.data=['1','2','3','4','5','6','7','8','9','10','11+'];
    retention_option.yAxis.name="留存率(%)";
    retention_option.series[0].data=['3','5','5.4','6','12','14','17','17.5','17.6','17.7','17.8'];
    var freqChart = echarts.init(document.getElementById('retention_freq'), 'macarons');
    freqChart.setOption(retention_option);

    retention_option.xAxis.name="客单价";
    retention_option.xAxis.data=['0-50','50-100','100-150','150-200','200-250','250-300','300-350','350-400','400-450','450-500','500+'];
    retention_option.yAxis.name="留存率(%)";
    retention_option.series[0].data=['3','5','5.4','6','12','14','17','17.5','17.6','17.7','17.8'];
    var priceChart = echarts.init(document.getElementById('retention_price'), 'macarons');
    priceChart.setOption(retention_option);

    retention_option.xAxis.name="购买间隔（天）";
    retention_option.xAxis.data=['0-30','30-60','60-90','90-120','120-150','150-180','180-210','210-240','240-270','270-300','300+'];
    retention_option.yAxis.name="留存率(%)";
    retention_option.series[0].data=['3','5','5.4','6','12','14','17','17.5','17.6','17.7','17.8'];
    var gapChart = echarts.init(document.getElementById('retention_gap'), 'macarons');
    gapChart.setOption(retention_option);

    retention_option.xAxis.name="品类";
    retention_option.xAxis.data=['1','2','3','4','5','6','7','8','9','10','11+'];
    retention_option.yAxis.name="留存率(%)";
    retention_option.series[0].data=['3','5','5.4','6','12','14','17','17.5','17.6','17.7','17.8'];
    var catChart = echarts.init(document.getElementById('retention_cat'), 'macarons');
    catChart.setOption(retention_option);

    //初始化用户生命周期雷达图
    var radarChart = echarts.init(document.getElementById('lifecycle_radar'), 'macarons');
    radarChart.setOption(radar_option);

    //初始化面积图
    area_option.xAxis.data=['新客期','成长期','成熟期','衰退期','流失期'];
    area_option.series[0].data=[4300,3200,5000,3500,6000]
    var shapChart = echarts.init(document.getElementById('lifecycle_shape'), 'macarons');
    shapChart.setOption(area_option);

});