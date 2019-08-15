let fromVal = 0;
var step = steps({
    el: "#step",
    data: [
        { title: "选择活动类型", description: "" },
        { title: "设置活动时间", description: "" },
        { title: "成长用户分析", description: "" },
        { title: "成长用户策略", description: "" }
    ],
    space: 180,
    center: true,
    active: 0,
    dataOrder: ["line", "title", "description"]
});

function addData(actName, actType, startDt, endDt, dateRange) {
    $.get("/activity/addData", {actName:actName, actType: actType, startDt:startDt, endDt:endDt, dateRange: dateRange}, function (r) {
        if(r.code != 200) {
            $MB.n_danger("保存失败，未知异常！");
        }
    });
}