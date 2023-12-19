package org.zetaframework.quartz.factory

import org.quartz.spi.TriggerFiredBundle
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import org.springframework.stereotype.Component

/**
 * 自定义任务工厂
 *
 * 说明：
 * 1.解决Quartz不能在SpringBoot中注入Bean的问题
 * 2.看了org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration类之后，
 *   发现不需要自定义SchedulerFactoryBean bean。也就是说，不需要写QuartzConfig类。有这个类就好了
 * @author gcc
 */
@Component
class JobFactory(private val factory: AutowireCapableBeanFactory): SpringBeanJobFactory() {

    /**
     * 创建指定作业类的实例。
     *
     * 可以被覆盖以对作业实例进行后处理。
     * @param： bundle - TriggerFiredBundle，可以从中获取 JobDetail 和与触发器触发相关的其他信息
     * @return： 作业实例
     * @throws： Exception - 如果作业实例化失败
     */
    override fun createJobInstance(bundle: TriggerFiredBundle): Any {
        // 实例化对象
        val instance = super.createJobInstance(bundle)
        // 进行注入（Spring管理该Bean）
        factory.autowireBean(instance)
        // 返回对象
        return instance
    }
}
