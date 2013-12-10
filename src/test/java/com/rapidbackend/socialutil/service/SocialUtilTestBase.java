package com.rapidbackend.socialutil.service;


import static com.rapidbackend.client.http.HttpCommandHelper.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.TestException;
import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.core.RapidbackendTestBase;
import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.IntListParam;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.core.request.ParamFactory;
import com.rapidbackend.core.request.StringParam;
import com.rapidbackend.core.request.util.ParamList;
import com.rapidbackend.security.session.SessionStore;
import com.rapidbackend.socialutil.install.dbinstall.DbConfigParser;
import com.rapidbackend.socialutil.install.dbinstall.DbInstaller;
import com.rapidbackend.socialutil.install.dbinstall.FollowableConfig;
import com.rapidbackend.socialutil.model.data.ModelFactory;
import com.rapidbackend.socialutil.model.extension.FeedContext;
import com.rapidbackend.socialutil.model.metadata.FeedMetaData;
import com.rapidbackend.socialutil.model.reserved.CommentBase;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.FollowableBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.model.util.TypeFinder;import com.rapidbackend.socialutil.util.FeedComparator;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.comm.redis.client.RedisClient;

/**
 * This class prepares some test datas for each followable category.
 * 
 * @author chiqiu
 *
 */
@RunWith(value = Parameterized.class)
public abstract class SocialUtilTestBase extends RapidbackendTestBase{
    protected static Logger logger = LoggerFactory.getLogger(SocialUtilTestBase.class);
    
    protected static long SLEEP_MILISECONDS_FOR_SINGLE_POST = 500*1;
    
    protected static int socializedUserNum = 10;// there will be userNum + 1 users created
    protected static int followableNum = socializedUserNum;
    protected static List<UserBase> users = new ArrayList<UserBase>();
     protected static HashMap<Integer, String> userSessions = new HashMap<Integer, String>();
    protected static HashMap<Integer, FollowableBase> followables = new HashMap<Integer, FollowableBase>();
    /**
     * followableId => posts
     */
    protected static HashMap<Integer, List<FeedContentBase>> cachedPosts = new HashMap<Integer, List<FeedContentBase>>();
    
    protected TypeFinder modelTypeFinder = (TypeFinder)getAppContext().getBean("SocialUtilModelTypeFinder");
    
    protected static SessionStore sessionStore;
    
    public static String getSessionId(UserBase user){
        return userSessions.get(user.getId());
    }
    /**
     * this can be an paragramized value
     */
    protected String followableName = "Group";
    
    
    public SocialUtilTestBase(String followableName){
        this.followableName = followableName;
    }
    
    @Parameters
    public static Collection<Object[]> data() {
        try {
            DbConfigParser dbConfigParser = new DbConfigParser();
            List<FollowableConfig> followableConfigs = (List<FollowableConfig>)dbConfigParser.parseSetting().get(DbConfigParser.FollowableConfigVariable);
            
            Object[][] dataObjects = new Object[followableConfigs.size()][1];
            int i= 0;
            for(FollowableConfig f:followableConfigs){
                Object[] item = dataObjects[i++];
                item[0] = StringUtils.capitalize(f.getName());
            }
            return Arrays.asList(dataObjects);
        } catch (Exception e) {
            throw new TestException("error duing parsing configuration for socialutility",e);
        }
        
    }
    
    public String getFollowableName() {
        return followableName;
    }

    public void setFollowableName(String followableName) {
        this.followableName = followableName;
    }
    public static void initDataContainers(){
        users = new ArrayList<UserBase>();
        userSessions = new HashMap<Integer, String>();
        cachedPosts = new HashMap<Integer, List<FeedContentBase>>();
        followables = new HashMap<Integer, FollowableBase>();
    }
    

    public String getFeedClassName(){
        return followableName + "Feed";
    }
    
    protected Class<?> feedContentClass(String followableName){
        String className =  followableName+"feedContent";
        return modelTypeFinder.getModelClass(className);
    }
    
    protected Class<?> feedCommentClass(String followableName){
        String className = followableName+"feedcomment";
        return modelTypeFinder.getModelClass(className);
    }
    
    
    public static String createFollowableCommand(String followableName){
        return "Create"+followableName;
    }
    
    public static String deleteFollowableCommand(String followableName){
        return "Delete"+followableName;
    }
    
