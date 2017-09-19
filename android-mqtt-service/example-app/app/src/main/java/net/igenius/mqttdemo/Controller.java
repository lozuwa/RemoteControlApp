package net.igenius.mqttdemo;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import net.igenius.mqttservice.MQTTServiceCommand;

import java.io.UnsupportedEncodingException;

public class Controller extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{

    /** Variables and instances */
    /** UI Elements */
    public ImageButton right;
    public ImageButton left;
    public ImageButton up;
    public ImageButton down;
    public ImageButton zUp;
    public ImageButton zDown;
    public ImageButton picButton;
    public ImageButton picButtonDefocused;
    public Switch switchLed;
    public Spinner parasiteSpinner;

    /** Constant strings */
    public static final String MICROSCOPE_TOPIC = "/microscope";
    public static final String MOVEFIELDX_TOPIC = "/movefieldy";
    public static final String MOVEFIELDY_TOPIC = "/movefieldx";
    public static final String Z_UP_TOPIC = "/zu";
    public static final String Z_DOWN_TOPIC = "/zd";
    public static final String LED_TOPIC = "/led";

    public static String CHOSEN_PARASITE = "";

    /** Constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Content */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /** UI Elements */
        right = (ImageButton) findViewById(R.id.right);
        left = (ImageButton) findViewById(R.id.left);
        down = (ImageButton) findViewById(R.id.down);
        up = (ImageButton) findViewById(R.id.up);
        zUp = (ImageButton) findViewById(R.id.zUp);
        zDown = (ImageButton) findViewById(R.id.zDown);
        picButton = (ImageButton) findViewById(R.id.picButton);
        picButtonDefocused = (ImageButton) findViewById(R.id.picButtonDefocused);
        switchLed = (Switch) findViewById(R.id.switchLed);
        parasiteSpinner = (Spinner) findViewById(R.id.spinnerParasite);
        parasiteSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                                                                            R.array.parasite_list,
                                                                            android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parasiteSpinner.setAdapter(adapter);

        /** UI Callbacks */
        /*right.setOnTouchListener(new OnSwipeTouchListener(Controller.this) {
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
        });*/

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage(MOVEFIELDX_TOPIC, "0");
                }
                else {
                }
                return true;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage(MOVEFIELDX_TOPIC, "1");
                }
                else {
                }
                return true;
            }
        });

        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage(MOVEFIELDY_TOPIC, "1");
                }
                else {
                }
                return true;
            }
        });

        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage(MOVEFIELDY_TOPIC, "0");
                }
                else {
                }
                return true;
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

        picButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String payload = "pic;" + CHOSEN_PARASITE;
                    publishMessage(MICROSCOPE_TOPIC, payload);
                }
                else{

                }
                return true;
            }
        });

        picButtonDefocused.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String payload = "picDefocused;" + CHOSEN_PARASITE;
                    publishMessage(MICROSCOPE_TOPIC, payload);
                }
                else {

                }
                return true;
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        Spinner spinner = (Spinner) adapterView;
        if(spinner.getId() == R.id.spinnerParasite) {
            CHOSEN_PARASITE = adapterView.getItemAtPosition(position).toString();
            //showToast(CHOSEN_PARASITE);
        }
        else {
            //nothing
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
