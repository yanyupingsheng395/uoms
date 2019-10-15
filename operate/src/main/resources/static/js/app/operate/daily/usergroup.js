$(function () {
    initTable();
});

function initTable() {
    var settings = {
        url: '/daily/userGroupListPage',
        pagination: true,
        singleSelect: false,
        sidePagination: "server",
        pageSize: 25,
        pageList: [25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'groupId',
            title: '组ID',
            visible: false
        }, {
            field: 'userValue',
            title: '用户价值',
            align: 'center',
            valign: "middle",
            formatter: function (value, row, index) {
                var res = "";
                switch (value) {
                    case "ULC_01":
                        res = "重要";
                        break;
                    case "ULC_02":
                        res = "主要";
                        break;
                    case "ULC_03":
                        res = "普通";
                        break;
                    case "ULC_04":
                        res = "长尾";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        }, {
            field: 'lifecycle',
            title: '生命周期',
            align: 'center',
            valign: "middle",
            formatter: function (value, row, index) {
                if (value == "1") {
                    return "新客";
                }
                if (value == "0") {
                    return "老客";
                }
                return "";
            }
        }, {
            field: 'pathActive',
            title: '活跃度',
            formatter: function (value, row, index) {
                var res = "";
                switch (value) {
                    case "UAC_01":
                        res = "高度活跃";
                        break;
                    case "UAC_02":
                        res = "中度活跃";
                        break;
                    case "UAC_03":
                        res = "流失预警";
                        break;
                    case "UAC_04":
                        res = "弱流失";
                        break;
                    case "UAC_05":
                        res = "强流失";
                        break;
                    case "UAC_06":
                        res = "沉睡";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        }, {
            field: 'smsContent',
            title: '模板内容',
            formatter: function (value, row, index) {
                if (value != null && value != undefined) {
                    let temp = value.length > 20 ? value.substring(0, 20) + "..." : value;
                    return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                } else {
                    return '-';
                }
            }
        }, {
            field: 'couponName',
            title: '券名称'
        }, {
            field: 'isCoupon',
            title: '是否有券',
            formatter: function (value, row, index) {
                if (value == '1') {
                    return '是';
                }
                if (value == '0') {
                    return '否';
                }
                return '-';
            }
        }, {
            title: '<i class="fa fa-edit"></i>&nbsp;操作',
            formatter: function (value, row, index) {
                let code = "-";
                if (row['isCoupon'] == '1') {
                    code = "<div class=\"btn-group\">\n" +
                        "<button class=\"btn  btn-sm btn-secondary\" onclick='editSmsContent(" + row['groupId'] + ", \"" + row['groupName'] + "\")'><i class=\"fa fa-envelope-o\"></i></button>\n" +
                        "<button class=\"btn btn-sm btn-secondary\" onclick='editCoupon(" + row['groupId'] + ", \"" + row['groupName'] + "\")'><i class=\"fa fa-credit-card\"></i></button>\n" +
                        "</div>";
                }
                if (row['isCoupon'] == '0') {
                    code = "<div class=\"btn-group\">\n" +
                        "<button class=\"btn  btn-sm btn-secondary\" onclick='editSmsContent(" + row['groupId'] + ", \"" + row['groupName'] + "\")'><i class=\"fa fa-envelope-o\"></i></button>\n" +
                        "</div>";
                }
                return code;
            }
        }],
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();

            // 合并单元格
            let data = $('#dailyGroupTable').bootstrapTable('getData', true);
            mergeCells(data, "userValue", 1, $('#dailyGroupTable'));
            for (let i = 0; i <= 8; i++) {
                $("#dailyGroupTable").bootstrapTable('mergeCells', {
                    index: i * 3,
                    field: "lifecycle",
                    colspan: 1,
                    rowspan: 3
                });
            }
        }
    };
    $MB.initTable('dailyGroupTable', settings);
}

// 合并单元格
function mergeCells(data, fieldName, colspan, target) {
    //声明一个map计算相同属性值在data对象出现的次数和
    var sortMap = {};
    for (var i = 0; i < data.length; i++) {
        for (var prop in data[i]) {
            if (prop == fieldName) {
                var key = data[i][prop]
                if (sortMap.hasOwnProperty(key)) {
                    sortMap[key] = sortMap[key] * 1 + 1;
                } else {
                    sortMap[key] = 1;
                }
                break;
            }
        }
    }
    for (var prop in sortMap) {
        console.log(prop, sortMap[prop])
    }
    var index = 0;
    for (var prop in sortMap) {
        var count = sortMap[prop] * 1;
        $(target).bootstrapTable('mergeCells', {index: index, field: fieldName, colspan: colspan, rowspan: count});
        index += count;
    }
}

// 编辑短信内容
function editSmsContent(groupId, groupName) {
    $("#groupName").show();
    $("#currentGroupName").text(groupName);
    $("#msg_modal").modal('show');
    smsTemplateTable(groupId);
    $("#currentGroupId").val(groupId);
}

// 编辑优惠券
function editCoupon(groupId, groupName) {
    $("#couponGroupName").show();
    $("#currentCouponGroupName").text(groupName);
    $("#coupon_modal").modal('show');
    couponTable(groupId);
    $("#currentCouponGroupId").val(groupId);
}

/**
 * 获取短信模板列表
 */
function smsTemplateTable(groupId) {
    var settings = {
        url: "/smsTemplate/list",
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
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1,
                param: {groupId: groupId}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'smsCode',
            title: '模板编码',
            visible: false
        }, {
            field: 'smsContent',
            title: '模板内容'
        }]
    };
    $("#smsTemplateTable").bootstrapTable('destroy');
    $MB.initTable('smsTemplateTable', settings);
}

