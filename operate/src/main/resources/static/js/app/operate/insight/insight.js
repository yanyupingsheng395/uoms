let retention_fit = false;
let retention_change_fit = false;
// 图表初始化
var chart_retention = echarts.init(document.getElementById("chart1"), 'macarons');
const chart_retention_change = echarts.init(document.getElementById("chart11"), 'macarons');

$(function () {
    initChart();
    findUserCntList();
    findSpuValueList();
});

function initChart() {
    chart_retention_change.on('legendselectchanged', function(obj) {
        var legend = obj.name;
        if(legend === '拟合值' && !retention_change_fit) {
            chart_retention_change.showLoading({
                text : '正在加载数据'
            });
            $.get("/insight/getRetentionChangeFitData", {id: $("#spuProductId1").val(), type: $("#spuProductType1").val(), period: $("#period").val()}, function (r) {
                data.fdata = r.data;
                var option = getOptionWithFit(data, "再次购买spu概率的变化率（%）", "再次购买spu概率的变化率");
                option.legend.selected = {
                    '实际值':true,
                    '拟合值':true
                };
                chart_retention_change.hideLoading();
                chart_retention_change.setOption(option);
                retention_change_fit = true;
            });
        }
    });

    chart_retention.on('legendselectchanged', function(obj) {
        var legend = obj.name;
        if(legend === '拟合值' && !retention_fit) {
            chart_retention.showLoading({
                text : '正在加载数据'
            });
            $.get("/insight/getRetentionFitData", {id: $("#spuProductId1").val(), type: $("#spuProductType1").val(), period:  $("#period").val()}, function (r) {
                data.fdata = r.data;
                var option = getOptionWithFit(data, "再次购买spu概率（%）", "再次购买spu概率");
                option.legend.selected = {
                    '实际值':true,
                    '拟合值':true
                };
                chart_retention.hideLoading();
                chart_retention.setOption(option);
                retention_fit = true;
            });
        }
    });
}

// 按时间范围查询
function searchInsight() {
    var dateRange = $("#dateRange").val();
    findUserCntList();
    $("#sankeyFrame").attr("src", "/sankey?dateRange=" + dateRange);
    $("#breakBtn").attr("href", "/sankey?dateRange=" + dateRange);

    findSpuValueList();
    findImportSpu();
}

function resetInsight() {
    $("#dateRange").find("option:selected").removeAttr("selected");
}

function findUserCntList() {
    var settings = {
        columns: [
            {
                field: 'purchType',
                title: '用户在品牌的购买次序',
                align: 'center'
            }, {
                field: 'purch1',
                title: '第1次',
                align: 'center'
            }, {
                field: 'purch2',
                title: '第2次',
                align: 'center'
            }, {
                field: 'purch3',
                title: '第3次',
                align: 'center'
            }, {
                field: 'purch4',
                title: '第4次'
            }, {
                field: 'purch5',
                title: '第5次',
                align: 'center'
            }, {
                field: 'purch6',
                title: '第6次',
                align: 'center'
            }, {
                field: 'purch7',
                title: '第7次',
                align: 'center'
            }, {
                field: 'purch8',
                title: '第8次',
                align: 'center'
            }]
    };
    $( "#userCntTable" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
    $.get( "/insight/findUserCntList", {dateRange: $( "#dateRange" ).val()}, function (r) {
        if (r.code == 200) {
            $( "#userCntTable" ).bootstrapTable( 'load', r.data );
        }
    });
}

function findSpuValueList() {
    var settings = {
        url: '/insight/findSpuValueList',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageSize: 5,
        pageList: [5, 15, 25, 50],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                sort: params.sort,
                order: params.order,
                param: {dateRange: $("#dateRange").val()}
            };
        },
        columns: [
            [
                {
                    field: 'growthNumber',
                    title: '旅程编号',
                    align:"center",
                    valign:"middle",
                    rowspan: 2
                },{
                field: '',
                title: '用户成长旅程的价值',
                align:"center",
                valign:"middle",
                colspan: 4
            }, {
                field: '',
                title: '用户在品牌的购买次序',
                align:"center",
                valign:"middle",
                colspan: 8
            }
            ],[
                {
                    field: 'copsValue',
                    title: '综合价值',
                    sortable : true
                },{
                    field: 'incomeValue',
                    title: '收入价值',
                    sortable : true
                }, {
                    field: 'stepValue',
                    title: '步长价值',
                    sortable : true
                }, {
                    field: 'universValue',
                    title: '普适性价值',
                    sortable : true
                }, {
                    field: 'purch1SpuName',
                    title: '第1次'
                }, {
                    field: 'purch2SpuName',
                    title: '第2次'
                }, {
                    field: 'purch3SpuName',
                    title: '第3次'
                }, {
                    field: 'purch4SpuName',
                    title: '第4次'
                }, {
                    field: 'purch5SpuName',
                    title: '第5次'
                }, {
                    field: 'purch6SpuName',
                    title: '第6次'
                }, {
                    field: 'purch7SpuName',
                    title: '第7次'
                }, {
                    field: 'purch8SpuName',
                    title: '第8次'
                }
            ]
        ],onLoadSuccess: function() {
            var tableId = document.getElementById('spuValueTable');
            tableId.rows[tableId.rows.length - 1].setAttribute("style", "background-color:#E7EAEC;");
        }
    };
    $("#spuValueTable").bootstrapTable('destroy').bootstrapTable(settings);
}

