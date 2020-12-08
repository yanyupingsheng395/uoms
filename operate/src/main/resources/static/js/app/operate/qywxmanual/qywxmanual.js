var $qywxManualForm = $( "#qywx-manual-form" );
var $qywxManualForm_validator;
$(function () {
    validQywxContact();
    initTable();
    statInputNum();
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
            title: '需要推送信息数（人）'
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
                    default:
                        res = "-";
                        break;
                }
                return res;
            }
        }, {
            field: 'successNum',
            title: '推送成功数量'
        }, {
            field: 'convertNum',
            title: '实际执行推送用户数'
        }, {
            field: 'mpUrl',
            title: '小程序链接'
        }, {
            field: 'mpTitle',
            title: '小程序标题'
        }, {
            field: 'mpMediald',
            title: '小程序封面ID'
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
    let file = document.getElementById('file').files;
    if(document.getElementById('file').files.length === 0) {
        $MB.n_warning("请上传文件！");
        return false;
    }
    if(file[0].size > 10485760) {
        $MB.n_warning("上传文件不能超过10M的文件！");
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

        formData.append("file", document.getElementById('file').files[0]);
        formData.append("smsContent",smsContent);
        formData.append("mpTitle",mpTitle);
        formData.append("mpUrl",mpUrl);
        formData.append("mediaId",mediaId);
        $.ajax({
            url: "/qywxmanual/saveManualData",
            type: "POST",
            data:formData,
            contentType: false,
            processData: false,
            success: function(data) {
                if(data.code === 200){
                    if(data.data.errorFlag=="N"){
                        $MB.n_danger(data.data.errorDesc);
                    }else{
                        $MB.n_success("数据提交成功！");
                    }
                }else {
                    $MB.n_danger(data.msg);
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

function validQywxContact() {
    var icon = "<i class='fa fa-close'></i> ";
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
            $MB.n_success("已经触发推送，请稍候...");
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
    $("#mpTitle").val('');
    $("#mpUrl").val('');
    $("#mediaId").val('');
});

function statInputNum() {
    $("#smsContent").on('input propertychange', function () {
        let curLength = $('#smsContent').val().length;
        let total_num = curLength+signatureLen+unsubscribeLen;
        let snum3=0;
        $("#snum1").text(curLength);
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

$("#send_modal").on('hidden.bs.modal', function () {
    $('#phoneNum').val("");
});
