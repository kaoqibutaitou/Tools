package com.kaoqibutaitou.bit.tools;

import com.kaoqibutaitou.bit.tools.impl.IAppImpl;
import com.kaoqibutaitou.bit.tools.inter.IApp;

import java.io.*;

/**
 * 用于统计项目的代码行数。
 */
public class CountProjectLineApp extends IAppImpl<Long> {
    private String projectDirectoryPathString;
    private String [] fileSuffixs;
    private ICountLine count;
    private long lineNo;
    private FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return isNeedFile(pathname);
        }
    };

    public CountProjectLineApp() {
        super();
        this.result = new Long(0);
        this.lineNo = 1;
        this.count = new CountLine(new FilterMultiLineComment());
    }

    public CountProjectLineApp(String[] args) {
        this(args,new CountLine(new FilterMultiLineComment()));
    }

    public CountProjectLineApp(String [] args, ICountLine count) {
        super(args);
        this.result = new Long(0);
        this.lineNo = 1;
        this.count = count;
    }



    public long count(File file) {
        long cnt = 0;

        if(file.isFile() && isNeedFile(file)) {
            BufferedReader br = null;
            try {
                String line;
                br = new BufferedReader(new FileReader(file));
                System.out.println("/********************"+file.getAbsolutePath()+"*********************/");
                while ((line = br.readLine()) != null) {
                    long c = count.count(line);
                    if(c > 0) System.out.println("["+(lineNo++)+"]"+line);
                    cnt += c;
                }
            } catch (IOException e) {
                this.state = AppState.RuntimeError.setStateInfo("Fail to Open File:"+file.getAbsolutePath());
                e.printStackTrace();
            } finally {
                if (null != br) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        this.state = AppState.RuntimeError.setStateInfo("Fail to Close File:"+file.getAbsolutePath());
                        e.printStackTrace();
                    }
                }
            }
        }else if (file.isDirectory()){
            File [] files = file.listFiles();
            for (File f: files) {
                cnt += count(f);
            }
        }

        return cnt;
    }

    private boolean isNeedFile(File file){
        if(null == fileSuffixs) return true;
        String fileName = file.getName();
        for (String suffix: this.fileSuffixs) {
            if (fileName.toLowerCase().endsWith(suffix)) return true;
        }
        return false;
    }

    @Override
    public boolean initParams(String[] args) {
        if (args.length>=1){
            projectDirectoryPathString = args[0];
        }else{
            projectDirectoryPathString = "";
        }

        if (args.length>=2){
            this.fileSuffixs = args[1].toLowerCase().split(",");
        }
        return true;
    }

    @Override
    public AppState run() {
        File file = new File(projectDirectoryPathString);
        this.result = count(file);
        return this.state;
    }

    @Override
    public String getExecuteCmdString() {
        StringBuilder sb = new StringBuilder(super.getExecuteCmdString());
        sb.append(" [directoryPath | filePath] [fileType]\n")
          .append("\t- ").append("directoryPath | filePath : The directory to search or file for counting the total line.\n")
          .append("\t- ").append("fileType : The file type to filter.\n");

        return sb.toString();
    }

    @Override
    public String getIntroduce() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append(" is used to count the total number of lines for a project or a file.");
        return sb.toString();
    }

    public static void main(String[] args) {
        IApp app = new CountProjectLineApp(new String[]{

        });
        if(app.getState() != IApp.AppState.NoError) return;
        if(app.run() == IApp.AppState.NoError){
            if(null != app.getResult()) {
                System.out.println("\n\nResult:" + app.getResult());
            }else{
                app.display();
            }
        }else{
            System.out.println("Error:"+app.getState().getStateInfo());
        }
    }


    /**
     * 计数接口
     */
    public interface ICountLine {
        long count(String str);
    }

    /**
     * 统计行数
     */
    public static class CountLine implements ICountLine {
        private FilterComment filterComment;

        public CountLine(FilterComment filterComment) {
            this.filterComment = filterComment;
        }

        @Override
        public long count(String str) {
            String l = str.trim();
            if (null!=str&&!l.isEmpty()&&!filterComment.isComment(l)){
                return 1;
            }
            return 0;
        }
    }

    /**
     * 统计单词数
     */
    public static class CountWord extends CountLine{
        private String splitWord;

        public CountWord(String splitWord,FilterComment filterComment) {
            super(filterComment);
            this.splitWord = splitWord;
        }

        public String getSplitWord() {
            return splitWord;
        }

        public void setSplitWord(String splitWord) {
            this.splitWord = splitWord;
        }

        @Override
        public long count(String str) {
            long cnt = 0;
            if (super.count(str) > 0){
                cnt += str.split(splitWord).length;
            }
            return cnt;
        }
    }

    public interface FilterComment{
        boolean isComment(String str);
    }

    public static class FilterSingleLineComment implements FilterComment{

        @Override
        public boolean isComment(String str) {
            return str.trim().startsWith("//");
        }
    }

    /**
     * 多行注释里面可能会有单行注释，单行注释后面也可能有多行注释
     */
    public static class FilterMultiLineComment extends FilterSingleLineComment{
        public boolean startComment;

        public FilterMultiLineComment() {
            this.startComment = false;
        }

        @Override
        public boolean isComment(String str) {
            String l = str.trim();
            if(super.isComment(l)) return true;
            int commentStartIndex = l.indexOf("/*");
            int commentEndIndex = l.indexOf("*/");
            if(commentStartIndex == 0 && commentEndIndex > 0){ //形如/*blabla...*/
                startComment = false;
                return true;
            }else if(startComment && commentStartIndex < 0 && commentEndIndex >= l.length()-3){ //当前为多行注释结束行*/,*/必须在行尾
                startComment = false;
                return true;
            }else if (commentStartIndex == 0){ //当前为多行注释开始行/*,/*必须在行头
                startComment = true;
                return true;
            }
            return startComment;
        }
    }

}
