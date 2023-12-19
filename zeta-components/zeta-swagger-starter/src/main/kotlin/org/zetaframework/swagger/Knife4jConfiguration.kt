package org.zetaframework.swagger

import cn.hutool.core.util.StrUtil
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.util.ClassUtils
import org.zetaframework.core.properties.SwaggerProperties
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration
import springfox.documentation.RequestHandler
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc
import java.util.*
import java.util.function.Predicate

/**
 * Knife4j 配置
 *
 * @author gcc
 */
@Configuration
@ConditionalOnWebApplication  // 在web工程条件下成立
@EnableSwagger2WebMvc
@Import(BeanValidatorPluginsConfiguration::class)
@EnableConfigurationProperties(SwaggerProperties::class)
class Knife4jConfiguration(
    private val swaggerProperties: SwaggerProperties,
    private val openApiExtensionResolver: OpenApiExtensionResolver
) : BeanFactoryAware {

    companion object {
        /** 分号 */
        private const val SEMICOLON = ";"
    }

    private lateinit var factory: BeanFactory

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
                if (!docketInfo.basePackage.isNullOrBlank()) {
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
            ))
            .version(swaggerProperties.version)
            .build()

        return Docket(DocumentationType.SWAGGER_2)
            .host(swaggerProperties.host)
            .apiInfo(apiInfo)
            .groupName(apiInfo.title)
            .select()
            .apis(handlerBasePackage(docketInfo.basePackage!!))
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
            .apis(handlerBasePackage(swaggerProperties.basePackage))
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
            ))
            .version(swaggerProperties.version)
            .build()
    }

    /**
     * Predicate that matches RequestHandler with given base package name for the class of the handler method.
     * This predicate includes all request handlers matching the provided basePackage
     *
     * 说明：
     * 1.替换原来的'RequestHandlerSelectors.basePackage()'方法。
     * 2.支持base-package: com.xxx.xxx.xxxController;com.xxx.xxx.xxxController;com.xxx.xxx.xxxController 这种写法
     *
     * @param basePackage - base package of the classes
     * @return this
     */
    private fun handlerBasePackage(basePackage: String): Predicate<RequestHandler> {
        return Predicate { input: RequestHandler ->
            Optional.ofNullable(input.declaringClass()).map { clazz: Class<*> ->
                StrUtil.split(basePackage, SEMICOLON).any {
                    ClassUtils.getPackageName(clazz).startsWith(it)
                }
            }.orElse(true)
        }
    }

}
