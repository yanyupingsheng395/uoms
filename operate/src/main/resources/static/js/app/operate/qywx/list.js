$(function () {
    var errmsg=$("#errormsg").val();
    if(null!=errmsg&&errmsg!='')
    {
        $MB.n_warning(errmsg);
    }
    init_date('taskDate', 'yyyy-mm-dd', 0, 2, 0);
    $("#taskDate").datepicker('setEndDate', new Date());
    initTable();
});

let userValueOption;
let currDate=getNowFormatDate();
function initTable() {
    var settings = {
        url: '/qywxDaily/getHeadList',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {taskDate: $("#taskDate").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'headId',
            title: 'ID',
            visible: false
        }, {
            field: 'taskDateStr',
            title: '推送日期'
        }
        // , {
        //     field: 'qywxMessageCount',
        //     title: '个性化消息数（条）'
        // }
        ,{
            field: 'totalNum',
            title: '消息所覆盖的用户数（人）'
        }, {
            field: 'staffCnt',
            title: '需要推送消息的成员数（人）'
        }, {
            field: 'convertNum',
            title: '消息实际推送到达用户数（人）'
        }, {
            field: 'convertAmount',
            title: '个性化消息转化金额（元）'
        }, {
            field: 'status',
            title: '任务执行状态',
            align: 'center',
            formatter: function (value, row, indx) {
                var res;
                switch (value) {
                    case "todo":
                        res = "<span class=\"badge bg-info\">待执行</span>";
                        break;
                    case "done":
                        res = "<span class=\"badge bg-success\">已执行</span>";
                        break;
                    case "done_ce":
                        res = "<span class=\"badge bg-warning\">已执行、部分发券错误</span>";
                        break;
                    case "done_pe":
                        res = "<span class=\"badge bg-warning\">已执行、部分推送消息错误</span>";
                        break;
                    case "finished":
                        res = "<span class=\"badge bg-primary\">已结束</span>";
                        break;
                    case "timeout":
                        res = "<span class=\"badge bg-gray\">过期未执行</span>";
                        break;
                    default:
                        res = "-";
                        break;
                }
                return res;
            }
        }, {
            title: '配置校验状态',
            align: 'center',
            formatter: function (value, row, indx) {
                var res = "-";
                if(row.taskDateStr ===currDate&&"通过"===row.validateLabel) {
                    res = "<span class=\"badge bg-success\"><a style='text-decoration: none;cursor: pointer;pointer-events: none;color:#fff;'>"+row.validateLabel+"</a></span>";
                }else if(row.taskDateStr ===currDate&&"未通过"===row.validateLabel)
                {
                    res = "<span class=\"badge bg-danger\"><a onclick='gotoConfig()' style='color: #fff;text-decoration: underline;cursor: pointer;'>"+row.validateLabel+"</a></span>";
                }else
                {
                    res='-';
                }
                return res;
            }
        }, {
            title: '配置校验结果',
            field: 'checkDesc'
        }]
    };
    $MB.initTable('qywxDailyTable', settings);
}

function resetQuery() {
    $("#taskDate").val("");
    $MB.refreshTable("qywxDailyTable");
}

$("#btn_query").click(function () {
    $MB.refreshTable("qywxDailyTable");
});

/**
 * 任务效果查看
 */
$("#btn_catch").click(function () {
    let selected = $("#qywxDailyTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选查看效果的任务！');
        return;
    }
    let status = selected[0].status;
    if (status === 'todo') {
        $MB.n_warning("当前任务状态不支持查看任务效果！");
        return;
    }
    let headId = selected[0].headId;
    window.location.href = "/page/qywxDaily/effect?headId=" + headId;
});

/**
 * 预览执行
 */
$("#btn_insight").click(function () {
    var selected = $("#qywxDailyTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要预览执行的任务！');
        return;
    };

    let headId = selected[0].headId;
    let status = selected[0].status;
    let taskDate = selected[0].taskDateStr;
    let totalNum = selected[0].totalNum;

    $("#headId").val(headId);

    if(totalNum==0)
    {
        $MB.n_warning('当前任务没有建议推送的用户！');
        return;
    }

    //打开遮罩层
    $MB.loadingDesc('show', '处理中，请稍候...');
    //获取预览数据
    $.get("/qywxDaily/getTaskOverViewData", {headId: headId}, function (r) {
        if(r.code == 200) {
            //初始化步骤组件
            setStep(status,taskDate);

            //加载当前任务所涉及的成员
            getAllFollowUserList(headId);

            getUserStrategyList(headId);

            //打开modal
            $("#viewPush_modal").modal('show');
        }else {
            //提示错误信息
            $MB.n_danger(r.msg);
        }
        $MB.loadingDesc('hide');
    });

});

