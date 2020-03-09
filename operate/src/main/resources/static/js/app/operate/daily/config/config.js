let USER_VALUE = {ULC_01: '重要',ULC_02: '主要',ULC_03: '普通',ULC_04: '长尾'};
let USER_LIFE_CYCLE = {0: '复购用户',1: '新用户'};
let PATH_ACTIVE = {UAC_01: '促活节点', UAC_02: '留存节点', UAC_03: '弱流失预警', UAC_04: '强流失预警', UAC_05: '沉睡预警'};
$(function () {
    initTable();
});

function initTable() {
    var settings = {
        pagination: false,
        singleSelect: false,
        columns: [[
            {
                checkbox: true,
                rowspan: 2
            }, {
                title: '用户在类目上成长的相关特征',
                colspan: 3
            }, {
                title: '为个性化推送配置要素',
                colspan: 4
            }, {
                title: '配置后的预览与校验',
                colspan: 4
            }
        ], [{
            field: 'userValue',
            title: '价值',
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
            title: '生命周期阶段',
            align: 'center',
            valign: "middle",
            formatter: function (value, row, index) {
                if (value == "1") {
                    return "新用户";
                }
                if (value == "0") {
                    return "复购用户";
                }
                return "";
            }
        }, {
            field: 'pathActive',
            title: '下步成长节点',
            formatter: function (value, row, index) {
                var res = "";
                switch (value) {
                    case "UAC_01":
                        res = "促活节点";
                        break;
                    case "UAC_02":
                        res = "留存节点";
                        break;
                    case "UAC_03":
                        res = "弱流失预警";
                        break;
                    case "UAC_04":
                        res = "强流失预警";
                        break;
                    case "UAC_05":
                        res = "沉睡预警";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        }, {
            field: 'groupInfo',
            title: '理解用户',
            align: 'center',
            formatter: function (value, row, index) {
                return "<a style='color: #333;cursor:pointer;' onclick='userInsight(\""+row['userValue']+"\",\""+ row['pathActive']+"\",\""+ row['lifecycle']+"\")'><i class='mdi mdi-account mdi-14px'></i></a>";
            }
        },{
            title: '文案',
            align: 'center',
            formatter: function (value, row, index) {
                // 获取群组信息
                let groupInfo = row['userValue'] + "|" + row['lifecycle'] + "|" + row['pathActive'] + "|" + row['groupInfo'];
                return "<a onclick='editSmsContent(\""+row['groupId']+"\", \"" + groupInfo + "\",  \"" + row.smsCode + "\")' style='color: #333;'><i class=\"fa fa-envelope-o\"></i></a>";
            }
        },{
            field: 'isCoupon',
            title: '补贴',
            align: 'center',
            formatter: function (value, row, index) {
                var res = '-';
                if(value === '0') {
                    res = "<a class='coupon' disabled='disabled' style='pointer-events:none;color: #ccc;'><i class=\"fa fa-credit-card\"></i></a>";
                }
                if(value === '1') {
                    let groupInfo = row['userValue'] + "|" + row['lifecycle'] + "|" + row['pathActive'] + "|" + row['groupInfo'];
                    res = "<a class='coupon' onclick='editCoupon(\""+row['groupId']+"\", \"" + groupInfo + "\")' style='color: #333;'><i class=\"fa fa-credit-card\"></i></a>";
                }
                return res;
            }
        },{
            field: 'timeAndShop',
            title: '时间与商品',
            align: 'center',
            valign: 'top',
            formatter: function (value, row, index) {
                return "<a href='/page/insight' style='color: #48b0f7;text-decoration: underline;'>系统配置</a>";
            }
        }, {
            field: 'checkFlag',
            title: '校验结果',
            align: 'center',
            formatter: function (value, row, index) {
                if(value === 'Y') {
                    return "<span class=\"badge bg-success\">通过</span>";
                }
                if(value === 'N') {
                    return "<span class=\"badge bg-danger\">未通过</span>";
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
                    return '<a style=\'color: #676a6c;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                } else {
                    return '-';
                }
            }
        }, {
            field: 'couponName',
            title: '预览补贴'
        }]]
    };
    $("#dailyGroupTable").bootstrapTable('destroy').bootstrapTable(settings);
    $.get("/daily/userGroupList", {}, function (r) {
        var dataList = r.data;
        var total = dataList.length;
        var limit = total/8;
        $("#dailyGroupTable").bootstrapTable('load', dataList);
        $("a[data-toggle='tooltip']").tooltip();
        // 合并单元格
        let data = $('#dailyGroupTable').bootstrapTable('getData', true);
        mergeCells(data, "userValue", 1, $('#dailyGroupTable'));
        for (let i = 0; i <= 8; i++) {
            $("#dailyGroupTable").bootstrapTable('mergeCells', {
                index: i * limit,
                field: "lifecycle",
                colspan: 1,
                rowspan: limit
            });
        }
        $("#dailyGroupTable").bootstrapTable('mergeCells', {
            index: 0,
            field: "timeAndShop",
            colspan: 1,
            rowspan: total
        });
    });
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

let SMS_CODE;
// 编辑短信内容
function editSmsContent(groupId, groupInfo, smsCode) {
    SMS_CODE = smsCode;
    $("#currentGroupId").val(groupId);
    $("#currentGroupInfo").val(groupInfo);
    getSelectedGroupInfo('selectedGroupInfo1');
    $("#msg_modal").modal('show');
}


// 编辑优惠券
function editCoupon(groupId, groupInfo, smsCode) {
    $("#currentGroupId").val(groupId);
    $("#currentGroupInfo").val(groupInfo);
    //设置默认选中
    setUserGroupChecked();
    $("#coupon_modal").modal('show');
}

let COUPON_IDS;
$("#coupon_modal").on('shown.bs.modal', function () {
    var groupId = $("#currentGroupId").val();
    couponTable(groupId);
    getSelectedGroupInfo('selectedGroupInfo2');
});

$("#msg_modal").on('shown.bs.modal', function () {
    smsTemplateTable();
});

$("#msg_modal").on('hidden.bs.modal', function () {
    resetSms();
});

// 获取弹窗群组信息
function getSelectedGroupInfo(tableId) {
    $('#' + tableId).find('tbody tr').each(function (i, tr) {
        $(tr).hide();
    });
    var groupInfo = $("#currentGroupInfo").val();
    var groupInfoArr = groupInfo.split("|");
    groupInfoArr.forEach((v,k)=>{
        if(k === 0) {
            var tmp = String((v === 'undefined' || v === 'null') ? '-': USER_VALUE[v]);
            $('#' + tableId).find('td').each(function (i, td) {
                if($(td).text() === tmp) {
                    $(td).parent().show();
                }
            });
        }
        if(k === 1) {
            $('#' + tableId).find('td').each(function (i, td) {
                if($(td).text() === ((v === 'undefined' || v === 'null') ? '-': USER_LIFE_CYCLE[v])) {
                    $(td).parent().show();
                }
            });
        }
        if(k === 2) {
            $('#' + tableId).find('td').each(function (i, td) {
                if($(td).text() === ((v === 'undefined' || v === 'null') ? '-': PATH_ACTIVE[v])) {
                    $(td).parent().show();
                }
            });
        }
    });
}

function smsRowStyle(row, index) {
    if(SMS_CODE != undefined && SMS_CODE != null && row.smsCode === SMS_CODE) {
        return {
            classes: 'success'
        };
    }
    return {};
}
/**
 * 获取短信模板列表
 */
function smsTemplateTable() {
    var settings = {
        clickToSelect: true,
        rowStyle: smsRowStyle,
        singleSelect: true,
        columns: [
            [
                {
                    checkbox: true,
                    rowspan: 2,
                    formatter: function (value, row, index) {
                        if(SMS_CODE != undefined && SMS_CODE != null && row.smsCode === SMS_CODE) {
                            return {
                                checked : true//设置选中
                            };
                        }
                    }
                }, {
                field: 'smsName',
                title: '文案名称',
                rowspan: 2,
                valign: "middle"
            }, {
                title: '文案适用用户群组',
                colspan: 3
            }, {
                field: 'smsContent',
                title: '文案内容',
                rowspan: 2,
                valign: "middle"
            }
            ], [
                {
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
                    title: '下步成长节点',
                    formatter:function (value, row, index) {
                        var res = [];
                        if(value !== undefined && value !== ''&& value !== null) {
                            value.split(",").forEach((v,k)=>{
                                switch (v) {
                                    case "UAC_01":
                                        res.push("促活节点");
                                        break;
                                    case "UAC_02":
                                        res.push("留存节点");
                                        break;
                                    case "UAC_03":
                                        res.push("弱流失预警");
                                        break;
                                    case "UAC_04":
                                        res.push("强流失预警");
                                        break;
                                    case "UAC_05":
                                        res.push("沉睡预警");
                                        break;
                                }
                            });
                        }
                        return res.length === 0 ? '-' : res.join(",");
                    }
                }
            ]
        ]
    };
    var url = "/smsTemplate/getTemplate";
    $("#smsTemplateTable").bootstrapTable(settings);
    var userValue = $("#sms-form").find("select[name='userValue']").find("option:selected").val();
    var pathActive = $("#sms-form").find("select[name='pathActive']").find("option:selected").val();
    var lifeCycle = $("#sms-form").find("select[name='lifeCycle']").find("option:selected").val();
    $.get(url, {userValue:userValue, pathActive: pathActive, lifeCycle: lifeCycle, smsCode:SMS_CODE}, function (r) {
        $("#smsTemplateTable").bootstrapTable('load', r.data);
    });
}

function searchSms() {
    smsTemplateTable();
}

function resetSms() {
    $("#sms-form").find("select[name='userValue']").find("option:selected").removeAttr('selected');
    $("#sms-form").find("select[name='pathActive']").find("option:selected").removeAttr('selected');
    $("#sms-form").find("select[name='lifeCycle']").find("option:selected").removeAttr('selected');
    smsTemplateTable();
}

/**
 * 获取优惠券列表
 */
function couponTable(groupId) {
    var settings = {
        cache: false,
        pagination: false,
        singleSelect: false,
        rowStyle: rowStyle,
        clickToSelect:true,
        columns: [[
            {
                checkbox: true,
                rowspan: 2
         //       formatter : stateFormatter
            },
            {
                title: '补贴适用用户群组',
                colspan: 3
            },
            {
                title: '补贴信息设置',
                colspan: 5
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
                title: '下步成长节点',
                formatter:function (value, row, index) {
                    var res = [];
                    if(value !== undefined && value !== ''&& value !== null) {
                        value.split(",").forEach((v,k)=>{
                            switch (v) {
                                case "UAC_01":
                                    res.push("促活节点");
                                    break;
                                case "UAC_02":
                                    res.push("留存节点");
                                    break;
                                case "UAC_03":
                                    res.push("弱流失预警");
                                    break;
                                case "UAC_04":
                                    res.push("强流失预警");
                                    break;
                                case "UAC_05":
                                    res.push("沉睡预警");
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
            }, {
                field: 'isRec',
                title: '是否推荐',
                formatter: function (value, row, index) {
                    var res = '-';
                    if(value === '1') {
                        res = "是";
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
                        return "<a target='_blank' href='" + value + "' style='color: #48b0f7;border-bottom: solid 1px #48b0f7'>" + value + "</a>";
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
    $('#couponTable').bootstrapTable('destroy').bootstrapTable(settings);
    //为刷新按钮绑定事件
    var userValue = $("#coupon-form").find("select[name='userValue']").val();
    var lifeCycle = $("#coupon-form").find("select[name='lifeCycle']").val();
    var pathActive = $("#coupon-form").find("select[name='pathActive']").val();
    $.get("/coupon/getCouponList", {groupId: groupId, userValue: userValue, lifeCycle: lifeCycle, pathActive: pathActive}, function (r) {
        $('#couponTable').bootstrapTable('load', r.data);
    });
}

// 给行设置颜色(选中的行给背景颜色)
function rowStyle(row, index) {
    if(row.isSelected === '1') {
        return {
            classes: 'success'
        };
    }
    return {};
}

// 给行设置选中
// function stateFormatter(value, row, index) {
//     if(row.isSelected === '1' || row.isRec === '1') {
//         return {
//             checked : true//设置选中
//         };
//     }
//     return value;
// }

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
            $MB.n_success("设置文案成功！");
        } else {
            $MB.n_danger(r.msg);
        }
        $("#msg_modal").modal('hide');
        initTable();
    });
}

// 批量设置群组文案
function batchUpdateTemplate() {
    var selected = $("#dailyGroupTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要设置文案的用户群组！');
        return;
    }

    var code = "";
    let groupIds = [];
    selected.forEach((v, k) => {
        groupIds.push(v.groupId);
        code += "<tr><td>"+USER_VALUE[v['userValue']]+"</td><td>"+USER_LIFE_CYCLE[v['lifecycle']]+"</td><td>"+PATH_ACTIVE[v['pathActive']]+"</td><td>"+((v['groupInfo']===undefined || v['groupInfo']===null)? '-':+v['groupInfo'])+"</td></tr>";
    });
    $("#selectedGroupInfo1").html('').append(code);
    $("#currentGroupId").val(groupIds.join(","));
    $("#msg_modal").modal('show');
}
/**
 * 根据groupId更新优惠券
 */
function updateCouponId() {
    var data= $('#couponTable').bootstrapTable('getData',true);
    var groupId = $("#currentGroupId").val();
    var selected = $("#couponTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择补贴！');
        return;
    }

    let couponId = [];
    var i = 0;
    var j = 0;
    selected.forEach((v, k) => {
        if(v.checkFlag === '0') {
            i++;
        }else {
            j++;
            couponId.push(v['couponId']);
        }
    });
    var title = '确认设置选中的补贴？';
    if(j === 0) {
        $MB.n_warning("请选择校验通过的补贴！");
        return;
    }
    if(i!==0) {
        title = "当前所选的补贴中存在" + i + "条记录验证不通过，点击'确认'只会设置校验通过的补贴？";
    }

    var count1 = 0;
    var count2 = 0;
    var count3 = 0;
    var count4 = 0;
    var count5 = 0;
    data.forEach((v, k) => {
        if(v['isRec'] === '1') {
            count1++;
        }
        if(v['checkFlag'] === '1' && v['isRec'] === '1') {
            count2++;
        }
        if(v['checkFlag'] === '0' && v['isRec'] === '0' && v['couponSource'] === '0') {
            count5++;
        }
    });
    selected.forEach((v, k) => {
        if(v['checkFlag'] === '1' && v['isRec'] === '1') {
            count3++;
        }
        if(v['isRec'] === '1') {
            count4++;
        }
    });
    var msg = "";
    // 本群组建议优惠券创建但是漏选
    if(count3 < count1 && (count1 === count2)) {
        msg = msg + "本群组建议优惠券创建但是漏选；<br/>";
    }
    // 本群组建议优惠券主观没有创建
    if(count2 < count1) {
        msg = msg + "本群组建议优惠券主观没有创建；<br/>";
    }
    // 本群组建议优惠券配了，但是其他的没有配
    if(count5 > 0 && (count1 === count2)) {
        msg = msg + "本群组建议优惠券配了，但是其他的没有配；<br/>";
    }

    if(msg !== "") {
        $MB.confirm({
            title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
            content: title
        }, function () {
            $MB.confirm({
                title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
                content: msg
            }, function () {
                $.get('/coupon/updateCouponId', {groupId: groupId, couponId: couponId.join(",")}, function (r) {
                    if (r.code === 200) {
                        $MB.n_success("设置补贴成功！");
                    } else {
                        $MB.n_danger("设置补贴失败！发生未知异常！");
                    }
                    $("#coupon_modal").modal('hide');
                    initTable();
                });
            });
        });
    }else {
        $MB.confirm({
            title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
            content: title
        }, function () {
            $.get('/coupon/updateCouponId', {groupId: groupId, couponId: couponId.join(",")}, function (r) {
                if (r.code === 200) {
                    $MB.n_success("设置补贴成功！");
                } else {
                    $MB.n_danger("设置补贴失败！发生未知异常！");
                }
                $("#coupon_modal").modal('hide');
                initTable();
            });
        });
    }
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
        content: '确认重置选中的补贴？'
    }, function () {
        $.getJSON("/coupon/deleteCouponGroup?groupId="+groupIds.join(","),function (resp) {
            if (resp.code === 200){
                lightyear.loading('hide');
                //提示成功
                $MB.n_success('重置补贴成功!');
                //刷新表格
                initTable();
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
            initTable();
            $MB.n_success("群组信息已全部验证完毕！");
        }else {
            $MB.n_warning("未知错误！");
        }
    });
}

// 智能补贴
$("#btn_intel").click(function () {
    $.get("/coupon/getCalculatedCoupon", {}, function (r) {
        if(r.code === 200) {
            $MB.n_success("智能补贴获取成功。");
            couponTable($("#currentGroupId").val());
        }
    });
});

// 配置群组
$("#config_modal").on('shown.bs.modal', function () {
    $.get("/daily/getDefaultGroup", {}, function (r) {
        if(r.code === 200) {
            var data =r.data;
            if(data !== '' && data !== null) {
                data.split(",").forEach((v, k) => {
                    $("#groupConfigForm").find("input[name='pathActive']:checkbox[value='"+v+"']").prop("checked", true);
                });
            }
        }
    });
});

function configGroup() {
    var codeArr = [];
    $("#groupConfigForm").find("input[name='pathActive']:checked").each(function () {
        codeArr.push($(this).val());
    });
    if(codeArr.length === 0) {
        $MB.n_warning("请配置活跃度！");
        return;
    }
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认提交当前选择？'
    }, function () {
        $.get("/daily/setDefaultGroup", {active: codeArr.join(",")}, function (r) {
            if(r.code === 200) {
                $MB.n_success("提交数据成功！");
            }
            $("#config_modal").modal('hide');
            initTable();
        });
    });
}

function userInsight(userValue,pathActive,lifecycle)
{
    //记载数据
    $.get("/daily/usergroupdesc", {userValue: userValue,pathActive:pathActive,lifecycle:lifecycle}, function (r) {
        if(r.code === 200) {
            let data=r.data;
            $("#valueDesc").text(data.valueDesc);
            $("#valuePolicy").text(data.valuePolicy);

            $("#activeDesc").text(data.activeDesc);
            $("#activePolicy").text(data.activePolicy);

            $("#lifecyleDesc").text(data.lifecyleDesc);
            $("#lifecyclePolicy").text(data.lifecyclePolicy);

            let code = "";
            $.each(data.activeResult,function(index,value){
                if(value.flag=='1')
                {
                    code +="<button class='btn btn-round btn-sm btn-info'>"+value.name+"</button>&nbsp;"
                }else
                {
                    code +="<button class='btn btn-round btn-sm btn-secondary'>"+value.name+"</button>&nbsp;"
                }
            });
            $("#activeBtns").html('').append(code);

            code = "";
            $.each(data.userValueResult,function(index,value){
                if(value.flag=='1')
                {
                    code +="<button class='btn btn-round btn-sm btn-warning'>"+value.name+"</button>&nbsp;"
                }else
                {
                    code +="<button class='btn btn-round btn-sm btn-secondary'>"+value.name+"</button>&nbsp;"
                }
            });
            $("#valueBtns").html('').append(code);

            code = "";
            $.each(data.lifecycleResult,function(index,value){
                if(value.flag=='1')
                {
                    code +="<button class='btn btn-round btn-sm btn-primary'>"+value.name+"</button>&nbsp;"
                }else
                {
                    code +="<button class='btn btn-round btn-sm btn-secondary'>"+value.name+"</button>&nbsp;"
                }
            });
            $("#lifecycleBtns").html('').append(code);

            $("#userInsight_modal").modal('show');
        }

    });
}

/**
 * 用户预览
 * @param userValue
 * @param pathActive
 * @param lifecycle
 */
function userInsight(userValue,pathActive,lifecycle)
{
    //记载数据
    $.get("/daily/usergroupdesc", {userValue: userValue,pathActive:pathActive,lifecycle:lifecycle}, function (r) {
        if(r.code === 200) {
            let data=r.data;
            $("#valueDesc").text(data.valueDesc);
            $("#valuePolicy").text(data.valuePolicy);

            $("#activeDesc").text(data.activeDesc);
            $("#activePolicy").text(data.activePolicy);

            $("#lifecyleDesc").text(data.lifecyleDesc);
            $("#lifecyclePolicy").text(data.lifecyclePolicy);

            let code = "";
            $.each(data.activeResult,function(index,value){
                if(value.flag=='1')
                {
                    code +="<button class='btn btn-round btn-sm btn-info m-t-5'>"+value.name+"</button>&nbsp;"
                }else
                {
                    code +="<button class='btn btn-round btn-sm btn-secondary m-t-5'>"+value.name+"</button>&nbsp;"
                }
            });
            $("#activeBtns").html('').append(code);

            code = "";
            $.each(data.userValueResult,function(index,value){
                if(value.flag=='1')
                {
                    code +="<button class='btn btn-round btn-sm btn-warning m-t-5'>"+value.name+"</button>&nbsp;"
                }else
                {
                    code +="<button class='btn btn-round btn-sm btn-secondary m-t-5'>"+value.name+"</button>&nbsp;"
                }
            });
            $("#valueBtns").html('').append(code);

            code = "";
            $.each(data.lifecycleResult,function(index,value){
                if(value.flag=='1')
                {
                    code +="<button class='btn btn-round btn-sm btn-primary m-t-5'>"+value.name+"</button>&nbsp;"
                }else
                {
                    code +="<button class='btn btn-round btn-sm btn-secondary m-t-5'>"+value.name+"</button>&nbsp;"
                }
            });
            $("#lifecycleBtns").html('').append(code);

            $("#userInsight_modal").modal('show');
        }

    });

}