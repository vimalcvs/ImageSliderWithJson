package com.vimalcvs.imagesliderwithjson.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import com.vimalcvs.imagesliderwithjson.R;


public class ImageSliderAdapter extends PagerAdapter {

    private Context context;
    private List<SliderUtils> sliderImg;


    public ImageSliderAdapter(List sliderImg, Context context) {
        this.sliderImg = sliderImg;
        this.context = context;
    }

    @Override
    public int getCount() {
        return sliderImg.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);

        SliderUtils utils = sliderImg.get(position);

        ImageView imageView = view.findViewById(R.id.imageView);


        final ProgressBar progressBar  = view.findViewById(R.id.progress_circular);

        Glide.with(imageView.getContext())
                .load(utils.getSliderImageUrl())
                .placeholder(R.drawable.ic_launcher_round)
                .transform(new CenterCrop(),new RoundedCorners(10))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position == 0){
                    Toast.makeText(context, "Slide 1 Clicked", Toast.LENGTH_SHORT).show();
                } else if(position == 1){
                    Toast.makeText(context, "Slide 2 Clicked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Slide 3 Clicked", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }

    public static class SliderUtils {

        String sliderImageUrl;

        public String getSliderImageUrl() {
            return sliderImageUrl;
        }

        public void setSliderImageUrl(String sliderImageUrl) {
            this.sliderImageUrl = sliderImageUrl;
        }
    }

    public static class CustomVolleyRequest {

        private static CustomVolleyRequest customVolleyRequest;
        private static Context context;
        private RequestQueue requestQueue;
        private ImageLoader imageLoader;

        private CustomVolleyRequest(Context context){

            this.context = context;
            this.requestQueue = getRequestQueue();

            imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {

                private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });

        }

        public static synchronized CustomVolleyRequest getInstance(Context context){

            if(customVolleyRequest == null){
                customVolleyRequest = new CustomVolleyRequest(context);
            }
            return customVolleyRequest;
        }

        public RequestQueue getRequestQueue(){

            if(requestQueue == null){
                Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
                Network network = new BasicNetwork(new HurlStack());
                requestQueue = new RequestQueue(cache, network);
                requestQueue.start();

            }
            return requestQueue;
        }

        public  void addToRequestQueue(Request req) {
            getRequestQueue().add(req);
        }

        public ImageLoader getImageLoader(){

            return imageLoader;

        }

    }
}