package com.jgangl.cat_o_matic;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
    Switch enableAllMeals;
    ProgressBar foodLevelProgBar;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meals = new ArrayList<Meal>();

        database = FirebaseDatabase.getInstance();

        textView = findViewById(R.id.test_view);

        foodLevelProgBar = findViewById(R.id.foodLevel_ProgressBar);
        manualMealButton = findViewById(R.id.ManualMeal_Button_Input);
        enableAllMeals = findViewById(R.id.AllMeals_Switch_Input);


        manualMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerManualFeed();
            }
        });

        switches = new Switch[5];
        for(int i = 0; i < switches.length; i++){
            String switchName = "Meal"+ (i+1) + "_Switch_Input";
            int resId = getResources().getIdentifier(switchName, "id", getPackageName());
            switches[i] = findViewById(resId);
        }

        timeInputs = new EditText[5];
        for(int i = 0; i < timeInputs.length; i++){
            String timeInputName = "Meal"+ (i+1) + "_Time_Input";
            int resId = getResources().getIdentifier(timeInputName, "id", getPackageName());
            timeInputs[i] = findViewById(resId);

            timeInputs[i].setInputType(InputType.TYPE_NULL);
            final int finalI = i;

            timeInputs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeDialog(timeInputs[finalI]);
                }
            });

            timeInputs[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        showTimeDialog(timeInputs[finalI]);
                    }
                }
            });
        }

        amountInputs = new EditText[5];
        for(int i = 0; i < amountInputs.length; i++){
            String amountInputName = "Meal"+ (i+1) + "_Amount_Input";
            int resId = getResources().getIdentifier(amountInputName, "id", getPackageName());
            amountInputs[i] = findViewById(resId);

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

        LoadMealsFromDatabase();
    }

    private void setFoodLevelProgBar(){
        int newProgress = 0;
        foodLevelProgBar.setProgress(newProgress);
    }

    private void triggerManualFeed(){
        //Manual Feed Triggered

        String path = "ManualFeedTrigger/";
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(true);

        //Meal meal = new Meal(1, 7, 30, true);
        //textView.setText(meal.getTime().toString());
        //updateDatabase("Meals/Meal1/Amount", 5);
    }



    private boolean updateMealAmount(Meal meal){
        int mealNum = meal.getNum();

        String path = "Meals/" + mealNum + "/Amount";
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(meal.getAmount());

        return true;
    }

    private boolean updateMealEnabled(Meal meal){
        return true;
    }

    private boolean updateMealTime(Meal meal){

        LocalTime time = meal.getTime();
        int mealNum = meal.getNum();

        String path = "Meals/" + mealNum + "/Time";
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(meal.getTime().toString());

        return true;
    }

    private void showTimeDialog(final EditText editTextToSet){
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

                        editTextToSet.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void updateUI(String str){
        textView.setText(str);
    }

    private void LoadFromDatabase(){
        DatabaseReference myRef = database.getReference("/");

    }

    private void LoadMealsFromDatabase(){
        String path = "Meals/";
        DatabaseReference myRef = database.getReference(path);

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

                    switches[i].setChecked(enabled);
                    timeInputs[i].setText(time);
                    amountInputs[i].setText(Long.toString(amount));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        //myRef.setValue(5);
        //myRef.
    }

}
