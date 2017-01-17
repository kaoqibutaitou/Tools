package com.kaoqibutaitou.bit.tools;

import com.kaoqibutaitou.bit.tools.impl.IAppImpl;
import com.kaoqibutaitou.bit.tools.inter.IApp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 删除指定目录下的空的目录或者不包含指定文件类型的目录
 */
public class DeleteDirectoryApp extends IAppImpl<Long> {
    private String rootPath;
    private FileFilter fileFilter;
    private long deleteCnt;
    private BufferedReader br;

    public DeleteDirectoryApp() {
        super();
    }

    public DeleteDirectoryApp(String[] args) {
        super(args);
        this.result = new Long(0);
    }

    @Override
    public boolean initParams(String[] args) {
        if (args.length>=1){
            this.rootPath = args[0];
        }else{
            this.state = AppState.InitParamError.setStateInfo("Root path have not been specificed!");
            return false;
        }

        if (args.length>=2){
            this.fileFilter = new DeleteDirectoryFileFilter(args[1]);
        }else{
            this.state = AppState.InitParamError.setStateInfo("File type have not been specificed!");
            return  false;
        }
        return true;
    }

    @Override
    public String getExecuteCmdString() {
        StringBuilder sb = new StringBuilder(super.getExecuteCmdString());
        sb.append(" rootPath fileType\n")
          .append("\t- rootPath: Directory to search!\n")
          .append("\t- fileType: The Specific file type\n");
        return sb.toString();
    }

    @Override
    public String getIntroduce() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" is a tool to Delete the directories that don't contain the specific file types in the specific directory!");
        return sb.toString();
    }

    private boolean deleteDirectory(File directory){
        if(directory.isFile()){
            return directory.delete();
        }else{
            boolean ret = true;
            File [] subFiles = directory.listFiles();
            for (File file:subFiles) {
                ret &= deleteDirectory(file);
            }
            ret &= directory.delete();
            return ret;
        }
    }

    @Override
    public AppState run() {
        File file = new File(rootPath);
        char c = '0';
        if (file.isDirectory()) {
            File[] subDirectories = file.listFiles();
            try {
                br = new BufferedReader(new InputStreamReader(System.in));
                DirectoryInfo directoryInfo = new DirectoryInfo();
                for (File d : subDirectories) {
                    if (d.isDirectory()) {
                        if (!isNeedDirectory(d)) {
                            directoryInfo.setRootPath(d.getAbsolutePath());
                            directoryInfo.subFiles.clear();

                            searchFiles(d,directoryInfo);

                            System.out.println();
                            System.out.println(directoryInfo);

                            System.out.println("[" + (++deleteCnt) + "] delete Directory:" + d.getAbsoluteFile() + "? (y/n)");

                            c = br.readLine().charAt(0);
                            if (c == 'y' || c == 'Y') {
                                if(deleteDirectory(d)){
                                    ++this.result;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                this.state = AppState.RuntimeError.setStateInfo("Delete file error!");
                e.printStackTrace();
            } finally {
                if (null != br) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        this.state = AppState.RuntimeError.setStateInfo("Close Input Stream error!");
                        e.printStackTrace();
                    }
                }
            }
        }
        return this.state;
    }

    private static class DeleteDirectoryFileFilter implements FileFilter {
        private String[] fileTypes;

        public DeleteDirectoryFileFilter(String fileTypeString) {
            this.fileTypes = fileTypeString.toLowerCase().split(",");
        }

        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory() || pathname.isHidden()) return true;
            for (String ft : fileTypes) {
                if (pathname.getName().toLowerCase().endsWith(ft)) return true;
            }
            return false;
        }
    }

    private static class DirectoryInfo{
        private String rootPath;
        private List<String> subFiles;

        public DirectoryInfo() {
            this.subFiles = new ArrayList<>();
        }

        public void setRootPath(String rootPath) {
            this.rootPath = rootPath;
        }

        public String getRootPath() {
            return rootPath;
        }

        public List<String> getSubFiles() {
            return subFiles;
        }

        @Override
        public String toString() {
            return "DirectoryInfo{" +
                    "rootPath='" + rootPath + '\'' +
                    ", subFiles=" + subFiles +
                    '}';
        }
    }

    public void searchFiles(File directory, DirectoryInfo directoryInfo){
        if(directory.isFile()){
            directoryInfo.subFiles.add(directory.getAbsolutePath());
        }else{
            File[] subFiles = directory.listFiles();
            for (File f:subFiles){
                searchFiles(f,directoryInfo);
            }
        }
    }

    public boolean isNeedDirectory(File directory) {
        if (directory.isFile()){
            if(fileFilter.accept(directory)) {
                System.out.println("\tNeedFile:"+directory.getAbsolutePath());
                return true;
            }else{
                return false;
            }
        } else {
            File[] subFiles = directory.listFiles(fileFilter);
            if (null == subFiles || subFiles.length <= 0) return false;
            boolean ret = false;
            for (File f : subFiles) {
                if(isNeedDirectory(f)) {
                    ret = true;
                    break;
                }
            }
            return ret;
        }
    }

    public static void main(String[] args) {
        String rootPathString="J:\\Movie";
        String fileTypesString="avi,mov,flv,mkv,mp4,rmvb";
        IApp app = new DeleteDirectoryApp(new String[]{rootPathString,fileTypesString});
        if(app.getState() != IApp.AppState.NoError) return;
        if(app.run() == IApp.AppState.NoError){
            if(null != app.getResult()) {
                System.out.println("\n\nResult:" + app.getResult());
            }else{
                app.display();
            }
        }else{
            System.out.println("Error:"+app.getState().getStateInfo());
        }
    }
}
