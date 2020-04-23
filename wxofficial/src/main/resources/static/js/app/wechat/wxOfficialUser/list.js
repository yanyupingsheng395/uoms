$( function () {
    getDataList();
} );

function getDataList() {
    let settings = {
        url: "/wxOfficialUser/getDataListPage",
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
                field: 'id',
                title: 'ID',
                visible: false
            },
            {
                field: 'headimgUrl',
                align: 'center',
                title: '头像',
                formatter: function (value, row, index) {
                    return "<img src='"+value+"'/>";
                }
            },
            {
                field: 'nickName',
                align: 'center',
                title: '昵称'
            }, {
                field: 'subscribe',
                align: 'center',
                title: '是否订阅',
                formatter: function (value, row, index) {
                    if (value == 1) {
                        return "是";
                    } else if (value == 0) {
                        return "否";
                    } else if (value == 2) {
                        return "网页授权用户";
                    } else {
                        return '-';
                    }
                }
            }, {
                field: 'openId',
                align: 'center',
                title: 'openId'
            }, {
                field: 'subscribeScene',
                align: 'center',
                title: '关注渠道',
                formatter: function (value, row, index) {
                    if (value === 'ADD_SCENE_SEARCH') {
                        return "公众号搜索";
                    } else if (value === 'ADD_SCENE_ACCOUNT_MIGRATION') {
                        return "公众号迁移";
                    } else if (value === 'ADD_SCENE_PROFILE_CARD') {
                        return "名片分享";
                    } else if (value === 'ADD_SCENE_QR_CODE') {
                        return "扫描二维码";
                    } else if (value === 'ADD_SCENEPROFILE') {
                        return "图文页内名称点击";
                    } else if (value === 'ADD_SCENE_PROFILE_ITEM') {
                        return "图文页右上角菜单";
                    } else if (value === 'ADD_SCENE_PAID') {
                        return '支付后关注';
                    } else if (value === 'ADD_SCENE_OTHERS') {
                        return '其他';
                    } else {
                        return '-';
                    }
                }
            }, {
                field: 'subscribeTime',
                align: 'center',
                title: '关注时间'
            }, {
                field: 'sex',
                align: 'center',
                title: '性别',
                formatter: function (value, row, index) {
                    if (value == 1) {
                        return "男";
                    } else if (value == 2) {
                        return "女";
                    } else if (value == 0) {
                        return "未知";
                    } else {
                        return "-";
                    }
                }
            }, {
                field: 'country',
                align: 'center',
                title: '国家'
            }, {
                field: 'province',
                align: 'center',
                title: '省份'
            }, {
                field: 'city',
                align: 'center',
                title: '城市'
            }, {
                field: 'language',
                align: 'center',
                title: '语言'
            }, {
                field: 'tagNames',
                align: 'center',
                title: '标签',
                formatter: function (value, row, index) {
                    var res = "";
                    if(value !== null && value !== "") {
                        var arr = value.split(",");
                        for (let i = 0; i < arr.length; i++) {
                            res += "<span class=\"badge bg-success\">"+arr[i]+"</span>&nbsp;";
                        }
                    }else {
                        res = "-";
                    }
                    return res;
                }
            }, {
                title: '操作',
                align: 'center',
                formatter: function (value, row, index) {
                    return "<a style='cursor: pointer;font-size: 13px;' onclick='viewUser(\"" + row['id'] + "\", \"" + row['name'] + "\")'><i class='fa fa-eye'></i>查看</a>&nbsp;&nbsp;<a style='cursor: pointer;font-size: 13px;' onclick='updateRemark(\"" + row['id'] + "\")'><i class='fa fa-edit'></i>修改备注</a>";
                }
            }]
    };
    $MB.initTable( 'userTable', settings );
}

// 打标签
$( "#addTagBtn" ).click( function () {
    operate_tag = "add";
    var selected = $( "#userTable" ).bootstrapTable( 'getSelections' );
    var length = selected.length;
    if (length === 0) {
        $MB.n_warning( "至少选择一条记录！" );
        return;
    }
    $( "input[name='tagId']:checked" ).each( function () {
        $( this ).removeAttr( "checked" );
    } );
    selected.forEach( (v, k) => {

    } );
    $( "#addTagModal" ).modal( 'show' );
} );

$( "#addTagModal" ).on( 'hidden.bs.modal', function () {
    $( "input[name='tagId']:checked" ).each( function () {
        $( this ).removeAttr( "checked" );
    } );
} );

