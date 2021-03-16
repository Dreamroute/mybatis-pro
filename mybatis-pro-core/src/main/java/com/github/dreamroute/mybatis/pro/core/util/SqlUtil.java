package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dreamroute.mybatis.pro.core.consts.KeyWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author w.dehai
 */
public class SqlUtil {

    private SqlUtil() {}

    private static final String AND = KeyWord.AND;
    private static final String OR = KeyWord.OR;
    private static final String CAN_EMPTY = "CanEmpty";

    private static final String ANDING = " and ";
    private static final String ORING = " or ";

    private static final String OB = "ORDERBY";

    public static String createSql(String methodName) {
        // 将OrderBy替换成ORDERBY，不然OrderBy的头两个字幕要和Or关键字冲突，造成分割错乱
        methodName = methodName.replace(KeyWord.ORDER_BY, OB);
        List<String> result = nameToken(methodName);
        return fragment(result, methodName.endsWith(CAN_EMPTY));
    }

    private static String fragment(List<String> tokens, boolean canEmpty) {
        StringBuilder builder = new StringBuilder();
        StringBuilder condition = new StringBuilder();
        StringBuilder orderByStr = new StringBuilder();
        for (String token : tokens) {
            String key;
            int pos;
            if (token.endsWith(AND)) {
                key = ANDING;
                pos = token.length() - 3;
            } else if (token.endsWith(OR)) {
                key = ORING;
                pos = token.length() - 2;
            } else {
                if (token.endsWith(CAN_EMPTY)) {
                    token = token.substring(0, token.length() - CAN_EMPTY.length());
                }
                key = "";
                pos = token.length();
            }
            String statement = token.substring(0, pos);
            String column;

            if (statement.endsWith(KeyWord.BETWEEN)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.BETWEEN));
                column = toLine(variableName);
                condition.append(column).append(" ").append(KeyWord.BETWEEN.toLowerCase()).append(" #{start} and #{end}");
            } else if (statement.endsWith(KeyWord.LESS_THAN)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.LESS_THAN));
                column = toLine(variableName);
                condition.append(column).append(" <![CDATA[<]]> #{").append(variableName).append("}");
            } else if (statement.endsWith(KeyWord.LESS_THAN_EQUAL)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.LESS_THAN_EQUAL));
                column = toLine(variableName);
                condition.append(column).append(" <![CDATA[<=]]> #{").append(variableName).append("}");
            } else if (statement.endsWith(KeyWord.GREATER_THAN)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.GREATER_THAN));
                column = toLine(variableName);
                condition.append(column).append(" <![CDATA[>]]> #{").append(variableName).append("}");
            } else if (statement.endsWith(KeyWord.GREATER_THAN_EQUAL)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.GREATER_THAN_EQUAL));
                column = toLine(variableName);
                condition.append(column).append(" <![CDATA[>=]]> #{").append(variableName).append("}");
            } else if (statement.endsWith(KeyWord.IS_NULL)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.IS_NULL));
                column = toLine(variableName);
                condition.append(column).append(" is null");
            } else if (statement.endsWith(KeyWord.IS_NOT_NULL)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.IS_NOT_NULL));
                column = toLine(variableName);
                condition.append(column).append(" is not null");
            } else if (statement.endsWith(KeyWord.IS_BLANK)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.IS_BLANK));
                column = toLine(variableName);
                condition.append(column).append(" is null or ").append(column).append(" = ''");
            } else if (statement.endsWith(KeyWord.IS_NOT_BLANK)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.IS_NOT_BLANK));
                column = toLine(variableName);
                condition.append(column).append(" is not null and ").append(column).append(" != ''");
            } else if (statement.endsWith(KeyWord.LIKE) && !statement.endsWith(KeyWord.NOT_LIKE)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.LIKE));
                column = toLine(variableName);
                condition.append(column).append(" like CONCAT('%', #{").append(variableName).append("}, '%')");
            } else if (statement.endsWith(KeyWord.NOT_LIKE)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.NOT_LIKE));
                column = toLine(variableName);
                condition.append(column).append(" not like CONCAT('%', #{").append(variableName).append("}, '%')");
            } else if (statement.endsWith(KeyWord.STARTING_WITH)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.STARTING_WITH));
                column = toLine(variableName);
                condition.append(column).append(" like CONCAT(#{").append(variableName).append("}, '%')");
            } else if (statement.endsWith(KeyWord.ENDING_WITH)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.ENDING_WITH));
                column = toLine(variableName);
                condition.append(column).append(" like CONCAT('%', #{").append(variableName).append("})");
            } else if (statement.endsWith(KeyWord.NOT)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.NOT));
                column = toLine(variableName);
                condition.append(column).append(" <![CDATA[<>]]> ").append("#{").append(variableName).append("}");
            } else if (statement.endsWith(KeyWord.IN) && !statement.endsWith(KeyWord.NOT_IN)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.IN));
                column = toLine(variableName);
                condition.append(column).append(" in ").append("<foreach collection='list' item='id' index='index' open='(' close=')' separator=','>#{id}</foreach>");
            } else if (statement.endsWith(KeyWord.NOT_IN)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.NOT_IN));
                column = toLine(variableName);
                condition.append(column).append(" not in ").append("<foreach collection='list' item='id' index='index' open='(' close=')' separator=','>#{id}</foreach>");
            } else {

                // 这里处理两类：1.处理不带后缀的条件，2.处理最后一个条件（可能包含OrderBy和DESC）

                // 1. 处理DESC
                int desc = statement.indexOf(KeyWord.DESC);
                if (desc != -1) {
                    statement = statement.substring(0, statement.length() - 4);
                    orderByStr.append(" desc ");
                }

                // 2. 处理OrderBy(也就是被替换之后的ORDERBY)
                int orderBy = statement.indexOf(OB);
                if (orderBy != -1) {
                    String[] ob = statement.split(OB);
                    statement = ob[0];
                    String orderByColumn = ob[1];
                    orderByStr.insert(0, orderByColumn).append(" ").insert(0, " order by ");
                }

                // 3. 处理条件
                String variableName = firstLower(statement);
                column = toLine(variableName);
                condition.append(column).append(" = ").append("#{").append(variableName).append("}");
            }
            if (canEmpty) {
                builder.append("<if test = \"" + column + " != null and " + column + " != ''\">" + condition + "</if>");
            } else {
                builder.append(condition);
            }
            condition = new StringBuilder();

            builder.append(key);
        }
        builder.insert(0, "<trim suffixOverrides = 'and|AND| and | AND |or|OR| or | OR '>");
        builder.append(" </trim>");
        builder.append(" </where>");

        // 将order by放在最后处理
        if (orderByStr != null && orderByStr.length() > 0) {
            builder.append(orderByStr);
        }

        return builder.toString();
    }

    private static String removeKeyWord(String column, String keyword) {
        return column.substring(0, column.length() - keyword.length());
    }

    private static List<String> nameToken(String name) {
        List<String> token = new ArrayList<>();
        while (name.length() > 0) {
            String spliter = keyPos(name);
            if (Objects.equals(spliter, "")) {
                token.add(name);
                break;
            }
            int pos = name.indexOf(spliter) + spliter.length();
            String before = name.substring(0, pos);

            token.add(before);
            name = name.substring(pos);
        }
        return token;
    }

    private static String keyPos(String name) {
        int andIndex = name.indexOf(AND);
        int orIndex = name.indexOf(OR);
        if (andIndex == -1 && orIndex == -1) {
            return "";
        } else if (andIndex == -1) {
            return OR;
        } else if (orIndex == -1) {
            return AND;
        } else {
            return andIndex < orIndex ? AND : OR;
        }
    }

    // 首字母改小写
    private static String firstLower(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    // 驼峰转下划线
    public static String toLine(String camelCase) {
        Pattern humpPattern = Pattern.compile("[A-Z]");
        Matcher matcher = humpPattern.matcher(camelCase);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
