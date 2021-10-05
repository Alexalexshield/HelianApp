package ru.mineradio.helianapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;


public class QuickSearch  extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> mSeriesX;
    private LineGraphSeries<DataPoint> mSeriesY;
    private LineGraphSeries<DataPoint> mSeriesZ;
    private double graphLastXValue = 5d;

    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_search);

        initGraph();

        String[] tagsFound = { "ID32532", "ID34732", "ID32518", "ID32511", "ID39812", "ID42455","ID42465",};
        random = new Random();

        // find list
        final ListView lvTagIdList = (ListView) findViewById(R.id.tagIdList);
        // create adaptor
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tagsFound);
        //assign the adapter to the list
        lvTagIdList.setAdapter(adapter);
        lvTagIdList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(QuickSearch.this, Triangulation.class);
                intent.putExtra("D1", 37);//random.nextInt(15));
                intent.putExtra("D2", 45);//random.nextInt(15)+2);
                intent.putExtra("D3", 43);//random.nextInt(15)+2);
                startActivity(intent);
            }
        });
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
        }

    @Override
    public void onResume() {
        super.onResume();

        mTimer1 = new Runnable() {
            @Override
            public void run() {
                graphLastXValue += 1d;
                mSeriesX.appendData(new DataPoint(graphLastXValue, getRandom()), true, 40);
                mSeriesY.appendData(new DataPoint(graphLastXValue, getRandom()), true, 40);
                mSeriesZ.appendData(new DataPoint(graphLastXValue, getRandom()), true, 40);
                mHandler.postDelayed(this, 200);
            }
        };
        mHandler.postDelayed(mTimer1, 1000);
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }

    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }

}
