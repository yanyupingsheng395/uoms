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


function search_data() {
    getData();
}

function reset_data() {
    var currentYear = new Date().getFullYear();
    var currentMonth = new Date().getMonth() + 1;
    $("#startDt").val(currentYear + "-01");
    $("#endDt").val(currentYear + "-" + (currentMonth < 10 ? "0" + currentMonth : currentMonth));
    getData();
}
function getData() {
    var startDt = $("#startDt").val();
    var endDt = $("#endDt").val();
    var datePeriod = $("#datePeriod").val();

    if(startDt === '' || endDt === '') {
        $MB.n_warning("开始时间和结束时间不能为空！");
    }else {
        $.get("/addUserMonitor/getApplySuccessData", {startDt:startDt, endDt:endDt, dateType: datePeriod}, function (r) {
            var data = r.data;
            var xdata = data.map(v=>v['add_date']);
            var ydata1 = data.map(v=>v['sum_cnt']);
            var ydata2 = data.map(v=>v['per_cnt']);
            chart1(xdata, ydata1, ydata2);
        });

        $.get("/addUserMonitor/getConvertCntAndRate", {startDt:startDt, endDt:endDt, dateType: datePeriod}, function (r) {
            var data = r.data;
            chart2(data['dateList'], data['pCnt'], data['tCnt'], data['pRate'], data['tRate'], data['pRateAvgList'], data['tRateAvgList']);
            chart3(data['dateList'], data['pTotal'], data['tTotal']);
        });
    }
}

function chart1(xdata, ydata1, ydata2) {
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
            data: xdata,
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
            data: ydata1,
            type: 'line'
        },{
            name: '分布趋势',
            data: ydata2,
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

function chart2(xdata, ydata1, ydata2, ydata3, ydata4, ydata5, ydata6) {
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
                data: xdata,
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
                data: ydata1
            },
            {
                name: '触发添加',
                type: 'bar',
                data: ydata2
            },
            {
                name: '主动添加转化率',
                type: 'line',
                yAxisIndex: 1,
                data: ydata3
            },
            {
                name: '触发添加转化率',
                type: 'line',
                yAxisIndex: 1,
                data: ydata4
            },
            {
                name: "主动添加平均转化率",
                data: ydata5,
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
                data: ydata6,
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

function chart3(xdata, ydata1, ydata2) {
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
                data: xdata,
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
                data: ydata1
            },
            {
                name: '触发添加',
                type: 'bar',
                data: ydata2
            }]
    };
    var chart = echarts.init(document.getElementById("chart3"), 'macarons');
    chart.setOption(option);
}