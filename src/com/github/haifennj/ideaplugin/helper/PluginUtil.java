package com.github.haifennj.ideaplugin.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.jetbrains.annotations.Nullable;

import com.github.haifennj.ideaplugin.library.FileSuffixFilter;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;

/**
 * Created by Haiifenng on 2017.01.16.
 */
public class PluginUtil {

	private static volatile boolean isAWS7 = false;

	public static Module getReleaseModule(Project project) {
		return getReleaseModule(project, false);
	}

	@Nullable
	public static Module getReleaseModule(Project project, boolean isMsg) {
		Module releaseModule = ModuleManager.getInstance(project).findModuleByName("release");
		if (releaseModule == null) {
			releaseModule = ModuleManager.getInstance(project).findModuleByName("aws.release");
		}
		if (releaseModule == null) {
			if (isMsg) {
				NotificationUtil.showErrorNotification(project, "当前Project中没有命名为[release]的Module");
			}
			System.err.println("当前Project中没有命名为[release]的Module");
			return null;
		}
		if (!"aws.release".equals(releaseModule.getName()) && releaseModule.getModuleFile() == null) {
			if (isMsg) {
				NotificationUtil.showErrorNotification(project, "当前Project中的[release]的不是一个有效的AWS资源");
			}
			System.err.println("当前Project中的[release]的不是一个有效的AWS资源");
			return null;
		}
		VirtualFile file = findReleaseModuleFile(project);
		//校验是不是一个有效的release
		if (file != null) {
			return releaseModule;
		} else {
			if (isMsg) {
				NotificationUtil.showErrorNotification(project, "当前Project中的[release]的不是一个有效的AWS资源");
			}
			System.err.println("当前Project中的[release]的不是一个有效的AWS资源");
			return null;
		}
	}

	public static VirtualFile findReleaseModuleFile(Project project) {
		Module releaseModule = ModuleManager.getInstance(project).findModuleByName("release");
		if (releaseModule == null) {
			releaseModule = ModuleManager.getInstance(project).findModuleByName("aws.release");
		}
		if (releaseModule == null) {
			return null;
		}
		VirtualFile file = null;
		if (releaseModule.getModuleFile()!=null) {
			file = releaseModule.getModuleFile().getParent();
			if (isReleaseDir(file)) {
				return file;
			}
		} else {
			Collection<VirtualFile> virtualFilesByName = FilenameIndex.getVirtualFilesByName(project, "release", GlobalSearchScope.allScope(project));
			for (VirtualFile virtualFile : virtualFilesByName) {
				if (virtualFile.isDirectory()) {
					boolean releaseDir = isReleaseDir(virtualFile);
					if (releaseDir) {
						return virtualFile;
					}
				}
			}
		}
		return null;
	}

	public static List<File> findAllFileInPath(String rootPath, FilenameFilter filenameFilter) {
		List<File> result = new ArrayList<>();
		File rootFile = new File(rootPath);
		LinkedList<File> list = new LinkedList<>();
		File[] childs = rootFile.listFiles();
		if (childs != null) {
			for (File child : childs) {
				list.add(child);
			}

		}
		while (!list.isEmpty()) {
			File wrap = list.removeFirst();
			if ((!wrap.isDirectory()) && (filenameFilter.accept(wrap, wrap.getName()))) {
				result.add(wrap);
			}

			childs = wrap.listFiles();
			if (childs != null) {
				for (File child : childs) {
					list.add(child);
				}
			}
		}
		return result;
	}

	public static boolean updateModuleLibraries(Project project, Module module, String[] libPaths) {
		String libName = "lib:" + module.getName() + "";
		List<VirtualFile> jarList = new ArrayList<>();
		for (String libPath : libPaths) {
			File fLibPath = new File(libPath);
			List<File> allFileInPath = PluginUtil.findAllFileInPath(fLibPath.getPath(), new FileSuffixFilter(".jar"));
			for (File file : allFileInPath) {
				if (file.getName().contains(module.getName())) {
					continue;
				}
				VirtualFile jar = JarFileSystem.getInstance().findFileByPath(file.getPath() + "!/");
				if (jar != null) {
					jarList.add(jar);
				}
			}
		}
		if (jarList.isEmpty()) {
			return false;
		}
		ApplicationManager.getApplication().runWriteAction(new Runnable() {
			@Override
			public void run() {
				LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
				Library _lib = libraryTable.getLibraryByName(libName);
				LibraryTable.ModifiableModel projectModel = libraryTable.getModifiableModel();
				if (_lib != null) {
					projectModel.removeLibrary(_lib);
				}
				projectModel.commit();
			}
		});

		ApplicationManager.getApplication().runWriteAction(new Runnable() {
			@Override
			public void run() {
				LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
				LibraryTable.ModifiableModel projectModel = libraryTable.getModifiableModel();
				Library _lib = projectModel.createLibrary(libName);

				final Library.ModifiableModel newLibModel = _lib.getModifiableModel();
				for (VirtualFile virtualFile : jarList) {
					newLibModel.addRoot(virtualFile, OrderRootType.CLASSES);
				}

				final ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(module).getModifiableModel();
				final List<OrderEntry> libEntry = new ArrayList<>();//遍历出lib的列表
				OrderEnumerator orderEnumerator = modifiableModel.orderEntries();
				orderEnumerator.forEach(new Processor<OrderEntry>() {
					@Override
					public boolean process(OrderEntry orderEntry) {
						if (orderEntry.getPresentableName().equals(libName)) {
							libEntry.add(orderEntry);
						}
						return true;
					}
				});
				for (OrderEntry orderEntry : libEntry) {
					modifiableModel.removeOrderEntry(orderEntry);
				}
				modifiableModel.addLibraryEntry(_lib);
				newLibModel.commit();
				modifiableModel.commit();
				projectModel.commit();
			}
		});
		return true;
	}

