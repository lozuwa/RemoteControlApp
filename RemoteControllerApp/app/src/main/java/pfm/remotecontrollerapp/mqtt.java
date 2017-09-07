package pfm.remotecontrollerapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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

/**
 * Created by HP on 6/9/2017.
 */

public class mqtt extends Service implements MqttCallback {
    /** Constant variables */
    public static final String TAG = "MQTT";

    /** Thread */
    public HandlerThread mMqttKeepAlive;
    public Handler mMqttHandler;
    public Runnable Mqttrunnable;

    /** mqtt client */
    public MqttAndroidClient client;
    public MqttConnectOptions options;
    /*options = new MqttConnectOptions();
    options.setMqttVersion(4);
    options.setKeepAliveInterval(300);
    options.setCleanSession(false);*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*** Constructor */
    public mqtt(Context ctx){
        connectMQTT();
    }

    /*** MQTT callbacks */
    @Override
    public void connectionLost(Throwable cause) {
        //showToast("Client disconneted because: " + cause.toString());
        if (!client.isConnected()) {
            connectMQTT();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //showToast("Topic: " + topic + " Message: " + message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    /*** Threads */
    public void startMQTTThread() {
        mMqttKeepAlive = new HandlerThread("RESTThread");
        mMqttKeepAlive.start();
        mMqttHandler = new Handler(mMqttKeepAlive.getLooper());
        Mqttrunnable = new Runnable() {
            @Override
            public void run() {
                ReconnectMQTT();
            }
        };
        mMqttHandler.postDelayed(Mqttrunnable, 60000);
    }

    public void stopBackgroundThread(){
        try {
            mMqttKeepAlive.quitSafely();
        } catch (Exception e){
            e.printStackTrace();
        }
        try{
            mMqttKeepAlive.join();
            mMqttKeepAlive = null;
            mMqttHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** Support methods */
    public void connectMQTT(){
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this, "tcp://192.168.3.193:1883", clientId);
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess");
                    //Toast.makeText(mqtt.this, "Connection successful", Toast.LENGTH_SHORT).show();
                    client.setCallback(mqtt.this);
                    final String topic = "/random_topic_with_no_intention";
                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(topic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken){
                            }
                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "onFailure");
                    //Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void ReconnectMQTT(){
        if (!client.isConnected()){
            //showToast("Client is disconnected");
            connectMQTT();
        }
    }

    public void publish_message(String topic, String payload){
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(false);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

}
