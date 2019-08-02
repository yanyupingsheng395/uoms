$(function () {
    init_date("touchDt", "yyyy-mm-dd", 0,2,0)
    $("#touchDt").datepicker('setEndDate',new Date());
    initTable();
});
function initTable() {
    var settings = {
        url: '/daily/getEffectPageList',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {touchDt: $("#touchDt").val()}
            };
        },
        columns: [[{
            field: 'touchDt',
            title: '日期',
            rowspan: 2,
            valign:"middle"
        },{
            title: '执行情况',
            colspan: 7
        },{
            title: '整体效果',
            colspan: 6
        }],[
            {
                field: 'totalNum',
                title: '任务建议（人）'
            },{
                field: 'actualNum',
                title: '实际推送（人）'
            },{
                field: 'touchRate',
                title: '触达率（%）'
            },{
                field: 'lossRate',
                title: '损耗率（%）'
            },{
                field: 'abandonRate',
                title: '放弃率（%）'
            },{
                field: 'convertCount',
                title: '转化人数（人）'
            },{
                field: 'convertRate',
                title: '转化率（%）'
            },{
                field: 'convertAmount',
                title: '转化金额（元）'
            },{
                field: 'subsidyAmount',
                title: '补贴金额（元）'
            },{
                field: 'subsidyCount',
                title: '补贴数量（个）'
            },{
                field: 'cancleCount',
                title: '核销数量（个）'
            },{
                field: 'cancleRate',
                title: '核销率（%）'
            },{
                field: 'subsidyRoi',
                title: '补贴ROI'
            }
        ]]
    };
    $MB.initTable('effectTable', settings);
}

function searchEffect() {
    $MB.refreshTable('effectTable');
}

function resetEffect() {
    $("#touchDt").val("");
}