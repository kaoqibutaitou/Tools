package com.kaoqibutaitou.bit.tools;

import com.kaoqibutaitou.bit.tools.impl.IAppImpl;
import com.kaoqibutaitou.bit.tools.inter.IApp;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yun on 2017/1/11.
 */
public class man extends IAppImpl<Void> {
    private String pkgFullName;
    private String pkgFullPath;
    private List<ToolBean> appsNames;
    private String toolName;

    private class ToolBean{
        private String name;
        private String intro;
        private String usage;

        public ToolBean(String name, String intro, String usage) {
            this.name = name;
            this.intro = intro;
            this.usage = usage;
        }
    }

    public man() {
        super();
    }

    public man(String[] args) {
        super(args);
        this.pkgFullName = man.class.getPackage().getName();
        this.pkgFullPath = this.pkgFullName.replace('.', '/');
        this.appsNames = new ArrayList<>();
    }

    @Override
    public boolean initParams(String[] args) {
        if(args.length < 1) {
            this.state = AppState.InitParamError.setStateInfo("The class of tool to view is not specified!");
            help();
            return false;
        }else {
            this.toolName = args[0];
        }
        return true;
    }

    @Override
    public String getExecuteCmdString() {
        StringBuilder sb = new StringBuilder(super.getExecuteCmdString());
        sb.append(" toolName\n")
          .append("\t -toolName : The tool to view the usage! Use ? to view the tool that can use!\n");
        return sb.toString();
    }

    @Override
    public String getIntroduce() {
        return "man is a helper to the tools for daily Tools!";
    }

    @Override
    public AppState run() {
        try {
            final Pattern pattern = Pattern.compile("^([\\w]+).class$");

            Enumeration<URL> toolPathUrls = Thread.currentThread().getContextClassLoader().getResources(this.pkgFullPath);
            while (toolPathUrls.hasMoreElements()){
                URL toolUrl = toolPathUrls.nextElement();
                if("file".equalsIgnoreCase(toolUrl.getProtocol())){
                    String toolPathName = URLDecoder.decode(toolUrl.getFile(), "UTF-8");
                    File toolPath = new File(toolPathName);
                    
                    if(toolPath.exists() && toolPath.isDirectory()){
                        File[] toolsClassFiles = toolPath.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".class");
                            }
                        });

                        for (File file:toolsClassFiles) {
                            if(file.exists() && file.isFile()){
                                String fileName = file.getName();
                                String simpleClazzName = null;
                                Matcher matcher = pattern.matcher(fileName);
                                while (matcher.find()){
                                    simpleClazzName = matcher.group(1);
                                    break;
                                }

                                if(null != simpleClazzName){
                                    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(this.pkgFullName + "." + simpleClazzName);
//                                    Class<?> clazz = Class.forName(this.pkgFullName + "." + simpleClazzName);

//                                    Class<?>[] interfaces = app.getClass().getInterfaces();
//                                    for (Class<?> inter:interfaces) {
//                                        if (inter == IApp.class){
//                                            check = true;
//                                            break;
//                                        }
//                                    }
                                    // 判断clazz是否是IApp的子类
                                    boolean check = IApp.class.isAssignableFrom(clazz);
                                    if(check) {
                                        IApp<?> app = (IApp<?>) clazz.newInstance();
                                        this.appsNames.add(new ToolBean(simpleClazzName, app.getIntroduce(), app.getExecuteCmdString()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return super.run();
    }

    @Override
    public void display() {
        if('?' ==  toolName.charAt(0)){
            for (ToolBean tool:appsNames) {
                System.out.print("\n####");
                System.out.println(tool.name);
                System.out.print(tool.usage);
                System.out.println(tool.intro);
            }
        }else{
            for (ToolBean tool:appsNames) {
                if(toolName.equalsIgnoreCase(tool.name)){
                    System.out.print("####");
                    System.out.println(tool.name);
                    System.out.print(tool.usage);
                    System.out.println(tool.intro);
                }
            }
        }
        super.display();
    }
}
