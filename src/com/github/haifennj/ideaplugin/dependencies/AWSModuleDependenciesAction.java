package com.github.haifennj.ideaplugin.dependencies;

import com.github.haifennj.ideaplugin.config.ConfigSettingKeys;
import com.github.haifennj.ideaplugin.config.ConfigSettingUtil;
import com.github.haifennj.ideaplugin.helper.NotificationUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by Haiifenng on 2017.05.07.
 */
public class AWSModuleDependenciesAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {

		String version = ConfigSettingUtil.getString(ConfigSettingKeys.SETTING_AWS_VERSION_CONFIG_KEY);
		String message = "";
		if (ConfigSettingKeys.SETTING_AWS_VERSION_VAL_7.equals(version)) {
			AWS7ModuleDependencies awsModuleDependencies = new AWS7ModuleDependencies(anActionEvent.getProject());
			message = awsModuleDependencies.updateDependencies();
		} else {
			AWS6ModuleDependencies awsModuleDependencies = new AWS6ModuleDependencies(anActionEvent.getProject());
			message = awsModuleDependencies.updateDependencies();
		}
		NotificationUtil.showInfoNotification(anActionEvent, message);
	}

}
