package org.zetaframework.quartz.builder

import org.quartz.*

/**
 * Trigger构造器类 (CalendarIntervalSchedule)
 *
 * 说明：
 * 每隔一段时间执行一次(年、月、日、时、分、秒、毫秒)
 *
 * 使用方式：
 * ```
 * // 自定义Trigger，一天触发一次
 * val trigger: Trigger = CalendarIntervalTriggerBuilder("testTrigger", 1, DateBuilder.IntervalUnit.DAY).build()
 *
 * // 自定义Trigger组名
 * val trigger: Trigger = CalendarIntervalTriggerBuilder("testTrigger", 1, DateBuilder.IntervalUnit.DAY, groupName = "trigger").build()
 *
 * // 有描述的Trigger
 * val trigger: Trigger = CalendarIntervalTriggerBuilder("testTrigger", 1, DateBuilder.IntervalUnit.DAY, description = "测试触发器,一天触发一次").build()
 * ```
 * @author gcc
 */
data class CalendarIntervalTriggerBuilder(

    /** 触发器名称 */
    var name: String,

    /** 触发器应该重复的时间间隔 */
    var timeInterval: Int,

    /** 间隔的时间单位 */
    var unit: DateBuilder.IntervalUnit,

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
    var description: String? = null

) {

    /**
     * 构造Trigger
     *
     * @return [Trigger]
     */
    fun build(): Trigger {
        val calendarIntervalSchedule = CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
        calendarIntervalSchedule.withInterval(this.timeInterval, this.unit)

        return TriggerBuilder.newTrigger()
            .withIdentity(this.name, this.groupName)
            .withDescription(this.description)
            .withPriority(this.priority)
            .withSchedule(calendarIntervalSchedule)
            .startNow()
            .build()
    }

}
