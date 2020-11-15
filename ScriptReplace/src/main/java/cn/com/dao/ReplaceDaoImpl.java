package cn.com.dao;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.lang.StringUtils;

import cn.com.entity.ConfigurationEntity;
import cn.com.entity.CopyScriptEntity;
import cn.com.entity.LogEntity;
import cn.com.utils.JDBCUtils;

public class ReplaceDaoImpl implements ReplaceDao {

    private QueryRunner qr = new QueryRunner(JDBCUtils.getDataSource());

    //获取输入要修改的数据库的内容
    @Override
    public List<ConfigurationEntity> getData() throws SQLException {


        String sql = "select * from bdpms62.replace_jobScript where create_time =curdate() and copyScript is null";


        List<ConfigurationEntity> query = qr.query(sql, new BeanListHandler<ConfigurationEntity>(ConfigurationEntity.class));


        return query;

    }

    //从生产库中得到得到脚本
    @Override
    public String getScript(int id) throws SQLException {
        String oldScript = null;

        String sql = "select script from bdpms62.t_bdpms_job where id = " + id + "";

        Map<String, Object> map = qr.query(sql, new MapHandler());
        if (map != null) {
            for (Object value : map.values()) {
                oldScript = (String) value;
            }
        } else
            return null;


        return oldScript;
    }

    @Override
    public String getFileScript(int id) throws SQLException {
        String oldFileScript = null;

        String sql = "select content from bdpms62.t_bdpms_file  where id = " + id + "";

        Map<String, Object> map = qr.query(sql, new MapHandler());

        if (map != null) {
            for (Object value : map.values()) {
                oldFileScript = (String) value;
            }
        } else
            return null;


        return oldFileScript;
    }


    //把修改后的脚本给写到对应的数据中
    @Override
    public void writeScipt(String newScript, int id, String flag) throws SQLException {
        if (flag.equals("t_bdpms_job")) {
            //更改bdpms.t_bdpms_job表的信息
            String sql = "update bdpms62.t_bdpms_job set script = ? where id = ?";
            qr.update(sql, newScript, id);
        } else if (flag.equals("t_bdpms_file")) {
            //更改bdpms.t_bdpms_file表的信息
            String sql1 = "update bdpms62.t_bdpms_file set content = ? where id = ?";
            qr.update(sql1, newScript, id);
        }

    }


    //得到所有的需要修改的脚本的依赖id
    @Override
    public List<Integer> getDependId(int Oldjobid_in) throws SQLException {

        String sql = "select aa.id from(\r\n" +
                "SELECT id,tgtobjid,tgtobjtypecode,statusCode FROM bdpms62.t_bdpms_job WHERE\r\n" +
                "statusCode  in(2,3) and id in (\r\n" +
                "select jobId from(\r\n" +
                "SELECT id,tgtobjid,tgtobjtypecode FROM bdpms62.t_bdpms_job WHERE id = ? and statusCode in(2,3))as a\r\n" +
                "LEFT JOIN bdpms62.t_bdpms_job_dependobj AS b\r\n" +
                "ON a.tgtObjID = b.dependObjID\r\n" +
                "AND a.tgtObjTypeCode = b.dependObjTypeCode))aa";
        List<Integer> list = new ArrayList<Integer>();
        List<Object[]> query = qr.query(sql, new ArrayListHandler(), Oldjobid_in);
        if (query != null) {
            for (Object[] objs : query) {
                String string = Arrays.toString(objs);

                Integer parseInt = Integer.parseInt(StringUtils.remove(StringUtils.remove(string, "["), "]"));
                list.add(parseInt);
            }
        } else
            return null;


        return list;
    }

    //从 t_bdpms_job_dependobj 表中得到正在跑的脚本的jobID
    public List<Integer> getDependId(int Oldjobid_in, int job_id) throws SQLException {
        String sql = "select aa.id from(\r\n" +
                "SELECT id,tgtobjid,tgtobjtypecode,statusCode FROM bdpms62.t_bdpms_job WHERE\r\n" +
                "statusCode in(2,3) and id in (\r\n" +
                "select jobId from(\r\n" +
                "SELECT id,tgtobjid,tgtobjtypecode FROM bdpms62.t_bdpms_job WHERE id = ? and statusCode in(2,3))as a\r\n" +
                "LEFT JOIN bdpms62.t_bdpms_job_dependobj AS b\r\n" +
                "ON b.jobid =? and a.tgtObjID = b.dependObjID\r\n" +
                "AND a.tgtObjTypeCode = b.dependObjTypeCode\r\n" +
                "))aa";
        List<Integer> list = new ArrayList<Integer>();
        List<Object[]> query = qr.query(sql, new ArrayListHandler(), Oldjobid_in, job_id);
        if (query != null) {
            for (Object[] objs : query) {
                String string = Arrays.toString(objs);

                Integer parseInt = Integer.parseInt(StringUtils.remove(StringUtils.remove(string, "["), "]"));
                list.add(parseInt);
            }
        } else
            return null;


        return list;

    }


    //复制脚本
    @Override
    public void copyScript(String script, Integer scriptId) throws SQLException {
        String sql = "insert into bdpms62.replace_jobScript(oldjobid_in,newjobid_in,copyScript,copyScriptId,create_time) "
                + "values(0,0,?,?,now())";
        qr.update(sql, script, scriptId);

    }

