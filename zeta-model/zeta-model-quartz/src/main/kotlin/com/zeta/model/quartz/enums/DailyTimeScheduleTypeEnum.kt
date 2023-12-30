package com.zeta.model.quartz.enums

/**
 * DailyTime类型调度器 执行类型枚举
 *
 * @author gcc
 */
enum class DailyTimeScheduleTypeEnum {
    /** 每天执行 */
    EveryDay,
    /** 每周六和周日执行 */
    SaturdayAndSunday,
    /** 每周一到周五执行 */
    MondayThroughFriday,
    /** 自定义周几执行 */
    DaysOfTheWeek,
    ;
}