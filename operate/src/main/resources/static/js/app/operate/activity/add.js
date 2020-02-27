let validator;
let validatorProduct;
let $activityAddForm = $( "#activity-add-form" );
let $activityProductAddForm = $( "#add-product-form" );

$( function () {
    validateRule();
    validateProductRule();

    //为用户推送预览tab增加事件
    //为tab页增加事件
    $("a[data-toggle='tab']").on('shown.bs.tab', function (ex) {
        // 获取已激活的标签页的名称
        var activeTab = $(ex.target).attr("href");

        //如果是推送预览界面
        if(activeTab=='#profile')
        {
            $MB.loadingDesc('show', '生成中，请稍后...');
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
                        field: 'content',
                        title: '活动推送内容示例',
                        formatter: function(value, row, index) {
                            if(value != null) {
                                let temp = value.substring(0, 20) + "&nbsp;...";
                                return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                            }else {
                                return '-';
                            }
                        }
                    }, {
                        field: 'contentNormal',
                        title: '常规推送内容示例',
                        formatter: function(value, row, index) {
                            if(value != null) {
                                let temp = value.substring(0, 20) + "&nbsp;...";
                                return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                            }else {
                                return '-';
                            }
                        }
                    }]
            };
            $("#userTable").bootstrapTable('destroy').bootstrapTable(settings);
            $.get("/activity/getActivityUserList", {headId: $( "#headId" ).val(), stage: stage},function (r) {
                if(r.code==200)
                {
                    $MB.loadingDesc('hide');
                    $("#userTable").bootstrapTable('load', r.data);
                    $("#userTable").bootstrapTable('mergeCells', {index: 0, field: 'groupName', rowspan: 4});
                    $("#userTable").bootstrapTable('mergeCells', {index: 4, field: 'groupName', rowspan: 4});
                    $("#userTable").bootstrapTable('mergeCells', {index: 0, field: 'groupUserCnt', rowspan: 4});
                    $("#userTable").bootstrapTable('mergeCells', {index: 4, field: 'groupUserCnt', rowspan: 4});
                    $("#userTable").bootstrapTable('mergeCells', {index: 0, field: 'inGrowthPath', rowspan: 3});
                    $("#userTable").bootstrapTable('mergeCells', {index: 4, field: 'inGrowthPath', rowspan: 3});
                    $("a[data-toggle='tooltip']").tooltip();
                }else
                {
                    ex.preventDefault();
                    $MB.loadingDesc('hide');
                    //提示
                    $MB.n_warning(r.msg);
                }
            });
        }
    });

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
            hasPreheat: {
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
            hasPreheat: {
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
                required: true,
                number: true
            },
            formalPrice: {
                required: true,
                number: true
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
            productAttr: {
                required: icon + "请选择活动属性"
            },
            productUrl: {
                required: icon + "请输入商品链接"
            }
        }
    } );
}


// 验证时间是否合法
function validPreheatAndFormalDate() {
    let preheatEndDt = $( "#preheatEndDt" ).val();
    let formalStartDt = $( "#formalStartDt" ).val();
    return new Date( String( formalStartDt ) ) > new Date( String( preheatEndDt ) );
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
                field: 'inGrowthPath',
                title: '成长节点与活动期',
                valign: "middle",
                align: 'center'
            }, {
                field: 'activeLevel',
                title: '活跃度',
                valign: "middle",
                align: 'center'
            }, {
                field: 'smsTemplateContent',
                title: '活动推送内容',
                formatter: function (value, row, index) {
                    if(value != null) {
                        let temp = value.substring(0, 20) + "&nbsp;...";
                        return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                    }else {
                        return '-';
                    }
                }
            }, {
                field: 'smsTemplateContentNormal',
                title: '常规推送内容',
                formatter: function (value, row, index) {
                    if(value != null) {
                        let temp = value.substring(0, 20) + "&nbsp;...";
                        return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                    }else {
                        return '-';
                    }
                }
            },{
                field: 'smsTemplateContent',
                title: '选择模板',
                align: "center",
                formatter: function(value, row, index) {
                    // 没有配置模板信息是图标，否则是短信内容的截取串
                    if(value === '' || value === null) {
                        return '<a onclick="getTemplateTable('+row.groupId+')" class="text-center" data-toggle="tooltip" data-html="true" data-original-title="尚未配置消息模板！" style="color:grey;"><i class="fa fa-envelope"></i></a>';
                    }else {
                        return '<a onclick="getTemplateTable('+row.groupId+')" class="text-center" style="color: #409eff;"><i class="fa fa-envelope"></i></a>';
                    }
                }
            }]
    };
    $("#userGroupTable").bootstrapTable('destroy').bootstrapTable(settings);
    $.get("/activity/getActivityUserGroupList", {headId: $( "#headId" ).val(), stage: stage},function (r) {
        $("#userGroupTable").bootstrapTable('load', r);
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'groupName', rowspan: 4});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 4, field: 'groupName', rowspan: 4});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 0, field: 'inGrowthPath', rowspan: 3});
        $("#userGroupTable").bootstrapTable('mergeCells', {index: 4, field: 'inGrowthPath', rowspan: 3});
        $("a[data-toggle='tooltip']").tooltip();
    });
}

