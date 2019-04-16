var  lineOption= {
    xAxis: {
        type: 'category',
        data: []
    },
    yAxis: {
        type: 'value'
    },
    series: [{
        data: [],
        type: 'line'
    }]
};

$(function () {

     //打开遮罩层，开始渲染数据
    lightyear.loading('show');

    var reasonId = $("#reasonId").val();
    //获取到原因的头数据
    $.getJSON("/reason/getReasonDetailById?reasonId="+reasonId,function (resp) {
        if (resp.code === 200){
            var msg=resp.msg;
           //获取到头的详细信息
           $("#zhibiao").val(msg.KPI_NAME);
           $("#kpiCode").val(msg.KPI_CODE);

           $("#start_dt").val(msg.BEGIN_DT);
           $("#end_dt").val(msg.END_DT);
           $("#period").val(msg.PERIOD_TYPE);
           //获取到详细信息
           var dims=msg.reasonDetail;
           $.each(dims,function (index,item) {
               $("#dimlist").append("<li class='list-group-item' style='height:43px;'><input  class='col-xs-11 dimDispaly'  value=\""+item.DIM_DISPLAY_VALUE+"\"  title=\""+item.DIM_DISPLAY_VALUE+"\" style='border:0px' disabled='true'/></li>");
           })

           //获取到模板信息，更新模板信息
            var templates=msg.template;
            //获取到第一个模板信息
            var activeTabCode='';
           $.each(templates,function (index,item) {
               if(index==0) //第一个tab为打开状态
               {
                   activeTabCode=item.TEMPLATE_CODE;
                   $("#templateCode").val(activeTabCode);
                   $("#historyTabs").append("<li class='active'><a href='#"+item.TEMPLATE_CODE+"'  data-toggle='tab'>"+item.TEMPLATE_NAME+"</a></li>");  //加载tab标签

                   //加载标签页显示数据的表格
                   $("#historyTabContent").append("<div class='tab-pane fade active in' id='"+item.TEMPLATE_CODE+"'><table id='"+item.TEMPLATE_CODE+"Table' class='table text-center'/></div>");

               }else
               {
                   $("#historyTabs").append("<li><a href='#"+item.TEMPLATE_CODE+"'  data-toggle='tab'>"+item.TEMPLATE_NAME+"</a></li>");
                   //加载标签页的列表
                   $("#historyTabContent").append("<div class='tab-pane fade' id='"+item.TEMPLATE_CODE+"'><table id='"+item.TEMPLATE_CODE+"Table' class='table text-center'/></div>");
               }

           });

            var kpiCode=$("#kpiCode").val();
            //填充此模板下指标的历史表现
            $.getJSON("/reason/getReasonKpiHistroy?reasonId="+reasonId+"&kpiCode="+kpiCode+"&templateCode="+activeTabCode, function (resp) {
                if (resp.code==200){
                    var tableName=activeTabCode+'Table';
                    createTableHeader(tableName);
                    $('#'+tableName).bootstrapTable('load', resp.msg);
                }
            })

            InitRelateKpiList(reasonId,activeTabCode);
        }

        //为tab页增加事件
        $("a[data-toggle='tab']:not(.templateother)").on('shown.bs.tab', function (e) {
            // 获取已激活的标签页的名称
            var activeTab = $(e.target).attr("href");
            var templateCode=activeTab.substr(1,activeTab.length);
            $("#templateCode").val(templateCode);

            //填充此模板下指标的历史表现
            $.getJSON("/reason/getReasonKpiHistroy?reasonId="+reasonId+"&kpiCode="+kpiCode+"&templateCode="+templateCode, function (resp) {
                if (resp.code==200){
                    var tableName=templateCode+'Table';

                    createTableHeader(tableName);
                    $('#'+tableName).bootstrapTable('load', resp.msg);
                }

                //填充指标下拉列表
                InitRelateKpiList(reasonId,templateCode);
            })
        });

        //为指标下拉列表增加切换事件
        $("#reasonkpiCode").on('change',function (e) {
            createChart($("#reasonId").val(),$("#templateCode").val(),$("#reasonkpiCode").find("option:selected").val());
        });


    });

    //btn_concern 绑定事件
    $('#btn_concern').on('click',function () {
         //获取关注列表
        $.getJSON("/reason/getConcernReasonKpis?reasonId="+reasonId, function (resp) {
            if (resp.code==200){
                createConcernTableHeader('concernTable');
                $('#concernTable').bootstrapTable('load', resp.msg);
                //打开模态对话框
                $("#concern_modal").modal("show");
            }
        })
    });

    //关闭遮罩层
    lightyear.loading('hide');
});

