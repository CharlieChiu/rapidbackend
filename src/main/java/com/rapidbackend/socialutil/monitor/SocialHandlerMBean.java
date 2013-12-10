package com.rapidbackend.socialutil.monitor;


public interface SocialHandlerMBean extends SocialMBean{
    public String getCurrentState();
    public String[] getAllPossiableStates();
    public long howManyRequstHanded();
    /**
     * how log has this bean lives
     * @return
     */
    public long howLongSinceIWasBorn();
    public long timeSpentOnCurrentRequest();
    
}
