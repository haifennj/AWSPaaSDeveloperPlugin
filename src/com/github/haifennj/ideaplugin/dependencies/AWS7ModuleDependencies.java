package com.github.haifennj.ideaplugin.dependencies;

import com.github.haifennj.ideaplugin.helper.PluginUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Processor;

import java.util.Collection;
import java.util.List;

/**
 * Module的依赖更新
 *
 * @author Haiifenng
 * @date 2017.05.07
 */
public class AWS7ModuleDependencies extends AWSBaseModuleDependencies {

	public AWS7ModuleDependencies(Project project) {
		super(project);
	}

	/**
	 * 更新当前Project的所有Module的依赖，主入口
	 *
	 * @return
	 */
	public String updateDependencies() {
		Collection<Module> modules = ModuleUtil.getModulesOfType(project, ModuleTypeManager.getInstance().getDefaultModuleType());
		StringBuilder message = new StringBuilder();
		for (Module module : modules) {
			String msg = updateDependencies(module);
			if (!StringUtil.isEmpty(msg)) {
				message.append(msg).append("\n");
			}
		}

		if (!StringUtil.isEmpty(message)) {
			message.setLength(message.toString().length() - 1);
			return String.format("更新了以下Module：\n%s", message.toString());
		} else {
			return "没有需要更新的Module";
		}
	}

	/**
	 * 更新指定的module的依赖
	 *
	 * @param module
	 * @return
	 */
	public String updateDependencies(Module module) {
		if (PluginUtil.isAWSWebModule(module.getName())) {
			return updateWebModuleDependencies(module);
		} else if (module.getName().equals("aws-platform-upgrade")) {
			return updateUpgradeModuleDependencies(module);
		} else {
			return updateCommonModuleDependencies(module);
		}
	}

	private String updateCommonModuleDependencies(Module rootModule) {
		final ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(rootModule).getModifiableModel();

		boolean isPlatformModule = PluginUtil.isAvailablePlatformModule(rootModule);
		boolean isAppModule = PluginUtil.isAvailableAppModule(rootModule);

		try {
			// 1.先删掉除sdk和source的所有内容
			if (!PluginUtil.isExcludeModule(rootModule)) {
				// 先把可能存在的依赖遍历一下删除
				modifiableModel.orderEntries().forEach(new Processor<OrderEntry>() {
					@Override
					public boolean process(OrderEntry orderEntry) {
						if (orderEntry instanceof InheritedJdkOrderEntry) {

						} else if (orderEntry instanceof ModuleSourceOrderEntry) {

						} else if (orderEntry instanceof ModuleOrderEntry) {
							modifiableModel.removeOrderEntry(orderEntry);
						} else if (orderEntry instanceof LibraryOrderEntry) {
							modifiableModel.removeOrderEntry(orderEntry);
						}
						return true;
					}
				});
			}

			// 2.如果是平台module：只依赖：其他平台module，aws_lib，不包括所有的应用
			if (isPlatformModule) {
				// getPlatformModuleList
				List<Module> platformModuleList = getPlatformModuleList();
				for (Module module : platformModuleList) {
					if (module.equals(rootModule)) {
						continue;
					}
					//判断是否在依赖列表，没有的话加入
					if (modifiableModel.findModuleOrderEntry(module) == null) {
						modifiableModel.addModuleOrderEntry(module);
					}
				}

				// aws_lib
				if (modifiableModel.findLibraryOrderEntry(aws_lib) == null) {
					modifiableModel.addLibraryEntry(aws_lib);
				}
			}

			// 3.如果是应用module，只依赖：平台的module，aws_lib，自身的lib
			if (isAppModule) {
				// getPlatformModuleList
				List<Module> platformModuleList = getPlatformModuleList();
				for (Module module : platformModuleList) {
					if (module.equals(rootModule)) {
						continue;
					}
					//判断是否在依赖列表，没有的话加入
					if (modifiableModel.findModuleOrderEntry(module) == null) {
						modifiableModel.addModuleOrderEntry(module);
					}
				}

				// aws_lib
				if (modifiableModel.findLibraryOrderEntry(aws_lib) == null) {
					modifiableModel.addLibraryEntry(aws_lib);
				}

				// 自身的lib
				String tmpModuleLibraryName = "lib:" + rootModule.getName();
				Library libraryByName = projectTable.getLibraryByName(tmpModuleLibraryName);
				if (libraryByName != null) {
					if (modifiableModel.findLibraryOrderEntry(libraryByName) == null) {
						modifiableModel.addLibraryEntry(libraryByName);
					}
				}

			}

			if (modifiableModel.isChanged()) {
				ApplicationManager.getApplication().invokeAndWait(() -> WriteAction.run(modifiableModel::commit));
				return rootModule.getName();
			} else {
				return "";
			}
		} finally {
			if (!modifiableModel.isDisposed()) {
				modifiableModel.dispose();
			}
		}
	}

}
