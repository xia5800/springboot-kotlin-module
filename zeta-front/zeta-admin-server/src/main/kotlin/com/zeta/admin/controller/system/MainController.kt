package com.zeta.admin.controller.system

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.StrUtil
import com.github.xiaoymin.knife4j.annotations.ApiSupport
import com.wf.captcha.SpecCaptcha
import com.zeta.biz.system.service.ISysUserService
import com.zeta.model.system.cacheKey.CaptchaStringCacheKey
import com.zeta.model.system.entity.SysUser
import com.zeta.model.system.enums.UserStateEnum
import com.zeta.model.system.param.LoginParam
import com.zeta.model.system.result.CaptchaResult
import com.zeta.model.system.result.LoginResult
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.zetaframework.annotation.redis.Limit
import org.zetaframework.base.result.ApiResult
import org.zetaframework.controller.SuperSimpleController
import org.zetaframework.core.enums.LoginStateEnum
import org.zetaframework.core.model.LoginLogDTO
import org.zetaframework.core.utils.ContextUtil
import org.zetaframework.crypto.helper.AESHelper
import org.zetaframework.log.event.LoginEvent
import javax.servlet.http.HttpServletRequest

/**
 * 登录认证
 * @author gcc
 */
@ApiSupport(order = 1)
@Api(tags = ["登录认证"])
@RestController
@RequestMapping("/api")
class MainController(
    private val applicationContext: ApplicationContext,
    private val captchaCacheKey: CaptchaStringCacheKey,
    private val aseHelper: AESHelper
): SuperSimpleController<ISysUserService, SysUser>() {

    @Value("\${spring.profiles.active:prod}")
    private val env: String? = null


    /**
     * 用户登录
     *
     * @param param 登录参数
     * @param request HttpServletRequest
     * @return ApiResult<LoginResult>
     */
    @ApiOperation(value = "登录")
    @PostMapping("/login")
    fun login(@RequestBody @Validated param: LoginParam, request: HttpServletRequest): ApiResult<LoginResult> {
        // 验证验证码
        val verifyCode = captchaCacheKey.get<String>(param.key)
        if (StrUtil.isBlank(verifyCode)) {
            return fail("验证码过期")
        }
        if (!param.code.equals(verifyCode, true)) {
            return fail("验证码错误")
        }
        captchaCacheKey.delete(param.key)

        // 查询用户, 因为账号已经判空过了所以这里直接param.account!!
        val user = service.getByAccount(param.account!!) ?: return fail("用户不存在")
        // 设置用户id，方便记录日志的时候设置创建人。
        ContextUtil.setUserId(user.id!!)

        // 密码解密
        val password = try {
            aseHelper.decryptStr(param.password!!)
        } catch (e: Exception) {
            ""
        }

        // 比较密码
        if (!service.comparePassword(password, user.password!!)) {
            applicationContext.publishEvent(
                LoginEvent(
                LoginLogDTO.loginFail(
                param.account!!, LoginStateEnum.ERROR_PWD, request
            ))
            )
            // 密码不正确
            return fail(LoginStateEnum.ERROR_PWD.desc)
        }
        // 判断用户状态
        if (user.state == UserStateEnum.FORBIDDEN.code) {
            applicationContext.publishEvent(
                LoginEvent(
                LoginLogDTO.loginFail(
                param.account!!, LoginStateEnum.FAIL, "用户被禁用，无法登录", request
            ))
            )
            return fail("用户被禁用，无法登录")
        }

        // 踢人下线并登录
        StpUtil.kickout(user.id)
        StpUtil.login(user.id)

        // 登录日志
        applicationContext.publishEvent(LoginEvent(LoginLogDTO.loginSuccess(param.account!!, request = request)))

        // 构造登录返回结果
        return success(LoginResult(StpUtil.getTokenName(), StpUtil.getTokenValue()))
    }


    /**
     * 注销登录
     *
     * @param request HttpServletRequest
     * @return ApiResult<Boolean>
     */
    @ApiOperation(value = "注销登录")
    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): ApiResult<Boolean> {
        val user = service.getById(StpUtil.getLoginIdAsLong()) ?: return fail("用户异常")

        // 登出日志
        applicationContext.publishEvent(LoginEvent(
            LoginLogDTO.loginFail(
            user.account ?: "", LoginStateEnum.LOGOUT, request
        )))

        // 注销登录
        StpUtil.logout()
        return success(true)
    }

    /**
     * 图形验证码
     *
     * 说明：
     * 限流规则一分钟十次调用
     */
    @Limit(name = "验证码接口限流", count = 10, describe = "您的操作过于频繁，请稍后再试")
    @ApiOperation(value = "图形验证码")
    @GetMapping("/captcha")
    fun captcha(): ApiResult<CaptchaResult> {
        val key = System.currentTimeMillis()

        // 验证码值缓存到redis, 5分钟有效
        val specCaptcha = SpecCaptcha(120, 40, 5)
        captchaCacheKey.set(key, specCaptcha.text())

        return if ("prod" === env) {
            // 如果生产环境，不返回验证码的值
            success(CaptchaResult(key, specCaptcha.toBase64()))
        } else success(CaptchaResult(key, specCaptcha.toBase64(), specCaptcha.text()))
    }

}
