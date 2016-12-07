package delivery.grazzy.app;

/**
 * Created by developer.nithin@gmail.com
 */
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by developer.nithin@gmail.com
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;
        return cacheSize;
    }

    public LruBitmapCache() {
        // TODO Auto-generated constructor stub
        this(getDefaultLruCacheSize());
    }

    public LruBitmapCache(int maxSize) {
        super(maxSize);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        // TODO Auto-generated method stub
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        // TODO Auto-generated method stub

        put(url, bitmap);


    }

}