/**
 * 获取短信模板列表
 */
function couponTable(groupId) {
    var settings = {
        url: "/coupon/list",
        method: 'post',
        cache: false,
        pagination: true,
        singleSelect: false,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 25,            //每页的记录行数（*）
        pageList: [25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'couponId',
            title: 'ID'
        }, {
            field: 'couponName',
            title: '优惠券名称'
        }, {
            field: 'couponUrl',
            title: '优惠券URL'
        }],
        onLoadSuccess: function () {
            $.get("/coupon/getCouponIdsByGroupId", {groupId: groupId}, function (r) {
                let data = r.data;
                $('#couponTable').bootstrapTable("checkBy", {field: 'couponId', values: data});
            });
        }
    };
    $("#couponTable").bootstrapTable('destroy');
    $MB.initTable('couponTable', settings);
}

function setSmsCode() {
    var selected = $("#smsTemplateTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择短信模板！');
        return;
    }
    var smsCode = selected[0].smsCode;
    var groupId = $("#currentGroupId").val();
    $.get('/daily/setSmsCode', {groupId: groupId, smsCode: smsCode}, function (r) {
        if (r.code === 200) {
            $MB.n_success("更改短信模板成功！");
        } else {
            $MB.n_danger("更改短信模板失败！发生未知异常！");
        }
        $("#msg_modal").modal('hide');
        $MB.refreshTable('dailyGroupTable');
    });
}

function batchUpdateTemplate() {
    $("#groupName").hide();
    var selected = $("#dailyGroupTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要编辑模板的组！');
        return;
    }
    let isCoupons = [];
    let groupIds = [];
    selected.forEach((v, k) => {
        groupIds.push(v.groupId);
        isCoupons.push(v.isCoupon);
    });

    if (isCoupons.indexOf('1') > -1 && isCoupons.indexOf('0') > -1) { // 有券
        $MB.n_warning("同时包含有券无券模板，无法批量操作！");
        return;
    }
    $("#currentGroupId").val(groupIds.join(","));
    $("#msg_modal").modal('show');
    smsTemplateTable(groupIds[0]);
}

/**
 * 根据groupId更新优惠券
 */
function updateCouponId() {
    var selected = $("#couponTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择优惠券！');
        return;
    }

    let couponId = [];
    selected.forEach((v, k) => {
        couponId.push(v['couponId']);
    });

    let groupId = $("#currentCouponGroupId").val();
    $.get('/coupon/updateCouponId', {groupId: groupId, couponId: couponId.join(",")}, function (r) {
        if (r.code === 200) {
            $MB.n_success("更改优惠券成功！");
        } else {
            $MB.n_danger("更改优惠券失败！发生未知异常！");
        }
        $("#coupon_modal").modal('hide');
        $MB.refreshTable('dailyGroupTable');
    });
}

// 删除券关系
function deleteCoupon() {

    var selected = $("#dailyGroupTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要删除券的组！');
        return;
    }
    let groupIds = [];
    selected.forEach((v, k) => {
        groupIds.push(v.groupId);
    });

    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除选中的券关系？'
    }, function () {
        $.getJSON("/coupon/deleteCoupon?id="+groupIds,function (resp) {
            if (resp.code === 200){
                lightyear.loading('hide');
                //提示成功
                $MB.n_success('删除成功!');
                //刷新表格
                $('#dailyGroupTable').bootstrapTable('refresh');
            }else {
                $MB.n_danger("未知异常！");
            }
        })
    });
}