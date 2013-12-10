package com.rapidbackend.core.model;
/**
 * Base class for data models. Used for generic database record handling.
 * Note: do not declare any fields that will be actual database columns
 * in this class. 
 * @author chiqiu
 */
public abstract class DbRecord {
    private Boolean notFound = false;
    private Long created;
    private Long modified;
    /**
     * the auto-generated primary key for a record
     */
    private Integer id;
    //TODO add the idToken String variable to replace the id integer
    // use Integer.toString(i,radix) to convert it
    
    public Boolean getNotFound() {
        return notFound;
    }
    public void setNotFound(Boolean notFound) {
        this.notFound = notFound;
    }
    /**
     * create
     * @param <T>
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T>DbRecord createNotFoundModel(Class<T> clazz) throws Exception{
        DbRecord obj = (DbRecord)clazz.newInstance();
        obj.setNotFound(true);
        return obj;
    }
    public static boolean isEmptyRecord(DbRecord record){
        return record==null || record.getNotFound();
    }
       
    public Long getCreated() {
        return created;
    }
    public void setCreated(Long created) {
        this.created = created;
    }
    
    public Long getModified() {
        return modified;
    }
    public void setModified(Long modified) {
        this.modified = modified;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public static DbRecord initTime(DbRecord record){
        Long currentTime = System.currentTimeMillis();
        record.setCreated(currentTime);
        record.setModified(currentTime);
        return record;
    }
}
