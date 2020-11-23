var validator;
var $contactWayForm = $( "#contactWay_edit" );
var modifyUrlValidator;
var userSelect = $contactWayForm.find( "select[name='userSelect']" );
var $usersList = $contactWayForm.find( "input[name='usersList']" );
$( function () {
    validateRule();
    modifyUrlValidateRule();
    initTable();

    let clipboard1 = new ClipboardJS( '.copy_btn' );
    clipboard1.on( 'success', function (e) {
        $MB.n_success( "成功复制到粘贴板！" );
    } );
} );

function initTable() {
    let settings = {
        url: '/contactWay/getList',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {state: $( "#state" ).val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'qrCode',
            title: '二维码样式',
            align: 'center',
            formatter: function (value, row, index) {
                return "<img style='width:120px;height:120px' src='" + value + "'><a href='/contactWay/download?configId=" + row.configId + "' style='font-size: 12px;' target='_blank'>下载</a>" +
                    "&nbsp;&nbsp;<a style='font-size: 12px;cursor: pointer;' data-clipboard-text='" + value + "' class='copy_btn'>复制二维码地址</a>";
            }
        }, {
            field: 'shortUrl',
            title: '海报短链接',
            align: 'center',
            formatter: function (value, row, indx) {
                return "<a href=http://" + value + " target='_blank'>" + value + "</a>";
            }
        }, {
            field: 'usersList',
            title: '可联系成员',
            align: 'center',
            width: 240,
            formatter: function (value, row, indx) {
                let result = '';
                if (null != value && value !== '') {
                    let arr = value.split( ',' );
                    for (let i = 0; i < arr.length; i++) {
                        if (i >= 1) {
                            result += "&nbsp;&nbsp;<span><i class='mdi mdi-account' style='color:#383838'></i>" + arr[i] + "</span>";
                        } else {
                            result += "<span><i class='mdi mdi-account' style='color:#383838'></i>" + arr[i] + "</span>";
                        }
                    }
                }

                let deptStr = row['deptList'];
                if (null != deptStr) {
                    var deptArr = deptStr.split( ',' );
                    for (let i = 0; i < deptArr.length; i++) {
                        if (i >= 1) {
                            result += "&nbsp;&nbsp;<span><i class='mdi mdi-home' style='color:#383838'></i>" + deptArr[i] + "</span>";
                        } else {
                            result += "&nbsp;&nbsp;<span><i class='mdi mdi-home' style='color:#383838'></i>" + deptArr[i] + "</span>";
                        }
                    }
                }
                return result;
            }
        }, {
            field: 'contactType',
            title: '二维码类型',
            align: 'center',
            formatter: function (value, row, indx) {
                if (value === '1') {
                    return "<span class=\"badge bg-info\">单人</span>";
                } else if (value === '2') {
                    return "<span class=\"badge bg-warning\">多人</span>";
                }
            }
        }, {
            field: 'state',
            title: '渠道',
            align: 'center'
        }, {
            field: 'externalUserNum',
            title: '添加客户人数',
            align: 'center',
            visible: false
        }, {
            field: 'createDt',
            title: '创建时间',
            align: 'center'
        }, {
            field: 'contactWayId',
            title: 'contactWayId',
            visible: false
        }, {
            field: 'configId',
            title: 'configId',
            visible: false
        }]
    };
    $MB.initTable( 'contactWayTable', settings );
}

function resetQuery() {
    $( "#state" ).val( "" );
    $MB.refreshTable( "contactWayTable" );
}

$( "#btn_query" ).click( function () {
    $MB.refreshTable( "contactWayTable" );
} );

/**
 * 编辑按钮
 */
$( "#btn_edit" ).click( function () {
    $( "#qrCodeDiv" ).show();
    $( "#btn_save" ).attr( "name", "update" );
    let selected = $( "#contactWayTable" ).bootstrapTable( 'getSelections' );
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择需要编辑的渠道活码！' );
        return;
    }
    createUserTree();
    let contactWayId = selected[0].contactWayId;
    //获取数据 并进行填充
    $.post( "/contactWay/getContactWayById", {"contactWayId": contactWayId}, function (r) {
        if (r.code === 200) {
            var $form = $( '#contactWay_edit' );
            let d = r.data;
            $( "#myLargeModalLabel" ).html( '修改渠道活码' );
            $form.find( "input[name='contactWayId']" ).val( d.contactWayId );
            $form.find( "input[name='configId']" ).val( d.configId );
            $form.find( "input[name='state']" ).val( d.state ).attr( "readOnly", "readOnly" );
            $form.find( "input[name='usersList']" ).val(d.usersList);
            $form.find( "input[name='deptList']" ).val(d.deptList);
            $form.find( "input[name='shortUrl']" ).val( d.shortUrl );
            var nodes = $('#userSelectTree').jstree(true);
            nodes.uncheck_all();
            checkTree(d.usersList);
            checkTree(d.deptList);
        } else {
            $MB.n_danger( r['msg'] );
        }
    } );
} );

$( "#add_modal" ).on( 'hidden.bs.modal', function () {
    var nodes = $('#userSelectTree').jstree(true);
    nodes.uncheck_all();
    nodes.close_all();
});

function checkTree(ids) {
    if(ids !== '' && ids !== null) {
        var idArr = ids.split(",");
        $('#userSelectTree').jstree('select_node', idArr, true);
        $('#userSelectTree').jstree().close_all();
    }
}

/**
 * 新增按钮
 */
