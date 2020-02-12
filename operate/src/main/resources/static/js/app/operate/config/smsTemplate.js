var validator;
$(function () {
    initTable();
    statInputNum();
});

//为刷新按钮绑定事件
$("#btn_refresh").on("click",function () {
    $('#smsTemplateTable').bootstrapTable('refresh');
});

function initTable() {
    var settings = {
        url: "/smsTemplate/list",
        method: 'post',
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 25,            //每页的记录行数（*）
        pageList: [25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit )+ 1,  //页码
                param: {
                    userValue: $('#userValue').val(),
                    lifeCycle: $('#userLifeCycle').val(),
                    pathActive: $('#activePath').val()
                }
            };
        },
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
                                }
                            });
                        }
                        return res.length === 0 ? '-' : res.join(",");
                    }
                }
            ]
        ]
    };
    $MB.initTable('smsTemplateTable', settings);
}

/**
 * 字数统计
 * @param textArea
 * @param numItem
 */
function statInputNum(textArea,numItem) {
    curLength = textArea.val().length;
    numItem.text(curLength);
    textArea.on('input propertychange', function () {
        var _value = $(this).val().replace(/\n/gi,"");
        numItem.text(_value.length);
    });
}


/**
 * 测试推送
 * @param smsCode
 */
function testSend()
{
    let selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择需要测试的文案！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];

    //根据获取到的数据查询
    $.getJSON("/smsTemplate/getSmsTemplateNotValid?smsCode="+smsCode,function (resp) {
        if (resp.code === 200){
            //更新测试面板
            $("#smsName1").val(resp.data.smsName);
            $("#smsContent1").val(resp.data.smsContent);

            var _value = $("#smsContent1").val().replace(/\n/gi,"");
            $("#word1").text(_value.length);

            $('#send_modal').modal('show');
        }
    })
}

function sendMessage()
{
    //验证
    var smsContent= $('#smsContent1').val();

    if($('input[name="phoneNum"]').eq(0).val() === '' && $('input[name="phoneNum"]').eq(1).val() === ''&& $('input[name="phoneNum"]').eq(2).val()=== '')
    {
        $MB.n_warning("手机号不能为空！");
        return;
    }
    var phoneNum=[];
    if($('input[name="phoneNum"]').eq(0).val() !== '') {
        phoneNum.push($('input[name="phoneNum"]').eq(0).val());
    }
    if($('input[name="phoneNum"]').eq(1).val() !== '') {
        phoneNum.push($('input[name="phoneNum"]').eq(1).val());
    }
    if($('input[name="phoneNum"]').eq(2).val() !== '') {
        phoneNum.push($('input[name="phoneNum"]').eq(2).val());
    }
    phoneNum = phoneNum.join(',');

    if(null==smsContent||smsContent=='')
    {
        $MB.n_warning("模板内容不能为空！");
        return;
    }

    //判断是否含有变量
    if(smsContent.indexOf("$") >= 0 ) {
        $MB.n_warning("模板内容的变量请替换为实际值！");
        return;
    }

    //提交后端进行发送
    lightyear.loading('show');

    let param = new Object();
    param.phoneNum=phoneNum;
    param.smsContent=smsContent;

    $.ajax({
        url: "/smsTemplate/testSend",
        data: param,
        type: 'POST',
        success: function (r) {
            lightyear.loading('hide');
            if(r.code==200)
            {
                $MB.n_success(r.msg);
            }else
            {
                $MB.n_danger(r.msg);
            }
        }
    });


}

