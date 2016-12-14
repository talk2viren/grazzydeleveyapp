package delivery.grazzy.app;

/**
 * Created by developer.nithin@gmail.com
 */
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by hp on 29/9/2016.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("onMessageReceived",remoteMessage.toString());

        AppController.getInstance().create_notification();

    }


}
