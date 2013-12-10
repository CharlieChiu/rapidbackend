package com.rapidbackend.core.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rapidbackend.core.process.ProcessStatus;
import com.rapidbackend.util.time.SimpleTimer;
import com.rapidbackend.security.session.SessionBase;
import com.rapidbackend.security.session.SessionFactory;
import com.rapidbackend.socialutil.model.reserved.UserBase;

/**
 * @author chiqiu
 */
public class RequestBase implements CommandRequest{
	
	protected String requestPath;
	protected Params requestParams = new ParamsBase();
	protected String command;
	protected ProcessStatus processStatus = ProcessStatus.Processing;
	protected List<HandleInfo> handleInfoTrack = new ArrayList<HandleInfo>(7);
	protected HashMap<String,Object> temporaryData = new HashMap<String,Object>();
    protected HandleInfo currentHandleInfo;
    protected SimpleTimer totalTimer;
    protected SessionBase session;
    protected boolean showHandleInfo;
    protected String requestIpAddress;
    protected static String Default_User_Object_Name = "UserInTheHouse";
    @Override
    public void setUser(UserBase user){
        temporaryData.put(Default_User_Object_Name, user);
    }
    @Override
    public UserBase getUser(){
        return (UserBase)temporaryData.get(Default_User_Object_Name);
    }
    
    @Override
    public String getRequestIpAddress() {
        return requestIpAddress;
    }
    @Override
    public void setRequestIpAddress(String requestIpAddress) {
        this.requestIpAddress = requestIpAddress;
    }
    @Override
    public boolean isShowHandleInfo() {
        return showHandleInfo;
    }
    @Override
    public void setShowHandleInfo(boolean showHandleInfo) {
        this.showHandleInfo = showHandleInfo;
    }
    
    public SessionBase getSession() {
        return session;
    }
    @Override
    public void setSession(SessionBase session) {
        this.session = session;
    }
    /**
     * Per our shiro integration, we always create an session for shiro.
     * But, we will never let shiro manage this session for us.
     * If we need to use a stored redis session, We should add a session creation handler
     * before our handle logic. If we want to store a session. We should append a session store handler to the pipeline
     */
    @Override
    public SessionBase getSession(boolean create){
        if(session==null && create){
            session = SessionFactory.createSession();
        }
        return session;
    }
    
    public RequestBase(){
        totalTimer = new SimpleTimer();
    }
    @Override
    public void putTemporaryData(String name, Object value) {
        temporaryData.put(name, value);
    }
    
    @Override
    public Object getTemporaryData(String name) {
        return temporaryData.get(name);
    }
    
    @Override
	public HandleInfo getCurrentHandleInfo() {
        return currentHandleInfo;
    }
    @Override
    public void setCurrentHandleInfo(HandleInfo currentHandleInfo) {
        this.currentHandleInfo = currentHandleInfo;
    }
    @Override
    public HashMap<String, Object> getTemporaryData() {
        return temporaryData;
    }
    @Override
    public void setTemporaryData(HashMap<String, Object> temporaryData) {
        this.temporaryData = temporaryData;
    }
	@Override
    public String getCommand() {
        return command;
    }
	@Override
    public void setCommand(String command) {
        this.command = command;
    }
    @Override
    public String getRequestPath() {
		return requestPath;
	}
	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}
    @Override
    public Params getRequestParams() {
		return requestParams;
	}
	public void setRequestParams(Params requestParams) {
		this.requestParams = requestParams;
	}
	@Override
	public CommandRequest addParam(CommandParam param){
		requestParams.setParam(param.getName(), param);
		return this;
	}
	@Override
	public CommandParam getParam(String name){
	    return requestParams.getParam(name);
	}
	@Override
    public ProcessStatus getProcessStatus() {
        return processStatus;
    }
	@Override
    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }
	@Override
	public void trackHandleInfo(HandleInfo handleInfo){
	    handleInfoTrack.add(handleInfo);
	    setCurrentHandleInfo(handleInfo);
	}
	@Override
	public void addHandleInformation(String message){
	    currentHandleInfo.addMessage(message);
	}
	/**
	 * returns handle infos too
	 */
	@Override
	public String toString(){
	    StringBuffer sb = new StringBuffer("");
	    sb.append("totalTime:").append(totalTimer.getIntervalMili()).append("ms:");
	    sb.append("ip:").append(requestIpAddress).append(":");
	    sb.append("request:");
        sb.append("command").append(":").append(getCommand());
        sb.append(",");
        sb.append(requestParams.toString());
        sb.append(";\n");
        for(HandleInfo handleInfo : handleInfoTrack){
            sb.append(handleInfo.toString());
        }
        return sb.toString();
	}
	
	public static class HandleInfo{
	    protected SimpleTimer timer;
	    protected StringBuffer processInfo = new StringBuffer("");
	    
	    public void addMessage(String message){
	        processInfo.append(message);
	        processInfo.append(", ");
	    }
	    public HandleInfo(SimpleTimer timer){
	        this.timer = timer;
	    }
	    
	    public SimpleTimer getTimer() {
            return timer;
        }

        public void setTimer(SimpleTimer timer) {
            this.timer = timer;
        }

        public String toString(){
	        StringBuffer sb = new StringBuffer();
	        sb.append("[(");
	        if(timer!=null){
	            sb.append(timer.getIntervalString());
	        }
	        sb.append(":");
	        sb.append(processInfo);
	        sb.append(")]\n");
	        return sb.toString();
	    }
	}
	
}
