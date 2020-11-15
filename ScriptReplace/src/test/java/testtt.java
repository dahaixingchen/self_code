
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import cn.com.dao.ReplaceDao;
import cn.com.dao.ReplaceDaoImpl;
import cn.com.service.ReplaceService;
import cn.com.service.ReplaceServiceImpl;
import cn.com.utils.JDBCUtils;

public class testtt {


    private static final String TABLE = "table";
    private static final String JOIN = "join";
    private static final String FROM = "from";
    private static final String LIKE = "like";
    private static final String USE = "use";
    private static final String SHOW = "show";
    private static final String EXISTS = "exists";
    private static final String UNION = "union";
    private static final String PARTITIONS = "partitions";

    private ReplaceDao r = new ReplaceDaoImpl();
    private ReplaceService re = new ReplaceServiceImpl();
    private static final String UNIONALL = "UNIONALL";
    private static final String INTO = "INTO";
    private static final String WITH = "with";


    private static String str = "SET mapred.job.name='TERADATA-EDW-t03_vou_merchant_rules_1-$(ct.format(\"yyyyMMdd\"))_4335641427054210761396197825D889-lv2';\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "use edw_work_bdpms;\r\n" +
            "\r\n" +
            "--/*Create temp table to load current data                                                           */\r\n" +
            "\r\n" +
            "DROP TABLE IF EXISTS T03_VOU_MERCHANT_RULES_S06_CUR_I_$(ct.format(\"yyyyMMdd\"))\r\n" +
            ";\r\n" +
            "\r\n" +
            "CREATE  TABLE T03_VOU_MERCHANT_RULES_S06_CUR_I_$(ct.format(\"yyyyMMdd\"))\r\n" +
            "  LIKE  edw_pdata_bdpms.T03_VOU_MERCHANT_RULES_internal\r\n" +
            ";\r\n" +
            "\r\n" +
            "--/***************************************************************************************************/\r\n" +
            "--/*Group1: Source Table:[S06_T_VOU_MERCHANT_RULE:T_VOU_MERCHANT_RULE]                               */\r\n" +
            "--/***************************************************************************************************/\r\n" +
            "SET mapred.job.name='TERADATA-EDW-t03_vou_merchant_rules_2-$(ct.format(\"yyyyMMdd\"))_9666D5EFD37069848947659069E079I3-lv2';\r\n" +
            "\r\n" +
            "DROP TABLE IF EXISTS T03_VOU_MERCHANT_RULES_S06_GRP1_BMAP_UNION_$(ct.format(\"yyyyMMdd\"))\r\n" +
            ";\r\n" +
            "CREATE TABLE T03_VOU_MERCHANT_RULES_S06_GRP1_BMAP_UNION_$(ct.format(\"yyyyMMdd\"))\r\n" +
            "AS\r\n" +
            "SELECT\r\n" +
            "            TRIM(CAST(SrcCde_Cd AS STRING)) AS PROVINCE_CODE\r\n" +
            "           ,TRIM(CAST(TargCde_Cd AS STRING)) AS PROVINCE_CODE_BMAP_VAL\r\n" +
            "           ,CAST('\\\\002' AS STRING) AS CITY_CODE\r\n" +
            "           ,NULL AS CITY_CODE_BMAP_VAL\r\n" +
            " FROM       (select * from edw_pdata_bdpms.T99_STD_CDE_MAP_INFO_internal where BDPMS_ETL_TIME = $(ct.get())) T3\r\n" +
            " \r\n" +
            " -- 吧T99_STD_CDE_MAP_INFO_internal 修改成T99_STD_CDE_MAP_INFO\r\n" +
            " \r\n" +
            "WHERE       UPPER(TRIM(SrcTab_Cd))<=>UPPER('S06_T_VOU_MERCHANT_RULE')\r\n" +
            "  AND       UPPER(TRIM(Cde_Type))<=>UPPER('PROVINCE_CODE')\r\n" +
            "UNION ALL\r\n" +
            "SELECT\r\n" +
            "            CAST('\\\\002' AS STRING) AS PROVINCE_CODE\r\n" +
            "           ,NULL AS PROVINCE_CODE_BMAP_VAL\r\n" +
            "           ,TRIM(CAST(SrcCde_Cd AS STRING)) AS CITY_CODE\r\n" +
            "           ,TRIM(CAST(TargCde_Cd AS STRING)) AS CITY_CODE_BMAP_VAL\r\n" +
            " FROM       (select * from edw_pdata_bdpms.T99_STD_CDE_MAP_INFO_internal where BDPMS_ETL_TIME = $(ct.get())) T4\r\n" +
            "WHERE       UPPER(TRIM(SrcTab_Cd))<=>UPPER('S06_T_VOU_MERCHANT_RULE')\r\n" +
            "  AND       UPPER(TRIM(Cde_Type))<=>UPPER('CITY_CODE')\r\n" +
            ";\r\n" +
            "\r\n" +
            "SET mapred.job.name='TERADATA-EDW-t03_vou_merchant_rules_3-$(ct.format(\"yyyyMMdd\"))_939D347521462E48839I947D28J96880-lv2';\r\n" +
            "\r\n" +
            "DROP TABLE IF EXISTS T03_VOU_MERCHANT_RULES_S06_GRP1_BMAP_T2_$(ct.format(\"yyyyMMdd\"))\r\n" +
            ";\r\n" +
            "\r\n" +
            "CREATE TABLE edw_work_bdpms.T03_VOU_MERCHANT_RULES_S06_GRP1_BMAP_T2_$(ct.format(\"yyyyMMdd\"))\r\n" +
            "AS\r\n" +
            "SELECT      /* +MAPJOIN(T2) */\r\n" +
            "            T1.BATCH_NO\r\n" +
            "           ,T1.MERCHANT_CODE\r\n" +
            "           ,T1.PROVINCE_CODE AS PROVINCE_CODE\r\n" +
            "           ,MAX(T2.PROVINCE_CODE_BMAP_VAL) AS PROVINCE_CODE_BMAP_VAL\r\n" +
            "           ,T1.CITY_CODE AS CITY_CODE\r\n" +
            "           ,MAX(T2.CITY_CODE_BMAP_VAL) AS CITY_CODE_BMAP_VAL\r\n" +
            "     FROM   (select * from edw_stage_bdpms.S06_T_VOU_MERCHANT_RULE_internal where BDPMS_ETL_TIME = $(ct.get())) T1\r\n" +
            "     \r\n" +
            "LEFT JOIN   edw_work_bdpms.T03_VOU_MERCHANT_RULES_S06_GRP1_BMAP_UNION_$(ct.format(\"yyyyMMdd\")) T2\r\n" +
            "    WHERE   T1.PROVINCE_CODE<=>T2.PROVINCE_CODE\r\n" +
            "       OR   T1.CITY_CODE<=>T2.CITY_CODE\r\n" +
            "     \r\n" +
            " GROUP BY   T1.BATCH_NO\r\n" +
            "           ,T1.MERCHANT_CODE\r\n" +
            "           ,T1.PROVINCE_CODE\r\n" +
            "           ,T1.CITY_CODE\r\n" +
            ";\r\n" +
            "\r\n" +
            "SET mapred.job.name='TERADATA-EDW-t03_vou_merchant_rules_4-$(ct.format(\"yyyyMMdd\"))_279E968I398025994BE86A5550407345-lv2';\r\n" +
            "\r\n" +
            "INSERT INTO TABLE edw_work_bdpms.T03_VOU_MERCHANT_RULES_S06_CUR_I_$(ct.format(\"yyyyMMdd\"))\r\n" +
            "PARTITION (BDPMS_ETL_TIME = 30001231000000)\r\n" +
            "SELECT\r\n" +
            "            COALESCE(T1.BATCH_NO,'')                                            \r\n" +
            "                                                                                        --/* Batch_Id */\r\n" +
            "           ,COALESCE(T1.MERCHANT_CODE,'')                                       \r\n" +
            "                                                                                     --/* Merchant_Id */\r\n" +
            "           ,T1.MERCHANT_TYPE                                                    \r\n" +
            "                                                                                --/* Merchant_Type_Cd */\r\n" +
            "           ,T1.USEFUL_FLAG                                                      \r\n" +
            "                                                                                       --/* Valid_Ind */\r\n" +
            "           ,T1.MERCHANT_SOURCE                                                  \r\n" +
            "                                                                                 --/* Merchant_Src_Cd */\r\n" +
            "           ,T1.BUSINESS_TYPE                                                    \r\n" +
            "                                                                                     --/* Biz_Type_Cd */\r\n" +
            "           ,T1.BUSINESS_AREA_CITY                                               \r\n" +
            "                                                                               --/* Biz_City_Cd_Group */\r\n" +
            "           ,COALESCE(T2.PROVINCE_CODE_BMAP_VAL,CONCAT('#',TRIM(T1.PROVINCE_CODE)),'')\r\n" +
            "                                                                                         --/* Prov_Cd */\r\n" +
            "           ,COALESCE(T2.CITY_CODE_BMAP_VAL,CONCAT('#',TRIM(T1.CITY_CODE)),'')   \r\n" +
            "                                                                                         --/* City_Cd */\r\n" +
            "           ,T1.PROVINCE_CODE                                                    \r\n" +
            "                                                                                     --/* Src_Prov_Cd */\r\n" +
            "           ,T1.CITY_CODE                                                        \r\n" +
            "                                                                                     --/* Src_City_Cd */\r\n" +
            "           ,'S06'                                                               \r\n" +
            "                                                                                         --/* src_sys */\r\n" +
            "           ,'S06_T_VOU_MERCHANT_RULE'                                           \r\n" +
            "                                                                                       --/* src_table */\r\n" +
            "           ,'$(ct.format(\"yyyy-MM-dd\"))'                                                        \r\n" +
            "                                                                                          --/* ins_dt */\r\n" +
            "           ,null                                                                \r\n" +
            "                                                                                          --/* upt_dt */\r\n" +
            "           ,'$(ct.format(\"yyyy-MM-dd HH:mm:ss\"))'                                                   \r\n" +
            "                                                                                         --/* proc_tm */\r\n" +
            "FROM        (select * from edw_stage_bdpms.S06_T_VOU_MERCHANT_RULE_internal where BDPMS_ETL_TIME = $(ct.get()) ) T1\r\n" +
            "LEFT JOIN   edw_work_bdpms.T03_VOU_MERCHANT_RULES_S06_GRP1_BMAP_T2_$(ct.format(\"yyyyMMdd\"))  T2\r\n" +
            "       ON   T1.BATCH_NO<=>T2.BATCH_NO\r\n" +
            "      AND   T1.MERCHANT_CODE<=>T2.MERCHANT_CODE\r\n" +
            "      AND   T1.PROVINCE_CODE<=>T2.PROVINCE_CODE\r\n" +
            "      AND   T1.CITY_CODE<=>T2.CITY_CODE\r\n" +
            "   \r\n" +
            ";\r\n" +
            "\r\n" +
            "DROP TABLE IF EXISTS T03_VOU_MERCHANT_RULES_S06_GRP1_BMAP_UNION_$(ct.format(\"yyyyMMdd\"))\r\n" +
            ";\r\n" +
            "DROP TABLE IF EXISTS T03_VOU_MERCHANT_RULES_S06_GRP1_BMAP_T2_$(ct.format(\"yyyyMMdd\"))\r\n" +
            ";\r\n" +
            "\r\n" +
            "--/***************************************************************************************************/\r\n" +
            "--/*Process CUR_I table according to statement in [Embedded SQL for Cur_I Table]                     */\r\n" +
            "INSERT overwrite  TABLE  edw_work_bdpms.T03_VOU_MERCHANT_RULES_S06_CUR_I_$(ct.format(\"yyyyMMdd\"))\r\n" +
            "PARTITION (BDPMS_ETL_TIME = 30001231000000) \r\n" +
            "select  \r\n" +
            "      Batch_Id \r\n" +
            ",     Merchant_Id \r\n" +
            ",     Merchant_Type_Cd \r\n" +
            ",     Valid_Ind \r\n" +
            ",     Merchant_Src_Cd \r\n" +
            ",     Biz_Type_Cd \r\n" +
            ",     Biz_City_Cd_Group \r\n" +
            ",     Prov_Cd \r\n" +
            ",     City_Cd \r\n" +
            ",     Src_Prov_Cd\r\n" +
            ",     Src_City_Cd\r\n" +
            ",     src_sys \r\n" +
            ",     src_table \r\n" +
            ",     ins_dt  \r\n" +
            ",     upt_dt \r\n" +
            ",     proc_tm \r\n" +
            "from \r\n" +
            "(select *,row_number() over (partition by Batch_Id,Merchant_Id  order by Valid_Ind ) as rn from edw_work_bdpms.T03_VOU_MERCHANT_RULES_S06_CUR_I_$(ct.format(\"yyyyMMdd\")) ) P1 \r\n" +
            "where P1.rn=1 ;\r\n" +
            "--/***************************************************************************************************/\r\n" +
            "\r\n" +
            "\r\n" +
            "--/***************************************************************************************************/\r\n" +
            "--/*Backup specified partition and truncate it                                                       */\r\n" +
            "SET mapred.job.name='TERADATA-EDW-t03_vou_merchant_rules_5-$(ct.format(\"yyyyMMdd\")_IH62884850468E7365761C5238555IF9-lv2';\r\n" +
            "\r\n" +
            "\r\n" +
            "             \r\n" +
            "\r\n" +
            "\r\n" +
            "DROP TABLE IF EXISTS T03_VOU_MERCHANT_RULES_S06_BAK_$(ct.format(\"yyyyMMdd\"))\r\n" +
            ";\r\n" +
            "\r\n" +
            "CREATE  TABLE T03_VOU_MERCHANT_RULES_S06_BAK_$(ct.format(\"yyyyMMdd\")) \r\n" +
            "  as select * from edw_pdata_bdpms.T03_VOU_MERCHANT_RULES_internal\r\n" +
            ";\r\n" +
            "\r\n" +
            "\r\n" +
            "MSCK REPAIR TABLE T03_VOU_MERCHANT_RULES_S06_BAK_$(ct.format(\"yyyyMMdd\"));\r\n" +
            "use edw_work_bdpms;\r\n" +
            "SET mapred.job.name='TERADATA-EDW-t03_vou_merchant_rules_6-$(ct.format(\"yyyyMMdd\"))_40653GD5426777829197G93877132H17-lv2';\r\n" +
            "\r\n" +
            "INSERT OVERWRITE TABLE edw_pdata_bdpms.T03_VOU_MERCHANT_RULES_internal\r\n" +
            "PARTITION (BDPMS_ETL_TIME = 30001231000000)\r\n" +
            "SELECT\r\n" +
            "            COALESCE(P1.batch_id,NULL) AS batch_id\r\n" +
            "           ,COALESCE(P1.merchant_id,NULL) AS merchant_id\r\n" +
            "           ,P1.merchant_type_cd AS merchant_type_cd\r\n" +
            "           ,P1.valid_ind AS valid_ind\r\n" +
            "           ,P1.merchant_src_cd AS merchant_src_cd\r\n" +
            "           ,P1.biz_type_cd AS biz_type_cd\r\n" +
            "           ,P1.biz_city_cd_group AS biz_city_cd_group\r\n" +
            "           ,P1.prov_cd AS prov_cd\r\n" +
            "           ,P1.city_cd AS city_cd\r\n" +
            "           ,P1.src_prov_cd AS src_prov_cd\r\n" +
            "           ,P1.src_city_cd AS src_city_cd\r\n" +
            "           ,P1.src_sys AS src_sys\r\n" +
            "           ,P1.src_table AS src_table\r\n" +
            "           ,CASE\r\n" +
            "                 WHEN (P2.Batch_Id<=>P1.Batch_Id AND P2.Merchant_Id<=>P1.Merchant_Id) THEN P2.ins_dt\r\n" +
            "                 ELSE P1.ins_dt\r\n" +
            "            END AS ins_dt\r\n" +
            "           ,CASE\r\n" +
            "                 WHEN (P2.Batch_Id<=>P1.Batch_Id AND P2.Merchant_Id<=>P1.Merchant_Id) THEN\r\n" +
            "                  (\r\n" +
            "                        CASE\r\n" +
            "                              WHEN P1.Merchant_Type_Cd<=>P2.Merchant_Type_Cd\r\n" +
            "                               AND P1.Valid_Ind<=>P2.Valid_Ind\r\n" +
            "                               AND P1.Merchant_Src_Cd<=>P2.Merchant_Src_Cd\r\n" +
            "                               AND P1.Biz_Type_Cd<=>P2.Biz_Type_Cd\r\n" +
            "                               AND P1.Biz_City_Cd_Group<=>P2.Biz_City_Cd_Group\r\n" +
            "                               AND P1.Prov_Cd<=>P2.Prov_Cd\r\n" +
            "                               AND P1.City_Cd<=>P2.City_Cd\r\n" +
            "                               AND P1.Src_Prov_Cd<=>P2.Src_Prov_Cd\r\n" +
            "                               AND P1.Src_City_Cd<=>P2.Src_City_Cd\r\n" +
            "                        THEN   P2.upt_dt\r\n" +
            "                        ELSE   '$(ct.format(\"yyyy-MM-dd\"))'\r\n" +
            "                         END\r\n" +
            "                    )\r\n" +
            "                 ELSE  P1.upt_dt\r\n" +
            "            END AS upt_dt\r\n" +
            "           ,CASE\r\n" +
            "                 WHEN (P2.Batch_Id<=>P1.Batch_Id AND P2.Merchant_Id<=>P1.Merchant_Id) THEN\r\n" +
            "                  (\r\n" +
            "                        CASE\r\n" +
            "                              WHEN P1.Merchant_Type_Cd<=>P2.Merchant_Type_Cd\r\n" +
            "                               AND P1.Valid_Ind<=>P2.Valid_Ind\r\n" +
            "                               AND P1.Merchant_Src_Cd<=>P2.Merchant_Src_Cd\r\n" +
            "                               AND P1.Biz_Type_Cd<=>P2.Biz_Type_Cd\r\n" +
            "                               AND P1.Biz_City_Cd_Group<=>P2.Biz_City_Cd_Group\r\n" +
            "                               AND P1.Prov_Cd<=>P2.Prov_Cd\r\n" +
            "                               AND P1.City_Cd<=>P2.City_Cd\r\n" +
            "                               AND P1.Src_Prov_Cd<=>P2.Src_Prov_Cd\r\n" +
            "                               AND P1.Src_City_Cd<=>P2.Src_City_Cd\r\n" +
            "                        THEN   P2.proc_tm\r\n" +
            "                        ELSE   '$(ct.format(\"yyyy-MM-dd HH:mm:ss\"))'\r\n" +
            "                         END\r\n" +
            "                    )\r\n" +
            "                 ELSE  P1.proc_tm\r\n" +
            "            END AS proc_tm\r\n" +
            "FROM       edw_work_bdpms.T03_VOU_MERCHANT_RULES_S06_CUR_I_$(ct.format(\"yyyyMMdd\")) P1\r\n" +
            "LEFT JOIN  edw_work_bdpms.T03_VOU_MERCHANT_RULES_S06_BAK_$(ct.format(\"yyyyMMdd\")) P2\r\n" +
            "ON         P1.Batch_Id<=>P2.Batch_Id\r\n" +
            "AND        P1.Merchant_Id<=>P2.Merchant_Id\r\n" +
            "\r\n" +
            "UNION ALL\r\n" +
            "SELECT\r\n" +
            "            COALESCE(P1.batch_id,NULL) AS batch_id\r\n" +
            "           ,COALESCE(P1.merchant_id,NULL) AS merchant_id\r\n" +
            "           ,P1.merchant_type_cd AS merchant_type_cd\r\n" +
            "           ,P1.valid_ind AS valid_ind\r\n" +
            "           ,P1.merchant_src_cd AS merchant_src_cd\r\n" +
            "           ,P1.biz_type_cd AS biz_type_cd\r\n" +
            "           ,P1.biz_city_cd_group AS biz_city_cd_group\r\n" +
            "           ,P1.prov_cd AS prov_cd\r\n" +
            "           ,P1.city_cd AS city_cd\r\n" +
            "           ,P1.src_prov_cd AS src_prov_cd\r\n" +
            "           ,P1.src_city_cd AS src_city_cd\r\n" +
            "           ,P1.src_sys AS src_sys\r\n" +
            "           ,P1.src_table AS src_table\r\n" +
            "           ,P1.ins_dt AS ins_dt\r\n" +
            "           ,P1.upt_dt AS upt_dt\r\n" +
            "           ,P1.proc_tm AS proc_tm\r\n" +
            "FROM       edw_work_bdpms.T03_VOU_MERCHANT_RULES_S06_BAK_$(ct.format(\"yyyyMMdd\")) P1\r\n" +
            "LEFT JOIN  edw_work_bdpms.T03_VOU_MERCHANT_RULES_S06_CUR_I_$(ct.format(\"yyyyMMdd\")) P2\r\n" +
            "ON         P1.Batch_Id<=>P2.Batch_Id\r\n" +
            "AND        P1.Merchant_Id<=>P2.Merchant_Id\r\n" +
            "WHERE      (P2.Batch_Id<=>P1.Batch_Id=FALSE\r\n" +
            "OR         P2.Merchant_Id<=>P1.Merchant_Id=FALSE)\r\n" +
            ";\r\n" +
            "\r\n" +
            "DROP TABLE IF EXISTS T03_VOU_MERCHANT_RULES_S06_CUR_I_$(ct.format(\"yyyyMMdd\"));\r\n" +
            "\r\n" +
            "DROP TABLE IF EXISTS T03_VOU_MERCHANT_RULES_S06_BAK_$(ct.format(\"yyyyMMdd\"));";


    public static void main(String[] args) {

    }

    /*------------------------------------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------------------------------------*/
    @Test
    public void aa() {
        String newScript = getNewScript(str, "edw_work_bdpms.T03_VOU_MERCHANT_RULES_S06_CUR_I_$(ct.format(\"yyyyMMdd\"))", "666666。888888", false);

        System.out.println(newScript);
    }

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
    public String dealStrin(String str, int index1, String oldData, String newData) {
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