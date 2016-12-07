package delivery.grazzy.app;

/**
 * Created by developer.nithin@gmail.com
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSListener extends BroadcastReceiver
{

    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Retrieves a map of extended data from the intent.

        Log.e("SmsReceiver", "onReceive");

        if(AppController.getInstance().check_sms)
        {
            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String message = currentMessage.getDisplayMessageBody();

                        if(phoneNumber.equals("MD-BIGPER") && message.equals("Verification Code is "+ AppController.getInstance().verification_code) )
                        {
                            AppController.getInstance().verification = true;
                            AppController.getInstance().set_login(true);
                            Log.e("onReceive", "Verified");

                        }

                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" +e);

            }
        }

    }



}