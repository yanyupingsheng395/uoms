$(function () {
     //打开遮罩层，开始渲染数据
    lightyear.loading('show');
    var reasonId = $("#reasonId").val();

    //获取到原因的头数据
    $.getJSON("/reason/getReasonInfoById?reasonId="+reasonId,function (resp) {
        if (resp.code === 200){
            var msg=resp.msg;
           //获取到头的详细信息

           $("#reasonName").val(msg.REASON_NAME);
           $("#source").val(msg.SOURCE);
           $("#zhibiao").val(msg.KPI_NAME);
           $("#kpiCode").val(msg.KPI_CODE);

           $("#start_dt").val(msg.BEGIN_DT);
           $("#end_dt").val(msg.END_DT);
           $("#period").val(msg.PERIOD_NAME);
           //获取到详细信息
           var dims=msg.reasonDetail;
           $.each(dims,function (index,item) {
               $("#dimlist").append("<li class='list-group-item' style='height:43px;'><input  class='col-xs-11 dimDispaly'  value=\""+item.DIM_DISPLAY_VALUE+"\"  title=\""+item.DIM_DISPLAY_VALUE+"\" style='border:0px' disabled='true'/></li>");
           })

            //获取到第一个模板信息
            var defaultTabName=$('#historyTabs').find("li.active a").attr("href");
            var defaultTemplateCode=defaultTabName.substring(1,defaultTabName.length);

            //填充此次探究此模板下原因指标的快照信息
            $.getJSON("/reason/getReasonKpisSnp?reasonId="+reasonId+"&templateCode="+defaultTemplateCode, function (resp) {
                if (resp.code==200){
                    var tableName=defaultTemplateCode+'Table';
                    createTableHeader(tableName);
                    $('#'+tableName).bootstrapTable('load', resp.msg);
                }
            })

        }

        //为tab页增加事件
        $("a[data-toggle='tab']").on('shown.bs.tab', function (e) {
            // 获取已激活的标签页的名称
            var activeTab = $(e.target).attr("href");
            var templateCode=activeTab.substring(1,activeTab.length);
            $("#templateCode").val(templateCode);

            //填充此次探究此模板下原因指标的快照信息
            $.getJSON("/reason/getReasonKpisSnp?reasonId="+reasonId+"&templateCode="+templateCode, function (resp) {
                if (resp.code==200){
                    var tableName=templateCode+'Table';
                    createTableHeader(tableName);
                    $('#'+tableName).bootstrapTable('load', resp.msg);
                }
            })
        });
    });

    // 因子表格
    getMatrix();

    //关闭遮罩层
    lightyear.loading('hide');
});

function createTableHeader(tableName)
{
    $("#"+tableName).bootstrapTable({
        dataType: "json",
        showHeader:true,
        columns: [{
            field: 'reasonKpiName',
            title: '指标',
            align: 'left'
        }, {
            field: 'relateValue',
            title: '相关性',
            align: 'center',
            formatter:function(value,row,index){
                if (row.relateFlag=="Y"){
                    return value+"&nbsp;<span class='mdi mdi-record' style='color:#02fd4f;font-size: 16px;'></span>";
                }else{
                    return value+"&nbsp;<span class='mdi mdi-record' style='color:#fd2502;font-size: 16px;'></span>";
                }
            }
        }
        ]
   })
}

var arr=null;
function getMatrix() {
     arr = new Array();
    $.get("/reasonMartix/getMartix", {reasonId: $("#reasonId").val()}, function (r) {
        var thead = "<thead><tr><th></th>";
        var head = "<tr><td></td>";
        var body = "";
        var flag = false;

        var i=0;
        $.each(r.data, function(k1, v1) {
            var obj = new Object();
            obj.index = i;
            body += "<tr><td>" + v1[0]['fName'] + "</td>";
            $.each(v1, function (k2, v2) {
                if(!flag) {
                    head += "<td>" + v2['rfName']+ "</td>";
                    thead += "<th><input type='checkbox'/></th>";
                }
                var relate = v2.relateValue;
                if(relate == "-1") {
                    body += "<td>-</td>";
                }else {
                    body += "<td>" + relate + "</td>";
                }
            });
            obj.code = v1[0]['fCode'];
            flag = true;
            body += "</tr>";
            arr.push(obj);
            i++;
        });
        thead += "</tr></thead>";
        head += "</tr>";
        $("#tableData").html("").html(thead + head + body);
    });
}

