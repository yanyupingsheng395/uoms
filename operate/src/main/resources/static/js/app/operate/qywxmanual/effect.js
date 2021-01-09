$(function () {
    getOverAllInfo();
});


// 获取页面头的当前日期和任务日期
function getOverAllInfo() {
    status=$('#status option:selected') .val();
    $.get("/qywxmanual/getOverAllInfo", {headId: headId,status:status}, function (r) {
        var data = r.data;
        if(data!=null){
            $("#totalNum").text(data.totalNum);
            $("#pushSuccessCnt").text(data.pushSuccessCnt);
            $("#pushSuccessRate").text(data.pushSuccessRate);
            $("#covCnt").text(data.covCnt);
            $("#covRate").text(data.covRate);
            $("#covAmount").text(data.covAmount);
        }
    });
}
