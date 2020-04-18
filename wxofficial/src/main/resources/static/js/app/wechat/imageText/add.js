$(function () {
    setEditor();
});

var editor;
function setEditor() {
    var E = window.wangEditor;
    editor = new E('#div1', '#div2');
    editor.customConfig.uploadImgShowBase64 = true;
    editor.create();
    editor.txt.html('');
}

var upload;
function fileUpload(data) {
    if(data.length > 0) {
        upload.uploadBox.remove();
        upload = new Cupload ({
            ele: '#cupload-create',
            num: 1,
            data: data,
            type: 'select', // select, upload
            callback: function() {
                $('#coverModal').modal('show');
                $('#imageTextAdd').modal('hide');
                getDataList('image', 'imageTable');
            }
        });
    }else {
        upload = new Cupload ({
            ele: '#cupload-create',
            num: 1,
            type: 'select', // select, upload
            callback: function() {
                $('#coverModal').modal('show');
                $('#imageTextAdd').modal('hide');
                getDataList('image');
            }
        });
    }
}

$("#saveDataBtn").click(function () {
    var title = $("#imageTextForm").find("input[name='title']").val();
    var author = $("#imageTextForm").find("input[name='author']").val();
    var wxAbstract = $("#imageTextForm").find("textarea[name='wxAbstract']").val();
    var content = editor.txt.html();
    var cover = $("#cover").val();
    if($(this).attr("name") === 'save') {
        $.post("/imageText/addImageText", {title: title, author: author, content: content, wxAbstract:wxAbstract, cover: cover}, function (r) {
            if(r.code === 200) {
                $MB.n_success("新增成功！");
                $("#imageTextAdd").modal('hide');
                $MB.refreshTable('imageTextTable');
            }
        });
    }else if($(this).attr("name") === 'update') {
        $.post("/imageText/updateImageText", {title: title, author: author, content: content, wxAbstract:wxAbstract, cover: cover}, function (r) {
            if(r.code === 200) {
                $MB.n_success("更新成功！");
                $("#imageTextAdd").modal('hide');
                $MB.refreshTable('imageTextTable');
            }
        });
    }
});

function addImageText() {
    $("#saveDataBtn").attr("name", "save");
    imageTextAddModalInit();
    $('#imageTextAdd').modal('show');
}

$("#imageTextAdd").on("hidden.bs.modal", function () {

});

// 新增图文窗口数据初始化
function imageTextAddModalInit() {
    $("#imageTextForm").find("input[name='title']").val('');
    $("#imageTextForm").find("input[name='author']").val('');
    $("#imageTextForm").find("textarea[name='wxAbstract']").val('');
    editor.txt.html('');
    $("#cover").val('');
    $("#mediaId").val('');
    if(upload !== undefined) {
        $("#cupload-create").html('');
        fileUpload([]);
    }else {
        fileUpload([]);
    }
}

$("#selectCoverBtn").click(function () {
    var selected = $("#imageTable").bootstrapTable('getSelections');
    var length = selected.length;
    if(length === 0) {
        $MB.n_warning("至少选择一条记录！");
        return;
    }
    $("#cover").val(selected[0]['mediaId']);
    fileUpload([selected[0]['url']]);
    $('#imageTextAdd').modal('show');
    $('#coverModal').modal('hide');
});