/*
 * Copyright(C)2001-2025 Actionsoft Co.,Ltd
 * AWS(Actionsoft workflow suite) BPM(Business Process Management) PLATFORM Source code
 * AWS is a application middleware for BPM System


 * 本软件工程编译的二进制文件及源码版权归北京炎黄盈动科技发展有限责任公司所有，
 * 受中国国家版权局备案及相关法律保护，未经书面法律许可，任何个人或组织都不得泄漏、
 * 传播此源码文件的全部或部分文件，不得对编译文件进行逆向工程，违者必究。

 * $$本源码是炎黄盈动最高保密级别的文件$$
 *
 * http://www.actionsoft.com.cn
 *
 */
package com.github.haifennj.ideaplugin.helper;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.haifennj.ideaplugin.file.FileExportAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;

import kotlin.Unit;
import kotlin.coroutines.Continuation;

/**
 * Description:
 * <p>
 * Date: 20250413 00:43
 *
 * @author zhanghf
 */
public class MyStartupActivity implements ProjectActivity {

	@Nullable
	@Override
	public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
		ActionManager actionManager = ActionManager.getInstance();
		DefaultActionGroup toolsMenu = (DefaultActionGroup) actionManager.getAction("ToolsMenu");
		// 创建一个新的子菜单
		DefaultActionGroup mySubMenu = new DefaultActionGroup("一键导出-其他", true);

		Future<Boolean> future = PluginUtil.checkAws7(project);

		// 使用 ApplicationManager 的调度器处理回调
		ApplicationManager.getApplication().executeOnPooledThread(() -> {
			try {
				boolean isAWS7 = future.get(); // 在后台线程获取结果
				// 回到 EDT 更新 UI
				ApplicationManager.getApplication().invokeLater(() -> {
					updateUIWithAWS7Status(project, isAWS7);
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				// 可选：记录错误或显示通知
				ApplicationManager.getApplication().invokeLater(() -> {
					NotificationUtil.showErrorNotification(project, "检查AWS版本失败: " + e.getMessage());
				});
			}
		});
		return null;
	}

	private void updateUIWithAWS7Status(Project project, boolean isAWS7) {
		ActionManager actionManager = ActionManager.getInstance();
		DefaultActionGroup toolsMenu = (DefaultActionGroup) actionManager.getAction("ToolsMenu");
		DefaultActionGroup subMenu = new DefaultActionGroup("一键导出(更多)", true);

		for (Map<String, Object> map : FileExportAction.FILE_PATHS_LIST) {
			if (map.containsKey("separator")) {
				toolsMenu.addSeparator();
				subMenu.addSeparator();
				continue;
			}
			String id = map.get("id").toString();
			String label = map.get("name").toString();
			int ver = Integer.parseInt(map.get("ver").toString());
			int level = Integer.parseInt(map.get("level").toString());
			boolean isAdd = isAWS7 ? ver >= 6 : ver == 6;

			if (isAdd) {
				FileExportAction action = new FileExportAction(label, id);
				actionManager.registerAction(id, action);
				if (level == 1) {
					toolsMenu.add(action);
				} else {
					subMenu.add(action);
				}
			}
		}
		toolsMenu.addSeparator();
		toolsMenu.add(subMenu);
	}
}
