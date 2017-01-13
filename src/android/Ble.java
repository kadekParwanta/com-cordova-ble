package com.cordova.ble;


import android.content.Context;
import android.content.IntentFilter;

import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.OnyxBeaconErrorListener;
import com.onyxbeacon.rest.auth.util.AuthenticationMode;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.ExposedJsApi;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import com.google.gson.Gson;
import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.model.Tag;
import com.onyxbeacon.model.web.BluemixApp;
import com.onyxbeacon.rest.auth.util.AuthenticationMode;

import java.util.ArrayList;


public class Ble extends CordovaPlugin implements OnyxBeaconErrorListener,BleStateListener  {

    private CallbackContext messageChannel;
    // OnyxBeacon SDK
    private OnyxBeaconManager beaconManager;
    private String CONTENT_INTENT_FILTER;
    private String BLE_INTENT_FILTER;
    private ContentReceiver mContentReceiver;
    private BleStateReceiver mBleReceiver;
    private boolean receiverRegistered = false;
    private boolean bleStateRegistered = false;
    public static final String TAG = "Device";
    private          boolean sendfalse;
    Ble instance;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {

        Log.v(TAG, "initialized BLE=" );


        super.initialize(cordova, webView);
        instance = this;
        beaconManager = OnyxBeaconApplication.getOnyxBeaconManager(cordova.getActivity());

        mContentReceiver = ContentReceiver.getInstance(this);
        //Register for BLE events
        mBleReceiver = BleStateReceiver.getInstance();
        mBleReceiver.setBleStateListener(this);

        BLE_INTENT_FILTER = cordova.getActivity().getPackageName() + ".scan";
        cordova.getActivity().registerReceiver(mBleReceiver, new IntentFilter(BLE_INTENT_FILTER));
        bleStateRegistered = true;

        CONTENT_INTENT_FILTER = cordova.getActivity().getPackageName() + ".content";
        cordova.getActivity().registerReceiver(mContentReceiver, new IntentFilter(CONTENT_INTENT_FILTER));
        receiverRegistered = true;
    }
    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArry of arguments for the plugin.
     * @param callbackContext The callback id used when calling back into JavaScript.
     * @return True if the action was valid, false if not.
     */
    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext)   {

        Log.v(TAG, "action=" + action);
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    if (action.equals("initSDK")) {
                        beaconManager.initSDK(AuthenticationMode.CLIENT_SECRET_BASED);
                        callbackContext.success("Success");
                    } else if (action.equals("isBluetoothAvailable")) {
                        Boolean isBluetooth = beaconManager.isBluetoothAvailable();
                        callbackContext.success(isBluetooth ? 1 : 0);
                    } else if (action.equals("enableBluetooth")) {
                        beaconManager.enableBluetooth();
                        callbackContext.success("Success");
                    } else if (action.equals("getTags")) {
                        beaconManager.getTags();
                        callbackContext.success("Success");
                    } else if (action.equals("sendGenericUserProfile")) {
                        beaconManager.sendGenericUserProfile(args.getString(0));
                        callbackContext.success("Success");
                    } else if (action.equals("setForegroundMode")) {
                        beaconManager.setForegroundMode(args.getBoolean(0));
                        callbackContext.success("Success");
                    } else if (action.equals("setBackgroundBetweenScanPeriod")) {
                        beaconManager.setBackgroundBetweenScanPeriod(args.getLong(0));
                        callbackContext.success("Success");
                    } else if (action.equals("startScan")) {
                        beaconManager.startScan();
                        callbackContext.success("Success");
                    } else if (action.equals("stopScan")) {
                        beaconManager.stopScan();
                        callbackContext.success("Success");
                    } else if (action.equals("isInForeground")) {
                        Boolean isInForeground = beaconManager.isInForeground();
                        callbackContext.success(isInForeground ? 1 : 0);
                    } else if (action.equals("isCouponEnabled")) {
                        Boolean isCouponEnabled = beaconManager.isCouponEnabled();
                        callbackContext.success(isCouponEnabled ? 1 : 0);
                    } else if (action.equals("isAPIEnabled")) {
                        Boolean isAPIEnabled = beaconManager.isAPIEnabled();
                        callbackContext.success(isAPIEnabled ? 1 : 0);
                    } else if (action.equals("setCouponEnabled")) {
                        beaconManager.setCouponEnabled(args.getBoolean(0));
                        callbackContext.success("Success");
                    } else if (action.equals("setLocationTrackingEnabled")) {
                        beaconManager.setLocationTrackingEnabled(args.getBoolean(0));
                        callbackContext.success("Success");
                    } else if (action.equals("logOut")) {
                        beaconManager.logOut();
                        callbackContext.success("Success");
                    } else if (action.equals("restartLocationTracking")) {
                        beaconManager.restartLocationTracking();
                        callbackContext.success("Success");
                    } else if (action.equals("enableGeofencing")) {
                        beaconManager.enableGeofencing(args.getBoolean(0));
                        callbackContext.success("Success");
                    } else if (action.equals("setAPIContentEnabled")) {
                        beaconManager.setAPIContentEnabled(args.getBoolean(0));
                        callbackContext.success("Success");
                    } else if (action.equals("sendDeviceToken")) {
                        beaconManager.sendDeviceToken(args.getString(0), args.getString(1));
                        callbackContext.success("Success");
                    } else if (action.equals("setAuthExtraData")) {
                        beaconManager.setAuthExtraData(args.getString(0));
                        callbackContext.success("Success");
                    } else if (action.equals("onPause")) {
                        beaconManager.setForegroundMode(false);
                        // Unregister content receiver

                        if (bleStateRegistered) {
                            cordova.getActivity().unregisterReceiver(mBleReceiver);
                            bleStateRegistered = false;
                        }

                        callbackContext.success("Success");
                    } else if (action.equals("onResume")) {
                        if (mBleReceiver == null) mBleReceiver = BleStateReceiver.getInstance();
                        cordova.getActivity().registerReceiver(mContentReceiver, new IntentFilter(CONTENT_INTENT_FILTER));
                        receiverRegistered = true;

                        if (mContentReceiver == null)
                            mContentReceiver = ContentReceiver.getInstance(instance);
                        cordova.getActivity().registerReceiver(mContentReceiver, new IntentFilter(CONTENT_INTENT_FILTER));
                        receiverRegistered = true;

                        if (beaconManager.isBluetoothAvailable()) {
                            // Enable scanner in foreground mode and register receiver
                            beaconManager.setForegroundMode(true);
                        } else {
                            //Toast.makeText(this, "Please turn on bluetooth", Toast.LENGTH_LONG).show();
                        }
                        callbackContext.success("Success");
                    } else if (action.equals("setTagsFilterForCoupons")) {


                        ArrayList<Tag> tagsFilter = new ArrayList<Tag>();

                        for (int i = 0; i < args.length(); i++) {

                            JSONArray arg = args.getJSONArray(i);
                            Tag rtag = new Tag();
                            rtag.id = arg.getInt(0);
                            rtag.name = arg.getString(1);
                            rtag.state = arg.getString(2);

                            tagsFilter.add(rtag);
                        }
                        beaconManager.setTagsFilterForCoupons(tagsFilter);
                        callbackContext.success("Success");
                    }
        /* Unknown methods */
                    else if (action.equals("sendReport")) {
                        String reporter = args.getString(0);
                        beaconManager.sendLogs(reporter);
                        callbackContext.success("sendReport Invoked");
                    } else {
                        sendfalse = true;
                    }
                } catch (JSONException e) {
                    callbackContext.success(e.getMessage());

                    sendfalse = true;
                }
            }

        });

        if(sendfalse)
            return false;
        return true;
    }

    public void onError(int errorCode, Exception e) {
        String js = String.format(
                "window.cordova.plugins.Ble.onyxBeaconError('%d:%s');",
                errorCode,e.getMessage());
        webView.sendJavascript(js);
    }


    public void onTagsReceived(String tags) {
        String js = String.format(
                "window.cordova.plugins.Ble.onTagsReceived('%s');",
                tags);
        webView.sendJavascript(js);
    }



    public void didRangeBeaconsInRegion( String  beacons) {
        String js = String.format(
                "window.cordova.plugins.Ble.didRangeBeaconsInRegion('%s');",
                beacons);
        webView.sendJavascript(js);
    }


    public void deleteCoupon( long  var1,int var2) {
        String js = String.format(
                "window.cordova.plugins.Ble.deleteCoupon('%d,%d');",
                var1,var2);
        webView.sendJavascript(js);
    }

    public void markAsTapped( long  var1) {
        String js = String.format(
                "window.cordova.plugins.Ble.markAsTapped('%d');",
                var1);
        webView.sendJavascript(js);
    }

    public void markAsOpened( long  var1) {
        String js = String.format(
                "window.cordova.plugins.Ble.markAsOpened('%d');",
                var1);
        webView.sendJavascript(js);
    }


    public void onCouponsReceived(String coupons, String  beacon) {
        String js = String.format(
                "window.cordova.plugins.Ble.onCouponsReceived('%s,%s');",
                coupons,beacon);
        webView.sendJavascript(js);
    }

    public void onBluemixCredentialsReceived( String blueMix ) {
        String js = String.format(
                "window.cordova.plugins.Ble.onBluemixCredentialsReceived('%s');",
                blueMix);
        webView.sendJavascript(js);
    }


    @Override
    public void onBleStackEvent(int event) {
        System.out.println(event);
        switch (event) {
            case 1:
                onError(event,new Exception("Probably your bluetooth stack has crashed. Please restart your bluetooth"));
                break;
            case 2:
                onError(event, new Exception("Beacons with invalid RSSI detected. Please restart your bluetooth."));
                break;

            default:onError(event, new Exception("This Error is unknown"));
                break;

        }
    }





}


