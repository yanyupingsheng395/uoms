$( function () {
    getTagGroupList();
});
//获取标签组列表
function getTagGroupList() {
    var settings = {
        url: "/qywxtag/getGroupTag",
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
            field: 'groupId',
            title: 'ID',
            visible: false
        }, {
            field: 'groupName',
            title: '标签组名称',
            align: 'center'
        }, {
            field: 'createTime',
            title: '标签组创建时间',
            align: 'center'
        }, {
            field: 'tagList',
            title: '子标签数量',
            align: 'center',
            formatter: function (value, row, index) {
                    return value.length;
            }

        }]
    };
    $MB.initTable( 'baseTable', settings );
}

/**
 * 新增标签组界面弹出
 */
$("#group_add").click(function () {
    $("#add_tag_group").modal("show");
});

/**
 * 新增标签组
 */
$("#tag_group_save").click(function () {
    var groupName=$("#groupName").val();
    var group_tag_name=$("#group_tag_name").val();
    $.get( "/qywxtag/addTagGroup", {groupName:groupName,groupTagName:group_tag_name}, function (r) {
        if (r.code === 200) {
            //给出提示
            $MB.n_success( "新建标签组成功!" );
            $( '#add_tag_group' ).modal( 'hide' );
            $MB.refreshTable( 'baseTable' );
        } else {
            $MB.n_danger( r.msg );
        }
    } );
});

/**
 * 修改标签组名称弹窗
 */
$("#group_update").click(function () {
    var selected = $( "#baseTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择一个标签组！' );
        return;
    }
    $("#groupTagid").val(selected[0]['groupId']);
    $("#groupTagName").val(selected[0]['groupName']);
    $("#up_tag_group").modal('show');
});

/**
 * 修改标签组名称
 */
$("#tag_update").click(function () {
    var groupid=$("#groupTagid").val();
    var groupName=$("#groupTagName").val();
    if(groupid==""||groupid==null){
        $MB.n_danger( "标签组ID不存在，请刷新页面后重新修改！");
        return;
    }
    if(groupName==null||groupName==""){
        $MB.n_danger( "请输入标签组名称" );
        return;
    }
    $.get( "/qywxtag/updateGroupTagName", {name:groupName,id:groupid,flag:"G"}, function (r) {
        if (r.code === 200) {
            $MB.n_success( "修改名称成功!" );
            $MB.refreshTable( 'baseTable' );
            $("#up_tag_group").modal('hide');
        } else {
            $MB.n_danger( r.msg );
        }
    } );
});
/**
 * 关闭标签组修改弹窗
 */
$("#group_close").click(function () {
    $("#up_tag_group").modal('hide');
});

/**
 * 删除标签组，并删除标签组下的所有标签
 */
$("#group_del").click(function () {
    var selected = $( "#baseTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择一个标签组！' );
        return;
    }
    var groupId=selected[0]['groupId'];
    $MB.confirm({
        title: '提示：',
        content: '确认删除当前标签组？'
    }, function () {
        $.post( "/qywxtag/delGroupTag", {id:groupId,flag:"G"}, function (r) {
            if (r.code === 200) {
                $MB.n_success( "删除标签组成功" );
                $MB.refreshTable( 'baseTable' );
            } else {
                $MB.n_danger( r.msg );
            }
        } );
    });

});


/**
 * 查看标签组内的所有标签
 */
$("#group_detail").click(function () {
    var selected = $( "#baseTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择一个标签组！' );
        return;
    }
    var groupId=selected[0]['groupId'];
    $("#chooseTag").modal('show');
    $("#groupId").val(groupId);
    getTagGroupDetail(groupId);
});

/**
 * 根据标签组ID获取下面的所有标签
 * @param groupId
 */
