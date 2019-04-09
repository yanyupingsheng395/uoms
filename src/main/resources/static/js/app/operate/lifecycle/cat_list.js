var selectId='';
var selectCatName='';

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
        nameRotate: 45
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
        nameRotate: 45
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
        nameRotate: 45
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

    //添加行点击事件
    $('#catListTable').on('click-row.bs.table', function (e,row,$element)
    {
        //选中的数据放入到全局对象中
        selectId=row.ROW_WID;
        $('.changeColor').removeClass('changeColor');
        $($element).addClass('changeColor');

        dataInit(selectId);
    });

    // 初始化数据
    function dataInit(cate_wid) {
        $("#initTab1, #initTab2").attr("style", "display:none;");  //隐藏提示

        // todo 判断当前tab处在那个位置

        $("#tabContent1").attr("class", "chartpanel");
        $("#tabContent2").attr("class", "chart_none_panel");
    }

    $('#catListTable').on('post-body.bs.table', function (e, settings) {
        if(null!=selectId&&selectId!='')
        {
            $.each(settings,function (i,v) {
                    if(v.ROW_WID === parseInt(selectId)){
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