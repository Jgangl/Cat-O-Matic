package com.jgangl.cat_o_matic;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity{
    FirebaseDatabase database;

    ArrayList<Meal> meals;
    Switch[] switches;
    EditText[] timeInputs;
    EditText[] amountInputs;

    Button manualMealButton;
    Switch disableAllMeals_switch;
    ProgressBar foodLevelProgBar;

    TextView textView;

    int foodLevelPercent = 0;
    boolean disableAllMeals;

    //TODO: Load DisableAllMeals on startup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meals = new ArrayList<Meal>();

        database = FirebaseDatabase.getInstance();

        textView = findViewById(R.id.test_view);

        foodLevelProgBar = findViewById(R.id.foodLevel_ProgressBar);
        manualMealButton = findViewById(R.id.ManualMeal_Button_Input);
        disableAllMeals_switch = findViewById(R.id.AllMeals_Switch_Input);

        manualMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerManualFeed();
            }
        });

        LoadDataFromDatabase();

        switches = new Switch[5];
        timeInputs = new EditText[5];
        amountInputs = new EditText[5];

        disableAllMeals_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UpdateAllMealsEnable(isChecked);
            }
        });

        CreateFoodLevelCallback();
    }

    private void InitializeSwitches(){
        for(int i = 0; i < switches.length; i++){
            String switchName = "Meal"+ (i+1) + "_Switch_Input";
            int resId = getResources().getIdentifier(switchName, "id", getPackageName());
            switches[i] = findViewById(resId);
            Log.d("IVAL", Integer.toString(meals.size()));
            meals.get(i).setEnableSwitch(switches[i]);

            final int finalI = i;
            switches[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    meals.get(finalI).setEnabled(isChecked);

                    updateMealEnabled(meals.get(finalI));
                }
            });
        }
    }

    private void InitializeTimeEditTexts(){
        for(int i = 0; i < timeInputs.length; i++){
            String timeInputName = "Meal"+ (i+1) + "_Time_Input";
            int resId = getResources().getIdentifier(timeInputName, "id", getPackageName());
            timeInputs[i] = findViewById(resId);

            timeInputs[i].setInputType(InputType.TYPE_NULL);

            meals.get(i).setTimeInput(timeInputs[i]);

            final int finalI = i;

            timeInputs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeDialog(meals.get(finalI));
                }
            });

            timeInputs[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        showTimeDialog(meals.get(finalI));
                    }
                }
            });
        }
    }

    private void InitializeAmountEditTexts(){
        for(int i = 0; i < amountInputs.length; i++){
            String amountInputName = "Meal"+ (i+1) + "_Amount_Input";
            int resId = getResources().getIdentifier(amountInputName, "id", getPackageName());
            amountInputs[i] = findViewById(resId);

            meals.get(i).setAmountInput(amountInputs[i]);

            final int finalI = i;

            amountInputs[i].setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        amountInputs[finalI].clearFocus();
                    }
                    return handled;
                }
            });

            amountInputs[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // When focus is lost check that the text field has valid values.

                    if (!hasFocus) {
                        //Need to update database
                        int newAmt = Integer.parseInt(amountInputs[finalI].getText().toString());
                        meals.get(finalI).setAmount(newAmt);
                        updateMealAmount(meals.get(finalI));
                    }
                }
            });
        }
    }

    private void setFoodLevelProgBar(int newProgress){
        if(newProgress > 100) {newProgress = 100;}
        if(newProgress < 0) {newProgress = 0;}

        foodLevelProgBar.setProgress(newProgress);
    }

    private void triggerManualFeed(){
        //Manual Feed Triggered
        String path = "ManualFeedTrigger/";
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(true);
    }

    private void UpdateAllMealsEnable(boolean val){
        String path = "DisableAllMeals/";
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(val);
    }

    private boolean updateMealAmount(Meal meal){
        int mealNum = meal.getNum();

        String path = "Meals/" + mealNum + "/Amount";
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(meal.getAmount());

        return true;
    }

    private boolean updateMealEnabled(Meal meal){
        int mealNum = meal.getNum();

        String path = "Meals/" + mealNum + "/Enabled";
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(meal.getEnabled());

        return true;
    }

    private boolean updateMealTime(Meal meal){
        //updateUI("UPDATING MEAL");
        LocalTime time = meal.getTime();
        int mealNum = meal.getNum();

        String path = "Meals/" + mealNum + "/Time";
        DatabaseReference myRef = database.getReference(path);
        //updateUI(meal.getTime().toString());
        myRef.setValue(meal.getTime().toString());

        return true;
    }

    private boolean updateMealTime(int mealNum, String time){
        String path = "Meals/" + mealNum + "/Time";
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(time);

        return true;
    }

    private void showTimeDialog(final Meal meal){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String newTime = hourOfDay + ":" + minute;
                        updateDebugText(newTime);
                        meal.getTimeInput().setText(newTime);
                        meal.setTime(hourOfDay, minute);
                        updateMealTime(meal);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void updateDebugText(String str){
        textView.setText(str);
    }

    private void CreateFoodLevelCallback(){
        DatabaseReference myRef = database.getReference("/FoodLevelPerc");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //Post post = dataSnapshot.getValue(Post.class);

                long tempFoodLevelPercent = (long) dataSnapshot.getValue();
                foodLevelPercent = (int)tempFoodLevelPercent;
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        myRef.addValueEventListener(postListener);
    }

    private void LoadDataFromDatabase(){
        String path = "Meals/";
        DatabaseReference myRef = database.getReference(path);
        Log.d("LOAD","Load meals");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = (int)dataSnapshot.getChildrenCount();

                long val = (long)dataSnapshot.child("5").child("Amount").getValue();

                //updateUI(Long.toString(val));
                for(int i = 0; i < dataSnapshot.getChildrenCount(); i++){
                    long amount = (long) dataSnapshot.child(Integer.toString(i+1)).child("Amount").getValue();
                    boolean enabled = (boolean) dataSnapshot.child(Integer.toString(i+1)).child("Enabled").getValue();
                    String time = (String) dataSnapshot.child(Integer.toString(i+1)).child("Time").getValue();
                    Meal meal = new Meal(i+1, (int)amount, enabled, time);
                    meals.add(meal);
                    Log.d("MEALADD", "Added Meal");
                }

                InitializeSwitches();
                InitializeTimeEditTexts();
                InitializeAmountEditTexts();
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        myRef = database.getReference("DisableAllMeals/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                disableAllMeals = (boolean)dataSnapshot.getValue();
                updateUI();
                //UpdateUI for allmeals switch
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        myRef = database.getReference("FoodLevelPerc/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long tempFoodLevel = (long)dataSnapshot.getValue();
                foodLevelPercent = (int)tempFoodLevel;
                updateUI();
                //UpdateUI for food Level Percent progress bar
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private void updateUI(){
        //Update Meals
        for(int i = 0; i < meals.size(); i++){
            Meal meal = meals.get(i);

            int amount = meal.getAmount();
            boolean enabled = meal.getEnabled();
            String time = meal.getTime().toString();

            switches[i].setChecked(enabled);
            timeInputs[i].setText(time);
            amountInputs[i].setText(Integer.toString(amount));
        }

        //Update DisableAllMeals switch
        disableAllMeals_switch.setChecked(disableAllMeals);

        //Update FoodLevel Bar
        foodLevelProgBar.setProgress(foodLevelPercent);
    }

}
