package com.kaoqibutaitou.bit;

import com.kaoqibutaitou.bit.tools.CountProjectLineApp;
import com.kaoqibutaitou.bit.tools.inter.IApp;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static String[] removeFirst(String [] args){
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
        if(args.length < 1) return;
        try {
            Class<?> clazz = Class.forName(args[0]);
            IApp<?> app = (IApp<?>) clazz.getDeclaredConstructor(String[].class).newInstance(removeFirst(args));
            if(app.getState() != IApp.AppState.NoError) return;
            if(app.run() != IApp.AppState.NoError){
                if(null != app.getResult()) {
                    System.out.println("\n\nResult:" + app.getResult());
                }else{
                    app.display();
                }
            }else{
                System.out.println("Error:"+app.getState().getStateInfo());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
