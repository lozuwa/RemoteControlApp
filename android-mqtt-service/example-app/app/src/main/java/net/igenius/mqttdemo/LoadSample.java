package net.igenius.mqttdemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.io.UnsupportedEncodingException;

/**
 * PREPARE SAMPLE
 * */

public class LoadSample extends AppCompatActivity implements OnShowcaseEventListener {

    /** UI Elements */
    public Button placeSample;
    public Button ready;

    /** Showcase */
    public ShowcaseView sv;
    public RelativeLayout.LayoutParams lps;
    public ViewTarget target;
    public ViewTarget target2;

    /** Constants */
    public static String EXTRA_ACTIONS_TOPIC = "/extra";
    public static String X_RIGHT_TOPIC = "/xr";
    public static String X_LEFT_TOPIC = "/xl";
    public static String Y_UP_TOPIC = "/yu";

    public static String Y_DOWN_TOPIC = "/yd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Content */
        setContentView(R.layout.activity_load_sample);

        /** Keep screen turned on */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /** Initialize buttons */
        placeSample = (Button) findViewById(R.id.placeSample);
        ready  = (Button) findViewById(R.id.ready);

        /** Configure showcase */
        lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                            ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        target = new ViewTarget(R.id.placeSample, this);
        target2 = new ViewTarget (R.id.ready, this);

        /** Show first button */
        sv = new ShowcaseView.Builder(LoadSample.this)
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle("Prepare the sample")
                .setContentText("Press the button to take the stage out")
                .setStyle(R.style.CustomShowcaseTheme3)
                .setShowcaseEventListener(LoadSample.this)
                .replaceEndButton(R.layout.view_custom_button)
                .build();
        sv.setButtonPosition(lps);

        /** Callbacks */
        placeSample.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                /** 1. Reset XY */
                publishMessage(EXTRA_ACTIONS_TOPIC, "home;None");
                /** 2. Remove stage and load sample in marked positions */
                /** 3. Place platina */
                sv = new ShowcaseView.Builder(LoadSample.this)
                        .withMaterialShowcase()
                        .setTarget(target2)
                        .setContentTitle("Load sample")
                        .setContentText("Press the button when the sample is prepared on the stage")
                        .setStyle(R.style.CustomShowcaseTheme3)
                        .setShowcaseEventListener(LoadSample.this)
                        .replaceEndButton(R.layout.view_custom_button)
                        .build();
                sv.setButtonPosition(lps);
            }
        });

        ready.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                /** 4. Press ok */
                /** 5. Place XY in (15, 40) */
                publishMessage(EXTRA_ACTIONS_TOPIC, "startPosition;None");
                Intent intent = new Intent(LoadSample.this, Controller.class);
                startActivity(intent);
            }
        });

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
            MQTTServiceCommand.publish(LoadSample.this, topic, encodedPayload, 2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

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
