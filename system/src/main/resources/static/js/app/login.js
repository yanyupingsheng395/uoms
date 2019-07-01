$(function(){
    $("#loginButton").on('click',function (event) {
        login();
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
                            location.href = '/main';
                        }

                    } else {
                        reloadCode();
                        $(".validate-form input[name='username']").val("");
                        $(".validate-form input[name='password']").val("");
                        $(".validate-form input[name='code']").val("");
                        //提示
                        login_notify(r.msg);
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

function login_notify(message)
{
    $.notify({
        icon: "fa fa-warning",
        title: "",
        message: message,
        url: ''
    }, {
        element: 'body',
        type: "warning",
        allow_dismiss: true,
        placement: {
            from: "top",
            align: "center"
        },
        offset: {
            x: 20,
            y: 20
        },
        spacing: 10,
        z_index: 3001,
        delay: 2500,
        timer: 1000,
        url_target: '_blank',
        mouse_over: false,
        animate: {
            enter: "animated fadeInDown",
            exit: "animated fadeOutUp"
        },
        template: '<div data-notify="container" class="alert alert-dismissible alert-{0} alert--notify" role="alert">' +
            '<span data-notify="icon"></span> ' +
            '<span data-notify="title">{1}</span> ' +
            '<span data-notify="message" style="font-weight: 600">{2}</span>' +
            '<div class="progress" data-notify="progressbar">' +
            '<div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0;"></div>' +
            '</div>' +
            '<a href="{3}" target="{4}" data-notify="url"></a>' +
            '<button type="button" aria-hidden="true" data-notify="dismiss" class="alert--notify__close"><span style="background-color: rgba(255,255,255,.2);line-height: 19px;height: 20px;width: 20px;border-radius: 50%;font-size: 1.1rem;display: block;" aria-hidden="true">×</span></button>' +
            '</div>'
    });
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
