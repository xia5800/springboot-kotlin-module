package com.zeta.generator.engine

import cn.hutool.core.util.StrUtil
import cn.hutool.json.JSONUtil
import cn.hutool.log.Log
import cn.hutool.log.LogFactory
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.generator.FastAutoGenerator
import com.baomidou.mybatisplus.generator.config.*
import com.baomidou.mybatisplus.generator.config.po.TableField
import com.baomidou.mybatisplus.generator.config.rules.DateType
import com.baomidou.mybatisplus.generator.engine.BeetlTemplateEngine
import com.zeta.generator.enums.EntityTypeEnum
import com.zeta.generator.enums.LanguageTypeEnum
import com.zeta.generator.enums.SwaggerTypeEnum
import java.util.function.Consumer

/**
 * 生成器
 * @author gcc
 */
object Generator {
    private val log: Log = LogFactory.get();

    /** 空字符串，用于join时多拼接一个'/' */
    private val EMPTY_STR = ""

    /** Kotlin or java 文件存放路径 后面需拼接对应的目录 java拼接java，kotlin拼接kotlin */
    private var SOURCE_PATH = "src/main/"

    /** 资源 文件存放路径 */
    private val RESOURCES_SOURCE_PATH = "src/main/resources"

    /** controller 文件存放路径 */
    private val CONTROLLER_PATH = "controller/{}"

    /** service 文件存放路径 */
    private val SERVICE_PATH = "biz/{}/service"

    /** service实现类 文件存放路径 */
    private val SERVICE_IMPL_PATH = "biz/{}/service/impl"

    /** mapper 文件存放路径 */
    private val MAPPER_PATH = "biz/{}/dao"

    /** xml 文件存放路径 */
    private val MAPPER_XML_PATH = "mapper"

    /** entity 文件存放路径 */
    private val ENTITY_PATH = "model/{}/entity"

    /** entityDTO 文件存放路径 */
    private val ENTITY_DTO_PATH = "model/{}/dto"

    /** entityQueryParam 文件存放路径 */
    private val ENTITY_QUERY_PARAM_PATH = "model/{}/param"

    /** 开发语言模板的路径 */
    private var LANGUAGE_TEMPLATE_PATH = ""

    /** 开发语言模板文件的后缀 */
    private var LANGUAGE_FILE_SUFFIX = ""

    /** 存放前端项目启动类的模块名称 */
    private const val FRONT_MODULE_NAME = "zeta-front"
    /** 存放service的模块名称 */
    private const val SERVICE_MODULE_NAME  = "zeta-service"
    /** 存放model的模块名称 */
    private const val MODEL_MODULE_NAME = "zeta-model"

    /**
     * 代码生成
     *
     * 说明：
     * 利用mybatis-plus-generator生成代码
     * 文档:https://baomidou.com/pages/981406/
     * @param config CodeGeneratorConfig
     */
    fun run(config: CodeGeneratorConfig) {
        when(config.languageType) {
            LanguageTypeEnum.KOTLIN -> {
                LANGUAGE_TEMPLATE_PATH = LanguageTypeEnum.KOTLIN.path
                LANGUAGE_FILE_SUFFIX = LanguageTypeEnum.KOTLIN.suffix
                SOURCE_PATH += LanguageTypeEnum.KOTLIN.path
            }
            LanguageTypeEnum.JAVA -> {
                LANGUAGE_TEMPLATE_PATH = LanguageTypeEnum.JAVA.path
                LANGUAGE_FILE_SUFFIX = LanguageTypeEnum.JAVA.suffix
                SOURCE_PATH += LanguageTypeEnum.JAVA.path
            }
        }

        FastAutoGenerator.create(config.dbUrl, config.dbUsername, config.dbPassword)
            // 全局配置
            .globalConfig(globalConfigBuild(config))
            // 包配置
            .packageConfig(packageConfigBuild(config))
            // 策略配置
            .strategyConfig(strategyConfigBuild(config))
            // 模板配置
            .templateConfig(templateConfigBuild(config))
            // 模板引擎配置
            .templateEngine(BeetlTemplateEngine())
            // 注入自定义配置参数
            .injectionConfig(injectionConfigBuild(config))
            .execute()
    }

