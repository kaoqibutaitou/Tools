package com.kaoqibutaitou.bit.tools.inter;

/**
 * Created by Yun on 2017/1/9.
 */
public interface IApp<Result> {
    boolean initParams(String [] args);
    void help();
    AppState run();
    AppState getState();
    Result getResult();
    void display();
    String getExecuteCmdString();

    enum AppState{
        InitParamError(-1,"Init Param failed!"),
        NoError(0,"No Error"),
        UnRun(1,"Don't invoke the run method!"),
        RuntimeError(2);

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

        public void setStateInfo(String stateInfo) {
            this.stateInfo = stateInfo;
        }
    }
}
