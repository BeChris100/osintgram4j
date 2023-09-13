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
            if (createDirs)
                createDirectory(dirs.getAbsolutePath());
            else
                throw new FileNotFoundException("No directories at \"" + dirs.getAbsolutePath() + "\" were found");
        }

        if (!file.createNewFile())
            throw new IOException("File at \"" + filePath + "\" could not be created");
    }

    public static void createDirectory(String path) throws IOException {
        File dir = new File(path.substring(0, Utility.getLastPathSeparator(path, true)));

        if (dir.exists())
            return;

        if (!dir.mkdirs())
            throw new IOException("Could not create new directories at \"" + path + "\"");
    }

    public static char[] readChars(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException("File at \"" + filePath + "\" not found");

        if (file.isDirectory())
            throw new IllegalStateException("\"" + filePath + "\" is a directory");

        if (!file.canRead())
            throw new AccessDeniedException("Read-access for file \"" + filePath + "\" denied");

        FileInputStream fis = new FileInputStream(file);
        StringBuilder str = new StringBuilder();
        int data;

        while ((data = fis.read()) != -1)
            str.append((char) data);

        fis.close();
        return str.toString().toCharArray();
    }

    public static byte[] readBytes(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException("File at \"" + filePath + "\" not found");

        if (file.isDirectory())
            throw new IllegalStateException("\"" + filePath + "\" is a directory");

        if (!file.canRead())
            throw new AccessDeniedException("Read-access for file \"" + filePath + "\" denied");

        FileInputStream fis = new FileInputStream(file);
        StringBuilder str = new StringBuilder();
        int data;

        while ((data = fis.read()) != -1)
            str.append((char) data);

        fis.close();
        return str.toString().getBytes();
    }

    public static String readString(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException("File at \"" + filePath + "\" not found");

        if (file.isDirectory())
            throw new IllegalStateException("\"" + filePath + "\" is a directory");

        if (!file.canRead())
            throw new AccessDeniedException("Read-access for file \"" + filePath + "\" denied");

        FileInputStream fis = new FileInputStream(file);
        StringBuilder str = new StringBuilder();
        int data;

        while ((data = fis.read()) != -1)
            str.append((char) data);

        fis.close();
        return str.toString();
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
            throw new AccessDeniedException("Could not make new directories at \"" + removeName(dirPath) + "\"");
    }

    public static void write(String filePath, char[] contents) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            createFile(filePath, true);

        if (file.exists() && file.isDirectory())
            throw new IllegalStateException("\"" + filePath + "\" is a directory");

        if (file.exists() && !file.canWrite())
            throw new AccessDeniedException("\"" + filePath + "\" could not be overwritten");

        if (!file.canWrite())
            throw new AccessDeniedException("\"" + filePath + "\" could not be written");

        FileOutputStream fos = new FileOutputStream(file);
        for (char c : contents)
            fos.write(c);

        fos.close();
    }

    public static void write(String filePath, byte[] contents) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            createFile(filePath, true);

        if (file.exists() && file.isDirectory())
            throw new IllegalStateException("\"" + filePath + "\" is a directory");

        if (file.exists() && !file.canWrite())
            throw new AccessDeniedException("\"" + filePath + "\" could not be overwritten");

        if (!file.canWrite())
            throw new AccessDeniedException("\"" + filePath + "\" could not be written");

        FileOutputStream fos = new FileOutputStream(file);
        for (byte c : contents)
            fos.write(c);

        fos.close();
    }

    public static void write(String filePath, String contents) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            createFile(filePath, true);

        if (file.exists() && file.isDirectory())
            throw new IllegalStateException("\"" + filePath + "\" is a directory");

        if (file.exists() && !file.canWrite())
            throw new AccessDeniedException("\"" + filePath + "\" could not be overwritten");

        if (!file.canWrite())
            throw new AccessDeniedException("\"" + filePath + "\" could not be written");

        FileWriter writer = new FileWriter(file, false);
        writer.write(contents);
        writer.close();
    }

    public static List<String> listDirectory(String dirPath, boolean nameSort, boolean removePaths, boolean ignoreErrors) throws IOException {
        List<String> data = new ArrayList<>();
        File dir = new File(dirPath);

        if (!dir.exists())
            throw new FileNotFoundException("\"" + dirPath + "\" does not exist");

        if (!dir.canRead()) {
            if (!ignoreErrors)
                throw new AccessDeniedException("Cannot access \"" + dirPath + "\"");
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
            if (data.size() != 0)
                data.replaceAll(FileUtil::removePath);
        }

        if (nameSort) {
            if (data.size() != 0)
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
                throw new IOException("Could not delete \"" + path + "\"");

            return;
        }

        if (listDirectory(path, false, false, false).size() == 0) {
            if (!inner.delete())
                throw new IOException("Could not delete \"" + path + "\"");

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
                    throw new IOException("Could not delete \"" + file.getAbsolutePath() + "\"");
            }

            if (fileAttrib.isSymbolicLink()) {
                if (!file.delete())
                    throw new IOException("Could not delete \"" + file.getAbsolutePath() + "\"");
            }

            if (fileAttrib.isOther()) {
                if (!file.delete())
                    throw new IOException("Could not delete \"" + file.getAbsolutePath() + "\"");
            }
        }

        if (!inner.delete())
            throw new IOException("Could not delete \"" + path + "\"");
    }

    public static void copyFile(String sourceFilePath, String destFilePath) throws IOException {
        File src = new File(sourceFilePath);
        File dest = new File(destFilePath);

        if (!src.exists())
            throw new FileNotFoundException("File at \"" + sourceFilePath + "\" not found");

        if (!src.canRead())
            throw new AccessDeniedException("The current user cannot read \"" + sourceFilePath + "\" (Permission denied)");

        if (!src.isFile())
            throw new IOException("\"" + sourceFilePath + "\" is not a file");

        if (!dest.exists())
            createFile(destFilePath, true);

        if (!dest.canWrite())
            throw new AccessDeniedException("The current user cannot copy contents from \"" + sourceFilePath + "\" to \"" + destFilePath + "\" (Permission denied)");

        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(dest);

        byte[] bis = new byte[2048];
        int len;

        while ((len = fis.read(bis, 0, 2048)) != -1)
            fos.write(bis, 0, len);

        fis.close();
        fos.close();
    }

    public static void moveFile(String sourceFilePath, String destFilePath) throws IOException {
        copyFile(sourceFilePath, destFilePath);
        delete(sourceFilePath);
    }

    public static void copyDir(String sourceDirPath, String destDirPath) throws IOException {
        File srcDir = new File(sourceDirPath);
        File destDir = new File(destDirPath);

        if (!srcDir.exists())
            throw new FileNotFoundException("File or directory at \"" + sourceDirPath + "\" not found");

        if (!srcDir.canRead())
            throw new AccessDeniedException("The current user cannot read \"" + sourceDirPath + "\" (Permission denied)");

        if (srcDir.isFile()) {
            copyFile(sourceDirPath, destDirPath + File.separator + srcDir.getName());
            return;
        }

        if (!destDir.exists())
            createDirectory(destDirPath);

        if (!destDir.canWrite())
            throw new AccessDeniedException("The current user cannot copy contents from \"" + sourceDirPath + "\" to \"" + destDirPath + "\" (Permission denied)");

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
