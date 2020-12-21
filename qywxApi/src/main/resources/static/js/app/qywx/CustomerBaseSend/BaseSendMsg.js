$( function () {
    getMessageList();
});

function getMessageList() {
    var settings = {
        url: "/qywxCustomer/getMeList",
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
}

function showSendTime() {
    $("#sendtime").show();
}
function closeSendTime() {
    $("#sendtime").hide();
    $("#sendDT").val("");
}

layui.use('laydate', function(){
    var laydate = layui.laydate;
    //日期时间选择器
    laydate.render({
        elem: '#test5'
        ,type: 'datetime'
    });
});

function smsContentValid() {
    $('#smsContentInput').val($('#smsContent').val());
    if($('#smsContentInput').val() !== '') {
        $('#smsContentInput').removeClass('error');
        $("#smsContentInput-error").remove();
    }
    var content = $('#smsContent').val() === "" ? "请输入短信内容": $('#smsContent').val();
    $("#article").html('').append(content);
}