/**
 * 微信消息列表
 */
function getWxMsgTableData() {
    let settings = {
        url: "/qywx/getDataListPage",
        cache: false,
        pagination: true,
        singleSelect: true,
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
                checkbox: true
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
                field: 'usedDays'
            }
        ]
    };
    $( '#msgListDataTable').bootstrapTable('destroy').bootstrapTable(settings);
}

function hiddenPersonalContent() {
    // $('input[name="materialType"]:checked').val();
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
    saveData();
});
function saveData() {
    var data = $("#wxMsgAddForm").serialize() + "&" + $("#mediaForm").serialize();
    $.post("/qywx/saveData", data, function (r) {
        if(r.code === 200) {
            $MB.n_success("保存成功！");
            $("#wxMsgAddModal").modal('hide');
            $("#wxMsgListModal").modal('show');
            getWxMsgTableData();
        }
    });
}

// 验证表单
function wxMsgContentValid() {

}

// 删除数据
function deleteDataById() {
    var selected = $("#msgListDataTable").bootstrapTable('getSelections');
    if(selected.length === 0) {
        $MB.n_warning("请先选择要删除的记录！");
        return;
    }
    $.post("/qywx/deleteDataById", {id:selected[0]['qywxId']}, function (r) {
        if(r.code === 200) {
            $MB.n_success("删除成功！");
            getWxMsgTableData();
        }
    });
}