// 重要spu
function findImportSpu() {
    var settings = {
        url: '/insight/findImportSpuList',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageSize: 5,
        pageList: [5, 15, 25, 50],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    spuName: $("#spuName").find("option:selected").text(),
                    purchOrder: $("#purchOrder1").val(),
                    dateRange: $("#dateRange").val()
                }
            };
        },
        columns: [
            {
                field: 'spuName',
                title: 'SPU名称'
            }, {
                field: 'contributeRate',
                title: '本次购买的用户贡献率（%）',
                sortable : true
            },{
                field: 'nextPurchProbal',
                title: '本购SPU后再购概率（%）',
                sortable : true
            }, {
                field: 'sameSpuProbal',
                title: '本购SPU后再购同SPU概率（%）',
                sortable : true
            }, {
                field: 'otherSpuProbal',
                title: '本购SPU后购其他SPU概率（%）',
                sortable : true
            }]
    };
    $( "#importSpu" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

function searchImportSpu() {
    if($("#spuName").val() === '') {
        $MB.n_warning("请选择购买次序！");
        return false;
    }
    if($("#purchOrder1").val() === '') {
        $MB.n_warning("请选择SPU名称！");
        return false;
    }
    findImportSpu();
}
// 重置筛选条件
function resetImportSpu() {
    $("#spuName").html('').append("<option value=''>请选择</option>");
    $("#purchOrder1").find("option:selected").removeAttr("selected");
}

$("#spuProductName1").click(
    function () {
        $("#ztreeContent1").toggle();
    }
);

$("#spuProductName2").click(
    function () {
        $("#ztreeContent2").toggle();
    }
);
/**
 * 留存率随购买次数的变化图
 */
function retentionInPurchaseTimes() {
    var id = $("#spuProductId1").val();
    var type = $("#spuProductType1").val();
    var period = $("#period").val();
    chart_retention.showLoading({
        text : '正在加载数据'
    });
    $.get("/insight/retentionInPurchaseTimes", {id: id, type: type, period: period}, function (r) {
        var data = r.data;
        data.fdata = [];
        var option = getOptionWithFit(data, "再次购买spu概率（%）", "再次购买spu概率");
        option.grid = {left: '15%', right:'15%'};
        chart_retention.hideLoading();
        chart_retention.setOption(option);
    });
}

/**
 * 留存率变化率随购买次数变化
 */
function retentionChangeRateInPurchaseTimes() {
    var id = $("#spuProductId1").val();
    var type = $("#spuProductType1").val();
    var period = $("#period").val();
    $.get("/insight/retentionChangeRateInPurchaseTimes", {id: id, type: type, period: period}, function (r) {
        var data = r.data;
        data.fdata = [];
        var option = getOptionWithFit(data, "再次购买spu概率的变化率（%）", "再次购买spu概率的变化率");
        option.grid = {left: '18%', right:'15%'};
        chart_retention_change.hideLoading();
        chart_retention_change.setOption(option);
    });
}

/**
 * 件单价随购买次数变化
 */
function unitPriceInPurchaseTimes() {
    var id = $("#spuProductId1").val();
    var type = $("#spuProductType1").val();
    var period = $("#period").val();
    $.get("/insight/unitPriceInPurchaseTimes", {id: id, type: type, period: period}, function (r) {
        var option = getOption(r.data, "平均件单价", "平均件单价");
        var chart = echarts.init(document.getElementById("chart2"), 'macarons');
        chart.setOption(option);
    });
}

/**
 * 连带率随购买次数变化
 */
function joinRateInPurchaseTimes() {
    var id = $("#spuProductId1").val();
    var type = $("#spuProductType1").val();
    var period = $("#period").val();

    $.get("/insight/joinRateInPurchaseTimes", {id: id, type: type, period: period}, function (r) {
        var option = getOption(r.data, "平均连带率", "平均连带率");
        var chart = echarts.init(document.getElementById("chart2"), 'macarons');
        chart.setOption(option);
    });
}

/**
 * 品类种数随购买次数变化
 */
function categoryInPurchaseTimes() {
    var id = $("#spuProductId1").val();
    var type = $("#spuProductType1").val();
    var period = $("#period").val();
    $.get("/insight/categoryInPurchaseTimes", {id: id, type: type, period: period}, function (r) {
        var option = getOption(r.data, "平均购买spu种类数", "平均购买spu种类数");
        var chart = echarts.init(document.getElementById("chart2"), 'macarons');
        chart.setOption(option);
    });
}

/**
 * 时间间隔随购买次数变化
 */
function periodInPurchaseTimes() {
    var id = $("#spuProductId1").val();
    var type = $("#spuProductType1").val();
    var period = $("#period").val();
    $.get("/insight/periodInPurchaseTimes", {id: id, type: type, period: period}, function (r) {
        var option = getOption(r.data, "平均购买间隔", "平均购买间隔");
        var chart = echarts.init(document.getElementById("chart2"), 'macarons');
        chart.setOption(option);
    });
}

function getOption(data, name, titleName) {
    return {
        tooltip: {
            trigger: 'axis',
            padding: [15, 20],
            axisPointer: {
                type: 'shadow'
            }
        },
        xAxis: {
            name: '购买次数',
            type: 'category',
            data: data.xdata,
            splitLine:{show: false},
            splitArea : {show : false}
        },
        yAxis: {
            name: name,
            type: 'value',
            splitLine:{show: false},
            splitArea : {show : false}
        },
        series: [{
            data: data.ydata,
            type: 'line'
        }],
        grid: {right:'15%'},
        title: {
            text: titleName + '随购买次数变化',
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
        }
    }
}

// 带拟合值
function getOptionWithFit(data, name, titleName) {
    return  {
        legend: {
            data: ['实际值', '拟合值'],
            align: 'right',
            right: 10,
            selected: {'实际值': true, '拟合值': false}
        },
        tooltip: {
            trigger: 'axis',
            padding: [15, 20],
            axisPointer: {
                type: 'shadow'
            }
        },
        xAxis: {
            name: '购买次数',
            type: 'category',
            data: data.xdata,
            splitLine:{show: false},
            splitArea : {show : false}
        },
        yAxis: {
            name: name,
            type: 'value',
            splitLine:{show: false},
            splitArea : {show : false}
        },
        series: [{
            name: '拟合值',
            data: data.fdata,
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
        }],
        grid: {right:'15%'},
        title: {
            text: titleName + '随购买次数变化',
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
        }
    }
}

function searchRetention() {
    retention_fit = false;
    retention_change_fit = false;
    if($("#spuProductId1").val() === '' || $("#period").val() === '') {
        $MB.n_warning("请选择商品/SPU，查询时间周期。");
        return false;
    }
    let kpi1 = $("#kpi1").val();
    let kpi2 = $("#kpi2").val();
    if(kpi1 === "1") {
        $("#chart1").show();
        $("#chart11").hide();
        setTimeout(function() {
            retentionInPurchaseTimes();
        },500);
    }else {
        $("#chart11").show();
        $("#chart1").hide();
        setTimeout(function() {
            retentionChangeRateInPurchaseTimes();
        },500);
    }
    if(kpi2 === "1") {
        unitPriceInPurchaseTimes();
    }else if(kpi2 === "2"){
        periodInPurchaseTimes();
    }else if(kpi2 === "3"){
        joinRateInPurchaseTimes();
    }else if(kpi2 === "4"){
        categoryInPurchaseTimes();
    }
}

// 重置留存率的条件
function resetRetention() {
    retention_fit = false;
    retention_change_fit = false;
    $("#spuProductName1").val("");
    $("#spuProductId1").val("");
    $("#spuProductType1").val("");
    $("#period").find("option:selected").removeAttr("selected");
}

// 获取下一个product转化概率的大小

function searchConvertRate() {
    let spuId = $("#convertSpu").val();
    let purchOrder = $("#purchOrder").val();
    // 获取柱状图
    getSpuRelation(spuId, purchOrder);
}

function getGrowthUserTable(spuId, purchOrder, ebpProductId, nextProductId) {
    let settings = {
        url: "/insight/getGrowthUser",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,  //页码
                param: {spuId: spuId, purchOrder:purchOrder, ebpProductId:ebpProductId, nextEbpProductId:nextProductId}
            };
        },
        columns: [{
            field: 'USER_ID',
            title: 'ID'
        }, {
            field: 'SPU_NAME',
            title: 'SPU'
        },{
            field: 'EBP_PRODUCT_NAME',
            title: '上次购买商品'
        }, {
            field: 'TO_NOW_DAYS',
            title: '间隔'
        }, {
            field: 'RN',
            title: '次序'
        }, {
            field: 'ACTIVE_LEVEL',
            title: '活跃度',
            formatter: function (value, row, index) {
                var res = "";
                switch (value) {
                    case "UAC_01":
                        res = "高度活跃";
                        break;
                    case "UAC_02":
                        res = "中度活跃";
                        break;
                    case "UAC_03":
                        res = "流失预警";
                        break;
                    case "UAC_04":
                        res = "弱流失";
                        break;
                    case "UAC_05":
                        res = "强流失";
                        break;
                    case "UAC_06":
                        res = "沉睡";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        }, {
            field: 'GROWTH_NODE_DATE',
            title: '成长节点'
        }]
    };
    $("#growthUserTable").bootstrapTable('destroy').bootstrapTable(settings);
}

function getConvertRateChart(spuId, purchOrder, ebpProductId, nextProductId) {
    var chart = echarts.init(document.getElementById("convert_chart"), 'macarons');
    chart.showLoading({
        text : '加载数据中...'
    });
    $.get("/insight/getConvertRateChart", {spuId:spuId, purchOrder:purchOrder, ebpProductId:ebpProductId, nextEbpProductId:nextProductId}, function (r) {
        var data = r.data;
        let option = {
            legend: {
                data: ['实际值', '拟合值'],
                align: 'right',
                right: 10,
                selected: {'实际值': true, '拟合值': false}
            },
            tooltip: {
                trigger: 'axis',
                padding: [15, 20],
                axisPointer: {
                    type: 'shadow'
                }
            },
            xAxis: {
                name: '购买间隔（天）',
                type: 'category',
                data: data.xdata,
                splitLine:{show: false},
                splitArea : {show : false}
            },
            yAxis: {
                name: "购买概率",
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
            }],
            grid: {left: '8%',right:'19%'},
            title: {
                text: '购买间隔随购买概率变化图',
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
            }
        };
        chart.hideLoading();
        chart.setOption(option);
    });
}
// 获取成长节点的表格
function getGrowthPoint(spuId, purchOrder, ebpProductId, nextProductId) {
    $.get("/insight/getUserGrowthPath", {spuId:spuId, purchOrder:purchOrder, ebpProductId:ebpProductId, nextEbpProductId:nextProductId}, function (r) {
        let code = "";
        r.data.forEach((v,k)=>{
            code += "<tr><td>"+v['ACTIVE_TYPE']+"</td><td>"+v['ACTIVE_DUAL']+"</td><td>"+v['PROB']+"</td><td>"+v['BEGIN']+"</td><td>"+v['END']+"</td></tr>";
        });
        $("#growthTableData").html('').append(code);
    });
}

