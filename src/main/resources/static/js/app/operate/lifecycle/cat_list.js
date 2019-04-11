var selectId='';
var selectCatName='';

//////////////////////////////////////////////虚拟数据/////////////////////////////////////////////////////////////
var opuser_data=[];
opuser_data.push({user_id:'14256',prod_name:'测试产品1',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'高'});
opuser_data.push({user_id:'14257',prod_name:'测试产品2',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'高'});
opuser_data.push({user_id:'14258',prod_name:'测试产品3',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'中'});
opuser_data.push({user_id:'41429',prod_name:'测试产品4',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'低'});
opuser_data.push({user_id:'14230',prod_name:'测试产品1',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'高'});
opuser_data.push({user_id:'14231',prod_name:'测试产品2',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'高'});
opuser_data.push({user_id:'14232',prod_name:'测试产品3',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'中'});
opuser_data.push({user_id:'14233',prod_name:'测试产品4',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'低'});
opuser_data.push({user_id:'14233',prod_name:'测试产品1',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'高'});
opuser_data.push({user_id:'14235',prod_name:'测试产品2',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'高'});
opuser_data.push({user_id:'14236',prod_name:'测试产品3',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'中'});
opuser_data.push({user_id:'14237',prod_name:'测试产品4',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'低'});
opuser_data.push({user_id:'14238',prod_name:'测试产品1',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'高'});
opuser_data.push({user_id:'14239',prod_name:'测试产品2',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'高'});
opuser_data.push({user_id:'14240',prod_name:'测试产品3',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'中'});
opuser_data.push({user_id:'14241',prod_name:'测试产品4',start_dt:'2019-04-10',end_dt:'2019-04-20',yhld:'低'});

var retention_option1= {
    tooltip: {
        trigger: 'axis'
    },
    xAxis: {
        type: 'category',
        data: [],
        name: '',
        nameTextStyle: 'oblique',
        boundaryGap: false,
        nameRotate: 73
    },
    axisLabel:{
        interval: 0,
        rotate: 45
    },
    yAxis: {
        type: 'value',
        name: '',
        max: 50
    },
    series: [{
        data: [],
        type: 'line',
        smooth: true
    }]
};

var retention_option2= {
    tooltip: {
        trigger: 'axis'
    },
    xAxis: {
        type: 'category',
        data: [],
        name: '',
        nameTextStyle: 'oblique',
        boundaryGap: false,
        nameRotate: 63
    },
    axisLabel:{
        interval: 0,
        rotate: 45
    },
    yAxis: {
        type: 'value',
        name: '',
        max: 50
    },
    series: [{
        data: [],
        type: 'line',
        smooth: true
    }]
};

var retention_option3= {
    tooltip: {
        trigger: 'axis'
    },
    xAxis: {
        type: 'category',
        data: [],
        name: '',
        nameTextStyle: 'oblique',
        boundaryGap: false,
        nameRotate: 80
    },
    axisLabel:{
        interval: 0,
        rotate: 45
    },
    yAxis: {
        type: 'value',
        name: '',
        max: 50
    },
    series: [{
        data: [],
        type: 'line',
        smooth: true
    }]
};

var radar_option = {
    tooltip: {},
    legend: {
        data: ['组合1', '组合2','组合3']
    },
    radar: {
        indicator: [
            { name: '购买次数', max: 50},
            { name: '客单价', max: 1000},
            { name: '购买间隔', max: 365},
        ],
        center: ['50%','50%'],
        radius: 80
    },
    series: [{
        type: 'radar',
        //  itemStyle: {normal: {areaStyle: {type: 'default'}}},
        data : [
            {
                value : [30, 256, 25],
                name : '组合1'
            },
            {
                value : [5, 999, 20],
                name : '组合2'
            },
            {
                value : [40, 500, 45],
                name : '组合3'
            }
        ]
    }]
};

var area_option = {
    tooltip : {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#6a7985'
            }
        }
    },
    xAxis: {
        type: 'category',
        boundaryGap: false,
        data: []
    },
    yAxis: {
        type: 'value'
    },
    series: [{
        data: [],
        type: 'line',
        areaStyle: {}
    }]
};

//构造留存率与其它指标关系的三个图
retention_option1.xAxis.name="购买次数";
retention_option1.xAxis.data=['1','2','3','4','5','6','7','8','9','10','11+'];
retention_option1.yAxis.name="留存率(%)";
retention_option1.series[0].data=['3','5','5.4','6','12','14','17','17.5','17.6','17.7','17.8'];

retention_option2.xAxis.name="客单价";
retention_option2.xAxis.data=['0-50','50-100','100-150','150-200','200-250','250-300','300-350','350-400','400-450','450-500','500+'];
retention_option2.yAxis.name="留存率(%)";
retention_option2.series[0].data=['3','5','5.4','6','12','14','17','17.5','17.6','17.7','17.8'];

