$(function() {
    getDataList();
});

function getDataList() {
    let settings = {
        url: "/wxUserFollow/getDataList",
        cache: false,
        pagination: false,
        singleSelect: true,
        columns: [
            {
                field: 'id',
                title: 'ID',
                visible: false
            },
            {
                field: 'msgType',
                title: '回复消息类型'
            },{
                title: '操作',
                formatter: function (value, row, index) {
                    return "<a style='cursor: pointer' onclick='editData(\""+row['id']+"\")'><i class='fa fa-edit'></i>编辑</a>&nbsp;&nbsp;<a style='cursor: pointer' onclick='deleteData(\""+row['id']+"\")'><i class='fa fa-trash'></i>删除</a>";
                }
            }]
    };
    $MB.initTable( 'dataTable', settings );
}

function getMaterialDataList(type, tableId) {
    let settings = {
        url: "/material/getDataList",
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
                pageNum: (params.offset / params.limit) + 1,
                param: {type: type}
            }
        },
        columns: [
            {
                checkbox: true,
                formatter: function (value, row, index) {
                    if(row['mediaId'] === media_id) {
                        return {checked: true};
                    }
                    return [];
                }
            },
            {
                field: 'mediaId',
                title: 'ID',
                visible: false
            },
            {
                field: 'name',
                title: '名称',
                formatter: function (value, row, index) {
                    if(media_type === 'image') {
                        return '<a href="' + row['url'] + '" target="_blank" style="text-decoration: underline">' + value + '</a>';
                    }else {
                        return value;
                    }
                }
            },
            {
                field: 'updateTime',
                title: '创建时间',
                align: 'center',
                formatter: function (value, row, index) {
                    return resolvingDate( value );
                }
            }]
    };
    $("#" + tableId).bootstrapTable('destroy').bootstrapTable(settings);
}

// 图文列表
function getImageTextTableData() {
    let settings = {
        url: "/imageText/getDataList",
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
                formatter: function (value, row, index) {
                    if(row['mediaId'] === media_id) {
                        return {checked: true};
                    }
                    return [];
                }
            },
            {
                field: 'title',
                title: '图文标题',
                formatter: function (value, row, index) {
                    return "<a onclick='window.open(\""+row['url']+"\", \"_target\")' style='text-decoration: underline;cursor: pointer;'>" + value + "</a>";
                }
            },
            {
                field: 'userGroup',
                title: '适用人群'
            }]
    };
    $("#imageTextTable").bootstrapTable('destroy').bootstrapTable(settings);
}

// 日期格式化
function resolvingDate(date) {
    let d = new Date( date );
    let month = (d.getMonth() + 1) < 10 ? '0' + (d.getMonth() + 1) : (d.getMonth() + 1);
    let day = d.getDate() < 10 ? '0' + d.getDate() : d.getDate();
    let hours = d.getHours() < 10 ? '0' + d.getHours() : d.getHours();
    let min = d.getMinutes() < 10 ? '0' + d.getMinutes() : d.getMinutes();
    let sec = d.getSeconds() < 10 ? '0' + d.getSeconds() : d.getSeconds();
    let times = d.getFullYear() + '-' + month + '-' + day + ' ' + hours + ':' + min;
    return times
}

var media_type = 'text';
$( "#image-tab" ).on( "shown.bs.tab", function () {
    media_type = 'image';
    getMaterialDataList( 'image', 'imageTable' );
} );

$( "#audio-tab" ).on( "shown.bs.tab", function () {
    media_type = 'voice';
    getMaterialDataList( 'voice', 'voiceTable' );
} );

$( "#video-tab" ).on( "shown.bs.tab", function () {
    media_type = 'video';
    getMaterialDataList( 'video', 'videoTable' );
} );

// 图文消息tab切换
$( "#imageText-tab" ).on( "shown.bs.tab", function () {
    media_type = 'news';
    getImageTextTableData();
} );

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

    var name = $("#saveBtn").attr("name");
    if(name == "save") {
        $.post("/wxUserFollow/saveData", data, function (r) {
            if(r.code === 200) {
                $MB.n_success("保存成功！");
                $("#replyModal").modal('hide');
                $MB.refreshTable('dataTable');
            }
        });
    }else {
        data['id'] = $("#id").val();
        $.post("/wxUserFollow/updateData", data, function (r) {
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
});

var media_id = "";
function editData(id) {
    $("#saveBtn").attr("name", "update");
    $("#replyModal").modal('show');
    $.get("/wxUserFollow/getDataById", {id: id}, function (r) {
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
        }
    });
}

function deleteData(id) {
    $MB.confirm({
        title: '提示：',
        content: '确认删除该记录？'
    }, function() {
        $.post("/wxUserFollow/deleteById", {id: id}, function (r) {
            if(r.code === 200) {
                $MB.n_success("删除成功！");
            }
            $MB.refreshTable('dataTable');
        });
    });
}