function getProductOption(xdata, ydata) {
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
            right: '19%',
            containLabel: true
        },
        xAxis : [
            {
                name: '下次转化的商品',
                type : 'category',
                data : xdata,
                axisTick: {
                    alignWithLabel: true
                }
            }
        ],
        yAxis : [
            {
                name: '转化概率（%）',
                type : 'value',
                splitArea: {show: false},
                splitLine: {show: false}
            },

        ],
        series : [
            {
                type:'bar',
                barWidth: '60%',
                data:ydata
            }
        ],
        title: {
            text: '下次转化商品图',
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
        }
    };
    return option;
}

function getSpuChartOption(data) {
    let option = {
        color: ['#CD2626'],
        title: {
            text: '购买间隔随购买概率变化图',
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
                name: "spu/商品",
                type : 'category',
                data : data.xdata1,
                axisTick: {
                    alignWithLabel: true
                }
            }
        ],
        yAxis : [
            {
                name: '用户数',
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
                data:data.ydataActual
            }
        ]
    };

    return option;
}

// 初始化的数据
// 关系柱状图1 + 关系柱状图2
let spu_relation_chart_flag = false;
let ebpProductIdArray;
let nextProductIdArray;
function getSpuRelation(spuId, purchOrder) {
    var chart1 = echarts.init(document.getElementById("chart_relation_1"), 'macarons');
    chart1.showLoading({
        text : '加载数据中...'
    });
    var chart2 = echarts.init(document.getElementById("chart_relation_2"), 'macarons');
    chart2.showLoading({
        text : '加载数据中...'
    });
    $.get("/insight/getSpuRelation", {spuId:spuId, purchOrder:purchOrder}, function (r) {
        ebpProductIdArray = r.data.productId;
        nextProductIdArray = r.data.nextProductId;
        var option1 = getSpuChartOption(r.data);
        chart1.hideLoading();
        chart1.setOption(option1);

        if(!spu_relation_chart_flag) {
            spu_relation_chart_flag = true;
            chart1.on('click', function(params) {
                if(params.dataIndex !== 0 && params.name !== '其他') {
                    let spuId = $("#convertSpu").val();
                    let purchOrder = $("#purchOrder").val();
                    let ebpProductId = ebpProductIdArray[params.dataIndex];
                    getProductRelation(ebpProductId, spuId, purchOrder);
                }
            });
        }
        var option2 = getProductOption(r.data.xdata2, r.data.ydata2);
        chart2.hideLoading();
        chart2.setOption(option2);

        let ebpProductId = ebpProductIdArray[1];
        let nextProductId = (nextProductIdArray === undefined ? "":nextProductIdArray[0]);
        // 获取表格
        getGrowthPoint(spuId, purchOrder, ebpProductId, nextProductId);
        // 获取转化概率
        getConvertRateChart(spuId, purchOrder, ebpProductId, nextProductId);
        // 获取用户表格
        getGrowthUserTable(spuId, purchOrder, ebpProductId, nextProductId);
    });
}

