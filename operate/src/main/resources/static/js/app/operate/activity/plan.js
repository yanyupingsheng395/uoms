var stepObj=null;
$(function () {
    //获取活动名称
    getActivityName();

    //获取计划列表
    getPlanTable();

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
                checkbox: true,
                valign: "middle",
            },
            {
                field: 'planType',
                title: '',
                visible: false
            },
            {
                field: 'effectFlag',
                title: '',
                visible: false
            },
            {
                field: 'planId',
                title: '',
                visible: false
            },
            {
                field: 'planDateWid',
                title: '推送日期',
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
                    {   if(row.stage==='preheat')
                        {
                            return '<span class="badge bg-success">预售</span>&nbsp;<span class="badge bg-info">活动通知</span>';
                        }else
                        {
                            return '<span class="badge bg-primary">正式</span>&nbsp;<span class="badge bg-info">活动通知</span>';
                        }
                    }else
                    {
                        if(row.stage==='formal')
                        {
                            return '<span class="badge bg-success">正式</span>&nbsp;<span class="badge bg-cyan">活动期间</span>';
                        }else
                        {
                            return '<span class="badge bg-primary">预售</span>&nbsp;<span class="badge bg-cyan">活动期间</span>';
                        }

                    }
                }
            },
            {
                field: 'userCnt',
                title: '建议推送人数(人)',
                align: 'center',
                valign: 'middle'
            },  {
                field: 'successNum',
                title: '实际成功推送人数(人)',
                align: 'center',
                valign: 'middle'
            },
             {
                field: 'planStatus',
                title: '任务执行状态',
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
                            res = "<span class=\"badge bg-gray\">过期未执行</span>";
                            break;
                        case "5":
                            res = "<span class=\"badge bg-danger\">终止</span>";
                            break;
                    }
                    return res;
                }
            },
            {
                field: 'covRate',
                title: '推送转化率(%)',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'covAmount',
                title: '推送转化金额(元)',
                align: 'center',
                valign: 'middle'
            }
            ]
    };
    $("#planTable").bootstrapTable(settings);
    $.get("/activityPlan/getPlanList", {headId: headId},function (r) {
        if(r.code === 200) {
            $("#planTable").bootstrapTable('load', r.data);
        }else {
            $MB.n_danger("获取计划数据异常！");
        }
    });
}

/**
 * 预览执行
 */
$("#btn_process").click(function () {
    let selected = $("#planTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择要预览执行的推送计划！');
        return;
    }
    let planId = selected[0].planId;
    let planStatus =selected[0].planStatus;
    let planType=selected[0].planType;

    $("#planId").val(planId);
    $("#planStatus").val(planStatus);
    $("#planType").val(planType);

    //待执行状态
    if(planStatus==1)
    {
        //初始化进度条
        stepObj=steps({
            el: "#pushSteps",
            data: [
                { title: "概要",description:""},
                { title: "明细",description:""},
                { title: "推送",description:"" }
            ],
            center: true,
            dataOrder: ["title", "line", "description"]
        });

        //生成文案
        $MB.loadingDesc('show', '转化文案中，请稍候...');
        $.get("/activityPlan/transActivityDetail", {planId: planId}, function (r) {
            if(r.code == 200) {
                //如果生成成功，加载数据
                getUserGroupTable(planId,planType);
                getSmsStatisTable(planId);
                //打开弹出面板
                $("#view_push_modal").modal('show');
            }else {
                //提示错误信息
                $MB.n_danger(r.msg);
            }
            $MB.loadingDesc('hide');
        });
    }else
    {
        //初始化进度条
        stepObj=steps({
            el: "#pushSteps",
            data: [
                { title: "概要",description:""},
                { title: "明细",description:""}
            ],
            center: true,
            dataOrder: ["title", "line", "description"]
        });

        $MB.loadingDesc('show', '加载中，请稍候...');
        getUserGroupTable(planId,planType);
        getSmsStatisTable(planId);
        //打开弹出面板
        $("#view_push_modal").modal('show');
        $MB.loadingDesc('hide');
    }
});

