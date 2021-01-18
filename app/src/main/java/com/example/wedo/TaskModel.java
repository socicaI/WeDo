package com.example.wedo;

import java.util.ArrayList;

public class TaskModel {
    String title;
    ArrayList<String> subTitle;

    public TaskModel() {
        this.title = "업무 설정 안됨";
        subTitle = new ArrayList<String>();
    }

    public TaskModel(String title) {
        this.title = title;
        subTitle = new ArrayList<String>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void addSubTitle(String sTitle) {
        subTitle.add(sTitle);
    }

    public String removeSubTitle(int index) {
        if (index >= 0 && index < subTitle.size()) {
            return subTitle.remove(index);
        } else {
            return null;
        }
    }

    public String getSubTitle(int index) {
        if (index >= 0 && index < subTitle.size()) {
            return subTitle.get(index);
        } else {
            return null;
        }
    }

    public int subTitleSize() {
        return subTitle.size();
    }

    public String[] getSubTitleArray() {
        String[] array = new String[subTitle.size()];

        for (int i = 0 ; i < subTitle.size() ; i++) {
            array[i] = subTitle.get(i);
        }

        return array;
    }

    public void print() {
        System.out.println("TASK 제목 : " + title);
        for (int i = 0 ; i < subTitle.size() ; i++) {
            System.out.println(Integer.toString(i + 1) + " 번 째 SUB TASK 제목 : " + subTitle.get(i));
        }
    }
}