function del() {
    var selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择要删除的文案！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];

    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除当前文案？'
    }, function () {
        $.get("/smsTemplate/smsIsUsed", {smsCode:smsCode}, function (resp) {
            if(resp) {
                $MB.confirm({
                    title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
                    content: '当前文案已被成长组引用，确认要删除？'
                },function () {
                    deleteSmsTemplate(smsCode);
                });
            }else {
                deleteSmsTemplate(smsCode);
            }
        });
    });
}
// 删除文案
function deleteSmsTemplate(smsCode) {
    $.getJSON("/smsTemplate/deleteSmsTemplate",{smsCode:smsCode}, function (resp) {
        if (resp.code === 200){
            //提示成功
            $MB.n_success('删除成功！');
            //刷新表格
            var groupId = $("#currentGroupId").val();
            smsTemplateTable(groupId);
        }else {
            $MB.n_danger(resp.msg);
        }
    })
}
function add() {
    $('#smsCode').val("");
    $('#smsContent').val("");
    $('#smsName').val("");
    $('#remark').val("");
    $("input[name='isCoupon']:radio").removeAttr("checked").removeAttr("disabled");
    $("input[name='isProductName']:radio").removeAttr("checked").removeAttr("disabled");
    $("input[name='isProductUrl']:radio").removeAttr("checked").removeAttr("disabled");
    $("#word").text("0:编写内容字符数 / 0:填充变量最大字符数 / "+smsLengthLimit+":文案总字符数");
    $("#fontNum").val('');
    $("#myLargeModalLabel3").text("新增文案");
    $("#btn_save_sms").attr("name", "save");
    $('#add_modal').modal('show');
    $("#smsTemplateAddForm").validate().resetForm();
}

// 字数统计
$("#btn_cal").click(function () {
    var data = getSmsContentFontCount();
    $("#fontNum").val(data.count + "个字");
});

function validCouponSendType(isCoupon) {
    let couponSendType = getCouponSendType();
    let smsContent = $('#smsContent').val();
    let isProductUrl = $("input[name='isProductUrl']:checked").val();
    let isProductName = $("input[name='isProductName']:checked").val();
    if(smsContent === '') {
        $MB.n_warning("文案内容不能为空！");
        return false;
    }
    if(couponSendType === 'A') { // 包含${COUPON_URL}
        // 不能包含url
        if(isCoupon === '0') {
            if(smsContent.indexOf("${COUPON_URL}") > -1) {
                $MB.n_warning("选择'不体现补贴链接与商品'，文案内容不能出现${COUPON_URL}");
                return false;
            }
            if(smsContent.indexOf("${COUPON_NAME}") > -1) {
                $MB.n_warning("选择'不体现补贴链接与商品'，文案内容不能出现${COUPON_NAME}");
                return false;
            }
        }

        // 不能包含url
        if(isCoupon === '1') {
            if(smsContent.indexOf("${COUPON_URL}") === -1) {
                $MB.n_warning("补贴发放方式为自行领取，模板内容未发现${COUPON_URL}");
                return false;
            }
            if(smsContent.indexOf("${COUPON_NAME}") === -1) {
                $MB.n_warning("补贴发放方式为自行领取，模板内容未发现${COUPON_NAME}");
                return false;
            }
        }
    }
    if(couponSendType === 'B') { // 不包含${COUPON_URL}
        if(smsContent.indexOf("${COUPON_URL}") > -1) {
            $MB.n_warning("补贴发放方式为系统发送，模板内容不能出现${COUPON_URL}");
            return false;
        }
        if(smsContent.indexOf("${COUPON_NAME}") > -1) {
            $MB.n_warning("补贴发放方式为系统发送，模板内容不能出现${COUPON_NAME}");
            return false;
        }
        if(isCoupon === '1') {
            $MB.n_warning("补贴发放方式为系统发送，'体现补贴链接与名称'不能为'是'");
            return;
        }
    }

    if(isProductUrl === '1') {
        if(smsContent.indexOf("${PROD_URL}") === -1) {
            $MB.n_warning("选择'体现推荐商品的详情页'，文案内容未发现${PROD_URL}");
            return false;
        }
    }
    if(isProductUrl === '0') {
        if(smsContent.indexOf("${PROD_URL}") > -1) {
            $MB.n_warning("选择'不体现推荐商品的详情页'，文案内容不能出现${PROD_URL}");
            return false;
        }
    }
    if(isProductName === '1') {
        if(smsContent.indexOf("${PROD_NAME}") === -1) {
            $MB.n_warning("选择'体现推荐商品名称'，文案内容未发现${PROD_NAME}");
            return false;
        }
    }
    if(isProductName === '0') {
        if(smsContent.indexOf("${PROD_NAME}") > -1) {
            $MB.n_warning("选择'不体现推荐商品名称'，文案内容不能出现${PROD_NAME}");
            return false;
        }
    }
    return true;
}

