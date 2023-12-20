package com.zeta.generator.enums

/**
 * 开发语言类型枚举
 * @author gcc
 */
enum class LanguageTypeEnum(
    /** 模板路径  对应template/{java or kotlin} */
    val path: String,
    /** 模板文件后缀  java->java  kotlin->kt */
    val suffix: String
) {
    JAVA("java", "java"),
    KOTLIN("kotlin", "kt")
}
