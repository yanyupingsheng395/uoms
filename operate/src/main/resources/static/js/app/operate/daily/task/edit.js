$(function () {
     generateStragegy();
});

/**
 * 生成每日运营策略
 */
function generateStragegy() {
    $MB.loadingDesc('show', '策略生成中，请稍后...');
    $.get("/daily/generatePushList", {headId: headId}, function (r) {
        if(r.code == 200) {
            $("#timestamp").val(r.msg);
            //如果生成成功，则加载表格
            getUserStrategyList();
        }else {
            //提示错误信息
            $MB.n_danger(r.msg);
        }
        $MB.loadingDesc('hide');
    });
}

function getUserStrategyList() {
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
                    headId: headId
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
                    if (value != null && value != undefined) {
                        let temp = value.length > 20 ? value.substring(0, 20) + "..." : value;
                        return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                    } else {
                        return '-';
                    }
                }
            },
            {
                title: '成长洞察',
                width: 80,
                formatter: function (value, row, idx)
                {
                    return "<button class='btn btn-primary btn-xs' onclick='growthInsight(\""+row['userId']+"\",\""+ headId+"\")'>know how</button>";
                }
            }],
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $MB.initTable('userStrategyListTable', settings);
}

/**
 * 成长洞察页
 * @param userId
 * @param headId
 */
var userId;
var headId;
function growthInsight(user_id,head_id)
{
    userId = user_id;
    headId = head_id;
    $("#personal_insight_modal").modal('show');
}

function searchPersonalInsight() {
    getUserBuyOrder(userId, headId);
    getDateChart(userId);
}

function resetPersonInsight() {
    var val = $("#spuId").find("option:eq(0)").val();
    $("#spuId").val(val).select2();
    getUserBuyOrder(userId, headId);
    getDateChart(userId);
}

$("#personal_insight_modal").on('shown.bs.modal', function () {
    getUserSpu(userId, headId);
    getDateChart(userId);
});

/**
 * 启动群组推送
 */
function submitData() {
    let flag = $("#push_ok").is(':checked');
    if (flag) {
        $("#btn_push").attr("disabled", true);
        $("#push_msg_modal").modal('hide');
        $.get("/daily/submitData", {
            headId: headId, pushMethod: $("input[name='pushMethod']:checked").val(),
            pushPeriod: $("#pushPeriod").find("option:selected").val(),
            timestamp: $("#timestamp").val()
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
        });
    }
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

$("#push_msg_modal").on('shown.bs.modal', function () {
    $.get("/daily/getPushInfo", {}, function (r) {
        let code = "";
        r.data['timeList'].forEach((v, k) => {
            code += "<option value='" + v + "'>" + v + "</option>";
        });
        $("#pushPeriod").html('').append(code);
        $('input[name="pushMethod"]').removeAttr("checked");
        $('input[name="pushMethod"][value="' + r.data.method + '"]').prop("checked", true);
        $("#pushPeriodDiv").hide();
    });
});



