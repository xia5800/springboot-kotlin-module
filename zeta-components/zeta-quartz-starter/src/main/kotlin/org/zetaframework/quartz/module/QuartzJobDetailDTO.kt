package org.zetaframework.quartz.module

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.util.*

/**
 * 任务 详情
 *
 * @author gcc
 */
@ApiModel(description = "任务详情")
class QuartzJobDetailDTO {

    /** 触发器名称 */
    @ApiModelProperty(value = "触发器名称")
    var triggerName: String? = null

    /** 触发器组名称 */
    @ApiModelProperty(value = "触发器组名称")
    var triggerGroupName: String? = null

    /** 触发器描述 */
    @ApiModelProperty(value = "触发器描述")
    var triggerDescription: String? = null

    /** 下次触发时间 */
    @ApiModelProperty(value = "下次触发时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    var nextFireTime: Date? = null

    /** 上次触发时间 */
    @ApiModelProperty(value = "上次触发时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    var prevFireTime: Date? = null

    /** 触发器优先级 */
    @ApiModelProperty(value = "触发器优先级")
    var priority: Int? = null

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    var startTime: Date? = null

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    var endTime: Date? = null

    /**
     * 触发器状态
     *
     * 状态值说明：
     *   null: 获取状态失败
     *   NONE: 触发器不存在或被删除
     *   NORMAL: 正常
     *   PAUSED: 暂停
     *   COMPLETE: 完成
     *   ERROR: 错误
     *   BLOCKED: 阻塞
     */
    @ApiModelProperty(value = "触发器状态", allowableValues = "null,NONE,NORMAL,PAUSED,COMPLETE,ERROR,BLOCKED")
    var triggerState: String? = null

    /** 触发器类型 */
    @ApiModelProperty(value = "触发器类型")
    var triggerType: String? = null


    /** 任务名称 */
    @ApiModelProperty(value = "任务名称")
    var jobName: String? = null

    /** 任务组名称 */
    @ApiModelProperty(value = "任务组名称")
    var jobGroupName: String? = null

    /** 任务描述 */
    @ApiModelProperty(value = "任务描述")
    var jobDescription: String? = null

    /** 任务执行类 */
    @ApiModelProperty(value = "任务执行类")
    var jobClassName: String? = null

    /** 任务参数 */
    @ApiModelProperty(value = "任务参数")
    var jobParam: String? = null

    /** cron表达式，仅当triggerType=CRON的时候才有值 */
    @ApiModelProperty(value = "cron表达式，仅当triggerType=CRON的时候才有值")
    var cron: String? = null
}
