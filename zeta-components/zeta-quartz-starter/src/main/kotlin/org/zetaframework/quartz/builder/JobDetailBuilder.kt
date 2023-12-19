package org.zetaframework.quartz.builder

import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.Scheduler

/**
 * JobDetail构造器类
 *
 * 使用方式：
 * ```
 * // 简单的JobDetail
 * val jobDetail: JobDetail = JobDetailBuilder("testJob").build()
 *
 * // 有描述的JobDetail
 * val jobDetail: JobDetail = JobDetailBuilder("testJob", description = "测试用定时任务").build()
 *
 * // 带参数的JobDetail
 * val jobDetail: JobDetail = JobDetailBuilder("testJob", param = mutableMapOf("key1" to "hello", "key2" to "world")).build()
 * ```
 * @author gcc
 */
class JobDetailBuilder(

    /** 任务名称 */
    var name: String,

    /** 任务类，必须实现Job接口 */
    var jobClazz: Class<Job>,

    /** 任务组名称 */
    var groupName: String = Scheduler.DEFAULT_GROUP,

    /** 任务描述 */
    var description: String? = null,

    /** 任务参数 */
    var param: MutableMap<String, Any?>? = mutableMapOf()

) {

    /**
     * 构造JobDetail对象
     *
     * @return [JobDetail]
     */
    fun build(): JobDetail {
        // 创建jobDetail实例
        val jobDetail = JobBuilder.newJob(this.jobClazz)
            .withIdentity(this.name, this.groupName)
            .withDescription(this.description)
            .build()

        // job参数
        if (this.param != null) {
            jobDetail.jobDataMap.putAll(this.param!!)
        }
        return jobDetail
    }

}
