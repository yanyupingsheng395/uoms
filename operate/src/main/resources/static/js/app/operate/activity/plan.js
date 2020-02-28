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
            },  {
                field: 'successNum',
                title: '成功推送人数',
                align: 'center',
                valign: 'middle'
            },
             {
                field: 'planType',
                title: '计划类型',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row, index) {
                    if (value === 'NOTIFY')
                    {
                        return '<span class="badge bg-success">活动提醒</span>';
                    }else
                    {
                        return '<span class="badge bg-success">活动期间</span>';
                    }
                }
            },{
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
                            res = "<span class=\"badge bg-danger\">过期未推送</span>";
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
                            res = "<a class='btn btn-sm btn-info' onclick='viewAndPush("+row['planDateWid']+")'><i class='fa fa-eye'></i>&nbsp;预览并推送</a>";
                            break;
                        case "2":// 执行中
                            res = "<a class='btn btn-sm btn-success' onclick='viewList("+row['planDateWid']+")'><i class='fa fa-street-view'></i>&nbsp;查看</a>";
                            break;
                        case "3":// 执行完
                            res = "<a class='btn btn-sm btn-success' onclick='viewList("+row['planDateWid']+")'><i class='fa fa-street-view'></i>&nbsp;查看</a>";
                            break;
                        case "4":// 已停止
                            res = "<a class='btn btn-sm btn-success' onclick='viewList("+row['planDateWid']+")'><i class='fa fa-street-view'></i>&nbsp;查看</a>";
                            break;
                    }
                    return res;
                }
            }]
    };
    $("#preheatPlanTable").bootstrapTable(settings);
    $("#formalPlanTable").bootstrapTable(settings);
    $.get("/activityPlan/getPlanList", {headId: headId},function (r) {
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
// 预览并推送
function viewAndPush(planDtWid) {
    //生成文案
    $MB.loadingDesc('show', '转化文案中，请稍后...');
    $.get("/activityPlan/transActivityDetail", {headId: headId,planDtWid:planDtWid}, function (r) {
        if(r.code == 200) {
            //如果生成成功，加载数据
            getUserGroupTable(planDtWid);
            getUserDetail(planDtWid,"-1");
            $("#planDtWid").val(planDtWid);
            $("#view_push_modal").modal('show');
        }else {
            //提示错误信息
            $MB.n_danger(r.msg);
        }
        $MB.loadingDesc('hide');
    });
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
                valign: "middle"
            }, {
                field: 'userNum',
                title: '人数（人）',
                valign: "middle",
                align: 'center',
                formatter: function (value, row, index) {
                    return "<a style='cursor: pointer;' onclick='clickUserGroup(\""+row.groupId+"\")'>" + value + "</a>";
                }
            }]
    };
    $("#userGroupTable").bootstrapTable(settings);
    $.get("/activityPlan/getUserGroupList", {headId: headId, planDtWid: planDtWid},function (r) {
        $("#userGroupTable").bootstrapTable('load', r);
        $("a[data-toggle='tooltip']").tooltip();
    });
}

/**
 * @param planDtWid
 */
function getUserDetail(planDtWid,groupId){
    let settings = {
        url: "/activityPlan/getDetailPage",
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
                param: {headId: headId, planDtWid: planDtWid,groupId:groupId}
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
            field: 'groupName',
            title: '用户与商品关系',
            formatter: function (value, row, index) {
                if (value != null && value != undefined) {
                    let temp = value.length > 20 ? value.substring(0, 20) + "..." : value;
                    return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                } else {
                    return '-';
                }
            }
        }, {
            field: 'smsContent',
            title: '推送内容',
            formatter: function (value, row, index) {
                if (value != null && value != undefined) {
                    let temp = value.length > 20 ? value.substring(0, 20) + "..." : value;
                    return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                } else {
                    return '-';
                }
            }
        }],
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $("#userDetailTable").bootstrapTable('destroy').bootstrapTable(settings);
}


/**
 * 点击用户群组的用户数刷新明细表格
 * @param groupId
 */
function clickUserGroup(groupId)
{
     //刷新表格
    getUserDetail( $("#planDtWid").val(),groupId);
}

$('input[name="pushMethod"]').click(function () {
    if ($(this).val() == "FIXED") {
        $("#pushPeriodDiv").show();
    } else {
        $("#pushPeriodDiv").hide();
    }
});

$("#push_ok").change(function () {
    let flag = $(this).is(':checked');
    if(flag) {
        $("#pushMsgBtn").removeAttr("disabled");
    }else {
        $("#pushMsgBtn").attr("disabled", true);
    }
});

