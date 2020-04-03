$(function () {
    getUserSpu(userId);
    getDateChart(userId);
});
function getUserSpu(userId) {
    $.get("/insight/getUserSpu", {userId: userId}, function (r) {
        let code = "";
        r.data.forEach((v,k)=> {
            code += "<option value='"+v['SPU_WID']+"'>" + v['SPU_NAME'] + "</option>";
        });
        $("#spuId").html('').append(code);
        $("#spuId").select2({
            placeholder: '请选择'
        });
        // 获取spu下的购买次序
        getUserBuyOrder(userId);
    });
}

// 获取某个用户在某个spu下的购买次序
function getUserBuyOrder(userId) {
    var spuId = $("#spuId").val();
    var spuName = $("#spuId").find("option:selected").text();
    $.get("/insight/getUserBuyOrder", {spuId: spuId, userId: userId}, function (r) {
        if(r.code === 200) {
            var n = parseInt(r.data);
            var m = parseInt(r.data) + 1;
            $("#buyOrder").val(n + "-" + m);
            getSpuRelation(userId, spuId, n, spuName);
        }else {
            $MB.n_danger("未知错误！");
        }
    });
}

// 获取spu下商品分布图
function getSpuRelation(userId, spuId, buyOrder, spuName) {
    var spu_relation_chart = echarts.init(document.getElementById("chart_relation_1"), 'macarons');
    var product_relation_chart = echarts.init(document.getElementById("chart_relation_2"), 'macarons');
    spu_relation_chart.showLoading({
        text : '加载数据中...'
    });
    product_relation_chart.showLoading({
        text : '加载数据中...'
    });
    $.get("/insight/getUserSpuRelation", {userId: userId, spuId:spuId, buyOrder:buyOrder}, function (r) {
        ebpProductIdArray = r.data.productId;
        nextProductIdArray = r.data.nextProductId;
        var option1 = getSpuChartOption(r.data, spuName, buyOrder);
        spu_relation_chart.hideLoading();
        spu_relation_chart.setOption(option1);
        var option2 = getProductOption(r.data.xdata2, r.data.ydata2, spuName, buyOrder, r.data);
        product_relation_chart.hideLoading();
        product_relation_chart.setOption(option2);

        let ebpProductId = r.data['ebpProductMap']['EBP_PRODUCT_ID'];
        let ebpProductName = r.data['ebpProductMap']['EBP_PRODUCT_NAME'];
        let nextEbpProductId = r.data['ebpProductMap']['NEXT_EBP_PRODUCT_ID'];
        let nextEbpProductName = r.data['ebpProductMap']['NEXT_EBP_PRODUCT_NAME'];
        // 获取转化概率
        getConvertRateChart(spuId, buyOrder, ebpProductId, nextEbpProductId, ebpProductName);
        // 获取用户表格
        getGrowthUserTable(userId, spuId);

        // 获取用户在类目的成长价值
        getUserValue(userId);
    });
}

