let create_step;
$( function () {
    create_step = steps({
        el: "#addStep",
        data: [
            {title: "活动信息", description: ""},
            {title: "商品信息", description: ""},
            {title: "配置消息", description: ""}
        ],
        center: true,
        dataOrder: ["title", "line", "description"]
    });
});
$(function (){
    initDt();
    validBasic();
    validateProductRule();
});

let validatorProduct;
let $activityProductAddForm = $( "#add-product-form" );

// 验证商品表
function validateProductRule() {
    let icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validatorProduct = $activityProductAddForm.validate( {
        rules: {
            productId: {
                required: true,
                remote: {
                    url: "/activity/checkProductId",
                    type: "get",
                    dataType: "json",
                    data: {
                        activityStage: function () {
                            return CURRENT_ACTIVITY_STAGE;
                        },
                        activityType: function () {
                            return $("#activityType").val();
                        },
                        productId: function () {
                            return $("#product_id").val()
                        },
                        headId: function () {
                            return $("#headId").val()
                        }
                    }
                }
            },
            productName: {
                required: true
            },
            activityPrice: {
                required: true,
                number: true
            },
            formalPrice: {
                required: true,
                number: true
            },
            groupId: {
                required: true
            },
            discountSize: {
                required: function () {
                    return $("#activityRule").val() === '1';
                }
            },
            discountThreadhold: {
                required: function () {
                    return $("#activityRule").val() === '2';
                }
            },
            discountDeno: {
                required: function () {
                    return $("#activityRule").val() === '2';
                }
            },
            discountAmount: {
                required: function () {
                    return $("#activityRule").val() === '4' || $("#activityRule").val() === '3';
                }
            },
            activityType: {
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
                required: icon + "请输入商品ID",
                remote: icon + '已有相同商品ID'
            },
            productName: {
                required: icon + "请输入商品名称"
            },
            activityPrice: {
                required: icon + "请输入活动商品单价"
            },
            formalPrice: {
                required: icon + "请输入日常商品单价"
            },
            groupId: {
                required: icon + "请选择店铺活动机制"
            },
            discountSize: {
                required: icon + "请输入满件打折的折扣"
            },
            discountThreadhold: {
                required: icon + "请输入满元减钱门槛"
            },
            discountDeno: {
                required: icon + "请输入满元减钱面额"
            },
            discountAmount: {
                required: icon + "请输入立减金额"
            },
            activityType: {
                required: icon + "请选择利益点适用场景"
            }
        }
    } );
}


// 查询商品信息
function searchActivityProduct() {
    $MB.refreshTable( 'activityProductTable' );
}

// 重置查询条件
function resetActivityProduct() {
    $( "#productId" ).val( "" );
    $( "#productName" ).val( "" );
    $( "#activity-form" ).find( "option:selected" ).removeAttr( "selected" );
    $MB.refreshTable( 'activityProductTable' );
}

$( "#btn_download" ).click( function () {
    window.location.href = "/activity/downloadFile";
} );