$("#view_push_modal").on('shown.bs.modal', function () {
    $.get("/activityPlan/getDefaultPushInfo", {}, function (r) {
        let code = "";
        r.data['timeList'].forEach((v, k) => {
            code += "<option value='" + v + "'>" + v + "</option>";
        });
        $("#pushPeriod").html('').append(code);
        $('input[name="pushMethod"]').removeAttr("checked");
        $('input[name="pushMethod"][value="' + r.data.method + '"]').prop("checked", true);
        //去掉确认按钮的勾选状态
        $("#push_ok").removeAttr("checked");
        $("#pushPeriodDiv").hide();
    });
});

////////////////////////////////////////////////预览////////////////////////////////////////////////
// 预览
function viewList(planDtWid) {
    getUserGroupTable2(planDtWid);
    getUserDetail2(planDtWid,"-1");
    $("#planDtWid2").val(planDtWid);
    $("#view_list_modal").modal('show');
}


$("#view_list_modal").on("hidden.bs.modal", function () {
    $("#planDtWid2").val('');
});


// 获取用户群组列表
function getUserGroupTable2(planDtWid) {
    var settings = {
        columns: [
            {
                field: 'groupName',
                title: '用户与商品关系',
                valign: "middle"
            }, {
                field: 'userNum',
                title: '人数（人）',
                valign: "middle",
                align: 'center',
                formatter: function (value, row, index) {
                    return "<a style='cursor: pointer;' onclick='clickUserGroup2(\""+row.groupId+"\")'>" + value + "</a>";
                }
            }]
    };
    $("#userGroupTable2").bootstrapTable(settings);
    $.get("/activityPlan/getUserGroupList", {headId: headId, planDtWid: planDtWid},function (r) {
        $("#userGroupTable2").bootstrapTable('load', r);
        $("a[data-toggle='tooltip']").tooltip();
    });
}

function getUserDetail2(planDtWid,groupId){
    let settings = {
        url: "/activityPlan/getDetailPage",
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
                param: {headId: headId, planDtWid: planDtWid,groupId:groupId}
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
            field: 'groupName',
            title: '用户与商品关系',
            formatter: function (value, row, index) {
                if (value != null && value != undefined) {
                    let temp = value.length > 20 ? value.substring(0, 20) + "..." : value;
                    return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                } else {
                    return '-';
                }
            }
        }, {
            field: 'smsContent',
            title: '推送内容',
            formatter: function (value, row, index) {
                if (value != null && value != undefined) {
                    let temp = value.length > 20 ? value.substring(0, 20) + "..." : value;
                    return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                } else {
                    return '-';
                }
            }
        },{
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
        },{
            field: 'pushDateStr',
            title: '推送时间'
        }],
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $("#userDetailTable2").bootstrapTable('destroy').bootstrapTable(settings);
}

function clickUserGroup2(groupId)
{
    //刷新表格
    getUserDetail2( $("#planDtWid2").val(),groupId);
}

// $("#btn_download").click(function() {
//     $MB.confirm({
//         title: "<i class='mdi mdi-alert-outline'></i>提示：",
//         content: "确定导出记录?"
//     }, function () {
//         $("#btn_download").text("下载中...").attr("disabled", true);
//         $.post("/activity/downloadDetail", {headId: headId, planDtWid:$("#planDtWid").val()}, function (r) {
//             if (r.code === 200) {
//                 window.location.href = "/common/download?fileName=" + r.msg + "&delete=" + true;
//             } else {
//                 $MB.n_warning(r.msg);
//             }
//             $("#btn_download").html("").append("<i class=\"fa fa-download\"></i> 导出名单").removeAttr("disabled");
//         });
//     });
// });

function submitData() {
    //禁用推送按钮
    $("#pushMsgBtn").attr("disabled", true);

    $MB.loadingDesc('show', '启动推送...');

    //获取planDateWid
    var planDateWid= $("#planDtWid").val();
    var pushMethod=$("input[name='pushMethod']:checked").val();
    var pushPeriod=$("#pushPeriod").find("option:selected").val();

    $.post("/activityPlan/startPush", {headId: headId, planDateWid:planDateWid,pushMethod:pushMethod,pushPeriod:pushPeriod}, function (r) {
        if(r.code === 200) {
            //关闭loading
            $MB.loadingDesc('hide');
            //提示
            $MB.n_success("推送成功！");
            $("#pushMsgBtn").attr("disabled", true);
            //关闭弹出面板
            $("#view_push_modal").modal('hide');

            $('#preheatPlanTable').bootstrapTable('destroy');
            $('#formalPlanTable').bootstrapTable('destroy');
            getPlanTable();
        }else {
            $MB.loadingDesc('hide');
            $MB.n_danger(r.msg);
            //释放推送按钮
            $("#pushMsgBtn").removeAttr("disabled");
        }
    });
}

function refreshPage()
{
    getPlanTable();
}