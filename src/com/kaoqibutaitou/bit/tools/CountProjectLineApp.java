package com.kaoqibutaitou.bit.tools;

import java.io.*;

/**
 * Created by Yun on 2016/12/22.
 */
public class CountProjectLineApp {
    private String projectDirectoryPathString;
    private String [] fileSuffixs;
    private Count count;
    private long lineNo;
    private FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return isNeedFile(pathname);
        }
    };

    public CountProjectLineApp(String [] args, Count count) {
        if (args.length>=1){
            projectDirectoryPathString = args[0];
        }else{
            projectDirectoryPathString = "";
        }

        if (args.length>=2){
            this.fileSuffixs = args[1].toLowerCase().split(",");
        }
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
                e.printStackTrace();
            } finally {
                if (null != br) {
                    try {
                        br.close();
                    } catch (IOException e) {
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

    public long run(){
        File file = new File(projectDirectoryPathString);
        return count(file);
    }


    public static void main(String[] args) {
        System.out.println(new CountProjectLineApp(new String[]{
                "K:\\Lab\\CountLine\\test.txt"
        },new CountLine(new FilterMultiLineComment())).run());
    }


    /**
     * 计数接口
     */
    public interface Count{
        long count(String str);
    }

    /**
     * 统计行数
     */
    public static class CountLine implements Count{
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
