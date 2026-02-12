package com.jgoodies.search;

/**
 * 完成项类
 */
public class Completion {
    private final String text;
    private final String description;
    
    public Completion(String text) {
        this(text, null);
    }
    
    public Completion(String text, String description) {
        this.text = text;
        this.description = description;
    }
    
    public String getText() {
        return text;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
    // 添加Builder内部类
    public static class Builder {
        private String text;
        private String description;
        
        public Builder text(String text) {
            this.text = text;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Completion build() {
            return new Completion(text, description);
        }
    }
}