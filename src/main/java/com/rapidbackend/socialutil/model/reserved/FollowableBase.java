package com.rapidbackend.socialutil.model.reserved;

import com.rapidbackend.core.model.DbRecord;

public class FollowableBase extends DbRecord{
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tag.id
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tag.modified
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    private Long modified;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tag.created
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    private Long created;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tag.createdby
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    private Integer createdby;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tag.id
     *
     * @return the value of tag.id
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tag.id
     *
     * @param id the value for tag.id
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tag.modified
     *
     * @return the value of tag.modified
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    public Long getModified() {
        return modified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tag.modified
     *
     * @param modified the value for tag.modified
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    public void setModified(Long modified) {
        this.modified = modified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tag.created
     *
     * @return the value of tag.created
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    public Long getCreated() {
        return created;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tag.created
     *
     * @param created the value for tag.created
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    public void setCreated(Long created) {
        this.created = created;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tag.createdby
     *
     * @return the value of tag.createdby
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    public Integer getCreatedby() {
        return createdby;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tag.createdby
     *
     * @param createdby the value for tag.createdby
     *
     * @mbggenerated Thu May 02 16:50:30 CST 2013
     */
    public void setCreatedby(Integer createdby) {
        this.createdby = createdby;
    }
}
