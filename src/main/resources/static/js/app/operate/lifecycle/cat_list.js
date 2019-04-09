var selectId='';
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
    });

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

    $("#tabs").find("li").click(function() {
        $(this).addClass("active");
        $(this).siblings().removeClass("active");

        if($(this).index() == 0) {
            $("#tab_kpis").attr("style", "display:block");
            $("#tab_lifecycle").attr("style", "display:none");
        }else {
            $("#tab_kpis").attr("style", "display:none");
            $("#tab_lifecycle").attr("style", "display:block");
        }
    });

});