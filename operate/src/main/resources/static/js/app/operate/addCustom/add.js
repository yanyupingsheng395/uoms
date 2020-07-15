let custom_step = steps({
    el: "#addStep",
    data: [
        {title: "选择目标用户", description: ""},
        {title: "配置触发机制", description: ""},
        {title: "编辑申请消息", description: ""}
    ],
    center: true,
    dataOrder: ["title", "line", "description"]
});

// 筛选用户
function selectUser(flag) {
    if(flag) {
        $("#userPropTable").find("tr td:not(:nth-child(1))").bind('click', function (e) {
            if($(this).text() !== '') {
                if($(this).is(".selected_td")) {
                    $(this).removeClass('selected_td');
                }else {
                    $(this).addClass('selected_td');
                }
            }
        });
    }else {
        $("#userPropTable").find("tr td:not(:nth-child(1))").removeClass('selected_td');
        $("#userPropTable").find("tr td:not(:nth-child(1))").unbind('click');
    }
}

function stepBreak(idx) {
    if(idx == 0) {
        custom_step.setActive(0);
        $("#step1").attr("style", "display:block;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:none;");
    }
    if(idx == 1) {
        custom_step.setActive(1);
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:block;");
        $("#step3").attr("style", "display:none;");
    }
    if(idx == 2) {
        custom_step.setActive(2);
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:block;");
    }
}

// 短信内容验证
function smsContentValid() {
    $('#smsContentInput').val($('#smsContent').val());
    if($('#smsContentInput').val() !== '') {
        $('#smsContentInput').removeClass('error');
        $("#smsContentInput-error").remove();
    }
    var content = $('#smsContent').val() === "" ? "请输入短信内容": $('#smsContent').val();
    $("#article").html('').append(content);
}

statTmpContentNum();
// 设置文案当前的字数
function statTmpContentNum() {
    var PROD_NAME_LEN = 12;
    var COUPON_NAME_LEN = 12;
    var CHANNEL_LEN = 12;
    var SMS_LEN_LIMIT = 70;
    $("#smsContent").on('input propertychange', function () {
        let smsContent = $('#smsContent').val();
        let y = smsContent.length;
        let m = smsContent.length;
        if(smsContent.indexOf('${商品名称}') > -1) {
            y = y - '${商品名称}'.length + parseInt(PROD_NAME_LEN);
            m = m - '${商品名称}'.length;
        }
        if(smsContent.indexOf('${补贴名称}') > -1) {
            y = y - '${补贴名称}'.length + parseInt(COUPON_NAME_LEN);
            m = m - '${补贴名称}'.length;
        }
        if(smsContent.indexOf('${渠道名称}') > -1) {
            y = y - '${渠道名称}'.length + parseInt(CHANNEL_LEN);
            m = m - '${渠道名称}'.length;
        }
        total_num = y;
        var code = "";
        code += m + ":编写内容字符数 / " + y + ":填充变量最大字符数 / " + SMS_LEN_LIMIT + ":文案总字符数";
        $("#word").text(code);
    });
}

// 获取短链
function getShortUrl() {
    var url = $("#longUrl").val();
    if(url.trim() == "") {
        $MB.n_warning("长链不能为空!");
        return;
    }
    $.get("/coupon/getShortUrl", {url: url}, function(r) {
        if(r.code === 200) {
            $("#shortUrl").val(r.data);
            $MB.n_success("生成短链成功!");
        }else {
            $MB.n_danger(r['msg']);
        }
    });
}

getQrData();
function getQrData() {
    var code = "";
    $.get("/contactWay/getContactWayData", {}, function(r) {
        var data = r.rows;
        data.forEach((k, v)=>{
            code += "<option value='"+k['id']+"'>"+k['state']+"</option>";
        });
        $("#qrData").html(code);
    });
}