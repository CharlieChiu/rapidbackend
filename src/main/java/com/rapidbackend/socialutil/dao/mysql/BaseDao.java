package com.rapidbackend.socialutil.dao.mysql;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;











import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.socialutil.dao.DataAccessException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.QueryParam.QueryPartial;
import com.rapidbackend.socialutil.dao.util.ModelBeanUtil;
import com.rapidbackend.socialutil.model.util.TypeFinder;
import com.rapidbackend.util.general.ConversionUtils;
import com.rapidbackend.util.general.Tuple;

import static com.rapidbackend.socialutil.dao.util.MysqlUtil.escape;
/**
 * The base dao for all common db usage.
 * Just override the method in the subclasses if you want to implement any special functions
 * Note: currently, this DAO implementation supports mysql dialect only. 
 * @author chiqiu
 *
 */
public abstract class BaseDao implements com.rapidbackend.socialutil.dao.BaseDao{
    final Logger logger = LoggerFactory.getLogger(BaseDao.class);
    
    protected static HashMap<String, Field> modelFieldsCache = new HashMap<String, Field>();
    
    protected String tableName;
        
    protected JdbcTemplate jdbcTemplate;
    
    protected TransactionTemplate transactionTemplate;
   
    protected TypeFinder typeFinder;
    
    protected SimpleJdbcInsert simpleJdbcInsert;
    
    
    public TypeFinder getTypeFinder() {
        return typeFinder;
    }
    
    @Required
    public void setTypeFinder(TypeFinder typeFinder) {
        this.typeFinder = typeFinder;
    }
    
    @Override
    public Class<?> getModelClass(){
        return typeFinder.getModelClass(tableName);
    }
    
    /**
     * @return the transactionTemplate
     */
    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    /**
     * @param transactionTemplate the transactionTemplate to set
     */
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * @return the tableName
     */
    @Override
    public String getTableName() {
        return tableName;
    }
    
    
    /**
     * @param tableName the tableName to set
     */
    @Required
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @return the simpleJdbcInsert
     */
    public SimpleJdbcInsert getSimpleJdbcInsert() {
        return simpleJdbcInsert;
    }

    /**
     * @param simpleJdbcInsert the simpleJdbcInsert to set
     */
    public void setSimpleJdbcInsert(SimpleJdbcInsert simpleJdbcInsert) {
        this.simpleJdbcInsert = simpleJdbcInsert;
    }
    

    String sqlQueryId = null;
    
    protected String getQueryBySingleIdSql(){
        if(sqlQueryId==null){
            sqlQueryId = "select * from "+ escape(tableName)+" where id=?;";
            return sqlQueryId;
        }else{
            return sqlQueryId;
        }
    }
    
    protected String escapeColumn(String col){
        String name = escape(col);
        Field field = ModelReflectionUtil.getModelField(col, getModelClass());
        Class<?> clazz = field.getType();
        if(clazz == Float.class){
            name = "CAST("+name+" as DECIMAL)";
        }
        return name;
    }
    
    protected String genQueryAllByParamSql(String column){
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(escape(tableName)).append(" where ").append(escapeColumn(column)).append("=?;");
        logger.debug(sb.toString());
        return sb.toString();
    }
    
    protected String genQueryAllByParamSql(Map nvPair,Class<?> modelClass){
        logger.debug("nvPair size is "+nvPair.size());
        if(nvPair.size()<1){
            return null;
        }
        Set<Map.Entry> set = nvPair.entrySet();
        String[] sqlParameterString = new String[nvPair.size()];
        int i = 0;
        for(Map.Entry e: set){
            String name = e.getKey().toString();
            String value = e.getValue().toString();
            String parameterString = escapeColumn(name)+"="+paramValueStringInSql(name, value, modelClass);
            sqlParameterString[i++] = parameterString;
        }
        String joinedString = StringUtils.join(sqlParameterString," and ");
        String returnSql = "select * from "+escape(tableName) +" where "+joinedString+";";
        logger.debug(returnSql);
        return returnSql;
    }
    
    protected static Integer defaultMaxRecordRange = 1000;
    protected static Integer defaultStart = 0;
    protected static Integer defaultRange = 20;
    protected Integer maxRecordRange = defaultMaxRecordRange;
    
    public Integer getMaxRecordRange() {
        return maxRecordRange;
    }

    public void setMaxRecordRange(Integer maxRecordRange) {
        this.maxRecordRange = maxRecordRange;
    }

