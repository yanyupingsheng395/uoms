//表头信息
var columns = [];
$(function () {
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

function searchData() {
    if($("#startDateOfRetention").val() == "") {
        $MB.n_warning("请选择日期！");
    }else {
        $("#tabs02").find("li[class='active']").find("a").click();
        $("#tabs03").find("li[class='active']").find("a").click();
    }
}

// 获取表格数据
function getData(idx) {
    $MB.loadingDesc('show', "正在计算数据中，请稍候...");
    var url = "";
    if(idx == 1) {
        url = "/kpiMonitor/getRetainData";
    }else if(idx == 2) {
        url = "/kpiMonitor/getRetainUserCount"
    }else if(idx == 3) {
        url = "/kpiMonitor/getLossUserRate"
    }else if(idx == 4) {
        url = "/kpiMonitor/getLossUser"
    }
    var $table = $('#dataTable' + idx);
    var start = $("#startDateOfRetention").val();
    var periodType=$("#cohortbtngroup1>.btn-primary:first").attr("name");
    $.get(url, {periodType: periodType, start: start}, function(r) {
        var columns = new Array();
        var percent = false;
        if(idx == 1 || idx == 3) {
            percent = true;
        }
        if(periodType == "dmonth") {
            columns = getDMonthCols(percent);
        }else if(periodType == "month"){
            columns = getMonthCols(r.data.columns, percent, null);
        }
        var data = r.data.data;
        initBootstrapTable($table, columns, data, 0, periodType);
        $MB.loadingDesc('hide');
    });
}

// 留存率间隔月
function getDMonthCols(percent) {
    var fix = "";
    if(percent) {
        fix = "%";
    }else {
        fix = "";
    }
    var fmt = function (value, row, index) {if(value == null) {return "";}else {return value + fix;}};
    var cols = [
        {field: 'month_id', title: '月份'},
        {field: 'total_user', title: '本月新增用户数', width: '132px'},
        {field: 'month1', title: '+1月', formatter: fmt},
        {field: 'month2', title: '+2月', formatter: fmt},
        {field: 'month3', title: '+3月', formatter: fmt},
        {field: 'month4', title: '+4月', formatter: fmt},
        {field: 'month5', title: '+5月', formatter: fmt},
        {field: 'month6', title: '+6月', formatter: fmt},
        {field: 'month7', title: '+7月', formatter: fmt},
        {field: 'month8', title: '+8月', formatter: fmt},
        {field: 'month9', title: '+9月', formatter: fmt},
        {field: 'month10', title: '+10月', formatter: fmt},
        {field: 'month11', title: '+11月', formatter: fmt},
        {field: 'month12', title: '+12月', formatter: fmt},
    ];
    return cols;
}

function getKpiDMonthCols(percent, type) {
    var fix = "";
    if(percent) {
        fix = "%";
    }else {
        fix = "";
    }
    var fmt = function (value, row, index) {
        if(value == null) {
            return "";
        }else {
            return value + fix;
        }
    };
    var cols = [
        {field: 'month_id', title: '月份'},
        {field: 'total_user', title: '本月新增用户数', width: '132px'},
        {field: 'current_month', title: type, width:'132px', formatter: fmt},
        {field: 'month1', title: '+1月', formatter: fmt},
        {field: 'month2', title: '+2月', formatter: fmt},
        {field: 'month3', title: '+3月', formatter: fmt},
        {field: 'month4', title: '+4月', formatter: fmt},
        {field: 'month5', title: '+5月', formatter: fmt},
        {field: 'month6', title: '+6月', formatter: fmt},
        {field: 'month7', title: '+7月', formatter: fmt},
        {field: 'month8', title: '+8月', formatter: fmt},
        {field: 'month9', title: '+9月', formatter: fmt},
        {field: 'month10', title: '+10月', formatter: fmt},
        {field: 'month11', title: '+11月', formatter: fmt},
        {field: 'month12', title: '+12月', formatter: fmt}
    ];
    return cols;
}

// 留存率自然月
function getMonthCols(data, percent, type) {
    var cols = [];
    $.each(data, function (k, v) {
        var o = new Object();
        o.field = v;
        if(v == "month") {
            o.title = "月份";
        }else if(v == "newUsers"){
            o.title = "本月新增用户数";
            o.width = "132px";
        }else if(v == "uprice") {
            o.title = type;
            o.width = "132px";
        }else {
            o.title = v;
        }
        o.formatter = function (value, row, index) {
            if (value == null) {
                return "";
            }else {
                var fix;
                if(percent) {
                    fix = "%";
                }else {
                    fix = "";
                }
                if(v != "month" && v != "newUsers") {
                    return value + fix;
                }else {
                    return value;
                }
            }
        };
        cols.push(o);
    });
    return cols;
}

function getData1(idx) {
    $MB.loadingDesc('show', "正在计算数据中,请稍候...");
    var url = "";
    var type = "";
    if(idx == 1) {
        url = "/kpiMonitor/getUpriceData";
        type = "本月客单价（元）";
    }else if(idx == 2) {
        url = "/kpiMonitor/getPriceData";
        type = "本月订单价（元）";
    }else if(idx == 3) {
        url = "/kpiMonitor/getPurchFreq";
        type = "本月购买频率";
    }else if(idx == 4) {
        url = "/kpiMonitor/getUnitPriceData";
        type = "本月件单价（元）";
    }else if(idx == 5) {
        url = "/kpiMonitor/getJoinRateData";
        type = "本月连带率";
    }
    var $table = $('#dataTable1' + idx);
    var start = $("#startDateOfRetention").val();
    var periodType=$("#cohortbtngroup2>.btn-primary:first").attr("name");
    $.get(url, {periodType: periodType, start: start}, function(r) {
        var columns = new Array();
        var percent = false;
        if(periodType == "dmonth") {
            columns = getKpiDMonthCols(percent, type);
        }else if(periodType == "month"){
            columns = getMonthCols(r.data.columns, percent, type);
        }
        var data = r.data.data;
        initBootstrapTable($table, columns, data, 0, periodType);
        $MB.loadingDesc('hide');
    });
}

/**
 * 数据量小会出现表头重叠，否则不出现。通过data.length判断解决
 * @param $el
 * @param columns
 * @param data
 * @param total
 * @param periodType
 */
// 初始化dataTable
function initBootstrapTable($el, columns, data, total, periodType) {
    var option = {
        columns: columns,
        data: data,
        search: false
    };
    $el.bootstrapTable('destroy').bootstrapTable(option);
}