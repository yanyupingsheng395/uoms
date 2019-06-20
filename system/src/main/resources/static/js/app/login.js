var layer;
$(document).ready(function () {
    layui.use(['layer'], function(){
        layer = layui.layer;});
});

function login() {
    var $loginButton = $("#loginButton");
    var username = $(".one input[name='username']").val().trim();
    var password = $(".one input[name='password']").val().trim();
    var code = $(".one input[name='code']").val().trim();
    if (username === "") {
        layer.msg("请输入用户名！");
        return;
    }
    if (password === "") {
        layer.msg("请输入密码！");
        return;
    }
    if (code === "") {
        layer.msg("请输入验证码！");
        return;
    }
    $loginButton.html("").append("<div class='login-loder'><div class='line-scale'><div></div><div></div><div></div><div></div><div></div></div></div>");
    $.ajax({
        type: "post",
        url: "/login",
        data: {
            "username": username,
            "password": password,
            "code": code
        },
        dataType: "json",
        success: function (r) {
            if (r.code === 200) {
                location.href = '/main';
            } else {
                reloadCode();
                layer.msg(r.msg);
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
function reloadCode() {
    $("#validateCodeImg").attr("src", "/gifCode?data=" + new Date() + "");
}