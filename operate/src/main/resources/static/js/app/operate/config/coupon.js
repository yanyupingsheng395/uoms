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
                title: '补贴适合用户群组',
                colspan: 3
            },
            {
                title: '补贴信息设置',
                colspan: 3
            },
            {
                title: '3方平台建立补贴并录入',
                colspan: 4
            },
            {
                title: '补贴检验',
                colspan: 4
            }
        ],
            [{
                field: 'userValue',
                title: '用户在类目的价值',
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
                title: '用户在类目上的生命周期阶段',
                formatter:function (value, row, index) {
                    var res = [];
                    if(value !== undefined && value !== ''&& value !== null) {
                        value.split(",").forEach((v,k)=>{
                            switch (v) {
                                case "0":
                                    res.push("老客");
                                    break;
                                case "1":
                                    res.push("新客");
                                    break;
                            }
                        });
                    }
                    return res.length === 0 ? '-' : res.join(",");
                }
            }, {
                field: 'pathActive',
                title: '用户在类目特定购买次序的活跃度',
                formatter:function (value, row, index) {
                    var res = [];
                    if(value !== undefined && value !== ''&& value !== null) {
                        value.split(",").forEach((v,k)=>{
                            switch (v) {
                                case "UAC_01":
                                    res.push("高度活跃");
                                    break;
                                case "UAC_02":
                                    res.push("中度活跃");
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
                                case "UAC_06":
                                    res.push("沉睡");
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
                title: '有效截止期至'
            }, {
                field: 'validStatus',
                title: '是否有效',
                formatter: function (value, row, index) {
                    if(value === 'Y') {
                        return "是";
                    }else if(value === 'N'){
                        return "否";
                    }
                    return "";
                }
            }, {
                field: 'checkFlag',
                title: '校验结果',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value === '1') {
                        return "<font color='green'><i class='fa fa-check'></i></font>";
                    }
                    if(value === '0') {
                        return "<font color='red'><i class='fa fa-close'></i></font>";
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
            if(!validCoupon()) {
                $MB.n_warning("当前日期大于有效截止日期，'券是否有效'的更改无效！");
                return;
            }
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
    var couponId = selected[0].couponId;
    $("#couponValid").show();
    $.post("/coupon/getByCouponId", {"couponId": couponId}, function (r) {
        if (r.code === 200) {
            var $form = $('#coupon_edit');
            $("#add_modal").modal('show');
            var coupon = r.data;
            $("#myLargeModalLabel").html('修改补贴');
            $form.find("input[name='couponId']").val(coupon.couponId);
            $form.find("input[name='couponName']").val(coupon.couponName).attr("readonly", true);
            $form.find("input[name='couponDenom']").val(coupon.couponDenom).attr("readonly", true);
            $form.find("input[name='couponThreshold']").val(coupon.couponThreshold).attr("readonly", true);
            $form.find("input[name='couponInfo2']").val(coupon.couponInfo2);
            $form.find("input[name='couponUrl']").val(coupon.couponUrl);
            $form.find("input[name='couponNum']").val(coupon.couponNum);
            $form.find("input[name='couponDisplayName']").val(coupon.couponDisplayName);
            $form.find("input[name='validEnd']").val(coupon.validEnd);
            $("input[name='validStatus']:radio[value='"+coupon.validStatus+"']").prop("checked", true);

            coupon.userValue.split(',').forEach((v,k)=>{
                $("input[name='userValue']:checkbox[value='" + v + "']").prop("checked", true);
            });
            coupon.lifeCycle.split(',').forEach((v,k)=>{
                $("input[name='lifeCycle']:checkbox[value='" + v + "']").prop("checked", true);
            });
            coupon.pathActive.split(',').forEach((v,k)=>{
                $("input[name='pathActive']:checkbox[value='" + v + "']").prop("checked", true);
            });
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
                required: $("#validUrl").val() === 'B'
            },
            couponUrl: {
                required: $("#validUrl").val() === 'B'
            },
            couponDisplayName: {
                required: true,
                remote: {
                    url: "/coupon/validCouponNameLen",
                    type: "get",
                    dataType: "json",
                    data: {
                        couponName: function () {
                            return $("#couponDisplayName").val();
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
            couponName: icon + "请输入名称",
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
                remote: icon + "长度不能超过"+couponNameLen+"个字符"
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