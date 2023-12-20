package com.zeta.model.quartz.dto

import org.quartz.JobDataMap

/**
 * 任务信息
 *
 * @author gcc
 */
data class JobInfoDTO(

    /** 触发器名称 */
    var triggerName: String? = null,

    /** 触发器组 */
    var triggerGroup: String? = null,

    /** 任务名 */
    var jobName: String? = null,

    /** 任务描述 */
    var jobDescription: String? = null,

    /** 类路径 */
    var jobClassName: String? = null,

    /** 任务参数 */
    var jobParam: String? = null,

    /** 任务参数 */
    var jobDataMap: JobDataMap? = null

)