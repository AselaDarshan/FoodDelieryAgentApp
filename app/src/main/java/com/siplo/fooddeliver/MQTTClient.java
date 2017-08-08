/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Dave Locke - initial API and implementation and/or initial documentation
 */

package com.siplo.fooddeliver;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * A sample application that demonstrates how to use the Paho MQTT v3.1 Client API in
 * non-blocking callback/notification mode.
 *
 * It can be run from the command line in one of two modes:
 *  - as a publisher, sending a single message to a topic on the server
 *  - as a subscriber, listening for messages from the server
 *
 *  There are three versions of the sample that implement the same features
 *  but do so using using different programming styles:
 *  <ol>
 *  <li>Sample which uses the API which blocks until the operation completes</li>
 *  <li>SampleAsyncWait shows how to use the asynchronous API with waiters that block until
 *  an action completes</li>
 *  <li>SampleAsyncCallBack (this one) shows how to use the asynchronous API where events are
 *  used to notify the application when an action completes<li>
 *  </ol>
 *
 *  If the application is run with the -h parameter then info is displayed that
 *  describes all of the options / parameters.
 */
public class MQTTClient implements MqttCallback {

    int state = BEGIN;

    static final int BEGIN = 0;
    static final int CONNECTED = 1;
    static final int PUBLISHED = 2;
    static final int SUBSCRIBED = 3;
    static final int DISCONNECTED = 4;
    static final int FINISH = 5;
    static final int ERROR = 6;
    static final int DISCONNECT = 7;





    // Private instance variables
    MqttAsyncClient 	client;
    String 				brokerUrl;
    private boolean 			quietMode;
    private MqttConnectOptions 	conOpt;
    private boolean 			clean;
    Throwable 			ex = null;
    Object 				waiter = new Object();
    boolean 			donext = false;
    private String password;
    private String userName;
    CheckUpdatesService checkUpdatesService;
    private Context context;
//    public MQTTClient() {
//        super("CommunicationService");
//    }


    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    public void disconnect(){
        state = DISCONNECT;
    }
    /**
     * Constructs an instance of the sample client wrapper
     *
     * @param context
     * @param brokerUrl the url to connect to
     * @param clientId the client id to connect with
     * @param cleanSession clear state at end of connection or not (durable or non-durable subscriptions)
     * @param quietMode whether debug should be printed to standard out
     * @param userName the username to connect with
     * @param password the password for the user
     * @throws MqttException
     */
    public void initializeMQTTClient(Context context,String clientId, boolean cleanSession,
                                     boolean quietMode, String userName, String password) throws MqttException {

        this.context = context;
        this.brokerUrl = Constants.MQTT_BROKER_URL;
        this.quietMode = quietMode;
        this.clean 	   = cleanSession;
        this.password = password;
        this.userName = userName;
        this.checkUpdatesService = checkUpdatesService;
       // clientId+= randomString(5);
        //This sample stores in a temporary directory... where messages temporarily
        // stored until the message has been delivered to the server.
        //..a real application ought to store them somewhere
        // where they are not likely to get deleted or tampered with
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

        try {
            // Construct the object that contains connection parameters
            // such as cleanSession and LWT
            conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(clean);
            Log.d("mqtt_client","Connection Time out: "+String.valueOf(conOpt.getConnectionTimeout())+"keep alive interval: "+String.valueOf(conOpt.getKeepAliveInterval()))  ;
            if(password != null ) {
                conOpt.setPassword(this.password.toCharArray());
            }
            if(userName != null) {
                conOpt.setUserName(this.userName);
            }

            // Construct the MqttClient instance
            client = new MqttAsyncClient(this.brokerUrl,clientId, dataStore);

            // Set this wrapper as the callback handler
            client.setCallback(this);

        } catch (MqttException e) {
            e.printStackTrace();
            log("Unable to set up client: "+e.toString());
          //  System.exit(1);
        }
    }



    /**
     * Publish / send a message to an MQTT server
     * @param topicName the name of the topic to publish to
     * @param qos the quality of service to delivery the message at (0,1,2)
     * @param payload the set of bytes to send to the MQTT server
     * @throws MqttException
     */
    public void publish(String topicName, int qos, byte[] payload) throws Throwable {
        // Use a state machine to decide which step to do next. State change occurs
        // when a notification is received that an MQTT action has completed
        while (state != FINISH) {
            switch (state) {
                case BEGIN:
                    // Connect using a non-blocking connect
                    MqttConnector con = new MqttConnector();
                    con.doConnect();
                    break;
                case CONNECTED:
                    // Publish using a non-blocking publisher
                    Publisher pub = new Publisher();
                    pub.doPublish(topicName, qos, payload);
                    break;
                case PUBLISHED:
                    state = DISCONNECT;
                    donext = true;
                    break;
                case DISCONNECT:
                    Disconnector disc = new Disconnector();
                    disc.doDisconnect();
                    break;
                case ERROR:
                    throw ex;
                case DISCONNECTED:
                    state = FINISH;
                    donext = true;
                    break;
            }

//    		if (state != FINISH) {
            // Wait until notified about a state change and then perform next action
            waitForStateChange(10000);
//    		}
        }
    }

