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

/**
 * This class is mainly associated with a specific Application, "Sketchware".
 * Since I like this class, I decided that I will keep it.
 */
public class FFileUtil {

    public static boolean exists(File file) {
        if (file == null)
            throw new NullPointerException("File Pointer is null");

        return file.exists();
    }

    public static void createFile(File file, boolean createDirs) throws IOException {
        if (file == null)
            throw new NullPointerException("File Pointer is null");

        FileUtil.createFile(file.getAbsolutePath(), createDirs);
    }

    public static void createDirectory(File dir) throws IOException {
        if (dir == null)
            throw new NullPointerException("File Pointer is null");

        FileUtil.createDirectory(dir.getAbsolutePath());
    }

    public static char[] readFileChars(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("File Pointer is null");

        return FileUtil.readFileChars(file.getAbsolutePath());
    }

    public static byte[] readFileBytes(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("File Pointer is null");

        return FileUtil.readFileBytes(file.getAbsolutePath());
    }

    public static String readFileString(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("File Pointer is null");

        return FileUtil.readFileString(file.getAbsolutePath());
    }

    public static void writeFileChars(File file, char[] contents) throws IOException {
        if (file == null)
            throw new NullPointerException("File Pointer is null");

        FileUtil.writeFileChars(file.getAbsolutePath(), contents);
    }

    public static void writeFileBytes(File file, byte[] contents) throws IOException {
        if (file == null)
            throw new NullPointerException("File Pointer is null");

        FileUtil.writeFileBytes(file.getAbsolutePath(), contents);
    }

    public static void writeFileString(File file, String contents) throws IOException {
        if (file == null)
            throw new NullPointerException("File Pointer is null");

        FileUtil.writeFileString(file.getAbsolutePath(), contents);
    }

    public static List<File> listDirectory(File path, boolean nameSort, boolean removePaths) throws IOException {
        if (path == null)
            throw new NullPointerException("File Pointer is null");

        List<String> contents = FileUtil.listDirectory(path.getAbsolutePath(), nameSort, removePaths);
        List<File> files = new ArrayList<>();

        for (String content : contents)
            files.add(new File(content));

        return files;
    }

    public static List<File> specificNameScan(File path, String name, boolean excludeAddingFolders) throws IOException {
        if (path == null)
            throw new NullPointerException("File Pointer is null");

        List<String> contents = FileUtil.specificNameScan(path.getAbsolutePath(), name, excludeAddingFolders);
        List<File> files = new ArrayList<>();

        for (String content : contents)
            files.add(new File(content));

        return files;
    }

    public static List<File> scanWithSpecificEnding(File path, String ending) throws IOException {
        if (path == null)
            throw new NullPointerException("File Pointer is null");

        List<String> contents = FileUtil.scanWithSpecificEnding(path.getAbsolutePath(), ending);
        List<File> files = new ArrayList<>();

        for (String content : contents)
            files.add(new File(content));

        return files;
    }

    public static List<File> scanFiles(File path) throws IOException {
        if (path == null)
            throw new NullPointerException("File Pointer is null");

        List<String> contents = FileUtil.scanFiles(path.getAbsolutePath());
        List<File> files = new ArrayList<>();

        for (String content : contents)
            files.add(new File(content));

        return files;
    }

    public static List<File> scanFolders(File path) throws IOException {
        if (path == null)
            throw new NullPointerException("File Pointer is null");

        List<String> contents = FileUtil.scanFolders(path.getAbsolutePath());
        List<File> files = new ArrayList<>();

        for (String content : contents)
            files.add(new File(content));

        return files;
    }

    public static void delete(File path) throws IOException {
        if (path == null)
            throw new NullPointerException("File Pointer is null");

        FileUtil.delete(path.getAbsolutePath());
    }

    public static void copyFile(File sourceFilePath, File destFilePath) throws IOException {
        if (sourceFilePath == null)
            throw new NullPointerException("File Pointer (source) is null");

        if (destFilePath == null)
            throw new NullPointerException("File Pointer (destination) is null");

        FileUtil.copyFile(sourceFilePath.getAbsolutePath(), destFilePath.getAbsolutePath());
    }

    public static void moveFile(File sourceFilePath, File destFilePath) throws IOException {
        if (sourceFilePath == null)
            throw new NullPointerException("File Pointer (source) is null");

        if (destFilePath == null)
            throw new NullPointerException("File Pointer (destination) is null");

        FileUtil.moveFile(sourceFilePath.getAbsolutePath(), destFilePath.getAbsolutePath());
    }

    public static void copyDir(File sourceDirPath, File destDirPath) throws IOException {
        if (sourceDirPath == null)
            throw new NullPointerException("File Pointer (source) is null");

        if (destDirPath == null)
            throw new NullPointerException("File Pointer (destination) is null");

        FileUtil.copyDir(sourceDirPath.getAbsolutePath(), destDirPath.getAbsolutePath());
    }

    // TODO: create moveDir
    public static void moveDir(File sourceDirPath, File destDirPath) throws IOException {
        copyDir(sourceDirPath, destDirPath);
        delete(sourceDirPath);
    }

}
