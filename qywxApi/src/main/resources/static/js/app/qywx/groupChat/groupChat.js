$("#btn_GroupChat").click(function () {
    $("#addGroupChat").modal('show');
    getDataList("chatTable");
});

function getDataList(tableId) {
    var settings = {
        url: "/qywxCustomer/getGroupChat",
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
            checkbox: true,
            valign: "middle",
        },{
                field: 'groupId',
                title: 'ID',
                visible: false
            },
            {
                field: 'groupName',
                title: '群名称',
                align: 'center'
            }, {
                field: 'lordName',
                title: '群主名称',
                align: 'center'
            }]
    };
    $MB.initTable( tableId, settings );
}

function closeChoose() {
    $("#addGroupChat").modal('hide');
}

function saveData() {
  //选择群聊点击确定操作
}