    /**
     * 全局配置 构造器
     * @param config CodeGeneratorConfig  代码生成器配置参数
     * @return Consumer<GlobalConfig.Builder>
     */
    private fun globalConfigBuild(config: CodeGeneratorConfig): Consumer<GlobalConfig.Builder> =
        Consumer {
            // 代码输出目录 D:/codeGen/zeta-kotlin
            it.outputDir("${config.outputDir}/${config.projectName}")
            it.author(config.author)
            if (!config.openDir) {
                // 禁止打开输出目录
                it.disableOpenDir()
            }
            if(config.languageType == LanguageTypeEnum.KOTLIN) {
                // 开启 kotlin 模式
                it.enableKotlin()
            }
            // 开启 swagger 模式
            it.enableSwagger()
            // 覆盖已有文件
            it.fileOverride()
            // 使用 java8 新时间类型： LocalDateTime、LocalDate、LocalTime
            it.dateType(DateType.TIME_PACK)
            // 指定注释日期格式化
            it.commentDate("yyyy-MM-dd HH:mm:ss")
        }

    /**
     * 包配置 构造器
     * @param config CodeGeneratorConfig  代码生成器配置参数
     * @return Consumer<PackageConfig.Builder>
     */
    private fun packageConfigBuild(config: CodeGeneratorConfig): Consumer<PackageConfig.Builder> = Consumer {
        // 父包名 eg: com.zeta
        it.parent(config.parentPackageName)

        // controller包名 eg: com.zeta.[admin].controller.[system]
        it.controller(StrUtil.format("{}.controller.{}", config.packageName, config.moduleName))
        // mapper包名 eg: com.zeta.biz.[system].dao
        it.mapper(StrUtil.format("biz.{}.dao", config.moduleName))
        // entity包名 eg: com.zeta.model.[system].entity
        it.entity(StrUtil.format("model.{}.entity", config.moduleName))
        // service包名 eg: com.zeta.biz.[system].service
        it.service(StrUtil.format("biz.{}.service", config.moduleName))
        it.serviceImpl(StrUtil.format("biz.{}.service.impl", config.moduleName))

        // 配置路径信息
        it.pathInfo(getPathInfos(config))
    }


    /**
     * 策略配置 构造器
     * @param config CodeGeneratorConfig  代码生成器配置参数
     * @return Consumer<StrategyConfig.Builder>
     */
    private fun strategyConfigBuild(config: CodeGeneratorConfig): Consumer<StrategyConfig.Builder> = Consumer {
        it.addInclude(config.tableInclude!!)
        it.addTablePrefix(*config.tablePrefix!!.toTypedArray())
        // Entity 策略配置
        it.entityBuilder().apply {
            // 如果需要继承父类
            if(!config.superEntity.eq(EntityTypeEnum.NONE)) {
                this.enableActiveRecord()
                this.superClass(config.superEntity.path)
                this.addSuperEntityColumns(*config.superEntity.columns)
            }
        }
            .enableRemoveIsPrefix()
            .enableTableFieldAnnotation()
            .logicDeleteColumnName("deleted")
            .logicDeletePropertyName("deleted")
            .idType(IdType.INPUT)
            .build()

        // Controller 策略配置
        it.controllerBuilder()
            .enableRestStyle()
            .build()

        // Mapper 策略配置
        it.mapperBuilder()
            .enableBaseResultMap()
            .enableBaseColumnList()
            .build()
    }

    /**
     * 模板配置 构造器
     * @return Consumer<TemplateConfig.Builder>
     */
    private fun templateConfigBuild(config: CodeGeneratorConfig): Consumer<TemplateConfig.Builder> = Consumer {
        // 配置模板路径
        it.controller("/templates/${LANGUAGE_TEMPLATE_PATH}/controller.${LANGUAGE_FILE_SUFFIX}")
        it.service("/templates/${LANGUAGE_TEMPLATE_PATH}/service.${LANGUAGE_FILE_SUFFIX}")
        it.serviceImpl("/templates/${LANGUAGE_TEMPLATE_PATH}/serviceImpl.${LANGUAGE_FILE_SUFFIX}")
        if (config.languageType == LanguageTypeEnum.KOTLIN) {
            it.entityKt("/templates/${LANGUAGE_TEMPLATE_PATH}/entity.${LANGUAGE_FILE_SUFFIX}")
        } else {
            it.entity("/templates/${LANGUAGE_TEMPLATE_PATH}/entity.${LANGUAGE_FILE_SUFFIX}")
        }
        it.mapper("/templates/${LANGUAGE_TEMPLATE_PATH}/mapper.${LANGUAGE_FILE_SUFFIX}")
        it.mapperXml("/templates/${LANGUAGE_TEMPLATE_PATH}/mapper.xml")
    }

