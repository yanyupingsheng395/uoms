var $testForm = $("#testPush");
var validator;
$(function () {
    validateRule();
});

// 表单验证规则
function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $testForm.validate({
        rules: {
            title: {
                required: true
            },
            pathAddress : {
                required: true
            },
            senderId: {
                required: true
            },
            externalContact: {
                required: true
            },
            messageTest: {
                required: true
            }
        },
        errorPlacement: function (error, element) {
            if (element.is(":checkbox") || element.is(":radio")) {
                error.appendTo(element.parent().parent());
            } else {
                error.insertAfter(element);
            }
        },
        messages: {
            title: icon + "请输入标题名称",
            pathAddress: icon + "请输入路径地址",
            senderId: icon+"请输入发送人ID",
            externalContact: icon + "请输入外部联系人ID",
            messageTest: icon + "请输入测试文本",
        }
    });
}
function pushMessage(){
    var validator =$testForm.validate();
    var flag = validator.form();
    if(!flag){return ;}
    var title=$("#title").val();
    var pathAddress=$("#pathAddress").val();
    var senderId=$("#senderId").val();
    var externalContact=$("#externalContact").val();
    var messageTest=$("#messageTest").val();
    $.get("/qywxDaily/testQywxPush", {title: title, pathAddress:pathAddress,senderId:senderId,externalContact:externalContact,messageTest:messageTest}, function (r) {
        var data=r.msg;
        var resultmsg=JSON.parse(data);
        if(resultmsg.code==200){
            $MB.n_success("发送成功，请前往企业微信端查看！");
        }else if (resultmsg.code==500){
            $MB.n_danger(resultmsg.msg);
        }
    });
}