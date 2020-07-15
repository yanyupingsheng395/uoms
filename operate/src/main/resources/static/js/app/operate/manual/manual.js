initTable();
function initTable() {
    let settings = {
        url: '/manual/getHeaderListPage',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {scheduleDate: $("#scheduleDate").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'headId',
            title: 'ID',
            visible: false
        }, {
            field: 'fileName',
            title: '文件名称'
        }, {
            field: 'allNum',
            title: '总手机号数量'
        }, {
            field: 'validNum',
            title: '有效手机号数量'
        }, {
            field: 'unvalidNum',
            title: '无效手机号数量'
        }, {
            field: 'successNum',
            title: '推送成功数量'
        }, {
            field: 'faildNum',
            title: '推送失败数量'
        }, {
            field: 'interceptNum',
            title: '拦截手机号数量'
        }, {
            field: 'smsContent',
            title: '短信内容',
            formatter: function (value, row, index) {
                if(value.length > 20) {
                    return "<a style='color: #48b0f7;' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-placement=\"bottom\" data-original-title=\""+value+"\" data-trigger=\"hover\">\n" +
                        value.substring(0, 20) + "..." +
                        "</a>";
                }
                return value;
            }
        }, {
            field: 'pushType',
            title: '推送类型',
            formatter: function (value, row, indx) {
                if(value === '0') {
                    return "定时发送"
                }else if(value === '1'){
                    return "立刻发送";
                }else {
                    return "-";
                }
            }
        }, {
            field: 'scheduleDate',
            title: '计划推送时间'
        }, {
            field: 'actualPushDate',
            title: '实际推送时间'
        }, {
            field: 'status',
            title: '状态',
            formatter: function (value, row, indx) {
                var res;
                switch (value) {
                    case "0":
                        res = "<span class=\"badge bg-info\">已上传，待推送</span>";
                        break;
                    case "1":
                        res = "<span class=\"badge bg-success\">已提交，计划中</span>";
                        break;
                    case "2":
                        res = "<span class=\"badge bg-primary\">推送完成</span>";
                        break;
                    default:
                        res = "-";
                        break;
                }
                return res;
            }
        }, {
            field: 'insertDt',
            title: '创建时间'
        }],onLoadSuccess: function(data){
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $MB.initTable('dataTable', settings);
}

function beforeUpload() {
    let fileName = document.getElementById('file').files[0].name;
    if(!fileName.endsWith(".txt")) {
        $MB.n_warning("仅支持txt格式的文件！");
        return;
    }
    $('#upload_file_name' ).text(document.getElementById('file').files[0].name).attr('style', 'display:inline-block;');
}

// 提交数据
function submitData() {
    if(beforeSubmit()) {
        $MB.loadingDesc("show", "正在处理数据中，请稍候...");
        let formData = new FormData();
        let smsContent = $("textarea[name='smsContent']").val();
        let sendType = $("input[name='sendType']:checked").val();
        let pushDate = $("input[name='pushDate']").val();

        formData.append("file", document.getElementById('file').files[0]);
        formData.append("smsContent", smsContent);
        formData.append("sendType", sendType);
        formData.append("pushDate", pushDate);
        $.ajax({
            url: "/manual/saveManualData",
            type: "POST",
            data:formData,
            contentType: false,
            processData: false,
            success: function(data) {
                if(data.code === 200){
                    $MB.n_success("数据提交成功！");
                }else {
                    $MB.n_success(data.msg);
                }
                $MB.loadingDesc('hide');
                $("#add_modal").modal('hide');
                $MB.refreshTable('dataTable');
            },
            error: function (data) {
                $MB.n_danger(data.msg);
            }
        });
    }
}

// 表单验证
function beforeSubmit() {
    let file = document.getElementById('file').files;
    if(document.getElementById('file').files.length === 0) {
        $MB.n_warning("请上传文件！");
        return false;
    }
    if(file[0].size > 10485760) {
        $MB.n_warning("上传文件不能超过10M的文件！");
        return false;
    }
    let smsContent = $("textarea[name='smsContent']").val();
    if(smsContent === "") {
        $MB.n_warning("短信内容不能为空！");
        return false;
    }

    let sendType = $("input[name='sendType']:checked").val();
    if(sendType === undefined) {
        $MB.n_warning("请选择短信推送方式！");
        return false;
    }
    if(sendType === "0" && $("input[name='pushDate']").val() === '') {
        $MB.n_warning("请选择推送时间！");
        return false;
    }
    return true;
}

// 推送短信
function pushMessage() {
    let selected = $("#dataTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要推送的记录！');
        return;
    }
    let headId = selected[0].headId;
    let status = selected[0].status;
    let validNum = selected[0].validNum;
    if(status !== '0') {
        $MB.n_warning("该记录的当前状态不支持推送操作！");
        return;
    }
    if(validNum === '0') {
        $MB.n_warning("该记录的合法数据为0条，不支持推送操作！");
        return;
    }
    getPushInfo(headId);
}

function getPushInfo(headId) {
    $.get("/manual/getPushInfo", {headId: headId}, function (r) {
        if(r.code === 200) {
            let pushType = r.data['PUSH_TYPE'];
            let scheduleDate = r.data['SCHEDULE_DATE'];
            let content = "";
            if(pushType === '0') {
                content = "短信推送方式为定时发送，推送时间为" + scheduleDate + "。确定推送？";
            }else {
                content = "短信推送方式为立刻发送。确定推送？";
            }
            $MB.confirm({
                title: "提示：",
                content: content
            }, function () {
                $MB.loadingDesc("show", "正在处理数据中，请稍候...");
                $.post("/manual/pushMessage", {headId: headId, pushType: pushType}, function (res) {
                    if(res.code === 200) {
                        $MB.n_success("已经触发推送，请稍候...");
                    }else {
                        $MB.n_danger(res.msg);
                    }
                    $MB.loadingDesc("hide");
                    $MB.refreshTable('dataTable');
                });
            });
        }
    });
}

$("#btn_query").click(function () {
    $MB.refreshTable('dataTable');
});

$("#btn_reset").click(function () {
    $("#scheduleDate").val('');
    $MB.refreshTable('dataTable');
});

$("#btn_delete").click(function () {
    let selected = $("#dataTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要删除的记录！');
        return;
    }
    let headId = selected[0].headId;
    let status = selected[0].status;
    if(status !== '0') {
        $MB.n_warning("该记录的当前状态不支持删除操作！");
        return;
    }
    $MB.confirm({
        title: "提示：",
        content: "确定删除当前选中的记录？"
    }, function () {
        $.get("/manual/deleteData", {headId: headId}, function (r) {
            if(r.code === 200) {
                $MB.n_success("删除成功！");
            }else {
                $MB.n_danger("删除失败！");
            }
            $MB.refreshTable('dataTable');
        });
    });
});

$("#add_modal").on('hidden.bs.modal', function () {
    $("#smsContent").val('');
    $("#upload_file_name").text('');
    $("#file").val('');
    $("input[name='sendType']:checked").attr("checked", false);
    $("#word").text('0');
});
statInputNum($("#smsContent"),$("#word"));
function statInputNum(textArea,numItem) {
    let curLength = textArea.val().length;
    numItem.text(curLength);
    textArea.on('input propertychange', function () {
        var _value = $(this).val().replace(/\n/gi,"");
        numItem.text(_value.length);
    });
}

function testSend() {
    let selected = $("#dataTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要测试的记录！');
        return;
    }
    let content = selected[0].smsContent;
    $('#smsContent1').val(content);
    $('#smsContent1').attr("readOnly", "readOnly");
    statInputNum($("#smsContent1"),$("#word1"));
    $('#send_modal').modal('show');
}

$("#send_modal").on('hidden.bs.modal', function () {
    $('#phoneNum').val("");
});

function sendMessage()
{
    //验证
    var phoneNum=$('#phoneNum').val();
    var smsContent = $('#smsContent1').val();

    if(null==phoneNum||phoneNum=='') {
        $MB.n_warning("手机号不能为空！");
        return;
    }
    if(null==smsContent||smsContent=='') {
        $MB.n_warning("消息内容不能为空！");
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