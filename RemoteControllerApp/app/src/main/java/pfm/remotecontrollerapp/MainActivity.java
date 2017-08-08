package pfm.remotecontrollerapp;

/**
 * Author: Rodrigo Loza
 * Company: pfm Medical Bolivia
 * Description: app designed to work as a remote controller for the click microscope and
 * the camera app.
 * */

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

public class MainActivity extends AppCompatActivity implements MqttCallback, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    /** Attributes */
    /** UI components */
    public ImageButton zup;
    public ImageButton zdown;
    public ImageButton picButton;
    public ImageButton homeButton;
    public ImageButton MoveFieldForward;
    public ImageButton MoveFieldBackward;
    public Button brokerbutton;
    public ToggleButton connection;
    public SeekBar seekBar0;
    public TextView textView0;
    public Spinner spinner;
    public Switch switch0;

    /** variables*/
    public String selected_field = "Nothing";
    public Boolean brokerBool = false;
    public static List<String> parasites_list;

    /** mqtt client */
    public MqttAndroidClient client;
    public MqttConnectOptions options;

    /** Constants */
    public static final String TEST_BROKER = "tcp://test.mosquitto.org:1883";
    public static final String PC_BROKER = "tcp://192.168.3.174:1883";
    public String CHOSEN_BROKER = PC_BROKER;

    public static final String CONNECTION_TOPIC = "/connect";
    public static final String Z_UP_TOPIC = "/zu";
    public static final String Z_DOWN_TOPIC = "/zd";
    public static final String MICROSCOPE_TOPIC = "/microscope";
    public static final String HOME_TOPIC = "/home";
    public static final String LED_TOPIC = "/led";
    public static final String MOVEFIELD_TOPIC = "/movefield";
    public static final String STEPS_TOPIC = "/steps";

    /** Thread */
    public HandlerThread mMqttKeepAlive;
    public Handler mMqttHandler;
    public Runnable Mqttrunnable;

    /** Debug tag */
    private String TAG = "MainActivity";

    /** Constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Build mqtt client and start connection */
        options = new MqttConnectOptions();
        options.setMqttVersion( 4 );
        options.setKeepAliveInterval( 300 );
        options.setCleanSession( false );
        connectMQTT();

        /** Initialize variables */
        parasites_list = new ArrayList<String>();
        parasites_list.add("Artefactos");
        parasites_list.add("Entamoeba histolytica");
        parasites_list.add("Entamoeba hartmanni");
        parasites_list.add("Iodamoeba butschilii");
        parasites_list.add("Endolimax nana");
        parasites_list.add("Chilomastix mesnilli");
        parasites_list.add("Blastocystis hominis");
        parasites_list.add("Giardia lamblia");
        parasites_list.add("Ascaris lumbricoides");
        parasites_list.add("Uncinaria spp.");
        parasites_list.add("Strongyloides estercoralis");
        parasites_list.add("Enterobius vermicularis");
        parasites_list.add("Hymenolepis nana");
        parasites_list.add("Hymenolepis diminuta");
        parasites_list.add("Taenia spp.");
        parasites_list.add("Trichiris trichuris");
        parasites_list.add("Fasciola hepatica");

        /**parasites_list.add("A.Lumbricoides");
        parasites_list.add("B.Hominis");
        parasites_list.add("E.Coli");
        parasites_list.add("G.Lamblia");*/

        /** Instantiate UI components and bind to xml */
        zup = (ImageButton)findViewById(R.id.zup);
        zdown = (ImageButton)findViewById(R.id.zdown);
        picButton = (ImageButton) findViewById(R.id.picButton);
        homeButton = (ImageButton) findViewById(R.id.homeButton);

        MoveFieldForward = (ImageButton) findViewById(R.id.movefieldforward);
        MoveFieldBackward = (ImageButton) findViewById(R.id.movefieldbackward);

        brokerbutton = (Button) findViewById(R.id.brokerbutton);

        connection = (ToggleButton) findViewById(R.id.connection);

        seekBar0 = (SeekBar) findViewById(R.id.seekBar0);
        seekBar0.setProgress(1);
        seekBar0.setOnSeekBarChangeListener(this);

        textView0 = (TextView) findViewById(R.id.textView0);

        spinner  = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.parasite_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        switch0 = (Switch) findViewById(R.id.switch0);

        /** Configure initial parameters of UI components */
        zup.setBackgroundColor(Color.BLUE);
        zdown.setBackgroundColor(Color.BLUE);
        MoveFieldBackward.setBackgroundColor(Color.BLUE);
        MoveFieldForward.setBackgroundColor(Color.BLUE);

        connection.setChecked(false);

        connection.setEnabled(true);
        zup.setEnabled(false);
        zdown.setEnabled(false);
        homeButton.setEnabled(false);
        picButton.setEnabled(false);
        MoveFieldForward.setEnabled(false);
        MoveFieldBackward.setEnabled(false);
        spinner.setEnabled(false);
        seekBar0.setEnabled(false);
        switch0.setEnabled(false);

        /** UI components' callback functions */
        connection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /** Change state*/
                    seekBar0.setEnabled(true);
                    zup.setEnabled(true);
                    zdown.setEnabled(true);
                    spinner.setEnabled(true);
                    picButton.setEnabled(true);
                    homeButton.setEnabled(true);
                    MoveFieldForward.setEnabled(true);
                    MoveFieldBackward.setEnabled(true);
                    switch0.setEnabled(true);

                    /** Send message to activate connection */
                    String topic = "/connect";
                    String payload = "1";
                    publish_message(CONNECTION_TOPIC, payload);
                }
                else {
                    /** Change state */
                    zup.setEnabled(false);
                    zdown.setEnabled(false);
                    picButton.setEnabled(false);
                    spinner.setEnabled(false);
                    seekBar0.setEnabled(false);
                    homeButton.setEnabled(false);
                    MoveFieldForward.setEnabled(false);
                    MoveFieldBackward.setEnabled(false);

                    /** Send message to deactivate connection */
                    String topic = "/connect";
                    String payload = "2";
                    publish_message(CONNECTION_TOPIC, payload);
                }
            }
        });

        picButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                if ( check_field(selected_field) ) {
                    String payload = "pic;" + selected_field;
                    publish_message(MICROSCOPE_TOPIC, payload);
                    //MoveField();
                }
                else{
                    showToast("Nombre no permitido");
                }
            }
        });

        switch0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    publish_message(LED_TOPIC, "1");
                }
                else {
                    publish_message(LED_TOPIC, "0");
                }
            }
        });

        brokerbutton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                brokerBool = !brokerBool;
                if (brokerBool){
                    CHOSEN_BROKER = PC_BROKER;
                    showToast("Connecting to: " + CHOSEN_BROKER);
                    connectMQTT();
                }
                else{
                    CHOSEN_BROKER = TEST_BROKER;
                    showToast("Connecting to: " + CHOSEN_BROKER);
                    connectMQTT();
                }
            }
        });

        homeButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                String topic = "/home";
                String payload = "1";
                publish_message(HOME_TOPIC, payload);
            }
        });

        zup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    zup.setBackgroundColor(Color.GREEN);
                    String topic = "/zu";
                    String payload = "1";
                    publish_message(Z_UP_TOPIC, payload);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    zup.setBackgroundColor(Color.BLUE);
                    String topic = "/zu";
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
                    zdown.setBackgroundColor(Color.GREEN);
                    String topic = "/zd";
                    String payload = "1";
                    publish_message(Z_DOWN_TOPIC, payload);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    zdown.setBackgroundColor(Color.BLUE);
                    String topic = "/zd";
                    String payload = "2";
                    publish_message(Z_DOWN_TOPIC, payload);
                }
                return true;
            }
        });

        MoveFieldForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    MoveFieldForward.setBackgroundColor(Color.GREEN);
                    MoveField(1);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    MoveFieldForward.setBackgroundColor(Color.BLUE);
                }
                return true;
            }
        });

        MoveFieldBackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    MoveFieldBackward.setBackgroundColor(Color.GREEN);
                    MoveField(0);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    MoveFieldBackward.setBackgroundColor(Color.BLUE);
                }
                return true;
            }
        });
    }

    /********************************Class' Callbacks*********************************************/
    @Override
    public void onStart(){
        super.onStart();
        /** Restart UI elements */
        spinner.setSelection(1);
        connection.setChecked(false);
        selected_field = "Nothing";
        /** Start thread */
        startPOSTThread();
    }

    @Override
    public void onResume(){
        super.onResume();
        client.registerResources( MainActivity.this );
        spinner.setSelection(0);
        connection.setChecked(false);
        selected_field = "Nothing";
    }

    @Override
    public void onStop(){
        super.onStop();
        /** UI elements */
        selected_field = "Nothing";
        spinner.setSelection(1);
        /** Restart threads */
        stopBackgroundThread();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        client.unregisterResources();
    }
    /********************************************************************************************/

    /*************************************Threads*************************************************/
    public void startPOSTThread() {
        mMqttKeepAlive = new HandlerThread("RESTThread");
        mMqttKeepAlive.start();
        mMqttHandler = new Handler(mMqttKeepAlive.getLooper());
        Mqttrunnable = new Runnable() {
            @Override
            public void run() {
                ReconnectMQTT();
            }
        };
        mMqttHandler.postDelayed(Mqttrunnable, 120000);
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
    /********************************************************************************************/

    /*************************************MQTT Callbacks*************************************************/
    @Override
    public void connectionLost(Throwable cause) {
        showToast("Client disconneted because: " + cause.toString());
        if (!client.isConnected()) {
            connectMQTT();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        showToast("Topic: " + topic + " Message: " + message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
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
                seekBar0.setProgress(1);
            }
            String payload = String.valueOf(progress);
            publish_message(STEPS_TOPIC, payload);
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
        selected_field = adapterView.getItemAtPosition(i).toString();
        showToast(selected_field);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        selected_field = "Nothing";
    }
    /**************************************************************************************/

    /**************************************Support classes************************************************/
    public Boolean check_field(String field){
        if (parasites_list.contains(field)){
            return true;
        }
        else {
            return false;
        }
    }

    public void MoveField(int direction){
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
        publish_message(MOVEFIELD_TOPIC, payload);
    }

    public void connectMQTT(){
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), CHOSEN_BROKER, clientId);
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess");
                    Toast.makeText(MainActivity.this, "Connection successful", Toast.LENGTH_SHORT).show();
                    client.setCallback(MainActivity.this);
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
                    Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void ReconnectMQTT() {
        if (!client.isConnected()) {
            showToast("Client is disconnected");
            connectMQTT();
        }
    }

    public void showToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
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
    /*************************************************************************************************/

}