    public static String UnsubscribeCommand(String followableName) {
        return "Unsubscribe"+followableName;
    }
    public static String subscribeCommand(String followableName) {
        return "Subscribe"+followableName;
    }
    public static String subscriptionModelClass(String followableName){
        return followableName+"subscription";
    }
    public static String postFeedCommand(String followableName){
        return "Post"+followableName+"Feed";//PostXxxxFeed
    }

    public static String commentFeedCommand(String followableName) {
        return "Comment"+followableName+"Feed";
    }
    
    public static String readFeedComment(String followableName){
        return "Read"+followableName+"FeedComment";
    }
    
    public static String updateFeedCommand(String followableName){
        return "Update"+followableName + "Feed";
    }
    public static String deleteFeedCommand(String followableName){
        return "Delete"+followableName+"Feed";//PostXxxxFeed
    }
    public static String readFollowableFeedCommand(String followableName){
        return "Read"+followableName+"Feed";//RepostXxxxFeed
    }
    public static String readFeedByFollowableCommand(String followableName){
        return "ReadFeedBy"+followableName;
    }
    public static String repostFeedCommand(String followableName){
        return "Repost"+followableName+"Feed";//RepostXxxxFeed
    }
    public static String getFollowerCommand(String followableName){
        return "Get"+followableName+"Follower";// GETXxxxFollower
    }    
    public static String getTimelineCommand(String followableName){
        return followableName + "FeedTimeline";
    }
    public static String readFeedMetadataCommand(String followableName){
        return "Read" + followableName +"FeedMetadata";
    }
    public static String readRepostsCommand(String followableName){
        return "Read"+followableName+"FeedReposts";
    }
    
    
    /**
     * create relationship among User0 ~ User(userNum), leave the last user alone
     * @throws Exception
     */
    public void createRelationships() throws Exception{
        
        for(int i=0;i<users.size()-1;i++){
            UserBase user = users.get(i);
            for(int j=0;j<users.size()-1;j++){
                if(j==i){
                    continue;
                }
                UserBase follower = users.get(j);
                createSubsctiprion(getFolloweableId(user),follower,false);
            }
        }
    }
    /**
     * 
     * @param followed
     * @param follower
     * @param followerSessionToken for now the token is sessionId
     * @throws Exception
     */
    public void createSubsctiprion(Integer followableId, UserBase follower,boolean wait) throws Exception{
        String followerSessionToken = userSessions.get(follower.getId());
        assertFalse(StringUtils.isEmpty(followerSessionToken));
        
        ParamList parms =  new ParamList();
        IntParam followerIdParam =  new IntParam(ParamNameUtil.FOLLOWER, follower.getId()); 
        IntParam followableIdParam =  new IntParam(ParamNameUtil.FOLLOWABLE, followableId);
        StringParam sessionId = new StringParam(ParamNameUtil.SESSION_ID,followerSessionToken);
        parms.appendParam(sessionId).appendParam(followableIdParam).appendParam(followerIdParam);
        
        HttpPost subscribe = createHttpPostWithoutFile(Protocol, LocalHost, subscribeCommand(followableName), parms);
        String subscribeResult = httpClient.getCommandResult(subscribe);
        logger.debug(subscribeResult);
        
        if(wait){
            sleep4Subscription();
        }
    }
    /**
     * unsubscribe a followable
     * @param followableId
     * @param follower
     * @throws Exception
     */
    public void unsubscribe(Integer followableId,UserBase follower) throws Exception{
        String followerSessionToken = userSessions.get(follower.getId());
        assertFalse(StringUtils.isEmpty(followerSessionToken));
        
        ParamList parms =  new ParamList();
        IntParam followerIdParam =  new IntParam(ParamNameUtil.FOLLOWER, follower.getId()); 
        IntParam followableIdParam =  new IntParam(ParamNameUtil.FOLLOWABLE, followableId);
        StringParam sessionId = new StringParam(ParamNameUtil.SESSION_ID,followerSessionToken);
        parms.appendParam(sessionId).appendParam(followableIdParam).appendParam(followerIdParam);
        
        HttpPost unsubscribe = createHttpPostWithoutFile(Protocol, LocalHost, UnsubscribeCommand(followableName), parms);
        String unsubscribeResult = httpClient.getCommandResult(unsubscribe);
        logger.debug(unsubscribeResult);
    }
    
    
    
