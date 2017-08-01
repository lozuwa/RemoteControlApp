package me.yogeshwardan.mqttsubscriber;

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

    public ImageButton yforward;
    public ImageButton ybackward;
    public ImageButton xleft;
    public ImageButton xright;
    public ImageButton zup;
    public ImageButton zdown;
    public ImageButton picButton;
    public ImageButton homeButton;
    public ImageButton MoveFieldForward;
    public ImageButton MoveFieldBackward;
    public ToggleButton connection;
    public ToggleButton automaticButton;
    public SeekBar seekBar0;
    public SeekBar seekBar1;
    public SeekBar seekBar3;
    public TextView textView;
    public TextView textView1;
    public TextView textView3;
    public Spinner spinner;
    public String selected_field = "Nothing";

    public MqttAndroidClient client;
    public MqttConnectOptions options;

    /**********************************MQTT***************************************************/

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        options = new MqttConnectOptions();
        options.setMqttVersion( 4 );
        options.setKeepAliveInterval( 300 );
        options.setCleanSession( false );

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.3.174", clientId);

         //tcp://test.mosquitto.org:1883
        //tcp://test.mosquitto.org:1883
        //tcp://10.42.0.1

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
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
                                //Toast.makeText(MainActivity.this, "Successfully subscribed to: " + topic, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                Toast.makeText(MainActivity.this, "Couldn't subscribe to: " + topic, Toast.LENGTH_SHORT).show();
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

        yforward = (ImageButton)findViewById(R.id.yforward);
        ybackward = (ImageButton)findViewById(R.id.ybackward);
        xleft = (ImageButton)findViewById(R.id.xleft);
        xright = (ImageButton)findViewById(R.id.xright);
        zup = (ImageButton)findViewById(R.id.zup);
        zdown = (ImageButton)findViewById(R.id.zdown);
        picButton = (ImageButton) findViewById(R.id.picButton);
        homeButton = (ImageButton) findViewById(R.id.homeButton);

        MoveFieldForward = (ImageButton) findViewById(R.id.movefieldforward);
        MoveFieldBackward = (ImageButton) findViewById(R.id.movefieldbackward);

        connection = (ToggleButton) findViewById(R.id.connection);
        automaticButton = (ToggleButton) findViewById(R.id.automaticButton);

        seekBar0 = (SeekBar) findViewById(R.id.seekBar0);
        seekBar0.setProgress(1);
        seekBar0.setOnSeekBarChangeListener(this);

        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar1.setProgress(50);
        seekBar1.setOnSeekBarChangeListener(this);

        seekBar3 = (SeekBar) findViewById(R.id.seekBar3);
        seekBar3.setProgress(1);
        seekBar3.setOnSeekBarChangeListener(this);

        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView3 = (TextView) findViewById(R.id.textView3);

        spinner  = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.parasite_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        textView.setText("Intensidad led: " + "1" + "%");
        textView1.setText("Tiempos: " + String.valueOf( 500 ) + " us");
        textView3.setText("Zoom (aumentos): " + "1");

        yforward.setBackgroundColor(Color.GREEN);
        ybackward.setBackgroundColor(Color.GREEN);
        xright.setBackgroundColor(Color.GREEN);
        xleft.setBackgroundColor(Color.GREEN);
        zup.setBackgroundColor(Color.GREEN);
        zdown.setBackgroundColor(Color.GREEN);

        connection.setChecked(false);
        automaticButton.setChecked(true);

        connection.setEnabled(true);
        automaticButton.setEnabled(false);
        homeButton.setEnabled(false);
        yforward.setEnabled(false);
        ybackward.setEnabled(false);
        xleft.setEnabled(false);
        xright.setEnabled(false);
        zup.setEnabled(false);
        zdown.setEnabled(false);
        picButton.setEnabled(false);
        MoveFieldForward.setEnabled(false);
        MoveFieldBackward.setEnabled(false);
        spinner.setEnabled(false);
        seekBar0.setEnabled(false);
        seekBar1.setEnabled(false);
        seekBar3.setEnabled(false);

        connection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    yforward.setEnabled(true);
                    ybackward.setEnabled(true);
                    xleft.setEnabled(true);
                    xright.setEnabled(true);
                    zup.setEnabled(true);
                    zdown.setEnabled(true);
                    picButton.setEnabled(true);
                    spinner.setEnabled(true);
                    seekBar0.setEnabled(true);
                    seekBar1.setEnabled(true);
                    seekBar3.setEnabled(true);
                    homeButton.setEnabled(true);
                    MoveFieldForward.setEnabled(true);
                    MoveFieldBackward.setEnabled(true);
                    automaticButton.setEnabled(true);
                    String topic = "/connect";
                    String payload = "1";
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        message.setRetained(false);
                        client.publish(topic, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                } else {
                    yforward.setEnabled(false);
                    ybackward.setEnabled(false);
                    xleft.setEnabled(false);
                    xright.setEnabled(false);
                    zup.setEnabled(false);
                    zdown.setEnabled(false);
                    picButton.setEnabled(false);
                    spinner.setEnabled(false);
                    seekBar0.setEnabled(false);
                    seekBar1.setEnabled(false);
                    seekBar3.setEnabled(false);
                    homeButton.setEnabled(false);
                    MoveFieldForward.setEnabled(false);
                    MoveFieldBackward.setEnabled(false);
                    automaticButton.setEnabled(false);
                    String topic = "/connect";
                    String payload = "2";
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
        });

        automaticButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    String topic = "/automatic";
                    String payload = "1";
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
                else{
                    String topic = "/automatic";
                    String payload = "0";
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
        });


        picButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                if ( check_field(selected_field) ) {
                    String topic = "/microscope";
                    String payload = "pic;" + selected_field;
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        message.setRetained(false);
                        client.publish(topic, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                    //MoveField();
                }
                else{
                    Toast.makeText(MainActivity.this, "Nombre no es permitido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        homeButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v){
                String topic = "/home";
                String payload = "1";
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
        });

        yforward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    yforward.setBackgroundColor(Color.CYAN);
                    String topic = "/yf";
                    String payload = "1";
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        message.setRetained(false);
                        client.publish(topic, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    yforward.setBackgroundColor(Color.GREEN);
                    String topic = "/yf";
                    String payload = "2";
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
                return true;
            }
        });

        ybackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ybackward.setBackgroundColor(Color.CYAN);
                    String topic = "/yb";
                    String payload = "1";
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        message.setRetained(false);
                        client.publish(topic, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    ybackward.setBackgroundColor(Color.GREEN);
                    String topic = "/yb";
                    String payload = "2";
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
                return true;
            }
        });

        xleft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    xleft.setBackgroundColor(Color.CYAN);
                    String topic = "/xl";
                    String payload = "1";
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        message.setRetained(false);
                        client.publish(topic, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    xleft.setBackgroundColor(Color.GREEN);
                    String topic = "/xl";
                    String payload = "2";
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
                return true;
            }
        });

        xright.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    xright.setBackgroundColor(Color.CYAN);
                    String topic = "/xr";
                    String payload = "1";
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        message.setRetained(false);
                        client.publish(topic, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    xright.setBackgroundColor(Color.GREEN);
                    String topic = "/xr";
                    String payload = "2";
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
                return true;
            }
        });

        zup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    zup.setBackgroundColor(Color.CYAN);
                    String topic = "/zu";
                    String payload = "1";
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        message.setRetained(false);
                        client.publish(topic, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    zup.setBackgroundColor(Color.GREEN);
                    String topic = "/zu";
                    String payload = "2";
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
                return true;
            }
        });

        zdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    zdown.setBackgroundColor(Color.CYAN);
                    String topic = "/zd";
                    String payload = "1";
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        message.setRetained(false);
                        client.publish(topic, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    zdown.setBackgroundColor(Color.GREEN);
                    String topic = "/zd";
                    String payload = "2";
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
                return true;
            }
        });

        MoveFieldForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    MoveField(1);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

                }
                return true;
            }
        });

        MoveFieldBackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    MoveField(0);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

                }
                return true;
            }
        });


    }
    /********************************************************************************************/

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

    /*************************************MQTT Callback*************************************************/
    @Override
    public void connectionLost(Throwable cause) {
        Toast.makeText(MainActivity.this, cause.toString(), Toast.LENGTH_SHORT).show();
        if (!client.isConnected()){
            try {
                client.connect(options);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Toast.makeText(MainActivity.this, "Topic: "+topic+"\nMessage: "+message.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
    /********************************************************************************************/

    /*************************************Seekbar*************************************************/
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //Toast.makeText(getApplicationContext(),"Brightness: "+progress, Toast.LENGTH_SHORT).show();

        if (seekBar.equals(seekBar0)){

            if (progress == 0){
                progress = 1;
                seekBar0.setProgress(1);
            }

            String topic = "/led";
            String payload = String.valueOf(progress);
            byte[] encodedPayload = new byte[0];
            try {
                textView.setText("Intensidad led: " + (progress) + "%");
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                message.setRetained(false);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }

        else if (seekBar.equals(seekBar1)){

            if (progress == 0){
                progress = 1;
                seekBar1.setProgress(1);
            }

            String payload = String.valueOf(progress);
            String time = String.valueOf( Double.valueOf(payload)/10 );

            String topic = "/timemicro";
            byte[] encodedPayload = new byte[0];
            try {
                progress = Integer.valueOf((int) (Double.valueOf(progress) * 10)+1) ;
                textView1.setText("Tiempo: " + progress + " us");
                encodedPayload = time.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                message.setRetained(false);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }

        }

        else if (seekBar.equals(seekBar3)){

            if (progress == 0){
                progress = 1;
                seekBar3.setProgress(1);
            }

            String topic = "/microscope";
            String payload = "z;" + String.valueOf(progress);
            byte[] encodedPayload = new byte[0];
            try {
                progress = Integer.valueOf((int) (((Double.valueOf(progress) / 100) * 50))+1) ;
                textView3.setText("Zoom (aumentos): " + progress);
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                message.setRetained(false);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }

        else{

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
        if (seekBar.equals(seekBar0)){

        }
        else if (seekBar.equals(seekBar1)){

        }
        else{

        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
    }
    /**************************************************************************************/

    /**************************************Spinner************************************************/
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

    public void ReconnectMQTT() {
        if (!client.isConnected()) {
            Toast.makeText(MainActivity.this, "Client is disconnected", Toast.LENGTH_SHORT).show();
            try {
                client.connect(options);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
    /********************************************************************************************/

}