$( "#btn_download" ).click( function () {
    window.location.href = "/activity/downloadFile";
} );

// 上一步
function prevStep() {
    let operateType = $( "#operateType" ).val();
    if (operateType == 'save') {
        $( "#operateType" ).val( "saveAndUpdate" );
        $("#stageName").html('').append("创建计划");
        // 表单禁用
        $("input[name='activityName']").attr("disabled", "disabled");
        $("input[name='hasPreheat']").attr("disabled", "disabled");
        $("input[name='preheatStartDt']").attr("disabled", "disabled");
        $("input[name='preheatEndDt']").attr("disabled", "disabled");
        $("input[name='formalStartDt']").attr("disabled", "disabled");
        $("input[name='formalEndDt']").attr("disabled", "disabled");
    }
    if (operateType == 'update') {
        $("#stageName").html('').append("修改计划");
    }
    if (operateType == 'saveAndUpdate') {
        $("#stageName").html('').append("创建计划");
    }
    step.setActive( 0 );
    $( "#step1" ).attr( "style", "display: block;" );
    $( "#step2" ).attr( "style", "display: none;" );
}

// 添加活动商品
$( "#saveActivityProduct" ).click( function () {
    let operateType = $("#operateType").val();
    let validator = $activityProductAddForm.validate();
    let flag = validator.form();
    if (flag) {
        let operate = $( "#saveActivityProduct" ).attr( "name" );
        if (operate === "save") {
            $.post( "/activity/saveActivityProduct", $( "#add-product-form" ).serialize() + "&headId=" +
                $( "#headId" ).val() + "&activityStage=" + $( "#activity_stage" ).val() + "&operateType=" + operateType,  function (r) {
                if (r.code === 200) {
                    $MB.n_success( "添加商品成功！" );
                    $MB.closeAndRestModal( "addProductModal" );
                    $MB.refreshTable( 'activityProductTable' );
                } else {
                    $MB.n_danger( r.msg );
                }
            } );
        }
        if (operate === 'update') {
            $.post( "/activity/updateActivityProduct", $( "#add-product-form" ).serialize() + "&headId=" +
                $( "#headId" ).val() + "&activityStage=" + $( "#activity_stage" ).val() + "&operateType=" + operateType, function (r) {
                if (r.code === 200) {
                    $MB.n_success( "更新商品成功！" );
                    $MB.closeAndRestModal( "addProductModal" );
                    $MB.refreshTable( 'activityProductTable' );
                } else {
                    $MB.n_danger(r.msg);
                }
            } );
        }
    }
} );

