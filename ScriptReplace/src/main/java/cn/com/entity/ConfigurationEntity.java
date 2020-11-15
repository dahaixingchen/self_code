package cn.com.entity;

/**
 * @author admin
 */
public class ConfigurationEntity {
    private String id;
    private String old_db;
    private String new_db;
    private String old_table;
    private String new_table;
    private Integer oldjobid_in;
    private Integer newjobid_in;
    private String copyScript;
    private Integer copyScriptId;
    private String create_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOld_db() {
        return old_db;
    }

    public void setOld_db(String old_db) {
        this.old_db = old_db;
    }

    public String getNew_db() {
        return new_db;
    }

    public void setNew_db(String new_db) {
        this.new_db = new_db;
    }

    public String getOld_table() {
        return old_table;
    }

    public void setOld_table(String old_table) {
        this.old_table = old_table;
    }

    public String getNew_table() {
        return new_table;
    }

    public void setNew_table(String new_table) {
        this.new_table = new_table;
    }

    public Integer getOldjobid_in() {
        return oldjobid_in;
    }

    public void setOldjobid_in(Integer oldjobid_in) {
        this.oldjobid_in = oldjobid_in;
    }

    public Integer getNewjobid_in() {
        return newjobid_in;
    }

    public void setNewjobid_in(Integer newjobid_in) {
        this.newjobid_in = newjobid_in;
    }

    public String getCopyScript() {
        return copyScript;
    }

    public void setCopyScript(String copyScript) {
        this.copyScript = copyScript;
    }

    public Integer getCopyScriptId() {
        return copyScriptId;
    }

    public void setCopyScriptId(Integer copyScriptId) {
        this.copyScriptId = copyScriptId;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }


}
