$(function () {
    var settings = {
        url: ctx + "gmvplan/list",
        pageSize: 10,
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
                    return "<span class='badge badge-info'>草稿</span>";
                }else if (value == "C") {
                    return "<span class='badge badge-warning'>更新数据中</span>";
                }else if (value == "E") {
                    return "<span class='badge badge-success'>执行</span>";
                }
            }
        }, {
            filed: 'button',
            title: '操作',
            formatter: function (value, row, index) {
                if (row.status == "D") {
                    return "<div class='btn btn-outline-success' onclick='excute()'>执行</div>&nbsp;<div class='btn btn-outline-warning' onclick='modify()'>修改</div>&nbsp;<div class='btn btn-outline-danger' onclick='del()'>删除</div>";
                }else if (row.status == "C") {
                    return "<div class='btn btn-outline-primary' onclick='view()'>查看</div>&nbsp;<div class='btn btn-outline-info' onclick='change()'>变更</div>";
                }
            }
        }]
    };
    $MB.initTable('gmvPlanTable', settings);
});

function deleteRoles() {
    var selected = $("#roleTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要删除的角色！');
        return;
    }
    var ids = "";
    for (var i = 0; i < selected_length; i++) {
        ids += selected[i].roleId;
        if (i !== (selected_length - 1)) ids += ",";
    }

    $MB.confirm({
        text: "删除选中角色将导致该角色对应账户失去相应的权限，确定删除？",
        confirmButtonText: "确定删除"
    }, function () {
        $.post(ctx + 'role/delete', {"ids": ids}, function (r) {
            if (r.code === 0) {
                $MB.n_success(r.msg);
                refresh();
            } else {
                $MB.n_danger(r.msg);
            }
        });
    });
}

function exportRoleExcel() {
    $.post(ctx + "role/excel", $(".role-table-form").serialize(), function (r) {
        if (r.code === 0) {
            window.location.href = "common/download?fileName=" + r.msg + "&delete=" + true;
        } else {
            $MB.n_warning(r.msg);
        }
    });
}

function exportRoleCsv() {
    $.post(ctx + "role/csv", $(".role-table-form").serialize(), function (r) {
        if (r.code === 0) {
            window.location.href = "common/download?fileName=" + r.msg + "&delete=" + true;
        } else {
            $MB.n_warning(r.msg);
        }
    });
}
function add() {
    $.post(ctx + "gmvplan/add", function (r) {
        if (r.code === 401) {
            $MB.n_danger("登录已失效，您的账号已被踢出或已在别的地方登录，请重新登录。如果密码遭到泄露，请立即修改密码！");
            setTimeout(function () {
                location.href = ctx + "login"
            }, 4000);
            return;
        } else if (r.code === 500) {
            $MB.n_danger(r.msg);
            return;
        }
        clearInterval(rediskeysSizeInterval);
        clearInterval(redisMemoryInfoInterval);
        $main_content.html("").append(r);

        var htmlCode = $breadcrumb.html();
        $breadcrumb.html("").append(htmlCode + "<li class=\"breadcrumb-item\">&nbsp;&nbsp;新增目标</li>");
    });
}

function excute() {
    $MB.confirm({
        text: "确定要执行此运营目标?执行状态的运营目标将会滚动计算，不允许再删除!",
        confirmButtonText: "确定执行"
    }, function() {
        setTimeout(function () {
            $MB.n_success("该运营目标执行成功！")
        }, 1000)
    });
}

function modify() {
    $.post(ctx + "gmvplan/modify", function (r) {
        if (r.code === 401) {
            $MB.n_danger("登录已失效，您的账号已被踢出或已在别的地方登录，请重新登录。如果密码遭到泄露，请立即修改密码！");
            setTimeout(function () {
                location.href = ctx + "login"
            }, 4000);
            return;
        } else if (r.code === 500) {
            $MB.n_danger(r.msg);
            return;
        }
        clearInterval(rediskeysSizeInterval);
        clearInterval(redisMemoryInfoInterval);
        $main_content.html("").append(r);

        var htmlCode = $breadcrumb.html();
        $breadcrumb.html("").append(htmlCode + "<li class=\"breadcrumb-item\">&nbsp;&nbsp;修改目标</li>");
    });
}
function del() {
    $MB.confirm({
        text: "确定要删除?",
        confirmButtonText: "确定"
    }, function() {
        setTimeout(function () {
            $MB.n_success("删除成功！")
        }, 1000)
    });
}
function change() {
    $.post(ctx + "gmvplan/change", function (r) {
        if (r.code === 401) {
            $MB.n_danger("登录已失效，您的账号已被踢出或已在别的地方登录，请重新登录。如果密码遭到泄露，请立即修改密码！");
            setTimeout(function () {
                location.href = ctx + "login"
            }, 4000);
            return;
        } else if (r.code === 500) {
            $MB.n_danger(r.msg);
            return;
        }
        clearInterval(rediskeysSizeInterval);
        clearInterval(redisMemoryInfoInterval);
        $main_content.html("").append(r);

        var htmlCode = $breadcrumb.html();
        $breadcrumb.html("").append(htmlCode + "<li class=\"breadcrumb-item\">&nbsp;&nbsp;变更目标</li>");
    });
}
function view() {
    modify();
}
