$(function () {
    getGroupCalenDarList();
});

function getGroupCalenDarList() {
    var settings = {
        url: "/qywxCustomer/getGroupCalenDarList",
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
        }, {
            field: 'groupName',
            title: '群名称',
            align: 'center'
        }, {
            field: 'insertBy',
            title: '创建人',
            align: 'center'
        }, {
            field: 'insertDt',
            title: '创建时间',
            align: 'center'
        }, {
            field: 'openFlag',
            title: '是否开启',
            align: 'center'
        }
        ]
    };
    $MB.initTable( 'baseTable', settings );
}
