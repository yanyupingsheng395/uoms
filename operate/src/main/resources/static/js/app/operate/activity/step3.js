getUserList();
function getUserList() {
    var settings = {
        url: '/daily/getUserEffect',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  //页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    headId: '34'
                }
            };
        },
        columns: [{
            field: 'userId',
            title: '用户ID',
        },{
            field: 'groupName',
            title: '所在群组'
        },{
            field: 'pathActiv',
            title: '活跃度',
            formatter: function (value, row, index) {
                var res = "";
                switch (value) {
                    case "UAC_01":
                        res = "高度活跃";
                        break;
                    case "UAC_02":
                        res = "中度活跃";
                        break;
                    case "UAC_03":
                        res = "流失预警";
                        break;
                    case "UAC_04":
                        res = "弱流失";
                        break;
                    case "UAC_05":
                        res = "强流失";
                        break;
                    case "UAC_06":
                        res = "沉睡";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        },{
            field: 'userValue',
            title: '价值',
            formatter: function (value, row, index) {
                var res = "";
                switch (value) {
                    case "ULC_01":
                        res = "重要";
                        break;
                    case "ULC_02":
                        res = "主要";
                        break;
                    case "ULC_03":
                        res = "普通";
                        break;
                    case "ULC_04":
                        res = "长尾";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        },{
            field: 'purchProductName',
            title: '推荐商品'
        },{
            field: 'piecePrice',
            title: '件单价（元）'
        },{
            field: 'couponDenom',
            title: '优惠券'
        }]
    };
    $MB.initTable('userTable', settings);
}

$("#btn_next_3").click(function () {

    loading2();
    setTimeout(function(){
        removeLoading('test');
        step4();
    },1000);

});

function step4() {
    $("#step1").attr("style", "display:none;");
    $("#step2_own").attr("style", "display:none;");
    $("#step2_plat").attr("style", "display:none;");
    $("#step3").attr("style", "display:none;");
    $("#step4").attr("style", "display:block;");

    step4Inint();
    step.setActive(3);
}