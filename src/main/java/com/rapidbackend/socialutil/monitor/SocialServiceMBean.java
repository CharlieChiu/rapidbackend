package com.rapidbackend.socialutil.monitor;

import java.util.List;

public interface SocialServiceMBean extends SocialMBean{
    public List<String> getServices();
    public void doStop() throws Exception;
    public void doStart() throws Exception;
}
