package com.kaoqibutaitou.bit.tools;

import java.io.File;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 列举指定目录下面的文件的类型
 * @author Yun
 * @version 1.0.
 */
public class ListFileTypesApp {
    private String rootPath;
    private Map<String,List<String>> typeFileList;
    private Pattern pattern;

    public ListFileTypesApp(String rootPath) {
        this.rootPath = rootPath;
        this.typeFileList = new HashMap<>();
        this.pattern = Pattern.compile(".+\\.(\\w*)");
    }

    private boolean addFile(String type, String filePath){
        List<String> files = typeFileList.get(type);
        if(null == files) {
            files = new ArrayList<>();
            typeFileList.put(type,files);
        }
        return files.add(filePath);
    }

    private void search(File file){
        if(file.isFile()){
            Matcher matcher = this.pattern.matcher(file.getName());
            String type = matcher.find()?matcher.group(1):"unknow";
            addFile(type,file.getAbsolutePath());
        }else if(file.isDirectory()){
            File [] files = file.listFiles();
            if(null!=files) {
                for (File f : files) {
                    search(f);
                }
            }
        }
    }

    public void run(){
        File file = new File(rootPath);
        search(file);
    }

    public Set<String> getFileTypes(){
        return typeFileList.keySet();
    }

    public Collection<List<String>> getFileNames(){
        return typeFileList.values();
    }

    public void display(){
        Set<Map.Entry<String, List<String>>> kvs = typeFileList.entrySet();
        for (Map.Entry<String, List<String>> kv:kvs) {
            String k = kv.getKey();
            System.out.println(k);

            for (String path:kv.getValue()) {
                System.out.print("----");
                System.out.println(path);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, List<String>>> kvs = typeFileList.entrySet();
        for (Map.Entry<String, List<String>> kv:kvs) {
            sb.append(kv.getKey()).append(",");
        }
        return sb.toString();
    }

    public static void testReg(){
        String fileName = "C:\\newRes\\UE4中文打包合集(淘宝店：骄阳教育)\\HTCViveTemplate-UE4-master(淘宝店：骄阳教育)";

        Pattern pattern = Pattern.compile(".+\\.(\\w*)");
        Matcher matcher = pattern.matcher(fileName);
//        System.out.println("match:"+matcher.matches());
//        for (int i=0;i<matcher.groupCount();++i){
//            System.out.println(matcher.group(i));
//        }
        System.out.println("groupCount:"+matcher.groupCount());
        //System.out.println(matcher.group(1));
        while (matcher.find()){
            System.out.println(matcher.group(1));
        }
    }

    public static void main(String[] args) {
        ListFileTypesApp app = new ListFileTypesApp("C:\\newRes\\UE4中文打包合集(淘宝店：骄阳教育)");
        app.run();
        app.display();
        System.out.println(app);
    }
}

