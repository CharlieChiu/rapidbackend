package com.rapidbackend.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import com.rapidbackend.util.general.ReflectionTools;

/**
 * This class wraps all the error codes of SocialUtil. Those exception definitions will be used in error handling.
 * The design purpose is to be able to return those definitions to user when an error happens.
 * @author chiqiu
 *
 */
@Deprecated
public abstract class BackendErrorCodes {
	public static HashMap<Integer, String> errorDefinationHashMap = new HashMap<Integer, String>(7);
	
	
	public static Integer DataRetrivalError = 20000;
	
	/*
     * processing error
     */
	public static Integer UnexpectedErrorHappenedInHandling = 30000;
	
    public static Integer ErrorCreatingCommand = 30001;
    public static Integer ErrorInSelectSingleDbRecordHandler = 30002;
    public static Integer ErrorInFeedCacheReadHandler = 30003;
    public static Integer ErrorInSelectMultipleDbRecordHandler = 30004;
    public static Integer ErrorInCacheUpdateHandler = 30005;
    public static Integer ErrorInCacheLockHandler = 30006;
    public static Integer ErrorInCacheUnLockHandler = 30007;
    
    public static Integer ErrorInCreateResultHandler = 30008;
    public static Integer ErrorInGetAllSeedFeedIdHandler = 30009;
    public static Integer ErrorInMergeFeedResultHandler = 30010;
    public static Integer ErrorInGetAllFeedUserIdHandler = 30011;
    
    public static Integer ErrorInUpdateSingleDbRecordHandler = 30012;
    
    public static Integer ErrorCreatingModelBeanInDbdataHandlerWithDefaultConstructor = 30014;
    public static Integer ErrorAutowiringModelProperty = 30015;
    public static Integer ErrorInDecreaseCounterHandler = 30016;
    
    public static Integer ErrorInReadFollowerListHandler = 30017;
    public static Integer ErrorInPostFeedHandler = 30018;
    public static Integer ErrorInGetInboxFeedIdListHandler = 30019;
    public static Integer ErrorInSearchFeedHandler = 30020;
    
    
    /*
     * 
     */
	
	/*
	 * param error
	 */
	public static Integer BadRequestParam = 40001;
	public static Integer RequestParamMissing = 40002;
    public static Integer BadCommandName = 40003;
    public static Integer MissingCommandName = 40004;
    public static Integer MissingRequestBody = 40005;
    
    
	
	/*
	 * Configuration errors
	 */
	public static Integer KestrelConfigError = 50101;
	public static Integer RedisPoolConfigError = 50201;
	
	
	/*
	 * platform errors
	 */
	public static Integer MBeanRegisterError = 60001;
	public static Integer MBeanUnRegisterError = 60002;
	
	public static Integer KestrelInitError = 60101;
	public static Integer RedisInitError = 60201;
	public static Integer RedisGenericError = 60202;
	public static Integer RedisQueueInitError = 60203;
	
	/*
	 * Internal request process error
	 */
	public static Integer InternalDataBaseObjectConvertError = 70001;
	
	public static Integer InternalFeedConversionError = 70101;
	public static Integer InternalFeedPostError = 70102;
	public static Integer InternalFeedRepostError = 70103;
	
		
	
	//Deprecated this map, keep error in error messages please, this map is use less
	static{
	    
	    errorDefinationHashMap.put(DataRetrivalError, "Error accessing data stored in database");
	    
	    errorDefinationHashMap.put(ErrorCreatingCommand, "error creating social command");
	    errorDefinationHashMap.put(ErrorInSelectSingleDbRecordHandler, "Error In SelectSingleDbRecordHandler");
	    errorDefinationHashMap.put(ErrorInSelectMultipleDbRecordHandler, "Error In SelectMultipleDbRecordHandler");
	    errorDefinationHashMap.put(ErrorInFeedCacheReadHandler, "Error In FeedCacheReadHandler");
	    errorDefinationHashMap.put(ErrorInCacheLockHandler, "Error In CacheLockHandler");
	    errorDefinationHashMap.put(ErrorInCacheUnLockHandler, "Error In CacheUnLockHandler");
	    errorDefinationHashMap.put(ErrorInCreateResultHandler, "Error In Create Result Handler");
	    errorDefinationHashMap.put(ErrorInGetAllSeedFeedIdHandler, "Error In GetAllSeedFeedIdHandler");
	    errorDefinationHashMap.put(ErrorInMergeFeedResultHandler, "Error In MergeFeedResultHandler");
	    errorDefinationHashMap.put(ErrorInGetAllFeedUserIdHandler, "Error In GetAllFeedUserIdHandler");
        
	    
	    
		errorDefinationHashMap.put(BadRequestParam, "Error format in one or more request params");
		errorDefinationHashMap.put(RequestParamMissing, "One of the requried param is missing");
		errorDefinationHashMap.put(BadCommandName, "unsupported command name");
		errorDefinationHashMap.put(MissingCommandName, "missing command name");
		errorDefinationHashMap.put(MissingRequestBody,"request body is null");
		
		
		errorDefinationHashMap.put(RedisPoolConfigError, "Redis Pool Config Error");
		errorDefinationHashMap.put(KestrelConfigError, "Kestrel Config Error");
		
		errorDefinationHashMap.put(MBeanRegisterError, "MBeanRegister Error");
		errorDefinationHashMap.put(MBeanUnRegisterError, "MBeanUnRegister Error");
		errorDefinationHashMap.put(KestrelInitError, "Kestrel Init Error");
		errorDefinationHashMap.put(RedisQueueInitError, "Redisqueue Init Error");
		errorDefinationHashMap.put(RedisInitError, "Redis Init Error");
		errorDefinationHashMap.put(RedisGenericError, "Redis generic error");
		
		errorDefinationHashMap.put(InternalDataBaseObjectConvertError, "Internal DataBase Object Convert Error");
		errorDefinationHashMap.put(InternalFeedConversionError, "Internal Feed Conversion Error");
		errorDefinationHashMap.put(InternalFeedPostError, "Internal FeedPost Error");
		errorDefinationHashMap.put(InternalFeedRepostError, "Internal Feed Repost Error");
	}
	
	public static String getErrorMessage(Integer errcode){
		String description = errorDefinationHashMap.get(errcode);
		return description;
	}
	/**
	 * TODO move this UT into junit test cases
	 * test if any code doesn't have a value in errorDefinationHashMap
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		List<Field> fields = ReflectionTools.getDeclaredFields(BackendErrorCodes.class, String.class);
		
		for(Field field : fields){
			System.out.println(field.getName());
			String value = (String)field.get(null);
			BackendErrorCodes.getErrorMessage(Integer.valueOf(value));
		}
	}
}
