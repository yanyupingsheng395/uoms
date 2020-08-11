let custom_step;
let daily_user_cnt;
let daily_apply_rate;
let head_id;
$(function () {
    // 初始化步骤条
    custom_step= steps({
        el: "#addStep",
        data: [
            {title: "选择目标用户", description: ""},
            {title: "编辑申请消息", description: ""}
        ],
        center: true,
        dataOrder: ["title", "line", "description"]
    });

    if(opType === 'update' && sendType === '1') {
        selectUser(true);

        var code = "";
        selected_city_name.forEach((v, k)=>{
            code += "<button type=\"button\" class=\"btn btn-round btn-sm btn-warning m-t-5\">"+v+"</button>&nbsp;";
        });
        $("#cityDiv").html(code);
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

// 切换步骤页面
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

// 获取二维码数据
function selectQrCode() {
    let settings = {
        url: '/contactWay/getList',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [{
            checkbox: true
        },  {
            field: 'qrCode',
            title: '二维码样式',
            align: 'center',
            formatter: function (value, row, indx)
            {
                return "<img style='width:120px;height:120px' src='"+value+"'><a href='/contactWay/download?configId="+row.configId+"' style='font-size: 12px;' target='_blank'>下载</a>" +
                    "&nbsp;&nbsp;<a style='font-size: 12px;cursor: pointer;' data-clipboard-text='"+value+"' class='copy_btn'>复制二维码地址</a>";
            }
        }, {
            field: 'shortUrl',
            title: '海报短链接',
            align: 'center',
            formatter: function (value, row, indx) {
                return "<a href=http://"+value+" target='_blank'>"+value+"</a>";
            }
        },  {
            field: 'usersList',
            title: '可联系成员',
            align: 'center',
            width: 240,
            formatter: function (value,row,indx) {
                let arr=value.split(',');
                let result='';
                if(null!=arr&&arr.length>0)
                {
                    for(let i=0;i<arr.length;i++)
                    {
                        if(i>=1)
                        {
                            result+= "&nbsp;&nbsp;<span><i class='mdi mdi-account' style='color:#33cabb'></i>"+arr[i]+"</span>";
                        }else
                        {
                            result+= "<span><i class='mdi mdi-account' style='color:#33cabb'></i>"+arr[i]+"</span>";
                        }


                    }
                }else
                {
                    result+= "<span><i class='mdi mdi-account' style='color:#33cabb'></i>"+value+"</span>";
                }
                return result;
            }
        },  {
            field: 'contactType',
            title: '二维码类型',
            align: 'center',
            formatter: function (value,row,indx) {
                if(value==='1')
                {
                    return "<span class=\"badge bg-info\">单人</span>";;
                }else if(value==='2')
                {
                    return "<span class=\"badge bg-warning\">多人</span>";;
                }
            }
        },  {
            field: 'state',
            title: '渠道',
            align: 'center'
        },  {
            field: 'externalUserNum',
            title: '添加客户人数',
            align: 'center',
            visible: false
        },  {
            field: 'createDt',
            title: '创建时间',
            align: 'center'
        },  {
            field: 'contactWayId',
            title: 'contactWayId',
            visible: false
        },  {
            field: 'configId',
            title: 'configId',
            visible: false
        }]
    };
    $("#qrDataTable").bootstrapTable(settings);
    $("#selectQrModal").modal('show');
}

// 选择二维码=》确定事件
function setQrCode() {
    var selected = $("#qrDataTable").bootstrapTable('getSelections');
    if(selected.length === 0) {
        $MB.n_warning("请选择二维码");
        return false;
    }
    $("#qrId").val(selected[0].contactWayId);
    $.get("/coupon/getShortUrl", {url: selected[0].qrCode}, function(r) {
        if(r.code === 200) {
            $("#copyQrCodeBtn").attr("data-clipboard-text", r.data);
            $("#qrCode").val(r.data);
        }else {
            $MB.n_danger(r['msg']);
        }
    });
    $("#selectQrModal").modal('hide');
}

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
                $MB.loadingDesc('show', '正在计算数据中,请稍后...');
                $.post("/addUser/saveData",{taskName:taskName, sendType: sendType, sourceId: sourceId,
                        regionId: regionId, sourceName: sourceName, regionName: regionName},
                    function (r) {
                        if(r.code === 200) {
                            $MB.loadingDesc('hide');
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
                            $("#totalUserCnt").text(r.data['dailyAddTotal']);
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
    var currentDailyUserCnt = $('input[name="dailyUserCnt"]').val();
    var currentDailyApplyRate = $('input[name="dailyApplyRate"]').val();
    var headId = head_id;
    if(opType === 'update') {
        headId = $("#headId").val();
    }
    $.get("/addUser/saveDailyUserData", {headId: headId, dailyUserCnt:currentDailyUserCnt, dailyApplyRate:currentDailyApplyRate}, function (r) {
        if(r.code === 200) {
            $("#saveDailyUserBtn").attr("style", "display:none;");
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
            if(selected_city_code.length == 0 && selected_source_code === null) {
                $MB.n_warning("请至少选择一组条件！");
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
function addRegion(id) {
    var cityName = $("#" + id).find("option:selected").val();
    var cityCode = $("#" + id).find("option:selected").attr("data-code");
    if(cityName !== undefined) {
        if(selected_city_code.indexOf(cityCode) == -1) {
            if(selected_city_code.length <= 4) {
                $("#cityTags").append("<span class=\"tag\"><span>" + cityName + "&nbsp;&nbsp;</span><a style=\"color: #fff;cursor: pointer;\" onclick=\"regionRemove(this, \'"+cityCode+"\', \'"+cityName+"\')\">x</a></span>");
                selected_city_code.push(cityCode);
                selected_city_name.push(cityName);
            }else {
                $MB.n_warning("最多可选5个城市！");
            }
        }
    }
}

// 移除region数据
function regionRemove(dom, cityCode, cityName) {
    $(dom).parent().remove();
    selected_city_code.splice(selected_city_code.indexOf(cityCode), 1);
    selected_city_name.splice(selected_city_name.indexOf(cityCode), 1);
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
        if(opType === 'save') {
            var code = "";
            r.data.forEach((v, k)=>{
                code += "<button type=\"button\" \n" +
                    "class=\"btn btn-round btn-sm btn-secondary m-t-5\" onclick='sourceClick(this, \""+v['id']+"\",  \""+v['name']+"\")'>"+v['name']+"\n" +
                    "</button>&nbsp;";
            });
            $("#sourceDiv").html(code);
        }
        if(opType === 'update') {
            var code = "";
            r.data.forEach((v, k)=>{
                if(v['name'] === sourceName) {
                    code += "<button type=\"button\" \n" +
                        "class=\"btn btn-round btn-sm btn-info m-t-5\">"+v['name']+"\n" +
                        "</button>&nbsp;";
                }else {
                    code += "<button type=\"button\" \n" +
                        "class=\"btn btn-round btn-sm btn-secondary m-t-5\">"+v['name']+"\n" +
                        "</button>&nbsp;";
                }
            });
            $("#sourceDiv").html(code);
        }

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

// 文案内容输入
function smsContentInput(dom) {
    $("#currentWordCnt").text($(dom).val().length);
    $("#article").html($(dom).val() === '' ? "请输入消息内容...":$(dom).val());
}

// 更新文案值
function updateSmsContent() {
    var headId = head_id;
    if(opType === 'update') {
        headId = $("#headId").val();
    }
    $.post("/addUser/updateSmsContentAndContactWay", {headId: headId, smsContent: $("textarea[name='smsContent']").val(),
    contactWayId: $("#qrId").val(), contactWayUrl: $("#qrCode").val()}, function (r) {
        if(r.code == 200) {
            $MB.n_success("更新成功！");
        }
    });
}