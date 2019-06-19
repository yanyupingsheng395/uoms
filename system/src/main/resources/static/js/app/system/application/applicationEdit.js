function updateApplication() {
    var selected = $("#applicationTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要修改的应用！');
        return;
    }
    if (selected_length > 1) {
        $MB.n_warning('一次只能修改一个应用！');
        return;
    }
    var id = selected[0].applicationId;
    $.post(ctx + "application/getApplication", {"id": id}, function (r) {
        if (r.code === 200) {
            var $form = $('#application-add');
            $form.modal();
            var application = r.msg;
            $("#system-add-modal-title").html('修改应用');
            $form.find("input[name='applicationName']").val(application.applicationName);
            $form.find("input[name='oldApplicationName']").val(application.applicationName);
            $form.find("input[name='applicationId']").val(application.applicationId);
            $form.find("input[name='remark']").val(application.remark);
            $form.find("input[name='domain']").val(application.domain);
            $("input[name='enableFlag']:radio[value='" + application.enableFlag + "']").prop("checked", true);
            $("#application-add-button").attr("name", "update");
        } else {
            $MB.n_danger(r.msg);
        }
    });
}