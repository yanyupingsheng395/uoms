let validator;
let validatorProduct;
let $activityAddForm = $( "#activity-add-form" );
let $activityProductAddForm = $( "#add-product-form" );
let product_id;

init_date_begin( 'preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd', 0, 2, 0 );
init_date_end( 'preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd', 0, 2, 0 );
init_date_begin( 'formalStartDt', 'formalEndDt', 'yyyy-mm-dd', 0, 2, 0 );
init_date_end( 'formalStartDt', 'formalEndDt', 'yyyy-mm-dd', 0, 2, 0 );


$( function () {
    validateRule();
    validateProductRule();
} );

let step = steps( {
    el: "#step",
    data: [
        {title: "设置活动", description: ""},
        {title: "活动策略", description: ""}
    ],
    space: 140,
    center: true,
    active: 0,
    dataOrder: ["line", "title", "description"]
} );

// 验证创建活动的表单
function validateRule() {
    let icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $activityAddForm.validate( {
        rules: {
            activityName: {
                required: true
            },
            preheatStartDt: {
                required: true
            },
            preheatEndDt: {
                required: true
            },
            formalStartDt: {
                required: true
            },
            formalEndDt: {
                required: true
            },
            activityStage: {
                required: true
            }
        },
        errorPlacement: function (error, element) {
            if (element.is( ":checkbox" ) || element.is( ":radio" )) {
                error.appendTo( element.parent().parent() );
            } else {
                error.insertAfter( element );
            }
        },
        messages: {
            activityName: {
                required: icon + "请输入活动名称"
            },
            preheatStartDt: {
                required: icon + "请输入预热开始日期"
            },
            preheatEndDt: {
                required: icon + "请输入预热结束日期"
            },
            formalStartDt: {
                required: icon + "请输入正式开始日期"
            },
            formalEndDt: {
                required: icon + "请输入正式结束日期"
            },
            activityStage: {
                required: icon + "请选择是否预售"
            }
        }
    } );
}

// 验证商品表
function validateProductRule() {
    let icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validatorProduct = $activityProductAddForm.validate( {
        rules: {
            productId: {
                required: true
            },
            productName: {
                required: true
            },
            minPrice: {
                required: true
            },
            formalPrice: {
                required: true
            },
            activityIntensity: {
                required: true
            },
            productAttr: {
                required: true
            },
            productUrl: {
                required: true
            }
        },
        errorPlacement: function (error, element) {
            if (element.is( ":checkbox" ) || element.is( ":radio" )) {
                error.appendTo( element.parent().parent() );
            } else {
                error.insertAfter( element );
            }
        },
        messages: {
            productId: {
                required: icon + "请输入商品ID"
            },
            productName: {
                required: icon + "请输入商品名称"
            },
            minPrice: {
                required: icon + "请输入活动期间最低价"
            },
            formalPrice: {
                required: icon + "请输入非活动正常价"
            },
            activityIntensity: {
                required: icon + "请输入活动力度"
            },
            productAttr: {
                required: icon + "请选择活动属性"
            },
            productUrl: {
                required: icon + "请输入商品链接"
            }
        }
    } );
}

// 是否有预售选中事件
$( "input[name='hasPreheat']" ).click( function () {
    let stage = $( this ).val();
    if (stage == 1) { // 是
        $( "#preheatDiv" ).attr( "style", "display:block;" );
        $( "#btn_preheat" ).attr( "style", "display:inline-block;" );

        if ($( "#preheatDiv" ).html() == '') {
            // language=HTML
            $( "#preheatDiv" ).html( '' ).append(
                "               <div class=\"col-md-6\">\n                    " +
                "                   <div class=\"form-group\">\n" +
                "                        <label class=\"col-md-3 control-label\">预热开始时间</label>\n" +
                "                        <div class=\"col-md-9\">\n" +
                "                            <input type=\"text\" name=\"preheatStartDt\" id=\"preheatStartDt\" class=\"form-control\"/>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"col-md-6\">\n" +
                "                    <div class=\"form-group\">\n" +
                "                        <label class=\"col-md-3 control-label\">预热结束日期</label>\n" +
                "                        <div class=\"col-md-9\">\n" +
                "                            <input class=\"form-control\" id=\"preheatEndDt\" name=\"preheatEndDt\"/>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>" );
        }
        init_date_begin( 'preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd', 0, 2, 0 );
        init_date_end( 'preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd', 0, 2, 0 );
    }
    if (stage == 0) { // 否
        $( "#preheatDiv" ).attr( "style", "display:none;" );
        $( "#btn_preheat" ).attr( "style", "display:none;" );
        // 通过移除页面元素去除验证
        $( "#preheatDiv" ).html( '' );
    }
} );


