let custom_step = steps({
    el: "#addStep",
    data: [
        {title: "选择目标用户", description: ""},
        {title: "编辑申请消息", description: ""}
    ],
    center: true,
    dataOrder: ["title", "line", "description"]
});
$(function () {
    if(opType === 'update' && sendType === '1') {
        selectUser(true);
    }
});
// 筛选用户
function selectUser(flag) {
    if(!flag) {
        $("#selectUserDiv").attr("style", "display:none;");
    }else {
        $("#selectUserDiv").attr("style", "display:block;");
    }
}

function stepBreak(idx) {
    if(idx == 0) {
        custom_step.setActive(0);
        $("#step1").attr("style", "display:block;");
        $("#step2").attr("style", "display:none;");
    }
    if(idx == 1) {
        if(validData(idx)) {
            custom_step.setActive(1);
            $("#step1").attr("style", "display:none;");
            $("#step2").attr("style", "display:block;");
        }
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
        if(smsContent.indexOf('${二维码短链}') > -1) {
            y = y - '${二维码短链}'.length + parseInt(CHANNEL_LEN);
            m = m - '${二维码短链}'.length;
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

// 获取二维码数据
function selectQrCode() {
    var settings = {
        url: "/contactWay/getList",
        cache: false,
        clickToSelect:true,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {qstate:''}
            };
        },
        columns: [
            {
                checkbox:true
            },{
            field: 'qrCode',
            align: "center",
            title: '二维码样式',
            formatter: function (value, row, index) {
                return "<img src='"+value+"' width='100' height='100'>";
            }
        },{
            field: 'usersList',
            align: "center",
            title: '可联系成员数（人）',
            formatter: function (value, row, index) {
                if(value !== '') {
                    var tmp = value.split(",");
                    return tmp.length;
                }
            }
        }, {
            field: 'type',
            align: "center",
            title: '二维码类型',
            formatter: function (value, row, index) {
                if(value === '1') {
                    return '单人';
                }
                if(value === '2') {
                    return '多人';
                }
            }
        }, {
            field: 'state',
            align: "center",
            title: '渠道'
        }, {
            field: 'remark',
            align: "center",
            title: '备注'
        },{
            field: 'createDt',
            align: "center",
            title: '创建人'
        }]
    };
    $("#qrDataTable").bootstrapTable(settings);
    $("#selectQrModal").modal('show');
}

// 选择二维码
function setQrCode() {
    var selected = $("#qrDataTable").bootstrapTable('getSelections');
    if(selected.length === 0) {
        $MB.n_warning("请选择二维码");
        return;
    }
    $("#qrId").val(selected[0].contactWayId);
    $("#selectQrModal").modal('hide');
    $("#selectQrCodeBtn").attr("class", "btn btn-info btn-sm");
    $("#selectQrCodeBtn").html('<i class="fa fa-check-square-o"></i>&nbsp;选择二维码');
}

var daily_user_cnt;
var daily_apply_rate;
var head_id;
// 保存数据
function saveData(opType) {
    // 验证表单数据
    if(validData(1)) {
        if(opType === 'save') {
            var taskName = $("input[name='taskName']").val();
            var sendType = $("input[name='sendType']:checked").val();
            var sourceId = selected_source_code;
            var sourceName = selected_source_name;
            var regionId = selected_city_code.join(",");
            var regionName = selected_city_name.join(",");
            if(sendType === '0') {
                sourceId = '';
                regionId = '';
            }
            $MB.confirm({
                title: '提示:',
                content: '确认保存数据？'
            }, function () {
                $.post("/addUser/saveData",{taskName:taskName, sendType: sendType, sourceId: sourceId,
                        regionId: regionId, sourceName: sourceName, regionName: regionName},
                    function (r) {
                        if(r.code === 200) {
                            $MB.n_success("保存成功！");
                            $("#sendUserDataDiv").attr("style", "display:block;");
                            $("#saveBasicBtn").attr("style", "display:none;");
                            $("input[name='taskName']").attr("disabled", "disabled").attr("readOnly", "readOnly");
                            $("input[name='sendType']").attr("disabled", "disabled").attr("readOnly", "readOnly");
                            $("input[name='dailyUserCnt']").val(r.data['dailyUserCnt']);
                            daily_user_cnt = r.data['dailyUserCnt'];
                            daily_apply_rate = r.data['dailyApplyRate'];
                            $("input[name='dailyApplyRate']").val(r.data['dailyApplyRate']);
                            $("input[name='dailyAddUserCnt']").val(r.data['dailyAddUserCnt']);
                            $("input[name='dailyWaitDays']").val(r.data['dailyWaitDays']);
                            $("input[name='dailyAddTotal']").val(r.data['dailyAddTotal']);
                            head_id = r.data['id'];
                        }else {
                            $MB.n_danger("保存失败！");
                        }
                    });
            });
        }else {
            $MB.confirm({
                title: '提示:',
                content: '确认修改数据？'
            }, function () {
                $.post("/addUser/editConfig",
                    $("#dataForm").serialize() + '&userValue=' + userValue.join() + "&lifeCycle=" + lifeCycle.join() +
                    "&pathActive=" + pathActive.join() + "&userGrowth=" + userGrowth.join(),
                    function (r) {
                        if(r.code === 200) {
                            $MB.n_success("修改成功！");
                            setTimeout(function () {
                                window.location.href = "/page/addCustom";
                            }, 1400);
                        }else {
                            $MB.n_danger("修改失败！");
                        }
                    });
            });
        }
    }
}

function userDataChange(dom, idx) {
    if(($("input[name='dailyUserCnt']").val() !== daily_user_cnt) || ($("input[name='dailyApplyRate']").val() !== daily_apply_rate)) {
        $("#saveDailyUserBtn").attr("style", "display:inline-block;");
    }else {
        $("#saveDailyUserBtn").attr("style", "display:none;");
    }
}
// 调整每日转化用户数
function saveDailyUserData() {
    $.get("/addUser/saveDailyUserData", {headId: head_id, dailyUserCnt:daily_user_cnt, dailyApplyRate:daily_apply_rate}, function (r) {
        if(r.code === 200) {
            $("input[name='dailyApplyRate']").val(r.data['dailyApplyRate']);
            $("input[name='dailyAddUserCnt']").val(r.data['dailyAddUserCnt']);
            $("input[name='dailyWaitDays']").val(r.data['dailyWaitDays']);
            $MB.n_success("更新成功！");
        }
    });
}
// 验证数据
function validData(stepIndex) {
    // 验证第一步
    if(stepIndex == 1) {
        var sendType = $("input[name='sendType']:checked").val();
        if(sendType === '1') {
            if(selected_city_code.length == 0) {
                $MB.n_warning("请选择地域！");
                return false;
            }
            if(selected_source_code === null) {
                $MB.n_warning("请选择渠道！");
                return false;
            }
        }
        var taskName = $("input[name='taskName']").val();
        if(taskName === '') {
            $MB.n_warning("请输入任务名称！");
            return false;
        }
        return true;
    }

    // 验证第二步
    if(stepIndex == 2) {
        var isChannel = $("input[name='isChannel']:checked").val();
        var isProduct = $("input[name='isProduct']:checked").val();
        var isCoupon = $("input[name='isCoupon']:checked").val();
        var isQrcode = $("input[name='isQrcode']:checked").val();
        if(isChannel === undefined) {
            $MB.n_warning("请选择渠道名称！");
            return false;
        }
        if(isProduct === undefined) {
            $MB.n_warning("请选择商品名称！");
            return false;
        }
        if(isCoupon === undefined) {
            $MB.n_warning("请选择补贴名称！");
            return false;
        }
        if(isQrcode === undefined) {
            $MB.n_warning("请选择二维码短链！");
            return false;
        }
        var qrId = $("#qrId").val();
        if(qrId === '') {
            $MB.n_warning("请配置二维码短链！");
            return false;
        }
        var smsContent = $("#smsContent").val();
        if(smsContent === undefined || smsContent === '') {
            $MB.n_warning("请输入短信内容！");
            return false;
        }
        if(isChannel === '1' && smsContent.indexOf('${渠道名称}') == -1) {
            $MB.n_warning("渠道名称：是，文案中没有发现${渠道名称}模板变量！");
            return false;
        }
        if(isChannel === '0' && smsContent.indexOf('${渠道名称}') > -1) {
            $MB.n_warning("渠道名称：否，文案中发现${渠道名称}模板变量！");
            return false;
        }

        if(isProduct === '1' && smsContent.indexOf('${商品名称}') == -1) {
            $MB.n_warning("商品名称：是，文案中没有发现${商品名称}模板变量！");
            return false;
        }
        if(isProduct === '0' && smsContent.indexOf('${商品名称}') > -1) {
            $MB.n_warning("商品名称：否，文案中发现${商品名称}模板变量！");
            return false;
        }

        if(isCoupon === '1' && smsContent.indexOf('${补贴名称}') == -1) {
            $MB.n_warning("补贴名称：是，文案中没有发现${补贴名称}模板变量！");
            return false;
        }
        if(isCoupon === '0' && smsContent.indexOf('${补贴名称}') > -1) {
            $MB.n_warning("补贴名称：否，文案中发现${补贴名称}模板变量！");
            return false;
        }

        if(isQrcode === '1' && smsContent.indexOf('${二维码短链}') == -1) {
            $MB.n_warning("二维码短链：是，文案中没有发现${二维码短链}模板变量！");
            return false;
        }
        if(isQrcode === '0' && smsContent.indexOf('${二维码短链}') > -1) {
            $MB.n_warning("二维码短链：否，文案中发现${二维码短链}模板变量！");
            return false;
        }
        // 判断文案的长度是否超最大限制
        return true;
    }
    return false;
}

// 选择城市modal
function selectCityModal() {
    $("#selectCityModal").modal('show');
}

var selected_city_code = [];
var selected_city_name = [];
function addCity() {
    var cityName = $("#city").find("option:selected").val();
    var cityCode = $("#city").find("option:selected").attr("data-code");
    if(selected_city_code.indexOf(cityCode) == -1) {
        if(selected_city_code.length <= 4) {
            $("#cityTags").append("<span class=\"tag\"><span>" + cityName + "&nbsp;&nbsp;</span><a style=\"color: #fff;cursor: pointer;\" onclick=\"$(this).parent().remove()\">x</a></span>");
            selected_city_code.push(cityCode);
            selected_city_name.push(cityName);
            console.log(selected_city_name)
        }else {
            $MB.n_warning("最多可选5个城市！");
        }
    }
}

function addCityClick() {
    if(selected_city_name.length == 0) {
        $MB.n_warning("您尚未选择城市！");
    }else {
        var code = "";
        selected_city_name.forEach((v, k)=>{
            code += "<button type=\"button\" class=\"btn btn-round btn-sm btn-warning m-t-5\">"+v+"</button>&nbsp;";
        });
        $("#cityDiv").html(code);
    }
    $("#selectCityModal").modal('hide');
}

getSource();
function getSource() {
    $.get("/addUser/getSource", {}, function (r) {
        var code = "";
        r.data.forEach((v, k)=>{
            code += "<button type=\"button\" \n" +
                "class=\"btn btn-round btn-sm btn-secondary m-t-5\" onclick='sourceClick(this, \""+v['id']+"\",  \""+v['name']+"\")'>"+v['name']+"\n" +
                "</button>&nbsp;";
        });
        $("#sourceDiv").html(code);
    });
}

var selected_source_code = null;
var selected_source_name = null;
function sourceClick(dom, id, name) {
    selected_source_code = id;
    selected_source_name = name;
    $(dom).addClass('btn-info').removeClass('btn-secondary');
    $(dom).siblings().removeClass('btn-info').addClass('btn-secondary');
}