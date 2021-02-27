init_date('endDate', 'yyyy-mm-dd', 0, 2, 0);
//getCouponData();
var $qywxManualForm = $( "#welcomeFormData" );
var $qywxManualForm_validator;

function getViewData() {
    var policyType = $("input[name='policyType']:checked").val();
    var policyTypeTmp = $("input[name='policyTypeTmp']:checked").val();
    if(policyType!='A'){
        $MB.n_danger("请选择'智能策略'！");
        return ;
    }
    if(policyTypeTmp==""||policyTypeTmp==null){
        $MB.n_danger("请选择配置策略方案！");
        return ;
    }
    if(policyType === 'A') {
        if(policyTypeTmp === 'COUPON') {
            $( "#couponModal" ).modal( 'show' );
        }
        if(policyTypeTmp === 'PRODUCT') {
            $( "#productModal" ).modal( 'show' );
        }
    }
}

// 点击不同策略
function policyClick() {
    var policyType = $("input[name='policyType']:checked").val();
    if(policyType === 'M') {
        $('#intelPolicy').attr('style', 'display:none;');
        $('#configBtn').attr('style', 'display:none;');
        $("#minititle").removeAttr("readonly");
        $("#miniaddress").removeAttr("readonly");
    }
    if(policyType === 'A') {
        $('#intelPolicy').attr('style', 'display:block;');
        $('#configBtn').attr('style', 'display:inline-block;')
        $("#minititle").attr({"readonly":"readonly"});
        $("#miniaddress").attr({"readonly":"readonly"});
    }
}

function getCouponData() {
    var settings = {
        url: "/qywxWelcomeCoupon/getTableDataList",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 5,
        pageList: [5, 15, 25, 50],
        clickToSelect: true,
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset
            }
        },
        columns: [{
            checkbox: true
        },{
            field: 'couponDeno',
            align: "center",
            title: '优惠券名称',
            formatter: function (value, row, index) {
                if(value !== '' && value !== null) {
                    return value + '元';
                }else {
                    return '-';
                }
            }
        },{
            field: 'couponScene',
            align: "center",
            title: '类型',
        },{
            field: 'couponThreshold',
            align: "center",
            title: '门槛（元）',
        },{
            field: 'couponDeno',
            align: "center",
            title: '面额（元）',
        },{
            field: 'couponUrl',
            align: "center",
            title: '地址',
        },{
            field: 'endDate',
            align: "center",
            title: '有效截止日期',
        },{
            field: 'checkFlag',
            align: "center",
            title: '校验结果',
            formatter: function (value, row, index) {
                if(value === 'Y') {
                    return '<span class="badge bg-primary">校验通过</span>';
                }else if(value === 'N') {
                    return '<span class="badge bg-danger">校验失败</span>';
                }
            }
        },{
            field: 'checkComment',
            align: "center",
            title: '失败原因',
        }]
    };
    $("#couponDataTable").bootstrapTable(settings);
}

//getProductData();
function getProductData() {
    var settings = {
        url: "/qywxWelcomeProduct/getTableDataList",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 5,
        pageList: [5, 15, 25, 50],
        clickToSelect: true,
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset
            }
        },
        columns: [{
            checkbox: true
        },{
            field: 'productId',
            align: "center",
            title: '商品ID',
        },{
            field: 'productName',
            align: "center",
            title: '商品名称',
        },{
            field: 'price',
            align: "center",
            title: '售价（元）',
        },{
            field: 'productType',
            align: "center",
            title: '商品类型',
        },{
            field: 'productUrl',
            align: "center",
            title: '地址',
        },{
            field: 'checkFlag',
            align: "center",
            title: '校验结果',
            formatter: function (value, row, index) {
                if(value === 'Y') {
                    return '<span class="badge bg-primary">校验通过</span>';
                }else if(value === 'N') {
                    return '<span class="badge bg-danger">校验失败</span>';
                }
            }
        },{
            field: 'checkContent',
            align: "center",
            title: '失败原因',
        }]
    };
    $("#productDataTable").bootstrapTable(settings);
}

function addProductModal() {
    $("#saveProductBtn").attr('name', 'save');
    $("#addProductTitle").html('新增商品');
    $("#addProductModal").modal('show');
    $("#productModal").modal('hide');

    $("#addProductForm").find('input[name="productId"]').val('');
    $("#addProductForm").find('input[name="productName"]').val('');
    $("#addProductForm").find('input[name="price"]').val('');
    $("#addProductForm").find('input[name="productType"]').val('');
    $("#addProductForm").find('input[name="productUrl"]').val('');
}

