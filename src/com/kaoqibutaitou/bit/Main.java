package com.kaoqibutaitou.bit;

import com.kaoqibutaitou.bit.tools.CountProjectLineApp;
import com.kaoqibutaitou.bit.tools.ListFileTypesApp;
import com.kaoqibutaitou.bit.tools.impl.IAppImpl;
import com.kaoqibutaitou.bit.tools.inter.IApp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public class Main extends IAppImpl<Void> {
    private String runClazzName;
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
     * com.kaoqibutaitou.bit.Main com.kaoqibutaitou.bit.Tools.XXXX [args]
     * @param args
     */
    public static void main(String[] args) {
        IApp<Void> app = new Main(args);
        if(app.getState() != IApp.AppState.NoError) {
            return;
        }
        if(AppState.NoError != app.run()){
            System.out.println("Error : " + app.getState().getStateInfo());
            app.help();
        };
        testGetPackage();

    }

    public static void testGetPackage(){
        System.out.println(Main.class.getClass().getPackage().getName());
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
            this.runClazzName = args[0];
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
                .append("\t- toolClazz : The clazz implements IApp interface or extends IAppImpl!\n")
                .append("\t- subArgs : The arg list for the Tool app!")
                .append("\t").append(this.getClass().getName()).append(" uniformed entrance to execute the tool that implements IApp interface or extends IAppImpl.");
        return sb.toString();
    }

    @Override
    public AppState run() {
        try {
            Class<?> clazz = Class.forName(this.runClazzName);
            Constructor<?> constructor = clazz.getDeclaredConstructor(String[].class);
            IApp<?> app = (IApp<?>) constructor.newInstance(new Object[]{this.args});
            if(app.getState() != IApp.AppState.NoError) return super.run();
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
