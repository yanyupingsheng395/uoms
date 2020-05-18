$( function () {
    getDataList( );
    $('#pushPeriod').datetimepicker({
        language:  'zh-CN',
        weekStart: 1,
        todayBtn:  1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        forceParse: 0,
        showMeridian: 1,
        startDate: new Date()
    });
});

var media_id = "";

function getDataList() {
    let settings = {
        url: "/wxMsgPush/getDataList",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        columns: [
            {
                checkbox: true,
            },
            {
                field: 'id',
                title: 'ID',
                visible: false
            },
            {
                field: 'createDt',
                align: 'center',
                title: '创建时间'
            },
            {
                field: 'isTotalUser',
                align: 'center',
                title: '是否全部用户',
                formatter: function (value, row, index) {
                    if(value === '1') {
                        return '是';
                    }else if(value === '0') {
                        return '否';
                    }
                    return "-";
                }
            },{
                field: 'msgType',
                align: 'center',
                title: '消息类型',
                formatter: function (value, row, index) {
                    var res = "-";
                    if(value === 'news') {
                        res = "图文";
                    }
                    if(value === 'text') {
                        res = "文本";
                    }
                    if(value === 'image') {
                        res = "图片";
                    }
                    if(value === 'video') {
                        res = "视频";
                    }
                    if(value === 'voice') {
                        res = "语音";
                    }
                    return res;
                }
            },{
                field: 'msgContent',
                align: 'center',
                title: '内容'
            },{
                field: 'status',
                align: 'center',
                title: '消息发送状态',
                formatter: function (value, row, index) {
                    var res = "-";
                    if(value === 'todo') {
                        res = "<span class=\"badge bg-info\">待推送</span>";
                    }
                    if(value === 'doing') {
                        res = "<span class=\"badge bg-warning\">推送中</span>";
                    }
                    if(value === 'done') {
                        res = "<span class=\"badge bg-success\">推送成功</span>";
                    }
                    return res;
                }
            }, {
                title: '操作',
                align: 'center',
                formatter: function (value, row, index) {
                    return "<a style='cursor: pointer' onclick='viewData(\""+row['id']+"\")'><i class='fa fa-eye'></i>查看</a>&nbsp;&nbsp;<a style='cursor: pointer' onclick='deleteData(\""+row['id']+"\")'><i class='fa fa-trash'></i>删除</a>";
                }
            }]
    };
    $MB.initTable( 'msgPushTable', settings );
}

function addMsgPush() {
    $("#msgAddModal").modal("show");
}

$("#msgAddModal").on("shown.bs.modal", function () {
    var code = "";
    $.get("/tag/getDataListPage", {}, function (r) {
        r.forEach(v=>{
            code += "<option value='"+v['id']+"'>"+v['name']+"</option>";
        });
        $("#tagId").html('').append(code);
        $("#tagId").selectpicker('refresh');
    });
});

// 保存数据
function saveData() {
    var data = {};
    if(media_type === 'text') {
        data['msgContent'] = $("#replyContent").val();
    }
    if(media_type === 'image') {
        var selected = $("#imageTable").bootstrapTable('getSelections');
        if(selected.length == 0) {
            $MB.n_warning("至少选择一条记录！");
            return;
        }
        data['mediaId'] = selected[0]['mediaId'];
    }
    if(media_type === 'voice') {
        var selected = $("#voiceTable").bootstrapTable('getSelections');
        if(selected.length == 0) {
            $MB.n_warning("至少选择一条记录！");
            return;
        }
        data['mediaId'] = selected[0]['mediaId'];
    }
    if(media_type === 'video') {
        var selected = $("#videoTable").bootstrapTable('getSelections');
        if(selected.length == 0) {
            $MB.n_warning("至少选择一条记录！");
            return;
        }
        data['mediaId'] = selected[0]['mediaId'];
    }
    if(media_type === 'news') {
        var selected = $("#imageTextTable").bootstrapTable('getSelections');
        if(selected.length == 0) {
            $MB.n_warning("至少选择一条记录！");
            return;
        }
        data['mediaId'] = selected[0]['mediaId'];
    }
    data['msgType'] = media_type;
    data['isTotalUser'] = $("#isTotalUser").val();
    var tagId = "";
    if($("#tagId").val() != null && $("#tagId").val().length > 0) {
        tagId = $("#tagId").val().join(",");
    }
    if($("#isTotalUser").val() === '0') {
        data['tagId'] = tagId;
    }
    $.post("/wxMsgPush/saveData", data, function (r) {
        if(r.code === 200) {
            $MB.n_success("保存成功！");
            $("#msgAddModal").modal('hide');
            $MB.refreshTable('msgPushTable');
        }
    });
}

$("#msgAddModal").on('hidden.bs.modal', function () {
    $("#replyContent").val('');
    $("#text-tab").tab('show');
});

function deleteData(id) {
    $MB.confirm({
        title: '提示：',
        content: '确认删除当前记录？'
    }, function () {
        $.post("/wxMsgPush/deleteById", {id: id}, function (r) {
            if(r.code === 200) {
                $MB.n_success("删除成功！");
                $MB.refreshTable('msgPushTable');
            }
        });
    });
}

$("#isTotalUser").change(function () {
    if($(this).val() === '1') {
        $("#tagDiv").attr("style", "display:none;");
    }else {
        $("#tagDiv").attr("style", "display:block;");
    }
});

$('input[name="pushMethod"]').click(function () {
    if ($(this).val() == "FIXED") {
        $("#pushPeriodDiv").show();
    } else {
        $("#pushPeriodDiv").hide();
    }
});

$("#push_ok").change(function () {
    let flag = $(this).is(':checked');
    if(flag) {
        $("#pushMsgBtn").removeAttr("disabled");
    }else {
        $("#pushMsgBtn").attr("disabled", true);
    }
});

// 保存并推送
function savePushMsg() {
    var pushMethod = $("input[name='pushMethod']:checked").val();
    var pushPeriod = $("#pushPeriod").val();
    var selected = $("#msgPushTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("至少选择一条记录！");
        return;
    }
    var headId = selected[0].id;
    $.post("/wxMsgPush/pushMsg", {pushMethod: pushMethod, pushPeriod:pushPeriod, headId:headId}, function (r) {
        if(r.code == 200) {
            $MB.n_success("任务已经提交，请稍后！");
            $('#pushMsgModal').modal('hide');
            $MB.refreshTable("msgPushTable");
        }
    });
}

function prePushPage() {
    var selected = $("#msgPushTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("至少选择一条记录！");
        return;
    }
    var status = selected[0].status;
    if(status !== 'todo') {
        $MB.n_warning("当前状态不支持任务推送！");
        return;
    }
    $('#pushMsgModal').modal('show');
}

function viewData(id) {
    $.get("/wxMsgPush/getMsgHeadById", {headId: id}, function (r) {
        if (r.code === 200) {
            var data = r.data;
            for (key in data) {
                $( "#msgForm" ).find( 'input[name="' + key + '"]' ).val(data[key]);
            }
        }
    });
    $('#viewPushModal').modal('show');
}