package io.github.cyal1.pyburp.script;

import io.github.cyal1.pyburp.PyBurp;
import io.github.cyal1.pyburp.Tools;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 脚本管理器
 * 负责脚本的加载、保存、执行等管理功能
 */
public class ScriptManager {
    private String defaultScript;
    private String scriptsPath;
    
    public ScriptManager() {
        loadPreferences();
    }
    
    /**
     * 加载用户偏好设置
     */
    private void loadPreferences() {
        scriptsPath = PyBurp.api.persistence().preferences().getString("scriptsPath");
        defaultScript = PyBurp.api.persistence().preferences().getString("defaultScript");
    }
    
    /**
     * 获取默认脚本内容
     */
    public String getDefaultScript() {
        return Objects.requireNonNullElseGet(defaultScript, 
            () -> Tools.readFromInputStream(PyBurp.class.getResourceAsStream("/examples/default.py")));
    }
    
    /**
     * 保存当前脚本为默认脚本
     */
    public void saveAsDefaultScript(String scriptContent) {
        String normalizedContent = scriptContent.replace("\r\n", "\n");
        PyBurp.api.persistence().preferences().setString("defaultScript", normalizedContent);
        this.defaultScript = normalizedContent;
    }
    
    /**
     * 保存脚本到指定路径
     */
    public boolean saveScript(String filePath, String content) {
        try {
            Files.write(Paths.get(filePath), content.getBytes());
            return true;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save script: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * 从文件加载脚本内容
     */
    public String loadScriptFromFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath))).replace("\r\n", "\n");
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load script: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * 获取示例文件列表
     */
    public ArrayList<String> getExampleFiles() {
        ArrayList<String> fileNames = new ArrayList<>();
        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        final JarFile jar;
        try {
            jar = new JarFile(jarFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read jar file", e);
        }
        final java.util.Enumeration<JarEntry> entries = jar.entries();
        while(entries.hasMoreElements()) {
            final String name = entries.nextElement().getName();
            if (name.startsWith("examples/")) {
                fileNames.add(name);
            }
        }
        try {
            jar.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close jar file", e);
        }
        return fileNames;
    }
    
    /**
     * 读取脚本目录内容
     */
    public ArrayList<String> readScriptDirectories() {
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("Last code used");
        
        if(scriptsPath != null && !scriptsPath.isEmpty()){
            File folder = new File(scriptsPath);
            if (folder.isDirectory()) {
                File[] folderList = folder.listFiles();
                if (folderList != null) {
                    Arrays.sort(folderList);
                    for (File file : folderList) {
                        if (!file.getName().startsWith(".")) {
                            fileList.add(folder.getAbsolutePath() + "/" + file.getName());
                        }
                    }
                }
            }
        }
        fileList.add(new JSeparator(JSeparator.HORIZONTAL).toString());
        
        ArrayList<String> files = getExampleFiles();
        java.util.Collections.sort(files);
        fileList.addAll(files);
        
        return fileList;
    }
    
    /**
     * 设置脚本路径
     */
    public void setScriptsPath(String path) {
        this.scriptsPath = path;
        PyBurp.api.persistence().preferences().setString("scriptsPath", path);
    }
    
    /**
     * 获取脚本路径
     */
    public String getScriptsPath() {
        return scriptsPath;
    }
    
    /**
     * 判断是否为示例文件
     */
    public boolean isExampleFile(String fileName) {
        return fileName.startsWith("examples/");
    }
    
    /**
     * 判断是否为Markdown文件
     */
    public boolean isMarkdownFile(String fileName) {
        return fileName.toLowerCase().endsWith(".md");
    }
}