/*
 * Copyright (c) 2017.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itfsw.mybatis.generator.plugins.utils;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;

/**
 * ---------------------------------------------------------------------------
 * Java ele 生成工具
 * ---------------------------------------------------------------------------
 * @author: hewei
 * @time:2017/4/21 16:22
 * ---------------------------------------------------------------------------
 */
public class JavaElementGeneratorTools {

    /**
     * 生成静态常量
     *
     * @param fieldName 常量名称
     * @param javaType 类型
     * @param initString 初始化字段
     * @param introspectedTable 表
     * @return
     */
    public static Field generateStaticFinalField(String fieldName, FullyQualifiedJavaType javaType, String initString, IntrospectedTable introspectedTable){
        Field field = new Field(fieldName, javaType);
        CommentTools.addFieldComment(field, introspectedTable);
        field.setVisibility(JavaVisibility.PUBLIC);
        field.setStatic(true);
        field.setFinal(true);
        if (initString != null){
            field.setInitializationString(initString);
        }
        return field;
    }

    /**
     * 生成属性
     *
     * @param fieldName 常量名称
     * @param  visibility 可见性
     * @param javaType 类型
     * @param initString 初始化字段
     * @param introspectedTable 表
     * @return
     */
    public static Field generateField(String fieldName, JavaVisibility visibility, FullyQualifiedJavaType javaType, String initString, IntrospectedTable introspectedTable){
        Field field = new Field(fieldName, javaType);
        CommentTools.addFieldComment(field, introspectedTable);
        field.setVisibility(visibility);
        if (initString != null){
            field.setInitializationString(initString);
        }
        return field;
    }

    /**
     * 生成方法
     *
     * @param methodName 方法名
     * @param visibility  可见性
     * @param returnType 返回值类型
     * @param introspectedTable 表
     * @param parameters 参数列表
     * @return
     */
    public static Method generateMethod(String methodName, JavaVisibility visibility, FullyQualifiedJavaType returnType, IntrospectedTable introspectedTable, Parameter ... parameters){
        Method method = new Method(methodName);
        CommentTools.addMethodComment(method, introspectedTable);
        method.setVisibility(visibility);
        method.setReturnType(returnType);
        if (parameters != null){
            for (Parameter parameter: parameters) {
                method.addParameter(parameter);
            }
        }

        return method;
    }

    /**
     * 生成方法实现体
     *
     * @param method 方法
     * @param bodyLines 方法实现行
     * @return
     */
    public static Method generateMethodBody(Method method, String ... bodyLines){
        if (bodyLines != null){
            for (String bodyLine: bodyLines){
                method.addBodyLine(bodyLine);
            }
        }
        return method;
    }

    /**
     * 生成Filed的Set方法
     *
     * @param field field
     * @param introspectedTable 表
     * @return
     */
    public static Method generateSetterMethod(Field field, IntrospectedTable introspectedTable){
        Method method = generateMethod(
                "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1),
                JavaVisibility.PUBLIC,
                null,
                introspectedTable,
                new Parameter(field.getType(), field.getName())
        );
        return generateMethodBody(method, "this." + field.getName() + " = " + field.getName() + ";");
    }

    /**
     * 生成Filed的Get方法
     *
     * @param field field
     * @param introspectedTable 表
     * @return
     */
    public static Method generateGetterMethod(Field field, IntrospectedTable introspectedTable){
        Method method = generateMethod(
                "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1),
                JavaVisibility.PUBLIC,
                field.getType(),
                introspectedTable
        );
        return generateMethodBody(method, "return this." + field.getName() + ";");
    }
}