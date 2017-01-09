package com.kaoqibutaitou.bit.tools.impl;

import com.kaoqibutaitou.bit.tools.inter.IApp;

/**
 * Created by Yun on 2017/1/9.
 */
public abstract class IAppImpl<Result> implements IApp<Result>{
    @Override
    public boolean initParams(String[] args) {
        return false;
    }

    @Override
    public void help() {

    }

    @Override
    public void run() {

    }

    @Override
    public Result getResult() {
        return null;
    }

    @Override
    public void display() {

    }
}
