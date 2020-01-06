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
                        case "0":// 尚未计算
                            res = "-";
                            break;
                        case "1": // 待执行
                            res = "<a class='btn btn-sm btn-info' onclick='viewPush(\""+row['planDateWid']+"\", \"0\")'><i class='fa fa-eye'></i>&nbsp;查看推送</a>" +
                                "&nbsp;<a class='btn btn-sm btn-success' onclick='startPush(\""+row['planDateWid']+"\", \""+row['stage']+"\")'><i class='fa fa-play'></i>&nbsp;开始执行</a>";
                            break;
                        case "2":// 执行中
                            res = "<a class='btn btn-sm btn-info' onclick='viewPush(\""+row['planDateWid']+"\", \"0\")'><i class='fa fa-eye'></i>&nbsp;查看推送</a>" +
                                "&nbsp;<a class='btn btn-sm btn-danger' onclick='stopPush(\""+row['planDateWid']+"\", \""+row['stage']+"\")'><i class='fa fa-stop'></i>&nbsp;停止执行</a>";
                            break;
                        case "3":// 执行完
                            res = "<a class='btn btn-sm btn-info' onclick='viewPush(\""+row['planDateWid']+"\",\"1\")'><i class='fa fa-eye'></i>&nbsp;预览推送</a>";
                            break;
                        case "4":// 已停止
                            res = "<a class='btn btn-sm btn-info' onclick='viewPush(\""+row['planDateWid']+"\", \"1\")'><i class='fa fa-eye'></i>&nbsp;预览推送</a>";
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
            if(r.data['formal'] != undefined) {
                $("#formalPlanTable").bootstrapTable('load', r.data['formal']);
            }
            if(r.data['preheat'] != undefined) {
                $("#preheatPlanTable").bootstrapTable('load', r.data['preheat']);
            }
        }else {
            $MB.n_danger("获取计划数据异常！");
        }
    });
}
// 推送预览
function viewPush(planDtWid, flag) {
    getUserGroupTable(planDtWid);
    getUserDetail(planDtWid, flag);
    $("#planDtWid").val(planDtWid);
    $("#view_push_modal").modal('show');
}

$("#view_push_modal").on("hidden.bs.modal", function () {
    $("#planDtWid").val('');
});

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

/**
 * flag:0 查看推送
 * flag:1 预览推送
 * @param planDtWid
 * @param flag
 */
function getUserDetail(planDtWid, flag){
    let settings = {
        url: "/activity/getDetailPage",
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
                param: {headId: headId, planDtWid: planDtWid}
            };
        },
        columns: [{
            field: 'epbProductId',
            title: '商品ID'
        },{
            field: 'epbProductName',
            title: '商品名称'
        }, {
            field: 'userId',
            title: '用户ID'
        }, {
            field: 'inGrowthPath',
            title: '成长节点与活动期',
            formatter: function (value, row, index) {
                let res = "-";
                switch (value) {
                    case "0":
                        res = "不在";
                        break;
                    case "1":
                        res = "在";
                        break;
                }
                return res;
            }
        }, {
            field: 'groupId',
            title: '用户与商品关系',
            formatter: function (value, row, index) {
                let res = "-";
                switch (value) {
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                        res = "成长用户";
                        break;
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                        res = "潜在用户";
                        break;
                }
                return res;
            }
        }, {
            field: 'pathActive',
            title: '活跃度',
            formatter: function (value, row, index) {
                var res = "";
                switch (value) {
                    case "UAC_01":
                        res = "高度活跃";
                        break;
                    case "UAC_02":
                        res = "中度活跃";
                        break;
                    case "UAC_03":
                        res = "流失预警";
                        break;
                    case "UAC_04":
                        res = "弱流失";
                        break;
                    case "UAC_05":
                        res = "强流失";
                        break;
                    case "UAC_06":
                        res = "沉睡";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        }, {
            field: 'userValue',
            title: '用户价值',
            formatter: function (value, row, index) {
                var res = "";
                switch (value) {
                    case "ULC_01":
                        res = "重要";
                        break;
                    case "ULC_02":
                        res = "主要";
                        break;
                    case "ULC_03":
                        res = "普通";
                        break;
                    case "ULC_04":
                        res = "长尾";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        }, {
            field: 'smsContent',
            title: '推送内容'
        }]
    };

    if(flag === '1') {
        settings.columns.push(
            {
                field: 'isPush',
                title: '是否推送',
                formatter: function (value, row, index) {
                    let res = "-";
                    switch (value) {
                        case "0":
                            res = "否";
                            break;
                        case "1":
                            res = "是";
                            break;
                    }
                    return res;
                }
            }
        );

        settings.columns.push(
            {
                field: 'pushDateStr',
                title: '推送时间'
            }
        );

        settings.columns.push(
            {
                field: 'pushStatus',
                title: '推送状态',
                formatter: function (value, row, index) {
                    let res = "-";
                    switch (value) {
                        case "P":
                            res = "计划中";
                            break;
                        case "F":
                            res = "成功";
                            break;
                        case "S":
                            res = "失败";
                            break;
                    }
                    return res;
                }
            }
        );
    }
    $("#userDetailTable").bootstrapTable('destroy').bootstrapTable(settings);
}

$("#btn_download").click(function() {
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定导出记录?"
    }, function () {
        $("#btn_download").text("下载中...").attr("disabled", true);
        $.post("/activity/downloadDetail", {headId: headId, planDtWid:$("#planDtWid").val()}, function (r) {
            if (r.code === 200) {
                window.location.href = "/common/download?fileName=" + r.msg + "&delete=" + true;
            } else {
                $MB.n_warning(r.msg);
            }
            $("#btn_download").html("").append("<i class=\"fa fa-download\"></i> 导出名单").removeAttr("disabled");
        });
    });
});

function startPush(planDateWid, stage) {
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认开始执行当前计划？'
    }, function () {
        $.post("/activity/startPush", {headId: headId, planDateWid:planDateWid, stage: stage}, function (r) {
            if(r.code === 200) {
                if(stage === 'preheat') {
                    $('#preheatPlanTable').bootstrapTable('destroy');
                    getPlanTable();
                }
                if(stage === 'formal') {
                    $('#formalPlanTable').bootstrapTable('destroy');
                    getPlanTable();
                }
                $MB.n_success("计划已开始执行！");
            }else {
                $MB.n_danger(r.msg);
            }
        });
    });
}

function stopPush(planDateWid, stage) {
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认停止执行当前计划？'
    }, function () {
        $.post("/activity/stopPush", {headId: headId, planDateWid:planDateWid}, function (r) {
            if(r.code === 200) {
                if(stage === 'preheat') {
                    $('#preheatPlanTable').bootstrapTable('destroy');
                    getPlanTable();
                }
                if(stage === 'formal') {
                    $('#formalPlanTable').bootstrapTable('destroy');
                    getPlanTable();
                }
                $MB.n_success("计划已停止执行！");
            }else {
                $MB.n_danger(r.msg);
            }
        });
    });
}