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
        toastr.warning("请输入起始时间！");
    }else {
        $("#tabs02").find("li[class='active']").find("a").click();
        $("#tabs03").find("li[class='active']").find("a").click();
    }
}

// 获取表格数据
function getData(idx) {
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
    var cols = [
        {field: 'MONTH_ID', title: '月份'},
        {field: 'TOTAL_USER', title: '本月新增用户数', width: '132px'},
        {field: 'MONTH1', title: '+1月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
        {field: 'MONTH2', title: '+2月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
        {field: 'MONTH3', title: '+3月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
        {field: 'MONTH4', title: '+4月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
        {field: 'MONTH5', title: '+5月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
        {field: 'MONTH6', title: '+6月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
        {field: 'MONTH7', title: '+7月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
        {field: 'MONTH8', title: '+8月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
        {field: 'MONTH9', title: '+9月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
        {field: 'MONTH10', title: '+10月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
        {field: 'MONTH11', title: '+11月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
        {field: 'MONTH12', title: '+12月', formatter: function (value, row, index) {if(value == "0") {return "";}else {return value + fix;}}},
    ];
    return cols;
}
// 客单价间隔月
function getDMonthUPriceCols(percent, type) {
    var fix = "";
    if(percent) {
        fix = "%";
    }else {
        fix = "";
    }

    var fmt = function (value, row, index) {if(value == "0" || value == undefined) {return "";}else {return value + fix;}};
    var cols = [
        {field: 'MONTH_ID', title: '月份'},
        {field: 'TOTAL_USER', title: '本月新增用户数', width: '132px', formatter: fmt},
        {field: 'UPRICE', title: type, width:'132px', formatter: fmt},
        {field: 'UPRICE1', title: '+1月', formatter: fmt},
        {field: 'UPRICE2', title: '+2月', formatter: fmt},
        {field: 'UPRICE3', title: '+3月', formatter: fmt},
        {field: 'UPRICE4', title: '+4月', formatter: fmt},
        {field: 'UPRICE5', title: '+5月', formatter: fmt},
        {field: 'UPRICE6', title: '+6月', formatter: fmt},
        {field: 'UPRICE7', title: '+7月', formatter: fmt},
        {field: 'UPRICE8', title: '+8月', formatter: fmt},
        {field: 'UPRICE9', title: '+9月', formatter: fmt},
        {field: 'UPRICE10', title: '+10月', formatter: fmt},
        {field: 'UPRICE11', title: '+11月', formatter: fmt},
        {field: 'UPRICE12', title: '+12月', formatter: fmt}
    ];
    return cols;
}

// 订单价间隔月
function getDMonthPriceCols(percent, type) {
    var fix = "";
    if(percent) {
        fix = "%";
    }else {
        fix = "";
    }

    var fmt = function (value, row, index) {if(value == "0" || value == undefined) {return "";}else {return value + fix;}};
    var cols = [
        {field: 'MONTH_ID', title: '月份'},
        {field: 'TOTAL_USER', title: '本月新增用户数', width: '132px', formatter: fmt},
        {field: 'PRICE', title: type, width:'132px', formatter: fmt},
        {field: 'PRICE1', title: '+1月', formatter: fmt},
        {field: 'PRICE2', title: '+2月', formatter: fmt},
        {field: 'PRICE3', title: '+3月', formatter: fmt},
        {field: 'PRICE4', title: '+4月', formatter: fmt},
        {field: 'PRICE5', title: '+5月', formatter: fmt},
        {field: 'PRICE6', title: '+6月', formatter: fmt},
        {field: 'PRICE7', title: '+7月', formatter: fmt},
        {field: 'PRICE8', title: '+8月', formatter: fmt},
        {field: 'PRICE9', title: '+9月', formatter: fmt},
        {field: 'PRICE10', title: '+10月', formatter: fmt},
        {field: 'PRICE11', title: '+11月', formatter: fmt},
        {field: 'PRICE12', title: '+12月', formatter: fmt}
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
            if (value == "0" || value == null) {
                return "";
            } else {
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
    var url = "";
    var type = "";
    if(idx == 1) {
        url = "/kpiMonitor/getUpriceData";
        type = "本月客单价（元）";
    }else if(idx == 2) {
        url = "/kpiMonitor/getPriceData"
        type = "本月订单价（元）";
    }else if(idx == 3) {
        url = "/kpiMonitor/getFreqData"
    }else if(idx == 4) {
        url = "/kpiMonitor/getSpriceData"
    }else if(idx == 5) {
        url = "/kpiMonitor/getJoinrateData"
    }
    var $table = $('#dataTable1' + idx);
    var start = $("#startDateOfRetention").val();
    var periodType=$("#cohortbtngroup2>.btn-primary:first").attr("name");
    $.get(url, {periodType: periodType, start: start}, function(r) {
        var columns = new Array();
        var percent = false;
        if(idx == 3 || idx == 4) {
            percent = true;
        }
        if(periodType == "dmonth") {
            if(idx == 1) {
                columns = getDMonthUPriceCols(percent, type);
            }else if(idx == 2) {
                columns = getDMonthPriceCols(percent, type);
            }
        }else if(periodType == "month"){
            columns = getMonthCols(r.data.columns, percent, type);
        }
        var data = r.data.data;
        initBootstrapTable($table, columns, data, 0, periodType);
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
        search: false,
    };
    $el.bootstrapTable('destroy').bootstrapTable(option);
}


