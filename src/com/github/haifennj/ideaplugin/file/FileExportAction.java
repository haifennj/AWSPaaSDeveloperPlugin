package com.github.haifennj.ideaplugin.file;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.haifennj.ideaplugin.helper.NotificationUtil;
import com.github.haifennj.ideaplugin.helper.PluginUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;

public class FileExportAction extends AnAction {
	// 文件路径映射表，根据 actionId 区分不同文件组
	private static final Map<String, List<String>> FILE_PATHS_MAP = new HashMap<>();
	static {
		FILE_PATHS_MAP.put("AWSFileExport01-process-pc", List.of(
				"apps/install/_bpm.portal/template/page/client.bpm.process.running.import.js.htm",
				"webserver/webapps/portal/apps/_bpm.portal/process/running"
		));
		FILE_PATHS_MAP.put("AWSFileExport02-process-mobile", List.of(
				"apps/install/_bpm.portal/template/page/client.bpm.process.running.mobile.import.js.htm",
				"webserver/webapps/portal/apps/_bpm.portal/process/running-mobile"
		));
		FILE_PATHS_MAP.put("AWSFileExport03-form-design", List.of(
				"apps/install/_bpm.platform/template/page/console.m.form.designer.plus.htm",
				"webserver/webapps/portal/apps/_bpm.platform/form/designer/main"
		));
		FILE_PATHS_MAP.put("AWSFileExport04-dw-design", List.of(
				"apps/install/_bpm.platform/template/page/console.m.dw.design.plus.htm",
				"webserver/webapps/portal/apps/_bpm.platform/dw/designer-plus/main"
		));
		FILE_PATHS_MAP.put("AWSFileExport05-dw-pc", List.of(
				"apps/install/_bpm.portal/template/page/client.data.window.portal.htm",
				"webserver/webapps/portal/apps/_bpm.portal/dw/exec/main"
		));
		FILE_PATHS_MAP.put("AWSFileExport06-dw-mobile", List.of(
				"apps/install/_bpm.portal/template/page/client.data.window.portal.mobile.htm",
				"webserver/webapps/portal/apps/_bpm.portal/dw/exec/mobile"
		));
	}
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

		String actionId = event.getActionManager().getId(this);
		List<String> filePaths = FILE_PATHS_MAP.get(actionId);

		boolean isAWS7 = PluginUtil.isAWS7(event.getProject());

		SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyyMMdd_HH");
		String time = datetimeFormat.format(System.currentTimeMillis());

		String userHome = System.getProperty("user.home");
		String fileSeparator = System.getProperty("file.separator");
		VirtualFile releaseModuleFile = PluginUtil.findReleaseModuleFile(event.getProject());
		if (releaseModuleFile == null) {
			NotificationUtil.showErrorNotification(event.getProject(), "未找到release模块");
			return;
		}
		String baseSourceDir = releaseModuleFile.getPath() + fileSeparator;
		String defaultOutput = userHome + fileSeparator + "Desktop" + fileSeparator + event.getProject().getName() + fileSeparator + event.getProject().getName()+"@"+time;

		for (String path : filePaths) {
			try {
				exportFiles(baseSourceDir, defaultOutput + fileSeparator + moduleName + fileSeparator,  checkFilePath(isAWS7, path));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		NotificationUtil.showInfoNotification(event.getProject(), "导出成功 🚀");
	}

	public void exportFiles(String baseSourceDir, String targetDir, String relativeFilePath) throws IOException {
		Path sourcePath = Paths.get(baseSourceDir, relativeFilePath); // Combine base directory and relative path
		Path targetPath = Paths.get(targetDir, relativeFilePath); // Same structure as source
		FileUtil.copyFileOrDir(sourcePath.toFile(), targetPath.toFile());
		System.out.println("File copied: " + sourcePath + " -> " + targetPath);
	}

	public String checkFilePath(boolean isAWS7, String filePath) {
		if (isAWS7) {
			if (filePath.contains("_bpm.portal/")) {
				filePath = filePath.replace("_bpm.portal/", "_platform.runtime/");
			}
			if (filePath.contains("_bpm.platform/")) {
				filePath = filePath.replace("_bpm.platform/", "_platform.infra/");
			}
		}
		return filePath;
	}

}
