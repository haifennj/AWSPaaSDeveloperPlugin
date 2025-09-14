package com.github.haifennj.ideaplugin.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileExportPathList {
	// 文件路径映射表，根据 actionId 区分不同文件组
	public static final List<Map<String, Object>> FILE_PATHS_LIST = new ArrayList<>();
	static {
		FILE_PATHS_LIST.addAll(List.of(
				Map.of("actionId", "1", "separator", true),
				Map.of("actionId", "2", "id", "AWSFileExport-process-pc", "level", 1, "ver", 6, "name", "一键导出(流程运行PC)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.bpm.process.running.import.js.htm",
						"apps/install/_platform.runtime/template/page/client.bpm.process.running.import.js.htm",
						"webserver/webapps/portal/apps/_bpm.portal/process/running",
						"webserver/webapps/portal/apps/_platform.runtime/process/running"
				)),
				Map.of("actionId", "3", "id", "AWSFileExport-process-pc-2", "level", 1, "ver", 7, "name", "一键导出(流程运行PC-低版浏览器)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.bpm.process.running.import.js.70.htm",
						"webserver/webapps/portal/apps/_platform.runtime/process/running-70"
				)),
				Map.of("actionId", "4", "id", "AWSFileExport-process-mobile", "level", 1, "ver", 6, "name", "一键导出(流程运行Mobile)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.bpm.process.running.mobile.import.js.htm",
						"apps/install/_platform.runtime/template/page/client.bpm.process.running.mobile.import.js.htm",
						"webserver/webapps/portal/apps/_bpm.portal/process/running-mobile",
						"webserver/webapps/portal/apps/_platform.runtime/process/running-mobile"
				)),
				Map.of("actionId", "5", "id", "AWSFileExport-form-design", "level", 1, "ver", 6, "name", "一键导出(表单设计器)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.m.form.designer.plus.htm",
						"apps/install/_platform.infra/template/page/console.m.form.designer.plus.htm",
						"webserver/webapps/portal/apps/_bpm.platform/form/designer/main",
						"webserver/webapps/portal/apps/_platform.infra/model/form/designer/main"
				)),
				Map.of("actionId", "6", "id", "AWSFileExport-process-design", "level", 1, "ver", 6, "name", "一键导出(流程设计器)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.m.process.designer.htm",
						"apps/install/_platform.infra/template/page/console.m.process.designer.htm",
						"webserver/webapps/portal/apps/_bpm.platform/process/designer",
						"webserver/webapps/portal/apps/_platform.infra/model/process/designer/main"
				)),
				Map.of("actionId", "7", "separator", true, "level", 2),
				Map.of("actionId", "8", "id", "AWSFileExport-process-prm", "level", 2, "ver", 6, "name", "一键导出(实例运行管理)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.prm.home.vue.htm",
						"apps/install/_platform.infra/template/page/console.prm.home.vue.htm",
						"webserver/webapps/portal/apps/_bpm.platform/process/prm",
						"webserver/webapps/portal/apps/_platform.infra/model/process/prm"
				)),
				Map.of("actionId", "9", "separator", true),
				Map.of("actionId", "10", "id", "AWSFileExport-dw-pc", "level", 1, "ver", 6, "name", "一键导出(视图PC)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.data.window.portal.htm",
						"apps/install/_platform.runtime/template/page/client.data.window.portal.htm",
						"webserver/webapps/portal/apps/_bpm.portal/dw/exec/main",
						"webserver/webapps/portal/apps/_platform.runtime/dw/exec/main"
				)),
				Map.of("actionId", "11", "id", "AWSFileExport-dw-pc-2", "level", 1, "ver", 7, "name", "一键导出(视图PC-低版浏览器)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.data.window.portal.70.htm",
						"webserver/webapps/portal/apps/_platform.runtime/dw/exec/main-70"
				)),
				Map.of("actionId", "12", "id", "AWSFileExport-视图mobile", "level", 1, "ver", 6, "name", "一键导出(视图Mobile)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.data.window.portal.mobile.htm",
						"apps/install/_platform.runtime/template/page/client.data.window.portal.mobile.htm",
						"webserver/webapps/portal/apps/_bpm.portal/dw/exec/mobile",
						"webserver/webapps/portal/apps/_platform.runtime/dw/exec/mobile"
				)),
				Map.of("actionId", "13", "id", "AWSFileExport-dw-design", "level", 1, "ver", 6, "name", "一键导出(视图设计器)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.m.dw.design.plus.htm",
						"apps/install/_platform.infra/template/page/console.m.dw.design.plus.htm",
						"webserver/webapps/portal/apps/_bpm.platform/dw/designer-plus/main",
						"webserver/webapps/portal/apps/_platform.infra/model/dw/designer-plus/main"
				)),
				Map.of("actionId", "14", "separator", true),
				Map.of("actionId", "15", "id", "AWSFileExport-dashboard-pc", "level", 1, "ver", 6, "name", "一键导出(图表运行PC)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.dashboard.portal.htm",
						"apps/install/_platform.runtime/template/page/client.dashboard.portal.htm",
						"webserver/webapps/portal/apps/_bpm.portal/dashboard/exec/main",
						"webserver/webapps/portal/apps/_platform.runtime/dashboard/exec/main"
				)),
				Map.of("actionId", "16", "id", "AWSFileExport-dashboard-pc-2", "level", 1, "ver", 7, "name", "一键导出(图表运行PC-低版浏览器)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.dashboard.portal.70.htm",
						"webserver/webapps/portal/apps/_platform.runtime/dashboard/exec/main-70"
				)),
				Map.of("actionId", "17", "id", "AWSFileExport-dashboard-mobile", "level", 1, "ver", 6, "name", "一键导出(图表运行Mobile)", "paths", List.of(
						"apps/install/_bpm.portal/template/page/client.dashboard.mobile.home.htm",
						"webserver/webapps/portal/apps/_bpm.portal/dashboard/mobile/main",
						"apps/install/_platform.runtime/template/page/client.dashboard.mobile.home.htm",
						"webserver/webapps/portal/apps/_platform.runtime/dashboard/mobile/main"
				)),
				Map.of("actionId", "18", "id", "AWSFileExport-dashboard-design", "level", 1, "ver", 6, "name", "一键导出(图表设计器)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.m.dashboard.designer.plus.htm",
						"webserver/webapps/portal/apps/_bpm.platform/dashboard-plus/main",
						"apps/install/_platform.infra/template/page/console.m.dashboard.designer.plus.htm",
						"webserver/webapps/portal/apps/_platform.infra/model/dashboard-plus/main"
				)),
				Map.of("actionId", "19", "separator", true, "level", 2),
				Map.of("actionId", "50", "id", "AWSFileExport-datav-pc", "level", 2, "ver", 7, "name", "一键导出(数据大屏-运行)", "paths", List.of(
						"apps/install/_platform.infra/template/page/console.m.datav.designer.htm",
						"webserver/webapps/portal/apps/_platform.infra/model/datav/designer/main"
				)),
				Map.of("actionId", "51", "id", "AWSFileExport-datav-design", "level", 2, "ver", 7, "name", "一键导出(数据大屏-设计器)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.m.datav.running.htm",
						"webserver/webapps/portal/apps/_platform.runtime/datav"
				)),
				Map.of("actionId", "52", "separator", true, "level", 2),
				Map.of("actionId", "53", "id", "AWSFileExport-portal-management", "level", 2, "ver", 7, "name", "一键导出(门户管理)", "paths", List.of(
						"apps/install/_platform.infra/template/page/console.p.portal.management.htm",
						"webserver/webapps/portal/apps/_platform.infra/portal/portal-management/main"
				)),
				Map.of("actionId", "54", "id", "AWSFileExport-portal-running-pc", "level", 2, "ver", 7, "name", "一键导出(门户运行)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.p.portal.runtime.js.htm",
						"webserver/webapps/portal/apps/_platform.runtime/portal-running/main"
				)),
				Map.of("actionId", "55", "id", "AWSFileExport-portal-running-pc-2", "level", 2, "ver", 7, "name", "一键导出(门户运行-低版浏览器)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.p.portal.runtime.js.70.htm",
						"webserver/webapps/portal/apps/_platform.runtime/portal-running/main-70"
				)),
				Map.of("actionId", "56", "id", "AWSFileExport-portal-running-mobile", "level", 2, "ver", 7, "name", "一键导出(门户运行-移动端)", "paths", List.of(
						"apps/install/_platform.runtime/template/page/client.p.portal.runtime.js.mobile.htm",
						"webserver/webapps/portal/apps/_platform.runtime/portal-running/mobile"
				)),
				Map.of("actionId", "57", "separator", true, "level", 2),
				Map.of("actionId", "58", "id", "AWSFileExport-dictionary-designer", "level", 2, "ver", 6, "name", "一键导出(字典模型-设计器)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.m.xml.dictionary.designer.htm",
						"webserver/webapps/portal/apps/_bpm.platform/dictionary",
						"apps/install/_platform.infra/template/page/console.m.xml.dictionary.designer.htm",
						"webserver/webapps/portal/apps/_platform.infra/model/dictionary"
				)),
				Map.of("actionId", "59", "id", "AWSFileExport-data-trigger", "level", 2, "ver", 7, "name", "一键导出(自动化-设计器)", "paths", List.of(
						"apps/install/_platform.infra/template/page/console.m.automation.designer.htm",
						"webserver/webapps/portal/apps/_platform.infra/model/automation/designer/main"
				)),
				Map.of("actionId", "60", "id", "AWSFileExport-model-repository", "level", 2, "ver", 6, "name", "一键导出(业务建模)", "paths", List.of(
						"apps/install/_bpm.platform/template/page/console.m.model.repository.htm",
						"apps/install/_platform.infra/template/page/console.m.model.repository.htm",
						"webserver/webapps/portal/apps/_bpm.platform/model-repository",
						"webserver/webapps/portal/apps/_platform.infra/model/model-repository"
				)),
				Map.of("actionId", "61", "separator", true, "level", 2),
				Map.of("actionId", "62", "id", "AWSFileExport-awsui-3-3", "level", 2, "ver", 7, "name", "一键导出(awsui@3.3.0)", "paths", List.of(
						"webserver/webapps/portal/commons/js/vue/awsui@3.3.0"
				)),
				Map.of("actionId", "63", "id", "AWSFileExport-awsui-3-4", "level", 2, "ver", 7, "name", "一键导出(awsui@3.4.0)", "paths", List.of(
						"webserver/webapps/portal/commons/js/vue/awsui@3.4.0"
				))
		));
	}

}
