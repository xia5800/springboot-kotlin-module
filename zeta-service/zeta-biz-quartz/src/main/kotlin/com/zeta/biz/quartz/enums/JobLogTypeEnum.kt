package com.zeta.biz.quartz.enums

/**
 * 定时任务日志类型 枚举
 *
 * @author gcc
 */
enum class JobLogTypeEnum(val desc: String) {
    /** 成功日志 */
    SUCCESS("success"),
    /** 异常日志 */
    EXCEPTION("exception"),
    ;
}