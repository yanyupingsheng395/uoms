$(function () {
    var settings = {
        url: "/reason/getResultTracelist",
        method: 'post',
        cache: false,
        pagination: true,
        sidePagination: "client",
        columns: [{
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
            field: 'createDt',
            title: '加入跟踪时间'
        },{
            filed: 'button',
            title: '操作',
            formatter: function (value, row, index) {
                var reasonId=row.reasonId;
                return "<div class='btn btn-info btn-sm' onclick='view("+row.reasonResultId+")'><i class='mdi mdi-redo'></i>查看数据</div><div class='btn btn-info btn-sm' onclick='updatedata("+row.reasonResultId+")'><i class='mdi mdi-redo'></i>取消跟踪</div>";
            }
        }]
    };

    $('#reasonResultTraceTable').bootstrapTable(settings);

});

function view(reasonId){

}

function del(reasonId) {

    //遮罩层打开
    lightyear.loading('show');

    //进行删除提示
        $.confirm({
            title: '确认',
            content: '是否删除数据？',
            theme: 'bootstrap',
            type: 'orange',
            buttons: {
                confirm: {
                    text: '确认',
                    btnClass: 'btn-blue',
                    action: function(){
                             $.getJSON("/reason/deleteReasonById?reasonId="+reasonId,function (resp) {
                                    if (resp.code === 200){
                                        lightyear.loading('hide');
                                        //提示成功
                                        toastr.success('删除成功!');
                                        //刷新表格
                                        //todo 如果在后面某个页上删除数据后，刷新后还停在当前页
                                        //var pageNum=$('#reasonTable').bootstrapTable('getOptions').pageNumber;
                                        $('#reasonTable').bootstrapTable('refresh');
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