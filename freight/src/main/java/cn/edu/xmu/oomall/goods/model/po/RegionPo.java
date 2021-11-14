package cn.edu.xmu.oomall.goods.model.po;

import java.time.LocalDateTime;

public class RegionPo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oomall_region.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oomall_region.pid
     *
     * @mbg.generated
     */
    private Long pid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oomall_region.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oomall_region.state
     *
     * @mbg.generated
     */
    private Byte state;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oomall_region.created_by
     *
     * @mbg.generated
     */
    private Long createdBy;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oomall_region.create_name
     *
     * @mbg.generated
     */
    private String createName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oomall_region.modified_by
     *
     * @mbg.generated
     */
    private Long modifiedBy;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oomall_region.modi_name
     *
     * @mbg.generated
     */
    private String modiName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oomall_region.gmt_create
     *
     * @mbg.generated
     */
    private LocalDateTime gmtCreate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oomall_region.gmt_modified
     *
     * @mbg.generated
     */
    private LocalDateTime gmtModified;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oomall_region.id
     *
     * @return the value of oomall_region.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oomall_region.id
     *
     * @param id the value for oomall_region.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oomall_region.pid
     *
     * @return the value of oomall_region.pid
     *
     * @mbg.generated
     */
    public Long getPid() {
        return pid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oomall_region.pid
     *
     * @param pid the value for oomall_region.pid
     *
     * @mbg.generated
     */
    public void setPid(Long pid) {
        this.pid = pid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oomall_region.name
     *
     * @return the value of oomall_region.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oomall_region.name
     *
     * @param name the value for oomall_region.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oomall_region.state
     *
     * @return the value of oomall_region.state
     *
     * @mbg.generated
     */
    public Byte getState() {
        return state;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oomall_region.state
     *
     * @param state the value for oomall_region.state
     *
     * @mbg.generated
     */
    public void setState(Byte state) {
        this.state = state;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oomall_region.created_by
     *
     * @return the value of oomall_region.created_by
     *
     * @mbg.generated
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oomall_region.created_by
     *
     * @param createdBy the value for oomall_region.created_by
     *
     * @mbg.generated
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oomall_region.create_name
     *
     * @return the value of oomall_region.create_name
     *
     * @mbg.generated
     */
    public String getCreateName() {
        return createName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oomall_region.create_name
     *
     * @param createName the value for oomall_region.create_name
     *
     * @mbg.generated
     */
    public void setCreateName(String createName) {
        this.createName = createName == null ? null : createName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oomall_region.modified_by
     *
     * @return the value of oomall_region.modified_by
     *
     * @mbg.generated
     */
    public Long getModifiedBy() {
        return modifiedBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oomall_region.modified_by
     *
     * @param modifiedBy the value for oomall_region.modified_by
     *
     * @mbg.generated
     */
    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oomall_region.modi_name
     *
     * @return the value of oomall_region.modi_name
     *
     * @mbg.generated
     */
    public String getModiName() {
        return modiName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oomall_region.modi_name
     *
     * @param modiName the value for oomall_region.modi_name
     *
     * @mbg.generated
     */
    public void setModiName(String modiName) {
        this.modiName = modiName == null ? null : modiName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oomall_region.gmt_create
     *
     * @return the value of oomall_region.gmt_create
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oomall_region.gmt_create
     *
     * @param gmtCreate the value for oomall_region.gmt_create
     *
     * @mbg.generated
     */
    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oomall_region.gmt_modified
     *
     * @return the value of oomall_region.gmt_modified
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oomall_region.gmt_modified
     *
     * @param gmtModified the value for oomall_region.gmt_modified
     *
     * @mbg.generated
     */
    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }
}