// 添加活动商品
$( "#saveActivityProduct" ).click( function () {
    let operateType = $("#activity_stage").val();
    let validator = $activityProductAddForm.validate();
    let flag = validator.form();
    if (flag) {
        let operate = $( "#saveActivityProduct" ).attr( "name" );
        if (operate === "save") {
            $.post( "/activity/saveActivityProduct", $( "#add-product-form" ).serialize() + "&headId=" + $( "#headId" ).val() + "&activityStage=" + CURRENT_ACTIVITY_STAGE,  function (r) {
                if (r.code === 200) {
                    $MB.n_success( "添加商品成功！" );
                    getProductInfo();
                    $MB.closeAndRestModal( "addProductModal" );
                } else {
                    $MB.n_danger( r.msg );
                }
            } );
        }
        if (operate === 'update') {
            $.post( "/activity/updateActivityProduct", $( "#add-product-form" ).serialize() + "&headId=" +
                $( "#headId" ).val() + "&activityStage=" + CURRENT_ACTIVITY_STAGE + "&operateType=" + operateType + "&productId=" + $("#product_id").val(), function (r) {
                if (r.code === 200) {
                    $MB.n_success( "更新商品成功！" );
                    getProductInfo();
                    $MB.closeAndRestModal( "addProductModal" );
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
    $( "input[name='activityPrice']" ).val( "" );
    $( "input[name='formalPrice']" ).val( "" );
    $( "input[name='discountSize']" ).val( "" );
    $( "input[name='discountThreadhold']" ).val( "" );
    $( "input[name='discountDeno']" ).val( "" );
    $( "input[name='discountAmount']" ).val( "" );
    $( "select[name='groupId']" ).find( "option:selected" ).removeAttr( "selected" );
    $( "select[name='activityType']" ).find( "option:selected" ).removeAttr( "selected" );
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
    if(selected_length > 1) {
        $MB.n_warning( '一次只能选择一条记录修改！' );
        return;
    }
    let productId = selected[0].productId;
    $.get( "/activity/getProductById", {headId: $("#headId").val(), activityStage: CURRENT_ACTIVITY_STAGE, productId: productId}, function (r) {
        if (r.code === 200) {
            let data = r.data;
            $("#add-product-form").find( "input[name='id']" ).val( data['id'] );
            $("#add-product-form").find( "input[name='productId']" ).val( data['productId'] ).attr("disabled", "disabled");
            $("#add-product-form").find( "input[name='productName']" ).val( data['productName'] );
            $("#add-product-form").find( "input[name='activityPrice']" ).val( data['activityPrice'] );
            $("#add-product-form").find( "input[name='formalPrice']" ).val( data['formalPrice'] );
            $("#add-product-form").find("select[name='groupId']").find("option[value='"+data['groupId']+"']").prop("selected", true);
            $("#add-product-form").find("select[name='activityType']").find("option[value='"+data['activityType']+"']").prop("selected", true);
            var val = $("#activityRule").find("option:selected").val();
            switch (val) {
                case "9":
                    $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                        '<label class="col-md-4 control-label">满件打折</label>\n' +
                        '<div class="col-md-7">\n' +
                        '<input class="form-control" type="text" name="discountSize" value="'+data['discountSize']+'"/>\n' +
                        '</div></div>');
                    break;
                case "10":
                    $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                        '<label class="col-md-4 control-label">满元减钱门槛(元)</label>\n' +
                        '<div class="col-md-7">\n' +
                        '<input class="form-control" type="text" name="discountThreadhold" value="'+data['discountThreadhold']+'"/>\n' +
                        '</div></div>').append('<div class="form-group">' +
                        '<label class="col-md-4 control-label">满元减钱面额(元)</label>\n' +
                        '<div class="col-md-7">\n' +
                        '<input class="form-control" type="text" name="discountDeno" value="'+data['discountDeno']+'"/>\n' +
                        '</div></div>');
                    break;
                case "11":
                    $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                        '<label class="col-md-4 control-label">立减金额</label>\n' +
                        '<div class="col-md-7">\n' +
                        '<input class="form-control" type="text" name="discountAmount" value="'+data['discountAmount']+'"/>\n' +
                        '</div></div>');
                    break;
                case "12":
                    $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                        '<label class="col-md-4 control-label">立减金额</label>\n' +
                        '<div class="col-md-7">\n' +
                        '<input class="form-control" type="text" name="discountAmount" value="'+data['discountAmount']+'"/>\n' +
                        '</div></div>');
                    break;
                default:
                    $("#shopOffer").attr("style", "display:none;").html('');
                    break;
            }
            $( "#addProductModal" ).modal( 'show' );
        } else {
            $MB.n_danger( "获取信息失败！" );
        }
    } );
} );

$( "#uploadFile" ).change( function () {
    $( '#filename' ).text( "文件名:" + $(this)[0].files[0].name).attr( "style", "display:inline-block;" );
    $( "#btn_upload" ).attr( "style", "display:inline-block;" );
} );

$('#btn_upload').click(function () {
    var repeatProduct = $("input[name='repeatProduct']:checked").val();
    var uploadMethod = $("input[name='uploadMethod']:checked").val();
    if($("#uploadFile")[0].files[0] === undefined) {
        $MB.n_warning("请选择上传的文件！");
        return;
    }
    if(repeatProduct === undefined) {
        $MB.n_warning("请选择重复商品！");
        return;
    }
    if(uploadMethod === undefined) {
        $MB.n_warning("请选择上传模式！");
        return;
    }

    $MB.confirm({
        title: "提示",
        content: "确定上传当前文件？"
    }, function () {
        $MB.loadingDesc('show', "上传中，请稍等...");
        let file = $("#uploadFile")[0].files[0];
        let formData = new FormData();
        formData.append("file", file);
        formData.append("headId", $("#headId").val());
        formData.append("repeatProduct", repeatProduct);
        formData.append("uploadMethod", uploadMethod);
        formData.append("stage", CURRENT_ACTIVITY_STAGE);
        $.ajax({
            url: "/activity/uploadExcel",
            type: "post",
            data: formData,
            cache: false,
            processData: false,
            contentType: false,
            success: function (res) {
                if(res.code === 200) {
                    if(res.data.length === 0) {
                        $MB.n_success("商品上传成功！");
                    }else {
                        makeErrorTable(res.data);
                    }
                    $MB.refreshTable('activityProductTable');
                    $("#uploadProduct").modal('hide');
                }else {
                    $MB.n_warning(res.msg);
                }
                $MB.loadingDesc('hide');
            },
            error: function (err) {
                $MB.n_danger(err);
                $("#uploadProduct").modal('hide');
                $("#uploadFile").val('');
                $("#filename").html('').attr("style", "display:none;");
                $MB.loadingDesc('hide');
            }
        });
    });
});

function makeErrorTable(data) {
    $("#errorDataTable").bootstrapTable({
        dataType: "json",
        showHeader:true,
        columns: [{
            field: 'errorDesc',
            title: '问题描述',
            align: 'left'
        }, {
            field: 'errorRows',
            title: '出现次数',
            align: 'center'
        },{
            field: 'firstErrorRow',
            title: '首次出现位置',
            align: 'center',
            formatter: function (value, row, index) {
                if(value === 0 || value === null) {
                    return '-';
                }else {
                    return "第" + value +  "行"
                }
            }
        }]
    });
    $("#errorDataTable").bootstrapTable('load', data);
    $("#upload_error_modal").modal('show');
}

$("#uploadProduct").on('hidden.bs.modal', function () {
    $("input[name='uploadMethod']").eq(0).prop('checked', true);
    $("input[name='repeatProduct']").eq(0).prop('checked', true);
    $("#uploadFile").val('');
    $("#filename").html('').attr("style", "display:none;");
});

// 提交计划
// type:NOTIFY 活动通知，DURING 活动期间
function submitActivity(type) {
    let headId = $("#headId").val();
    var stage = $("#activity_stage").val();
    $.get("/activity/validSubmit", {headId: $("#headId").val(), stage: stage, type: type}, function (r) {
        if(r.code === 200) {
            var data = r.data;
            if(data['error'] !== null && data['error'] !== undefined) {
                $MB.n_warning(data['error']);
                return;
            }
            if(data['warn'] !== null && data['warn'] !== undefined) {
                $MB.confirm({
                    title: '提示:',
                    content: '成长商品没有活动价格的用户群组尚未配置文案，如未配置，此群组将不进行推送。确认保存？'
                }, function () {
                    if(type === 'DURING') {
                        if(stage === 'preheat') {
                            if(preheatStatus !== 'edit' && preheatStatus !== '' && preheatStatus !== null && preheatStatus !== undefined) {
                                $MB.n_success("保存计划成功！");
                            }else {
                                submitData(headId, type);
                            }
                        }else {
                            if(formalStatus !== 'edit' &&  formalStatus !== 'edit' && formalStatus !== '' && formalStatus !== null && formalStatus !== undefined) {
                                $MB.n_success("保存计划成功！");
                            }else {
                                submitData(headId, type);
                            }
                        }
                    }else {
                        if(stage === 'preheat') {
                            if(preheatNotifyStatus !== 'edit' && preheatNotifyStatus !== '' && preheatNotifyStatus !== null && preheatNotifyStatus !== undefined) {
                                $MB.n_success("保存计划成功！");
                            }else {
                                submitData(headId, type);
                            }
                        }else {
                            if(formalNotifyStatus !== 'edit' && formalNotifyStatus !== '' && formalNotifyStatus !== null && formalNotifyStatus !== undefined) {
                                $MB.n_success("保存计划成功！");
                            }else {
                                submitData(headId, type);
                            }
                        }
                    }
                });
            }else {
                $MB.confirm({
                    title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
                    content: "确认保存计划？"
                }, function () {
                    if(type === 'DURING') {
                        if(stage === 'preheat') {
                            if(preheatStatus !== 'edit' && preheatStatus !== '' && preheatStatus !== null && preheatStatus !== undefined) {
                                $MB.n_success("保存计划成功！");
                            }else {
                                submitData(headId, type);
                            }
                        }else {
                            if(formalStatus !== 'edit' &&  formalStatus !== 'edit' && formalStatus !== '' && formalStatus !== null && formalStatus !== undefined) {
                                $MB.n_success("保存计划成功！");
                            }else {
                                submitData(headId, type);
                            }
                        }
                    }else {
                        if(stage === 'preheat') {
                            if(preheatNotifyStatus !== 'edit' && preheatNotifyStatus !== '' && preheatNotifyStatus !== null && preheatNotifyStatus !== undefined) {
                                $MB.n_success("保存计划成功！");
                            }else {
                                submitData(headId, type);
                            }
                        }else {
                            if(formalNotifyStatus !== 'edit' && formalNotifyStatus !== '' && formalNotifyStatus !== null && formalNotifyStatus !== undefined) {
                                $MB.n_success("保存计划成功！");
                            }else {
                                submitData(headId, type);
                            }
                        }
                    }
                });
            }
        }else {
            $MB.n_warning(r.msg);
        }
    });
}
function submitData(headId, type) {
    $.post("/activity/submitActivity", {headId: headId, operateType: operate_type, stage: CURRENT_ACTIVITY_STAGE, type: type}, function (r) {
        if(r.code === 200) {
            $MB.n_success("保存计划成功！");
        }else {
            $MB.n_danger("保存计划失败！");
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
    var stage = CURRENT_ACTIVITY_STAGE;
    var productIds = [];
    selected.forEach((v, k)=>{
        productIds.push(v['productId']);
    });

    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除选中的商品记录？'
    }, function () {
        $.post("/activity/deleteProduct", {headId:headId, stage: stage, productIds: productIds.join(",")}, function (r) {
            if(r.code === 200) {
                $MB.n_success("删除成功！");
                $MB.refreshTable('activityProductTable');
            }else {
                $MB.n_danger("删除失败！");
            }
        });
    });
});

function getProductInfo() {
    var settings = {
        url: '/activity/getActivityProductPage',
        pagination: true,
        singleSelect: false,
        sidePagination: "server",
        clickToSelect: true,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    headId: $( "#headId" ).val(),
                    productId: $( "#productId" ).val(),
                    productName: $( "#productName" ).val(),
                    groupId: $("#groupId").find("option:selected").val(),
                    activityStage: CURRENT_ACTIVITY_STAGE
                }
            };
        },
        columns: [
            {
                checkbox: true
            },
            {
                field: 'productId',
                title: '商品ID',
                valign: 'middle',
                align: 'center'
            }, {
                field: 'productName',
                title: '名称',
                valign: 'middle',
                align: 'center'
            },{
                field: 'groupId',
                title: '店铺活动机制',
                valign: 'middle',
                align: 'center',
                formatter: function (value, row, index) {
                    var res = "-";
                    if(value === '9') {
                        res =  "满件打折";
                    }
                    if(value === '10') {
                        res =  "满元减钱";
                    }
                    if(value === '11') {
                        res =  "特价秒杀";
                    }
                    if(value === '12') {
                        res =  "预售付尾立减";
                    }
                    if(value === '13') {
                        res = '无店铺活动'
                    }
                    return res;
                }
            },{
                field: 'minPrice',
                title: '活动通知体现最<br/>低单价（元/件）',
                align: 'center',
                formatter: function (value, row, index) {
                    if(row['activityType'] === 'NOTIFY' || row['activityType'] === 'NOTIFY,DURING' || row['activityType'] === 'DURING,NOTIFY'){
                        return value;
                    }else {
                        return '-';
                    }
                }
            },{
                field: 'activityProfit',
                title: '活动通知体现利益点',
                valign: 'middle',
                align: 'center',
                formatter: function (value, row, index) {
                    if(row['activityType'] === 'NOTIFY' || row['activityType'] === 'NOTIFY,DURING' || row['activityType'] === 'DURING,NOTIFY'){
                        if(row['groupId'] == '9') {
                            return (value*10) +'折';
                        }else {
                            return value +'元';
                        }
                    }else {
                        return '-';
                    }
                }
            },  {
                field: 'minPrice',
                title: '活动期间体现最<br/>低单价（元/件）',
                align: 'center',
                formatter: function (value, row, index) {
                    if(row['activityType'] === 'DURING' || row['activityType'] === 'NOTIFY,DURING' || row['activityType'] === 'DURING,NOTIFY'){
                        return value;
                    }else {
                        return '-';
                    }
                }
            }, {
                field: 'activityProfit',
                title: '活动期间体现利益点',
                valign: 'middle',
                align: 'center',
                formatter: function (value, row, index) {
                    if(row['activityType'] === 'DURING' || row['activityType'] === 'NOTIFY,DURING' || row['activityType'] === 'DURING,NOTIFY'){
                        if(row['groupId'] == '9') {
                            return (value*10) +'折';
                        }else {
                            return value +'元';
                        }
                    }else {
                        return '-';
                    }
                }
            }, {
                field: 'checkFlag',
                title: '校验结果',
                valign: 'middle',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value === 'Y') {
                        return "<span class=\"badge bg-success\">通过</span>";
                    }
                    if(value === 'N') {
                        return "<span class=\"badge bg-danger\">未通过</span>";
                    }
                    return "-";
                }
            }, {
                field: 'checkComments',
                title: '失败原因',
                valign: 'middle',
                align: 'center'
            }]
    };
    $( "#activityProductTable" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}
