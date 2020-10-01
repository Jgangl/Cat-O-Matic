package com.jgangl.cat_o_matic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity{

    Button manualMealButton;
    Switch enableAllMeals;

    ProgressBar foodLevelProgBar;

    // Specific Time
    LocalTime newtime = LocalTime.of(7, 20, 45, 0);
    //System.out.println(time2);

    // Specific Time
    //LocalTime time3 = LocalTime.parse("12:32:22", DateTimeFormatter.ISO_TIME);
    //System.out.println(time3);

    //Meal mealOne = new Meal();
    //Meal mealTwo = new Meal();
    //Meal mealThree = new Meal();
    //Meal mealFour = new Meal();
    //Meal mealFive = new Meal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(newtime);

        foodLevelProgBar = findViewById(R.id.foodLevel_ProgressBar);
        manualMealButton = findViewById(R.id.ManualMeal_Button_Input);
        enableAllMeals = findViewById(R.id.AllMeals_Switch_Input);

        final EditText mealOneTime = findViewById(R.id.MealOne_Time_Input);
        final EditText mealTwoTime = findViewById(R.id.MealTwo_Time_Input);
        final EditText mealThreeTime = findViewById(R.id.MealThree_Time_Input);
        final EditText mealFourTime = findViewById(R.id.MealFour_Time_Input);
        final EditText mealFiveTime = findViewById(R.id.MealFive_Time_Input);

        manualMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerManualFeed();
            }
        });

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
    }



    private void setFoodLevelProgBar(){
        int newProgress = 0;
        foodLevelProgBar.setProgress(newProgress);
    }

    private void triggerManualFeed(){
        //Manual Feed Triggered
        sendTestRequest();
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

    private void sendTestRequest(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.1.198/";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        foodLevelProgBar.setProgress(75);
                        Log.println(Log.DEBUG,"NONE","Worked!!");
                        // Display the first 500 characters of the response string.
                        //textView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                foodLevelProgBar.setProgress(25);

                Log.println(Log.DEBUG,"NONE",error.getMessage());
                //.setText("That didn't work!");
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("Param1","This Is Param1");
                params.put("Param2","This Is Param2");
                return params;
        }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
