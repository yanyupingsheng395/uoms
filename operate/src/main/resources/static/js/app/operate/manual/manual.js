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
                param: {touchDt: $("#touchDt").val()}
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
            title: '总记录数'
        }, {
            field: 'validNum',
            title: '合法记录数'
        }, {
            field: 'unvalidNum',
            title: '非法记录数'
        }, {
            field: 'successNum',
            title: '推送成功记录数'
        }, {
            field: 'faildNum',
            title: '推送失败记录数'
        }, {
            field: 'smsContent',
            title: '短信内容'
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
        }]
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
                    $MB.n_success("数据提交失败！");
                }
                $("#add_modal").modal('hide');
                $MB.refreshTable('dataTable');
            }
        });
    }
}

// 表单验证
function beforeSubmit() {
    if(document.getElementById('file').files.length === 0) {
        $MB.n_warning("请上传文件！");
        return false;
    }
    let smsContent = $("textarea[name='smsContent']").val();
    if(smsContent === "") {
        $MB.n_warning("短信内容不能为空！");
        return false;
    }
    let valid = true;
    // 验证短信长度
    $.ajax({
        url: '/smsTemplate/validSmsContentLength',
        data: {smsContent: smsContent},
        async: false,
        success: function (r) {
            valid = r.data;
        }
    });
    if(!valid) {
        $MB.n_warning("短信长度超出系统限制！");
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
    if(status !== '0') {
        $MB.n_warning("该记录的当前状态不支持推送操作！");
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
                $.post("/manual/pushMessage", {headId: headId, pushType: pushType}, function (res) {
                    if(res.code === 200) {
                        $MB.n_success("已经触发推送，请稍后...");
                    }else {
                        $MB.n_danger(res.msg);
                    }
                });
            });
        }
    });
}