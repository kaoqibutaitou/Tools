package com.kaoqibutaitou.bit.tools;

import java.io.*;

/**
 * 删除指定目录下的空的目录或者不包含指定文件类型的目录
 * @author Yun
 * @version 1.0.
 */
public class DeleteDirectoryApp {
    private static class DeleteDirectoryFileFilter implements FileFilter{
        private String [] fileTypes;

        public DeleteDirectoryFileFilter(String fileTypeString) {
            this.fileTypes = fileTypeString.toLowerCase().split(",");
        }

        @Override
        public boolean accept(File pathname) {
            if(pathname.isDirectory()||pathname.isHidden()) return true;
            for (String ft:fileTypes){
                if(pathname.getName().toLowerCase().endsWith(ft)) return true;
            }
            return false;
        }
    }
    public static class DeleteDirectory{
        private String rootPath;
        private FileFilter fileFilter;
        private long deleteCnt;
        private BufferedReader br;

        public DeleteDirectory(String rootPath, String fileTypes) {
            this.rootPath = rootPath;
            this.fileFilter = new DeleteDirectoryFileFilter(fileTypes);
            this.deleteCnt = 0;
        }

        public boolean isNeedDirectory(File directory){
            if(directory.isFile() && fileFilter.accept(directory)) return true;
            else{
                File [] subFiles = directory.listFiles(fileFilter);
                if (null == subFiles || subFiles.length <= 0) return false;
                boolean ret = false;
                for (File f:subFiles){
                    ret &= isNeedDirectory(f);
                    if(ret) break;
                }
                return ret;
            }
        }

        public void run(){
            File file = new File(rootPath);
            char c = '0';
            if(file.isDirectory()){
                File [] subDirectories = file.listFiles();
                try {
                    br = new BufferedReader(new InputStreamReader(System.in));
                    for (File d : subDirectories) {
                        if (d.isDirectory()) {
                            if (!isNeedDirectory(d)) {
                                System.out.println("[" + (++deleteCnt) + "] delete Directory:" + d.getAbsoluteFile() + "? (y/n)");
                                c = br.readLine().charAt(0);
                                if(c == 'y' || c == 'Y') {
                                    d.delete();
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(null != br){
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
//        String rootPathString="J:\\deleteFile";
//        String fileTypesString="avi,mov,flv,mkv,mp4";
//        new DeleteDirectory(rootPathString,)
    }
}