// 获取优惠券发送方式
function getCouponSendType() {
    var res;
    $.ajax({
        url: "/smsTemplate/getCouponSendType",
        method: "get",
        async: false,
        success: function (r) {
            res = r.data;
        }
    });
    return res;
}

function getSmsContentFontCount() {
    let smsContent = $('#smsContent').val();
    var res;
    $.ajax({
        url: "/smsTemplate/calFontNum",
        data: {smsContent: smsContent},
        method: "get",
        async: false,
        success: function (r) {
            res = r.data;
        }
    });
    return res;
}

// 新增&修改
$("#btn_save_sms").click(function () {
    var url = "";
    var success_msg = "";
    var sure_msg = "";
    if($(this).attr("name") == "save") {
        url = "/smsTemplate/addSmsTemplate";
        success_msg = "新增成功！";
        sure_msg = "确定要保存数据？";
    }else {
        url = "/smsTemplate/updateSmsTemplate";
        success_msg = "修改成功！";
        sure_msg = "确定要修改数据？";
    }
    if(validate()) {
        //提示是否要保存
        $MB.confirm({
            title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
            content: sure_msg
        }, function () {
            $("input[name='userValue']").removeAttr("disabled");
            $("input[name='lifeCycle']").removeAttr("disabled");
            $("input[name='pathActive']").removeAttr("disabled");
            $("#smsCode").removeAttr("disabled");
            $("#smsName").removeAttr("disabled");
            $("input[name='isCouponUrl']").removeAttr("disabled");
            $("input[name='isCouponName']").removeAttr("disabled");
            $("input[name='isProductName']").removeAttr("disabled");
            $("input[name='isProductUrl']").removeAttr("disabled");
            var data = $("#smsTemplateAddForm").serialize();
            setDisabled();
            $.post(url, data, function (r) {
                if(r.code === 200) {
                    $MB.n_success(success_msg);
                }else {
                    $MB.n_danger(r.msg);
                }
                $('#add_modal').modal('hide');
                $('#smsTemplateTable').bootstrapTable('refresh');
            });
        });
    }
});
function setDisabled() {
    $("input[name='userValue']").attr("disabled", "disabled");
    $("input[name='lifeCycle']").attr("disabled", "disabled");
    $("input[name='pathActive']").attr("disabled", "disabled");
    $("#smsCode").attr("disabled", "disabled");
    $("#smsName").attr("disabled", "disabled");
    $("input[name='isCouponUrl']").attr("disabled", "disabled");
    $("input[name='isCouponName']").attr("disabled", "disabled");
    $("input[name='isProductName']").attr("disabled", "disabled");
    $("input[name='isProductUrl']").attr("disabled", "disabled");
}

function clearDisabled() {
    $("input[name='userValue']").removeAttr("disabled");
    $("input[name='lifeCycle']").removeAttr("disabled");
    $("input[name='pathActive']").removeAttr("disabled");
    $("#smsCode").removeAttr("disabled");
    $("#smsName").removeAttr("disabled");
    $("input[name='isCouponUrl']").removeAttr("disabled");
    $("input[name='isCouponName']").removeAttr("disabled");
    $("input[name='isProductName']").removeAttr("disabled");
    $("input[name='isProductUrl']").removeAttr("disabled");
}
// 表单验证
function validate() {
    if(!validCouponSendType()) {
        return false;
    }
    if(total_num > smsLengthLimit) {
        $MB.n_warning('文案字数超出最大限制！');
        return false;
    }
    return true;
}