function addCouponModal() {
    $("#saveCouponBtn").attr('name', 'save');
    $("#addCouponTitle").html('新增优惠券');
    $("#addCouponModal").modal('show');
    $("#couponModal").modal('hide');

    $("#addCouponForm").find('input[name="couponId"]').val('');
    $("#addCouponForm").find('input[name="couponDeno"]').val('');
    $("#addCouponForm").find('input[name="couponThreshold"]').val('');
    $("#addCouponForm").find('input[name="endDate"]').val('');
    $("#addCouponForm").find('input[name="couponUrl"]').val('');
}

// 获取图片表单数据
function getTabFormData() {
    var tabFormData = '';
    var id = $("#myTabs").find("li.active a").attr('id');
    if(id === 'text-tab') {

    }else if(id === 'image-tab') {
        var selected = $("#imageTable1").bootstrapTable('getSelections');
        if(selected.length > 0) {
            tabFormData = "&picId=" + selected[0]['imgId'] + "&picUrl=" + selected[0]['imgUrl'];
        }
    }else if(id === 'web-tab') {
        var selected = $("#imageTable2").bootstrapTable('getSelections');
        if(selected.length > 0) {
            tabFormData = "&linkPicurl=" + selected[0]['imgUrl'];
        }
    }else if(id === 'miniprogram-tab') {

    }
    return tabFormData;
}

function saveDataWel(dom) {
    var operate = $(dom).attr('name');
    validWelcomeForm();
    var validator = $qywxManualForm.validate();
    if(operate === 'save') {
        if(validator.form()) {
            $MB.confirm({
                title: "<i class='mdi mdi-alert-outline'></i>提示：",
                content: "确定保存当前记录?"
            }, function () {
                $.post("/welcome/saveData", $("#welcomeFormData").serialize() + getTabFormData(), function (r) {
                    if(r.code == 200) {
                        $MB.n_success("保存成功！");
                        $("#qywxWelcomeId").val(r.data);
                    }else {
                        $MB.n_danger("保存失败！");
                    }
                });
            });
        }
    }

    if(operate === 'update') {
        if(validator.form()) {
            $MB.confirm({
                title: "<i class='mdi mdi-alert-outline'></i>提示：",
                content: "确定更新当前记录?"
            }, function () {
                $.post("/welcome/updateData", $("#welcomeFormData").serialize() + getTabFormData(), function (r) {
                    if(r.code == 200) {
                        $MB.n_success("更新成功！");
                    }else {
                        $MB.n_danger("更新失败！");
                    }
                });
            });
        }
    }
}

function saveProductData() {
    var flag = validator_product.form();
    if(flag) {
        var operate = $("#saveProductBtn").attr('name');
        if(operate === 'save') {
            $.post("/qywxWelcomeProduct/saveData", $("#addProductForm").serialize(), function (r) {
                if(r.code == 200) {
                    $MB.n_success("保存成功！");
                }else {
                    $MB.n_danger("保存失败！");
                }
                $("#addProductModal").modal('hide');
                $("#productModal").modal('show');
                $MB.refreshTable('productDataTable');
            });
        }else {
            $.post("/qywxWelcomeProduct/updateData", $("#addProductForm").serialize(), function (r) {
                if(r.code == 200) {
                    $MB.n_success("更新成功！");
                }else {
                    $MB.n_danger("更新失败！");
                }
                $("#addProductModal").modal('hide');
                $("#productModal").modal('show');
                $MB.refreshTable('productDataTable');
            });
        }
    }
}

function beforeUpdateProduct() {
    var selected = $("#productDataTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        $.get("/qywxWelcomeProduct/getDataById", {productId: selected[0]['productId']}, function (r) {
            if(r.code == 200) {
                var data = r.data;
                addProductModal();
                $("#addProductForm").find('input[name="productId"]').val(data['productId']).attr('readOnly', 'readOnly');
                $("#addProductForm").find('input[name="productName"]').val(data['productName']);
                $("#addProductForm").find('input[name="price"]').val(data['price']);
                $("#addProductForm").find('input[name="productType"]').val(data['productType']);
                $("#addProductForm").find('input[name="productUrl"]').val(data['productUrl']);
                $("#saveProductBtn").attr('name', 'update');
                $("#addProductTitle").html('修改商品');
            }
        });
    }
}

// 删除商品数据
function deleteProduct() {
    var selected = $("#productDataTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        $MB.confirm({
            title: "<i class='mdi mdi-alert-outline'></i>提示：",
            content: "确定删除选中的记录?"
        }, function () {
            $.post("/qywxWelcomeProduct/deleteProductById", {productId: selected[0]['productId']}, function(r) {
                if(r.code == 200) {
                    $MB.n_success("删除成功！");
                    $MB.refreshTable('productDataTable');
                }
            });
        });
    }
}

