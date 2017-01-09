package com.kaoqibutaitou.bit.tools;

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
 * Created by Yun on 2017/1/6.
 */
public class ListMovieFileTime {
    private Encoder encoder;
    private List<PathInfo> fileInfo;
    private String rootPathName;
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
    public ListMovieFileTime(String rootPathName, String fileType){
        this.rootPathName = rootPathName;
        this.encoder = new Encoder();
        this.fileInfo = new ArrayList<>();
        this.movFileFilter = new MovFileFilter(fileType);
        this.index = new AtomicInteger(1);
    }

    public void run(){
        File rootDirectory = new File(rootPathName);

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
            e.printStackTrace();
        }
    }

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

    public void show(){
        for (PathInfo p:fileInfo){
            System.out.println(p.path+"\t"+p.files.size()+"\t"+p.time);
        }
    }


    public static void main(String[] args) {
        String filePath = "C:\\newRes\\UE4中文打包合集(淘宝店：骄阳教育)";
        ListMovieFileTime lmft = new ListMovieFileTime(filePath,"avi,flv,avi,mp4,mkv");
        lmft.run();
        lmft.show();
    }
}
