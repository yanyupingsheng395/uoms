let create_step;
let CURRENT_ACTIVITY_STAGE;
let CURRENT_ACTIVITY_TYPE;
let validatorProduct;
let $activityProductAddForm = $( "#add-product-form" );
var basic_validator;
var $basicAddForm = $( "#basic-add-form" );
let CURRENT_TMP_CODE;
let CURRENT_GROUP_ID;
let CURRENT_TYPE;

$( function () {
    stepInit();
    editInit();
    initDt();
    validBasic();
    validateProductRule();
    statTmpContentNum();
});

// 步骤条初始化
function stepInit() {
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
}

// 编辑页面初始化
function editInit() {
    if(operate_type === 'update') {
        var hasPreheatVal = $( "input[name='hasPreheat']:checked" ).val();
        haspreheat(hasPreheatVal);
    }
}

// 是否预售按钮点击事件
function haspreheat(v) {
    if(v === '1') {
        CURRENT_ACTIVITY_STAGE = 'preheat';
        $("#creatPreheat").removeAttr("style");
    }
    if(v === '0') {
        CURRENT_ACTIVITY_STAGE = 'formal';
        $("#productListTitle1").html('正式通知活动商品列表');
        $("#productListTitle12").html('正式期间活动商品列表');
        $("#creatPreheat").attr("style", "display:none;");
    }
}

// 平台优惠点击是否
$("select[name='platDiscount']").change(function() {
    var idx = $(this).find("option:selected").val();
    if(idx === 'N'){
        $("#addPlatDiscountBtn").hide();
        $("#platDiscountItems").hide();
        $("#platCouponDiv").removeAttr("style");
    }
    if(idx === 'Y'){
        $("#addPlatDiscountBtn").show();
        if($("#platDiscountItems").text().trim() !== '') {
            $("#platDiscountItems").show();
            $("#platCouponDiv").attr("style", "border: dashed 1px #e7eaec;margin-top: 10px;padding-bottom: 10px;");
        }
    }
});

// 店铺优惠点击事件
$("select[name='shopDiscount']").change(function() {
    var idx = $(this).find("option:selected").val();
    if(idx === 'N'){
        $("#addShopDiscountBtn").hide();
        $("#shopDiscountItems").hide();
        $("#shopCouponDiv").removeAttr("style");
    }
    if(idx === 'Y'){
        $("#addShopDiscountBtn").show();
        if($("#shopDiscountItems").text().trim() !== '') {
            $("#shopDiscountItems").show();
            $("#shopCouponDiv").attr("style", "border: dashed 1px #e7eaec;margin-top: 10px;padding-bottom: 10px;");
        }
    }
});

// 预售按钮点击
function preheatClick() {
    CURRENT_ACTIVITY_STAGE = 'preheat';
    $("#productListTitle1").html('预售通知活动商品列表');
    $("#productListTitle2").html('预售期间活动商品列表');
    stepBreak(1);
}

// 正式按钮点击
function formalClick() {
    CURRENT_ACTIVITY_STAGE = 'formal';
    $("#productListTitle1").html('正式通知活动商品列表');
    $("#productListTitle2").html('正式期间活动商品列表');
    stepBreak(1);
}

// 保存基本信息之前的校验
function beforeSave(activityStage){
    // CURRENT_ACTIVITY_STAGE = activityStage;
    // if(operate_type === 'save') {
    //     var validator = $basicAddForm.validate();
    //     if (validator.form() && validBasicDt() && validDiscount()) {
    //         $MB.confirm( {
    //             title: '提示:',
    //             content: '确定保存信息？'
    //         }, function () {
    //             saveActivityHead();
    //         } );
    //     }
    // }
    // if(operate_type === 'update') {
    //     stepBreak(1);
    // }

    stepBreak(1);
}

// 验证平台优惠 + 补贴优惠
function validDiscount() {
    var platDiscount = $("select[name='platDiscount']").find("option:selected").val();
    var shopDiscount = $("select[name='shopDiscount']").find("option:selected").val();
    var flag = true;
    if(platDiscount === 'Y') {
        $("#platDiscountItems").find(".row").each(function (v, k) {
            var couponThreshold = $(this).find("input[name='couponThreshold']").val();
            var couponDenom = $(this).find("input[name='couponDenom']").val();
            if(couponThreshold === '') {
                $MB.n_warning("请输入平台优惠的门槛！");
                return (flag = false);
            }
            if(couponDenom === '') {
                $MB.n_warning("请输入平台优惠的面额！");
                return (flag = false);
            }
        });
    }
    if(shopDiscount === 'Y') {
        var cnt = 0;
        $("#shopDiscountItems").find(".row").each(function (v, k) {
            var addFlag = $(this).find("select[name='addFlag']").find("option:selected").val();
            if(addFlag === '1') {
                cnt++;
            }
            var couponThreshold = $(this).find("input[name='couponThreshold']").val();
            var couponDenom = $(this).find("input[name='couponDenom']").val();
            if(couponThreshold === '') {
                $MB.n_warning("请输入店铺优惠的门槛！");
                return (flag = false);
            }
            if(couponDenom === '') {
                $MB.n_warning("请输入店铺优惠的面额！");
                return (flag = false);
            }
        });
        if(cnt > 1) {
            $MB.n_warning("店铺优惠只能有一个可叠加券！");
            return (flag = false);
        }
    }
    return flag;
}

