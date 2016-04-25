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

    public static RequestQueue getInstance(Context context) {

        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }

        return queue;
    }

}
