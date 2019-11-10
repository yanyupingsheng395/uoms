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
                field: 'planDateWid',
                title: '日期',
                align: 'center',
                valign: 'middle'
            }, {
                field: 'userCnt',
                title: '计划人数',
                align: 'center',
                valign: 'middle'
            }, {
                field: 'planStatus',
                title: '状态',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row, index) {
                    let res = "";
                    switch (value) {
                        case "0":
                            res = "<span class=\"badge bg-primary\">尚未计算</span>";
                            break;
                        case "1":
                            res = "<span class=\"badge bg-success\">待执行</span>";
                            break;
                        case "2":
                            res = "<span class=\"badge bg-warning\">执行中</span>";
                            break;
                        case "3":
                            res = "<span class=\"badge bg-info\">执行完</span>";
                            break;
                        case "4":
                            res = "<span class=\"badge bg-danger\">已停止</span>";
                            break;

                    }
                    return res;
                }
            }, {
                title: '操作',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row, index) {
                    let res = "-";
                    let status = row['planStatus'];
                    switch (status) {
                        case "0":
                            res = "<a class='btn btn-sm btn-info' onclick='viewPush(\""+row['planDateWid']+"\")'><i class='fa fa-eye'></i>&nbsp;查看推送</a>" +
                                "&nbsp;<a class='btn btn-sm btn-success'><i class='fa fa-play'></i>&nbsp;开始执行</a>" +
                                "&nbsp;<a class='btn btn-sm btn-danger'><i class='fa fa-stop'></i>&nbsp;停止执行</a>";
                            break;
                        case "1":
                            res = "<a class='btn btn-sm btn-info'><i class='fa fa-eye'></i>&nbsp;查看推送</a>" +
                                "&nbsp;<a class='btn btn-sm btn-success'><i class='fa fa-play'></i>&nbsp;开始执行</a>" +
                                "&nbsp;<a class='btn btn-sm btn-danger'><i class='fa fa-stop'></i>&nbsp;停止执行</a>";
                            break;
                        case "2":
                            res = "<a class='btn btn-sm btn-info'><i class='fa fa-eye'></i>&nbsp;查看推送</a>" +
                                "&nbsp;<a class='btn btn-sm btn-success'><i class='fa fa-play'></i>&nbsp;开始执行</a>" +
                                "&nbsp;<a class='btn btn-sm btn-danger'><i class='fa fa-stop'></i>&nbsp;停止执行</a>";
                            break;
                        case "3":
                            res = "<a class='btn btn-sm btn-info'><i class='fa fa-eye'></i>&nbsp;查看推送</a>" +
                                "&nbsp;<a class='btn btn-sm btn-success'><i class='fa fa-play'></i>&nbsp;开始执行</a>" +
                                "&nbsp;<a class='btn btn-sm btn-danger'><i class='fa fa-stop'></i>&nbsp;停止执行</a>";
                            break;
                        case "4":
                            res = "<a class='btn btn-sm btn-info'><i class='fa fa-eye'></i>&nbsp;查看推送</a>" +
                                "&nbsp;<a class='btn btn-sm btn-success'><i class='fa fa-play'></i>&nbsp;开始执行</a>" +
                                "&nbsp;<a class='btn btn-sm btn-danger'><i class='fa fa-stop'></i>&nbsp;停止执行</a>";
                            break;

                    }
                    return res;
                }
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

// 推送预览
function viewPush(planDtWid) {
    getUserGroupTable(planDtWid);
    getUserDetail(planDtWid);
    $("#view_push_modal").modal('show');
}
// 获取用户群组列表
function getUserGroupTable(planDtWid) {
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
            }]
    };
    $("#userGroupTable").bootstrapTable(settings);
    $.get("/activity/getUserGroupList", {headId: headId, planDtWid: planDtWid},function (r) {
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

function getUserDetail(planDtWid){
    let settings = {
        url: "",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,  //页码
                param: {name: $("#name").val(), date: $("#date").val(), status: $("#status").val()}
            };
        },
        columns: [{
            field: 'preheatStartDt',
            title: '商品名称'
        }, {
            field: 'preheatEndDt',
            title: '商品ID'
        },{
            field: 'formalStartDt',
            title: '用户ID'
        }, {
            field: 'formalEndDt',
            title: '成长节点与活动期'
        }, {
            field: 'formalEndDt',
            title: '用户与商品关系'
        }, {
            field: 'formalEndDt',
            title: '活跃度'
        }, {
            field: 'formalEndDt',
            title: '用户价值'
        }, {
            field: 'formalEndDt',
            title: '推送内容'
        }]
    };
    $MB.initTable('userDetailTable', settings);
}