// 验证补贴
function validCouponSendType() {
    let couponSendType = getCouponSendType();
    let smsName = $('#smsName').val();
    let smsContent = $('#smsContent').val();
    let isCouponUrl = $("input[name='isCouponUrl']:checked").val();
    let isCouponName = $("input[name='isCouponName']:checked").val();
    let isProductUrl = $("input[name='isProductUrl']:checked").val();
    let isProductName = $("input[name='isProductName']:checked").val();

    if(isCouponUrl === undefined) {
        $MB.n_warning("请选择补贴短链接！");
        return false;
    }
    if(isCouponName === undefined) {
        $MB.n_warning("请选择补贴名称！");
        return false;
    }
    if(isProductUrl === undefined) {
        $MB.n_warning("请选择推荐商品名称！");
        return false;
    }
    if(isProductName === undefined) {
        $MB.n_warning("请选择推荐商品短链接！");
        return false;
    }
    if(smsName === '') {
        $MB.n_warning("文案名称不能为空！");
        return false;
    }
    if(smsContent === '') {
        $MB.n_warning("文案内容不能为空！");
        return false;
    }
    if(couponSendType === 'A') { // 包含${COUPON_URL}
        // 不能包含url
        if(isCouponUrl === '0') {
            if(smsContent.indexOf("${COUPON_URL}") > -1) {
                $MB.n_warning("选择'补贴链接:否'，文案内容不能出现${COUPON_URL}");
                return false;
            }
        }
        if(isCouponName === '0') {
            if(smsContent.indexOf("${COUPON_URL}") > -1) {
                $MB.n_warning("选择'补贴名称:否'，文案内容不能出现${COUPON_NAME}");
                return false;
            }
        }

        // 不能包含url
        if(isCouponUrl === '1') {
            if(smsContent.indexOf("${COUPON_URL}") === -1) {
                $MB.n_warning("补贴发放方式为自行领取且您选择了'补贴链接：是'，模板内容未发现${COUPON_URL}");
                return false;
            }
        }
        if(isCouponName === '1') {
            if(smsContent.indexOf("${COUPON_NAME}") === -1) {
                $MB.n_warning("补贴发放方式为自行领取且您选择了'补贴名称：是'，模板内容未发现${COUPON_NAME}");
                return false;
            }
        }

        if(isCouponName === '1' && isCouponUrl === '0') {
            $MB.n_warning("补贴发放方式为自行领取且您选择了'补贴名称：是'，'补贴链接'不能选否！");
            return false;
        }
    }
    if(couponSendType === 'B') { // 不包含${COUPON_URL}
        if(smsContent.indexOf("${COUPON_URL}") > -1) {
            $MB.n_warning("补贴发放方式为系统发送，文案内容不能出现${COUPON_URL}！");
            return false;
        }
        if(isCouponUrl === '1') {
            $MB.n_warning("补贴发放方式为系统发送，补贴链接不能选是！");
            return false;
        }

        if(isCouponName === '1') {
            if(smsContent.indexOf("${COUPON_NAME}") === -1) {
                $MB.n_warning("补贴发放方式为系统发送且选择了'补贴名称：是'，文案内容没有发现${COUPON_NAME}！");
                return false;
            }
        }else {
            if(smsContent.indexOf("${COUPON_NAME}") > -1) {
                $MB.n_warning("'补贴名称：否'，文案内容不能出现${COUPON_NAME}！");
                return false;
            }
        }
    }

    if(isProductUrl === '1') {
        if(smsContent.indexOf("${PROD_URL}") === -1) {
            $MB.n_warning("选择'推荐商品链接：是'，文案内容未发现${PROD_URL}");
            return false;
        }
    }
    if(isProductUrl === '0') {
        if(smsContent.indexOf("${PROD_URL}") > -1) {
            $MB.n_warning("选择'推荐商品链接：否'，文案内容不能出现${PROD_URL}");
            return false;
        }
    }
    if(isProductName === '1') {
        if(smsContent.indexOf("${PROD_NAME}") === -1) {
            $MB.n_warning("选择'推荐商品名称：是'，文案内容未发现${PROD_NAME}");
            return false;
        }
    }
    if(isProductName === '0') {
        if(smsContent.indexOf("${PROD_NAME}") > -1) {
            $MB.n_warning("选择'推荐商品名称：否'，文案内容不能出现${PROD_NAME}");
            return false;
        }
    }
    return true;
}

