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
 * 启动屏
 * from：http://www.devio.org
 * Author:CrazyCodeBoy
 * GitHub:https://github.com/crazycodeboy
 * Email:crazycodeboy@gmail.com
 */
public class SplashScreen {
    private static Dialog mSplashDialog;
    private static WeakReference<Activity> mActivity;
    @SuppressLint("StaticFieldLeak")
    private static VideoView mVideoView;

    /**
     * 打开启动屏
     */
    public static void show(final Activity activity, final int themeResId) {
        if (activity == null) return;
        mActivity = new WeakReference<Activity>(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!activity.isFinishing()) {
                    mSplashDialog = new Dialog(activity, themeResId);
                    mSplashDialog.setContentView(R.layout.launch_screen);
                    mSplashDialog.setCancelable(false);

                    Uri uri = Uri.parse("android.resource://"+activity.getPackageName()+"/"+ R.raw.splash); //Declare your url here.

                    mVideoView = mSplashDialog.findViewById(R.id.video_view);
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

                            //For Center Inside use the Math.min scale.
                            //I prefer Center Inside so I am using Math.min
                            float scale = Math.min(xScale, yScale);
                            mVideoView.setScaleX(1/scale);
                            mVideoView.setScaleY(1/scale);

                            float scaledWidth = scale * videoWidth;
                            float scaledHeight = scale * videoHeight;

                            // Set the new size for the VideoView based on the dimensions of the video
                            ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
                            layoutParams.width = (int)scaledWidth;
                            layoutParams.height = (int)scaledHeight;
                            mVideoView.setLayoutParams(layoutParams);
                        }
                    });

                    mVideoView.requestFocus();
                    mVideoView.seekTo(1);

                    if (!mSplashDialog.isShowing()) {
                        mSplashDialog.show();
                    }

                   mSplashDialog.getWindow().getDecorView().setSystemUiVisibility(
                           View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                       | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                       | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                       | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                       | View.SYSTEM_UI_FLAG_FULLSCREEN
                                       | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
    }

    /**
     * 打开启动屏
     */
    public static void show(final Activity activity, final boolean fullScreen) {
        int resourceId = fullScreen ? R.style.SplashScreen_Fullscreen : R.style.SplashScreen_SplashTheme;

        show(activity, resourceId);
    }

    /**
     * 打开启动屏
     */
    public static void show(final Activity activity) {
        show(activity, true);
    }

    /**
     * 关闭启动屏
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

                    SystemClock.sleep(2200);
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