retention_option3.xAxis.name="购买间隔（天）";
retention_option3.xAxis.data=['0-30','30-60','60-90','90-120','120-150','150-180','180-210','210-240','240-270','270-300','300+'];
retention_option3.yAxis.name="留存率(%)";
retention_option3.series[0].data=['3','5','5.4','6','12','14','17','17.5','17.6','17.7','17.8'];

//初始化面积图
area_option.xAxis.data=['新客期','成长期','成熟期','衰退期','流失期'];
area_option.series[0].data=[4300,3200,5000,3500,6000]


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
$(function () {

    var settings = {
        url: "/lifecycle/getCatList?cateName="+$("#cate_name").val()+"&orderColumn="+$("#orderColumn").val(),
        method: 'post',
        cache: false,
        pagination: true,
        //   striped: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 5,            //每页的记录行数（*）
        pageList: [5,10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit )+ 1  //页码
            };
        },
        columns: [{
            field: 'CATE_TYPE3',
            title: '品类名称'
        }, {
            field: 'GMV_RATE',
            title: 'GMV贡献率',
            formatter: function (value, row, index) {
                return value == null ? "" : value + "%"
            }
        }, {
            field: 'GMV_RELATE',
            title: 'GMV相关性',
            formatter: function (value, row, index) {
                return value == null ? "":value
            }
        }, {
            field: 'PROFIT_RATE',
            title: '利润贡献率',
            formatter: function (value, row, index) {
                return value == null ? "" : value + "%"
            }
        }, {
            field: 'PROFIT_RELATE',
            title: '利润相关性',
            formatter: function (value, row, index) {
                return value == null ? "":value
            }
        }, {
            field: 'PROFIT_PCT',
            title: '利润率',
            formatter: function (value, row, index) {
                return value == null ? "" : value + "%"
            }
        }, {
            field: 'SALES_CNT',
            title: '销量',
            formatter: function (value, row, index) {
                return value == null ? "":value
            }
            }]
    };
    $('#catListTable').bootstrapTable(settings);

    var flag = false;
    //添加行点击事件
    $('#catListTable').on('click-row.bs.table', function (e,row,$element)
    {
        //选中的数据放入到全局对象中
        selectId=row.CATE_WID;
        $('.changeColor').removeClass('changeColor');
        $($element).addClass('changeColor');

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
        var opt = {
            url: "/lifecycle/getCatList?cateName="+$("#cate_name").val()+"&orderColumn="+$("#orderColumn").val()};

        $('#catListTable').bootstrapTable("refresh",opt);
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

                var freqChart = echarts.init(document.getElementById('retention_freq'), 'macarons');
                var priceChart = echarts.init(document.getElementById('retention_price'), 'macarons');
                var gapChart = echarts.init(document.getElementById('retention_gap'), 'macarons');
                var radarChart = echarts.init(document.getElementById('lifecycle_radar'), 'macarons');
                var shapChart = echarts.init(document.getElementById('lifecycle_shape'), 'macarons');

                freqChart.setOption(retention_option1);
                priceChart.setOption(retention_option2);
                gapChart.setOption(retention_option3);
                radarChart.setOption(radar_option);
                shapChart.setOption(area_option);
            }else
            {
                $("#tabContent2").attr("class", "chartpanel");
            }
        }
    });

});

init_date("startDate1", "yyyy-mm", 1,2,1);
init_date("endDate1", "yyyy-mm", 1,2,1);
init_date("startDate2", "yyyy-mm", 1,2,1);
init_date("endDate2", "yyyy-mm", 1,2,1);
init_date("startDate3", "yyyy-mm", 1,2,1);
init_date("endDate3", "yyyy-mm", 1,2,1);

function view_matrix(period,tasktype) {
    //弹出面板
    $('#matrix_modal').modal('show');
    var matrixChart = echarts.init(document.getElementById('matrix_chart'), 'macarons');
    matrixChart.setOption(matrix_option);
    matrixChart.on('click', refreshOpUserTable);
}

