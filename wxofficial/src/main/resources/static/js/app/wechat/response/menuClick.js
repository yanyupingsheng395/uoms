$( function () {
    getDataList(  );
} );

function getDataList() {
    let settings = {
        url: "",
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
                field: '',
                title: '回复消息类型'
            },{
                field: '',
                title: '人群标签'
            }]
    };
    $MB.initTable( 'dataTable', settings );
}