    /**
     * 注入自定义配置参数  构造器
     * @return Consumer<InjectionConfig.Builder>
     */
    private fun injectionConfigBuild(config: CodeGeneratorConfig): Consumer<InjectionConfig.Builder> = Consumer {
        // 输出文件之前处理
        it.beforeOutputFile { tableInfo, objectMap ->
            log.info(JSONUtil.toJsonStr(tableInfo))
            log.info(JSONUtil.toJsonStr(objectMap))

            // 修改表名，去掉表名xxx表中的`表`字
            val comment = StrUtil.removeSuffix(tableInfo.comment, "表")
            tableInfo.comment = comment

            // 这里也可以配置自定义模板的参数
            val entityName = tableInfo.entityName
            val entityDTOName = entityName + "DTO"
            val entitySaveDTOName = entityName + "SaveDTO"
            val entityUpdateDTOName = entityName + "UpdateDTO"
            val entityQueryParamName = entityName + "QueryParam"
            val lowFirstCharEntityName = entityName.lowCaseKeyFirstChar()
            // dtoBasePackagePath：com.zeta.model.system.dto.sysUser
            val dtoBasePackagePath = "${config.parentPackageName}.model.${config.moduleName}.dto.${lowFirstCharEntityName}"
            // paramBasePackagePath: com.zeta.model.system.param
            val paramBasePackagePath = "${config.parentPackageName}.model.${config.moduleName}.param"
            val customTemplateParam: MutableMap<String, String> = mutableMapOf(
                // DTO类、QueryParam类类名
                "entityDTO" to entityDTOName,
                "entitySaveDTO" to entitySaveDTOName,
                "entityUpdateDTO" to entityUpdateDTOName,
                "entityQueryParam" to entityQueryParamName,
                // DTO类、QueryParam类包路径
                "dtoPackagePath" to dtoBasePackagePath,
                "paramBasePackagePath" to paramBasePackagePath,
            )
            // 自定义dto类名、包路径
            objectMap["customPackage"] = customTemplateParam

            // 获取权限码  表名：sys_user -> sys:user
            val authorityCode = tableInfo.name.split("_").joinToString(":")
            objectMap["authorityCode"] = authorityCode

            // 特有的字段处理 TreeEntity、StateEntity特有字段处理
            objectMap["uniqueColumns"] = uniqueFieldHandler(config, tableInfo.commonFields)

            // 是否将javax包替换成 jakarta包
            objectMap["isJakarta"] = config.jdkVersion >= 17
            // swagger是否使用springfox注解
            objectMap["isSpringFox"] = config.swaggerType === SwaggerTypeEnum.SPRING_FOX

            // 判断是否有BigDecimal
            objectMap["bigDecimalPackage"] = if (tableInfo.fields.any { it.columnType.type == "BigDecimal" }) {
                "java.math.BigDecimal"
            } else ""

            /**
             * entityDTO :          【d://codeGen】/【zeta-kotlin-module】/【zeta-model】/【zeta-model-】{modelName}/src/main/【kotlin or java】/【com/zeta】/model/{modelName}/dto/【tableName】
             * entitySaveDTO :      【d://codeGen】/【zeta-kotlin-module】/【zeta-model】/【zeta-model-】{modelName}/src/main/【kotlin or java】/【com/zeta】/model/{modelName}/dto/【tableName】
             * entityUpdateDTO :    【d://codeGen】/【zeta-kotlin-module】/【zeta-model】/【zeta-model-】{modelName}/src/main/【kotlin or java】/【com/zeta】/model/{modelName}/【tableName】
             * entityQueryParam ：  【d://codeGen】/【zeta-kotlin-module】/【zeta-model】/【zeta-model-】{modelName}/src/main/【kotlin or java】/【com/zeta】/model/{modelName}/param
             */
            // 包路径  例如：com/zeta
            val packagePath = config.parentPackageName.split(".").joinToString("/")
            // 基础路径             zeta-model         zeta-model-system                           src/main/kotlin  com/zeta
            val basePath = listOf(MODEL_MODULE_NAME, "${MODEL_MODULE_NAME}-${config.moduleName}", SOURCE_PATH,     packagePath).joinToString("/")
            // entityDTOPath: 【d://codeGen】/【zeta-kotlin-module】/【zeta-model】/【zeta-model-】{modelName}/src/main/【kotlin or java】/【com/zeta】/model/{moduleName}/dto/tableName
            val entityDTOPath = listOf(
                basePath,
                "${StrUtil.format(ENTITY_DTO_PATH, config.moduleName)}/${lowFirstCharEntityName}"
            ).joinToString("/")
            // paramPath: 【d://codeGen】/【zeta-kotlin-module】/【zeta-model】/【zeta-model-】{modelName}/src/main/【kotlin or java】/【com/zeta】/model/{moduleName}/param/tabNameQueryName.kt
            val paramPath = listOf(
                basePath,
                StrUtil.format(ENTITY_QUERY_PARAM_PATH, config.moduleName),
                "${entityQueryParamName}.${LANGUAGE_FILE_SUFFIX}"
            ).joinToString("/")
            // 在这里配置自定义模板的位置和生成出来的文件名
            it.customFile(mutableMapOf<String, String>(
                // ps：由于上面配置了other目录的pathInfo。所以这里的key（即文件名）要配一个路径。为什么要`../`相对路径是因为不这样会创建一个和表同名的文件夹
                "../../../../../../${entityDTOPath}/${entityDTOName}.${LANGUAGE_FILE_SUFFIX}" to "/templates/${LANGUAGE_TEMPLATE_PATH}/entityDTO.${LANGUAGE_FILE_SUFFIX}.btl",
                "../../../../../../${entityDTOPath}/${entitySaveDTOName}.${LANGUAGE_FILE_SUFFIX}" to "/templates/${LANGUAGE_TEMPLATE_PATH}/entitySaveDTO.${LANGUAGE_FILE_SUFFIX}.btl",
                "../../../../../../${entityDTOPath}/${entityUpdateDTOName}.${LANGUAGE_FILE_SUFFIX}" to "/templates/${LANGUAGE_TEMPLATE_PATH}/entityUpdateDTO.${LANGUAGE_FILE_SUFFIX}.btl",
                "../../../../../../${paramPath}" to "/templates/${LANGUAGE_TEMPLATE_PATH}/param.${LANGUAGE_FILE_SUFFIX}.btl",
            ))
        }

        // 在这里配置自定义的模板参数 。可以直接在模板中使用${packageName}取值
        it.customMap(mutableMapOf<String, Any>(
            "packageName" to "${config.parentPackageName}.${config.packageName}",
            "timePackage" to "java.time.*",
            "repositoryAnnotation" to config.enableRepository
        ))
    }

