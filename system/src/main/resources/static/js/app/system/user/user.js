$.validator.addMethod("passwordValidate",function(value,element,param){
    var name = $(this).attr("name");

    if(name=='insert'&&value.length==0){
        return false;
    }else
    {
        return true;
    }
},"密码不能为空!");

$(function () {
    var $userTableForm = $("#user-form");
    var settings = {
        url: "/user/list",
        pageSize: 10,
        singleSelect: true,
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: params.offset / params.limit + 1,
                username: $userTableForm.find("input[name='qusername']").val().trim(),
                status: $userTableForm.find("select[name='qstatus']").val()
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'userId',
            visible: false
        }, {
            field: 'username',
            title: '用户名'
        }, {
            field: 'email',
            title: '邮箱'
        }, {
            field: 'mobile',
            title: '手机'
        },{
            field: 'status',
            title: '状态',
            formatter: function (value, row, index) {
                if (value === '1') return '<span class="badge bg-success">启用</span>';
                if (value === '0') return '<span class="badge bg-warning">禁用</span>';
            }
        },
        {
            field: 'expireDate',
            title: '失效日期'
        },{
            field: 'userType',
            title: '登录类型',
            formatter: function (value, row, index) {
                if (value === 'PASS') return '<span class="badge bg-success">密码</span>';
                if (value === 'QYWX') return '<span class="badge bg-warning">企微扫码</span>';
                if (value === 'QYWX') return '<span class="badge bg-warning">密码 | 企微扫码</span>';
            }
        }, {
                field: 'createDt',
                title: '创建时间'
            }
            ,{
                field: 'updateDt',
                title: '更新时间'
            }
        ]
    };
    $MB.initTable('userTable', settings);
});

function searchUser() {
    $MB.refreshTable('userTable');
}

function refreshUser() {
    $("input[name='qusername']").val("");
    $("select[name='qstatus']").find("option:selected").removeAttr("selected");
    $MB.refreshTable('userTable');
}

var userName = $("#loginUser").text();
function deleteUsers() {
    var selected = $("#userTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    var contain = false;
    if (!selected_length) {
        $MB.n_warning('请勾选需要删除的用户！');
        return;
    }
    var ids = "";
    for (var i = 0; i < selected_length; i++) {
        ids += selected[i].userId;
        if (i !== (selected_length - 1)) ids += ",";
        if (userName === selected[i].username) contain = true;
    }
    if (contain) {
        $MB.n_warning('勾选用户中包含当前登录用户，无法删除！');
        return;
    }

    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定删除选中用户?"
    }, function () {
        $.post(ctx + 'user/delete', {"ids": ids}, function (r) {
            if (r.code === 200) {
                $MB.n_success(r.msg);
                refreshUser();
            } else {
                $MB.n_danger(r.msg);
            }
        });
    });
}

function restPassword() {
    var userId = $("#userTable").bootstrapTable('getSelections')[0].userId;
    var uName = $("#userTable").bootstrapTable('getSelections')[0].username;

    if (null==userId||""==userId) {
        $MB.n_warning('请勾选需要重置密码的用户！');
        return;
    }

    if (userName === uName) {
        $MB.n_warning('勾选用户中包含当前登录用户，无法重置密码！');
        return;
    }

    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定要重置选中用户的密码么?"
    }, function () {
        $.post(ctx + 'user/resetPassword', {"userId": userId}, function (r) {
            if (r.code === 200) {
                $MB.n_success(r.msg);
                refreshUser();
            } else {
                $MB.n_danger(r.msg);
            }
        });
    });
}