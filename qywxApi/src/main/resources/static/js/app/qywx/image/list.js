$( function () {
    getDataList( 'imageTable');
} );

function getDataList(tableId) {
    let settings = {
        url: "/wxMedia/getImageList",
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
                pageNum: (params.offset / params.limit)+ 1
            }
        },
        columns: [
            {
                field: 'imgId',
                title: 'ID',
                visible: false
            },
            {
                field: 'imgTitle',
                title: '图片名称',
                formatter: function (value, row, index) {
                    return '<a href="' + row['imgUrl'] + '" target="_blank" style="text-decoration: underline">' + value + '</a>';
                }
            }, {
                field: 'imgUrl',
                title: '图片链接',
                formatter: function (value, row, index) {
                    return '<a href="' + value + '" target="_blank" style="text-decoration: underline">' + value + '</a>';
                }
            },
            {
                field: 'insertDt',
                title: '创建时间',
                align: 'center'
            }]
    };
    $MB.initTable( tableId, settings );
}

var upload;
image();

function image() {
    upload = new Cupload( {
        ele: '#cupload-create',
        num: 1
    } );
}

// 保存素材
function saveMaterial() {
    var title=$( "#title" ).val();
    var code=$( "input[name='image[]']" ).val();

    if(title === '') {
        $MB.n_warning("图片名称不能为空！");
        return false;
    }

    if(code === ''||null==code) {
        $MB.n_warning("图片不能为空！");
        return false;
    }
    $MB.loadingDesc('show', '保存中，请稍候...');
    $.post( "/wxMedia/uploadImage", {
        title: title,
        mediaType: 'image',
        base64Code: code
    }, function (r) {
        $MB.loadingDesc( 'hide' );
        if (r.code === 200) {
            $MB.n_success( "保存成功！" );
            //关闭弹出框
            $("#materialModal").modal('hide');
           //刷新表格
            $MB.refreshTable('imageTable');
            clearImg();
        }
    } );
}

function closeimgModel() {
    clearImg();
    //关闭弹出框
    $("#materialModal").modal('hide');
}

//上传成功后，将弹窗内容清除
function clearImg() {
    $("#title").val("");
    //清除所选图片
    $("#cupload-create").html("");
    image();
}

