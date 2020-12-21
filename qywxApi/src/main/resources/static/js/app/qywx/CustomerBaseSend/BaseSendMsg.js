$( function () {
});

function showSendTime() {
    $("#sendtime").show();
}
function closeSendTime() {
    $("#sendtime").hide();
    $("#sendDT").val("");
}

layui.use('laydate', function(){
    var laydate = layui.laydate;
    //日期时间选择器
    laydate.render({
        elem: '#test5'
        ,type: 'datetime'
    });
});

function smsContentValid() {
    $('#smsContentInput').val($('#smsContent').val());
    if($('#smsContentInput').val() !== '') {
        $('#smsContentInput').removeClass('error');
        $("#smsContentInput-error").remove();
    }
    var content = $('#smsContent').val() === "" ? "请输入短信内容": $('#smsContent').val();
    $("#article").html('').append(content);
}