// 点击上一步下一步按钮
function stepBreak(index) {
    if(index == 0) {
        create_step.setActive(0);
        $("#step1").attr("style", "display:block;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:none;");

        $("#creatPreheat").attr('onclick', 'preheatClick()');
        $("#creatFormal").attr('onclick', 'formalClick()');
    }
    if(index == 1) {
        create_step.setActive(1);
        if(operate_type === 'update') {
            if(CURRENT_ACTIVITY_STAGE === 'preheat') {
                if(preheatNotifyStatus === 'doing' || preheatNotifyStatus === 'done' || preheatNotifyStatus === 'timeout') {
                    $("#btn_add_shop1").attr("style", "display:none;");
                    $("#btn_edit_shop1").attr("style", "display:none;");
                    $("#btn_delete_shop1").attr("style", "display:none;");
                    $("#btn_batch_upload1").attr("style", "display:none;");
                    $("#changePlan").attr("style", "display:none;");
                    $("#calculateCovDataBtn").attr("style", "display:none;");
                    $("#submitBtn1").attr("style", "display:none;");
                }else {
                    $("#btn_add_shop1").attr("style", "display:inline-block;");
                    $("#btn_edit_shop1").attr("style", "display:inline-block;");
                    $("#btn_delete_shop1").attr("style", "display:inline-block;");
                    $("#btn_batch_upload1").attr("style", "display:inline-block;");
                    $("#changePlan").attr("style", "display:inline-block;");
                    $("#calculateCovDataBtn").attr("style", "display:inline-block;");
                    $("#submitBtn1").attr("style", "display:block;");
                }
                if(preheatStatus === 'done' || preheatStatus === 'timeout') {
                    $("#btn_add_shop2").attr("style", "display:none;");
                    $("#btn_edit_shop2").attr("style", "display:none;");
                    $("#btn_delete_shop2").attr("style", "display:none;");
                    $("#btn_batch_upload2").attr("style", "display:none;");
                    $("#submitBtn2").attr("style", "display:none;");
                }else {
                    $("#btn_add_shop2").attr("style", "display:inline-block;");
                    $("#btn_edit_shop2").attr("style", "display:inline-block;");
                    $("#btn_delete_shop2").attr("style", "display:inline-block;");
                    $("#btn_batch_upload2").attr("style", "display:inline-block;");
                    $("#submitBtn2").attr("style", "display:block;");
                }
            }
            if(CURRENT_ACTIVITY_STAGE === 'formal') {
                if(formalNotifyStatus === 'doing' || formalNotifyStatus === 'done' || formalNotifyStatus === 'timeout') {
                    $("#btn_add_shop1").attr("style", "display:none;");
                    $("#btn_edit_shop1").attr("style", "display:none;");
                    $("#btn_delete_shop1").attr("style", "display:none;");
                    $("#btn_batch_upload1").attr("style", "display:none;");
                    $("#changePlan").attr("style", "display:none;");
                    $("#calculateCovDataBtn").attr("style", "display:none;");
                    $("#submitBtn1").attr("style", "display:none;");
                }else {
                    $("#btn_add_shop1").attr("style", "display:inline-block;");
                    $("#btn_edit_shop1").attr("style", "display:inline-block;");
                    $("#btn_delete_shop1").attr("style", "display:inline-block;");
                    $("#btn_batch_upload1").attr("style", "display:inline-block;");
                    $("#changePlan").attr("style", "display:inline-block;");
                    $("#calculateCovDataBtn").attr("style", "display:inline-block;");
                    $("#submitBtn1").attr("style", "display:block;");
                }
                if(formalStatus === 'done' || formalStatus === 'timeout') {
                    $("#btn_add_shop2").attr("style", "display:none;");
                    $("#btn_edit_shop2").attr("style", "display:none;");
                    $("#btn_delete_shop2").attr("style", "display:none;");
                    $("#btn_batch_upload2").attr("style", "display:none;");
                    $("#submitBtn2").attr("style", "display:none;");
                }else {
                    $("#btn_add_shop2").attr("style", "display:inline-block;");
                    $("#btn_edit_shop2").attr("style", "display:inline-block;");
                    $("#btn_delete_shop2").attr("style", "display:inline-block;");
                    $("#btn_batch_upload2").attr("style", "display:inline-block;");
                    $("#submitBtn2").attr("style", "display:block;");
                }
            }
        }
        getProductInfo('NOTIFY', 'activityProductTable1');
        getProductInfo('DURING', 'activityProductTable2');
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:block;");
        $("#step3").attr("style", "display:none;");
    }
    if(index == 2) {
        $.get("/qywxActivity/validProduct", {headId: $("#headId").val(), stage: CURRENT_ACTIVITY_STAGE}, function (r) {
            if(r.code === 200) {
                if(r.data > 0) {
                    $MB.n_warning("存在校验不通过的商品！");
                }else {
                    $.get("/qywxActivity/validUserGroup", {headId: $("#headId").val(), stage: CURRENT_ACTIVITY_STAGE}, function (r) {
                        if(r.code === 200) {
                            ifCalculate();
                            create_step.setActive(2);
                            $("#step1").attr("style", "display:none;");
                            $("#step2").attr("style", "display:none;");
                            $("#step3").attr("style", "display:block;");
                            createActivity(CURRENT_ACTIVITY_STAGE);
                        }
                    });

                }
            }
        });
    }
}

