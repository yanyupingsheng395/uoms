getCouponData();
function getCouponData() {
    var settings = {
        url: "#",
        cache: false,
        pagination: false,
        singleSelect: true,
        clickToSelect: true,
        columns: [{
            checkbox: true
        },{
            field: 'name',
            align: "center",
            title: '优惠券名称',
        },{
            field: 'mediaType',
            align: "center",
            title: '类型',
        },{
            field: 'insertDt',
            align: "center",
            title: '门槛（元）',
        },{
            field: 'status',
            align: "center",
            title: '面额（元）',
        },{
            field: 'status',
            align: "center",
            title: '地址',
        },{
            field: 'status',
            align: "center",
            title: '有效截止日期',
        },{
            field: 'status',
            align: "center",
            title: '校验结果',
        },{
            field: 'status',
            align: "center",
            title: '失败原因',
        }]
    };
    $("#couponDataTable").bootstrapTable(settings);
}

getProductData();
function getProductData() {
    var settings = {
        url: "#",
        cache: false,
        pagination: false,
        singleSelect: true,
        clickToSelect: true,
        columns: [{
            checkbox: true
        },{
            field: 'name',
            align: "center",
            title: '商品ID',
        },{
            field: 'mediaType',
            align: "center",
            title: '商品名称',
        },{
            field: 'insertDt',
            align: "center",
            title: '售价（元）',
        },{
            field: 'status',
            align: "center",
            title: '商品类型',
        },{
            field: 'status',
            align: "center",
            title: '地址',
        },{
            field: 'status',
            align: "center",
            title: '校验结果',
        },{
            field: 'status',
            align: "center",
            title: '失败原因',
        }]
    };
    $("#productDataTable").bootstrapTable(settings);
}

function addProductModal() {
    $("#addProductModal").modal('show');
    $("#productModal").modal('hide');
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
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  //页面大小
                pageNum: (params.offset / params.limit)+ 1
            }
        },
        columns: [
            {
                field: 'imgId',
                title: 'ID',
                visible: false
            },
            {
                field: 'imgTitle',
                title: '图片名称',
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

function saveData() {
    $.post("/welcome/saveData", $("#welcomeFormData").serialize(), function (r) {
        if(r.code == 200) {
            $MB.n_success("保存成功！");
        }
    });
}