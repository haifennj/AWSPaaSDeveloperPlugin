package com.github.haifennj.ideaplugin.file;

import com.github.haifennj.ideaplugin.helper.PluginConst;
import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileExportActionGroup extends ActionGroup {

	// 文件路径映射表，根据 actionId 区分不同文件组
	public static final List<Map<String, Object>> FILE_PATHS_LIST = FileExportPathList.FILE_PATHS_LIST;

	@Override
	public AnAction[] getChildren(@Nullable AnActionEvent e) {
		List<AnAction> actions = new ArrayList<>();
		ActionManager actionManager = ActionManager.getInstance();

		// 这里可以从配置文件、数据库、网络请求动态生成菜单
		for (Map<String, Object> map : FILE_PATHS_LIST) {
			if (map.containsKey("separator")) {
				if (map.containsKey("level")) {
					int level = Integer.parseInt(map.get("level").toString());
					if (level > 1) {
						actions.add(Separator.getInstance());
					}
				}
				continue;
			}
			String id = map.get("id").toString();
			String label = map.get("name").toString();
			int ver = Integer.parseInt(map.get("ver").toString());
			int level = Integer.parseInt(map.get("level").toString());
			label =  label + (ver == 7 ? " (AWS" + ver + ")" : "");

			String actionId = PluginConst.MY_PLUGIN_ACTION_PREFIX + map.get("actionId");
			if (level > 1) {
				// 用已有的 FileExportAction
				FileExportAction action = new FileExportAction(label, id);
				// 注册到 ActionManager，如果没注册过
				if (actionManager.getAction(actionId) == null) {
					actionManager.registerAction(actionId, action);
				}
				actions.add(action);
			}
		}
		return actions.toArray(new AnAction[0]);
	}

}
