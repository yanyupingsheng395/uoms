$(function () {
    getPlanTable();
    getActivityName();
});

// 获取活动名称
function getActivityName() {
    $.get("/activity/getActivityName", {headId: headId}, function(r){
        $("#activityName").html(r.data);
    });
}

// 获取计划表数据
function getPlanTable() {
    var settings = {
        singleSelect: true,
        columns: [
            {
                checkbox: true
            },
            {
                field: 'planDateStr',
                title: '日期'
            }, {
                field: 'userCnt',
                title: '计划人数'
            }, {
                field: 'planStatus',
                title: '状态'
            }, {
                title: '操作'
            }]
    };
    $("#preheatPlanTable").bootstrapTable(settings);
    $("#formalPlanTable").bootstrapTable(settings);
    $.get("/activity/getPlanList", {headId: headId},function (r) {
        if(r.code === 200) {
            $("#preheatPlanTable").bootstrapTable('load', r.data['preheat']);
            $("#formalPlanTable").bootstrapTable('load', r.data['formal']);
        }else {
            $MB.n_danger("获取计划数据异常！");
        }
    });
}