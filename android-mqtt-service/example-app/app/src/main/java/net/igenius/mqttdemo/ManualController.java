package net.igenius.mqttdemo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.io.UnsupportedEncodingException;

public class ManualController extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{

    /*** Variables and instances */
    /** UI Elements */
    public ImageButton right;
    public ImageButton left;
    public ImageButton up;
    public ImageButton down;
    public ImageButton zUp;
    public ImageButton zDown;
    public ImageButton picButton;
    public Switch switchLed;
    public Spinner parasiteSpinner;
    public ImageButton autofocusButton;
    public ImageButton homeButton;

    /** Vibrate */
    public Vibrator vibrator;

    /*** Constant strings */
    /** MQTT Topics */
    public static final String X_RIGHT_TOPIC = "/xr";
    public static final String X_LEFT_TOPIC = "/xl";
    public static final String Y_UP_TOPIC = "/yu";
    public static final String Y_DOWN_TOPIC = "/yd";
    public static final String Z_UP_TOPIC = "/zu";
    public static final String Z_DOWN_TOPIC = "/zd";
    public static final String LED_TOPIC = "/led";
    public static final String AUTOFOCUS_APP_TOPIC = "/autofocusApp";
    public static final String CAMERA_APP_TOPIC = "/cameraApp";
    public static final String EXTRA_ACTIONS_TOPIC = "/extra";
    /** Deprecated */
    public static final String MOVEFIELDX_TOPIC = "/movefieldy";
    public static final String MOVEFIELDY_TOPIC = "/movefieldx";
    /** Holder variables */
    public static String CHOSEN_PARASITE = "";

