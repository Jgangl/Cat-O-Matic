package com.jgangl.cat_o_matic;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.Calendar;

public class MainActivity extends Activity{

    String espIP = "192.168.1.198";
    int espPort = 4210;
    int espReceivePort = 50000;

    int value = 0;

    String newTextString;

    Button manualMealButton;
    Switch enableAllMeals;

    ProgressBar foodLevelProgBar;

    TextView textView;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0){
                updateUI();
            }else{
                //showError();
            }
        }
    };

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

        textView = findViewById(R.id.test_view);
        //ReceiveData(espReceivePort);

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
        //sendTestRequest();

        //String data = "1:0|1800|10,2:1|0800|11,3:0|0400|08,4:0|2000|10,5:50,6:0";
        //String data = "6:10";
        String data = "4:1|1754|8";

        textView.setText("");

        SendData(data, espPort, espIP);

        //UDPSocket sock = new UDPSocket(espIP, espPort);
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
/*
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
*/



    private DatagramSocket UDPSocket;
    private InetAddress address;

    //  Initializes a socket with the parameters retrieved in the graphical interface for sending data
    public void Initialize(InetAddress address) {
        try {
            this.UDPSocket = new DatagramSocket();
            this.address = address;
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    //  Send the data in the socket defined by the InitReseau method
    public void SendInstruction(final byte[] data, final int port) {
        new Thread() {
            @Override
            public void run() {
                try {

                    DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                    UDPSocket.send(packet);

                    final int taille = 1024;
                    final byte[] buffer = new byte[taille];

                    DatagramPacket packetreponse = new DatagramPacket(buffer, buffer.length);

                    UDPSocket.receive(packetreponse); //Seems to error here
                    OnReceiveData(packetreponse);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    //  Sending X times the data
    public void SendData(final String Sdata , final int port, final String address) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Initialize(InetAddress.getByName(address));

                    byte[] data = Sdata.getBytes();
                    SendInstruction(data, port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void OnReceiveData(DatagramPacket packet){
        String str = new String(packet.getData());

        newTextString = str;

        handler.sendEmptyMessage(0);

        Log.d("THREAD", str);

    }

    public void ChangeValue(){
        value = 5;
    }

    public void updateUI(){
        textView.setText(newTextString);
    }


/*
    //  scan the port set
    public void ReceiveData(final int portNum) {
        new Thread() {
            @Override
            public void run() {
                try {

                    final int taille = 1024;
                    final byte[] buffer = new byte[taille];
                    DatagramSocket socketReceive = new DatagramSocket(portNum);
                    while (true) {

                        textView.setText("Recieving");


                        DatagramPacket data = new DatagramPacket(buffer, buffer.length);
                        socketReceive.receive(data);
                        DisplayData(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
*/


/*
    // Modifies the display according to the tram received
    public void DisplayData(DatagramPacket data) {
        textView.setText("Hello");

        if(data.getLength() != 0){
            textView.setText(new String(data.getData()));
            textView.invalidate();
            textView.requestLayout();
        }

        //data.getData().toString()
        //System.out.println(data);
    }
*/

}
