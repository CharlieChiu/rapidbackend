package com.rapidbackend.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.util.comm.redis.client.Jedis;

import redis.clients.jedis.Tuple;

import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.util.comm.redis.client.RedisClient;
import com.rapidbackend.util.comm.redis.client.RedisClientPoolContainer;
import com.rapidbackend.util.general.ConversionUtils;
import com.rapidbackend.util.io.JsonUtil;
import com.rapidbackend.util.io.SmileJsonUtil;
/**
 * a wrapper of Redis instances.
 * @author chiqiu
 */
public class RedisCache extends IntKeyLockable{
    Logger logger = LoggerFactory.getLogger(RedisCache.class);
    protected CacheMapper cacheMapper;
    protected RedisCacheConfig redisCacheConfig;
    public CacheMapper getCacheMapper() {
        return cacheMapper;
    }
    public void setCacheMapper(CacheMapper cacheMapper) {
        this.cacheMapper = cacheMapper;
    }
    
    public RedisCacheConfig getRedisCacheConfig() {
        return redisCacheConfig;
    }
    public void setRedisCacheConfig(RedisCacheConfig redisCacheConfig) {
        this.redisCacheConfig = redisCacheConfig;
    }
    
    public static String emptyRecord = "recordNotFound";
    
    
    public static String getEmptyRecord() {
        return emptyRecord;
    }
    public static void setEmptyRecord(String emptyRecord) {
        RedisCache.emptyRecord = emptyRecord;
    }
    /**
     * check if all targets have been assigned a client pool successfully
     * @return
     */
    Boolean cacheIsReady = null;
    public boolean isCacheReady(){
        
        if(cacheIsReady == null){
            Set<String> redisTargets = cacheMapper.getAllRedisTargetNames();
            
            cacheIsReady = false;
            for(String target:redisTargets){
                logger.debug("checking cache target "+ target);
                if(Rapidbackend.getCore().getRedisClientPoolContainer().checkPoolExistance(target) ==null){
                    cacheIsReady = false;
                    logger.debug("cache target "+ target + " not ready");
                    break;
                }else {
                    logger.debug("cache target "+ target + " ready");
                    cacheIsReady = true;
                }
            }
            
        }
        
        return cacheIsReady;
    }

