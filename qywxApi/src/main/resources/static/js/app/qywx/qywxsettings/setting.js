var $qywxsetting = $( "#qywx-setting-form" );
var qywx_validator;
var $qywxcontact = $( "#qywx-contact-form" );
var qywx_contact_validator;
String.prototype.endWith = function (str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length)
        return false;
    if (this.substring(this.length - str.length) == str)
        return true;
    else
        return false;
    return true;
};
$(function () {
    validBasic();
    validQywxContact();
    getQywxParam();
});

/**
 * 获取企业微信应用设置中的公司ID和应用秘钥。
 */
function getQywxParam(){
    $.get("/qywx/getQywxParam",function (r) {
        if(r.code === 200) {
            if(r.msg.msg=='Y'){
                $("#corpId").val(r.msg.corpId);
                $("#applicationSecret").val(r.msg.secret);
            }else{
                $MB.n_danger("未获取到企业微信基本信息！");
            }
        }else {
            $MB.n_danger("获取数据异常！");
        }
    });
}

/**
 * 更新企业微信应用配置
 */
function updateWxSetting() {
    var validator = $qywxsetting.validate();
    if(validator.form()){
        var corpId=$("#corpId").val().trim();
        var secret=$("#applicationSecret").val().trim();
        var data ="corpId="+corpId+"&secret="+secret;
        $.post( "/qywx/updateCorpInfo", data, function (r) {
            if (r.code === 200) {
                $MB.n_success("更新成功！");
            } else {
                $MB.n_danger( "更新失败" );
            }
        } );
    }
}

function validBasic() {
    var icon = "<i class='fa fa-close'></i> ";
    qywx_contact_validator = $qywxsetting.validate( {
        rules: {
            corpId: {
                required: true
            },
            applicationSecret: {
                required: true
            }
        },
        errorPlacement: function (error, element) {
            if (element.is( ":checkbox" ) || element.is( ":radio" )) {
                error.appendTo( element.parent().parent() );
            } else {
                error.insertAfter( element );
            }
        },
        messages: {
            corpId: {
                required: icon + "请输入企业微信ID"
            },
            applicationSecret: {
                required: icon + "请输入应用秘钥"
            }
        }
    } );
}

function validQywxContact() {
    var icon = "<i class='fa fa-close'></i> ";
    qywx_validator = $qywxcontact.validate( {
        rules: {
            eventToken: {
                required: true
            },
            eventAesKey: {
                required: true
            }
        },
        errorPlacement: function (error, element) {
            if (element.is( ":checkbox" ) || element.is( ":radio" )) {
                error.appendTo( element.parent().parent() );
            } else {
                error.insertAfter( element );
            }
        },
        messages: {
            eventToken: {
                required: icon + "请输入企业微信token"
            },
            eventAesKey: {
                required: icon + "请输入企业微信aesKey"
            }
        }
    } );
}

/**
 * 获取外部联系人信息
 */
function getContact() {
    $.get("/qywx/getContact",function (r) {
        if(r.code === 200) {
            $("#eventUrl").val(r.msg.eventUrl);
            $("#eventToken").val(r.msg.eventToken);
            $("#eventAesKey").val(r.msg.eventAesKey);
        }else {
            $MB.n_danger("获取数据异常！");
        }
    });
}

/**
 * 更新外部联系人信息
 */
function updateContact() {
    var validator = $qywxcontact.validate();
   if(validator.form()){
       var eventToken= $("#eventToken").val();
       var eventAesKey= $("#eventAesKey").val();
       var data ="eventToken="+eventToken+"&eventAesKey="+eventAesKey;
       $.post( "/qywx/updateContact", data, function (r) {
           if (r.code === 200) {
               $MB.n_success("更新成功！");
           } else {
               $MB.n_danger( "更新失败" );
           }
       } );
   }
}


$("#navTabs1").find('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
    var startDt = $("#startDt").val();
    if (startDt == "") {
        $MB.n_warning("请选择时间！");
    } else {
        if (e.target.href.endWith("#qywxSetting")) {
            getQywxParam();
        }else{
            getContact();
        }
    }
});