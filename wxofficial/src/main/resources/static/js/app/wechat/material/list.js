$( function () {
    getDataList( 'image', 'imageTable' );
} );

function getDataList(type, tableId) {
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
    $MB.initTable( tableId, settings );
}

var media_type = 'image';
$( "#image-tab" ).on( "shown.bs.tab", function () {
    media_type = 'image';
    getDataList( 'image', 'imageTable' );
    $( "#videoAndVoice" ).attr( "style", "display:none" );
    $( "#imageBox" ).attr( "style", "display:block" );
} );

$( "#audio-tab" ).on( "shown.bs.tab", function () {
    media_type = 'voice';
    getDataList( 'voice', 'voiceTable' );
    $( "#videoAndVoice" ).attr( "style", "display:block" );
    $( "#imageBox" ).attr( "style", "display:none" );
} );

$( "#video-tab" ).on( "shown.bs.tab", function () {
    media_type = 'video';
    getDataList( 'video', 'videoTable' );
    $( "#videoAndVoice" ).attr( "style", "display:block" );
    $( "#imageBox" ).attr( "style", "display:none" );
} );

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

var upload;
image();

function image() {
    upload = new Cupload( {
        ele: '#cupload-create',
        num: 1
    } );
}

$( "#uploadFile" ).change( function () {
    $( '#filename' ).text( $( this )[0].files[0].name ).attr( "style", "display:inline-block;" );
} );

// 保存素材
function saveMaterial() {
    alert( 1 )
    if (media_type === 'image') {
        $.post( "/material/uploadMaterial", {
            title: $( "#title" ).val(),
            introduction: $( "#introduction1" ).val(),
            mediaType: media_type,
            base64Code: $( "input[name='image[]']" ).val()
        }, function (r) {
            if (r.code === 200) {
                $MB.n_success( "保存成功！" );
            }
        } );
    }
    // 音频
    if (media_type === 'voice' || media_type === 'video') {
        let formData = new FormData();
        formData.append( "file", $( "#uploadFile" )[0].files[0] );
        formData.append( "mediaType", media_type );
        formData.append( "introduction", $( "#introduction2" ).val() );
        formData.append( "title", $( "#title" ).val() );

        $.ajax( {
            url: '/material/uploadMaterial',
            data: formData,
            type: 'post',
            contentType: false,
            processData: false,
            success: function (r) {
                if (r.code === 200) {
                    $MB.n_success( "保存成功！" );
                }
            }
        } );
    }
}

$( "#btn_delete" ).click( function () {
    var tableId = "";
    if (media_type === 'image') {
        tableId = 'imageTable';
    }
    if (media_type === 'voice') {
        tableId = 'voiceTable';
    }
    if (media_type === 'video') {
        tableId = 'videoTable';
    }
    $MB.confirm( {
            title: "<i class='mdi mdi-alert-outline'></i>提示：",
            content: "确认删除选中的素材？"
        },
        function () {
            var selected = $( "#" + tableId ).bootstrapTable( 'getSelections' );
            var length = selected.length;
            if (length === 0) {
                $MB.n_warning( "至少选择一条记录！" );
                return;
            }
            $.post( "/material/deleteMaterial", {id: selected[0]['mediaId']}, function (r) {
                if (r.code === 200) {
                    $MB.n_success( "删除素材成功！" );
                } else {
                    $MB.n_danger( "删除素材失败！" );
                }
                $MB.refreshTable( tableId );
            } );
        } );
} );

$("#materialModal").on('hidden.bs.modal', function (r) {
    $("#title").val('');
    $("#introduction1").val('');
    $("#introduction2").val('');
    $("#uploadFile").val('');
    $("#filename").html('');
    $("#cupload-create").html('');
    new Cupload( {
        ele: '#cupload-create',
        num: 1
    } );
});

$("#btn_download").click(function () {
    var tableId = "";
    if (media_type === 'image') {
        tableId = 'imageTable';
    }
    if (media_type === 'voice') {
        tableId = 'voiceTable';
    }
    if (media_type === 'video') {
        tableId = 'videoTable';
    }
    var selected = $( "#" + tableId ).bootstrapTable( 'getSelections' );
    var length = selected.length;
    if (length === 0) {
        $MB.n_warning( "至少选择一条记录！" );
        return;
    }

    if (media_type === 'image' || media_type === 'voice') {
        window.location.href = "/material/getMaterialOther?mediaId=" + selected[0]['mediaId'] + "&fileName=" + selected[0]['name'];
    }
    if(media_type === 'video') {
        $.get("/material/getMaterialVideo", {mediaId: selected[0]['mediaId']}, function (r) {
            window.open(r.data['downUrl'], '_blank');
        });
    }
});