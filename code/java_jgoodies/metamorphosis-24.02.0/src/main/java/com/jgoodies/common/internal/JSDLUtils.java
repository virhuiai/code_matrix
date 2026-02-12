package com.jgoodies.common.internal;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * JSDL工具类
 * 提供标准对话框相关的实用方法
 */
public class JSDLUtils {
    
    /**
     * 显示文件选择对话框
     * @param parent 父窗口
     * @param title 对话框标题
     * @param currentDirectory 当前目录
     * @return 选中的文件，如果取消则返回null
     */
    public static File showOpenFileDialog(Dialog parent, String title, File currentDirectory) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        if (currentDirectory != null) {
            chooser.setCurrentDirectory(currentDirectory);
        }
        
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }
    
    /**
     * 显示文件保存对话框
     * @param parent 父窗口
     * @param title 对话框标题
     * @param currentDirectory 当前目录
     * @return 选中的文件，如果取消则返回null
     */
    public static File showSaveFileDialog(Dialog parent, String title, File currentDirectory) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        if (currentDirectory != null) {
            chooser.setCurrentDirectory(currentDirectory);
        }
        
        int result = chooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }
    
    /**
     * 将Frame转换为Dialog
     * @param frame Frame对象
     * @return Dialog对象
     */
    public static Dialog toDialog(Frame frame) {
        if (frame instanceof Dialog) {
            return (Dialog) frame;
        }
        // 简单实现，实际项目中可能需要更复杂的转换逻辑
        return new Dialog(frame);
    }
}