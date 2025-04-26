package com.github.haifennj.ideaplugin.library;

import org.jetbrains.annotations.NotNull;

import com.github.haifennj.ideaplugin.helper.PluginUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by Haiifenng on 2017.01.07.
 */
public class AWSLibraryRefreshAction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		AWSLibraryRefresh r = new AWSLibraryRefresh(anActionEvent.getProject());
		r.refreshAWSLibrary();
	}

	@Override
	public void update(@NotNull AnActionEvent e) {
		e.getPresentation().setVisible(!PluginUtil.isAWS7());
	}
}
