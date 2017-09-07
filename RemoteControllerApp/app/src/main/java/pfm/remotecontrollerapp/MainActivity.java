package pfm.remotecontrollerapp;

/**
 * Author: Rodrigo Loza
 * Company: pfm Medical Bolivia
 * Description: app designed to work as a remote controller for the click microscope and
 * the camera app.
 * */

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
                                                                AdapterView.OnItemSelectedListener {

    /**Intents*/
    Intent intent;

    /*** UI components */
    public Button toController;

    /** Actions */
    public ImageButton picButton;
    public ImageButton homeButton;
    public Button brokerButton;
    public ToggleButton connection;
    /** Selections */
    public SeekBar seekBar0;
    public Spinner spinnerBroker;

    /** variables*/
    public String selected_field = "Nothing";

    /** Constants */
    public static final String EXTRA_MESSAGE = "com.example.pfm.remoteControllerApp";

    public static final String PC_BROKER = "tcp://192.168.3.193:1883";
    public String CHOSEN_BROKER = PC_BROKER;

    public static final String CONNECTION_TOPIC = "/connect";

    public static final String MICROSCOPE_TOPIC = "/microscope";
    public static final String HOME_TOPIC = "/home";
    public static final String MOVEFIELDX_TOPIC = "/movefieldx";
    public static final String MOVEFIELDY_TOPIC = "/movefieldy";
    public static final String STEPS_TOPIC = "/steps";
    public static final String AUTOFOCUS_TOPIC = "/autofocus";

    public static final int TIME_CHECK_CONNECTION = 60000;

    /** Debug tag */
    private String TAG = "MainActivity";

    /** Constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Force landscape orientation */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /** Instantiate UI components and bind to xml */
        toController = (Button) findViewById(R.id.toController);
        picButton = (ImageButton) findViewById(R.id.picButton);
        homeButton = (ImageButton) findViewById(R.id.homeButton);

        brokerButton = (Button) findViewById(R.id.brokerButton);

        connection = (ToggleButton) findViewById(R.id.connection);

        seekBar0 = (SeekBar) findViewById(R.id.seekBar0);
        seekBar0.setProgress(1);
        seekBar0.setOnSeekBarChangeListener(this);

        spinnerBroker = (Spinner) findViewById(R.id.spinnerBroker);
        spinnerBroker.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.broker_list, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBroker.setAdapter(adapter2);

        /** Configure initial parameters of UI components */
        connection.setChecked(false);

        connection.setEnabled(true);
        homeButton.setEnabled(false);
        picButton.setEnabled(false);
        seekBar0.setEnabled(false);

        /** UI components' callback functions */
        connection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /** Change state*/
                    seekBar0.setEnabled(true);
                    picButton.setEnabled(true);
                    homeButton.setEnabled(true);
                    /** Send message to activate connection */
                    String payload = "1";
                    //publish_message(CONNECTION_TOPIC, payload);
                }
                else {
                    /** Change state */
                    picButton.setEnabled(false);
                    seekBar0.setEnabled(false);
                    homeButton.setEnabled(false);
                    /** Send message to deactivate connection */
                    String payload = "2";
                    //publish_message(CONNECTION_TOPIC, payload);
                }
            }
        });

        picButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
            String payload = "pic;" + selected_field;
            //publish_message(MICROSCOPE_TOPIC, payload);
            }
        });

        brokerButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                //CHOSEN_BROKER = TEST_BROKER;
                showToast("Connecting to: " + CHOSEN_BROKER);
                //connectMQTT();
            }
        });

        homeButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                String payload = "1";
                //publish_message(HOME_TOPIC, payload);
            }
        });

        toController.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                intent = new Intent(MainActivity.this, Controller.class);
                String message = "message";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });

    }

    /********************************Class' Callbacks*********************************************/
    @Override
    public void onStart(){
        super.onStart();
        /** Restart UI elements */
        connection.setChecked(false);
        selected_field = "Nothing";
        /** Start thread */
        //startMQTTThread();
    }

    @Override
    public void onResume(){
        super.onResume();
        //client.registerResources(MainActivity.this);
        /** Reset UI components */
        connection.setChecked(false);
        selected_field = "Nothing";
        /** Reconnect MQTT */
        //ReconnectMQTT();
        //startMQTTThread();
    }

    @Override
    public void onStop(){
        super.onStop();
        /** UI elements */
        selected_field = "Nothing";
        /** Restart threads */
        //stopBackgroundThread();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //client.unregisterResources();
    }
    /********************************************************************************************/

    /*************************************Seekbar Callbacks*************************************************/
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar.equals(seekBar0)){
        }
        else{
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.equals(seekBar0)){
            if (progress == 0){
                progress = 1;
                seekBar0.setProgress(progress);
            }
            String payload = String.valueOf(progress);
            //publish_message(STEPS_TOPIC, payload);
        }
        else{
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
    /**************************************************************************************/

    /**************************************Spinner Callbacks************************************************/
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        CHOSEN_BROKER = adapterView.getItemAtPosition(i).toString();
        showToast(CHOSEN_BROKER);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        selected_field = "Nothing";
    }
    /**************************************************************************************/

    /**************************************Support classes************************************************/
    public void MoveFieldX(int direction){
        String payload = "";
        if (direction == 1){
            payload = "1";
        }
        else if (direction == 0){
            payload = "0";
        }
        else{
            payload = "--";
        }
        //publish_message(MOVEFIELDX_TOPIC, payload);
    }

    public void MoveFieldY(int direction){
        String payload = "";
        if (direction == 1){
            payload = "1";
        }
        else if (direction == 0){
            payload = "0";
        }
        else{
            payload = "--";
        }
        //publish_message(MOVEFIELDY_TOPIC, payload);
    }

    public void showToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