// 保存活动信息
function saveDailyHead(stage) {
    let operateType = $( "#operateType" ).val();
    let msg = "";
    if (operateType === "save") {
        msg = "执行下一步会保存活动信息?";
    } else {
        msg = "执行下一步会更新活动信息?";
    }
    if (operate_type === 'save') {
        $MB.confirm( {
            title: "<i class='mdi mdi-alert-circle-outline'></i>提示：",
            content: msg
        }, function () {
            $.post( "/activity/saveActivityHead", $activityAddForm.serialize(), function (r) {
                if (r.code === 200) {
                    step.setActive( 1 );
                    $( "#step1" ).attr( "style", "display: none;" );
                    $( "#step2" ).attr( "style", "display: block;" );
                    $MB.n_success( r.msg );
                    $( "#headId" ).val( r.data );
                    getUserGroupTable( stage );
                } else {
                    $MB.n_danger( "有错误发生！" )
                }
            } );
        } );
    } else {
        step.setActive( 1 );
        $( "#step1" ).attr( "style", "display: none;" );
        $( "#step2" ).attr( "style", "display: block;" );
        getUserGroupTable( stage );
    }
}

// 第二步
function step2(stage) {
    let validator = $activityAddForm.validate();
    let flag = validator.form();
    if (flag) {
        saveDailyHead(stage);
        // 获取商品数据
        getProductInfo( stage );
        // 获取用户信息
        getUserTable();
    }
}

// 验证时间是否合法
function validPreheatAndFormalDate() {
    let preheatEndDt = $( "#preheatEndDt" ).val();
    let formalStartDt = $( "#formalStartDt" ).val();
    return new Date( String( formalStartDt ) ) > new Date( String( preheatEndDt ) );
}

// 下一步
// param:stage 活动阶段
function nextStep(stage) {
    // 页面保存
    $( "#activity_stage" ).val( stage );
    let validator = $activityAddForm.validate();
    let flag = validator.form();
    if ($( "input[name='activityStage']:checked" ).val() == '1') {
        let dateFlag = validPreheatAndFormalDate();
        if (!dateFlag) {
            $MB.n_warning( "正式阶段的开始时间必须大于预热阶段的结束时间！" );
            return;
        }
    }
    if (flag) {
        step2( stage );
    }
}

// 第二步获取产品数据
function getProductInfo(stage) {
    var settings = {
        url: '/activity/getActivityProductPage',
        pagination: true,
        singleSelect: false,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    headId: $( "#headId" ).val(),
                    productId: $( "#productId" ).val(),
                    productName: $( "#productName" ).val(),
                    productAttr: $( "#productAttr" ).val(),
                    stage: stage
                }
            };
        },
        columns: [
            {
                checkbox: true
            },
            {
                field: 'productId',
                title: '商品ID'
            }, {
                field: 'productName',
                title: '名称'
            }, {
                field: 'minPrice',
                title: '活动期间最低价（元）'
            }, {
                field: 'formalPrice',
                title: '非活动日常单价（元）'
            }, {
                field: 'activityIntensity',
                title: '活动力度（%）'
            }, {
                field: 'productAttr',
                title: '活动属性',
                formatter: function (value, row, index) {
                    let res = '-';
                    switch (value) {
                        case "0":
                            res = "主推";
                            break;
                        case "1":
                            res = "参活";
                            break;
                        case "2":
                            res = "正常";
                            break;
                    }
                    return res;
                }
            }, {
                field: 'productUrl',
                title: '商品链接'
            }]
    };
    $MB.initTable( 'activityProductTable', settings );
}

