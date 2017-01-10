package com.kaoqibutaitou.bit.tools;

import com.kaoqibutaitou.bit.tools.impl.IAppImpl;
import com.kaoqibutaitou.bit.tools.inter.IApp;

import java.io.*;

/**
 * 删除指定目录下的空的目录或者不包含指定文件类型的目录
 *
 * @author Yun
 * @version 1.0.
 */
public class DeleteDirectoryApp extends IAppImpl<Long> {
    private String rootPath;
    private FileFilter fileFilter;
    private long deleteCnt;
    private BufferedReader br;

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
          .append("\t- fileType: The Specific file type")
          .append("\t").append(super.getExecuteCmdString()).append(" is a tool to Delete the directories that don't contain the specific file types in the specific directory!");
        return sb.toString();
    }

    @Override
    public AppState run() {
        File file = new File(rootPath);
        char c = '0';
        if (file.isDirectory()) {
            File[] subDirectories = file.listFiles();
            try {
                br = new BufferedReader(new InputStreamReader(System.in));
                for (File d : subDirectories) {
                    if (d.isDirectory()) {
                        if (!isNeedDirectory(d)) {
                            System.out.println("[" + (++deleteCnt) + "] delete Directory:" + d.getAbsoluteFile() + "? (y/n)");
                            c = br.readLine().charAt(0);
                            if (c == 'y' || c == 'Y') {
                                if(d.delete()){
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

    public boolean isNeedDirectory(File directory) {
        if (directory.isFile() && fileFilter.accept(directory)) return true;
        else {
            File[] subFiles = directory.listFiles(fileFilter);
            if (null == subFiles || subFiles.length <= 0) return false;
            boolean ret = false;
            for (File f : subFiles) {
                ret &= isNeedDirectory(f);
                if (ret) break;
            }
            return ret;
        }
    }

    public static void main(String[] args) {
        String rootPathString="J:\\deleteFile";
        String fileTypesString="avi,mov,flv,mkv,mp4";
        IApp<Long> app = new CountProjectLineApp(new String[]{rootPathString,fileTypesString});
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
