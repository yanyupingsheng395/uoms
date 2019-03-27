$(function () {
    var settings = {
        url: "/gmvplan/list",
        method: 'post',
        cache: false,
        pagination: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: params.offset / params.limit + 1
            };
        },
        columns: [{
            field: 'yearId',
            title: '年份'
        }, {
            field: 'gmvTarget',
            title: 'GMV目标值'
        }, {
            field: 'targetRate',
            title: '相比上年增长率',
            formatter: function (value, row, index) {
                return value == null ? "" : value + "%"
            }
        }, {
            // D表示草稿  C表示更新数据中 E表示已下达执行
            field: 'status',
            title: '状态',
            formatter: function (value, row, index) {
                if (value == "D") {
                    return "<span class='label label-info'>草稿</span>";
                }else if (value == "C") {
                    return "<span class='label label-primary'>更新数据中</span>";
                }else if (value == "E") {
                    return "<span class='label label-warning'>执行</span>";
                }
            }
        }, {
            filed: 'button',
            title: '操作',
            formatter: function (value, row, index) {
                if (row.status == "D") {
                    return "<div class='btn btn-success' onclick='excute()'>执行</div>&nbsp;<div class='btn btn-warning' onclick='modify()'>修改</div>&nbsp;<div class='btn btn-danger' onclick='del()'>删除</div>";
                }else if (row.status == "C") {
                    return "<div class='btn btn-primary' onclick='view()'>查看</div>&nbsp;<div class='btn btn-info' onclick='change()'>变更</div>";
                }
            }
        }]
    };
    $('#gmvPlanTable').bootstrapTable(settings);
});

// function excute() {
//     $MB.confirm({
//         text: "确定要执行此运营目标?执行状态的运营目标将会滚动计算，不允许再删除!",
//         confirmButtonText: "确定执行"
//     }, function() {
//         setTimeout(function () {
//             $MB.n_success("该运营目标执行成功！")
//         }, 1000)
//     });
// }
//
// function modify() {
//     $.post(ctx + "gmvplan/modify", function (r) {
//         if (r.code === 401) {
//             $MB.n_danger("登录已失效，您的账号已被踢出或已在别的地方登录，请重新登录。如果密码遭到泄露，请立即修改密码！");
//             setTimeout(function () {
//                 location.href = ctx + "login"
//             }, 4000);
//             return;
//         } else if (r.code === 500) {
//             $MB.n_danger(r.msg);
//             return;
//         }
//         clearInterval(rediskeysSizeInterval);
//         clearInterval(redisMemoryInfoInterval);
//         $main_content.html("").append(r);
//
//         var htmlCode = $breadcrumb.html();
//         $breadcrumb.html("").append(htmlCode + "<li class=\"breadcrumb-item\">&nbsp;&nbsp;修改目标</li>");
//     });
// }
// function del() {
//     $MB.confirm({
//         text: "确定要删除?",
//         confirmButtonText: "确定"
//     }, function() {
//         setTimeout(function () {
//             $MB.n_success("删除成功！")
//         }, 1000)
//     });
// }
// function change() {
//     $.post(ctx + "gmvplan/change", function (r) {
//         if (r.code === 401) {
//             $MB.n_danger("登录已失效，您的账号已被踢出或已在别的地方登录，请重新登录。如果密码遭到泄露，请立即修改密码！");
//             setTimeout(function () {
//                 location.href = ctx + "login"
//             }, 4000);
//             return;
//         } else if (r.code === 500) {
//             $MB.n_danger(r.msg);
//             return;
//         }
//         clearInterval(rediskeysSizeInterval);
//         clearInterval(redisMemoryInfoInterval);
//         $main_content.html("").append(r);
//
//         var htmlCode = $breadcrumb.html();
//         $breadcrumb.html("").append(htmlCode + "<li class=\"breadcrumb-item\">&nbsp;&nbsp;变更目标</li>");
//     });
// }
// function view() {
//     modify();
// }