// 查询商品信息
function searchActivityProduct() {
    $MB.refreshTable( 'activityProductTable' );
}

// 重置查询条件
function resetActivityProduct() {
    $( "#productId" ).val( "" );
    $( "#productName" ).val( "" );
    $( "#productAttr" ).find( "option:selected" ).removeAttr( "selected" );
    $MB.refreshTable( 'activityProductTable' );
}

// 获取用户群组列表
function getUserGroupTable(stage) {
    var settings = {
        columns: [
            {
                field: 'groupName',
                title: '用户与商品关系',
                valign: "middle",
                align: 'center'
            }, {
                field: 'groupUserCnt',
                title: '人数（人）',
                valign: "middle",
                align: 'center'
            }, {
                field: 'inGrowthPath',
                title: '成长节点与活动期',
                valign: "middle",
                align: 'center'
            }, {
                field: 'growthUserCnt',
                title: '人数（人）',
                valign: "middle",
                align: 'center'
            }, {
                field: 'activeLevel',
                title: '活跃度',
                valign: "middle",
                align: 'center'
            }, {
                field: 'activeUserCnt',
                title: '人数（人）',
                valign: "middle",
                align: 'center'
            }, {
                field: 'smsTemplateContent',
                title: '选择模板',
                align: "center",
                formatter: function(value, row, index) {
                    // 没有配置模板信息是图标，否则是短信内容的截取串
                    if(value === '' || value === null) {
                        return '<a onclick="getTemplateTable('+row.groupId+')" class="text-center" data-toggle="tooltip" data-html="true" data-original-title="尚未配置消息模板！"><i class="fa fa-envelope"></i></a>';
                    }else {
                        return '<a onclick="getTemplateTable('+row.groupId+')" class="text-center" data-toggle="tooltip" data-html="true" data-original-title="'+value+'"><i class="fa fa-envelope"></i></a>';
                    }
                }
            }]
    };
    $("#userGroupTable").bootstrapTable(settings);
    $.get("/activity/getActivityUserGroupList", {headId: $( "#headId" ).val(), stage: stage},function (r) {
        $("#userGroupTable").bootstrapTable('load', r);
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'groupName', rowspan: 4});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'groupUserCnt', rowspan: 4});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'inGrowthPath', rowspan: 3});
        $("a[data-toggle='tooltip']").tooltip();
    });
}

// 获取用户列表
function getUserTable() {
    let stage = $("#activity_stage").val();
    var settings = {
        columns: [
            {
                field: 'groupName',
                title: '用户与商品关系',
                valign: 'middle',
                align: 'center'
            }, {
                field: 'inGrowthPath',
                title: '成长节点与活动期',
                valign: 'middle',
                align: 'center'
            }, {
                field: 'activeLevel',
                title: '活跃度',
                valign: 'middle',
                align: 'center'
            }, {
                field: 'smsTemplateContent',
                title: '模板示例'
            }]
    };
    $("#userTable").bootstrapTable(settings);
    $.get("/activity/getActivityUserList", {headId: $( "#headId" ).val(), stage: stage},function (r) {
        $("#userTable").bootstrapTable('load', r);
        $("#userTable").bootstrapTable('mergeCells', {index: 0, field: 'groupName', rowspan: 4});
        $("#userTable").bootstrapTable('mergeCells', {index: 0, field: 'inGrowthPath', rowspan: 3});
    });
}

$( "#btn_download" ).click( function () {
    window.location.href = "/activity/downloadFile";
} );

// 上一步
function prevStep() {
    $( "#operateType" ).val( "update" );
    step.setActive( 0 );
    $( "#step1" ).attr( "style", "display: block;" );
    $( "#step2" ).attr( "style", "display: none;" );
}

