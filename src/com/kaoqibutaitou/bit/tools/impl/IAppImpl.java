package com.kaoqibutaitou.bit.tools.impl;

import com.kaoqibutaitou.bit.tools.inter.IApp;

/**
 * Created by Yun on 2017/1/9.
 */
public abstract class IAppImpl<Result> implements IApp{
    protected volatile AppState state;
    protected volatile Result result;

    public IAppImpl() {
        this.state = AppState.NoError;
        this.result = null;
    }

    public IAppImpl(String [] args) {
        this();
        if(!initParams(args)) {
            state = AppState.InitParamError;
            help();
        }
    }

    @Override
    public AppState getState() {
        return state;
    }

    @Override
    public AppState run() {
        return this.state;
    }

    @Override
    public Result getResult() {
        if(AppState.NoError != this.state) {
            return result;
        }else{
            return null;
        }
    }

    @Override
    public void display() {
        Result r = getResult();
        if(state != AppState.NoError){
            System.out.println("Error: "+state.getStateInfo());
        }else{
            if(null != r) {
                System.out.println("Result: " + result);
            }
        }
    }

    @Override
    public void help() {
        if(state!=AppState.NoError){
            System.out.println("Error:" + state.getStateInfo());
        }
        System.out.println(getExecuteCmdString() + "\t" + getIntroduce() + "\n");
    }

    @Override
    public String getExecuteCmdString() {
        return "usage : java -jar Tools.jar "+getClass().getSimpleName();
    }
}
