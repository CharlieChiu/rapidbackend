package com.rapidbackend.security.session;
/**
 * For now , Let our session implementation be compatible with shiro's session.
 * We want shiro to write some information as username, authorities into our session store to enhance shiro performance.
 * So we will not disable shiro session totally. Or maybe we should disable shiro session totally and make all our shiro realm session aware?
 * @author chiqiu
 *
 */
@Deprecated
public interface Session extends org.apache.shiro.session.Session{
    public SessionBase getSessionBase();
    public void setSessionBase(SessionBase sessionBase);
}
