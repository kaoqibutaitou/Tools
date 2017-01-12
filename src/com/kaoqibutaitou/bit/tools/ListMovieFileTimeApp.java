package com.kaoqibutaitou.bit.tools;


import com.kaoqibutaitou.bit.tools.impl.IAppImpl;
import com.kaoqibutaitou.bit.tools.inter.IApp;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 采用多线程的方式来列举指定目录下的视频文件的时长
 * 依赖库为jave-1.0.2
 * @see {http://www.sauronsoftware.it/projects/jave/download.php}
 */
public class ListMovieFileTimeApp extends IAppImpl<Void> {
    private Encoder encoder;
    private List<PathInfo> fileInfo;
    private String rootPath;
    private FileFilter movFileFilter;
    private CountDownLatch syn;
    private AtomicInteger index;
    private static class PathInfo{
        public String path;
        public List<String> files;
        public long time;
        public PathInfo(){
            super();
            this.files = new ArrayList<>();
            this.time = 0;
        }
    }
    private class CountThread implements Runnable{
        private PathInfo pathInfo;
        private File directory;

        public CountThread(PathInfo pathInfo, File directory) {
            this.pathInfo = pathInfo;
            this.directory = directory;
        }

        public void count(PathInfo info, File directory){
            File [] files = directory.listFiles(movFileFilter);
            for (File file:files ) {
                if (file.isDirectory()) {
                    count(info,file);
                }else{
                    try {
                        System.out.println("["+index.getAndIncrement()+"]" + file.getAbsoluteFile());
                        MultimediaInfo movInfo = encoder.getInfo(file);
                        info.files.add(file.getAbsolutePath());
                        info.time += movInfo.getDuration()/1000;
                    } catch (EncoderException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void run() {
            count(pathInfo, directory);
            syn.countDown();
        }
    }
    private static class MovFileFilter implements FileFilter{
        private String [] fileTypes;
        public MovFileFilter(String fileType) {
            this.fileTypes = fileType.toLowerCase().split(",");
        }

        @Override
        public boolean accept(File pathname) {
            if(pathname.isDirectory()) return true;
            for (String fileType:fileTypes) {
                if(pathname.isFile() && pathname.getName().toLowerCase().endsWith(fileType)) return true;
            }
            return false;
        }
    }

    public ListMovieFileTimeApp(String [] args){
        super(args);
        this.encoder = new Encoder();
        this.fileInfo = new ArrayList<>();
        this.index = new AtomicInteger(1);
    }

    public ListMovieFileTimeApp() {
        super();
        this.encoder = new Encoder();
        this.fileInfo = new ArrayList<>();
        this.index = new AtomicInteger(1);
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
            this.movFileFilter = new MovFileFilter(args[1]);
        }else{
            this.state = AppState.InitParamError.setStateInfo("File type have not been specificed!");
            return  false;
        }
        return true;
    }

    @Override
    public AppState run() {
        File rootDirectory = new File(rootPath);

        File [] directories = rootDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        syn = new CountDownLatch(directories.length);

        for (File directory : directories){
            PathInfo info = new PathInfo();
            info.path = directory.getAbsolutePath();
            this.fileInfo.add(info);
            new Thread(new CountThread(info,directory)).start();
        }

        try {
            syn.await();
        } catch (InterruptedException e) {
            this.state = AppState.RuntimeError.setStateInfo("Syn Thread is interruputed!");
            e.printStackTrace();
        }
        return super.run();
    }

    @Override
    public String getExecuteCmdString() {
        StringBuilder sb = new StringBuilder(super.getExecuteCmdString());
        sb.append(" root path\n")
                .append("\t- rootPath: Directory to search!\n")
                .append("\t- fileType: The movie file type to search!\n");
        return sb.toString();
    }

    @Override
    public String getIntroduce() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" is a tool to List the all movie file play time information in the subPaths of specific path!");
        return sb.toString();
    }

    //    单线程统计
//    public void count(PathInfo info, File directory){
//        File [] files = directory.listFiles(movFileFilter);
//        for (File file:files ) {
//            if (file.isDirectory()) {
//                count(info,file);
//            }else{
//                try {
//                    System.out.println(file.getAbsoluteFile());
//                    MultimediaInfo movInfo = encoder.getInfo(file);
//                    info.files.add(file.getAbsolutePath());
//                    info.time += movInfo.getDuration()/1000;
//                } catch (EncoderException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


    @Override
    public void display() {
        System.out.println("Result:");
        for (PathInfo p:fileInfo){
            System.out.println(p.path+"\t"+p.files.size()+"\t"+p.time);
        }
    }


    public static void main(String[] args) {
        String filePath = "C:\\newRes\\UE4中文打包合集(淘宝店：骄阳教育)";
        IApp app = new ListMovieFileTimeApp(new String[]{
                filePath,"avi,flv,avi,mp4,mkv"
        });
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
