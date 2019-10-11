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
            pushPeriod: $("#pushPeriod").find("option:selected").val()
        }, function (r) {
            if (r.code === 200) {
                $MB.n_success("启动推送成功！");
                setTimeout(function () {
                    window.location.href = "/page/daily";
                }, 1000);
            } else {
                $("#btn_push").attr("disabled", false);
                $MB.n_danger(r.msg);
            }
        });
    }
}

function step1() {
    step.setActive(0);
    $("#step1").attr("style", "display: block;");
    $("#step2").attr("style", "display: none;");
    $("#step3").attr("style", "display: none;");
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
        columns: [[{
            field: 'userId',
            title: '用户ID',
            rowspan: 2,
            valign: "middle"
        }, {
            title: '当日成长策略',
            colspan: 4
        }, {
            title: '当日触达动作',
            colspan: 3
        }], [
            {
                field: 'tarOrderPrice',
                title: '目标订单价（元/单）'
            }, {
                field: 'recProdName',
                title: '目标商品'
            }, {
                field: 'referDeno',
                title: '建议补贴面额（元）'
            }, {
                field: 'orderPeriod',
                title: '建议触达时段（时）'
            }, {
                field: 'couponMin',
                title: '优惠门槛（元）'
            }, {
                field: 'couponDeno',
                title: '优惠面额（元）'
            }, {
                field: 'smsContent',
                title: '短信文案',
                formatter: function (value, row, idx) {
                    let temp = value.substring(0, 20) + "...";
                    return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                }
            }
        ]],
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $('#userStrategyListTable').bootstrapTable('destroy');
    $MB.initTable('userStrategyListTable', settings);
}

$("#push_msg_modal").on('shown.bs.modal', function () {
    $.get("/daily/getPushInfo", {}, function (r) {
        let code = "";
        r.data['timeList'].forEach((v, k) => {
            code += "<option value='" + v + "'>" + v + "</option>";
        });
        $("#pushPeriod").html('').append(code);
        $('input[name="pushMethod"]').removeAttr("checked");
        $('input[name="pushMethod"][value="' + r.data.method + '"]').prop("checked", true);
    });
});

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