// 获取用户群组列表
function getUserGroupTable(planId,planType) {

    $("#userGroupTable").bootstrapTable('destroy').bootstrapTable({
        url: '/activityPlan/getUserGroupList',
        columns: [
            {
                field: 'groupName',
                title: '店铺活动机制'
            }, {
                field: 'groupUserNum',
                title: '人数（人）',
                align: 'center'
            }
        ],
        queryParams : function(params) {
            return {
                planId : planId
            }
        }
    });
}


/**
 * 获取短信统计的表格
 */
function getSmsStatisTable(planId) {
    $("#smsStatisTable").bootstrapTable('destroy').bootstrapTable({
        url: '/activityPlan/getPlanSmsStatis',
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 5,            //每页的记录行数（*）
        pageList: [5,10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,  //页码
                param: {planId: planId}
            };
        },
        columns: [
            {field: 'smsContent', title: '文案内容'},
            {field: 'smsLength', title: '文案长度'},
            {field: 'cnt', title: '发送人数'}
        ],
    });
}

/**
 * @param planId
 */
function getUserDetail(){
    var planId=$("#planId").val()
    var planType=$("#planType").val()
    let settings = {
        url: "/activityPlan/getDetailPage",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 5,            //每页的记录行数（*）
        pageList: [5,10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,  //页码
                param: {planId: planId}
            };
        },
        columns: [
            {field: 'epbProductId', title: '商品ID'},
            {field: 'epbProductName', title: '商品名称'},
            {field: 'userId', title: '用户ID'},
            {
                field: 'groupName',
                title: '店铺活动机制'
            },
            {field: 'orderPeriod', title: '建议推送时段'},
            {
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
            }
            //{
            //     title: '成长洞察',
            //     width: 80,
            //     formatter: function (value, row, idx)
            //     {
            //         return "<button class='btn btn-primary btn-xs' onclick='growthInsight(\""+row['userId']+"\",\""+ headId+"\")'>know how</button>";
            //     }
            // }
        ],
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $("#userDetailTable").bootstrapTable('destroy').bootstrapTable(settings);
}

/**
 * 如果推送方式是固定时间，则让选择要推送的时间
 */
$('input[name="pushMethod"]').click(function () {
    if ($(this).val() == "FIXED") {
        $("#pushPeriodDiv").show();
    } else {
        $("#pushPeriodDiv").hide();
    }
});

/**
 * 用户选择确定checkbox之后，再让推送按钮可点击
 */
$("#push_ok").change(function () {
    let flag = $(this).is(':checked');
    if(flag) {
        $("#pushBtn").removeAttr("disabled");
    }else {
        $("#pushBtn").attr("disabled", true);
    }
});

/**
 * 预览页  下一步按钮的点击事件
 */
$("#next1Btn").on('click',function () {
    stepObj.setActive(1);

    //隐藏第一个div
    $("#overview").hide();
    $("#pushSet").hide();

    //判断状态，决定第二个页是否有下一步
    var planStatus=$("#planStatus").val();
    if(planStatus==='1')
    {
        //显示下一步按钮
        $("#next2Btn").show();
    }else
    {
        $("#next2Btn").hide();
    }

    //加载明细页数据
    getUserDetail();
    $("#pushdetail").show();
});


/**
 *  明细页上一步按钮
 */
$("#pre2Btn").on('click',function () {
    stepObj.setActive(0);

    //重新加载消息统计数据
    getSmsStatisTable($("#planId").val());

    $("#overview").show();
    $("#pushdetail").hide();
    $("#pushSet").hide();
});

/**
 *  明细页下一步按钮
 */
$("#next2Btn").on('click',function () {
    stepObj.setActive(2);

    $("#overview").hide();
    $("#pushdetail").hide();
    $("#pushSet").show();

    //加载一些推送相关的配置
    $.get("/activityPlan/getDefaultPushInfo", {}, function (r) {
        let code = "";
        r.data['timeList'].forEach((v, k) => {
            code += "<option value='" + v + "'>" + v + "</option>";
        });
        $("#pushPeriod").html('').append(code);
        //设置默认的推送方式
        $('input[name="pushMethod"]').removeAttr("checked");
        $('input[name="pushMethod"][value="' + r.data.method + '"]').prop("checked", true);
        //去掉确认按钮的勾选状态
        $("#push_ok").removeAttr("checked");
        //隐藏掉选择时间的选择框
        $("#pushPeriodDiv").hide();
    });
});

