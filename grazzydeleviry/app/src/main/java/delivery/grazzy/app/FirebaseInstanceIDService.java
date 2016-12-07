package delivery.grazzy.app;

/**
 * Created by developer.nithin@gmail.com
 */
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by developer.nithin@gmail.com
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        AppController.getInstance().token_value= FirebaseInstanceId.getInstance().getToken();
        Log.e("new did", AppController.getInstance().token_value);
        AppController.getInstance().token=true;
        AppController.getInstance().register_user();


    }

}
