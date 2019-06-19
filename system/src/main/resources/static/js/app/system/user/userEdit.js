function updateUser() {
    var selected = $("#userTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要修改的用户！');
        return;
    }
    if (selected_length > 1) {
        $MB.n_warning('一次只能修改一个用户！');
        return;
    }
    var userId = selected[0].userId;
    $.post(ctx + "user/getUser", {"userId": userId}, function (r) {
        if (r.code === 200) {
            var $form = $('#user-add');
            $form.modal();
            var user = r.msg;
            $form.find(".user_password").hide();
            $("#user-add-modal-title").html('修改用户');
            $form.find("input[name='username']").val(user.username).attr("readonly", true);
            $form.find("input[name='oldusername']").val(user.username);
            $form.find("input[name='userId']").val(user.userId);
            $form.find("input[name='email']").val(user.email);
            $form.find("input[name='mobile']").val(user.mobile);
            $form.find("input[name='expire']").val(user.expire);
            init_date("expire", "yyyy-mm-dd", 0, 2, 0, new Date(), "");
            var roleArr = [];
            for (var i = 0; i < user.roleIds.length; i++) {
                roleArr.push(user.roleIds[i]);
            }
            $form.find("select[name='rolesSelect']").selectpicker('val', roleArr);
            $form.find("input[name='roles']").val($form.find("select[name='rolesSelect']").selectpicker('val'));
            $("input[name='status']:radio[value='" + user.status + "']").prop("checked", true);
            $("input[name='ssex']:radio[value='" + user.ssex + "']").prop("checked", true);
            $("#user-add-button").attr("name", "update");
        } else {
            $MB.n_danger(r.msg);
        }
    });
}