var validator;
var $couponForm = $("#coupon_edit");
init_date('validEnd', 'yyyy-mm-dd', 0,2,0);
$("#validEnd").datepicker('setStartDate', new Date());
$(function () {
    validateRule();
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
    $form.find("input[name='couponName']").val("").removeAttr("readOnly");
    $form.find("input[name='couponDenom']").val("").removeAttr("readOnly");
    $form.find("input[name='couponThreshold']").val("").removeAttr("readOnly");
    $form.find("input[name='couponInfo2']").val("").removeAttr("readOnly");
    $form.find("input[name='couponUrl']").val("").removeAttr("readOnly");
    $form.find("input[name='couponNum']").val("").removeAttr("readOnly");
    $form.find("input[name='couponDisplayName']").val("").removeAttr("readOnly");
    $form.find("input[name='validEnd']").val("").removeAttr("readOnly");
    $("input[name='validStatus']:radio[value='Y']").prop("checked", true);
    $MB.closeAndRestModal("add_modal");
    $("#btn_save").attr("name", "save");
    $("#couponValid").hide();
    $("#coupon_edit").validate().resetForm();
}

$("#add_modal").on('hidden.bs.modal', function () {
    closeModal();
});

function updateCoupon() {
    $("#btn_save").attr("name", "update");
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
            $("#add_modal").modal('show');
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
        } else {
            $MB.n_danger(r['msg']);
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
                required: true,
                digits: true
            },
            couponThreshold: {
                required: true,
                digits: true
            },
            couponInfo2: {
                required: function () {
                    return ($("#couponSendType").val() === 'A');
                }
            },
            couponUrl: {
                required: function () {
                    return ($("#couponSendType").val() === 'A');
                }
            },
            couponDisplayName: {
                required: true,
                maxlength: couponNameLen
            },
            validEnd: {
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
            couponDenom: {
                required: icon + "请输入补贴面额",
                digits: icon + "只能是整数"
            },
            couponThreshold: {
                required: icon + "请输入补贴门槛",
                digits: icon + "只能是整数"
            },
            couponInfo2: icon + "请输入长链接",
            couponUrl: icon + "请输入短链接",
            couponDisplayName: {
                required: icon + "请输入补贴名称",
                maxlength: icon + "最大长度不能超过"+couponNameLen+"个字符"
            },
            validEnd: icon + "请输入有效截止日期"
        }
    });
}

$("#btn_delete").click(function () {
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
});

/**
 * 获取短链
 */
function getShortUrl() {
    var url = $("#couponInfo2").val();
    if(url.trim() == "") {
        $MB.n_warning("长链不能为空！");
        return;
    }
    $.get("/coupon/getShortUrl", {url: url}, function(r) {
        if(r.code === 200) {
            $("#couponUrl").val(r.data);
            //给出提示
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

// 智能补贴
$("#btn_intel").click(function () {
    intelCouponData();
    $( "#intel_coupon_modal" ).modal( 'show' );
});

// 获取智能补贴信息
function intelCouponData() {
    var settings = {
        url: '/coupon/getIntelCouponList',
        cache: false,
        pagination: false,
        singleSelect: false,
        clickToSelect: true,
        rowStyle: couponRowStyle,
        columns: [{
            checkbox: true,
            formatter: function (value, row, index) {
                var couponId = row['couponId'];
                if(couponId === 1) {
                    return {
                        checked: true//设置选中
                    };
                }else {
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
                if(row['couponDenom'] !== '' && row['couponDenom'] !== null && row['couponDenom'] !== undefined) {
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
                if(couponId === 1) {
                    return '否'
                }else {
                    return '是'
                }
            }
        }, {
            title: '处理方式',
            align: 'center',
            formatter: function (value, row, index) {
                var couponId = row['couponId'];
                if(couponId === 1) {
                    return '新增'
                }else {
                    return '忽略'
                }
            }
        }]
    };
    $( '#intelCouponTable' ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

function couponRowStyle(row, index) {
    if (row['couponId'] === 0) {
        return {
            classes: 'info'
        };
    }
    return {};
}

function saveIntelCoupon() {
    $MB.confirm({
        title: '提示：',
        content: '确定执行当前操作？'
    }, function () {
        var selected = $("#intelCouponTable").bootstrapTable( 'getSelections' );
        var coupon = [];
        selected.forEach((v,k)=>{
            if(v['couponId'] === 1) {
                var tmp = {};
                tmp.couponDenom = v['couponDenom'];
                tmp.couponThreshold = v['couponThreshold'];
                coupon.push(tmp);
            }
        });
        $.get("/coupon/getCalculatedCoupon", {coupon: JSON.stringify(coupon)}, function (r) {
            if(r.code === 200) {
                $MB.n_success("智能补贴更新成功。");
                $( "#intel_coupon_modal" ).modal( 'hide' );
                $( "#coupon_modal" ).modal( 'show' );
                couponTable($("#currentGroupId").val());
            }
        });
    });
}

// 用来解决编辑情况下，日期插件的值会清空的问题
var VALID_END;
function resetValidEndVal() {
    if(VALID_END !== undefined && VALID_END !== '') {
        $("#validEnd").val(VALID_END);
        VALID_END = "";
    }
}