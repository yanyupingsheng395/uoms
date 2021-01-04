var validator;
var modifyUrlValidator;
$( function () {
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
    var selected = $( "#contactWayTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择需要编辑的渠道活码！' );
        return;
    }
    var contactWayId = selected[0].contactWayId;
    window.location.href="/page/contactWay/add?contactWayId="+contactWayId;
})


/**
 * 新增按钮
 */
$( "#btn_add" ).click( function () {
    window.location.href="/page/contactWay/add";
} );

$( "#btn_delete" ).click( function () {
    var selected = $( "#contactWayTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;

    if (!selected_length) {
        $MB.n_warning( '请选择需要删除的渠道活码！' );
        return;
    }
    var configId = selected[0].configId;
    var contactWayId = selected[0].contactWayId;

    $MB.confirm( {
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定删除选中的渠道活码?"
    }, function () {
        $.post( '/contactWay/delete', {"configId": configId,"contactWayId":contactWayId}, function (r) {
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
    clearData();
} );

$( "#btn_close" ).click( function () {
    clearData();
})

/**
 * 清空数据
 */
function clearData(){
    userData=null;
    deptData=null;
    Single=false;
    $("#alllist").html("");
    $( "#btn_save" ).attr( "name", "save" );
    dept_list=[];
    user_list=[];
    $contactWayForm.validate().resetForm();
    $contactWayForm.find( "input[name='contactWayId']" ).val( "" );
    $contactWayForm.find( "input[name='configId']" ).val( "" );
    $contactWayForm.find( "input[name='state']" ).val( "" );
    $contactWayForm.find( "input[name='usersList']" ).val( "" );
    $contactWayForm.find( "input[name='deptList']" ).val( "" );
    $contactWayForm.find( "input[name='validUser']" ).val( "" );
}

$( "#btn_save" ).click( function () {
    var name = $( this ).attr( "name" );
    getDeptAndUserId();
    var validator = $contactWayForm.validate();
    var flag = validator.form();
    if(Single){
        if(user_list.length>1){
            $MB.n_danger( "该渠道活吗初始类型是单人，不能添加多人！");
            return;
        }
    }
    if (flag) {
        //打开遮罩层
        $MB.loadingDesc( 'show', '保存中，请稍候...' );
        if (name === "save") {
            $.post( "/contactWay/save", $( "#contactWay_edit" ).serialize(), function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success( r.msg );
                    $MB.refreshTable( "contactWayTable" );
                    clearData();
                } else {
                    $MB.n_danger( r.msg );
                };
                $MB.loadingDesc( 'hide' );
            } );
        }
        if (name === "update") {
            $.post( "/contactWay/update", $( "#contactWay_edit" ).serialize(), function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success( r.msg );
                    $MB.refreshTable( "contactWayTable" );
                    clearData();
                } else {
                    $MB.n_danger( r.msg );
                }
            } );
            $MB.loadingDesc( 'hide' );
        }
    }
} );



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