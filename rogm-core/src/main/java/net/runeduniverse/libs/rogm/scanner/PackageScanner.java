package net.runeduniverse.libs.rogm.scanner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import net.runeduniverse.libs.utils.DataHashMap;
import net.runeduniverse.libs.utils.DataMap;

public class PackageScanner {

	private final List<ClassLoader> loader = new ArrayList<>();
	private final List<String> pkgs = new ArrayList<>();
	private final List<IScanner> scanner = new ArrayList<>();
	private boolean includeSubPkgs = false;

	public PackageScanner includeClassLoader(ClassLoader... loader) {
		this.loader.addAll(Arrays.asList(loader));
		return this;
	}

	public PackageScanner includeSubPkgs() {
		this.includeSubPkgs = true;
		return this;
	}

	public void scan() {
		if (this.loader.isEmpty())
			this.loader.add(PackageScanner.class.getClassLoader());

		DataMap<Class<?>, ClassLoader, String> classes = new DataHashMap<>();
		for (ClassLoader classLoader : loader)
			for (String pkg : pkgs)
				findClasses(classes, classLoader, pkg);

		classes.forEach((c, l, p) -> {
			for (IScanner s : PackageScanner.this.scanner)
				s.scan(c, l, p);
		});
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 */
	private void findClasses(DataMap<Class<?>, ClassLoader, String> classes, ClassLoader classLoader, String pkg) {
		Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(pkg.replace('.', '/'));
		} catch (IOException e) {
			return;
		}
		List<File> dirs = new ArrayList<>();
		while (resources.hasMoreElements())
			dirs.add(new File(resources.nextElement().getFile()));
		for (File directory : dirs)
			findClasses(classes, classLoader, directory, pkg);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 */
	private void findClasses(DataMap<Class<?>, ClassLoader, String> classes, ClassLoader classLoader, File directory,
			String pkg) {
		if (!directory.exists())
			return;
		for (File file : directory.listFiles())
			if (file.isDirectory() && !file.getName().contains(".")) {
				if (this.includeSubPkgs)
					findClasses(classes, classLoader, file, pkg + "." + file.getName());
			} else if (file.getName().endsWith(".class"))
				try {
					classes.put(Class.forName(pkg + '.' + file.getName().substring(0, file.getName().length() - 6),
							true, classLoader), classLoader, pkg);
				} catch (ClassNotFoundException e) {
				}
	}
}
