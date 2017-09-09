package net.igenius.mqttdemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.igenius.mqttservice.MQTTService;
import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceLogger;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
                                                                AdapterView.OnItemSelectedListener {

    /*** Intents */
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

    /** Debug tag */
    private String TAG = "MainActivity";

    private MQTTServiceReceiver receiver = new MQTTServiceReceiver() {

        private static final String TAG = "Receiver";

        @Override
        public void onSubscriptionSuccessful(Context context, String requestId, String topic) {
            Log.e(TAG, "Subscribed to " + topic);

            JsonObject request = new JsonObject();
            request.addProperty("question", "best time to post");
            request.addProperty("lang", "en");
            request.addProperty("request_uid", "testAndroid/" + new Date().getTime());

            byte[] payload = new Gson().toJson(request).getBytes();

            MQTTServiceCommand.publish(context, "/advisor/topic", payload);
        }

        @Override
        public void onSubscriptionError(Context context, String requestId, String topic, Exception exception) {
            Log.e(TAG, "Can't subscribe to " + topic, exception);
        }

        @Override
        public void onPublishSuccessful(Context context, String requestId, String topic) {
            Log.e(TAG, "Successfully published on topic: " + topic);
        }

        @Override
        public void onMessageArrived(Context context, String topic, byte[] payload) {
            Log.e(TAG, "New message on " + topic + ":  " + new String(payload));
            //showToast();
        }

        @Override
        public void onConnectionSuccessful(Context context, String requestId) {
            Log.e(TAG, "Connected!");
        }

        @Override
        public void onException(Context context, String requestId, Exception exception) {
            exception.printStackTrace();
            Log.e(TAG, requestId + " exception");
        }

        @Override
        public void onConnectionStatus(Context context, boolean connected) {

        }
    };

    public String server = CHOSEN_BROKER; //"tcp://192.168.3.193:1883"; //"ssl://yourserver.com:port";
    public String username = "username";
    public String password = "password";
    public String clientId = UUID.randomUUID().toString();
    public String publishTopic = "/connect";
    public String subscribeTopic = "/connect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Force landscape orientation */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /** Force landscape orientation */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /** Instantiate UI components and bind to xml */
        homeButton = (ImageButton) findViewById(R.id.homeButton);
        brokerButton = (Button) findViewById(R.id.brokerButton);

        seekBar0 = (SeekBar) findViewById(R.id.seekBar0);
        seekBar0.setProgress(1);
        seekBar0.setOnSeekBarChangeListener(this);

        spinnerBroker = (Spinner) findViewById(R.id.spinnerBroker);
        spinnerBroker.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.broker_list, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBroker.setAdapter(adapter2);

        /** Configure initial parameters of UI components */
        homeButton.setEnabled(false);
        seekBar0.setEnabled(false);

        /** UI components' callback functions */
        homeButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                String payload = "1";
                //publish_message(HOME_TOPIC, payload);
            }
        });

        brokerButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                MQTTService.NAMESPACE = "net.igenius.mqttdemo";
                MQTTServiceLogger.setLogLevel(MQTTServiceLogger.LogLevel.DEBUG);
                MQTTServiceCommand.connectAndSubscribe(MainActivity.this,
                                                        server,
                                                        clientId,
                                                        username,
                                                        password,
                                                        0,
                                                        true,
                                                        subscribeTopic);

                intent = new Intent(MainActivity.this, Controller.class);
                String message = "message";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        receiver.unregister(this);
    }

    /*** Seekbar Callbacks */
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

    /*** Spinner callbacks */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        CHOSEN_BROKER = adapterView.getItemAtPosition(i).toString();
        showToast(CHOSEN_BROKER);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        selected_field = "Nothing";
    }


    public void showToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}

