package org.zetaframework.log.event

import org.springframework.context.ApplicationEvent
import org.zetaframework.core.model.LoginLogDTO

/**
 * 登录日志 事件
 *
 * 说明：
 * 在[LoginLogListener]中处理本事件
 * @author gcc
 */
class LoginEvent(source: LoginLogDTO): ApplicationEvent(source) {

}