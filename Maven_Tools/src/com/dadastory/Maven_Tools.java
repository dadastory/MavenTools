package com.dadastory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author:chenda
 * @Date:2021/7/20 15:07
 */
public class Maven_Tools {
    private static final Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        while (true) {
            System.out.println("欢迎使用Idea清理小工具");
            System.out.println("###################################");
            System.out.println("-----------------------------------");
            System.out.println("1.清除Maven仓库未成功下载的POM依赖文件  |");
            System.out.println("2.清除Idea项目文件下所有的编译缓存      |");
            System.out.println("3.退出                              |");
            System.out.println("-----------------------------------");
            System.out.println("请选择你需要的功能项：");
            String fuc = sc.nextLine();
            if (!isDigit2(fuc)) System.out.println("您输入的格式有误，请重新输入！");
            int num = Integer.parseInt(fuc);
            switch (num) {
                case 1:
                    cleanMavenPom();
                    break;
                case 2:
                    cleanMyProject();
                    break;
                case 3:
                    System.exit(0);
            }
        }
    }

    //清除项目下面的所有target编译文件
    private static void cleanMyProject() {
        System.out.println("请输入您的项目工作空间的绝对路径路径:（例如：D:\\Java_workspace）");
        String path = sc.nextLine();
        File destDir = new File(path);
        if (!destDir.exists()) {
            System.out.println("您输入的文件夹不存在，请重新输入！");
            return;
        }
        List<File> list = new ArrayList<>();
        findAllTarget(destDir, list);
        System.out.println("从您的项目中找到了以下的target文件夹：");
        list.forEach(System.out::println);
        System.out.println("您确认是否删除上述文件？（y/n）");
        String confirm = sc.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            for (File file : list) {
                deleteFile(file);
                System.out.println(file.toString() + "清理成功！");
            }
        }

    }


    //清除所有pom文件
    private static void cleanMavenPom() {
        System.out.println("请输入您的Maven仓库文件夹的绝对路径路径:（例如：D:\\Maven\\Repository）");
        String path = sc.nextLine();
        File destDir = new File(path);
        if (!destDir.exists()) {
            System.out.println("您输入的文件夹不存在，请重新输入！");
            return;
        }
        List<File> list = new ArrayList<>();
        findAllPom(destDir, list);
        System.out.println("从仓库中找到了以下文件：");
        list.forEach(System.out::println);
        System.out.println("您确认是否删除文件？（y/n）");
        String confirm = sc.nextLine();
        if ("y".equalsIgnoreCase(confirm)) {
            for (File file : list) {
                File parentFile = file.getParentFile();
                file.delete();
                parentFile.delete();
                System.out.println(file.toString() + "清理成功！");
            }
        }
    }


    //查询所有pom文件,并添加到集合
    private static void findAllPom(File destDir, List<File> list) {
        File[] files = destDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) findAllPom(file, list);
                if (file.isFile()) {
                    if (file.toString().contains("lastUpdated")) {
                        list.add(file);
                    }
                }
            }
        }
    }

    //查询所有target文件夹,并添加到集合
    private static void findAllTarget(File destDir, List<File> list) {
        if (destDir.isFile()) return;
        if (destDir.isDirectory() && destDir.toString().endsWith("target")) list.add(destDir);
        File[] files = destDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) findAllTarget(file, list);
            }
        }
    }


    // 判断一个字符串是否都为数字
    private static boolean isDigit2(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) strNum);
        return matcher.matches();
    }

    //递归删除文件夹
    private static void deleteFile(File desDir) {
        File[] files = desDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) deleteFile(file);
                if (file.isFile()) file.delete();
            }
        }
        desDir.delete();
    }

}
