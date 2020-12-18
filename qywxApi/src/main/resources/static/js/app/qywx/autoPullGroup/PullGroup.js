$(function () {
    getPullGroupList();
});

function getPullGroupList() {
    var settings = {
        url: "/qywxCustomer/getPullGroupList",
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
        columns: [ {
            checkbox: true,
        },{
            field: 'groupId',
            title: 'ID',
            visible: false
        },{
            field: 'qrCodeConnection',
            title: '二维码',
            align: 'center',
            formatter: function (value, row, index) {
                return "<img style='width:120px;height:120px' src='" + value + "'><a href='/contactWay/download?configId=" + row.configId + "' style='font-size: 12px;' target='_blank'>下载</a>" +
                    "&nbsp;&nbsp;<a style='font-size: 12px;cursor: pointer;' data-clipboard-text='" + value + "' class='copy_btn'>复制二维码地址</a>";
            }
        }, {
            field: 'groupName',
            title: '名称',
            align: 'center'
        }, {
            field: 'userName',
            title: '使用成员',
            align: 'center'
        }, {
            field: 'addNumber',
            title: '添加好友数',
            align: 'center'
        }, {
            field: 'insertDt',
            title: '创建时间',
            align: 'center'
        }
        ]
    };
    $MB.initTable( 'baseTable', settings );
}

function addGroup() {
   window.location.href="/page/goAddGroup";
}

var upload;
image();

function image() {
    upload = new Cupload( {
        ele: '#cupload-create',
        num: 1
    } );
}