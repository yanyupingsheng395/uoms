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
           $("#period").val(msg.PERIOD_TYPE);
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

    //btn_concern 绑定事件  暂时注释
    // $('#btn_concern').on('click',function () {
    //      //获取关注列表
    //     $.getJSON("/reason/getConcernReasonKpis?reasonId="+reasonId, function (resp) {
    //         if (resp.code==200){
    //             createConcernTableHeader('concernTable');
    //             $('#concernTable').bootstrapTable('load', resp.msg);
    //             //打开模态对话框
    //             $("#concern_modal").modal("show");
    //         }
    //     })
    // });

    //关闭遮罩层
    lightyear.loading('hide');
});

function createTableHeader(tableName)
{
    $("#"+tableName).bootstrapTable({
        dataType: "json",
        showHeader:true,
        columns: [{
            field: 'REASON_KPI_NAME',
            title: '指标',
            align: 'left'
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

// function createConcernTableHeader(tableName)
// {
//     $("#"+tableName).bootstrapTable({
//         dataType: "json",
//         showHeader:true,
//         columns: [{
//             field: 'TEMPLATE_NAME',
//             title: '模板名称',
//             align: 'center'
//         },{
//             field: 'REASON_KPI_TYPE',
//             title: '指标分类',
//             align: 'center'
//         }, {
//             field: 'REASON_KPI_NAME',
//             title: '指标',
//             align: 'center'
//         }, {
//             field: 'RELATE_VALUE',
//             title: '相关性',
//             align: 'center',
//             formatter:function(value,row,index){
//                 if (value>0.5){
//                     return value+"&nbsp;<span class='mdi mdi-record' style='color:#02fd4f;font-size: 16px;'></span>";
//                 }else{
//                     return value+"&nbsp;<span class='mdi mdi-record' style='color:#fd2502;font-size: 16px;'></span>";
//                 }
//             }
//         }, {
//             field: 'REGRESSION_VALUE',
//             title: '回归系数',
//             align: 'center'
//         }
//         ]
//     })
// }

/**
 * 处理 关注 取消关注的事件
 */
function processConcern(data,index) {

    //开启遮罩层
    lightyear.loading('show');

    var reasonKpiCode=data.REASON_KPI_CODE;
    var templateCode=data.TEMPLATE_CODE;
    var reasonId = $("#reasonId").val();
    $.get("/reason/addConcernKpi?reasonId="+reasonId+"&templateCode="+templateCode+"&reasonKpiCode="+reasonKpiCode, null, function (r) {
       //刷新当前指标列表页的数据

        $.getJSON("/reason/getReasonKpisSnp?reasonId="+reasonId+"&templateCode="+templateCode, function (resp) {
            if (resp.code==200){
                var tableName=templateCode+'Table';
                createTableHeader(tableName);
                $('#'+tableName).bootstrapTable('load', resp.msg);
            }

            lightyear.loading('hide');
        })
    });
}

var arr = null;
// 因子表格
getMatrix();
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
                    body += "<td></td>";
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
    var tmp = new Array();
    $("#tableData").find("tr:eq(0)").find("th").find("input[type='checkbox']").each(function (k, v) {
        if($(this).is(':checked')) {
            tmp.push(arr[k]['code']);
        }
    });
    var code = tmp.join(",");
    $.get("/reason/getEffectForecast", {reasonId: $("#reasonId").val(), code: code}, function (r){
        console.log(r);
    });
}
