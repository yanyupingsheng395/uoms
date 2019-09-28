var ctx = "/";
$(function () {
    setUsername();
    var $logTableForm = $(".push-table-form");
    var settings = {
        url: ctx + "push/list",
        pageSize: 10,
        singleSelect: true,
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: params.offset / params.limit + 1,
                timeField: $logTableForm.find("input[name='timeField']").val().trim(),
                username: $logTableForm.find("select[name='username']").find("option:selected").val(),
                operation: $logTableForm.find("input[name='operation']").val().trim()
            };
        },
        columns: [{
            checkbox: true
        },
            {
                field: 'username',
                title: '操作用户'
            }, {
                field: 'operation',
                title: '描述'
            }, {
                field: 'time',
                title: '耗时（毫秒）'
            }, {
                field: 'ip',
                title: 'IP地址'
            }, {
                field: 'location',
                title: '操作地点'
            }, {
                field: 'createTime',
                title: '操作时间'
            }
        ]
    };

    $MB.initTable('logTable', settings);
    $MB.calenders('input[name="timeField"]', true, false);
});
function setUsername() {
    var code = "<option value=''>所有</option>";
    $.get("/user/findAllUser", {}, function (r) {
        $.each(r.data, function (k, v) {
            code += "<option value='" + v["USERNAME"] + "'>" + v["USERNAME"] + "</option>";
        });
        $("#username").html("").append(code);
    });
}

function searchLog() {
    $MB.refreshTable('logTable');
}

function refreshLog() {
    $(".push-table-form")[0].reset();
    searchLog();
}

function deleteLogs() {
    var selected = $("#logTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要删除的日志！');
        return;
    }
    var ids = "";
    for (var i = 0; i < selected_length; i++) {
        ids += selected[i].id;
        if (i !== (selected_length - 1)) ids += ",";
    }

    $MB.confirm({
        title: "<i class='mdi mdi-alert-circle-outline'></i>提示：",
        content: "确定删除选中的日志？"
    }, function () {
        $.post(ctx + 'push/delete', {"ids": ids}, function (r) {
            if (r.code === 0) {
                $MB.n_success(r.msg);
                refreshLog();
            } else {
                $MB.n_danger(r.msg);
            }
        });
    });
}

function exportLogExcel() {
    $.post(ctx + "push/excel", $(".push-table-form").serialize(), function (r) {
        if (r.code === 200) {
            window.location.href = "common/download?fileName=" + r.msg + "&delete=" + true;
        } else {
            $MB.n_warning(r.msg);
        }
    });
}

function exportLogCsv() {
    $.post(ctx + "push/csv", $(".push-table-form").serialize(), function (r) {
        if (r.code === 200) {
            window.location.href = "common/download?fileName=" + r.msg + "&delete=" + true;
        } else {
            $MB.n_warning(r.msg);
        }
    });
}