var urlstr = "";
var sysId = null;
$(document).ready(function () {
    allExceptionCatch();
    initSysInfo();
    if(sysId == null) {
        sysId = getQueryVariable("id");
        if(sysId) {
            localStorage.setItem("sysId", sysId);
        }else {
            sysId = localStorage.getItem("sysId");
        }
    }
    getUserMenu(sysId);

    //消息提示组件
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "newestOnTop": true,
        "progressBar": true,
        "rtl": false,
        "positionClass": "toast-top-center",
        "preventDuplicates": true,
        "onclick": null,
        "showDuration": 300,
        "hideDuration": 1000,
        "timeOut": 5000,
        "extendedTimeOut": 1000,
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
});
function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == variable){return pair[1];}
    }
    return(false);
}
/**
 * 初始化系统信息
 */
function initSysInfo() {
    $.get("/sysinfo", {}, function (r) {
        var version = r.data.version;
        var username = r.data.currentUser;

        $("#version").html("").html("v" + version);
        $("#loginUser").html("").html(username + "<span class=\"caret\"></span>");
    });
}

// 全局异常拦截
function allExceptionCatch() {
    $.ajaxSetup({
        statusCode: {
            404: function() {
                $.confirm({
                    title: '提示',
                    content: '请求的资源不存在，请联系系统维护人员！',
                    theme: 'bootstrap',
                    type: 'orange',
                    buttons: {
                        confirm: {
                            text: '确认',
                            btnClass: 'btn-blue'
                        }
                    }
                });
            },
            403: function() {
                $.confirm({
                    title: '提示',
                    content: '无权限的访问请求，请联系系统管理员获取授权！',
                    theme: 'bootstrap',
                    type: 'orange',
                    buttons: {
                        confirm: {
                            text: '确认',
                            btnClass: 'btn-blue'
                        }
                    }
                });
            },
            401: function() {
                $.confirm({
                    title: '提示',
                    content: '会话失效，请重新登录系统！',
                    theme: 'bootstrap',
                    type: 'orange',
                    buttons: {
                        confirm: {
                            text: '确认',
                            btnClass: 'btn-blue',
                            action: function(){
                                window.location= "/index"
                            }
                        }
                    }
                });
            },
            500: function() {
                $.confirm({
                    title: '提示',
                    content: '操作失败，服务出现异常了，快反馈给系统运维人员吧！',
                    theme: 'bootstrap',
                    type: 'orange',
                    buttons: {
                        confirm: {
                            text: '确认',
                            btnClass: 'btn-blue'
                        }
                    }
                });
                //不管有没有出现loading 组件，都进行一次隐藏操作
                lightyear.loading('hide');

            }
        }
    });
}

function getUserMenu(sysId) {
    $.get("/api/getUserMenu", {sysId: sysId}, function (r) {
        $(".nav-drawer").html("").html(forTree(r.data.tree.children));
        menu_tree();
        subMenu();
    });
}


function subMenu() {
    $('.nav-item-has-subnav > a' ).on( 'click', function() {
        $subnavToggle = jQuery( this );
        $navHasSubnav = $subnavToggle.parent();
        $topHasSubNav = $subnavToggle.parents('.nav-item-has-subnav').last();
        $subnav       = $navHasSubnav.find('.nav-subnav').first();
        $viSubHeight  = $navHasSubnav.siblings().find('.nav-subnav:visible').outerHeight();
        $scrollBox    = $('.lyear-layout-sidebar-scroll');
        $navHasSubnav.siblings().find('.nav-subnav:visible').slideUp(500).parent().removeClass('open');
        $subnav.slideToggle( 300, function() {
            $navHasSubnav.toggleClass( 'open' );

            // 新增滚动条处理
            var scrollHeight  = 0;
            pervTotal     = $topHasSubNav.prevAll().length,
                boxHeight     = $scrollBox.outerHeight(),
                innerHeight   = $('.sidebar-main').outerHeight(),
                thisScroll    = $scrollBox.scrollTop(),
                thisSubHeight = $(this).outerHeight(),
                footHeight    = 121;

            if (footHeight + innerHeight - boxHeight >= (pervTotal * 48)) {
                scrollHeight = pervTotal * 48;
            }
            if ($subnavToggle.parents('.nav-item-has-subnav').length == 1) {
                $scrollBox.animate({scrollTop: scrollHeight}, 300);
            } else {
                // 子菜单操作
                if (typeof($viSubHeight) != 'undefined' && $viSubHeight != null) {
                    scrollHeight = thisScroll + thisSubHeight - $viSubHeight;
                    $scrollBox.animate({scrollTop: scrollHeight}, 300);
                } else {
                    if ((thisScroll + boxHeight - $scrollBox[0].scrollHeight) == 0) {
                        scrollHeight = thisScroll - thisSubHeight;
                        $scrollBox.animate({scrollTop: scrollHeight}, 300);
                    }
                }
            }
        });
    });
}