function InitRelateKpiList(reasonId,templateCode)
{
    //清空相关指标下的选项
    $("#reasonkpiCode  option").remove();

    //填充此模板下的相关指标列表
    $.getJSON("/reason/getRelatedKpiList?reasonId="+reasonId+"&templateCode="+templateCode, function (resp) {
        if (resp.code==200){
            $.each(resp.msg,function (index,item) {
                $("#reasonkpiCode").append("<option value='"+item.REASON_KPI_CODE+"'>"+item.REASON_KPI_NAME+"</option>");
            })
            $("#reasonkpiCode").selectpicker('refresh');
            //渲染图形
            createChart(reasonId,templateCode,$('#reasonkpiCode option:first-child').val());  //原因ID 模板CODE 指标CODE
        }
    })
}
function createChart(reasonId,templateCode,reasonKpiCode)
{
    $.getJSON("/reason/getReasonRelatedKpi?reasonId="+reasonId+"&reasonKpiCode="+reasonKpiCode+"&templateCode="+templateCode, function (resp) {
        if (resp.code==200){
           //绘制echarts图
            lineOption.xAxis.data=resp.data.xdata;
            lineOption.series[0].data=resp.data.ydata;
            var kpiChart = echarts.init(document.getElementById('linechart'), 'macarons');
            kpiChart.setOption(lineOption);
        }
    })
}

function createTableHeader(tableName)
{
    $("#"+tableName).bootstrapTable({
        dataType: "json",
        showHeader:true,
        columns: [{
            field: 'REASON_KPI_TYPE',
            title: '指标分类',
            align: 'center'
        }, {
            field: 'REASON_KPI_NAME',
            title: '指标',
            align: 'center'
        }, {
            field: 'RELATE_VALUE',
            title: '相关性',
            align: 'center',
            formatter:function(value,row,index){
                if (value>0.5){
                    return value+"&nbsp;<span class='mdi mdi-record' style='color:#02fd4f;font-size: 16px;'></span>";
                }else{
                    return value+"&nbsp;<span class='mdi mdi-record' style='color:#fd2502;font-size: 16px;'></span>";
                }
            }
        }, {
            field: 'REGRESSION_VALUE',
            title: '回归系数',
            align: 'center'
        },
        {
            filed: 'button',
            title: '关注',
            formatter: function (value, row, index) {
                var data=JSON.stringify(row);
                if (row.IS_CONCERN == "N") {
                    return "<a><i class='mdi mdi-star-outline' onclick='processConcern("+data+","+index+")'></i></a>";
                }else{
                    return "<a><i class='mdi mdi-star'  onclick='processConcern("+data+","+index+")'></i></a>";
                }
            }
        }
        ]
   })
}

function createConcernTableHeader(tableName)
{
    $("#"+tableName).bootstrapTable({
        dataType: "json",
        showHeader:true,
        columns: [{
            field: 'TEMPLATE_NAME',
            title: '模板名称',
            align: 'center'
        },{
            field: 'REASON_KPI_TYPE',
            title: '指标分类',
            align: 'center'
        }, {
            field: 'REASON_KPI_NAME',
            title: '指标',
            align: 'center'
        }, {
            field: 'RELATE_VALUE',
            title: '相关性',
            align: 'center',
            formatter:function(value,row,index){
                if (value>0.5){
                    return value+"&nbsp;<span class='mdi mdi-record' style='color:#02fd4f;font-size: 16px;'></span>";
                }else{
                    return value+"&nbsp;<span class='mdi mdi-record' style='color:#fd2502;font-size: 16px;'></span>";
                }
            }
        }, {
            field: 'REGRESSION_VALUE',
            title: '回归系数',
            align: 'center'
        }
        ]
    })
}

/**
 * 处理 关注 取消关注的事件
 */
function processConcern(data,index) {

    //开启遮罩层
    lightyear.loading('show');

    var reasonKpiCode=data.REASON_KPI_CODE;
    var templateCode=data.TEMPLATE_CODE;
    var kpiCode=data.KPI_CODE;
    var reasonId = $("#reasonId").val();
    $.get("/reason/addConcernKpi?reasonId="+reasonId+"&kpiCode="+kpiCode+"&templateCode="+templateCode+"&reasonKpiCode="+reasonKpiCode, null, function (r) {
       //刷新当前指标列表页的数据

        $.getJSON("/reason/getReasonKpiHistroy?reasonId="+reasonId+"&kpiCode="+kpiCode+"&templateCode="+templateCode, function (resp) {
            if (resp.code==200){
                var tableName=templateCode+'Table';
                createTableHeader(tableName);
                $('#'+tableName).bootstrapTable('load', resp.msg);
            }

            //填充指标下拉列表
            InitRelateKpiList(reasonId,templateCode);

            lightyear.loading('hide');
        })
    });
}


