$(function () {
    var settings = {
        url: "/op/getOpDayList",
        method: 'post',
        cache: false,
        pagination: true,
     //   striped: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit )+ 1,  //页码
                param: {daywid:'20190401'}
            };
        },
        columns: [{
            field: 'HEAD_ID',
            title: '编号'
        }, {
            field: 'DESCR',
            title: '描述'
        }, {
            field: 'INSERT_DT',
            title: '写入时间'
        },  {
            field: 'TOUCH_DT',
            title: '应触达日期'
        }]
    };
    $('#opdayTable').bootstrapTable(settings);

    //为刷新按钮绑定事件
    $("#btn_refresh").on("click",function () {

    });
});


