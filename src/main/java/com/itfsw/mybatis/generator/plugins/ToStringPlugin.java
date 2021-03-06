package com.itfsw.mybatis.generator.plugins;

import com.itfsw.mybatis.generator.plugins.utils.BasePlugin;
import com.itfsw.mybatis.generator.plugins.utils.FormatTools;
import com.itfsw.mybatis.generator.plugins.utils.JavaElementGeneratorTools;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.ArrayList;
import java.util.List;

/**
 * toString方法生成插件
 *
 * @author LanyuanXiaoyao
 * @date 2019-05-06
 */
public class ToStringPlugin extends BasePlugin {

    private static final String PROPERTY_NAME = "type";
    private static final String STRING_JOINER = "StringJoiner";
    private static final String STRING_PLUS = "StringPlus";

    private String type = STRING_JOINER;

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);
        if (introspectedTable.getTableConfigurationProperty(PROPERTY_NAME) != null) {
            type = this.properties.getProperty(PROPERTY_NAME);
        }
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.StringJoiner"));

        Method toStringMethod = JavaElementGeneratorTools.generateMethod(
                "toString",
                JavaVisibility.PUBLIC,
                FullyQualifiedJavaType.getStringInstance()
        );
        toStringMethod.addAnnotation("@Override");
        commentGenerator.addGeneralMethodComment(toStringMethod, introspectedTable);

        List<String> methodBody = new ArrayList<>();
        switch (type) {
            case STRING_JOINER:
                methodBody = generateStringJoiner(introspectedTable);
                break;
            case STRING_PLUS:
                methodBody = generateStringPlus(introspectedTable);
                break;
            default:
                methodBody.add("return super.toString();");
        }

        toStringMethod.addBodyLines(methodBody);

        FormatTools.addMethodWithBestPosition(topLevelClass, toStringMethod);

        return true;
    }

    private List<String> generateStringJoiner(IntrospectedTable introspectedTable) {
        List<String> bodyLines = new ArrayList<>();
        bodyLines.add("return new StringJoiner(\", \", \"" + new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getShortName() + " [\", \"]\")");
        introspectedTable.getPrimaryKeyColumns()
                .forEach(column -> bodyLines.add(".add(\"" + column.getJavaProperty() + "=\" + " + generateGetter(column.getJavaProperty()) + ")"));
        introspectedTable.getNonPrimaryKeyColumns()
                .forEach(column -> bodyLines.add(".add(\"" + column.getJavaProperty() + "=\" + " + column.getJavaProperty() + ")"));
        bodyLines.add(".toString();");
        return bodyLines;
    }

    private List<String> generateStringPlus(IntrospectedTable introspectedTable) {
        List<String> bodyLines = new ArrayList<>();
        bodyLines.add("return \"" + new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getShortName() + "{\" +");
        boolean isFirstLine = true;
        for (int i = 0; i < introspectedTable.getPrimaryKeyColumns().size(); i++) {
            IntrospectedColumn column = introspectedTable.getPrimaryKeyColumns().get(i);
            if (isFirstLine) {
                bodyLines.add("\"" + column.getJavaProperty() + "='\" + " + generateGetter(column.getJavaProperty()) + " + '\\'' +");
                isFirstLine = false;
            } else {
                bodyLines.add("\", " + column.getJavaProperty() + "='\" + " + generateGetter(column.getJavaProperty()) + " + '\\'' +");
            }
        }
        for (int i = 0; i < introspectedTable.getNonPrimaryKeyColumns().size(); i++) {
            IntrospectedColumn column = introspectedTable.getPrimaryKeyColumns().get(i);
            if (isFirstLine) {
                bodyLines.add("\"" + column.getJavaProperty() + "='\" + " + column.getJavaProperty() + " + '\\'' +");
                isFirstLine = false;
            } else {
                bodyLines.add("\", " + column.getJavaProperty() + "='\" + " + column.getJavaProperty() + " + '\\'' +");
            }
        }
        bodyLines.add("'}';");
        return bodyLines;
    }

    private String generateGetter(String propertyName) {
        return String.format(
                "get%s%s()",
                propertyName.substring(0, 1).toUpperCase(),
                propertyName.substring(1)
        );
    }

}