// 下一次转化商品
let product_relation_chart_flag = false;
function getProductRelation(ebpProductId, spuId, purchOrder) {
    var chart = echarts.init(document.getElementById("chart_relation_2"), 'macarons');
    chart.showLoading({
        text : '加载数据中...'
    });
    $.get("/insight/getProductConvertRate", {spuId:spuId, purchOrder:purchOrder, productId:ebpProductId}, function (r) {
        nextProductIdArray = r.data['nextProductId'];

        if(!product_relation_chart_flag) {
            chart.on('click', function(params) {
                product_relation_chart_flag = true;
                if(params.name !== '其他') {
                    let nextProductId = nextProductIdArray[params.dataIndex];
                    let spuId = $("#convertSpu").val();
                    let purchOrder = $("#purchOrder").val();
                    // 获取表格
                    getGrowthPoint(spuId, purchOrder, ebpProductId, nextProductId);
                    // 获取转化概率
                    getConvertRateChart(spuId, purchOrder, ebpProductId, nextProductId);
                    // 获取用户表格
                    getGrowthUserTable(spuId, purchOrder, ebpProductId, nextProductId);
                }
            });
        }
        var option = getProductOption(r.data.xdata, r.data.ydata);
        chart.hideLoading();
        chart.setOption(option);
        let nextProductId = nextProductIdArray === undefined ? "":nextProductIdArray[0];
        // 获取表格
        getGrowthPoint(spuId, purchOrder, ebpProductId, nextProductId);
        // 获取转化概率
        getConvertRateChart(spuId, purchOrder, ebpProductId, nextProductId);
        // 获取用户表格
        getGrowthUserTable(spuId, purchOrder, ebpProductId, nextProductId);
    });
}