// 添加活动商品
$( "#saveActivityProduct" ).click( function () {
    let validator = $activityProductAddForm.validate();
    let flag = validator.form();
    if (flag) {
        let operate = $( "#saveActivityProduct" ).attr( "name" );
        if (operate === "save") {
            $.post( "/activity/saveActivityProduct", $( "#add-product-form" ).serialize() + "&headId=" +
                $( "#headId" ).val() + "&activityStage=" + $( "#activity_stage" ).val(), function (r) {
                if (r.code === 200) {
                    $MB.n_success( "添加商品成功！" );
                    $MB.closeAndRestModal( "addProductModal" );
                    $MB.refreshTable( 'activityProductTable' );
                } else {
                    $MB.n_danger( "添加商品失败！" );
                }
            } );
        }
        if (operate === 'update') {
            $.post( "/activity/updateActivityProduct", $( "#add-product-form" ).serialize() + "&headId=" +
                $( "#headId" ).val() + "&activityStage=" + $( "#activity_stage" ).val(), function (r) {
                if (r.code === 200) {
                    $MB.n_success( "更新商品成功！" );
                    $MB.closeAndRestModal( "addProductModal" );
                    $MB.refreshTable( 'activityProductTable' );
                } else {
                    $MB.n_danger( "更新商品失败！" );
                }
            } );
        }
    }
} );

$( "#addProductModal" ).on( "hidden.bs.modal", function () {
    $( "input[name='productId']" ).val( "" );
    $( "input[name='productName']" ).val( "" );
    $( "input[name='minPrice']" ).val( "" );
    $( "input[name='formalPrice']" ).val( "" );
    $( "input[name='activityIntensity']" ).val( "" );
    $( "select[name='productAttr']" ).find( "option:selected" ).removeAttr( "selected" );
    $( "input[name='productUrl']" ).val( "" );
} );

// 修改商品信息
$( "#btn_edit_shop" ).click( function () {
    $("#modalLabel").html('').append('修改商品');
    $( "#saveActivityProduct" ).attr( "name", "update" );
    let selected = $( "#activityProductTable" ).bootstrapTable( 'getSelections' );
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择需要编辑的商品！' );
        return;
    }
    let id = selected[0].id;
    $.get( "/activity/getProductById", {id: id}, function (r) {
        if (r.code === 200) {
            let data = r.data;
            $( "input[name='id']" ).val( data['id'] );
            $( "input[name='productId']" ).val( data['productId'] );
            $( "input[name='productName']" ).val( data['productName'] );
            $( "input[name='minPrice']" ).val( data['minPrice'] );
            $( "input[name='formalPrice']" ).val( data['formalPrice'] );
            $( "input[name='activityIntensity']" ).val( data['activityIntensity'] );
            $( "select[name='productAttr']" ).val( data['productAttr'] );
            $( "input[name='productUrl']" ).val( data['productUrl'] );
            $( "#addProductModal" ).modal( 'show' );
        } else {
            $MB.n_danger( "获取信息失败！" );
        }
    } );
} );

$( "#btn_add_shop" ).click( function () {
    $( "#saveActivityProduct" ).attr( "name", "save" );
    $( '#addProductModal' ).modal( 'show' );
    $("#modalLabel").html('').append('添加商品');
} );

$( "#uploadFile" ).change( function () {
    $( '#filename' ).text( "文件名:" + $( this ).val() );
    $( "#btn_upload" ).attr( "style", "display:inline-block;" );
} );

$('#btn_upload').click(function () {
    let file = $("#uploadFile")[0].files[0];
    let formData = new FormData();
    formData.append("file", file);
    formData.append("headId", $("#headId").val());
    formData.append("stage", $("#activity_stage").val());
    $.ajax({
        url: "/activity/uploadExcel",
        type: "post",
        data: formData,
        cache: false,
        processData: false,
        contentType: false,
        success: function (res) {
            if(res.code === 200) {
                $MB.refreshTable('activityProductTable');
                $MB.n_success("文件上传成功！");
                $("#btn_upload").attr("style", "display:none;");
                $("#filename").html('').attr("style", "display:none;");
            }else {
                $MB.n_danger(res['msg']);
            }
        },
        error: function (err) {
            $MB.n_danger("未知错误发生！");
        }
    });
});