let CURRENT_ACTIVITY_STAGE = 'preheat';
// 跳转到
function createActivity(stage) {
    CURRENT_ACTIVITY_STAGE = stage;
    $("#activity_stage").val(stage);
    getGroupList( stage, 'NOTIFY', 'table1');
    getGroupList( stage, 'DURING', 'table5');
    covertDataTable();
    setTitle(stage);
    // 根据不同的状态禁用相关通知的按钮
    if(stage === 'preheat') {
        if(preheatNotifyStatus === 'done' || preheatNotifyStatus === 'timeout') {
            $("#changePlan").attr("disabled", "disabled");
            $("#notifySaveBtn").attr("disabled", "disabled");
        }else {
            var data = getPlanStatus($("#headId").val(), stage);
            if(data) {
                $("#notifySaveBtn").removeAttr("disabled");
                $("#changePlan").removeAttr("disabled");
            }else {
                $("#notifySaveBtn").attr("disabled", "disabled");
                $("#changePlan").attr("disabled", "disabled");
            }
        }
        if(preheatStatus === 'done') {
            $("#duringSaveBtn").attr("disabled", "disabled");
        }else {
            $("#duringSaveBtn").removeAttr("disabled");
        }
    } else {
        if(formalNotifyStatus === 'done' || formalNotifyStatus === 'timeout') {
            $("#changePlan").attr("disabled", "disabled");
            $("#notifySaveBtn").attr("disabled", "disabled");
        }else {
            var data = getPlanStatus($("#headId").val(), stage);
            if(data) {
                $("#notifySaveBtn").removeAttr("disabled");
                $("#changePlan").removeAttr("disabled");
            }else {
                $("#notifySaveBtn").attr("disabled", "disabled");
                $("#changePlan").attr("disabled", "disabled");
            }
        }
        if(formalStatus === 'done') {
            $("#duringSaveBtn").attr("disabled", "disabled");
        }else {
            $("#duringSaveBtn").removeAttr("disabled");
        }
    }
}

// 获取plan的状态
function getPlanStatus(headId, stage) {
    var res = $.ajax({
        url: '/activityPlan/getPlanStatus',
        data: {headId: headId, stage: stage},
        async: false
    });
    var status = JSON.parse(res.responseText).data;
    if(status === '0' || status === '' || status === undefined || status === null || status === 'null') {
        return true;
    }
    return false;
}

// 根据当前选择设置标题
function setTitle(stage) {
    if(stage === 'preheat') {
        $("#notifyTimeTile").text("预计在"+timeRevert($("#preheatNotifyDt").val())+"完成对目标用户的活动通知；");
        $("#duringTimeTile").text("预计从"+timeRevert($("#preheatStartDt").val())+"至"+timeRevert($("#preheatEndDt").val())+"，对被活动通知过但是没有产生购买行为的用户进行推送；");
    }
    if(stage === 'formal') {
        $("#notifyTimeTile").text("预计在"+timeRevert($("#formalNotifyDt").val())+"完成对目标用户的活动通知；");
        $("#duringTimeTile").text("预计从"+timeRevert($("#formalStartDt").val())+"至"+timeRevert($("#formalEndDt").val())+"，对被活动通知过但是没有产生购买行为的用户进行推送；");
    }
}

// 时间转换
function timeRevert(dateStr) {
    var dateArr = dateStr.split("-");
    var year = (dateArr[0] !== undefined && dateArr[0] !== '' && dateArr[0] !== null) ? dateArr[0] : '-';
    var month = (dateArr[1] !== undefined && dateArr[1] !== '' && dateArr[1] !== null) ? dateArr[1] : '-';
    var day = (dateArr[2] !== undefined && dateArr[2] !== '' && dateArr[2] !== null) ? dateArr[2] : '-';
    return year + "年" + month + "月" + day + "日";
}

