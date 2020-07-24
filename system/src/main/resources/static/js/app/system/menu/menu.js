$(function () {
    initTreeTable();
});

var ctx = "/";

function initTreeTable() {
    $MB.loading('show');
    var $menuTableForm = $("#menu-table-form");
    var setting = {
        id: 'menuId',
        code: 'menuId',
        url: ctx + 'menu/list',
        expandAll: false,
        expandColumn: "1",
        singleSelect: true,
        ajaxParams: {
            menuName: $menuTableForm.find("input[name='qmenuName']").val() == null ? null : $menuTableForm.find("input[name='qmenuName']").val().trim(),
            type: $menuTableForm.find("select[name='type']").find("option:selected").val(),
            sysCode: $menuTableForm.find("select[name='systemId']").find("option:selected").val()
        },
        columns: [
            {
                field: 'selectItem',
                checkbox: true
            },
            {
                title: '名称',
                field: 'menuName'
            },{
                title: '显示排序',
                field: 'orderNum'
            },
            {
                title: '系统',
                field: 'sysName'
            },
            {
                title: '图标',
                field: 'icon',
                formatter: function (item, index) {
                    return '<i class="zmdi ' + item.icon + '"></i>';
                }
            },
            {
                title: '类型',
                field: 'type',
                formatter: function (item, index) {
                    if (item.type === '0') return '<span class="badge bg-success">菜单</span>';
                    if (item.type === '1') return '<span class="badge bg-warning">按钮</span>';
                }

            },
            {
                title: '地址',
                field: 'url',
                formatter: function (item, index) {
                    return item.url === 'null' ? '' : item.url;
                }
            },
            {
                title: '权限标识',
                field: 'perms',
                formatter: function (item, index) {
                    return item.perms === 'null' ? '' : item.perms;
                }
            },
            {
                title: '创建时间',
                field: 'createDt'
            }
        ]
    };
    $MB.initTreeTable('menuTable', setting);
    $MB.loading('hide');

    createMenuTree();
    getSystem();
}

function getSystem() {
    $.get(ctx + "system/findAllSystem", {}, function (r) {
        var options = "<option value=''>所有</option>";
        $(r.msg).each(function (k, v) {
            options += "<option value='"+v.code+"'>"+v.name+"</option>";
        });
        $("#systemId").html("").html(options);

        //绑定新增页面的选择列表
        $("#sysCode").html("").html(options);
        $("#sysCode").selectpicker("refresh");
    });
}

function searchMenu() {
    initTreeTable();
}

function refreshMenu() {
    initTreeTable();
    $MB.refreshJsTree("menuTree", createMenuTree());
    $("input[name='menuName']").val("");
    $("select[name='type']").find("option:selected").removeAttr("selected");
}

function deleteMenus() {
    var ids = $("#menuTable").bootstrapTreeTable("getSelections");
    var ids_arr = "";
    if (!ids.length) {
        $MB.n_warning("请勾选需要删除的菜单或按钮！");
        return;
    }
    for (var i = 0; i < ids.length; i++) {
        ids_arr += ids[i].id;
        if (i !== (ids.length - 1)) ids_arr += ",";
    }
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定删除选中菜单或按钮？"
    }, function () {
        $.post(ctx + 'menu/delete', {"ids": ids_arr}, function (r) {
            if (r.code === 200) {
                $MB.n_success(r.msg);
                refreshMenu();
            } else {
                $MB.n_danger(r.msg);
            }
        });
    });
}
