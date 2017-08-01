package pfm.remotecontrollerapp;

/**
 * Author: Rodrigo Loza
 * Company: pfm Medical Bolivia
 * Description: app designed to work as a remote controller for the click microscope and
 * the camera app.
 * */

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
    public ToggleButton connection;
    public SeekBar seekBar0;
    public TextView textView0;
    public Spinner spinner;
    public Switch switch0;

    /** variables*/
    public String selected_field = "Nothing";

    /** mqtt client */
    public MqttAndroidClient client;
    public MqttConnectOptions options;

    /** Constants */
    public static final String TEST_BROKER = "tcp://test.mosquitto.org:1883";
    public static final String BROKER = "tcp://192.168.3.174";

    public static final String CONNECTION_TOPIC = "/connect";
    public static final String Z_UP = "/zu";
    public static final String Z_DOWN = "/zd";
    public static final String MICROSCOPE = "/microscope";
    public static final String HOME = "/home";

    /** Debug tag */
    private static final String TAG = "MainActivity";

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

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), TEST_BROKER, clientId);
        //connectMQTT();
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

        /** Instantiate UI components and bind to xml */
        zup = (ImageButton)findViewById(R.id.zup);
        zdown = (ImageButton)findViewById(R.id.zdown);
        picButton = (ImageButton) findViewById(R.id.picButton);
        homeButton = (ImageButton) findViewById(R.id.homeButton);

        MoveFieldForward = (ImageButton) findViewById(R.id.movefieldforward);
        MoveFieldBackward = (ImageButton) findViewById(R.id.movefieldbackward);

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
                    publish_message(topic, payload);
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
                    publish_message(topic, payload);
                }
            }
        });

        picButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                if ( check_field(selected_field) ) {
                    String topic = "/microscope";
                    String payload = "pic;" + selected_field;
                    publish_message(topic, payload);
                    //MoveField();
                }
                else{
                    showToast("Nombre no permitido");
                }
            }
        });

        homeButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                String topic = "/home";
                String payload = "1";
                publish_message(topic, payload);
            }
        });

        zup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    zup.setBackgroundColor(Color.GREEN);
                    String topic = "/zu";
                    String payload = "1";
                    publish_message(topic, payload);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    zup.setBackgroundColor(Color.BLUE);
                    String topic = "/zu";
                    String payload = "2";
                    publish_message(topic, payload);
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
                    publish_message(topic, payload);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    zdown.setBackgroundColor(Color.BLUE);
                    String topic = "/zd";
                    String payload = "2";
                    publish_message(topic, payload);
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

    /********************************Callbacks class*********************************************/
    @Override
    public void onStart(){
        super.onStart();
        spinner.setSelection(0);
        connection.setChecked(false);
        selected_field = "Nothing";
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
        selected_field = "Nothing";
        spinner.setSelection(0);
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        client.unregisterResources();
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
            String topic = "/timemicro";
            String payload = String.valueOf(progress);
            publish_message(topic, payload);
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
        if (selected_field.equals("A.Lumbricoides")){
            selected_field = "A.Lumbricoides";
        }

        else if (selected_field.equals("Artefactos")){
            selected_field = "Artefactos";
        }

        else if (selected_field.equals("B.Hominis")){
            selected_field = "B.Hominis";
        }

        else if (selected_field.equals("E.Coli")){
            selected_field= "E.Coli";
        }

        else if (selected_field.equals("G.Lamblia")){
            selected_field= "G.Lamblia";
        }

        else{

        }
        //Toast.makeText(MainActivity.this, selected_field, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        selected_field = "Nothing";
    }
    /**************************************************************************************/

    /**************************************Support classes************************************************/
    public Boolean check_field(String field){
        ReconnectMQTT();

        List<String> list = new ArrayList<String>();
        list.add("Artefactos");
        list.add("A.Lumbricoides");
        list.add("B.Hominis");
        list.add("E.Coli");
        list.add("G.Lamblia");

        if (list.contains(field)){
            return true;
        }
        else {
            return false;
        }
    }

    public void MoveField(int direction){
        ReconnectMQTT();

        String topic = "/movefield";
        String payload = "";
        if (direction == 1){
            payload = "1";
        }
        else if (direction == 0){
            payload = "0";
        }
        else{
            payload = "500";
        }

        publish_message(topic, payload);
    }

    public void connectMQTT(){
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
