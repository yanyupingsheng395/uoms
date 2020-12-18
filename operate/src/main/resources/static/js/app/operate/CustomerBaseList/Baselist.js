$(function () {
    var settings = {
        url: "/qywxCustomer/getContractList",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [ {
                checkbox: true,
            },{
                field: 'groupId',
                title: 'ID',
                visible: false
            },{
                field: 'groupName',
                title: '群名称',
                align: 'center'
            }, {
                field: 'lordName',
                title: '群主',
                align: 'center'
            }, {
                field: 'groupNumber',
                title: '群人数',
                align: 'center'
            }, {
                field: 'todayJoinGroupNumber',
                title: '今日入群',
                align: 'center'
            }, {
                field: 'todayDropOutNumber',
                title: '今日退群',
                align: 'center'
            }, {
                field: 'insertDt',
                title: '创建时间',
                align: 'center'
            }
        ]
    };
    $MB.initTable( 'baseTable', settings );
});


$("#btn_lable").click(function () {
    $("#lableModal").modal('show');
});