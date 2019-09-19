/**
 * 启动群组推送
 */
function submitData() {
    $("#btn_push").attr("disabled", true);
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定启动推送群组?"
    }, function () {
        $.get("/daily/submitData", {headId: headId}, function (r) {
            if(r.code === 200) {
                $MB.n_success("启动推送成功！");
            }else {
                $("#btn_push").attr("disabled", false);
                $MB.n_warning("数据已被其它用户修改，请查看！")
            }
            setTimeout(function () {
                window.location.href = "/page/daily";
            }, 1000);
        });
    });
}

function step1() {
    step.setActive(0);
    $("#step1").attr("style", "display: block;");
    $("#step2").attr("style", "display: none;");
    $("#step3").attr("style", "display: none;");
}

function getUserStrategyList() {
    let settings = {
        url: '/daily/getUserStrategyList',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  //页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    headId: headId
                }
            };
        },
        columns: [[{
            field: 'userId',
            title: '用户ID',
            rowspan: 2,
            valign: "middle"
        }, {
            title: '当日成长策略',
            colspan: 4
        }, {
            title: '当日触达动作',
            colspan: 3
        }], [
            {
                field: 'tarOrderPrice',
                title: '目标订单价（元/单）'
            }, {
                field: 'recProdName',
                title: '目标商品'
            }, {
                field: 'tarProductNum',
                title: '建议补贴面额（元）'
            }, {
                field: 'tarProductNum',
                title: '建议触达时段（时）'
            },{
                field: 'tarProductNum',
                title: '优惠门槛（元）'
            },{
                field: 'couponDeno',
                title: '优惠面额（元）'
            },{
                field: 'tarProductNum',
                title: '短信文案'
            }
        ]]
    };
    $('#userStrategyListTable').bootstrapTable('destroy');
    $MB.initTable('userStrategyListTable', settings);
}