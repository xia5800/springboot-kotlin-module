package org.zetaframework.log.event

import org.springframework.context.ApplicationEvent
import org.zetaframework.core.model.LogDTO

/**
 * 系统日志 事件
 *
 * 说明：
 * 在[LogListener]中处理本事件
 * @author gcc
 */
class LogEvent(source: LogDTO): ApplicationEvent(source) {

}