    public static void loginAllUsers() throws Exception{
        for(UserBase user : users){
            String sessionId = loginOfflineUser(user);
            userSessions.put(user.getId(), sessionId);
        }
    }
    
    public String getUserSessionId(Integer userId){
        return userSessions.get(userId);
    }
    
    public static String loginOfflineUser(UserBase user) throws Exception{
        HttpPost login  = createHttpPostWithoutFile(Protocol, LocalHost, "Login", (DbRecord)user);
        Class<?> userClass = ModelReflectionUtil.getUserClass();
        String loginResult = httpClient.getCommandResult(login);
        logger.debug(loginResult);
        CommandResult<?> commandResult = parseResult(loginResult, userClass);
        assertNotNull(commandResult.getSessionId());
        String sessionId = commandResult.getSessionId();
        return sessionId;
    }
    /**
     * note all the posts should be stored in a static global container, then it can be used by the user
     */
    
    /**
     * create 10 users each of them follows the rest ones, then create 1 user who follows nobody.<br/>
     * Users login right after been created
     */
    public void createUsersAndFollowables() throws Exception{
        
        for(int i=0;i<socializedUserNum+1;i++){
            users.add(createUser());
        }
        
        loginAllUsers();
        
        if(!followableName.equalsIgnoreCase("user")){
            for(UserBase user: users){
                FollowableBase followable = (FollowableBase)createFollowable(user).getResult();
                followables.put(user.getId(), followable);
            }
        }
        
        
    }
    @BeforeClass
    public static void init() throws Exception{
        try {
            prepareTest();
            sessionStore = (SessionStore)getApplicationContext().getBean("SessionStore");
            cleanDb();
            cleanRedisInstances();
            initDataContainers();
        } catch (Exception e) {
            logger.error("hit exceptions during init()",e);
            throw e;
        }
        
    }
    @AfterClass
    public static void destroy() throws Exception{
        try {
            cleanRedisInstances();
            cleanDb();
            t.destroy();
        } catch (Exception e) {
            logger.error("hit exceptions during destroy()",e);
            throw e;
        }
        
    }
    /**
     * flush all redis instances registered to the server
     * @throws Exception
     */
    public static void cleanRedisInstances() throws Exception{
        Collection<BlockingQueue<RedisClient>> poolCollection = Rapidbackend.getCore().getRedisClientPoolContainer().getClientPools().values();
        for(BlockingQueue<RedisClient> queue:poolCollection){
            RedisClient client = queue.peek();
            if(client!=null)
                client.getJedis().flushAll();
        }
    }
    
    public static void cleanDb() throws Exception{
        DbInstaller dbInstaller = new DbInstaller();
        dbInstaller.installDB();
    }
    
    protected static UserBase createUser() throws Exception{
        ModelFactory modelFactory = new ModelFactory();
        Class<?> userClass = ModelReflectionUtil.getUserClass();
        UserBase generatedUser = (UserBase)modelFactory.createModel(userClass);
                
        HttpPost create = createHttpPostWithoutFile(Protocol, LocalHost, "CreateUser", generatedUser);
        String createResult = httpClient.getCommandResult(create);        
        logger.debug("create Result for "+ModelReflectionUtil.getUserClass().getSimpleName());
        logger.debug(createResult);
        CommandResult<?> CreateCommandResult = parseResult(createResult, userClass);
        
        boolean error = CreateCommandResult.isError();
        assertFalse(error);
        UserBase user = (UserBase)CreateCommandResult.getResult();
        generatedUser.setId(user.getId());
        return generatedUser;
    }
    /**
     * store them in a map
     * @param user
     * @param feedContentBase
     */
    protected void cacheUserPostedFeed(Integer followableId,FeedContentBase feedContentBase){
        
        List<FeedContentBase> feeds = cachedPosts.get(followableId);
        if(feeds == null){
            feeds = new ArrayList<FeedContentBase>();
            cachedPosts.put(followableId, feeds);
        }
        feeds.add(feedContentBase);
    }
    
    /**
     * 
     * @param user
     * @return
     * @throws Exception
     */
    public List<FeedContext> getTimeline(UserBase user) throws Exception{
        return getTimeline(user,null,null);
    }
    
