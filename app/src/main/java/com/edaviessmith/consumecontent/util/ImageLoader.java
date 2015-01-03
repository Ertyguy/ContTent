package com.edaviessmith.consumecontent.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static String TAG = "ImageLoader";
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    final int buffer_size = 1024;

    public ImageLoader(Context context){
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    public void DisplayImage(String url, ImageView imageView) {
        DisplayImage(url, imageView, null, true);
    }

    public void DisplayImage(String url, ImageView imageView, ProgressBar progressBar) {
        DisplayImage(url, imageView, progressBar, true);
    }

    public void DisplayImage(String url, ImageView imageView, ProgressBar progressBar, boolean hideImage) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
            if(progressBar != null) progressBar.setVisibility(View.GONE);
        } else {
            if(progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if(hideImage) imageView.setImageResource(android.R.color.transparent);

            PhotoToLoad p = new PhotoToLoad(url, imageView, progressBar);
            executorService.submit(new PhotosLoader(p));
        }
    }

    public void DisplayImage(Listener listener, String url, ImageView imageView, ProgressBar progressBar, boolean hideImage) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
            if(progressBar != null) progressBar.setVisibility(View.GONE);
        } else {
            if(progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if(hideImage) imageView.setImageResource(android.R.color.transparent);

            PhotoToLoad p = new PhotoToLoad(url, imageView, progressBar);
            executorService.submit(new PhotosLoader(p, listener));
        }
    }
         

     
    public Bitmap getBitmap(String url) throws FileNotFoundException {
        File f = fileCache.getFile(url);
         
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b != null) return b;
         
        //from web
        try {
            Bitmap bitmap;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = new FlushedInputStream(conn.getInputStream());
            OutputStream os = new FileOutputStream(f);

            try {
                byte[] bytes = new byte[buffer_size];
                for (; ; ) {
                    int count = is.read(bytes, 0, buffer_size);
                    if (count == -1)
                        break;
                    os.write(bytes, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            os.close();
            is.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (FileNotFoundException ex) {
            throw ex;   //Throw exception when the file cannot be found
        } catch (Throwable ex){

           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }
 
    //decodes thumbnail and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
       // try {
    	Bitmap bitmap = null;
    	if(f.exists()) {
        	try {
        		final int IMAGE_MAX_SIZE = 600000; // 1.2MP
        		
	        	//Scale bitmap to maximum size first
	            BitmapFactory.Options options = new BitmapFactory.Options();
	            options.inJustDecodeBounds = true;
	            InputStream inputStream = null;	            
	            inputStream = new FileInputStream(f);
	            BitmapFactory.decodeStream(new FlushedInputStream(inputStream), null, options);
	            inputStream.close();
	            // here w and h are the desired width and height
	
	            int scale = 1;
	            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >  IMAGE_MAX_SIZE) {
	               scale++;
	            }
	            
	            inputStream = new FileInputStream(f);
	            if (scale > 1) {
	                scale--;
	                // scale to max possible inSampleSize that still yields an thumbnail
	                // larger than target
	                options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
	                options.inSampleSize = scale;
	                bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream), null, options);
	
	                // resize to desired dimensions
	                int height = bitmap.getHeight();
	                int width = bitmap.getWidth();
	                //Log.d(TAG, "1th scale operation dimenions - width: " + width + ",height: " + height);
	
	                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
	                double x = (y / height) * width;
	
	                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) x, (int) y, true);
	                //bitmap.recycle();
	                bitmap = scaledBitmap;
	
	                System.gc();
	            } else {
	            	bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
	            }
        	
        	} catch (IOException e) {
        	    Log.e(TAG, e.getMessage(),e);
        	    return null;
        	} catch (Throwable ex) {
                ex.printStackTrace();
                if (ex instanceof OutOfMemoryError)
                    clearCache();
                return null;
            }
    	}
        return bitmap;
    }
     
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public ProgressBar progressBar;

        public PhotoToLoad(String u, ImageView i, ProgressBar p){
            url = u;
            imageView = i;
            progressBar = p;
        }
    }
     
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        Listener listener;

        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad = photoToLoad;
        }

        PhotosLoader(PhotoToLoad photoToLoad, Listener listener){
            this.photoToLoad = photoToLoad;
            this.listener = listener;
        }
         
        @Override
        public void run() {
            if(imageViewReused(photoToLoad))  return;
            Bitmap bmp = null;
            try {
                bmp = getBitmap(photoToLoad.url);
            } catch(FileNotFoundException ex) {
                //TODO: Update the db record
                listener.onError("");
            }
            if(bmp == null) return;
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad)) return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
     
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag = imageViews.get(photoToLoad.imageView);
        if(tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
     
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){
        	bitmap = b;
        	photoToLoad = p;
        }
        
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageDrawable(null);
            if(photoToLoad.progressBar != null)
            photoToLoad.progressBar.setVisibility(View.GONE);
        }
    }
 
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
 
    
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}