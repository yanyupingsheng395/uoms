$( function () {
    findUserCntList();
    findSpuValueList();
    findImportSpu();
    kpiInit();
} );

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

// 5个指标随购买次数变化曲线

var setting = {
    async: {
        enable: true,
        url: getUrl
    },
    check: {
        enable: true,
        chkStyle: "radio",
        radioType: "all"
    },
    view: {
        dblClickExpand: false,
        showIcon: false
    },
    data: {
        simpleData: {
            enable: true
        }
    },
    callback: {
        onClick: onClick,
        onCheck: onCheck,
        beforeExpand: beforeExpand,
        onAsyncSuccess: onAsyncSuccess,
        onAsyncError: onAsyncError
    }
};

function onClick(e, treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj("spuTree");
    zTree.checkNode(treeNode, !treeNode.checked, null, true);
    return false;
}

function onCheck(e, treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj("spuTree"),
        nodes = zTree.getCheckedNodes(true),
        v = "";
    for (var i=0, l=nodes.length; i<l; i++) {
        v += nodes[i].name + ",";
    }
    if (v.length > 0 ) v = v.substring(0, v.length-1);
    var spuOrProductName = $("#spuOrProductName");
    spuOrProductName.attr("value", v);
    $("#id").val(nodes[0].id);
    $("#menuContent").toggle();
    if(treeNode.isParent) {
        $("#type").val("spu");
    }else {
        $("#type").val("product");
    }
}

function beforeExpand(treeId, treeNode) {
    if (!treeNode.isAjaxing) {
        ajaxGetNodes(treeNode, "refresh");
        return true;
    } else {
        $MB.n_warning("正在下载数据中，请稍后展开节点...");
        return false;
    }
}
var className = "dark";
function onAsyncSuccess(event, treeId, treeNode, msg) {
    if (!msg || msg.length == 0) {
        return;
    }
    var zTree = $.fn.zTree.getZTreeObj("spuTree"),
        totalCount = treeNode.count;
    if (treeNode.children.length < totalCount) {
        setTimeout(function() {ajaxGetNodes(treeNode);}, perTime);
    } else {
        treeNode.icon = "";
        zTree.updateNode(treeNode);
        zTree.selectNode(treeNode.children[0]);
        className = (className === "dark" ? "":"dark");
    }
}
function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
    var zTree = $.fn.zTree.getZTreeObj("spuTree");
    $MB.n_danger("异步获取数据出现异常。");
    treeNode.icon = "";
    zTree.updateNode(treeNode);
}
function ajaxGetNodes(treeNode, reloadType) {
    var zTree = $.fn.zTree.getZTreeObj("spuTree");
    if (reloadType == "refresh") {
        zTree.updateNode(treeNode);
    }
    zTree.reAsyncChildNodes(treeNode, reloadType, true);
}

// 指标随购买次数变化曲线初始化条件
function kpiInit() {
    var left = $("#spuOrProductName").offset().left - $(".row").offset().left - 14;
    var width = $("#spuOrProductName").width() + 5;
    $("#spuTree").attr("style", "margin-left:"+ left +"px;width:"+width+"px;");
    initZtree();
}

const promise = new Promise(function (resolve, reject) {
    $.get("/insight/getSpuTree", {}, function (r){
        if(r.code === 200) {
            resolve(r);
        } else {
            reject(r);
        }
    });
});

async function initZtree() {
    let result = await promise;
    $.fn.zTree.init($("#spuTree"), setting, result.data);
}

$("#spuOrProductName").click(
    function () {
        $("#menuContent").toggle();
    }
);

function getUrl(treeId, treeNode) {
    return "/insight/getProductTree?spuWid=" + treeNode.id;
}

/**
 * 留存率随购买次数的变化图
 */
function retentionInPurchaseTimes() {
    $.get("/insight/retentionInPurchaseTimes", {id: $("#id").val(), type: $("#type").val(), period: $("#period").val()}, function (r) {
        var option = getOption(r.data, "留存率");
        var chart = echarts.init(document.getElementById("chart1"), 'macarons');
        chart.setOption(option);
    });
}

/**
 * 件单价随购买次数变化
 */
function unitPriceInPurchaseTimes() {
    $.get("/insight/unitPriceInPurchaseTimes", {id: $("#id").val(), type: $("#type").val(), period: $("#period").val()}, function (r) {
        var option = getOption(r.data, "件单价");
        var chart = echarts.init(document.getElementById("chart3"), 'macarons');
        chart.setOption(option);
    });
}

/**
 * 连带率随购买次数变化
 */
function joinRateInPurchaseTimes() {
    $.get("/insight/joinRateInPurchaseTimes", {id: $("#id").val(), type: $("#type").val(), period: $("#period").val()}, function (r) {
        var option = getOption(r.data, "连带率");
        var chart = echarts.init(document.getElementById("chart4"), 'macarons');
        chart.setOption(option);
    });
}

/**
 * 品类种数随购买次数变化
 */
function categoryInPurchaseTimes() {
    $.get("/insight/categoryInPurchaseTimes", {id: $("#id").val(), type: $("#type").val(), period: $("#period").val()}, function (r) {
        var option = getOption(r.data, "品类种数");
        var chart = echarts.init(document.getElementById("chart5"), 'macarons');
        chart.setOption(option);
    });
}

/**
 * 时间间隔随购买次数变化
 */
function periodInPurchaseTimes() {
    $.get("/insight/periodInPurchaseTimes", {id: $("#id").val(), type: $("#type").val(), period: $("#period").val()}, function (r) {
        var option = getOption(r.data, "时间间隔");
        var chart = echarts.init(document.getElementById("chart6"), 'macarons');
        chart.setOption(option);
    });
}

/**
 * 留存率变化率随购买次数变化
 */
function retentionChangeRateInPurchaseTimes() {
    $.get("/insight/retentionChangeRateInPurchaseTimes", {id: $("#id").val(), type: $("#type").val(), period: $("#period").val()}, function (r) {
        var option = getOption(r.data, "留存率变化率");
        var chart = echarts.init(document.getElementById("chart2"), 'macarons');
        chart.setOption(option);
    });
}

function getOption(data, name) {
    return option = {
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
            text: name + '随购买次数变化',
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
    if($("#id").val() === '' || $("#period").val() === '') {
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
    $("#mask").show();
    $("#charts").hide();
    $("#spuOrProductName").val("");
    $("#id").val("");
    $("#type").val("");
    $("#period").find("option:selected").removeAttr("selected");
}