var validator;
$( function () {
    initTable();
    let clipboard1 = new ClipboardJS( '.copy_btn' );
    clipboard1.on( 'success', function (e) {
        $MB.n_success( "成功复制到粘贴板！" );
    } );
} );

/**
 * 获取渠道活码数据
 */
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
                param: {qstate: $( "#qstate" ).val()}
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

/**
 * 重置
 */
function resetQuery() {
    $( "#qstate" ).val( "" );
    $MB.refreshTable( "contactWayTable" );
}

/**
 * 搜索
 */
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

/**
 * 删除渠道活码
 */
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
