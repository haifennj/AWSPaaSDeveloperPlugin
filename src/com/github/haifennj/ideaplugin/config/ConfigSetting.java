package com.github.haifennj.ideaplugin.config;

import com.github.haifennj.ideaplugin.config.ui.ConfigSettingFormUI;
import com.github.haifennj.ideaplugin.helper.NotificationUtil;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConfigSetting implements SearchableConfigurable {
	private ConfigSettingFormUI form = new ConfigSettingFormUI();

	@Override
	public @NotNull
	@NonNls
	String getId() {
		return ConfigSettingKeys.SETTING_ID;
	}

	@Override
	public @NlsContexts.ConfigurableName String getDisplayName() {
		return ConfigSettingKeys.SETTING_NAME;
	}

	@Override
	public @Nullable
	JComponent createComponent() {
		String devVersion = ConfigSettingUtil.getString(ConfigSettingKeys.SETTING_AWS_VERSION_CONFIG_KEY);
		if (ConfigSettingKeys.SETTING_AWS_VERSION_VAL_6.equals(devVersion)) {
			form.getAWS6().setSelected(true);
		} else if (ConfigSettingKeys.SETTING_AWS_VERSION_VAL_7.equals(devVersion)) {
			form.getAWS7().setSelected(true);
		}
		return form.getRootPanel();
	}

	@Override
	public boolean isModified() {
		return true;
	}

	@Override
	public void apply() throws ConfigurationException {
		JRadioButton aws6RadioButton = form.getAWS6();
		JRadioButton aws7RadioButton = form.getAWS7();
		String text = "";
		if (aws6RadioButton.isSelected()) {
			text = ConfigSettingKeys.SETTING_AWS_VERSION_VAL_6;
		} else if (aws7RadioButton.isSelected()) {
			text = ConfigSettingKeys.SETTING_AWS_VERSION_VAL_7;
		}
		ConfigSettingUtil.save(ConfigSettingKeys.SETTING_AWS_VERSION_CONFIG_KEY, text);
		NotificationUtil.info("save " + text + " success!");
	}
}
