package pfm.remotecontrollerapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class Controller extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    /*** UI Elements and variables */
    /** Controller*/
    public ImageButton zup;
    public ImageButton zdown;
    public ImageButton XYController;
    /** parasite spinner */
    public Spinner spinnerParasite;
    /** Led switch */
    public Switch switchLed;
    /** Constant variables */
    public static final String Z_UP_TOPIC = "/zu";
    public static final String Z_DOWN_TOPIC = "/zd";
    public static final String LED_TOPIC = "/led";

    /** Force landscape orientation */
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        /*** Receive message */
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        /*** Instance UI */
        /** Controller */
        zup = (ImageButton)findViewById(R.id.zUp);
        zdown = (ImageButton)findViewById(R.id.zDown);
        XYController = (ImageButton) findViewById(R.id.XYController);
        /** Spinner parasite */
        spinnerParasite  = (Spinner) findViewById(R.id.spinnerParasite);
        spinnerParasite.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.parasite_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParasite.setAdapter(adapter);
        /** Led switch */
        switchLed = (Switch) findViewById(R.id.switchLed);

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

        switchLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //publish_message(LED_TOPIC, "1");
                }
                else {
                    //publish_message(LED_TOPIC, "0");
                }
            }
        });

    }

    /*** Callback spinners */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        //selected_field = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /*** Support methods */
    public void showToast(String message){
        Toast.makeText(Controller.this, message, Toast.LENGTH_SHORT).show();
    }

}
