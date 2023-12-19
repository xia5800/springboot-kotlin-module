package org.zetaframework.quartz.utils

import org.quartz.Trigger
import org.quartz.impl.jdbcjobstore.Constants

/**
 * Quartz工具类
 * @author gcc
 */
object QuartzUtil {


    /**
     * 将数据库中触发器状态 转换成 Quartz TriggerState枚举对应的状态
     *
     * @param state 数据库中触发器对应的状态
     */
    fun jdbcTriggerTypeConvert(state: String?): String {
        if (state.isNullOrBlank()) return Trigger.TriggerState.NONE.name

        return when(state) {
            Constants.STATE_DELETED -> Trigger.TriggerState.NONE.name
            Constants.STATE_COMPLETE -> Trigger.TriggerState.COMPLETE.name
            Constants.STATE_PAUSED, Constants.STATE_PAUSED_BLOCKED -> Trigger.TriggerState.PAUSED.name
            Constants.STATE_ERROR -> Trigger.TriggerState.ERROR.name
            Constants.STATE_BLOCKED -> Trigger.TriggerState.BLOCKED.name
            else -> Trigger.TriggerState.NORMAL.name
        }
    }
}
