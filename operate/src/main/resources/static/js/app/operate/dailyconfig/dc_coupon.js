var validator;
var $couponForm = $("#coupon_edit");
init_date('validEnd', 'yyyy-mm-dd', 0,2,0);
$("#validEnd").datepicker('setStartDate', new Date());
$(function () {
    validateRule();
});
// 新增补贴
function addCoupon() {
    $('#coupon_modal').modal('hide');
    $('#coupon_add_modal').modal('show');
    $("#btn_save_coupon").attr('name', 'save');
}

$("#coupon_add_modal").on('hidden.bs.modal', function () {
    closeModal();
});


/**
 * 优惠券列表
 */
function couponTable() {
    var settings = {
        url: "/coupon/couponList",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        clickToSelect: true,
        queryParams: function(param) {
            return {
                limit: param.limit,
                offset: param.offset
            }
        },
        columns: [{
            checkbox: true
        },{
            field: 'couponDisplayName',
            align: 'center',
            title: '补贴名称'
        }, {
            field: 'couponSource',
            title: '补贴类型',
            align: 'center',
            formatter: function (value, row, index) {
                var res = '-';
                if (value === '0') {
                    res = "智能";
                }
                if (value === '1') {
                    res = "手动";
                }
                return res;
            }
        }, {
            field: 'couponThreshold',
            align: 'center',
            title: '补贴门槛(元)'
        }, {
            field: 'couponDenom',
            align: 'center',
            title: '补贴面额(元)'
        }, {
            field: 'couponUrl',
            align: 'center',
            title: '补贴短链接',
            formatter: function (value, row, index) {
                if (value !== undefined && value !== null && value !== '') {
                    var tmp = value.indexOf("http://") > -1 ? value:"http://"+value;
                    return "<a target='_blank' href='" + tmp + "' style='color: #48b0f7;border-bottom: solid 1px #48b0f7'>" + value + "</a>";
                } else {
                    return "-";
                }
            }
        }, {
            field: 'validEnd',
            align: 'center',
            title: '有效截止期'
        }, {
            field: 'checkFlag',
            title: '校验结果',
            align: 'center',
            formatter: function (value, row, index) {
                if (value === '1') {
                    return "<span class=\"badge bg-success\">通过</span>";
                }
                if (value === '0') {
                    return "<span class=\"badge bg-danger\">未通过</span>";
                }
                return "-";
            }
        }, {
            field: 'checkComments',
            title: '失败原因'
        }]
    };
    $( '#couponTable' ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

// 获取智能补贴数据
function getIntellectCoupon() {
    intelCouponData();
    $( "#coupon_modal" ).modal( 'hide' );
    $( "#intel_coupon_modal" ).modal( 'show' );
}

function intelCouponData() {
    var settings = {
        url: '/coupon/getIntelCouponList',
        cache: false,
        pagination: false,
        singleSelect: false,
        clickToSelect: true,
        columns: [{
            checkbox: true,
            formatter: function (value, row, index) {
                var couponId = row['couponId'];
                if (couponId === 1) {
                    return {
                        checked: true//设置选中
                    };
                } else {
                    return {
                        checked: true,
                        disabled: true
                    };
                }
            }
        }, {
            title: '补贴名称',
            align: 'center',
            formatter: function (value, row, index) {
                if (row['couponDenom'] !== '' && row['couponDenom'] !== null && row['couponDenom'] !== undefined) {
                    return row['couponDenom'] + '元'
                }
            }
        }, {
            field: 'couponThreshold',
            align: 'center',
            title: '补贴门槛(元)'
        }, {
            field: 'couponDenom',
            align: 'center',
            title: '补贴面额(元)'
        }, {
            title: '是否已经存在',
            align: 'center',
            formatter: function (value, row, index) {
                var couponId = row['couponId'];
                if (couponId === 1) {
                    return '否'
                } else {
                    return '是'
                }
            }
        }, {
            title: '处理方式',
            align: 'center',
            formatter: function (value, row, index) {
                var couponId = row['couponId'];
                if (couponId === 1) {
                    return '新增'
                } else {
                    return '忽略'
                }
            }
        }]
    };
    $( '#intelCouponTable' ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

// 关闭新增补贴
function closeModal() {
    var $form = $('#coupon_edit');
    $form.find("input[name='couponName']").val("").removeAttr("readOnly");
    $form.find("input[name='couponDenom']").val("").removeAttr("readOnly");
    $form.find("input[name='couponThreshold']").val("").removeAttr("readOnly");
    $form.find("input[name='couponInfo2']").val("").removeAttr("readOnly");
    $form.find("input[name='couponUrl']").val("").removeAttr("readOnly");
    $form.find("input[name='couponNum']").val("").removeAttr("readOnly");
    $form.find("input[name='couponDisplayName']").val("").removeAttr("readOnly");
    $form.find("input[name='validEnd']").val("").removeAttr("readOnly");
    $("input[name='validStatus']:radio[value='Y']").prop("checked", true);
    $MB.closeAndRestModal("coupon_add_modal");
    $("#couponValid").hide();
    $("#coupon_edit").validate().resetForm();
    $("#coupon_modal").modal('show');
}

// 表单验证规则
function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $couponForm.validate({
        rules: {
            couponDenom: {
                required: true,
                digits: true
            },
            couponThreshold: {
                required: true,
                digits: true
            },
            couponInfo2: {
                required: false
            },
            couponUrl: {
                required: false
            },
            couponDisplayName: {
                required: true,
                maxlength: couponNameLen
            },
            validEnd: {
                required: true
            },
        },
        errorPlacement: function (error, element) {
            if (element.is(":checkbox") || element.is(":radio")) {
                error.appendTo(element.parent().parent());
            } else {
                error.insertAfter(element);
            }
        },
        messages: {
            couponDenom: {
                required: icon + "请输入补贴面额",
                digits: icon + "只能是整数"
            },
            couponThreshold: {
                required: icon + "请输入补贴门槛",
                digits: icon + "只能是整数"
            },
            couponInfo2: icon + "请输入原链接",
            couponUrl: icon + "请输入短链接",
            couponDisplayName: {
                required: icon + "请输入补贴名称",
                maxlength: icon + "最大长度不能超过"+couponNameLen+"个字符"
            },
            validEnd: icon + "请输入有效截止日期",
        }
    });
}

// 券保存
$("#btn_save_coupon").click(function () {
    var name = $(this).attr("name");
    var validator = $couponForm.validate();
    var flag = validator.form();
    if (flag) {
        var formData = $("#coupon_edit").serialize();
        if (name === "save") {
            var url = "/coupon/save";;
            $.post(url, formData, function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success(r.msg);
                    $MB.refreshTable("couponTable");
                } else $MB.n_danger(r.msg);
            });
        }
        if (name === "update") {
            var url = "/coupon/update";
            if(!validCoupon()) {
                $MB.n_warning("当前日期大于有效截止日期，'补贴是否有效'的更改无效！");
                return;
            }
            $.post(url, formData, function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success(r.msg);
                    $MB.refreshTable("couponTable");
                } else $MB.n_danger(r.msg);
            });
        }
    }
});

