package com.icecodersteam.kiria.postpigeon;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by kiria on 13.06.2016.
 */
public class QueueSingleton {
    public static int instCount = 0;
    private static QueueSingleton instance;
    private static Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private QueueSingleton(Context context){
        instCount++;
        mContext = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

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
    public static synchronized QueueSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new QueueSingleton(context);
        }
        return instance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

}
