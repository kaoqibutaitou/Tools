package com.kaoqibutaitou.bit.tools.impl;

import com.kaoqibutaitou.bit.tools.inter.IApp;
import com.sun.corba.se.spi.orbutil.fsm.Guard;

/**
 * Created by Yun on 2017/1/9.
 */
public abstract class IAppImpl<Result> implements IApp<Result>{
    protected volatile AppState state;
    protected volatile Result result;
    public IAppImpl(String [] args) {
        super();
        this.state = AppState.NoError;
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
        if(AppState.NoError != state) {
            return result;
        }else{
            return null;
        }
    }

    @Override
    public void display() {
        Result result = getResult();
        if(null == result){
            System.out.println("Error: "+state.getStateInfo());
        }else{
            System.out.println("Result: "+result);
        }
    }

    @Override
    public void help() {
        if(state!=AppState.NoError){
            System.out.println("Error:" + state.getStateInfo());
        }
        System.out.println(getExecuteCmdString());
    }

    @Override
    public String getExecuteCmdString() {
        return getClass().getName();
    }
}