let convert_product;
function getProductOption(xdata, ydata, spuName, purchOrder, data) {
    convert_product = xdata[0];
    var option = {
        color: ['#3398DB'],
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        grid: {
            left: '8%',
            right: '22%',
            containLabel: true
        },
        xAxis : [
            {
                name: '下次有可能购买的商品',
                type : 'category',
                data : xdata,
                axisTick: {
                    alignWithLabel: true
                },
                axisLabel: {
                    show: true,
                    interval:0,
                    rotate:45
                }
            }
        ],
        yAxis : [
            {
                name: '购买概率(%)',
                type : 'value',
                splitArea: {show: false},
                splitLine: {show: false}
            },

        ],
        series : [
            {
                type:'bar',
                barWidth: '60%',
                data:ydata,
                itemStyle: {
                    normal: {
                        color: function (d) {
                            if(d.dataIndex === 0) {
                                return "#3398DB";
                            }else {
                                return "rgba(51,152,219,0.4)";
                            }
                        }
                    }
                }
            }
        ],
        title: {
            text: '在'+spuName+'类目第'+purchOrder+'次购买'+data['ebpProductMap']['EBP_PRODUCT_NAME']+'的用户，第'+(parseInt(purchOrder) + 1)+'次购买商品的概率分布',
            x: 'center',
            y: 'top',
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
        }
    };
    return option;
}
function getSpuChartOption(data, spuName, purchOrder) {
    var flag = false;
    let option = {
        color: ['#CD2626'],
        title: {
            text: '用户在' + spuName + '类目第' + purchOrder + '次购买的商品分布',
            x: 'center',
            y: 'top',
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
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            },
            formatter: function (params) {
                var tar = params[1];
                // return tar.name + '<br/>' + tar.seriesName + ' : ' + tar.value;
                return tar.name + ":"  + tar.value;
            }
        },
        grid: {
            left: '8%',
            right: '19%',
            containLabel: true
        },
        xAxis : [
            {
                name: "购买类目/商品",
                type : 'category',
                data : data.xdata1,
                axisTick: {
                    alignWithLabel: true
                },
                axisLabel: {
                    show: true,
                    interval:0,
                    rotate:45
                }
            }
        ],
        yAxis : [
            {
                name: '用户数(人)',
                type : 'value',
                splitArea: {show: false},
                splitLine: {show: false}
            }
        ],
        series : [
            {
                name: '辅助',
                type: 'bar',
                stack:  '总量',
                itemStyle: {
                    normal: {
                        barBorderColor: 'rgba(0,0,0,0)',
                        color: 'rgba(0,0,0,0)'
                    },
                    emphasis: {
                        barBorderColor: 'rgba(0,0,0,0)',
                        color: 'rgba(0,0,0,0)'
                    }
                },
                data: data.ydataReduce
            },
            {
                type: 'bar',
                stack: '总量',
                label: {
                    normal: {
                        show: true,
                        position: 'inside'
                    }
                },
                data:data.ydataActual,
                itemStyle: {
                    normal: {
                        color: function (d) {
                            if(d.name === data['ebpProductMap']['EBP_PRODUCT_NAME']) {
                                flag = true;
                                return "#CD2626";
                            }else {
                                if(!flag && d.name === '其他') {
                                    return '#CD2626';
                                }
                                return "rgba(205,38,38,0.4)";
                            }
                        }
                    }
                }
            }
        ]
    };

    return option;
}

// 获取购买概率随购买间隔的分布图
function getConvertRateChart(spuId, purchOrder, ebpProductId, nextProductId, ebpProductName) {
    var spuName = $("#spuId").find("option:selected").text();
    var buyOrder = $("#buyOrder").val();
    var chart = echarts.init(document.getElementById("convert_chart"), 'macarons');
    chart.showLoading({
        text : '加载数据中...'
    });
    $.get("/insight/getConvertRateChart", {spuId:spuId, purchOrder:purchOrder, ebpProductId:ebpProductId, nextEbpProductId:nextProductId}, function (r) {
        var data = r.data;
        let option = {
            legend: {
                data: ['实际值', '拟合值', '距今购买间隔'],
                selected: {'实际值': false, '拟合值': true, '距今购买间隔': true},
                top: '25'
            },
            tooltip: {
                trigger: 'axis',
                padding: [15, 20],
                axisPointer: {
                    type: 'shadow'
                }
            },
            xAxis: {
                name: '购买间隔(天)',
                type: 'category',
                data: data.xdata,
                splitLine:{show: false},
                splitArea : {show : false}
            },
            yAxis: {
                name: "购买概率(%)",
                type: 'value',
                splitLine:{show: false},
                splitArea : {show : false}
            },
            series: [{
                name: '拟合值',
                data: data.zdata,
                type: 'line',
                itemStyle: {
                    normal: {
                        color: '#AAF487',
                        borderColor: "#AAF487"
                    }
                }
            }, {
                name: '实际值',
                data: data.ydata,
                type: 'line'
            }, {
                name: '距今购买间隔',
                type: 'line',
                markLine :{
                    symbol: ['none', 'none'],//去掉箭头
                    itemStyle: {
                        normal: {
                            yAxisIndex: 0,
                            lineStyle: {
                                type: 'dotted',
                                color:{//设置渐变
                                    type: 'linear',

                                    colorStops: [{
                                        offset: 0, color: 'red '// 0% 处的颜色
                                    }, {
                                        offset: 1, color: 'red' // 100% 处的颜色
                                    }],
                                    global: false // 缺省为 false
                                }
                            },
                            label: {
                                show: true,
                                position:'middle',
                                color: 'red',
                                formatter:'{c} 天'
                            }
                        }
                    },
                    data: [{
                        xAxis : getUserLastBuyDual(spuId)
                    }]
                }
            }],
            grid: {left: '8%',right:'19%'},
            title: {
                text: '在'+spuName+'类目中，第'+buyOrder.split('-')[0]+'次购买'+ebpProductName+'，第'+buyOrder.split('-')[1]+'次购买'+convert_product+'时，购买间隔与购买的概率分布',
                x: 'center',
                y: 'top',
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
            }
        };
        chart.hideLoading();
        chart.setOption(option);
    });
}

