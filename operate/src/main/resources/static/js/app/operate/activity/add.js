let validator;
let $activityAddForm = $("#activity-add-form");

$(function () {
    validateRule();
});

let step = steps({
    el: "#step",
    data: [
        {title: "设置活动", description: ""},
        {title: "活动策略", description: ""}
    ],
    space: 140,
    center: true,
    active: 0,
    dataOrder: ["line", "title", "description"]
});


// 验证创建活动的表单
function validateRule() {
    let icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $activityAddForm.validate({
        rules: {
            activityName: {
                required: true
            },
            preheatStartDt: {
                required: true
            },
            preheatEndDt: {
                required: true
            },
            formalStartDt: {
                required: true
            },
            formalEndDt: {
                required: true
            },
            activityStage: {
                required: true
            }
        },
        errorPlacement: function (error, element) {
            if (element.is(":checkbox") || element.is(":radio")) {
                error.appendTo(element.parent().parent());
            } else {
                error.insertAfter(element);
            }
        },
        messages: {
            activityName: {
                required: icon + "请输入活动名称"
            },
            preheatStartDt: {
                required: icon + "请输入预热开始日期"
            },
            preheatEndDt: {
                required: icon + "请输入预热结束日期"
            },
            formalStartDt: {
                required: icon + "请输入正式开始日期"
            },
            formalEndDt: {
                required: icon + "请输入正式结束日期"
            },
            activityStage: {
                required: icon + "请选择活动阶段"
            }
        }
    });
}

// 下一步
function nextStep(i) {
    let validator = $activityAddForm.validate();
    let flag = validator.form();
    if (flag) {
        step.setActive(i);

        // if (i == 1) {
        //     var activityType = $("#activityType").find("option:selected").val();
        //     var activityId = $("#activityId").find("option:selected").val();
        //     if (activityType == '' || activityId == '') {
        //         $MB.n_warning("请选择活动类型或活动名称！");
        //         return;
        //     }
        //
        //     $MB.confirm({
        //         title: "<i class='mdi mdi-alert-outline'></i>提示：",
        //         content: "确认提交当前活动?"
        //     }, function () {
        //         saveActivity();
        //         step2Init();
        //         $("#step1").attr("style", "display: none;");
        //         $("#step2").attr("style", "display: block;");
        //     });
        // }

        step2Init();
        $("#step1").attr("style", "display: none;");
        $("#step2").attr("style", "display: block;");
        if (i == 2) {
            step3Init();
        }
    }

}

// 第二步获取用户数据
function step2Init() {
    var settings = {
        url: '/activity/getActivityUserListPage',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {startDate: $("#beforeDate").val(), endDate: $("#afterDate").val()}
            };
        },
        columns: [
            {
                checkbox: true
            },
            {
                field: 'spuName',
                title: '商品ID'
            }, {
                field: 'planPurch',
                title: '名称'
            }, {
                field: 'recPiecePrice',
                title: '活动期间最低价（元）'
            }, {
                field: 'recRetentionName',
                title: '非活动日常单价（元）'
            }, {
                field: 'recUpName',
                title: '活动力度（%）'
            }, {
                field: 'recCrossName',
                title: '活动属性'
            }, {
                field: 'discountLevel',
                title: '商品链接'
            }]
    };
    $MB.initTable('activityUserTable', settings);

    getUserGroupTable();
    getUserTable();
}

// 获取用户群组列表
function getUserGroupTable() {
    var settings = {
        url: '/activity/getActivityUserListPage',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {startDate: $("#beforeDate").val(), endDate: $("#afterDate").val()}
            };
        },
        columns: [
            {
                checkbox: true
            },
            {
                field: 'spuName',
                title: '用户与商品关系'
            }, {
                field: 'planPurch',
                title: '人数（人）'
            }, {
                field: 'recPiecePrice',
                title: '成长节点与活动期'
            }, {
                field: 'recRetentionName',
                title: '人数（人）'
            }, {
                field: 'recUpName',
                title: '活跃度'
            }, {
                field: 'recCrossName',
                title: '人数（人）'
            }, {
                field: 'discountLevel',
                title: '选择模板'
            }]
    };
    $MB.initTable('userGroupTable', settings);
}

// 获取用户列表
function getUserTable() {
    var settings = {
        url: '/activity/getActivityUserListPage',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {startDate: $("#beforeDate").val(), endDate: $("#afterDate").val()}
            };
        },
        columns: [
            {
                checkbox: true
            },
            {
                field: 'spuName',
                title: '用户ID'
            }, {
                field: 'planPurch',
                title: '成长节点与活动期'
            }, {
                field: 'recPiecePrice',
                title: '用户与商品关系'
            }, {
                field: 'recRetentionName',
                title: '活跃度'
            }, {
                field: 'recUpName',
                title: '价值'
            }, {
                field: 'recCrossName',
                title: '推送内容'
            }]
    };
    $MB.initTable('userTable', settings);
}

