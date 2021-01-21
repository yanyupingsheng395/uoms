var $testForm = $("#testPush");
var validator;
// 表单验证规则
function validQywxContact() {
    var icon = "<i class='fa fa-close'></i> ";
    var msgType = $('input[name="msgType"]:checked').val();
    if(msgType=="applets"){
        validator = $testForm.validate( {
            rules: {
                messageTest: {
                    required: true
                },
                senderId: {
                    required: true
                },
                externalContact: {
                    required: true
                },
                mpTitle: {
                    required: true
                },
                mpUrl: {
                    required: true
                },
                mediaId: {
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
                messageTest: {
                    required: icon + "请输入文本内容！"
                },
                senderId: {
                    required: icon + "请输入发送人ID！"
                },
                externalContact: {
                    required: icon + "请输入外部联系人ID！"
                },
                mpTitle: {
                    required: icon + "请输入小程序标题"
                },
                mpUrl: {
                    required: icon + "请输入小程序连接"
                },
                mediaId: {
                    required: icon + "请输入小程序封面ID"
                }
            }
        } );
    }else if(msgType=="image"){
        validator = $testForm.validate( {
            rules: {
                picUrl: {
                    required: true
                },
                messageTest: {
                    required: true
                },
                senderId: {
                    required: true
                },
                externalContact: {
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
                picUrl: {
                    required: icon + "请选择图片地址！"
                },
                messageTest: {
                    required: icon + "请输入文本内容！"
                },
                senderId: {
                    required: icon + "请输入发送人ID！"
                },
                externalContact: {
                    required: icon + "请输入外部联系人ID！"
                }
            }
        } );
    }else if(msgType=="web"){
        validator = $testForm.validate( {
            rules: {
                linkTitle: {
                    required: true
                },
                linkUrl: {
                    required: true
                },
                messageTest: {
                    required: true
                },
                senderId: {
                    required: true
                },
                externalContact: {
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
                linkTitle: {
                    required: icon + "请填写网页标题！"
                }, linkUrl: {
                    required: icon + "请填写网页地址！"
                },
                messageTest: {
                    required: icon + "请输入文本内容！"
                },
                senderId: {
                    required: icon + "请输入发送人ID！"
                },
                externalContact: {
                    required: icon + "请输入外部联系人ID！"
                }
            }
        } );
    }
}

function pushMessage(){
    validQywxContact();
    var validator =$testForm.validate();
    var flag = validator.form();
    if(!flag){return ;}
    var mpTitle=$("#mpTitle").val();
    var mpUrl=$("#mpUrl").val();
    var mediaId=$("#mediaId").val();

    var linkPicurl=$("#linkPicurl").val();
    var linkUrl=$("#linkUrl").val();
    var linkDesc=$("#linkDesc").val();
    var linkTitle=$("#linkTitle").val();

    var picUrl=$("#picUrl").val();

    var senderId=$("#senderId").val();
    var externalContact=$("#externalContact").val();
    var messageTest=$("#messageTest").val();
    var msgType = $('input[name="msgType"]:checked').val();
    $.get("/qywxDaily/testQywxPush", {mpTitle: mpTitle, mpUrl:mpUrl,mediaId:mediaId,linkPicurl:linkPicurl,linkUrl:linkUrl,linkDesc:linkDesc,linkTitle:linkTitle,picUrl:picUrl,msgType:msgType,senderId:senderId,externalContact:externalContact,messageTest:messageTest}, function (r) {
        if(r.code==200){
            $MB.n_success("发送成功，请前往企业微信端查看！");
        }else if (r.code==500){
            $MB.n_danger(r.msg);
        }
    });
}

function selectType(type) {
    if (type == "image") {
        $("#image").show();
        $("#webPage").hide();
        $("#applets").hide();
        $("#linkTitle").val("");
        $("#linkDesc").val("");
        $("#linkUrl").val("");
        $("#linkPicurl").val("");
        $("#mpTitle").val("");
        $("#mpUrl").val("");
        $("#mediaId").val("");
    } else if (type == "webPage") {
        $("#image").hide();
        $("#webPage").show();
        $("#applets").hide();
        $("#picUrl").val("");
        $("#mpTitle").val("");
        $("#mpUrl").val("");
        $("#mediaId").val("");
    } else if (type == "applets") {
        $("#image").hide();
        $("#webPage").hide();
        $("#applets").show();
        $("#linkTitle").val("");
        $("#linkDesc").val("");
        $("#linkUrl").val("");
        $("#linkPicurl").val("");
        $("#mpTitle").val("");
        $("#mpUrl").val("");
        $("#mediaId").val("");
    }
}