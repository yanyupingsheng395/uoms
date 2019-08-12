/**
 * 获取所有群组表，未被选择的群组浅色标记，且不可编辑短信模板
 */
function getGroupDataList() {
    var settings = {
        url: '/daily/getGroupDataList',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  //页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {headId: headId}
            };
        },
        columns: [[{
            field: 'groupName',
            title: '成长群组',
            rowspan: 2,
            valign:"middle"
        }, {
            title: '群组描述',
            colspan: 3
        }, {
            field: 'smsContent',
            title: '选择消息模板',
            rowspan: 2,
            valign: 'middle',
            formatter: function (value, row, idx) {
                var temp = value.substring(0, 20) + "...";
                if(row['isCheck'] == '1') {
                    return '<a onclick="editSmsContent(\''+row['groupId']+'\', \'' + row['groupName'] + '\')" style=\'color: #000000;border-bottom: 1px solid;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '"><i class="fa fa-edit"></i>'+temp+'</a>';
                }else {
                    return '<a style=\'color: #CDC9C9;border-bottom: 1px solid\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">'+temp+'</a>';
                }
            }
        },{
            field: 'isCheck',
            title: '是否已选',
            visible: false
        }],[{
            field: 'userValue',
            title: '用户价值',
            formatter: function (value,row,index) {
                switch (value) {
                    case "ULC_01":
                        res = "重要";
                        break;
                    case "ULC_02":
                        res = "主要";
                        break;
                    case "ULC_03":
                        res = "普通";
                        break;
                    case "ULC_04":
                        res = "长尾";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }}, {
            field: 'isNew',
            title: '生命周期阶段',
            formatter:function (value, row, index) {
                if(value == "1") {
                    return "非新手期";
                }else {
                    return "新手期";
                }
            }
        }, {
            field: 'pathMulti',
            title: '品牌广度（SPU数）',
            formatter:function (value, row, index) {
                if(value == '1') {
                    return "大于1";
                }else {
                    return "1";
                }
            }
        }]],onLoadSuccess: function () {
            $("#groupTable").find("tr").find("td").addClass("text-center");
            $("a[data-toggle='tooltip']").tooltip();

            setTrColor();
        }
    };
    $('#groupTable').bootstrapTable('destroy');
    $MB.initTable('groupTable', settings);
}

/**
 * 根据是否已选，设置行的颜色
 */
function setTrColor() {
    var tableId = document.getElementById("groupTable");
    var allData = $("#groupTable").bootstrapTable('getData');
    allData.forEach(function(value, index, arr) {
        if(value.isCheck == '0') {
            tableId.rows[index + 2].setAttribute("style","color: #CDC9C9;");
        }
    });
}

/**
 * 选择短信模板
 * @param groupId
 */
function editSmsContent(groupId, groupName) {
    $("#currentGroupName").text(groupName);
    $("#msg_modal").modal('show');
    smsTemplateTable();
    $("#currentGroupId").val(groupId);
}

function setSmsCode() {

    var selected = $("#smsTemplateTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    var contain = false;
    if (!selected_length) {
        $MB.n_warning('请选择消息模板！');
        return;
    }
    var smsCode = selected[0].smsCode;
    var groupId = $("#currentGroupId").val();
    $.get('/daily/setSmsCode', {groupId: groupId, smsCode: smsCode, headId: headId}, function (r) {
        if(r.code === 200) {
            $MB.n_success("设置消息模板成功！");
        }else {
            $MB.n_danger("设置消息模板失败！发生未知异常！");
        }
        $("#msg_modal").modal('hide');
        $MB.refreshTable('groupTable');
    });
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
                pageNum: (params.offset / params.limit )+ 1,  //页码
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

/**
 * 启动群组推送
 */
function submitData() {
    $("#btn_push").attr("disabled", true);
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定启动推送群组?"
    }, function () {
        $.get("/daily/submitData", {headId: headId}, function (r) {
            if(r.code === 200) {
                $MB.n_success("启动推送成功！");
            }else {
                $("#btn_push").attr("disabled", false);
                $MB.n_warning("数据已被其它用户修改，请查看！")
            }
            setTimeout(function () {
                window.location.href = "/page/daily";
            }, 1000);
        });
    });
}

function step1() {
    step.setActive(0);
    $("#step1").attr("style", "display: block;");
    $("#step2").attr("style", "display: none;");
}