package org.devio.rn.splashscreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import java.lang.ref.WeakReference;

/**
 * SplashScreen
 * GitHub:https://github.com/jp928
 * Email:piaojingtai@gmail.com
 */
public class SplashScreen {
    private static Dialog mSplashDialog;
    private static WeakReference<Activity> mActivity;
    @SuppressLint("StaticFieldLeak")
    private static VideoView mVideoView;

    /**
     * Open splash
     */
    public static void show(final Activity activity, final int themeResId) {
        if (activity == null) return;
        mActivity = new WeakReference<Activity>(activity);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!activity.isFinishing()) {
                    mSplashDialog = new Dialog(activity, themeResId);
                    mSplashDialog.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    mSplashDialog.setContentView(R.layout.launch_screen);
                    mSplashDialog.setCancelable(false);
                    mVideoView = mSplashDialog.findViewById(R.id.video_view);
                    Uri uri = Uri.parse("android.resource://"+mActivity.get().getPackageName()+"/"+ R.raw.splash);
                    mVideoView.setVideoURI(uri);

                    mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            //Get your video's width and height
                            int videoWidth = mp.getVideoWidth();
                            int videoHeight = mp.getVideoHeight();

                            int videoViewWidth = mVideoView.getWidth();
                            int videoViewHeight = mVideoView.getHeight();

                            float xScale = (float) videoViewWidth / videoWidth;
                            float yScale = (float) videoViewHeight / videoHeight;
                            
                            // Set the new size for the VideoView based on the dimensions of the video
                            ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
                            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

                            mVideoView.setLayoutParams(layoutParams);

                            mVideoView.setScaleX(yScale/xScale);

                        }
                    });

                    mVideoView.requestFocus();
                    mVideoView.seekTo(1);

                    if (!mSplashDialog.isShowing()) {
                        mSplashDialog.show();
                    }
                }
            }
        });
    }

    /**
     * Open splash
     */
    public static void show(final Activity activity, final boolean fullScreen) {
        int resourceId = fullScreen ? R.style.SplashScreen_Fullscreen : R.style.SplashScreen_SplashTheme;

        show(activity, resourceId);
    }

    /**
     * Open splash
     */
    public static void show(final Activity activity) {
        show(activity, true);
    }

    /**
     * Hide splash
     */
    public static void hide(Activity activity) {
        if (activity == null) {
            if (mActivity == null) {
                return;
            }
            activity = mActivity.get();
        }

        if (activity == null) return;

        final Activity _activity = activity;

        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSplashDialog != null && mSplashDialog.isShowing()) {
                    mVideoView.start();

                    SystemClock.sleep(2500);
                    boolean isDestroyed = false;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        isDestroyed = _activity.isDestroyed();
                    }

                    if (!_activity.isFinishing() && !isDestroyed) {
                        mSplashDialog.dismiss();
                    }
                    mSplashDialog = null;
                }
            }
        });
    }
}