//
function smsContentValid() {
    $('#smsContentInput').val($('#smsContent').val());
    if($('#smsContentInput').val() !== '') {
        $('#smsContentInput').removeClass('error');
        $("#smsContentInput-error").remove();
    }
}

$("#add_modal").on('hidden.bs.modal', function () {
    $('#smsCode').val("");
    $('#smsContent').val("");
    $('#smsContentInput').val("");
    $("input[name='userValue']").removeAttr("disabled");
    $("input[name='userValue']:checked").removeAttr("checked");
    $("input[name='lifeCycle']").removeAttr("disabled");
    $("input[name='lifeCycle']:checked").removeAttr("checked");
    $("input[name='pathActive']").removeAttr("disabled");
    $("input[name='pathActive']:checked").removeAttr("checked");
    $("#word").text("0:编写内容字符数 / 0:填充变量最大字符数 / "+smsLengthLimit+":文案总字符数");
    $("#fontNum").val('');
    $("#remark").val('');
    $("#smsTemplateAddForm").validate().resetForm();
});

// 文案检索
function searchSmsTemplate() {
    $MB.refreshTable('smsTemplateTable');
}

function resetSmsTemplate() {
    $("input[name='smsCode']").val("");
    $MB.refreshTable('smsTemplateTable')
}

