$( function () {
    getMessageList();
});

function getMessageList() {
    var settings = {
        url: "/qywxChatMsg/getMeList",
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
            field: 'batchMsgId',
            title: 'ID',
            visible: false
        },{
            field: 'batchMsgName',
            title: '名称',
            align: 'center'
        }, {
            field: 'chatOwnerSize',
            title: '群主数',
            align: 'center'
        }/*, {
            field: 'chatSize',
            title: '群聊数',
            align: 'center'
        }, {
            field: 'userSize',
            title: '需要推送用户数',
            align: 'center'
        }, {
            field: 'execChatOwnerSize',
            title: '实际执行的群主数',
            align: 'center'
        }, {
            field: 'execChatSize',
            title: '成功推送的群数',
            align: 'center'
        }, {
            field: 'execUserSize',
            title: '成功推送的用户数',
            align: 'center'
        }*/,{
            field: 'insertDt',
            title: '创建时间',
            align: 'center'
        },{
            field: 'execDate',
            title: '执行时间',
            align: 'center'
        } ,{
            field: 'execBy',
            title: '执行人',
            align: 'center'
        },{
            field: 'status',
            title: '状态',
            align: 'center',
            formatter: function (value, row, index) {
                var res = "-";
                if(value === 'todo') {
                    res = "<span class=\"badge bg-info\">待推送</span>";
                }
                if(value === 'fail') {
                    res = "<span class=\"badge bg-warning\">推送失败</span>";
                }
                if(value === 'done') {
                    res = "<span class=\"badge bg-success\">推送成功</span>";
                }
                return res;
            }

        }]
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


/**
 *键盘键入事件
 */
function smsContentValid() {
    $('#smsContentInput').val($('#textContent').val());
    if($('#smsContentInput').val() !== '') {
        $('#smsContentInput').removeClass('error');
        $("#smsContentInput-error").remove();
    }
    var content = $('#textContent').val() === "" ? "请输入短信内容": $('#textContent').val();
    $("#article").html('').append(content);
}

/**
 *进入新增消息界面
 */
$("#btn_save").click(function () {
    window.location.href="/page/goChatMsgList/add";
});
/**
 * 新增一条群聊
 */
var validator_coupon;
function saveCouponData() {
    selectType( $("#msgType").val());
    $( "input[name='chatOwnerList']" ).val( user_list.join( "," ) );
    var flag = validator_coupon.form();
    if(flag) {
        $.post("/qywxChatMsg/saveData", $("#smsTemplateAddForm").serialize(), function (r) {
            if(r.code == 200) {
                $MB.n_success("保存成功！");
                window.location.href="/page/goChatMsgList";
            }else {
                $MB.n_danger("保存失败！");
            }
        });
    }
}

//删除
$("#btn_delete").click(function () {
    var selected = $( "#baseTable" ).bootstrapTable( 'getSelections' );
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        $MB.confirm( {
            title: "<i class='mdi mdi-alert-outline'></i>提示：",
            content: "确定删除选中的记录?"
        }, function () {
            $.post( "/qywxChatMsg/deleteById", {id: selected[0]['batchMsgId']}, function (r) {
                if (r.code == 200) {
                    $MB.n_success( "删除成功！" );
                    $MB.refreshTable( 'baseTable' );
                }
            } );
        } );
    }

});

/**
 * 添加渠道和职员选项
 * @param selid
 */
var user_list =[];//添加人员的集合
function addRegion(selid) {
    var id = $( "#" + selid ).find( "option:selected" ).val();
    var name = $( "#" + selid ).find( "option:selected" ).text();
    if(id!=""&&id!=null){
        if(user_list.indexOf(id)==-1){
            user_list.push(id);
            $( "#alllist" ).append( "<span class=\"tag\"><span>" + name + "&nbsp;&nbsp;</span><a style=\"color: #fff;cursor: pointer;\" onclick=\"regionRemove(this, \'" + id + "\', \'" + selid + "\')\">x</a></span>" );
        }
    }
}

// 移除region数据
function regionRemove(dom, id, selid) {
    $( dom ).parent().remove();
    id=parseInt(id);
    user_list.splice( user_list.indexOf( id ), 1 );
}

function selectType(type) {
    $("#msgType").val(type);
    var icon = "<i class='mdi mdi-close-circle'></i> ";
    var rule = {
        rules: {
            batchMsgName: {
                required: true
            },
            textContent: {
                required: true
            },
            chatOwnerList:{
                required: true
            }
        },
        messages: {
            batchMsgName: {
                required: icon + "请输入群发名称"
            },
            textContent: {
                required: icon + "请输入群发消息内容"
            },
            chatOwnerList: {
                required: icon + "请选择群主"
            }
        }
    };
    validator_coupon = $("#smsTemplateAddForm").validate(rule);

    if (type == "image") {
        //添加对图片的校验
        $("#picUrl").rules("add",{required:true,messages:{required:"请选择图片地址！"}});

        //清除对小程序和web的校验
        $("#mpTitle").rules("remove");
        $("#mpUrl").rules("remove");
        $("#mediaId").rules("remove");

        $("#linkTitle").rules("remove");
        $("#linkUrl").rules("remove");
    } else if (type == "web") {

        $("#linkTitle").rules("add",{required:true,messages:{required:"请填写网页标题！"}});
        $("#linkUrl").rules("add",{required:true,messages:{required:"请填写网页地址！"}});

        $("#mpTitle").rules("remove");
        $("#mpUrl").rules("remove");
        $("#mediaId").rules("remove");

        $("#picUrl").rules("remove");
    } else if (type == "miniprogram") {
        //添加对小程序的校验
        $("#mpTitle").rules("add",{required:true,messages:{required:"请输入小程序标题！"}});
        $("#mpUrl").rules("add",{required:true,messages:{required:"请输入小程序地址！"}});
        $("#mediaId").rules("add",{required:true,messages:{required:"请选择小程序封面ID！"}});

        $("#linkTitle").rules("remove");
        $("#linkUrl").rules("remove");

        $("#picUrl").rules("remove");
    }
    validator_coupon.resetForm();
}

//推送信息
$("#btn_push").click(function () {
    var selected = $( "#baseTable" ).bootstrapTable( 'getSelections' );
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }
    let batchMsgId=selected[0]['batchMsgId'];
    $MB.loadingDesc("show", "正在推送中，请稍候...");
    $.post( "/qywxChatMsg/pushMessage", {batchMsgId: batchMsgId}, function (r) {
        $MB.loadingDesc('hide');
        if (r.code == 200) {
            $MB.n_success( "群发消息推送成功！" );
        }else{
            $MB.n_danger("群发消息推送失败");
        }
        $MB.refreshTable( 'baseTable' );
    } );
});