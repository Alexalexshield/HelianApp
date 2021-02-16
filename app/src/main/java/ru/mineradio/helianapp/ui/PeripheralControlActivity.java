package ru.mineradio.helianapp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.mineradio.helianapp.Constants;
import ru.mineradio.helianapp.R;
import ru.mineradio.helianapp.bluetooth.BleAdapterService;

public class PeripheralControlActivity extends Activity {
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ID = "id";

    private String device_address;
    private BleAdapterService bluetooth_le_adapter;

    // empty list for services
    private ArrayList<String> charNames = new ArrayList<>();
    // create adapter to link with ListView
    private ArrayAdapter<String> adapterList;

    private final ServiceConnection service_connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetooth_le_adapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetooth_le_adapter.setActivityHandler(message_handler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetooth_le_adapter = null;
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler message_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            String service_uuid = "";
            String characteristic_uuid = "";
            byte[] b = null;

            boolean back_requested = false;
            switch (msg.what) {
                case BleAdapterService.MESSAGE:
                    Log.d(Constants.TAG, "MESSAGE");
                    bundle = msg.getData();
                    String text = bundle.getString(BleAdapterService.PARCEL_TEXT);
                    showMsg(text);
                    break;
                case BleAdapterService.GATT_CONNECTED:
//                    ((Button) PeripheralControlActivity.this.findViewById(R.id.connectButton)).setEnabled(false);
                    showMsg("CONNECTED");
                    bluetooth_le_adapter.discoverServices();
//TRY TO SOLVE PROBLEM NOTIFICATION
                    bluetooth_le_adapter.setIndicationsState(BleAdapterService.SPP_SERVICE_UUID, BleAdapterService.COMMAND_NOTIFY, true);

                    break;

                case BleAdapterService.GATT_DISCONNECT:
                    ((Button) PeripheralControlActivity.this.findViewById(R.id.connectButton)).setEnabled(true);
                    // we're disconnected
                    showMsg("DISCONNECTED");
                    if (back_requested) {
                        PeripheralControlActivity.this.finish();
                    }
                    break;

                case BleAdapterService.GATT_SERVICES_DISCOVERED:

                    // validate services and if ok....
                    List<BluetoothGattService> slist = bluetooth_le_adapter.getSupportedGattServices();
                    boolean link_loss_present = false;

                    for (BluetoothGattService svc : slist) {

                        updateCharacteristicsList(svc.getUuid().toString().toUpperCase());

                        Log.d(Constants.TAG,
                                "UUID=" + svc.getUuid().toString().toUpperCase() + " INSTANCE=" + svc.getInstanceId());
                        if (svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.SPP_SERVICE_UUID)) {
                            link_loss_present = true;
                        }
                    }

                    if (link_loss_present){

                        showMsg("Device has expected services");

                        ((Button) PeripheralControlActivity.this.findViewById(R.id.sendButton)).setEnabled(true);

                        if (bluetooth_le_adapter.setIndicationsState(BleAdapterService.SPP_SERVICE_UUID,
                                BleAdapterService.COMMAND_NOTIFY, true)) {
                        } else {
                            showMsg("Failed to inform SPP monitoring has been enabled");
                        }
//                        bluetooth_le_adapter.readCharacteristic(BleAdapterService.SPP_SERVICE_UUID,
//                                BleAdapterService.SPP_COMMAND_SEND);

                    } else {
                        showMsg("Device does not have expected GATT services");
                    }
                    break;

                case BleAdapterService.GATT_CHARACTERISTIC_READ:
                    bundle = msg.getData();
                    Log.d(Constants.TAG,
                            "Service=" + bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase()
                                    + " Characteristic="
                                    + bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase());
                    if (bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase()
                            .equals(BleAdapterService.SPP_COMMAND_SEND)
                            && bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase()
                            .equals(BleAdapterService.SPP_SERVICE_UUID)) {
                        b = bundle.getByteArray(BleAdapterService.PARCEL_VALUE);
                        Log.d(Constants.TAG, byteArrayAsHexString(b));
                    }
                    break;
                case BleAdapterService.GATT_CHARACTERISTIC_WRITTEN:
                    Log.d(Constants.TAG, "GATT_CHARACTERISTIC_WRITTEN");
                    bundle = msg.getData();
                    Log.d(Constants.TAG,
                            "Service=" + bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase()
                                    + " Characteristic="
                                    + bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase());
                    if (bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase()
                            .equals(BleAdapterService.SPP_COMMAND_SEND)
                            && bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase()
                            .equals(BleAdapterService.SPP_SERVICE_UUID)) {
                        b = bundle.getByteArray(BleAdapterService.PARCEL_VALUE);
                        Log.d(Constants.TAG, "GATT_CHARACTERISTIC_WRITTEN set to: " + byteArrayAsHexString(b));
                    }
                    break;
                case BleAdapterService.NOTIFICATION_OR_INDICATION_RECEIVED:
                    Log.d(Constants.TAG, "NOTIFICATION_OR_INDICATION_RECEIVED");
                    bundle = msg.getData();
                    service_uuid = bundle.getString(BleAdapterService.PARCEL_SERVICE_UUID);
                    characteristic_uuid = bundle.getString(BleAdapterService.PARCEL_CHARACTERISTIC_UUID);
                    b = bundle.getByteArray(BleAdapterService.PARCEL_VALUE);
//                    showMsg(byteArrayAsHexString(b));
                    showMsg(new String(b));
                    //Log.d(Constants.TAG, byteArrayAsHexString(b));
                    Log.d(Constants.TAG, new String(b));
            }
        }
    };

    public void updateCharacteristicsList(String string){
        charNames.add(0, string);
        adapterList.notifyDataSetChanged();
    }

    public void onSend(View view) {
//        String json ="{\"freq\":8000,\"command\":0,  \"modulation\":\"ASK\",\"flag_on_air\":1}";
        String json ="{\"flag_on_air\":1}";
        //todo make extension for long packet of data
        byte[] al = json.getBytes();
        bluetooth_le_adapter.writeCharacteristic(BleAdapterService.SPP_SERVICE_UUID,
                BleAdapterService.SPP_COMMAND_SEND, al);

//        final Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
    }