    public List<FeedContext> getTimeline(UserBase user, Integer start, Integer pageSize) throws Exception{
        String timeLineCommand = getTimelineCommand(followableName);
        ParamList timelineParamList = new ParamList();
        IntParam followerId = new IntParam(ParamNameUtil.USER_ID, user.getId());
        
        if (start!=null) {
            IntParam startParam = new IntParam(ParamNameUtil.START,start);
            timelineParamList.add(startParam);
        }
        
        if(pageSize!=null){
            IntParam pageSizeParam = new IntParam(ParamNameUtil.PAGE_SIZE,pageSize);
            timelineParamList.add(pageSizeParam);
        }
        
        timelineParamList.add(followerId);
        
        HttpGet getTimeline = createHttpGet(Protocol, LocalHost, timeLineCommand, timelineParamList);
        String getTimelineResult = httpClient.getCommandResult(getTimeline);
        logger.debug(getTimelineResult);
        CommandResult<?> timelineResult = parseTimelineResult(getTimelineResult, feedContentClass(followableName));
        return (List<FeedContext>)timelineResult.getResult();
    }
    /**
     * only returns the first 20 followers
     * @param followable
     * @return
     * @throws Exception
     */
    public List<UserBase> getFollowers(Integer followable) throws Exception{
        String getFollowerCommand = getFollowerCommand(followableName);
        ParamList getfollowerParams = new ParamList();
        IntParam followableId = new IntParam(ParamNameUtil.FOLLOWABLE,followable);
        getfollowerParams.add(followableId);
        HttpGet getFollower = createHttpGet(Protocol, LocalHost, getFollowerCommand, getfollowerParams);
        String getFollowerResult = httpClient.getCommandResult(getFollower);//TODO encapsulate into a function
        logger.debug(getFollowerResult);
        CommandResult<?> getFollowerResults = parseListResult(getFollowerResult, modelTypeFinder.getModelClass("User"));
        List<UserBase> followers = (List<UserBase>)getFollowerResults.getResult();
        return followers;
    }
    
    public CommandResult<List<UserBase>> getFollowers(Integer followable,Integer start, Integer pageSize) throws Exception{
        String getFollowerCommand = getFollowerCommand(followableName);
        ParamList getfollowerParams = new ParamList();
        IntParam followableId = new IntParam(ParamNameUtil.FOLLOWABLE,followable);
        IntParam startParam = new IntParam(ParamNameUtil.START,start);
        IntParam pageSizeParam = new IntParam(ParamNameUtil.PAGE_SIZE,pageSize);
        
        getfollowerParams.appendParam(followableId).appendParam(startParam).appendParam(pageSizeParam);
        HttpGet getFollower = createHttpGet(Protocol, LocalHost, getFollowerCommand, getfollowerParams);
        String getFollowerResult = httpClient.getCommandResult(getFollower);//TODO encapsulate into a function
        logger.debug(getFollowerResult);
        CommandResult<?> getFollowerResults = parseListResult(getFollowerResult, modelTypeFinder.getModelClass("User"));
        
        return (CommandResult<List<UserBase>>)getFollowerResults;
    }
    
    /**
     * post a generated feed by one user
     * @param author
     * @param followableId
     * @param wait set to true is you want the system to wait for a short period after posting
     * @return
     * @throws Exception
     */
    public FeedContentBase postFeed(UserBase author, Integer followableId,boolean wait) throws Exception{
        
        IntParam userIdParam = new IntParam(ParamNameUtil.USER_ID,author.getId());
        IntParam followableIdParam = new IntParam(ParamNameUtil.Followable_ID,followableId);
        ModelFactory modelFactory = new ModelFactory();
        FeedContentBase feedContentBase = (FeedContentBase)modelFactory.createModel(feedContentClass(followableName));
        modelFactory.eraseSpecifiedFileds(feedContentBase, new String[]{"replytoId","repostToId","repostToUserId","seedFeedId"});
        
        List<CommandParam> feedContentParams = ParamFactory.convertModelToParams(feedContentBase);
        
        String postCommandPath = postFeedCommand(followableName);
        List<CommandParam> params = new ArrayList<CommandParam>();
        params.add(userIdParam);
        params.add(followableIdParam);
        
        StringParam sessionId = new StringParam(ParamNameUtil.SESSION_ID,getUserSessionId(userIdParam.getData()));
        
        params.add(sessionId);
        
        for(CommandParam param:feedContentParams){
            params.add(param);
        }
        
        HttpGet get = createHttpGet(Protocol, LocalHost, postCommandPath, params);
        String postResult = httpClient.getCommandResult(get);
        CommandResult<?> commandResult = parseResult(postResult, feedContentBase.getClass());
        FeedContentBase postedFeed = (FeedContentBase)commandResult.getResult();
        
        assertNotNull(commandResult.getSessionId());
        assertFalse(commandResult.isError());
        assertNotNull(postedFeed);
        assertNotNull(postedFeed.getId());
        cacheUserPostedFeed(followableId, postedFeed);
        if(wait){
            Thread.currentThread().sleep(SLEEP_MILISECONDS_FOR_SINGLE_POST);//
        }
        return postedFeed;
    }
    
