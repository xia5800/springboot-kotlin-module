package org.zetaframework.core.enums

/**
 * QuartzTask定时任务类
 *
 * @author gcc
 */
enum class QuartzJobEnum(
    /** 类路径 */
    val clazzPath: String,
    /** 类描述 */
    val description: String,
    /** 是否返回给前端 */
    val isShow: Boolean
) {

    /** 定时任务 案例 */
    DEMO_JOB("com.zeta.biz.quartz.task.DemoJob", "案例Job", true),

    /** 定时任务 清除操作日志 */
    CLEAN_OPT_LOG_JOB("com.zeta.biz.quartz.task.CleanOptLogJob", "清除操作日志", true),

    ;
}
