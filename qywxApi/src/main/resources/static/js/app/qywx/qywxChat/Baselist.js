$(function () {
    getFollower();
    getTable();
});

/**
 * 获取页面列表数据
 */
function getTable(){
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
                pageNum: (params.offset / params.limit) + 1,
                param: {owner: $( "#followUser" ).val(), status: $( "#status" ).val()}
            };
        },
        columns: [ {
            checkbox: true,
        },{
            field: 'chatId',
            title: 'ID',
            visible: false
        },{
            field: 'groupName',
            title: '群名称',
            align: 'center',
            formatter: function (value, row, index) {
                if(value==='')
                {
                    return '群聊';
                } else{
                    return value;
                }
            }
        }, {
            field: 'owner',
            title: '群主',
            align: 'center'
        }, {
            field: 'groupNumber',
            title: '群人数',
            align: 'center'
        }, {
            field: 'createTime',
            title: '创建时间',
            align: 'center'
        }
        ]
    };
    $MB.initTable( 'baseTable', settings );
}

/**
 * 获取群主列表
 */
function getFollower(){
    $.get( "/contactWay/getFollowUserList", {}, function (r) {
        var resultdata=r.data;
        var html="<option value=''>全部</option>";
        if (r.code === 200) {
            for(var i=0;i<resultdata.length;i++){
                html= html+"<option value='"+resultdata[i].userId+"'>"+resultdata[i].name+"</option>";
            }
            $("#followUser").html(html);
        } else {
            $MB.n_warning( r.msg );
        }
    } );
}
//搜索
function searchActivity(){
    $MB.refreshTable( 'baseTable' );
}
//重置
function resetActivity(){
    $( "#followUser" ).find( 'option:selected' ).removeAttr( 'selected' );
    $( "#status" ).find( 'option:selected' ).removeAttr( 'selected' );
    $MB.refreshTable( 'baseTable' );
}

//详情
$( "#btn_Details" ).click( function () {
    var selected = $( "#baseTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择要查看的群！' );
        return;
    }
    var chatId = selected[0].chatId
    window.location.href="/page/goCustomerBase/goChatDetail/"+chatId;
} );


//打标签
$("#btn_lable").click(function () {
    var selected = $( "#baseTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
       $MB.n_warning( '请选择需要打标签的群！' );
        return;
    }
    $("#lableModal").modal('show');
    showAllTag();
});

function chooseLable(dom) {
   $(dom).css("background-color","#9174e23d")
}
//打标签界面保存
function saveMaterial() {
    $("#lableModal").modal('hide');
}
