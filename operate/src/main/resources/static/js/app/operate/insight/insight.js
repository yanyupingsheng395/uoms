$(function () {
    findUserCntList();
    findSpuValueList();
    findImportSpu();
});

// 按时间范围查询
function searchInsight() {
    var dateRange = $("#dateRange").val();
    findUserCntList();
    $("#sankeyFrame").attr("src", "/sankey?dateRange=" + dateRange);
    $("#breakBtn").attr("href", "/sankey?dateRange=" + dateRange);
}

function resetInsight() {
    $("#dateRange").find("option:selected").removeAttr("selected");
}

function findUserCntList() {
    var settings = {
        columns: [
            {
                field: 'purchType',
                title: '购买次序',
                align: 'center'
            }, {
                field: 'purch1',
                title: '1购',
                align: 'center'
            }, {
                field: 'purch2',
                title: '2购',
                align: 'center'
            }, {
                field: 'purch3',
                title: '3购',
                align: 'center'
            }, {
                field: 'purch4',
                title: '4购'
            }, {
                field: 'purch5',
                title: '5购',
                align: 'center'
            }, {
                field: 'purch6',
                title: '6购',
                align: 'center'
            }, {
                field: 'purch7',
                title: '7购',
                align: 'center'
            }, {
                field: 'purch8',
                title: '8购',
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
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                sort: params.sort,
                order: params.order
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
                title: '旅程价值',
                align:"center",
                valign:"middle",
                colspan: 4
            }, {
                field: '',
                title: '购买次序',
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
                    title: '1购'
                }, {
                    field: 'purch2SpuName',
                    title: '2购'
                }, {
                    field: 'purch3SpuName',
                    title: '3购'
                }, {
                    field: 'purch4SpuName',
                    title: '4购'
                }, {
                    field: 'purch5SpuName',
                    title: '5购'
                }, {
                    field: 'purch6SpuName',
                    title: '6购'
                }, {
                    field: 'purch7SpuName',
                    title: '7购'
                }, {
                    field: 'purch8SpuName',
                    title: '8购'
                }
            ]
        ],onLoadSuccess: function() {
            var tableId = document.getElementById('spuValueTable');
            tableId.rows[tableId.rows.length - 1].setAttribute("style", "background-color:#E7EAEC;");
        }
    };
    $MB.initTable('spuValueTable', settings);
}

