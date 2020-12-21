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
                field: 'owner',
                title: '群主',
                align: 'center'
            }, {
                field: 'groupNumber',
                title: '群人数',
                align: 'center'
            }, {
                field: 'groupJoin',
                title: '今日入群',
                align: 'center'
            }, {
                field: 'groupOut',
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


$( "#btn_Details" ).click( function () {
    var selected = $( "#baseTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择要查看的群！' );
        return;
    }
    var groupId = selected[0].groupId;
    $("#customDetail").modal('show');
    $("#groupName").val(selected[0].groupName);
    $("#owner").val(selected[0].owner);
    $("#notice").val(selected[0].notice);
    getCustomerList(groupId);
} );

function getCustomerList(groupId) {
    var settings = {
        url: "/qywxCustomer/getCustomerList",
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
                pageNum: (params.offset / params.limit)+ 1,
                groupId: groupId
            }
        },
        columns: [{
                field: 'userName',
                title: '用户名称',
                align: 'center'
            }, {
                field: 'joinTime',
                title: '加入时间',
                align: 'center'
            }, {
                field: 'joinScene',
                title: '加入方式',
                align: 'center',
            formatter: function (value, row, indx) {
                if (value === '1') {
                    return "<span >直接邀请入群</span>";
                } else if (value === '2') {
                    return "<span >通过邀请链接入群</span>";
                } else if (value === '3') {
                    return "<span>扫描群二维码入群</span>";
                }
            }
            }, {
                field: 'userType',
                title: '用户类型',
                align: 'center',
            formatter: function (value, row, indx) {
                if (value === '1') {
                    return "<span>企业成员</span>";
                } else if (value === '2') {
                    return "<span >外部联系人</span>";
                }
            }
        }]
    };
    $MB.initTable( "customerList", settings );
}

function closeMod(){
    $("#customDetail").modal('hide');
}

$("#btn_lable").click(function () {
    var selected = $( "#baseTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择需要打标签的群！' );
        return;
    }
    $("#lableModal").modal('show');
});

function chooseLable(dom) {
   $(dom).css("background-color","#9174e23d")
    console.log(dom);
}

function saveMaterial() {
    $("#lableModal").modal('hide');
}