// 获取消息模板列表
function getTemplateTable(groupId) {
    var settings = {
        singleSelect: true,
        columns: [
            {
                checkbox: true
            },
            {
                field: 'code',
                title: '模板编码'
            }, {
                field: 'content',
                title: '模板内容'
            }, {
                field: 'remark',
                title: '备注'
            }]
    };
    $("#templateDataTable").bootstrapTable(settings);
    $.get("/activity/getTemplateTableData", {},function (r) {
        if(r.code === 200) {
            $("#selectGroupId").val(groupId);
            $("#templateDataTable").bootstrapTable('load', r.data);
            $("#smsTemplateModal").modal('show');
        }else {
            $MB.n_danger("获取模板数据异常！");
        }
    });
}

$("#smsTemplateModal").on('hidden.bs.modal', function () {
    $("#selectGroupId").val('');
});

$("#template-add-btn").click(function () {
    let groupId = $("#selectGroupId").val();
    var selected = $("#templateDataTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择消息模板！');
        return;
    }
    var templateCode = selected[0].code;
    $.get("/activity/updateGroupTemplate", {groupId: groupId, code: templateCode}, function (r) {
        if(r.code === 200) {
            $MB.n_success("更新消息模板成功！");
            $("#userGroupTable").bootstrapTable('destroy');
            getUserGroupTable($('#activity_stage').val());
            $MB.closeAndRestModal('smsTemplateModal');
        }else {
            $MB.n_danger("未知错误！");
        }
    });
});

// 提交计划
function submitActivity() {
    let headId = $("#headId").val();
    let stage = $("#activity_stage").val();
    $.post("/activity/submitActivity", {headId: headId, stage: stage}, function (r) {
        if(r.code === 200) {
            $MB.n_success("提交计划成功！");
            setTimeout(function () {
                window.location.href = "/page/activity";
            },2400);
        }else {
            $MB.n_danger("提交计划失败！");
        }
    });
}

// 预览推送
$("#btn_view_shop").click(function () {
    let stage = $("#activity_stage").val();
    var selected = $("#activityProductTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要预览的商品！');
        return;
    }
    var code = "<span style='float: left;'>当前选中商品：</span>";
    selected.forEach((v, k) => {
        code += "<span class='tag'><span>"+v['productName']+"</span></span>";
    });
    $("#productTag").html('').append(code);
    var settings = {
        columns: [
            {
                field: 'groupName',
                title: '用户与商品关系',
                valign: "middle",
                align: 'center'
            }, {
                field: 'groupUserCnt',
                title: '人数（人）',
                valign: "middle",
                align: 'center'
            }, {
                field: 'inGrowthPath',
                title: '成长节点与活动期',
                valign: "middle",
                align: 'center'
            }, {
                field: 'growthUserCnt',
                title: '人数（人）',
                valign: "middle",
                align: 'center'
            }, {
                field: 'activeLevel',
                title: '活跃度',
                valign: "middle",
                align: 'center'
            }, {
                field: 'activeUserCnt',
                title: '人数（人）',
                valign: "middle",
                align: 'center'
            }]
    };
    $("#viewUserGroupTable").bootstrapTable(settings);
    $.get("/activity/getActivityUserGroupList", {headId: $( "#headId" ).val(), stage: stage},function (r) {
        $("#viewUserGroupTable").bootstrapTable('load', r);
        $("#viewUserGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'groupName', rowspan: 4});
        $("#viewUserGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'groupUserCnt', rowspan: 4});
        $("#viewUserGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'inGrowthPath', rowspan: 3});
        $("a[data-toggle='tooltip']").tooltip();

        $("#viewUserGroupModal").modal('show');
    });
});