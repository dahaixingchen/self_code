package cn.com.app;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore.Processor.list_privileges;

import cn.com.entity.ConfigurationEntity;
import cn.com.entity.ExplainHiveEntity;
import cn.com.entity.LogEntity;
import cn.com.service.ReplaceService;
import cn.com.service.ReplaceServiceImpl;

public class ReplaceAPP {
    private static ReplaceService rs = new ReplaceServiceImpl();
    private static LogEntity logEntity = new LogEntity();


    public static void main(String[] args) {
        Integer globalDependId = 0;
        String globalScript = null;
        ArrayList list = new ArrayList();
        int count = 0;
        int count1 = 0;

        String whichStep;
        try {
            // 到mysql中读取要修改的数据
            List<ConfigurationEntity> alterDatas = rs.getData();

            if (alterDatas.size() != 0) {
                for (ConfigurationEntity alterData : alterDatas) {
                    count++;
                    // 得到需要修改的老脚本的id
                    List<Integer> dependIds = rs.getDependId(alterData.getOldjobid_in());
                    System.out.println("------------------------------------------------------------------------");
                    System.out.println("您需要替换的第" + count + "任务正在运行，请稍等。");

                    if (count > 50) {
                        System.out.println("单次执行的任务不能超过50个任务。");
                        break;
                    }
                    System.out.println(
                            "新任务id为" + alterData.getNewjobid_in() + "的任务对应有" + (dependIds.size()) + "个依赖脚本需要替换\n");
                    for (Integer dependId : dependIds) {
                        System.out.println("------------------------------------------------------------------------");
                        count1++; //控制循环不能超过一定的次数
                        if (count > 100) {
                            System.out.println("单次依赖的任务不能超出100个");
                            break;
                        }
                        globalDependId = dependId;

                        // 得到job表的脚本
                        String oldScript = rs.getScript(dependId);

                        //写入任务的第一次日志
                        whichStep = "1";
                        logEntity.setWhich_step(whichStep);
                        logEntity.setJobId(globalDependId);
                        logEntity.setCurScript(oldScript);
                        logEntity.setLog("t_bdpms_job表的原脚本");
                        rs.writeLog(logEntity);
                        list = replaceing(oldScript, globalDependId, dependIds, "t_bdpms_job", alterData);
                        if ((boolean) list.toArray()[2]) {//说明t_bdpms_job表脚本能替换成功
                            //得到file表的脚本
                            String oldFileScript = rs.getFileScript(dependId);

                            logEntity.setWhich_step("t_bdpms_file表的原脚本的信息");
                            logEntity.setJobId(globalDependId);
                            logEntity.setCurScript(oldFileScript);
                            logEntity.setLog("t_bdpms_file表的原脚本");
                            ArrayList list2 = replaceing(oldFileScript, globalDependId, dependIds, "t_bdpms_file", alterData);
                            if ((boolean) list2.toArray()[2])
                                System.out.println("=====================恭喜您当前任务的脚本依赖修改成功=====================");
                            else
                                System.out.println("任务对应的脚本替换失败");
                        } else {
                            System.out.println("任务脚本替换失败，程序结束");
                        }


                        System.out.println("您可以到bdpms62.log_project表中查到对应的日志，对应的taskId字段为：" + list.toArray()[1].toString());
                        System.out.println("------------------------------------------------------------------------");
                    } // for循环

                }
            } else {
                System.out.println("取不到需要修改的数据，请检查当前插入replace_jobscript表的数据");
            }

        } catch (SQLException e) {// sql语句异常
            e.printStackTrace();

            //一般只回写job表中的数据
            if (list.toArray()[0].equals("t_bdpms_file")) {
                try {
                    // 当file脚本不能执行脚本就数据回滚
                    String jobCopyScript = rs.getCopyScript(globalDependId);
                    rs.returnData(jobCopyScript, globalDependId, "t_bdpms_job");
                } catch (SQLException e1) {

                    e1.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

            //一般只回写job表中的数据
            if (list.toArray()[0].equals("t_bdpms_file")) {
                try {
                    // 当file脚本不能执行脚本就数据回滚
                    String jobCopyScript = rs.getCopyScript(globalDependId);
                    rs.returnData(jobCopyScript, globalDependId, "t_bdpms_job");
                } catch (SQLException e1) {

                    e1.printStackTrace();
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();

            //一般只回写job表中的数据
            if (list.toArray()[0].equals("t_bdpms_file")) {
                try {
                    // 当file脚本不能执行脚本就数据回滚
                    String jobCopyScript = rs.getCopyScript(globalDependId);
                    rs.returnData(jobCopyScript, globalDependId, "t_bdpms_job");
                } catch (SQLException e1) {

                    e1.printStackTrace();
                }
            }
        }

    }

    //辅助方法，原脚本不用跑通就可以替换
    public static ArrayList replaceing(String oldScript, int globalDependId, List<Integer> dependIds, String flag, ConfigurationEntity alterData) throws SQLException, IOException, InterruptedException {
        ArrayList list = new ArrayList();
        list.add(flag);

        String whichStep = flag + "表中原脚本的数据";
        logEntity.setWhich_step(whichStep);
        logEntity.setJobId(globalDependId);
        logEntity.setCurScript(oldScript);
        logEntity.setLog(flag + "原脚本的数据");
        Integer taskId = rs.writeLog(logEntity);
        list.add(taskId);

        if (oldScript == null) {
            list.add(false);
            System.out.println("您表：" + flag + "，id为" + globalDependId + "的原脚本为null");
            return list;
        } else if (oldScript.trim().equals("")) {
            list.add(false);
            System.out.println("您表：" + flag + "，id为" + globalDependId + "的原脚本为空");
            return list;
        }

        String globalScript = rs.copyScript(oldScript, globalDependId);

        // 开始替换脚本
        globalScript = rs.ReplaceData(alterData, globalDependId, flag);

        //替换脚本的日志信息
        whichStep = flag + "表的脚本替换后的日志信息";
        logEntity.setWhich_step(whichStep);
        logEntity.setJobId(globalDependId);
        logEntity.setCurScript(globalScript);
        logEntity.setLog(flag + "表中的替换后的脚本");
        rs.writeLog(logEntity);

        // 如果替换后的脚本和替换前的一样说明没有替换-----要求去注释后，对比两个脚本中的所有非空白字符都相等
        String newGlobalScript = rs.removeComment(globalScript, "--", "\n");
        String newOldScript = rs.removeComment(oldScript, "--", "\n");
        String charStr = newOldScript.substring(newOldScript.length() - 1, newOldScript.length());
        if (charStr != null && !charStr.equals(";"))//加上；让他们相等
            newOldScript = newOldScript.trim() + ";";

        if (newGlobalScript != null && StringUtils.deleteWhitespace(newGlobalScript).equals(StringUtils.deleteWhitespace(newOldScript))) {//去除所有的空白字符

            //替换脚本的日志信息
            whichStep = "表: " + flag + " 替换后和替换前一样";
            logEntity.setWhich_step(whichStep);
            logEntity.setJobId(globalDependId);
            logEntity.setCurScript(globalScript);
            logEntity.setLog("您表：" + flag + "，id为" + globalDependId + "没有发生替换，可能是脚本里没有您要替换的数据\n");
            rs.writeLog(logEntity);
            System.out.println("您表：" + flag + "，id为" + globalDependId + "没有发生替换，可能是脚本里没有您要替换的数据\n");
            if (flag != null && flag.equals("t_bdpms_job")) {
                list.add(true);
            } else { //当job替换成功而file表没有发生替换的时候，也需要修改对应的依赖
                list.add(false);
                boolean b = rs.alterDepend(alterData.getOldjobid_in(), alterData.getNewjobid_in(), globalDependId);
                if (b)
                    System.out.println("恭喜您任务的 依赖修改成功。");
                else {
                    System.out.println(flag + " 表依赖修改失败。");
                }
            }
            return list;
        }
        // 正在跑新脚本
        ExplainHiveEntity hiveResult = rs.explainHive(globalScript);
        //ExplainHiveEntity hiveResult = new ExplainHiveEntity();

        //正在跑替换后脚本的日志信息
        whichStep = "替换过的" + flag + "脚本正在跑的日志";
        logEntity.setWhich_step(whichStep);
        logEntity.setJobId(globalDependId);
        logEntity.setCurScript(hiveResult.getScript());
        logEntity.setLog(flag + "表中跑hiveSQL的日志信息： " + hiveResult.getLog());
        rs.writeLog(logEntity);

        if (hiveResult.getFlag() == 0) {
            System.out.println("恭喜您表：" + flag + " 替换后的脚本跑通。");
            //修改依赖并判断依赖是否能修改成功
            if (flag != null && flag.equals("t_bdpms_file")) {//只在跑file表的时候才开始替换脚本
                boolean b = rs.alterDepend(alterData.getOldjobid_in(), alterData.getNewjobid_in(), globalDependId);
                if (b)
                    System.out.println("恭喜您任务的 依赖修改成功。");
                else {
                    System.out.println(flag + " 表依赖修改失败。");
                    System.out.println("正在回滚t_bdpms_job中的数据\n");
                    String jobCopyScript = rs.getCopyScript(globalDependId);
                    rs.returnData(jobCopyScript, globalDependId, "t_bdpms_job");
                    list.add(false);
                    return list;
                }
            }
            // 把新脚本写入对应的数据库中
            rs.writeScipt(globalScript, globalDependId, flag);
            // 修改老任务的依赖关系
            System.out.println("恭喜您表：" + flag + "。id为" + globalDependId + "的脚本替换成功\n");

            list.add(true);  //程序到这里说明成功了
        } else {
            System.out.println("您表：" + flag + "。id为" + globalDependId + "新替换的脚本不能跑通，请检查要替换的新的库名表名的正确性\n");
            System.out.println("您表：" + flag + "。id为" + globalDependId + "的脚本没有做替换工作\n");
            if (flag != null && flag.equals("t_bdpms_file")) { //当替换file表的时候失败才需要把job表中的数据回滚
                System.out.println("正在回滚t_bdpms_job中的数据\n");
                String jobCopyScript = rs.getCopyScript(globalDependId);
                rs.returnData(jobCopyScript, globalDependId, "t_bdpms_job");
            }

            list.add(false);
        }

        return list;
    }
}
