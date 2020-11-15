package cn.com.service;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import cn.com.entity.ConfigurationEntity;
import cn.com.entity.ExplainHiveEntity;
import cn.com.entity.FlagEntity;
import cn.com.entity.LogEntity;

public interface ReplaceService {

    /**
     * 判断能否在hive环境中执行成功
     * 如果能就返回执行过程中的日志，执行的脚本, 以及能否执行成功的状态码
     * 如果不能执行程序终止
     */
    public ExplainHiveEntity explainHive(String sql) throws IOException, InterruptedException;

    /**
     * 脚本替换
     * 返回替换后的新的脚本
     */
    public String ReplaceData(ConfigurationEntity tableData, Integer dependId, String flag) throws SQLException;


    /**
     * 获取数据库需要修改的数据
     * 返回数据库内容
     */
    public List getData() throws SQLException;


    /**
     * 修改依赖id
     */
    public boolean alterDepend(Integer oldjobid_in, Integer newjobid_in, Integer globalDependId) throws SQLException;

    /**
     * 或得所有要修改的脚本的id
     */
    public List<Integer> getDependId(int Oldjobid_in) throws SQLException;

    /**
     * 如果数据替换失败要回写数据到原来的位置
     */
    public void returnData(String globalScript, Integer globalDependId, String flag) throws SQLException;

    /**
     * 读取数据库中的脚本信息
     * 返回String类的脚本内容
     */
    public String getScript(int id) throws SQLException;

    /**
     * 读取数据库中的脚本信息
     * 返回String类的脚本内容
     */
    public String getFileScript(int id) throws SQLException;

    /**
     * 复制一个脚本插入到需要修改的数据库中
     */
    public String copyScript(String script, Integer scriptId) throws SQLException;

    /**
     * 得到复制的到mysql的备用脚本
     */
    public String getCopyScript(Integer copyScriptId) throws SQLException;

    /**
     * 把新修改后的脚本写回到数据库中
     */
    public void writeScipt(String newScript, int id, String flag) throws SQLException;

    /**
     * 去掉脚本中的注释
     */
    public String removeComment(String str, String str1, String str2);

    /**
     * 把脚本处理成hive能跑的
     */
    public StringBuffer dealStr(String str);

    /**
     * 修正收SQL脚本的语法
     */
    public String dealWithStr(String s);

    /**
     * 匹配括号，返回第一个括号中年的内容
     */
    public String extractMessage(String msg);

    /**
     * 填写日志,
     */
    public Integer writeLog(LogEntity log) throws SQLException;

    /*----------------------------------------下面的方法是对SQL脚本中库表表名的替换-------------------------------------------*/

    /**
     * 得到最终的修改好的SQL脚本
     */
    public String getNewScript(String str, String oldTable, String newTable, boolean tableAndDB);

    /**
     * 匹配SQL关键字为基础进行字符串的处理工作
     */
    public String deal(String sql, String oldTable, String newTable, boolean tableAndDB);

    /**
     * 匹配sql关键字的全文搜索处理，
     * 返回经过处理后的结果
     */
    public String whileUtil(String str, String keyboard, String oldTable, String newTable, boolean tableAndDB);

    /**
     * 抠出SQL关键字后的库名表名进行替换工作
     * 返回处理后的结果以及指针的索引
     */
    public FlagEntity flagStr(String str, String keywords, int flag, int index1, String oldTable, String newTable, boolean tableAndDB);

    /**
     * 对脚本进行处理，辅助替换库名表名
     */
    public String dealString(String str, int index1, String oldTable, String newTable);

    /**
     * 解决只在修改表名的时候，表名带点号
     */
    public String dealStrSuper(String str, int index1, String oldTable, String newTable);

    /**
     * 判断字符串开头空白字符串的数量
     */
    public int strLeng(String substring);

    /**
     * 得到SQL关键字后的库名表名
     */
    public String getTableOrDB(String str, String str1, String str2);

    /**
     * 去除脚本注释中的分号
     */
    public String removeSemicolon(String str, String str1, String str2);

    /**
     * 得到str1和str2在str中字符的长度
     */
    public int getStrLeng(String str, String str1, String str2, int flag);


}
