init();
function init() {
    var settings = {
        url: "/welcome/getDataTableList",
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        clickToSelect: true,
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset
            };
        },
        columns: [{
            checkbox: true
        },{
            field: 'welcomeName',
            align: "center",
            title: '欢迎语名称',
        },{
            field: 'policyType',
            align: "center",
            title: '素材类型',
            formatter: function (value, row, index) {
                if(value === 'M') {
                    return "自定义";
                }else if(value === 'PRODUCT' || value === 'COUPON') {
                    return "智能策略";
                }
            }
        },{
            field: 'insertDt',
            align: "center",
            title: '配置时间',
        },{
            field: 'status',
            align: "center",
            title: '使用状态',
            formatter: function (value, row, index) {
                if(value === '0') {
                    return '未使用';
                }
                if(value === '1') {
                    return '使用中';
                }
            }
        }]
    };
    $("#dataTable").bootstrapTable(settings);
}

// 删除欢迎语
function deleteWelcome() {

}