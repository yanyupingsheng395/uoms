var media_type = 'text';
$( "#text-tab" ).on( "shown.bs.tab", function () {
    media_type = 'text';
} );

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