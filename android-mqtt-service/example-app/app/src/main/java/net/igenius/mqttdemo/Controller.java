package net.igenius.mqttdemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.io.UnsupportedEncodingException;

/**
 * SET A STARTING POINT
 *
 * 1. Use the controller to find a visible field of view
 * 2. Focus point with the controller
 * 3. Press ok
 * */

public class Controller extends AppCompatActivity implements OnShowcaseEventListener {

    /*** Variables and instances */
    /** UI Elements */
    public ImageButton right;
    public ImageButton left;
    public ImageButton up;
    public ImageButton down;
    public ImageButton zUp;
    public ImageButton zDown;
    public Switch switchLed;
    public Button ready;

    /** Showcase */
    public ShowcaseView sv;
    public RelativeLayout.LayoutParams lps;
    public ViewTarget target;

    /** Vibrate */
    public Vibrator vibrator;

    /** Constant strings */
    /** MQTT Topics */
    public static final String X_RIGHT_TOPIC = "/xr";
    public static final String X_LEFT_TOPIC = "/xl";
    public static final String Y_UP_TOPIC = "/yu";
    public static final String Y_DOWN_TOPIC = "/yd";
    public static final String Z_UP_TOPIC = "/zu";
    public static final String Z_DOWN_TOPIC = "/zd";
    public static final String LED_TOPIC = "/led";
    public static final String CAMERA_APP_TOPIC = "/cameraApp";

    /** Constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Content */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        /** Define orientation */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /** Keep screen on */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /** Vibrator */
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        /** Configure showcase */
        lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);
        target = new ViewTarget(R.id.controllerLayout, this);

        /** Instantiate UI Elements */
        right = (ImageButton) findViewById(R.id.right);
        left = (ImageButton) findViewById(R.id.left);
        down = (ImageButton) findViewById(R.id.down);
        up = (ImageButton) findViewById(R.id.up);
        zUp = (ImageButton) findViewById(R.id.zUp);
        zDown = (ImageButton) findViewById(R.id.zDown);
        switchLed = (Switch) findViewById(R.id.switchLed);
        ready = (Button) findViewById(R.id.ready);

        /** Initial states */
        switchLed.setChecked(true);
        /** Showcase first button */
        sv = new ShowcaseView.Builder(Controller.this)
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle("Prepare the sample")
                .setContentText("Press the button to take the stage out")
                .setStyle(R.style.CustomShowcaseTheme3)
                .setShowcaseEventListener(Controller.this)
                .replaceEndButton(R.layout.view_custom_button)
                .build();
        sv.setButtonPosition(lps);

        /** Backgrounds */
        right.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        left.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        down.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        up.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        zDown.setBackground(getResources().getDrawable(R.drawable.curvebutton));
        zUp.setBackground(getResources().getDrawable(R.drawable.curvebutton));

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

        ready.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                publishMessage(CAMERA_APP_TOPIC, "start");
                /** Compute automatic */
                Intent intent = new Intent(Controller.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    /** Support methods */
    /** Show message
     * @param message: input String that contains the message to be displayed
     * */
    public void showToast(String message){
        Toast.makeText(Controller.this, message, Toast.LENGTH_SHORT).show();
    }

    /** Publish a message
     * @param topic: input String that defines the target topic of the mqtt client
     * @param message: input String that contains a message to be published
     * @return no return
     * */
    public void publishMessage(String topic, String message) {
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = message.getBytes("UTF-8");
            MQTTServiceCommand.publish(Controller.this, topic, encodedPayload, 2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Instance of class MQTTReceiver
     * This class handles the mqtt receiving callbacks and its subscriptions and publishing results
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

    /** Showcase callbacks */
    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

    }

}
