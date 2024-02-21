package com.praise.photoalbum;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.praise.photoalbum.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    RecyclerView.LayoutManager layoutManager;
    ArrayList<Picture> pictureArrayList;
    ActivityMainBinding binding;
    int page , limit = 12;
Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        button = findViewById(R.id.next);

        layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        binding.recycler.setLayoutManager(layoutManager);

        getPictures();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Swipe.class));
            }
        });

        /// imagefilter view
        //saturation = 0; grayscale
        //convert to motionlayout

    }


    private void getPictures() {
        Random random = new Random();
        int x = random.nextInt(100);
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
        pictureArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                String url = (object.getString("download_url"));
                String name = (object.getString("author"));
                pictureArrayList.add(new Picture(name,url));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        CustomAdapter customAdapter= new CustomAdapter(pictureArrayList,MainActivity.this);
        binding.recycler.setAdapter(customAdapter);


    }

}