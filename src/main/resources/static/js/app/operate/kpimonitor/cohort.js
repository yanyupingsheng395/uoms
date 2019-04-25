//表头信息
var columns = [];
$(function () {

    init_date("startDate_1", "yyyy-mm-dd", 0,2,0);
    init_date("endDate_1", "yyyy-mm-dd", 0,2,0);
    getData(1);
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

var indexTotal = 1;
function tabClick(idx) {
    indexTotal = idx;
    getData(idx);
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
        console.log(r);

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

// 初始化dataTable
function initBootstrapTable($el, columns, data, total, periodType) {
    var option = {
        columns: columns,
        data: data,
        search: false,
        fixedColumns: true,
        fixedNumber: 2
    };
    // if(data.length > 12) {
    //     option.height = 400;
    // }
    $el.bootstrapTable('destroy').bootstrapTable(option);
    // 合并单元格
    if(periodType.indexOf("month") > -1) {
        total.month = '汇总：';
        $el.bootstrapTable('append', total);
        $el.bootstrapTable('mergeCells',{index:data.length, field:'month', colspan: 2});
    }else {
        total.week = '汇总：';
        $el.bootstrapTable('append', total);
        $el.bootstrapTable('mergeCells',{index:data.length, field:'week', colspan: 2});
    }
}