function getActivityProductTable() {
    var settings = {
        url: '/activity/getActivityProductListPage',
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
                param: {headId: headId}
            };
        },
        columns: [{
            field: 'headId',
            title: 'ID',
            visible: false
        }, {
            field: 'productId',
            title: '商品ID'
        }, {
            field: 'userCount',
            title: '覆盖成长用户数量（人）'
        }, {
            field: 'productPrice',
            title: '商品单价（元）'
        }, {
            field: 'executeRate',
            title: '优惠分析',
            formatter: function (value, row, index) {
                let headId = row['headId'];
                let productId = row['productId'];
                let code = "<a style='color: #0f0f0f;text-decoration: underline;' onclick='getUserMap(" + headId + ", " + productId + ")'><i class='fa fa-area-chart fa-12x'></i></a>";
                return code;
            }
        }, {
            field: 'preferType',
            title: '优惠类型',
            formatter: function (value, row, index) {
                if(value == "discount") {
                    return "打折";
                }
                if(value == "reduce") {
                    return "满减";
                }
                if(value == "promote") {
                    return "促销价";
                }
            }
        }, {
            field: 'preferValue',
            title: '优惠值',
            formatter: function (value, row, index) {
                if(row['preferType'] == "discount") {
                    return (100 - (value / row['productPrice']).toFixed(2) * 100) + "%";
                }else {
                    return value;
                }
            }
        }, {
            field: 'productActPrice',
            title: '建议销售价（元）'
        }, {
            field: 'minPrice15',
            title: '15天之内最低价（元）'
        }, {
            field: 'minPrice30',
            title: '30天之内最低价（元）'
        }]
    };
    $MB.initTable('activityProductTable', settings);
}

function step3Init() {
    $MB.loadingDesc('show', "计算数据中，请稍后...");
    $.get("/activity/saveActivityProduct", {
        headId: headId,
        startDate: $("#beforeDate").val(),
        endDate: $("#afterDate").val()
    }, function (r) {
        if (r.code === 200) {
            getActivityProductTable();
            $("#step2").attr("style", "display: none;");
            $("#step3").attr("style", "display: block;");
        } else {
            $MB.n_danger("发生未知异常！");
        }
        $MB.loadingDesc('hide');
    });
}

let head_id;
let product_id;

function getUserMap(headId, productId) {
    $("#userMapModal").modal('show');
    head_id = headId;
    product_id = productId;
}

$("#userMapModal").on('shown.bs.modal', function () {
    $.get("/activity/getCouponBoxData", {
        productId: product_id,
        startDate: $("#beforeDate").val(),
        endDate: $("#afterDate").val()
    }, function (r) {
        let data = r.data;
        let xdata = [data['min'], data['q1'], data['q2'], data['q3'], data['max'], data['avg']];
        let option = gearsOption('优惠值', xdata, product_id);
        let chart = echarts.init(document.getElementById('userMapChart'), 'macarons');
        chart.setOption(option);
    });
});


function gearsOption(yName, data, id) {
    var option = {
        title: {
            text: '商品ID[' + id + ']的优惠面额箱线图',
            x: 'center',
            y: 'bottom',
            textStyle: {
                //文字颜色
                color: '#000',
                //字体风格,'normal','italic','oblique'
                fontStyle: 'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight: 'normal',
                //字体系列
                fontFamily: 'sans-serif',
                //字体大小
                fontSize: 12
            }
        },
        tooltip: {
            trigger: 'item',
            axisPointer: {
                type: 'shadow'
            }
        },
        // grid: [{
        //     height: '50%',
        //     left: '10%',
        //     right: '20%',
        // }],
        yAxis: [{
            type: 'category',
            data: [''],
            nameTextStyle: {
                color: '#3259B8',
                fontSize: 16,
            },

            axisTick: {
                show: false,
            }
        }],
        xAxis: [{
            name: yName,
            type: 'value',
            axisTick: {
                show: false,
            },
            splitArea: {
                show: false
            }
        }],
        series: [{
            name: 'XXX',
            type: 'boxplot',
            data: [data],
            itemStyle: {
                normal: {
                    borderColor: {
                        type: 'linear',
                        x: 1,
                        y: 0,
                        x2: 0,
                        y2: 0,
                        colorStops: [{
                            offset: 0,
                            color: '#3EACE5' // 0% 处的颜色
                        }, {
                            offset: 1,
                            color: '#956FD4' // 100% 处的颜色
                        }],
                        globalCoord: false // 缺省为 false
                    },
                    borderWidth: 2,
                    color: {
                        type: 'linear',
                        x: 1,
                        y: 0,
                        x2: 0,
                        y2: 0,
                        colorStops: [{
                            offset: 0,
                            color: 'rgba(62,172,299,0.7)'  // 0% 处的颜色
                        }, {
                            offset: 1,
                            color: 'rgba(149,111,212,0.7)'  // 100% 处的颜色
                        }],
                        globalCoord: false // 缺省为 false
                    },
                }
            },
            tooltip: {
                formatter: function (param) {
                    return [
                        '平均值:' + param.data[6],
                        '最大值: ' + param.data[5],
                        '第三个中位数: ' + param.data[4],
                        '中位数: ' + param.data[3],
                        '第一个中位数: ' + param.data[2],
                        '最小值: ' + param.data[1]
                    ].join('<br/>')
                }
            }
        }]
    };
    return option;
}

$("#btn_download").click(function () {
    window.location.href = "/activity/downloadFile";
});