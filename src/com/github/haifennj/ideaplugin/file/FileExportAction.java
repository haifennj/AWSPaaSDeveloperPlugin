package com.github.haifennj.ideaplugin.file;

import com.github.haifennj.ideaplugin.helper.NotificationUtil;
import com.github.haifennj.ideaplugin.helper.PluginConst;
import com.github.haifennj.ideaplugin.helper.PluginUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileExportAction extends AnAction {

	private String label = "";
	private String exportId = "";

	public FileExportAction() {
	}

	public FileExportAction(String label, String exportId) {
		super(label);
		this.label = label;
		this.exportId = exportId;
	}

	// 文件路径映射表，根据 actionId 区分不同文件组
	public static final List<Map<String, Object>> FILE_PATHS_LIST = FileExportPathList.FILE_PATHS_LIST;

	@Override
	public void actionPerformed(AnActionEvent event) {
		Module releaseModule = PluginUtil.getReleaseModule(event.getProject(), true);
		if (releaseModule == null) {
			return;
		}
		String moduleName = releaseModule.getName();
		if (moduleName.equals("aws.release")) {
			moduleName = "release";
		}

		List<String> filePaths = findFilePathsById(findExportIdByActionId(event.getActionManager().getId(this)));
		if (filePaths.isEmpty()) {
			return;
		}

		boolean isAWS7 = PluginUtil.isAWS7();

		SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyyMMdd_HH");
		String time = datetimeFormat.format(System.currentTimeMillis());
		Presentation p = event.getPresentation();
		String name = p.getText().replace("一键导出", "");

		String userHome = System.getProperty("user.home");
		String fileSeparator = System.getProperty("file.separator");
		VirtualFile releaseModuleFile = PluginUtil.findReleaseModuleFile(event.getProject());
		if (releaseModuleFile == null) {
			return;
		}
		String baseSourceDir = releaseModuleFile.getPath() + fileSeparator;
		String defaultOutput = userHome + fileSeparator + "Desktop" + fileSeparator + event.getProject().getName() + fileSeparator + event.getProject().getName()+"@"+time+name;

		for (String path : filePaths) {
			try {
				exportFiles(baseSourceDir, defaultOutput + fileSeparator + moduleName + fileSeparator,  checkFilePath(isAWS7, path));
			} catch (Exception e) {
				// 不处理异常
			}
		}
		NotificationUtil.info("导出成功 🚀");
	}

	@Override
	public void update(@NotNull AnActionEvent e) {
		Presentation p = e.getPresentation();
		if (p.getText().equals(PluginConst.SEPARATOR)) {
			p.setEnabled(false);
			return;
		}
		String staticActionId = e.getActionManager().getId(this);
		if (staticActionId == null) {
			return;
		}
		String index = staticActionId.replace(PluginConst.MY_PLUGIN_ACTION_PREFIX, ""); // 提取 1,2,...
		String name = findNamesByActionId(index);
		if (!name.isEmpty()) {
			p.setText(name);
			if (name.equals(PluginConst.SEPARATOR)) {
				p.setEnabled(false);
			} else {
				p.setEnabledAndVisible(true);
			}
		} else {
			p.setEnabledAndVisible(false);
		}
	}

	private String findNamesByActionId(String actionId) {
		for (Map<String, Object> map : FILE_PATHS_LIST) {
			if (actionId.equals(map.get("actionId"))) {
				if (map.containsKey("level")) {
					int level = Integer.parseInt(map.get("level").toString());
					if (level > 1) {
						return "";
					}
				}
				if (map.containsKey("separator")) {
					return PluginConst.SEPARATOR;
				}
				if (map.containsKey("name")) {
					int ver = Integer.parseInt(map.get("ver").toString());
					return map.get("name") + (ver == 7 ? " (AWS" + map.get("ver") + ")" : "");
				}
			}
		}
		return "";
	}

	private String findExportIdByActionId(String staticActionId) {
		if (staticActionId == null) {
			return exportId;
		}
		String index = staticActionId.replace(PluginConst.MY_PLUGIN_ACTION_PREFIX, ""); // 提取 1,2,...
		for (Map<String, Object> map : FILE_PATHS_LIST) {
			if (index.equals(map.get("actionId"))) {
				return (String) map.get("id");
			}
		}
		return "";
	}

	private List<String> findFilePathsById(String id) {
		for (Map<String, Object> map : FILE_PATHS_LIST) {
			if (id.equals(map.get("id"))) {
				return (List<String>) map.get("paths");
			}
		}
		return new ArrayList<>();
	}

	public void exportFiles(String baseSourceDir, String targetDir, String relativeFilePath) throws IOException {
		Path sourcePath = Paths.get(baseSourceDir, relativeFilePath); // Combine base directory and relative path
		Path targetPath = Paths.get(targetDir, relativeFilePath); // Same structure as source
		if (!sourcePath.toFile().exists()) {
			return;
		}
		FileUtil.copyFileOrDir(sourcePath.toFile(), targetPath.toFile());
		System.out.println("File copied: " + sourcePath + " -> " + targetPath);
	}

	public String checkFilePath(boolean isAWS7, String filePath) {
//		if (isAWS7) {
//			if (filePath.contains("_bpm.portal/")) {
//				filePath = filePath.replace("_bpm.portal/", "_platform.runtime/");
//			}
//			if (filePath.contains("_bpm.platform/")) {
//				filePath = filePath.replace("_bpm.platform/", "_platform.infra/");
//			}
//		}
		return filePath;
	}

}
