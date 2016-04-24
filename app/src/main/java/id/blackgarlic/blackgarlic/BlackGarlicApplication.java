package id.blackgarlic.blackgarlic;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by JustinKwik on 1/20/16.
 */
public class BlackGarlicApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        Fresco.initialize(BlackGarlicApplication.this);
    }

}