// 重要spu
function findImportSpu() {
    var settings = {
        url: '/insight/findImportSpuList',
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
                    spuName: $("input[name='spuName']").val(),
                    purchOrder: $("select[name='purchOrder']").val()
                }
            };
        },
        columns: [
            {
                field: 'spuName',
                title: 'SPU名称'
            }, {
                field: 'contributeRate',
                title: '本次购买的用户贡献率（%）'
            },{
                field: 'nextPurchProbal',
                title: '本购SPU后再购概率（%）'
            }, {
                field: 'sameSpuProbal',
                title: '本购SPU后再购同SPU概率（%）'
            }, {
                field: 'otherSpuProbal',
                title: '本购SPU后购其他SPU概率（%）'
            }]
    };
    $( "#importSpu" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

function searchImportSpu() {
    findImportSpu();
}

function resetImportSpu() {
    $("input[name='spuName']").val("");
    $("select[name='purchOrder']").find("option:selected").removeAttr("selected");
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

var retention_fit = false;
var retention_change_fit = false;
/**
 * 留存率随购买次数的变化图
 */
let retention_fit_flag;
let retention_change_fit_flag;
function retentionInPurchaseTimes() {
    var id = $("#spuProductId1").val();
    var type = $("#spuProductType1").val();
    var period = $("#period").val();
    $.get("/insight/retentionInPurchaseTimes", {id: id, type: type, period: period}, function (r) {
        var data = r.data;
        data.fdata = [];
        var option = getOptionWithFit(data, "留存率（%）", "留存率");
        var chart = echarts.init(document.getElementById("chart1"), 'macarons');
        chart.setOption(option);
        if(retention_fit_flag == undefined) {
            chart.on('legendselectchanged', function(obj) {
                retention_fit_flag = true;
                var legend = obj.name;
                if(legend === '拟合值' && !retention_fit) {
                    chart.showLoading({
                        text : '正在加载数据'
                    });
                    $.get("/insight/getRetentionFitData", {id: id, type: type, period: period}, function (r) {
                        data.fdata = r.data;
                        option = getOptionWithFit(data, "留存率（%）", "留存率");
                        option.legend.selected = {
                            '实际值':true,
                            '拟合值':true
                        };
                        chart.setOption(option);
                        chart.hideLoading();
                        retention_fit = true;
                    });
                }
            });
        }
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
        var option = getOptionWithFit(data, "留存率变化率（%）", "留存率变化率");
        var chart = echarts.init(document.getElementById("chart2"), 'macarons');
        chart.setOption(option);
        if(retention_change_fit_flag === undefined) {
            chart.on('legendselectchanged', function(obj) {
                retention_change_fit_flag = true;
                var legend = obj.name;
                if(legend === '拟合值' && !retention_change_fit) {
                    chart.showLoading({
                        text : '正在加载数据'
                    });
                    $.get("/insight/getRetentionChangeFitData", {id: id, type: type, period: period}, function (r) {
                        data.fdata = r.data;
                        option = getOptionWithFit(data, "留存率变化率（%）", "留存率变化率");
                        option.legend.selected = {
                            '实际值':true,
                            '拟合值':true
                        };
                        chart.setOption(option);
                        chart.hideLoading();
                        retention_change_fit = true;
                    });
                }
            });
        }
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
        var option = getOption(r.data, "件单价", "件单价");
        var chart = echarts.init(document.getElementById("chart3"), 'macarons');
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
        var option = getOption(r.data, "连带率", "连带率");
        var chart = echarts.init(document.getElementById("chart4"), 'macarons');
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
        var option = getOption(r.data, "品类种数", "品类种数");
        var chart = echarts.init(document.getElementById("chart5"), 'macarons');
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
        var option = getOption(r.data, "时间间隔", "时间间隔");
        var chart = echarts.init(document.getElementById("chart6"), 'macarons');
        chart.setOption(option);
    });
}

function getOption(data, name, titleName) {
    return option = {
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
    return option = {
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
    $("#mask").hide();
    $("#charts").show();
    retentionInPurchaseTimes();
    unitPriceInPurchaseTimes();
    joinRateInPurchaseTimes();
    categoryInPurchaseTimes();
    periodInPurchaseTimes();
    retentionChangeRateInPurchaseTimes();
}

// 重置留存率的条件
function resetRetention() {
    retention_fit = false;
    retention_change_fit = false;
    $("#mask").show();
    $("#charts").hide();
    $("#spuProductName1").val("");
    $("#spuProductId1").val("");
    $("#spuProductType1").val("");
    $("#period").find("option:selected").removeAttr("selected");
}

// 获取下一个product转化概率的大小
function searchConvertRate() {
    $("#mask1").hide();
    $("#relationChartRow").show();
    var id = $("#spuProductId2").val();
    var type = $("#spuProductType2").val();
    var purchOrder =$("#purchOrder").val();

    if(id === '' || purchOrder === '') {
        $MB.n_warning("请选择查询条件！");
        return false;
    }
    var chart = echarts.init(document.getElementById("chart_relation"), 'macarons');
    chart.showLoading({
        text : '加载数据中...'
    });
    $.get("/insight/getSpuConvertRate", {id:id, type:type, purchOrder:purchOrder}, function (r) {
        var data = r.data;
        if(data == null) {
            chart.hideLoading();
            $("#mask1").show();
            $("#relationChartRow").hide();
            $MB.n_warning("查询到的数据为空！");
            return;
        }
        var option = relationChart(data);
        chart.hideLoading();
        chart.setOption(option);
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
    $("#mask1").show();
    $("#relationChartRow").hide();
    $("#spuProductName2").val("");
    $("#spuProductId2").val("");
    $("#spuProductType2").val("");
    $("#purchOrder").find("option:selected").removeAttr("selected");
}


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