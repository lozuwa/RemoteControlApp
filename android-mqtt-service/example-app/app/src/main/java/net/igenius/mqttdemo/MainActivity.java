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
import android.widget.Spinner;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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
    //String server = "tcp://192.168.3.174:1883"; //"ssl://yourserver.com:port";
    public String username = "pfm";
    public String password = "161154029";
    public String clientId = UUID.randomUUID().toString();
    public String publishTopic = "/random";
    public String subscribeTopic = "/random_topic_with_no_intention";

    /** UI Elements */
    public Spinner spinnerBroker;
    public Button brokerButton;

    /** Constants */
    public static String CHOSEN_BROKER = "";

    /*** Constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Content */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /** UI Elements */
        spinnerBroker = (Spinner) findViewById(R.id.spinnerBroker);
        brokerButton = (Button) findViewById(R.id.brokerButton);

        spinnerBroker.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                                                                            R.array.broker_list,
                                                                            android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBroker.setAdapter(adapter);

        /** Action callbacks */
        brokerButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                showToast("Connecting to: " + CHOSEN_BROKER);
                /** MQTT */
                MQTTService.NAMESPACE = "net.igenius.mqttdemo";
                MQTTServiceLogger.setLogLevel(MQTTServiceLogger.LogLevel.DEBUG);
                MQTTServiceCommand.connectAndSubscribe(MainActivity.this,
                                                        CHOSEN_BROKER,
                                                        clientId,
                                                        username,
                                                        password,
                                                        0,
                                                        true,
                                                        subscribeTopic);
                Intent intent = new Intent(MainActivity.this, Controller.class);
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });

    }

    /*** Callback on resume */
    @Override
    protected void onResume() {
        super.onResume();
        receiver.register(this);
    }

    /*** Callback on pause */
    @Override
    protected void onPause() {
        super.onPause();
        receiver.unregister(this);
    }

    /*** Callback spinner */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if(spinner.getId() == R.id.spinnerBroker){
            CHOSEN_BROKER = adapterView.getItemAtPosition(i).toString();
            showToast(CHOSEN_BROKER);
        }
        else{
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /*** Support classes */
    /** Show toast */
    public void showToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}

