$(function () {
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
                param: {smsCode: $("input[name='smsCode']").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'smsCode',
            title: '模板编码'
        }, {
            field: 'smsContent',
            title: '模板内容'
        }, {
            field: 'isCoupon',
            title: '是否有券',
            formatter: function (value, row, index) {
                let res = "-";
                if(value == '1') {
                    res = "是";
                }
                if(value == '0') {
                    res = "否";
                }
                return res;
            }
        }]
    };
    $MB.initTable('smsTemplateTable', settings);
    //为刷新按钮绑定事件
    $("#btn_refresh").on("click",function () {
        $('#smsTemplateTable').bootstrapTable('refresh');
    });

    statInputNum($("#smsContent"),$("#word"));
    statInputNum($("#smsContent1"),$("#word1"));
});

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
        $MB.n_warning('请选择需要测试的模板！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];

    //根据获取到的数据查询
    $.getJSON("/smsTemplate/getSmsTemplate?smsCode="+smsCode,function (resp) {
        if (resp.code === 200){
            //更新测试面板
            $("#smsCode1").val(resp.data.smsCode);
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
    var phoneNum=$('#phoneNum').val();
    var smsContent= $('#smsContent1').val();

    if(null==phoneNum||phoneNum=='')
    {
        $MB.n_warning("手机号不能为空！");
        return;
    }

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
        $MB.n_warning('请选择要删除的模板！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];

    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除短信模板？'
    }, function () {
        $.getJSON("/smsTemplate/deleteSmsTemplate?smsCode="+smsCode,function (resp) {
            if (resp.code === 200){
                //提示成功
                $MB.n_success('删除成功！');
                //刷新表格
                $('#smsTemplateTable').bootstrapTable('refresh');
            }else {
                $MB.n_danger(resp.msg);
            }
        })
    });
}

function add() {
    $('#smsCode').val("").removeAttr("readOnly");
    $('#smsContent').val("");
    $("input[name='isCoupon']:radio").removeAttr("checked");
    $("#word").text("0");
    $("#myLargeModalLabel3").text("新增模板");
    $("#btn_save").attr("name", "save");
    $('#add_modal').modal('show');
}

// 字数统计
$("#btn_cal").click(function () {
    var data = getSmsContentFontCount();
    $("#fontNum").val(data.count + "个字");
});

function validCouponSendType(isCoupon) {
    let couponSendType = getCouponSendType();
    let smsContent = $('#smsContent').val();
    if(smsContent === '') {
        $MB.n_warning("模板内容不能为空！");
        return false;
    }
    if(couponSendType === 'A') { // 包含${COUPON_URL}
        // 不能包含url
        if(isCoupon === '0') {
            if(smsContent.indexOf("${COUPON_URL}") > -1) {
                $MB.n_warning("选择不包含券，模板内容不能出现${COUPON_URL}");
                return false;
            }
        }

        // 不能包含url
        if(isCoupon === '1') {
            if(smsContent.indexOf("${COUPON_URL}") === -1) {
                $MB.n_warning("优惠券发放方式为自行领取，模板内容未发现${COUPON_URL}");
                return false;
            }
        }
    }
    if(couponSendType === 'B') { // 不包含${COUPON_URL}
        if(smsContent.indexOf("${COUPON_URL}") > -1) {
            $MB.n_warning("优惠券发放方式为系统发送，模板内容发现${COUPON_URL}");
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
$("#btn_save").click(function () {
    var alert_str="";

    //验证
    var smsCode= $('#smsCode').val();
    var smsContent= $('#smsContent').val();
    var isCoupon = $("input[name='isCoupon']:checked").val();

    if(null==smsCode||smsCode=='')
    {
        alert_str+='模板编码不能为空！';
    }

    if(null==smsContent||smsContent=='')
    {
        alert_str+='模板内容不能为空！';
    }

    if(null==isCoupon||isCoupon==' ')
    {
        alert_str+='请选择是否包含优惠券！';
    }

    if(!validCouponSendType(isCoupon)) {
        return;
    }

    var data = getSmsContentFontCount();
    if(!data.valid) {
        alert_str+='短信模板字数超出最大限制！';
    }

    if(null!=alert_str&&alert_str!='')
    {
        $MB.n_warning(alert_str);
        return;
    }
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


    //提示是否要保存
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认提交数据？'
    }, function () {
        var param = new Object();
        param.smsCode=smsCode;
        param.smsContent=smsContent;
        param.isCoupon = isCoupon;

        $.ajax({
            url: url,
            data: JSON.stringify(param),
            type: 'POST',
            contentType: "application/json;charset=utf-8",
            success: function (r) {
                if(r.code === 200) {
                    $MB.n_success(success_msg);
                }else {
                    $MB.n_danger("未知错误发生，请检查！");
                }
                $('#add_modal').modal('hide');
                $('#smsTemplateTable').bootstrapTable('refresh');
            }
        });
    });

});

$("#add_modal").on('hidden.bs.modal', function () {
    $('#smsCode').val("");
    $('#smsContent').val("");
    $("input[name='isCoupon']").removeAttr("disabled");
});

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
        $MB.n_warning('请选择要修改的模板！');
        return;
    }

    var smsCode =selectRows[0]["smsCode"];
    $.getJSON("/smsTemplate/getSmsTemplate?smsCode="+smsCode,function (resp) {
        if (resp.code === 200){
            var data = resp.data;
            $("#smsCode").val(data.smsCode).attr("readOnly", true);
            $("#smsContent").val(data.smsContent);
            statInputNum($("#smsContent"),$("#word"));
            $("input[name='isCoupon']:radio[value='" + data.isCoupon + "']").prop("checked", true);
            $("input[name='isCoupon']").attr("disabled", "disabled");
            $("#myLargeModalLabel3").text("修改模板");
            $("#btn_save").attr("name", "update");
            $("#add_modal").modal('show');
        }
    });
});