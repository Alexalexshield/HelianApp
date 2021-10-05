package ru.mineradio.helianapp;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Triangulation extends AppCompatActivity {
    private TextView distance;
    private ImageView arrow;
    private Random random;
    private int degree = 90;
    private int old_degree = 0;

    private double D1,D2,D3;
    private double K = 10.0; // 2 meters
    private double AMBIGIUOUS_ANGLE = 3.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.triangulation);
        D1 = getIntent().getIntExtra("D1",0);
        D2 = getIntent().getIntExtra("D2",0);
        D3 = getIntent().getIntExtra("D3",0);
        init();
        show_location();

    }

    public void onClickReStart(View view)
    {
        show_location();
    }

    private void show_location() {
        old_degree  = 0;

        double alpha = CalculateOppositeAngle(D2,D1,K);
        Log.d("ALPHA",String.valueOf(Math.toDegrees(alpha)));
        double theta = CalculateOppositeAngle(D3,K,D1);
        alpha = Math.toDegrees(alpha);
        theta = Math.toDegrees(theta);

        double angle_adjustment = Math.toDegrees(sin((K/2.0)/D1));
        double dist_adjustment = sqrt(D1*D1 + K*K);
        double ambigious_diff = D1-0.9986*D1; //D1*cos(Math.toRadians(AMBIGIUOUS_ANGLE));

        Log.d("ALPHA", new StringBuilder().append("D1 ").append(String.valueOf(D1)).toString());
        Log.d("ALPHA", new StringBuilder().append("D2 ").append(String.valueOf(D2)).toString());
        Log.d("ALPHA", new StringBuilder().append("D3 ").append(String.valueOf(D3)).toString());
        Log.d("ALPHA", new StringBuilder().append("dist_adjustment ").append(String.valueOf(dist_adjustment)).toString());
        Log.d("ALPHA", new StringBuilder().append("ambigious_diff ").append(String.valueOf(ambigious_diff)).toString());

        if (Math.abs(D2-dist_adjustment) < ambigious_diff){
            if (D3<D1)
                alpha = 90.0;
            else
                alpha = 270.0;
            theta = alpha;
        }
        else if (Math.abs((D2+K)-D1) < ambigious_diff){
            alpha = 0.0;
            theta = 0.0;
        }
        else if (Math.abs((D2-K)-D1)< ambigious_diff){
            alpha = 180.0;
            theta = 180.0;
        }
        else if (Math.abs((D3+K)-D1) < ambigious_diff){
            alpha = 90.0;
            theta = 90.0;
        }
        else if (Math.abs((D3-K)-D1) < ambigious_diff){
            alpha = 270.0;
            theta = 270.0;
        }
        else if (D2 < dist_adjustment){
            if (D3 < dist_adjustment){
                alpha = alpha;
                theta = 90.0-theta;
            }
            else{
                alpha = 360-alpha;
                theta = 360-(theta-90);
            }
        }
        else{
            if (D3 < dist_adjustment)
            {
                alpha = alpha;
                theta = theta+90.0;
            }
            else
            {
                alpha = 360.0 - alpha;
                theta = 90.0 + theta;
            }
        }

        degree = (int) alpha;

        Log.d("ALPHA2",String.valueOf(degree));

        RotateAnimation rotate = new RotateAnimation(old_degree,degree,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(500);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new DecelerateInterpolator());
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                distance.setText("");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                distance.setText(String.valueOf(D1));

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        arrow.startAnimation(rotate);
    }

    private double CalculateOppositeAngle(double a, double b, double c) {
        double denom = (2.0*(b*c));
        double value = (b*b+c*c-(a*a));

        if (denom != 0.0)
        {
            value = value/denom;
        }
        if (value <= -1.0)
            return Math.PI;
        else if (value >= 1.0)
            return 0.0;
        return acos(value);
    }

    private double CalculateOppositeSide(double alpha, double b, double c)
    {
        double cosO = cos(Math.toRadians(alpha));
        double sinO = sin(Math.toRadians(alpha));
        double value = (b*b)*(cosO*cosO)+(b*b)*(sinO*sinO)+(c*c)-(2.0*(b*c*cosO));
        return sqrt(value);
    }


    private void init()
    {
        distance = findViewById(R.id.distance);
        arrow = findViewById(R.id.arrow);
        random = new Random();

        ImageButton quickSearchBut = (ImageButton) findViewById(R.id.quickSearch);
        quickSearchBut.setOnClickListener(quickSearchButList);
    }

    private final View.OnClickListener quickSearchButList = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Triangulation.this, QuickSearch.class);
            startActivity(intent);
        }
    };


}