    public FeedContentBase deleteFeed(UserBase author,Integer followableId,Integer feedId) throws Exception{
        IntParam userIdParam = new IntParam(ParamNameUtil.USER_ID,author.getId());
        IntParam followableIdParam = new IntParam(ParamNameUtil.Followable_ID,followableId);
       
        
        String deleteCommandPath = deleteFeedCommand(followableName);
        List<CommandParam> params = new ArrayList<CommandParam>();
        params.add(userIdParam);
        params.add(followableIdParam);
        
        StringParam sessionId = new StringParam(ParamNameUtil.SESSION_ID,getUserSessionId(userIdParam.getData()));
        
        params.add(sessionId);
        
        HttpDelete delete = createHttpDelete(Protocol, LocalHost, deleteCommandPath, params);
        
        
        String deleteResult = httpClient.getCommandResult(delete);
        
        
        
        CommandResult<?> commandResult = parseResult(deleteResult, FeedContentBase.class);
        FeedContentBase deletedFeed = (FeedContentBase)commandResult.getResult();
        
        assertNotNull(commandResult.getSessionId());
        assertFalse(commandResult.isError());
        assertNotNull(deletedFeed);
        assertNotNull(deletedFeed.getId());
        cacheUserPostedFeed(followableId, deletedFeed);
        return deletedFeed;
    } 
    
    /**
     * 
     * @param author
     * @param followableId
     * @param repostToFeedId
     * @param content
     * @param wait set to true if you want the system to wait for a short period after posting
     * @return
     * @throws Exception
     */
    public FeedContentBase repostFeed(UserBase author, Integer followableId, Integer repostToFeedId,String content ,boolean wait) throws Exception{
        IntParam userIdParam = new IntParam(ParamNameUtil.USER_ID,author.getId());
        IntParam followableIdParam = new IntParam(ParamNameUtil.Followable_ID,followableId);
        IntParam repostToFeedIdParam = new IntParam(ParamNameUtil.REPOST_TO_FEED_ID,repostToFeedId);
        StringParam contentParam = new StringParam(ParamNameUtil.CONTENT,content);
        
        ParamList params = new ParamList();
        params.appendParam(userIdParam).appendParam(contentParam).appendParam(repostToFeedIdParam).appendParam(followableIdParam);
                
        String repostCommandPath = repostFeedCommand(followableName);
        
        StringParam sessionId = new StringParam(ParamNameUtil.SESSION_ID,getUserSessionId(userIdParam.getData()));
        
        params.add(sessionId);
                
        HttpGet get = createHttpGet(Protocol, LocalHost, repostCommandPath, params);
        String repostResult = httpClient.getCommandResult(get);
        
        logger.debug(repostResult);
        CommandResult<?> commandResult = parseResult(repostResult, feedContentClass(followableName));
        FeedContentBase postedFeed = (FeedContentBase)commandResult.getResult();
        
        assertNotNull(commandResult.getSessionId());
        assertFalse(commandResult.isError());
        
        cacheUserPostedFeed(followableId, postedFeed);
        if(wait){
            Thread.currentThread().sleep(SLEEP_MILISECONDS_FOR_SINGLE_POST);// 
        }
        return postedFeed;
    }
    
    public List<FeedContentBase> sortAllPosts(){
        TreeSet set = new TreeSet(new FeedComparator());
        for(List<FeedContentBase> list : cachedPosts.values()){
            for(FeedContentBase feed:list){
                set.add(feed);
            }
        }
        List<FeedContentBase> list = new ArrayList<FeedContentBase>();
        
        for(Object o:set){
            list.add((FeedContentBase)o);
        }
        return list;
    }
    
