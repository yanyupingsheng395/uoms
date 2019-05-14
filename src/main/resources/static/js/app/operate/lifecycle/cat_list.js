// spu id
var selectId='';
var currentSpu = "";
var stats = null;

$(function () {
    init_date("startDate_1", "yyyy-mm", 1,2,1);
    init_date("startDate_2", "yyyy-mm", 1,2,1);
    periodTypeOption();
    initTableData();
    var flag = false;
    // 表格点击事件
    $('#catListTable').on('click-row.bs.table', function (e,row,$element)
    {
        selectId=row.spuWid;
        $('.changeColor').removeClass('changeColor');
        $($element).addClass('changeColor');
        currentSpu = row.spuName;
        dataInit();
        tab3DataInit();
    });

    // 初始化数据
    function dataInit() {
        if(!flag) {
            flag = true;
            $(".initTab").attr("style", "display:none;");  //隐藏提示
            $("#tabContent1").addClass("chartpanel").removeClass("chart_none_panel");
            $("#tabContent2").addClass("chartpanel").removeClass("chart_none_panel");
            $("#tabContent3").addClass("chartpanel").removeClass("chart_none_panel");
        }

        var tab = $("#tabs").find(".active").children().attr("href");
        if(tab == "#tab_kpis") {
            lightyear.loading('show');
            gmvChart();
            tradeUserChart();
            avgCsPriceChart();
            lightyear.loading('hide');
        }else if(tab == "#tab_lifecycle") {
            tab2Init();
        }else if(tab == "#tab_user_target"){
            tab3DataInit();
        }else if(tab == "#tab_contemporaneous"){
            getData(1);
            getData1(1);
        }
    }

    $('#catListTable').on('post-body.bs.table', function (e, settings) {
        if(null!=selectId&&selectId!='')
        {
            $.each(settings,function (i,v) {
                if(v.CATE_WID === parseInt(selectId)){
                    $(e.target).find('tbody tr').eq(i).addClass('changeColor');
                }
            });
        }
    });

    //给查询按钮绑定事件
    $("#query").on("click",function () {
        initTableData();
    });
});

// 切换tab时初始化数据
$("a[data-toggle='tab']").on('shown.bs.tab', function (e) {

    // 获取已激活的标签页的名称
    var activeTab = $(e.target).attr("href");
    if(selectId == "") {
        $(".initTab").attr("style", "display:block;");
    }else
    {
        $(".initTab").attr("style", "display:none;");
        if(activeTab=='#tab_kpis')
        {
            // 初始化表格数据
            $("#tabContent1").attr("class", "chartpanel");
            $("#tabContent1").removeClass("chart_none_panel");
        }else if(activeTab == "#tab_lifecycle"){
            tab2Init();
            $("#tabContent2").attr("class", "chartpanel");
            $("#tabContent2").removeClass("chart_none_panel");
        }else if(activeTab == "#tab_user_target"){
            tab3DataInit();
            $("#tabContent3").attr("class", "chartpanel");
            $("#tabContent3").removeClass("chart_none_panel");
        }else{
            getData(1);
            getData1(1);
            $("#tabContent4").attr("class", "chartpanel");
            $("#tabContent4").removeClass("chart_none_panel");
        }
    }
});

init_date("startDate_1", "yyyy-mm", 1,2,1);
init_date("startDate_2", "yyyy-mm", 1,2,1);

function periodTypeOption() {
    var option = "";
    $.ajax({
        url: '/lifecycle/getSpuFilterList',
        async: false,
        success: function (r) {
            $.each(r.data, function(k, v) {
                $.each(v, function (k1, v1) {
                    option += "<option value='"+k1+"'>"+v1+"</option>";
                });
            });
            $("#conditions").html("").html(option);
        }
    });
}


