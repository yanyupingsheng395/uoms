$(function () {
    var settings = {
        url: "/diag/list",
        method: 'post',
        cache: false,
        pagination: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1,
                param: {diagName: $("input[name='diagName']").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'diagName',
            title: '名称'
        }, {
            field: 'periodType',
            title: '周期',
            formatter: function (value, row, index) {
                if (value == "M") {
                    return "月";
                }
                if (value == "D") {
                    return "日";
                }
            }
        }, {
            title: '周期时间',
            formatter: function (value, row, index) {
                return row.beginDt + "至" + row.endDt;
            }
        }, {
            field: 'dimDisplayName',
            title: '维度&值',
            formatter: function (value, row, index) {
                if(value == null) {
                    return "-";
                }else {
                    if(value.length >= 10) {
                        var newVal = value.substr(0, 10) + "...";
                        var title = value.replace(";", ";&nbsp;&nbsp;");
                        return "<a style='color: #000000;border-bottom: 1px solid' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-placement=\"bottom\" data-original-title=\""+title+"\">"+newVal+"</a>";
                    }else if(value.length < 10 && value.length > 0) {
                        return value;
                    }
                    if(value == "" || value == undefined) {
                        return "-";
                    }
                }
            }
        }, {
            field: 'createDt',
            title: '创建时间'
        }],onLoadSuccess: function(data){
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $MB.initTable('diagTable', settings);
});

function viewDiag() {
    var selected = $("#diagTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要查看的诊断！');
        return;
    }
    if (selected_length > 1) {
        $MB.n_warning('一次只能查看一个诊断！');
        return;
    }
    var id = selected[0]["diagId"];
    window.location.href = "/page/diagnosis/view?id=" + id;
}

function editDiag() {
    var selected = $("#diagTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要查看的诊断！');
        return;
    }
    if (selected_length > 1) {
        $MB.n_warning('一次只能编辑一个诊断！');
        return;
    }
    var id = selected[0]["diagId"];
    window.location.href = "/page/diagnosis/edit?id=" + id;
}

/**
 * 删除确认
 * @param id
 */
function deleteDiag() {
    var selected = $("#diagTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要删除的诊断！');
        return;
    }

    var ids = "";
    for (var i = 0; i < selected_length; i++) {
        ids += selected[i].diagId;
        if (i !== (selected_length - 1)) ids += ",";
    }
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定删除选中的诊断?"
    }, function () {
        deleteData(ids);
    });
}

function deleteData(id) {
    $.post("/diag/deleteById", {id: id}, function (r) {
        if(r.code == 200) {
            $MB.n_success(r.msg);
        }else {
            $MB.n_danger(r.msg);
        }
        $('#diagTable').bootstrapTable('refresh');
    });
}

function searchDiag() {
    $MB.refreshTable('diagTable');
}

function resetDiag() {
    $("input[name='diagName']").val('');
    $MB.refreshTable('diagTable');
}