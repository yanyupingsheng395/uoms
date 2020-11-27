$(document).ready(function () {
    allExceptionCatch();
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
            }
        }
    });
}