    //得到复制的脚本
    @Override
    public String getCopyScript(Integer copyScriptId) throws SQLException {
        String oldScript = null;
        String sql = null;

        //得到job表的原脚本
        sql = "select id, copyScript from bdpms62.replace_jobScript where copyScriptId = " + copyScriptId + " "
                + "and id  in(select max(id)-1 from bdpms62.replace_jobScript)";

        CopyScriptEntity copyScript = qr.query(sql, new BeanHandler<CopyScriptEntity>(CopyScriptEntity.class));

        if (qr != null)
            return copyScript.getCopyScript();
        else
            return null;
    }

    @Override
    public Integer writeLog(LogEntity log) throws SQLException {
        String sql = null;
        Integer taskID = 0;
        // 得到taskID
        if (log != null && log.getWhich_step() != null && log.getWhich_step().equals("1")) {//如果是第一步，就插入一条没有日志的信息
            // 只创建一条空的数据
            sql = "insert into bdpms62.log_project(which_step) values(?)";
            qr.update(sql, log.getWhich_step());
            return 0;

        } else {//插入日志信息
            //得到taskID
            sql = "select id,taskid from bdpms62.log_project a where id in\r\n" +
                    "(select max(id) from bdpms62.log_project )";
            LogEntity tableBean = qr.query(sql, new BeanHandler<LogEntity>(LogEntity.class));
            if (tableBean != null && tableBean.getTaskId() != null) {
                taskID = tableBean.getTaskId();
                sql = "insert into bdpms62.log_project(taskId,which_step,jobId,curScript,log,update_time)"
                        + " values(?,?,?,?,?,now())";
                qr.update(sql, taskID, log.getWhich_step(), log.getJobId(), log.getCurScript(), log.getLog());
            } else { //taskID为null说明是任务的第一次插入数据
                taskID = tableBean.getId();
                sql = "insert into bdpms62.log_project(taskId,which_step,jobId,curScript,log,update_time)"
                        + " values(?,?,?,?,?,now())";
                qr.update(sql, taskID, log.getWhich_step(), log.getJobId(), log.getCurScript(), log.getLog());
                return taskID;
            }

            return taskID;
        }


    }

    //修改依赖
    @Override
    public void alterDepend(Integer oldjobid_in, Integer newjobid_in, Integer globalDependId) throws SQLException {
        Integer oldJobDependId = null;
        Integer newJobDependId = null;

        //得到job表的原脚本
        String sql1 = "SELECT  DISTINCT b.dependObjID FROM (\r\n" +
                "SELECT id,tgtobjid,statuscode,tgtobjtypecode FROM bdpms62.t_bdpms_job WHERE id = ? and statusCode in(2,3)) AS a \r\n" +
                "LEFT JOIN bdpms62.t_bdpms_job_dependobj AS b\r\n" +
                "ON a.tgtObjID = b.dependObjID\r\n" +
                "and a.tgtObjTypeCode = b.dependObjTypeCode";
        Object[] query = qr.query(sql1, new ArrayHandler(), oldjobid_in);
        for (Object id : query) {
            oldJobDependId = Integer.parseInt(id.toString());
        }

        //得到job表的原脚本
        String sql2 = "SELECT  DISTINCT b.dependObjID FROM (\r\n" +
                "SELECT id,tgtobjid,statuscode,tgtobjtypecode FROM bdpms62.t_bdpms_job WHERE id = ? and statusCode in(2,3)) AS a \r\n" +
                "LEFT JOIN bdpms62.t_bdpms_job_dependobj AS b\r\n" +
                "ON a.tgtObjID = b.dependObjID\r\n" +
                "and a.tgtObjTypeCode = b.dependObjTypeCode";
        Object[] query1 = qr.query(sql1, new ArrayHandler(), newjobid_in);
        for (Object id : query1) {
            newJobDependId = Integer.parseInt(id.toString());
        }

        String sql3 = "UPDATE \r\n" +
                "bdpms62.t_bdpms_job_dependobj\r\n" +
                "SET dependObjID = ?, \r\n" +
                "    updatedat = now(),\r\n" +
                "	  updatedby = 13 \r\n" +
                "where ? is not null and dependObjID = ?  and jobid in(\r\n" +
                "select aa.id from(\r\n" +
                "SELECT id,tgtobjid,tgtobjtypecode,statusCode FROM bdpms62.t_bdpms_job WHERE\r\n" +
                "statusCode in(2,3) and id in (\r\n" +
                "select jobId from(\r\n" +
                "SELECT id,tgtobjid,tgtobjtypecode FROM bdpms62.t_bdpms_job WHERE id = ? and statusCode in(2,3))as a\r\n" +
                "LEFT JOIN bdpms62.t_bdpms_job_dependobj AS b\r\n" +
                "ON a.tgtObjID = b.dependObjID\r\n" +
                "AND a.tgtObjTypeCode = b.dependObjTypeCode))aa\r\n" +
                ") and jobID = ?;";
        qr.update(sql3, newJobDependId, newJobDependId, oldJobDependId, oldjobid_in, globalDependId);


    }

}