    /**
     * Wait for a maximum amount of time for a state change event to occur
     * @param maxTTW  maximum time to wait in milliseconds
     * @throws MqttException
     */
    private void waitForStateChange(int maxTTW ) throws MqttException {
        synchronized (waiter) {
            if (!donext ) {
                try {
                    waiter.wait(maxTTW);
                } catch (InterruptedException e) {
                    log("timed out");
                    e.printStackTrace();
                }

                if (ex != null) {
                    throw (MqttException)ex;
                }
            }
            donext = false;
        }
    }

    /**
     * Subscribe to a topic on an MQTT server
     * Once subscribed this method waits for the messages to arrive from the server
     * that match the subscription. It continues listening for messages until the enter key is
     * pressed.
     * @param topicName to subscribe to (can be wild carded)
     * @param qos the maximum quality of service to receive messages at for this subscription
     * @throws MqttException
     */
    public void subscribe(String topicName,String topic2, int qos) throws Throwable {
        // Use a state machine to decide which step to do next. State change occurs
        // when a notification is received that an MQTT action has completed
        log("Subscribing.. state :"+state+" topic:"+topicName);
        while (state != FINISH) {
            switch (state) {
                case BEGIN:
                    // Connect using a non-blocking connect
                    MqttConnector con = new MqttConnector();
                    con.doConnect();
                    break;
                case CONNECTED:
                    // Subscribe using a non-blocking subscribe
                    Subscriber sub = new Subscriber();
                    sub.doSubscribe(topicName, qos);
                    sub.doSubscribe(topic2, qos);
                    break;
                case SUBSCRIBED:
                    // Block until Enter is pressed allowing messages to arrive
//                    log("Press <Enter> to exit");
//                    try {
//                        System.in.read();
//                    } catch (IOException e) {
//                        //If we can't read we'll just exit
//                    }
//                    state = DISCONNECT;
//                    donext = true;
                    break;
                case DISCONNECT:
                    Disconnector disc = new Disconnector();
                    disc.doDisconnect();
                    break;
                case ERROR:
                    throw ex;
                case DISCONNECTED:
                    state = FINISH;
                    donext = true;
                    break;
            }

//    		if (state != FINISH && state != DISCONNECT) {
            waitForStateChange(10000);
        }
//    	}
    }

    /**
     * Utility method to handle logging. If 'quietMode' is set, this method does nothing
     * @param message the message to log
     */
    void log(String message) {
        if (!quietMode) {
            System.out.println(message);
        }
    }

    /****************************************************************/
	/* Methods to implement the MqttCallback interface              */
    /****************************************************************/

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        // Called when the connection to the server has been lost.
        // An application may choose to implement reconnection
        // logic at this point. This sample simply exits.
        log("Connection to " + brokerUrl + " lost!" + cause);

        if(cause.getMessage().contains("java.net.SocketException"))
            internalBroadcast(Constants.MQTT_CONNECTION_STATE_ACTION,Constants.MQTT_CONNECTION_FAILED);
        if(cause.toString().contains("Timed out")){
            log("timed out. re-subscribing..");
            CheckUpdatesService.startActionUpdateCheck(context);
        }
       // System.exit(1);
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Called when a message has been delivered to the
        // server. The token passed in here is the same one
        // that was returned from the original call to publish.
        // This allows applications to perform asynchronous
        // delivery without blocking until delivery completes.
        //
        // This sample demonstrates asynchronous deliver, registering
        // a callback to be notified on each call to publish.
        //
        // The deliveryComplete method will also be called if
        // the callback is set on the client
        //
        // note that token.getTopics() returns an array so we convert to a string
        // before printing it on the console
        log("Delivery complete callback: Publish Completed "+Arrays.toString(token.getTopics()));

            internalBroadcast(Constants.MQTT_PUBLISH_STATE_ACTION,Constants.MQTT_DELIVER_SUCCESS);

    }

    public void internalBroadcast(String action,String message){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(Constants.RESPONSE_KEY, message);
        context.sendBroadcast(intent);
    }
    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        // Called when a message arrives from the server that matches any
        // subscription made by the client
        String time = new Timestamp(System.currentTimeMillis()).toString();
        System.out.println("Time:\t" +time +
                "  Topic:\t" + topic +
                "  Message:\t" + new String(message.getPayload()) +
                "  QoS:\t" + message.getQos());


