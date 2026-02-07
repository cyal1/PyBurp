package io.github.cyal1.pyburp.plugin;

import burp.api.montoya.core.Registration;
import io.github.cyal1.pyburp.PyBurpTab;

import java.util.ArrayList;

/**
 * 插件管理器
 * 负责插件的生命周期管理和状态跟踪
 */
public class PluginManager {
    private final PyBurpTab tab;
    private final ArrayList<Registration> registeredPlugins;
    
    public PluginManager(PyBurpTab tab) {
        this.tab = tab;
        this.registeredPlugins = new ArrayList<>();
    }
    
    /**
     * 添加已注册的插件
     */
    public void addPlugin(Registration plugin) {
        registeredPlugins.add(plugin);
    }
    
    /**
     * 注销所有已注册的插件
     */
    public void deregisterAllPlugins() {
        for(Registration plugin : registeredPlugins){
            try {
                plugin.deregister();
            } catch (Exception e) {
                // 记录注销失败，但继续处理其他插件
                System.err.println("Failed to deregister plugin: " + e.getMessage());
            }
        }
        registeredPlugins.clear();
    }
    
    /**
     * 获取已注册插件数量
     */
    public int getRegisteredPluginCount() {
        return registeredPlugins.size();
    }
    
    /**
     * 获取所有已注册的插件
     */
    public ArrayList<Registration> getRegisteredPlugins() {
        return new ArrayList<>(registeredPlugins);
    }
    
    /**
     * 清空插件列表（不执行注销）
     */
    public void clearPlugins() {
        registeredPlugins.clear();
    }
}