// 初始化日期控件
function initDt() {
    init_date( 'preheatNotifyDt', 'yyyy-mm-dd', 0, 2, 0 );
    init_date( 'formalNotifyDt', 'yyyy-mm-dd', 0, 2, 0 );
    init_date_begin( 'preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd', 0, 2, 0 );
    init_date_end( 'preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd', 0, 2, 0 );
    init_date_begin( 'formalStartDt', 'formalEndDt', 'yyyy-mm-dd', 0, 2, 0 );
    init_date_end( 'formalStartDt', 'formalEndDt', 'yyyy-mm-dd', 0, 2, 0 );

    var date = new Date();
    date.setDate( date.getDate() + 1 );
    $( "#preheatStartDt" ).datepicker( 'setStartDate', date );
    $( "#preheatEndDt" ).datepicker( 'setStartDate', date );
    $( "#formalStartDt" ).datepicker( 'setStartDate', date );
    $( "#formalEndDt" ).datepicker( 'setStartDate', date );
    $( "#preheatNotifyDt" ).datepicker( 'setStartDate', date );
    $( "#formalNotifyDt" ).datepicker( 'setStartDate', date );
}

$( "#btn_basic" ).click( function () {
    if(operate_type === 'save') {
        var validator = $basicAddForm.validate();
        if (validator.form() && validBasicDt()) {
            $MB.confirm( {
                title: '提示:',
                content: '确定保存信息？'
            }, function () {
                saveActivityHead();
                stepBreak(1);
            } );
        }
    }
    if(operate_type === 'update') {
        stepBreak(1);
    }
    //stepBreak(1);
});

// 点击上一步下一步按钮
function stepBreak(index) {
    if(index == 0) {
        create_step.setActive(0);
        $("#step1").attr("style", "display:block;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:none;");
    }
    if(index == 1) {
        create_step.setActive(1);
        getProductInfo();
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:block;");
        $("#step3").attr("style", "display:none;");
    }
    if(index == 2) {
        $.get("/activity/validProduct", {headId: $("#headId").val(), stage: CURRENT_ACTIVITY_STAGE}, function (r) {
            if(r.code === 200) {
                if(r.data > 0) {
                    $MB.n_warning("存在校验不通过的商品！");
                }else {
                    create_step.setActive(2);
                    $("#step1").attr("style", "display:none;");
                    $("#step2").attr("style", "display:none;");
                    $("#step3").attr("style", "display:block;");
                    createActivity(CURRENT_ACTIVITY_STAGE);
                }
            }
        });
    }
}