function saveCouponData() {
    var flag = validator_coupon.form();
    if(flag) {
        var operate = $("#saveCouponBtn").attr('name');
        if(operate === 'save') {
            $.post("/qywxWelcomeCoupon/saveData", $("#addCouponForm").serialize(), function (r) {
                if(r.code == 200) {
                    $MB.n_success("保存成功！");
                }else {
                    $MB.n_danger("保存失败！");
                }
                $("#addCouponModal").modal('hide');
                $("#couponModal").modal('show');
                $MB.refreshTable('couponDataTable');
            });
        }else {
            $.post("/qywxWelcomeCoupon/updateData", $("#addCouponForm").serialize(), function (r) {
                if(r.code == 200) {
                    $MB.n_success("更新成功！");
                }else {
                    $MB.n_danger("更新失败！");
                }
                $("#addCouponModal").modal('hide');
                $("#couponModal").modal('show');
                $MB.refreshTable('couponDataTable');
            });
        }
    }
}

function beforeUpdateCoupon() {
    var selected = $("#couponDataTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        $.get("/qywxWelcomeCoupon/getDataById", {couponId: selected[0]['couponId']}, function (r) {
            if(r.code == 200) {
                var data = r.data;
                addCouponModal();
                $("#addCouponForm").find('input[name="couponId"]').val(data['couponId']);
                $("#addCouponForm").find('input[name="couponDeno"]').val(data['couponDeno']);
                $("#addCouponForm").find('input[name="couponThreshold"]').val(data['couponThreshold']);
                $("#addCouponForm").find('input[name="endDate"]').val(data['endDate']);
                $("#addCouponForm").find('input[name="couponUrl"]').val(data['couponUrl']);
                $("#saveCouponBtn").attr('name', 'update');
                $("#addCouponTitle").html('修改优惠券');
            }
        });
    }
}

// 删除商品数据
function deleteCoupon() {
    var selected = $("#couponDataTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        $MB.confirm({
            title: "<i class='mdi mdi-alert-outline'></i>提示：",
            content: "确定删除选中的记录?"
        }, function () {
            $.post("/qywxWelcomeCoupon/deleteCouponById", {couponId: selected[0]['couponId']}, function(r) {
                if(r.code == 200) {
                    $MB.n_success("删除成功！");
                    $MB.refreshTable('couponDataTable');
                }
            });
        });
    }
}
// wxPreview
 $("#textContent1").bind('input', function () {
    var content = $(this).val();
    content=content.replace(/\r\n/g,'<br/>').replace(/\n/g,'<br/>').replace(/\s/g,' ');
    if(content.length >= 155) {
        content = content.substr(0, 154) + "&nbsp;...";
    }
    $('#wxPreview').html('').append(content==='' ? '请输入欢迎语内容':content);

    var height = document.getElementById('preview').offsetHeight;
    height = height + 60;
    $("#chatSend").attr('style', 'position: relative;margin-top:' + height + 'px;');
});


$("#myTabs").on('shown.bs.tab', function (e) {
    var height = document.getElementById('preview').offsetHeight;
    height = height + 60;
    // chatDiv
    if(e.target.id === 'image-tab') {
        $("#wxChat").attr("style", "height: auto;width: 100%;background-color: rgb(235,235,235);border-bottom-left-radius: 10px;border-bottom-right-radius: 10px;padding-bottom: 15px");
        $("#chatDiv").html('<div style="overflow:hidden;">\n' +
            '<div id="chatSend" class="send" style="position: relative;margin-top: ' + height + 'px;">\n' +
            '<div class="arrow"></div>\n' +
            '<div style=" width: 230px;height: 170px;background-image: url(https://goss.veer.com/creative/vcg/veer/1600water/veer-158345394.jpg);-webkit-background-size:cover;-moz-background-size: cover;-o-background-size: cover;background-size: cover;"></div>\n' +
            '</div>\n' +
            '</div>');
    }else if(e.target.id === 'web-tab'){
        $("#wxChat").attr("style", "height: auto;width: 100%;background-color: rgb(235,235,235);border-bottom-left-radius: 10px;border-bottom-right-radius: 10px;padding-bottom: 15px");
        $("#chatDiv").html('<div style="overflow:hidden;">\n' +
            '<div id="chatSend" class="send" style="position: relative;margin-top: ' + height + 'px;">\n' +
            '<div class="arrow"></div>\n' +
            '<div style=" width: 230px;height: 170px;background-image: url(https://goss.veer.com/creative/vcg/veer/1600water/veer-141727262.jpg);-webkit-background-size:cover;-moz-background-size: cover;-o-background-size: cover;background-size: cover;"></div>\n' +
            '</div>\n' +
            '</div>');
    }else if(e.target.id === 'miniprogram-tab') {
        $("#wxChat").attr("style", "height: auto;width: 100%;background-color: rgb(235,235,235);border-bottom-left-radius: 10px;border-bottom-right-radius: 10px;padding-bottom: 15px");
        $("#chatDiv").html('<div style="overflow:hidden;">\n' +
            '<div id="chatSend" class="send" style="position: relative;margin-top: ' + height + 'px;">\n' +
            '<div class="arrow"></div>\n' +
            '<p class="h5">送你一张五元券</p>\n' +
            '<div style=" width: 230px;height: 170px;background-image: url(https://goss.veer.com/creative/vcg/veer/800water/veer-307142650.jpg);-webkit-background-size:cover;-moz-background-size: cover;-o-background-size: cover;background-size: cover;"></div>\n' +
            '<hr style="background: #b0b0b0;margin-top: 6px;margin-bottom: 6px;"/>\n' +
            '<p style="margin-bottom: 0px;color: #838383"><i class="fa fa-skyatlas" style="color: #1296db"></i>&nbsp;小程序</p>\n' +
            '</div>\n' +
            '</div>');
    }
});

