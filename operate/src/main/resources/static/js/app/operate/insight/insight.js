$( function () {
    findUserCntList();
    findSpuValueList();
    findImportSpu();
} );

// 按时间范围查询
function searchInsight() {
    var dateRange = $("#dateRange").val();
    findUserCntList();
    $("#sankeyFrame").attr("src", "/sankey?dateRange=" + dateRange);
}

function resetInsight() {
    $("#dateRange").find("option:selected").removeAttr("selected");
}

function findUserCntList() {
    var settings = {
        columns: [
            {
                field: 'purchType',
                title: '购买次序',
                align: 'center'
            }, {
                field: 'purch1',
                title: '1购',
                align: 'center'
            }, {
                field: 'purch2',
                title: '2购',
                align: 'center'
            }, {
                field: 'purch3',
                title: '3购',
                align: 'center'
            }, {
                field: 'purch4',
                title: '4购'
            }, {
                field: 'purch5',
                title: '5购',
                align: 'center'
            }, {
                field: 'purch6',
                title: '6购',
                align: 'center'
            }, {
                field: 'purch7',
                title: '7购',
                align: 'center'
            }, {
                field: 'purch8',
                title: '8购',
                align: 'center'
            }]
    };
    $( "#userCntTable" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
    $.get( "/insight/findUserCntList", {dateRange: $( "#dateRange" ).val()}, function (r) {
        if (r.code == 200) {
            $( "#userCntTable" ).bootstrapTable( 'load', r.data );
        }
    });
}

function findSpuValueList() {
    var settings = {
        url: '/insight/findSpuValueList',
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
                sort: params.sort,
                order: params.order
            };
        },
        columns: [
            [
                {
                    field: 'growthNumber',
                    title: '旅程编号',
                    align:"center",
                    valign:"middle",
                    rowspan: 2
                },{
                field: '',
                title: '旅程价值',
                align:"center",
                valign:"middle",
                colspan: 4
            }, {
                field: '',
                title: '购买次序',
                align:"center",
                valign:"middle",
                colspan: 8
            }
            ],[
                {
                    field: 'copsValue',
                    title: '综合价值',
                    sortable : true
                },{
                    field: 'incomeValue',
                    title: '收入价值',
                    sortable : true
                }, {
                    field: 'stepValue',
                    title: '步长价值',
                    sortable : true
                }, {
                    field: 'universValue',
                    title: '普适性价值',
                    sortable : true
                }, {
                    field: 'purch1SpuName',
                    title: '1购'
                }, {
                    field: 'purch2SpuName',
                    title: '2购'
                }, {
                    field: 'purch3SpuName',
                    title: '3购'
                }, {
                    field: 'purch4SpuName',
                    title: '4购'
                }, {
                    field: 'purch5SpuName',
                    title: '5购'
                }, {
                    field: 'purch6SpuName',
                    title: '6购'
                }, {
                    field: 'purch7SpuName',
                    title: '7购'
                }, {
                    field: 'purch8SpuName',
                    title: '8购'
                }
            ]
        ],onLoadSuccess: function() {
            var tableId = document.getElementById('spuValueTable');
            tableId.rows[tableId.rows.length - 1].setAttribute("style", "background-color:#E7EAEC;");
        }
    };
    $MB.initTable('spuValueTable', settings);
}

// 重要spu
function findImportSpu() {
    var settings = {
        url: '/insight/findImportSpuList',
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
                param: {
                    spuName: $("input[name='spuName']").val(),
                    purchOrder: $("select[name='purchOrder']").val()
                }
            };
        },
        columns: [
            {
                field: 'spuName',
                title: 'SPU名称',
                align: 'center'
            }, {
                field: 'contributeRate',
                title: '本次购买的用户贡献率（%）',
                align: 'center'
            },{
                field: 'nextPurchProbal',
                title: '本购SPU后再购概率（%）',
                align: 'center'
            }, {
                field: 'sameSpuProbal',
                title: '本购SPU后再购同SPU概率（%）'
            }, {
                field: 'otherSpuProbal',
                title: '本购SPU后购其他SPU概率（%）',
                align: 'center'
            }]
    };
    $( "#importSpu" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

function searchImportSpu() {
    findImportSpu();
}

function resetImportSpu() {
    $("input[name='spuName']").val("");
    $("select[name='purchOrder']").find("option:selected").removeAttr("selected");
}