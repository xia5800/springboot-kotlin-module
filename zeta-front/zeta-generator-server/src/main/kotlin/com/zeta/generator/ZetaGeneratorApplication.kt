package com.zeta.generator

import com.zeta.generator.engine.CodeGeneratorConfig
import com.zeta.generator.engine.Generator
import com.zeta.generator.enums.EntityTypeEnum
import com.zeta.generator.enums.LanguageTypeEnum

/**
 * Main方法
 *
 * @author gcc
 */
fun main(args: Array<String>) {
    // 生成代码
    Generator.run(buildConfig())
}

/**
 * 获取配置
 * @return CodeGeneratorConfig
 */
private fun buildConfig(): CodeGeneratorConfig  {
    // 要生成代码的表
    val tableInclude = mutableListOf(
        "sys_opt_log",
    )
    // 需要去除的表前缀
    val tablePrefix = mutableListOf(
        ""
    )

    return CodeGeneratorConfig.build("zeta-kotlin-module", "system").apply {
        this.tableInclude = tableInclude
        this.tablePrefix = tablePrefix
        this.openDir = true
        // 父包名。除非你修改了父包名，否则不要修改
        // this.parentPackageName = "com.zeta"

        // 启动类所在模块名
        this.frontName = "zeta-quartz-server"
        // 子包名。 说明：父包名默认为“com.zeta”,生成代码的时候会自动拼接父包名得到“com.zeta.admin”
        this.packageName = "admin"
        // 代码输出目录，不要带项目名，生成代码的时候会自动拼接项目名。 ps: 为了防止手滑导致重新生成代码，建议此处目录不要是当前项目所在的目录
        this.outputDir = "D://codeGen"

        // entity父类路径（默认包含，id、创建人创建时间、修改人修改时间）。 如不需要修改人修改时间，请使用SuperEntity。不需要继承父类请设置EntityTypeEnum.NONE值
        this.superEntity = EntityTypeEnum.ENTITY

        // 说明：jdk版本大于17才需要设置
        // this.jdkVersion = 17
        // 说明：knife4j 2.x版本使用springFox  3.x和4.x版本使用springDoc
        // this.swaggerType = SwaggerTypeEnum.SPRING_DOC

        // 数据库配置
        this.dbUrl = "jdbc:mysql://127.0.0.1:3306/zeta_kotlin_module?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8"
        this.dbUsername = "root"
        this.dbPassword = "root"
    }
}
