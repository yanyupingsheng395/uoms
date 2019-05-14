/**
 * 根据值大小，赋予不同颜色代码值
 * @param type
 * @param size
 */
function colorCode(type, size) {
    if(type == 1) {
        if(size < 30) {
            return "#87CEFA";
        }
        if(size < 50 && size >= 30) {
            return "#6495ED";
        }
        if(size < 100 && size >= 50) {
            return "#1E90FF";
        }
        if(size >= 100) {
            return "#4169E1";
        }
    }
    if(type == 2) {
        if(size < 5) {
            return "#87CEFA";
        }
        if(size < 10 && size >= 5) {
            return "#6495ED";
        }
        if(size < 20 && size >= 10) {
            return "#1E90FF";
        }
        if(size >= 20) {
            return "#4169E1";
        }
    }
}

//表头信息
var columns = [];
$(function () {
    var date = new Date();
    date.setMonth(date.getMonth()-12);
    var month = "";
    if(date.getMonth() < 9) {
        month = "0" + (date.getMonth() + 1);
    }else {
        month = date.getMonth() + 1;
    }
    $("#startDate_1").val(date.getFullYear() + "-" + month);
    $("#startDate_2").val(date.getFullYear() + "-" + month);
    init_date("startDate_1", "yyyy-mm", 1,2,1);
    init_date("startDate_2", "yyyy-mm", 1,2,1);
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
    getData(indexTotal);
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
    var start = $("#startDate_1").val();
    var periodType=$("#cohortbtngroup1>.btn-primary:first").attr("name");
    $.get(url, {periodType: periodType, start: start}, function(r) {
        console.log(r)
        var columns = new Array();
        var percent = false;
        if(idx == 1 || idx == 3) {
            percent = true;
        }
        if(periodType == "dmonth") {
            columns = getDMonthCols(percent);
        }else if(periodType == "month"){
            columns = getMonthCols(r.data.columns, percent);
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

// 留存率自然月
function getMonthCols(data, percent) {
    var cols = [];
    $.each(data, function (k, v) {
        var o = new Object();
        o.field = v;
        if(v == "month") {
            o.title = "月份";
        }else if(v == "newUsers"){
            o.title = "本月新增用户数";
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