var basic_validator;
var $basicAddForm = $( "#basic-add-form" );
function validBasic() {
    var icon = "<i class='fa fa-close'></i> ";
    basic_validator = $basicAddForm.validate( {
        rules: {
            activityflag: {
                required: true
            },
            activityName: {
                required: true
            },
            hasPreheat: {
                required: true
            },
            preheatNotifyDt: {
                required: function () {
                    return $( "input[name='hasPreheat']:checked" ).val() === '1';
                }
            },
            preheatStartDt: {
                required: function () {
                    return $( "input[name='hasPreheat']:checked" ).val() === '1';
                }
            },
            preheatEndDt: {
                required: function () {
                    return $( "input[name='hasPreheat']:checked" ).val() === '1';
                }
            },
            formalNotifyDt: {
                required: true
            },
            formalStartDt: {
                required: true
            },
            formalEndDt: {
                required: true
            },
            platDiscount: {
                required: true
            },
            platThreshold: {
                required: function () {
                    return $( "input[name='platDiscount']:checked" ).val() === '1';
                }
            },
            platDeno: {
                required: function () {
                    return $( "input[name='platDiscount']:checked" ).val() === '1';
                }
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
            activityflag: {
                required: icon + "请选择活动类型"
            },
            activityName: {
                required: icon + "请输入活动名称"
            },
            hasPreheat: {
                required: icon + "请选择是否预售"
            },
            preheatNotifyDt: {
                required: icon + "请输入预售通知时间"
            },
            preheatStartDt: {
                required: icon + "请输入预售开始时间"
            },
            preheatEndDt: {
                required: icon + "请输入预售结束时间"
            },
            formalNotifyDt: {
                required: icon + "请输入正式通知时间"
            },
            formalStartDt: {
                required: icon + "请输入正式开始时间"
            },
            formalEndDt: {
                required: icon + "请输入正式结束时间"
            },
            platDiscount: {
                required: icon + "请选择平台优惠"
            },
            platThreshold: {
                required: icon + "请输入门槛"
            },
            platDeno: {
                required: icon + "请输入面额"
            }
        }
    } );
}

// 验证基本信息的时间
function validBasicDt() {
    var flag = true;
    var hasPreheat = $( "input[name='hasPreheat']:checked" ).val();
    let preheatStartDt = $( "#preheatStartDt" ).val();
    let preheatEndDt = $( "#preheatEndDt" ).val();
    let formalStartDt = $( "#formalStartDt" ).val();
    var preheatNotifyDt = $( "#preheatNotifyDt" ).val();
    var formalNotifyDt = $( "#formalNotifyDt" ).val();

    if (hasPreheat === '1') {
        var flag1 = new Date( String( formalStartDt ) ) > new Date( String( preheatEndDt ) );
        if (!flag1) {
            flag = false;
            $MB.n_warning( "预热结束时间必须小于正式开始时间！" );
        }
        var flag2 = new Date( String( preheatStartDt ) ) > new Date( String( preheatNotifyDt ) );
        if (!flag2) {
            flag = false;
            $MB.n_warning( "预售提醒时间必须小于预售开始时间！" );
        }
    }
    var flag3 = new Date( String( formalStartDt ) ) > new Date( String( formalNotifyDt ) );
    if (!flag3) {
        flag = false;
        $MB.n_warning( "正式提醒时间必须小于正式开始时间！" );
    }
    return flag;
}

// 是否预售 点击事件
$( "input[name='hasPreheat']" ).click( function () {
    var hasPreheat = $( this ).val();
    if (hasPreheat === '1') {
        $( "#btn_create_preheat" ).attr( "style", "display:inline;" );
        $( "#preheatDiv" ).attr( "style", "display:block;" );
    } else {
        $( "#btn_create_preheat" ).attr( "style", "display:none;" );
        $( "#preheatDiv" ).attr( "style", "display:none;" );
    }
} );

// 保存基本信息
function saveActivityHead() {
    $.post( "/activity/saveActivityHead", $basicAddForm.serialize(), function (r) {
        if (r.code === 200) {
            $MB.n_success( r.msg );
            $( "#headId" ).val( r.data );
            $( "#basic-add-form" ).find( 'input' ).attr( "disabled", "disabled" );
            $( '#btn_basic' ).attr( "disabled", "disabled" );
        } else {
            $MB.n_danger( "有错误发生！" );
        }
    } );
}

// 添加商品
$( "#btn_add_shop" ).click( function () {
    var headId = $( "#headId" ).val();
    $( "#saveActivityProduct" ).attr( "name", "save" );
    $( '#addProductModal' ).modal( 'show' );
    $( "#modalLabel" ).html( '' ).append( '添加商品' );
    $("#shopOffer").html('');
} );

// 批量添加商品
$( "#btn_batch_upload" ).click( function () {
    $("#uploadProduct").modal('show');
} );

$( "#btn_create_preheat" ).click( function () {
    var headId = $( "#headId" ).val();
    if (headId === '') {
        $MB.n_warning( "请先保存基本信息！" );
        return;
    } else {
        validProduct('preheat');
    }
} );

// 验证已有商品是否合法
function validProduct(stage) {
    $.get("/activity/validProduct", {headId: $("#headId").val()}, function (r) {
        if(r.code === 200) {
            if(r.data > 0) {
                $MB.n_warning("存在校验不通过的商品！");
            }else {
                createActivity( stage );
            }
        }
    });
}

// 检测是否上传商品
$( "#btn_create_formal" ).click( function () {
    var num = $('#activityProductTable').bootstrapTable('getOptions').totalRows;
    var headId = $( "#headId" ).val();
    if (headId === '') {
        $MB.n_warning( "请先保存基本信息！" );
        return;
    } else {
        validProduct('formal');
    }
} );

// 获取群组列表信息
function getGroupList(stage, type, tableId) {
    var flag = getPlanStatus($("#headId").val(), stage);
    var headId = $( "#headId" ).val();
    var settings = {
        url: '/activity/getGroupList',
        pagination: false,
        singleSelect: false,
        queryParams: function () {
            return {
                headId: headId,
                    stage: stage,
                    type: type
            };
        },
        columns: [
            {
                field: 'groupName',
                title: '店铺活动机制'
            },
            {
                field: 'groupInfo',
                title: '理解用户群组'
            }, {
                title: '短信',
                align: 'center',
                formatter: function (value, row, index) {
                    var res = "";
                    if(tableId === 'table1') {
                        if(stage === 'preheat') {
                            if(preheatNotifyStatus === 'done' || preheatNotifyStatus === 'timeout' || !flag) {
                                res = "<span style='color: #333'><i class='fa fa-envelope-o'></i>&nbsp;&nbsp;<i class='fa fa-refresh'></i></span>";
                            }else {
                                res = "<a onclick='selectGroup(\"" + type + "\",\"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-envelope-o'></i>" +
                                    "&nbsp;&nbsp;<a onclick='removeSmsSelected(\"NOTIFY\", \""+stage+"\", \"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-refresh'></i></a>" +
                                    "</a>";
                            }
                        }else {
                            if(formalNotifyStatus === 'done' || formalNotifyStatus === 'timeout' || !flag) {
                                res = "<span style='color: #333'><i class='fa fa-envelope-o'></i>&nbsp;&nbsp;<i class='fa fa-refresh'></i></span>";
                            }else {
                                res = "<a onclick='selectGroup(\"" + type + "\",\"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-envelope-o'></i>" +
                                    "&nbsp;&nbsp;<a onclick='removeSmsSelected(\"NOTIFY\",\""+stage+"\", \"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-refresh'></i></a>" +
                                    "</a>";
                            }
                        }
                    }else {
                        if(stage === 'preheat') {
                            if(preheatStatus === 'done') {
                                res = "<span style='color: #333'><i class='fa fa-envelope-o'></i>&nbsp;&nbsp;<i class='fa fa-refresh'></i></span>";
                            }else {
                                res = "<a onclick='selectGroup(\"" + type + "\",\"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-envelope-o'></i>" +
                                    "&nbsp;&nbsp;<a onclick='removeSmsSelected(\"DURING\", \""+stage+"\", \"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-refresh'></i></a>" +
                                    "</a>";
                            }
                        }else {
                            if(formalStatus === 'done') {
                                res = "<span style='color: #333'><i class='fa fa-envelope-o'></i>&nbsp;&nbsp;<i class='fa fa-refresh'></i></span>";
                            }else {
                                res = "<a onclick='selectGroup(\"" + type + "\",\"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-envelope-o'></i>" +
                                    "&nbsp;&nbsp;<a onclick='removeSmsSelected(\"DURING\", \""+stage+"\", \"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-refresh'></i></a>" +
                                    "</a>";
                            }
                        }
                    }
                    return res;
                }
            }, {
                field: 'smsTemplateContent',
                title: '企业微信',
                formatter: function (value, row, index) {
                    return longTextFormat(value, row, index);
                }
            }, {
                field: 'checkFlag',
                title: '校验结果',
                formatter: function (value, row, index) {
                    if(value === 'Y') {
                        return "<span class=\"badge bg-success\">通过</span>";
                    }
                    if(value === 'N') {
                        return "<span class=\"badge bg-danger\">未通过</span>";
                    }
                    return "-";
                }
            },{
                field: 'checkComments',
                title: '失败原因'
            }],
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $( "#" + tableId ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

// 群组列表点击文案图标
let CURRENT_TMP_CODE;
let CURRENT_GROUP_ID;
let CURRENT_TYPE;
function selectGroup(type, tmpCode, groupId, tableId) {
    CURRENT_TMP_CODE = tmpCode;
    CURRENT_GROUP_ID = groupId;
    CURRENT_TYPE = type;
    getTmpTableData();
    $( '#smstemplate_modal' ).modal( 'show' );
}

// 获取文案列表数据
function getTmpTableData() {
    var settings = {
        clickToSelect: true,
        rowStyle: tmpRowStyle,
        url: '/activity/getSmsTemplateList',
        pagination: false,
        singleSelect: true,
        queryParams: function () {
            return {
                name: $("#sms-form").find("input[name='name']").val(),
                isPersonal: $("#sms-form").find("select[name='isPersonal']").val(),
                relation: $("#sms-form").find("select[name='relation']").val(),
                scene: $("#sms-form").find("select[name='scene']").val()
            }
        },
        columns: [
            {
                checkbox: true
            },{
                field: 'content',
                title: '消息内容',
                formatter: function (value, row, index) {
                    return longTextFormat(value, row, index);
                }
            },{
                field: 'insertDt',
                title: '创建时间',
                align: 'center'
            },{
                field: 'scene',
                title: '使用场景',
                align: 'center'
            }, {
                field: 'isPersonal',
                title: '个性化',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value === '1') {
                        return "是";
                    }
                    if(value === '0') {
                        return "否";
                    }
                    return '';
                }
            }
        ], onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $( "#tmpTable").bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

//  保存文案内容
$( "#btn_save_sms" ).click( function () {
    var operateType = $( this ).attr( "name" );
    var flag = validTmp();
    if (flag) {
        $MB.confirm( {
            title: '提示:',
            content: operateType === 'save' ? '确定保存文案信息？' : '确定更新文案信息？'
        }, function () {
            var url = '';
            if (operateType === 'save') {
                url = '/activity/saveSmsTemplate';
            } else if (operateType === 'update') {
                url = '/activity/updateSmsTemplate';
            }
            $.post( url, $( "#tmp_add_form" ).serialize(), function (r) {
                if (r.code === 200) {
                    if (operateType === 'save') {
                        $MB.n_success( "保存文案信息成功！" );
                    } else if (operateType === 'update') {
                        $MB.n_success( "更新文案信息成功！" );
                    }
                }
                $( "#sms_add_modal" ).modal( 'hide' );
                $( "#smstemplate_modal" ).modal( 'show' );
                getTmpTableData();
            } );
        } );
    }
} );

// 验证文案信息
function validTmp() {
    var isProdName = $( "input[name='isProdName']:checked" ).val();
    var isProdUrl = $( "input[name='isProdUrl']:checked" ).val();
    var isPrice = $( "input[name='isPrice']:checked" ).val();
    var isProfit = $( "input[name='isProfit']:checked" ).val();
    var contet = $( "#content" ).val();
    if (isProdName === undefined) {
        $MB.n_warning( "请选择商品名称" );
        return false;
    }
    if (isProdUrl === undefined) {
        $MB.n_warning( "请选择商品详情页短链" );
        return false;
    }
    if (isProfit === undefined) {
        $MB.n_warning( "请选择商品利益点" );
        return false;
    }
    if (isPrice === undefined) {
        $MB.n_warning( "请选择商品活动期间最低单价" );
        return false;
    }
    if (contet === '') {
        $MB.n_warning( "文案内容不能为空" );
        return false;
    }

    // 验证短信内容是否合法
    if(isProdName === '1') {
        if(contet.indexOf("${商品名称}") === -1) {
            $MB.n_warning("'商品名称：是'，文案内容未发现${商品名称}");
            return false;
        }
    }else {
        if(contet.indexOf("${商品名称}") !== -1) {
            $MB.n_warning("'商品名称：否'，文案内容却发现${商品名称}");
            return false;
        }
    }

    if(isProdUrl === '1') {
        if(contet.indexOf("${商品详情页短链}") === -1) {
            $MB.n_warning("'商品详情页短链：是'，文案内容未发现${商品详情页短链}");
            return false;
        }
    }else {
        if(contet.indexOf("${商品详情页短链}") !== -1) {
            $MB.n_warning("'商品详情页短链：否'，文案内容却发现${商品详情页短链}");
            return false;
        }
    }

    if(isProfit === '1') {
        if(contet.indexOf("${商品利益点}") === -1) {
            $MB.n_warning("'商品利益点：是'，文案内容未发现${商品利益点}");
            return false;
        }
    }else {
        if(contet.indexOf("${商品利益点}") !== -1) {
            $MB.n_warning("'商品利益点：否'，文案内容却发现${商品利益点}");
            return false;
        }
    }

    if(isPrice === '1') {
        if(contet.indexOf("${商品最低单价}") === -1) {
            $MB.n_warning("'商品最低单价：是'，文案内容未发现${商品最低单价}");
            return false;
        }
    }else {
        if(contet.indexOf("${商品最低单价}") !== -1) {
            $MB.n_warning("'商品最低单价：否'，文案内容却发现${商品最低单价}");
            return false;
        }
    }
    let smsContent = $('#content').val();
    let y = smsContent.length;
    let m = smsContent.length;
    let n = smsContent.length;
    if(smsContent.indexOf('${商品详情页短链}') > -1) {
        y = y - '${商品详情页短链}'.length + parseInt(PROD_URL_LEN);
        m = m - '${商品详情页短链}'.length;
    }
    if(smsContent.indexOf('${商品名称}') > -1) {
        y = y - '${商品名称}'.length + parseInt(PROD_NAME_LEN);
        m = m - '${商品名称}'.length;
    }
    if(smsContent.indexOf('${商品利益点}') > -1) {
        y = y - '${商品利益点}'.length + parseInt(PROFIT_LEN);
        m = m - '${商品利益点}'.length;
    }
    if(smsContent.indexOf('${商品最低单价}') > -1) {
        y = y - '${商品最低单价}'.length + parseInt(PRICE_LEN);
        m = m - '${商品最低单价}'.length;
    }

    if(y > SMS_LEN_LIMIT) {
        $MB.n_warning("文案字符数超过允许最大长度！");
        return false;
    }
    return true;
}

// 当前群组所选的文案
function tmpRowStyle(row, index) {
    if (CURRENT_TMP_CODE != undefined && CURRENT_TMP_CODE != null && row.code === CURRENT_TMP_CODE) {
        return {
            classes: 'success'
        };
    }
    return {};
}

/**
 * 为群组设置模板信息
 * @param type 类型：期间，通知
 */
function setTmpCode() {
    var groupId = CURRENT_GROUP_ID;
    var selected = $( "#tmpTable").bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择为群组设置的文案！' );
        return;
    }
    $MB.confirm( {
        title: '提示：',
        content: '确定为当前群组设置选中的文案？'
    }, function () {
        var tmpCode = selected[0].code;
        $.post( "/activity/setSmsCode", {
            groupId: groupId,
            tmpCode: tmpCode,
            stage: CURRENT_ACTIVITY_STAGE,
            headId: $( "#headId" ).val(),
            type: CURRENT_TYPE
        }, function (r) {
            if (r.code === 200) {
                $MB.n_success( "当前群组设置文案成功！" );
            }
            $( "#smstemplate_modal" ).modal( 'hide' );
            if(CURRENT_TYPE === 'DURING') {
                getGroupList( CURRENT_ACTIVITY_STAGE, "DURING", 'table5');
            }
            if(CURRENT_TYPE === 'NOTIFY') {
                getGroupList( CURRENT_ACTIVITY_STAGE, "NOTIFY", 'table1');
            }
        });
    });
}

function contextOnChange() {
    var content = $('#content').val() === "" ? "请输入短信内容": $('#content').val();
    $("#article").html('').append(content);
}

// 点击编辑文案按钮
function editTmp() {
    var selected = $( "#tmpTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择需要编辑的文案！' );
        return;
    }
    var code = selected[0].code;
    $.get("/activity/checkTmpIsUsed", {tmpCode: code}, function (r) {
        if(r.code === 200) {
            if(r.data) {
                $MB.n_warning("当前文案已被引用无法修改！");
            }else {
                $.get( "/activity/getTemplate", {code: code}, function (r) {
                    var data = r.data;
                    $( "#code" ).val( data.code );
                    $( "#content" ).val( data.content );
                    $("#article").html('').append(data.content);
                    $( "input[name='isProdName']:radio[value='" + data.isProdName + "']" ).prop( "checked", true );
                    $( "input[name='isProdUrl']:radio[value='" + data.isProdUrl + "']" ).prop( "checked", true );
                    $( "input[name='isPrice']:radio[value='" + data.isPrice + "']" ).prop( "checked", true );
                    $( "input[name='isProfit']:radio[value='" + data.isProfit + "']" ).prop( "checked", true );
                    $( "#btn_save_sms" ).attr( 'name', 'update' );
                    $( "#smstemplate_modal" ).modal( 'hide' );
                    $( "#sms_add_modal" ).modal( 'show' );
                } );
            }
        }
    });
}

/**
 * 重置新增文案modal
 */
$( "#sms_add_modal" ).on( 'hidden.bs.modal', function () {
    $( "#sms_add_title" ).text( "新增文案" );
    $( "#code" ).val( "" );
    $( "#name" ).val( "" );
    $( "#content" ).val( "" );
    $( "input[name='isProdName']" ).removeAttr( "checked" );
    $( "input[name='isProdUrl']" ).removeAttr( "checked" );
    $( "input[name='isPrice']" ).removeAttr( "checked" );
    $( "input[name='isProfit']" ).removeAttr( "checked" );
    $("#article").html('').append("请输入短信内容");
    $( "#smstemplate_modal" ).modal( 'show' );
    $( "#btn_save_sms" ).attr( 'name', 'save' );
    $("#word").text('');
} );

// 删除文案
function deleteTmp() {
    var selected = $( "#tmpTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择需要删除的文案！' );
        return;
    }
    var code = selected[0].code;
    $.get("/activity/checkTmpIsUsed", {tmpCode: code}, function (r) {
        if(r.code === 200) {
            if(r.data) {
                $MB.n_warning("当前文案已被引用无法删除！");
            }else {
                $MB.confirm( {
                    title: '提示：',
                    content: '确认删除选中的文案？'
                }, function () {
                    $.post( "/activity/deleteTmp", {code: code}, function (r) {
                        if (r.code === 200) {
                            $MB.n_success( "删除成功！" );
                        }
                        getTmpTableData();
                    } );
                } );
            }
        }
    });
}

// 屏蔽测试
function testSend() {
    let selectRows = $( "#tmpTable" ).bootstrapTable( 'getSelections' );
    if (null == selectRows || selectRows.length == 0) {
        $MB.n_warning( '请选择需要测试的文案！' );
        return;
    }
    var code = selectRows[0]["code"];
    //根据获取到的数据查询
    $.getJSON( "/activity/getActivityTemplateContent?code=" + code, function (resp) {
        if (resp.code === 200) {
            $( "#smstemplate_modal" ).modal( 'hide' );
            //更新测试面板
            $( "#smsContent1" ).val( resp.data );
            $( '#send_modal' ).modal( 'show' );
        }
    } )
}

$( "#send_modal" ).on( 'hidden.bs.modal', function () {
    $( "#smstemplate_modal" ).modal( 'show' );
} );

// 屏蔽测试短信发送
function sendMessage() {
    //验证
    let phoneNums=[];
    $("input[name='phoneNum']").each(function(){
        let temp=$(this).val();
        if(temp!=='')
        {
            phoneNums.push(temp);
        }
    })


    if(phoneNums.length==0)
    {
        $MB.n_warning("至少输入一个手机号！");
        return;
    }

    let phoneNum = phoneNums.join(',');
    let testSmsCode=$("#testSmsCode").val();

    //提交后端进行发送
    lightyear.loading( 'show' );

    let param = new Object();
    param.phoneNum = phoneNum;
    param.smsCode = testSmsCode;

    $.ajax( {
        url: "/activity/activityContentTestSend",
        data: param,
        type: 'POST',
        success: function (r) {
            lightyear.loading( 'hide' );
            if (r.code == 200) {
                $MB.n_success( r.msg );
            } else {
                $MB.n_danger( r.msg );
            }
        }
    } );
}

// 重置文案的搜索信息
function resetTmpInfo() {
    $("#sms-form").find("input[name='name']").val("");
    $("#sms-form").find("select[name='isPersonal']").find("option:selected").removeAttr("selected");
    $("#sms-form").find("select[name='relation']").find("option:selected").removeAttr("selected");
    $("#sms-form").find("select[name='scene']").find("option:selected").removeAttr("selected");
    getTmpTableData();
}

// 获取默认的转化数据
// function geConvertInfo() {
//     $.get("/activity/geConvertInfo", {headId: $("#headId").val(), stage: CURRENT_ACTIVITY_STAGE}, function (r) {
//         var data = r.data;
//         var covRate = data['covRate'];
//         covRate = (covRate !== null && covRate !== '' && covRate !== undefined) ? parseFloat((data['covRate'] * 100).toFixed(2)) : '';
//         $("#covListId").val(data['covListId']);
//         $("#covRate").val(covRate);
//         $("#expectPushNum").val(data['expectPushNum']);
//         $("#expectCovNum").val(data['expectCovNum']);
//     });
// }

function add_sms() {
    $('#smstemplate_modal').modal('hide');
    $('#sms_add_modal').modal('show');
}

// 调整系统方案
function changePlan() {
    $("#plan_change_modal").modal('show');
    table3();
    var data = [{
        name: '改变方案对转化率造成的预期改变'
    },{
        name: '改变方案对推送用户数造成的预期改变'
    },{
        name: '改变方案对转化用户数造成的预期改变'
    }];
    table4(data);
}

// 调整方案
$("#changePlan").click(function () {
    changePlan();
});

function covRowStyle(row, index) {
    var covListId = $("#covertDataTable").bootstrapTable('getData')[0]['covListId'];
    if(row.covListId === covListId) {
        return {
            classes: 'success'
        };
    }
    return {};
}

function table3() {
    var settings = {
        url: '/activity/getCovList',
        clickToSelect: true,
        singleSelect: true,
        rowStyle: covRowStyle,
        onCheck:function(row){
            var covListId = $("#covertDataTable").bootstrapTable('getData')[0]['covListId'];
            if(covListId !== row['covListId']) {
                var changedCovId = row['covListId'];
                calculateCov(changedCovId);
            }
        },
        queryParams: function() {
            return {
                headId: $("#headId").val(),
                stage: CURRENT_ACTIVITY_STAGE,
                covListId: $("#covertDataTable").bootstrapTable('getData')[0]['covListId']
            }
        },
        columns: [
            {
                field: 'check',
                checkbox: true,
                formatter: function (value, row, index) {
                    if(row.covListId === $("#covertDataTable").bootstrapTable('getData')[0]['covListId']) {
                        return {checked: true};
                    }
                    return {};
                }
            }, {
                field: 'covRate',
                title: '推送的期望转化率（%）',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value !== '' && value !== null && value !== undefined) {
                        return parseFloat((value * 100).toFixed(2));
                    }else {
                        return '-';
                    }
                }
            }, {
                field: 'expectPushNum',
                align: 'center',
                title: '达成期望转化率<br/>对应的推送用户数（人）'
            }, {
                field: 'expectCovNum',
                align: 'center',
                title: '达成期望转化率<br/>对应的转化用户数（人）'
            }
        ]
    };
    $("#table3").bootstrapTable('destroy').bootstrapTable(settings);
}

// 测算转化率的值
function calculateCov(changedCovId) {
    var defaultCovId = $("#covertDataTable").bootstrapTable('getData')[0]['covListId'];
    var headId = $("#headId").val();
    $.get("/activity/calculateCov", {headId: headId, defaultCovId: defaultCovId, changedCovId: changedCovId, stage: CURRENT_ACTIVITY_STAGE}, function (r) {
        $("#table4").bootstrapTable('load', r.data);
    });
}

function table4(data) {
    var settings = {
        clickToSelect: true,
        singleSelect: true,
        columns: [
            {
                field: 'name',
                title: ''
            }, {
                field: 'val',
                title: '改变绝对值',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value !== null && value !== '' && value !== undefined) {
                        if(index === 0) {
                            return parseFloat((value * 100).toFixed(2));
                        }else {
                            return value;
                        }
                    }
                    return '-';
                }
            }, {
                field: 'per',
                title: '改变幅度（%）',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value !== null && value !== '' && value !== undefined) {
                        return parseFloat((value * 100).toFixed(2));
                    }
                    return "-";
                }
            }
        ]
    };
    $("#table4").bootstrapTable('destroy').bootstrapTable(settings);
    $("#table4").bootstrapTable('load', data);
}

