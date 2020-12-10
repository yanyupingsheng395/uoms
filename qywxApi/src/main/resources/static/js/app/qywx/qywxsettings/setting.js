var $qywxsetting = $( "#qywx-setting-form" );
var $qywxcontact = $( "#qywx-contact-form" );
var $qywxappid = $( "#qywx-apples-form" );
var qywx_contact_validator;
var qywx_validator;
var qywx_mp_appid;
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
    validQywxAppId();
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
                $("#agentId").val(r.msg.agentId);
            }else{
                $MB.n_danger("尚未配置企业微信基本信息！");
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
        var agentId=$("#agentId").val().trim();
        var data ="corpId="+corpId+"&secret="+secret+"&agentId="+agentId;
        $.post( "/qywx/updateCorpInfo", data, function (r) {
            if (r.code === 200) {
                $MB.n_success("更新成功！");
            } else {
                $MB.n_danger( "更新失败" );
            }
        } );
    }
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

/**
 * 获取小程序ID
 */
function getAppID(){
    $.get("/qywx/getAppID",function (r) {
        if(r.code === 200) {
            $("#mpappid").val(r.msg.appId);
        }else {
            $MB.n_danger("获取数据异常！");
        }
    });
}

/**
 * 更新小程序ID
 */
function setMpAppId(){
    var validator = $qywxappid.validate();
    if(validator.form()){
        var mpappid= $("#mpappid").val();
        var data ="mpappid="+mpappid;
        $.post( "/qywx/setMpAppId", data, function (r) {
            if (r.code === 200) {
                $MB.n_success("更新成功！");
            } else {
                $MB.n_danger( "更新失败" );
            }
        } );
    }
}

/**
 * 获取是否开启欢迎语
 */
function getEnableWel(){
    $.get("/qywx/getEnableWel",function (r) {
        if(r.code === 200) {
            if("N"== r.msg.status){
                $("#closeWel").attr("checked","checked");
                $('#openWel').removeAttr("checked");
            }else{
                $("#openWel").attr("checked","checked");
                $('#closeWel').removeAttr("checked");
            }
        }else {
            $MB.n_danger("获取数据异常！");
        }
    });
}

/**
 * 设置欢迎语状态
 */
function setEnableWelcome(){
    var status= $("input[name='isopenwel']:checked").val();
    var data ="status="+status;
    $.post( "/qywx/setEnableWelcome", data, function (r) {
        if (r.code === 200) {
            $MB.n_success("更新成功！");
        } else {
            $MB.n_danger( "更新失败" );
        }
    } );
}

/**
 *切换table栏，更换数据
 */
$("#navTabs1").find('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
    var startDt = $("#startDt").val();
    if (startDt == "") {
        $MB.n_warning("请选择时间！");
    } else {
        if (e.target.href.endWith("#qywxSetting")) {
            getQywxParam();
        }else if(e.target.href.endWith("#qywxContact")){
            getContact();
        }else if(e.target.href.endWith("#qywxAppletsId")){
            getAppID();
        }else if(e.target.href.endWith("#qywxWelcome")){
            getEnableWel();
        }else if(e.target.href.endWith("#checkFile")){
            getFileMessage();
        }
    }
});

/**
 * 判断企业微信应用配置栏中，内容是否未填全
 */
function validBasic() {
    var icon = "<i class='fa fa-close'></i> ";
    qywx_contact_validator = $qywxsetting.validate( {
        rules: {
            corpId: {
                required: true
            },
            applicationSecret: {
                required: true
            },
            agentId: {
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
            },
            agentId: {
                required: icon + "请输入应用agentID"
            }
        }
    } );
}

/**
 * 校验外部联系人内容
 */
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
 *小程序ID是否有空填
 */
function validQywxAppId() {
    var icon = "<i class='fa fa-close'></i> ";
    qywx_mp_appid = $qywxappid.validate( {
        rules: {
            mpappid: {
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
            mpappid: {
                required: icon + "请输入小程序ID"
            }
        }
    } );
}

/**
 * 获取校验文件内容和文件名称
 */
function getFileMessage() {
    $.get("/qywx/getFileMessage",function (r) {
        if(r.code === 200) {
            var data=r.data;
            if(data!=null&&!isEmpty(data.oauthFilename)&&!isEmpty(data.oauthFile)){
                $("#filename").val(data.oauthFilename);
                $('#content').val(data.oauthFile);
                $("#showfile").show();
            }
        }else {
            $MB.n_danger("获取数据异常！");
        }
    });
}

function isEmpty(obj){
    if(typeof obj == "undefined" || obj == null || obj == ""){
        return true;
    }else{
        return false;
    }
}

var filename;
var filetype;
// 文件上传按钮选择事件
$( "#uploadFile" ).change( function () {
    filename=$(this)[0].files[0].name;
    if(filename!=null&&filename!=""){
        filename=filename.substr(0, filename.indexOf("."));
    }
    $( '#filename1' ).text( "文件名:" + $(this)[0].files[0].name).attr( "style", "display:inline-block;" );
    $( "#btn_upload1" ).attr( "style", "display:inline-block;" );
} );

/**
 * 保存校验文件内容
 */
function uploadfile() {
    let file = $("#uploadFile")[0].files[0];
    let formData = new FormData();
    formData.append("file", file);
    formData.append("title", filename);
    $.ajax({
        url: "/qywx/saveFile",
        type: "post",
        data: formData,
        cache: false,
        processData: false,
        contentType: false,
        success: function (res) {
            if(res.code === 200) {
                $MB.n_success( "保存成功！" );
                //关闭弹出框
                $("#materialModal").modal('hide');
                getFileMessage();
            }else{
                $MB.n_danger(res.msg);
            }
        },
        error: function (err) {
            $MB.n_danger("解析文件失败！");
        }
    });
}