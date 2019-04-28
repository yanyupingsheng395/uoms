var selectId='';
var currentSpu = "";
var stats = null;
//////////////////////////////////////////////虚拟数据/////////////////////////////////////////////////////////////

function getOpuserDdata() {
    var opuser_data=[];
    opuser_data.push();
    opuser_data.push({user_id:'14256',spu_name: currentSpu,buy_date:'2019-04-10',buy_time:'2',user_value:'0.2', active_level: '低', order_price: '1205'});
    opuser_data.push({user_id:'14257',spu_name: currentSpu,buy_date:'2019-04-03',buy_time:'3',user_value:'0.3', active_level: '中', order_price: '2310'});
    opuser_data.push({user_id:'14258',spu_name: currentSpu,buy_date:'2019-04-03',buy_time:'4',user_value:'0.4', active_level: '高', order_price: '205'});
    opuser_data.push({user_id:'41429',spu_name: currentSpu,buy_date:'2019-04-05',buy_time:'5',user_value:'0.5', active_level: '低', order_price: '1232'});
    opuser_data.push({user_id:'14230',spu_name: currentSpu,buy_date:'2019-04-10',buy_time:'2',user_value:'0.6', active_level: '中', order_price: '4325'});
    opuser_data.push({user_id:'14231',spu_name: currentSpu,buy_date:'2019-04-10',buy_time:'2',user_value:'0.7', active_level: '低', order_price: '5485'});
    opuser_data.push({user_id:'14232',spu_name: currentSpu,buy_date:'2019-04-03',buy_time:'4',user_value:'0.5', active_level: '中', order_price: '1298'});
    opuser_data.push({user_id:'14233',spu_name: currentSpu,buy_date:'2019-04-10',buy_time:'2',user_value:'0.2', active_level: '高', order_price: '1300'});
    opuser_data.push({user_id:'14234',spu_name: currentSpu,buy_date:'2019-04-03',buy_time:'5',user_value:'0.2', active_level: '低', order_price: '400'});
    opuser_data.push({user_id:'14235',spu_name: currentSpu,buy_date:'2019-04-03',buy_time:'2',user_value:'0.5', active_level: '中', order_price: '256'});
    opuser_data.push({user_id:'14236',spu_name: currentSpu,buy_date:'2019-04-10',buy_time:'4',user_value:'0.2', active_level: '低', order_price: '148'});
    opuser_data.push({user_id:'14237',spu_name: currentSpu,buy_date:'2019-04-03',buy_time:'2',user_value:'0.2', active_level: '高', order_price: '4135'});
    opuser_data.push({user_id:'14238',spu_name: currentSpu,buy_date:'2019-04-10',buy_time:'5',user_value:'0.5', active_level: '中', order_price: '412'});
    opuser_data.push({user_id:'14239',spu_name: currentSpu,buy_date:'2019-04-03',buy_time:'2',user_value:'0.4', active_level: '低', order_price: '135'});
    opuser_data.push({user_id:'14240',spu_name: currentSpu,buy_date:'2019-04-10',buy_time:'5',user_value:'0.5', active_level: '低', order_price: '505'});
    opuser_data.push({user_id:'14241',spu_name: currentSpu,buy_date:'2019-04-10',buy_time:'5',user_value:'0.4', active_level: '中', order_price: '705'});
    opuser_data.push({user_id:'14242',spu_name: currentSpu,buy_date:'2019-04-03',buy_time:'2',user_value:'0.5', active_level: '高', order_price: '205'});
    opuser_data.push({user_id:'14243',spu_name: currentSpu,buy_date:'2019-04-03',buy_time:'5',user_value:'0.2', active_level: '低', order_price: '1205'});
    return opuser_data;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
$(function () {
    periodTypeOption();
    // 初始化表格数据
    initTableData();

    var flag = false;
    //添加行点击事件
    $('#catListTable').on('click-row.bs.table', function (e,row,$element)
    {
        //选中的数据放入到全局对象中
        selectId=row.CATE_WID;
        $('.changeColor').removeClass('changeColor');
        $($element).addClass('changeColor');
        currentSpu = row.spuName;
        dataInit(selectId);
    });

    // 初始化数据
    function dataInit(cate_wid) {
        if(!flag) {
            flag = true;
            $("#initTab1, #initTab2").attr("style", "display:none;");  //隐藏提示

            // todo 判断当前tab处在那个位置

            $("#tabContent1").attr("class", "chartpanel");
            $("#tabContent2").attr("class", "chart_none_panel");
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

    //为tab页增加事件
    $("a[data-toggle='tab']").on('shown.bs.tab', function (e) {
        // 获取已激活的标签页的名称
        var activeTab = $(e.target).attr("href");
        if(selectId == "") {
            $("#initTab1, #initTab2").attr("style", "display:block;");
        }else
        {
            $("#initTab1, #initTab2").attr("style", "display:none;");
            if(activeTab='#tab_kpis')
            {
                $("#tabContent2").attr("class", "chartpanel");
                tab2Init();
            }else
            {
                $("#tabContent2").attr("class", "chartpanel");
            }
        }
    });

});

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
            console.log(r);
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

init_date("startDate1", "yyyy-mm-dd", 0,2,0);
init_date("endDate1", "yyyy-mm-dd", 0,2,0);
init_date("startDate2", "yyyy-mm-dd", 0,2,0);
init_date("endDate2", "yyyy-mm-dd", 0,2,0);
init_date("startDate3", "yyyy-mm-dd", 0,2,0);
init_date("endDate3", "yyyy-mm-dd", 0,2,0);

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

var opuser_columns=[{
    field: 'user_id',
    title: '用户ID'
}, {
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
}];





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
            right: '10%',
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