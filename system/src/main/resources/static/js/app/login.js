$(function(){
    $("#loginButton").on('click',function (event) {
        login();
    });

    /**
     * 为标签页切换增加事件
     */
    $("a[data-toggle='tab']").on('shown.bs.tab', function (e) {
        // 获取已激活的标签页的名称
        var activeTab = $(e.target).attr("href");
        if(activeTab=='#qywxLogin')
        {
            //企业微信方式登录 获取企业微信的相关参数
            $.getJSON("/qw/getLoginParam", function (resp) {
                if (resp.code==200){
                    console.log(resp.data);
                    //调用企业微信方法
                    window.WwLogin({
                        "id" : "qywxQrCode",
                        "appid" : resp.data.corpId,
                        "agentid" : resp.data.agentId,
                        "redirect_uri" : resp.data.redirectUrl,
                        "state" : resp.data.random,
                        "href" : "",
                    });
                }else
                {
                    $('#qrMsg').text(resp.msg);
                }
            })
        }
    });

});

document.onkeyup = function (e) {
    if (window.event)
        e = window.event;
    var code = e.charCode || e.keyCode;
    if (code === 13) {
        login();
    }
};

function login()
{
    var input = $('.validate-input .input100');
        var check = true;

        for(var i=0; i<input.length; i++) {
            if(validate(input[i]) == false){
                showValidate(input[i]);
                check=false;
            }
        }

        if(check)
        {
            var $loginButton = $("#loginButton");
            var username = $(".validate-form input[name='username']").val().trim();
            var password = $(".validate-form input[name='password']").val().trim();
            var code = $(".validate-form input[name='code']").val().trim();

            $loginButton.html("").append("<i class=\"fa fa-spinner fa-pulse\"></i>&nbsp;登录中");
            $loginButton.attr("disabled", true);
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
                        if("Y"===r.msg)
                        {
                            location.href = '/resetPass';
                        }else
                        {
                            location.href = '/index';
                        }

                    } else {
                        reloadCode();
                        $(".validate-form input[name='password']").val("");
                        $(".validate-form input[name='code']").val("");
                        //提示
                        $MB.n_danger(r.msg);
                        $loginButton.html("登录");
                        $loginButton.attr("disabled", false);
                    }
                }
            });
        }else
        {
            return false;
        }
}



function reloadCode() {
    $("#validateCodeImg").attr("src", "/gifCode?data=" + new Date() + "");
}

$('.input100').each(function(){
    $(this).on('blur', function(){
        if($(this).val().trim() != "") {
            $(this).addClass('has-val');
        }
        else {
            $(this).removeClass('has-val');
        }
    })
})

$('.validate-form .input100').each(function(){
    $(this).focus(function(){
        hideValidate(this);
    });
});

function validate (input) {
    if($(input).attr('type') == 'email' || $(input).attr('name') == 'email') {
        if($(input).val().trim().match(/^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{1,5}|[0-9]{1,3})(\]?)$/) == null) {
            return false;
        }
    }
    else {
        if($(input).val().trim() == ''){
            return false;
        }
    }
}

function showValidate(input) {
    var thisAlert = $(input).parent();

    $(thisAlert).addClass('alert-validate');
}

function hideValidate(input) {
    var thisAlert = $(input).parent();

    $(thisAlert).removeClass('alert-validate');
}
