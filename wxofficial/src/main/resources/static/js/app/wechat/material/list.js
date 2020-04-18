function getImageDataList() {
    let settings = {
        url: "/material/getDataList",
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
                formatter: function (value, row, index) {
                    return resolvingDate( value );
                }
            }]
    };
    $MB.initTable( 'imageTable', settings );
}

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