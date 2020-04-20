$( function () {
    getDataList( );
} );

function getDataList() {
    let settings = {
        url: "/tag/getDataListPage",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "client",
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
                field: 'name',
                align: 'center',
                title: '名称'
            },
            {
                field: 'count',
                align: 'center',
                title: '人数'
            }, {
                title: '操作',
                align: 'center',
                formatter: function (value, row, index) {
                    return "<a style='cursor: pointer' onclick='editData(\""+row['id']+"\", \""+row['name']+"\")'><i class='fa fa-edit'></i>编辑</a>&nbsp;&nbsp;<a style='cursor: pointer' onclick='deleteData(\""+row['id']+"\")'><i class='fa fa-trash'></i>删除</a>";
                }
            }]
    };
    $MB.initTable( 'tagTable', settings );
}

function deleteData(id) {
    $MB.confirm({
        title: '提示：',
        content: '确认删除当前记录？'
    }, function () {
        $.post("/tag/deleteData", {id: id}, function (r) {
            if(r.code === 200) {
                $MB.n_success("删除成功！");
                $MB.refreshTable('tagTable');
            }
        });
    });
}

// 保存数据
function saveData() {
    var operate = $("#saveBtn").attr("name");
    var name = $("#tagName").val();
    if(operate === 'save') {
        $.post("/tag/saveData", {name: name}, function (r) {
            if(r.code === 200) {
                $MB.n_success("保存成功！");
                $MB.refreshTable('tagTable');
                $('#tagAddModal').modal('hide');
            }else {
                $MB.n_danger("保存失败！");
            }
        });
    }
    if(operate === 'update') {
        $.post("/tag/updateData", {id: $("#tagId").val(), name: $("#tagName").val()}, function (r) {
            if(r.code === 200) {
                $MB.n_success("修改成功！");
                $MB.refreshTable('tagTable');
                $('#tagAddModal').modal('hide');
            }else {
                $MB.n_danger("修改失败！");
            }
        });
    }
}

function editData(id, name) {
    $('#tagAddModal').modal('show');
    $("#tagModalTitle").html('修改标签');
    $("#tagName").val(name);
    $("#tagId").val(id);
    $("#saveBtn").attr("name", "update");
}

$("#tagAddModal").on('hidden.bs.modal', function () {
    $("#tagModalTitle").html('新增标签');
    $("#saveBtn").attr("name", "save");
    $("#tagName").val('');
});

function addTag() {
    $('#tagAddModal').modal('show');
    $("#saveBtn").attr("name", "save");
}