function getSeries(data) {
    var getSeries = [];
    data.forEach((v, k)=>{
        getSeries.push(
            {
                name: v['name'],
                type: 'graph',
                layout: 'force',
                force: {
                    repulsion: 300
                },
                data: v['data'],
                links: v['links'],
                categories: v['categories'],
                focusNodeAdjacency: true,
                roam: true,
                label: {
                    normal: {
                        show: true,
                        position: 'top',
                    }
                },
                lineStyle: {
                    normal: {
                        color: 'source',
                        curveness: 0,
                        type: "solid"
                    }
                }
            }
        );
    });

    return getSeries;
}

// 获取下一个product转化概率的大小
function resetConvertRate() {
    $("#spuProductName2").val("");
    $("#spuProductId2").val("");
    $("#spuProductType2").val("");
    $("#purchOrder").find("option:selected").removeAttr("selected");
}

// 关系柱状图
function relationChart(data) {
    let categories = data.categories;
    let option = {
        tooltip: {
            formatter: function(params) {
                return params.name + (params.value ? ' : ' + params.value : '')
            }
        },
        animationDurationUpdate: 1500,
        animationEasingUpdate: "quinticInOut",

        toolbox: {
            feature: {
                restore: {}
            }
        },
        legend: {
            show: true,
            data: categories,
            type: 'scroll',
            orient: 'vertical',
            right: 10,
            top: 20,
            bottom: 20,
            selected: legendSeleted(categories)
        },
        series: [{
            type: "graph",
            layout: "force",
            roam: true,
            hoverAnimation: true,
            focusNodeAdjacency: true,
            draggable: true,
            symbolSize: 33,
            force: {
                repulsion: 200,
                edgeLength: 100
            },
            itemStyle: {
                normal: {
                    borderColor: "#fff",
                    borderWidth: 1,
                    shadowBlur: 10,
                    shadowColor: "rgba(0, 0, 0, 0.3)"
                }
            },
            lineStyle: {
                width: 0.5,
                curveness: 0.3,
                opacity: 0.8
            },
            label: {
                emphasis: {
                    position: 'right',
                    show: true
                }
            },

            data: data.data,
            links: data.links,
            categories: categories
        }]
    };
    return option;
}