$( "#addProductModal" ).on( "hidden.bs.modal", function () {
    $( "input[name='productId']" ).val( "" ).removeAttr("disabled");
    $( "input[name='productName']" ).val( "" );
    $( "input[name='minPrice']" ).val( "" );
    $( "input[name='formalPrice']" ).val( "" );
    $( "input[name='activityIntensity']" ).val( "" );
    $( "select[name='productAttr']" ).find( "option:selected" ).removeAttr( "selected" );
    $( "input[name='productUrl']" ).val( "" );
    $( "input[name='skuCode']" ).val( "" );
    $activityProductAddForm.validate().resetForm();
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
            $( "input[name='productId']" ).val( data['productId'] ).attr("disabled", "disabled");
            $( "input[name='productName']" ).val( data['productName'] );
            $( "input[name='skuCode']" ).val( data['skuCode'] );
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



$( "#uploadFile" ).change( function () {
    $( '#filename' ).text( "文件名:" + $( this ).val() ).attr( "style", "display:inline-block;" );
    $( "#btn_upload" ).attr( "style", "display:inline-block;" );
} );

$('#btn_upload').click(function () {
    let file = $("#uploadFile")[0].files[0];
    let formData = new FormData();
    formData.append("file", file);
    formData.append("headId", $("#headId").val());
    formData.append("stage", $("#activity_stage").val());
    formData.append("operateType", $("#operateType").val());
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
                $MB.n_success(res.msg);
            }else {
                $MB.n_danger(res['msg']);
            }
            $("#uploadFile").val('');
            $("#btn_upload").attr("style", "display:none;");
            $("#filename").html('').attr("style", "display:none;");
        },
        error: function (err) {
            $MB.n_danger("未知错误发生！");
            $("#btn_upload").attr("style", "display:none;");
            $("#filename").html('').attr("style", "display:none;");
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
                title: '活动推送内容模板',
                formatter: function (value, row, index) {
                    let temp = value.substring(0, 20) + "&nbsp;...";
                    return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                }
            }, {
                field: 'contentNormal',
                title: '常规推送内容模板',
                formatter: function (value, row, index) {
                    let temp = value.substring(0, 20) + "&nbsp;...";
                    return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
                }
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
            $("a[data-toggle='tooltip']").tooltip();
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
    $.get("/activity/updateGroupTemplate", {
            groupId: groupId, code: templateCode, headId: $("#headId").val(), stage: $("#activity_stage").val(), operateType: $("#operateType").val()
        }, function (r) {
        if(r.code === 200) {
            $MB.n_success("更新消息模板成功！");
            // $("#userGroupTable").bootstrapTable('destroy');
            getUserGroupTable($('#activity_stage').val());
            $MB.closeAndRestModal('smsTemplateModal');
        }else {
            $MB.n_danger("未知错误！");
        }
    });
});

// 提交计划
function submitActivity() {
    if(!$("#push_ok").is(":checked")) {
        $MB.n_warning("请在\"推送预览\"面板对待推送的样例文案先进行预览确认。");
        return;
    }
    let headId = $("#headId").val();
    let stage = $("#activity_stage").val();

    // 验证短信模板是否已经配置
    $.get("/activity/validSubmit", {headId: $("#headId").val(), stage: $("#activity_stage").val()}, function (r) {
        if(r.code === 200) {
            $MB.confirm({
                title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
                content: '确认提交计划？'
            }, function () {
                $.post("/activity/submitActivity", {headId: headId, stage: stage, operateType: $("#operateType").val()}, function (r) {
                    if(r.code === 200) {
                        $MB.n_success("提交计划成功！");
                        setTimeout(function () {
                            window.location.href = "/page/activity";
                        },1500);
                    }else {
                        $MB.n_danger("提交计划失败！");
                    }
                });
            });
        }else {
            $MB.n_warning(r.msg);
        }
    });
}

// 删除商品，同时更改头表数据状态
$("#btn_delete_shop").click(function () {
    var selected = $("#activityProductTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要删除的商品记录！');
        return;
    }
    var headId = $("#headId").val();
    var stage = $("#activity_stage").val();
    var productIds = [];
    selected.forEach((v, k)=>{
        productIds.push(v['productId']);
    });

    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除选中的商品记录？'
    }, function () {
        $.post("/activity/deleteProduct", {headId:headId, stage: stage, productIds: productIds.join(","), operateType: $("#operateType").val()}, function (r) {
            if(r.code === 200) {
                $MB.n_success("删除成功！");
                $MB.refreshTable('activityProductTable');
            }else {
                $MB.n_danger("删除失败！");
            }
        });
    });
});

////////////////////////////////////////////////////////////////////////////////////////////

