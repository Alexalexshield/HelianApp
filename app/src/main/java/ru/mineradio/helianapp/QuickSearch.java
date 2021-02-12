package ru.mineradio.helianapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;
import ru.mineradio.helianapp.bluetooth.BleAdapterService;


public class QuickSearch  extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> mSeriesX;
    private LineGraphSeries<DataPoint> mSeriesY;
    private LineGraphSeries<DataPoint> mSeriesZ;
    private double graphLastXValue = 1d;
    private int graphLastCursorPosition =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_search);

        initGraph();

        String[] tagsFound = { "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",
                "Костя", "Игорь", "Анна", "Денис", "Андрей" };
        // find list
        ListView lvTagIdList = (ListView) findViewById(R.id.tagIdList);
        // create adaptor
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tagsFound);
        //assign the adapter to the list
        lvTagIdList.setAdapter(adapter);
    }


    void initGraph(){
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        mSeriesX = new LineGraphSeries<>();
        mSeriesX.setColor(Color.RED);
        mSeriesX.setTitle("X axis");
        mSeriesX.setThickness(8);

        mSeriesY = new LineGraphSeries<>();
        mSeriesY.setColor(Color.GREEN);
        mSeriesY.setTitle("Y axis");
        mSeriesY.setThickness(8);

        mSeriesZ = new LineGraphSeries<>();
        mSeriesZ.setColor(Color.BLUE);
        mSeriesZ.setTitle("Z axis");
        mSeriesZ.setThickness(8);



        graph.addSeries(mSeriesX);
        graph.addSeries(mSeriesY);
        graph.addSeries(mSeriesZ);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);
        };

    @Override
    public void onResume() {
        super.onResume();

//        String json ="{\"siski\"}";
//        byte[] al = json.getBytes();
//         bluetooth_le_adapter.writeCharacteristic(BleAdapterService.IMMEDIATE_ALERT_SERVICE_UUID,
//                BleAdapterService.SPP_COMMAND_SEND, al);

        //TODO PUT GENERATE DATA in BLUETOOTH
        @SuppressLint("HandlerLeak")

        Handler message_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle;
                String service_uuid = "";
                String characteristic_uuid = "";
                byte[] b = null;

                switch (msg.what) {
                    case BleAdapterService.MESSAGE:
                        bundle = msg.getData();
                        String text = bundle.getString(BleAdapterService.PARCEL_TEXT);
                        Log.d("QUICKSEARCH", "GATT_CHARACTERISTIC_WRITTEN set to: " + text);
                        //showMsg(text);
                        break;

                    case BleAdapterService.GATT_DISCONNECT:
                        // we're disconnected

                        Log.d("QUICKSEARCH", "DISCONNECTED");
//                        showMsg("DISCONNECTED");
                        break;

                    case BleAdapterService.GATT_CHARACTERISTIC_READ:
                        bundle = msg.getData();
                        Log.d("QUICKSEARCH",
                                "Service=" + bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase()
                                        + " Characteristic="
                                        + bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase());
                        if (bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase()
                                .equals(BleAdapterService.SPP_COMMAND_SEND)
                                && bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase()
                                .equals(BleAdapterService.SPP_SERVICE_UUID)) {
                        }
                        break;
                    case BleAdapterService.GATT_CHARACTERISTIC_WRITTEN:
                        bundle = msg.getData();
                        Log.d("QUICKSEARCH",
                                "Service=" + bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase()
                                        + " Characteristic="
                                        + bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase());
                        if (bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase()
                                .equals(BleAdapterService.SPP_COMMAND_SEND)
                                && bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase()
                                .equals(BleAdapterService.SPP_SERVICE_UUID)) {
                            b = bundle.getByteArray(BleAdapterService.PARCEL_VALUE);
                            Log.d("QUICKSEARCH", "GATT_CHARACTERISTIC_WRITTEN set to: " + byteArrayAsHexString(b));
                        }
                        break;
                    case BleAdapterService.NOTIFICATION_OR_INDICATION_RECEIVED:
                        bundle = msg.getData();
                        service_uuid = bundle.getString(BleAdapterService.PARCEL_SERVICE_UUID);
                        characteristic_uuid = bundle.getString(BleAdapterService.PARCEL_CHARACTERISTIC_UUID);
                        b = bundle.getByteArray(BleAdapterService.PARCEL_VALUE);
                        Log.d("QUICKSEARCH", byteArrayAsHexString(b));
                }
            }
        };

//        mTimer1 = new Runnable() {
//            @Override
//            public void run() {
//                double[] buffX;
//                double[] buffY;
//                double[] buffZ;
//                if (true){
//                    buffX = generateData();
//                    buffY = generateData();
//                    buffZ = generateData();
//                }
//
//
//                graphLastXValue += 1d;
//                mSeriesX.appendData(new DataPoint(graphLastXValue, buffX[graphLastCursorPosition]), true, 40);
//                mSeriesY.appendData(new DataPoint(graphLastXValue, buffY[graphLastCursorPosition]), true, 40);
//                mSeriesZ.appendData(new DataPoint(graphLastXValue, buffZ[graphLastCursorPosition]), true, 40);
//                mHandler.postDelayed(this, 100);
//
//                if (graphLastCursorPosition<buffX.length-1) {
//                    graphLastCursorPosition += 1;
//                }
//                else
//                {
//                    graphLastCursorPosition = 0;
//                }
//
//            }
//        };
//        mHandler.postDelayed(mTimer1, 1000);
    }

//    @Override
//    public void onPause() {
//        mHandler.removeCallbacks(mTimer1);
//        super.onPause();
//    }
//
//    private double[] generateData() {
//        int count = 30;
//        double[] values= new double[30];
//        for (int i=0; i<count; i++) {
//            double x = i;
//            double f = mRand.nextDouble()*0.15+0.3;
//            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
//            double v = y;
//            values[i] = v;
//        }
//        return values;
//    }
//
//    double mLastRandom = 2;
//    Random mRand = new Random();
//    private double getRandom() {
//        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
//    }
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
