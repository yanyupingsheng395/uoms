var validator;
var $couponForm = $("#coupon_edit");

init_date('validEnd', 'yyyymmdd', 0,2,0);
$("#validEnd").datepicker('setStartDate', new Date());
$(function () {
    validateRule();
    var settings = {
        url: "/coupon/list",
        method: 'post',
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
                param: {smsCode: $("input[name='smsCode']").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'couponId',
            title: '优惠券编号'
        }, {
            field: 'couponDenom',
            title: '优惠券面额'
        }, {
            field: 'couponThreshold',
            title: '优惠券门槛'
        }, {
            field: 'couponName',
            title: '优惠券名称'
        }, {
            field: 'couponUrl',
            title: '优惠券领用地址',
            formatter: function (value, row, index) {
                return "<a href='" + value + "' style='color: #48b0f7;border-bottom: solid 1px #48b0f7'>" + value + "</a>";
            }
        }]
    };

    $MB.initTable('couponTable', settings);
    //为刷新按钮绑定事件
    $("#btn_refresh").on("click", function () {
        $('#couponTable').bootstrapTable('refresh');
    });
});

$("#btn_save").click(function () {
    var name = $(this).attr("name");
    var validator = $couponForm.validate();
    var flag = validator.form();
    if (flag) {
        if (name === "save") {
            $.post("/coupon/save", $("#coupon_edit").serialize(), function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success(r.msg);
                    $MB.refreshTable("couponTable");
                } else $MB.n_danger(r.msg);
            });
        }
        if (name === "update") {
            $.post("/coupon/update", $("#coupon_edit").serialize(), function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success(r.msg);
                    $MB.refreshTable("couponTable");
                } else $MB.n_danger(r.msg);
            });
        }
    }
});

function closeModal() {
    var $form = $('#coupon_edit');
    $form.find("input[name='couponName']").val("");
    $form.find("input[name='couponDenom']").val("");
    $form.find("input[name='couponThreshold']").val("");
    $form.find("input[name='couponInfo2']").val("");
    $form.find("input[name='couponUrl']").val("");
    $form.find("input[name='couponNum']").val("");
    $form.find("input[name='couponDisplayName']").val("");
    $form.find("input[name='validEnd']").val("");
    $MB.closeAndRestModal("add_modal");
}

function updateCoupon() {
    var selected = $("#couponTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要修改的优惠券！');
        return;
    }
    var couponId = selected[0].couponId;
    $.post("/coupon/getByCouponId", {"couponId": couponId}, function (r) {
        if (r.code === 200) {
            var $form = $('#coupon_edit');
            $("#add_modal").modal('show');
            var coupon = r.data;
            $("#myLargeModalLabel").html('修改优惠券');
            $form.find("input[name='couponId']").val(coupon.couponId);
            $form.find("input[name='couponName']").val(coupon.couponName).attr("readonly", true);
            $form.find("input[name='couponDenom']").val(coupon.couponDenom).attr("readonly", true);
            $form.find("input[name='couponThreshold']").val(coupon.couponThreshold).attr("readonly", true);
            $form.find("input[name='couponInfo2']").val(coupon.couponInfo2);
            $form.find("input[name='couponUrl']").val(coupon.couponUrl).attr("readonly", true);;
            $form.find("input[name='couponNum']").val(coupon.couponNum);
            $form.find("input[name='couponDisplayName']").val(coupon.couponDisplayName);
            $form.find("input[name='validEnd']").val(coupon.validEnd);
            $("#btn_save").attr("name", "update");
        } else {
            $MB.n_danger(r.msg);
        }
    });
}

// 表单验证规则
function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $couponForm.validate({
        rules: {
            couponName: {
                required: true
            },
            couponDenom: {
                required: true
            },
            couponThreshold: {
                required: true
            },
            couponInfo2: {
                required: true
            },
            couponUrl: {
                required: true
            },
            couponDisplayName: {
                required: true
            },
            validEnd: {
                required: true
            },
            couponNum: {
                required: true
            }
        },
        errorPlacement: function (error, element) {
            if (element.is(":checkbox") || element.is(":radio")) {
                error.appendTo(element.parent().parent());
            } else {
                error.insertAfter(element);
            }
        },
        messages: {
            couponName: icon + "请输入名称",
            couponDenom: icon + "请输入面额",
            couponThreshold: icon + "请输入门槛",
            couponInfo2: icon + "请输入长链",
            couponUrl: icon + "请输入短链",
            couponDisplayName: icon + "请输入引用名",
            validEnd: icon + "请输入截止日期",
            couponNum: icon + "请输入数量"
        }
    });
}


$("#btn_delete").click(function () {
    var selected = $("#couponTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要删除的优惠券！');
        return;
    }
    var couponId = selected[0].couponId;
    $.post("/coupon/deleteByCouponId", {"couponId": couponId}, function (r) {
        if(r.code == 200) {
            $MB.n_success("删除成功！");
        }else {
            $MB.n_danger(r.msg);
        }
        $MB.refreshTable("couponTable");
    });
});

