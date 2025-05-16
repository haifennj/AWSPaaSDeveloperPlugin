package com.github.haifennj.ideaplugin.link;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.haifennj.ideaplugin.helper.NotificationUtil;
import com.github.haifennj.ideaplugin.helper.OSUtil;
import com.github.haifennj.ideaplugin.helper.PluginUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by Haiifenng on 2017.05.19.
 */
public class LinkAppAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		DataContext dataContext = e.getDataContext();
		VirtualFile[] data = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
		if (data != null) {
			StringBuilder message = new StringBuilder();
			Module releaseModule = PluginUtil.getReleaseModule(e.getProject(), true);
			if (releaseModule == null) {
				return;
			}
			for (VirtualFile file : data) {
				if (checkFileExist(e, file)) {
					continue;
				}
				createLink(releaseModule, file);
			}
			String installPath = releaseModule.getModuleFile().getParent().getPath() + "/apps/install/";
			VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(installPath));
			virtualFile.refresh(false, true);
			NotificationUtil.showInfoNotification(e, "符号/目录链接创建成功");
		}
	}

	protected void createLink(Module releaseModule, VirtualFile file) {
		List<Map<String, String>> list = new ArrayList<>();
		String targetFile = releaseModule.getModuleFile().getParent().getPath() + "/apps/install/" + file.getName();
		String sourceFile = file.getPath();
		Map<String, String> cmdPath = null;
		// 检查sourceFile下面有没有web目录
		String webRoot = "/webserver/webapps/portal";
		String webTargetFile = releaseModule.getModuleFile().getParent().getPath() + webRoot + "/apps/" + file.getName();
		String webSourceFile = file.getPath() + "/web/" + file.getName();
		File webSourceFileObj = new File(webSourceFile);
		if (webSourceFileObj.exists()) {
			// cd /data/develop/release/webserver/webapps/portal/apps/
			// ln -s /data/develop/release/apps/install/com.actionsoft.apps.dingding.yijing/web/com.actionsoft.apps.dingding.yijing com.actionsoft.apps.dingding.yijing
			cmdPath = new HashMap<>();
			cmdPath.put("targetFile", webTargetFile);
			cmdPath.put("sourceFile", webSourceFile);
			list.add(cmdPath);
		}
		cmdPath = new HashMap<>();
		cmdPath.put("targetFile", targetFile);
		cmdPath.put("sourceFile", sourceFile);
		list.add(cmdPath);
		for (Map<String, String> map : list) {
			execCMD(map.get("targetFile"), map.get("sourceFile"));
		}
	}

	private void execCMD(String targetFile, String sourceFile) {
		String cmd = "";
		if (OSUtil.isMacOSX() || OSUtil.isLinux()) {
			cmd = "ln -s " + sourceFile + " " + targetFile;
			link(cmd);
		} else if (OSUtil.isWindows()) {
			sourceFile = sourceFile.replaceAll("/", "\\\\");
			targetFile = targetFile.replaceAll("/", "\\\\");
			cmd = "cmd.exe /c mklink /j " + targetFile + " " + sourceFile;
			link(cmd);
		}
	}

	protected void link(String cmd) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(AnActionEvent e) {
		VirtualFile[] data = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
		VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
		if (file == null) {
			e.getPresentation().setVisible(false);
		} else {
			if (data.length > 1) {
				for (VirtualFile virtualFile : data) {
					checkFile(e, virtualFile, true);
				}
				e.getPresentation().setText("Link Apps");
			} else {
				checkFile(e, file, false);
			}
		}
	}

	private void checkFile(AnActionEvent e, VirtualFile file, boolean isMulti) {
		String flag = "/apps/";
		Module releaseModule = PluginUtil.getReleaseModule(e.getProject());
		if (releaseModule == null) {
			e.getPresentation().setVisible(false);
			return;
		}
		if (!file.isDirectory()) {
			e.getPresentation().setVisible(false);
			return;
		}
		if (file.getPath().contains("/apps/install/") || file.getPath().contains("release/")) {
			e.getPresentation().setVisible(false);
			return;
		}
		if (!isMulti && checkFileExist(e, file)) {
			e.getPresentation().setText("Already Linked");
			e.getPresentation().setEnabled(false);
			return;
		}
		String filePath = file.getPath();
		if (file.getName().startsWith("_bpm")) {
			e.getPresentation().setVisible(false);
			return;
		}
		if (!checkManifestXml(e, file)) {
			e.getPresentation().setVisible(false);
			return;
		}
		if (filePath.contains(flag)) {
			String appId = filePath.substring(filePath.indexOf(flag) + flag.length());
			//说明是子文件夹或文件
			if (appId.contains("/")) {
				if (StringUtil.countChars(appId, File.separatorChar) != 1) {
					e.getPresentation().setVisible(false);
				}
			}
		} else {
			e.getPresentation().setVisible(false);
		}
	}

	protected boolean checkManifestXml(AnActionEvent e, VirtualFile file) {
		return PluginUtil.checkManifestXml(file);
	}

	protected boolean checkFileExist(AnActionEvent e, VirtualFile file) {
		VirtualFile releaseModuleFile = PluginUtil.findReleaseModuleFile(e.getProject());
		if (releaseModuleFile != null) {
			String targetFilePath = releaseModuleFile.getPath() + "/apps/install/" + file.getName();
			return new File(targetFilePath).exists();
		}
		return false;
	}

}
