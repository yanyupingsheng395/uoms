var IS_COUPON_NAME_DISABLED;
var IS_COUPON_URL_DISABLED;
var IS_PROD_URL_DISABLED;
$(function () {
    statInputNum();
    //根据变量配置一些信息是否显示
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

});

/**
 * 打开短信消息配置面板
 * @param groupId
 * @param smsCode
 * @param userValue
 * @param lifecycle
 * @param pathActive
 */
function openSmsTemplateModal(groupId, smsCode, userValue, lifecycle, pathActive) {
    $( "#currentGroupId").val(groupId);
    //加载短信列表
    smsTemplateTable();
    getGroupDescription(userValue, lifecycle, pathActive, 'selectedGroupInfo1');
    $( "#smsTemplateModal" ).modal( 'show' );
}

/**
 * 获取短信模板列表
 */
function smsTemplateTable() {
    var settings = {
        url: '/smsTemplate/selectSmsTemplateListWithGroup',
        pagination: true,
        clickToSelect: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset,
                groupId: $("#currentGroupId").val()
            };
        },
        columns: [
            {
                checkbox: true,
                formatter: function (value, row, index) {
                    if(row.currentFlag=='1') {
                        return {
                            checked : true
                        };
                    }
                }
            },
            {
                field: 'currentFlag',
                title: '当前选中文案',
                visible: false
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
    $( "#smsTemplateTable" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

// 新增文案
function add() {
    $("#smsTemplateModal").modal('hide');
    $('#smsCode').val("");
    $('#smsContent').val("");
    $("#snum1").text("0");
    $("#snum2").text("0");
    $("#snum3").text("0");
    $("#myLargeModalLabel3").text("新增文案");
    $("#btn_save_sms").attr("name", "save");
    $('#add_modal').modal('show');
}

/**
 * 为当前组设置文案信息
 */
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
    $.get( '/dailyConfig/setSmsCode', {groupId: groupId, smsCode: smsCode}, function (r) {
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
    $("#smsCode").attr("disabled", "disabled");
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
function statInputNum() {
    $("#smsContent").on('input propertychange', function () {
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
        let total_num = y+signatureLen+unsubscribeLen;
        let snum3=0;
        $("#snum1").text(m);
        $("#snum2").text(total_num);

        if(total_num<=70)
        {
            snum3=1;
        }else
        {
            snum3=total_num%67===0?total_num/67:(parseInt(total_num/67)+1);
        }
        //计算文案的条数
        $("#snum3").text(snum3);
    });
}

/**
 * 编辑 -初始化字数统计信息
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
    let total_num = y+signatureLen+unsubscribeLen;
    let snum3=0;
    $("#snum1").text(m);
    $("#snum2").text(total_num);

    if(total_num<=70)
    {
        snum3=1;
    }else
    {
        snum3=total_num%67===0?total_num/67:(parseInt(total_num/67)+1);
    }
    //计算文案的条数
    $("#snum3").text(snum3);
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
            $("#smsTemplateModal").modal('hide');
            //更新测试面板
            $("#testSmsContent").val(resp.data);
            $("#testSmsCode").val(smsCode);
            let _value = $("#testSmsContent").val();
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

    let param = {};
    param.phoneNum=phoneNum;
    param.smsCode=testSmsCode;

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
                    content: '当前文案已被用户组引用，确认要删除？'
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
                $MB.n_warning("个性化要素:商品详情页短链接为是，文案内容未发现${商品详情页短链}");
                return false;
            }
        }
        if(isProductUrl === '0') {
            if(smsContent.indexOf("${商品详情页短链}") > -1) {
                $MB.n_warning("个性化要素:商品详情页短链接为否'，文案内容不能出现${商品详情页短链}");
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
            $MB.n_warning("个性化要素:商品名称为是，文案内容未发现${商品名称}");
            return false;
        }
    }
    if(isProductName === '0') {
        if(smsContent.indexOf("${商品名称}") > -1) {
            $MB.n_warning("个性化要素:商品名称为否，文案内容不能出现${商品名称}");
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
                $MB.n_warning("个性化要素:补贴短链接为否，文案内容不能出现${补贴短链}");
                return false;
            }
        }
        //不体现补贴名称个性化
        if(isCouponName === '0') {
            if(smsContent.indexOf("${补贴名称}") > -1) {
                $MB.n_warning("个性化要素:补贴名称为否，文案内容不能出现${补贴名称}");
                return false;
            }
        }

        // 补贴短链 需要个性化
        if(isCouponUrl === '1') {
            if(smsContent.indexOf("${补贴短链}") === -1) {
                $MB.n_warning("个性化要素:补贴短链接为是，文案内容未发现${补贴短链}");
                return false;
            }
        }
        if(isCouponName === '1') {
            if(smsContent.indexOf("${补贴名称}") === -1) {
                $MB.n_warning("个性化要素:补贴名称为是，文案内容未发现${补贴名称}");
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

// 新增&修改
$("#btn_save_sms").click(function () {
    var url = "";
    var success_msg = "";
    var sure_msg = "";
    if($(this).attr("name") == "save") {
        url = "/smsTemplate/addSmsTemplate";
        success_msg = "新增文案成功！";
        sure_msg = "确定要新增文案？";
    }else {
        url = "/smsTemplate/updateSmsTemplate";
        success_msg = "修改文案成功！";
        sure_msg = "确定要修改文案？";
    }
    if(validate()) {
        //提示是否要保存
        $MB.confirm({
            title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
            content: sure_msg
        }, function () {
            $("#smsCode").removeAttr("disabled");
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
    if(!validateTemplate()) {
        return false;
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
    var content = $('#smsContent').val() === "" ? "请输入短信内容": signature+$('#smsContent').val()+unsubscribe;
    $("#article").html('').append(content);
}

$("#add_modal").on('hidden.bs.modal', function () {
    $('#smsCode').val("");
    $('#smsContent').val("");
    $('#smsContentInput').val("");
    $("#snum1").text("0");
    $("#snum2").text("0");
    $("#snum3").text("0");

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


$("#btn_edit").click(function () {
    let selectRows=$("#smsTemplateTable").bootstrapTable('getSelections');
    if(null==selectRows||selectRows.length==0)
    {
        lightyear.loading('hide');
        $MB.n_warning('请选择需要编辑的文案！');
        return;
    }

    let smsCode =selectRows[0]["smsCode"];
    //当前正在使用的文案
    let currentFlag=selectRows[0]["currentFlag"];

    //获取引用当前文案的群组信息
    $.get("/smsTemplate/getSmsUsedGroupInfo", {smsCode:smsCode}, function (r) {
        let data = r.data;
        let len = data.length;
        let code = "";
        data.forEach((v, k)=>{
            code += "<br/>" + v + "；";
        });

        //如果当前文案被引用次数>1 或者被引用次数是1但是不是当前组正在使用的文案，则给出提示
        if(len>1||(len==1&&currentFlag=='0')) {

            $.confirm({
                title: "提示：",
                content: "您选择的文案正在被以下" + len + "个群组引用，修改将会引起这些群组的文案被同步修改!" +
                    "<br/>------------------------------------------------------" + code,
                type: 'orange',
                typeAnimated: true,
                buttons: {
                    modify:
                        {
                            text: '确定修改',
                            btnClass: 'btn-orange',
                            action: function () {
                                //编辑
                                modifySmsTemplate(smsCode);
                            }
                        },
                    close: {
                        text: '取消',
                        action: function () {
                            $MB.loading('hide');
                        }
                    }
                }
            });
        }else {
             //直接打开修改框
            modifySmsTemplate(smsCode);
        }
    });
});

function modifySmsTemplate(smsCode)
{
    $.getJSON("/smsTemplate/getSmsTemplate?smsCode="+smsCode,function (resp) {
        if (resp.code === 200){
            $("#smsTemplateModal").modal('hide');
            var data = resp.data;
            $("#smsCode").val(data.smsCode);
            $("#smsContent").val(data.smsContent);
            $("#article").html('').append(signature+data.smsContent+unsubscribe);
            $("#smsContentInput").val(data.smsContent);
            $("input[name='isCouponUrl']:radio[value='" + data.isCouponUrl + "']").prop("checked", true);
            $("input[name='isCouponName']:radio[value='" + data.isCouponName + "']").prop("checked", true);
            $("input[name='isProductName']:radio[value='" + data.isProductName + "']").prop("checked", true);
            $("input[name='isProductUrl']:radio[value='" + data.isProductUrl + "']").prop("checked", true);

            $("#myLargeModalLabel3").text("修改文案");
            $("#btn_save_sms").attr("name", "update");
            $("#add_modal").modal('show');

            initGetInputNum();
        }else {
            $MB.n_danger(resp.msg);
        }
    });
}

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

function copyString(id) {
    $('#' + id).select();
    document.execCommand("copy");
    $MB.n_success("成功复制到粘贴板!");
}