// 更新转化率表
function updateCovInfo() {
    let selectRows = $( "#table3" ).bootstrapTable( 'getSelections' );
    if (null == selectRows || selectRows.length == 0) {
        $MB.n_warning( '请选择需要调整的方案！' );
        return;
    }
    var covId = selectRows[0]["covListId"];
    $MB.confirm({
        title: '提示：',
        content: '确定设置为当前的方案？'
    }, function () {
        $.post("/activity/updateCovInfo", {headId: $("#headId").val(), stage: CURRENT_ACTIVITY_STAGE, covId: covId}, function (r) {
            if(r.code === 200) {
                $MB.n_success("设置成功！");
            }
            geConvertInfo();
            $("#plan_change_modal").modal('hide');
        });
    });
}

// 统计短信内容的字数
statTmpContentNum();
function statTmpContentNum() {
    $("#content").on('input propertychange', function () {
        let smsContent = $('#content').val();
        let y = smsContent.length;
        let m = smsContent.length;
        let n = smsContent.length;
        if(smsContent.indexOf('${PROD_URL}') > -1) {
            y = y - '${PROD_URL}'.length + parseInt(PROD_URL_LEN);
            m = m - '${PROD_URL}'.length;
        }
        if(smsContent.indexOf('${PROD_NAME}') > -1) {
            y = y - '${PROD_NAME}'.length + parseInt(PROD_NAME_LEN);
            m = m - '${PROD_NAME}'.length;
        }
        if(smsContent.indexOf('${PRICE}') > -1) {
            y = y - '${PRICE}'.length + parseInt(PRICE_LEN);
            m = m - '${PRICE}'.length;
        }
        total_num = y;
        var code = "";
        code += m + ":编写内容字符数 / " + y + ":填充变量最大字符数 / " + SMS_LEN_LIMIT + ":文案总字符数";
        $("#word").text(code);
    });
}

