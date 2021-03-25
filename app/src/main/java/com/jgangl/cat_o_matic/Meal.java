package com.jgangl.cat_o_matic;

import android.widget.EditText;
import android.widget.Switch;

import java.time.LocalTime;

public class Meal {

    private int num;
    private LocalTime time;
    private String timeStr;
    private boolean enabled;
    private int amount;

    private Switch enableSwitch;
    private EditText timeInput;
    private EditText amountInput;

    Meal(){
        this.time = LocalTime.of(8, 0);
        this.enabled = true;
        this.num = 1;
        this.amount = 0;
    }

    Meal(int num, int amount, boolean enabled, int hour, int minute){
        if(hour > 23) {hour = 23;}
        if(hour < 0) {hour = 0;}
        if(minute > 59) {minute = 59;}
        if(minute < 0) {minute = 0;}
        this.num = num;
        this.time = LocalTime.of(hour, minute);
        this.amount = amount;
        this.enabled = enabled;

    }

    Meal(int num, int amount, boolean enabled, String timeStr){
        this.num = num;
        this.timeStr = timeStr;
        this.amount = amount;
        this.enabled = enabled;

        String[] timeParts = timeStr.split(":");

        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        this.time = LocalTime.of(hour, minute);
    }
    /*
    Meal(int num, int hour, int minute){
        if(hour > 23) {hour = 23;}
        if(hour < 0) {hour = 0;}
        if(minute > 59) {minute = 59;}
        if(minute < 0) {minute = 0;}
        this.num = num;
        this.amount = amount;
        this.enabled = false;
        this.time = LocalTime.of(hour, minute);
    }
     */



    public void Enable(){
        enabled = true;
    }

    public void Disable(){
        enabled = false;
    }

    public LocalTime getTime(){
        return time;
    }

    public void setTime(int newMealHour, int newMealMinute){
        time = LocalTime.of(newMealHour, newMealMinute);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Switch getEnableSwitch() {
        return enableSwitch;
    }

    public void setEnableSwitch(Switch enableSwitch) {
        this.enableSwitch = enableSwitch;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public EditText getTimeInput() {
        return timeInput;
    }

    public void setTimeInput(EditText timeInput) {
        this.timeInput = timeInput;
    }

    public EditText getAmountInput() {
        return amountInput;
    }

    public void setAmountInput(EditText amountInput) {
        this.amountInput = amountInput;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
