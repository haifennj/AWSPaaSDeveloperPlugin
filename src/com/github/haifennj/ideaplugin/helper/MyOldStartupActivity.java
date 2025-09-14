package com.github.haifennj.ideaplugin.helper;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;

/**
 * Description:
 * <p>
 * Date: 20250413 00:43
 *
 * @author zhanghf
 */
public class MyOldStartupActivity  implements StartupActivity, DumbAware {
	@Override
	public void runActivity(@NotNull Project project) {
		MyInitHelper.init(project);

		// 再探测新版接口并做增强（如果存在）
		try {
			Class<?> bgClass = Class.forName("com.intellij.openapi.startup.BackgroundPostStartupActivity");
			// 如果类存在，可以执行额外逻辑或把初始化动作转给新版入口
			// 例如：把 InitHelper 的逻辑包成 Runnable，并通过新版机制安排
			// 这里仅演示存在性检测
			System.out.println("Platform supports BackgroundPostStartupActivity — do extra work if needed");
		} catch (ClassNotFoundException ignored) {
			// 平台太旧，什么也不做
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
