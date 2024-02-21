package com.praise.photoalbum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;
import com.yuyakaido.android.cardstackview.internal.CardStackSmoothScroller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Swipe extends AppCompatActivity {


    CardStackView cardStackView;
    private static final String TAG = "Swiper";
    CardStackLayoutManager manager;
    CardStackAdapter adapter;
    ProgressBar progressBar;
    boolean isReady = false;
    boolean isLinkAvailable= false;
    int page , limit = 10;
    List<ItemModel> items = new ArrayList<>();
    List<ItemModel> itemsNew = new ArrayList<>();
    int test = 0;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        cardStackView = findViewById(R.id.card_stack);
        progressBar = findViewById(R.id.progressBar);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_STATUS_ACCURACY_LOW);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
         runtimePermission();



    }
    private void paginate() {
        List<ItemModel> old = adapter.getItems();
        List<ItemModel> baru = new ArrayList<>(addNewList());
        CardStackCallback callback = new CardStackCallback(old, baru);
        DiffUtil.DiffResult hasil = DiffUtil.calculateDiff(callback);
        adapter.setItems(baru);
       hasil.dispatchUpdatesTo(adapter);
       Log.d("list size",""+items.size());


    }
    private List<ItemModel> addList() {
        isReady = false;
        return items;
    }
    private void listAdder(String name,String imgUrl,String id){
        items.add(new ItemModel(name,imgUrl,id));
        if(test > 0 ){
            itemsNew.add(new ItemModel(name,imgUrl,id));
            Log.d("list size","this was called, size: "+itemsNew.size());
        }
    }
    private List<ItemModel> addNewList() {
        return itemsNew;
    }

    public void rewind()
    {
        RewindAnimationSetting settings = new RewindAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Slow.duration)
                .setInterpolator(new DecelerateInterpolator())
                .build();

        manager.setRewindAnimationSetting(settings);
        cardStackView.setLayoutManager(manager);
        cardStackView.rewind();

        // Toast.makeText(getApplicationContext(), "Executed Rewind", Toast.LENGTH_SHORT).show();
    }

    private void getPictures() {
        Random random = new Random();
        int x = random.nextInt(200);
        page = x;
        String sUrl = "https://picsum.photos/v2/list?page=" + page + "&limit=" + limit;
        StringRequest request = new StringRequest(sUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        parseArray(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void parseArray(JSONArray jsonArray) {

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                String url = (object.getString("download_url"));
                String name = (object.getString("author"));
                String id = (object.getString("id"));
                listAdder(name,url,id);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // Log.d("check", daysPicture().get(0));
        isReady = true;
        checkLinks();
    }

    public void checkLinks(){
        if(isReady){
            isLinkAvailable = true;
            progressBar.setVisibility(View.GONE);
             Log.d("check", "link is available");
             showCards();
        }
        else {
            isLinkAvailable = false;
            progressBar.setVisibility(View.VISIBLE);
            Log.d("check", "link is not available");
            getPictures();
        }

    }

    private void showCards() {
        manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {

                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right) {
                    //  Toast.makeText(StepsActivity.this, "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top) {
                    //  Toast.makeText(StepsActivity.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left) {
                    //  Toast.makeText(StepsActivity.this, "Direction Left", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom) {
                    //  Toast.makeText(StepsActivity.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }
                if(isReady = false){
                    getPictures();
                }

                // Paginating
                if (manager.getTopPosition() == adapter.getItemCount()  -1) {
                    // paginate();
                    checkLinks();
                    test = test +1;
                    getPictures();
                    if(isLinkAvailable){
                        paginate();
                    }

                }



            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_day);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_day);
                Log.d(TAG, "onCardDisAppeared: " + position + ", nama: " + tv.getText());
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(addList(),getApplicationContext());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    public void runtimePermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
               checkLinks();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
                if (mAccel > 12) {
//                    SwipeAnimationSetting swipeAnimationSetting = new SwipeAnimationSetting.Builder()
//                            .setDirection(Direction.Right)
//                            .setDuration(Duration.Slow.duration).setInterpolator(new DecelerateInterpolator()).build();
//                    manager.setSwipeAnimationSetting(swipeAnimationSetting);
//                    manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
//                    cardStackView.swipe();
                    rewind();
                Log.d("cardswiper","this supposed to work");
            } else {

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mSensorListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_STATUS_ACCURACY_LOW);
    }
}
