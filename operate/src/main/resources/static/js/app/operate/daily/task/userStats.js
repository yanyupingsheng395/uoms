/**
 * 获取数据
 */
let stepObj=null;
let chart1;
let chart1a;
let chart1b;
let chart2;
let chart3;

$("#userStats_modal").on("shown.bs.modal", function () {
    $("#effectDays").ionRangeSlider({
        min: 1,
        max: 10,
        from: 5,
        skin: "modern"
    });
    init();
    chart1.resize();
    chart1a.resize();
    chart1b.resize();
    chart2.resize();
    chart3.resize();
});

$("#userStats_modal").on("hidden.bs.modal", function () {
    stepObj.setActive(0);
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
    $.get("/daily/getPushInfo", {}, function (r) {
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
let current_touchDt='';
let currDay=getNowFormatDate();
function setStep(status,touchDtStr) {
    current_status = status;
    current_touchDt=touchDtStr;

    //如果状态为todo 且 任务为当前日期，则显示 推送页
    if(status === 'todo'&&current_touchDt==currDay) {
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
    }else {
        stepObj=steps({
            el: "#pushSteps",
            data: [
                { title: "概要",description:""},
                { title: "明细",description:""}
                ],
            center: true,
            dataOrder: ["title", "line", "description"]
        });
    }
}

/**
 * 步骤发生改变
 */
var current_step = 0;
function changeStep(count) {
    current_step = current_step + count;
    if(current_step > 3) {
        current_step = 3;
    }
    if(current_step < 0) {
        current_step = 0;
    }
    //概要
    if(current_step === 0) {
        $("#prevStepBtn").attr("style", "display:none;");
        $("#nextStepBtn").attr("style", "display:inline-block");
        $("#pushMsgBtn").attr("style", "display:none;");
        $("#step1").attr("style", "display:block;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:none;");
        stepObj.setActive(0);
    }
    //明细
    if(current_step === 1) {
        $("#prevStepBtn").attr("style", "display:inline-block");
        $("#pushMsgBtn").attr("style", "display:none;");
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:block;");
        $("#step3").attr("style", "display:none;");
        if(current_status === 'todo'&&current_touchDt==currDay) {
            $("#nextStepBtn").attr("style", "display:inline-block;");
            getUserStrategyList($("#headId").val());
        }else {
            $("#nextStepBtn").attr("style", "display:none;");
            getUserStrategyList($("#headId").val());
        }
        stepObj.setActive(1);
    }
    //推送
    if(current_step === 2) {
        if(current_status === 'todo'&&current_touchDt==currDay) {
            $.get("/daily/validUserGroup", {}, function(r) {
                if(r.code == 200) {
                    if(r.data) {
                        $MB.n_warning("成长组配置验证未通过！");
                        return false;
                    }else {
                        $("#prevStepBtn").attr("style", "display:inline-block;");
                        $("#nextStepBtn").attr("style", "display:none;");
                        $("#pushMsgBtn").attr("style", "display:inline-block;");
                        $("#step1").attr("style", "display:none;");
                        $("#step2").attr("style", "display:none;");
                        $("#step3").attr("style", "display:block;");
                        stepObj.setActive(2);
                    }
                }
            });
        }
    }
}

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
    if($("input[name='pushMethod']:checked").val() === undefined) {
        $MB.n_warning("请先选择推送方式！");
        return;
    }
    $("#btn_push").attr("disabled", true);
    $("#push_msg_modal").modal('hide');

    $MB.loadingDesc('show', '推送中，请稍候...');
    $.get("/daily/submitData", {
        headId: $("#headId").val(), pushMethod: $("input[name='pushMethod']:checked").val(),
        pushPeriod: $("#pushPeriod").find("option:selected").val(),
        effectDays: $("#effectDays").val()
    }, function (r) {
        if (r.code === 200) {
            $MB.n_success("启动推送成功！");
            setTimeout(function () {
                window.location.href = "/page/daily/task";
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
        url: '/daily/getUserStrategyList',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  //页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    headId: pheadId
                }
            };
        },
        columns: [
            {
                field: 'userId',
                title: '用户ID',
                valign: "middle",
                width: 100
            },
            {
                field: 'recProdName',
                title: '推荐商品',
                width: 300
            },
            {
                field: 'couponMin',
                title: '补贴',
                width: 100,
                formatter: function (value, row, idx) {
                    if(row.couponMin==''||row.couponMin=='null'||row.couponMin==null)
                    {
                        return "无";
                    }else
                    {
                        return  row.couponDeno+"元";
                    }
                }

            },
            {
                field: 'orderPeriod',
                title: '建议触达时段（时）',
                width: 100
            },
            {
                field: 'smsContent',
                title: '推送预览',
                formatter: function (value, row, index) {
                    return longTextFormat(value, row, index);
                }
            },
            {
                title: '成长洞察',
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
    $MB.initTable('userStrategyListTable', settings);
}

function growthInsight(user_id,head_id)
{
    $.get("/daily/getTouchDt", {headId: head_id}, function (res) {
        var taskDt = res.data;
        if(res.code === 200 && taskDt !== undefined && taskDt !== '') {
            window.open("/page/personInsight?userId=" + user_id + "&taskDt=" + taskDt, "_blank");
        }else {
            $MB.n_warning("获取任务日期有误！");
        }
    });

}

/**
 * 获取当前日期的年月日格式
 * @returns {string}
 */
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
    return currentdate;
}



