$( function () {
    initDt();
});

function showSendTime() {
    $("#sendtime").show();
}
function closeSendTime() {
    $("#sendtime").hide();
    $("#sendDT").val("");
}
// 初始化日期控件
function initDt() {
    var date = new Date();
    init_date( 'sendDT', 'yyyy-mm-dd  HH-mm-ss', 0, 2, 0 );
    $( "#sendDT" ).datepicker( 'setStartDate', date );
}