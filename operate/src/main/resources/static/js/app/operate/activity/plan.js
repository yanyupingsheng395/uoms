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
                field: 'planDateWid',
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
            if(r.data['preheat'] !== undefined || Object.keys(r.data).length === 0) {
                $("#preheatDiv").attr("style", "display:block");
            }
            $("#preheatPlanTable").bootstrapTable('load', r.data['preheat']);
            $("#formalPlanTable").bootstrapTable('load', r.data['formal']);
        }else {
            $MB.n_danger("获取计划数据异常！");
        }
    });
}

// 获取用户群组列表
function getUserGroupTable(stage) {
    var settings = {
        columns: [
            {
                field: 'groupName',
                title: '用户与商品关系',
                valign: "middle",
                align: 'center'
            }, {
                field: 'groupUserCnt',
                title: '人数（人）',
                valign: "middle",
                align: 'center'
            }, {
                field: 'inGrowthPath',
                title: '成长节点与活动期',
                valign: "middle",
                align: 'center'
            }, {
                field: 'growthUserCnt',
                title: '人数（人）',
                valign: "middle",
                align: 'center'
            }, {
                field: 'activeLevel',
                title: '活跃度',
                valign: "middle",
                align: 'center'
            }, {
                field: 'activeUserCnt',
                title: '人数（人）',
                valign: "middle",
                align: 'center'
            }, {
                field: 'smsTemplateContent',
                title: '选择模板',
                align: "center",
                formatter: function(value, row, index) {
                    if(row['groupName'] === '总计') {
                        return "-";
                    }
                    // 没有配置模板信息是图标，否则是短信内容的截取串
                    if(value === '' || value === null) {
                        return '<a onclick="getTemplateTable('+row.groupId+')" class="text-center" data-toggle="tooltip" data-html="true" data-original-title="尚未配置消息模板！" style="color:grey;"><i class="fa fa-envelope"></i></a>';
                    }else {
                        return '<a onclick="getTemplateTable('+row.groupId+')" class="text-center" data-toggle="tooltip" data-html="true" data-original-title="'+value+'" style="color: #409eff;"><i class="fa fa-envelope"></i></a>';
                    }
                }
            }]
    };
    $("#userGroupTable").bootstrapTable(settings);
    $.get("/activity/getActivityUserGroupList", {headId: $( "#headId" ).val(), stage: stage},function (r) {
        $("#userGroupTable").bootstrapTable('load', r);
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'groupName', rowspan: 4});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 4, field: 'groupName', rowspan: 4});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'groupUserCnt', rowspan: 4});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 4, field: 'groupUserCnt', rowspan: 4});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'inGrowthPath', rowspan: 3});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 4, field: 'inGrowthPath', rowspan: 3});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'growthUserCnt', rowspan: 3});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 4, field: 'growthUserCnt', rowspan: 3});
        $("a[data-toggle='tooltip']").tooltip();
    });
}