/**
 * 获取短链
 */
function getShortUrl() {
    var url = $("#couponInfo2").val();
    if(url.trim() == "") {
        $MB.n_warning("长链不能为空!");
        return;
    }
    $.get("/coupon/getShortUrl", {url: url}, function(r) {
        if(r.code === 200) {
            $("#couponUrl").val(r.data);
            $MB.n_success("生成短链成功!");
        }else {
            $MB.n_danger(r['msg']);
        }
    });
}

// 去除日期的验证
function removeValid() {
    var validEnd = $("#validEnd").val();
    if(validEnd !== '') {
        $("#validEnd").removeClass('error');
        $("#validEnd-error").remove();
    }
}

// 用来解决编辑情况下，日期插件的值会清空的问题
var VALID_END;
function resetValidEndVal() {
    if(VALID_END !== undefined && VALID_END !== '') {
        $("#validEnd").val(VALID_END);
        VALID_END = "";
    }
}
// 编辑补贴
function updateCoupon() {
    $("#btn_save_coupon").attr("name", "update");
    var selected = $("#couponTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要修改的补贴！');
        return;
    }
    if(selected_length > 1) {
        $MB.n_warning("一次只能编辑一条记录！");
        return;
    }
    var couponId = selected[0].couponId;
    $("#couponValid").show();
    $.post("/coupon/getByCouponId", {"couponId": couponId}, function (r) {
        if (r.code === 200) {
            var $form = $('#coupon_edit');
            $("#coupon_add_modal").modal('show');
            $("#coupon_modal").modal('hide');
            var coupon = r.data;
            $("#myLargeModalLabel").html('修改补贴');
            $form.find("input[name='couponId']").val(coupon.couponId);
            $form.find("input[name='validStatus']").val(coupon.validStatus);
            $form.find("input[name='couponDenom']").val(coupon.couponDenom).attr("readonly", true);
            $form.find("input[name='couponThreshold']").val(coupon.couponThreshold).attr("readonly", true);
            $form.find("input[name='couponInfo2']").val(coupon.couponInfo2);
            $form.find("input[name='couponUrl']").val(coupon.couponUrl);
            $form.find("input[name='couponNum']").val(coupon.couponNum);
            $form.find("input[name='couponDisplayName']").val(coupon.couponDisplayName).attr("readonly", true);
            $form.find("input[name='validEnd']").val(coupon.validEnd);
            VALID_END = coupon.validEnd;
            $("input[name='validStatus']:radio[value='"+coupon.validStatus+"']").prop("checked", true);
        } else {
            $MB.n_danger(r['msg']);
        }
    });
}

