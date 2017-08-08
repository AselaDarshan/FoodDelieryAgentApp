package com.siplo.fooddeliver;

/**
 * Created by asela on 5/20/17.
 */
public class Constants {

    public static final int SUCCESS_RESPONSE = 1;
    public static final String ERROR_RESPONSE = "error";
    public static final String RESPONSE_KEY = "response";
    public static final String ORDER_RECEIVED_ACTION = "order_action";
    public static final String CHECKUPDATES_ACTION = "check_updates_action";
    public static final String ADD_ORDER_ACTION = "add_order_action";
    public static final String ADD_ORDER_MENUS_ACTION = "add_order_menus_action";
    public static final String OPTIONS_UPDATE_ACTION = "options_update_action";

    public static final String MQTT_ACTION = "mqtt_action";
    public static final String MQTT_PUBLISH_STATE_ACTION = "mqtt_publish_state";
    public static final String MQTT_CONNECTION_STATE_ACTION = "mqtt_connection_state";
    public static final String MQTT_DISCONNECTED_ACTION = "mqtt_connection_state";
    public static final String MQTT_CONNECTION_FAILED = "mqtt_connection_failed";
    public static final String MQTT_CONNECTION_SUCCESS = "mqtt_connection_success";
    public static final String MQTT_DELIVER_SUCCESS = "mqtt_deliver_success";
    public static final String MQTT_PUBLISH_FAILED = "mqtt_publish_failed";
    public static final String MQTT_NEW_MESSAGE_ACTION = "mqtt_new_message_action";

    public static final String MENU_UPDATE_ACTION = "menu_update_action";
    public static final String CATEGORIES_UPDATE_ACTION = "categories_update_action";
    public static final String ORDERS_UPDATE_ACTION = "orders_update_action";
    public static final String WAITER_LIST_RETRIVE_ACTION = "waiter_list_retrieve_action";
    public static final String EXTENDED_DATA_STATUS = "data_status";
    public static final String COMMUNICATION_ERROR = "error";

    public static final String SERVER_IP = "http://com";
    public static final String API_BASE_URL = SERVER_IP+"/api/api.php/";
    public static final String IMAGE_THUMBS_URL = SERVER_IP+"/assets/images/thumbs/";
    public static final String API_MENU = "forsj3vth_menus";
    public static final String API_CATEGORIES = "forsj3vth_categories";
    public static final String API_STAFF = "forsj3vth_staffs";
    public static final String API_ORDER_MENUS = "forsj3vth_order_menus";
    public static final String API_OPTION_VALUES = "forsj3vth_option_values";
    public static final String API_MENU_OPTIONS = "forsj3vth_menu_options";


    public static final String RECORDS_KEY = "items";
    public static final String USERNAME_KEY = "username";

    public static final String LOCATION_KEY = "location";
    public static final String PHONE_NUMBER_KEY = "phoneNo";

    public static final String NEW_ORDER_TOPIC = "food_delivery_new_order";

    public static String ITEM_STATE_SENT = "SENT";
    public static String ITEM_STATE_PREPARED = "PREPARED";
    public static String ITEM_STATE_READY = "READY";
    public static String ITEM_STATE_COMPLETED = "COMPLETED";

    public static String ITEM_QTY_KEY = "item_qty";
    public static String ITEM_ID_KEY = "item_id";
    public static String ITEM_NAME_KEY = "item_name_key";
    public static String ITEM_PRICE_KEY = "item_price_key";



    public static int userIdMultipler = 100000000;
    public static int tableIdMultipler = 1000000;
    public static int localIdMultipler = 10000;
    public static int preparedInMultipler = 1000;
    public static String MQTT_BROKER_URL = "tcp://13.58.144.197:1883";
    public static final int KITCHEN = 12;
    public static final int BAR = 13;
    public static final int WAITER_GROUP_ID = 13;
}
