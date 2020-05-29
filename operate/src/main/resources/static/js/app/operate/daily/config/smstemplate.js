var IS_COUPON_NAME_DISABLED;
var IS_COUPON_URL_DISABLED;
var IS_PROD_URL_DISABLED;
$(function () {
    statInputNum();
});

function openSmsTemplateModal(groupId, smsCode, userValue, lifecycle, pathActive) {
    $( "#currentGroupId" ).val(groupId);
    smsTemplateTable(smsCode);
    getUserGroupValue(userValue, lifecycle, pathActive, 'selectedGroupInfo1');
    $( "#smsTemplateModal" ).modal( 'show' );
}

$("#smsTemplateModal").on('hidden.bs.modal', function () {
    $( "#currentGroupId" ).val("");
});

/**
 * 获取短信模板列表
 */
function smsTemplateTable(smsCode) {
    var settings = {
        url: '/smsTemplate/smsTemplateList',
        pagination: true,
        clickToSelect: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            console.log(params)
            return {
                limit: params.limit,
                offset: params.offset
            };
        },
        columns: [
            {
                checkbox: true,
                formatter: function (value, row, index) {
                    if(row['smsCode'] === smsCode) {
                        return {
                            checked : true
                        };
                    }
                }
            },
            {
                title: '消息内容',
                field: 'smsContent'
            },
            {
                title: '创建时间',
                field: 'createDt'
            },{
                title: '使用天数',
                field: 'usedDays'
            }
        ]
    };
    $( "#smsTemplateTable" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

// 新增文案
function add() {
    $("#smsTemplateModal").modal('hide');
    $('#smsCode').val("");
    $('#smsContent').val("");
    $('#smsName').val("");
    $('#remark').val("");
    $("#word").text("0:编写内容字符数 / 0:填充变量最大字符数 / "+smsLengthLimit+":文案总字符数");
    $("#fontNum").val('');
    $("#myLargeModalLabel3").text("新增文案");
    $("#btn_save_sms").attr("name", "save");
    $('#add_modal').modal('show');
}

function setSmsCode() {
    var selected = $( "#smsTemplateTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择文案！' );
        return;
    }
    var smsCode = selected[0].smsCode;
    var groupId = $( "#currentGroupId" ).val();
    if (!(groupId !== undefined && groupId !== '')) {
        $MB.n_warning( "请选择一条群组信息。" );
        return;
    }
    $.get( '/daily/setSmsCode', {groupId: groupId, smsCode: smsCode}, function (r) {
        if (r.code === 200) {
            $MB.n_success( "设置文案成功！" );
        } else {
            $MB.n_danger( r.msg );
        }
        $("#smsTemplateModal").modal('hide');
        getTableData();
    } );
}

// 新增文案的时候，给群组信息设置默认选中和只读
function setUserGroupChecked() {
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
            $("#smsTemplateModal").modal('hide');
            //更新测试面板
            $("#smsName1").val(resp.data.smsName);
            $("#smsContent1").val(resp.data.smsContent);

            var _value = $("#smsContent1").val().replace(/\n/gi,"");
            $("#word1").text(_value.length);
            $('#send_modal').modal('show');
        }
    })
}

$("#send_modal").on('hidden.bs.modal', function () {
    $('#smsTemplateModal').modal('show');
});


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

    let param = {};
    param.phoneNum=phoneNum;
    param.smsContent=smsContent;

    $.ajax({
        url: "/smsTemplate/testSend",
        data: param,
        type: 'POST',
        success: function (r) {
            lightyear.loading('hide');
            if(r.code===200)
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
            if(smsContent.indexOf("${COUPON_NAME}") > -1) {
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
            setUserGroupChecked();
            $.post(url, data, function (r) {
                if(r.code === 200) {
                    $MB.n_success(success_msg);
                }else {
                    $MB.n_danger(r.msg);
                }
                $('#add_modal').modal('hide');
                $('#smsTemplateTable').bootstrapTable('refresh');
                $("#smsTemplateModal").modal('show');
            });
        });
    }
});
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