function initTableData() {
    stats = new Object();
    var startDt =  $("#startDt").val();
    var endDt = $("#endDt").val();
    var source = $("#source").find("option:selected").val();
    var filterType = $("#conditions").find("option:selected").val();
    $.ajax({
        url:"/lifecycle/getSpuList",
        data:{startDt:startDt,endDt:endDt, source:source, filterType:filterType},
        async: true,
        success: function (r) {
            stats = r.data.stats;
            var columns = new Array();
            $.each(r.data.columns, function (k, v) {
                var obj = new Object();
                $.each(v, function (k1, v1) {
                    obj.field = k1;
                    obj.title = v1;
                    obj.formatter = function (value, row, index) {
                        if(value == null) {
                            return "";
                        }else {
                            return value;
                        }
                    };
                    if(k1 == "spuWid") {
                        obj.visible = false;
                    }
                });
                columns.push(obj);
            });
            columns.push({
                field: "operate",
                title: "操作",
                formatter: function (value, row, index) {
                    var val = null;
                    switch (filterType) {
                        case "gmv":
                            val = "gmvCont";
                            break;
                        case "user":
                            val = "userCont";
                            break;
                        case "pocount":
                            val = "poCount";
                            break;
                        case "joinrate":
                            val = "joinrate";
                            break;
                        case "sprice":
                            val = "sprice";
                            break;
                        case "profit":
                            val = "profit";
                            break;
                    }
                    return "<button style='margin-right: 5px;' class='btn btn-secondary btn-sm' onclick='gearBtnClick("+row[""+val+""]+")'><i class='mdi mdi-chemical-weapon'></i>档位</button>";
                }
            });
            var option = {
                columns: columns,
                data: r.data.data,
                pageNumber : 1,
                pagination : true,
                sidePagination : 'client',
                pageSize: 5,
                pageList: [5,10, 25, 50, 100],
                search: false
            };
            $('#catListTable').bootstrapTable('destroy').bootstrapTable(option);
        }
    });
}

var data2 = null;
var yName = null;
function gearBtnClick(val) {
    data2 = val;
    yName = $("#conditions").find("option:selected").text();
    $("#gearsModal").modal('show');
}
$('#gearsModal').on('shown.bs.modal', function (event) {
    var data1 = new Array();
    data1.push(stats.min);
    data1.push(stats.q1);
    data1.push(stats.q2);
    data1.push(stats.q3);
    data1.push(stats.max);
    var opt = gearsOption(yName, data1, data2);
    var chart = echarts.init(document.getElementById('gearChart'), 'macarons');
    chart.setOption(opt);
});

function view_matrix() {
    //弹出面板
    $('#matrix_modal').modal('show');
    refreshOpUserTable();
}

// 获取modal的table data
function refreshOpUserTable() {
    $('#opuserListTable').bootstrapTable('destroy').bootstrapTable({
        data: getOpuserDdata(),
        pagination: true,                   //是否显示分页（*）
        sidePagination: "client",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber: 1,                      //初始化加载第一页，默认第一页,并记录
        pageSize: 5,                     //每页的记录行数（*）
        pageList: [5,10, 25, 50, 100],        //可供选择的每页的行数（*）
        columns: opuser_columns
    });
}

var opuser_columns=[[
    {
        field: 'user_id',
        title: '用户ID',
        align:"center",
        valign:"middle",
        rowspan: 2
    },
    {
        title: "当前目标",
        align:"center",
        colspan: 3
    },
    {
        title: "当前状态",
        align:"center",
        colspan: 3
    }
],[{
    field: 'buy_date',
    title: '购买时间'
},  {
    field: 'spu_name',
    title: 'SPU'
}, {
    field: 'buy_time',
    title: '已购买次数'
}, {
    field: 'order_price',
    title: '订单价（元）'
}, {
    field: 'user_value',
    title: '用户价值'
}, {
    field: "active_level",
    title: "活跃度"
}]];

///////////////
// 初始化时间插件
init_date_begin("startDt", "endDt", "yyyy-mm-dd",0, 2, 0);
init_date_end("startDt", "endDt", "yyyy-mm-dd", 0, 2, 0);

