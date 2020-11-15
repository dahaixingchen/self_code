package cn.com.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.com.dao.ReplaceDao;
import cn.com.dao.ReplaceDaoImpl;
import cn.com.entity.ConfigurationEntity;
import cn.com.entity.ExplainHiveEntity;
import cn.com.entity.FlagEntity;
import cn.com.entity.LogEntity;

public class ReplaceServiceImpl implements ReplaceService {

    private static final String TABLE = "table";
    private static final String JOIN = "join";
    private static final String FROM = "from";
    private static final String LIKE = "like";
    private static final String USE = "use";
    private static final String SHOW = "show";
    private static final String EXISTS = "exists";
    private static final String PARTITIONS = "PARTITIONS";
    private static final String UNION = "union";
    private static final String UNIONALL = "all";
    private static final String INTO = "into";
    private static final String WITH = "with";

    private ReplaceDao scriptDao = new ReplaceDaoImpl();

    //得到自建的数据库中修改的信息
    @Override
    public List<ConfigurationEntity> getData() throws SQLException {

        List<ConfigurationEntity> data = (List<ConfigurationEntity>) scriptDao.getData();
        return data;
    }

    //脚本的替换
    @Override
    public String ReplaceData(ConfigurationEntity tableData, Integer dependId, String flag) throws SQLException {
        String oldScript = null;
        String newScript = null;

        //老脚本的id不为空
        if (dependId != 0) {
            //老脚本必须存在
            if (scriptDao.getScript(dependId) != null) {
                //根据不同的情况得到不同的老脚本
                if (flag.equals("t_bdpms_job"))
                    oldScript = scriptDao.getScript(dependId);
                else if (flag.equals("t_bdpms_file"))
                    oldScript = scriptDao.getFileScript(dependId);

                //对修改库名,表名的判断
                if (tableData.getOld_db() != null && !tableData.getOld_db().equals("") && tableData.getOld_db() != null) { //老库名不为空

                    if (tableData.getOld_table() != null && !tableData.getOld_table().equals("")) {//老的表名不为空,说明既修改表名, 又修改库名
                        System.out.println("正在替换您的库名表名。");
                        String oldTableAndDB = tableData.getOld_db() + "." + tableData.getOld_table();
                        String newTableAndDB = tableData.getNew_db() + "." + tableData.getNew_table();

                        newScript = getNewScript(oldScript, oldTableAndDB, newTableAndDB, false);//既修改库名也修改表名


                    } else {//老的库名不为空,老表名为空,说明只改库名
                        System.out.println("正在替换您的库名。");
                        newScript = getNewScript(oldScript, tableData.getOld_db(), tableData.getNew_db(), true);//修改库名

                    }

                } else { //老库名为空
                    if (tableData.getOld_table() != null && !tableData.getOld_table().equals("")) { //老的表名不为空, 说明只改表名
                        System.out.println("正在替换您的表名。");
                        newScript = getNewScript(oldScript, tableData.getOld_table(), tableData.getNew_table(), false);//修改表名

                    } else { //老的库名和表名都为空, 定义为非法数据, 请重新输入
                        System.out.println("自建表的id为:" + tableData.getId() + ",脚本数据对应的id为:" + dependId + "老的库名和表名都为空, 定义为非法数据, 是非法数据, 请重新输入");

                    }
                }
            } else {//脚本不存在,定义为数据是非法的, 给出警告提示
                System.out.println("自建表的id为:" + tableData.getId() + ",脚本数据对应的id为(" + dependId + ")不存在,定义为非法数据, 请重新输入");

            }

        }
        return newScript;
    }

    //测试跑hive语句用
    @Override
    public ExplainHiveEntity explainHive(String sql) throws IOException, InterruptedException {
        Process process = null;
        ExplainHiveEntity hiveEntity = new ExplainHiveEntity();
        String s = "";
        System.out.println("正在测试您的hiveSQL语句的正确性，请稍后：");
        StringBuffer strBu = dealStr(sql);
        if (strBu != null) {
            s = strBu.toString();
            hiveEntity.setScript(s);
        } else {
            hiveEntity.setScript(null);
        }
        File file = new File("./hiveSQL.sql");
        PrintStream ps = new PrintStream(new FileOutputStream(file));
        ps.println(s);// 往文件里写入字符串
        ps.close();

        //执行shell脚本并返回状态值
        String command = "hive -f ./hiveSQL.sql";
        process = Runtime.getRuntime().exec(command);
        int waitFor = process.waitFor();//线程等待命令执行完成，如果执行成功返回0

        StringBuffer dataBuffer = new StringBuffer();
        String data = "";

        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader br1 = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        //标准的输出流
        dataBuffer.append("标准输出流打出的数据。------------------------------------------------------------------\n");
        while ((data = br.readLine()) != null) {

            dataBuffer.append(data + "\n");
        }
        //错误流
        dataBuffer.append("错误输出流打出的数据。------------------------------------------------------------------\n");
        while ((data = br1.readLine()) != null) {
            System.out.println(data);
            dataBuffer.append(data + "\n");
        }

        hiveEntity.setLog(dataBuffer.toString());
        hiveEntity.setFlag(waitFor);
        br.close();
        return hiveEntity;

    }

