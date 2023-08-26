package com.github.haifennj.ideaplugin.file;

import com.github.haifennj.ideaplugin.helper.OSUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;

public class FileCopyAction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		VirtualFile[] data = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(anActionEvent.getDataContext());
		for (VirtualFile file : data) {
			FileCopy fc = new FileCopy(anActionEvent.getProject());
			fc.copyToDesktop(file);
		}
	}

	@Override
	public void update(AnActionEvent e) {
		VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
		if (file == null) {
			e.getPresentation().setVisible(false);
		} else {
			if (file.isDirectory()) {
				e.getPresentation().setVisible(false);
				return;
			}
			e.getPresentation().setEnabledAndVisible(OSUtil.isMacOSX());
		}
	}
}