    public List<FeedContentBase> sortPosts(List<UserBase> users){
        TreeSet set = new TreeSet(new FeedComparator());
        for(UserBase user : users){
            List<FeedContentBase> list = cachedPosts.get(getFolloweableId(user));
            for(FeedContentBase feed:list){
                set.add(feed);
            }
        }
        
        List<FeedContentBase> list = new ArrayList<FeedContentBase>();
        for(Object o:set){
            list.add((FeedContentBase)o);
        }
        return list;
    }
    
    protected Integer getFolloweableId(UserBase user){
        if(!followableName.equalsIgnoreCase("user")){
            return followables.get(user.getId()).getId();
        }else {
            return user.getId();
        }
        
    }
    
    protected List<UserBase> socializedUsers(){
        ArrayList<UserBase> socializedUsers = new ArrayList<UserBase>();
        for(int i=0;i<socializedUserNum;i++){
            socializedUsers.add(users.get(i));
        }
        return socializedUsers;
    }
    
    /**
     * 
     * @param feed
     * @param posts
     * @return true if find feed in posts
     */
    protected boolean findFeed(FeedContentBase feed,List<FeedContentBase> posts){
        assertNotNull(feed);
        boolean result = false;
        for(FeedContentBase fb:posts){
            if(fb.getId().intValue() == feed.getId().intValue()){
                result = true;
                break;
            }
        }
        return result;
    }
    /**
     * 
     * @return the user who doesn't follow nothing
     */
    protected UserBase lonelyOne(){
        return users.get(socializedUserNum);
    }
    
    public static void sleep4ever(){
        try {
            Thread.currentThread().sleep(Long.MAX_VALUE);
        } catch (Exception ignore) {
        }
    }
    public static void sleep4Subscription(){
        try {
            Thread.currentThread().sleep(1000l);
        } catch (Exception ignore) {
        }
    }
    /**
     * sleep 5 seconds, wait until all posts has been pushed into followers' inboxes
     */
    public static void sleep4Posts(){
        try {
            Thread.currentThread().sleep(10*1000l);
        } catch (Exception ignore) {
        }
    }
    /**
     * post multiple posts by user(0)
     * @param numOfPosts
     * @throws Exception
     */
    public void postMultipleFeedByOneFollowable(UserBase user,int numOfPosts) throws Exception{
        postMultipleFeedByOneFollowable(user, numOfPosts, true);
    }
    
    public void postMultipleFeedByOneFollowable(UserBase user,int numOfPosts,boolean sleep) throws Exception{
        for(int i= 0; i<numOfPosts;i++){
            postFeed(user, getFolloweableId(user),false);
        }
        if(sleep)
            sleep4Posts();
    }
    
    public void postMultipleFeedByMultipleFollowable(List<UserBase> socializedones, int numOfPostsEach) throws Exception{
        int postNum = numOfPostsEach;
        for(UserBase author : socializedones){
            for(int i = 0;i<postNum;i++)
                postFeed(author, getFolloweableId(author),false);
        }
        sleep4Posts();
    }
    
    public CommandResult<?> readFeedsByFollowables(Integer followableId, int start, int pagesize) throws Exception{
        String readFeedByFollowableCommand = readFeedByFollowableCommand(followableName);
        ParamList readFeedParam = new ParamList();
        IntParam id = new IntParam(ParamNameUtil.ID,followableId);
        IntParam startParam = new IntParam(ParamNameUtil.START,start);
        IntParam pageSizeParam = new IntParam(ParamNameUtil.PAGE_SIZE, pagesize);
        readFeedParam.appendParam(id).appendParam(startParam).appendParam(pageSizeParam);
        
        HttpGet readFeeds = createHttpGet(Protocol, LocalHost, readFeedByFollowableCommand, readFeedParam);
        String readFeedsResult = httpClient.getCommandResult(readFeeds);
        logger.debug(readFeedsResult);
        CommandResult<?> readResult = parseTimelineResult(readFeedsResult, feedContentClass(followableName));
        assertFalse(readResult.isError());
        List<FeedContext> feeds =  (List<FeedContext>)readResult.getResult();
        return readResult;
    }
    