// 选定优惠券
function setCoupon() {
    var selected = $("#couponDataTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        $MB.confirm({
            title: "<i class='mdi mdi-alert-outline'></i>提示：",
            content: "确定设置当前选中的记录?"
        }, function () {
            $("input[name='miniprogramTitle']").val("送您一张" + selected[0]['couponDeno'] + "元券");
            $("input[name='miniprogramPage']").val(selected[0]['couponUrl']);
            $("#couponModal").modal('hide');
            $("#qywxCouponId").val(selected[0]['couponId']);
            $("#myTabs").find("li").eq(3).addClass("active");
            $("#myTabContent").find("div.tab-pane").eq(3).addClass("active in");
            $("#myTabContent").find("div.tab-pane").eq(3).siblings().removeClass("active in");
            $("#myTabs").find("li").eq(3).siblings().removeClass("active");
            updateWelcomeInfo();
        });
    }
}

function setProduct() {
    var selected = $("#productDataTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        $MB.confirm({
            title: "<i class='mdi mdi-alert-outline'></i>提示：",
            content: "确定设置当前选中的记录?"
        }, function () {
            $("input[name='miniprogramTitle']").val(selected[0]['productName']);
            $("input[name='miniprogramPage']").val(selected[0]['productUrl']);
            $("#productModal").modal('hide');
            $("#qywxProductId").val(selected[0]['productId']);
            $("#myTabs").find("li").eq(3).addClass("active");
            $("#myTabContent").find("div.tab-pane").eq(3).addClass("active in");
            $("#myTabContent").find("div.tab-pane").eq(3).siblings().removeClass("active in");
            $("#myTabs").find("li").eq(3).siblings().removeClass("active");
            updateWelcomeInfo();
        });
    }
}

function selectType(type) {
    if(type=="image"){
        $("#image").show();
        $("#web").hide();
        $("#miniprogram").hide();
        $('#configBtn').attr('style', 'display:none;');
    }else if(type=="web"){
        $("#image").hide();
        $("#web").show();
        $("#miniprogram").hide();
        $('#configBtn').attr('style', 'display:none;');
    }else if(type=="miniprogram"){
        $("#image").hide();
        $("#web").hide();
        $("#miniprogram").show();
        $('#configBtn').attr('style', 'display:inline-block;');
    }
    $qywxManualForm_validator.resetForm();

}

