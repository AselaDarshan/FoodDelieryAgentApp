package com.siplo.fooddeliver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActiveOrdersActivity extends AppCompatActivity {
    private RVAdapter adapter;
    private List<ActiveOrder> activeOrders;
    private List<ActiveOrder> activeOrderList;

    private Receiver receiver = new Receiver();
    private int printOrderId = -1;

    //protected Button deliverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_orders);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=#8B0000>Active Orders</font>"));


//        activeOrders = ActiveOrder.findWithQuery(ActiveOrder.class, "SELECT * from ACTIVE_ORDER where IS_COMPLETED=0");
//        activeOrderList = new ArrayList<>();
//
//
//
//        for (ActiveOrder order:activeOrders){
//
//            activeOrderList.add(order);
//
//            List<ActiveOrderItem> activeOrderItems = ActiveOrderItem.findWithQuery(ActiveOrderItem.class, "SELECT * from ACTIVE_ORDER_ITEM where TABLE_ID=" +order.tableId);
//            order.setItemList(activeOrderItems);
//        }


//        adapter = new RVAdapter(this, activeOrderList);
//        rv.setAdapter(adapter);
        retrieveOrderList();


        IntentFilter mqttIntentFilter = new IntentFilter(Constants.MQTT_NEW_MESSAGE_ACTION);
//
        IntentFilter mqttConnectionIntentFilter = new IntentFilter(Constants.MQTT_CONNECTION_STATE_ACTION);
        IntentFilter orderUpdateIntentFilter = new IntentFilter(Constants.ORDERS_UPDATE_ACTION);
        this.registerReceiver(receiver, mqttIntentFilter);
        this.registerReceiver(receiver, orderUpdateIntentFilter);
        this.registerReceiver(receiver, mqttConnectionIntentFilter);
        IntentFilter publishFilter = new IntentFilter(Constants.MQTT_PUBLISH_STATE_ACTION);
//
        this.registerReceiver(receiver, publishFilter);
        printOrderId = -1;


        //activeOrderList = new ArrayList<>();
        CheckUpdatesService.startActionUpdateCheck(this);
//        try {
//            updateOrderList(new JSONObject("{\"username\":\"Asela\",\"items\":[[\"Egg\",2,\"100.0000\"],[\"Chicken\",2,\"140.0000\"]]}"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onResume() {
        Log.d("ActiveOrdersActivity", "onResume");
        super.onResume();
//        CheckUpdatesService.startActionUpdateCheck(this);
    }

    @Override
    protected void onPause() {
        Log.d("ActiveOrdersActivity", "onPause");
        super.onPause();
//        CheckUpdatesService.mqttClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        Log.d("ActiveOrdersActivity", "onDestroy");
        super.onDestroy();
        try {
            this.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {

        }

    }

    public void retrieveOrderList() {
        activeOrderList = ActiveOrder.findWithQuery(ActiveOrder.class,"SELECT * FROM active_order ORDER BY is_completed ASC LIMIT 25");
        for (ActiveOrder activeOrder:activeOrderList) {

            List<ActiveOrderItem> activeOrderItemList = ActiveOrderItem.findWithQuery(ActiveOrderItem.class, "SELECT * FROM active_order_item WHERE order_id="+activeOrder.getId());
            activeOrder.setItemList(activeOrderItemList);
            for(ActiveOrderItem activeOrderItem:activeOrderItemList)
                activeOrder.total += activeOrderItem.price * Integer.parseInt(activeOrderItem.qty);
        }

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        rv.setLayoutManager(llm);
        adapter = new RVAdapter(this, activeOrderList);
        rv.setAdapter(adapter);
//        WebServerCommunicationService.sendGetRequest(this,Constants.API_BASE_URL+Constants.API_ORDER_MENUS+"?filter[]=comment,sw,"+GlobalState.getCurrrentUserId()+".&filter[]=option_values,neq,"+Constants.ITEM_STATE_COMPLETED+"&satisfy=all",Constants.ORDERS_UPDATE_ACTION);
    }

//    public void printBillButtonClick(View v) {
//        if (printOrderId == -1) {
//            deliverButton = (Button) v;
//            v.setEnabled(false);
//            int position = (Integer) (((CardView) v.getParent().getParent()).getTag());
//            ActiveOrder order = activeOrderList.get(position);
//            JSONObject dataToSendToPrinter = new JSONObject();
//            printOrderId = position;
//            try {
////            dataToSendToPrinter.put("TABLE",tableIdText.getText());
//                dataToSendToPrinter.put("ORDER_ID", order.getItemList().get(0).orderId);
//                dataToSendToPrinter.put("WAITER", GlobalState.getCurrentUsername());
//
////            dataToSendToPrinter.put("WAITER",GlobalState.getCurrentUsername());
//                for (ActiveOrderItem item : order.getItemList()) {
//                    // if(item.state.equals(Constants.ITEM_STATE_PREPARED)) {
//                    JSONObject menuItem = new JSONObject();
//                    menuItem.put(Constants.ITEM_QTY_KEY, item.qty);
//                    menuItem.put(Constants.ITEM_NAME_KEY, item.itemName);
//                    menuItem.put(Constants.ITEM_PRICE_KEY, item.price);
//                    menuItem.put(Constants.ITEM_ID_KEY, item.itemId);
//                    dataToSendToPrinter.put(item.itemName, menuItem);
////                    }
////                    else{
////                        Toast toast = Toast.makeText(getApplicationContext(), "Can't print the bill until all item has been prepared", Toast.LENGTH_SHORT);
////                        toast.show();
////                        printOrderId = -1;
////                        deliverButton.setEnabled(true);
////                        return;
////                    }
//
//                }
////                MQTTClient mqttClient = new MQTTClient();
////                Log.d("avtive_order", "sending to printer");
////                mqttClient.initializeMQTTClient(this.getBaseContext(), "tcp://iot.eclipse.org:1883", "app:waiter:printer" + GlobalState.getCurrentUsername(), false, false, null, null);
////                mqttClient.publish("print_bill", 2, dataToSendToPrinter.toString().getBytes());
//            } catch (JSONException e) {
//                printOrderId = -1;
//                deliverButton.setEnabled(true);
//                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
//
////            } catch (MqttException e) {
////                printOrderId = -1;
////                deliverButton.setEnabled(true);
////                Toast.makeText(getApplicationContext(), "Can't print the bill due to connection error", Toast.LENGTH_SHORT).show();
////                e.printStackTrace();
//            } catch (Throwable throwable) {
//                printOrderId = -1;
//                deliverButton.setEnabled(true);
//                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
//
//                throwable.printStackTrace();
//            }
//
////        activeOrderList.remove(position);
////        adapter.notifyDataSetChanged();
////        adapter.notifyItemRemoved(position);
//            Log.d("Active_orders", "print bill clicked:" + position);
////        activeOrderList.get(0).itemList.get(0).state="testt";
//        } else {
//            Toast toast = Toast.makeText(getApplicationContext(), "Can't print the bill now! Bill print is in process", Toast.LENGTH_SHORT);
//            toast.show();
//        }
//
//    }

    protected void printSuccess() {
        ActiveOrder order = activeOrderList.get(printOrderId);
        List<ActiveOrderItem> itemList = order.getItemList();
//        for(ActiveOrderItem item:itemList)
//            UpdateBackendIntentService.startSyncronizeRequest(getBaseContext(),item.itemId,Constants.ITEM_STATE_COMPLETED);
        activeOrderList.remove(printOrderId);
        adapter.notifyDataSetChanged();
        adapter.notifyItemRemoved(printOrderId);
        Log.d("Active_orders", "bill sent to print" + printOrderId);
        printOrderId = -1;

    }

    protected void markItemAsReady(String item, String itemId) {
        String itemName = item.split(" for table ")[0];

        Log.d("Active_orders", "item ready: " + itemName + " " + itemId);
        try {
            for (ActiveOrder order : activeOrderList) {
                order.changeState(itemId, Constants.ITEM_STATE_PREPARED);

            }

        } catch (NullPointerException ex) {
            Log.e("Error_ActiveOrders", ex.toString());
        }

        recreate();

    }

   // HashMap<String, ActiveOrder> activeOrderMap;

    String location;
    String phoneNo;
    public void updateOrderList(JSONObject ordersObject) {
        JSONArray orderArray = null;

        try {
            orderArray = ordersObject.getJSONArray(Constants.RECORDS_KEY);
            List<MenuItem> items = new ArrayList<>();
            String username = ordersObject.getString(Constants.USERNAME_KEY);
            location = ordersObject.getString("location");
            phoneNo = ordersObject.getString("phoneNo");

            ActiveOrder activeOrder = new ActiveOrder(username);

          //  activeOrder.itemList.add(activeOrderItem);
          //  activeOrder.total+=item.getInt(1)*item.getDouble(2);
         //   activeOrderMap.put(username, activeOrder);

//            SugarRecord.
//            ActiveOrdersaveInTx(activeOrder);
            activeOrder.save();
            long orderId = activeOrder.getId();
            for (int i = 0; i < orderArray.length(); i++) {
                JSONArray item = orderArray.getJSONArray(i);
                String itemData = "12345";

                ActiveOrderItem activeOrderItem = new ActiveOrderItem(item.getString(0), String.valueOf(item.getInt(1)), null, username, orderId, itemData, item.getDouble(2));

              //  activeOrderItem.save();


                activeOrder.itemList.add(activeOrderItem);
                activeOrder.total+=item.getInt(1)*item.getDouble(2);

            }
            ActiveOrder.saveInTx(activeOrder.getItemList());
          //  Set<String> keys = activeOrderMap.keySet();
          //  activeOrderList = new ArrayList<>();
           // for (String key : keys) {


            activeOrderList.add(0,activeOrder);
             //   Log.d("active_order_activity", activeOrderMap.get(key).tableId);
            //}
            //show in list
            RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
            rv.setHasFixedSize(true);

            LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
            rv.setLayoutManager(llm);
            adapter = new RVAdapter(this, activeOrderList);
            rv.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void deliveredButtonClick(View v){
        Button deliverButton = (Button)v;
        deliverButton.setText("Completed");
        deliverButton.setBackgroundColor(Color.argb(255,50,150,50));

        ActiveOrder.executeQuery("UPDATE ACTIVE_ORDER SET is_completed=1 WHERE id="+deliverButton.getTag());
    }
    public void locationButtonClick(View v){
        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+location);

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);

    }
    public void callButtonClick(View v) {
        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:"+phoneNo));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.startActivity(intent);
    }
    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String response = arg1.getExtras().getString(Constants.RESPONSE_KEY);
            String action = arg1.getAction();
            Log.d("broadcast_received act",arg1.getAction());

           if(action.equals(Constants.MQTT_NEW_MESSAGE_ACTION)){
                if (response != null && !response.contains(Constants.ERROR_RESPONSE)) {
                    Log.d("update Received", "Received to activeOrderActivity: " + response);
                    String topic = response.split("~")[0];

                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getBaseContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("New Order")
                                    .setContentText("Click here to view the order ");
// Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(getBaseContext(),ActiveOrdersActivity.class);


// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
// Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(ActiveOrdersActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().
                    mNotificationManager.notify(0, mBuilder.build());

                    //  if(topic.contains(Constants.NEW_ORDER_TOPIC)) {
                        try {
                            updateOrderList(new JSONObject(response.split("~")[1].split("`")[0]));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//
//                        showMesage(response.split("~")[1].split("`")[0]);
//
//                        String itemId = response.split("~")[1].split("`")[1];
//                        ActiveOrder.executeQuery("UPDATE ACTIVE_ORDER_ITEM SET STATE = '"+Constants.ITEM_STATE_PREPARED+"' WHERE ITEM_ID='"+itemId+"'");
//                        markItemAsReady(response.split("~")[1].split("`")[0],response.split("~")[1].split("`")[1]);
//                        UpdateBackendIntentService.startSyncronizeRequest(getBaseContext(),itemId,Constants.ITEM_STATE_PREPARED);
                    //}

                } else {

                    Log.d("communication", "Received to activeOrderActivity: error ");
                    Toast toast = Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }else if(action.equals(Constants.MQTT_CONNECTION_STATE_ACTION)){
                if (response != null && response.equals(Constants.MQTT_CONNECTION_SUCCESS)) {
                    GlobalState.setConnected(true);
                }
                else if (response != null && response.equals(Constants.MQTT_CONNECTION_FAILED)){
                    GlobalState.setConnected(false);
            //        CheckUpdatesService.stopActionUpdateCheck(getBaseContext());
            //        CheckUpdatesService.startActionUpdateCheck(getBaseContext());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("mqtt", "Connection failed! reconnecting.. ");
//                    Toast.makeText(getApplicationContext(), "Connection failed! Reconnecting..", Toast.LENGTH_SHORT).show();
                }
            }
            else if(action.equals(Constants.ORDERS_UPDATE_ACTION)){
               try {
                   JSONObject jObject = new JSONObject(response);
                   updateOrderList(jObject);


               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }else if(arg1.getAction().equals(Constants.MQTT_PUBLISH_STATE_ACTION)) {
//                unregisterReceiver(receiver);
              //  String response = arg1.getExtras().getString(Constants.RESPONSE_KEY);
                Log.d("mqtt", "Received to activeOrderActivity: " + response);
                if (response != null && response.equals(Constants.MQTT_DELIVER_SUCCESS)) {
//                    Log.d("mqtt", "Received to activeOrderActivity: error ");
                    Toast toast = Toast.makeText(getApplicationContext(), "Bill is printing..", Toast.LENGTH_SHORT);
                    toast.show();
                    printSuccess();
//                    orderSucceed();
                }
                else if(response != null && response.equals(Constants.MQTT_PUBLISH_FAILED)){
                    Log.d("mqtt", "Received to activeOrderActivity: error ");
            //        deliverButton.setEnabled(true);
                    Toast toast = Toast.makeText(getApplicationContext(), "Can't print the bill due to connection error!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
             ///       deliverButton.setEnabled(true);
                    Log.d("mqtt", "Received to activeOrderActivity: error ");
                    Toast toast = Toast.makeText(getApplicationContext(), "Communication Error!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

        }
    }

    public void showMesage(String message){
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Order Ready!")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

}
