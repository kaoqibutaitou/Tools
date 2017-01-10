package com.kaoqibutaitou.bit.tools;

import com.kaoqibutaitou.bit.tools.impl.IAppImpl;
import com.kaoqibutaitou.bit.tools.inter.IApp;

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
public class ListFileTypesApp extends IAppImpl<String> {
    private String rootPath;
    private Map<String,List<String>> typeFileList;
    private Pattern pattern;

    public ListFileTypesApp(String [] args) {
        super(args);
        this.result = null;
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

    @Override
    public boolean initParams(String[] args) {
        if (args.length>=1){
            this.rootPath = args[0];
        }else{
            this.state = AppState.InitParamError.setStateInfo("Root path have not been specificed!");
            help();
            return false;
        }
        return true;
    }

    @Override
    public AppState run() {
        File file = new File(rootPath);
        search(file);
        return super.run();
    }

    @Override
    public String getResult() {
        if(null != result) return result;

        StringBuilder sb = new StringBuilder();
        Set<String> keys = getFileTypes();
        for (String k:keys) {
            sb.append(k).append(",");
        }
        this.result = sb.toString();
        return super.getResult();
    }

    @Override
    public String getExecuteCmdString() {
        StringBuilder sb = new StringBuilder(super.getExecuteCmdString());
        sb.append(" root path\n")
          .append("\t- root path: Directory to search!\n")
          .append("\t").append(super.getExecuteCmdString()).append(" is a tool to List the all file types in the specific path!");
        return sb.toString();
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
        return getResult();
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
        IApp<String> app = new ListFileTypesApp(new String[]{
                "C:\\newRes\\UE4中文打包合集(淘宝店：骄阳教育)"
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