//            orderWindow.addOrder(new String(message.getPayload()));
//            Intent intent = new Intent();
//            intent.setAction(Constants.CHECKUPDATES_ACTION);
//            intent.putExtra(Constants.RESPONSE_KEY,  new String(message.getPayload()));
//            sendBroadcast(intent);
//            checkUpdatesService.messgeRecived(new String(message.getPayload()),topic);
                internalBroadcast(Constants.MQTT_NEW_MESSAGE_ACTION,topic+"~"+new String(message.getPayload()));

    }



    /**
     * Connect in a non-blocking way and then sit back and wait to be
     * notified that the action has completed.
     */
    public class MqttConnector {

        public MqttConnector() {
        }

        public void doConnect() {
            // Connect to the server
            // Get a token and setup an asynchronous listener on the token which
            // will be notified once the connect completes
            log("Connecting to "+brokerUrl + " with client ID "+client.getClientId());

            IMqttActionListener conListener = new IMqttActionListener() {
                public void onSuccess(IMqttToken asyncActionToken) {
                    log("Connected");
                    state = CONNECTED;
                    internalBroadcast(Constants.MQTT_CONNECTION_STATE_ACTION,Constants.MQTT_CONNECTION_SUCCESS);
                    carryOn();
                }

                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    ex = exception;
                    state = ERROR;
                    log ("connect failed" +exception);
                    internalBroadcast(Constants.MQTT_CONNECTION_STATE_ACTION,Constants.MQTT_CONNECTION_FAILED);
                    carryOn();
                }

                public void carryOn() {
                    synchronized (waiter) {
                        donext=true;
                        waiter.notifyAll();
                    }
                }
            };

            try {
                // Connect using a non-blocking connect
                client.connect(conOpt,"Connect sample context", conListener);
            } catch (MqttException e) {
                // If though it is a non-blocking connect an exception can be
                // thrown if validation of parms fails or other checks such
                // as already connected fail.
                internalBroadcast(Constants.MQTT_CONNECTION_STATE_ACTION,Constants.MQTT_CONNECTION_FAILED);
                state = ERROR;
                donext = true;
                ex = e;
            }
        }
    }

    /**
     * Publish in a non-blocking way and then sit back and wait to be
     * notified that the action has completed.
     */
    public class Publisher {
        public void doPublish(String topicName, int qos, byte[] payload) {
            // Send / publish a message to the server
            // Get a token and setup an asynchronous listener on the token which
            // will be notified once the message has been delivered
            MqttMessage message = new MqttMessage(payload);
            message.setQos(qos);


            String time = new Timestamp(System.currentTimeMillis()).toString();
            log("Publishing at: "+time+ " to topic \""+topicName+"\" qos "+qos);

            // Setup a listener object to be notified when the publish completes.
            //
            IMqttActionListener pubListener = new IMqttActionListener() {
                public void onSuccess(IMqttToken asyncActionToken) {
                    log("Publish Completed");
                    state = PUBLISHED;
                    carryOn();
                }

                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    ex = exception;
                    state = ERROR;
                    log ("Publish failed" +exception);
                    internalBroadcast(Constants.MQTT_PUBLISH_STATE_ACTION,Constants.MQTT_PUBLISH_FAILED);
                    carryOn();
                }

                public void carryOn() {
                    synchronized (waiter) {
                        donext=true;
                        waiter.notifyAll();
                    }
                }
            };

            try {
                // Publish the message
                client.publish(topicName, message, "Pub sample context", pubListener);
            } catch (MqttException e) {
                state = ERROR;
                donext = true;
                ex = e;
            }
        }
    }

    /**
     * Subscribe in a non-blocking way and then sit back and wait to be
     * notified that the action has completed.
     */
    public class Subscriber {
        public void doSubscribe(String topicName, int qos) {
            // Make a subscription
            // Get a token and setup an asynchronous listener on the token which
            // will be notified once the subscription is in place.
            log("Subscribing to topic \""+topicName+"\" qos "+qos);

            IMqttActionListener subListener = new IMqttActionListener() {
                public void onSuccess(IMqttToken asyncActionToken) {
                    log("Subscribe Completed");
                    state = SUBSCRIBED;
                    carryOn();
                }

                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    ex = exception;
                    state = ERROR;
                    log ("Subscribe failed" +exception);
                    carryOn();
                }

                public void carryOn() {
                    synchronized (waiter) {
                        donext=true;
                        waiter.notifyAll();
                    }
                }
            };

            try {
                client.subscribe(topicName, qos, "Subscribe sample context", subListener);
            } catch (MqttException e) {
                state = ERROR;
                donext = true;
                ex = e;
            }
        }
    }

    /**
     * Disconnect in a non-blocking way and then sit back and wait to be
     * notified that the action has completed.
     */
    public class Disconnector {
        public void doDisconnect() {
            // Disconnect the client
            log("Disconnecting");

            IMqttActionListener discListener = new IMqttActionListener() {
                public void onSuccess(IMqttToken asyncActionToken) {
                    log("Disconnect Completed");
                    state = DISCONNECTED;
                    carryOn();
                }

                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    ex = exception;
                    state = ERROR;
                    log ("Disconnect failed" +exception);
                    carryOn();
                }
                public void carryOn() {
                    synchronized (waiter) {
                        donext=true;
                        waiter.notifyAll();
                    }
                }
            };

            try {
                client.disconnect("Disconnect sample context", discListener);
            } catch (MqttException e) {
                state = ERROR;
                donext = true;
                ex = e;
            }
        }
    }
}