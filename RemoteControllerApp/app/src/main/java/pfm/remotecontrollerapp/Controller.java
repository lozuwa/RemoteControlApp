package pfm.remotecontrollerapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Controller extends AppCompatActivity {
    /*** UI Elements */
    /** Controller*/
    public ImageButton zup;
    public ImageButton zdown;
    public ImageButton XYController;

    public static final String Z_UP_TOPIC = "/zu";
    public static final String Z_DOWN_TOPIC = "/zd";

    /** Force landscape orientation */
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        /** Instance UI */
        zup = (ImageButton)findViewById(R.id.zUp);
        zdown = (ImageButton)findViewById(R.id.zDown);
        XYController = (ImageButton) findViewById(R.id.XYController);

        zup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    zup.setBackgroundColor(Color.GREEN);
                    String payload = "1";
                    //publish_message(Z_UP_TOPIC, payload);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    zup.setBackgroundColor(Color.BLUE);
                    String payload = "2";
                    //publish_message(Z_UP_TOPIC, payload);
                }
                return true;
            }
        });

        zdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    zdown.setBackgroundColor(Color.GREEN);
                    String payload = "1";
                    //publish_message(Z_DOWN_TOPIC, payload);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    zdown.setBackgroundColor(Color.BLUE);
                    String payload = "2";
                    //publish_message(Z_DOWN_TOPIC, payload);
                }
                return true;
            }
        });

        XYController.setOnTouchListener(new OnSwipeTouchListener(Controller.this) {
            public void onSwipeTop() {
                Toast.makeText(Controller.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Toast.makeText(Controller.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(Controller.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(Controller.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });
        
    }

    

}