function getTagGroupDetail(groupId) {
    var settings = {
        url: "/qywxtag/getTagGroupDetail",
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
                param: {groupId: groupId}
            };
        },
        columns: [{
            checkbox: true,
        },{
            field: 'tagId',
            title: 'ID',
            visible: false
        }, {
            field: 'tagName',
            title: '标签名称',
            align: 'center'
        }, {
            field: 'tagCreateTime',
            title: '标签创建时间',
            align: 'center'
        }, {
            field: 'insertBy',
            title: '创建人',
            align: 'center',
            formatter: function (value, row, index) {
                if(value==null||value==""){
                    return "-";
                }else{
                    return value;
                }
            }
        }]
    };
    $("#TagTable").bootstrapTable('destroy').bootstrapTable(settings);
}

/**
 * 关闭查看所有标签的弹窗
 */
function closeChoose() {
    $("#chooseTag").modal('hide');
}

/**
 * 展示新增标签弹窗，隐藏标签列表弹窗
 */
$("#tag_add").click(function () {
    $( '#add_tag' ).modal( 'show' );
    $("#tagGroupid").val( $("#groupId").val());
    hideTagList();
});
/**
 * 隐藏新增标签弹窗，显示标签列表弹窗
 */
$("#tag_close").click(function () {
    $( '#add_tag' ).modal( 'hide' );
    $("#tagName").val("");
    showTagList();
});


/**
 * 新增标签
 */
$("#tag_save").click(function () {
    var tagName=$("#tagName").val();
    var groupid=$("#tagGroupid").val();
    if(tagName==null||tagName==""){
        $MB.n_warning( "请填写标签名称！" );
        return;
    }
    $.post( "/qywxtag/addTag", {tagName:tagName,groupid:groupid}, function (r) {
        if (r.code === 200) {
            $MB.n_success( "新建标签成功!" );
            $( '#add_tag' ).modal( 'hide' );
            showTagList();
            $("#tagName").val("");
            $MB.refreshTable( 'TagTable' );
        } else {
            $MB.n_danger( r.msg );
        }
    } );
});


/**
 * 展示修改标签弹窗，隐藏标签列表弹窗
 */
$("#tag_up").click(function () {
    var selected = $( "#TagTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择一个标签！' );
        return;
    }
    var tagId=selected[0]['tagId'];
    var tagName=selected[0]['tagName'];
    $( '#update_tag' ).modal( 'show' );
    $("#updateTagId").val(tagId );
    $("#updateTagName").val(tagName );
    hideTagList();
});
/**
 * 隐藏修改标签弹窗，显示标签列表弹窗
 */
$("#tag_update_close").click(function () {
    $( '#update_tag' ).modal( 'hide' );
    $("#updateTagName").val("");
    showTagList();
});

/**
 * 修改标签
 */
$("#tag_update_save").click(function () {
    var tagName=$("#updateTagName").val();
    var tagId=$("#updateTagId").val();
    if(tagName==null||tagName==""){
        $MB.n_warning( "请填写标签名称！" );
        return;
    }
    $.post( "/qywxtag/updateGroupTagName", {name:tagName,id:tagId,flag:"T"}, function (r) {
        if (r.code === 200) {
            $MB.n_success( "修改标签成功!" );
            $( '#update_tag' ).modal( 'hide' );
            showTagList();
            $("#updateTagName").val("");
            $MB.refreshTable( 'TagTable' );
        } else {
            $MB.n_danger( r.msg );
        }
    } );
});


/**
 * 删除标签
 */
$("#tag_del").click(function () {
    var selected = $( "#TagTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择一个标签！' );
        return;
    }
    var tagId=selected[0]['tagId'];
    $MB.confirm({
        title: '提示：',
        content: '确认删除当前标签？'
    }, function () {
        $.post( "/qywxtag/delGroupTag", {id:tagId,flag:"T"}, function (r) {
            if (r.code === 200) {
                $MB.n_success( "删除标签成功" );
                $MB.refreshTable( 'TagTable' );
            } else {
                $MB.n_danger( r.msg );
            }
        } );
    });
});


/**
 * 展示标签列表页
 */
function showTagList() {
    $("#chooseTag").show();
}

/**
 * 隐藏标签列表页
 */
function hideTagList() {
    $("#chooseTag").hide();
}

