$(function () {
    var settings = {
        url: "/reason/getResultTracelist",
        method: 'post',
        singleSelect: true,
        cache: false,
        pagination: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit )+ 1  //页码
            };
        },
        columns: [{
            checkbox: true
        },{
            field: 'reasonName',
            title: '原因编号'
        } ,  {
            field: 'kpiName',
            title: '指标'
        },{
            field: 'periodName',
            title: '周期',
            formatter: function (value, row, index) {
               return row.beginDt+"至"+row.endDt;
            }
        },{
            field: 'formulaDesc',
            title: '业务解释'
        },{
            field: 'formula',
            title: '回归公式'
        },{
            field: 'createDt',
            title: '加入跟踪时间'
        }]
    };
    $MB.initTable('reasonResultTraceTable',settings);
});

/**
 * 将原因结果取消跟踪
 */
function  deleteTrace() {

    var selected = $("#reasonResultTraceTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要取消的原因效果！');
        return;
    }
    var reasonId = selected[0]["reasonId"];
    var reasonResultId = selected[0]["reasonResultId"];


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
                            $('#reasonResultTraceTable').bootstrapTable('refresh');
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

var trace_option= {
    tooltip: {
        trigger: 'axis'
    },
    xAxis: {
        type: 'category',
        data: [],
        name: '',
        nameTextStyle: 'oblique',
        boundaryGap: false
    },
    axisLabel:{
        interval: 0,
        rotate: 45
    },
    yAxis: [
        {
            name: 'gmv',
            type: 'value'
        },
        {
            name: '活动强度',
            type: 'value'
        }
    ],
    series: [{
        data: [50000,54000,53000,55000,56000],
        name: 'gmv',
        type: 'line',
        smooth: true
    },
        {
            data: [3,2,4,2,1],
            name: '活动强度',
            yAxisIndex:1,
            type: 'line',
            smooth: true
        }]
};

trace_option.xAxis.name="时间周期";
trace_option.xAxis.data=['2019-01','2019-02','2019-03','2019-04','2019-05'];

function viewTraceData(){
    var selected = $("#reasonResultTraceTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要查看的原因效果！');
        return;
    }
    var reasonId = selected[0]["reasonId"];
    var reasonResultId = selected[0]["reasonResultId"];

    //加载数据，弹开bootstrap窗口
    $('#reasonResultTrace_modal').modal('show');
    var traceChart = echarts.init(document.getElementById('trace_chart'), 'macarons');
    traceChart.setOption(trace_option);
}
