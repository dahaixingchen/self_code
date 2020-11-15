import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class testMothd {
    public String str = "-- 建表语句\r\n" +
            "create table T05_BPBP_REFUND_INIT(\r\n" +
            "            refund_id string\r\n" +
            "           ,user_acct_id string\r\n" +
            "           ,platform_order_id string\r\n" +
            "           ,pay_order_id string\r\n" +
            "           ,pay_serial_id string\r\n" +
            "           ,biz_type_cd string\r\n" +
            "           ,comd_short_nm string\r\n" +
            "           ,order_stat_cd string\r\n" +
            "           ,src_order_stat_cd string\r\n" +
            "           ,refund_amt double\r\n" +
            "           ,refund_times bigint\r\n" +
            "           ,hdl_fee double\r\n" +
            "           ,ccy string\r\n" +
            "           ,refund_compl_tm timestamp\r\n" +
            "           ,refund_meth_cd string\r\n" +
            "           ,refund_serial_id string\r\n" +
            "           ,bill_id string\r\n" +
            "           ,blend_rst string\r\n" +
            "           ,biz_order_id string\r\n" +
            "           ,orig_order_create_tm timestamp\r\n" +
            "           ,biz_compl_tm timestamp\r\n" +
            "           ,biz_amt double\r\n" +
            "           ,pay_amt double\r\n" +
            "           ,merchant_id string\r\n" +
            "           ,cha_type_cd string\r\n" +
            "           ,pay_type_cd string\r\n" +
            "           ,card_id string\r\n" +
            "           ,create_tm timestamp\r\n" +
            "           ,last_upd_tm timestamp\r\n" +
            "           ,src_sys string\r\n" +
            "           ,src_table string\r\n" +
            "           ,ins_dt string\r\n" +
            "           ,upt_dt string\r\n" +
            "           ,proc_tm timestamp);\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "SET mapred.job.name='TERADATA-EDW-t05_bpbp_refund_init_s20_1-$(ct.format(\"yyyyMMdd\"))_557F9A9A3925894G1000357041618131-lv2';\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "use edw_work_bdpms;\r\n" +
            "\r\n" +
            "--/*Create temp table to load current data                                                           */\r\n" +
            "\r\n" +
            "DROP TABLE IF EXISTS T05_BPBP_REFUND_INIT_S20_CUR_I_$(ct.format(\"yyyyMMdd\"))\r\n" +
            ";\r\n" +
            "\r\n" +
            "CREATE  TABLE T05_BPBP_REFUND_INIT_S20_CUR_I_$(ct.format(\"yyyyMMdd\"))\r\n" +
            "  LIKE  edw_pdata_bdpms.T05_BPBP_REFUND_INIT\r\n" +
            ";\r\n" +
            "\r\n" +
            "--/***************************************************************************************************/\r\n" +
            "--/*Group1: Source Table:[S20_T_BPBP_REFUND_ORDER:T_BPBP_REFUND_ORDER]                               */\r\n" +
            "--/***************************************************************************************************/\r\n" +
            "SET mapred.job.name='TERADATA-EDW-t05_bpbp_refund_init_s20_2-$(ct.format(\"yyyyMMdd\"))_70212202690D8C245CHEI45649H65681-lv2';\r\n" +
            "\r\n" +
            "INSERT INTO TABLE edw_work_bdpms.T05_BPBP_REFUND_INIT_S20_CUR_I_$(ct.format(\"yyyyMMdd\"))\r\n" +
            "PARTITION (BDPMS_ETL_TIME = $(ct.get()),incr_col='create_tm')\r\n" +
            "SELECT      /*+ MAPJOIN(T2) */\r\n" +
            "            COALESCE(T1.REFUND_ID,'')                                           \r\n" +
            "                                                                                       --/* REFUND_ID */\r\n" +
            "           ,T1.USER_ACCOUNT                                                     \r\n" +
            "                                                                                    --/* User_Acct_Id */\r\n" +
            "           ,T1.BPBP_ORDER_NO                                                    \r\n" +
            "                                                                               --/* Platform_Order_Id */\r\n" +
            "           ,T1.ORDER_PAY_ID                                                     \r\n" +
            "                                                                                    --/* Pay_Order_Id */\r\n" +
            "           ,T1.ORDER_PAY_SEQ_NO                                                 \r\n" +
            "                                                                                   --/* Pay_Serial_Id */\r\n" +
            "           ,T1.BIZ_TYPE                                                         \r\n" +
            "                                                                                     --/* Biz_Type_Cd */\r\n" +
            "           ,T1.COMMODITY_NAME                                                   \r\n" +
            "                                                                                   --/* Comd_Short_Nm */\r\n" +
            "           ,COALESCE(T2.TargCde_Cd,CONCAT('#',TRIM(T1.STAT)),'')                \r\n" +
            "                                                                                   --/* Order_Stat_Cd */\r\n" +
            "           ,T1.STAT                                                             \r\n" +
            "                                                                               --/* Src_Order_Stat_Cd */\r\n" +
            "           ,T1.REFUND_AMOUNT                                                    \r\n" +
            "                                                                                      --/* Refund_Amt */\r\n" +
            "           ,T1.REFUND_NUM                                                       \r\n" +
            "                                                                                    --/* Refund_Times */\r\n" +
            "           ,T1.COUNTER_FEE                                                      \r\n" +
            "                                                                                         --/* Hdl_Fee */\r\n" +
            "           ,T1.CURTYPE                                                          \r\n" +
            "                                                                                             --/* Ccy */\r\n" +
            "           ,T1.REFUND_DATE                                                      \r\n" +
            "                                                                                 --/* Refund_Compl_Tm */\r\n" +
            "           ,T1.REFUND_MODE                                                      \r\n" +
            "                                                                                  --/* Refund_Meth_Cd */\r\n" +
            "           ,T1.REFUND_SEQ_NO                                                    \r\n" +
            "                                                                                --/* Refund_Serial_Id */\r\n" +
            "           ,T1.TICKET_NO                                                        \r\n" +
            "                                                                                         --/* Bill_Id */\r\n" +
            "           ,T1.DEAL_FLAG                                                        \r\n" +
            "                                                                                       --/* Blend_Rst */\r\n" +
            "           ,T1.ORDER_BIZ_ID                                                     \r\n" +
            "                                                                                    --/* Biz_Order_Id */\r\n" +
            "           ,T1.OLD_CREATE_DATE                                                  \r\n" +
            "                                                                            --/* Orig_Order_Create_Tm */\r\n" +
            "           ,T1.CONFIRM_DATE                                                     \r\n" +
            "                                                                                    --/* Biz_Compl_Tm */\r\n" +
            "           ,T1.ORDER_BIZ_PRICE                                                  \r\n" +
            "                                                                                         --/* Biz_Amt */\r\n" +
            "           ,T1.ORDER_PAY_PRICE                                                  \r\n" +
            "                                                                                         --/* Pay_Amt */\r\n" +
            "           ,T1.SUPPLIER_CODE                                                    \r\n" +
            "                                                                                     --/* Merchant_Id */\r\n" +
            "           ,T1.CHANNEL_TYPE                                                     \r\n" +
            "                                                                                     --/* Cha_Type_Cd */\r\n" +
            "           ,T1.PAYMENT_TYPE                                                     \r\n" +
            "                                                                                     --/* Pay_Type_Cd */\r\n" +
            "           ,T1.CUST_CODE                                                        \r\n" +
            "                                                                                         --/* Card_Id */\r\n" +
            "           ,T1.CREATE_DATE                                                      \r\n" +
            "                                                                                       --/* Create_Tm */\r\n" +
            "           ,T1.UPDATE_DATE                                                      \r\n" +
            "                                                                                     --/* Last_Upd_Tm */\r\n" +
            "           ,'S20'                                                               \r\n" +
            "                                                                                         --/* src_sys */\r\n" +
            "           ,'S20_T_BPBP_REFUND_ORDER_INIT'                                      \r\n" +
            "                                                                                       --/* src_table */\r\n" +
            "           ,'$(ct.format(\"yyyy-MM-dd\"))'                                                        \r\n" +
            "                                                                                          --/* ins_dt */\r\n" +
            "           ,'$(ct.format(\"yyyy-MM-dd\"))'                                                        \r\n" +
            "                                                                                          --/* upt_dt */\r\n" +
            "           ,'$(ct.format(\"yyyy-MM-dd HH:mm:ss\"))'                                                   \r\n" +
            "                                                                                         --/* proc_tm */\r\n" +
            "FROM        edw_stage_bdpms.S20_T_BPBP_REFUND_ORDER_INIT  T1\r\n" +
            "LEFT JOIN   (select * from edw_pdata_bdpms.T99_STD_CDE_MAP_INFO_internal where BDPMS_ETL_TIME = $(ct.get()))  T2\r\n" +
            "       ON   T1.STAT<=>T2.SrcCde_Cd\r\n" +
            "      AND   UPPER(T2.SrcTab_Cd)<=>UPPER('S20_T_BPBP_REFUND_ORDER_INIT')\r\n" +
            "      AND   UPPER(T2.Cde_Type)<=>UPPER('STAT')\r\n" +
            "	  \r\n" +
            ";\r\n" +
            "\r\n" +
            "SET mapred.job.name='TERADATA-EDW-t05_bpbp_refund_init_s20_3-$(ct.format(\"yyyyMMdd\"))_64375710888B3591D24113467579I730-lv2';\r\n" +
            "\r\n" +
            "INSERT OVERWRITE TABLE edw_pdata_bdpms.T05_BPBP_REFUND_INIT\r\n" +
            "PARTITION (BDPMS_ETL_TIME = $(ct.get()),incr_col='create_tm')\r\n" +
            "SELECT\r\n" +
            "            COALESCE(refund_id,NULL)\r\n" +
            "           ,user_acct_id\r\n" +
            "           ,platform_order_id\r\n" +
            "           ,pay_order_id\r\n" +
            "           ,pay_serial_id\r\n" +
            "           ,biz_type_cd\r\n" +
            "           ,comd_short_nm\r\n" +
            "           ,order_stat_cd\r\n" +
            "           ,src_order_stat_cd\r\n" +
            "           ,refund_amt\r\n" +
            "           ,refund_times\r\n" +
            "           ,hdl_fee\r\n" +
            "           ,ccy\r\n" +
            "           ,refund_compl_tm\r\n" +
            "           ,refund_meth_cd\r\n" +
            "           ,refund_serial_id\r\n" +
            "           ,bill_id\r\n" +
            "           ,blend_rst\r\n" +
            "           ,biz_order_id\r\n" +
            "           ,orig_order_create_tm\r\n" +
            "           ,biz_compl_tm\r\n" +
            "           ,biz_amt\r\n" +
            "           ,pay_amt\r\n" +
            "           ,merchant_id\r\n" +
            "           ,cha_type_cd\r\n" +
            "           ,pay_type_cd\r\n" +
            "           ,card_id\r\n" +
            "           ,create_tm\r\n" +
            "           ,last_upd_tm\r\n" +
            "           ,src_sys\r\n" +
            "           ,src_table\r\n" +
            "           ,ins_dt\r\n" +
            "           ,upt_dt\r\n" +
            "           ,proc_tm\r\n" +
            "FROM        edw_work_bdpms.T05_BPBP_REFUND_INIT_S20_CUR_I_$(ct.format(\"yyyyMMdd\"))\r\n" +
            ";\r\n" +
            "\r\n" +
            "DROP TABLE IF EXISTS T05_BPBP_REFUND_INIT_S20_CUR_I_$(ct.format(\"yyyyMMdd\"));\r\n" +
            "\r\n" +
            "";


    @Test
    public void aa() {
        System.out.println(str);

        System.out.println("sdfdsa");
    }

    @Test
    public void dealStr() {
        //去除注释中的分号
        StringBuffer strB = new StringBuffer();
        String[] split = null;
        //去掉注释
        str = removeComment(str, "--", "\n");
        if (str != null) {
            split = str.trim().split(";");
        } else
            ;

        for (String s : split) {
            //修正脚本中$(ct.xxxx)的语法
            //当语句中包含set,use,drop的时候不做处理
            if (StringUtils.startsWithIgnoreCase(s.trim(), "set") || StringUtils.startsWithIgnoreCase(s.trim(), "use")) {
                ;
            } else {
                s = dealWithStr(s);
            }

            //给插入脚本的语句走执行计划
            //当语句中包含set,use,drop,create语句的时候就直接执行
            if (StringUtils.startsWithIgnoreCase(s.trim(), "set") || StringUtils.startsWithIgnoreCase(s.trim(), "use") || StringUtils.startsWithIgnoreCase(s.trim(), "drop") || StringUtils.startsWithIgnoreCase(s.trim(), "create")) {
                strB.append(s + ";");
            } else {
                s = "explain " + s + ";";
                strB.append(s);
            }

        }

        System.out.println(strB.toString());
        ;
    }


    //修正SQL的语法
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


    //去除脚本的注释
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

    public static String extractMessage(String msg) {

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
//					return msg.substring(start + 1, i);
                }
            }
        }
        return list.get(0);
    }
}
