package com.rapidbackend.util.thread;

import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * orginal written by apache camel(http://camel.apache.org)
 */
public final class DefaultThreadFactory implements ThreadFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultThreadFactory.class);

    private final String pattern;
    private final String name;
    private final boolean daemon;

    public DefaultThreadFactory(String pattern, String name, boolean daemon) {
        this.pattern = pattern;
        this.name = name;
        this.daemon = daemon;
    }

    public Thread newThread(Runnable runnable) {
        String threadName = ThreadHelper.resolveThreadName(pattern, name);
        Thread answer = new Thread(runnable, threadName);
        answer.setDaemon(daemon);
        LOG.trace("Created thread[{}]: {}", name, answer);
        return answer;
    }

    public String toString() {
        return "DefaultThreadFactory[" + name + "]";
    }
}