function gearsOption(yName, data1, data2) {
    var option = {
        tooltip: {
            trigger: 'item',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: [{
            height:'50%',
            left: '10%',
            right: '20%',
        }],
        yAxis: [{
            type: 'category',
            data: [''],
            nameTextStyle: {
                color: '#3259B8',
                fontSize: 16,
            },

            axisTick:{
                show:false,
            }
        }],
        xAxis: [{
            name: yName,
            type: 'value',
            nameTextStyle: {
                color: '#3259B8',
                fontSize: 14,
            },
            axisTick:{
                show:false,
            }

        }],
        series: [{
            name: 'XXX',
            type: 'boxplot',
            data:  [data1],
            itemStyle: {
                normal:{
                    borderColor: {
                        type: 'linear',
                        x: 1,
                        y: 0,
                        x2: 0,
                        y2: 0,
                        colorStops: [{
                            offset: 0,
                            color: '#3EACE5' // 0% 处的颜色
                        }, {
                            offset: 1,
                            color: '#956FD4' // 100% 处的颜色
                        }],
                        globalCoord: false // 缺省为 false
                    },
                    borderWidth:2,
                    color: {
                        type: 'linear',
                        x: 1,
                        y: 0,
                        x2: 0,
                        y2: 0,
                        colorStops: [{
                            offset: 0,
                            color: 'rgba(62,172,299,0.7)'  // 0% 处的颜色
                        }, {
                            offset: 1,
                            color:'rgba(149,111,212,0.7)'  // 100% 处的颜色
                        }],
                        globalCoord: false // 缺省为 false
                    },
                }
            },
            tooltip: {
                formatter: function(param) {
                    return [

                        '最大值: ' + param.data[5],
                        '第三个中位数: ' + param.data[4],
                        '中位数: ' + param.data[3],
                        '第一个中位数: ' + param.data[2],
                        '最小值: ' + param.data[1]
                    ].join('<br/>')
                }
            }
        },{
            name: '实际值',
            type: 'scatter',
            data: [data2],
            symbolSize: 11,
            tooltip: {
                formatter: function(param) {
                    return [
                        '实际值: ' + param.data
                    ].join('<br/>')
                }
            }
        }]
    };
    return option;
}

function tab3DataInit() {
    var settings = {
        url: "/spucycle/list",
        method: 'post',
        cache: false,
        pagination: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit )+ 1  //页码
            };
        },
        columns: [{
            field: 'userId',
            title: '用户ID'
        },{
            field: 'spuName',
            title: 'SPU/品类',
            formatter: function (value, row, index) {
                return currentSpu;
            }
        },{
            field: 'cycleStage',
            title: '周期阶段'
        },{
            field: 'rePurch',
            title: '复购',
            formatter: function (value, row, index) {
                if(value == "Y") {
                    return "<a style='color: #000000;border-bottom: 1px solid' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-original-title=\"达成目标的理想时间：2019-06-30<br/>达成目标的一般时间：2019-05-30<br/>达成目标的最后时间：2019-08-30\">是</a>";
                }else {
                    return "<a style='color: #000000;border-bottom: 1px solid' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-original-title=\"达成目标的理想时间：2019-06-30<br/>达成目标的一般时间：2019-05-30<br/>达成目标的最后时间：2019-08-30\">否</a>";
                }
            }
        },{
            field: 'avgPiecePrice',
            title: '平均件单价（元）',
            formatter: function(value, row, index) {
                if(value != null) {
                    return "<a style='color: #000000;border-bottom: 1px solid' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-original-title=\"达成目标的理想时间：2019-06-30<br/>达成目标的一般时间：2019-05-30<br/>达成目标的最后时间：2019-08-30\">"+value+"</a>";
                }
            }
        },{
            field: 'purchInterval',
            title: '平均购买间隔（天）'
        },{
            field: 'jointRate',
            title: '平均连带率',
            formatter: function(value, row, index) {
                if(value != null) {
                    return "<a style='color: #000000;border-bottom: 1px solid' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-original-title=\"达成目标的理想时间：2019-06-30<br/>达成目标的一般时间：2019-05-30<br/>达成目标的最后时间：2019-08-30\">"+value+"</a>";
                }
            }
        },{
            field: 'numPurchType',
            title: '平均购买品类种数'
        },{
            field: 'accPurchTimes',
            title: '累计购买次数'
        }],
        onLoadSuccess: function(data){
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $('#dataTable').bootstrapTable('destroy').bootstrapTable(settings);
}
