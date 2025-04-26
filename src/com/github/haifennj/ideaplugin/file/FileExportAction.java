package com.github.haifennj.ideaplugin.file;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

	private final String label;
	private final String exportType;

	public FileExportAction(String label, String exportType) {
		super(label);
		this.label = label;
		this.exportType = exportType;
	}

	// 文件路径映射表，根据 actionId 区分不同文件组
	public static final List<Map<String, Object>> FILE_PATHS_LIST = new ArrayList<>();
	static {
		FILE_PATHS_LIST.addAll(List.of(
				Map.of("separator", true),
				Map.of("id", "AWSFileExport-process-pc", "level", 1, "ver", 6, "name", "一键导出(流程运行PC)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.bpm.process.running.import.js.htm",
						"apps/install/_platform.runtime/template/page/client.bpm.process.running.import.js.htm",
						"webserver/webapps/portal/apps/_bpm.portal/process/running",
						"webserver/webapps/portal/apps/_platform.runtime/process/running"
				)),
				Map.of("id", "AWSFileExport-process-pc-2", "level", 1, "ver", 7, "name", "一键导出(流程运行PC-低版浏览器)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.bpm.process.running.import.js.70.htm",
						"webserver/webapps/portal/apps/_platform.runtime/process/running-70"
				)),
				Map.of("id", "AWSFileExport-process-mobile", "level", 1, "ver", 6, "name", "一键导出(流程运行Mobile)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.bpm.process.running.mobile.import.js.htm",
						"apps/install/_platform.runtime/template/page/client.bpm.process.running.mobile.import.js.htm",
						"webserver/webapps/portal/apps/_bpm.portal/process/running-mobile",
						"webserver/webapps/portal/apps/_platform.runtime/process/running-mobile"
				)),
				Map.of("id", "AWSFileExport-form-design", "level", 1, "ver", 6, "name", "一键导出(表单设计器)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.m.form.designer.plus.htm",
						"apps/install/_platform.infra/template/page/console.m.form.designer.plus.htm",
						"webserver/webapps/portal/apps/_bpm.platform/form/designer/main",
						"webserver/webapps/portal/apps/_platform.infra/model/form/designer/main"
				)),
				Map.of("id", "AWSFileExport-process-design", "level", 1, "ver", 6, "name", "一键导出(流程设计器)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.m.process.designer.htm",
						"apps/install/_platform.infra/template/page/console.m.process.designer.htm",
						"webserver/webapps/portal/apps/_bpm.platform/process/designer",
						"webserver/webapps/portal/apps/_platform.infra/model/process/designer/main"
				)),
				Map.of("separator", true),
				Map.of("id", "AWSFileExport-process-prm", "level", 2, "ver", 6, "name", "一键导出(实例运行管理)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.prm.home.vue.htm",
						"apps/install/_platform.infra/template/page/console.prm.home.vue.htm",
						"webserver/webapps/portal/apps/_bpm.platform/process/prm",
						"webserver/webapps/portal/apps/_platform.infra/model/process/prm"
				)),
				Map.of("separator", true),
				Map.of("id", "AWSFileExport-dw-pc", "level", 1, "ver", 6, "name", "一键导出(视图PC)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.data.window.portal.htm",
						"apps/install/_platform.runtime/template/page/client.data.window.portal.htm",
						"webserver/webapps/portal/apps/_bpm.portal/dw/exec/main",
						"webserver/webapps/portal/apps/_platform.runtime/dw/exec/main"
				)),
				Map.of("id", "AWSFileExport-dw-pc-2", "level", 1, "ver", 7, "name", "一键导出(视图PC-低版浏览器)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.data.window.portal.70.htm",
						"webserver/webapps/portal/apps/_platform.runtime/dw/exec/main-70"
				)),
				Map.of("id", "AWSFileExport-视图mobile", "level", 1, "ver", 6, "name", "一键导出(视图Mobile)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.data.window.portal.mobile.htm",
						"apps/install/_platform.runtime/template/page/client.data.window.portal.mobile.htm",
						"webserver/webapps/portal/apps/_bpm.portal/dw/exec/mobile",
						"webserver/webapps/portal/apps/_platform.runtime/dw/exec/mobile"
				)),
				Map.of("id", "AWSFileExport-dw-design", "level", 1, "ver", 6, "name", "一键导出(视图设计器)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.m.dw.design.plus.htm",
						"apps/install/_platform.infra/template/page/console.m.dw.design.plus.htm",
						"webserver/webapps/portal/apps/_bpm.platform/dw/designer-plus/main",
						"webserver/webapps/portal/apps/_platform.infra/model/dw/designer-plus/main"
				)),
				Map.of("separator", true),
				Map.of("id", "AWSFileExport-dashboard-pc", "level", 1, "ver", 6, "name", "一键导出(图表运行PC)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.dashboard.portal.htm",
						"apps/install/_platform.runtime/template/page/client.dashboard.portal.htm",
						"webserver/webapps/portal/apps/_bpm.portal/dashboard/exec/main",
						"webserver/webapps/portal/apps/_platform.runtime/dashboard/exec/main"
				)),
				Map.of("id", "AWSFileExport-dashboard-pc-2", "level", 1, "ver", 7, "name", "一键导出(图表运行PC-低版浏览器)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.dashboard.portal.70.htm",
						"webserver/webapps/portal/apps/_platform.runtime/dashboard/exec/main-70"
				)),
				Map.of("id", "AWSFileExport-dashboard-mobile", "level", 1, "ver", 6, "name", "一键导出(图表移动端)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.dashboard.mobile.home.htm",
						"webserver/webapps/portal/apps/_bpm.portal/dashboard/mobile/main",
						"apps/install/_platform.runtime/template/page/client.dashboard.mobile.home.htm",
						"webserver/webapps/portal/apps/_platform.runtime/dashboard/mobile/main"
				)),
				Map.of("id", "AWSFileExport-dashboard-design", "level", 1, "ver", 6, "name", "一键导出(图表设计器)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.m.dashboard.designer.plus.htm",
						"webserver/webapps/portal/apps/_bpm.platform/dashboard-plus/main",
						"apps/install/_platform.infra/template/page/console.m.dashboard.designer.plus.htm",
						"webserver/webapps/portal/apps/_platform.infra/model/dashboard-plus/main"
				)),
				Map.of("separator", true),
				Map.of("id", "AWSFileExport-datav-pc", "level", 2, "ver", 7, "name", "一键导出(数据大屏-运行)", "paths", List.of(
						"apps/install/_platform.infra/template/page/console.m.datav.designer.htm",
						"webserver/webapps/portal/apps/_platform.infra/model/datav/designer/main"
				)),
				Map.of("id", "AWSFileExport-datav-design", "level", 2, "ver", 7, "name", "一键导出(数据大屏-设计器)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.m.datav.running.htm",
						"webserver/webapps/portal/apps/_platform.runtime/datav"
				)),
				Map.of("separator", true),
				Map.of("id", "AWSFileExport-portal-management", "level", 2, "ver", 7, "name", "一键导出(门户管理)", "paths", List.of(
						"apps/install/_platform.infra/template/page/console.p.portal.management.htm",
						"webserver/webapps/portal/apps/_platform.infra/portal/portal-management/main"
				)),
				Map.of("id", "AWSFileExport-portal-running-pc", "level", 2, "ver", 7, "name", "一键导出(门户运行)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.p.portal.runtime.js.htm",
						"webserver/webapps/portal/apps/_platform.runtime/portal-running/main"
				)),
				Map.of("id", "AWSFileExport-portal-running-pc-2", "level", 2, "ver", 7, "name", "一键导出(门户运行-低版浏览器)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.p.portal.runtime.js.70.htm",
						"webserver/webapps/portal/apps/_platform.runtime/portal-running/main-70"
				)),
				Map.of("id", "AWSFileExport-portal-running-mobile", "level", 2, "ver", 7, "name", "一键导出(门户运行-移动端)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.p.portal.runtime.js.mobile.htm",
						"webserver/webapps/portal/apps/_platform.runtime/portal-running/mobile"
				)),
				Map.of("separator", true),
				Map.of("id", "AWSFileExport-dictionary-designer", "level", 2, "ver", 6, "name", "一键导出(字典模型-设计器)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.m.xml.dictionary.designer.htm",
						"webserver/webapps/portal/apps/_bpm.platform/dictionary",
						"apps/install/_platform.infra/template/page/console.m.xml.dictionary.designer.htm",
						"webserver/webapps/portal/apps/_platform.infra/model/dictionary"
				)),
				Map.of("id", "AWSFileExport-data-trigger", "level", 2, "ver", 7, "name", "一键导出(自动化-设计器)", "paths", List.of(
						"apps/install/_platform.infra/template/page/console.m.automation.designer.htm",
						"webserver/webapps/portal/apps/_platform.infra/model/automation/designer/main"
				)),
				Map.of("id", "AWSFileExport-model-repository", "level", 2, "ver", 6, "name", "一键导出(业务建模)", "paths", List.of(
						"apps/install/_platform.infra/template/page/console.m.model.repository.htm",
						"apps/install/_platform.infra/template/page/console.m.model.repository.htm",
						"webserver/webapps/portal/apps/_platform.infra/model/dashboard-plus/main",
						"webserver/webapps/portal/apps/_platform.infra/model/model-repository"
				)),
				Map.of("separator", true),
				Map.of("id", "AWSFileExport-awsui-3-3", "level", 2, "ver", 7, "name", "一键导出(awsui@3.3.0)", "paths", List.of(
						"webserver/webapps/portal/commons/js/vue/awsui@3.3.0"
				)),
				Map.of("id", "AWSFileExport-awsui-3-4", "level", 2, "ver", 7, "name", "一键导出(awsui@3.4.0)", "paths", List.of(
						"webserver/webapps/portal/commons/js/vue/awsui@3.4.0"
				))
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
		List<String> filePaths = findFilePathsByActionId(actionId);
		if (filePaths.isEmpty()) {
			return;
		}

		boolean isAWS7 = PluginUtil.isAWS7();

		SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyyMMdd_HH");
		String time = datetimeFormat.format(System.currentTimeMillis());

		String userHome = System.getProperty("user.home");
		String fileSeparator = System.getProperty("file.separator");
		VirtualFile releaseModuleFile = PluginUtil.findReleaseModuleFile(event.getProject());
		if (releaseModuleFile == null) {
			return;
		}
		String baseSourceDir = releaseModuleFile.getPath() + fileSeparator;
		String defaultOutput = userHome + fileSeparator + "Desktop" + fileSeparator + event.getProject().getName() + fileSeparator + event.getProject().getName()+"@"+time;

		for (String path : filePaths) {
			try {
				exportFiles(baseSourceDir, defaultOutput + fileSeparator + moduleName + fileSeparator,  checkFilePath(isAWS7, path));
			} catch (Exception e) {
				// 不处理异常
			}
		}
		NotificationUtil.info("导出成功 🚀");
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

	private List<String> findFilePathsByActionId(String actionId) {
		for (Map<String, Object> map : FILE_PATHS_LIST) {
			if (actionId.equals(map.get("id"))) {
				return (List<String>) map.get("paths");
			}
		}
		return new ArrayList<>();
	}

}
