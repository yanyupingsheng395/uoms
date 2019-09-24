getReduceInfo('n_1', 'strategyTable1');
function getReduceInfo(flag, table) {
    var settings = {
        url: '/member/getReduceInfo',
        pagination: false,
        singleSelect: true,
        queryParams: function (params) {
            return {
                headId: headId,
                reduceFlag: flag
            };
        },
        columns: [{
            checkbox: true
        },{
            field: 'memberDateStr',
            title: '满送分类'
        },{
            field: 'userCount',
            title: '定向用户数（人）'
        },{
            field: 'convertUserCount',
            title: '涉及SPU数（种）'
        },{
            field: 'convertRate',
            title: '涉及商品数（个）'
        },{
            field: 'convertAmount',
            title: '导出店铺宝模版'
        },{
            field: 'convertAmount',
            title: '定向人群名单'
        },{
            field: 'convertAmount',
            title: '短信文案'
        }]
    };
    $(table).bootstrapTable('destroy');
    $MB.initTable(table, settings);
}

String.prototype.endWith=function(str){
    if(str==null||str==""||this.length==0||str.length>this.length)
        return false;
    if(this.substring(this.length-str.length)==str)
        return true;
    else
        return false;
    return true;
};

// 切换不同tab获取不同的列表
$("#tabList").find('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
    if(e.target.href.endWith("#one_n_1_div")) {
        getReduceInfo('n_1', 'strategyTable1');
    }
    if(e.target.href.endWith("#multi_n_1_div")) {
        getReduceInfo('n_1', 'strategyTable2');
    }
    if(e.target.href.endWith("#one_1_1_div")) {
        getReduceInfo('n_1', 'strategyTable3');
    }
    if(e.target.href.endWith("#single_div")) {
        getReduceInfo('n_1', 'strategyTable4');
    }
});

function step1() {
    step.setActive(0);
    $("#step1").attr("style", "display: block;");
    $("#step2").attr("style", "display: none;");
}

