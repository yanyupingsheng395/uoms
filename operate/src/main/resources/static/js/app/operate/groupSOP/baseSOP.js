$( function () {
    initDt();
    getSopList();
});

function createRules() {
    $("#lableModal").modal('show');
};
// 初始化日期控件
function initDt() {
    var date = new Date();
   init_date( 'sendDT', 'yyyy-mm-dd ', 0, 2, 0 );
    $( "#sendDT" ).datepicker( 'setStartDate', date );
};

//获取规则列表
function getSopList() {
    var settings = {
        url: "/qywxCustomer/getSopList",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [{
            checkbox: true,
        },{
            field: 'sopID',
            title: 'ID',
            visible: false
        },{
            field: 'roleName',
            title: '名称',
            align: 'center'
        }, {
            field: 'insetBy',
            title: '创建人',
            align: 'center'
        }, {
            field: 'insertDt',
            title: '创建时间',
            align: 'center'
        }, {
            field: 'openFlag',
            title: '是否开启',
            formatter: function (value, row, index) {
              /*  if(media_type === 'image') {
                    return '<a href="' + row['url'] + '" target="_blank" style="text-decoration: underline">' + value + '</a>';
                }else {
                    return value;
                }*/
            }
        }]
    };
    $MB.initTable( 'baseTable', settings );
}