    //处理脚本，返回能运行的一条条语句
    @Override
    public StringBuffer dealStr(String str) {
        //去除注释中的分号
        StringBuffer strB = new StringBuffer();
        String[] split = null;
        //去掉注释
        str = removeComment(str, "--", "\n");
        if (str != null) {
            split = str.trim().split(";");
        } else
            return null;

        for (String s : split) {
            //修正脚本中$(ct.xxxx)的语法
            //当语句中包含set,use,drop的时候不做处理
            if (StringUtils.startsWithIgnoreCase(s.trim(), "set") || StringUtils.startsWithIgnoreCase(s.trim(), "use")) {
                ;
            } else {
                s = dealWithStr(s);
            }

            //当语句中包含use,drop,create语句的时候就直接执行
            if (StringUtils.startsWithIgnoreCase(s.trim(), "set") || StringUtils.startsWithIgnoreCase(s.trim(), "use") || StringUtils.startsWithIgnoreCase(s.trim(), "drop") || StringUtils.startsWithIgnoreCase(s.trim(), "create")) {
                strB.append(s + ";");
            } else {
                s = "explain " + s + ";";
                strB.append(s);
            }

        }

        return strB;
    }

    //修正SQL的语法
    @Override
    public String dealWithStr(String s) {
        int index = 0;
        int index1 = 0;
        StringBuffer strB = new StringBuffer();

        //得到当前的时间
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);

//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd000000");
        String nowDay = dateFormat.format(calendar.getTime());

        while (index < s.length()) {
            index1 = index;
            index = s.indexOf("$", index1);
            if (index == -1) {
                strB.append(s.substring(index1, s.length()));
                break;
            }

            String subStr = s.substring(index1, index);
            strB.append(subStr);
            int subStrleng = subStr.trim().length();
            //判断最后一个字符是不是“ ' ”或是“ " ”号
            String substring = subStr.trim().substring(subStrleng - 1, subStrleng);

            boolean equals = substring.equals("'");
            if (subStr.trim().substring(subStrleng - 1, subStrleng).equals("'") || subStr.trim().substring(subStrleng - 1, subStrleng).equals("\"")) {

                String ss = extractMessage(s.substring(index, s.length()));
                index = index + ss.length() + 3; //2表示两个括号的字符数
                ss = StringUtils.remove(ss, "'");
                ss = StringUtils.remove(ss, "\"");
                strB.append("$(" + ss + ")");

            } else if (subStr.trim().substring(subStrleng - 1, subStrleng).equals("_")) {
                String ss = extractMessage(s.substring(index, s.length()));
                index = index + ss.length() + 3; //2表示两个括号的字符数
                strB.append(nowDay);
            } else { // $前面一个字符是' 或是" 就不用处理直接跳过
                //要在$前面加上'号
                //得到$后面括号的字符串
                String ss = extractMessage(s.substring(index, s.length()));
                index = index + ss.length() + 3; //2表示两个括号的字符数
                ss = StringUtils.remove(ss, "\'");
                ss = StringUtils.remove(ss, "\"");
                if (ss.contains("format"))
                    strB.append("\'$(" + ss + ")\'");
                else
                    strB.append(nowDay);
            }
        }

