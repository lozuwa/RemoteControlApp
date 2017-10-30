package net.igenius.mqttdemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.igenius.mqttservice.MQTTService;
import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceLogger;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.util.Date;
import java.util.UUID;

/**
 * Created by HP on 20/10/2017.
 */

public class Initializer extends Application {

    /**
     * MQTT Variables
     * */
    public String BROKER = "tcp://192.168.0.103:1883";
    public String username = "pfm";
    public String password = "161154029";
    public String clientId = UUID.randomUUID().toString();
    public int qos = 2;

    /**
     * MQTT Topics
     * */
    static public String MICROSCOPE_TOPIC = "/microscope";
    static public String CAMERA_APP_TOPIC = "/cameraApp";
    static public String AUTOFOCUS_APP_TOPIC = "/autofocusApp";
    static public String REMOTE_CONTROLLER_TOPIC = "/remoteController";
    static public String MACROS_TOPIC = "/macros";

    /**
     * MQTT Messages
     * */
    // /microscope
    public static String MOVE_X_RIGHT_FIELD = "move;x;right;field;1";
    public static String MOVE_X_LEFT_FIELD = "move;x;left;field;1";
    public static String MOVE_X_RIGHT_PROCESS_START = "move;x;right;process;1";
    public static String MOVE_X_LEFT_PROCESS_START = "move;x;left;process;1";
    public static String MOVE_X_RIGHT_PROCESS_END = "move;x;right;process;0";
    public static String MOVE_X_LEFT_PROCESS_END = "move;x;left;process;0";

    public static String MOVE_Y_UP_FIELD = "move;y;up;field;1";
    public static String MOVE_Y_DOWN_FIELD = "move;y;down;field;1";
    public static String MOVE_Y_UP_PROCESS_START = "move;y;up;process;1";
    public static String MOVE_Y_DOWN_PROCESS_START = "move;y;down;process;1";
    public static String MOVE_Y_UP_PROCESS_END = "move;y;up;process;0";
    public static String MOVE_Y_DOWN_PROCESS_END = "move;y;down;process;0";

    public static String MOVE_Z_UP_FIELD = "move;z;up;field;1";
    public static String MOVE_Z_DOWN_FIELD = "move;z;down;field;1";
    public static String MOVE_Z_UP_PROCESS_START = "move;z;up;process;1";
    public static String MOVE_Z_DOWN_PROCESS_START = "move;z;down;process;1";
    public static String MOVE_Z_UP_PROCESS_END = "move;z;up;process;0";
    public static String MOVE_Z_DOWN_PROCESS_END = "move;z;down;process;0";

    public static String HOME_X = "home;x;None;None;None";
    public static String HOME_Y = "home;y;None;None;None";
    public static String HOME_Z_TOP = "home;z;top;None;None";
    public static String HOME_Z_BOTTOM = "home;z;bottom;None;None";

    // /macros
    public static String STAGE_RESTART_HOME = "stage;restart;home;None;None";

    // /cameraApp
    public static String TAKE_PICTURE_FROM_REMOTE_CONTROLLER = "takePictureRemoteController;None;None;None;";
    public static String EXIT_ACTIVITY_CREATE_PATIENT = "exit;ManualController;CreatePatient;None;None";

    public static String REQUEST_SERVICE_AUTOFOCUS = "requestService;autofocus;ManualController;None;None";

    @Override
    public void onCreate(){
        super.onCreate();
        /** Initialize variables for MQTT Service */
        MQTTService.NAMESPACE = "com.example.android.camera2basic"; //or BuildConfig.APPLICATION_ID;
        MQTTService.KEEP_ALIVE_INTERVAL = 15; //in seconds
        MQTTService.CONNECT_TIMEOUT = 30; //in seconds
        /** MQTT */
        MQTTService.NAMESPACE = "net.igenius.mqttdemo";
        MQTTServiceLogger.setLogLevel(MQTTServiceLogger.LogLevel.DEBUG);
        MQTTServiceCommand.connectAndSubscribe(Initializer.this,
                                                BROKER,
                                                clientId,
                                                username,
                                                password,
                                                qos,
                                                true,
                                                REMOTE_CONTROLLER_TOPIC);
    }

}
