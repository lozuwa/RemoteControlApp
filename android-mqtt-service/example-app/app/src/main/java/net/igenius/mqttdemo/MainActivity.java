package net.igenius.mqttdemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.igenius.mqttservice.MQTTService;
import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceLogger;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    /*** Variables and instances */
    /** MQTT Variables */
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
    public String username = "pfm";
    public String password = "161154029";
    public String clientId = UUID.randomUUID().toString();
    public String publishTopic = "/random";
    public String subscribeTopic = "/random_topic_with_no_intention";

    /** UI Elements */
    public Button connectButton;
    public Button manualButton;
    public Button automaticButton;
    public EditText patientEditText;
    public EditText brokerEditText;

    /** Constants */
    public static String BROKER;
    public static final String CAMERA_APP_TOPIC = "/cameraApp";

    /*** Constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Content */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Orientation */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /** Keep screen turned on */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /** UI Elements */
        connectButton = (Button) findViewById(R.id.ConnectButton);
        manualButton = (Button) findViewById(R.id.startManualButton);
        automaticButton = (Button) findViewById(R.id.startAutomaticButton);
        patientEditText = (EditText) findViewById(R.id.PatientEditText);
        brokerEditText = (EditText) findViewById(R.id.BrokerEditText);

        /** Initial states */

        /** Action callbacks */
        connectButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                BROKER = brokerEditText.getText().toString();
                final int qos = 2;
                showToast("Connecting to: " + BROKER);
                /** MQTT */
                MQTTService.NAMESPACE = "net.igenius.mqttdemo";
                MQTTServiceLogger.setLogLevel(MQTTServiceLogger.LogLevel.DEBUG);
                MQTTServiceCommand.connectAndSubscribe(MainActivity.this,
                                                        BROKER,
                                                        clientId,
                                                        username,
                                                        password,
                                                        qos,
                                                        true,
                                                        subscribeTopic);
            }
        });

        manualButton.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                /** Create respective folder */
                /*final String FOLDER_NAME = patientEditText.getText().toString();
                if (FOLDER_NAME.isEmpty() || (FOLDER_NAME.length() < 5)){
                    showToast("Patient's name is empty");
                }
                else{
                    //publishMessage(CAMERA_APP_TOPIC, "createFolder;"+FOLDER_NAME);
                }*/
                /** Start remote controller */
                Intent intent = new Intent(MainActivity.this, ManualController.class);
                startActivity(intent);
            }
        });

        automaticButton.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                /** Create respective folder */
                final String FOLDER_NAME = patientEditText.getText().toString();
                if (FOLDER_NAME.isEmpty() || (FOLDER_NAME.length() < 5)){
                    showToast("Patient's name is empty");
                }
                else {
                    //publishMessage(CAMERA_APP_TOPIC, "createFolder;" + FOLDER_NAME);
                    /** Start remote controller */
                    //Intent intent = new Intent(MainActivity.this, LoadSample.class);
                    //startActivity(intent);
                }
            }
        });
    }

    /** Callback on resume */
    @Override
    protected void onResume(){
        super.onResume();
        receiver.register(this);
    }

    /** Callback on pause */
    @Override
    protected void onPause(){
        super.onPause();
        receiver.unregister(this);
    }

    /** Support classes */
    /** Show toast */
    public void showToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
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
            MQTTServiceCommand.publish(MainActivity.this, topic, encodedPayload, 2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}

