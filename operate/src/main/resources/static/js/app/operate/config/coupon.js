$(function () {
    var settings = {
        url: "/coupon/list",
        method: 'post',
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,  //页码
                param: {smsCode: $("input[name='smsCode']").val()}
            };
        },
        columns: [{
            field: 'couponId',
            title: '优惠券编号'
        }, {
            field: 'couponDenom',
            title: '优惠券面额'
        }, {
            field: 'couponThreshold',
            title: '优惠券门槛'
        }, {
            field: 'couponName',
            title: '优惠券名称'
        }, {
            field: 'couponUrl',
            title: '优惠券领用地址',
            formatter: function (value, row, index) {
                return "<a href='" + value + "' style='color: #48b0f7;border-bottom: solid 1px #48b0f7'>" + value + "</a>";
            }
        }]
    };

    $MB.initTable('couponTable', settings);
    //为刷新按钮绑定事件
    $("#btn_refresh").on("click", function () {
        $('#couponTable').bootstrapTable('refresh');
    });
});