    /**
     * 特有的字段处理
     *
     * 说明：
     * 像TreeEntity、StateEntity这样的类，有一些特有的字段（除了id、create_time...之外的字段）。例如：'label'、'parent_id'、'state'
     * 在生成SaveDTO、UpdateDTO的时候不会生成进来。所以需要特别处理
     *
     * @param config 代码生成器配置参数
     * @param tableInfo  表信息，关联到当前字段信息
     */
    private fun uniqueFieldHandler(config: CodeGeneratorConfig, commonFields: List<TableField>): List<TableField> {
        var uniqueColumns: List<TableField> = mutableListOf()

        when(config.superEntity) {
            EntityTypeEnum.TREE_ENTITY -> {
                uniqueColumns = commonFields.filter {
                    // 筛选出字段名在特有字段里面的
                    it.columnName in EntityTypeEnum.TREE_ENTITY.uniqueColumns!!.toList()
                }
            }
            EntityTypeEnum.STATE_ENTITY -> {
                uniqueColumns = commonFields.filter {
                    // 筛选出字段名在特有字段里面的
                    it.columnName in EntityTypeEnum.STATE_ENTITY.uniqueColumns!!.toList()
                }
            }
            else -> {}
        }

        return uniqueColumns
    }

    /**
     * 获取生成文件的路径信息
     *
     * @param config CodeGeneratorConfig  代码生成器配置参数
     * @return MutableMap<OutputFile, String>
     */
    private fun getPathInfos(config: CodeGeneratorConfig): MutableMap<OutputFile, String> {
        // 配置什么文件生成在什么地方
        // 包路径  例如：com/zeta
        val packagePath = config.parentPackageName.split(".").joinToString("/")
        // basePath: 【d://codeGen】/【zeta-kotlin-module】/src/main/【kotlin or java】/【com/zeta】
        val basePath = listOf(config.outputDir, config.projectName, SOURCE_PATH, packagePath).joinToString("/")

        // controller: 【d://codeGen】/【zeta-kotlin-module】/【zeta-front】/【zeta-admin-server】/src/main/【kotlin or java】/【com/zeta/】【admin/】controller/{modelName}
        val controllerPath = listOf(
            //      d://codeGen      zeta-kotlin-module  zeta-front         zeta-admin-server src/main/kotlin  com/zeta     admin
            listOf(config.outputDir, config.projectName, FRONT_MODULE_NAME, config.frontName, SOURCE_PATH,     packagePath, config.packageName).joinToString("/"),
            // controller/system
            StrUtil.format(CONTROLLER_PATH, config.moduleName),
            EMPTY_STR
        ).joinToString("/")

        // entity: 【d://codeGen】/【zeta-kotlin-module】/【zeta-model】/【zeta-model-】{modelName}/src/main/【kotlin or java】/【com/zeta】/model/{modelName}/entity/
        val entityPath = listOf(
            //     d://codeGen       zeta-kotlin-module  zeta-model         zeta-model-system                            src/main/kotlin  com/zeta
            listOf(config.outputDir, config.projectName, MODEL_MODULE_NAME, "${MODEL_MODULE_NAME}-${config.moduleName}", SOURCE_PATH,     packagePath).joinToString("/"),
            // model/system/entity
            StrUtil.format(ENTITY_PATH, config.moduleName),
            EMPTY_STR
        ).joinToString("/")

        // service基础路径             d://codeGen       zeta-kotlin-module  zeta-service         zeta-biz-system                  src/main/kotlin  com/zeta
        val serviceBasePath = listOf(config.outputDir, config.projectName, SERVICE_MODULE_NAME, "zeta-biz-${config.moduleName}", SOURCE_PATH,     packagePath).joinToString("/")
        // service: 【d://codeGen】/【zeta-kotlin-module】/【zeta-service】/【zeta-biz-】{modelName}/src/main/【kotlin or java】/【com/zeta/】biz/service/
        val servicePath = listOf(serviceBasePath, StrUtil.format(SERVICE_PATH, config.moduleName), EMPTY_STR).joinToString("/")
        // serviceImpl: 【d://codeGen】/【zeta-kotlin-module】/【zeta-service】/【zeta-biz-】{modelName}/src/main/【kotlin or java】/【com/zeta/】biz/service/impl/
        val serviceImplPath = listOf(serviceBasePath, StrUtil.format(SERVICE_IMPL_PATH, config.moduleName), EMPTY_STR).joinToString("/")
        // dao: 【d://codeGen】/【zeta-kotlin-module】/【zeta-service】/【zeta-biz-】{modelName}/src/main/【kotlin or java】/【com/zeta/】/biz/dao/
        val mapperPath = listOf(serviceBasePath, StrUtil.format(MAPPER_PATH, config.moduleName), EMPTY_STR).joinToString("/")

        // mapper.xml: 【d://codeGen】/【zeta-kotlin-module】/【zeta-service】/【zeta-biz-】{modelName}/src/main/resources/mapper_{modelName}
        val xmlPath = listOf(
            //     d://codeGen       zeta-kotlin-module  zeta-service         zeta-biz-system
            listOf(config.outputDir, config.projectName, SERVICE_MODULE_NAME, "zeta-biz-${config.moduleName}").joinToString("/"),
            RESOURCES_SOURCE_PATH, MAPPER_XML_PATH, config.moduleName
        ).joinToString("/")

        return mutableMapOf(
            OutputFile.controller to controllerPath,
            OutputFile.entity to entityPath,
            OutputFile.mapper to mapperPath,
            OutputFile.mapperXml to xmlPath,
            OutputFile.service to servicePath,
            OutputFile.serviceImpl to serviceImplPath,
            OutputFile.other to basePath,
        )
    }

    /**
     * 首字母小写
     *
     * 说明：这是一个String类的扩展函数
     * @param key String?
     * @return String?
     */
    private fun String.lowCaseKeyFirstChar(): String {
        return if (Character.isLowerCase(this[0])) {
            this
        } else {
            StringBuilder().append(Character.toLowerCase(this[0])).append(this.substring(1)).toString()
        }
    }

}
