package com.kaoqibutaitou.bit;

import com.kaoqibutaitou.bit.tools.ListFileTypesApp;
import com.kaoqibutaitou.bit.tools.impl.IAppImpl;
import com.kaoqibutaitou.bit.tools.inter.IApp;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends IAppImpl<Void> {
    private String runClazzName;
    private static final String defaultPkg = Main.class.getPackage().getName()+".tools.";
    private String [] args;
    public Main(String[] args) {
        super(args);
    }

    protected String[] removeFirst(String [] args){
        String [] newStrs = new String[args.length-1];
        System.arraycopy(args,1,newStrs,0,args.length-1);
        return newStrs;
    }

    /**
     * 运行指定的工具，运行命令格式
     * com.kaoqibutaitou.bit.Main XXXX [args]
     * @param args
     */
    public static void main(String[] args) {
        IApp<?> app = new Main(args);
        if(app.getState() != IApp.AppState.NoError) {
            return;
        }
        if(AppState.NoError != app.run()){
            System.out.println("Error : " + app.getState().getStateInfo());
            app.help();
        };

//        testGetPackage();
    }

    public static void testGetPackage(){
//        Thread.currentThread()
//        System.out.println(Main.class.getPackage().getName());
//
//        System.out.println(Thread.currentThread().getClass().getPackage().getName());
//
//        Pattern pattern = Pattern.compile("^([\\w]+).class$");
//        Matcher m = pattern.matcher("CCCC.class");
//        while (m.find()){
//            System.out.println(m.group(1));
//        }
//
//        m.matches();
//
//        Class<?>[] interfaces = Main.class.getInterfaces();
//        for (Class<?> inter:interfaces){
//            System.out.println(inter.getCanonicalName());
//            System.out.println(inter == IApp.class);
//        }
//
//        System.out.println(IApp.class.isAssignableFrom(Main.class));
    }

    public static void testCreateApp(String [] args){
        Constructor<?> [] constructors = ListFileTypesApp.class.getConstructors();

        for (Constructor<?> c:constructors) {
            System.out.println("GenericParameterTypes:");
            Type[] gpt = c.getGenericParameterTypes();
            for (Type t : gpt){
                System.out.println("\t"+t.getClass().getName());
            }
            System.out.println("GenericParameterTypes:");
            Type[] get = c.getGenericExceptionTypes();
            for (Type t : get){
                System.out.println("\t"+t.getClass().getName());
            }
            System.out.println("ParameterTypes:");
            Class<?>[] clazzs = c.getParameterTypes();
            for (Class cc : clazzs) {
                System.out.println("\t"+ get.getClass().getName());
            }

            System.out.println(c);
        }

        try {
            ListFileTypesApp app = (ListFileTypesApp) constructors[0].newInstance(new String[] {
                "E:\\Project\\Tools"
            });
            app.help();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean initParams(String[] args) {
        if(args.length < 1) {
            this.state = AppState.InitParamError.setStateInfo("The class of tool to run is not specified!");
            help();
            return false;
        }else{
            String toolName = args[0];

            Pattern pkgPattern = Pattern.compile("^(\\w+\\.)+(\\w+)$");
            Matcher pkgMatcher = pkgPattern.matcher(toolName);
            boolean match = false;

            while (pkgMatcher.find() && !match){
                match = true;
                toolName = pkgMatcher.group(1);
            }

            if(match){
                System.out.println(toolName);
                this.runClazzName = args[0];
            }else {
                this.runClazzName = defaultPkg + args[0];
            }
        }

        if(args.length >= 2){
            this.args = removeFirst(args);
        }
        return true;
    }

    @Override
    public String getExecuteCmdString() {
        StringBuilder sb = new StringBuilder(super.getExecuteCmdString());
        sb.append(" [toolClazz] [subArgs]\n")
                .append("\t- toolClazz : The clazz implements IApp interface or extends IAppImpl, that must be full name of the tool or the clazz is save in the package named com.kaoqibutaitou.bit.tools.toolClazz!\n")
                .append("\t- subArgs : The arg list for the Tool app!")
                .append("\t").append(this.getClass().getName()).append(" uniformed entrance to execute the tool that implements IApp interface or extends IAppImpl.");
        return sb.toString();
    }

    @Override
    public String getIntroduce() {
        return "Main is the entrance for the daily tools!";
    }

    @Override
    public AppState run() {
        try {
            Class<?> clazz = Class.forName(this.runClazzName);
            Constructor<?> constructor = clazz.getDeclaredConstructor(String[].class);
            IApp app = (IApp) constructor.newInstance(new Object[]{this.args});

//            IApp app = (IApp) clazz.newInstance();
//            app.initParams(this.args);
            if(app.getState() != IApp.AppState.NoError) return super.run();

            //反射会将字符串自动补充为空字符串""
//            app.setResult(null);

            if(app.run() == IApp.AppState.NoError){
//                System.out.println("Main++++:"+app.getResult());
                if(null != app.getResult()) {
                    System.out.println("\n\nResult:" + app.getResult());
                }else{
                    app.display();
                }
            }else{
                System.out.println("Error:"+app.getState().getStateInfo());
            }
        } catch (ClassNotFoundException e) {
            this.state = AppState.RuntimeError.setStateInfo("The class is not found!");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            this.state = AppState.InitParamError.setStateInfo("The class "+this.runClazzName+" doesn't declare the constructor with the String [] arg!");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            this.state = AppState.InitParamError.setStateInfo("The constructor of class "+this.runClazzName+" is not public!");
            e.printStackTrace();
        } catch (InstantiationException e) {
            this.state = AppState.InitParamError.setStateInfo("The instance of class "+this.runClazzName+" is not created!");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            this.state = AppState.InitParamError.setStateInfo("The invoke of instance of class "+this.runClazzName+" failed!");
            e.printStackTrace();
        }
        return super.run();
    }
}
