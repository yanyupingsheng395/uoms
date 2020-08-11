let custom_step;
let daily_user_cnt;
let daily_apply_rate;
let head_id;
let upgradeFlag='N';
$(function () {
    // 初始化步骤条
    custom_step= steps({
        el: "#addStep",
        data: [
            {title: "选择目标用户", description: ""},
            {title: "编辑申请文案", description: ""}
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
    getSource();
    statInputNum();
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
        if(validStep1Data('Y')) {
            custom_step.setActive(1);
            initGetInputNum();
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
            checkbox: true,
            formatter: function (value, row, index) {
                if(row.contactWayId === Number.parseInt($("#qrId").val())) {
                    return {checked: true};
                }
                return {};
            }
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
    $("#copyQrCodeBtn").attr("data-clipboard-text", selected[0].shortUrl);
    $("#qrCode").val(selected[0].shortUrl);
    $("#selectQrModal").modal('hide');
}

/**
 * 选择完条件后保存主记录的保存
 */
function saveData() {
    // 验证表单数据
    if(validStep1Data('N')) {
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
            content: '确认保存？'
        }, function () {
            //打开遮罩层
            $MB.loadingDesc('show', '保存中，请稍候...');
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
                    $MB.loadingDesc('hide');
                });
        });
    }
}

function userDataChange(dom, idx) {
    if(($("input[name='dailyUserCnt']").val() !== daily_user_cnt) || ($("input[name='dailyApplyRate']").val() !== daily_apply_rate)) {
        $("#saveDailyUserBtn").attr("style", "display:inline-block;");
        upgradeFlag='Y';
    }else {
        $("#saveDailyUserBtn").attr("style", "display:none;");
        upgradeFlag='N';
    }
}

/**
 * 调整参数保存
 */
function saveDailyUserData() {
    var currentDailyUserCnt = $('input[name="dailyUserCnt"]').val();
    var currentDailyApplyRate = $('input[name="dailyApplyRate"]').val();
    var headId = head_id;
    if(opType === 'update') {
        headId = $("#headId").val();
    }

    //判断headId是否为空
    if(null==headId||headId == undefined || headId == '')
    {
        $MB.n_warning("请先完成任务的保存！");
        return false;
    }

    $.get("/addUser/saveDailyUserData", {headId: headId, dailyUserCnt:currentDailyUserCnt, dailyApplyRate:currentDailyApplyRate}, function (r) {
        if(r.code === 200) {
            $("#saveDailyUserBtn").attr("style", "display:none;");
            $("input[name='dailyApplyRate']").val(r.data['dailyApplyRate']);
            $("input[name='dailyAddUserCnt']").val(r.data['dailyAddUserCnt']);
            $("input[name='dailyWaitDays']").val(r.data['dailyWaitDays']);
            $MB.n_success("更新成功！");
            //隐藏更新按钮
            $("#saveDailyUserBtn").attr("style", "display:none;");
            upgradeFlag='N';
        }else
        {
            $MB.n_danger(r.msg);
        }
    });
}

/**
 * 完成对数据的校验(步骤1)
 * @param stepIndex
 * @returns {boolean}
 */
function validStep1Data(flag) {
    if(opType === 'save')
    {
        let sendType = $("input[name='sendType']:checked").val();
        if(sendType === '1') {
            if(selected_city_code.length == 0 && selected_source_code === null) {
                $MB.n_warning("请至少选择一组条件！");
                return false;
            }
        }
        let taskName = $("input[name='taskName']").val();
        if(taskName === '') {
            $MB.n_warning("请输入任务名称！");
            return false;
        }
    }

    var headId = head_id;
    if(opType === 'update') {
        headId = $("#headId").val();
    }

    //额外校验是否完成了主记录的保存
    if(flag=='Y')
    {
       if(null==headId||headId == undefined || headId == '')
       {
           $MB.n_warning("请先完成任务的保存！");
           return false;
       }
    }

    //验证是否需要先更新数据
    if(upgradeFlag=='Y')
    {
        $MB.n_warning("请先完成任务的更新！");
        return false;
    }

    return true;
}

/**
 * 完成对数据的校验(步骤2)
 * @param stepIndex
 * @returns {boolean}
 */
function validStep2Data() {
    var qrId = $("#qrId").val();

    if(qrId === '') {
        $MB.n_warning("请配置渠道活码二维码短链！");
        return false;
    }
    var smsContent = $("#smsContent").val();
    if(smsContent === undefined || smsContent === '') {
        $MB.n_warning("请输入短信内容！");
        return false;
    }
    return true;
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

// 获取渠道数据
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
    if($(dom).hasClass('btn-info')) {
        $(dom).removeClass('btn-info').addClass('btn-secondary');
    }else {
        selected_source_code = id;
        selected_source_name = name;
        $(dom).addClass('btn-info').removeClass('btn-secondary');
        $(dom).siblings().removeClass('btn-info').addClass('btn-secondary');
    }
}

// 文案内容输入
function smsContentInput() {
    let content = $('#smsContent').val() === "" ? "请输入消息内容": signature+$('#smsContent').val()+unsubscribe;
    $("#article").html('').append(content);
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
        let total_num = y+signatureLen+unsubscribeLen;
        let snum3=0;
        $("#snum1").text(y);
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
 * 编辑 - 设置初始的字数统计信息
 */
function initGetInputNum() {
    let smsContent = $('#smsContent').val();
    let y = smsContent.length;
    let total_num = y+signatureLen+unsubscribeLen;
    let snum3=0;
    $("#snum1").text(y);
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

    let content = $('#smsContent').val() === "" ? "请输入消息内容": signature+$('#smsContent').val()+unsubscribe;
    $("#article").html('').append(content);
}

// 更新文案值
function updateSmsContent() {
    var headId = head_id;
    if(opType === 'update') {
        headId = $("#headId").val();
    }

    if(validStep2Data())
    {
        if(null==headId||headId == undefined || headId == '')
        {
            $MB.n_warning("请先完成任务的保存！");
            return false;
        }

        $.post("/addUser/updateSmsContentAndContactWay", {headId: headId, smsContent: $("textarea[name='smsContent']").val(),
            contactWayId: $("#qrId").val(), contactWayUrl: $("#qrCode").val()}, function (r) {
            if(r.code == 200) {
                $MB.n_success("任务消息编辑完成！");
            }
        });
    }
}