    protected Rapidbackend rapidbackend;
    /**
     * set the object with key 'id', object will be convert to byte[], in smile json
     * @param <T>
     * @param object
     * @param id
     * @param redisTarget
     * @throws Exception
     */
    public void setJsonObject(Object object, Integer id,String redisTarget) throws InterruptedException,IOException{
        RedisClient client = null;
        try{
            byte[] data = JsonUtil.writeObject(object);            
            client = getRedisPoolContainer().borrowClient(redisTarget);
            byte[] idkey = id.toString().getBytes();
            client.getJedis().set(idkey, data);
        }finally {
             getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    /**
     * same object with non int key
     * @param object
     * @param key
     * @param redisTarget
     * @throws InterruptedException
     * @throws IOException
     */
    public void setJsonObject(Object object, String key,String redisTarget) throws InterruptedException,IOException{
        RedisClient client = null;
        try{
            byte[] data = JsonUtil.writeObject(object);            
            client = getRedisPoolContainer().borrowClient(redisTarget);
            byte[] idkey = key.getBytes();
            client.getJedis().set(idkey, data);
        }finally {
             getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    /**
     * 
     * @param object
     * @param key
     * @param id
     * @throws InterruptedException
     * @throws IOException
     */
    public void setJsonObject(Object object, String key,Integer id) throws InterruptedException,IOException{
        String redisTarget = cacheMapper.getRedisTargetName(id);
        setJsonObject(object, key, redisTarget);
    }
    /**
     * 
     * @param object
     * @param id
     * @throws Exception
     */
    public void setJsonObject(Object object, Integer id) throws InterruptedException,IOException{
        String redisTarget = cacheMapper.getRedisTargetName(id);
        setJsonObject(object, id,redisTarget);
    }
    
    /**
     * 
     * @param object
     * @param id
     * @throws Exception
     */
    public void setJsonObjectWithKeyPrefix(Object object, Integer id,String keyPrefix) throws InterruptedException,IOException{
        String redisTarget = cacheMapper.getRedisTargetName(id);
        setJsonObject(object, keyPrefix+id,redisTarget);
    }
    
    public void setString(String string,Integer id) throws InterruptedException{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(id);
        try{
            byte[] data = string.getBytes();            
            client = getRedisPoolContainer().borrowClient(redisTarget);
            byte[] idkey = id.toString().getBytes();
            client.getJedis().set(idkey, data);
        }finally {
             getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    public void setStringWithPrefix(String string,Integer id,String keyPrefix) throws InterruptedException{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(id);
        try{
            byte[] data = string.getBytes();            
            client = getRedisPoolContainer().borrowClient(redisTarget);
            byte[] idkey = (keyPrefix+id).getBytes();
            client.getJedis().set(idkey, data);
        }finally {
             getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    
    public Object eval(String luaScript,List<String> keys,List<String> args,Object cacheMapperInput) throws InterruptedException{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        Object object =null;
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            object = client.getJedis().eval(luaScript, keys, args);
        }finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
        return object;
    }
    /**
     * 
     * deprecated because 10000 key, set costs:used_memory_human:11.19M
     * hset costs:used_memory_human:13.18M
     * hset's compress is for multi field, not oneone k-v
     */
    @Deprecated
    public <T>void hsetJsonObject(T object, Integer id,String redisTarget) throws InterruptedException,IOException{
        RedisClient client = null;
        try{
            byte[] data = JsonUtil.writeObject(object);            
            client = getRedisPoolContainer().borrowClient(redisTarget);
            byte[] idkey = id.toString().getBytes();
            String defaultField = "feed";
            client.getJedis().hset(idkey, defaultField.getBytes(), data);
        }finally {
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    
    /**
     * Remember: this function only talks to one single redis db <br/>
     * which means all Integer keys in the input parm 'map' should be mapped to one redis target<br/>
     * @param <T>
     * @param map contains integer keys,null values. values will be updated if we hit stored value in redis
     * @param clazz
     * @param redisTarget
     */
    public <T>void getJsonObjects(HashMap<Integer, DbRecord> map, Class<T> clazz,String redisTarget) throws Exception{
        RedisClient client = null;
        try{
            client = getRedisPoolContainer().borrowClient(redisTarget);
            if(map.size()>0){
                Set<Integer> keySet = map.keySet();
                
                Integer[] ids = keySet.toArray(new Integer[0]);
                /*
                String[] keys = new String[ids.length];
                int j = 0;
                for(Integer i:ids){
                    keys[j++] = Integer.toString(i);
                }*/
                byte[][] keys = ConversionUtils.integerCollectionToByteArray(keySet);
                List<byte[]> values = client.getJedis().mget(keys);
                int j = 0;
                while(j<values.size()){
                    Integer key = ids[j];
                    byte[] value = null;
                    if((value = values.get(j++))!=null){
                        DbRecord obj = null;
                        String strValue = new String(value);
                        if(strValue.startsWith(emptyRecord)){
                            obj = DbRecord.createNotFoundModel(clazz);
                        }else{
                            obj = (DbRecord)JsonUtil.readObject(value, clazz);
                        }
                        if (key.equals(obj.getId())) {// a little check here
                            map.put(key, obj);
                        }
                    }
                }
            }
        }finally {
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    

    /**
     * Remember: this function only talks to one single redis db <br/>
     * which means all Integer keys in the input parm 'map' should be mapped to one redis target<br/>
     * @param <T>
     * @param map contains integer keys,null values. values will be updated if we hit stored value in redis
     * @param clazz
     * @param redisTarget
     */
    public <T>void getJsonObjectsWithKeyPrefix(HashMap<Integer, DbRecord> map, Class<T> clazz,String redisTarget,String keyPrefix) throws Exception{
        RedisClient client = null;
        try{
            client = getRedisPoolContainer().borrowClient(redisTarget);
            if(map.size()>0){
                Set<Integer> keySet = map.keySet();
                
                Integer[] ids = keySet.toArray(new Integer[0]);
                /*
                String[] keys = new String[ids.length];
                int j = 0;
                for(Integer i:ids){
                    keys[j++] = Integer.toString(i);
                }*/
                byte[][] keys = ConversionUtils.integerCollectionToByteArrayWithPrefix(keySet,keyPrefix);
                List<byte[]> values = client.getJedis().mget(keys);
                int j = 0;
                while(j<values.size()){
                    Integer key = ids[j];
                    byte[] value = null;
                    if((value = values.get(j++))!=null){
                        DbRecord obj = null;
                        String strValue = new String(value);
                        if(strValue.startsWith(emptyRecord)){
                            obj = DbRecord.createNotFoundModel(clazz);
                        }else{
                            obj = (DbRecord)JsonUtil.readObject(value, clazz);
                        }
                        if (key.equals(obj.getId())) {// a little check here
                            map.put(key, obj);
                        }
                    }
                }
            }
        }finally {
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    /**
     * 
     * @param key
     * @param redisTarget
     * @param clazz
     * @return
     * @throws Exception
     */
    public <T> T getJsonObject(String key,String redisTarget,Class<T> clazz) throws Exception{
        RedisClient client = null;
        T result = null;
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            byte[] value = client.getJedis().get(key.getBytes());
            if(value!=null){
                result = JsonUtil.readObject(value, clazz);
            }
        } finally {
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
        return result;
    }
    /**
     * 
     * @param key
     * @param mapperInput
     * @param clazz
     * @return
     * @throws Exception
     */
    public <T> T getJsonObject(String key,Object mapperInput,Class<T> clazz) throws Exception{
        String redisTarget = cacheMapper.getRedisTargetName(mapperInput);
        return getJsonObject(key, redisTarget, clazz);
    }
    
    public Set<String> zrange(String key,Object cacheMapperInput) throws Exception{
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        RedisClient client = null;
        Set<String> result = null;
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            result= jedis.zrange(key, 0, Integer.MAX_VALUE);
            if(null == result){
                result = emptyStringSet;
            }
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
        return result;
    }
    
    public boolean flushAll(Object cacheMapperInput) throws Exception{
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        RedisClient client = null;
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            String pong = jedis.flushAll();
            return pong!=null && pong.equalsIgnoreCase("ok");
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    public boolean ping(Object cacheMapperInput) throws Exception{
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        RedisClient client = null;
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            String pong = jedis.ping();
            return pong!=null && pong.equalsIgnoreCase("pong");
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    public Set<String> zrange(String key,Object cacheMapperInput,int start,int end) throws InterruptedException{
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        RedisClient client = null;
        Set<String> result = null;
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            result= jedis.zrange(key, start, end);
            if(null == result){
                result = emptyStringSet;
            }
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
        return result;
    }
    protected static Set<String> emptyStringSet = new TreeSet<String>();
    public Set<String> zrevrange(String key,Object cacheMapperInput,int start,int end) throws InterruptedException{
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        RedisClient client = null;
        Set<String> result = null;
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            result= jedis.zrevrange(key, start, end);
            if(null == result){
                result = emptyStringSet;
            }
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
        return result;
    }
    public void zaddMulti(String key,Map<Double, String> items,Object cacheMapperInput) throws InterruptedException{
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        RedisClient client = null;
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            jedis.zadd(key, items);
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    /**
     * 
     * @param key
     * @param cacheMapperInput
     * @param ttl time to live, in seconds
     * @throws Exception
     */
    public void expire(String key, Object cacheMapperInput,int ttl) throws InterruptedException{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            jedis.expire(key, ttl);
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    public boolean exists(String key, Object cacheMapperInput) throws InterruptedException{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        boolean result = false;
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            result = jedis.exists(key);
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
        return result;
    }
    
    public void delete(String key,Object mapperInput) throws Exception{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(mapperInput);
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            jedis.del(key);
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    public void zrem(String key,String[] members,Object cacheMapperInput) throws InterruptedException{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            jedis.zrem(key, members);
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    public void zadd(String key,Tuple member,Object cacheMapperInput) throws InterruptedException{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            jedis.zadd(key, member.getScore(), member.getElement());
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }

    public long zcount(String key,double minScore,double maxScore,Object cacheMapperInput) throws InterruptedException{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        long result = 0;
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            result = jedis.zcount(key, minScore, minScore);
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
        return result;
    }
    
    public void zadd(String key,double score,String member,Object cacheMapperInput) throws InterruptedException{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            jedis.zadd(key, score, member);
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    public void zadd(String key,Tuple member,Object cacheMapperInput,int setMaxSize) throws InterruptedException{
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        zadd(key, member, redisTarget, setMaxSize);
    }
    
    public void zaddMultiPopSmallest(String key, Map<Double, String> members,Object cacheMapperInput,int setMaxSize) throws InterruptedException{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            jedis.zadd(key, members);
            jedis.zremrangeByRank(key, -1*Integer.MAX_VALUE, -1*setMaxSize-1);
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    /**
     * Add one item to sorted set.If the set size is bigger than the setMaxSize,
     * remove the items with smallest scores from the set.
     * @param key
     * @param member
     * @param redisTarget
     * @param setMaxSize
     * @throws InterruptedException
     */
    public void zaddLuaRemSmallest(String key,Tuple member,Object cacheMapperInput, int setMaxSize) throws InterruptedException{
        RedisClient client = null;
        String redisTarget = cacheMapper.getRedisTargetName(cacheMapperInput);
        try {
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            List<String> keys = new ArrayList<String>();
            List<String> args = new ArrayList<String>();
            String sscore = String.valueOf(member.getScore());
            String smember = String.valueOf(member.getElement());
            keys.add(key);
            args.add(sscore);
            args.add(smember);
            String script = createZaddLuaRemSmallestScript(setMaxSize);
            jedis.eval(script, keys, args);
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    /**
     * TODO Change the script now it removes the tails , we need to remove the head
     */
    static String zaddLuaRemSmallest = "local res\n" +
    "local exist\n" +
    "exist = redis.call('exists', KEYS[1])\n"+
    "if exist==0 then\n"+
    "  redis.call('zadd',KEYS[1],'9.223372036854776E18','reservedEmptyMember')\n"+
    "end\n"+
    "res = redis.call('zadd',KEYS[1],ARGV[1],ARGV[2])\n" +
    "redis.call('zremrangeByRank',KEYS[1],-2147483648,";
    
    public static String createZaddLuaRemSmallestScript(int sortedSetMaxSize){
        StringBuffer sb = new StringBuffer(128);
        int negIndex = (sortedSetMaxSize*-1)-1;
        return sb.append(zaddLuaRemSmallest).append(negIndex).append(")").append("\n").toString();
    }
    
    static String luaSetAndExpire = 
    "redis.call('set',KEYS[1],ARGV[1])\n"+
    "return redis.call('expire',KEYS[1],ARGV[2])\n";
    
    /**
     * 
     * @param key
     * @param object
     * @param mapperInput
     * @param expireSeconds
     * @return
     * @throws Exception
     */
    public boolean setJsonObjectAndExpire(String key,Object value,Object mapperInput,int expireSeconds) throws Exception{
        String redisTargetString = cacheMapper.getRedisTargetName(mapperInput);
        return setJsonObjectAndExpire(key, value, redisTargetString, expireSeconds);
    }
    /**
     * 
     * @param key
     * @param object
     * @param redisTarget
     * @param expireSeconds
     * @return
     * @throws Exception
     */
    public boolean setJsonObjectAndExpire(String key,Object value,String redisTarget,int expireSeconds) throws Exception{
        RedisClient client = null;
        try {
            /* deprecated because redis lua doesn't support binary arguments
            String valueString = JsonUtil.writeObjectAsString(value);
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            
            List<String> keys = new ArrayList<String>();
            List<String> args = new ArrayList<String>();
            keys.add(key);
            args.add(valueString);
            Object result = jedis.eval(luaSetAndExpire, keys, args);
            */
            byte[] json = JsonUtil.writeObject(value);
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            jedis.set(key.getBytes(), json);
            long result = jedis.expire(key, expireSeconds);
            if(result == 1){
                return true;
            }else {
                return false;
            }            
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    public Long lpushJsonObject(String listName,Object value,String redisTarget) throws Exception{
        RedisClient client = null;
        try{
            logger.debug("lpush ====>");
            byte[] json = JsonUtil.writeObject(value);
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            Long result = jedis.lpush(listName.getBytes(), json);
            logger.debug("lpush <====");
            return result;
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    protected static String empty  = "";
    public String rpop(String listName, String redisTarget) throws Exception{
        RedisClient client = null;
        String result = null;
        try{
            //logger.debug("rpop borrowClient ====>"+System.currentTimeMillis());
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            result = jedis.rpop(listName);
            //logger.debug("rpop borrowClient <===="+System.currentTimeMillis()+result);
            if(result== null){
                return empty;
            }else {
                return result;
            }
        }catch(Exception e){
            logger.error("rpop",e);
            throw e;
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    public <T>T rpopJsonObject(String listName, String redisTarget,Class<T> clazz) throws Exception{
        RedisClient client = null;
        T result = null;
        try{
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            byte[] content = jedis.rpop(listName.getBytes());
            if(content!= null)
                result =  SmileJsonUtil.readObject(content, clazz);
            return result;
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    public <T>T lindexLastJsonObject(String listName, String redisTarget,Class<T> clazz) throws Exception{
        RedisClient client = null;
        T result = null;
        try{
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            byte[] content = jedis.lindex(listName.getBytes(),-1);
            if(content!= null)
                result =  SmileJsonUtil.readObject(content, clazz);
            return result;
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    public <T>T lindexFirstJsonObject(String listName, String redisTarget,Class<T> clazz) throws Exception{
        RedisClient client = null;
        T result = null;
        try{
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            byte[] content = jedis.lindex(listName.getBytes(),0);
            if(content!= null)
                result =  SmileJsonUtil.readObject(content, clazz);
            return result;
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    public Long del(String redisTarget,String... keys) throws Exception{
        RedisClient client = null;
        try{
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            return jedis.del(keys);
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    public Long listLength(String listName, String redisTarget) throws Exception{
        RedisClient client = null;
        try{
            client = getRedisPoolContainer().borrowClient(redisTarget);
            Jedis jedis = client.getJedis();
            return jedis.llen(listName);
        } finally{
            getRedisPoolContainer().returnClient(client, redisTarget);
        }
    }
    
    public boolean assertEvalResult(String result,String expected){
        return result.equalsIgnoreCase(expected);
    }
    
    public boolean assertEvalResult(Integer result,Integer expected){
        return result.equals(expected);
    }
    
    /**
     * create
     * @param <T>
     * @param clazz
     * @return
     * @throws Exception
     */
    @Deprecated
    public <T>DbRecord createNotFoundModel(Class<T> clazz) throws Exception{
        DbRecord obj = (DbRecord)clazz.newInstance();
        obj.setNotFound(true);
        return obj;
    }
    
    public RedisClientPoolContainer getRedisPoolContainer(){
        if(rapidbackend == null){
            rapidbackend = Rapidbackend.getCore();
        }
        return rapidbackend.getRedisClientPoolContainer();
    }
    
    public String getRedisTargetName(Object cacheMapperInput){
        return cacheMapper.getRedisTargetName(cacheMapperInput);
    }
    
}