    protected Integer getRecordStart(Integer start,Integer defaultStart){
        if(start!=null && start>=0){
            return start;
        }else {
            return defaultStart;
        }
    }
    /**
     * ensure the max range is in a proper value between 0 and the maxRecordRange.
     * @param range
     * @param defaultRange
     * @return
     */
    protected Integer getRecordRange(Integer range,Integer defaultRange){
        if(range!=null && range>=0 && range<=getMaxRecordRange()){
            return range;
        }else if (range!=null && range>getMaxRecordRange()) {
            return getMaxRecordRange();
        }else {
            return defaultRange;
        }
    }
    @Override
    public String createQuerySqlWithQueryPartials(List<QueryPartial> queryPartials,PagingInfo pagingInfo,DisplayOrder displayOrder){
        String[] queryParameterString = new String[queryPartials.size()];
        int i = 0;
        for(QueryPartial queryPartial:queryPartials){
            String colname = queryPartial.getCol();
            String value = queryPartial.getValue();
            if(value == null){
                throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"no data contained, empty param:" + colname +" in request");
            }
            
            String parameterString = escapeColumn(colname)+queryPartial.getOp()+paramValueStringInSql(colname,value,getModelClass());
            queryParameterString[i++] = parameterString;
        }
        String joinedParameters = StringUtils.join(queryParameterString," and ");
        Integer pageSize = pagingInfo.getPageSize();
        Integer start = pagingInfo.getStart();
        if(null == start){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"param start is null");
        }
        if(null == pageSize){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"param pageSize is null");
        }
        StringBuffer sb = new StringBuffer();
        
        if(start == 0 && displayOrder == DisplayOrder.desc){
            start = Integer.MAX_VALUE;
        }
        String idRestriction = "";
        if(displayOrder == DisplayOrder.asc){
            idRestriction = " and id >"+start;
        }else {
            idRestriction = " and id <"+start;
        }
        
        sb.append("select * from ").append(escape(tableName)).append(" where ").append(joinedParameters);
        sb.append(idRestriction);
        sb.append(" order by id ").append(displayOrder.toString());
        sb.append(" limit ").append(pageSize);
        sb.append(";");
        
        String returnSql = sb.toString();
        logger.debug(returnSql);
        return returnSql;
    }
    
    
    @Override
    public String createQuerySqlFromQueryPartials(List<Tuple<CommandParam, String>> queryPartials,CommandRequest request,PagingInfo pagingInfo,DisplayOrder displayOrder){
        String[] queryParameterString = new String[queryPartials.size()];
        int i = 0;
        for(Tuple<CommandParam, String> queryPartial :queryPartials){
            CommandParam param = request.getParam(queryPartial.getLeft().getName());
            if(param == null ){
                throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"no param found for " + queryPartial.getLeft().getName() +" in request");
            }
            if(param.getData() == null ){
                throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"no data contained, empty param:" + queryPartial.getLeft().getName() +" in request");
            }
            
            String name = param.getName();
            String value = param.getData().toString();
            String parameterString = escapeColumn(name)+"="+paramValueStringInSql(name,value,getModelClass());
            queryParameterString[i++] = parameterString;
            
        }
        
        String joinedString = StringUtils.join(queryParameterString," and ");
        
        Integer pageSize = pagingInfo.getPageSize();
        Integer start = pagingInfo.getStart();
        if(null == start){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"param start is null");
        }
        if(null == pageSize){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"param pageSize is null");
        }
        
        if(start == 0 && displayOrder == DisplayOrder.desc){
            start = Integer.MAX_VALUE;
        }
        String idRestriction = "";
        if(displayOrder == DisplayOrder.asc){
            idRestriction = " and id >"+start;
        }else {
            idRestriction = " and id <"+start;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("select * from ").append(escape(tableName)).append(" where ").append(joinedString);
        sb.append(idRestriction);
        sb.append(" order by id ").append(displayOrder.toString());
        sb.append(" limit ").append(pageSize);
        sb.append(";");
        
        String returnSql = "select * from "+escape(tableName) +" where "+joinedString+";";
        return returnSql;
    }
    
    
    /**
     * 
     * @param <T>
     * @param params
     * @param modelClass
     * @param start
     * @param range
     * @param order
     * @return
     * @throws DataAccessException
     */
    public <T> List<T> selectListByParamsWithPaging(Map params,Class<T> modelClass,Integer start,Integer range,RecordOrder order) throws DataAccessException{
        List<T> res = null;
        res = this.jdbcTemplate.query(
                genSelectByParamSqlWithPaging(params,modelClass,start,range,order), 
                ParameterizedBeanPropertyRowMapper.newInstance(modelClass));
        return res;
    }
    /**
     * only select several colums from a table for performance
     * @param <T>
     * @param columns
     * @param params
     * @param modelClass
     * @param start
     * @param range
     * @param order
     * @return
     * @throws DataAccessException
     */
    public <T> List<T> selectListByParamsWithPaging(String[] columns,Map params,Class<T> modelClass,Integer start,Integer range,RecordOrder order) throws DataAccessException{
        List<T> res = null;
        res = this.jdbcTemplate.query(
                genSelectColumnsByParamSqlWithPaging(columns,params,modelClass,start,range,order), 
                ParameterizedBeanPropertyRowMapper.newInstance(modelClass));
        return res;
    }
    
    /**
     * return only one record， if the query returns more than 1 record, an exception is throwed
     * @param <T>
     * @param params
     * @param modeClass
     * @return
     */
    public <T> T selectSingleRecordByParams(Map params ,Class<T> modelClass) throws DataAccessException{
        T res = null;
        res = this.jdbcTemplate.queryForObject
        (genSelectByParamSqlWithPaging(params,modelClass,null,null,null), 
                ParameterizedBeanPropertyRowMapper.newInstance(modelClass));
        return res;
    }
    
    public RecordOrder getOrder(RecordOrder order){
        if (order==null) {
            return defaultOrder;
        }else {
            return order;
        }
    }
    
    protected static RecordOrder defaultOrder = new RecordOrder(DisplayOrder.asc, com.rapidbackend.socialutil.dao.BaseDao.OrderByColumn.id);
    /**
     * use this method always to avoid performance hang
     * @param queryParams
     * @param modelClass
     * @param start
     * @param range
     * @param order if this value is empty, the function will use defaultOrder as the order
     * @return
     */
    public String genSelectByParamSqlWithPaging(Map queryParams,Class modelClass,Integer start, Integer range,RecordOrder order){
        if(queryParams.size()<1){
            logger.error("genSelectByParamSqlWithPaging: param size is 0, return null");
            return null;
        }
        Set<Map.Entry> set = queryParams.entrySet();
        String[] queryParameterString = new String[queryParams.size()];
        int i = 0;
        for(Map.Entry e: set){
            String name = e.getKey().toString();
            String value = e.getValue().toString();
            String parameterString = escapeColumn(name)+"="+paramValueStringInSql(name,value,modelClass);
            queryParameterString[i++] = parameterString;
        }
        
        StringBuffer sb = new StringBuffer();
        String whereClauseParameter = StringUtils.join(queryParameterString," and ");
        sb.append("select * from ").append(escape(tableName)).
        append(" where ").append(whereClauseParameter);
        sb.append(getOrder(order).toString());
        sb.append(" limit ").append(getRecordStart(start, defaultStart)).append(",")
        .append(getRecordRange(range, defaultRange)).append(";");
        return sb.toString();
    }
    /**
     * only select several columns from a table
     * @param queryParams
     * @param modelClass
     * @param start
     * @param range
     * @param order
     * @return
     */
    public String genSelectColumnsByParamSqlWithPaging(String[] columns,Map queryParams,Class modelClass,Integer start, Integer range,RecordOrder order){
        if(queryParams.size()<1){
            logger.error("genSelectByParamSqlWithPaging: param size is 0, return null");
            return null;
        }
        Set<Map.Entry> set = queryParams.entrySet();
        String[] queryParameterString = new String[queryParams.size()];
        int i = 0;
        for(Map.Entry e: set){
            String name = e.getKey().toString();
            String value = e.getValue().toString();
            String parameterString = escapeColumn(name)+"="+paramValueStringInSql(name,value,modelClass);
            queryParameterString[i++] = parameterString;
        }
        
        StringBuffer sb = new StringBuffer();
        String whereClauseParameter = StringUtils.join(queryParameterString," and ");
        String[] escapedColumns = new String[columns.length];
        int j=0;
        for(String c:columns){
            escapedColumns[j++] = escape(c);
        }
        
        String cols = StringUtils.join(escapedColumns,",");
        sb.append("select ").append(cols).append(" from ").append(escape(tableName)).
        append(" where ").append(whereClauseParameter);
        sb.append(getOrder(order).toString());
        sb.append(" limit ").append(getRecordStart(start, defaultStart)).append(",")
        .append(getRecordRange(range, defaultRange)).append(";");
        return sb.toString();
    }
    /**
     * for now , three fields : ide created notfound form root class DbRecord should not be updated
     * @param fieldName
     * @return
     */
    protected boolean isFieldToSkipInUpdate(String fieldName){
        return (fieldName.equalsIgnoreCase("id")||
                    fieldName.equalsIgnoreCase("created")||
                    fieldName.equalsIgnoreCase("notFound"));
    }
    
    protected boolean valueToSkipInUpdate(Object value){
        return value.getClass().isArray();
    }
    /**
     * 
     * @param queryParams
     * @param valueParams
     * @param clazz
     * @return
     */
    protected String genUpdateByColumnsSql(Map queryParams,Map valueParams,Class clazz){
        if(queryParams.size()<1||valueParams.size()<1){
            logger.error("genUpdateByColumnsSql: param size is 0, return null");
            return null;
        }
        Set<Map.Entry> set = queryParams.entrySet();
        List<String> queryParameterString = new ArrayList<String>();
        //String[] queryParameterString = new String[queryParams.size()];
        
        for(Map.Entry e: set){
            String name = e.getKey().toString();
            String value = e.getValue().toString();
            String parameterString = escapeColumn(name)+"="+paramValueStringInSql(name,value,clazz);
            queryParameterString.add(parameterString);
        }
        List<String> setValues = new ArrayList<String>();
        Set<Map.Entry> newValues = valueParams.entrySet();
        
        for(Map.Entry e: newValues){
            
            String name = e.getKey().toString();
            if(isFieldToSkipInUpdate(name) || valueToSkipInUpdate(e.getValue())){
                continue;// skipping some reserved parameters
            }
            String value = e.getValue().toString();
            String parameterString = escape(name)+"="+paramValueStringInSql(name,value,clazz);
            setValues.add(parameterString);
        }
        String whereClauseParameter = StringUtils.join(queryParameterString," and ");
        String setClauseParameter = StringUtils.join(setValues,",");
        StringBuffer stringBuffer = new StringBuffer("");
        
        stringBuffer.append("update ").append(escape(tableName)).append(" set ").
        append(setClauseParameter).append(" where ").append(whereClauseParameter).append(";");
        
        return stringBuffer.toString();
    }
    /**
     * Return the appropriate format of the param values in a sql
     * For example, if field is String, it should be 'value'.
     * Now this method only supports long, int, text, varchar,float
     * which corespond to java.lang.Long,java.lang.integer,java.lang.String,java.lang.Float
     * @param fieldName
     * @param fieldValueString
     * @param clazz
     * @return
     */
    public static String paramValueStringInSql(String fieldName, String fieldValueString,Class<?> modelClazz){
        Field field = ModelReflectionUtil.getModelField(fieldName, modelClazz);
        Class<?> clazz = field.getType();
        String className = clazz.getName();
        if(clazz.equals(String.class)){
            return "'"+fieldValueString+"'";
        }else if(clazz.equals(Float.class)){
            return "CAST("+fieldValueString+" as DECIMAL)";
        }
        else if (
                clazz.equals(Long.class)||
                clazz.equals(Integer.class)
                ) {
            return fieldValueString;
        }else {
            throw new UnsupportedOperationException(className+" is not supported");
        }
    }
    
    
    protected String genQueryAllByIdsSql(int[] ids){
        if(ids==null || ids.length==0){
            return null;
        }
        String joinedString = ConversionUtils.join(ids, ',');
        return "select * from "+tableName +" where id in ("+joinedString+")"+";";
    }
    
    
    
    
    protected String genQuerySingleColumnByParamSql(Map nvPair,String column,Class modelClass){
        if(nvPair.size()<1){
            return null;
        }
        Set<Map.Entry> set = nvPair.entrySet();
        String[] sqlParameterString = new String[nvPair.size()];
        int i = 0;
        for(Map.Entry e: set){
            String name = e.getKey().toString();
            String value = e.getValue().toString();
            String parameterString = escapeColumn(name)+"="+paramValueStringInSql(name, value, modelClass);
            //logger.info("getQueryColumnSql : parameterString is "+parameterString);
            sqlParameterString[i++] = parameterString;
        }
        String joinedString = StringUtils.join(sqlParameterString," and ");
        return "select "+column+" from "+escape(tableName) +" where "+joinedString+";";
    }
    
    protected String genQueryMultiColumnsByParamSql(Map nvPair,String[] columns,Class modelClass){
        if(nvPair.size()<1){
            return null;
        }
        Set<Map.Entry> set = nvPair.entrySet();
        String[] sqlParameterString = new String[nvPair.size()];
        int i = 0;
        for(Map.Entry e: set){
            String name = e.getKey().toString();
            String value = e.getValue().toString();
            String parameterString = escapeColumn(name)+"="+paramValueStringInSql(name, value, modelClass);
            //logger.info("getQueryColumnSql : parameterString is "+parameterString);
            sqlParameterString[i++] = parameterString;
        }
        String joinedString = StringUtils.join(sqlParameterString," and ");
        return "select "+StringUtils.join(columns,',')+" from "+escape(tableName) +" where "+joinedString+";";
    }
    
    /**
     * this function does not support paging
     * @param <T>
     * @param column
     * @param value
     * @param modelClass
     * @return
     * @throws DataAccessException
     */
    public <T> List<T> selectListByColumn(String column,String value, Class<T> modelClass) throws DataAccessException{
    	List<T> res = null;
    	res = this.jdbcTemplate.query(
    			genQueryAllByParamSql(column), 
                ParameterizedBeanPropertyRowMapper.newInstance(modelClass),
                paramValueStringInSql(column,value,modelClass));
    	return res;
    }
    
    public <T> List<T> selectListBySql(String sqlQuery,Class<T> modelClass) throws DataAccessException{
        List<T> res = null;
        res = this.jdbcTemplate.query(
                sqlQuery, 
                ParameterizedBeanPropertyRowMapper.newInstance(modelClass));
        return res;
    }
    
    
    public <T> T loadByColumn(String column,String value, Class<T> modelClass) throws DataAccessException{
        T res = null;
        try{
            res = this.jdbcTemplate.queryForObject(
            		genQueryAllByParamSql(column),
                    ParameterizedBeanPropertyRowMapper.newInstance(modelClass),
                    value
                    );
        }catch(EmptyResultDataAccessException ex){
            throw new ObjectRetrievalFailureException(modelClass, column+"="+value);
        }
        return res;
    }
    public <T> List<T> selectListByColumns(Map nameValuePair, Class<T> modelClass) throws DataAccessException{
    	List<T> res = null;
    	res = this.jdbcTemplate.query(
    			genQueryAllByParamSql(nameValuePair,modelClass), 
                ParameterizedBeanPropertyRowMapper.newInstance(modelClass));
    	return res;
    }
    public int updateRecordByColumns(Map queryNameValuePair,Map updateValuePair,Class<?> clazz) throws DataAccessException{
        return this.jdbcTemplate.update(genUpdateByColumnsSql(queryNameValuePair,updateValuePair,clazz));
    }
    public <T> T selectSingleRecordByColumns(Map nameValuePair, Class<T> modelClass) throws DataAccessException{
        T res = null;
        try{
            res = this.jdbcTemplate.queryForObject(
            		genQueryAllByParamSql(nameValuePair,modelClass),
                    ParameterizedBeanPropertyRowMapper.newInstance(modelClass)
                    );
        }catch(EmptyResultDataAccessException ex){
            throw new ObjectRetrievalFailureException(modelClass, nameValuePair);
        }
        return res;
    }
    public <T> List<T> selectByIds(int[] ids, Class<T> modelClass) throws DataAccessException{
        List<T> res = null;
        res = this.jdbcTemplate.query(
                genQueryAllByIdsSql(ids),
                ParameterizedBeanPropertyRowMapper.newInstance(modelClass)
                );
        return res;
    }
    public <T> T selectById(int id, Class<T> modelClass) throws DataAccessException{
        T res = null;
        try{
            res = this.jdbcTemplate.queryForObject(
                    getQueryBySingleIdSql(),
                    ParameterizedBeanPropertyRowMapper.newInstance(modelClass),
                    id
                    );
        }catch(EmptyResultDataAccessException ex){
            throw new ObjectRetrievalFailureException(modelClass, new Integer(id));
        }
        return res;
    }
    public <T> T selectBySql(String sqlQuery, Class<T> modelClass) throws DataAccessException{
        T res = null;
        try{
            res = this.jdbcTemplate.queryForObject(
                    sqlQuery,
                    ParameterizedBeanPropertyRowMapper.newInstance(modelClass)
                    );
        }catch(EmptyResultDataAccessException ex){
            throw new ObjectRetrievalFailureException(modelClass, sqlQuery);
        }
        return res;
    }
    /**
     * select collums which contains only a int value
     * @param sqlQuery
     * @return
     * @throws DataAccessException
     */
    public List<Integer> selectIntColumBySql(String sqlQuery) throws DataAccessException{
    	List<Integer> result = new ArrayList<Integer>();
    	try{
    		result = this.jdbcTemplate.query(sqlQuery, new SingleColumnRowMapper<Integer>());
    	}catch(EmptyResultDataAccessException ex){
            throw new ObjectRetrievalFailureException(Integer.class, sqlQuery);
        }
    	return result;
    }
    /**
     * 
     * @param param
     * @param column
     * @return
     * @throws DataAccessException
     */
    public List<Integer> selectIntColum(Map param,String column,Class modelClass) throws DataAccessException{
    	List<Integer> result = new ArrayList<Integer>();
    	String sqlquery = genQuerySingleColumnByParamSql(param, column,modelClass);
    	try{
    		result = this.jdbcTemplate.query(sqlquery, new SingleColumnRowMapper<Integer>());
    	}catch(EmptyResultDataAccessException ex){
            throw new ObjectRetrievalFailureException(Integer.class, sqlquery);
        }
    	return result;
    }
    
    public Integer selectIntObject(Map param,String column,Class modelClass) throws DataAccessException{
        Integer result = null;
        String sqlquery = genQuerySingleColumnByParamSql(param, column,modelClass);
        try{
            result = this.jdbcTemplate.queryForInt(sqlquery);
        }catch(EmptyResultDataAccessException ex){
            throw new ObjectRetrievalFailureException(Integer.class, sqlquery);
        }
        return result;
    }
    /**
     * return the load id sql,  select id only for smaller data size and better performance
     * @param nvPair
     * @return
     */
    protected String getLoadByIdSql(Map nvPair){
    	logger.debug("nvPair size is "+nvPair.size());
        if(nvPair.size()<1){
            return null;
        }
        Set<Map.Entry> set = nvPair.entrySet();
        String[] sqlParameterString = new String[nvPair.size()];
        int i = 0;
        for(Map.Entry e: set){
            String name = e.getKey().toString();
            name = escape(name);
            String value = e.getValue().toString();
            String parameterString = name+"="+value;
            logger.debug("getQueryColumnSql : parameterString is "+parameterString);
            sqlParameterString[i++] = parameterString;
        }
        String joinedString = StringUtils.join(sqlParameterString," and ");
        return "select id from "+escape(tableName) +" where "+joinedString+";";
    }
    /**
     * check if the record exists
     * @param nameValuePair
     * @return
     * @throws DataAccessException
     */
    public boolean checkRecordExists(Map nameValuePair) throws DataAccessException{
    	return loadIdByValue(nameValuePair)>0;
    }
    
    public int loadIdByValue(Map nameValuePair) throws DataAccessException{
    	int result = -1;
    	try{
    		result = this.jdbcTemplate.
        	queryForInt(getLoadByIdSql(nameValuePair));
    	}catch(EmptyResultDataAccessException e){
    		result = 0;
    	}
    	return result;
    }
    /**
     * load row ids(the id collumn, which is default PKEY to every table in this application)
     * @param nameValuePair
     * @return
     * @throws DataAccessException
     */
    public List<Integer> loadIdListByValue(Map nameValuePair) throws DataAccessException{
    	List<Integer> idList = new ArrayList<Integer>();
    	try{
    		idList = this.jdbcTemplate.query(getLoadByIdSql(nameValuePair),
    				new SingleColumnRowMapper<Integer>());
    	}catch(EmptyResultDataAccessException e){
    		
    	}
    	return idList;
    }
    /**
     * 
     * @param fieldName
     * @return
     */
    protected boolean isFieldToSkipInInsert(String fieldName){
        return (fieldName.equalsIgnoreCase("id")||
                    fieldName.equalsIgnoreCase("notFound"));
    }
    /**
     * 
     * @param model
     * @return
     */
    protected List<Tuple<Object, Object>> getInsertValues(Object model){
        if(model==null){
            throw new IllegalArgumentException("model object is null");
        }
        Class<?> modelClass = model.getClass();
        Field[] fields = ModelReflectionUtil.getModelFields(model.getClass());
        
        List<Tuple<Object, Object>> insertValues = new ArrayList<Tuple<Object, Object>>();
        for(Field field:fields){
            String fieldName = field.getName();
            Object value = ModelReflectionUtil.getPropertyValue(modelClass, fieldName, model);
            if(!isFieldToSkipInInsert(fieldName)){
                insertValues.add(new Tuple<Object, Object>(fieldName, value));
            }
        }
        if(insertValues.size()==0){
            throw new IllegalArgumentException("insert values are empty");
        }
        return insertValues;
    }
    
    protected String createInsertSql(List<Tuple<Object, Object>> insertValues,Object modelBean) {        
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(escape(tableName)).append("(");
        int i=0;
        for(Tuple<Object, Object> tuple:insertValues){
            String column = tuple.getLeft().toString();
            sb.append(escape(column));
            if(i<insertValues.size()-1){
                sb.append(',');i++;
            }
        }
        sb.append(") values(");
        i=0;
        for(Tuple<Object, Object> tuple:insertValues){
            //sb.append(paramValueStringInSql(tuple.getKey().toString(), tuple.getValue().toString(), modelBean.getClass()));
            sb.append('?');
            if(i<insertValues.size()-1){
                sb.append(',');i++;
            }
        }
        sb.append(")");
        return sb.toString();
    }
    private static class ArgPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {

        private final Object[] args;


        /**
         * Create a new ArgPreparedStatementSetter for the given arguments.
         * @param args the arguments to set
         */
        public ArgPreparedStatementSetter(Object[] args) {
            this.args = args;
        }


        public void setValues(PreparedStatement ps) throws SQLException {
            if (this.args != null) {
                for (int i = 0; i < this.args.length; i++) {
                    Object arg = this.args[i];
                    doSetValue(ps, i + 1, arg);
                }
            }
        }

        /**
         * Set the value for prepared statements specified parameter index using the passed in value.
         * This method can be overridden by sub-classes if needed.
         * @param ps the PreparedStatement
         * @param parameterPosition index of the parameter position
         * @param argValue the value to set
         * @throws SQLException
         */
        protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
            if (argValue instanceof SqlParameterValue) {
                SqlParameterValue paramValue = (SqlParameterValue) argValue;
                StatementCreatorUtils.setParameterValue(ps, parameterPosition, paramValue, paramValue.getValue());
            }
            else {
                StatementCreatorUtils.setParameterValue(ps, parameterPosition, SqlTypeValue.TYPE_UNKNOWN, argValue);
            }
        }

        public void cleanupParameters() {
            StatementCreatorUtils.cleanupParameters(this.args);
        }

    }
    private static class SimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

        private final String sql;

        public SimplePreparedStatementCreator(String sql) {
            Assert.notNull(sql, "SQL must not be null");
            this.sql = sql;
        }
        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            return con.prepareStatement(this.sql,Statement.RETURN_GENERATED_KEYS);
        }
        @Override
        public String getSql() {
            return this.sql;
        }
    }
    private static class InsertCallBack implements PreparedStatementCallback<Integer>{
        Logger logger = LoggerFactory.getLogger(getClass());
        private Object[] values;
        private GeneratedKeyHolder keyHolder;
        public InsertCallBack(Object[] values,GeneratedKeyHolder keyHolder){
            this.values = values;
            this.keyHolder = keyHolder;
        }
        
        public GeneratedKeyHolder getKeyHolder() {
            return keyHolder;
        }

        public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
            int i=1;
            ArgPreparedStatementSetter preparedStatementSetter = new ArgPreparedStatementSetter(values);
            try {
                preparedStatementSetter.setValues(ps);
                int rows = ps.executeUpdate();
                logger.debug("SQL update affected " + rows + " rows");
                
                List<Map<String, Object>> generatedKeys = keyHolder.getKeyList();
                generatedKeys.clear();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys != null) {
                    try {
                        RowMapperResultSetExtractor<Map<String, Object>> rse =
                                new RowMapperResultSetExtractor<Map<String, Object>>(new ColumnMapRowMapper(), 1);
                        generatedKeys.addAll(rse.extractData(keys));
                    }
                    finally {
                        JdbcUtils.closeResultSet(keys);
                    }
                }
                
                return rows;
            } finally {
                preparedStatementSetter.cleanupParameters();
            }
        }
    }
    /**
     * stores a model and returns the key
     * @param modelBean should not be null
     * @return the id of the return inserted row, return -1 if error happens
     * @throws DataAccessException
     */
    public int storeModelBean(Object modelBean) throws DataAccessException{
        if(modelBean!=null){
        	InsertCallBack insertCallBack = null;
        	try {
        		List<Tuple<Object, Object>> insertValues = getInsertValues(modelBean);
                String sql = createInsertSql(insertValues,modelBean);
                logger.debug(sql);
                GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
                
                final Object[] values = new Object[insertValues.size()];
                int i = 0;
                for(Tuple<Object, Object> tuple:insertValues){
                    values[i++] = tuple.getRight();
                }
                insertCallBack = new InsertCallBack(values,keyHolder);
                //this.jdbcTemplate.update(new SimplePreparedStatementCreator(sql), keyHolder);
                //return keyHolder.getKey().intValue();
                this.jdbcTemplate.execute(new SimplePreparedStatementCreator(sql), insertCallBack);
                return insertCallBack.getKeyHolder().getKey().intValue();
			} catch (Exception e) {
				throw new DataAccessException("error store model " + modelBean.getClass().getSimpleName(),e);
			}
            
        }else{
            return -1;
        }
    }
    public int storeNewModelBean(Object modelBean) throws DataAccessException{
        if(modelBean!=null){
            try {

                ModelBeanUtil.initBeanTimestamps(modelBean);
            } catch (Exception e) {
                throw new DataAccessException("error happens when try to init bean's create time'",e);
            }
            return storeModelBean(modelBean);
        }else{
            return -1;
        }
    }
    public int storeModifiedModelBean(Object modelBean) throws DataAccessException{
        if(modelBean!=null){
            try {
                ModelBeanUtil.setBeanModifiedTime(modelBean);
            } catch (Exception e) {
                throw new DataAccessException("error happens when try to modify model bean's property 'modified'",e);
            }
            return storeModelBean(modelBean);
        }else{
            return -1;
        }
    }
    
    protected String genDeleteByIdSql(int id){
        StringBuffer sb = new StringBuffer("");
        sb.append("delete from ").append(escape(tableName)).append(" where id=").append(id);
        return sb.toString();
    }
    
    @Override
    public int deleteModelById(int id) throws DataAccessException{
        return this.jdbcTemplate.update(genDeleteByIdSql(id));
    }
    
    @Override
    public int deleteModelByParam(Map param) throws DataAccessException{
        return this.jdbcTemplate.update(genDeleteByParamSql(param));
    }
    
    protected String genDeleteByParamSql(Map nvPair) throws DataAccessException{
        logger.debug("nvPair size is "+nvPair.size());
        if(nvPair.size()<1){
            return null;
        }
        Set<Map.Entry> set = nvPair.entrySet();
        String[] sqlParameterString = new String[nvPair.size()];
        int i = 0;
        for(Map.Entry e: set){
            String name = e.getKey().toString();
            String value = e.getValue().toString();
            String parameterString = escapeColumn(name)+"="+paramValueStringInSql(name, value, getModelClass());
            sqlParameterString[i++] = parameterString;
        }
        String joinedString = StringUtils.join(sqlParameterString," and ");
        String returnSql = "delete from "+escape(tableName) +" where "+joinedString+";";
        logger.debug(returnSql);
        return returnSql;
    }
    
    public void storeModelBeanBatch(List modelBeanList) throws DataAccessException{
        BeanPropertySqlParameterSource[] bpsps = new BeanPropertySqlParameterSource[modelBeanList.size()];
        int i = 0;
        for(Object m : modelBeanList){
            bpsps[i++] = new BeanPropertySqlParameterSource(m);
        }
        this.simpleJdbcInsert.executeBatch(bpsps);
    }
    /**
     * Example transaction method, user can use transaction in this way or just use spring annotation @Transactional
     * @return
     */
    @Deprecated  
    public Object transactionalMethod(){
        final Object  object = new Object();
        return transactionTemplate.execute(
                new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus status) {
                        try{
                            return null;
                        }catch(Exception e){
                            status.setRollbackOnly();
                        }finally{
                            return null;
                        }
                        
                      }
                }
                );
    }
    
}