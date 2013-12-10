package com.rapidbackend.core.process;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanNameAware;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.core.process.interceptor.NiceGuyInterceptor;
import com.rapidbackend.core.process.interceptor.CommandInterceptor;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.IntListParam;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.ParamFactory;
import com.rapidbackend.core.request.RequestBase.HandleInfo;
import com.rapidbackend.core.request.StringParam;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.model.util.TypeFinder;
import com.rapidbackend.util.general.ConversionUtils;
import com.rapidbackend.util.time.SimpleTimer;

/**
 * @author chiqiu
 */
public abstract class RequestHandlerBase extends AppContextAware implements RequestHandler, BeanNameAware {
    
    protected String handlerName;
    protected RequestHandler nextHandler;
    protected boolean disabled = false;
    protected CommandInterceptor[] interceptors = new CommandInterceptor[]{new NiceGuyInterceptor()};
    protected static String Default_User_Object_Name = "UserInTheHouse";
    
    protected int handlerIndex;
    
    @Override
    public void setIndex(int index) {
        this.handlerIndex = index;
    }
    
    @Override
    public void modifyHandlerName(String name){
        this.handlerName = name;
    }
    
    public RequestHandlerBase(){
    }
    public boolean isDisabled() {
        return disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    protected String beanName;
    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }
    @Override
    public String getHandlerName() {
        if(StringUtils.isEmpty(handlerName)){
            return beanName;
        }else {
            return handlerName;
        }
    }

    @Override
    public abstract void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException;
    @Override
    public RequestHandler getNextHandler(){
        return nextHandler;
    }
    public void setNextHandler(RequestHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
    @Override
    public void prepareHandling(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        SimpleTimer timer = new SimpleTimer(getHandlerName());
        HandleInfo handleInfo = new HandleInfo(timer);
        request.trackHandleInfo(handleInfo);
    }
    @Override
    public void endHandling(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        request.getCurrentHandleInfo().getTimer().stop();
    }
    @Deprecated
    public static  UserBase getCurrentUser(CommandRequest request){
        return (UserBase)request.getTemporaryData(Default_User_Object_Name);
    }
    
    public static Integer getCurrentUserId(CommandRequest request){
        Integer userId = null;
        if(userId == null){
            UserBase user = request.getUser();
            if(user!= null){
                userId = user.getId();
            }
        }
        if(userId == null){
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"can't find userId is request and can't find user object in request context");
        }
        return userId;
    }
    
    /**
     * 
     * @param request
     * @param paramName
     * @return
     */
    public int[] getIntListInRequest(CommandRequest request, String paramName){
        int[] ids = null;
        Object o = request.getTemporaryData(paramName);
        if(o!=null && o instanceof int[]){
            ids = (int[])o;// first check temp data, controlled by us
        }
        if(o!=null && o instanceof List<?>){
            int[] result = ConversionUtils.integerCollectionToIntArray((List<Integer>)o);
            ids = result;
        }
        if(ids == null){
            IntListParam param = (IntListParam)request.getParam(paramName);// then check input param, controlled by user
            if(param!=null){
                ids = param.getData();
            }
        }
        if (ids == null) {// finally check if we can convert it from temporary data
            HashMap<Integer, Object> previousResults = (HashMap<Integer, Object>)request.getTemporaryData(paramName);
            if(previousResults!=null){
                Set<Integer> keys = previousResults.keySet();
                ids = ConversionUtils.integerCollectionToIntArray(keys);
            }
        }
        if(ids == null || ids.length ==0){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"id list "+paramName+" is empty");
        }
        