function refreshOpUserTable(param) {
    var value=param.value[0];
    var activity=param.value[1];

    var valueLabel='';
    var activityLabel='';

    if(value>0.7)
    {
        valueLabel='高价值';
    }else if(value<0.3)
    {
        valueLabel='低价值';
    }else{
        valueLabel='中价值';
    }

    if(activity>0.7)
    {
        activityLabel='高价值';
    }else if(activity<0.3)
    {
        activityLabel='低价值';
    }else{
        activityLabel='中价值';
    }

    $('#valuelabel').text(valueLabel);
    $('#activitylabel').text(activityLabel);

    //重新获取数据
    var newData=[];
    for ( var i = 0; i <opuser_data.length; i++){
        var rn=Math.floor(Math.random() * Math.floor(2))
        if(rn==1)
        {
            newData.push(opuser_data[i]);
        }

    }
    $('#opuserListTable').bootstrapTable('load',newData );

}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var sourceData = [{
    name: '高价值、高活跃',
    value: 0.8,
    activity: 0.8,
    ucnt:230,
    symbolSize: 30,
    color:"#409EFF"
}, {
    name: '高价值，中活跃',
    value: 0.7,
    activity: 0.2,
    ucnt:145,
    symbolSize: 25
}, {
    name: '高价值、低活跃',
    value: 0.7,
    activity: -0.3,
    ucnt:110,
    symbolSize: 22
}, {
    name: '中价值、高活跃',
    value: 0.2,
    activity: 0.9,
    ucnt:175,
    symbolSize: 18
}, {
    name: '中价值、中活跃',
    value: 0.4,
    activity: -0.2,
    ucnt:90,
    symbolSize: 19
}, {
    name: '中价值、低活跃',
    value: 0.2,
    activity: -0.8,
    ucnt:143,
    symbolSize: 23
}, {
    name: '低价值、高活跃',
    value: -0.65,
    activity: 0.65,
    ucnt:170,
    symbolSize: 27
}, {
    name: '低价值、中活跃',
    value: -0.75,
    activity: 0.1,
    ucnt:132,
    symbolSize: 25
}, {
    name: '低价值、低活跃',
    value: -0.7,
    activity: -0.8,
    ucnt:190,
    symbolSize: 28,
    color:"#5E5E5E"
}]
var  matrix_datas = [];
for (var i = 0; i < sourceData.length; i++) {
    var item = sourceData[i];
    matrix_datas.push({
        name: item.name,
        value: [item.value,item.activity,item.ucnt],
        symbolSize:item.symbolSize,
        label: {
            normal: {
                textStyle: {
                    fontSize: 11
                }
            }
        },
        itemStyle: {
            normal: {
                color:  item.color
            }
        },
    })
}

var matrix_option = {
    tooltip: {
        formatter: function(obj) {
            if (obj.componentType == "series") {
                return '<div style="border-bottom: 1px solid rgba(255,255,255,.3); font-size: 18px;padding-bottom: 7px;margin-bottom: 7px">' +
                    obj.name +
                    '</div>' +
                    '<span>' +
                    '目标人数' +
                    '</span>' +
                    ' : ' + obj.data.value[2] + '（人）'
            }
        }
    },
    label: {
        normal: {
            show: true,
            position: 'top',
            formatter: function(params) {
                return params.name
            }
        },
        emphasis: {
            show: true,
            position: 'bottom',
        }
    },
    xAxis: {
        name: '价值',
        type: 'value',
        min: -2,
        max: 2,
        scale: true,
        axisLine: {
            symbol: ['none', 'arrow'],
            symbolSize: [10, 13],
            lineStyle: {
                color: "#3259B8"
            }
        },
        axisLabel: {
            show: false
        },
        splitLine: {
            show: false
        },
        axisTick: {
            show: false
        }
    },
    yAxis: {
        name: '活跃度',
        type: 'value',
        min: -2,
        max: 2,
        scale: true,
        axisLabel: {
            show: false
        },
        splitLine: {
            show: false
        },
        axisTick: {
            show: false
        },
        axisLine: {
            symbol: ['none', 'arrow'],
            symbolSize: [10, 12],
            lineStyle: {
                color: "#3259B8"
            }
        }
    },
    series:[{
        type: 'scatter',
        data: matrix_datas,
        label: {
            show: true,
            position: 'top',
            formatter: function(params) {
                return params.name;
            }
        }}]
};

var opuser_columns=[{
    field: 'user_id',
    title: '用户编号'
}, {
    field: 'prod_name',
    title: '产品'
},  {
    field: 'start_dt',
    title: '开始时间'
}, {
    field: 'end_dt',
    title: '最晚时间'
}, {
    field: 'yhld',
    title: '优惠力度'
}];


$('#opuserListTable').bootstrapTable('destroy');
$('#opuserListTable').bootstrapTable({
    data: opuser_data,
    pagination: true,                   //是否显示分页（*）
    sidePagination: "client",           //分页方式：client客户端分页，server服务端分页（*）
    pageNumber: 1,                      //初始化加载第一页，默认第一页,并记录
    pageSize: 5,                     //每页的记录行数（*）
    pageList: [5,10, 25, 50, 100],        //可供选择的每页的行数（*）
    columns: opuser_columns
});