    /*** Constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Content */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_controller);

        /** Define orientation */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /** Keep screen turned on */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /** Vibrator */
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        /** Instantiate UI Elements */
        right = (ImageButton) findViewById(R.id.right);
        left = (ImageButton) findViewById(R.id.left);
        down = (ImageButton) findViewById(R.id.down);
        up = (ImageButton) findViewById(R.id.up);
        zUp = (ImageButton) findViewById(R.id.zUp);
        zDown = (ImageButton) findViewById(R.id.zDown);
        picButton = (ImageButton) findViewById(R.id.picButton);
        autofocusButton = (ImageButton) findViewById(R.id.autofocusButton);
        homeButton = (ImageButton) findViewById(R.id.homeButton);
        switchLed = (Switch) findViewById(R.id.switchLed);
        parasiteSpinner = (Spinner) findViewById(R.id.spinnerParasite);
        parasiteSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.parasite_list,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parasiteSpinner.setAdapter(adapter);

        /** Initial states */
        switchLed.setChecked(true);

        /** Backgrounds */
        right.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        left.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        down.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        up.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        zDown.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        zUp.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        picButton.setBackground(getResources().getDrawable(R.drawable.camera));
        autofocusButton.setBackground(getResources().getDrawable(R.drawable.camera));
        homeButton.setBackground(getResources().getDrawable(R.drawable.camera));

        /** UI Callbacks */
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage(X_LEFT_TOPIC, "1");
                    left.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
                }
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                    publishMessage(X_LEFT_TOPIC, "0");
                    left.setBackground(getResources().getDrawable(R.drawable.curvebutton));
                }
                else{

                }
                return true;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage(X_RIGHT_TOPIC, "1");
                    right.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
                }
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                    publishMessage(X_RIGHT_TOPIC, "0");
                    right.setBackground(getResources().getDrawable(R.drawable.curvebutton));
                }
                else{

                }
                return true;
            }
        });

        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage(Y_UP_TOPIC, "1");
                    up.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
                }
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                    publishMessage(Y_UP_TOPIC, "0");
                    up.setBackground(getResources().getDrawable(R.drawable.curvebutton));
                }
                else{

                }
                return true;
            }
        });

        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage(Y_DOWN_TOPIC, "1");
                    down.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
                }
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                    publishMessage(Y_DOWN_TOPIC, "0");
                    down.setBackground(getResources().getDrawable(R.drawable.curvebutton));
                }
                else{

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
                    zUp.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    String payload = "0";
                    publishMessage(Z_UP_TOPIC, payload);
                    zUp.setBackground(getResources().getDrawable(R.drawable.curvebutton));
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
                    zDown.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    String payload = "0";
                    publishMessage(Z_DOWN_TOPIC, payload);
                    zDown.setBackground(getResources().getDrawable(R.drawable.curvebutton));
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
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    if (CHOSEN_PARASITE.equals("Seleccionar")){
                        showToast("Nombre no permitido");
                        vibrator.vibrate(500);
                    }
                    else {
                        String payload = "takePictureRemoteController;" + CHOSEN_PARASITE;
                        publishMessage(CAMERA_APP_TOPIC, payload);
                        picButton.setBackground(getResources().getDrawable(R.drawable.camerapressed));
                    }
                }
                else{
                    picButton.setBackground(getResources().getDrawable(R.drawable.camera));
                }
                return true;
            }
        });

        autofocusButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String payload = "start";
                    publishMessage(AUTOFOCUS_APP_TOPIC, payload);
                    autofocusButton.setBackground(getResources().getDrawable(R.drawable.camerapressed));
                }
                else {
                    autofocusButton.setBackground(getResources().getDrawable(R.drawable.camera));
                }
                return true;
            }
        });

        homeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String payload = "home;home";
                    publishMessage(EXTRA_ACTIONS_TOPIC, payload);
                    homeButton.setBackground(getResources().getDrawable(R.drawable.camerapressed));
                }
                else {
                    homeButton.setBackground(getResources().getDrawable(R.drawable.camera));
                }
                return true;
            }
        });

    }

    /*** Support methods */
    /** Show message
     * @param message: input String that contains the message to be displayed
     * */
    public void showToast(String message){
        Toast.makeText(ManualController.this, message, Toast.LENGTH_SHORT).show();
    }

    /*** Publish a message
     * @param topic: input String that defines the target topic of the mqtt client
     * @param message: input String that contains a message to be published
     * @return no return
     * */
    public void publishMessage(String topic, String message) {
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = message.getBytes("UTF-8");
            MQTTServiceCommand.publish(ManualController.this, topic, encodedPayload, 2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public class MQTTReceiver extends MQTTServiceReceiver {

        @Override
        public void onPublishSuccessful(Context context, String requestId,
                                        String topic) {
            // called when a message has been successfully published
        }

        @Override
        public void onSubscriptionSuccessful(Context context, String requestId,
                                             String topic) {
            // called when a subscription is successful
        }

        @Override
        public void onSubscriptionError(Context context, String requestId,
                                        String topic, Exception exception) {
            // called when a subscription is not successful.
            // This usually happens when the broker does not give permissions
            // for the requested topic
        }

        @Override
        public void onMessageArrived(Context context, String topic,
                                     byte[] payload) {
            // called when a new message arrives on any topic
        }

        @Override
        public void onConnectionSuccessful(Context context, String requestId) {
            // called when the connection is successful
        }

        @Override
        public void onException(Context context, String requestId,
                                Exception exception) {
            // called when an error happens
        }

        @Override
        public void onConnectionStatus(Context context, boolean connected) {
            // called when connection status is requested or changes
        }
    }

    /*** Callback parasite spinner */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        Spinner spinner = (Spinner) adapterView;
        if(spinner.getId() == R.id.spinnerParasite) {
            CHOSEN_PARASITE = adapterView.getItemAtPosition(position).toString();
            if (CHOSEN_PARASITE.equals("Seleccionar")) {
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.spinnerseleccionar));
            }
            else if (CHOSEN_PARASITE.equals("Ascaris lumbricoides")) {
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Blastocystis hominis")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Chilomastix mesnilli")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Entamoeba hartmanni")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Entamoeba histolytica")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Entamoeba coli")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Endolimax nana")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Enterobius vermicularis")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Fasciola hepatica")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Giardia lamblia")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Hymenolepis diminuta")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Hymenolepis nana")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Iodamoeba butschilii")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Strongyloides estercoralis")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Taenia spp.")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Trichiris trichuris")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else if (CHOSEN_PARASITE.equals("Uncinaria spp.")){
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebuttonpressed));
            }
            else {
                parasiteSpinner.setBackground(getResources().getDrawable(R.drawable.curvebutton));
            }
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
