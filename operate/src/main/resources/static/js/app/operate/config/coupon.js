var validator;
var $couponForm = $("#coupon_edit");
init_date('validEnd', 'yyyy-mm-dd', 0,2,0);
$("#validEnd").datepicker('setStartDate', new Date());
$(function () {
    validateRule();
    var settings = {
        url: "/coupon/list",
        method: 'post',
        cache: false,
        pagination: true,
        singleSelect: false,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,  //页码
                param: {validStatus: $("select[name='validStatus']").val()}
            };
        },
        columns: [[
            {
                checkbox: true,
                rowspan: 2
            },
            {
                title: '补贴适用用户群组',
                colspan: 3
            },
            {
                title: '补贴信息设置',
                colspan: 4
            },
            {
                title: '3方平台建立补贴并录入',
                colspan: 2
            },
            {
                title: '补贴检验',
                colspan: 2
            }
        ],
            [{
                field: 'userValue',
                title: '价值',
                formatter:function (value, row, index) {
                    var res = [];
                    if(value !== undefined && value !== ''&& value !== null) {
                        value.split(",").forEach((v,k)=>{
                            switch (v) {
                                case "ULC_01":
                                    res.push("重要");
                                    break;
                                case "ULC_02":
                                    res.push("主要");
                                    break;
                                case "ULC_03":
                                    res.push("普通");
                                    break;
                                case "ULC_04":
                                    res.push("长尾");
                                    break;
                            }
                        });
                    }
                    return res.length === 0 ? '-' : res.join(",");
                }
            }, {
                field: 'lifeCycle',
                title: '生命周期阶段',
                formatter:function (value, row, index) {
                    var res = [];
                    if(value !== undefined && value !== ''&& value !== null) {
                        value.split(",").forEach((v,k)=>{
                            switch (v) {
                                case "0":
                                    res.push("复购用户");
                                    break;
                                case "1":
                                    res.push("新用户");
                                    break;
                            }
                        });
                    }
                    return res.length === 0 ? '-' : res.join(",");
                }
            }, {
                field: 'pathActive',
                title: '下步成长旅程活跃度',
                formatter:function (value, row, index) {
                    var res = [];
                    if(value !== undefined && value !== ''&& value !== null) {
                        value.split(",").forEach((v,k)=>{
                            switch (v) {
                                case "UAC_01":
                                    res.push("活跃");
                                    break;
                                case "UAC_02":
                                    res.push("留存");
                                    break;
                                case "UAC_03":
                                    res.push("流失预警");
                                    break;
                                case "UAC_04":
                                    res.push("弱流失");
                                    break;
                                case "UAC_05":
                                    res.push("强流失");
                                    break;
                            }
                        });
                    }
                    return res.length === 0 ? '-' : res.join(",");
                }
            },{
                field: 'couponSource',
                title: '补贴类型',
                formatter: function (value, row, index) {
                    var res = '-';
                    if(value === '0') {
                        res = "智能";
                    }
                    if(value === '1') {
                        res = "手动";
                    }
                    return res;
                }
            },{
                field: 'couponDisplayName',
                title: '补贴名称(文案中体现)'
            }, {
                field: 'couponThreshold',
                title: '补贴门槛(元)'
            },{
                field: 'couponDenom',
                title: '补贴面额(元)'
            }, {
                field: 'couponUrl',
                title: '补贴短链接',
                formatter: function (value, row, index) {
                    if(value !== undefined && value !== null) {
                        return "<a href='" + value + "' style='color: #48b0f7;border-bottom: solid 1px #48b0f7'>" + value + "</a>";
                    }else {
                        return "-";
                    }
                }
            }, {
                field: 'validEnd',
                title: '有效截止期'
            }, {
                field: 'checkFlag',
                title: '校验结果',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value === '1') {
                        return "<span class=\"badge bg-success\">通过</span>";
                    }
                    if(value === '0') {
                        return "<span class=\"badge bg-danger\">未通过</span>";
                    }
                    return "-";
                }
            }, {
                field: 'checkComments',
                title: '失败原因'
            }]]
    };

    $MB.initTable('couponTable', settings);
    //为刷新按钮绑定事件
    $("#btn_refresh").on("click", function () {
        $('#couponTable').bootstrapTable('refresh');
    });
});

