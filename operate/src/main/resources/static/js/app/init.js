var urlstr = "";
$(document).ready(function () {
    allExceptionCatch();
    getUserMenu();
});

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

function getUserMenu() {
    $.get("/findUserMenu", function (r) {
        if(r.code===200)
        {
            var username=r.msg.username;
            $(".nav-drawer").html("").html(forTree(r.msg.tree.children));
            $("#loginUser").html("").html(username + "<span class=\"caret\"></span>");

            menu_tree();
            subMenu();
            $("#pageTitle").html("").html(r.data);

            //设置返回导航页
            $("#navigatorUrl").attr("href",r.msg.navigatorUrl);

            //设置退出
            $("#logoutbtn").attr("href",r.msg.logoutUrl);

            if(r.msg.single) {
                $("#selectSys").attr("hidden", true);
            }
        }
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
                    urlstr += "<li> <a href=\""+o[i]["url"]+"\">"+o[i]["text"]+"</a> </li>";
                }else {
                    urlstr += "<li class=\"nav-item\"> <a href=\""+o[i]["url"]+"\"><i class=\""+o[i]["icon"]+"\"></i>"+o[i]["text"]+"</a> </li>";
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
    // 防止域名中包含路由字符串
    var urlStr = location.href;
    var domain = urlStr.indexOf(".com") > -1 ? ".com" : "localhost";
    urlStr = urlStr.split(domain)[1];
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