/**
 * 推送页 上一步按钮
 */
$("#pre3Btn").on('click',function () {
    stepObj.setActive(1);

    $("#overview").hide();
    //加载明细页数据
    getUserDetail();
    $("#pushdetail").show();
    $("#pushSet").hide();
});

/**
 * 启动推送
 */
$("#pushBtn").on('click',function () {
//禁用推送按钮
    $("#pushBtn").attr("disabled", true);
    $MB.loadingDesc('show', '启动推送...');

    //获取planId
    var planId= $("#planId").val();
    var pushMethod=$("input[name='pushMethod']:checked").val();
    var pushPeriod=$("#pushPeriod").find("option:selected").val();

    $.post("/activityPlan/startPush", {planId: planId,pushMethod:pushMethod,pushPeriod:pushPeriod}, function (r) {
        if(r.code === 200) {
            //关闭loading
            $MB.loadingDesc('hide');
            //提示
            $MB.n_success("推送成功！");
            $("#pushBtn").attr("disabled", true);
            //关闭弹出面板
            $("#view_push_modal").modal('hide');

            $('#planTable').bootstrapTable('destroy');
            getPlanTable();
        }else {
            $MB.loadingDesc('hide');
            $MB.n_danger(r.msg);
            //释放推送按钮
            $("#pushBtn").removeAttr("disabled");
        }
    });
});

/**
 * 关闭面板
 */
$("#view_push_modal").on("hidden.bs.modal", function () {
    //隐藏预览执行面板，清空一些隐藏变量
    $("#planId").val('');
    $("#planStatus").val('');
    $("#planType").val('');
    //隐藏推送按钮
    $("#pushBtn").hide();

    $("#overview").show();
    $("#pushdetail").hide();
    $("#pushSet").hide();

    stepObj.setActive(0);
});

function refreshPage()
{
    getPlanTable();
}

/**
 * 效果
 */
$("#btn_effect").click(function () {
    let selected = $("#planTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择要查看效果的推送计划！');
        return;
    }
    let planId = selected[0].planId;
    let status = selected[0].planStatus;
    let effectFlag = selected[0].effectFlag;

    if (status == '2' || status === '3') {
        if(effectFlag=='N')
        {
            $MB.n_warning("效果尚未进行计算，请于推送完成后第二日再来查看！");
        }else
        {
            window.location.href = "/page/activity/planEffect?planId=" + planId;
        }
    } else {
        $MB.n_warning("只有完成推送以后才能查看效果！");
    }
});

/**
* 终止
 */
$("#btn_stop").click(function () {
    let selected = $("#planTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择要中止的推送计划！');
        return;
    }
    let planId = selected[0].planId;
    let status = selected[0].planStatus;

    if (status == '1') {
        $MB.confirm({
            title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
            content: '确定要终止当前执行计划么?'
        }, function () {
            $MB.loadingDesc('show', '开始终止执行计划');
            $.post("/activityPlan/sopPlan", {planId: planId}, function (r) {
                if (r.code === 200) {
                    //关闭loading
                    $MB.loadingDesc('hide');
                    //提示
                    $MB.n_success("终止成功！");
                    $('#planTable').bootstrapTable('destroy');
                    getPlanTable();
                } else {
                    $MB.loadingDesc('hide');
                    $MB.n_danger(r.msg);
                }
            })
        });
    }
    else {
        $MB.n_warning("只有待执行的计划才能终止！");
    }
});

// function growthInsight(user_id,head_id)
// {
//     $("#personal_insight_modal").modal('show');
// }
//
// $("#personal_insight_modal").on('shown.bs.modal', function () {
//     getUserSpu(userId, headId);
//     getDateChart(userId);
// });