function gotoConfig() {
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: "去完成配置？"
    }, function () {
        location.href = "/page/daily/qywxDailyConfig";
    });
}

function getNowFormatDate() {
    var date = new Date();
    var year = date.getFullYear()+"";
    var month = (date.getMonth() + 1)+"";
    var strDate = date.getDate()+"";
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    let currentdate = year +month + strDate;
    return currentdate;
}

////////////////////////////////预览执行//////////////////////////////////
/**
 * 打开预览推送面板
 */
var stepObj=null;
$("#viewPush_modal").on("shown.bs.modal", function () {
    $("#effectDays").ionRangeSlider({
        min: 1,
        max: 10,
        from: 5,
        skin: "modern"
    });
    init();
});

/**
 * 隐藏预览推送面板
 */
$("#viewPush_modal").on("hidden.bs.modal", function () {
    if(stepObj!=null)
    {
        stepObj.setActive(0);
    }

    $("#prevStepBtn").attr("style", "display:none;");
    $("#nextStepBtn").attr("style", "display:inline-block");
    $("#pushMsgBtn").attr("style", "display:none;");
    $("#step1").attr("style", "display:block;");
    $("#step2").attr("style", "display:none;");
    $("#step3").attr("style", "display:none;");
    $("#effectDays").ionRangeSlider({
        min: 1,
        max: 10,
        from: 5,
        skin: "modern"
    });
    $("#push_ok").prop("checked", false);
    $("input[name='pushMethod']:checked").prop("checked", false);
    current_step = 0;
});

function init() {
    let effectRange = $("#effectDays").data("ionRangeSlider");
    $.get("/qywxDaily/getPushInfo", {}, function (r) {
        let code = "";
        r.data['timeList'].forEach((v, k) => {
            code += "<option value='" + v + "'>" + v + "</option>";
        });
        $("#pushPeriod").html('').append(code);
        $('input[name="pushMethod"]').removeAttr("checked");
        $('input[name="pushMethod"][value="' + r.data.method + '"]').prop("checked", true);
        $("#pushPeriodDiv").hide();

        //有效天数滑块设置为默认值
        effectRange.update({
            min: 1,
            max: 10,
            from: r.data.effectDays,
            skin: "modern"
        });
    });
}

/**
 * 设置step
 */
let current_status='';
let current_taskDt='';
let currDay=getNowFormatDate();
function setStep(status,taskDate) {
    current_status = status;
    current_taskDt=taskDate;
    //如果状态为todo 且 任务为当前日期，则显示 推送页
    if(status === 'todo'&&current_taskDt==currDay) {
        stepObj=steps({
            el: "#pushSteps",
            data: [
                { title: "今日成长用户详情",description:""},
                { title: "向成员派发推送任务",description:""}
            ],
            center: true,
            dataOrder: ["title", "line", "description"]
        });
    }else {
        //临时修改
        // stepObj=steps({
        //     el: "#pushSteps",
        //     data: [
        //         { title: "今日成长用户详情",description:""},
        //         { title: "向成员派发推送任务",description:""}
        //     ],
        //     center: true,
        //     dataOrder: ["title", "line", "description"]
        // });
       $("#nextStepBtn").attr("style", "display:none;");
    }
}

/**
 * 步骤发生改变
 */
var current_step = 0;
function changeStep(count) {
    current_step = current_step + count;
    if(current_step >= 2) {
        current_step = 1;
    }
    if(current_step < 0) {
        current_step = 0;
    }
    if(current_step === 0) {
        $("#prevStepBtn").attr("style", "display:none;");
        $("#pushMsgBtn").attr("style", "display:none;");
        $("#step1").attr("style", "display:block;");
        $("#step2").attr("style", "display:none;");

        $("#nextStepBtn").attr("style", "display:inline-block;");
        getUserStrategyList($("#headId").val());
        stepObj.setActive(1);
    }
    //推送面板
    if(current_step === 1) {
        $.get("/qywxDaily/validUserGroupForQywx", {}, function(r) {
            if(r.code == 200) {
                if(r.data['flag'] === '未通过') {
                    $MB.n_warning("成长组配置验证未通过！");
                    return false;
                }else {
                    $("#prevStepBtn").attr("style", "display:inline-block;");
                    $("#nextStepBtn").attr("style", "display:none;");
                    $("#pushMsgBtn").attr("style", "display:inline-block;");
                    $("#step1").attr("style", "display:none;");
                    $("#step2").attr("style", "display:block;");
                    stepObj.setActive(2);
                }
            }
        });
    }
}

