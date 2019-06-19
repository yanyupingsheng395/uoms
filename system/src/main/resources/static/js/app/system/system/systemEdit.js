function updateSystem() {
    var selected = $("#systemTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要修改的系统！');
        return;
    }
    if (selected_length > 1) {
        $MB.n_warning('一次只能修改一个系统！');
        return;
    }
    var id = selected[0].id;
    $.post(ctx + "system/getSystem", {"id": id}, function (r) {
        if (r.code === 0) {
            var $form = $('#system-add');
            $form.modal();
            var system = r.msg;
            $("#system-add-modal-title").html('修改系统');
            $form.find("input[name='name']").val(system.name);
            $form.find("input[name='oldName']").val(system.name);
            $form.find("input[name='id']").val(system.id);
            $form.find("input[name='remark']").val(system.remark);
            $form.find("input[name='domain']").val(system.domain);
            $("#system-add-button").attr("name", "update");
        } else {
            $MB.n_danger(r.msg);
        }
    });
}