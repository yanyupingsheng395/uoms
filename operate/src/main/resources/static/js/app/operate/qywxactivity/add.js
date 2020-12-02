let create_step;
let validatorProduct;
let $activityProductAddForm = $( "#add-product-form" );
var basic_validator;
var $basicAddForm = $( "#basic-add-form" );
let CURRENT_TMP_CODE;
let CURRENT_GROUP_ID;
let CURRENT_TYPE;
let CURRENT_ACTIVITY_TYPE;

$( function () {
    stepInit();
    initDt();
    validBasic();
    validateProductRule();
    $("#activitySource").css('pointer-events', 'none');
    $("#activityflag").css('pointer-events', 'none');
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

// 保存基本信息之前的校验
function beforeSave(){
    if(operate_type === 'save') {
        var validator = $basicAddForm.validate();
        if (validator.form() && validBasicDt() && validDiscount()) {
            $MB.confirm( {
                title: '提示:',
                content: '确定保存信息？'
            }, function () {
                saveActivityHead();
            } );
        }
    }
    if(operate_type === 'update') {
        stepBreak(1);
    }
}

// 验证平台优惠 + 补贴优惠
function validDiscount() {
    var shopDiscount = $("select[name='shopDiscount']").find("option:selected").val();
    var flag = true;
    if(shopDiscount === 'Y') {
        $("#shopDiscountItems").find(".row").each(function (v, k) {
            var addFlag = $(this).find("select[name='addFlag']").find("option:selected").val();
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

        $("#creatFormal").attr('onclick', 'formalClick()');
    }
    if(index == 1) {
        create_step.setActive(1);
        getProductInfo('NOTIFY', 'activityProductTable1');
        getProductInfo('DURING', 'activityProductTable');
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:block;");
        $("#step3").attr("style", "display:none;");
    }
    if(index == 2) {
        $.get("/qywxActivity/validProduct", {headId: $("#headId").val()}, function (r) {
            if(r.code === 200) {
                if(r.data > 0) {
                    $MB.n_warning("存在校验不通过的商品！");
                }else {
                    $.get("/qywxActivity/validUserGroup", {headId: $("#headId").val()}, function (r) {
                        if(r.code === 200) {
                            create_step.setActive(2);
                            $("#step1").attr("style", "display:none;");
                            $("#step2").attr("style", "display:none;");
                            $("#step3").attr("style", "display:block;");
                            createActivity();
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
            },
            spCouponThreshold: {
                required: function () {
                    return $("#spCouponFlag").val() === '1';
                }
            },
            spCouponDenom: {
                required: function () {
                    return $("#spCouponFlag").val() === '1';
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
            },
            spCouponThreshold: {
                required: icon + '请输入单品券门槛'
            },
            spCouponDenom: {
                required: icon + '请输入单品券面额'
            }
        }
    } );
}

// 查询活动通知商品信息
function searchActivityProduct1() {
    $MB.refreshTable( 'activityProductTable1' );
}
function searchActivityProduct() {
    $MB.refreshTable( 'activityProductTable' );
}

// 重置查询商品条件
function resetActivityProduct1() {
    $( "#productId1" ).val( "" );
    $( "#productName1" ).val( "" );
    $( "#groupId1" ).find( "option:selected" ).removeAttr( "selected" );
    $MB.refreshTable( 'activityProductTable1' );
}
function resetActivityProduct() {
    $( "#productId" ).val( "" );
    $( "#productName" ).val( "" );
    $( "#groupId" ).find( "option:selected" ).removeAttr( "selected" );
    $MB.refreshTable( 'activityProductTable' );
}

function addPlatCoupon(idx) {
    var couponType = (idx == 1) ? 'P' : 'S';
    var code = '<div class="row m-t-10">\n' +
        '<input type="hidden" name="couponType" value="'+couponType+'"/>\n' +
        '                        <div class="col-md-4">\n' +
        '                            <div class="form-group">\n' +
        '                                &#12288;券叠加：<select name="addFlag" class="form-control" style="width: 172px;">\n' +
        '                                    <option value="1">是</option>\n' +
        '                                    <option value="0">否</option>\n' +
        '                                </select>\n' +
        '                            </div>\n' +
        '                        </div>\n' +
        '                        <div class="col-md-4">\n' +
        '                            <div class="form-group">\n' +
        '                                &#12288;&#12288;门槛：<input type="text" name="couponThreshold" class="form-control"/>\n' +
        '                            </div>\n' +
        '                        </div>\n' +
        '                        <div class="col-md-4">\n' +
        '                            <div class="form-group">\n' +
        '                                &#12288;&#12288;面额：<input type="text" name="couponDenom" class="form-control"/>\n' +
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


// 商品数据下载
$( "#btn_download" ).click( function () {
    window.location.href = "/qywxActivity/downloadFile";
} );

//清除页面数据
function clearData(){
    $("#spCouponDiv").attr("style", "display:none;");
    $("input[name='spCouponThreshold']").val('');
    $("input[name='spCouponDenom']").val('');
    $("#activityRule").val("");
    $("#shopAcvity").attr("style", "display:block;");
    $("#chooseSp").attr("style", "display:block;");
    $("#shopOffer").html("");
}

$("#closeEditProduct").click(function () {
    clearData();
})

// 添加活动商品
$( "#saveActivityProduct" ).click( function () {
    let validator = $activityProductAddForm.validate();
    let flag = validator.form();
    //if (flag) {
        let operate = $( "#saveActivityProduct" ).attr( "name" );
        if (operate === "save") {
            $.post( "/qywxActivity/saveActivityProduct", $( "#add-product-form" ).serialize() + "&headId=" + $( "#headId" ).val()+ "&activityType=" + CURRENT_ACTIVITY_TYPE,  function (r) {
                if (r.code === 200) {
                    $MB.n_success( "添加商品成功！" );
                    if(CURRENT_ACTIVITY_TYPE === 'NOTIFY') {
                        getProductInfo('NOTIFY', 'activityProductTable1');
                    }
                    if(CURRENT_ACTIVITY_TYPE === 'DURING') {
                        getProductInfo('DURING', 'activityProductTable');
                    }
                   // getProductInfo();
                    $MB.closeAndRestModal( "addProductModal" );
                    clearData();
                } else {
                    $MB.n_danger( r.msg );
                }
            } );
        }
        if (operate === 'update') {
            $.post( "/qywxActivity/updateActivityProduct", $( "#add-product-form" ).serialize() + "&headId=" + $( "#headId" ).val(), function (r) {
                if (r.code === 200) {
                    $MB.n_success( "更新商品成功！");
                    if(CURRENT_ACTIVITY_TYPE === 'NOTIFY') {
                        getProductInfo('NOTIFY', 'activityProductTable1');
                    }
                    if(CURRENT_ACTIVITY_TYPE === 'DURING') {
                        getProductInfo('DURING', 'activityProductTable');
                    }
                   // getProductInfo();
                    $MB.closeAndRestModal( "addProductModal" );
                    clearData();
                } else {
                    $MB.n_danger(r.msg);
                }
            } );
        }
   // }
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
$( "#btn_edit_shop" ).click( function () {
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
        tableId = 'activityProductTable';
    }
    $("#modalLabel").html('').append('修改商品');
    $( "#saveActivityProduct" ).attr( "name", "update" );
    let selected = $( "#"+tableId).bootstrapTable( 'getSelections' );
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
                    $("#chooseSp").attr("style","display:none");
                    $("#spCouponDiv").hide();
                    break;
                case "14":
                    $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                        '<label class="col-md-4 control-label">立减特价金额（元）</label>\n' +
                        '<div class="col-md-7">\n' +
                        '<input class="form-control" type="text" name="discountAmount" value="'+data['discountAmount']+'"/>\n' +
                        '</div></div>');
                    $("#chooseSp").attr("style","display:none");
                    $("#spCouponDiv").hide();
                    break;
                default:
                    $("#shopOffer").attr("style", "display:none;").html('');
                    $("#chooseSp").attr("style","display:none");
                    $("#spCouponDiv").hide();
                    break;
            }
            $( "#addProductModal" ).modal( 'show' );

            if(data['spCouponFlag']=="0"){
                $("#spCouponDiv").hide();
            }else if(data['spCouponFlag']=="1"){
                $("#spCouponDiv").show();
                $("#shopOffer").html("");
                $("#shopAcvity").attr("style","display:none");
                $("#chooseSp").attr("style","display:block");
            }
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
                        $MB.refreshTable('activityProductTable');
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
$("#btn_delete_shop").click(function () {
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
        tableId = 'activityProductTable';
    }
    var selected = $("#"+tableId).bootstrapTable('getSelections');
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
                    }else if(tableId === 'activityProductTable') {
                        return $( "#productId" ).val();
                    }
                },
                productName:function() {
                    if(tableId === 'activityProductTable1') {
                        return $( "#productName1" ).val();
                    }else if(tableId === 'activityProductTable') {
                        return $( "#productName" ).val();
                    }
                },
                groupId: function() {
                    if(tableId === 'activityProductTable1') {
                        return $("#groupId1").find("option:selected").val();
                    }else if(tableId === 'activityProductTable') {
                        return $("#groupId").find("option:selected").val();
                    }
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
                title: title+'商品体现利益点',
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
    $( "#" + tableId  ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

// 提交计划 type:NOTIFY 活动通知，DURING 活动期间
function submitActivity(type) {
    let headId = $("#headId").val();
    $.get("/qywxActivity/validSubmit", {headId: $("#headId").val(), type: type}, function (r) {
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
                    if(formalStatus !== 'edit' &&  formalStatus !== 'edit' && formalStatus !== '' && formalStatus !== null && formalStatus !== undefined) {
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
            });
        }else {
            $MB.n_warning(r.msg);
        }
    });
}
// 保存计划
function submitData(headId, type) {
    $.post("/qywxActivity/submitActivity", {headId: headId, operateType: operate_type, type: type}, function (r) {
        if(r.code === 200) {
            $MB.n_success("保存计划成功！");
        }else {
            $MB.n_danger("保存计划失败！");
        }
    });
}
// 正式和预售页面之间的跳转
function createActivity() {
    getGroupList( 'NOTIFY', 'table1');
    getGroupList('DURING', 'table5');

    // 根据不同的状态禁用相关通知的按钮
    if(formalNotifyStatus === 'done' || formalNotifyStatus === 'timeout') {
        $("#changePlan").attr("disabled", "disabled");
        $("#notifySaveBtn").attr("disabled", "disabled");
    }else {
        var data = false;
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

// 获取plan的状态
function getPlanStatus(headId, remark) {
    var res = $.ajax({
        url: '/qywxActivityPlan/getPlanStatus',
        data: {headId: headId},
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
    init_date( 'formalNotifyDt', 'yyyy-mm-dd', 0, 2, 0 );
    init_date_begin( 'formalStartDt', 'formalEndDt', 'yyyy-mm-dd', 0, 2, 0 );
    init_date_end( 'formalStartDt', 'formalEndDt', 'yyyy-mm-dd', 0, 2, 0 );
    var date = new Date();
    $( "#formalStartDt" ).datepicker( 'setStartDate', date );
    $( "#formalEndDt" ).datepicker( 'setStartDate', date);
    $( "#formalNotifyDt" ).datepicker( 'setStartDate', date );
}

// 验证活动基本信息的表单
function validBasic() {
    var icon = "<i class='fa fa-close'></i> ";
    basic_validator = $basicAddForm.validate( {
        rules: {
            activityName: {
                required: true
            },
            formalNotifyDt: {
                required: true
            },
            formalStartDt: {
                required: true
            },
            formalEndDt: {
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
            formalNotifyDt: {
                required: icon + "请输入通知时间"
            },
            formalStartDt: {
                required: icon + "请输入开始时间"
            },
            formalEndDt: {
                required: icon + "请输入结束时间"
            }
        }
    } );
}

// 验证基本信息的时间
function validBasicDt() {
    let formalStartDt = $( "#formalStartDt" ).val();
    var formalNotifyDt = $( "#formalNotifyDt" ).val();
    var flag = new Date( String( formalStartDt ) ) > new Date( String( formalNotifyDt ) );
    if (!flag) {
        $MB.n_warning( "正式提醒时间必须小于正式开始时间！" );
        return false;
    }
    return flag;
}


// 保存基本信息
function saveActivityHead() {
    var data = $basicAddForm.serialize();
    var couponList = [];
    var shopDiscount = $("select[name='shopDiscount']").find("option:selected").val();
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
            $( "#basic-add-form" ).find( 'input' ).attr( "disabled", "disabled" );
            $( "#basic-add-form" ).find( 'select' ).attr( "disabled", "disabled" );
            $( '#btn_basic' ).attr( "disabled", "disabled" );
            stepBreak(1);
        } else {
            $MB.n_danger( "有错误发生！" );
        }
    } );
}

function spCouponFlagChange(dom) {
    var val = $(dom).find("option:selected").val();
    if(val === '1') {
        $("#spCouponDiv").attr("style", "display:block;");
        $("#shopAcvity").attr("style", "display:none;");
        $("#activityRule").val("14");
        $("#shopOffer").html("");
    }else {
        $("#spCouponDiv").attr("style", "display:none;");
        $("input[name='spCouponThreshold']").val('');
        $("input[name='spCouponDenom']").val('');
        $("#activityRule").val("");
        $("#shopAcvity").attr("style", "display:block;");
    }
}
//添加商品
$( "#btn_add_shop1" ).click( function () {
    CURRENT_ACTIVITY_TYPE = 'NOTIFY';
    var headId = $( "#headId" ).val();
    $( "#saveActivityProduct" ).attr( "name", "save" );
    $( '#addProductModal' ).modal( 'show' );
    $( "#modalLabel" ).html( '' ).append( '添加商品' );
    $("#shopOffer").html('');
} );

$( "#btn_add_shop" ).click( function () {
    CURRENT_ACTIVITY_TYPE = 'DURING';
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
// 批量添加商品
$( "#btn_batch_upload" ).click( function () {
    CURRENT_ACTIVITY_TYPE = 'DURING';
    $("#uploadProduct").modal('show');
});

// 获取群组列表信息
function getGroupList(type, tableId) {
    var headId = $( "#headId" ).val();
    var settings = {
        url: '/qywxActivity/getGroupList',
        pagination: false,
        singleSelect: false,
        queryParams: function () {
            return {
                headId: headId,
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
                title: '设置消息',
                align: 'center',
                visible: setContent(tableId),
                formatter: function (value, row, index) {
                    if(tableId === 'table1') {
                        return "<a onclick='selectGroup(\"" + type + "\"," + row['smsTemplateCode'] + ", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-envelope-o'></i>" +
                            "&nbsp;&nbsp;<a onclick='removeSmsSelected(\"NOTIFY\", \"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-refresh'></i></a>" +
                            "</a>";
                    }else {
                        return "<a onclick='selectGroup(\"" + type + "\"," + row['smsTemplateCode'] + ", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-envelope-o'></i>" +
                            "&nbsp;&nbsp;<a onclick='removeSmsSelected(\"DURING\", \"" + row['smsTemplateCode'] + "\", \"" + row['groupId'] + "\")' style='color:#333;'><i class='fa fa-refresh'></i></a>" +
                            "</a>";
                    }
                }
            }, {
                field: 'smsTemplateContent',
                title: '消息内容',
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
function setContent(flag, tableId) {
    if(tableId === 'table1' && (formalNotifyStatus === 'doing' || formalNotifyStatus === 'done' || formalNotifyStatus === 'timeout')) {
        return false;
    }
    if(tableId === 'table5' && (formalStatus === 'doing' || formalStatus === 'done' || formalStatus === 'timeout')) {
        return false;
    }
    return true;
}

// 选择群组打开model
function selectGroup(type, tmpCode, groupId) {
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
                groupId: CURRENT_GROUP_ID,
                headId: $( "#headId" ).val()
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
                title: '编号',
                visible: false
            },{
                field: 'content',
                title: '消息内容'
            },{
                field: 'insertDt',
                title: '创建时间',
                align: 'center'
            },{
                field: 'usedDays',
                title: '使用天数',
                align: 'center'
            }
        ], onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $( "#tmpTable").bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

/**
 * 点击关闭按钮，清楚输入框内容
 */
function clearcontent(){
    $("#content_1").html("");
    $("#content").val("");
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
                    //点击保存按钮，清除输入框中内容
                    clearcontent();
                });
            });
        }else
        {
            //更新操作 判断当前文案是否在其它地方已被引用
            $.get("/qywxActivity/checkTemplateUsed", {
                code: $("#code").val(),
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
                            //点击保存按钮，清除输入框中内容
                            clearcontent();
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
        $MB.n_warning( "消息内容不能为空！" );
        return false;
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
        $MB.n_warning( '请选择为群组设置的消息！' );
        return;
    }
    $MB.confirm( {
        title: '提示：',
        content: '确定为当前群组设置选中的消息？'
    }, function () {
        var tmpCode = selected[0].code;
        $.post( "/qywxActivity/setSmsCode", {
            groupId: groupId,
            tmpCode: tmpCode,
            headId: $( "#headId" ).val(),
            type: CURRENT_TYPE
        }, function (r) {
            if (r.code === 200) {
                $MB.n_success( "当前群组设置文案成功！" );
            }
            $( "#smstemplate_modal" ).modal( 'hide' );
            getGroupList("DURING", 'table5');
            getGroupList( "NOTIFY", 'table1');
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
        getGroupList("DURING", 'table5');
    }
    if(CURRENT_TYPE === 'NOTIFY') {
        getGroupList( "NOTIFY", 'table1');
    }
}
// 短信内容和展示的联动
function contextOnChange() {
    let content = $('#content').val() === "" ? "请输入消息内容": $('#content').val();
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
        $("#article").html('').append(data.content);
        $( "#content" ).val( data.content );
        $( "input[name='isProdName']:radio[value='" + data.isProdName + "']" ).prop( "checked", true );
        $( "input[name='isProdUrl']:radio[value='" + data.isProdUrl + "']" ).prop( "checked", true );
        $( "input[name='isPrice']:radio[value='" + data.isPrice + "']" ).prop( "checked", true );
        $( "input[name='isProfit']:radio[value='" + data.isProfit + "']" ).prop( "checked", true );
        $( "#btn_save_sms" ).attr( 'name', 'update' );

        $( "#smstemplate_modal" ).modal( 'hide' );
        $( "#sms_add_modal" ).modal( 'show' );

        var textval=data.content;
        var textarr=textval.split('\n');
        var val="";
        for (var i = 0; i < textarr.length; i++) {
            var obj= document.createElement("div");
            val+="<div>"+textarr[i].trim()+"</div>";
        }
        $("#content_1").html(val);
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
    $('#sms_add_modal').modal('show');
}

// 活动商品数据下载
$("#btn_download_data1").click(function () {
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定下载商品数据?"
    }, function () {
        $("#btn_download_data1").text("下载中...").attr("disabled", true);
        $.post("/qywxActivity/downloadExcel", {headId: $("#headId").val(), activityType: 'NOTIFY'}, function (r) {
            if (r.code === 200) {
                window.location.href = "/common/download?fileName=" + r.msg + "&delete=" + true;
            } else {
                $MB.n_warning(r.msg);
            }
            $("#btn_download_data1").html("").append("<i class=\"fa fa-download\"></i> 下载数据").removeAttr("disabled");
        });
    });
});

$("#btn_download_data").click(function () {
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定下载商品数据?"
    }, function () {
        $("#btn_download_data").text("下载中...").attr("disabled", true);
        $.post("/qywxActivity/downloadExcel", {headId: $("#headId").val(), activityType: 'DURING'}, function (r) {
            if (r.code === 200) {
                window.location.href = "/common/download?fileName=" + r.msg + "&delete=" + true;
            } else {
                $MB.n_warning(r.msg);
            }
            $("#btn_download_data").html("").append("<i class=\"fa fa-download\"></i> 下载数据").removeAttr("disabled");
        });
    });
});

// 移除组上的短信
function removeSmsSelected(type, smsCode, groupId) {
    if(smsCode === null || smsCode === '' || smsCode === 'null') {
        $MB.n_warning("当前群组尚未配置文案！");
        return;
    }
    $MB.confirm({
        title: '提示',
        content: '确定重置当前群组配置的消息吗？'
    }, function () {
        $.post("/qywxActivity/removeSmsSelected", {type: type, headId: $("#headId").val(),groupId: groupId}, function (r) {
            if(r.code === 200) {
                $MB.n_success("当前群组配置的消息已重置！");
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
            $("#chooseSp").attr("style", "display:none;");
            break;
        case "14":
            $("#shopOffer").attr("style", "display:block;").html('').append('<div class="form-group">' +
                '<label class="col-md-4 control-label">立减特价金额（元）</label>\n' +
                '<div class="col-md-7">\n' +
                '<input class="form-control" type="text" name="discountAmount"/>\n' +
                '</div></div>');
            $("#chooseSp").attr("style", "display:none;");
            break;
        case "13":
            $("#shopOffer").attr("style", "display:none;").html('');
            $("#chooseSp").attr("style", "display:none;");
            break;
        default:
            $("#shopOffer").attr("style", "display:none;").html('');
            $("#chooseSp").attr("style", "display:block;");
            break;
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