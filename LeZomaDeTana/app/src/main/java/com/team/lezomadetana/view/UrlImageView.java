package com.team.lezomadetana.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.team.lezomadetana.activity.BaseActivity;

/**
 * Created by team on 31/08/2018.
 **/

@SuppressLint("AppCompatCustomView")
public class UrlImageView extends ImageView {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private Context mContext;

    public boolean useRoundedBitmap;

    // ===========================================================
    // Constructors
    // ===========================================================

    public UrlImageView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public UrlImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public UrlImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    public void setImageBitmap(Bitmap bm) {

        Bitmap output;
        if (useRoundedBitmap) {
            output = getCircleBitmap(bm);
        } else {
            output = bm;
        }

        super.setImageBitmap(output);
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void setImageUrl(String imageUrl) {
        setImageUrl(imageUrl, null, null, false);
    }

    public void setImageUrl(String imageUrl, ProgressBar imageProgressBar) {
        setImageUrl(imageUrl, null, imageProgressBar, false);
    }

    public void setImageUrl(String imageUrl, ImageLoadingListener imageLoadingListener) {
        setImageUrl(imageUrl, imageLoadingListener, null, false);
    }

    public void setRoundedImageUrl(String imageUrl) {
        setImageUrl(imageUrl, null, null, true);
    }

    public void setImageUrl(String imageUrl, final ImageLoadingListener imageLoadingListener, final ProgressBar imageProgressBar, Boolean rounded) {
        if (imageUrl == null || imageUrl.isEmpty() || this.mContext == null)
            return;

        this.setDefaultValues();

        DisplayImageOptions imageOptions;
        if (rounded) {

            imageOptions = ImageLoader.roundedImageOptionsBuilder
                    .displayer(new RoundedBitmapDisplayer(((int) (this.getMeasuredHeight() / 2))))
                    .build();
        } else {
            imageOptions = ImageLoader.displayImageOptions;
        }

        ImageLoader.imageLoader().displayImage(imageUrl, this, imageOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                startProgressLoading();
                if (imageLoadingListener != null)
                    imageLoadingListener.onLoadingStarted(imageUri, view);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                stopProgressLoading();
                if (imageLoadingListener != null)
                    imageLoadingListener.onLoadingFailed(imageUri, view, failReason);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                setBackgroundColor(Color.TRANSPARENT);
                stopProgressLoading();

                if (imageLoadingListener != null)
                    imageLoadingListener.onLoadingComplete(imageUri, view, loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                stopProgressLoading();

                if (imageLoadingListener != null)
                    imageLoadingListener.onLoadingCancelled(imageUri, view);
            }

            public void startProgressLoading() {
                if (imageProgressBar != null)
                    imageProgressBar.setVisibility(VISIBLE);
            }

            public void stopProgressLoading() {
                if (imageProgressBar != null)
                    imageProgressBar.setVisibility(INVISIBLE);
            }
        });
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void init() {
        setDefaultValues();
    }

    private void setDefaultValues() {
        /*if (!isInEditMode()) {
            this.setBackgroundColor(mContext.getResources().getColor(R.color.background_holo_light));
        }*/
    }

    // Circle shaped bitmap
    private static Bitmap getCircleBitmap(Bitmap bm) {

        int sice = Math.min((bm.getWidth()), (bm.getHeight()));

        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bm, sice, sice);

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xffff0000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 4);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

    private static class ImageLoader {

        private static com.nostra13.universalimageloader.core.ImageLoader imageLoader = null;

        private static com.nostra13.universalimageloader.core.ImageLoader imagePreLoader = null;

        public static DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(500, true, false, false))
                .build();

        public static DisplayImageOptions.Builder roundedImageOptionsBuilder = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888);

        public static com.nostra13.universalimageloader.core.ImageLoader imageLoader() {
            if (imageLoader == null) {
                imageLoader = new ImageLoaderInstance();

                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(BaseActivity._context)
                        .threadPriority(Thread.NORM_PRIORITY - 2)
                        .build();

                imageLoader.init(config);
            }
            return imageLoader;
        }

        public static com.nostra13.universalimageloader.core.ImageLoader imagePreLoader() {
            if (imagePreLoader == null) {
                imagePreLoader = new ImageLoaderInstance();

                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(BaseActivity._context)
                        .threadPriority(Thread.MIN_PRIORITY)
                        .build();

                imagePreLoader.init(config);
            }
            return imagePreLoader;
        }

        public static class ImageLoaderInstance extends com.nostra13.universalimageloader.core.ImageLoader {
            public ImageLoaderInstance() {
                super();
            }
        }
    }
}