// 获取用户上一次购买距离每日运营的天数
function getUserLastBuyDual(spuId) {
    var data = '';
    $.ajax({
        url:"/insight/getUserBuyDual",
        async: false,
        data:{taskDt: task_dt, userId: userId, spuId:spuId},
        success: function (r) {
            data = r.data;
        }
    });
    return data;
}

function getGrowthUserTable(userId, spuId) {
    $.get("/insight/getUserBuyDual", {taskDt:task_dt, userId: userId, spuId:spuId}, function (res) {
       var dual = res.data;
       var flag = false;
        $.get("/insight/getUserGrowthPathPoint", {userId:userId, spuId:spuId}, function (r) {
            let code = "<thead><tr><th>下一步成长节点</th><th>距上次购买第天（天）</th><th>再次购买概率（%）</th><th>用户上次购买时间</th><th>用户成长节点日期</th></tr></thead>";
            code += "<tbody>";
            r.data.forEach((v,k)=>{
                if(k < 3) {
                    if(!flag && v['ACTIVE_DUAL'] > dual) {
                        flag = true;
                        code += "<tr style='background-color: #FFEFD5'><td>"+v['ACTIVE_TYPE']+"</td><td>"+v['ACTIVE_DUAL']+"</td><td>"+v['PROB']+"</td><td>"+v['LAST_BUY_DT']+"</td><td>"+v['GROWTH_DT']+"</td></tr>";
                    }else {
                        code += "<tr><td>"+v['ACTIVE_TYPE']+"</td><td>"+v['ACTIVE_DUAL']+"</td><td>"+v['PROB']+"</td><td>"+v['LAST_BUY_DT']+"</td><td>"+v['GROWTH_DT']+"</td></tr>";
                    }
                }else {
                    code += "<tr style='background-color: #ccc'><td>"+v['ACTIVE_TYPE']+"</td><td>"+v['ACTIVE_DUAL']+"</td><td>"+v['PROB']+"</td><td>"+v['LAST_BUY_DT']+"</td><td>"+v['GROWTH_DT']+"</td></tr>";
                }
            });
            code += "</tbody>";
            if(code === "") {
                code = "<tr class='text-center'><td colspan='5'>没有找到匹配的记录</td></tr>";
            }
            $("#growthTable").html('').append(code).find("tbody td:eq(3)").attr("style", "background-color:#fff;");
            mergeCell('growthTable', 0, 5, 3);
        });
    });
}

function mergeCell(table1, startRow, endRow, col) {
    var tb = document.getElementById(table1);
    if(!tb || !tb.rows || tb.rows.length <= 0) {
        return;
    }
    if(col >= tb.rows[0].cells.length || (startRow >= endRow && endRow != 0)) {
        return;
    }
    if(endRow == 0) {
        endRow = tb.rows.length - 1;
    }
    for(var i = startRow; i < endRow; i++) {
        if(tb.rows[startRow].cells[col].innerHTML == tb.rows[i + 1].cells[col].innerHTML) { //如果相等就合并单元格,合并之后跳过下一行
            tb.rows[i + 1].removeChild(tb.rows[i + 1].cells[col]);
            tb.rows[startRow].cells[col].rowSpan = (tb.rows[startRow].cells[col].rowSpan) + 1;
        } else {
            mergeCell(table1, i + 1, endRow, col);
            break;
        }
    }
}
// 获取用户在类目的成长价值
function getUserValue(userId) {
    var spuId = $("#spuId").val();
    $.get("/insight/getUserValueWithSpu", {userId: userId, spuId:spuId}, function (r) {
        var option = getUserValueOption(r.data);
        var chart = echarts.init(document.getElementById("userValueChart"), 'macarons');
        chart.setOption(option);
    });
}

