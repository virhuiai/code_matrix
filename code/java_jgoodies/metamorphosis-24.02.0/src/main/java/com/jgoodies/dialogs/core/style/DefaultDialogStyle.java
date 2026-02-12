package com.jgoodies.dialogs.core.style;

/**
 * 默认对话框样式实现
 */
public class DefaultDialogStyle implements DialogStyle {
    private String title;
    private boolean modal = true;
    private int width = 400;
    private int height = 300;
    
    public DefaultDialogStyle() {
    }
    
    public DefaultDialogStyle(String title) {
        this.title = title;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public boolean isModal() {
        return modal;
    }
    
    @Override
    public void setModal(boolean modal) {
        this.modal = modal;
    }
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public void setWidth(int width) {
        this.width = width;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
    
    @Override
    public void setHeight(int height) {
        this.height = height;
    }
}