    /**
     * 
     * @param ids
     * @return
     * @throws Exception
     */
    public List<FeedContext> readFeeds(int[] ids) throws Exception{
        String readFeedCommand = readFollowableFeedCommand(followableName);
        ParamList readFeedParam = new ParamList();
        IntListParam fids = new IntListParam(ParamNameUtil.FEED_IDS, ids);
        readFeedParam.add(fids);
        HttpGet readFeeds = createHttpGet(Protocol, LocalHost, readFeedCommand, readFeedParam);
        String readFeedsResult = httpClient.getCommandResult(readFeeds);
        logger.debug("readFeedsResult:"+readFeedsResult);
        CommandResult<?> readResult = parseTimelineResult(readFeedsResult, feedContentClass(followableName));
        assertFalse(readResult.isError());
        List<FeedContext> feeds =  (List<FeedContext>)readResult.getResult();
        return feeds;
    }
    /**
     * @param requester should be author of this feed
     */
    public FeedContentBase deleteFeed(UserBase requester, FeedContentBase toDelete) throws Exception{
        logger.debug("delete feed "+ toDelete.getId()+" by user:"+requester.getId());
        IntParam feedIdParam = new IntParam(ParamNameUtil.ID, toDelete.getId());
        
        IntParam userIdParam = new IntParam(ParamNameUtil.USER_ID,requester.getId());
        IntParam followableIdParam = new IntParam(ParamNameUtil.Followable_ID,getFolloweableId(requester));
       
        
        String deleteCommandPath = deleteFeedCommand(followableName);
        ParamList params = new ParamList();
        
        StringParam sessionId = new StringParam(ParamNameUtil.SESSION_ID,getUserSessionId(userIdParam.getData()));
        
        params.appendParam(sessionId).appendParam(feedIdParam).appendParam(followableIdParam);
        
        HttpDelete delete = createHttpDelete(Protocol, LocalHost, deleteCommandPath, params);
        
        String deleteResult = httpClient.getCommandResult(delete);
        logger.debug("deleteResult:"+ deleteResult);
        
        CommandResult<?> commandResult = parseResult(deleteResult, FeedContentBase.class);
        FeedContentBase deletedFeed = (FeedContentBase)commandResult.getResult();
        assertFalse(commandResult.isError());
        
        return deletedFeed;
    }
    
    public CommandResult<List<FeedMetaData>> readMetaData(int[] fids) throws Exception{
        IntListParam feedIds = new IntListParam(ParamNameUtil.FEED_IDS, fids);
        ParamList readFeedMetadataParam = new ParamList();
        readFeedMetadataParam.add(feedIds);
        
        String readFeedMetadataCommand = readFeedMetadataCommand(followableName);
        HttpGet readFeedMetadatas = createHttpGet(Protocol, LocalHost, readFeedMetadataCommand, readFeedMetadataParam);
        
        String readFeedMetadataResult = httpClient.getCommandResult(readFeedMetadatas);
        logger.debug("readMetaData:"+readFeedMetadataResult);
        
        CommandResult<?> commandResult = parseListResult(readFeedMetadataResult, FeedMetaData.class);
        assertFalse(commandResult.isError());
        
        return (CommandResult<List<FeedMetaData>>)commandResult;
    }
    
    public CommandResult<List<FeedContentBase>> readFeedReposts(Integer feedId,Integer start,Integer pageSize) throws Exception{
        IntParam feedIdParam = new IntParam(ParamNameUtil.REPOST_TO_FEED_ID, feedId);
        IntParam startParam = new IntParam(ParamNameUtil.START,start);
        IntParam pageSizeParam = new IntParam(ParamNameUtil.PAGE_SIZE,pageSize);
        
        ParamList readRepostsParam = new ParamList();
        readRepostsParam.appendParam(feedIdParam).appendParam(startParam).appendParam(pageSizeParam);
        String readRepostsCommand = readRepostsCommand(followableName);
        
        HttpGet readReposts = createHttpGet(Protocol, LocalHost, readRepostsCommand, readRepostsParam);
        
        String readRepostsResult = httpClient.getCommandResult(readReposts);
        logger.debug(readRepostsResult);
        
        CommandResult<?> commandResult = parseListResult(readRepostsResult, FeedContentBase.class);
        assertFalse(commandResult.isError());
        
        return (CommandResult<List<FeedContentBase>>)commandResult;
    }
    