$( "#btn_add" ).click( function () {
    $( "#myLargeModalLabel" ).html( '新增渠道活码' );
    $( "#state" ).removeAttr( "readOnly" );
    $( "#qrCodeDiv" ).hide();
    $( "#longUrl" ).val( "" );
    $( "#shortUrl" ).val( "" );
    createUserTree();
} );

$( "#btn_delete" ).click( function () {
    var selected = $( "#contactWayTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;

    if (!selected_length) {
        $MB.n_warning( '请选择需要删除的渠道活码！' );
        return;
    }
    let configId = selected[0].configId;

    $MB.confirm( {
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定删除选中的渠道活码?"
    }, function () {
        $.post( '/contactWay/delete', {"configId": configId}, function (r) {
            if (r.code === 200) {
                $MB.n_success( r.msg );
                $MB.refreshTable( "contactWayTable" );
            } else {
                $MB.n_danger( r.msg );
            }
        } );
    } );
} );

$( "#add_modal" ).on( 'hidden.bs.modal', function () {
    //执行一些清空操作
    $( "#btn_save" ).attr( "name", "save" );
    $contactWayForm.validate().resetForm();
    $contactWayForm.find( "input[name='contactWayId']" ).val( "" );
    $contactWayForm.find( "input[name='configId']" ).val( "" );
    $contactWayForm.find( "input[name='state']" ).val( "" );
    $contactWayForm.find( "input[name='usersList']" ).val( "" );
    $contactWayForm.find( "input[name='deptList']" ).val( "" );
    $contactWayForm.find( "input[name='validUser']" ).val( "" );
} );

$( "#btn_save" ).click( function () {
    var name = $( this ).attr( "name" );
    getDeptAndUserId();
    var validator = $contactWayForm.validate();
    var flag = validator.form();
    if (flag) {
        //打开遮罩层
        $MB.loadingDesc( 'show', '保存中，请稍候...' );
        if (name === "save") {
            $.post( "/contactWay/save", $( "#contactWay_edit" ).serialize(), function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success( r.msg );
                    $MB.refreshTable( "contactWayTable" );
                } else {
                    $MB.n_danger( r.msg );
                }
                ;
                $MB.loadingDesc( 'hide' );
            } );
        }
        if (name === "update") {
            $.post( "/contactWay/update", $( "#contactWay_edit" ).serialize(), function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success( r.msg );
                    $MB.refreshTable( "contactWayTable" );
                } else {
                    $MB.n_danger( r.msg );
                }
            } );
            $MB.loadingDesc( 'hide' );
        }
    }
} );

// 表单验证规则
function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $contactWayForm.validate( {
        rules: {
            state: {
                required: true,
                maxlength: 30
            },
            validUser: {
                required: true
            }
        },
        errorPlacement: function (error, element) {
            if (element.is( ":checkbox" ) || element.is( ":radio" )) {
                error.appendTo( element.parent().parent() );
            } else {
                error.insertAfter( element );
            }
        },
        messages: {
            state: {
                required: icon + "请输入渠道",
                maxlength: icon + "最大长度不能超过30个字符"
            },
            validUser: {
                required: icon + "请选择可联系成员"
            }
        }
    } );
}

// 表单验证规则
function modifyUrlValidateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    modifyUrlValidator = $( "#modifyurlForm" ).validate( {
        rules: {
            shortUrl: {
                required: true,
                maxlength: 15
            }
        },
        errorPlacement: function (error, element) {
            if (element.is( ":checkbox" ) || element.is( ":radio" )) {
                error.appendTo( element.parent().parent() );
            } else {
                error.insertAfter( element );
            }
        },
        messages: {
            shortUrl: {
                required: icon + "短链不能为空",
                maxlength: icon + "最大长度不能超过15个字符"
            }
        }
    } );
}

function createUserTree() {
    $.post( "/contactWay/getDeptAndUserTree", {}, function (r) {
        var data = r.data;
        if (r.code === 200) {
            $( '#userSelectTree' ).jstree( {
                "core": {
                    'data': data.children
                },
                "state": {
                    "disabled": true
                },
                "checkbox": {
                    "three_state": false
                },
                "plugins": ["wholerow", "checkbox"]
            } );
            $( '#add_modal' ).modal( 'show' );
        } else {
            $MB.n_warning( r.msg );
        }
    } );
}

function getDeptAndUserId() {
    var checkedNodes = $( '#userSelectTree' ).jstree( true ).get_checked( true );
    var userIds = checkedNodes.filter( (v, k) => v.original['type'] === 'user' ).map( (v, k) => v['id'] );
    var deptIds = checkedNodes.filter( (v, k) => v.original['type'] === 'dept' ).map( (v, k) => v['id'] );
    $( "input[name='usersList']" ).val( userIds.join( "," ) );
    $( "input[name='deptList']" ).val( deptIds.join( "," ) );
    $( "input[name='validUser']" ).val( userIds.join( "," ) + deptIds.join( "," ));

}

function closeModal() {
    $MB.closeAndRestModal( "add_modal" );
}

/**
 * 获取短链
 */
function getShortUrl() {
    var url = $( "#longUrl" ).val();
    if (url.trim() == "") {
        $MB.n_warning( "长链不能为空！" );
        return;
    }

    var Expression = /http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/;
    var objExp = new RegExp( Expression );
    if (objExp.test( url ) != true) {
        $MB.n_warning( "长链格式错误！" );
        return;
    }
    $.get( "/coupon/getShortUrl", {url: url}, function (r) {
        if (r.code === 200) {
            $( "#shortUrl" ).val( r.data );
            //给出提示
            $MB.n_success( "生成短链成功!" );
        } else {
            $MB.n_danger( r['msg'] );
        }
    } );
}