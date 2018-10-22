package com.cbre.tsandford.cbreinspector.misc;

public class PromptRunnable implements Runnable{

    private String PromptResultData;

    public void SetValue(String value){
        this.PromptResultData = value;
    }

    public String GetValue(){
        return this.PromptResultData;
    }

    @Override
    public void run() {

    }

}
