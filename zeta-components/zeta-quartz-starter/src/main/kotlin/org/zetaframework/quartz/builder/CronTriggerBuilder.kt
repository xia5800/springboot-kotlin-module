package org.zetaframework.quartz.builder

import org.quartz.CronScheduleBuilder
import org.quartz.Scheduler
import org.quartz.Trigger
import org.quartz.TriggerBuilder

/**
 * Trigger构造器类 (CronSchedule)
 *
 * 使用方式：
 * ```
 * // 简单的CronTrigger
 * val trigger: Trigger = CronTriggerBuilder("testTrigger", "0/1 * * * * ?").build()
 *
 * // 自定义Trigger组名
 * val trigger: Trigger = CronTriggerBuilder("testTrigger", "0/1 * * * * ?", groupName = "testTrigger").build()
 *
 * // 有描述的Trigger
 * val trigger: Trigger = CronTriggerBuilder("testTrigger", "0/1 * * * * ?", description = "测试触发器").build()
 * ```
 * @author gcc
 */
class CronTriggerBuilder(

    /** 触发器名称 */
    var name: String,

    /** cron表达式 */
    var cron: String,

    /**
     * 触发器优先级
     *
     * 说明：
     * 当多个 Trigger 具有相同的触发时间时，调度程序将首先触发具有最高优先级的触发
     */
    var priority: Int = Trigger.DEFAULT_PRIORITY,

    /** 触发器组名称 */
    var groupName: String = Scheduler.DEFAULT_GROUP,

    /** 触发器描述 */
    var description: String? = null,

) {

    /**
     * 构造Trigger
     *
     * @return [Trigger]
     */
    fun build(): Trigger {
        return TriggerBuilder.newTrigger()
            .withIdentity(this.name, this.groupName)
            .withDescription(this.description)
            .withPriority(this.priority)
            .withSchedule(CronScheduleBuilder.cronSchedule(this.cron))
            .startNow()
            .build()
    }

}
