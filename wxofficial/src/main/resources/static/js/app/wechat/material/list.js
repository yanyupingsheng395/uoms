$( function () {
    getDataList('image', 'imageTable');
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
                    return '<a href="' + row['url'] + '" target="_blank" style="text-decoration: underline">' + value + '</a>';
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
$("#image-tab").on("shown.bs.tab", function () {
    media_type = 'image';
    getDataList('image', 'imageTable');
    $("#videoAndVoice").attr("style", "display:none");
    $("#imageBox").attr("style", "display:block");
});

$("#audio-tab").on("shown.bs.tab", function () {
    media_type = 'voice';
    getDataList('voice', 'voiceTable');
    $("#videoAndVoice").attr("style", "display:block");
    $("#imageBox").attr("style", "display:none");
});

$("#video-tab").on("shown.bs.tab", function () {
    media_type = 'video';
    getDataList('video', 'videoTable');
    $("#videoAndVoice").attr("style", "display:block");
    $("#imageBox").attr("style", "display:none");
});

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
    upload = new Cupload ({
        ele: '#cupload-create',
        num: 1
    });
}

$( "#uploadFile" ).change( function () {
    $( '#filename' ).text($(this)[0].files[0].name).attr( "style", "display:inline-block;" );
} );

// 保存素材
function saveMaterial() {
    if(media_type === 'image') {
        $.post("/material/uploadMaterial", {
            title: $("#title").val(),
            introduction: $("#introduction").val(),
            mediaType: media_type,
            base64Code: $("input[name='image[]']").val()
        }, function (r) {
            if(r.code === 200) {
                $MB.n_success("保存成功！");
            }
        });
    }
}
