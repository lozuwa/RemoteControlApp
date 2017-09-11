package net.igenius.mqttdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import net.igenius.mqttservice.MQTTServiceCommand;

import java.io.UnsupportedEncodingException;

public class Controller extends AppCompatActivity {

    /** Variables and instances */
    /** UI Elements */
    public ImageButton XYController;
    public ImageButton zUp;
    public ImageButton zDown;
    public Switch switchLed;

    /** Constant strings */
    public static final String MOVEFIELDX_TOPIC = "/movefieldx";
    public static final String MOVEFIELDY_TOPIC = "/movefieldy";
    public static final String Z_UP_TOPIC = "/zUp";
    public static final String Z_DOWN_TOPIC = "/zDown";
    public static final String LED_TOPIC = "/led";

    /** Constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Content */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        /** UI Elements */
        XYController = (ImageButton) findViewById(R.id.XYController);
        zUp = (ImageButton) findViewById(R.id.zUp);
        zDown = (ImageButton) findViewById(R.id.zDown);
        switchLed = (Switch) findViewById(R.id.switchLed);

        /** UI Callbacks */
        XYController.setOnTouchListener(new OnSwipeTouchListener(Controller.this) {
            public void onSwipeTop() {
                publishMessage(MOVEFIELDY_TOPIC, "1");
                //showToast("Top");
            }

            public void onSwipeRight() {
                publishMessage(MOVEFIELDX_TOPIC, "1");
                //showToast("Right");
            }

            public void onSwipeLeft() {
                publishMessage(MOVEFIELDX_TOPIC, "0");
                //showToast("Left");
            }

            public void onSwipeBottom() {
                publishMessage(MOVEFIELDY_TOPIC, "0");
                //showToast("Bottom");
            }
        });

        zUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String payload = "1";
                    publishMessage(Z_UP_TOPIC, payload);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    String payload = "2";
                    publishMessage(Z_UP_TOPIC, payload);
                }
                return true;
            }
        });

        zDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String payload = "1";
                    publishMessage(Z_DOWN_TOPIC, payload);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    String payload = "2";
                    publishMessage(Z_DOWN_TOPIC, payload);
                }
                return true;
            }
        });

        switchLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    publishMessage(LED_TOPIC, "1");
                }
                else {
                    publishMessage(LED_TOPIC, "0");
                }
            }
        });

    }

    /*** Support methods */
    /** Show message */
    public void showToast(String message){
        Toast.makeText(Controller.this, message, Toast.LENGTH_SHORT).show();
    }

    /** Publish a message */
    public void publishMessage(String topic, String message) {
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = message.getBytes("UTF-8");
            MQTTServiceCommand.publish(Controller.this, topic, encodedPayload, 2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
