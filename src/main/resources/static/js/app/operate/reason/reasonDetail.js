
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
           $("#peroid").val(msg.PERIOD_TYPE);
           //获取到详细信息
           var dims=msg.reasonDetail;
           $.each(dims,function (index,item) {
               $("#dimlist").append("<li class='list-group-item' style='height:43px;'><input  class='col-xs-11 dimDispaly'  value='"+item.DIM_DISPLAY_VALUE+"' style='border:0px' disabled='true' maxlength='150'/></li>");
           })

           //获取到模板信息，更新模板信息
            var templates=msg.template;
            //获取到第一个模板信息
            var activeTabCode='';
           $.each(templates,function (index,item) {
               if(index==0) //第一个tab为打开状态
               {
                   activeTabCode=item.TEMPLATE_CODE;
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

           //增加 我的关注 tab
           //  $("#historyTabs").append("<li><a href='#concern'  data-toggle='tab'>我的关注</a></li>");
           //  //加载标签页的列表
           //  $("#historyTabContent").append("<div class='tab-pane fade' id='concern'><table id='concernTable' class='table text-center'/></div>");


            var kpiCode=$("#kpiCode").val();
            //填充此模板下指标的历史表现
            $.getJSON("/reason/getReasonKpiHistroy?kpiCode="+kpiCode+"&templateCode="+activeTabCode, function (resp) {
                if (resp.code==200){
                    var tableName=activeTabCode+'Table';
                    createTableHeader(tableName);
                    $('#'+tableName).bootstrapTable('load', resp.msg);
                }
            })


            //填充此模板下的相关指标列表
            $.getJSON("/reason/getRelatedKpiList?reasonId="+reasonId+"&templateCode="+activeTabCode, function (resp) {
                if (resp.code==200){
                    $.each(resp.msg,function (index,item) {
                        $("#reasonkpiCode").append("<option value='"+item.REASON_KPI_CODE+"'>"+item.REASON_KPI_NAME+"</option>");
                    })
                }
            })

            //对于第一个指标 渲染图形
            var defaultRelateKpi=$('#reasonkpiCode').val();
            console.log($('#reasonkpiCode').find("option:selected"));
            alert(defaultRelateKpi);
        }

        //为tab页增加事件
        $("a[data-toggle='tab']").on('shown.bs.tab', function (e) {
            // 获取已激活的标签页的名称
            var activeTab = $(e.target).attr("href");
            var templateCode=activeTab.substr(1,activeTab.length);

            //填充此模板下指标的历史表现
            $.getJSON("/reason/getReasonKpiHistroy?kpiCode="+kpiCode+"&templateCode="+templateCode, function (resp) {
                if (resp.code==200){
                    var tableName=templateCode+'Table';

                    createTableHeader(tableName);
                    $('#'+tableName).bootstrapTable('load', resp.msg);

                }
            })
        });


    });

    //todo 获取到所有的关注指标信息

    //关闭遮罩层
    lightyear.loading('hide');


});


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
                    return value+"&nbsp;<span class='mdi mdi-record' style='color:#008000;size: 25px;'></span>";
                }else{
                    return value+"&nbsp;<span class='mdi mdi-record' style='color:#8B0000;size: 25px;'></span>";
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


