package com.example.wedo.Schedule;

import java.util.ArrayList;

public class TaskModel {
    String title;
    ArrayList<String> subTitle;
    ArrayList<String> booleanValue;
    int percent  = 0;
    int all;
    int part;
    String[] array;
    String[] checkArray;

    public TaskModel() {
        this.title = "업무 설정 안됨";
        subTitle = new ArrayList<String>();
        booleanValue = new ArrayList<String>();
    }

    public TaskModel(String title) {
        this.title = title;
        subTitle = new ArrayList<String>();
        booleanValue = new ArrayList<String>();
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

    public void addBooleanValue(String check) {
        booleanValue.add(check);
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

    public int booleanValueSize() {
        return booleanValue.size();
    }

    public String[] getSubTitleArray() {
        array = new String[subTitle.size()];
        System.out.println("체크박스여부1: " + array.length);
        all  =  array.length;

        for (int i = 0; i < subTitle.size(); i++) {
            array[i] = subTitle.get(i);
        }
        return array;
    }

    public String[] getBooleanValueArray() {
         checkArray = new String[booleanValue.size()];
        System.out.println("체크박스여부2: " + booleanValue.size());

        for (int i = 0; i < booleanValue.size(); i++) {
            checkArray[i] = booleanValue.get(i);
        }
        return checkArray;
    }

    public void print() {
        System.out.println("TASK 제목 : " + title);
        for (int i = 0; i < subTitle.size(); i++) {
            System.out.println(Integer.toString(i + 1) + " 번 째 SUB TASK 제목 : " + subTitle.get(i));
        }
        for (int i = 0; i < booleanValue.size(); i++) {
            System.out.println(Integer.toString(i + 1) + " 번 째 SUB TASK 제목 : " + booleanValue.get(i));
        }
    }
//    public int getBooleanPercent(){
//        for(int i = 0; i <booleanValue.size(); i++){
//            if(booleanValue.get(i).equals("true")){
//                part +=1;
//
//            }
//        }
//
//        System.out.println("파트 222 : "  + all);
//        percent = (int)( (double)part/ (double)all * 100.0 );
//
//        System.out.println("퍼센트 333 : "  + percent);
//
//        return percent;
//    }

}
