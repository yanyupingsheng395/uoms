let validator;
$(function () {
    initTable();
    statInputNum();
    initCommentsDisplay();
});

/**
 * 根据控制变量设置注释及变量是否可用
 */
function initCommentsDisplay() {
   if('Y'!==prodUrlEnabled)
   {
       $("#produrlComments").addClass('hidden');
       $("#produrlDiv").hide();
   }
   if('B'===couponSendType)
   {
       $("#couponurlComments").addClass('hidden');
       $("#couponurlDiv").hide();
   }
}

function initTable() {
    let settings = {
        url: '/smsTemplate/smsTemplateList',
        pagination: true,
        clickToSelect: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset
            };
        },
        columns: [
            {
                checkbox: true
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
                field: 'usedDays',
                align: 'center'
            },{
                title: '被引用组数',
                field: 'refCnt',
                align: 'center'
            }
        ]
    };
    $MB.initTable('smsTemplateTable', settings);
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
    let smsCode =selectRows[0]["smsCode"];
    //根据获取到的数据查询
    $.getJSON("/smsTemplate/getSmsTemplateContent?smsCode="+smsCode,function (resp) {
        if (resp.code === 200){
            //更新测试面板
            $("#smsContent1").val(resp.data);
            let _value = $("#smsContent1").val().replace(/\n/gi,"");
            $("#word1").text(_value.length);
            $('#send_modal').modal('show');
        }
    })
}

