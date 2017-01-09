package com.kaoqibutaitou.bit.tools.inter;

/**
 * Created by Yun on 2017/1/9.
 */
public interface IApp<Result> {
    boolean initParams(String [] args);
    void help();
    void run();
    Result getResult();
    void display();
}
