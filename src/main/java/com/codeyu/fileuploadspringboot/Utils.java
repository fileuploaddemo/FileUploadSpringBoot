package com.codeyu.fileuploadspringboot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.codeyu.fileuploadspringboot.domain.FileInfo;

public class Utils {
    public static String get32UUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

    public static final void traverseFileList(String strPath, List<FileInfo> filelist) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    traverseFileList(files[i].getAbsolutePath(), filelist); // 获取文件绝对路径
                } else {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setId(fileName.substring(0, files[i].getName().indexOf("_")));
                    fileInfo.setName(fileName);
                    fileInfo.setSize(files[i].length());
                    fileInfo.setPath("");
                    filelist.add(fileInfo);
                }
            }

        }
    }

    public static final boolean deleteFile(String strPath, String fileName) {
        File file = findIt(strPath, fileName);
        if (file != null) {
            return file.delete();
        }
        return false;
    }

    public static final File findIt(String strPath, String fileName) {
        File rootDir = new File(strPath);
        File[] files = rootDir.listFiles();
        List<File> directories = new ArrayList<File>(files.length);
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            } else if (file.isDirectory()) {
                directories.add(file);
            }
        }
        for (File directory : directories) {
            File file = findIt(directory.getAbsolutePath(), fileName);
            if (file != null) {
                return file;
            }
        }
    
        return null;
    }
}