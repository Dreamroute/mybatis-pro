package com.github.dreamroute.mybatis.pro.core;

import com.github.dreamroute.mybatis.pro.core.consts.KeyWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtil {

    private SqlUtil() {}

    private static final String AND = "And";
    private static final String OR = "Or";

    private static final String ANDING = " and ";
    private static final String ORING = " or ";

    private static final String OB = "ORDERBY";

    public static String createSql(String methodName) {
        // 将OrderBy替换成ORDERBY，不然OrderBy的头两个字幕要和Or关键字冲突，造成分割错乱
        methodName = methodName.replace("OrderBy", OB);
        List<String> result = nameToken(methodName.substring(6));
        String sql = fragment(result);
        return sql;
    }

    private static String fragment(List<String> tokens) {
        StringBuilder builder = new StringBuilder();
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
                key = "";
                pos = token.length();
            }
            String statement = token.substring(0, pos);

            if (statement.endsWith(KeyWord.BETWEEN)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.BETWEEN));
                String column = toLine(variableName);
                builder.append(column).append(" ").append(KeyWord.BETWEEN.toLowerCase()).append(" #{start} and #{end}").append(key);
            } else if (statement.endsWith(KeyWord.LESS_THAN)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.LESS_THAN));
                String column = toLine(variableName);
                builder.append(column).append(" < #{").append(variableName).append("}").append(key);
            } else if (statement.endsWith(KeyWord.LESS_THAN_EQUAL)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.LESS_THAN_EQUAL));
                String column = toLine(variableName);
                builder.append(column).append(" <= #{").append(variableName).append("}").append(key);
            } else if (statement.endsWith(KeyWord.GREATER_THAN)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.GREATER_THAN));
                String column = toLine(variableName);
                builder.append(column).append(" > #{").append(variableName).append("}").append(key);
            } else if (statement.endsWith(KeyWord.GREATER_THAN_EQUAL)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.GREATER_THAN_EQUAL));
                String column = toLine(variableName);
                builder.append(column).append(" >= #{").append(variableName).append("}").append(key);
            } else if (statement.endsWith(KeyWord.IS_NULL)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.IS_NULL));
                String column = toLine(variableName);
                builder.append(column).append(" is null").append(key);
            } else if (statement.endsWith(KeyWord.IS_NOT_NULL)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.IS_NOT_NULL));
                String column = toLine(variableName);
                builder.append(column).append(" is not null").append(key);
            } else if (statement.endsWith(KeyWord.LIKE) && !statement.endsWith(KeyWord.NOT_LIKE)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.LIKE));
                String column = toLine(variableName);
                builder.append(column).append(" like '%${").append(variableName).append("}%'").append(key);
            } else if (statement.endsWith(KeyWord.NOT_LIKE)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.NOT_LIKE));
                String column = toLine(variableName);
                builder.append(column).append(" not like '%${").append(variableName).append("}%'").append(key);
            } else if (statement.endsWith(KeyWord.STARTING_WITH)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.STARTING_WITH));
                String column = toLine(variableName);
                builder.append(column).append(" like '${").append(variableName).append("}%'").append(key);
            } else if (statement.endsWith(KeyWord.ENDING_WITH)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.ENDING_WITH));
                String column = toLine(variableName);
                builder.append(column).append(" like '%${").append(variableName).append("}'").append(key);
            } else if (statement.endsWith(KeyWord.NOT)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.NOT));
                String column = toLine(variableName);
                builder.append(column).append(" <> ").append("#{").append(variableName).append("}").append(key);
            } else if (statement.endsWith(KeyWord.IN) && !statement.endsWith(KeyWord.NOT_IN)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.IN));
                String column = toLine(variableName);
                builder.append(column).append(" in ").append("<foreach collection='list' item='id' index='index' open='(' close=')' separator=','>#{id}</foreach>").append(key);
            } else if (statement.endsWith(KeyWord.NOT_IN)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.NOT_IN));
                String column = toLine(variableName);
                builder.append(column).append(" not in ").append("<foreach collection='list' item='id' index='index' open='(' close=')' separator=','>#{id}</foreach>").append(key);
            } else if (statement.endsWith(KeyWord.TRUE)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.TRUE));
                String column = toLine(variableName);
                builder.append(column).append(" = true").append(key);
            } else if (statement.endsWith(KeyWord.FALSE)) {
                String variableName = firstLower(removeKeyWord(statement, KeyWord.FALSE));
                String column = toLine(variableName);
                builder.append(column).append(" = false").append(key);
            } else {

                // 这里处理两类：1.处理不带后缀的条件，2.处理最后一个条件（可能包含OrderBy和DESC）

                // 1. 处理DESC
                int desc = statement.indexOf(KeyWord.DESC);
                if (desc != -1) {
                    statement = statement.substring(0, statement.length() - 4);
                }

                // 2. 处理OrderBy(也就是被替换之后的ORDERBY)
                String orderByColumn = null;
                int orderBy = statement.indexOf(OB);
                if (orderBy != -1) {
                    String[] ob = statement.split(OB);
                    statement = ob[0];
                    orderByColumn = ob[1];
                }

                // 3. 处理条件
                String variableName = firstLower(statement);
                String column = toLine(variableName);
                builder.append(column).append(" = ").append("#{").append(variableName).append("}").append(key);
                if (orderBy != -1) {
                    orderByColumn = toLine(firstLower(orderByColumn)); // 将OrderBy字段首字符转小写以及转下划线
                    builder.append(" order by ").append(orderByColumn);
                }
                if (desc != -1) {
                    builder.append(" desc");
                }
            }
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
        } else if (andIndex == -1 && orIndex != -1) {
            return OR;
        } else if (andIndex != -1 && orIndex == -1) {
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