var operate_tag = "";
// 保存标签数据
function saveTagData() {
    if(operate_tag === 'add') {
        var data = getAddTagData();
        $.post( "/wxOfficialUser/updateTagIds", {data: JSON.stringify(data)}, function (r) {
            if (r.code === 200) {
                $MB.n_success( "打标签成功！" );
            }
            $( "#addTagModal" ).modal( 'hide' );
            $MB.refreshTable( 'userTable' );
        } );
    }else if(operate_tag === 'remove') {
        var data = getRemoveTagData();
        $.post( "/wxOfficialUser/updateTagIds", {data: JSON.stringify(data)}, function (r) {
            if (r.code === 200) {
                $MB.n_success( "去除标签成功！" );
            }
            $( "#addTagModal" ).modal( 'hide' );
            $MB.refreshTable( 'userTable' );
        } );
    }

}

function getAddTagData() {
    var selected = $( "#userTable" ).bootstrapTable( 'getSelections' );
    var tagIds = [];
    var userIds = [];
    var oldTagIds = [];
    $( "input[name='tagId']:checked" ).each( function () {
        tagIds.push( $( this ).val() );
    } );

    selected.forEach( (v, k) => {
        if(v !== undefined) {
            userIds.push( v['id'] );
            oldTagIds.push(v['tagidList']);
        }
    } );
    if(tagIds.length == 0) {
        $MB.n_warning('您没有选择任何标签！');
        return;
    }
    var data = [];
    for (let i = 0; i < userIds; i++) {
        var tmp = {};
        var newTagIds = tagIds;
        if(oldTagIds.length != 0) {
            var oldTag = oldTagIds[i];
            if(oldTag !== '' && oldTag !== null) {
                var tag = oldTag.split(",");
                tag.forEach(v=>{
                    if(newTagIds.indexOf(v) == -1) {
                        newTagIds.push(v);
                    }
                });
            }
        }
        newTagIds.sort(compare);
        tmp['userId'] = userIds[i];
        tmp['tagId'] = newTagIds.join(",") == "" ? null:newTagIds.join(",");
        data.push(tmp);
    }
    return data;
}

// 获取移除标签的数据
function getRemoveTagData() {
    var selected = $( "#userTable" ).bootstrapTable( 'getSelections' );
    var tagIds = [];
    var userIds = [];
    var oldTagIds = [];
    $( "input[name='tagId']:checked" ).each( function () {
        tagIds.push( $( this ).val() );
    } );

    selected.forEach( (v, k) => {
        if(v !== undefined) {
            userIds.push( v['id'] );
            oldTagIds.push(v['tagidList']);
        }
    } );
    if(tagIds.length == 0) {
        $MB.n_warning('您没有选择任何标签！');
        return;
    }
    var data = [];
    for (let i = 0; i < userIds; i++) {
        var tmp = {};
        var newTagIds = [];
        if(oldTagIds.length != 0) {
            var oldTag = oldTagIds[i];
            if(oldTag !== '' && oldTag !== null) {
                newTagIds = oldTag.split(",");
                for (let j = 0; j < tagIds.length; j++) {
                    var idx = newTagIds.indexOf(tagIds[j]);
                    if(idx > -1) {
                        newTagIds.splice(idx, 1);
                    }
                }
            }
        }
        newTagIds.sort(compare);
        tmp['userId'] = userIds[i];
        tmp['tagId'] = newTagIds.join(",");
        data.push(tmp);
    }
    return data;
}

function compare(val1,val2){
    return val1-val2;
}

// 修改备注
function updateRemark(id) {
    $( "#remark" ).val( '' );
    $( "#userId" ).val( id );
    $( "#remarkModal" ).modal( "show" );
}

function saveRemark() {
    $.post( "/wxOfficialUser/updateRemark", {userId: $( "#userId" ).val(), remark: $( "#remark" ).val()}, function (r) {
        if (r.code === 200) {
            $MB.n_success( "修改成功！" );
        }
        $( "#remarkModal" ).modal( 'hide' );
        $MB.refreshTable( 'userTable' );
    } );
}

// 去除标签
function removeTag() {
    operate_tag = "remove";
    $("#addTagModal").modal('show');
}

// 查看用户
function viewUser(id) {
    $.get( "/wxOfficialUser/getUserById", {userId: id}, function (r) {
        if (r.code === 200) {
            var data = r.data;
            for (key in data) {
                $( "#userForm" ).find( 'input[name="' + key + '"]' ).val(data[key]).attr("disabled", "disabled");
            }
        }
    });
    $( "#viewUser" ).modal( 'show' );
}

// 同步用户数据
$("#syncUserBtn").click(function () {
    $.post("/wxOfficialUser/requestSyncWxUser", {}, function (r) {
        if(r.code === 200) {
            $MB.n_success("开始同步中，请稍后!");
        }
    });
});