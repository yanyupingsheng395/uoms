$( function () {
    getTagGroupList();
});

function createRules() {
    $("#lableModal").modal('show');
};
// 初始化日期控件
function initDt() {
    var date = new Date();
   init_date( 'sendDT', 'yyyy-mm-dd ', 0, 2, 0 );
    $( "#sendDT" ).datepicker( 'setStartDate', date );
};

//获取规则列表
function getTagGroupList() {
    var settings = {
        url: "/qywxCustomer/getTagGroupList",
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
        columns: [{
            checkbox: true,
        },{
            field: 'tagId',
            title: 'ID',
            visible: false
        },{
            field: 'taskName',
            title: '任务名称',
            align: 'center'
        }, {
            field: 'groupName',
            title: '群名称',
            align: 'center'
        }, {
            field: 'sendInviteMembers',
            title: '发送邀请成员',
            align: 'center'
        }, {
            field: 'insertDt',
            title: '创建时间',
            align: 'center'
        }, {
            field: 'inviteMembers',
            title: '已邀请客户',
            align: 'center'
        }, {
            field: 'joinMembers',
            title: '已入群客户',
            align: 'center'
        }, {
            field: 'NoSendMembers',
            title: '未发送成员',
            align: 'center'
        }, {
            field: 'noInviteMembers',
            title: '未邀请成员',
            align: 'center'
        }]
    };
    $MB.initTable( 'baseTable', settings );
}