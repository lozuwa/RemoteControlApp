package net.igenius.mqttdemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.igenius.mqttservice.MQTTService;
import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceLogger;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.util.Date;

public class Controller extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /*** UI Elements and variables */
    /** Controller*/
    public ImageButton zup;
    public ImageButton zdown;
    public ImageButton XYController;
    /** parasite spinner */
    public Spinner spinnerParasite;
    public String CHOOSE_PARASITE = "";
    /** Led switch */
    public Switch switchLed;
    /** Picture button */
    public ImageButton picButton;
    /** Constant variables */
    public static final String MICROSCOPE_TOPIC = "/microscope";
    public static final String Z_UP_TOPIC = "/zu";
    public static final String Z_DOWN_TOPIC = "/zd";
    public static final String LED_TOPIC = "/led";
    public static final String MOVEFIELDX_TOPIC = "/movefieldx";
    public static final String MOVEFIELDY_TOPIC = "/movefieldy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /*** Receive message */
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        /*** Instance UI */
        /** Controller */
        zup = (ImageButton)findViewById(R.id.zUp);
        zdown = (ImageButton)findViewById(R.id.zDown);
        XYController = (ImageButton) findViewById(R.id.XYController);
        /** Spinner parasite */
        spinnerParasite  = (Spinner) findViewById(R.id.spinnerParasite);
        spinnerParasite.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.parasite_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParasite.setAdapter(adapter);
        /** Led switch */
        switchLed = (Switch) findViewById(R.id.switchLed);
        /** Picture button */
        picButton = (ImageButton) findViewById(R.id.picButton);

        /** Action callbacks */
        picButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String payload = "pic;" + CHOOSE_PARASITE;
                    publish_message(MICROSCOPE_TOPIC, payload);
                }
                else {

                }
                return true;
            }
        });

        zup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String payload = "1";
                    publish_message(Z_UP_TOPIC, payload);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    String payload = "2";
                    publish_message(Z_UP_TOPIC, payload);
                }
                return true;
            }
        });

        zdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String payload = "1";
                    publish_message(Z_DOWN_TOPIC, payload);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    String payload = "2";
                    publish_message(Z_DOWN_TOPIC, payload);
                }
                return true;
            }
        });

        XYController.setOnTouchListener(new OnSwipeTouchListener(Controller.this) {
            public void onSwipeTop() {
                publish_message(MOVEFIELDY_TOPIC, "1");
            }
            public void onSwipeRight() {
                publish_message(MOVEFIELDX_TOPIC, "1");
            }
            public void onSwipeLeft() {
                publish_message(MOVEFIELDX_TOPIC, "0");
            }
            public void onSwipeBottom() {
                publish_message(MOVEFIELDY_TOPIC, "0");
            }
        });

        switchLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    publish_message(LED_TOPIC, "1");
                }
                else {
                    publish_message(LED_TOPIC, "0");
                }
            }
        });
    }

    /*** Callback spinners */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        CHOOSE_PARASITE = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /*** Support methods */
    public void showToast(String message){
        Toast.makeText(Controller.this, message, Toast.LENGTH_SHORT).show();
    }

    public void publish_message(String topic, String message){
        byte[] payload = message.getBytes();
        MQTTServiceCommand.publish(Controller.this, topic, payload);
    }

}