$("#btn_edit").click(function () {
    var selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择要修改的文案！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];
    $.getJSON("/smsTemplate/getSmsTemplateNotValid?smsCode="+smsCode,function (resp) {
        if (resp.code === 200){
            var data = resp.data;
            $("#smsCode").val(data.smsCode).attr('disabled', 'disabled');
            $("#smsName").val(data.smsName).attr('disabled', 'disabled');
            $("#smsContent").val(data.smsContent);
            $("#smsContentInput").val(data.smsContent);
            statInputNum($("#smsContent"),$("#word"));
            $("input[name='isCouponUrl']:radio[value='" + data.isCouponUrl + "']").prop("checked", true).attr('disabled', 'disabled');
            $("input[name='isCouponName']:radio[value='" + data.isCouponName + "']").prop("checked", true).attr('disabled', 'disabled');
            $("input[name='isProductName']:radio[value='" + data.isProductName + "']").prop("checked", true).attr('disabled', 'disabled');
            $("input[name='isProductUrl']:radio[value='" + data.isProductUrl + "']").prop("checked", true).attr('disabled', 'disabled');
            $("#remark").val(data.remark);
            if(data.userValue !== null) {
                data.userValue.split(',').forEach((v,k)=>{
                    $("input[name='userValue']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }
            if(data.lifeCycle !== null) {
                data.lifeCycle.split(',').forEach((v,k)=>{
                    $("input[name='lifeCycle']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }
            if(data.pathActive !== null) {
                data.pathActive.split(',').forEach((v,k)=>{
                    $("input[name='pathActive']:checkbox[value='" + v + "']").prop("checked", true);
                });
            }
            $("input[name='userValue']").attr('disabled', 'disabled');
            $("input[name='lifeCycle']").attr('disabled', 'disabled');
            $("input[name='pathActive']").attr('disabled', 'disabled');
            $("#myLargeModalLabel3").text("修改文案");
            $("#btn_save_sms").attr("name", "update");
            $("#add_modal").modal('show');
            initGetInputNum();
        }else {
            $MB.n_danger(resp.msg);
        }
    });
});

/**
 * 字数统计
 * @param textArea
 * @param numItem
 */
let total_num;
function statInputNum() {
    $("#smsContent").on('input propertychange', function () {
        let smsContent = $('#smsContent').val();
        let y = smsContent.length;
        let m = smsContent.length;
        let n = smsContent.length;
        if(smsContent.indexOf('${COUPON_URL}') > -1) {
            y = y - '${COUPON_URL}'.length + shortUrlLen;
            m = m - '${COUPON_URL}'.length;
        }
        if(smsContent.indexOf('${COUPON_NAME}') > -1) {
            y = y - '${COUPON_NAME}'.length + couponNameLen;
            m = m - '${COUPON_NAME}'.length;
        }
        if(smsContent.indexOf('${PROD_NAME}') > -1) {
            y = y - '${PROD_NAME}'.length + prodNameLen;
            m = m - '${PROD_NAME}'.length;
        }
        if(smsContent.indexOf('${PROD_URL}') > -1) {
            y = y - '${PROD_URL}'.length + shortUrlLen;
            m = m - '${PROD_URL}'.length;
        }
        total_num = y;
        var code = "";
        code += m + ":编写内容字符数 / " + y + ":填充变量最大字符数 / " + smsLengthLimit + ":文案总字符数";
        $("#word").text(code);
    });
}

$("#add_modal").on('shown.bs.modal', function () {
    var userBox = document.getElementById('user-box');
    $("#config-box").attr("style", "border: solid 1px #ebebeb;padding: 8px; float: left;width:" + userBox.clientWidth + "px;");
});

var IS_COUPON_NAME_DISABLED;
var IS_COUPON_URL_DISABLED;
var IS_PROD_URL_DISABLED;
// 补贴链接选是，补贴名称自动选是、商品链接自动选否；
function isCouponUrlTrueClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponName"]:radio[value="1"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isCouponName"]').attr("disabled", "disabled");
    IS_COUPON_NAME_DISABLED = true;

    $("#smsTemplateAddForm").find('input[name="isProductUrl"]:radio[value="0"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isProductUrl"]').attr("disabled", "disabled");
    IS_PROD_URL_DISABLED = true;

    $('#isCouponUrl-error').show();
    $('#isCouponName-error').show();
    $('#isProductUrl-error').hide();
}

// 补贴链接选否，补贴名称自动选否、商品链接可选；
function isCouponUrlFalseClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponName"]:radio[value="0"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isProductUrl"]').removeAttr("disabled");
    IS_PROD_URL_DISABLED = false;
    $('#isCouponUrl-error').hide();
    $('#isCouponName-error').hide();
}

// 补贴名称选是，补贴链接自动选是；
function isCouponNameTrueClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]:radio[value="1"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]').attr("disabled", "disabled");
    IS_COUPON_URL_DISABLED = true;
    $('#isCouponName-error').show();
    $('#isCouponUrl-error').show();
}
// 补贴名称选否，补贴链接自动选否；
function isCouponNameFalseClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]:radio[value="0"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]').attr("disabled", "disabled");
    IS_COUPON_URL_DISABLED = true;
    $('#isCouponName-error').hide();
    $('#isCouponUrl-error').hide();
}
// 商品链接选是，补贴链接自动选否；
function isProdUrlTrueClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]:radio[value="0"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]').attr("disabled", "disabled");
    IS_COUPON_URL_DISABLED = true;
    $('#isProductUrl-error').show();
    $('#isCouponUrl-error').hide();
}
// 商品链接选否，补贴链接可选；
function isProdUrlFalseClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]').removeAttr("disabled");
    IS_COUPON_URL_DISABLED = false;
    $('#isProductUrl-error').hide();
}

function initGetInputNum() {
    let smsContent = $('#smsContent').val();
    let y = smsContent.length;
    let m = smsContent.length;
    let n = smsContent.length;
    if(smsContent.indexOf('${COUPON_URL}') > -1) {
        y = y - '${COUPON_URL}'.length + shortUrlLen;
        m = m - '${COUPON_URL}'.length;
    }
    if(smsContent.indexOf('${COUPON_NAME}') > -1) {
        y = y - '${COUPON_NAME}'.length + couponNameLen;
        m = m - '${COUPON_NAME}'.length;
    }
    if(smsContent.indexOf('${PROD_NAME}') > -1) {
        y = y - '${PROD_NAME}'.length + prodNameLen;
        m = m - '${PROD_NAME}'.length;
    }
    if(smsContent.indexOf('${PROD_URL}') > -1) {
        y = y - '${PROD_URL}'.length + shortUrlLen;
        m = m - '${PROD_URL}'.length;
    }
    total_num = y;
    var code = "";
    code += m + ":编写内容字符数 / " + y + ":填充变量最大字符数 / " + smsLengthLimit + ":文案总字符数";
    $("#word").text(code);
}