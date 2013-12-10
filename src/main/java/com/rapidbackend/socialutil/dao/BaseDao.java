package com.rapidbackend.socialutil.dao;

import java.util.List;
import java.util.Map;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.OrderParam;
import com.rapidbackend.core.request.QueryParam.QueryPartial;
import com.rapidbackend.util.general.Tuple;

public interface BaseDao {
    
    public static enum DisplayOrder{
        desc,asc
    }

    public static enum OrderByColumn{
        id,created,modified
    }

    public static class RecordOrder{
        private DisplayOrder order;
        private OrderByColumn column;
        public RecordOrder(DisplayOrder order,OrderByColumn column){
            this.order = order;
            this.column = column;
        }
        private String stringVal = null;
        @Override
        public String toString(){
            if(stringVal == null){
                stringVal = " ORDER BY "+column+" "+order+" ";
            }
            return stringVal;
        }
        public DisplayOrder getOrder() {
            return order;
        }
        
    }
    
    public static class PagingInfo{
        public static int DEFAULT_START=0;
        public static int DEFAULT_PAGESIZE=20;
        private Integer start = DEFAULT_START;
        private Integer pageSize = DEFAULT_PAGESIZE;
        private Integer nextStart = -1;
        /**
         * @return the start index , exclusive
         */
        public Integer getStart() {
            return start;
        }
        /**
         * 
         * @param start the start index , exclusive
         */
        public void setStart(Integer start) {
            this.start = start;
        }
        public Integer getPageSize() {
            return pageSize;
        }
        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }
        /**
         * 
         * @return the next start index, exclusive. This index will be negative if there is no more item to fetch. 
         * Notice that this may not be the actual id of the next record to fetch. It can be an id stored in another class.
         * We can use this id to map to the class we want to query.
         */
        public Integer getNextStart() {
            return nextStart;
        }
        public void setNextStart(Integer nextStart) {
            this.nextStart = nextStart;
        }
        
        public PagingInfo(Integer start, Integer pageSize){
            if(start == null){
                this.start = DEFAULT_START;
            }else {
                this.start = start;
            }
            if(pageSize == null){
                this.pageSize = DEFAULT_PAGESIZE;
            }else {
                this.pageSize = pageSize;
            }
        }
        
        public PagingInfo(){}
        /**
         * 
         * @param existingPagingInfo paging info we recieved from request
         * @param result the ordered result list
         * @return
         */
        public static PagingInfo createPagingInfo(PagingInfo existingPagingInfo,List<?> result){
            Integer start = existingPagingInfo.getStart();
            Integer pageSize = existingPagingInfo.getPageSize();
            PagingInfo newPagingInfo = new PagingInfo();
            
            if(result == null || result.size() ==0){
                newPagingInfo.setPageSize(0);
                newPagingInfo.setNextStart(-1);
                newPagingInfo.setStart(start);
            }else {
                Object lastRecord = result.get(result.size()-1);
                Integer lastId = 0;
                
                if(lastRecord instanceof Integer){
                    lastId = (Integer)lastRecord;
                }else if (lastRecord instanceof DbRecord) {
                    lastId = ((DbRecord)lastRecord).getId();
                }else {
                    throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"unsupported object type in creating paging info:"+lastRecord);
                }
                newPagingInfo.setStart(start);
                newPagingInfo.setPageSize(result.size());
                if(result.size() >= pageSize){
                    newPagingInfo.setNextStart(lastId);
                }else {
                    newPagingInfo.setNextStart(-1);
                }
            }
            
            return newPagingInfo;
        }
    }
    
    //TODO add a set of APIs to hide modelClass
    
    public Class<?> getModelClass() throws DataAccessException;
    
    public <T> T selectById(int id, Class<T> modelClass) throws DataAccessException;
    
    public int storeModelBean(Object modelBean) throws DataAccessException;
    
    public int storeModifiedModelBean(Object modelBean) throws DataAccessException;
    
    public int storeNewModelBean(Object modelBean) throws DataAccessException;
    
    public int deleteModelById(int id) throws DataAccessException;
    
    public int deleteModelByParam(Map param) throws DataAccessException;
    
    public <T> List<T> selectByIds(int[] ids, Class<T> modelClass) throws DataAccessException;
    
    public <T> T loadByColumn(String column,String value, Class<T> trunkModelClass) throws DataAccessException;
    
    public <T> List<T> selectListBySql(String sqlQuery,Class<T> modelClass) throws DataAccessException;
    
    public <T> T selectSingleRecordByParams(Map params ,Class<T> modelClass) throws DataAccessException;
    
    public List<Integer> selectIntColumBySql(String sqlQuery) throws DataAccessException;
    
    /**
     * @return the tableName
     */
    public String getTableName() ;
    
    public <T> List<T> selectListByColumn(String column,String value, Class<T> modelClass) throws DataAccessException;
    
    public <T> List<T> selectListByColumns(Map nameValuePair, Class<T> modelClass) throws DataAccessException;
    
    public Integer selectIntObject(Map param,String column,Class modelClass) throws DataAccessException;
    /**
     * for performance consideration, it should be used only when the start is set to 0 if the table you query is very large
     * @param params equation conditions
     * @param modelClass
     * @param start
     * @param pageSize
     * @param order
     * @return
     * @throws DataAccessException
     */
    public <T> List<T> selectListByParamsWithPaging(Map params,Class<T> modelClass,Integer start,Integer pageSize,RecordOrder order) throws DataAccessException;
    /**
     * for performance consideration, it should be used when the start is set to 0 if the table you query is very large
     * @param columns
     * @param params equation conditions
     * @param modelClass
     * @param start
     * @param pageSize
     * @param order
     * @return
     * @throws DataAccessException
     */
    public <T> List<T> selectListByParamsWithPaging(String[] columns,Map params,Class<T> modelClass,Integer start,Integer pageSize,RecordOrder order) throws DataAccessException;
    
    public int updateRecordByColumns(Map queryNameValuePair,Map updateValuePair,Class<?> clazz) throws DataAccessException;
    
    public <T> T selectSingleRecordByColumns(Map nameValuePair, Class<T> modelClass) throws DataAccessException;
    /**
     * create the querying sql by the query params.
     * @param queryPartials query params
     * @param request incoming request
     * @param pagingInfo the pagingInfo mation, note that the paging here only works on column 'id'
     * @param recordOrder order of the returned value
     * @return
     */
    @Deprecated
    public String createQuerySqlFromQueryPartials(List<Tuple<CommandParam, String>> queryPartials,CommandRequest request,PagingInfo pagingInfo,DisplayOrder displayOrder);
    
    public String createQuerySqlWithQueryPartials(List<QueryPartial> queryPartials,PagingInfo pagingInfo,DisplayOrder displayOrder);
}
