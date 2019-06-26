function updatePwd() {
    if(validateForm()) {
        $.get("/user/updatePassword", {newPassword: $("#pwd1").val()}, function (r) {
            if(r.code === 200) {
                $MB.n_success(r.msg);
                setTimeout(function () {
                    window.location.href = "/logout";
                }, 2000)
            }else {
                $MB.n_danger(r.msg);
                setTimeout(function () {
                    window.location.href = "/logout";
                }, 2000)
            }
        });
    }
}

function validateForm() {
    var pwd1 = $("#pwd1").val();
    var pwd2 = $("#pwd2").val();
    if(pwd1.trim() == "" || pwd2.trim() == "") {
        $MB.n_warning("请输入要更改的密码！")
        return false;
    }
    if(pwd1 != pwd2) {
        $MB.n_warning("两次输入的密码不一致，请重新输入！")
        return false;
    }
    return true;
}