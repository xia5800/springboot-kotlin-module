package com.zeta.generator.engine

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.StrUtil
import com.zeta.generator.enums.EntityTypeEnum
import com.zeta.generator.enums.LanguageTypeEnum
import com.zeta.generator.enums.SwaggerTypeEnum

/**
 * 代码生成器配置参数
 * @author gcc
 */
data class CodeGeneratorConfig(
    /** 输出目录 不包含项目名。 eg: D://project */
    var outputDir: String = "",
    /** 作者名称 */
    var author: String = "AutoGenerator",
    /** 数据库连接url */
    var dbUrl: String = "",
    /** 数据库用户名 */
    var dbUsername: String = "",
    /** 数据库密码 */
    var dbPassword: String = "",
    /** 父包名 */
    var parentPackageName: String = "com.zeta",
    /** 子包名 */
    var packageName: String = "",
    /** 项目名 eg: zeta-kotlin-module */
    var projectName: String = "",
    /** 启动类所在模块名 eg: zeta-admin-server */
    var frontName: String = "",
    /** 模块名 eg: system */
    var moduleName: String = "",
    /** 需要生成的表 */
    var tableInclude: MutableList<String>? = mutableListOf(),
    /** 需要去除的表前缀 */
    var tablePrefix: MutableList<String>? = null,

    /** entity父类路径 */
    var superEntity: EntityTypeEnum = EntityTypeEnum.ENTITY,

    /** 是否启用mapper.java类上的@Repository注解 */
    var enableRepository: Boolean = true,
    /** 生成完代码是否打开生成目录 */
    var openDir: Boolean = true,

    /** jdk版本 说明：jdk17以上部分包路径发生了变化，需要特殊处理 */
    var jdkVersion: Int = 8,
    /** swagger类型 说明：knife4j 3.x以上请使用springdoc */
    var swaggerType: SwaggerTypeEnum = SwaggerTypeEnum.SPRING_FOX,

    /** 开发语言类型 */
    var languageType: LanguageTypeEnum = LanguageTypeEnum.KOTLIN
) {

    companion object {

        /**
         * 构造器
         *
         * @param projectName String 项目名
         * @param moduleName String 模块名
         * @param tablePrefix String 表前缀
         * @param author String 作者
         * @param tableInclude MutableList<String> 需要生成的表
         * @return CodeGeneratorConfig
         */
        fun build(projectName: String, moduleName: String, author: String = "AutoGenerator", tablePrefix: String = "", tableInclude: MutableList<String>? = null): CodeGeneratorConfig {
            return CodeGeneratorConfig().apply {
                // 设置项目名
                this.projectName = projectName
                // 设置模块名称
                this.moduleName = moduleName
                // 设置作者，如果有的话
                if(StrUtil.isNotBlank(author)) {
                    this.author = author
                }
                // 设置需要去除的表前缀，如果有的话
                if(StrUtil.isNotBlank(tablePrefix)) {
                    this.tablePrefix = mutableListOf(tablePrefix)
                }
                // 设置需要生成的表，如果有的话
                if(CollUtil.isNotEmpty(tableInclude)) {
                    this.tableInclude = tableInclude
                }
            }
        }
    }
}
