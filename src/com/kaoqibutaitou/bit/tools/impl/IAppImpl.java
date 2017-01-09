package com.kaoqibutaitou.bit.tools.impl;

import com.kaoqibutaitou.bit.tools.inter.IApp;

/**
 * Created by Yun on 2017/1/9.
 */
public abstract class IAppImpl<Result> implements IApp<Result>{
    private AppState state;
    public IAppImpl(String [] args) {
        super();
        state = AppState.NoError;
        if(!initParams(args)) {
            state = AppState.InitParamError;
        }
    }

    @Override
    public AppState getState() {
        return state;
    }

    @Override
    public void help() {
        if(state!=AppState.NoError){
            System.out.println("Error:" + state.getStateInfo());
        }
        System.out.println(getExecuteCmdString());
    }
}