//    public void onBackPressed() {
//        Log.d(Constants.TAG, "onBackPressed");
//        back_requested = true;
//        if (bluetooth_le_adapter.isConnected()) {
//            try {
//                bluetooth_le_adapter.disconnect();
//            } catch (Exception e) {
//                Log.d("VLF","BackPressedException");
//            }
//        } else {
//            finish();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral_control);

        // read intent data
        final Intent intent = getIntent();
        String device_name = intent.getStringExtra(EXTRA_NAME);
        device_address = intent.getStringExtra(EXTRA_ID);
        // show the device name
        ((TextView) this.findViewById(R.id.nameTextView))
                .setText("Device : " + device_name + " [" + device_address + "]");
        // disable the send msg button
        ((Button) PeripheralControlActivity.this.findViewById(R.id.sendButton)).setEnabled(false);

        // connect to the Bluetooth adapter service
        Intent gattServiceIntent = new Intent(this, BleAdapterService.class);
        bindService(gattServiceIntent, service_connection, BIND_AUTO_CREATE);

//todo remove the listView from onCreate
        // get instance of ListView
        ListView listView = (ListView) PeripheralControlActivity.this.findViewById(R.id.characteristicsListView);
        adapterList = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, charNames);
        // connect array throw adapter to ListView
        listView.setAdapter(adapterList);

        showMsg("READY");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(service_connection);
        bluetooth_le_adapter = null;
    }

    private void showMsg(final String msg) {
        Log.d(Constants.TAG, msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.msgTextView)).setText(msg);
            }
        });
    }

    public void onConnect(View view) {
        showMsg("onConnect");
        if (bluetooth_le_adapter != null) {
            if (bluetooth_le_adapter.connect(device_address)) {

                ((Button) PeripheralControlActivity.this.findViewById(R.id.connectButton)).setEnabled(false);
                bluetooth_le_adapter.setIndicationsState(BleAdapterService.SPP_SERVICE_UUID, BleAdapterService.COMMAND_NOTIFY, true);
            } else {
                showMsg("onConnect: failed to connect");
            }
        } else {
            showMsg("onConnect: bluetooth_le_adapter=null");
        }
    }

    public String byteArrayAsHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        int l = bytes.length;
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < l; i++) {
            if ((bytes[i] >= 0) & (bytes[i] < 16))
                hex.append("0");
            hex.append(Integer.toString(bytes[i] & 0xff, 16).toUpperCase());
        }
        return hex.toString();
    }
}


