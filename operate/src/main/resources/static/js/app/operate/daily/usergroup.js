$(function () {
    initTable();
});

function initTable() {
    var settings = {
        url: '/daily/userGroupListPage',
        pagination: true,
        singleSelect: false,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'groupId',
            title: '组ID',
        }, {
            field: 'groupName',
            title: '组名称'
        }, {
            field: 'smsCode',
            title: '模板ID'
        }, {
            field: 'smsContent',
            title: '模板内容'
        }, {
            field: 'isCoupon',
            title: '是否有券',
            formatter: function (value, row, index) {
                if (value == '1') {
                    return '是';
                }
                if (value == '0') {
                    return '否';
                }
                return '';
            }
        }, {
            title: '操作',
            formatter: function (value, row, index) {
                return '<a onclick="editSmsContent(\'' + row['groupId'] + '\', \'' + row['groupName'] + '\')" style="color: #0f0f0f"><i class="fa fa-edit"></i></a>';
            }
        }]
    };
    $MB.initTable('dailyGroupTable', settings);
}

function editSmsContent(groupId, groupName) {
    $("#groupName").show();
    $("#currentGroupName").text(groupName);
    $("#msg_modal").modal('show');
    smsTemplateTable();
    $("#currentGroupId").val(groupId);
}

/**
 * 获取短信模板列表
 */
function smsTemplateTable() {
    var settings = {
        url: "/smsTemplate/list",
        method: 'post',
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,  //页码
                param: {smsCode: $("input[name='smsCode']").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'smsCode',
            title: '模板编码',
            visible: false
        }, {
            field: 'smsContent',
            title: '模板内容'
        }]
    };
    $("#smsTemplateTable").bootstrapTable('destroy');
    $MB.initTable('smsTemplateTable', settings);
}

function setSmsCode() {
    var selected = $("#smsTemplateTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择短信模板！');
        return;
    }
    var smsCode = selected[0].smsCode;
    var groupId = $("#currentGroupId").val();
    $.get('/daily/setSmsCode', {groupId: groupId, smsCode: smsCode}, function (r) {
        if (r.code === 200) {
            $MB.n_success("更改短信模板成功！");
        } else {
            $MB.n_danger("更改短信模板失败！发生未知异常！");
        }
        $("#msg_modal").modal('hide');
        $MB.refreshTable('dailyGroupTable');
    });
}

function batchUpdateTemplate() {
    $("#groupName").hide();
    var selected = $("#dailyGroupTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要编辑模板的组！');
        return;
    }
    let groupIds = new Array();
    selected.forEach((v, k)=>{
        groupIds.push(v.groupId);
    });
    $("#currentGroupId").val(groupIds.join(","));
    $("#msg_modal").modal('show');
    smsTemplateTable();
}