function searchCoupon() {
    $MB.refreshTable('couponTable');
}

function resetCoupon() {
    $("#validStatus").find("option:selected").removeAttrs("selected");
    $MB.refreshTable('couponTable');
}

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
    $("input[name='userValue']:checked").removeAttr("checked");
    $("input[name='lifeCycle']:checked").removeAttr("checked");
    $("input[name='pathActive']:checked").removeAttr("checked");
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
            $form.find("input[name='couponDenom']").val(coupon.couponDenom).attr("readonly", true);
            $form.find("input[name='couponThreshold']").val(coupon.couponThreshold).attr("readonly", true);
            $form.find("input[name='couponInfo2']").val(coupon.couponInfo2);
            $form.find("input[name='couponUrl']").val(coupon.couponUrl);
            $form.find("input[name='couponNum']").val(coupon.couponNum);
            $form.find("input[name='couponDisplayName']").val(coupon.couponDisplayName).attr("readonly", true);
            $form.find("input[name='validEnd']").val(coupon.validEnd);
            VALID_END = coupon.validEnd;
            $("input[name='validStatus']:radio[value='"+coupon.validStatus+"']").prop("checked", true);

            if(coupon.userValue !== null) {
                coupon.userValue.split(',').forEach((v,k)=>{
                    $("input[name='userValue']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }
            if(coupon.lifeCycle !== null) {
                coupon.lifeCycle.split(',').forEach((v,k)=>{
                    $("input[name='lifeCycle']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }
            if(coupon.pathActive !== null) {
                coupon.pathActive.split(',').forEach((v,k)=>{
                    $("input[name='pathActive']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }
            $("#btn_save").attr("name", "update");
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
                    return ($("#validUrl").val() === 'A');
                }
            },
            couponUrl: {
                required: function () {
                    return ($("#validUrl").val() === 'A');
                }
            },
            couponDisplayName: {
                required: true,
                maxlength: couponNameLen,
                remote: {
                    url: "/coupon/checkCouponName",
                    type: "get",
                    dataType: "json",
                    data: {
                        couponDisplayName: function () {
                            return $("#couponDisplayName").val();
                        },
                        operate: function () {
                            return $("#btn_save").attr("name");
                        }
                    }
                }
            },
            validEnd: {
                required: true
            },
            couponNum: {
                required: true,
                digits: true
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
            couponName: {
                required: icon + "请输入名称",
                remote: icon + "补贴名称已存在"
            },

            couponDenom: {
                required: icon + "请输入面额",
                digits: icon + "只能是整数"
            },
            couponThreshold: {
                required: icon + "请输入门槛",
                digits: icon + "只能是整数"
            },
            couponInfo2: icon + "请输入长链",
            couponUrl: icon + "请输入短链",
            couponDisplayName: {
                required: icon + "请输入引用名",
                maxlength: icon + "最大长度不能超过"+couponNameLen+"个字符",
                remote: icon + "补贴名称已存在"
            },
            validEnd: icon + "请输入截止日期",
            couponNum: {
                required: icon + "请输入数量",
                digits: icon + "只能是整数"
            }
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
    $.get("/coupon/getCalculatedCoupon", {}, function (r) {
        if(r.code === 200) {
            $MB.n_success("智能补贴获取成功。");
        }
        $MB.refreshTable('couponTable');
    });
});


// 用来解决编辑情况下，日期插件的值会清空的问题
var VALID_END;
function resetValidEndVal() {
    if(VALID_END !== undefined && VALID_END !== '') {
        $("#validEnd").val(VALID_END);
        VALID_END = "";
    }
}