package id.blackgarlic.blackgarlic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PDFWebView extends AppCompatActivity {

    private static WebView pdfWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfweb_view);

        final View loadingEmptyView = findViewById(R.id.loadingEmptyView);
        loadingEmptyView.bringToFront();

        final ProgressBar webViewProgressBar = (ProgressBar) findViewById(R.id.webViewProgressBar);
        webViewProgressBar.bringToFront();

        final TextView percentageWebViewLoading = (TextView) findViewById(R.id.percentageWebViewLoading);
        percentageWebViewLoading.bringToFront();

        String menuId = getIntent().getExtras().getString("menu_id", "");
        Log.e("menu_id", menuId);
        pdfWebView = (WebView) findViewById(R.id.pdfWebView);

        String pdfUrl = "http://bgpdf.kilatstorage.com/menu_id.pdf";
        String googleDocPdfViewerUrl = "http://docs.google.com/gview?embedded=true&url=";

        WebSettings webSettings = pdfWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);

        pdfWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 90) {

                    Animation fadeOutAnimation = AnimationFactory.fadeOutAnimation(1000, 0);

                    loadingEmptyView.startAnimation(fadeOutAnimation);

                    percentageWebViewLoading.setVisibility(View.GONE);
                    webViewProgressBar.setVisibility(View.GONE);

                    fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            loadingEmptyView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
                percentageWebViewLoading.setText("Loading... " + String.valueOf(newProgress) + "%");

            }

        });


        pdfWebView.loadUrl(googleDocPdfViewerUrl + pdfUrl.replace("menu_id", menuId));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && (pdfWebView.canGoBack())) {
            pdfWebView.goBack();
            return true;
        } else {
            finish();
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
