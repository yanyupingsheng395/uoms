$( function () {
    getDataList( 'imageTable');
} );

function getDataList(tableId) {
    let settings = {
        url: "/qywxMedia/getDataList",
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
    $.post( "/qywxMedia/uploadMaterial", {
        title: $( "#title" ).val(),
        mediaType: 'image',
        base64Code: $( "input[name='image[]']" ).val()
    }, function (r) {
        if (r.code === 200) {
            $MB.n_success( "保存成功！" );
        }
    } );
}

