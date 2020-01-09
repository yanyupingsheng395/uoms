let USER_VALUE = {ULC_01: '重要',ULC_02: '主要',ULC_03: '普通',ULC_04: '长尾'};
let USER_LIFE_CYCLE = {0: '老客',1: '新客'};
let PATH_ACTIVE = {UAC_01: '高度活跃', UAC_02: '中度活跃', UAC_03: '流失预警', UAC_04: '弱流失', UAC_05: '强流失', UAC_06: '沉睡'};
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
        columns: [[
            {
                checkbox: true,
                rowspan: 2
            }, {
                title: '用户在类目上成长的相关特征',
                colspan: 4
            }, {
                title: '为个性化推送配置要素',
                colspan: 3
            }, {
                title: '配置后的预览与校验',
                colspan: 4
            }
        ], [{
            field: 'userValue',
            title: '用户在类目的价值',
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
            title: '用户在类目上的生命周期阶段',
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
            title: '用户在类目特定购买次序的活跃度',
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
            field: 'groupInfo',
            title: '理解用户'
        },{
            title: '文案',
            align: 'center',
            formatter: function (value, row, index) {
                // 获取群组信息
                let groupInfo = row['userValue'] + "|" + row['lifecycle'] + "|" + row['pathActive'] + "|" + row['groupInfo'];
                return "<a onclick='editSmsContent(\""+row['groupId']+"\", \"" + groupInfo + "\")' style='color: #333;'><i class=\"fa fa-envelope-o\"></i></a>";
            }
        },{
            title: '补贴',
            align: 'center',
            formatter: function (value, row, index) {
                return "<a class='coupon' onclick='editCoupon()' disabled='disabled' style='pointer-events:none;color: #ccc;'><i class=\"fa fa-credit-card\"></i></a>";
            }
        },{
            field: 'timeAndShop',
            title: '时间与商品',
            formatter: function (value, row, index) {
                return "<a href='/page/insight' style='color: #48b0f7;text-decoration: underline;'>系统配置</a>";
            }
        }, {
            field: 'checkFlag',
            title: '校验结果',
            align: 'center',
            formatter: function (value, row, index) {
                if(value === 'Y') {
                    return "<font color='green'><i class='fa fa-check'></i></font>";
                }
                if(value === 'N') {
                    return "<font color='red'><i class='fa fa-close'></i></font>";
                }
                return "-";
            }
        }, {
            field: 'checkComments',
            title: '失败原因'
        }, {
            field: 'smsContent',
            title: '预览文案',
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
            title: '预览补贴'
        }]],
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
            $("#dailyGroupTable").bootstrapTable('mergeCells', {
                index: 0,
                field: "timeAndShop",
                colspan: 1,
                rowspan: 24
            });
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
    var index = 0;
    for (var prop in sortMap) {
        var count = sortMap[prop] * 1;
        $(target).bootstrapTable('mergeCells', {index: index, field: fieldName, colspan: colspan, rowspan: count});
        index += count;
    }
}

// 编辑短信内容
function editSmsContent(groupId, groupInfo) {
    $("#currentGroupId").val(groupId);
    $("#currentGroupInfo").val(groupInfo);
    $("#msg_modal").modal('show');
}

$("#msg_modal").on('hide.bs.modal', function () {
    // $("#currentGroupId").val("");
    // $("#currentGroupInfo").val("");
});

$("#msg_modal").on('shown.bs.modal', function () {
    var groupId = $("#currentGroupId").val();
    smsTemplateTable(groupId);

    var groupInfo = $("#currentGroupInfo").val();
    var code = "<tr>";
    var groupInfoArr = groupInfo.split("|");
    groupInfoArr.forEach((v,k)=>{
        code += "<td>";
        if(k === 0) {
            code += v === 'undefined' ? '-': USER_VALUE[v];
        }
        if(k === 1) {
            code += v === 'undefined' ? '-':USER_LIFE_CYCLE[v];
        }
        if(k === 2) {
            code += v === 'undefined' ? '-':PATH_ACTIVE[v];
        }
        if(k === 3) {
            code += v === 'undefined' ? '-':v;
        }
        code += "</td>";
    });
    code += "</tr>";
    $("#selectedGroupInfo").html('').append(code);
});

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
        singleSelect: true,
        columns: [
            [
                {
                    checkbox: true,
                    rowspan: 2
                }, {
                field: 'smsName',
                title: '文案名称',
                rowspan: 2,
                valign: "middle",
                clickToSelect: true
            }, {
                title: '适用人群',
                colspan: 4
            }, {
                field: 'smsContent',
                title: '文案内容',
                rowspan: 2,
                valign: "middle"
            }
            ], [
                {
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
                }, {
                    field: 'isCoupon',
                    title: '有无补贴',
                    formatter: function (value, row, index) {
                        let res = "-";
                        if (value === '1') {
                            res = "有";
                        }
                        if (value === '0') {
                            res = "无";
                        }
                        return res;
                    }
                }
            ]
        ]
    };
    var url = "/smsTemplate/getTemplateByGroupId";
    $("#smsTemplateTable").bootstrapTable(settings);
    $.get(url, {groupId: groupId}, function (r) {
        $("#smsTemplateTable").bootstrapTable('load', r.data);
    });
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
                pageNum: (params.offset / params.limit) + 1,
                param: {validStatus: "Y"}
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
        $MB.n_warning('请选择文案！');
        return;
    }
    var smsCode = selected[0].smsCode;
    var groupId = $("#currentGroupId").val();
    if(!(groupId !== undefined && groupId !== '')) {
        $MB.n_warning("请选择一条群组信息。");
        $("#msg_modal").modal('hide');
        return;
    }
    $.get('/daily/setSmsCode', {groupId: groupId, smsCode: smsCode}, function (r) {
        if (r.code === 200) {
            $MB.n_success("更改文案成功！");
        } else {
            $MB.n_danger("更改文案失败！发生未知异常！");
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

// 验证群组信息, 没有验证通过则会阻止进入编辑页
function validUserGroup() {
    $.get("/daily/validUserGroup", {}, function (r) {
        if(r.code === 200) {
            $MB.refreshTable('dailyGroupTable');
            $MB.n_success("群组信息已全部验证完毕！");
        }else {
            $MB.n_warning("未知错误！");
        }
    });
}