// 验证商品信息
function validateProductRule() {
    let icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i>";
    validatorProduct = $activityProductAddForm.validate( {
        rules: {
            productId: {
                required: true,
                remote: {
                    url: "/qywxActivity/checkProductId",
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
                    return $("#activityRule").val() === '9';
                }
            },
            discountCnt: {
                required: function () {
                    return $("#activityRule").val() === '9';
                }
            },
            discountAmount: {
                required: function () {
                    return $("#activityRule").val() === '13';
                }
            },
            spCouponFlag: {
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
            discountCnt: {
                required: icon + "请输入满件打折的件数"
            },
            discountAmount: {
                required: icon + "请输入立减金额"
            },
            spCouponFlag: {
                required: icon + '请选择单品券'
            }
        }
    } );
}

// 查询活动通知商品信息
function searchActivityProduct1() {
    $MB.refreshTable( 'activityProductTable1' );
}

// 查询活动期间商品信息
function searchActivityProduct2() {
    $MB.refreshTable( 'activityProductTable2' );
}

// 重置查询商品条件
function resetActivityProduct1() {
    $( "#productId1" ).val( "" );
    $( "#productName1" ).val( "" );
    $( "#groupId1" ).find( "option:selected" ).removeAttr( "selected" );
    $MB.refreshTable( 'activityProductTable1' );
}

// 重置查询商品条件
function resetActivityProduct2() {
    $( "#productId2" ).val( "" );
    $( "#productName2" ).val( "" );
    $( "#groupId2" ).find( "option:selected" ).removeAttr( "selected" );
    $MB.refreshTable( 'activityProductTable2' );
}

// 商品数据下载
$( "#btn_download" ).click( function () {
    window.location.href = "/qywxActivity/downloadFile";
} );

// 添加活动商品
$( "#saveActivityProduct" ).click( function () {
    let validator = $activityProductAddForm.validate();
    let flag = validator.form();
    if (flag) {
        let operate = $( "#saveActivityProduct" ).attr( "name" );
        if (operate === "save") {
            $.post( "/qywxActivity/saveActivityProduct", $( "#add-product-form" ).serialize() + "&headId=" + $( "#headId" ).val() + "&activityStage=" + CURRENT_ACTIVITY_STAGE + "&activityType=" + CURRENT_ACTIVITY_TYPE,  function (r) {
                if (r.code === 200) {
                    $MB.n_success( "添加商品成功！" );
                    if(CURRENT_ACTIVITY_TYPE === 'NOTIFY') {
                        getProductInfo('NOTIFY', 'activityProductTable1');
                    }
                    if(CURRENT_ACTIVITY_TYPE === 'DURING') {
                        getProductInfo('DURING', 'activityProductTable2');
                    }
                    $MB.closeAndRestModal( "addProductModal" );
                } else {
                    $MB.n_danger( r.msg );
                }
            } );
        }
        if (operate === 'update') {
            $.post( "/qywxActivity/updateActivityProduct", $( "#add-product-form" ).serialize() + "&headId=" + $( "#headId" ).val(), function (r) {
                if (r.code === 200) {
                    $MB.n_success( "更新商品成功！" );
                    if(CURRENT_ACTIVITY_TYPE === 'NOTIFY') {
                        getProductInfo('NOTIFY', 'activityProductTable1');
                    }
                    if(CURRENT_ACTIVITY_TYPE === 'DURING') {
                        getProductInfo('DURING', 'activityProductTable2');
                    }
                    $MB.closeAndRestModal( "addProductModal" );
                } else {
                    $MB.n_danger(r.msg);
                }
            } );
        }
    }
} );

// 添加商品信息model关闭时，重置控件数据
$( "#addProductModal" ).on( "hidden.bs.modal", function () {
    $( "input[name='productId']" ).val( "" ).removeAttr("disabled");
    $( "input[name='productName']" ).val( "" );
    $( "input[name='activityPrice']" ).val( "" );
    $( "input[name='formalPrice']" ).val( "" );
    $( "input[name='discountCnt']" ).val( "" );
    $( "input[name='discountSize']" ).val( "" );
    $( "input[name='discountAmount']" ).val( "" );
    $( "input[name='spCouponThreshold']" ).val( "" );
    $( "input[name='spCouponDenom']" ).val( "" );
    $( "select[name='groupId']" ).find( "option:selected" ).removeAttr( "selected" );
    $( "select[name='spCouponFlag']" ).find( "option:selected" ).removeAttr( "selected" );
    $( "select[name='activityType']" ).find( "option:selected" ).removeAttr( "selected" );
    $activityProductAddForm.validate().resetForm();
} );

// 修改活动通知商品信息
$( "#btn_edit_shop1" ).click( function () {
    editShop('NOTIFY');
    CURRENT_ACTIVITY_TYPE = 'NOTIFY';
});

// 修改活动期间商品信息
$( "#btn_edit_shop2" ).click( function () {
    editShop('DURING');
    CURRENT_ACTIVITY_TYPE = 'DURING';
});

// 编辑商品信息
function editShop(type) {
    var tableId = '';
    if(type === 'NOTIFY') {
        tableId = 'activityProductTable1';
    }
    if(type === 'DURING') {
        tableId = 'activityProductTable2';
    }
    $("#modalLabel").html('').append('修改商品');
    $( "#saveActivityProduct" ).attr( "name", "update" );
    let selected = $( "#" + tableId ).bootstrapTable( 'getSelections' );
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择需要编辑的商品！' );
        return;
    }
    if(selected_length > 1) {
        $MB.n_warning( '一次只能选择一条记录修改！' );
        return;
    }
    let id = selected[0].id;
    $.get( "/qywxActivity/getProductById", {id: id}, function (r) {
        if (r.code === 200) {
            let data = r.data;
            $("#add-product-form").find( "input[name='id']" ).val( data['id'] );
            $("#add-product-form").find( "input[name='productId']" ).val( data['productId'] ).attr("disabled", "disabled");
            $("#add-product-form").find( "input[name='productName']" ).val( data['productName'] );
            $("#add-product-form").find( "input[name='activityPrice']" ).val( data['activityPrice'] );
            $("#add-product-form").find( "input[name='formalPrice']" ).val( data['formalPrice'] );
            $("#add-product-form").find( "input[name='spCouponThreshold']" ).val( data['spCouponThreshold'] );
            $("#add-product-form").find( "input[name='spCouponDenom']" ).val( data['spCouponDenom'] );
            $("#add-product-form").find("select[name='groupId']").find("option[value='"+data['groupId']+"']").prop("selected", true);
            $("#add-product-form").find("select[name='spCouponFlag']").find("option[value='"+data['spCouponFlag']+"']").prop("selected", true);
            $("#add-product-form").find("select[name='activityType']").find("option[value='"+data['activityType']+"']").prop("selected", true);
            var val = $("#activityRule").find("option:selected").val();
            switch (val) {
                case "9":
                    $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                        '<label class="col-md-4 control-label">满件打折件数（件）</label>\n' +
                        '<div class="col-md-7">\n' +
                        '<input value="'+data['discountCnt']+'" class="form-control" type="text" name="discountCnt"/>\n' +
                        '</div></div>').append('<div class="form-group">' +
                        '<label class="col-md-4 control-label">满件打折力度（折）</label>\n' +
                        '<div class="col-md-7">\n' +
                        '<input class="form-control" type="text" name="discountSize" value="' + data['discountSize']+'"/>\n' +
                        '</div></div>');
                    break;
                case "14":
                    $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                        '<label class="col-md-4 control-label">立减特价金额（元）</label>\n' +
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
}

// 文件上传按钮选择事件
$( "#uploadFile" ).change( function () {
    $( '#filename' ).text( "文件名:" + $(this)[0].files[0].name).attr( "style", "display:inline-block;" );
    $( "#btn_upload" ).attr( "style", "display:inline-block;" );
} );

// 文件上传
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
        formData.append("activityType", CURRENT_ACTIVITY_TYPE);
        $.ajax({
            url: "/qywxActivity/uploadExcel",
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
                    if(CURRENT_ACTIVITY_TYPE == 'NOTIFY') {
                        $MB.refreshTable('activityProductTable1');
                    }
                    if(CURRENT_ACTIVITY_TYPE == 'DURING') {
                        $MB.refreshTable('activityProductTable2');
                    }
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

// 上传失败的提示列表数据
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

// 文件上传model关闭，重置数据
$("#uploadProduct").on('hidden.bs.modal', function () {
    $("input[name='uploadMethod']").eq(0).prop('checked', true);
    $("input[name='repeatProduct']").eq(0).prop('checked', true);
    $("#uploadFile").val('');
    $("#filename").html('').attr("style", "display:none;");
});

// 删除商品信息
$("#btn_delete_shop1").click(function () {
    CURRENT_ACTIVITY_TYPE = 'NOTIFY';
    deleteShop(CURRENT_ACTIVITY_TYPE);
});

// 删除商品信息
$("#btn_delete_shop2").click(function () {
    CURRENT_ACTIVITY_TYPE = 'DURING';
    deleteShop(CURRENT_ACTIVITY_TYPE);
});

// 删除商品，同时更改头表数据状态
function deleteShop(type){
    var tableId;
    if(type === 'NOTIFY') {
        tableId = 'activityProductTable1';
    }
    if(type === 'DURING') {
        tableId = 'activityProductTable2';
    }
    var selected = $("#" + tableId).bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要删除的商品记录！');
        return;
    }
    var ids = [];
    selected.forEach((v, k)=>{
        ids.push(v['id']);
    });
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除选中的商品记录？'
    }, function () {
        $.post("/qywxActivity/deleteProduct", {ids: ids.join(",")}, function (r) {
            if(r.code === 200) {
                $MB.n_success("删除成功！");
                $MB.refreshTable(tableId);
            }else {
                $MB.n_danger("删除失败！");
            }
        });
    });
}

// 获取商品信息
function getProductInfo(type, tableId) {
    var title = '活动通知';
    if(type === 'NOTIFY') {
        title = '活动通知';
    }
    if(type === 'DURING') {
        title = '活动期间';
    }
    var settings = {
        url: '/qywxActivity/getActivityProductPage',
        pagination: true,
        singleSelect: false,
        sidePagination: "server",
        clickToSelect: true,
        pageSize: 5,
        pageList: [5, 15, 50, 100],
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset,
                headId: $( "#headId" ).val(),
                productId: function() {
                    if(tableId === 'activityProductTable1') {
                        return $( "#productId1" ).val();
                    }else if(tableId === 'activityProductTable2') {
                        return $( "#productId1" ).val();
                    }
                },
                productName:function() {
                    if(tableId === 'activityProductTable1') {
                        return $( "#productName1" ).val();
                    }else if(tableId === 'activityProductTable2') {
                        return $( "#productName2" ).val();
                    }
                },
                groupId: function() {
                    if(tableId === 'activityProductTable1') {
                        return $("#groupId1").find("option:selected").val();
                    }else if(tableId === 'activityProductTable2') {
                        return $("#groupId2").find("option:selected").val();
                    }
                },
                activityStage: function () {
                    return CURRENT_ACTIVITY_STAGE;
                },
                activityType: type
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
                    if(value === '14') {
                        res =  "立减特价";
                    }
                    if(value === '13') {
                        res = '仅店铺券'
                    }
                    return res;
                }
            },{
                field: 'activityProfit',
                title: title + '体现利益点',
                valign: 'middle',
                align: 'center',
                formatter: function (value, row, index) {
                    if(row['groupId'] == '9') {
                        return value +'折';
                    }else {
                        return value +'元';
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
    $( "#" + tableId ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

// 提交计划 type:NOTIFY 活动通知，DURING 活动期间
function submitActivity(type) {
    let headId = $("#headId").val();
    var stage = $("#activity_stage").val();
    $.get("/qywxActivity/validSubmit", {headId: $("#headId").val(), stage: stage, type: type}, function (r) {
        if(r.code === 200) {
            var data = r.data;
            if(data['error'] !== null && data['error'] !== undefined) {
                $MB.n_warning(data['error']);
                return;
            }
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
        }else {
            $MB.n_warning(r.msg);
        }
    });
}
// 保存计划
function submitData(headId, type) {
    $.post("/qywxActivity/submitActivity", {headId: headId, operateType: operate_type, stage: CURRENT_ACTIVITY_STAGE, type: type}, function (r) {
        if(r.code === 200) {
            $MB.n_success("保存计划成功！");
        }else {
            $MB.n_danger("保存计划失败！");
        }
    });
}
// 正式和预售页面之间的跳转
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
            var data = getPlanStatus($("#headId").val(), stage, 'button');
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
            var data = getPlanStatus($("#headId").val(), stage, 'button');
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
function getPlanStatus(headId, stage, remark) {
    var res = $.ajax({
        url: '/activityPlan/getPlanStatus',
        data: {headId: headId, stage: stage},
        async: false
    });
    var status = JSON.parse(res.responseText).data;
    if(remark === 'smsContent') {
        if(status === '0' || status === '1' || status === '' || status === undefined || status === null || status === 'null') {
            return true;
        }
    }
    if(remark === 'button') {
        if(status === '0' || status === '' || status === undefined || status === null || status === 'null') {
            return true;
        }
    }
    return false;
}

/********************************* 活动运营计划 end *********************************/

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
    $( "#preheatStartDt" ).datepicker( 'setStartDate', date );
    $( "#preheatEndDt" ).datepicker( 'setStartDate', date );
    $( "#formalStartDt" ).datepicker( 'setStartDate', date );
    $( "#formalEndDt" ).datepicker( 'setStartDate', date);
    $( "#preheatNotifyDt" ).datepicker( 'setStartDate', date );
    $( "#formalNotifyDt" ).datepicker( 'setStartDate', date );
}

// 验证活动基本信息的表单
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
});

// 保存基本信息
function saveActivityHead() {
    var data = $basicAddForm.serialize();
    var couponList = [];
    var platDiscount = $("select[name='platDiscount']").find("option:selected").val();
    var shopDiscount = $("select[name='shopDiscount']").find("option:selected").val();
    if(platDiscount === 'Y') {
        $("#platDiscountItems").find(".row").each(function (v, k) {
            var addFlag = $(this).find("select[name='addFlag']").find("option:selected").val();
            var couponThreshold = $(this).find("input[name='couponThreshold']").val();
            var couponDenom = $(this).find("input[name='couponDenom']").val();
            var couponType = $(this).find("input[name='couponType']").val();
            var coupon = {};
            coupon['addFlag'] = addFlag;
            coupon['couponThreshold'] = couponThreshold;
            coupon['couponDenom'] = couponDenom;
            coupon['couponType'] = couponType;
            couponList.push(coupon);
        });
    }
    if(shopDiscount === 'Y') {
        $("#shopDiscountItems").find(".row").each(function (v, k) {
            var addFlag = $(this).find("select[name='addFlag']").find("option:selected").val();
            var couponThreshold = $(this).find("input[name='couponThreshold']").val();
            var couponDenom = $(this).find("input[name='couponDenom']").val();
            var couponType = $(this).find("input[name='couponType']").val();
            var coupon = {};
            coupon['addFlag'] = addFlag;
            coupon['couponThreshold'] = couponThreshold;
            coupon['couponDenom'] = couponDenom;
            coupon['couponType'] = couponType;
            couponList.push(coupon);
        });
    }

    data += "&coupons=" + JSON.stringify(couponList);
    $.post( "/qywxActivity/saveActivityHead", data, function (r) {
        if (r.code === 200) {
            $MB.n_success( r.msg );
            $( "#headId" ).val( r.data );
            if(CURRENT_ACTIVITY_STAGE === 'preheat') {
                preheatClick();
            }
            if(CURRENT_ACTIVITY_STAGE === 'formal') {
                formalClick();
            }
            $( "#basic-add-form" ).find( 'input' ).attr( "disabled", "disabled" );
            $( "#basic-add-form" ).find( 'select' ).attr( "disabled", "disabled" );
            $( '#btn_basic' ).attr( "disabled", "disabled" );
        } else {
            $MB.n_danger( "有错误发生！" );
        }
    } );
}

// 添加商品
$( "#btn_add_shop1" ).click( function () {
    CURRENT_ACTIVITY_TYPE = 'NOTIFY';
    var headId = $( "#headId" ).val();
    $( "#saveActivityProduct" ).attr( "name", "save" );
    $( '#addProductModal' ).modal( 'show' );
    $( "#modalLabel" ).html( '' ).append( '添加商品' );
    $("#shopOffer").html('');
} );

$( "#btn_add_shop2" ).click( function () {
    CURRENT_ACTIVITY_TYPE = 'DURING';
    var headId = $( "#headId" ).val();
    $( "#saveActivityProduct" ).attr( "name", "save" );
    $( '#addProductModal' ).modal( 'show' );
    $( "#modalLabel" ).html( '' ).append( '添加商品' );
    $("#shopOffer").html('');
} );

// 批量添加商品
$( "#btn_batch_upload1" ).click( function () {
    CURRENT_ACTIVITY_TYPE = 'NOTIFY';
    $("#uploadProduct").modal('show');
});

$( "#btn_batch_upload2" ).click( function () {
    CURRENT_ACTIVITY_TYPE = 'DURING';
    $("#uploadProduct").modal('show');
});

// 获取群组列表信息
function getGroupList(stage, type, tableId) {
    var flag = getPlanStatus($("#headId").val(), stage, 'smsContent');
    var headId = $( "#headId" ).val();
    var settings = {
        url: '/qywxActivity/getGroupList',
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
                title: '设置文案',
                align: 'center',
                visible: setContent(stage, flag, tableId),
                formatter: function (value, row, index) {
                    if(tableId === 'table1') {
                        return "<a onclick='selectGroup(\"" + type + "\"," + row['smsTemplateCode'] + ", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-envelope-o'></i>" +
                            "&nbsp;&nbsp;<a onclick='removeSmsSelected(\"NOTIFY\", \""+stage+"\", \"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-refresh'></i></a>" +
                            "</a>";
                    }else {
                        return "<a onclick='selectGroup(\"" + type + "\"," + row['smsTemplateCode'] + ", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-envelope-o'></i>" +
                            "&nbsp;&nbsp;<a onclick='removeSmsSelected(\"DURING\", \""+stage+"\", \"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-refresh'></i></a>" +
                            "</a>";
                    }
                }
            }, {
                field: 'smsTemplateContent',
                title: '文案内容',
                //td宽度及内容超过宽度隐藏
                cellStyle : function(value, row, index, field){
                    return {
                        css: {"min-width": '300px',
                              "max-width":'300px'
                        }
                    }},
                formatter: function (value, row, index) {
                    if(value === null || value.length==0)
                    {
                        return '';
                    }else if(value.length <20) {
                        return "<a style='color: #48b0f7;' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-placement=\"bottom\" data-original-title=\""+value+"\" data-trigger=\"hover\">\n" +
                            value+ "</a>&nbsp;&nbsp;<a class='btn-xs' style='cursor:pointer' onclick='copyToClipboard(this)'>复制</a>";
                    }else
                    {
                        return "<a style='color: #48b0f7;' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-placement=\"bottom\" data-original-title=\""+value+"\" data-trigger=\"hover\">\n" +
                            value.substring(0, 20) + "...</a>&nbsp;&nbsp;<a class='btn-xs' style='cursor:pointer' onclick='copyToClipboard(this)'>复制</a>";
                    }
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

// 判断编辑页的文案设置按钮是否需要展示
function setContent(stage, flag, tableId) {
    if(stage === 'preheat') {
        if(tableId === 'table1' && (preheatNotifyStatus === 'doing' || preheatNotifyStatus === 'done' || preheatNotifyStatus === 'timeout' || !flag)) {
            return false;
        }
        if(tableId === 'table5' && preheatStatus === 'done') {
            return false;
        }
        return true;
    }
    if(stage === 'formal') {
        if(tableId === 'table1' && (formalNotifyStatus === 'doing' || formalNotifyStatus === 'done' || formalNotifyStatus === 'timeout' || !flag)) {
            return false;
        }
        if(tableId === 'table5' && formalStatus === 'done') {
            return false;
        }
        return true;
    }
}

// 选择群组打开model
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
        url: '/qywxActivity/getSmsTemplateList',
        pagination: false,
        singleSelect: true,
        queryParams: function () {
            return {
                isPersonal: $("#sms-form").find("select[name='isPersonal']").val(),
                scene: $("#sms-form").find("select[name='scene']").val(),
                headId: $( "#headId" ).val(),
                stage: CURRENT_ACTIVITY_STAGE,
                type: CURRENT_TYPE
            }
        },
        columns: [
            {
                checkbox: true,
                formatter: function (value, row, index) {
                    if(row.code===CURRENT_TMP_CODE) {
                        return {
                            checked : true
                        };
                    }
                }
            },
            {
                field: 'code',
                title: '文案编号'
            },{
                field: 'content',
                title: '文案内容'
            },{
                field: 'opDt',
                title: '更新时间',
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

/**
 * 保存文案内容
 */
$( "#btn_save_sms").click( function () {
    let operateType = $( this ).attr( "name" );
    let flag = validateTemplate();
    if (flag) {
        //新增
        if(operateType === 'save')
        {
            $MB.confirm( {
                title: '提示:',
                content: '确定保存文案信息？'
            }, function () {
                $.post( '/qywxActivity/saveSmsTemplate', $( "#tmp_add_form" ).serialize(), function (r) {
                    if (r.code === 200) {
                        $MB.n_success( "保存文案信息成功！" );
                    }
                    $( "#sms_add_modal" ).modal( 'hide' );
                    $( "#smstemplate_modal" ).modal( 'show' );
                    getTmpTableData();
                });
            });
        }else
        {
            //更新操作 判断当前文案是否在其它地方已被引用
            $.get("/qywxActivity/checkTemplateUsed", {
                code: $("#code").val(),
                stage: CURRENT_ACTIVITY_STAGE,
                headId: $( "#headId" ).val(),
                type: CURRENT_TYPE
            }, function (r) {
                let confirmmsg='';
                let flag='';
                if(r.code === 200) {
                    if(r.msg==='Y') {
                        confirmmsg='当前文案已在其它活动中被引用，无法直接保存，系统将会为您新增一条文案，确定要继续么？';
                        flag='Y';
                    }else {
                        confirmmsg='确定更新文案?';
                        flag='N';
                    }
                    $MB.confirm( {
                        title: '提示:',
                        content: confirmmsg,
                    }, function () {
                        $.post( '/qywxActivity/updateSmsTemplate', $("#tmp_add_form").serialize()+"&flag="+flag, function (r) {
                            if (r.code === 200) {
                                $MB.n_success(r.msg);
                            }
                            $( "#sms_add_modal" ).modal( 'hide' );
                            $( "#smstemplate_modal" ).modal( 'show' );
                            getTmpTableData();
                        } );
                    });
                }
            });
        }
    }
});

/**
 * 验证文案信息
 * @returns {boolean}
 */
function validateTemplate() {
    let isProdName = $( "input[name='isProdName']:checked" ).val();
    let isProdUrl = $( "input[name='isProdUrl']:checked" ).val();
    let isPrice = $( "input[name='isPrice']:checked" ).val();
    let isProfit = $( "input[name='isProfit']:checked" ).val();
    let smsContent = $('#content').val();

    if (isProdName === undefined) {
        $MB.n_warning( "请选择个性化要素:商品名称！" );
        return false;
    }
    if (isProfit === undefined) {
        $MB.n_warning( "请选择个性化要素:商品利益点！" );
        return false;
    }
    if (isPrice === undefined) {
        $MB.n_warning( "请选择个性化要素:商品最低单价！" );
        return false;
    }
    if (smsContent === '') {
        $MB.n_warning( "文案内容不能为空！" );
        return false;
    }

    //商品详情页url是否可用 Y表示可用
    if('Y'===prodUrlEnabled)
    {
        if(isProdUrl === undefined) {
            $MB.n_warning("请选择个性化要素:商品详情页短链接！");
            return false;
        }

        if(isProdUrl === '1') {
            if(smsContent.indexOf("${商品详情页短链}") === -1) {
                $MB.n_warning("个性化要素:商品详情页短链接为是，文案内容未发现${商品详情页短链}！");
                return false;
            }
        }
        if(isProdUrl === '0') {
            if(smsContent.indexOf("${商品详情页短链}") > -1) {
                $MB.n_warning("个性化要素:商品详情页短链接为否'，文案内容不能出现${商品详情页短链}！");
                return false;
            }
        }
    }else
    {
        if(smsContent.indexOf("${商品详情页短链}") > -1) {
            $MB.n_warning("当前系统配置不允许出现${商品详情页短链}变量！");
            return false;
        }
    }


    // 验证短信内容是否合法
    if(isProdName === '1') {
        if(smsContent.indexOf("${商品名称}") === -1) {
            $MB.n_warning("个性化要素:商品名称为是，文案内容未发现${商品名称}!");
            return false;
        }
    }else {
        if(smsContent.indexOf("${商品名称}") !== -1) {
            $MB.n_warning("个性化要素:商品名称为否，文案内容不能出现${商品名称}!");
            return false;
        }
    }

    if(isProfit === '1') {
        if(smsContent.indexOf("${商品利益点}") === -1) {
            $MB.n_warning("个性化要素:商品利益点为是，文案内容未发现${商品利益点}!");
            return false;
        }
    }else {
        if(smsContent.indexOf("${商品利益点}") !== -1) {
            $MB.n_warning("个性化要素:商品利益点为否'，文案内容不能出现${商品利益点}!");
            return false;
        }
    }

    if(isPrice === '1') {
        if(smsContent.indexOf("${商品最低单价}") === -1) {
            $MB.n_warning("个性化要素:商品最低单价为是，文案内容未发现${商品最低单价}");
            return false;
        }
    }else {
        if(smsContent.indexOf("${商品最低单价}") !== -1) {
            $MB.n_warning("个性化要素:商品最低单价为否，文案内容不能出现${商品最低单价}");
            return false;
        }
    }
    return true;
}

/**
 * 为群组设置模板信息
 * @param type 类型：期间，通知
 */
function setTemplateCode() {
    let groupId = CURRENT_GROUP_ID;
    let selected = $( "#tmpTable").bootstrapTable( 'getSelections' );
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择为群组设置的文案！' );
        return;
    }
    $MB.confirm( {
        title: '提示：',
        content: '确定为当前群组设置选中的文案？'
    }, function () {
        var tmpCode = selected[0].code;
        $.post( "/qywxActivity/setSmsCode", {
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

/**
 * 关闭文案选择面板
 */
function closeTemplateCode()
{
    $( "#smstemplate_modal" ).modal( 'hide' );
    if(CURRENT_TYPE === 'DURING') {
        getGroupList( CURRENT_ACTIVITY_STAGE, "DURING", 'table5');
    }
    if(CURRENT_TYPE === 'NOTIFY') {
        getGroupList( CURRENT_ACTIVITY_STAGE, "NOTIFY", 'table1');
    }
}

// 短信内容和展示的联动
function contextOnChange() {
    let content = $('#content').val() === "" ? "请输入短信内容": signature+$('#content').val()+unsubscribe;
    $("#article").html('').append(content);
}

// 点击编辑文案按钮
function editTemplate() {
    var selected = $( "#tmpTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择需要编辑的文案！' );
        return;
    }
    let code = selected[0].code;
    $.get( "/qywxActivity/getTemplate", {code: code}, function (r) {
        var data = r.data;
        $( "#code" ).val( data.code );
        $( "#content" ).val( data.content );
        $("#article").html('').append(signature+data.content+unsubscribe);
        $( "input[name='isProdName']:radio[value='" + data.isProdName + "']" ).prop( "checked", true );
        $( "input[name='isProdUrl']:radio[value='" + data.isProdUrl + "']" ).prop( "checked", true );
        $( "input[name='isPrice']:radio[value='" + data.isPrice + "']" ).prop( "checked", true );
        $( "input[name='isProfit']:radio[value='" + data.isProfit + "']" ).prop( "checked", true );
        $( "#btn_save_sms" ).attr( 'name', 'update' );

        //根据变量设置一些初始化是否显示的参数
        if('Y'!==prodUrlEnabled)
        {
            $("#produrlComments").addClass('hidden');
            $("#produrlDiv").hide();
        }

        // 统计短信内容的字数
        initGetInputNum();

        $( "#smstemplate_modal" ).modal( 'hide' );
        $( "#sms_add_modal" ).modal( 'show' );
    } );
}

/**
 * 重置新增文案modal
 */
$( "#sms_add_modal" ).on( 'hidden.bs.modal', function () {
    $( "#sms_add_title" ).text( "新增文案" );
    $( "#code" ).val("");
    $( "#content" ).val("");
    $("#snum1").text("0");
    $("#snum2").text("0");
    $("#snum3").text("0");
    $( "input[name='isProdName']" ).removeAttr( "checked" );
    $( "input[name='isProdUrl']" ).removeAttr( "checked" );
    $( "input[name='isPrice']" ).removeAttr( "checked" );
    $( "input[name='isProfit']" ).removeAttr( "checked" );
    $("#article").html('').append("请输入短信内容");
    $( "#smstemplate_modal" ).modal( 'show' );
    $( "#btn_save_sms" ).attr( 'name', 'save' );
    $("#word").text('');
    //根据变量设置一些初始化是否显示的参数
    if('Y'!==prodUrlEnabled)
    {
        $("#produrlComments").addClass('hidden');
        $("#produrlDiv").hide();
    }

} );

// 删除文案
function deleteTemplate() {
    var selected = $( "#tmpTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择需要删除的文案！' );
        return;
    }
    var code = selected[0].code;

    $MB.confirm( {
        title: '提示：',
        content: '确认删除选中的文案？'
    }, function () {
        $.post( "/qywxActivity/deleteActivityTemplate",
            {code: code,
                    headId:$( "#headId" ).val(),
                    stage: CURRENT_ACTIVITY_STAGE,
                    type: CURRENT_TYPE
            }, function (r) {
            if (r.code === 200) {
                $MB.n_success( "删除成功");
                getTmpTableData();
            }else
            {
                $MB.n_warning(r.msg);
            }

        } );
    });
}

// 屏蔽测试
function testSend() {
    let selectRows = $( "#tmpTable" ).bootstrapTable( 'getSelections' );
    if (null == selectRows || selectRows.length == 0) {
        $MB.n_warning('请选择需要测试的文案！');
        return;
    }
    let code = selectRows[0]["code"];
    //根据获取到的数据查询
    $.getJSON( "/qywxActivity/getActivityTemplateContent?code=" + code, function (resp) {
        if (resp.code === 200) {
            $( "#smstemplate_modal" ).modal( 'hide' );
            //更新测试面板
            $("#testSmsCode").val(code);
            $( "#smsContent1" ).val( resp.data );
            $( '#send_modal' ).modal( 'show' );
        }
    } )
}

// 短信model打开
$( "#send_modal" ).on( 'hidden.bs.modal', function () {
    $( "#smstemplate_modal" ).modal( 'show' );
});

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
        url: "/qywxActivity/activityContentTestSend",
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

// 点击添加文案按钮
function addTemplate() {
    $('#smstemplate_modal').modal('hide');
    //根据变量设置一些初始化是否显示的参数
    if('Y'!==prodUrlEnabled)
    {
        $("#produrlComments").addClass('hidden');
        $("#produrlDiv").hide();
    }
    initGetInputNum();
    $('#sms_add_modal').modal('show');
}


// 计算文案的字数和条数
function statTmpContentNum() {
    $("#content").on('input propertychange', function () {
        let smsContent = $('#content').val();
        let y = smsContent.length;
        let m = smsContent.length;
        if(smsContent.indexOf('${商品详情页短链}') > -1) {
            y = y - '${商品详情页短链}'.length + parseInt(PROD_URL_LEN);
            m = m - '${商品详情页短链}'.length;
        }
        if(smsContent.indexOf('${商品名称}') > -1) {
            y = y - '${商品名称}'.length + parseInt(PROD_NAME_LEN);
            m = m - '${商品名称}'.length;
        }
        if(smsContent.indexOf('${商品最低单价}') > -1) {
            y = y - '${商品最低单价}'.length + parseInt(PRICE_LEN);
            m = m - '${商品最低单价}'.length;
        }
        if(smsContent.indexOf('${商品利益点}') > -1) {
            y = y - '${商品利益点}'.length + parseInt(PROFIT_LEN);
            m = m - '${商品利益点}'.length;
        }

        let total_num = y+signatureLen+unsubscribeLen;
        let snum3=0;
        $("#snum1").text(m);
        $("#snum2").text(total_num);

        if(total_num<=70)
        {
            snum3=1;
        }else
        {
            snum3=total_num%67===0?total_num/67:(parseInt(total_num/67)+1);
        }
        //计算文案的条数
        $("#snum3").text(snum3);
    });
}

// 初始化计算文案的字数和条数
function initGetInputNum() {
    let smsContent = $('#content').val();
    let y = smsContent.length;
    let m = smsContent.length;

    if(smsContent.indexOf('${商品详情页短链}') > -1) {
        y = y - '${商品详情页短链}'.length + PROD_URL_LEN;
        m = m - '${商品详情页短链}'.length;
    }
    if(smsContent.indexOf('${商品名称}') > -1) {
        y = y - '${商品名称}'.length + PROD_NAME_LEN;
        m = m - '${商品名称}'.length;
    }
    if(smsContent.indexOf('${商品最低单价}') > -1) {
        y = y - '${商品最低单价}'.length + PRICE_LEN;
        m = m - '${商品最低单价}'.length;
    }
    if(smsContent.indexOf('${商品利益点}') > -1) {
        y = y - '${商品利益点}'.length + PROFIT_LEN;
        m = m - '${商品利益点}'.length;
    }
    let total_num = y+signatureLen+unsubscribeLen;
    let snum3=0;
    $("#snum1").text(m);
    $("#snum2").text(total_num);

    if(total_num<=70)
    {
        snum3=1;
    }else
    {
        snum3=total_num%67===0?total_num/67:(parseInt(total_num/67)+1);
    }
    //计算文案的条数
    $("#snum3").text(snum3);
}

/**
 * 调整转化率
 */
$("#changePlan").click(function () {

    // 判断活动通知商品是否上传
    var data = $("#activityProductTable1").bootstrapTable('getData');
    if(data.length === 0) {
        $MB.n_warning("请先上传有效的活动通知商品！");
    }else {
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

// 转化率数据表
function table3() {
    var settings = {
        url: '/qywxActivity/getCovList',
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
    $.get("/qywxActivity/calculateCov", {headId: headId, defaultCovId: defaultCovId, changedCovId: changedCovId, stage: CURRENT_ACTIVITY_STAGE}, function (r) {
        $("#table4").bootstrapTable('load', r.data);
    });
}

// 转化率改变值的数据表
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
        $.post("/qywxActivity/updateCovInfo", {headId: $("#headId").val(), stage: CURRENT_ACTIVITY_STAGE, covId: covId}, function (r) {
            if(r.code === 200) {
                $MB.n_success("设置成功！");
            }
            covertDataTable();
            $("#plan_change_modal").modal('hide');
        });
    });
}

/**
 * 获取当前活动所处阶段配置的转化率数据
 */
function covertDataTable() {
    var settings = {
        url: '/qywxActivity/getConvertInfo',
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
                align: 'center',
                formatter: function (value, row, index) {
                    if(value !== null && value !== '' && value !== undefined) {
                        return value * 100;
                    }else {
                        return '-';
                    }
                }
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

// 活动通知商品数据下载
$("#btn_download_data1").click(function () {
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定下载商品数据?"
    }, function () {
        $("#btn_download_data1").text("下载中...").attr("disabled", true);
        $.post("/qywxActivity/downloadExcel", {headId: $("#headId").val(), activityStage: CURRENT_ACTIVITY_STAGE
        , activityType: 'NOTIFY'}, function (r) {
            if (r.code === 200) {
                window.location.href = "/common/download?fileName=" + r.msg + "&delete=" + true;
            } else {
                $MB.n_warning(r.msg);
            }
            $("#btn_download_data1").html("").append("<i class=\"fa fa-download\"></i> 下载数据").removeAttr("disabled");
        });
    });
});

// 活动期间商品数据下载
$("#btn_download_data2").click(function () {
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定下载商品数据?"
    }, function () {
        $("#btn_download_data2").text("下载中...").attr("disabled", true);
        $.post("/qywxActivity/downloadExcel", {headId: $("#headId").val(), activityStage: CURRENT_ACTIVITY_STAGE
            , activityType: 'DURING'}, function (r) {
            if (r.code === 200) {
                window.location.href = "/common/download?fileName=" + r.msg + "&delete=" + true;
            } else {
                $MB.n_warning(r.msg);
            }
            $("#btn_download_data2").html("").append("<i class=\"fa fa-download\"></i> 下载数据").removeAttr("disabled");
        });
    });
});

// 移除组上的短信
function removeSmsSelected(type, stage, smsCode, groupId) {
    if(smsCode === null || smsCode === '' || smsCode === 'null') {
        $MB.n_warning("当前群组尚未配置文案！");
        return;
    }
    $MB.confirm({
        title: '提示',
        content: '确定重置当前群组配置的文案吗？'
    }, function () {
        $.post("/qywxActivity/removeSmsSelected", {type: type, headId: $("#headId").val(), stage:stage,groupId: groupId}, function (r) {
            if(r.code === 200) {
                $MB.n_success("当前群组配置的文案已重置！");
                $MB.refreshTable("table1");
                $MB.refreshTable("table5");
            }
        });
    });
}


// 店铺活动机制发生变化
function groupIdChange(dom) {
    $(dom).trigger('blur');
    var val = $(dom).find("option:selected").val();
    switch (val) {
        case "9":
            $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                '<label class="col-md-4 control-label">满件打折件数（件）</label>\n' +
                '<div class="col-md-7">\n' +
                '<input class="form-control" type="text" name="discountCnt"/>\n' +
                '</div></div>').append('<div class="form-group">' +
                '<label class="col-md-4 control-label">满件打折力度（折）</label>\n' +
                '<div class="col-md-7">\n' +
                '<input class="form-control" type="text" name="discountSize"/>\n' +
                '</div></div>');
            break;
        case "14":
            $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                '<label class="col-md-4 control-label">立减特价金额（元）</label>\n' +
                '<div class="col-md-7">\n' +
                '<input class="form-control" type="text" name="discountAmount"/>\n' +
                '</div></div>');
            break;
        default:
            $("#shopOffer").attr("style", "display:none;").html('');
            break;
    }
}

/**
 * 计算商品转化率
 */
function genCovInfo() {
    var len = $("#activityProductTable1").bootstrapTable('getData').length;
    if(len === 0) {
        $MB.n_warning("没有获取到有效的活动通知商品，请先添加商品！");
        return;
    }
    $MB.loadingDesc('show', '正在计算活动转化率....');
    $.get("/qywxActivity/genCovInfo", {headId: $("#headId").val(), activityStage: CURRENT_ACTIVITY_STAGE}, function (r) {
        if(r.data == 1) {
            $MB.n_success("获取数据成功！");
        }else if(r.data == 0){
            $MB.n_warning("获取数据失败！");
        }else {
            $MB.n_warning("未知异常！");
        }
        $MB.refreshTable('covertDataTable');
        $MB.refreshTable('table3');
        $MB.loadingDesc('hide');
    });
}

/**
 * 是否需要计算逻辑
 */
function ifCalculate() {
    $.get("/qywxActivity/ifCalculate", {headId: $("#headId").val(), stage:CURRENT_ACTIVITY_STAGE}, function(r) {
        if(r) {
            $("#calculateCovDataBtn").attr("style", "display:inline;");
        }else {
            $("#calculateCovDataBtn").attr("style", "display:none;");
        }
    });
}

// 添加平台优惠
function addPlatCoupon(idx) {
    var couponType = (idx == 1) ? 'P' : 'S';
    var code = '<div class="row m-t-10">\n' +
        '<input type="hidden" name="couponType" value="'+couponType+'"/>\n' +
        '                        <div class="col-md-4">\n' +
        '                            <div class="form-group">\n' +
        '                                &#12288;&#12288;&#12288;券叠加：<select name="addFlag" class="form-control" style="width: 172px;">\n' +
        '                                    <option value="1">是</option>\n' +
        '                                    <option value="0">否</option>\n' +
        '                                </select>\n' +
        '                            </div>\n' +
        '                        </div>\n' +
        '                        <div class="col-md-4">\n' +
        '                            <div class="form-group">\n' +
        '                                &#12288;&#12288;&#12288;&#12288;门槛：<input type="text" name="couponThreshold" class="form-control"/>\n' +
        '                            </div>\n' +
        '                        </div>\n' +
        '                        <div class="col-md-4">\n' +
        '                            <div class="form-group">\n' +
        '                                &#12288;&#12288;&#12288;&#12288;面额：<input type="text" name="couponDenom" class="form-control"/>\n' +
        '                            </div>\n' +
        '                            &nbsp;&nbsp;<a style="color: #f96868;cursor: pointer;" onclick="deleteCoupon(this)"><i class="fa fa-trash"></i>删除</a>\n' +
        '                        </div>\n' +
        '                    </div>';
    if(idx == 1) {
        if($('#platCouponDiv').find('.row').length == 1) {
            $("#platDiscountItems").show();
            $("#platCouponDiv").attr("style", "border: dashed 1px #e7eaec;margin-top: 10px;padding-bottom: 10px;");
        }
        $("#platDiscountItems").append(code);
    }else {
        if($('#shopCouponDiv').find('.row').length == 1) {
            $("#shopDiscountItems").show();
            $("#shopCouponDiv").attr("style", "border: dashed 1px #e7eaec;margin-top: 10px;padding-bottom: 10px;");
        }
        $("#shopDiscountItems").append(code);
    }
}

// 删除优惠券
function deleteCoupon(dom) {
    $(dom).parent().parent().remove();
    if($('#platCouponDiv').find('.row').length == 1) {
        $('#platCouponDiv').removeAttr("style");
    }
    if($('#shopCouponDiv').find('.row').length == 1) {
        $('#shopCouponDiv').removeAttr("style");
    }
}