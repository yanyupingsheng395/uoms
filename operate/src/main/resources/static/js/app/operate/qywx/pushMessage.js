var $testForm = $("#testPush");
var validator;
//提交数据
function pushMessage(){
    var msgType = $('input[name="msgType"]:checked').val();
    selectType(msgType);
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

    $.get("/qywxDaily/testQywxPush", {mpTitle: mpTitle, mpUrl:mpUrl,mediaId:mediaId,linkPicurl:linkPicurl,linkUrl:linkUrl,linkDesc:linkDesc,linkTitle:linkTitle,picUrl:picUrl,msgType:msgType,senderId:senderId,externalContact:externalContact,messageTest:messageTest}, function (r) {
        if(r.code==200){
            $MB.n_success("发送成功，请前往企业微信端查看！");
        }else if (r.code==500){
            $MB.n_danger(r.msg);
        }
    });
}
//更换消息类型
function selectType(type) {
    var icon = "<i class='fa fa-close'></i> ";
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
            }
        }
    } );


    if (type == "image") {
        $("#image").show();
        $("#web").hide();
        $("#applets").hide();
        //添加对图片的校验
        $("#picUrl").rules("add",{required:true,messages:{required:"请选择图片地址！"}});

        //清除对小程序和web的校验
        $("#mpTitle").rules("remove");
        $("#mpUrl").rules("remove");
        $("#mediaId").rules("remove");

        $("#linkTitle").rules("remove");
        $("#linkUrl").rules("remove");
    } else if (type == "web") {
        $("#image").hide();
        $("#web").show();
        $("#applets").hide();

        $("#linkTitle").rules("add",{required:true,messages:{required:"请填写网页标题！"}});
        $("#linkUrl").rules("add",{required:true,messages:{required:"请填写网页地址！"}});

        $("#mpTitle").rules("remove");
        $("#mpUrl").rules("remove");
        $("#mediaId").rules("remove");

        $("#picUrl").rules("remove");
    } else if (type == "applets") {
        $("#image").hide();
        $("#web").hide();
        $("#applets").show();
        //添加对小程序的校验
        $("#mpTitle").rules("add",{required:true,messages:{required:"请输入小程序标题！"}});
        $("#mpUrl").rules("add",{required:true,messages:{required:"请输入小程序地址！"}});
        $("#mediaId").rules("add",{required:true,messages:{required:"请选择小程序封面ID！"}});

        $("#linkTitle").rules("remove");
        $("#linkUrl").rules("remove");

        $("#picUrl").rules("remove");
    }
    validator.resetForm();
}