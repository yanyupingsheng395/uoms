//显示选择临时素材列界面
function chooseMediaImg() {
    $("#add_modal").hide();
    $("#chooseMediaImg").modal('show');
    getDataList_img( 'imageTable_MediaImg');
}

var upload;
image();

function image() {
    upload = new Cupload( {
        ele: '#cupload-create_MediaImg',
        num: 1
    } );
}

function getDataList_img(tableId) {
    var settings = {
        url: "/wxMedia/getPermanentImg",
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
        columns: [{
            checkbox: true
        },{
                field: 'imgId',
                title: 'ID',
                visible: false
            },
            {
                field: 'imgTitle',
                title: '图片名称',
                align: 'center'
            }, {
                field: 'imgUrl',
                title: '图片连接',
                align: 'center'
            }, {
                field: 'insertDt',
                title: '图片创建时间',
                align: 'center'
            }]
    };
    $MB.initTable( tableId, settings );
}


// 保存素材
function saveMediaImgMaterial() {
    var MediaImgtitle=$( "#MediaImgtitle" ).val();
    var code=$( "input[name='image[]']" ).val();

    if(MediaImgtitle === '') {
        $MB.n_warning("图片名称不能为空！");
        return false;
    }

    if(code === ''||null==code) {
        $MB.n_warning("图片不能为空！");
        return false;
    }
    $MB.loadingDesc("show", "图片正在上传中，请稍候...");
    $.post( "/wxMedia/uploadPermanentImg", {
        title: MediaImgtitle,
        mediaType: 'image',
        base64Code: code
    }, function (r) {
        $MB.loadingDesc("hide");
        if (r.code === 200) {
            $MB.n_success( "保存成功！" );
            //关闭弹出框
            $("#MediaImgModal").modal('hide');
            $("#chooseMediaImg").show();
            //刷新表格
            $MB.refreshTable('imageTable_MediaImg');
        }
    } );
}

function closeMediaImgMaterial() {
    $("#MediaImgModal").modal('hide');
    $("#chooseMediaImg").show();
    clearImg();
}

function showUpMediaImg() {
    $("#chooseMediaImg").hide();
    $('#MediaImgModal').modal('show');
}

function closeMediaImg() {
    $("#add_modal").show();
    $("#chooseMediaImg").modal('hide');
}

function saveMediaImg() {
    var selected = $("#imageTable_MediaImg").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
       var imgUrl=selected[0]['imgUrl'];
        $("#add_modal").show();
        $("#chooseMediaImg").modal('hide');
        $("#picUrl").val(imgUrl);
    }
}

//上传成功后，将弹窗内容清除
function clearImg() {
    $("#MediaImgtitle").val("");
    //清除所选图片
    $("#cupload-create_MediaImg").html("");
    image();
}