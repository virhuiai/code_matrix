package com.jgoodies.dialogs.core.style;

/**
 * 对话框样式接口
 */
public interface DialogStyle {
    String getTitle();
    void setTitle(String title);
    boolean isModal();
    void setModal(boolean modal);
    int getWidth();
    void setWidth(int width);
    int getHeight();
    void setHeight(int height);
}