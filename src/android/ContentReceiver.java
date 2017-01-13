package com.cordova.ble;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeacon.listeners.OnyxCouponsListener;
import com.onyxbeacon.listeners.OnyxPushListener;
import com.onyxbeacon.listeners.OnyxTagsListener;
import com.onyxbeacon.model.Tag;
import com.onyxbeacon.model.web.BluemixApp;
import com.onyxbeacon.rest.model.Coupon;
import com.onyxbeaconservice.IBeacon;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Work 2 on 4/2/2015.
 */
public class ContentReceiver extends BroadcastReceiver {


    private static ContentReceiver sInstance;
    static Ble blePlugin;
    private Gson gson = new Gson();

    public ContentReceiver() {}

    public static ContentReceiver getInstance(Ble bp) {
        if(blePlugin == null){
            blePlugin = bp;
        }
        if (sInstance == null) {
            sInstance = new ContentReceiver();
            return sInstance;
        } else {
            return sInstance;
        }
    }

    public void onReceive(Context context, Intent intent) {
        String payloadType = intent.getStringExtra(OnyxBeaconApplication.PAYLOAD_TYPE);

        if (payloadType == OnyxBeaconApplication.TAG_TYPE) {

            ArrayList<Tag> tagsList = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_TAGS);
            blePlugin.onTagsReceived(gson.toJson(tagsList));
        }
        else if (payloadType == OnyxBeaconApplication.BEACON_TYPE) {
                ArrayList<IBeacon> beacons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_BEACONS);
                blePlugin.didRangeBeaconsInRegion(gson.toJson(beacons));
        }
        else if (payloadType == OnyxBeaconApplication.COUPON_TYPE) {

            ArrayList<Coupon> coupons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_COUPONS);
            IBeacon beacon = intent.getParcelableExtra(OnyxBeaconApplication.EXTRA_BEACON);
            blePlugin.onCouponsReceived(gson.toJson(coupons), gson.toJson(beacon));

        }
        else if (payloadType == OnyxBeaconApplication.PUSH_TYPE) {
            BluemixApp bluemixApp = intent.getParcelableExtra(OnyxBeaconApplication.EXTRA_BLUEMIX);
            System.out.println("PUSH Received bluemix credentials " + gson.toJson(bluemixApp));
            blePlugin.onBluemixCredentialsReceived(gson.toJson(bluemixApp));

        }
        else if (payloadType == OnyxBeaconApplication.WEB_REQUEST_TYPE) {
            String extraInfo = intent.getStringExtra(OnyxBeaconApplication.EXTRA_INFO);
            System.out.println("AUTH Web reguest info " + extraInfo);
            if (extraInfo.equals(OnyxBeaconApplication.REQUEST_UNAUTHORIZED)) {
                // Pin based session expired
            }
        }


    }
}