// 验证欢迎语表单
function validWelcomeForm() {
    var icon = "<i class='fa fa-close'></i> ";
    var msgType = $('input[name="msgType"]:checked').val();
    if(msgType=="applets"){
        $qywxManualForm_validator = $qywxManualForm.validate( {
            rules: {
                miniprogramTitle: {
                    required: true
                },
                miniprogramPage: {
                    required: true
                },
                miniprogramImageName:{
                    required: true
                },
                welcomeName:{
                    required: true
                },
                content:{
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
                miniprogramTitle: {
                    required: icon + "请输入小程序标题"
                },
                miniprogramPage: {
                    required: icon + "请输入小程序地址"
                },
                miniprogramImageName: {
                    required: icon + "请上传小程序封面图片"
                },
                welcomeName:{
                    required: icon + "请输入欢迎语名称"
                },
                content:{
                    required: icon + "请输入欢迎语内容"
                }
            }
        } );
    }else if(msgType=="image"){
        $qywxManualForm_validator = $qywxManualForm.validate( {
            rules: {
                picUrl: {
                    required: true
                },
                welcomeName:{
                    required: true
                },
                content:{
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
                picUrl: {
                    required: icon + "请选择图片地址！"
                },
                welcomeName:{
                    required: icon + "请输入欢迎语名称"
                },
                content:{
                    required: icon + "请输入欢迎语内容"
                }
            }
        } );
    }else if(msgType=="web"){
        $qywxManualForm_validator = $qywxManualForm.validate( {
            rules: {
                linkTitle: {
                    required: true
                },
                linkUrl: {
                    required: true
                },
                welcomeName:{
                    required: true
                },
                content:{
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
                linkTitle: {
                    required: icon + "请填写网页标题！"
                }, linkUrl: {
                    required: icon + "请填写网页地址！"
                },
                welcomeName:{
                    required: icon + "请输入欢迎语名称"
                },
                content:{
                    required: icon + "请输入欢迎语内容"
                }
            }
        } );
    }
}

// 更新欢迎语信息
function updateWelcomeInfo() {
    var id = $("#qywxWelcomeId").val();
    if(id !== '') {
        $.post("/welcome/updateData", $("#welcomeFormData").serialize() + getTabFormData(), function (r) {
            if(r.code == 200) {
                $MB.n_success("更新成功！");
            }else {
                $MB.n_danger("更新失败！");
            }
        });
    }
}


validCoupon();
var validator_coupon;
function validCoupon() {
    var icon = "<i class='mdi mdi-close-circle'></i> ";
    var rule = {
        rules: {
            couponDeno: {
                required: true
            },
            couponThreshold: {
                required: true
            },
            couponUrl: {
                required: true
            },
            endDate: {
                required: true
            }
        },
        messages: {
            couponDeno: {
                required: icon + "请输入门槛"
            },
            couponThreshold: {
                required: icon + "请输入面额"
            },
            couponUrl: {
                required: icon + "请输入优惠券地址"
            },
            endDate: {
                required: icon + "请输入有效截止日期"
            }
        }
    };
    validator_coupon = $("#addCouponForm").validate(rule);
}

validProduct();
var validator_product;
function validProduct() {
    var icon = "<i class='mdi mdi-close-circle'></i> ";
    var rule = {
        rules: {
            productId: {
                required: true
            },
            productName: {
                required: true
            },
            price: {
                required: true
            },
            productType: {
                required: true
            },
            productUrl: {
                required: true
            }
        },
        messages: {
            productId: {
                required: icon + "请输入商品ID"
            },
            productName: {
                required: icon + "请输入商品名称"
            },
            price: {
                required: icon + "请输入售价"
            },
            productType: {
                required: icon + "请输入商品类型"
            },
            productUrl: {
                required: icon + "请输入地址"
            }
        }
    };
    validator_product = $("#addProductForm").validate(rule);
}

$("#addProductModal").on('hidden.bs.modal', function () {
    validator_product.resetForm();
});

$("#addCouponModal").on('hidden.bs.modal', function () {
    validator_coupon.resetForm();
});



var upload;
image();

function image() {
    upload = new Cupload( {
        ele: '#cupload-create',
        num: 1
    } );
}

function uploadMediaImg() {
    $("#mediaModal").modal('show');
}

function saveModelImg() {
    var code=$( "input[name='image[]']" ).val();

    if(code === ''||null==code) {
        $MB.n_warning("图片不能为空！");
        return false;
    }
    var filename=document.getElementsByClassName("cupload-image-delete")[0].title;
    if(filename.substr(filename.lastIndexOf(".")+1)!="png"&&filename.substr(filename.lastIndexOf(".")+1)!="PNG"){
        $MB.n_warning("图片格式不正确，请上传png格式图片！");
        return false;
    }
    $MB.loadingDesc("show", "图片正在上传中，请稍候...");
    $.post( "/welcome/saveModelImg", {
        mediaType: 'image',
        base64Code: code,
        filename:filename.substr(0,filename.lastIndexOf("."))
    }, function (r) {
        $MB.loadingDesc("hide");
        if (r.code === 200) {
            $MB.n_success( "保存成功！" );
            $("#miniprogramImageName").val(filename);
            $("#miniprogramImagePath").val(r.data);
            $("#mediaModal").modal('hide');
            clearImg();
        }
    } );
}


function closeModelImg() {
    $("#mediaModal").modal('hide');
    clearImg();
}
/**
 * 清除上传内容
 */
function clearImg() {
    //清除所选图片
    $("#cupload-create").html("");
    image();
}