package org.zetaframework.quartz.builder

import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder

/**
 * Trigger构造器类 (SimpleSchedule)
 *
 * 说明：
 * 每隔一段时间执行一次(时、分、秒、毫秒)，可以设置执行总次数
 *
 * 使用方式：
 * ```
 * // 自定义Trigger，2秒触发一次(触发之后只执行一次)
 * val trigger: Trigger = SimpleTriggerBuilder("testTrigger", 2L).withIntervalInSeconds()
 *
 * // 自定义Trigger，8小时触发一次(触发之后执行3+1次)
 * val trigger: Trigger = SimpleTriggerBuilder(
 *     name = "testTrigger",
 *     timeInterval = 2L,
 *     repeatCount = 3,
 *     groupName = "testTrigger",
 *     description = "测试触发器"
 * ).withIntervalInHours()
 *
 * // 自定义Trigger，15分支触发一次，一直重复
 * val trigger: Trigger = SimpleTriggerBuilder(
 *     name = "testTrigger",
 *     timeInterval = 15L,
 *     repeatForever = true,
 *     groupName = "testTrigger",
 *     description = "测试触发器"
 * ).withIntervalInMinutes()
 * ```
 * @author gcc
 */
class SimpleTriggerBuilder(

    /** 触发器名称 */
    var name: String,

    /** 触发器应该重复的时间间隔 */
    var timeInterval: Long,

    /** 重复次数 */
    var repeatCount: Int? = null,

    /** 是否永远重复执行下去 */
    var repeatForever: Boolean? = null,

    /**
     * 触发器优先级
     *
     * 说明：
     * 当多个 Trigger 具有相同的触发时间时，调度程序将首先触发具有最高优先级的触发
     */
    var priority: Int = Trigger.DEFAULT_PRIORITY,

    /** 触发器组名称 */
    var groupName: String? = Scheduler.DEFAULT_GROUP,

    /** 触发器描述 */
    var description: String? = null,

) {

    /**
     * 构造Trigger
     *
     * @return [Trigger]
     */
    fun build(operation: (simpleScheduler: SimpleScheduleBuilder) -> Unit): Trigger {
        val simpleScheduler = SimpleScheduleBuilder.simpleSchedule()
        // 执行lambda方法
        operation(simpleScheduler)

        // 指定触发器重复的次数  ps:触发的总次数将是这个数字 + 1
        if (repeatCount != null) {
            simpleScheduler.withRepeatCount(repeatCount!!)
        }

        // 无限重复执行触发器
        if (repeatForever != null && repeatForever == true) {
            simpleScheduler.repeatForever()
        }
        return TriggerBuilder.newTrigger()
            .withIdentity(this.name, this.groupName)
            .withDescription(this.description)
            .withPriority(this.priority)
            .withSchedule(simpleScheduler)
            .startNow()
            .build()
    }

    /**
     * 构造 以小时为单位的 Trigger
     *
     * @return [Trigger]
     */
    fun withIntervalInHours(): Trigger {
        return build { simpleScheduler ->
            simpleScheduler.withIntervalInHours(this.timeInterval.toInt())
        }
    }

    /**
     * 构造 以分钟为单位的 Trigger
     *
     * @return [Trigger]
     */
    fun withIntervalInMinutes(): Trigger {
        return build { simpleScheduler ->
            simpleScheduler.withIntervalInMinutes(this.timeInterval.toInt())
        }
    }

    /**
     * 构造 以秒为单位的 Trigger
     *
     * @return [Trigger]
     */
    fun withIntervalInSeconds(): Trigger {
        return build { simpleScheduler ->
            simpleScheduler.withIntervalInSeconds(this.timeInterval.toInt())
        }
    }

    /**
     * 构造 以毫秒为单位的 Trigger
     *
     * @return [Trigger]
     */
    fun withIntervalInMilliseconds(): Trigger {
        return build { simpleScheduler ->
            simpleScheduler.withIntervalInMilliseconds(this.timeInterval)
        }
    }

}
