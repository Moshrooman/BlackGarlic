package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by JustinKwik on 1/18/16.
 */
public class ConnectionManager {

    private static RequestQueue queue;
    private static ImageLoader imageLoader;

    public static RequestQueue getInstance(Context context) {

        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }

        return queue;
    }

    public static ImageLoader getImageLoader(Context context) {

        if (imageLoader == null) {
            imageLoader = new ImageLoader(getInstance(context), new ImageLoader.ImageCache() {

                private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

                @Override
                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }
            });
        }

        return imageLoader;
    }

}