    public CommandResult<FeedContentBase> updateFeed(FeedContentBase feed,UserBase user,String updatedContent) throws Exception{
        IntParam fid = new IntParam(ParamNameUtil.ID, feed.getId());
        String sessionid = userSessions.get(user.getId());
        StringParam sid = new StringParam(ParamNameUtil.SESSION_ID,sessionid);
        
        ParamList updateFeedParamList = new ParamList();
        
        feed.setContent(updatedContent);
        List<CommandParam> updates = ParamFactory.convertModelToParams(feed);
        
        updateFeedParamList.addAll(updates);
        updateFeedParamList.appendParam(sid);
        
        HttpPut update = createHttpPutWithoutFile(Protocol , LocalHost , updateFeedCommand(followableName), updateFeedParamList);
        
        String updateResult = httpClient.getCommandResult(update);
        logger.debug(updateResult);
        CommandResult<?> commandResult = parseResult(updateResult, FeedContentBase.class);
        assertFalse(commandResult.isError());
        return (CommandResult<FeedContentBase>)commandResult;
    }
    
    public CommandResult<CommentBase> commentFeed(FeedContentBase feed,UserBase user,String commentContent) throws Exception{
        IntParam feedId = new IntParam(ParamNameUtil.FEED_ID, feed.getId());
        IntParam userId = new IntParam(ParamNameUtil.USER_ID,user.getId());
        StringParam paramContent = new StringParam(ParamNameUtil.CONTENT,commentContent);
        StringParam sessionID = new StringParam(ParamNameUtil.SESSION_ID , getSessionId(user));
        
        ParamList commentParamList = new ParamList();
        ModelFactory modelFactory = new ModelFactory();
        CommentBase commentBase = (CommentBase)modelFactory.createModel(feedCommentClass(followableName));
        modelFactory.eraseSpecifiedFileds(commentBase, new String[]{"screenName","feedId","content","userId"});
        
        List<CommandParam> feedCommentParams = ParamFactory.convertModelToParams(commentBase);
        
        commentParamList.appendParam(feedId).appendParam(userId).appendParam(paramContent).appendParam(sessionID);
        commentParamList.addAll(feedCommentParams);
        
        HttpPost comment = createHttpPostWithoutFile(Protocol, LocalHost, commentFeedCommand(followableName), commentParamList);
        
        String commentResult = httpClient.getCommandResult(comment);
        logger.debug(commentResult);
        CommandResult<?> commandResult = parseResult(commentResult, CommentBase.class);
        assertFalse(commandResult.isError());
        return (CommandResult<CommentBase>)commandResult;
    }
    public CommandResult<List<CommentBase>> readFeedComment(FeedContentBase feed,Integer start,Integer pageSize) throws Exception{
        IntParam feedId = new IntParam(ParamNameUtil.FEED_ID, feed.getId());
        IntParam pageSizeParam = new IntParam(ParamNameUtil.PAGE_SIZE,pageSize);
        IntParam startParam = new IntParam(ParamNameUtil.START, start);
        
        ParamList commentParamList = new ParamList();
        commentParamList.appendParam(startParam).appendParam(pageSizeParam).appendParam(feedId);
        
        HttpGet comment = createHttpGet(Protocol, LocalHost, readFeedComment(followableName), commentParamList);
        
        String commentResult = httpClient.getCommandResult(comment);
        logger.debug(commentResult);
        CommandResult<?> commandResult = parseResult(commentResult, CommentBase.class);
        assertFalse(commandResult.isError());
        return (CommandResult<List<CommentBase>>)commandResult;
    }
    
    public CommandResult createFollowable(UserBase user) throws Exception{
        IntParam userId = new IntParam(ParamNameUtil.USER_ID,user.getId());
        String sessionId = getUserSessionId(user.getId());
        StringParam sessionIdParam = new StringParam(ParamNameUtil.SESSION_ID,sessionId);
        ParamList paramList = new ParamList();
        
        paramList.appendParam(userId).appendParam(sessionIdParam);
        ModelFactory modelFactory = new ModelFactory();
        Class<?> followableClass  = modelTypeFinder.getModelClass(followableName);
        FollowableBase followable = (FollowableBase)modelFactory.createModel(followableClass);
        followable.setCreatedby(user.getId());
        List<CommandParam> fields = ParamFactory.convertModelToParams(followable);
        for(CommandParam f:fields){
            paramList.appendParam(f);
        }
        HttpPost createFollowable = createHttpPostWithoutFile(Protocol, LocalHost, createFollowableCommand(followableName), paramList);
        String createResult = httpClient.getCommandResult(createFollowable);
        logger.debug("create followable result:"+createResult);
        
        CommandResult result = parseResult(createResult, followableClass);
        assertFalse(result.isError());
        return result;
    }
}
