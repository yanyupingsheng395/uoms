toastr.options = {
    "closeButton": true,
    "debug": false,
    "newestOnTop": true,
    "progressBar": true,
    "rtl": false,
    "positionClass": "toast-top-center",
    "preventDuplicates": true,
    "onclick": null,
    "showDuration": 300,
    "hideDuration": 1000,
    "timeOut": 1500,
    "extendedTimeOut": 1000,
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};

function login() {
    var $loginButton = $("#loginButton");
    var username = $(".one input[name='username']").val().trim();
    var password = $(".one input[name='password']").val().trim();
    var code = $(".one input[name='code']").val().trim();
    if (username === "") {
        toastr.warning("请输入用户名！");
        return;
    }
    if (password === "") {
        toastr.warning("请输入密码！");
        return;
    }
    if (code === "") {
        toastr.warning("请输入验证码！");
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
                location.href = '/page/index';
            } else {
                reloadCode();
                toastr.warning(r.msg);
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