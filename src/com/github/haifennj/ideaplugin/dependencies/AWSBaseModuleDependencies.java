package com.github.haifennj.ideaplugin.dependencies;

import com.github.haifennj.ideaplugin.helper.PluginConst;
import com.github.haifennj.ideaplugin.helper.PluginUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AWSBaseModuleDependencies {
	protected Project project;

	List<Module> list = new ArrayList<>();

	LibraryTable projectTable = null;
	Library aws_lib = null;


	public AWSBaseModuleDependencies(Project project) {
		this.project = project;
		initPlatformModuleList();
		projectTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
		aws_lib = projectTable.getLibraryByName(PluginConst.PLUGIN_AWS_LIBRARY_NAME);
	}

	protected String updateWebModuleDependencies(Module module) {
		Module releaseModule = PluginUtil.getReleaseModule(project, true);
		String webServerLib = releaseModule.getModuleFile().getParent().getPath() + "/webserver/lib";
		String portal = "";
		File webappsPath = new File(releaseModule.getModuleFile().getParent().getPath() + "/webserver/webapps");
		File[] files = webappsPath.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				File libJar = new File(file.getPath() + "/WEB-INF/lib/aws-infrastructure-web.jar");
				if (libJar.exists()) {
					portal = libJar.getParentFile().getPath();
					break;
				}
			}
		}
		PluginUtil.updateModuleLibraries(project, module, new String[]{webServerLib, portal});
		return module.getName();
	}

	protected String updateUpgradeModuleDependencies(Module module) {
		String libPath = module.getModuleFile().getParent().getPath() + "/shell/lib";
		PluginUtil.updateModuleLibraries(project, module, new String[]{libPath});
		return module.getName();
	}

	protected void initPlatformModuleList() {
		List<Module> list = new ArrayList<>();
		Module[] modules = ModuleManager.getInstance(project).getSortedModules();
		for (Module m : modules) {
			if (PluginUtil.isExcludeModule(m)) {
				continue;
			}
			if (PluginUtil.isAWSWebModule(m.getName())) {
				continue;
			}
			if (PluginUtil.isAvailablePlatformModule(m)) {
				list.add(m);
			}
		}
		this.list.addAll(list);
	}

	protected List<Module> getPlatformModuleList() {
		return list;
	}

}
