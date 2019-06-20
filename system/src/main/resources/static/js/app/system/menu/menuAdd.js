var validator;
var $menuAddForm = $("#menu-add-form");
var $menuName = $menuAddForm.find("input[name='menuName']");
var $icon = $menuAddForm.find("input[name='icon']");
var $icon_drop = $menuAddForm.find("div.icon-drop");
var $menuOrderNumRow = $menuAddForm.find(".menu-order-num-row");
var $menuUrlListRow = $menuAddForm.find(".menu-url-list-row");
var $menuIconListRow = $menuAddForm.find(".menu-icon-list-row");
var $menuTree = $menuAddForm.find(".menu-tree");

$(function () {
    $icon_drop.hide();
    validateRule();
    createMenuTree();
    getSystemInfo();
    getApplication();

    $menuAddForm.find("input[name='type']").change(function () {
        var $value = $menuAddForm.find("input[name='type']:checked").val();
        if ($value === "0") {
            $menuName.parent().prev().text("菜单名称：");
            $menuUrlListRow.show();
            $menuIconListRow.show();
            $menuOrderNumRow.show();
        } else {
            $menuName.parent().prev().text("按钮名称：");
            $menuUrlListRow.hide();
            $menuIconListRow.hide();
            $menuOrderNumRow.show();

        }
    });

    $menuAddForm.find("input[name='icon']").focus(function () {
        $icon_drop.show();
    });

    $("#menu-add").click(function (event) {
        var obj = event.srcElement || event.target;
        if (!$(obj).is("input[name='icon']")) {
            $icon_drop.hide();
        }
    });

    $icon_drop.find(".menu-icon .col-sm-1").on("click", function () {
        var icon = "mdi " + $(this).find("i").attr("class").split(" ")[1];
        $icon.val(icon);
    });

    $("#menu-add .btn-save").click(function () {
        var name = $(this).attr("name");
        var flag = validator.form();
        if (flag) {
            getMenu();
            validator = $menuAddForm.validate();
            if (name === "save") {
                $.post(ctx + "menu/add", $menuAddForm.serialize(), function (r) {
                    if (r.code === 200) {
                        refreshMenu();
                        closeModal();
                        $MB.n_success(r.msg);
                    } else $MB.n_danger(r.msg);
                });
            }
            if (name === "update") {
                $.post(ctx + "menu/update", $menuAddForm.serialize(), function (r) {
                    if (r.code === 200) {
                        refreshMenu();
                        closeModal();
                        $MB.n_success(r.msg);
                    } else $MB.n_danger(r.msg);
                });
            }
        }
    });

    $("#menu-add .btn-close").click(function () {
        $("input:radio[value='0']").trigger("click");
        closeModal();
    });

});

// 获取所有业务系统
function getSystemInfo() {
    $.get(ctx + "system/findAllSystem", {}, function (r) {
        var options = "<option value=''>请选择</option>";
        $(r.msg).each(function (k, v) {
            options += "<option value='"+v.id+"'>"+v.name+"</option>";
        });
        $("#sysId").html("").html(options);
        $("#sysId").selectpicker("refresh");
    });
}

// 获取应用
function getApplication() {
    $.get(ctx + "application/findAllApplication", {}, function (r) {
        var options = "<option value=''>请选择</option>";
        $(r.msg).each(function (k, v) {
            options += "<option value='"+v.applicationId+"'>"+v.applicationName+"</option>";
        });
        $("#appId").html("").html(options);
        $("#appId").selectpicker("refresh");
    });
}

function closeModal() {
    clearMenuAddForm();
}

function clearMenuAddForm() {
    $menuName.parent().prev().text("菜单名称：");
    $("#menu-add-modal-title").html('新增菜单/按钮');
    $("#menu-add-button").attr("name", "save");
    $menuUrlListRow.show();
    $menuIconListRow.show();
    $menuOrderNumRow.show();
    validator.resetForm();
    $MB.closeAndRestModal("menu-add");
    // $MB.refreshJsTree("menuTree", createMenuTree());

    $("#menu-add-form")[0].reset();
    $("#sysId").selectpicker('val', '');
    $("input[name='system']").val("");
}

$('#menu-add').on('hidden.bs.modal', function () {
    clearMenuAddForm();
});

$('#menu-add').on('shown.bs.modal', function () {
    if($(".btn-save").attr("name") === 'update' && currentMenuId != null && currentMenuParentId != null) {
        $("#menuTree").jstree('select_node', currentMenuParentId, true);
        $("#menuTree").jstree('disable_node', currentMenuId);
    }
});


function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $menuAddForm.validate({
        rules: {
            menuName: {
                required: true,
                minlength: 2,
                maxlength: 10,
                remote: {
                    url: "menu/checkMenuName",
                    type: "get",
                    dataType: "json",
                    data: {
                        menuName: function () {
                            return $("input[name='menuName']").val().trim();
                        },
                        oldMenuName: function () {
                            return $("input[name='oldMenuName']").val().trim();
                        },
                        type: function () {
                            return $("input[name='type']").val();
                        }
                    }
                }
            },
            system: {required: true},
            appName: {required: true},
            orderNum: {required: true, digits: true}
        },
        messages: {
            menuName: {
                required: icon + "请输入名称",
                minlength: icon + "名称长度2到10个字符",
                remote: icon + "该名称已经存在"
            },
            system: {
                required: icon + "请选择业务系统"
            },
            appName: {
                required: icon + "请选择应用"
            },
            orderNum: {required: icon + "请输入显示排序", digits: icon + "只能输入0或正整数"}
        }
    });

}

$('#sysId').on('changed.bs.select',function(e){
    var sysId = $(this).val();
    $("input[name='system']").val(sysId);
    $MB.loading('show');
    $.jstree.destroy();
    createMenuTree(sysId);
});

$('#appId').on('changed.bs.select',function(e){
    var appId = $(this).val();
    $("input[name='appName']").val(appId);
});

function createMenuTree(sysId) {
    $.post(ctx + "menu/tree", {sysId: sysId}, function (r) {
        if (r.code === 200) {
            var data = r.msg;
            $('#menuTree').jstree({
                "core": {
                    'data': data.children,
                    'multiple': false
                },
                "state": {
                    "disabled": true
                },
                "checkbox": {
                    "three_state": false
                },
                "plugins": ["wholerow", "checkbox"]
            });

            if(data.children.length != 0) {
                $menuTree.attr("style", "display:block;");
            }else {
                $menuTree.attr("style", "display:none;");
            }
        } else {
            $MB.n_danger(r.msg);
        }
        $MB.loading('hide');
    });
}

function getMenu() {
    var ref = $('#menuTree').jstree(true);
    $("[name='parentId']").val(ref.get_checked()[0]);
}