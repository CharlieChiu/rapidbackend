package com.rapidbackend.security.shiro; 

import java.util.Collection;

import org.apache.shiro.ShiroException;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.subject.support.DefaultSubjectContext;

import com.rapidbackend.security.session.SessionBase;
/**
 * Override DefaultSecurityManager's behaviors on creating subject and session contexts.
 * Session is not managed by shiro. Shiro shares our own redis based sessions.
 * Shiro will not create any session. So when configured in spring, we should diable session
 * validation by config the default session manager correctly.
 * 
 * TODO config subjectdao, config and disable sessionManager sheduler
 * @author chiqiu
 *
 */
public class SimpleSecurityManager extends DefaultSecurityManager{
    public SimpleSecurityManager(){
        super();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        DefaultSubjectDAO defaultSubjectDAO = new DefaultSubjectDAO();
        defaultSubjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        this.setSubjectDAO(defaultSubjectDAO);// disable session store
        this.setSessionManager(new DisabledSessionManager());
    }
    /**
     * override this method from DefaultSecurityManager.
     * Shiro calls this method in subjectFactory.createSubject() .
     * Shiro is actually using internal delegating subject as subjectcontext which should be used to store subject principals and session. 
     * This is confusing to developers and themselves.
     * So this function in future shiro releases might change though.<br/>
     * [Warning!]Remember to check shiro source code if we want to upgrade to newer version.
     * Check if this function still be called in DefaultSecurityManager.createSubject(SubjectContext) and currently this function in
     * DefaultSecurityManager's implementation(version 1.2.1) is :
     * 
     * * Creates and returns a new {@code Subject} instance reflecting the cumulative state acquired by the
         * other methods in this class.
     * protected Subject doCreateSubject(SubjectContext context) {
        return getSubjectFactory().createSubject(context);
        }
     */
    @Override
    protected Subject doCreateSubject(SubjectContext context) {
        Subject result = null;
        if(context instanceof DefaultSubjectContext){
            if (context.getSubject()!=null) {
                Subject subject = context.getSubject();
                if(subject instanceof SimpleDelegatingSubject){
                    SimpleDelegatingSubject simpleDelegatingSubject
                    =(SimpleDelegatingSubject) subject;
                    /*
                     * lines commented out here are properties should be set 
                     * when we created the original subject 
                     */
                    //SecurityManager securityManager = context.resolveSecurityManager();
                    //Session session = context.resolveSession();
                    //boolean sessionCreationEnabled = context.isSessionCreationEnabled();
                    PrincipalCollection principals = context.resolvePrincipals();
                    boolean authenticated = context.resolveAuthenticated();
                    String host = context.resolveHost();
                    simpleDelegatingSubject.setHost(host);
                    simpleDelegatingSubject.setAuthenticated(authenticated);
                    simpleDelegatingSubject.setPrincipals(principals);
                    
                    result = simpleDelegatingSubject;
                }else {
                    throw new ShiroException("Only SimpleDelegatingSubject allowed, unsupported subject type :"+subject.getClass().getName());
                }
                
            }else {
                throw new ShiroException("Subject in subjectContext is null!!");
            }
        }else {
            throw new ShiroException("SubjectContext context type is not DefaultSecurityContext! unsupported subjectcontext type :"+context.getClass().getName());
        }
        return result;
    }
    /**
     * enables session cache for every realm
     * @param sessionBase
     */
    public void enableSessionCache(SessionBase sessionBase){
        if(sessionBase==null){
            //Do we need to throw exception?
        }else {
            Collection<Realm> realms = getRealms();
            for(Realm realm : realms){
                if(realm instanceof CommandSpecificRealm){
                    ((CommandSpecificRealm)realm).setSession(sessionBase);
                }
            }
        }
        
    }
}