/**
 * 确认复选框被选中
 */
$("#push_ok").change(function () {
    let flag = $(this).is(':checked');
    if(flag) {
        $("#pushMsgBtn").removeAttr("disabled");
    }else {
        $("#pushMsgBtn").attr("disabled", true);
    }
});

/**
 * 启动群组推送
 */
function submitData() {
    if(!$("#push_ok").is(':checked')) {
        $MB.n_warning("请先勾选我要推送！");
        return;
    }
    $("#btn_push").attr("disabled", true);
    $("#push_msg_modal").modal('hide');

    $MB.loadingDesc('show', '推送中，请稍候...');
    $.get("/qywxDaily/submitTask", {
        headId: $("#headId").val(), pushMethod: $("input[name='pushMethod']:checked").val(),
        pushPeriod: $("#pushPeriod").find("option:selected").val(),
        effectDays: $("#effectDays").val()
    }, function (r) {
        if (r.code === 200) {
            $MB.n_success("启动推送成功！");
            setTimeout(function () {
                window.location.href = "/page/qywxDaily/list";
            }, 1000);
        } else {
            $("#btn_push").attr("disabled", false);
            $MB.n_danger(r.msg);
        }
        $MB.loadingDesc('hide');
    });
}

$('input[name="pushMethod"]').click(function () {
    if ($(this).val() == "FIXED") {
        $("#pushPeriodDiv").show();
    } else {
        $("#pushPeriodDiv").hide();
    }
});

function getUserStrategyList(pheadId) {
    let settings = {
        url: '/qywxDaily/getDetailList',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  //页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    headId: pheadId,
                    followUserId: $("#followUserSelect").find("option:selected").val()
                }
            };
        },
        columns: [
            {
                field: 'textContent',
                title: '个性化消息内容',
                formatter: function (value, row, index) {
                    return longTextFormat(value, row, index);
                }
            },
            {
                field: 'followUserName',
                title: '推送消息的成员',
                align: "center",
            },
            {
                field: 'qywxContactName',
                title: '成员推送消息的用户',
                align: "center",
            },{
                field: 'recProdName',
                title: '推荐购买的商品',
                align: "center",
            },{
                field: 'couponDeno',
                title: '基于商品的补贴',
                align: "center",
                formatter: function (value, row, index) {
                    if(value !== '' && value !== null) {
                        return value + '元券';
                    }else {
                        return '无';
                    }
                }
            },
            {
                title: '为什么这样做',
                width: 80,
                formatter: function (value, row, idx)
                {
                    return "<button class='btn btn-primary btn-xs' onclick='growthInsight(\""+row['userId']+"\",\""+ $("#headId").val() +"\")'>know how</button>";
                }
            }],
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $("#userListTable").bootstrapTable('destroy').bootstrapTable(settings);
}

/**
 * 单个用户成长洞察
 * @param user_id
 * @param head_id
 */
function growthInsight(user_id,head_id)
{
    $.get("/qywxDaily/getTaskDt", {headId: head_id}, function (res) {
        var taskDt = res.data;
        if(res.code === 200 && taskDt !== undefined && taskDt !== '') {
            window.open("/page/personInsight?headId="+head_id+"&userId=" + user_id + "&taskDt=" + taskDt, "_blank");
        }else {
            $MB.n_warning("获取任务日期有误！");
        }
    });
}

function getAllFollowUserList(pheadId) {
    $.get("/qywxDaily/getFollowUserList", {headId: pheadId}, function (r) {
        var code = "<option value='-1'>所有</option>";
        $.each(r.data,function (index,value) {
            code += "<option value="+value.followUserId+">"+value.followUserName+"</option>";
        });
        $("#followUserSelect").change(function() {
            //重新加载数据
            getUserStrategyList(pheadId);
        });
        $("#followUserSelect").html('').append(code);
    });
}