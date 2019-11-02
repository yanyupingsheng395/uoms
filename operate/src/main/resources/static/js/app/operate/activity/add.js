let validator;
let validatorProduct;
let $activityAddForm = $("#activity-add-form");
let $activityProductAddForm = $("#add-product-form");

init_date_begin('preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd',0,2,0);
init_date_end('preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd',0,2,0);
init_date_begin('formalStartDt', 'formalEndDt', 'yyyy-mm-dd',0,2,0);
init_date_end('formalStartDt', 'formalEndDt', 'yyyy-mm-dd',0,2,0);


$(function () {
    validateRule();
    validateProductRule();
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
                required: icon + "请选择是否预售"
            }
        }
    });
}

// 验证商品表
function validateProductRule() {
    let icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validatorProduct = $activityProductAddForm.validate({
        rules: {
            productId: {
                required: true
            },
            productName: {
                required: true
            },
            minPrice: {
                required: true
            },
            formalPrice: {
                required: true
            },
            activityIntensity: {
                required: true
            },
            productAttr: {
                required: true
            },
            productUrl: {
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
            productId: {
                required: icon + "请输入商品ID"
            },
            productName: {
                required: icon + "请输入商品名称"
            },
            minPrice: {
                required: icon + "请输入活动期间最低价"
            },
            formalPrice: {
                required: icon + "请输入非活动正常价"
            },
            activityIntensity: {
                required: icon + "请输入活动力度"
            },
            productAttr: {
                required: icon + "请选择活动属性"
            },
            productUrl: {
                required: icon + "请输入商品链接"
            }
        }
    });
}

// 是否有预售选中事件
$("input[name='activityStage']").click(function () {
    let stage = $(this).val();
    if(stage == 1) { // 是
        $("#preheatDiv").attr("style", "display:block;");
        $("#btn_preheat").attr("style", "display:inline-block;");

        if($("#preheatDiv").html() == '') {
            // language=HTML
            $("#preheatDiv").html('').append(
                "               <div class=\"col-md-6\">\n                    " +
                "                   <div class=\"form-group\">\n" +
                "                        <label class=\"col-md-3 control-label\">预热开始时间</label>\n" +
                "                        <div class=\"col-md-9\">\n" +
                "                            <input type=\"text\" name=\"preheatStartDt\" id=\"preheatStartDt\" class=\"form-control\"/>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"col-md-6\">\n" +
                "                    <div class=\"form-group\">\n" +
                "                        <label class=\"col-md-3 control-label\">预热结束日期</label>\n" +
                "                        <div class=\"col-md-9\">\n" +
                "                            <input class=\"form-control\" id=\"preheatEndDt\" name=\"preheatEndDt\"/>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>" );
        }
        init_date_begin('preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd',0,2,0);
        init_date_end('preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd',0,2,0);
    }
    if(stage == 0) { // 否
        $("#preheatDiv").attr("style", "display:none;");
        $("#btn_preheat").attr("style", "display:none;");
        // 通过移除页面元素去除验证
        $("#preheatDiv").html('');
    }
});



// 保存活动信息
function saveDailyHead() {
    let operateType = $("#operateType").val();
    let msg = "";
    if(operateType === "save") {
        msg = "执行下一步会保存活动信息?";
    }else {
        msg = "执行下一步会更新活动信息?";
    }
    if(operate_type === 'save') {
        $MB.confirm({
            title: "<i class='mdi mdi-alert-circle-outline'></i>提示：",
            content: msg
        }, function () {
            $.post("/activity/saveActivityHead", $activityAddForm.serialize(), function (r) {
                if(r.code === 200) {
                    step.setActive(1);
                    $("#step1").attr("style", "display: none;");
                    $("#step2").attr("style", "display: block;");
                    $MB.n_success(r.msg);
                    $("#headId").val(r.data);
                }else {
                    $MB.n_danger("有错误发生！")
                }
            });
        });
    }else {
        step.setActive(1);
        $("#step1").attr("style", "display: none;");
        $("#step2").attr("style", "display: block;");
    }
}

// 第二步
function step2(stage) {
    let validator = $activityAddForm.validate();
    let flag = validator.form();
    if (flag) {
        saveDailyHead();
        // 获取商品数据
        getProductInfo(stage);
    }
}

// 验证时间是否合法
function validPreheatAndFormalDate() {
    let preheatEndDt = $("#preheatEndDt").val();
    let formalStartDt = $("#formalStartDt").val();
    return new Date(String(formalStartDt)) > new Date(String(preheatEndDt));
}

// 下一步
// param:stage 活动阶段
function nextStep(stage) {
    // 页面保存
    $("#activity_stage").val(stage);
    let validator = $activityAddForm.validate();
    let flag = validator.form();
    if($("input[name='activityStage']:checked").val() == '1') {
        let dateFlag = validPreheatAndFormalDate();
        if(!dateFlag) {
            $MB.n_warning("正式阶段的开始时间必须大于预热阶段的结束时间！");
            return;
        }
    }
    if (flag) {
        step2(stage);
    }
}

// 第二步获取产品数据
function getProductInfo(stage) {
    var settings = {
        url: '/activity/getActivityProductPage',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    headId: $("#headId").val(),
                    productId: $("#productId").val(),
                    productName: $("#productName").val(),
                    productAttr: $("#productAttr").val(),
                    stage: stage
                }
            };
        },
        columns: [
            {
                checkbox: true
            },
            {
                field: 'productId',
                title: '商品ID'
            }, {
                field: 'productName',
                title: '名称'
            }, {
                field: 'minPrice',
                title: '活动期间最低价（元）'
            }, {
                field: 'formalPrice',
                title: '非活动日常单价（元）'
            }, {
                field: 'activityIntensity',
                title: '活动力度（%）'
            }, {
                field: 'productAttr',
                title: '活动属性'
            }, {
                field: 'productUrl',
                title: '商品链接'
            }]
    };
    $MB.initTable('activityProductTable', settings);
}

// 查询商品信息
function searchActivityProduct() {
    $MB.refreshTable('activityProductTable');
}

// 重置查询条件
function resetActivityProduct() {
    $("#productId").val("");
    $("#productName").val("");
    $("#productAttr").find("option:selected").removeAttr("selected");
    $MB.refreshTable('activityProductTable');
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


let head_id;
let product_id;


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

// 上一步
function prevStep() {
    $("#operateType").val("update");
    step.setActive(0);
    $("#step1").attr("style", "display: block;");
    $("#step2").attr("style", "display: none;");
}

// 添加活动商品
$("#saveActivityProduct").click(function () {
    let validator = $activityProductAddForm.validate();
    let flag = validator.form();
    if(flag) {
        $.post("/activity/saveActivityProduct", $("#add-product-form").serialize() + "&headId=" +
            $("#headId").val() + "&activityStage=" + $("#activity_stage").val() , function (r) {
            if(r.code === 200) {
                $MB.n_success("保存成功！");
                $MB.closeAndRestModal("addProductModal");
                $MB.refreshTable('activityProductTable');
            }else {
                $MB.n_danger("保存失败！");
            }
        });
    }
});

$("#addProductModal").on("hidden.bs.modal", function () {
    $("input[name='productId']").val("");
    $("input[name='productName']").val("");
    $("input[name='minPrice']").val("");
    $("input[name='formalPrice']").val("");
    $("input[name='activityIntensity']").val("");
    $("select[name='productAttr']").find("option:selected").removeAttr("selected");
    $("input[name='productUrl']").val("");
});