var $qywxManualForm = $( "#qywx-manual-form" );
var $qywxManualForm_validator;
$(function () {
    initTable();
});

function initTable() {
    let settings = {
        url: '/qywxmanual/getHeaderListPage',
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
        }, {
            field: 'headId',
            title: 'ID',
            visible: false
        },  {
            field: 'userNumber',
            title: '用户数（人）'
        }, {
            field: 'totalNum',
            title: '需要推送客户数（人）'
        },{
            field: 'status',
            title: '状态',
            formatter: function (value, row, indx) {
                var res;
                switch (value) {
                    case "0":
                        res = "<span class=\"badge bg-info\">已上传，待推送</span>";
                        break;
                    case "1":
                        res = "<span class=\"badge bg-primary\">推送完成</span>";
                        break;
                    case "3":
                        res = "<span class=\"badge bg-warning\">推送失败</span>";
                        break;
                    default:
                        res = "-";
                        break;
                }
                return res;
            }
        }, {
            field: 'successNum',
            title: '成功推送客户数'
        }, {
            field: 'convertNum',
            title: '实际执行推送用户数'
        }, {
            field: 'textContent',
            title: '推送内容',
            formatter: function (value, row, index) {
                if(null!=value&&value.length > 20) {
                    return "<a style='color: #48b0f7;' data-toggle='tooltip' data-html='true' title='' data-placement='bottom' data-original-title='"+value+"' data-trigger='hover'>\n" +
                        value.substring(0, 20) + "..." +
                        "</a>&nbsp;<a style='text-decoration: underline;cursor: pointer;font-size: 12px;' data-clipboard-text='"+value+"' class='copy_btn'>复制</a>";
                }else if(null!=value&&value!='')
                {
                    return value+"&nbsp;<a style='text-decoration: underline;cursor: pointer;font-size: 12px;' data-clipboard-text='"+value+"' class='copy_btn'>复制</a>";
                }
                return value;
            }
        }, {
            field: 'pushDate',
            title: '推送时间'
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
    var fileName = document.getElementById('file').files[0].name;
    if(!fileName.endsWith(".csv")) {
        $MB.n_warning("仅支持csv格式的文件！");
        return;
    }
    $('#upload_file_name' ).text(document.getElementById('file').files[0].name).attr('style', 'display:inline-block;');
}

// 提交数据
function submitData() {
    validQywxContact();
    let file = document.getElementById('file').files;
    if(document.getElementById('file').files.length === 0) {
        $MB.n_warning("请上传数据！");
        return false;
    }
    if(file[0].size > 10485760) {
        $MB.n_warning("上传数据不能超过10M！");
        return false;
    }
    var validator = $qywxManualForm.validate();
    if(validator.form()) {
        $MB.loadingDesc("show", "正在处理数据中，请稍候...");
        let formData = new FormData();
        let smsContent = $("textarea[name='smsContent']").val();
        let mpTitle = $("#mpTitle").val();
        let mpUrl = $("#mpUrl").val();
        let mediaId = $("#mediaId").val();
        let picUrl = $("#picUrl").val();
        let linkTitle = $("#linkTitle").val();
        let linkDesc = $("#linkDesc").val();
        let linkUrl = $("#linkUrl").val();
        let linkPicurl = $("#linkPicurl").val();
        let msgType = $('input[name="msgType"]:checked').val();

        formData.append("file", document.getElementById('file').files[0]);
        formData.append("smsContent", smsContent);
        formData.append("mpTitle", mpTitle);
        formData.append("mpUrl", mpUrl);
        formData.append("mediaId", mediaId);
        formData.append("picUrl", picUrl);
        formData.append("linkTitle", linkTitle);
        formData.append("linkDesc", linkDesc);
        formData.append("linkUrl", linkUrl);
        formData.append("linkPicurl", linkPicurl);
        formData.append("msgType", msgType);
        $.ajax({
            url: "/qywxmanual/saveManualData",
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
                if (data.code === 200) {
                    if (data.data.errorFlag == "N") {
                        $MB.n_danger(data.data.errorDesc);
                    } else {
                        $MB.n_success("数据提交成功！");
                    }
                    $MB.loadingDesc('hide');
                    $("#add_modal").modal('hide');
                    $MB.refreshTable('dataTable');
                } else {
                    $MB.loadingDesc('hide');
                    $MB.n_danger(data.msg);
                }
            },
            error: function (data) {
                $MB.n_danger(data.msg);
            }
        });
    }
}

// 上传失败的提示列表数据
function makeErrorTable(data) {
    $("#errorDataTable").bootstrapTable({
        showHeader:true,
        columns: [{
            title: '问题描述',
            align: 'left',
            formatter: function (value, row, index) {
                return "文件中成员信息未查到或成员信息和外部客户信息不匹配！"
            }
        },{
            title: '出现位置',
            align: 'center',
            formatter: function (value, row, index) {
                return "第"+row+"行"
            }
        }]
    });
    $("#errorDataTable").bootstrapTable('load', data);
    $("#upload_error_modal").modal('show');
}

