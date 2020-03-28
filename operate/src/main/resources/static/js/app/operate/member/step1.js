$(function () {
    getUserDataList();
});
function getUserDataList() {
    let settings = {
        url: '/member/getDetailListPage',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    headId: headId,
                    userValue: $("#userValue").find("option:selected").val(),
                    pathActive: $("#pathActive").find("option:selected").val(),
                    brandDeep: $("#brandDeep").find("option:selected").val(),
                    joinRate: $("#joinRate").find("option:selected").val()
                },
                sort: params.sort,      //排序列名
                sortOrder: params.order,
            };
        },
        columns: [[{
            field: 'userId',
            title: '用户ID',
            rowspan: 2,
            valign: "middle"
        }, {
            title: '会员日成长目标',
            colspan: 5
        }, {
            title: '当日用户状态',
            colspan: 2
        }], [
            {
                field: 'targetSpu',
                title: '目标SPU'
            }, {
                field: 'spuBuyTime',
                title: '目标SPU购买次序'
            },{
                field: 'targetProduct',
                title: '目标商品'
            }, {
                field: 'targetProductNum',
                title: '目标商品件数（件）'
            }, {
                field: 'growthDay',
                title: '距离成长节点（天）'
            }, {
                field: 'pathActive',
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
            }, {
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
            }
        ]]
    };
    $('#userListTable').bootstrapTable('destroy');
    $MB.initTable('userListTable', settings);
}

function step2() {
    step.setActive(1);
    $("#step1").attr("style", "display: none;");
    $("#step2").attr("style", "display: block;");
}

/**
 * 搜索群组内的用户
 */
$("#btn_search").click(function () {
    $MB.refreshTable('userListTable');
});

/**
 * 重置筛选条件
 */
function refreshSelectedUser() {
    $("#userValue").find("option:selected").removeAttr("selected");
    $("#pathActive").find("option:selected").removeAttr("selected");
    $("#brandDeep").find("option:selected").removeAttr("selected");
    $("#joinRate").find("option:selected").removeAttr("selected");
    $MB.refreshTable('userListTable');
}