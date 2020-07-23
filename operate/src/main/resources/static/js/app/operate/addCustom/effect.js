$(function () {
    var xdata=['7-01','7-02','7-03','7-04','7-05','7-06','7-07','7-08','7-09','7-10','7-11','7-12','7-13','7-14','7-15','7-16','7-17','7-18','7-19','7-20','7-21'];
    var ydata1=[561,199,548,184,199,210,188,122,56,61,660,309,374,174,536,226,187,151,84,355,189,534];
    var ydata2=[41,33,49,49,19,50,15,9,3,13,138,89,108,19,150,40,26,27,10,102,24,64];
    let chart1 = echarts.init(document.getElementById("chart1"), 'macarons');
    let option1 = getChartOption(xdata, ydata1, ydata2, "发送申请人数(人)", '发送申请人数，通过申请人数随时间变化图');
    chart1.setOption(option1);
});


// 获取echart数据
function getChartOption(xdata, yData1, yData2, yName, title) {
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
            data: ['发送申请人数(人)', '通过申请人数(人)'],
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
                boundaryGap: false,
                axisPointer: {
                    type: 'shadow'
                }
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: yName,
                splitLine: {show: false},
                axisTick: {show: false},
                splitArea: {show: false},
            }
        ],
        series: [
            {
                name: '发送申请人数(人)',
                type: 'line',
                data: yData1
            },
            {
                name: '通过申请人数(人)',
                type: 'line',
                data: yData2
            }
        ]
    };
}