function sendMessage()
{
    let phoneNums=[];
    $("input[name='phoneNum']").each(function(){
        let temp=$(this).val();
        if(temp!=='')
        {
            phoneNums.push(temp);
        }
    })
    if(phoneNums.length==0)
    {
        $MB.n_warning("至少输入一个手机号！");
        return;
    }
    let phoneNum = phoneNums.join(',');
    let testSmsCode=$("#testSmsCode").val();

    //提交后端进行发送
    lightyear.loading('show');
    let param = new Object();
    param.phoneNum=phoneNum;
    param.smsCode=testSmsCode;
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

/**
 * 删除文案
 */
function del() {
    let selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
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
            $MB.n_success('删除成功！');
            $MB.refreshTable('smsTemplateTable');
        }else {
            $MB.n_danger(resp.msg);
        }
    })
}
function add() {
    $('#smsCode').val("");
    $('#smsContent').val("");
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

// 新增&修改
$("#btn_save_sms").click(function () {
    var url = "";
    var success_msg = "";
    var sure_msg = "";
    if($(this).attr("name") == "save") {
        url = "/smsTemplate/addSmsTemplate";
        success_msg = "新增成功！";
        sure_msg = "确定要新增文案？";
    }else {
        url = "/smsTemplate/updateSmsTemplate";
        success_msg = "修改成功！";
        sure_msg = "确定要修改文案？";
    }
    if(validate()) {
        //提示是否要保存
        $MB.confirm({
            title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
            content: sure_msg
        }, function () {
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

/**
 * 禁用个性化的几个属性
 */
function setDisabled() {
    $("input[name='isCouponUrl']").attr("disabled", "disabled");
    $("input[name='isCouponName']").attr("disabled", "disabled");
    $("input[name='isProductName']").attr("disabled", "disabled");
    $("input[name='isProductUrl']").attr("disabled", "disabled");
}

// 表单验证
function validate() {
    if(!validateTemplate()) {
        return false;
    }
    if(total_num > smsLengthLimit) {
        $MB.n_warning('文案字数超出最大限制！');
        return false;
    }
    return true;
}

// 验证文案
function validateTemplate() {
    let smsContent = $('#smsContent').val();
    let isCouponUrl = $("input[name='isCouponUrl']:checked").val();
    let isCouponName = $("input[name='isCouponName']:checked").val();
    let isProductUrl = $("input[name='isProductUrl']:checked").val();
    let isProductName = $("input[name='isProductName']:checked").val();

    if(isProductName === undefined) {
        $MB.n_warning("请选择个性化要素:商品名称！");
        return false;
    }

    if(isCouponName === undefined) {
        $MB.n_warning("请选择个性化要素:补贴名称！");
        return false;
    }
    if(smsContent === '') {
        $MB.n_warning("文案内容不能为空！");
        return false;
    }

    //商品详情页url是否可用 Y表示可用
    if('Y'===prodUrlEnabled)
    {
        if(isProductUrl === undefined) {
            $MB.n_warning("请选择个性化要素:商品详情页短链接！");
            return false;
        }

        if(isProductUrl === '1') {
            if(smsContent.indexOf("${商品详情页短链}") === -1) {
                $MB.n_warning("个性化要素:商品详情页短链接为是，文案内容未发现${商品详情页短链}！");
                return false;
            }
        }
        if(isProductUrl === '0') {
            if(smsContent.indexOf("${商品详情页短链}") > -1) {
                $MB.n_warning("个性化要素:商品详情页短链接为否'，文案内容不能出现${商品详情页短链}！");
                return false;
            }
        }
    }else
    {
        if(smsContent.indexOf("${商品详情页短链}") > -1) {
            $MB.n_warning("当前系统配置不允许出现${商品详情页短链}变量！");
            return false;
        }
    }

    if(isProductName === '1') {
        if(smsContent.indexOf("${商品名称}") === -1) {
            $MB.n_warning("个性化要素:商品名称为是，文案内容未发现${商品名称}！");
            return false;
        }
    }
    if(isProductName === '0') {
        if(smsContent.indexOf("${商品名称}") > -1) {
            $MB.n_warning("个性化要素:商品名称为否，文案内容不能出现${商品名称}！");
            return false;
        }
    }

    //用户自行领券
    if(couponSendType === 'A') {
        if(isCouponUrl === undefined) {
            $MB.n_warning("请选择个性化要素:补贴短链接！");
            return false;
        }

        //不体现短链名称个性化
        if(isCouponUrl === '0') {
            if(smsContent.indexOf("${补贴短链}") > -1) {
                $MB.n_warning("个性化要素:补贴短链接为否，文案内容不能出现${补贴短链}！");
                return false;
            }
        }
        //不体现补贴名称个性化
        if(isCouponName === '0') {
            if(smsContent.indexOf("${补贴名称}") > -1) {
                $MB.n_warning("个性化要素:补贴名称为否，文案内容不能出现${补贴名称}！");
                return false;
            }
        }

        // 补贴短链 需要个性化
        if(isCouponUrl === '1') {
            if(smsContent.indexOf("${补贴短链}") === -1) {
                $MB.n_warning("个性化要素:补贴短链接为是，文案内容未发现${补贴短链}！");
                return false;
            }
        }
        if(isCouponName === '1') {
            if(smsContent.indexOf("${补贴名称}") === -1) {
                $MB.n_warning("个性化要素:补贴名称为是，文案内容未发现${补贴名称}！");
                return false;
            }
        }

        if(isCouponName === '1' && isCouponUrl === '0') {
            $MB.n_warning("个性化要素:补贴名称为是，则个性化要素:补贴链接也必须选是！");
            return false;
        }
    }
    //自动发送券到用户账号
    if(couponSendType === 'B') {
        if(smsContent.indexOf("${补贴短链}") > -1) {
            $MB.n_warning("当前系统配置不允许出现${补贴短链}变量！");
            return false;
        }

        if(isCouponName === '1') {
            if(smsContent.indexOf("${补贴名称}") === -1) {
                $MB.n_warning("个性化要素:补贴名称为是，文案内容未发现${补贴名称}！");
                return false;
            }
        }else {
            if(smsContent.indexOf("${补贴名称}") > -1) {
                $MB.n_warning("个性化要素补贴名称为否，文案内容不能出现${补贴名称}！");
                return false;
            }
        }
    }
    return true;
}

/**
 * 用户输入文案内容时触发的事件
 */
function smsContentValid() {
    $('#smsContentInput').val($('#smsContent').val());
    if($('#smsContentInput').val() !== '') {
        $('#smsContentInput').removeClass('error');
        $("#smsContentInput-error").remove();
    }
    let content = $('#smsContent').val() === "" ? "请输入短信内容": $('#smsContent').val();
    $("#article").html('').append(content);
}

$("#add_modal").on('hidden.bs.modal', function () {
    $('#smsCode').val("");
    $('#smsContent').val("");
    $('#smsContentInput').val("");
    $("#word").text("0:编写内容字符数 / 0:填充变量最大字符数 / "+smsLengthLimit+":文案总字符数");
    $("#fontNum").val('');
    $("#remark").val('');
    $("#smsTemplateAddForm").validate().resetForm();

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

    var content = "请输入短信内容";
    $("#article").html('').append(content);
});


$("#btn_edit").click(function () {
    var selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择要修改的文案！');
        return;
    }

    let smsCode =selectRows[0]["smsCode"];
    $.get("/smsTemplate/smsIsUsed", {smsCode:smsCode}, function (r) {
        if(r) {
            // 被引用 给出提示
            $MB.n_warning("当前文案已被群组引用，修改后将会立即生效！！");
        }
        $.getJSON("/smsTemplate/getSmsTemplate?smsCode="+smsCode,function (resp) {
            if (resp.code === 200){
                $("#msg_modal").modal('hide');
                var data = resp.data;
                $("#smsCode").val(data.smsCode);
                $("#smsContent").val(data.smsContent);
                $("#smsContentInput").val(data.smsContent);
                $("input[name='isCouponUrl']:radio[value='" + data.isCouponUrl + "']").prop("checked", true);
                $("input[name='isCouponName']:radio[value='" + data.isCouponName + "']").prop("checked", true);
                $("input[name='isProductName']:radio[value='" + data.isProductName + "']").prop("checked", true);
                $("input[name='isProductUrl']:radio[value='" + data.isProductUrl + "']").prop("checked", true);
                $("#remark").val(data.remark);
                $("#myLargeModalLabel3").text("修改文案");
                $("#btn_save_sms").attr("name", "update");
                $("#add_modal").modal('show');
                $("#article").html('').append(data.smsContent);
                initGetInputNum();
            }else {
                $MB.n_danger(resp.msg);
            }
        });
    });
});

/**
 * 编辑 - 设置初始的字数统计信息
 */
function initGetInputNum() {
    let smsContent = $('#smsContent').val();
    let y = smsContent.length;
    let m = smsContent.length;
    let n = smsContent.length;
    if(smsContent.indexOf('${补贴短链}') > -1) {
        y = y - '${补贴短链}'.length + shortUrlLen;
        m = m - '${补贴短链}'.length;
    }
    if(smsContent.indexOf('${补贴名称}') > -1) {
        y = y - '${补贴名称}'.length + couponNameLen;
        m = m - '${补贴名称}'.length;
    }
    if(smsContent.indexOf('${商品名称}') > -1) {
        y = y - '${商品名称}'.length + prodNameLen;
        m = m - '${商品名称}'.length;
    }
    if(smsContent.indexOf('${商品详情页短链}') > -1) {
        y = y - '${商品详情页短链}'.length + shortUrlLen;
        m = m - '${商品详情页短链}'.length;
    }
    total_num = y;
    var code = "";
    code += m + ":编写内容字符数 / " + y + ":填充变量最大字符数 / " + smsLengthLimit + ":文案总字符数";
    $("#word").text(code);
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
        //当前文案替换变量后的长度
        let y = smsContent.length;
        //当前文案的长度
        let m = smsContent.length;
        if(smsContent.indexOf('${补贴短链}') > -1) {
            y = y - '${补贴短链}'.length + shortUrlLen;
            m = m - '${补贴短链}'.length;
        }
        if(smsContent.indexOf('${补贴名称}') > -1) {
            y = y - '${补贴名称}'.length + couponNameLen;
            m = m - '${补贴名称}'.length;
        }
        if(smsContent.indexOf('${商品名称}') > -1) {
            y = y - '${商品名称}'.length + prodNameLen;
            m = m - '${商品名称}'.length;
        }
        if(smsContent.indexOf('${商品详情页短链}') > -1) {
            y = y - '${商品详情页短链}'.length + shortUrlLen;
            m = m - '${商品详情页短链}'.length;
        }
        total_num = y;
        let code = "";
        code += m + ":编写内容字符数 / " + y + ":填充变量最大字符数 / " + smsLengthLimit + ":文案总字符数";
        $("#word").text(code);
    });
}

$("#add_modal").on('shown.bs.modal', function () {

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
