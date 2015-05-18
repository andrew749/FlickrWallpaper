package com.andrew749.flickrwallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.SurfaceHolder;

import java.util.ArrayList;

/**
 * Created by andrewcodispoti on 2015-05-10.
 */
public class WallpaperService extends android.service.wallpaper.WallpaperService {
    int x, y;
    private Paint paint = new Paint();

    @Override
    public Engine onCreateEngine() {
        return new PhotoEngine();
    }

    class PhotoEngine extends Engine {
        private final Handler handler = new Handler();
        ArrayList<Bitmap> images = new ArrayList<Bitmap>();
        Bitmap currentImage;
        LocalStorage storage;
        int index = 0;
        private boolean visible = true;
        private final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (index > images.size()) index = 0;
                //gets the next image
                currentImage = images.get(index++);
                draw(currentImage);
                if (images.size() - index < 2) {
                    //get more images
                    images.addAll(storage.getImages());
                }
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            paint.setColor(Color.BLUE);
            LocalStorage storage = new LocalStorage(getApplicationContext());
            images = storage.getImages();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            // if screen wallpaper is visible then draw the image otherwise do not draw
            if (visible) {
                handler.postDelayed(runnable, 1000);
            } else {
                handler.removeCallbacks(runnable);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(runnable);
        }

        //draws the supplied image to the canvas
        void draw(Bitmap bm) {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                // clear the canvas
                c.drawColor(Color.BLACK);
                c.drawBitmap(bm, 0, 0, paint);
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }

            handler.removeCallbacks(runnable);
            if (visible) {
                handler.postDelayed(runnable, 10); // delay 10 mileseconds
            }

        }

    }
}
