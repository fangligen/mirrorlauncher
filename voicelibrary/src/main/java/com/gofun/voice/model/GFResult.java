package com.gofun.voice.model;
public class GFResult {
    private String data;
    private int ic;
    private String semantic;
    private String text;
    private String answer;
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public int getIc() {
        return ic;
    }
    public void setIc(int ic) {
        this.ic = ic;
    }
    public String getSemantic() {
        return semantic;
    }
    public void setSemantic(String semantic) {
        this.semantic = semantic;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