function submitData() {
    //开启遮罩层
    lightyear.loading('show');

    var tmp = new Array();
    $("#tableData").find("tr:eq(0)").find("th").find("input[type='checkbox']").each(function (k, v) {
        if($(this).is(':checked')) {
            tmp.push(arr[k]['code']);
        }
    });
    var code = tmp.join(",");

    //提交后端进行校验
    $.get("/reason/validateRelateKpi", {reasonId: $("#reasonId").val(), code: code}, function (resp){
        if (resp.code == 200) {
            var data=resp.data;

            if(data.length==0)  //校验通过 提交后端进行计算
            {
                $.get("/reason/getEffectForecast", {reasonId: $("#reasonId").val(), code: code}, function (resp){
                    if(resp.code==200)
                    {
                        //关闭遮罩层
                        lightyear.loading('hide');
                        //弹框显示表格
                        $("#reasonResult_modal").modal("show");
                        createResultTableHeader('reasonResultTable');
                        $('#reasonResultTable').bootstrapTable('load', resp.data);
                    }else{
                        //关闭遮罩层
                        lightyear.loading('hide');
                        toastr.error('服务异常，快反馈给系统运维人员吧！');
                    }
                });

            }else  //校验不通过，进行提示
            {
                //显示提示信息
                var alert_val='';
                $.each(data,function (index,value) {
                    alert_val+='</br>'+value+"!";
                })

                //关闭遮罩层
                lightyear.loading('hide');
                toastr.warning(alert_val);
            }
        }
    });
}

/**
 * 将原因结果取消追踪
 */
function  deleteTrace(reasonId,reasonResultId) {
    //遮罩层打开
    lightyear.loading('show');

    //进行删除提示
    $.confirm({
        title: '确认',
        content: '确定要取消结果跟踪？',
        theme: 'bootstrap',
        type: 'orange',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'btn-blue',
                action: function(){
                    $.getJSON("/reason/deleteReasonResultToTrace?reasonResultId="+reasonResultId+"&reasonId="+reasonId,function (resp) {
                        if (resp.code === 200){
                            //刷新表格
                            $.get("/reason/getEffectForecast", {reasonId: $("#reasonId").val(), code: ''}, function (resp){
                                if(resp.code==200)
                                {
                                    $('#reasonResultTable').bootstrapTable('load', resp.data);
                                    //提示成功
                                    toastr.success('取消成功!');
                                }else{
                                    toastr.error('服务异常，快反馈给系统运维人员吧！');
                                }
                            });
                            lightyear.loading('hide');
                        }
                    })
                }
            },
            cancel: {
                text: '取消',
                action: function () {
                    lightyear.loading('hide');
                }
            }
        }
    });
}

/**
 * 将原因结果加入跟踪列表
 */
function  trace(reasonId,reasonResultId) {
    //遮罩层打开
    lightyear.loading('show');

    //进行删除提示
    $.confirm({
        title: '确认',
        content: '确定要进行结果跟踪？',
        theme: 'bootstrap',
        type: 'orange',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'btn-blue',
                action: function(){
                    $.getJSON("/reason/addReasonResultToTrace?reasonResultId="+reasonResultId+"&reasonId="+reasonId,function (resp) {
                        if (resp.code === 200){
                            //刷新表格
                            $.get("/reason/getEffectForecast", {reasonId: $("#reasonId").val(), code: ''}, function (resp){
                                if(resp.code==200)
                                {
                                    $('#reasonResultTable').bootstrapTable('load', resp.data);
                                    //提示成功
                                    toastr.success('加入成功，请稍后查看跟踪结果!');
                                }else{
                                    toastr.error('服务异常，快反馈给系统运维人员吧！');
                                }
                            });
                            lightyear.loading('hide');
                        }
                    })
                }
            },
            cancel: {
                text: '取消',
                action: function () {
                    lightyear.loading('hide');
                }
            }
        }
    });
}

function createResultTableHeader(tableName)
{
    $("#"+tableName).bootstrapTable({
        dataType: "json",
        showHeader:true,
        columns: [{
            field: 'formulaDesc',
            title: '变量名称',
            align: 'left'
        },{
            field: 'formula',
            title: '回归公式',
            align: 'left'
        }, {
            field: 'business',
            title: '业务解释',
            align: 'left'
        }, {
            field: 'createDt',
            title: '创建时间',
            align: 'left'
        },{
            filed: 'button',
            title: '操作',
            formatter: function (value, row, index) {
                var traceFlag=row.traceFlag;
                var reasonId=row.reasonId;

                if("Y"==traceFlag)
                {
                    return "<div class='btn btn-warning btn-sm' onclick='deleteTrace("+reasonId+","+row.reasonResultId+")'><i class='mdi mdi-monitor'></i>取消跟踪效果</div>";
                }else
                {
                    return "<div class='btn btn-info btn-sm' onclick='trace("+reasonId+","+row.reasonResultId+")'><i class='mdi mdi-monitor'></i>加入效果跟踪</div>";
                }
            }
        }
        ]
    })
}
