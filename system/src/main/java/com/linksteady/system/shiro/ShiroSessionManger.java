package com.linksteady.system.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
 * @author hxcao
 * @date 2019-07-03
 */
public class ShiroSessionManger extends DefaultWebSessionManager {

    public ShiroSessionManger() {}

    @Override
    protected Session retrieveSession(SessionKey sessionKey){
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;
        if(sessionKey instanceof WebSessionKey){
            request = ((WebSessionKey)sessionKey).getServletRequest();
        }
        if(request != null && sessionId != null){
            Session session =  (Session) request.getAttribute(sessionId.toString());
            if(session != null){
                return session;
            }
        }
        Session session = super.retrieveSession(sessionKey);
        if(request != null && sessionId != null){
            request.setAttribute(sessionId.toString(),session);
        }
        return session;
    }
}
