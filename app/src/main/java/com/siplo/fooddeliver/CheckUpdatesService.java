package com.siplo.fooddeliver;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CheckUpdatesService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.enet.smartrestaurent.action.FOO";
    private static final String ACTION_BAZ = "com.enet.smartrestaurent.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.enet.smartrestaurent.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.enet.smartrestaurent.extra.PARAM2";

    private static Intent intent;

    public static MQTTClient mqttClient=null;

    public CheckUpdatesService() {
        super("CheckUpdatesService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionUpdateCheck(Context context) {
        intent = new Intent(context, CheckUpdatesService.class);
        intent.setAction(ACTION_FOO);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);

    }

    public static void stopActionUpdateCheck(Context context) {
        context.stopService(intent);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionCheckUpdates();
            } else if (ACTION_BAZ.equals(action)) {

            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCheckUpdates() {
//        while (true) {
//            CommunicationService.sendToServer(this, "updates", Constants.CHECKUPDATES_ACTION);
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        try {
            Log.d("checkupdateservice","subscribing");
            mqttClient = new MQTTClient();

            mqttClient.initializeMQTTClient(this.getBaseContext(), "food_deliver_agent:subscribe:1", false, false, null, null);
            mqttClient.subscribe(Constants.NEW_ORDER_TOPIC+GlobalState.getCurrentUsername(),"test_food_delivery_agent_app",2);

            mqttClient.disconnect();
        } catch (Throwable throwable) {
            Log.d("mqtt","exception on subscribe");
            throwable.printStackTrace();
        }
    }






}
