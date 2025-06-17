package com.danghuuthinh.lustre.models;

public class NotificationItem {
    private boolean isSection;
    private String sectionTitle;
    private String title;
    private String message;
    private String time;
    private int iconResId;

    public NotificationItem(String sectionTitle) {
        this.isSection = true;
        this.sectionTitle = sectionTitle;
    }

    public NotificationItem(String title, String message, String time, int iconResId) {
        this.isSection = false;
        this.title = title;
        this.message = message;
        this.time = time;
        this.iconResId = iconResId;
    }

    public boolean isSection() {
        return isSection;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public int getIconResId() {
        return iconResId;
    }
}
