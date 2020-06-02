$(function () {
    getTableData();
});

function getTableData() {
    let settings = {
         // url: "/dailyConfig/getDataList",
        url: "",
        cache: false,
        pagination: true,
        singleSelect: false,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        columns: [
            [
                {
                    title: '成长目标',
                    colspan: 1
                },
                {
                    title: '用户差异',
                    colspan: 4
                },
                {
                    title: '培养策略',
                    colspan: 2
                }
            ],[
                {
                    field: 'col1',
                    title: '购买商品与时间',
                },
                {
                    field: 'col2',
                    title: '用户对类目的价值/沉默成本'},
                {title: '用户对类目的生命周期阶段'},
                {title: '用户下一次转化的活跃度节点'},
                {
                    title: '理解用户群组',
                    align: 'center',
                    formatter: function (value, row, index) {
                        return "<a style='color: #4c4c4c'><i class='fa fa-user'></i></a>";
                    }
                },
                {
                    title: '配置个性化消息',
                    align: 'center',
                    formatter: function (value, row, index) {
                        return "<a style='color: #4c4c4c' onclick='personalMsg()'><i class='fa fa-envelope-o'></i></a>";
                    }
                },
                {
                    title: '配置个性化补贴',
                    align: 'center',
                    formatter: function (value, row, index) {
                        return "<a style='color: #4c4c4c'><i class='fa fa-credit-card'></i></a>";
                    }}
            ]
        ]
    };
    $MB.initTable('imageTextTable', settings);
    $("#imageTextTable").bootstrapTable('load', {
        rows: [
            {col1: '1', col2: '2'}
        ],
        total: 1
    })
}

function personalMsg() {
    $("#msgListModal").modal('show');
}

function addMsg() {
    $("#msgListModal").modal('hide');
    $('#add_modal').modal('show')
}

function addMaterial() {
    $("#msgListModal").modal('hide');
    $('#add_material_modal').modal('show')
}


var IS_COUPON_NAME_DISABLED;
var IS_COUPON_URL_DISABLED;
var IS_PROD_URL_DISABLED;
// 补贴链接选是，补贴名称自动选是、商品链接自动选否；
function isCouponUrlTrueClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponName"]:radio[value="1"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isCouponName"]').attr("disabled", "disabled");
    IS_COUPON_NAME_DISABLED = true;

    $("#smsTemplateAddForm").find('input[name="isProductUrl"]:radio[value="0"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isProductUrl"]').attr("disabled", "disabled");
    IS_PROD_URL_DISABLED = true;

    $('#isCouponUrl-error').show();
    $('#isCouponName-error').show();
    $('#isProductUrl-error').hide();
}

// 补贴链接选否，补贴名称自动选否、商品链接可选；
function isCouponUrlFalseClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponName"]:radio[value="0"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isProductUrl"]').removeAttr("disabled");
    IS_PROD_URL_DISABLED = false;
    $('#isCouponUrl-error').hide();
    $('#isCouponName-error').hide();
}

// 补贴名称选是，补贴链接自动选是；
function isCouponNameTrueClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]:radio[value="1"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]').attr("disabled", "disabled");
    IS_COUPON_URL_DISABLED = true;
    $('#isCouponName-error').show();
    $('#isCouponUrl-error').show();
}
// 补贴名称选否，补贴链接自动选否；
function isCouponNameFalseClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]:radio[value="0"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]').attr("disabled", "disabled");
    IS_COUPON_URL_DISABLED = true;
    $('#isCouponName-error').hide();
    $('#isCouponUrl-error').hide();
}
// 商品链接选是，补贴链接自动选否；
function isProdUrlTrueClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]:radio[value="0"]').prop("checked", true);
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]').attr("disabled", "disabled");
    IS_COUPON_URL_DISABLED = true;
    $('#isProductUrl-error').show();
    $('#isCouponUrl-error').hide();
}
// 商品链接选否，补贴链接可选；
function isProdUrlFalseClick() {
    $("#smsTemplateAddForm").find('input[name="isCouponUrl"]').removeAttr("disabled");
    IS_COUPON_URL_DISABLED = false;
    $('#isProductUrl-error').hide();
}

function contentInput() {
    var content = $("#smsContent").val();
    if(content === '') {
        content = "请输入消息内容";
    }
    $('#preview').html('<div class=\'arrow\'></div>' + content)
}