$("#btn_valid_product").click(function () {
    $.get("/activity/validProductInfo", {headId: $("#headId").val()}, function (r) {
        if(r.code === 200) {
            $MB.n_success("数据已校验完毕！");
            $MB.refreshTable('activityProductTable');
        }
    });
});

$("#btn_download_data").click(function () {
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定下载商品数据?"
    }, function () {
        $("#btn_download_data").text("下载中...").attr("disabled", true);
        $.post("/activity/downloadExcel", {headId: $("#headId").val(), activityStage: CURRENT_ACTIVITY_STAGE}, function (r) {
            if (r.code === 200) {
                window.location.href = "/common/download?fileName=" + r.msg + "&delete=" + true;
            } else {
                $MB.n_warning(r.msg);
            }
            $("#btn_download_data").html("").append("<i class=\"fa fa-download\"></i> 下载数据").removeAttr("disabled");
        });
    });
});

function convertShortUrl() {
    var longUrl = $("#longUrl").val();
    if(longUrl === '' || longUrl.trim() === '') {
        $MB.n_warning("长链不能为空！");
        return;
    }
    $.get("/activity/convertShortUrl", {url:longUrl}, function (r) {
        $("#longUrl").val(r.data);
    });
}

function copyShortUrl() {
    $('#longUrl').select();
    document.execCommand("copy");
    $MB.n_success("成功复制到粘贴板!");
}

