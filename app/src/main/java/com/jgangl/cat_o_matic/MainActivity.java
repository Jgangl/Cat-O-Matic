package com.jgangl.cat_o_matic;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
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

    //String espIP = "192.168.1.198";
    //int espPort = 4210;
    //int espReceivePort = 50000;
    FirebaseDatabase database;

    String newTextString;

    Button manualMealButton;
    Switch enableAllMeals;

    Switch[] switches;
    EditText[] timeInputs;
    EditText[] amountInputs;


    ProgressBar foodLevelProgBar;

    TextView textView;

    ArrayList<Meal> meals;

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


        //final EditText mealOneTime = findViewById(R.id.MealOne_Time_Input);
        //final EditText mealTwoTime = findViewById(R.id.MealTwo_Time_Input);
        //final EditText mealThreeTime = findViewById(R.id.MealThree_Time_Input);
        //final EditText mealFourTime = findViewById(R.id.MealFour_Time_Input);
        //final EditText mealFiveTime = findViewById(R.id.MealFive_Time_Input);

        manualMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerManualFeed();
            }
        });

        /*
        mealOneTime.setInputType(InputType.TYPE_NULL);
        mealOneTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(mealOneTime);
            }
        });
        mealOneTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                showTimeDialog(mealOneTime);
            }
            }
        });

        mealTwoTime.setInputType(InputType.TYPE_NULL);
        mealTwoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(mealTwoTime);
            }
        });
        mealTwoTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showTimeDialog(mealTwoTime);
                }
            }
        });

        mealThreeTime.setInputType(InputType.TYPE_NULL);
        mealThreeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(mealThreeTime);
            }
        });
        mealThreeTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showTimeDialog(mealThreeTime);
                }
            }
        });

        mealFourTime.setInputType(InputType.TYPE_NULL);
        mealFourTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(mealFourTime);
            }
        });
        mealFourTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showTimeDialog(mealFourTime);
                }
            }
        });

        mealFiveTime.setInputType(InputType.TYPE_NULL);
        mealFiveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(mealFiveTime);
            }
        });
        mealFiveTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showTimeDialog(mealFiveTime);
                }
            }
        });
        */
        LoadMealsFromDatabase();
    }
    /*
    public void mealTimeClicked(View view){
        switch (view.getId()) {
            case R.id.Meal1_Time_Input:
                // Do something
                updateUI("1");
                break;
            case R.id.Meal2_Time_Input:
                // Do something
                updateUI("2");
                break;
            case R.id.Meal3_Time_Input:
                // Do something
                break;
            case R.id.Meal4_Time_Input:
                // Do something
                break;
            case R.id.Meal5_Time_Input:
                // Do something
                break;
        }
    }

     */



    private void setFoodLevelProgBar(){
        int newProgress = 0;
        foodLevelProgBar.setProgress(newProgress);
    }

    private void triggerManualFeed(){
        //Manual Feed Triggered


        //Meal meal = new Meal(1, 7, 30, true);
        //textView.setText(meal.getTime().toString());
        //updateDatabase("Meals/Meal1/Amount", 5);
    }



    private boolean updateMealAmount(Meal meal){
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
                    //updateUI(Integer.toString(i));

                    if(enabled){
                        switches[i].setChecked(true);
                    }

                    timeInputs[i].setText(time);
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