        return strB.toString();
    }

    //匹配括号的方法
    @Override
    public String extractMessage(String msg) {

        List<String> list = new ArrayList<String>();
        int start = 0;
        int startFlag = 0;
        int endFlag = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == '(') {
                startFlag++;
                if (startFlag == endFlag + 1) {
                    start = i;
                }
            } else if (msg.charAt(i) == ')') {
                endFlag++;
                if (endFlag == startFlag) {
                    list.add(msg.substring(start + 1, i));
//						return msg.substring(start + 1, i);
                }
            }
        }
        return list.get(0);
    }

    //修改老任务脚本的依赖
    @Override
    public boolean alterDepend(Integer oldjobid_in, Integer newjobid_in, Integer globalDependId) throws SQLException {

        scriptDao.alterDepend(oldjobid_in, newjobid_in, globalDependId);

        //判断语句执行后是否达到我们想要的效果
        List<Integer> dependIds = scriptDao.getDependId(oldjobid_in, globalDependId);
        //如果集合里没有值说明依赖都修改成功了，
        if (dependIds.size() == 0)
            return true;
        return false;


    }

    //如果出错脚本回滚
    @Override //暂时没有实现
    public void returnData(String jobCopyScript, Integer globalDependId, String flag) throws SQLException {
        //得到复制的脚本，根据id
        String returnScript = "";

        //得到刚回滚的脚本
        if (flag != null && flag.equals("t_bdpms_job")) {
            scriptDao.writeScipt(jobCopyScript, globalDependId, flag);
            returnScript = scriptDao.getScript(globalDependId);
            if (returnScript.equals(jobCopyScript)) { //判断是否写成功
                System.out.println("恭喜你数据回滚成功");
            } else {
                System.out.println("数据回滚失败，请到log_project日志表中手动恢复 " + flag + " 的脚本");
            }
        } else {
            System.out.println("无法验证数据回滚的正确性，请到 log_project 日志表中手动恢复 " + flag + " 表中的脚本数据");
        }

    }

    //得到所有的需要修改的脚本的依赖id
    @Override
    public List<Integer> getDependId(int Oldjobid_in) throws SQLException {
        return scriptDao.getDependId(Oldjobid_in);
    }

    //去除脚本的注释
    @Override
    public String removeComment(String str, String str1, String str2) {
        //去掉str1和str2后的新字符串
        while (true) {
            int index1 = StringUtils.indexOfIgnoreCase(str, str1);
            if (index1 != -1) {
                int index2 = StringUtils.indexOfIgnoreCase(str, str2, index1);
                if (index2 != -1) {
                    str = StringUtils.substring(str, 0, index1) + StringUtils.substring(str, index2, str.length());
                } else {
                    return str;
                }
            } else {
                return str;
            }
        }
    }

    //根据id得到脚本
    @Override
    public String getScript(int id) throws SQLException {
        return scriptDao.getScript(id);
    }

    //根据id得到file表中到脚本
    @Override
    public String getFileScript(int id) throws SQLException {

        return scriptDao.getFileScript(id);
    }

    //得到备份在mysql里的脚本
    @Override
    public String getCopyScript(Integer copyScriptId) throws SQLException {
        return scriptDao.getCopyScript(copyScriptId);
    }

    //复制脚本
    public String copyScript(String script, Integer scriptId) throws SQLException {
        scriptDao.copyScript(script, scriptId);
        return script;
    }

    //把脚本写回到job表和file表中
    @Override
    public void writeScipt(String newScript, int id, String flag) throws SQLException {
        scriptDao.writeScipt(newScript, id, flag);

    }

    //日志填写
    public Integer writeLog(LogEntity log) throws SQLException {

        return scriptDao.writeLog(log);
    }

    /*----------------------------------------下面的方法是对SQL脚本中库表表名的替换-------------------------------------------*/
    /*----------------------------------------下面的方法是对SQL脚本中库表表名的替换-------------------------------------------*/

    //得到最终修改好的SQL脚本
    public String getNewScript(String str, String oldTable, String newTable, boolean tableAndDB) {
        StringBuffer sb = new StringBuffer();
        String s = new String();
        if (str == null) {
            sb.append("null");
        } else if (str.trim().equals("")) {
            sb.append("");
        } else {
            String subRangeString = removeSemicolon(str, "--", "\n");  //把注释中的分号给去掉，避免妨碍字符串的切割
            String[] split = subRangeString.trim().split(";"); //把脚本切割成一条一条的SQL语句
            for (String sql : split) {
                s = deal(sql, oldTable, newTable, tableAndDB);
                sb.append(s + ";");
            }
        }
        return sb.toString();
    }


    //能避免SQL的关键字作为库名表名，以及关键字作为一部分的库名表名，以匹配SQL关键字为基础
    public String deal(String sql, String oldTable, String newTable, boolean tableAndDB) {
        sql = sql.replaceAll("\r", ""); //避免不同操作系统的回车换行符不一致问题
        sql = sql.replaceAll("\t", " "); //避免制表符的问题
        ArrayList<String> list = new ArrayList<String>();
        list.add(TABLE);
        list.add(JOIN);
        list.add(FROM);
        list.add(USE);
        list.add(LIKE);
        list.add(EXISTS);
        list.add(SHOW);
        list.add(PARTITIONS);
        list.add(UNION);
        list.add(UNIONALL);
        list.add(INTO);
        list.add(WITH);
        String str = sql;

        for (String keyBoard : list) {
            str = whileUtil(str, keyBoard, oldTable, newTable, tableAndDB);
        }
        return str;
    }

    //一个SQL关键字的全文索引，返回经过这个关键字处理后的结果字符串
    public String whileUtil(String str, String keyboard, String oldTable, String newTable, boolean tableAndDB) {
        int count = 0;
        int flag1 = 0;
        int index1 = 0;
        while (true) {
            count++;
            if (count > 500) {
                System.out.println("脚本的语句太长，超过了500个，请查看下您的脚本是否正确");
                break;
            }
            if (StringUtils.indexOfIgnoreCase(str, keyboard + " ", flag1) != -1 || StringUtils.indexOfIgnoreCase(str, keyboard + "\n", flag1) != -1
                    || StringUtils.indexOfIgnoreCase(str, "--" + " ", flag1) != -1) {
                FlagEntity flagEntity = flagStr(str, keyboard, flag1, index1, oldTable, newTable, tableAndDB);

                flag1 = flagEntity.getFlag();
                str = flagEntity.getStr();
            } else {
                return str;
            }
        }
        return str;
    }


    //根据不同的关键字进行替换
    public FlagEntity flagStr(String str, String keywords, int flag, int index1, String oldTable, String newTable, boolean tableAndDB) {
        int ind1 = StringUtils.indexOfIgnoreCase(str, keywords + " ", flag);
        int ind2 = StringUtils.indexOfIgnoreCase(str, keywords + "\n", flag);
        int ind3 = StringUtils.indexOfIgnoreCase(str, "--", flag);
        int leng = 0;
        FlagEntity flagEntity = new FlagEntity();

        //控制指针的标号
        if (ind1 != -1 && ind2 != -1 && ind3 != -1) { //三个索引都有值
            //脚本中既有注释，也有上面的两种索引情况
            index1 = (ind1 < ind2 ? ind1 : ind2) < ind3 ? (ind1 < ind2 ? ind1 : ind2) : ind3;
            if (index1 == ind1 || index1 == ind2) {   //其他两个索引中的一个

                if (oldTable.contains(".") && !tableAndDB) //表名中带有.的且只是修改表名的情况
                    str = dealStrSuper(str, index1 + keywords.length() + 1, oldTable, newTable);
                else
                    str = dealString(str, index1 + keywords.length() + 1, oldTable, newTable);
                flag = index1 + keywords.length() + 1;
                flagEntity.setFlag(flag);
                flagEntity.setStr(str);
            } else if (index1 == ind3) { //说明只有注释
                index1 = ind3;
                leng = getStrLeng(str, "--", "\n", flag);
                flag = index1 + leng;
                flagEntity.setFlag(flag);
                flagEntity.setStr(str);
            }
        } else if (ind1 != -1 && ind2 != -1 && ind3 == -1) { //脚本中有第一,第二种索引
            index1 = ind1 < ind2 ? ind1 : ind2;
            //找到最小的来进行索引
            if (oldTable.contains(".") && !tableAndDB) //表名中带有.的且只是修改表名的情况
                str = dealStrSuper(str, index1 + keywords.length() + 1, oldTable, newTable);
            else
                str = dealString(str, index1 + keywords.length() + 1, oldTable, newTable);
            flag = index1 + keywords.length() + 1;
            flagEntity.setFlag(flag);
            flagEntity.setStr(str);
        } else if (ind1 != -1 && ind2 == -1 && ind3 != -1) { //脚本中有第一，第三种索引
            index1 = ind1 < ind3 ? ind1 : ind3;
            if (index1 == ind1) { //第一种索引更小
                if (oldTable.contains(".") && !tableAndDB) //表名中带有.的且只是修改表名的情况
                    str = dealStrSuper(str, index1 + keywords.length() + 1, oldTable, newTable);
                else
                    str = dealString(str, index1 + keywords.length() + 1, oldTable, newTable);
                flag = index1 + keywords.length() + 1;
                flagEntity.setFlag(flag);
                flagEntity.setStr(str);
            } else if (index1 == ind3) {
                leng = getStrLeng(str, "--", "\n", flag);
                flag = index1 + leng;
                flagEntity.setFlag(flag);
                flagEntity.setStr(str);
            }

        } else if (ind1 == -1 && ind2 != -1 && ind3 != -1) {//脚本中第二，第三种索引
            index1 = ind2 < ind3 ? ind2 : ind3;
            if (index1 == ind2) { //第一种索引更小
                if (oldTable.contains(".") && !tableAndDB) //表名中带有.的且只是修改表名的情况
                    str = dealStrSuper(str, index1 + keywords.length() + 1, oldTable, newTable);
                else
                    str = dealString(str, index1 + keywords.length() + 1, oldTable, newTable);
                flag = index1 + keywords.length() + 1;
                flagEntity.setFlag(flag);
                flagEntity.setStr(str);
            } else if (index1 == ind3) {
                leng = getStrLeng(str, "--", "\n", flag);
                flag = index1 + leng;
                flagEntity.setFlag(flag);
                flagEntity.setStr(str);
            }

        } else if (ind1 != -1 && ind2 == -1 && ind3 == -1) {//脚本中第一种索引
            index1 = ind1;
            if (oldTable.contains(".") && !tableAndDB) //表名中带有.的且只是修改表名的情况
                str = dealStrSuper(str, index1 + keywords.length() + 1, oldTable, newTable);
            else
                str = dealString(str, index1 + keywords.length() + 1, oldTable, newTable);
            flag = index1 + keywords.length() + 1;
            flagEntity.setFlag(flag);
            flagEntity.setStr(str);
        } else if (ind1 == -1 && ind2 != -1 && ind3 == -1) {//脚本中第二种索引
            index1 = ind2;
            if (oldTable.contains(".") && !tableAndDB) //表名中带有.的且只是修改表名的情况
                str = dealStrSuper(str, index1 + keywords.length() + 1, oldTable, newTable);
            else
                str = dealString(str, index1 + keywords.length() + 1, oldTable, newTable);
            flag = index1 + keywords.length() + 1;
            flagEntity.setFlag(flag);
            flagEntity.setStr(str);
        } else if (ind1 == -1 && ind2 == -1 && ind3 != -1) {//脚本中第三种索引
            index1 = ind3;
            leng = getStrLeng(str, "--", "\n", flag);
            flag = index1 + leng;
            flagEntity.setFlag(flag);
            flagEntity.setStr(str);
        } else {
            return flagEntity;
        }
        return flagEntity;
    }

    //不完善，要把表名的那个逻辑单独独立出来这样才行
    private String dealStrin(String str, int index1, String oldData, String newData) {
        //把字符串截取出来用来替换
        String substr = StringUtils.substring(str, index1).trim(); //去除字符串前后的空格和换行符
        String substr2 = getTableOrDB(substr, "", " ");
        String substr1 = getTableOrDB(substr, "", "\n");
        String substr3 = getTableOrDB(substr, "", "("); //直接跟在表名后面的可能会有"("号
        String substr4 = getTableOrDB(substr, "", ")"); //直接跟在表名后面的可能会有"("号
        String substr5 = getTableOrDB(substr, "", "-"); //直接跟在表名后面的可能会有"-"号
        String[] strings = null;

        boolean a = !substr5.isEmpty();
        boolean b = StringUtils.containsIgnoreCase(oldData.trim(), substr5);

        String replaceStr = null;
        //得到表名前的非空字符的长度（注意一定要包括前后的空格的）
        int leng = strLeng(StringUtils.substring(str, index1));

        if (!substr2.isEmpty() && (StringUtils.containsIgnoreCase(oldData.trim(), substr2) || StringUtils.containsIgnoreCase(substr2, oldData.trim()))) {  //包含要替换的数据
            if (StringUtils.equalsIgnoreCase(oldData.trim(), substr2)) { //表示要替换关键字后面全部的东西

                str = returnDBTableStr(str, oldData, newData, substr2, substr2, index1, leng);
            } else { //只匹配到一部分（包括库名，表名，或是其中的几个字符）
                //切割字符串，
                strings = substr2.split("\\.");
                if (strings.length == 1) { //库名.表名 的写法，要不匹配库名，要不匹配表名
                    if (StringUtils.equalsIgnoreCase(oldData.trim(), strings[0].trim())) { //匹配到的是库名

                        str = returnDBStr(str, oldData, newData, strings[0], substr2, strings[1], index1, leng);
                    }
                }
            }
        } else if (!substr1.isEmpty() && (StringUtils.containsIgnoreCase(oldData.trim(), substr1) || StringUtils.containsIgnoreCase(substr1, oldData.trim()))) {
            if (StringUtils.equalsIgnoreCase(oldData.trim(), substr1)) { //表示要替换关键字后面全部的东西

                str = returnDBTableStr(str, oldData, newData, substr1, substr1, index1, leng);
            } else { //只匹配到一部分（包括库名，表名，或是其中的几个字符）
                //切割字符串，
                strings = substr1.split("\\.");
                if (strings.length == 2) { //库名.表名 的写法，要不匹配库名，要不匹配表名
                    if (StringUtils.equalsIgnoreCase(oldData.trim(), strings[0].trim())) { //匹配到的是库名

                        str = returnDBStr(str, oldData, newData, strings[0], substr1, strings[1], index1, leng);
                    } else if (StringUtils.equalsIgnoreCase(oldData.trim(), strings[1].trim())) {//匹配到的是表名

                        str = returnTableStr(str, oldData, newData, strings[1], substr1, strings[0], index1, leng);
                    }
                }
            }
        } else if (!substr3.isEmpty() && (StringUtils.containsIgnoreCase(oldData.trim(), substr3) || StringUtils.containsIgnoreCase(substr3, oldData.trim()))) {
            if (StringUtils.equalsIgnoreCase(oldData.trim(), substr3)) { //表示要替换关键字后面全部的东西

                str = returnDBTableStr(str, oldData, newData, substr3, substr3, index1, leng);
            } else { //只匹配到一部分（包括库名，表名，或是其中的几个字符）
                //切割字符串，
                strings = substr3.split("\\.");
                if (strings.length == 2) { //库名.表名 的写法，要不匹配库名，要不匹配表名
                    if (StringUtils.equalsIgnoreCase(oldData.trim(), strings[0].trim())) { //匹配到的是库名

                        str = returnDBStr(str, oldData, newData, strings[0], substr3, strings[1], index1, leng);
                    } else if (StringUtils.equalsIgnoreCase(oldData.trim(), strings[1].trim())) {//匹配到的是表名

                        str = returnTableStr(str, oldData, newData, strings[1], substr3, strings[0], index1, leng);
                    }
                }
            }
        } else if (!substr4.isEmpty() && (StringUtils.containsIgnoreCase(oldData.trim(), substr4) || StringUtils.containsIgnoreCase(substr4, oldData.trim()))) {
            if (StringUtils.equalsIgnoreCase(oldData.trim(), substr4)) { //表示要替换关键字后面全部的东西

                str = returnDBTableStr(str, oldData, newData, substr4, substr4, index1, leng);
            } else { //只匹配到一部分（包括库名，表名，或是其中的几个字符）
                //切割字符串，
                strings = substr4.split("\\.");
                if (strings.length == 2) { //库名.表名 的写法，要不匹配库名，要不匹配表名
                    if (StringUtils.equalsIgnoreCase(oldData.trim(), strings[0].trim())) { //匹配到的是库名

                        str = returnDBStr(str, oldData, newData, strings[0], substr4, strings[1], index1, leng);
                    } else if (StringUtils.equalsIgnoreCase(oldData.trim(), strings[1].trim())) {//匹配到的是表名

                        str = returnTableStr(str, oldData, newData, strings[1], substr4, strings[0], index1, leng);
                    }
                }
            }
        } else if (!substr5.isEmpty() && (StringUtils.containsIgnoreCase(oldData.trim(), substr5) || StringUtils.containsIgnoreCase(substr5, oldData.trim()))) {
            if (StringUtils.equalsIgnoreCase(oldData.trim(), substr5)) { //表示要替换关键字后面全部的东西

                str = returnDBTableStr(str, oldData, newData, substr1, substr1, index1, leng);
            } else { //只匹配到一部分（包括库名，表名，或是其中的几个字符）
                //切割字符串，
                strings = substr5.split("\\.");
                if (strings.length == 2) { //库名.表名 的写法，要不匹配库名，要不匹配表名
                    if (StringUtils.equalsIgnoreCase(oldData.trim(), strings[0].trim())) { //匹配到的是库名

                        str = returnDBStr(str, oldData, newData, strings[0], substr5, strings[1], index1, leng);
                    } else if (StringUtils.equalsIgnoreCase(oldData.trim(), strings[1].trim())) {//匹配到的是表名

                        str = returnTableStr(str, oldData, newData, strings[1], substr5, strings[0], index1, leng);
                    }
                }
            }
        } else if (!substr.isEmpty() && (StringUtils.containsIgnoreCase(oldData.trim(), substr) || StringUtils.containsIgnoreCase(substr, oldData.trim()))) {
            if (StringUtils.equalsIgnoreCase(oldData.trim(), substr1)) { //表示要替换关键字后面全部的东西

                replaceStr = substr.trim().replaceFirst(("(?i)" + oldData).trim(), newData);
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + substr.length() + 1);
            } else { //只匹配到一部分（包括库名，表名，或是其中的几个字符）
                //切割字符串，
                strings = substr2.split("\\.");
                if (strings.length == 2) { //库名.表名 的写法，要不匹配库名，要不匹配表名
                    if (StringUtils.equalsIgnoreCase(oldData.trim(), strings[0].trim())) { //匹配到的是库名

                        replaceStr = strings[0].trim().replaceFirst(("(?i)" + oldData).trim(), newData);
                        str = StringUtils.substring(str, 0, index1 + leng) + replaceStr + "." + strings[1] +   //另外还需要加上后面的表名
                                StringUtils.substring(str, index1 + substr.length() + 1);

                    } else if (StringUtils.equalsIgnoreCase(oldData.trim(), strings[1].trim())) {//匹配到的是表名

                        replaceStr = strings[1].trim().replaceFirst(("(?i)" + oldData).trim(), newData);
                        str = StringUtils.substring(str, 0, index1 + leng) + strings[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                                StringUtils.substring(str, index1 + substr.length() + 1);
                    }
                }
            }
        }

        return str;
    }

    //修改库名，表名一起的
    public String returnDBTableStr(String str, String oldData, String newData, String strings, String s, int index1, int length) {

        String replaceStr = strings.trim().replaceFirst(("(?i)" + oldData).trim(), newData);
        return str = StringUtils.substring(str, 0, index1 + length) + replaceStr +
                StringUtils.substring(str, index1 + length + s.length());
    }

    //修改库名
    public String returnDBStr(String str, String oldData, String newData, String strings, String s, String s1, int index1, int length) {

        String replaceStr = strings.trim().replaceFirst(("(?i)" + oldData).trim(), newData);
        return str = StringUtils.substring(str, 0, index1 + length) + replaceStr + "." + s1 +   //另外还需要加上后面的表名
                StringUtils.substring(str, index1 + length + s.length());
    }


    //修改表名
    public String returnTableStr(String str, String oldData, String newData, String strings, String s, String s1, int index1, int length) {
        String replaceStr = strings.trim().replaceFirst(("(?i)" + oldData).trim(), newData);

        return str = StringUtils.substring(str, 0, index1 + length) + s1 + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                StringUtils.substring(str, index1 + length + s.length());
    }


    //字符串的处理
    public String dealString(String str, int index1, String oldTable, String newTable) {
        //不能解决单修改表名的时候表明中带点号，

        //把字符串截取出来用来替换
        String substr = StringUtils.substring(str, index1).trim(); //去除字符串前后的空格和换行符
        String[] strings = substr.split("\\.");

        String substr1 = getTableOrDB(substr, "", " ");
        String[] strings1 = substr1.split("\\.");

        String substr2 = getTableOrDB(substr, "", "\n");
        String[] strings2 = substr2.split("\\.");

        String substr3 = getTableOrDB(substr, "", "("); //直接跟在表名后面的可能会有"("号
        String[] strings3 = substr3.split("\\.");

        String substr4 = getTableOrDB(substr, "", ")"); //直接跟在表名后面的可能会有"("号
        String[] strings4 = substr4.split("\\.");

        String substr5 = getTableOrDB(substr, "", "-"); //直接跟在表名后面的可能会有"("号
        String[] strings5 = substr5.split("\\.");

        String replaceStr = null;
        //得到表名前的非空字符的长度（注意一定要包括前后的空格的）
        int leng = strLeng(StringUtils.substring(str, index1));

        //判断两个字符串那个是需要修改的
        if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr2)) {  //库名表名同时替换的
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr2 = substr2.toUpperCase();

            replaceStr = substr2.trim().replace(oldTable.trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +
                    StringUtils.substring(str, index1 + leng + substr2.length());

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr1)) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr1 = substr1.toUpperCase();

            replaceStr = substr1.trim().replace((oldTable).trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                    StringUtils.substring(str, index1 + leng + substr1.length());

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr3)) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr3 = substr3.toUpperCase();

            replaceStr = substr3.trim().replace((oldTable).trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                    StringUtils.substring(str, index1 + leng + substr3.length());

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr4)) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr4 = substr4.toUpperCase();

            replaceStr = substr4.trim().replace((oldTable).trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                    StringUtils.substring(str, index1 + leng + substr4.length());

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr5)) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr5 = substr5.toUpperCase();

            replaceStr = substr5.trim().replace((oldTable).trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                    StringUtils.substring(str, index1 + leng + substr5.length());

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr)) { //这是最后的一个字符串也表示这个也是
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr = substr.toUpperCase();

            replaceStr = substr.trim().replace(oldTable.trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                    StringUtils.substring(str, index1 + substr.length() + 1);
        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), strings2[0]) && strings2.length <= 2) { //只是替换库名的
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            strings2[0] = strings2[0].toUpperCase();

            replaceStr = strings2[0].trim().replace((oldTable).trim(), newTable);
            if (strings2.length == 2) {//这种表示在修改库名的时候它候命又带有表名
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr + "." + strings2[1] +   //另外还需要加上后面的表名
                        StringUtils.substring(str, index1 + leng + substr2.length());
            } else if (strings2.length == 1) {//这种表示只有单单一个库名,比如ues的用法
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr2.length());
            }

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), strings1[0]) && strings1.length <= 2) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            strings1[0] = strings1[0].toUpperCase();

            replaceStr = strings1[0].trim().replace((oldTable).trim(), newTable);
            if (strings1.length == 2) {//这种表示在修改库名的时候它候命又带有表名
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr + "." + strings1[1] +   //另外还需要加上后面的表名
                        StringUtils.substring(str, index1 + leng + substr1.length());
            } else if (strings1.length == 1) {//这种表示只有单单一个库名,比如ues的用法
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr1.length());
            }

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), strings3[0]) && strings3.length <= 2) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            strings3[0] = strings3[0].toUpperCase();

            replaceStr = strings3[0].trim().replace((oldTable).trim(), newTable);
            if (strings3.length == 2) {//这种表示在修改库名的时候它候命又带有表名
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr + "." + strings3[1] +   //另外还需要加上后面的表名
                        StringUtils.substring(str, index1 + leng + substr3.length());
            } else if (strings3.length == 1) {//这种表示只有单单一个库名,比如ues的用法
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr3.length());
            }

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), strings4[0]) && strings4.length <= 2) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            strings4[0] = strings4[0].toUpperCase();

            replaceStr = strings4[0].trim().replace((oldTable).trim(), newTable);
            if (strings4.length == 2) {//这种表示在修改库名的时候它候命又带有表名
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr + "." + strings4[1] +   //另外还需要加上后面的表名
                        StringUtils.substring(str, index1 + leng + substr4.length());
            } else if (strings4.length == 1) {//这种表示只有单单一个库名,比如ues的用法
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr4.length());
            }

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), strings5[0]) && strings5.length <= 2) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            strings5[0] = strings5[0].toUpperCase();

            replaceStr = strings5[0].trim().replace((oldTable).trim(), newTable);
            if (strings5.length == 2) {//这种表示在修改库名的时候它候命又带有表名
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr + "." + strings5[1] +   //另外还需要加上后面的表名
                        StringUtils.substring(str, index1 + leng + substr5.length());
            } else if (strings5.length == 1) {//这种表示只有单单一个库名,比如ues的用法
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr5.length());
            }

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), strings[0]) && strings.length <= 2) { //这是最后的一个字符串也表示这个也是
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            strings[0] = strings[0].toUpperCase();

            replaceStr = strings[0].trim().replace((oldTable).trim(), newTable);
            if (strings.length == 2) {//这种表示在修改库名的时候它候命又带有表名
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr + "." + strings[1] +   //另外还需要加上后面的表名
                        StringUtils.substring(str, index1 + substr.length() + 1);
            } else if (strings.length == 1) {//这种表示只有单单一个库名,比如ues的用法
                str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + substr.length() + 1);
            }
        } else { //表示需要替换的字符一定在截取到的第二个字符串中， 还包括了不能匹配到老表

            if (strings1.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings1[1])) { //表示只修改表名
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings1[1] = strings1[1].toUpperCase();

                replaceStr = strings1[1].trim().replace((oldTable).trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings1[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr1.length());
            }

            if (strings2.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings2[1])) {
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings2[1] = strings2[1].toUpperCase();

                replaceStr = strings2[1].trim().replace((oldTable).trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings2[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr2.length());
            }
            if (strings3.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings3[1])) {
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings3[1] = strings3[1].toUpperCase();

                replaceStr = strings3[1].trim().replace((oldTable).trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings3[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr3.length());
            }
            if (strings4.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings4[1])) {
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings4[1] = strings4[1].toUpperCase();

                replaceStr = strings4[1].trim().replace(oldTable.trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings4[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr4.length());
            }
            if (strings5.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings5[1])) {
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings5[1] = strings5[1].toUpperCase();

                replaceStr = strings5[1].trim().replace((oldTable).trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings5[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr5.length());
            }
            if (strings.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings[1])) {
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings[1] = strings[1].toUpperCase();

                replaceStr = strings[1].trim().replace((oldTable).trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + substr.length() + 1);
            }
        }

        ///////////////////////////////////////

        return str;
    }

    public String dealStrSuper(String str, int index1, String oldTable, String newTable) {

        //把字符串截取出来用来替换
        String substr = StringUtils.substring(str, index1).trim(); //去除字符串前后的空格和换行符
        String[] strings = substr.split("\\.", 2);

        String substr1 = getTableOrDB(substr, "", " ");
        String[] strings1 = substr1.split("\\.", 2);

        String substr2 = getTableOrDB(substr, "", "\n");
        String[] strings2 = substr2.split("\\.", 2);

        String substr3 = getTableOrDB(substr, "", "("); //直接跟在表名后面的可能会有"("号
        String[] strings3 = substr3.split("\\.", 2);

        String substr4 = getTableOrDB(substr, "", ")"); //直接跟在表名后面的可能会有"("号
        String[] strings4 = substr4.split("\\.", 2);

        String substr5 = getTableOrDB(substr, "", "-"); //直接跟在表名后面的可能会有"("号
        String[] strings5 = substr5.split("\\.", 2);

        String replaceStr = null;
        //得到表名前的非空字符的长度（注意一定要包括前后的空格的）
        int leng = strLeng(StringUtils.substring(str, index1));

        //判断两个字符串那个是需要修改的
        if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr2)) {  //库名表名同时替换的
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr2 = substr2.toUpperCase();

            replaceStr = substr2.trim().replace(oldTable.trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +
                    StringUtils.substring(str, index1 + leng + substr2.length());

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr1)) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr1 = substr1.toUpperCase();

            replaceStr = substr1.trim().replace((oldTable).trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                    StringUtils.substring(str, index1 + leng + substr1.length());

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr3)) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr3 = substr3.toUpperCase();

            replaceStr = substr3.trim().replace((oldTable).trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                    StringUtils.substring(str, index1 + leng + substr3.length());

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr4)) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr4 = substr4.toUpperCase();

            replaceStr = substr4.trim().replace((oldTable).trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                    StringUtils.substring(str, index1 + leng + substr4.length());

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr5)) {
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr5 = substr5.toUpperCase();

            replaceStr = substr5.trim().replace((oldTable).trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                    StringUtils.substring(str, index1 + leng + substr5.length());

        } else if (StringUtils.equalsIgnoreCase(oldTable.trim(), substr)) { //这是最后的一个字符串也表示这个也是
            //把库名表名统一变成大写
            oldTable = oldTable.toUpperCase();
            substr = substr.toUpperCase();

            replaceStr = substr.trim().replace(oldTable.trim(), newTable);
            str = StringUtils.substring(str, 0, index1 + leng) + replaceStr +    //这里截取的时候要包括空格一起截取
                    StringUtils.substring(str, index1 + substr.length() + 1);
        } else {

            if (strings1.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings1[1])) { //表示只修改表名
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings1[1] = strings1[1].toUpperCase();

                replaceStr = strings1[1].trim().replace((oldTable).trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings1[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr1.length());
            }

            if (strings2.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings2[1])) {
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings2[1] = strings2[1].toUpperCase();

                replaceStr = strings2[1].trim().replace((oldTable).trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings2[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr2.length());
            }
            if (strings3.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings3[1])) {
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings3[1] = strings3[1].toUpperCase();

                replaceStr = strings3[1].trim().replace((oldTable).trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings3[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr3.length());
            }
            if (strings4.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings4[1])) {
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings4[1] = strings4[1].toUpperCase();

                replaceStr = strings4[1].trim().replace(oldTable.trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings4[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr4.length());
            }
            if (strings5.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings5[1])) {
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings5[1] = strings5[1].toUpperCase();

                replaceStr = strings5[1].trim().replace((oldTable).trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings5[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + leng + substr5.length());
            }
            if (strings.length == 2 && StringUtils.equalsIgnoreCase(oldTable.trim(), strings[1])) {
                //把库名表名统一变成大写
                oldTable = oldTable.toUpperCase();
                strings[1] = strings[1].toUpperCase();

                replaceStr = strings[1].trim().replace((oldTable).trim(), newTable);
                str = StringUtils.substring(str, 0, index1 + leng) + strings[0] + "." + replaceStr +    //这里截取的时候要包括空格一起截取
                        StringUtils.substring(str, index1 + substr.length() + 1);
            }


        }


        return str;
    }

    //判断字符串开头空白字符串的数量
    public int strLeng(String substring) {
        int count = 0;
        char[] cs = substring.toCharArray();
        for (char c : cs) {
            if (String.valueOf(c).equals(" ") || String.valueOf(c).equals("\n")) {
                count++;
            } else
                break;
        }
        return count;
    }

    //得到关键字后的表名或是库名
    public String getTableOrDB(String str, String str1, String str2) {
        //
        String tableStr = "";
        int index1 = StringUtils.indexOfIgnoreCase(str, str1);
        if (index1 != -1) {
            int index2 = StringUtils.indexOfIgnoreCase(str, str2, index1);
            if (index2 != -1)
                tableStr = str.substring(index1, index2);
        }

        return tableStr;
    }


    //去除注释中的;号
    public String removeSemicolon(String str, String str1, String str2) {
        //去掉str1和str2后的新字符串
        int flag = 0;
        while (true) {
            int index1 = StringUtils.indexOfIgnoreCase(str, str1, flag);
            if (index1 != -1) {
                int index2 = StringUtils.indexOfIgnoreCase(str, str2, index1);
                if (index2 != -1) {
                    String str3 = StringUtils.substring(str, index1, index2);
                    String remove = StringUtils.remove(str3, ";");
                    str3 = StringUtils.substring(str, 0, index1) + remove
                            + StringUtils.substring(str, index2, str.length());
                    str = str3;
                    flag = index2;
                } else {
                    return str;
                }
            } else {
                return str;
            }
        }
    }

    public String subRangeString(String str, String str1, String str2) {
        //去掉str1和str2后的新字符串
        while (true) {
            int index1 = StringUtils.indexOfIgnoreCase(str, str1);
            if (index1 != -1) {
                int index2 = StringUtils.indexOfIgnoreCase(str, str2, index1);
                if (index2 != -1) {
                    String str3 = str.substring(0, index1) + str.substring(index2 + str2.length(), str.length());
                    str = str3;
                } else {
                    return str;
                }
            } else {
                return str;
            }
        }
    }

    //得到str1和str2在str中字符的长度
    public int getStrLeng(String str, String str1, String str2, int flag) {
        int length = 0;
        int index1 = StringUtils.indexOfIgnoreCase(str, str1, flag);
        if (index1 != -1) {
            int index2 = StringUtils.indexOfIgnoreCase(str, str2, index1);
            if (index2 != -1)
                length = str.substring(index1, index2).length();
            return length;
        }
        return length;
    }

}
