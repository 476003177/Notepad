package com.example.hao.notepad.db;

import cn.bmob.v3.BmobObject;
/**
 * Created by 梁 on 2018/5/5
 */
public class DiaryData extends BmobObject implements Cloneable{
    private String date;//时间
    private String title;//标题
    private String content;//内容
    private String tag;//标签

    //构造函数
    public DiaryData(){}
    public DiaryData(String date,String title,String content,String tag){
        setTitle(title);
        setContent(content);
        setDate(date);
        setTag(tag);
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    protected Object clone() {
        DiaryData data = null;
        try{
            data = (DiaryData)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return data;
    }
}
