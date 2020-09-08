init_date('endDate', 'yyyy-mm-dd', 0, 2, 0);
getCouponData();

function getViewData() {
    var policyType = $("input[name='policyType']:checked").val();
    var policyTypeTmp = $("input[name='policyTypeTmp']:checked").val();
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
        $('#configBtn').attr('style', 'display:none;')
    }
    if(policyType === 'A') {
        $('#intelPolicy').attr('style', 'display:block;');
        $('#configBtn').attr('style', 'display:inline-block;')
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

getProductData();
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

// 获取图片列表的setting
function getImageTableSetting() {
    return {
        url: "/qywxMedia/getDataList",
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
        columns: [
            {
                checkbox: true
            },
            {
                field: 'imgId',
                title: 'ID',
                visible: false
            },
            {
                field: 'imgTitle',
                title: '图片名称',
                align: 'center',
                formatter: function (value, row, index) {
                    return '<a href="' + row['imgUrl'] + '" target="_blank" style="text-decoration: underline">' + value + '</a>';
                }
            },
            {
                field: 'insertDt',
                title: '创建时间',
                align: 'center'
            }]
    };
}

imageTable1();
function imageTable1() {
    $("#imageTable1").bootstrapTable(getImageTableSetting());
}

imageTable2();
function imageTable2() {
    $("#imageTable2").bootstrapTable(getImageTableSetting());
}

imageTable3();
function imageTable3() {
    $("#imageTable3").bootstrapTable(getImageTableSetting());
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

function saveData() {
    $.post("/welcome/saveData", $("#welcomeFormData").serialize() + getTabFormData(), function (r) {
        if(r.code == 200) {
            $MB.n_success("保存成功！");
        }else {
            $MB.n_danger("保存失败！");
        }
    });
}

function saveProductData() {
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