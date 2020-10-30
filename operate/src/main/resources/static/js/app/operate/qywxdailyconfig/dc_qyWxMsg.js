/**
 * 微信消息列表
 */
function getWxMsgTableData(qywxId) {
    let settings = {
        url: "/qywx/getDataListPage",
        cache: false,
        pagination: true,
        singleSelect: true,
        clickToSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function(param) {
            return {
                limit: param.limit,
                offset: param.offset
            };
        },
        columns: [
            {
                checkbox: true,
                formatter: function (value, row, index) {
                    if(row['qywxId'] == qywxId) {
                        return {
                            checked: true
                        };
                    }
                }
            },
            {
                title: '消息内容',
                field: 'textContent'
            },
            {
                title: '创建时间',
                field: 'insertDt'
            },
            {
                title: '使用天数',
                field: 'usedDays',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value !== '' && value !== null) {
                        return parseFloat(Number(value).toFixed(0));
                    }
                }
            },
            {
                title: '被引用组数',
                field: 'usedGroups',
                align: 'center'
            }
        ]
    };
    $( '#msgListDataTable').bootstrapTable('destroy').bootstrapTable(settings);
}

function hiddenPersonalContent() {
    if($('input[name="isPersonal"]:checked').val() === '1') {
        $("#materialTypeDiv").attr("style", "display:none;");
        $("#materialContentDiv").attr("style", "display:none;");
    }else {
        $("#materialTypeDiv").attr("style", "display:block;");
        $("#materialContentDiv").attr("style", "display:block;");
    }
}
// 保存数据
$("#btn_save_qywx").click(function () {
    if(qywxValid()) {
        saveData();
    }
});

// 保存数据
$("#btn_close_qywx").click(function () {
    $("#textContent1_1").html("");
});

function qywxValid() {
    var smsContent = $("#textContent1").val();
    let couponName = $("input[name='couponName']:checked").val();
    let productName = $("input[name='productName']:checked").val();
    if(productName === undefined) {
        $MB.n_warning("请选择商品名称！");
        return false;
    }
    if(couponName === undefined) {
        $MB.n_warning("请选择补贴名称！");
        return false;
    }
    if(smsContent === '') {
        $MB.n_warning("请输入文案内容！");
        return false;
    }

    if(productName === '0') {
        if(smsContent.indexOf("${商品名称}") > -1) {
            $MB.n_warning("选择'商品名称:否'，文案内容不能出现${商品名称}");
            return false;
        }
    }

    if(couponName === '0') {
        if(smsContent.indexOf("${补贴名称}") > -1) {
            $MB.n_warning("选择'补贴名称:否'，文案内容不能出现${补贴名称}");
            return false;
        }
    }

    if(productName === '1') {
        if(smsContent.indexOf("${商品名称}") == -1) {
            $MB.n_warning("选择'商品名称:是'，文案内容没有发现${商品名称}");
            return false;
        }
    }

    if(couponName === '1') {
        if(smsContent.indexOf("${补贴名称}") == -1) {
            $MB.n_warning("选择'补贴名称:是'，文案内容没有发现${补贴名称}");
            return false;
        }
    }
    return true;
}

function saveData() {
    var data = $("#wxMsgAddForm").serialize() + "&" + $("#mediaForm").serialize() + "&textContent=" + $("#textContent1").val();
    var operate = $("#btn_save_qywx").attr("name");
    if(operate === 'save') {
        $MB.confirm({
            title: '提示',
            content:  '确认保存当前记录？'
        }, function () {
            $.post("/qywx/saveData", data, function (r) {
                if(r.code === 200) {
                    $MB.n_success("保存成功！");
                    $("#wxMsgAddModal").modal('hide');
                    $("#wxMsgListModal").modal('show');
                    $("#textContent1_1").html("");
                    getWxMsgTableData();
                }
            });
        });
    }else {
        $MB.confirm({
            title: '提示',
            content:  '确认修改当前记录？'
        }, function () {
            $.post("/qywx/updateData", data, function (r) {
                if(r.code === 200) {
                    $MB.n_success("修改成功！");
                    $("#wxMsgAddModal").modal('hide');
                    $("#wxMsgListModal").modal('show');
                    getWxMsgTableData();
                }
            });
        });
    }
}