function smsContentValid() {
    $('#smsContentInput').val($('#smsContent').val());
    if($('#smsContentInput').val() !== '') {
        $('#smsContentInput').removeClass('error');
        $("#smsContentInput-error").remove();
    }
    var content = $('#smsContent').val() === "" ? "请输入短信内容": $('#smsContent').val();
    $("#article").html('').append(content);
}
$("#add_modal").on('hidden.bs.modal', function () {
    $('#smsCode').val("");
    $('#smsName').val("");
    $('#smsContent').val("");
    $('#smsContentInput').val("");
    $("input[name='userValue']:checked").removeAttr("checked");
    $("input[name='lifeCycle']:checked").removeAttr("checked");
    $("input[name='pathActive']:checked").removeAttr("checked");
    $("#word").text("0:编写内容字符数 / 0:填充变量最大字符数 / "+smsLengthLimit+":文案总字符数");
    $("#fontNum").val('');
    $("#remark").val('');
    $("input[name='userValue']:checked").removeAttr("checked");
    $("input[name='lifeCycle']:checked").removeAttr("checked");
    $("input[name='pathActive']:checked").removeAttr("checked");
    $("input[name='isCouponUrl']").removeAttr("disabled");
    $("input[name='isCouponUrl']:checked").removeAttr("checked");
    $("input[name='isCouponName']").removeAttr("disabled");
    $("input[name='isCouponName']:checked").removeAttr("checked");
    $("input[name='isProductName']").removeAttr("disabled");
    $("input[name='isProductName']:checked").removeAttr("checked");
    $("input[name='isProductUrl']").removeAttr("disabled");
    $("input[name='isProductUrl']:checked").removeAttr("checked");

    $('#isCouponUrl-error').hide();
    $('#isCouponName-error').hide();
    $('#isProductUrl-error').hide();
    $('#isProductName-error').hide();
    $("#article").html('请输入短信内容');
    $("#smsTemplateModal").modal('show');
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
        $MB.n_warning('请选择需要编辑的文案！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];
    $.get("/smsTemplate/getSmsUsedGroupInfo", {smsCode:smsCode}, function (r) {
        var data = r.data;
        var len = data.length;
        var code = "";
        data.forEach((v, k)=>{
            code += "<br/>" + v + "；";
        });
        if(len === 0) {
            $.getJSON("/smsTemplate/getSmsTemplate?smsCode="+smsCode,function (resp) {
                if (resp.code === 200){
                    $("#smsTemplateModal").modal('hide');
                    var data = resp.data;
                    $("#smsCode").val(data.smsCode);
                    $("#smsName").val(data.smsName);
                    $("#smsContent").val(data.smsContent);
                    $("#article").html(data.smsContent);
                    $("#smsContentInput").val(data.smsContent);
                    $("input[name='isCouponUrl']:radio[value='" + data.isCouponUrl + "']").prop("checked", true);
                    $("input[name='isCouponName']:radio[value='" + data.isCouponName + "']").prop("checked", true);
                    $("input[name='isProductName']:radio[value='" + data.isProductName + "']").prop("checked", true);
                    $("input[name='isProductUrl']:radio[value='" + data.isProductUrl + "']").prop("checked", true);
                    $("#remark").val(data.remark);
                    $("#myLargeModalLabel3").text("修改文案");
                    $("#btn_save_sms").attr("name", "update");
                    $("#add_modal").modal('show');

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
                    initGetInputNum();
                }else {
                    $MB.n_danger(resp.msg);
                }
            });
        }else {
            $.confirm({
                title: "提示：",
                content: "您选择的文案正在被以下" + len + "个群组引用，不可变更文案内容；可先解除文案与群组的引用关系；" +
                    "<br/>------------------------------------------------------" + code,
                type: 'red',
                typeAnimated: true,
                buttons: {
                    close: {
                        text: '取消',
                        action: function () {
                            $MB.loading('hide');
                        }
                    }
                }
            });
        }
    });
});

// 补贴链接选是，补贴名称自动选是、商品链接自动选否；
function isCouponUrlTrueClick() {
    $( "#smsTemplateAddForm" ).find( 'input[name="isCouponName"]:radio[value="1"]' ).prop( "checked", true );
    $( "#smsTemplateAddForm" ).find( 'input[name="isCouponName"]' ).attr( "disabled", "disabled" );
    IS_COUPON_NAME_DISABLED = true;

    $( "#smsTemplateAddForm" ).find( 'input[name="isProductUrl"]:radio[value="0"]' ).prop( "checked", true );
    $( "#smsTemplateAddForm" ).find( 'input[name="isProductUrl"]' ).attr( "disabled", "disabled" );
    IS_PROD_URL_DISABLED = true;
}

// 补贴链接选否，补贴名称自动选否、商品链接可选；
function isCouponUrlFalseClick() {
    $( "#smsTemplateAddForm" ).find( 'input[name="isCouponName"]:radio[value="0"]' ).prop( "checked", true );
    $( "#smsTemplateAddForm" ).find( 'input[name="isProductUrl"]' ).removeAttr( "disabled" );
    IS_PROD_URL_DISABLED = false;
}

// 补贴名称选是，补贴链接自动选是；
function isCouponNameTrueClick() {
    $( "#smsTemplateAddForm" ).find( 'input[name="isCouponUrl"]:radio[value="1"]' ).prop( "checked", true );
    $( "#smsTemplateAddForm" ).find( 'input[name="isCouponUrl"]' ).attr( "disabled", "disabled" );
    IS_COUPON_URL_DISABLED = true;
}

// 补贴名称选否，补贴链接自动选否；
function isCouponNameFalseClick() {
    $( "#smsTemplateAddForm" ).find( 'input[name="isCouponUrl"]:radio[value="0"]' ).prop( "checked", true );
    $( "#smsTemplateAddForm" ).find( 'input[name="isCouponUrl"]' ).attr( "disabled", "disabled" );
    IS_COUPON_URL_DISABLED = true;
}

// 商品链接选是，补贴链接自动选否；
function isProdUrlTrueClick() {
    $( "#smsTemplateAddForm" ).find( 'input[name="isCouponUrl"]:radio[value="0"]' ).prop( "checked", true );
    $( "#smsTemplateAddForm" ).find( 'input[name="isCouponUrl"]' ).attr( "disabled", "disabled" );
    IS_COUPON_URL_DISABLED = true;
}

// 商品链接选否，补贴链接可选；
function isProdUrlFalseClick() {
    $( "#smsTemplateAddForm" ).find( 'input[name="isCouponUrl"]' ).removeAttr( "disabled" );
    IS_COUPON_URL_DISABLED = false;
}

$("#btn_refresh").click(function () {
    var selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择需要解除引用的文案！');
        return;
    }
    var smsCode =selectRows[0]["smsCode"];
    $.get("/smsTemplate/getSmsUsedGroupInfo", {smsCode:smsCode}, function (res) {
        var data = res.data;
        var len = data.length;
        var code = "";
        data.forEach((v, k)=>{
            code += "<br/>" + v + "；";
        });
        if(len === 0) {
            $MB.n_warning("当前文案未被引用，无需解除引用。");
        }else {
            $MB.confirm({
                content: "您选择的文案正在被以下" + len + "个群组引用，解除文案会影响到其他群组的文案引用关系，确定要解除吗？" +
                    "<br/>------------------------------------------------------" + code,
                title: "提示："
            }, function () {
                $.post("/smsTemplate/updateSmsCodeNull", {smsCode:smsCode}, function (r2) {
                    if(r2.code === 200) {
                        $MB.n_success("当前文案已解除引用。");
                        SMS_CODE = null;
                        smsTemplateTable();
                        initTable();
                    }
                });
            });
        }
    });
});

function copyString(id) {
    $('#' + id).select();
    document.execCommand("copy");
    $MB.n_success("成功复制到粘贴板!");
}