// 验证券是否有效
function validCoupon() {
    let validStatus = $("input[name='validStatus']:checked").val();
    if(validStatus === 'Y') {
        let validEnd = $("#validEnd").val();
        let date = new Date();
        let now = String(date.getFullYear()) + (date.getMonth()+1).toString().padStart(2,'0') + (date.getDate().toString().padStart(2,'0'));
        return Number(now) <= Number(validEnd);
    }
    return true;
}

/**
 * 删除补贴数据
 */
function deleteCoupon() {
    var selected = $("#couponTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要删除的补贴！');
        return;
    }
    var couponId = [];
    selected.forEach((v, k) => {
        couponId.push(v['couponId']);
    });

    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除选中的补贴？'
    }, function () {
        $.post("/coupon/deleteCoupon", {"couponId": couponId.join(",")}, function (r) {
            if(r.code == 200) {
                $MB.n_success(r.msg);
            }else {
                $MB.n_danger(r.msg);
            }
            $MB.refreshTable('couponTable');
        });
    });
}

function saveIntelCoupon() {
    $MB.confirm({
        title: '提示：',
        content: '确定执行当前操作？'
    }, function () {
        let selected = $("#intelCouponTable").bootstrapTable( 'getSelections' );
        let coupon = [];
        selected.forEach((v,k)=>{
            if(v['couponId'] === 1) {
                var tmp = {};
                tmp.couponDenom = v['couponDenom'];
                tmp.couponThreshold = v['couponThreshold'];
                coupon.push(tmp);
            }
        });
        let url = '/coupon/getCalculatedCoupon';
        $.get(url, {coupon: JSON.stringify(coupon)}, function (r) {
            if(r.code === 200) {
                $MB.n_success("智能补贴更新成功。");
                $( "#intel_coupon_modal" ).modal( 'hide' );
                $MB.refreshTable('couponTable');
            }
        });
    });
}