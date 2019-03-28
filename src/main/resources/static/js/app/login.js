function login() {
    var $loginButton = $("#loginButton");
    var username = $(".one input[name='username']").val().trim();
    var password = $(".one input[name='password']").val().trim();
    if (username === "") {
      //  tips("请输入用户名！");
        return;
    }
    if (password === "") {
      //  tips("请输入密码！");
        return;
    }
    $loginButton.html("").append("<div class='login-loder'><div class='line-scale'><div></div><div></div><div></div><div></div><div></div></div></div>");
    $.ajax({
        type: "post",
        url: "/login",
        data: {
            "username": username,
            "password": password
        },
        dataType: "json",
        success: function (r) {
            if (r.code === 0) {
                location.href = '/index';
            } else {
             //   tips(r.msg);
                $loginButton.html("登录");
            }
        }
    });
}

document.onkeyup = function (e) {
    if (window.event)
        e = window.event;
    var code = e.charCode || e.keyCode;
    if (code === 13) {
        login();
    }
};