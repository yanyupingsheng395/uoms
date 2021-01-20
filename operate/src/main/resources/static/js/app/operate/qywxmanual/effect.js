$(function () {
    getOverAllInfo();
});


// 获取页面头的当前日期和任务日期
function getOverAllInfo() {
    status=$('#status option:selected') .val();
    $.get("/qywxmanual/getOverAllInfo", {headId: headId,status:status}, function (r) {
        var data = r.data;
        if(r.code==200)
        {
            $("#totalNum").text(data.total_num);
            $("#pushSuccessCnt").text(data.push_success_cnt);
            $("#pushSuccessRate").text(data.push_success_rate);
            $("#covCnt").text(data.cov_cnt);
            $("#covRate").text(data.cov_rate);
            $("#covAmount").text(data.cov_amount);
        }else
        {
            $MB.n_warning(r.msg);
            return;
        }
    });
}
