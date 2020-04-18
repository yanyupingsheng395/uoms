$(function () {
    getTableData();
});

function getTableData() {
    let settings = {
        url: "/imageText/getDataList",
        cache: false,
        pagination: true,
        singleSelect: false,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        columns: [
            {
                checkbox: true,
            },
            {
                field: 'title',
                title: '图文标题',
            },
            {
                field: 'userGroup',
                title: '适用人群'
            },
            {
                title: "图文预览",
                align: "center",
                formatter: function (value, row, index) {
                    return '<a href=""><i class="fa fa-eye"></i></a>';
                }
            }]
    };
    $MB.initTable('imageTextTable', settings);
}

$( "#btn_delete" ).click( function () {
    $MB.confirm( {
            title: "<i class='mdi mdi-alert-outline'></i>提示：",
            content: "确认删除选中的图文？"
        },
        function () {
            var selected = $( "#imageTextTable" ).bootstrapTable( 'getSelections' );
            var length = selected.length;
            if (length === 0) {
                $MB.n_warning( "至少选择一条记录！" );
                return;
            }
            $.post( "/imageText/deleteImageText", {id: selected[0]['mediaId']}, function (r) {
                if (r.code === 200) {
                    $MB.n_success( "删除图文成功！" );
                } else {
                    $MB.n_danger( "删除图文失败！" );
                }
                $MB.refreshTable('imageTextTable');
            } );
        } );
} );

$( "#btn_view" ).click( function () {
    var selected = $( "#imageTextTable" ).bootstrapTable( 'getSelections' );
    var length = selected.length;
    if (length === 0) {
        $MB.n_warning( "至少选择一条记录！" );
        return;
    }
    window.open(selected[0]['url'], '_blank');
} );

$( "#btn_edit" ).click( function () {
    $("#saveDataBtn").attr("name", "update");
    var selected = $( "#imageTextTable" ).bootstrapTable( 'getSelections' );
    var length = selected.length;
    if (length === 0) {
        $MB.n_warning( "至少选择一条记录！" );
        return;
    }
    imageTextAddModalInit();
    $("#imageTextAdd").modal('show');
    var data = selected[0];
    $("#imageTextForm").find("input[name='title']").val(data['title']);
    $("#imageTextForm").find("input[name='author']").val(data['author']);
    $("#imageTextForm").find("textarea[name='wxAbstract']").val(data['digest']);
    editor.txt.html(data['content']);
    $("#cover").val(data['thumbMediaId']);
    $("#mediaId").val(data['mediaId']);
    fileUpload([data['thumbUrl']]);
} );
