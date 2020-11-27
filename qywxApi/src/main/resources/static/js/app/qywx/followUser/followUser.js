var ctx = "/";
$(function () {
    let settings = {
        url: ctx + "follow/followsList",
        pageSize: 10,
        singleSelect: true,
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [
            {
                field: 'userId',
                title: '用户ID'
            }, {
                field: 'gender',
                title: '性别',
                formatter: genderFormatter
            }
        ]
    };
    $('#followUserTable').bootstrapTable(settings);
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



