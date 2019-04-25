//表头信息
var columns = [];
$(function () {

    init_date("startDate_1", "yyyy-mm-dd", 0,2,0);
    init_date("endDate_1", "yyyy-mm-dd", 0,2,0);

    init_date("startDate_2", "yyyy-mm-dd", 0,2,0);
    init_date("endDate_2", "yyyy-mm-dd", 0,2,0);
    getData(1);
    getData1(1);
});

// 时间维度的click
$("#cohortbtngroup1>.cohortbtn").on('click',function () {
    //判断当前元素是否具有btn-primary 的class属性 如果已经有，则什么也不做
    if(!$(this).hasClass("btn-primary"))
    {
        //修改当前元素的样式
        $(this).removeClass("btn-default").addClass("btn-primary");
        $(this).siblings().removeClass("btn-primary").addClass("btn-default");
    }
    getData(indexTotal);
});


$("#cohortbtngroup2>.cohortbtn").on('click',function () {
    //判断当前元素是否具有btn-primary 的class属性 如果已经有，则什么也不做
    if(!$(this).hasClass("btn-primary"))
    {
        //修改当前元素的样式
        $(this).removeClass("btn-default").addClass("btn-primary");
        $(this).siblings().removeClass("btn-primary").addClass("btn-default");
    }
    getData1(indexTotal1);
});

var indexTotal = 1;
var indexTotal1 = 1;

function tabClick(idx) {
    indexTotal = idx;
    getData(idx);
}

function tab1Click(idx) {
    indexTotal1 = idx;
    getData1(idx);
}

// 获取表格数据
function getData(idx) {
    var url = "";
    if(idx == 1) {
        url = "/kpiMonitor/getRetainData";
    }else if(idx == 2) {
        url = "/kpiMonitor/getRetainCntData"
    }else if(idx == 3) {
        url = "/kpiMonitor/getLossData"
    }else if(idx == 4) {
        url = "/kpiMonitor/getLossCntData"
    }
    var $table = $('#dataTable' + idx);
    var start = $("#startDate_1").val();
    var end = $("#endDate_1").val();
    var periodType=$("#cohortbtngroup1>.btn-primary:first").attr("name");
    $.get(url, {periodType: periodType, start: start, end: end}, function(r) {
        console.log(r)
        var columns = r.data.columns;
        var data = r.data.data;

        var tmp = new Array();
        $.each(columns, function (k, v) {
            if(v == "month") {
                tmp.push({
                    field: v,
                    title: '月份',
                    width: '132px',
                    sortable: false
                });
            }else if(v == "week") {
                tmp.push({
                    field: v,
                    title: '周',
                    width: '132px',
                    sortable: false
                });
            }else if(v == "newuser") {
                tmp.push({
                    field: v,
                    title: '本月新增用户数',
                    width: '132px',
                    sortable: false
                });
            }else {
                tmp.push({
                    field: v,
                    title: v,
                    width: '132px',
                    sortable: false,
                    formatter: function (value, row, index) {
                        if(value == "-1") {
                            return "";
                        }else {
                            if(indexTotal == 1 || indexTotal == 3) {
                                return value + "%";
                            }else {
                                return value;
                            }
                        }
                    }
                });
            }
        });
        initBootstrapTable($table, tmp, data, r.data.total, periodType);
    });
}


function getData1(idx) {
    var url = "";
    if(idx == 1) {
        url = "/kpiMonitor/getUpriceData";
    }else if(idx == 2) {
        url = "/kpiMonitor/getPriceData"
    }else if(idx == 3) {
        url = "/kpiMonitor/getFreqData"
    }else if(idx == 4) {
        url = "/kpiMonitor/getSpriceData"
    }else if(idx == 5) {
        url = "/kpiMonitor/getJoinrateData"
    }
    var $table = $('#dataTable1' + idx);
    var start = $("#startDate_2").val();
    var end = $("#endDate_2").val();
    var periodType=$("#cohortbtngroup2>.btn-primary:first").attr("name");
    $.get(url, {periodType: periodType, start: start, end: end}, function(r) {
        var columns = r.data.columns;
        var data = r.data.data;

        var tmp = new Array();
        $.each(columns, function (k, v) {
            if(v == "month") {
                tmp.push({
                    field: v,
                    title: '月份',
                    width: '132px',
                    sortable: false
                });
            }else if(v == "week") {
                tmp.push({
                    field: v,
                    title: '周',
                    width: '132px',
                    sortable: false
                });
            }else if(v == "newuser") {
                tmp.push({
                    field: v,
                    title: '本月新增用户数',
                    width: '132px',
                    sortable: false
                });
            }else {
                tmp.push({
                    field: v,
                    title: v,
                    width: '132px',
                    sortable: false,
                    formatter: function (value, row, index) {
                        if(value == "-1") {
                            return "";
                        }else {
                            return value;
                        }
                    }
                });
            }
        });
        initBootstrapTable($table, tmp, data, r.data.total, periodType);
    });
}

// 初始化dataTable
function initBootstrapTable($el, columns, data, total, periodType) {
    var option = {
        columns: columns,
        data: data,
        search: false,
        fixedColumns: true,
        fixedNumber: 2
    };
    if(data.length > 12) {
        option.height = 400;
    }
    $el.bootstrapTable('destroy').bootstrapTable(option);
    // 合并单元格
    if(periodType.indexOf("month") > -1) {
        total.month = '合计：';
        $el.bootstrapTable('append', total);
    }else {
        total.week = '合计：';
        $el.bootstrapTable('append', total);
    }
}