        return ids;
    }
    /**
     * 
     * @param request
     * @param paramName
     * @return
     */
    public int[] getIntListInRequestSliently(CommandRequest request, String paramName){
        int[] ids = null;
        Object o = request.getTemporaryData(paramName);
        if(o!=null && o instanceof int[]){
            ids = (int[])o;// first check temp data, controlled by us
        }
        if(ids == null){
            IntListParam param = (IntListParam)request.getParam(paramName);// then check input param, controlled by user
            if(param!=null){
                ids = param.getData();
            }
        }
        if (ids == null) {// finally check if we can convert it from temporary data
            HashMap<Integer, Object> previousResults = (HashMap<Integer, Object>)request.getTemporaryData(paramName);
            if(previousResults!=null){
                Set<Integer> keys = previousResults.keySet();
                ids = ConversionUtils.integerCollectionToIntArray(keys);
            }
        }
        if(ids == null){
            ids = new int[0];
        }
        
        return ids;
    }
    
    /**
     * get integer value of the param, will throw an exception if the param cann't be found
     * @param request
     * @param paramName
     * @return
     */
    public Integer getIntParamInRequest(CommandRequest request, String paramName){
        Integer id = null;
        id = (Integer)request.getTemporaryData(paramName);// first check temp data, controlled by us
        if(id == null){
            IntParam param = (IntParam)request.getParam(paramName);// then check input param, controlled by user
            if(param!=null){
                id = param.getData();
            }else {
				throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"cann't find param "+ paramName);
			}
        }
        return id;
    }
    /**
     * get integer value of the param, return null if the param cann't be found
     * @param request
     * @param paramName
     * @return
     */
    public Integer getIntParamInRequestSilently(CommandRequest request, String paramName){
        Integer id = null;
        id = (Integer)request.getTemporaryData(paramName);// first check temp data, controlled by us
        if(id == null){
            IntParam param = (IntParam)request.getParam(paramName);// then check input param, controlled by user
            if(param!=null){
                id = param.getData();
            }
        }
        return id;
    }
    
    
    
    public String getStringParamInRequest(CommandRequest request, String paramName){
        String result = null;
        result = (String)request.getTemporaryData(paramName);// first check temp data, controlled by us
        if(result == null){
            StringParam param = (StringParam)request.getParam(paramName);// then check input param, controlled by user
            if(param!=null){
                result = param.getData();
            }
        }
        return result;
    }
    @Override
    public void handleException(Integer errCode, String message,Exception e) throws BackendRuntimeException{
        if(e instanceof BackendRuntimeException){
            throw (BackendRuntimeException)e;
        }else {
            if(StringUtils.isEmpty(message)){
                throw new BackendRuntimeException(errCode, getHandlerName(), e);
            }else {
                throw new BackendRuntimeException(errCode, message, e);
            }
        }
    }
    @Override
    public CommandInterceptor[] getInterceptors() {
        return interceptors;
    }
    public void setInterceptors(CommandInterceptor[] interceptors) {
        this.interceptors = interceptors;
    }
    /**
     * 
     */
    @Override
    public void cancelCurrentCommand(CommandRequest request,CommandResponse response){
        request.setProcessStatus(ProcessStatus.Canceled);
    }
    @Override
    /**
     * By default, all handlers will handle all requests.
     * Please override this function in handler implementation.
     */
    public boolean interceptRequest(CommandRequest request,CommandResponse response){
        if (null== getInterceptors()) {
            return true;
        }else {
            boolean result = true;
            for(CommandInterceptor interceptor : getInterceptors()){
                result = interceptor.agree(request, response);
                if(!result){
                    break;
                }
            }
            return result;
        }
    }
    @Override
    public boolean isRequestReadyForHandling(CommandRequest request){
        return request.getProcessStatus()!=ProcessStatus.Canceled;
    }
    @Override
    public boolean isAbortCurrentCommand(CommandRequest request, CommandResponse response){
        return !isRequestReadyForHandling(request) //request not ready
        || !interceptRequest(request, response);// or our interceptor rejected it
    }
    /**
     * Simply cancel this request.
     * Please override this function in subclass if you want more control over the request and response
     */
    @Override
    public void handleAborting(CommandRequest request,CommandResponse response){
        cancelCurrentCommand(request,response);
    }
    protected static HashSet<String> defaultExcludedModelFieldNames = new HashSet<String>();
    static{
        defaultExcludedModelFieldNames.add("id");
    }
    
    protected HashSet<String> excludedModelFieldNames = defaultExcludedModelFieldNames;
    /**
     * 
     * @param request
     * @param modelClassName
     * @return
     * @throws BackendRuntimeException
     */
    public Object createModelObjectFromRequest(CommandRequest request,String modelClassName) throws BackendRuntimeException{
        Class<?> clazz = modelTypeFinder.getModelClass(modelClassName);
        return createModelObjectFromRequest(request,clazz);
    }
    /**
     * 
     * @param request
     * @param clazz
     * @return
     */
    public Object createModelObjectFromRequest(CommandRequest request,Class<?> clazz){
        Field[] fields = ModelReflectionUtil.getModelFields(clazz);
        Object model = null;
        try {
            model = clazz.newInstance();
        } catch (Exception e) {
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,getHandlerName()+": error creating model "+clazz.getSimpleName()+" from request",e);
        }
        
        for(Field f: fields){
            CommandParam param = request.getParam(f.getName());
            if(param!=null&&ParamFactory.isAllowedModelFieldParamType(param)){
                if(!excludedModelFieldNames.contains(param.getName())){
                    try {
                        ModelReflectionUtil.setPropertyValue(clazz, f.getName(), param.getData(), model);
                        //f.set(model, param.getData());
                    } 
                    catch (RuntimeException ex) {
                        throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,getHandlerName()+": error creating model "+clazz.getSimpleName()+" from request", ex);
                    }
                }
            }
        }
        
        return model;
    }
    protected TypeFinder modelTypeFinder ;
    public TypeFinder getModelTypeFinder() {
        return modelTypeFinder;
    }
    
    public void setModelTypeFinder(TypeFinder modelTypeFinder) {
        this.modelTypeFinder = modelTypeFinder;
    }
    
    public Class<?> getModelClass(String className){
        return modelTypeFinder.getModelClass(className);
    }
    
    public HashSet<String> getExcludedModelFieldNames() {
        return excludedModelFieldNames;
    }
    public void setExcludedModelFieldNames(HashSet<String> excludedModelFieldNames) {
        this.excludedModelFieldNames = excludedModelFieldNames;
    }
}