function validQywxContact() {
    var icon = "<i class='fa fa-close'></i> ";
    let msgType = $('input[name="msgType"]:checked').val();
    if(msgType=="applets"){
        $qywxManualForm_validator = $qywxManualForm.validate( {
            rules: {
                smsContent: {
                    required: true
                },
                mpTitle: {
                    required: true
                },
                mpUrl: {
                    required: true
                },
                mediaId: {
                    required: true
                }
            },
            errorPlacement: function (error, element) {
                if (element.is( ":checkbox" ) || element.is( ":radio" )) {
                    error.appendTo( element.parent().parent() );
                } else {
                    error.insertAfter( element );
                }
            },
            messages: {
                smsContent: {
                    required: icon + "请输入内容！"
                },
                mpTitle: {
                    required: icon + "请输入小程序标题"
                },
                mpUrl: {
                    required: icon + "请输入小程序连接"
                },
                mediaId: {
                    required: icon + "请输入小程序封面ID"
                }
            }
        } );
    }else if(msgType=="image"){
        $qywxManualForm_validator = $qywxManualForm.validate( {
            rules: {
                picUrl: {
                    required: true
                }
            },
            errorPlacement: function (error, element) {
                if (element.is( ":checkbox" ) || element.is( ":radio" )) {
                    error.appendTo( element.parent().parent() );
                } else {
                    error.insertAfter( element );
                }
            },
            messages: {
                picUrl: {
                    required: icon + "请选择图片地址！"
                }
            }
        } );
    }else if(msgType=="web"){
        $qywxManualForm_validator = $qywxManualForm.validate( {
            rules: {
                linkTitle: {
                    required: true
                },
                linkUrl: {
                    required: true
                }
            },
            errorPlacement: function (error, element) {
                if (element.is( ":checkbox" ) || element.is( ":radio" )) {
                    error.appendTo( element.parent().parent() );
                } else {
                    error.insertAfter( element );
                }
            },
            messages: {
                linkTitle: {
                    required: icon + "请填写网页标题！"
                }, linkUrl: {
                    required: icon + "请填写网页地址！"
                }
            }
        } );
    }

}

// 推送信息
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
    $MB.loadingDesc("show", "正在处理数据中，请稍候...");
    $.post("/qywxmanual/pushMessage", {headId: headId}, function (res) {
        if(res.code === 200) {
            $MB.n_success("推送成功！");
        }else {
            $MB.n_danger(res.msg);
        }
        $MB.loadingDesc("hide");
        $MB.refreshTable('dataTable');
    });
}

$("#btn_query").click(function () {
    $MB.refreshTable('dataTable');
});

$("#btn_reset").click(function () {
    $("#pushDate").val('');
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
        $.get("/qywxmanual/deleteData", {headId: headId}, function (r) {
            if(r.code === 200) {
                $MB.n_success("删除成功！");
            }else {
                $MB.n_danger(r.msg);
            }
            $MB.refreshTable('dataTable');
        });
    });
});

$("#add_modal").on('hidden.bs.modal', function () {
    $("#smsContent").val('');
    $("#upload_file_name").text('');
    $("#file").val('');
    $("#mpTitle").val('');
    $("#mpUrl").val('');
    $("#mediaId").val('');
    $("#picUrl").val('');
    $("#linkTitle").val('');
    $("#linkDesc").val('');
    $("#linkUrl").val('');
    $("#linkPicurl").val('');
    $(":radio[name='sendType'][value='applets']").prop("checked", "checked");
    selectType("applets");
});

$("#send_modal").on('hidden.bs.modal', function () {
    $('#phoneNum').val("");
});

$("#btn_effect").click(function () {
    let selected = $("#dataTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要查看效果的记录！');
        return;
    }
    let status=selected[0].status;
    let headId = selected[0].headId;
    if (status!='1') {
        $MB.n_warning('尚未推送完成，不支持查看效果！');
        return;
    }
    window.location.href="/page/qywxManual/effect?headId="+headId;
});

function selectType(type) {
    if(type=="image"){
        $("#image").show();
        $("#webPage").hide();
        $("#applets").hide();
        $("#linkTitle").val("");
        $("#linkDesc").val("");
        $("#linkUrl").val("");
        $("#linkPicurl").val("");
        $("#mpTitle").val("");
        $("#mpUrl").val("");
        $("#mediaId").val("");
    }else if(type=="webPage"){
        $("#image").hide();
        $("#webPage").show();
        $("#applets").hide();
        $("#picUrl").val("");
        $("#mpTitle").val("");
        $("#mpUrl").val("");
        $("#mediaId").val("");
    }else if(type=="applets"){
        $("#image").hide();
        $("#webPage").hide();
        $("#applets").show();
        $("#linkTitle").val("");
        $("#linkDesc").val("");
        $("#linkUrl").val("");
        $("#linkPicurl").val("");
        $("#mpTitle").val("");
        $("#mpUrl").val("");
        $("#mediaId").val("");
    }
}
