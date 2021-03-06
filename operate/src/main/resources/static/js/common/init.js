var urlstr = "";
$(document).ready(function () {
    allExceptionCatch();
    getUserMenu();
    getSysMsg();
});

$("#dropdownMenu,#msg_detail_modal").on('click', function (e) {
    e.stopPropagation();
});

/**
 * 获取系统通知消息
 */
function getSysMsg() {
    $.get("/msg/getMsgList", {}, function (r) {
        if(r.code === 200) {
            var data = r.data;
            var len = data.length;
            if(len === 0) {
                $("#msgTable").hide();
                $("#dropdownMenu").attr("style", 'width:200px;');
            }else {
                $("#msgTable").show();
                $("#dropdownMenu").attr("style", 'width:400px;');
            }
            $("#noReadCnt").text(data['dataCount']);
            appendTable(data['msgList']);
        }
    });
}

function appendTable(data) {
    var code = '';
    data.forEach((v, k)=>{
        var createDt = v['createDt'];
        var msgTitle = v['msgTitle'];
        var content = v['msgContent'];
        var msgLevelDesc=v['msgLevelDesc'];
        var newContent = content.length > 30 ? content.substr(0, 30) + " ..." : content;
        code += "<tr>" +
            "<td>"+v['createDt']+"&nbsp;&nbsp;<a style=\"text-decoration:underline dotted;cursor: pointer;color: #333;\" onclick=\"msgTitleClick(this, "+v['msgId']+", "+v['readFlag']+")\">"+msgTitle+"</a>" +
            "<hr style=\"margin-top: 5px;margin-bottom: 5px;\" hidden/>" +
            "<p class=\"h6\" hidden><a style='color: #48b0f7;cursor: pointer;' onclick='viewDetail(\""+createDt+"\", \""+msgTitle+"\", \""+content+"\",\""+msgLevelDesc+"\")'>"+newContent+"</a></p></td>" +
            "<td>"+getIcon(v['readFlag'])+"</td>" +
            "</tr>";
    });
    $("#msgTable").html('').append(code);
}

function getIcon(readFlag) {
    if (readFlag === '0') {
        return "<span style='color: #f96868'><i class='fa fa-circle'></i></span>";
    } else {
        return "";
    }
}
function viewDetail(createDt, msgTitle, content,msgLevelDesc) {
    $("#msgDetailDiv").html('').append("<p>时间："+createDt+"</p><p>等级："+msgLevelDesc+"</p><p>标题："+msgTitle+"</p><p>内容："+content+"</p>");
    $("#msg_detail_modal").modal('show');
}

function msgTitleClick(dom, msgId, readFlag) {
    $(dom).nextAll().toggle();
    if(readFlag === 0) {
        $.get("/msg/updateMsgRead", {msgId: msgId}, function (r) {
            if(r.code === 200) {
                $(dom).parent().next().html('').append('');
            }
        });
    }
}

function readAll() {
    $.get("/msg/updateMsgRead", {}, function (r) {
        if(r.code === 200) {
            $MB.n_success("标记成功！");
            //刷新表格
            getSysMsg();
        }
    });
}

// 全局异常拦截
function allExceptionCatch() {
    $.ajaxSetup({
        statusCode: {
            404: function() {
                $MB.n_danger('请求的资源不存在，请联系系统维护人员！');
            },
            403: function() {
                $MB.n_danger('无权限的访问请求，请联系系统管理员获取授权！');
            },
            401: function() {
                $MB.confirm({
                    title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
                    content: '会话失效，请重新登录系统！',

                },function () {
                    window.location= "/index";
                });
            },
            500: function() {
                $MB.n_danger('操作失败，服务出现异常了，快反馈给系统运维人员吧！');
                //不管有没有出现loading 组件，都进行一次隐藏操作
                lightyear.loading('hide');
                $MB.loadingDesc('hide');

            }
        }
    });
}

function getUserMenu() {
    if(document.getElementsByClassName("sidebar-main").length != 0) {
        $.get("/api/findUserMenu", function (r) {
            if(r.code===200)
            {
                var username=r.msg.username;
                $(".nav-drawer").html("").html(forTree(r.msg.tree.children));
                $("#loginUser").html("").html(username + "<span class=\"caret\"></span>");

                menu_tree();
                subMenu();
                $("#pageTitle").html("").html(r.data);

                //设置退出
                $("#logoutbtn").attr("href",r.msg.logoutUrl);
            }
        });
    }
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
