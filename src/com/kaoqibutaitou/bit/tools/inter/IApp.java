package com.kaoqibutaitou.bit.tools.inter;

import java.util.Objects;

/**
 * Created by Yun on 2017/1/9.
 */
public interface IApp<Result>{
    boolean initParams(String [] args);
    void help();
    AppState run();
    AppState getState();
    Result getResult();
    void display();
    String getExecuteCmdString();
    String getIntroduce();
    enum AppState{
        InitParamError(-1,"Init Param failed!"),
        NoError(0,"No Error"),
        RuntimeError(1);

        AppState(int stateCode) {
            this.stateCode = stateCode;
        }

        AppState(int stateCode, String stateInfo) {
            this.stateCode = stateCode;
            this.stateInfo = stateInfo;
        }

        private int stateCode;
        private String stateInfo;

        public String getStateInfo() {
            return stateInfo;
        }

        public AppState setStateInfo(String stateInfo) {
            this.stateInfo = stateInfo;
            return this;
        }
    }
}
