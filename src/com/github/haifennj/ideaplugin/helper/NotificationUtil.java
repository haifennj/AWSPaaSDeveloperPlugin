package com.github.haifennj.ideaplugin.helper;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class NotificationUtil {
	// 获取通知组管理器
	private static NotificationGroupManager manager = NotificationGroupManager.getInstance();
	// 获取注册的通知组
	private static NotificationGroup balloon = manager.getNotificationGroup("com.github.haifennj.ideaplugin.notification.balloon");

	private static void showNotification(AnActionEvent anActionEvent, String content, NotificationType notificationType) {
		Notification notification = new Notification("AWS Developer Plugins Notification Group", "AWS Developer Plugins", content, notificationType);
		notification.notify(AnAction.getEventProject(anActionEvent));
	}

	public static void showInfoNotification(AnActionEvent anActionEvent, String content) {
		showNotification(anActionEvent, content, NotificationType.INFORMATION);
	}

	public static void showWarningNotification(AnActionEvent anActionEvent, String content) {
		showNotification(anActionEvent, content, NotificationType.WARNING);
	}

	public static void showErrorNotification(AnActionEvent anActionEvent, String content) {
		showNotification(anActionEvent, content, NotificationType.ERROR);
	}

	private static void showNotification(Project project, String content, NotificationType notificationType) {
		Notification notification = new Notification("AWS Developer Plugins Notification Group", "AWS Developer Plugins", content, notificationType);
		notification.notify(project);
	}

	public static void showInfoNotification(Project project, String content) {
		showNotification(project, content, NotificationType.INFORMATION);
	}

	public static void showWarningNotification(Project project, String content) {
		showNotification(project, content, NotificationType.WARNING);
	}

	public static void showErrorNotification(Project project, String content) {
		showNotification(project, content, NotificationType.ERROR);
	}

	public static void info(String msg) {
		Notification notification = balloon.createNotification(msg, NotificationType.INFORMATION);
		Notifications.Bus.notify(notification);
	}

	public static void warning(String msg) {
		Notification notification = balloon.createNotification(msg, NotificationType.WARNING);
		Notifications.Bus.notify(notification);
	}

	public static void error(String msg) {
		Notification notification = balloon.createNotification(msg, NotificationType.ERROR);
		Notifications.Bus.notify(notification);
	}
}