function getUserValueOption(data) {
    var current = [
        data.current
    ];

    var schema = [{
        name: '用户对类目的收入贡献',
        index: 0,
        text: '用户对类目的收入贡献'
        },
        {
            name: '用户对类目的价值潜力',
            index: 1,
            text: '用户对类目的价值潜力'
        },
        {
            name: '用户对类目价格敏感度',
            index: 2,
            text: '用户对类目价格敏感度'
        },
        {
            name: '用户在类目的价值',
            index: 3,
            text: '用户在类目的价值'
        }
    ];
    var lineStyle = {
        normal: {
            width: 1,
            opacity: 1
        }
    };
    return {
        color: ['#da0d68', '#975e6d', '#a2b029', '#c78936', '#8c292c', '#f89a1c', '#dd4c51', '#3e0317', '#f2684b', '#be8663', '#6569b0'],
        parallelAxis: [{
            left: '20%',
            dim: 0,
            name: schema[0].text,
            type: 'category',
            data: ['低', '中', '高', '很高','超高']
        },
            {
                dim: 1,
                name: schema[1].text,
                type: 'category',
                data:['低', '高']
            },
            {
                dim: 2,
                name: schema[2].text,
                type: 'category',
                data: ['高', '中', '低']
            },
            {
                dim: 3,
                name: schema[3].text,
                type: 'category',
                data: ['长尾', '普通', '主要', '重要']
            }
        ],
        parallel: {
            left: '6%',
            right: '13%',
            bottom: '15%',
            top: '16%',
            parallelAxisDefault: {
                type: 'value',
                nameLocation: 'end',
                nameGap: 20
            }
        },
        series: [{
            name: '当前用户',
            type: 'parallel',
            lineStyle: lineStyle,
            smooth: false,
            data: current
        }]
    };
}

// 转化时间
function getDateChart(userId) {
    $.get("/insight/getUserConvert", {userId: userId}, function (r) {
        getDateChartOption('chart1', r.data['data1'], '转化');
        getDateChartOption('chart2', r.data['data2'], '推送');
        getDateChartOption('chart3', r.data['data3'], '推送并转化');
    });
}

function getDateChartOption(chartId, data, title) {
    var color = ['#da0d68', '#975e6d', '#a2b029', '#c78936', '#8c292c', '#f89a1c', '#dd4c51', '#3e0317', '#f2684b', '#be8663', '#6569b0'];
    var hours = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9','10','11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'];
    var days = ['星期六', '星期五', '星期四', '星期三', '星期二', '星期一', '星期日'];
    var option = {
        title: {
            text: title + '时间',
            x: 'center',
            y: 'top',
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
                fontSize: 12,
            }
        },
        color: [color[Math.floor(Math.random()*color.length)]],
        polar: {},
        tooltip: {
            formatter: function (params) {
                return params.value[0] + "：" + hours[params.value[1]] + "点，当前用户有" + params.value[2] + "1次" + title;
            }
        },
        angleAxis: {
            type: 'category',
            data: hours,
            boundaryGap: false,
            splitLine: {
                show: true,
                lineStyle: {
                    color: '#999',
                    type: 'dashed'
                }
            },
            axisLine: {
                show: false
            }
        },
        radiusAxis: {
            type: 'category',
            data: days,
            axisLine: {
                show: false
            },
            axisLabel: {
                interval:0,
                rotate: 45
            }
        },
        series: [{
            name: 'Punch Card',
            type: 'scatter',
            coordinateSystem: 'polar',
            symbolSize: function (val) {
                return val[2] * 10;
            },
            data: data,
            animationDelay: function (idx) {
                return idx * 5;
            }
        }]
    };
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
}