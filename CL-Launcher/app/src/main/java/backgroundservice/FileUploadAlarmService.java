package backgroundservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by IMFCORP\alok.acharya on 7/3/17.
 */

public class FileUploadAlarmService extends WakefulBroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;


    @Override
    public void onReceive(Context context, Intent intent) {
        // BEGIN_INCLUDE(alarm_onreceive)
        /*
         * If your receiver intent includes extras that need to be passed along to the
         * service, use setComponent() to indicate that the service should handle the
         * receiver's intent. For example:
         *
         * ComponentName comp = new ComponentName(context.getPackageName(),
         *      MyService.class.getName());
         *
         * // This intent passed in this call will include the wake lock extra as well as
         * // the receiver intent contents.
         * startWakefulService(context, (intent.setComponent(comp)));
         *
         * In this example, we simply create a new intent to deliver to the service.
         * This intent holds an extra identifying the wake lock.
         */
        Log.i("FileUpload", "allarm calling");
        Intent service = new Intent(context, FileUploadingIntentService.class);
        service.setAction(FileUploadingIntentService.ACTION_FILE_COPY);

        // Start the service, keeping the device awake while it is launching.
         startWakefulService(context, service);
        // END_INCLUDE(alarm_onreceive)
    }

    // BEGIN_INCLUDE(set_alarm)

    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     *
     * @param context
     */
    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, FileUploadAlarmService.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Set the alarm's trigger time to 9:00 a.m.
        calendar.set(Calendar.HOUR_OF_DAY,9);
        calendar.set(Calendar.MINUTE, 00);

        /*
         * If you don't have precise time requirements, use an inexact repeating alarm
         * the minimize the drain on the device battery.
         *
         * The call below specifies the alarm type, the trigger time, the interval at
         * which the alarm is fired, and the alarm's associated PendingIntent.
         * It uses the alarm type RTC_WAKEUP ("Real Time Clock" wake up), which wakes up
         * the device and triggers the alarm according to the time of the device's clock.
         *
         * Alternatively, you can use the alarm type ELAPSED_REALTIME_WAKEUP to trigger
         * an alarm based on how much time has elapsed since the device was booted. This
         * is the preferred choice if your alarm is based on elapsed time--for example, if
         * you simply want your alarm to fire every 60 minutes. You only need to use
         * RTC_WAKEUP if you want your alarm to fire at a particular date/time. Remember
         * that clock-based time may not translate well to other locales, and that your
         * app's behavior could be affected by the user changing the device's time setting.
         *
         * Here are some examples of ELAPSED_REALTIME_WAKEUP:
         *
         * // Wake up the device to fire a one-time alarm in one minute.
         * alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
         *         SystemClock.elapsedRealtime() +
         *         60*1000, alarmIntent);
         *
         * // Wake up the device to fire the alarm in 30 minutes, and every 30 minutes
         * // after that.
         * alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
         *         AlarmManager.INTERVAL_HALF_HOUR,
         *         AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
         */

       // long repeatTime = AlarmManager.INTERVAL_HOUR;
        //repeatTime 4hrs
        long repeatTime = 14400000;
        // Set the alarm to fire at approximately 8:30 a.m., according to the device's
        // clock, and to repeat once a day.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), repeatTime, alarmIntent);

        //   alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        //          AlarmManager.INTERVAL_DAY, alarmIntent);

        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, AppBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(set_alarm)

    /**
     * Cancels the alarm.
     *
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }

        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, AppBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
// END_INCLUDE(cancel_alarm)
}
