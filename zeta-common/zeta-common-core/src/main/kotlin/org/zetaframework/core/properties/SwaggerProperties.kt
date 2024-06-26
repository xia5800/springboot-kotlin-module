package org.zetaframework.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * swagger配置参数
 * @author gcc
 */
@ConfigurationProperties(prefix = "zeta.swagger")
class SwaggerProperties(
    /** 标题 */
    var title: String = "在线文档",

    /** 自定义组名 */
    var group: String = "default",

    /** 描述 */
    var description: String = "项目接口文档",

    /** 版本 */
    var version: String = "1.0",

    /** 作者信息 */
    var contact: Contact? = null,

    /** swagger会解析的包路径 */
    var basePackage: String = "com.zeta",

    /** 许可证  */
    var license: String = "LICENSE",

    /** 许可证URL */
    var licenseUrl: String = "http://swagger.io",

    /** 服务URL */
    var termsOfServiceUrl: String? = "",

    /** host */
    var host: String? = "",

    /** 分组文档 */
    var docket: Map<String, DocketInfo> = mutableMapOf()
) {

    data class Contact (
        /** 联系人 */
        var name: String? = "",
        /** 联系人url */
        var url: String? = "",
        /** 联系人email */
        var email: String? = ""
    )

    data class DocketInfo (
        /** 标题  */
        var title: String? = "",
        /** 描述 */
        var description: String? = "",

        /** 【必填】swagger会解析的包路径 */
        var basePackage: String? = "",

        ) {
        fun getTitleOrDefault(defaultValue: String): String? =
            if (this.title == null) { defaultValue } else { this.title }

        fun getDescriptionOrDefault(defaultValue: String): String? =
            if (this.description == null) { defaultValue } else { this.description }
    }
}
