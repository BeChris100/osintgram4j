package net.bc100dev.commons.utils.io;

import net.bc100dev.commons.utils.RuntimeEnvironment;
import net.bc100dev.commons.utils.Utility;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static void createFile(String filePath, boolean createDirs) throws IOException {
        File file = new File(filePath);
        File dirs = new File(filePath.substring(0, Utility.getLastPathSeparator(filePath, false)));

        if (!dirs.exists()) {
            if (!createDirs)
                throw new FileNotFoundException(String.format("No directories at \"%s\" were found", dirs.getAbsolutePath()));

            createDirectory(dirs.getAbsolutePath());
        }

        if (!file.createNewFile())
            throw new IOException(String.format("File at \"%s\" could not be created", filePath));
    }

    public static void createDirectory(String path) throws IOException {
        File dir = new File(path.substring(0, Utility.getLastPathSeparator(path, true)));

        if (dir.exists())
            return;

        if (!dir.mkdirs())
            throw new IOException(String.format("Could not create new directories at \"%s\"", path));
    }

    public static char[] readChars(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException(String.format("File at \"%s\" not found", filePath));

        if (file.isDirectory())
            throw new IllegalStateException(String.format("\"%s\" is a directory", filePath));

        if (!file.canRead())
            throw new AccessDeniedException(String.format("Read-access for file \"%s\" denied", filePath));

        try (FileInputStream fis = new FileInputStream(file)) {
            StringBuilder str = new StringBuilder();

            int len;
            byte[] buff = new byte[1024];

            while ((len = fis.read(buff, 0, 1024)) != -1)
                str.append(new String(buff, 0, len));

            return str.toString().toCharArray();
        }
    }

    public static byte[] readBytes(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException(String.format("File at \"%s\" not found", filePath));

        if (file.isDirectory())
            throw new IllegalStateException(String.format("\"%s\" is a directory", filePath));

        if (!file.canRead())
            throw new AccessDeniedException(String.format("Read-access for file \"%s\" denied", filePath));

        try (FileInputStream fis = new FileInputStream(file)) {
            StringBuilder str = new StringBuilder();

            int len;
            byte[] buff = new byte[1024];

            while ((len = fis.read(buff, 0, 1024)) != -1)
                str.append(new String(buff, 0, len));

            return str.toString().getBytes();
        }
    }

    public static String readString(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException(String.format("File at \"%s\" not found", filePath));

        if (file.isDirectory())
            throw new IllegalStateException(String.format("\"%s\" is a directory", filePath));

        if (!file.canRead())
            throw new AccessDeniedException(String.format("Read-access for file \"%s\" denied", filePath));

        try (FileInputStream fis = new FileInputStream(file)) {
            StringBuilder str = new StringBuilder();

            int len;
            byte[] buff = new byte[1024];

            while ((len = fis.read(buff, 0, 1024)) != -1)
                str.append(new String(buff, 0, len));

            return str.toString();
        }
    }

    public static String removeName(String path) {
        String cutPath = removePath(path);
        return path.replaceFirst(cutPath, "");
    }

    public static String removePath(String path) {
        int sep = Utility.getLastPathSeparator(path, false);
        return path.substring(sep + 1);
    }

    public static void makeDirs(String dirPath) throws Exception {
        File dir = new File(removeName(dirPath));

        if (dir.exists())
            return;

        if (!dir.mkdirs())
            throw new AccessDeniedException(String.format("Could not make new directories at \"%s\"", removeName(dirPath)));
    }

    public static void write(String filePath, char[] contents) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            createFile(filePath, true);

        if (file.exists() && file.isDirectory())
            throw new IllegalStateException(String.format("\"%s\" is a directory", filePath));

        if (file.exists() && !file.canWrite())
            throw new AccessDeniedException(String.format("\"%s\" could not be overwritten", filePath));

        if (!file.canWrite())
            throw new AccessDeniedException(String.format("\"%s\" could not be written", filePath));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (char c : contents)
                fos.write(c);
        }
    }

    public static void write(String filePath, byte[] contents) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            createFile(filePath, true);

        if (file.exists() && file.isDirectory())
            throw new IllegalStateException(String.format("\"%s\" is a directory", filePath));

        if (file.exists() && !file.canWrite())
            throw new AccessDeniedException(String.format("\"%s\" could not be overwritten", filePath));

        if (!file.canWrite())
            throw new AccessDeniedException(String.format("\"%s\" could not be written", filePath));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (byte c : contents)
                fos.write(c);
        }
    }

    public static void write(String filePath, String contents) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            createFile(filePath, true);

        if (file.exists() && file.isDirectory())
            throw new IllegalStateException(String.format("\"%s\" is a directory", filePath));

        if (file.exists() && !file.canWrite())
            throw new AccessDeniedException(String.format("\"%s\" could not be overwritten", filePath));

        if (!file.canWrite())
            throw new AccessDeniedException(String.format("\"%s\" could not be written", filePath));

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(contents);
        }
    }

    public static List<String> listDirectory(String dirPath, boolean nameSort, boolean removePaths, boolean ignoreErrors) throws IOException {
        List<String> data = new ArrayList<>();
        File dir = new File(dirPath);

        if (!dir.exists())
            throw new FileNotFoundException(String.format("\"%s\" does not exist", dirPath));

        if (!dir.canRead()) {
            if (!ignoreErrors)
                throw new AccessDeniedException(String.format("Cannot access \"%s\"", dirPath));
        }

        if (dir.isFile()) {
            data.add(dirPath);

            if (removePaths)
                data.set(0, removePath(data.get(0)));

            return data;
        }

        File[] files = dir.listFiles();
        if (files == null)
            return data;

        for (File file : files)
            data.add(file.getAbsolutePath());

        if (removePaths) {
            if (!data.isEmpty())
                data.replaceAll(FileUtil::removePath);
        }

        if (nameSort) {
            if (!data.isEmpty())
                Collections.sort(data);
        }

        return data;
    }

    public static List<String> specificNameScan(String path, String name, boolean excludeAddingFolders, boolean ignoreErrors) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile()) {
            if (path.contains(name))
                results.add(path);

            return results;
        }

        List<String> data = listDirectory(path, true, false, ignoreErrors);

        for (String item : data) {
            File file = new File(item);

            if (!RuntimeEnvironment.isWindows()) {
                if ((file.getAbsolutePath().toLowerCase().contains("proton") && file.getAbsolutePath().toLowerCase().contains(":")) ||
                        (file.getAbsolutePath().toLowerCase().contains("dosdevices") && file.getAbsolutePath().toLowerCase().contains(":")))
                    continue;
            }

            if (file.getAbsolutePath().toLowerCase().contains("steam"))
                continue;

            if (excludeAddingFolders) {
                if (file.getAbsolutePath().contains(name) && file.isFile())
                    results.add(file.getAbsolutePath());
            } else {
                if (file.getAbsolutePath().contains(name))
                    results.add(file.getAbsolutePath());
            }

            if (file.isDirectory())
                results.addAll(specificNameScan(item, name, excludeAddingFolders, ignoreErrors));
        }

        return results;
    }

    public static List<String> scanWithSpecificEnding(String path, String ending, boolean ignoreErrors) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile()) {
            if (path.endsWith(ending))
                results.add(path);

            return results;
        }

        List<String> data = listDirectory(path, true, false, ignoreErrors);

        for (String item : data) {
            File file = new File(item);

            if (file.isFile()) {
                if (file.getAbsolutePath().endsWith(ending))
                    results.add(item);
            }

            if (file.isDirectory())
                results.addAll(scanWithSpecificEnding(item, ending, ignoreErrors));
        }

        return results;
    }

    public static List<String> scanFiles(String path, boolean ignoreErrors) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile())
            return results;

        List<String> data = listDirectory(path, true, false, ignoreErrors);

        for (String itemPath : data) {
            File file = new File(itemPath);

            if (file.isFile())
                results.add(itemPath);

            if (file.isDirectory())
                results.addAll(scanFiles(itemPath, ignoreErrors));
        }

        return results;
    }

    public static List<String> scanFolders(String path, boolean ignoreErrors) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile())
            return results;

        List<String> data = listDirectory(path, true, false, ignoreErrors);

        for (String itemPath : data) {
            File file = new File(itemPath);

            if (file.isFile())
                continue;

            if (file.isDirectory()) {
                results.add(itemPath);
                results.addAll(scanFolders(itemPath, ignoreErrors));
            }
        }

        return results;
    }

    public static void delete(String path) throws IOException {
        File inner = new File(path);

        if (!inner.exists())
            return;

        if (inner.isFile()) {
            if (!inner.delete())
                throw new IOException(String.format("Could not delete \"%s\"", path));

            return;
        }

        if (listDirectory(path, false, false, false).isEmpty()) {
            if (!inner.delete())
                throw new IOException(String.format("Could not delete \"%s\"", path));

            return;
        }

        File[] files = inner.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            BasicFileAttributes fileAttrib = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

            if (fileAttrib.isDirectory())
                delete(file.getAbsolutePath());

            if (fileAttrib.isRegularFile()) {
                if (!file.delete())
                    throw new IOException(String.format("Could not delete a file at \"%s\"", file.getAbsolutePath()));
            }

            if (fileAttrib.isSymbolicLink()) {
                if (!file.delete())
                    throw new IOException(String.format("Could not delete a linked file at \"%s\"", file.getAbsolutePath()));
            }

            if (fileAttrib.isOther()) {
                if (!file.delete())
                    throw new IOException(String.format("Could not delete \"%s\"", file.getAbsolutePath()));
            }
        }

        if (!inner.delete())
            throw new IOException(String.format("Could not complete deletion at \"%s\"", inner.getAbsolutePath()));
    }

    public static void copyFile(String sourceFilePath, String destFilePath) throws IOException {
        File src = new File(sourceFilePath);
        File dest = new File(destFilePath);

        if (!src.exists())
            throw new FileNotFoundException(String.format("File at \"%s\" not found", sourceFilePath));

        if (!src.canRead())
            throw new AccessDeniedException(String.format("The current user cannot access \"%s\" (Permission denied)", sourceFilePath));

        if (!src.isFile())
            throw new IOException(String.format("\"%s\" is not a file", sourceFilePath));

        if (!dest.exists())
            createFile(destFilePath, true);

        if (!dest.canWrite())
            throw new AccessDeniedException(String.format("Cannot make copy operation from \"%s\" to \"%s\" (Permission denied)", sourceFilePath, destFilePath));

        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] bis = new byte[2048];
            int len;

            while ((len = fis.read(bis, 0, 2048)) != -1)
                fos.write(bis, 0, len);
        }
    }

    public static void moveFile(String sourceFilePath, String destFilePath) throws IOException {
        copyFile(sourceFilePath, destFilePath);
        delete(sourceFilePath);
    }

    public static void copyDir(String sourceDirPath, String destDirPath) throws IOException {
        File srcDir = new File(sourceDirPath);
        File destDir = new File(destDirPath);

        if (!srcDir.exists())
            throw new FileNotFoundException(String.format("File or directory at \"%s\" not found", sourceDirPath));

        if (!srcDir.canRead())
            throw new AccessDeniedException(String.format("Cannot make read operations on \"%s\" (Permission denied)", sourceDirPath));

        if (srcDir.isFile()) {
            copyFile(sourceDirPath, destDirPath + File.separator + srcDir.getName());
            return;
        }

        if (!destDir.exists())
            createDirectory(destDirPath);

        if (!destDir.canWrite())
            throw new AccessDeniedException(String.format("Cannot make copy operations from \"%s\" to \"%s\" (Permission denied)", sourceDirPath, destDirPath));

        List<String> srcList = listDirectory(srcDir.getAbsolutePath(), true, false, false);
        for (String srcItem : srcList) {
            File srcData = new File(srcItem);

            if (srcData.isFile())
                copyFile(srcData.getAbsolutePath(), destDir.getAbsolutePath() + File.separator + srcData.getName());

            if (srcData.isDirectory()) {
                createDirectory(srcItem);
                copyDir(srcData.getAbsolutePath(), destDir.getAbsolutePath() + File.separator + srcData.getName());
            }
        }
    }

}