	public static boolean isAvailablePlatformModule(Module module) {
		if (module == null) {
			return false;
		}
		String name = module.getName();
		if (PluginUtil.isExcludeModule(module)) {
			return false;
		}
		if (isAvailableAppModule(module)) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isAvailableAppModule(Module module) {
		if (module == null) {
			return false;
		}
		String name = module.getName();
		if (PluginUtil.isExcludeModule(module)) {
			return false;
		}
		String moduleFilePath = module.getModuleFilePath();
		if (checkManifestXml(module.getModuleFile()) && (moduleFilePath.contains("apps/install") || moduleFilePath.contains("apps/"))) {
			return true;
		} else {
			return false;
		}
	}

	//	public static boolean isExcludeModule(String name) {
	//		String[] excludes = { "docs", "release", "aws-all", "aws", "apps", "web", "h5designer", "aws-security", "security", "aws-schema" };
	//		List<String> strings = Arrays.asList(excludes);
	//		return strings.contains(name);
	//	}

	/**
	 * 是否排除的module
	 * @param module
	 * @return
	 */
	public static boolean isExcludeModule(Module module) {
		VirtualFile file = module.getModuleFile();
		if (file == null) {
			return false;
		}
		VirtualFile srcMainPath = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(file.getParent().getPath(), "src/main/java"));
		VirtualFile srcPath = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(file.getParent().getPath(), "src"));
		if (srcMainPath != null && srcMainPath.exists()) {
			return false;
		}
		if (srcPath != null && srcPath.exists()) {
			return false;
		}
		return true;
	}

	public static boolean isAWS7() {
		return isAWS7;
	}

	public static Future<Boolean> checkAws7(Project project) {
		return ApplicationManager.getApplication().executeOnPooledThread(() ->
				ApplicationManager.getApplication().runReadAction((Computable<Boolean>) () -> {
					boolean result = realCheckAws7(project);
					isAWS7 = result; // 更新静态变量
					return result;
				})
		);
	}

	private static boolean realCheckAws7(Project project) {
		Collection<VirtualFile> virtualFilesByName = FilenameIndex.getVirtualFilesByName(project, "release", GlobalSearchScope.allScope(project));
		for (VirtualFile virtualFile : virtualFilesByName) {
			if (virtualFile.isDirectory()) {
				String releasePath = virtualFile.getPath();
				File file_release7_1 = new File(releasePath + "/bin/conf/application-dev.yml");
				File file_release7_2 = new File(releasePath + "/bin/conf/bootstrap.yml");
				if (file_release7_1.exists() && file_release7_2.exists()) {//AWS7版本
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isReleaseDir(VirtualFile file) {
		//校验是不是一个有效的release
		String releasePath = file.getPath();
		File file_release7_1 = new File(releasePath + "/bin/conf/application-dev.yml");
		File file_release7_2 = new File(releasePath + "/bin/conf/application.yml");

		File file_release6_1 = new File(releasePath + "/bin/conf/server.xml");
		File file_release6_2 = new File(releasePath + "/bin/lib/aws-license.jar");

		File file_release5_1 = new File(releasePath + "/bin/system.xml");
		File file_release5_2 = new File(releasePath + "/bin/lib/aws.platform.jar");

		if (file_release7_1.exists() && file_release7_2.exists()) {//AWS7版本
			return true;
		} else if (file_release6_1.exists() && file_release6_2.exists()) {//AWS6版本
			return true;
		} else if (file_release5_1.exists() && file_release5_2.exists()) {//AWS5版本
			return true;
		} else {
			return false;
		}
	}

	public static boolean isJavaModuleDir(VirtualFile file) {
		if (file == null) {
			return false;
		}
		VirtualFile srcMainPath = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(file.getPath(), "src/main/java"));
		VirtualFile srcPath = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(file.getPath(), "src"));
		if (srcMainPath != null && srcMainPath.exists()) {
			return true;
		}
		if (srcPath != null && srcPath.exists()) {
			return true;
		}
		return false;
	}

	public static boolean isAWSWebModule(String name) {
		String[] excludes = { "aws-infrastructure-web", "aws-node-wrapper", "aws-coe-web", "aws-api-client" };
		List<String> strings = Arrays.asList(excludes);
		return strings.contains(name);
	}

	public static List<String> getAppDirs(File installDir) {
		List<String> list = new ArrayList<>();
		File[] files = installDir.listFiles();
		for (File file : files) {
			if (".DS_Store".equals(file.getName())) {
				continue;
			}
			if (file.isDirectory() && !file.getName().startsWith("_bpm")) {
				list.add(file.getName());
			}
		}
		return list;
	}

	public static boolean checkManifestXml(VirtualFile file) {
		if (file == null) {
			return false;
		}
		File manifestFile = null;
		if (file.isDirectory()) {
			manifestFile = new File(file.getPath() + "/manifest.xml");
		} else {
			manifestFile = new File(file.getParent().getPath() + "/manifest.xml");
		}
		return manifestFile.exists();
	}

}
