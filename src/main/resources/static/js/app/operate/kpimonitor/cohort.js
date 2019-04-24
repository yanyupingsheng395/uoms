//表头信息
var columns = [];

$(function () {

    init_date("startDate_1", "yyyy-mm-dd", 0,2,0);
    init_date("endDate_1", "yyyy-mm-dd", 0,2,0);

    $("#cohortbtngroup1>.cohortbtn").on('click',function () {

        //判断当前元素是否具有btn-primary 的class属性 如果已经有，则什么也不做
        if(!$(this).hasClass("btn-primary"))
        {
              //修改当前元素的样式
            $(this).removeClass("btn-default").addClass("btn-primary");
            $(this).siblings().removeClass("btn-primary").addClass("btn-default");

            //更新数据 TODO

        }

    });

    //给bootstrap tab添加点击事件 todo

    createDataForRetain();
});

/**
 * 留存率
 */
function createDataForRetain() {
    var start = $("#startDate_1").val();
    var end = $("#endDate_1").val();

    //获取选择的周期类型
    var periodType=$("#cohortbtngroup1>.btn-primary:first").attr("name");

    //清空表头信息
    columns = [];
    $.getJSON("/kpiMonitor/getRetainData?periodType="+periodType+"&startDt="+start+"&endDt="+end, function (resp) {
        if (resp.code==200){
            createTableHeader('dataTable1',resp.msg.columns);
            //加载数据
            $('#dataTable1').bootstrapTable('load', resp.data);
        }
    })
    //initTable();
    // var head = "<thead><tr><th>日期</th>";
    // var body = "<tbody>";
    // for(var i=0; i<month.length; i++) {
    //     head += "<th> " + month[i] + " </th>";
    //     body += "<tr><td>" + month[i] + "</td>";
    //     for(var j=0; j<month.length; j++) {
    //         if(i <= j) {
    //             body += "<td>"+getRandom(0, 100) + "." + getRandom(0, 100) + "%</td>";
    //         }else {
    //             body += "<td></td>";
    //         }
    //     }
    //     body += "</tr>";
    // }
    // head += "</tr></thead>";
    // body += "</tbody>";
    // $("#dataTable1").html("").html(head);
    // $("#dataTable1").append(body);
}

function createTableHeader(tableID,columns)
{
    $("#"+tableID).bootstrapTable("destory").bootstrapTable({
        dataType: "json",
        showHeader:true,
        columns: columns,
        fixedColumns : false,
        pagination : false,
        height: 400,
        sidePagination : "server"
    });
}

/**
 * 留存用户数量
 */
function createDataForRetainCnt() {
    var start = $("#startDate_1").val();
    var end = $("#endDate_1").val();
    var month = getMonthPeriod(start, end);


    //获取列的数量

    //获取行头

    var head = "<thead><tr><th>日期</th>";
    var body = "<tbody>";
    for(var i=0; i<month.length; i++) {
        head += "<th> " + month[i] + " </th>";
        body += "<tr><td>" + month[i] + "</td>";
        for(var j=0; j<month.length; j++) {
            if(i <= j) {
                body += "<td>"+getRandom(0, 100) + "." + getRandom(0, 100) + "%</td>";
            }else {
                body += "<td></td>";
            }
        }
        body += "</tr>";
    }
    head += "</tr></thead>";
    body += "</tbody>";
    $("#dataTable1").html("").html(head);
    $("#dataTable1").append(body);
}

/**
 * 流失率
 */
function createDataForLoss() {
    var start = $("#startDate_1").val();
    var end = $("#endDate_1").val();
    var month = getMonthPeriod(start, end);


    //获取列的数量

    //获取行头

    var head = "<thead><tr><th>日期</th>";
    var body = "<tbody>";
    for(var i=0; i<month.length; i++) {
        head += "<th> " + month[i] + " </th>";
        body += "<tr><td>" + month[i] + "</td>";
        for(var j=0; j<month.length; j++) {
            if(i <= j) {
                body += "<td>"+getRandom(0, 100) + "." + getRandom(0, 100) + "%</td>";
            }else {
                body += "<td></td>";
            }
        }
        body += "</tr>";
    }
    head += "</tr></thead>";
    body += "</tbody>";
    $("#dataTable1").html("").html(head);
    $("#dataTable1").append(body);
}

/**
 * 流失用户数量
 */
function createDataForLossCnt() {
    var start = $("#startDate_1").val();
    var end = $("#endDate_1").val();
    var month = getMonthPeriod(start, end);


    //获取列的数量

    //获取行头

    var head = "<thead><tr><th>日期</th>";
    var body = "<tbody>";
    for(var i=0; i<month.length; i++) {
        head += "<th> " + month[i] + " </th>";
        body += "<tr><td>" + month[i] + "</td>";
        for(var j=0; j<month.length; j++) {
            if(i <= j) {
                body += "<td>"+getRandom(0, 100) + "." + getRandom(0, 100) + "%</td>";
            }else {
                body += "<td></td>";
            }
        }
        body += "</tr>";
    }
    head += "</tr></thead>";
    body += "</tbody>";
    $("#dataTable1").html("").html(head);
    $("#dataTable1").append(body);
}

// $("#"+tableName).bootstrapTable({
//     dataType: "json",
//     showHeader:true,
//     columns: [{
//         field: 'fname',
//         title: '因子名称',
//         align: 'left'
//     },{
//         field: 'formula',
//         title: '回归公式',
//         align: 'left'
//     }, {
//         field: 'business',
//         title: '说明',
//         align: 'left'
//     }
//     ]
// })