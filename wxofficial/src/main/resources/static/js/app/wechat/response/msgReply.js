$(function() {
    getDataList();
});

function getDataList() {
    let settings = {
        url: "/wxMsgReply/getDataList",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  //页面大小
                pageNum: (params.offset / params.limit) + 1
            }
        },
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
                field: 'msgType',
                title: '回复消息类型',
                align: 'center'
            }, {
                field: 'tagNames',
                title: '人群标签',
                align: 'center',
                formatter: function (value, row, index) {
                    var res = "";
                    if(value !== null && value !== "") {
                        var arr = value.split(",");
                        for (let i = 0; i < arr.length; i++) {
                            res += "<span class=\"badge bg-success\">"+arr[i]+"</span>&nbsp;";
                        }
                    }else {
                        res = "-";
                    }
                    return res;
                }
            },{
                title: '操作',
                align: 'center',
                formatter: function (value, row, index) {
                    return "<a style='cursor: pointer' onclick='editData(\""+row['id']+"\")'><i class='fa fa-edit'></i>编辑</a>&nbsp;&nbsp;<a style='cursor: pointer' onclick='deleteData(\""+row['id']+"\")'><i class='fa fa-trash'></i>删除</a>";
                }
            }]
    };
    $MB.initTable( 'dataTable', settings );
}

// 保存素材信息
function saveData() {
    var data = {};
    if(media_type === 'text') {
        data['content'] = $("#replyContent").val();
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
    data['reqType'] = $("#reqType").val();
    var tagId = "";
    if($("#tagId").val().length > 0) {
        tagId = $("#tagId").val().join(",");
    }
    data['tagId'] = tagId;
    var name = $("#saveBtn").attr("name");
    if(name == "save") {
        $.post("/wxMsgReply/saveData", data, function (r) {
            if(r.code === 200) {
                $MB.n_success("保存成功！");
                $("#replyModal").modal('hide');
                $MB.refreshTable('dataTable');
            }
        });
    }else {
        data['id'] = $("#id").val();
        $.post("/wxMsgReply/updateData", data, function (r) {
            if(r.code === 200) {
                $MB.n_success("修改成功！");
                $("#replyModal").modal('hide');
                $MB.refreshTable('dataTable');
            }
        });
    }
}

$("#replyModal").on("hidden.bs.modal", function () {
    $("#saveBtn").attr("name", "save");
    $("#replyContent").val('');
    $("#text-tab").tab('show');
    media_id = "";
    $("#tagId").val("").trigger("change");
});

var media_id = "";
var tag_id = [];
function editData(id) {
    $("#saveBtn").attr("name", "update");
    $.get("/wxMsgReply/getDataById", {id: id}, function (r) {
        if(r.code === 200) {
            var data = r.data;
            var msgType = data.msgType;
            var mediaId = data.mediaId;
            $("#id").val(data['id']);
            media_id = mediaId;
            media_type = msgType;
            if(media_type === 'text') {
                $("#text-tab").tab('show');
                $("#replyContent").val(data['contentStr']);
            }
            if(media_type === 'image') {
                $("#image-tab").tab('show');
            }
            if(media_type === 'voice') {
                $("#audio-tab").tab('show');
            }
            if(media_type === 'video') {
                $("#video-tab").tab('show');
            }
            if(media_type === 'news') {
                $("#imageText-tab").tab('show');
            }

            $("#replyModal").modal('show');
            var tagId = data['tagId'].split(",");
            tag_id = tagId;
        }
    });
}

function deleteData(id) {
    $MB.confirm({
        title: '提示：',
        content: '确认删除该记录？'
    }, function() {
        $.post("/wxMsgReply/deleteById", {id: id}, function (r) {
            if(r.code === 200) {
                $MB.n_success("删除成功！");
            }
            $MB.refreshTable('dataTable');
        });
    });
}

$("#replyModal").on("shown.bs.modal", function () {
    var code = "";
    $.get("/tag/getDataListPage", {}, function (r) {
        r.forEach(v=>{
            code += "<option value='"+v['id']+"'>"+v['name']+"</option>";
        });
        $("#tagId").html('').append(code);
        $("#tagId").selectpicker('refresh');
        if(tag_id.length > 0) {
            $("#tagId").selectpicker('val', tag_id);
        }
    });
});