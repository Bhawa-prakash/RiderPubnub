package com.e.rider;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        PNConfiguration pnc = new PNConfiguration()
                .setSubscribeKey("sub-c-cb8aa4e2-55f5-11e9-93f3-8ed1bbcba485");
        PubNub pubnub = new PubNub(pnc);
        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, final PNMessageResult message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!TextUtils.isEmpty(message.getMessage().toString())){
                            try {
                                JSONObject jsonObject = new JSONObject(message.getMessage().toString());
                                double lat = Double.parseDouble(jsonObject.optString("latt"));
                                double longitute = Double.parseDouble(jsonObject.optString("long"));
                                LatLng sydney = new LatLng(lat,longitute);
                                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        Toast.makeText(MapsActivity.this, "punbnub data" + message.getMessage().toString(), Toast.LENGTH_LONG).show();


                    }
                });
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
        pubnub.subscribe()
                .channels(Arrays.asList("Rider_channel"))
                .execute();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }
}
