package com.rapidbackend.socialutil.process.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.core.process.interceptor.CommandInterceptor;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.dao.BaseDao;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.time.SimpleTimer;


/**
 * verifies if the user who requests the current command is the author of target DB Record
 * @author chiqiu
 *
 */
public class VerifyAuthorInterceptor extends AppContextAware implements CommandInterceptor{
    Logger logger = LoggerFactory.getLogger(VerifyAuthorInterceptor.class);
    protected BaseDao modelDao;
    
    public BaseDao getModelDao() {
        return modelDao;
    }
    @Required
    public void setModelDao(BaseDao modelDao) {
        this.modelDao = modelDao;
    }

    @Override
    public boolean agree(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        boolean result = false;
        SimpleTimer timer = new SimpleTimer("VerifyAuthorInterceptor");
        try {
            CommandParam idparam = request.getParam(ParamNameUtil.ID);
            
            if(idparam == null || !(idparam instanceof IntParam)){
                throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"can't find id param");
            }
            
            IntParam id = (IntParam)idparam;
            UserBase userBase = request.getUser();
            if(userBase == null){
                throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"can't find current user");
            }
            
            Object model = modelDao.selectById(id.getData(), modelDao.getModelClass());
            
            Integer userId = (Integer)ModelReflectionUtil.getPropertyValue(modelDao.getModelClass(), ParamNameUtil.USER_ID, model);
            if(userId == userBase.getId()){
                result = true;
                request.getCurrentHandleInfo().addMessage(timer.getIntervalString());
            }else {
                request.getCurrentHandleInfo().addMessage(timer.getIntervalString()+": rejected");
            }
            
        } catch (Exception e) {
            logger.error("VerifyAuthorInterceptor: rejected request because of ",e);
            request.getCurrentHandleInfo().addMessage(timer.getIntervalString()+": rejected");
            throw new BackendRuntimeException(BackendRuntimeException.METHOD_NOT_ALLOWED,"VerifyAuthorInterceptor: rejected request because of ",e);
        }
        if(!result){
            throw new BackendRuntimeException(BackendRuntimeException.METHOD_NOT_ALLOWED,"VerifyAuthorInterceptor: rejected ");
        }
        return result;
    }
}
