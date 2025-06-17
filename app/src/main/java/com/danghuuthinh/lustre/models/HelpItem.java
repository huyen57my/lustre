package com.danghuuthinh.lustre.models;

public class HelpItem {
    private String question;
    private String answer;
    private String category;
    private boolean isExpanded = false;

    public HelpItem(String question, String answer, String category) {
        this.question = question;
        this.answer = answer;
        this.category = category;
    }
    public boolean isExpanded() {
        return isExpanded;
    }
    public void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCategory() {
        return category;
    }
}

