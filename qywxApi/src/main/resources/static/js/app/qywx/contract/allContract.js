var ctx = "/";
$(function () {
    var settings = {
        url: ctx + "external/contractList",
        pageSize: 10,
        singleSelect: true,
        detailView:true, //1，开启详情视树形图模式
        detailFormatter:"detailFormatter", //2，定义详情显示函数
        detailViewIcon: false,//3，隐藏图标列
        detailViewByClick: true,//4,隐藏图标列
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [
            {
                field: 'externalUserid',
                title: '客户ID',
                visible: false
            }, {
                field: 'name',
                title: '客户名称'
            }, {
                field: 'type',
                title: '客户类型',
                formatter: typeFormatter
            }, {
                field: 'gender',
                title: '性别',
                formatter: genderFormatter
            }, {
                field: 'followerUserId',
                title: '添加此客户的企业成员'
            }, {
                field: 'createtime',
                title: '添加时间'
            }
        ]
    };
    $('#contractTable').bootstrapTable(settings);
});


/**
 * 性别的格式化类
 */
function genderFormatter(value, row, index)
{

    if (value === '1')
    {
        return '<span class="badge bg-success">男</span>';
    }else if(value==='2')
    {
        return '<span class="badge bg-success">女</span>';
    }else if(value==='0')
    {
        return '<span class="badge bg-success">未定义</span>';
    }else
    {
        return '';
    }
}
/**
 * 客户类型的格式化类
 */
function typeFormatter(value, row, index)
{

    if (value === '1')
    {
        return '<span class="badge bg-success">微信客户</span>';
    }else if(value==='2')
    {
        return '<span class="badge bg-success">企业微信客户</span>';
    }else
    {
        return '';
    }
}