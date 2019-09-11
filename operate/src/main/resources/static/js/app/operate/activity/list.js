$(function () {
    let settings = {
        url: "/activity/gePageOfHead",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit )+ 1,  //页码
                param: {name: $("input[name='actName']").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'headId',
            title: 'ID',
            visible: false
        }, {
            field: 'activityName',
            title: '活动名称'
        }, {
            field: 'activityType',
            title: '活动类型',
            formatter: function (value, row, index) {
                if(value == "own") {
                    return "自主";
                }else if(value == "plat"){
                    return "平台";
                }else {
                    return "";
                }
            }
        }, {
            field: 'startDate',
            title: '活动开始时间'
        },  {
            field: 'endDate',
            title: '活动结束时间'
        }, {
            field: 'beforeDate',
            title: '活动影响开始时间'
        },  {
            field: 'afterDate',
            title: '活动影响结束时间'
        }, {
            field: 'coverNum',
            title: '预计覆盖人数（人）'
        }, {
            field: 'convertNum',
            title: '活动转化人数（人）'
        }, {
            field: 'numYearPercent',
            title: '年同比（%）'
        }, {
            field: 'convertAmount',
            title: '活动转化金额（元）'
        }, {
            field: 'amountYearPercent',
            title: '年同比（%）'
        }, {
            field: 'status',
            title: '状态',
            formatter: function (value, row, index) {
                // if(value == 'todo') {
                //     return "待编辑";
                // }
                return "-";
            }
        }]
    };
    $MB.initTable('activityTable', settings);
});

function searchActivity() {
    $MB.refreshTable('activityTable');
}

function resetActivity() {
    $("input[name='actName']").val('');
    $MB.refreshTable('activityTable');
}

$("#btn_edit").click(function () {
    let selected = $("#activityTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要查看的活动！');
        return;
    }
    // let status = selected[0].status;
    // if(status != "todo") {
    //     $MB.n_warning('当前记录不可编辑，请选择待编辑状态的记录！');
    //     return;
    // }
    let headId = selected[0].headId;
    window.location.href = "/page/activity/edit?id=" + headId;
});

$("#btn_add").click(function () {
    window.location.href = "/page/activity/add";
});

let validator;
let $activityAddForm = $("#activity-add-form");

function init() {
    init_date_begin('startDate', 'endDate', 'yyyy-mm-dd', 0, 2, 0);
    init_date_end('startDate', 'endDate', 'yyyy-mm-dd', 0, 2, 0);
    $('#startDate').datepicker('setStartDate', new Date());
}

$(function () {
    $MB.loading('show');
    init();
    validateRule();
    getCalendar();
    $MB.loading('hide');
});


function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $activityAddForm.validate({
        rules: {
            activityTypeVal: {
                required: true
            },
            activityName: {
                required: true
            },
            startDate: {
                required: true
            },
            endDate: {
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
            activityTypeVal: {
                required: icon + "请输入活动类型"
            },
            activityName: {
                required: icon + "请输入活动名称"
            },
            startDate: {
                required: icon + "请输入开始日期"
            },
            endDate: {
                required: icon + "请输入结束日期"
            }
        }
    });
}

$("#activityType").change(function () {
    $("#activityTypeVal").val($(this).find("option:selected").val());
});

$("#activity-add-btn").click(function () {
    let validator = $activityAddForm.validate();
    let flag = validator.form();
    if (flag) {
        $.post("/activity/saveActivityConfig", $activityAddForm.serialize(), function (r) {
            $("#activity-add").modal('hide');
            if (r.code == 200) {
                $MB.n_success("新增活动成功！");
                getCalendar();
            } else {
                $MB.n_danger("未知错误！");
            }
            getCalendarDataSource();
        });
    }
});

// 关闭modal清空值
$("#activity-add").on('hidden.bs.modal', function () {
    $("#activityType").find("option[value='']").prop("selected", "selected");
    $("#activityName").val("");
    $("#startDate").val("");
    $("#endDate").val("");
});

// 封装calendar的datasource
function getCalendarDataSource() {
    let data = getActivityConfig();
    let dataSource = [];
    data.forEach(e => {
        let o = new Object();
        o.id = e.id;
        o.name = e.activityName;
        o.startDate = detailDate(e.startDate);
        o.endDate = detailDate(e.endDate);
        dataSource.push(o);
    });

    return dataSource;
}

// 将日期处理成calendar需要的日期
function detailDate(date) {
    let dateArr = date.split("-");
    return new Date(Number.parseInt(dateArr[0]), Number.parseInt(dateArr[1]) - 1, Number.parseInt(dateArr[2]));
}

// 获取日历
function getCalendar() {
    $('#calendar').html('');
    let date = new Date();
    let today = new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime();
    let dataSource = getCalendarDataSource();
    $('#calendar').calendar({
        language: 'zh-CN',
        mouseOnDay: function (e) {
            let content = '';
            for (let i in e.events) {
                content += '<div class="event-tooltip-content">'
                    + '<div class="event-name">' + e.events[i].name + '</div>'
                    + '</div>';
            }
            $(e.element).popover({
                trigger: 'manual',
                container: 'body',
                html: true,
                content: content,
                placement: 'top'
            });

            $(e.element).popover('show');
        },
        mouseOutDay: function (e) {
            if (e.events.length > 0) {
                $(e.element).popover('hide');
            }
        },
        dataSource: dataSource,
        customDayRenderer: function (element, date) {
            if (date.getTime() == today) {
                $(element).css('background-color', 'red');
                $(element).css('color', 'white');
                $(element).css('border-radius', '2px');
            }
        }
    });
    $('[data-toggle="tooltip"]').tooltip({
        "container": 'body',
    });
}

// 获取创建的活动
function getActivityConfig() {
    let data = null;
    $.ajax({
        url: '/activity/getActivityConfig',
        data: {},
        async: false,
        success: function (r) {
            data = r.data;
        }
    });
    return data;
}

$("#btn_view").click(function () {
    var selected = $("#activityTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要查看的活动！');
        return;
    }
    let headId = selected[0].headId;
    window.location.href = "/page/activity/view?id=" + headId;
});