var forTree = function (o) {
    for (var i = 0; i < o.length; i++) {
        try {
            if(o[i]["hasChildren"]) {
                urlstr += " <li class=\"nav-item nav-item-has-subnav\">";
                urlstr += "<a href=\"javascript:void(0)\"><i class=\"" + o[i]["icon"] + "\"></i>" + o[i]["text"] + "</a>";
                urlstr += "<ul class=\"nav nav-subnav\">";
                forTree(o[i]["children"]);
                urlstr += "</ul>";
                urlstr += "</li>";
            }else {
                if(o[i]["hasParent"]) {
                    urlstr += "<li> <a href=\"/"+o[i]["url"]+"\">"+o[i]["text"]+"</a> </li>";
                }else {
                    urlstr += "<li class=\"nav-item\"> <a href=\"/"+o[i]["url"]+"\"><i class=\""+o[i]["icon"]+"\"></i>"+o[i]["text"]+"</a> </li>";
                }
            }
        } catch (e) {
            console.log(e);
        }
    }
    return urlstr;
};

// 菜单点击效果
function menu_tree() {
    var urlStr = location.href;
    $(".sidebar-main ul li a").each(function () {
        if ((urlStr + '/').indexOf($(this).attr('href')) > -1 && $(this).attr('href') != '') {
            $(this).parent("li").addClass("active");
            $(this).parent("li").parents("li").addClass("open").addClass("active");
            $(this).parent("li").siblings().removeClass("active");
            $(this).parent("li").parents("li").siblings().removeClass("open").removeClass("active");
            return;
        }
    });
}

/**
 * 初始化起止日期插件：开始
 * @param beginId
 * @param endId
 * @param format
 * @param startView
 * @param maxViewMode
 * @param minViewMode
 */
function init_date_begin(beginId, endId, format, startView, maxViewMode, minViewMode) {
    var date = new Date();
    date.setMonth(date.getMonth() - 1);
    $('#' + beginId).datepicker({
        format: format,
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true,
        startView: startView,
        maxViewMode: maxViewMode,
        minViewMode: minViewMode,
    }).on("changeDate",function(ev){  //值改变事件
        //选择的日期不能大于第二个日期控件的日期
        if(ev.date){
            $("#" + endId).datepicker('setStartDate', new Date(ev.date.valueOf()));
        }else{
            $("#" + endId).datepicker('setStartDate', date);
        }
    });
}

/**
 * 初始化起止日期插件：结束
 * @param beginId
 * @param endId
 * @param format
 * @param startView
 * @param maxViewMode
 * @param minViewMode
 */
function init_date_end(beginId, endId, format, startView, maxViewMode, minViewMode) {
    $('#' + endId).datepicker({
        format: format,
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true,
        startView: startView,
        maxViewMode: maxViewMode,
        minViewMode: minViewMode,
    }).on("changeDate",function(ev){  //值改变事件
        //选择的日期不能大于第二个日期控件的日期
        if(ev.date){
            $("#" + beginId).datepicker('setEndDate', new Date(ev.date.valueOf()));
        }else{
            $("#" + beginId).datepicker('setEndDate',null);
        }
    });
}

/**
 * 初始化日期插件
 * @param id 控件ID名称
 * @param format 时间格式
 * @param startView 起始选择范围 0为日，1为月，2为年
 * @param maxViewMode 最大选择范围 0为日，1为月，2为年
 * @param minViewMode 最小选择范围 0为日，1为月，2为年
 *
 */
function init_date(id, format, startView, maxViewMode, minViewMode) {
    $('#' + id).datepicker({
        format: format,
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true,
        startView: startView,
        maxViewMode: maxViewMode,
        minViewMode: minViewMode
    });
}