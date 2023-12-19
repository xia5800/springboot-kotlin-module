package org.zetaframework.quartz.builder

import org.quartz.*
import org.zetaframework.base.exception.ArgumentException
import java.util.Calendar

/**
 * Trigger构造器类 (DailyTimeIntervalSchedule)
 *
 * 说明：
 * 指定一周的哪几天、什么时间段、执行间隔。用这个构造器
 *
 * 使用方式：
 * ```
 * // 自定义Trigger: 每天执行
 * val trigger: Trigger = DailyTimeIntervalTriggerBuilder("testTrigger", description="每天执行").onEveryDay()
 *
 * // 自定义Trigger: 周一到周五执行
 * val trigger: Trigger = DailyTimeIntervalTriggerBuilder("testTrigger", description="周一到周五执行").onMondayThroughFriday()
 *
 * // 自定义Trigger: 周六周日执行
 * val trigger: Trigger = DailyTimeIntervalTriggerBuilder("testTrigger", description="周六周日执行").onSaturdayAndSunday()
 *
 * // 自定义Trigger: 周一到周五, 08:00:00-20:00:00执行
 * val trigger: Trigger = DailyTimeIntervalTriggerBuilder(
 *     name = "testTrigger",
 *     startingDailyAt = TimeOfDay(8, 00),
 *     endingDailyAt = TimeOfDay(20, 00),
 *     description = "周一到周五, 08:00:00-20:00:00执行"
 * ).onMondayThroughFriday()
 *
 * // 自定义Trigger: 周一、周三、周五，06:00-06:30执行, 每5分钟执行一次
 * val trigger: Trigger = DailyTimeIntervalTriggerBuilder(
 *     name = "testTrigger",
 *     timeInterval = 5,
 *     unit = DailyTimeIntervalTriggerBuilder.IntervalUnit.MINUTE,
 *     startingDailyAt = TimeOfDay(6, 0),
 *     endingDailyAt = TimeOfDay(6, 30),
 *     description = "周一、周三、周五，06:00-06:30执行, 每5分钟执行一次"
 * ).onDaysOfTheWeek(mutableListOf(
 *     // 有亿点长。。。以后再优化
 *     DailyTimeIntervalTriggerBuilder.DaysOfWeek.MONDAY,
 *     DailyTimeIntervalTriggerBuilder.DaysOfWeek.WEDNESDAY,
 *     DailyTimeIntervalTriggerBuilder.DaysOfWeek.FRIDAY,
 * ))
 * ```
 * @author gcc
 */
data class DailyTimeIntervalTriggerBuilder(
    /** 触发器名称 */
    var name: String,

    /** 触发器应该重复的时间间隔 */
    var timeInterval: Int? = null,

    /** 间隔的时间单位  范围：时分秒 */
    var unit: IntervalUnit? = null,

    /** 每天开始于 */
    var startingDailyAt: TimeOfDay? = null,

    /** 每天结束于 */
    var endingDailyAt: TimeOfDay? = TimeOfDay(23, 59, 59),

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
     * 执行间隔单位 枚举
     *
     * 说明：
     * DailyTimeIntervalSchedule类的执行间隔单位只能选时、分、秒。
     * 为了限制用户不乱传值，自定义了一个枚举类
     *
     * @author gcc
     */
    enum class IntervalUnit(val value: DateBuilder.IntervalUnit) {
        /** 时 */
        HOUR(DateBuilder.IntervalUnit.HOUR),
        /** 分 */
        MINUTE(DateBuilder.IntervalUnit.MINUTE),
        /** 秒 */
        SECOND(DateBuilder.IntervalUnit.SECOND);
    }

    /**
     * 星期 枚举
     * 说明：
     * DailyTimeIntervalSchedule类的onDaysOfTheWeek()方法只能传星期对应的int值。
     * 为了限制用户不乱传值，自定义了一个枚举类
     *
     * @author gcc
     */
    enum class DaysOfWeek(val value: Int) {
        /** 星期日 */
        SUNDAY(Calendar.SUNDAY),
        /** 星期一 */
        MONDAY(Calendar.MONDAY),
        /** 星期二 */
        TUESDAY(Calendar.TUESDAY),
        /** 星期三 */
        WEDNESDAY(Calendar.WEDNESDAY),
        /** 星期四 */
        THURSDAY(Calendar.THURSDAY),
        /** 星期五 */
        FRIDAY(Calendar.FRIDAY),
        /** 星期六 */
        SATURDAY(Calendar.SATURDAY);
    }

    /**
     * 构造Trigger
     *
     * 说明：
     * 1.从下面几个onXXX()方法可以看出，为了消除大量的模板代码，build方法接收一个lambda参数。该参数用于对DailyTimeIntervalSchedule类进行配置
     * 2.kotlin的特性, 如果方法的最后一个参数是一个lambda。lambda可以放在圆括号之外
     *
     * 使用方式：
     * ```
     * build { dailyTimeIntervalSchedule ->
     *     // 将触发器设置为一周内每天都触发
     *     dailyTimeIntervalSchedule.onEveryDay()
     * }
     * ```
     * @param operation Lambda表达式。 传入你要执行的操作
     * @return [Trigger]
     */
    fun build(operation: (dailyTimeIntervalSchedule: DailyTimeIntervalScheduleBuilder) -> Unit): Trigger {
        val dailyTimeIntervalSchedule = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
        // 执行lambda方法
        operation(dailyTimeIntervalSchedule)

        // 设置触发间隔
        if (timeInterval != null && unit != null) {
            dailyTimeIntervalSchedule.withInterval(timeInterval!!, unit!!.value)
        }

        // 设置每天于几点执行
        if (startingDailyAt != null) {
            dailyTimeIntervalSchedule.startingDailyAt(startingDailyAt)
        }

        // 设置每日于几点结束执行
        if (endingDailyAt != null) {
            dailyTimeIntervalSchedule.endingDailyAt(endingDailyAt)
        }

        return TriggerBuilder.newTrigger()
            .withIdentity(this.name, this.groupName)
            .withDescription(this.description)
            .withPriority(this.priority)
            .withSchedule(dailyTimeIntervalSchedule)
            .startNow()
            .build()
    }

    /**
     * 构造 一周内给定日期执行的 Trigger
     *
     * @return [Trigger]
     */
    fun onDaysOfTheWeek(daysOfWeek: List<DaysOfWeek>): Trigger {
        if (daysOfWeek.isEmpty()) {
            throw ArgumentException("参数错误，触发器构造失败")
        }

        // 提取List中，每个DaysOfWeek对象的value值
        val onDaysOfWeek = daysOfWeek.map { it.value }
        return build {
            // 将触发器设置为在一周中的给定日期触发
            it.onDaysOfTheWeek(*onDaysOfWeek.toTypedArray())
        }
    }


    /**
     * 构造 星期一到星期五期间执行的 Trigger
     *
     * @return [Trigger]
     */
    fun onMondayThroughFriday(): Trigger {
        return build {
            // 将触发器设置为周一到周五触发
            it.onMondayThroughFriday()
        }
    }

    /**
     * 构造 只在周六周日执行的 Trigger
     *
     * @return [Trigger]
     */
    fun onSaturdayAndSunday(): Trigger {
        return build {
            // 将触发器设置为只在周六和周日触发
            it.onSaturdayAndSunday()
        }
    }


    /**
     * 构造 一周内每天都执行的 Trigger
     *
     * @return [Trigger]
     */
    fun onEveryDay(): Trigger {
        return build {
            // 将触发器设置为一周内每天都触发
            it.onEveryDay()
        }
    }

}