function copyString(data) {
    $('#copy_content').val(data);
    $('#copy_content').select();
    // 执行浏览器复制命令
    document.execCommand("copy");
    $MB.n_success("成功复制到粘贴板!");
}

function copyString(data){
    $("#copyContent").text(data);
    let range = document.createRange();
    range.selectNodeContents(document.getElementById('copyContent'));
    let selection = document.getSelection();
    selection.removeAllRanges();
    selection.addRange(range);
    document.execCommand('Copy');
    $MB.n_success("成功复制到粘贴板!");
}

// 移除组上的短信
function removeSmsSelected(type, stage, smsCode, groupId) {
    if(smsCode === null || smsCode === '' || smsCode === 'null') {
        $MB.n_warning("当前群组不需要重置文案！");
        return;
    }
    $MB.confirm({
        title: '提示',
        content: '确定重置当前群组配置的文案吗？'
    }, function () {
        $.post("/activity/removeSmsSelected", {type: type, headId: $("#headId").val(), stage:stage, smsCode: smsCode, groupId: groupId}, function (r) {
            if(r.code === 200) {
                $MB.n_success("当前群组配置的文案已重置！");
                $MB.refreshTable("table1");
                $MB.refreshTable("table5");
            }
        });
    });
}
// 获取配置消息的用户转化数据
function covertDataTable() {
    var settings = {
        url: '/activity/geConvertInfo',
        pagination: false,
        singleSelect: true,
        queryParams: function () {
            return {headId: $("#headId").val(), stage: CURRENT_ACTIVITY_STAGE}
        },
        columns: [
            {
                field: 'covListId',
                visible: false
            },
            {
                field: 'covRate',
                title: '期望转化率（%）',
                align: 'center'
            }, {
                field: 'expectPushNum',
                title: '对应推送用户数（人）',
                align: 'center'
            }, {
                field: 'expectCovNum',
                title: '对应的转化用户数',
                align: 'center'
            }
        ]
    };
    $( "#covertDataTable").bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

// 店铺活动机制发生变化
function groupIdChange(dom) {
    $(dom).trigger('blur');
    var val = $(dom).find("option:selected").val();
    switch (val) {
        case "9":
            $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                '<label class="col-md-4 control-label">满件打折</label>\n' +
                '<div class="col-md-7">\n' +
                '<input class="form-control" type="text" name="discountSize"/>\n' +
                '</div></div>');
            break;
        case "10":
            $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                '<label class="col-md-4 control-label">满元减钱门槛(元)</label>\n' +
                '<div class="col-md-7">\n' +
                '<input class="form-control" type="text" name="discountThreadhold"/>\n' +
                '</div></div>').append('<div class="form-group">' +
                '<label class="col-md-4 control-label">满元减钱面额(元)</label>\n' +
                '<div class="col-md-7">\n' +
                '<input class="form-control" type="text" name="discountDeno"/>\n' +
                '</div></div>');
            break;
        case "11":
            $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                '<label class="col-md-4 control-label">立减金额</label>\n' +
                '<div class="col-md-7">\n' +
                '<input class="form-control" type="text" name="discountAmount"/>\n' +
                '</div></div>');
            break;
        case "12":
            $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                '<label class="col-md-4 control-label">立减金额</label>\n' +
                '<div class="col-md-7">\n' +
                '<input class="form-control" type="text" name="discountAmount"/>\n' +
                '</div></div>');
            break;
        case "13":
            $("#shopOffer").attr("style", "display:none;").html('');
        default:
            $("#shopOffer").attr("style", "display:none;").html('');
            break;
    }
}

function editFormalActivity() {
    CURRENT_ACTIVITY_STAGE = 'formal';
    getProductInfo();
    $("#productListTitle").html('正式商品列表');
    create_step.setActive(1);
    $("#step1").attr("style", "display:none;");
    $("#step2").attr("style", "display:block;");
    $("#step3").attr("style", "display:none;");

    $("#preheatFinishBtn").attr("style", "display:none;");
    $("#formalFinishBtn").attr("style", "display:block;");
}

function platDiscountClick(idx) {
    if(idx === 0){
        $("#platThresholdDiv").attr("style", "display:none;");
        $("#platDenoDiv").attr("style", "display:none;");
    }
    if(idx === 1){
        $("#platThresholdDiv").attr("style", "display:block;");
        $("#platDenoDiv").attr("style", "display:block;");
    }
}