// 验证表单
function wxMsgContentValid(dom) {
    $('#wxPreview').html('').append($(dom).html()==='' ? '请输入消息内容':$(dom).html());
}

// 删除数据
function deleteDataById() {
    var selected = $("#msgListDataTable").bootstrapTable('getSelections');
    if(selected.length === 0) {
        $MB.n_warning("请先选择要删除的记录！");
        return;
    }

    $MB.confirm({
        title: '提示',
        content: '确定删除所选记录？'
    }, function () {
        $.post("/qywx/deleteDataById", {id:selected[0]['qywxId']}, function (r) {
            if(r.code === 200) {
                $MB.n_success("删除成功！");
                getWxMsgTableData();
            }
        });
    });
}

// 解除引用
function refreshDataById() {
    var selected = $("#msgListDataTable").bootstrapTable('getSelections');
    if(selected.length === 0) {
        $MB.n_warning("请先选择要解除引用的记录！");
        return;
    }

    $MB.confirm({
        title: '提示',
        content: '确定要解除所选记录？'
    }, function () {
        $.post("/qywx/refreshDataById", {id:selected[0]['qywxId']}, function (r) {
            if(r.code === 200) {
                $MB.n_success("解除成功！");
                getWxMsgTableData();
                $MB.refreshTable('userGroupTable');
            }
        });
    });
}

// 编辑数据
function editDataById() {
    var selected = $("#msgListDataTable").bootstrapTable('getSelections');
    if(selected.length === 0) {
        $MB.n_warning("请先选择要编辑的记录！");
        return;
    }
    $.get("/qywx/getDataById", {id: selected[0]['qywxId']}, function (r) {
        $("#btn_save_qywx").attr('name', 'update');
        var data = r.data;
        $("input[name='productName']:radio[value='"+data['productName']+"']").prop("checked", true);
        $("input[name='productUrl']:radio[value='"+data['productUrl']+"']").prop("checked", true);
        $("input[name='couponUrl']:radio[value='"+data['couponUrl']+"']").prop("checked", true);
        $("input[name='couponName']:radio[value='"+data['couponName']+"']").prop("checked", true);
        $("input[name='isPersonal']:radio[value='"+data['isPersonal']+"']").prop("checked", true);
        $("input[name='materialType']:radio[value='"+data['materialType']+"']").prop("checked", true);
        $("#textContent1").val(data['textContent']);
        $("#wxPreview").html(data['textContent']);
        $("input[name='materialContent']").val(data['materialContent']);
        $("input[name='qywxId']").val(data['qywxId']);
        hiddenPersonalContent();
        $("#wxMsgAddModal").modal('show');
        $("#wxMsgListModal").modal('hide');
        var textval=data['textContent'];
        var textarr=textval.split('\n');
        var val="";
        for (var i = 0; i < textarr.length; i++) {
            var obj= document.createElement("div");
            val+="<div>"+textarr[i]+"</div>";
        }
        $("#textContent1_1").html(val);
    });
}

$("#wxMsgAddModal").on('hidden.bs.modal', function () {
    $("input[name='productName']:radio").removeAttr("checked");
    $("input[name='productUrl']:radio").removeAttr("checked");
    $("input[name='couponUrl']:radio").removeAttr("checked");
    $("input[name='couponName']:radio").removeAttr("checked");
    $("input[name='materialType']:radio").removeAttr("checked");
    $("textarea[name='textContent']").val('');
    $("input[name='materialContent']").val('');
    $("input[name='qywxId']").val('');
    $("#textContent1").val('');
    $("#wxPreview").html('请输入消息内容');
    hiddenPersonalContent();
    $("#btn_save_qywx").attr('name', 'save');
});

$("#textContent1").bind('input', function () {
    var content = $(this).val();
    content=content.replace(/\r\n/g,'<br/>').replace(/\n/g,'<br/>').replace(/\s/g,' ');
    $('#wxPreview').html('').append(content);
});