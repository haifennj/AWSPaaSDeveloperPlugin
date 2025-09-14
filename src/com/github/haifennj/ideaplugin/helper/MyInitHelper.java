package com.github.haifennj.ideaplugin.helper;

import com.github.haifennj.ideaplugin.file.FileExportAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;

import java.util.Arrays;
import java.util.Map;

/**
 * 暂时作废
 * <p>
 * Date: 20250909 22:43
 *
 * @author zhanghf
 */
public class MyInitHelper {
	public static void init(Project project) {
		updateUIWithAWS7Status(project, true);
	}
	public static void updateUIWithAWS7Status(Project project, boolean isAWS7) {
		ActionManager actionManager = ActionManager.getInstance();
		DefaultActionGroup toolsMenu = (DefaultActionGroup) actionManager.getAction("ToolsMenu");
		DefaultActionGroup subMenu = new DefaultActionGroup("一键导出(更多)", true);

		boolean alreadyAdded = Arrays.stream(toolsMenu.getChildren(null))
				.anyMatch(action -> "AWSFileExport-process-pc".equals(ActionManager.getInstance().getId(action)));
		if (alreadyAdded) {
			return;
		}

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
			boolean isAdd = true;//isAWS7 ? ver >= 6 : ver == 6;

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
