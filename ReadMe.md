---
title: 
tags: 
grammar_cjkRuby: true
---

[toc]

# 简介

这是一个日常实用的小工具集的源代码。

# 工具

Main外程序的入口地址。

其内部采用反射机制来实现扩展。

## DeleteDirectoryApp.java

删除指定目录下的空的目录或者不包含指定文件类型的目录。

## ListFileTypesApp.java

列举指定目录下的文件类型，包括具体的文件路径。

> 此处发现了怪异的行为，准备后期采用线程来查看具体的问题在哪。Thread.currentThread()

## CountProjectLineApp.java

计数项目下的指定类型代码的行数。

## ListMovieFileTimeApp.java

列举指定目录下特定类型视屏文件的累计播放时长。