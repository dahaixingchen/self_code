package cn.com.dao;

import java.sql.SQLException;
import java.util.List;

import cn.com.entity.LogEntity;

public interface ReplaceDao {

    /**
     * 读取数据库需要修改的数据
     * 返回符合条件的所有数据
     */
    public List getData() throws SQLException;

    /**
     * 读取数据库job表中的信息
     * 返回String类的脚本内容
     */
    public String getScript(int id) throws SQLException;

    /**
     * 在自建的表中根据dependID得到我们复制的脚本
     */
    public String getCopyScript(Integer copyScriptId) throws SQLException;


    /**
     * 把新修改后的脚本写回到数据库中
     */
    public void writeScipt(String newScript, int id, String flag) throws SQLException;

    /**
     * 得到修改了依赖id的所有对应的脚本id
     */
    public List<Integer> getDependId(int Oldjobid_in) throws SQLException;

    /**
     * 从 t_bdpms_job_dependobj 表中得到正在跑的脚本的jobID
     */
    public List<Integer> getDependId(int Oldjobid_in, int job_id) throws SQLException;


    /**
     * 复制一个脚本插入到需要修改的数据库中
     */
    public void copyScript(String script, Integer scriptId) throws SQLException;

    /**
     * 删除自建表中复制的脚本对应的那条信息
     */
//	public void delectCopyCript(List<Integer> dependIds) throws SQLException;

//	public void delectCopyCript(Integer dependId) throws SQLException;

    /**
     * 填写日志
     */
    public Integer writeLog(LogEntity log) throws SQLException;

    /**
     * 得到file表中的数据
     */
    public String getFileScript(int id) throws SQLException;

    /**
     * 修改依赖
     *
     * @throws SQLException
     */
    public void alterDepend(Integer oldjobid_in, Integer newjobid_in, Integer globalDependId) throws SQLException;


}
