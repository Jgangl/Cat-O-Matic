package com.jgangl.cat_o_matic;

import java.time.LocalTime;

public class Meal {

    private LocalTime mealTime;
    private boolean enabled;

    Meal(int hour, int minute, boolean enabled){
        mealTime = LocalTime.of(hour, minute);
        this.enabled = enabled;
    }

    Meal(int hour, int minute){
        mealTime = LocalTime.of(hour, minute);
        this.enabled = false;
    }

    public void Enable(){
        enabled = true;
    }

    public void Disable(){
        enabled = false;
    }

    public LocalTime GetMealTime(){
        return mealTime;
    }

    public void SetMealTime(int newMealHour, int newMealMinute){
        mealTime = LocalTime.of(newMealHour, newMealMinute);
    }

}
