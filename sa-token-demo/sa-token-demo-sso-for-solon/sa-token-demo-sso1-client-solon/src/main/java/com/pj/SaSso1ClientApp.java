package com.pj;


import cn.dev33.satoken.sso.SaSsoManager;
import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

/**
 * SSO模式一，Client端 Demo 
 * @author click33
 *
 */
@SolonMain
public class SaSso1ClientApp {

	public static void main(String[] args) {
		Solon.start(SaSso1ClientApp.class, args);
		System.out.println("\nSa-Token SSO模式一 Client端启动成功");

		System.out.println();
		System.out.println("---------------------- Solon Sa-Token SSO 模式一 Client 端启动成功 ----------------------");
		System.out.println("配置信息：" + SaSsoManager.getClientConfig());
		System.out.println("测试访问应用端一: http://s1.stp.com:9001");
		System.out.println("测试访问应用端二: http://s2.stp.com:9001");
		System.out.println("测试访问应用端三: http://s3.stp.com:9001");
		System.out.println("测试前需要根据官网文档修改hosts文件，测试账号密码：sa / 123456");
		System.out.println();
	}
	
}