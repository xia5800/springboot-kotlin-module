package com.zeta

import org.mybatis.spring.annotation.MapperScan
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Repository

/**
 * TODO class name
 * @author gcc
 */
@EnableAsync
@ComponentScan(basePackages = ["com.zeta", "org.zetaframework"])
@MapperScan(value= ["com.zeta.**.dao"], annotationClass = Repository::class)
@SpringBootApplication
class ZetaApplication

private val log: Logger = LoggerFactory.getLogger(ZetaApplication::class.java)

fun main(args: Array<String>) {
    val context = runApplication<ZetaApplication>(*args)
    val env = context.environment
    log.info("""
----------------------------------------------------------
	项目 '${env.getProperty("spring.application.name")}' 启动成功! 访问连接:
	Swagger文档: 	 http://127.0.0.1:${env.getProperty("server.port", "8080")}/doc.html
	数据库监控: 		 http://127.0.0.1:${env.getProperty("server.port", "8080")}/druid
----------------------------------------------------------
            """)
}
