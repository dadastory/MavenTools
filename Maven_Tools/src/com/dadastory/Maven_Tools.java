package com.dadastory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author:chenda
 * @Date:2021/7/20 15:07
 */
public class Maven_Tools {
    private static final Properties properties = new Properties();

    static {
        try {
            File file = new File("config.properties");
            if (file.exists()) {
                properties.load(new FileInputStream(file));
                System.out.println("您的配置文件如下：");
                String JavaWorkspacePath = properties.getProperty("JavaWorkspacePath");
                String MavenRepositoryPath = properties.getProperty("MavenRepositoryPath");
                if (JavaWorkspacePath != null && MavenRepositoryPath != null) {
                    System.out.println("Java项目工作路径为：" + JavaWorkspacePath);
                    System.out.println("Maven仓库路径地址为：" + MavenRepositoryPath);
                } else {
                    changeProperties();
                }
                System.out.println("");
            } else {
                System.out.println("配置文件不存在！");
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("欢迎使用Idea清理小工具");
            System.out.println("###################################");
            System.out.println("-----------------------------------");
            System.out.println("1.清除Maven仓库未成功下载的POM依赖文件  |");
            System.out.println("2.清除Idea项目文件下所有的编译缓存      |");
            System.out.println("3.修改配置文件路径                    |");
            System.out.println("4.退出                              |");
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
                    try {
                        changeProperties();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.exit(0);
            }
        }
    }

    //修改配置路径
    private static void changeProperties() throws IOException {
        System.out.println("请输入新的项目工作空间的绝对路径路径:（例如：D:\\Java_workspace）");
        String JavaWorkspacePath = sc.nextLine();
        System.out.println("请输入新的Maven仓库文件夹的绝对路径路径:（例如：D:\\Maven\\Repository）");
        String MavenRepositoryPath = sc.nextLine();
        File file1 = new File(JavaWorkspacePath);
        File file2 = new File(MavenRepositoryPath);
        if (!file1.exists()) System.out.println("您输入的" + JavaWorkspacePath + "不存在！");
        if (!file2.exists()) System.out.println("您输入的" + MavenRepositoryPath + "不存在！");
        if (file1.exists() && file2.exists()) {
            properties.setProperty("JavaWorkspacePath", JavaWorkspacePath);
            properties.setProperty("MavenRepositoryPath", MavenRepositoryPath);
            File file = new File("config.properties");
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            properties.store(fileWriter, "");
            System.out.println("配置更新成功！");
            fileWriter.close();
        }
    }


    //清除项目下面的所有target编译文件
    private static void cleanMyProject() {
        String path = properties.getProperty("JavaWorkspacePath");
        File destDir = new File(path);
        if (!destDir.exists()) {
            System.out.println("有效路径不存在，请重新配置！");
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
        String path = properties.getProperty("MavenRepositoryPath");
        File destDir = new File(path);
        if (!destDir.exists()) {
            System.out.println("有效路径不存在，请重新配置路径！");
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
