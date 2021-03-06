package org.zetaframework.core.swagger

import cn.hutool.core.collection.CollUtil
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.zetaframework.core.swagger.properties.SwaggerProperties
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc

/**
 * Knife4j 配置
 *
 * @author gcc
 */
@Configuration
@EnableSwagger2WebMvc
@Import(BeanValidatorPluginsConfiguration::class)
@EnableConfigurationProperties(SwaggerProperties::class)
class Knife4jConfiguration(
    private var factory: BeanFactory,
    private val swaggerProperties: SwaggerProperties,
    private val openApiExtensionResolver: OpenApiExtensionResolver
) : BeanFactoryAware {

    override fun setBeanFactory(beanFactory: BeanFactory) {
        factory = beanFactory
    }


    /**
     * 动态分组配置
     *
     * 说明：
     * 1.随着项目越来越大，接口越来越多。一个分组要展示的接口太多了，所以把每个业务包的接口进行分组处理
     * 2.参考lamp-util的swagger配置实现
     */
    @Bean
    fun dynamicDockets(): List<Docket> {
        val configurableBeanFactory = factory as ConfigurableBeanFactory
        val docketList: MutableList<Docket> = mutableListOf()

        // 没有分组
        if(swaggerProperties.docket.isEmpty()) {
            val docket = createDocket(swaggerProperties)
            // 注册Bean
            configurableBeanFactory.registerSingleton(swaggerProperties.title, docket)
            docketList.add(docket)

        }
        // 有分组
        else {
            swaggerProperties.docket.forEach { (title, docketInfo) ->
                // 有basePackage才注册Bean
                if (docketInfo.basePackage != null) {
                    val docket = createDocket(docketInfo, swaggerProperties)
                    // 注册Bean
                    configurableBeanFactory.registerSingleton(title, docket)
                    docketList.add(docket)
                }
            }
        }

        return docketList
    }


    /**
     * 创建自定义的分组接口文档
     *
     * @param docketInfo
     * @param swaggerProperties  其实这个参数可以不写的，不写可能会造成理解上的问题，就写上了
     */
    private fun createDocket(docketInfo: SwaggerProperties.DocketInfo, swaggerProperties: SwaggerProperties): Docket {
        val apiInfo = ApiInfoBuilder()
            .title(docketInfo.getTitleOrDefault(swaggerProperties.title))
            .description(docketInfo.getDescriptionOrDefault(swaggerProperties.description))
            .license(swaggerProperties.license)
            .licenseUrl(swaggerProperties.licenseUrl)
            .termsOfServiceUrl(swaggerProperties.termsOfServiceUrl)
            .contact(Contact(
                swaggerProperties.contact?.name,
                swaggerProperties.contact?.url,
                swaggerProperties.contact?.email,
            )
            )
            .version(swaggerProperties.version)
            .build()

        return Docket(DocumentationType.SWAGGER_2)
            .host(swaggerProperties.host)
            .apiInfo(apiInfo)
            .groupName(apiInfo.title)
            .select()
            .apis(RequestHandlerSelectors.basePackage(docketInfo.basePackage))
            .paths(PathSelectors.any())
            .build()
            // 赋予插件体系 主要是为了让 knife4j.setting配置生效
            .extensions(openApiExtensionResolver.buildExtensions(apiInfo.title))
    }

    /**
     * 创建Default分组接口文档
     *
     * @param swaggerProperties
     */
    private fun createDocket(swaggerProperties: SwaggerProperties): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .host(swaggerProperties.host)
            .apiInfo(apiInfo(swaggerProperties))
            .groupName(swaggerProperties.group)
            .select()
            .apis(RequestHandlerSelectors.basePackage(swaggerProperties.basePackage))
            .paths(PathSelectors.any())
            .build()
            // 赋予插件体系 主要是为了让 knife4j.setting配置生效
            .extensions(openApiExtensionResolver.buildExtensions(swaggerProperties.group))
    }


    /**
     * 获取api信息
     *
     * @param swaggerProperties
     */
    private fun apiInfo(swaggerProperties: SwaggerProperties): ApiInfo {
        return ApiInfoBuilder()
            .title(swaggerProperties.title)
            .description(swaggerProperties.description)
            .license(swaggerProperties.license)
            .licenseUrl(swaggerProperties.licenseUrl)
            .termsOfServiceUrl(swaggerProperties.termsOfServiceUrl)
            .contact(Contact(
                    swaggerProperties.contact?.name,
                    swaggerProperties.contact?.url,
                    swaggerProperties.contact?.email,
                )
            )
            .version(swaggerProperties.version)
            .build()
    }

}
