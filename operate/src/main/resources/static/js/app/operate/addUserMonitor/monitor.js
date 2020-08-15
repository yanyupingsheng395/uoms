$("#datePeriod").change(function () {
    $("#startDt").val("");
    $("#endDt").val("");
    $("#startDt").datepicker('destroy');
    $("#endDt").datepicker('destroy');
    var period = $(this).find("option:selected").val();
    if (period == 'Y') {
        init_date_begin("startDt", "endDt", "yyyy", 2, 2, 2);
        init_date_end("startDt", "endDt", "yyyy", 2, 2, 2);
    }
    if (period == "M") {
        init_date_begin("startDt", "endDt", "yyyy-mm", 1, 2, 1);
        init_date_end("startDt", "endDt", "yyyy-mm", 1, 2, 1);
    }
    if (period == "D") {
        init_date_begin("startDt", "endDt", "yyyy-mm-dd", 0, 2, 0);
        init_date_end("startDt", "endDt", "yyyy-mm-dd", 0, 2, 0);
    }
    $('#startDt').datepicker("setEndDate", new Date());
    $('#endDt').datepicker("setEndDate", new Date());
});

chart1();
function chart1() {
    var option = {
        title: {
            text: '企业微信通过申请人数变化趋势',
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
        tooltip: {
            trigger: 'axis',
            formatter: '{b} : {c}' + '人',
            axisPointer: {
                animation: false
            }
        },
        legend: {
            data: ['累计趋势', '分布趋势'],
            selected: {'累计趋势':true, '分布趋势':false}
        },
        xAxis: {
            name: '日期',
            type: 'category',
            data: ['202001', '202002', '202003', '202004', '202005', '202006', '202007'],
            splitLine:{show: false},
            splitArea : {show : false}
        },
        yAxis: {
            name: '申请通过人数（人）',
            type: 'value',
            splitLine:{show: false},
            splitArea : {show : false}
        },
        series: [{
            name: '累计趋势',
            data: [820, 932, 901, 934, 1290, 1330, 1320],
            type: 'line'
        },{
            name: '分布趋势',
            data: [182, 293, 390, 923, 1239, 1313, 12],
            type: 'line'
        }]
    };
    var chart = echarts.init(document.getElementById("chart1"), 'macarons');
    chart.on('legendselectchanged', function(params) {
        var option = this.getOption();
        var select_key = Object.keys(params.selected);
        var select_value = Object.values(params.selected);

        var current_name = params.name;
        if(current_name === '累计趋势') {
            if(select_value[select_key.indexOf(current_name)]) {
                console.log(option.legend)
                option.legend[0].selected['分布趋势'] = false;
            }else {
                option.legend[0].selected['分布趋势'] = true;
            }
        }
        if(current_name === '分布趋势') {
            if(select_value[select_key.indexOf(current_name)]) {
                option.legend[0].selected['累计趋势'] = false;
            }else {
                option.legend[0].selected['累计趋势'] = true;
            }
        }
        this.setOption(option)
    });
    chart.setOption(option);
}

chart2();
function chart2() {
    var option = {
        title: {
            text: '不同添加方式下的转化人数及转化率',
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
            data: ['主动添加', '触发添加', '主动添加转化率', '触发添加转化率', '主动添加平均转化率', '触发添加平均转化率']
        },
        grid: {
            top: '30%'
        },
        xAxis: [
            {
                name: '日期',
                type: 'category',
                data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
                axisPointer: {
                    type: 'shadow'
                },
                splitLine:{show: false},
                splitArea : {show : false}
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '转化人数',
                min: 0,
                max: 250,
                interval: 50,
                axisLabel: {
                    formatter: '{value}'
                },
                splitLine:{show: false},
                splitArea : {show : false}
            },
            {
                type: 'value',
                name: '转化率',
                min: 0,
                max: 25,
                interval: 5,
                axisLabel: {
                    formatter: '{value}%'
                },
                splitLine:{show: false},
                splitArea : {show : false}
            }
        ],
        series: [
            {
                name: '主动添加',
                type: 'bar',
                data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3]
            },
            {
                name: '触发添加',
                type: 'bar',
                data: [2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3]
            },
            {
                name: '主动添加转化率',
                type: 'line',
                yAxisIndex: 1,
                data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2]
            },
            {
                name: '触发添加转化率',
                type: 'line',
                yAxisIndex: 1,
                data: [3.0, 2.2, 4.3, 4.6, 2.3, 12.2, 10.3, 23.4, 23.0, 16.5, 12.0, 6.2]
            },
            {
                name: "主动添加平均转化率",
                data: [13.0, 13.0, 13.0, 13.0, 13.0, 13.0, 13.0, 13.0, 13.0, 13.0, 13.0, 13.0],
                type: 'line',
                yAxisIndex: 1,
                smooth: false,
                itemStyle: {
                    normal: {
                        lineStyle: {
                            width: 2,
                            type: 'dotted'  //'dotted'虚线 'solid'实线
                        }
                    }
                }
            },
            {
                name: "触发添加平均转化率",
                yAxisIndex: 1,
                data: [17.0, 17.0, 17.0, 17.0, 17.0, 17.0, 17.0, 17.0, 17.0, 17.0, 17.0, 17.0, 17.0],
                type: 'line',
                smooth: false,
                itemStyle: {
                    normal: {
                        lineStyle: {
                            width: 2,
                            type: 'dotted'  //'dotted'虚线 'solid'实线
                        }
                    }
                }
            }]
    };
    var chart = echarts.init(document.getElementById("chart2"), 'macarons');
    chart.setOption(option);
}

chart3();
function chart3() {
    var option = {
        title: {
            text: '不同添加方式下的推送人数',
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
            data: ['主动添加', '触发添加']
        },
        grid: {
            top: '30%'
        },
        xAxis: [
            {
                name: '日期',
                type: 'category',
                data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
                axisPointer: {
                    type: 'shadow'
                },
                splitLine:{show: false},
                splitArea : {show : false}
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '推送人数',
                min: 0,
                max: 250,
                interval: 50,
                axisLabel: {
                    formatter: '{value}'
                },
                splitLine:{show: false},
                splitArea : {show : false}
            }
        ],
        series: [
            {
                name: '主动添加',
                type: 'bar',
                data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3]
            },
            {
                name: '触发添加',
                type: 'bar',
                data: [2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3]
            }]
    };
    var chart = echarts.init(document.getElementById("chart3"), 'macarons');
    chart.setOption(option);
}