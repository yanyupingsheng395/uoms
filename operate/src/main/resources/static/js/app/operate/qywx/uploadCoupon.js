$(function () {
    initTable();
});
function initTable() {
    let settings = {
        url: '/qywxDaily/getCouponListPage',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'couponDisplayName',
            align: 'center',
            title: '优惠券名称'
        }, {
            field: 'couponThreshold',
            align: 'center',
            title: '优惠券门槛'
        }, {
            field: 'couponDenom',
            align: 'center',
            title: '优惠券面额'
        }, {
            field: 'couponIdentity',
            align: 'center',
            title: '券码标记'
        }, {
            field: 'couponSerialNum',
            align: 'center',
            title: '券码总数量'
        }, {
            field: 'couponSeriaNolNum',
            align: 'center',
            title: '券码未使用数量'
        }],onLoadSuccess: function(data){
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $MB.initTable('dataTable', settings);
}

//新增优惠券页面展示
function showAdd() {
    var selected = $("#dataTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要关联的优惠券ID！');
        return;
    };
    $('#add_modal').modal('show');
   let couponId= selected[0]["couponId"];
   let couponIdentity= selected[0]["couponIdentity"];
   $("#couponId").val(couponId);
   $("#couponIdentity").val(couponIdentity);
}

//查看券码
function viewCoupon() {
    var selected = $("#dataTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要查看的优惠券ID！');
        return;
    };
    let couponId= selected[0]["couponId"];
    let couponIdentity= selected[0]["couponIdentity"];
    viewCouponData(couponId,couponIdentity);
    $('#view_modal').modal('show');
}

function viewCouponData(couponId,couponIdentity) {
    let settings = {
        url: '/qywxDaily/viewCouponData',
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
                param: {couponId:couponId, couponIdentity:couponIdentity}
            };
        },
        columns: [ {
            field: 'serialNo',
            align: 'center',
            title: '券码编号'
        }, {
            field: "usedFlag",
            align: 'center',
            title: "是否使用",
            formatter: function (value, row, index) {
                let res = "-";
                if(value == 'Y') {
                    res = '是';
                }
                if(value == 'N') {
                    res = '否';
                }
                return res;
            }
        }, {
            field: 'insertDt',
            align: 'center',
            title: '创建时间'
        }, {
            field: 'usedDt',
            align: 'center',
            title: '使用时间'
        }]
    };
    $("#viewTable").bootstrapTable('destroy').bootstrapTable(settings);
    //$MB.initTable('viewTable', settings);
}


/*上传文件校验*/
function beforeUpload() {
    var fileName = document.getElementById('file').files[0].name;
    if(!fileName.endsWith(".xls")) {
        $MB.n_warning("仅支持xls格式的文件！");
        return;
    }
    $('#upload_file_name' ).text(document.getElementById('file').files[0].name).attr('style', 'display:inline-block;');
}

/*生成优惠券码*/
function uploadCoupon() {
    let formData = new FormData();
    formData.append("couponId",$("#couponId").val());
    formData.append("couponIdentity",$("#couponIdentity").val());
    if(couponSnManualUpload){
        let file = document.getElementById('file').files;
        if(document.getElementById('file').files.length === 0) {
            $MB.n_warning("请上传数据！");
            return false;
        }
        if(file[0].size > 10485760) {
            $MB.n_warning("上传数据不能超过10M！");
            return false;
        }
        formData.append("file", document.getElementById('file').files[0]);
        $.ajax({
            url: "/qywxDaily/uploadCoupon",
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
                if (data.code === 200) {
                    $MB.n_success("数据提交成功！");
                } else {
                    $MB.n_danger(data.msg);
                }
            },
            error: function (data) {
                $MB.n_danger(data.msg);
            }
        });
    }else{
        $.ajax({
            url: "/qywxDaily/couponToSequence",
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
                if (data.code === 200) {
                    $MB.n_success("数据生成成功！");
                } else {
                    $MB.n_danger(data.msg);
                }
            },
            error: function (data) {
                $MB.n_danger(data.msg);
            }
        });
    }
    $('#add_modal').modal('hide');
    $MB.refreshTable("dataTable");
}

function closeCoupon() {
    $('#add_modal').modal('hide');
}