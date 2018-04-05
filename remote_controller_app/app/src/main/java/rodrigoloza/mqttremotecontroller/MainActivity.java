package rodrigoloza.mqttremotecontroller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceReceiver;
import net.igenius.mqttservice.MQTTService;
import net.igenius.mqttservice.MQTTServiceLogger;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Constant variables
    private String broker = "tcp://192.168.0.108:1883";
    private String username = "rodrigoloza";
    private String password = "65478912";
    private String clientId = UUID.randomUUID().toString();
    private int qos = 2;

    // Topics
    public static final String STAGE_RESTART_HOME = "stage;restart;home;None;None";
    public static final String REQUEST_SERVICE_AUTOFOCUS_MANUAL = "requestService;autofocus;ManualController;None;None";
    public static final String TAKE_PICTURE_AUTOMATIC = "takePicture;None;None;None;";

    // UI elements
    // 3 DOF controllers
    public ImageButton buttonXRight;
    public ImageButton buttonXLeft;
    public ImageButton buttonYUp;
    public ImageButton buttonYDown;
    public ImageButton buttonZUp;
    public ImageButton buttonZDown;
    // Extra buttons
    public ImageButton buttonExtra1;
    public ImageButton buttonExtra2;
    public ImageButton buttonExtra3;
    // Vibrate service
    public Vibrator vibrator;

    // MQTT Topics
    public static final String X_RIGHT_TOPIC = "/xr";
    public static final String X_LEFT_TOPIC = "/xl";
    public static final String Y_UP_TOPIC = "/yu";
    public static final String Y_DOWN_TOPIC = "/yd";
    public static final String Z_UP_TOPIC = "/zu";
    public static final String Z_DOWN_TOPIC = "/zd";
    public static final String EXTRA_BUTTON_1_TOPIC = "/macros";
    public static final String EXTRA_BUTTON_2_TOPIC = "/autofocusApp";
    public static final String EXTRA_BUTTON_3_TOPIC = "/cameraApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Define orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Connect mqtt service
        connectMQTT();
        // Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Instances UI elements
        buttonXRight = (ImageButton)findViewById(R.id.button_x_right);
        buttonXLeft = (ImageButton)findViewById(R.id.button_x_left);
        buttonYUp = (ImageButton)findViewById(R.id.button_y_up);
        buttonYDown = (ImageButton)findViewById(R.id.button_y_down);
        buttonZUp = (ImageButton)findViewById(R.id.button_z_up);
        buttonZDown = (ImageButton)findViewById(R.id.button_z_down);
        buttonExtra1 = (ImageButton)findViewById(R.id.button_extra_1);
        buttonExtra2 = (ImageButton)findViewById(R.id.button_extra_2);
        buttonExtra3 = (ImageButton)findViewById(R.id.button_extra_3);
        // Initial states
        buttonXRight.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
        buttonXLeft.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
        buttonYUp.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
        buttonYDown.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
        buttonZUp.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
        buttonZDown.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
        // Action callbacks
        buttonXRight.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    publishMessage(X_RIGHT_TOPIC, "1");
                    buttonXRight.setBackground(getResources().getDrawable(R.drawable.pressedbutton));
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                    publishMessage(X_RIGHT_TOPIC, "0");
                    buttonXRight.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
                }
                return true;
            }
        });

        buttonXLeft.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    publishMessage(X_LEFT_TOPIC, "1");
                    buttonXLeft.setBackground(getResources().getDrawable(R.drawable.pressedbutton));
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                    publishMessage(X_LEFT_TOPIC, "0");
                    buttonXLeft.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
                }
                return true;
            }
        });

        buttonYUp.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    publishMessage(Y_UP_TOPIC, "1");
                    buttonYUp.setBackground(getResources().getDrawable(R.drawable.pressedbutton));
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                    publishMessage(Y_UP_TOPIC, "0");
                    buttonYUp.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
                }
                return true;
            }
        });

        buttonYDown.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    publishMessage(Y_DOWN_TOPIC, "1");
                    buttonYDown.setBackground(getResources().getDrawable(R.drawable.pressedbutton));
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                    publishMessage(Y_DOWN_TOPIC, "0");
                    buttonYDown.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
                }
                return true;
            }
        });

        buttonZUp.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    publishMessage(Z_UP_TOPIC, "1");
                    buttonZUp.setBackground(getResources().getDrawable(R.drawable.pressedbutton));
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                    publishMessage(Z_UP_TOPIC, "0");
                    buttonZUp.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
                }
                return true;
            }
        });

        buttonZDown.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    publishMessage(Z_DOWN_TOPIC, "1");
                    buttonZDown.setBackground(getResources().getDrawable(R.drawable.pressedbutton));
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                    publishMessage(Z_DOWN_TOPIC, "0");
                    buttonZDown.setBackground(getResources().getDrawable(R.drawable.notpressedbutton));
                }
                return true;
            }
        });

        // Home
        buttonExtra1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishMessage(EXTRA_BUTTON_1_TOPIC, STAGE_RESTART_HOME);
            }
        });

        // Autofocus
        buttonExtra2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishMessage(EXTRA_BUTTON_2_TOPIC, REQUEST_SERVICE_AUTOFOCUS_MANUAL);
            }
        });

        // Take picture
        buttonExtra3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                String randomCode = "";
                for (int i = 0; i < 5;){
                    int n = rand.nextInt(50) + 1;
                    randomCode += String.valueOf(n);
                }
                publishMessage(EXTRA_BUTTON_3_TOPIC, TAKE_PICTURE_AUTOMATIC + randomCode);
            }
        });

    }

    /** Publish a message
     * @param topic: String that defines the target topic of the mqtt client.
     * @param message: String that contains a message to be published.
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

    /**
     * Message receiver class.
     * */
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

    /** Connect mqtt client to a broker.
     * @return no return */
    public void connectMQTT(){
        MQTTService.NAMESPACE = "rodrigoloza.mqttremotecontroller"; //or BuildConfig.APPLICATION_ID;
        MQTTService.KEEP_ALIVE_INTERVAL = 15; //in seconds
        MQTTService.CONNECT_TIMEOUT = 30; //in seconds
        MQTTService.NAMESPACE = "rodrigoloza.mqttremotecontroller";
        MQTTServiceLogger.setLogLevel(MQTTServiceLogger.LogLevel.DEBUG);
        MQTTServiceCommand.connectAndSubscribe(MainActivity.this,
                                                broker,
                                                clientId,
                                                username,
                                                password,
                                                qos,
                                                true);
    }

}
