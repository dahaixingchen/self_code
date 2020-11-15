package cn.com.entity;

public class LogEntity {
    private Integer id;
    private Integer taskId;
    private String which_step;
    private Integer jobId;
    private String curScript;
    private String log;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getWhich_step() {
        return which_step;
    }

    public void setWhich_step(String which_step) {
        this.which_step = which_step;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getCurScript() {
        return curScript;
    }

    public void setCurScript(String curScript) {
        this.curScript = curScript;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }


}
