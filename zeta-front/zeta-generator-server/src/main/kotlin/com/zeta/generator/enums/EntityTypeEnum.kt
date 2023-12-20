package com.zeta.generator.enums

/**
 * 实体类类型枚举
 * @author gcc
 */
enum class EntityTypeEnum(
    /** 类全称，带路径 */
    val path: String,
    /** 公共字段 */
    val columns: Array<String>,
    /** 特有字段 */
    val uniqueColumns: Array<String>? = null
) {
    /** 包括id、create_time、create_by字段的表继承的基础实体 */
    SUPER_ENTITY("org.zetaframework.base.entity.SuperEntity", arrayOf("id", "create_time", "created_by")),

    /** 包括id、create_time、create_by、update_by、update_time字段的表继承的基础实体 */
    ENTITY("org.zetaframework.base.entity.Entity", arrayOf("id", "create_time", "created_by", "update_time", "updated_by")),

    /** 包括id、create_time、create_by、update_by、update_time、label、parent_id、sort_value 字段的表继承的树形实体 */
    TREE_ENTITY(
        "org.zetaframework.base.entity.TreeEntity",
        arrayOf("id", "create_time", "created_by", "update_time", "updated_by", "label", "parent_id", "sort_value"),
        arrayOf("label", "parent_id", "sort_value") // 特有字段，关系到SaveDTO、UpdateDTO是否生成对应的字段
    ),

    /** 包括id、create_time、create_by、update_by、update_time、state字段的表继承的基础实体 */
    STATE_ENTITY(
        "org.zetaframework.base.entity.StateEntity",
        arrayOf("id", "create_time", "created_by", "update_time", "updated_by", "state"),
        arrayOf("state") // 特有字段，关系到SaveDTO、UpdateDTO是否生成对应的字段
    ),

    /** 不继承任何实体 */
    NONE("", arrayOf(""));

    /**
     * equals
     * @param type EntityTypeEnum
     * @return Boolean
     */
    fun eq(type: EntityTypeEnum): Boolean = eq(type.name)

    /**
     * equals
     * @param name String
     * @return Boolean
     */
    fun eq(name: String): Boolean = this.name == name
}