function legendSeleted(data) {
    var res = {};
    data.forEach((v, k)=>{
        var name = v['name'];
        if(k < 10) {
            res[name] = true;
        }else {
            res[name] = false;
        }
    });
    return res;
}

$("#purchOrder1").change(function () {
    getSpuName();
});

getSpuName();
function getSpuName() {
    let purchOrder = $("#purchOrder1").val();
    $.get("/insight/findSpuByPurchOrder", {purchOrder: purchOrder}, function (r) {
        let code = "";
        r.data.forEach((v,k)=>{
            code += "<option value='"+v['ID']+"'>"+v['NAME']+"</option>";
        });
        $("#spuName").html('').append(code);

        $('#spuName').select2({
            placeholder: '请选择'
        });

        findImportSpu();
    });
}

$("#dateRange").change(function () {
    $("#dateRange1").val($(this).find("option:selected").text());
    $("#dateRange2").val($(this).find("option:selected").text());
});

// 获取路径上的spu
getPathSpu();
function getPathSpu() {
    $.get("/insight/getPathSpu", {}, function (r) {
        let code = "";
        r.data.forEach((v,k)=> {
            code += "<option value='"+v['SPU_WID']+"'>" + v['SPU_NAME'] + "</option>";
        });
        $("#convertSpu").html('').append(code);
        $("#convertSpu").select2({
            placeholder: '请选择'
        });
        // 获取spu下的购买次序
        getPurchOrder($("#convertSpu").val());
    });
}

$("#convertSpu").change(function () {
    var spuId = $(this).val();
    if(spuId !== '') {
        getPurchOrder(spuId);
    }
});

// 获取路径上的购买次序
var search_flag = false;
function getPurchOrder(spuId) {
    $.get("/insight/getPathPurchOrder", {spuId: spuId}, function (r) {
        let code = "";
        r.data.forEach((v,k)=> {
            code += "<option value='"+v+"'>" + v + "</option>";
        });
        $("#purchOrder").html('').append(code);
        $("#purchOrder").select2();

        if(!search_flag) {
            search_flag = true;
            searchConvertRate();
        }
    });
}