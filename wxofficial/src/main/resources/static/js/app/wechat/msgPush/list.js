$( function () {
    getDataList( );
} );

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
                title: '消息类型'
            },{
                field: 'msgContent',
                align: 'center',
                title: '内容'
            },{
                field: 'status',
                align: 'center',
                title: '消息发送状态'
            }, {
                title: '操作',
                align: 'center',
                formatter: function (value, row, index) {
                    return "<a style='cursor: pointer' onclick='editData(\""+row['id']+"\", \""+row['name']+"\")'><i class='fa fa-eye'></i>查看</a>&nbsp;&nbsp;<a style='cursor: pointer' onclick='deleteData(\""+row['id']+"\")'><i class='fa fa-trash'></i>删除</a>";
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

$("#pushMsgModal").on('shown.bs.modal', function () {
    $.get("/wxMsgPush/getPushInfo", {}, function (r) {
        let code = "";
        r.data.forEach( (v, k) => {
            code += "<option value='" + v + "'>" + v + "</option>";
        } );
        $( "#pushPeriod" ).html( '' ).append( code );
    });
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
    
}