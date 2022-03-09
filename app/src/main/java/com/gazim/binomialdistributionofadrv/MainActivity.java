package com.gazim.binomialdistributionofadrv;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    byte b = 18;
    double p = 0;
    int n = 0, k = 0;
    boolean active = false;
    MathJaxWebView mathJaxWebView;
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mathJaxWebView = findViewById(R.id.WebView);
        mathJaxWebView.getSettings().setJavaScriptEnabled(true);
        final Button btnCalculate = findViewById(R.id.btnCalculate);
        final EditText editTextP = findViewById(R.id.editTextP),
                editTextN = findViewById(R.id.editTextN),
                editTextK = findViewById(R.id.editTextK),
                editTextRound = findViewById(R.id.editTextRound);



        btnCalculate.setOnClickListener(view ->
                {
                    b = 18;
                    p = 0;
                    n = 0;
                    k = 0;
                    try {
                        n = Integer.parseInt(editTextN.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        b = Byte.parseByte(editTextRound.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        p = Double.parseDouble(editTextP.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        k = Integer.parseInt(editTextK.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    active = allInformationJS(p, n, k, b);
                }
        );

    }
    public boolean allInformationJS(double p, int n, int k, byte roundAll) {
        boolean bool = true;
        if (n < 1) {
            bool = false;
            Toast.makeText(this, "the number of experiments 'n' must be greater than 0", Toast.LENGTH_LONG).show();
        }
        if (p < 0 || p > 1) {
            bool = false;
            Toast.makeText(this, "probability 'p' must be between 0 and 1", Toast.LENGTH_LONG).show();
        }
        if (k < 0) {
            bool = false;
            Toast.makeText(this, "the number of experiments in which the 'k' event occurred must be greater than 0", Toast.LENGTH_LONG).show();
        }
        if (roundAll < 0 || roundAll > 18) {
            bool = false;
            Toast.makeText(this, "rounding \"round\" must be between 0 and 18", Toast.LENGTH_LONG).show();
        }
        if (bool) {
            String formulaColor = "#707070";
            String DX = fmt(round(dispersion(n, p), roundAll), roundAll),
                    P = fmt(round(p, (byte) 15), (byte) 15),
                    Q = fmt(round(1 - p, (byte) 15), (byte) 15);
            String s1 = "$$$$";
            StringBuilder s = new StringBuilder();
            if (getResources().getConfiguration().uiMode == 33) formulaColor = "#B3B3B3";
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                s1 = "";

            s.append("$$\\color{").append(formulaColor).append("}{p=").append(P).append("{\\quad}q=").
                    append(Q).append("{\\quad}n=").append(n).
                    append("{\\quad}k=").append(k).append("}$$$$\\color{").append(formulaColor).append("}{P_n(K)=C_n^k{\\cdot}p^k{\\cdot}q^{n-k}}$$$$$$");
            for (int i = 0; i <= k; i++)
                s.append("$$\\color{").append(formulaColor).append("}{P_{").append(n).append("}(X=").append(i).append(")=").append("C_{").
                        append(n).append("}^{").append(i).append("}\\cdot").append(P).
                        append("^{").append(i).append("}\\cdot").append(Q).
                        append("^{").append(n - i).append("}=").append("}").append(s1).append("\\color{").append(formulaColor).append("}{").append("\\frac{").append(n).
                        append("!}{").append(i).append("!\\cdot(").append(n).append('-').append(i).
                        append(")!}\\cdot").append(fmt(round(Math.pow(p, i), (byte) 15), (byte) 15)).append("\\cdot").
                        append(fmt(round(Math.pow(1 - p, n - i), (byte) 15), (byte) 15)).append('=').
                        append(fmt(round(BernoulliFormula(p, n, i), roundAll), roundAll)).append("{\\quad}}$$$$$$");

            s.append("$$\\color{").append(formulaColor).append("}{M(X)=").append(P).append("\\cdot").append(n).append('=').
                    append(fmt(round(expectedValue(n, p), roundAll), roundAll)).append("}$$$$\\color{").append(formulaColor).append("}{D(X)=").append(fmt(round(p, (byte) 15), (byte) 15)).
                    append("\\cdot").append(n).append("\\cdot").append(Q).append('=').
                    append(DX).append("}$$$$\\color{").append(formulaColor).append("}{σ(X)=").append("\\sqrt{").append(DX).
                    append("}=").append(fmt(round(sigma(n, p), roundAll), roundAll)).append("}$$");
            mathJaxWebView.setText(s.toString());
        } else mathJaxWebView.setText("");
        return bool;
    }

    public double BernoulliFormula(double p, int n, int k) {
        return combination(n, k)*Math.pow(p, k)*Math.pow(1-p, n-k);
    }

    public int factorial(int i) {
        int start = 1;
        for (int i0 = 1; i0<=i; i0++) start*=i0;
        return start;
    }

    public double combination(int n, int k) {
        return (double) factorial(n)/(factorial(k)*factorial(n-k));
    }

    public double expectedValue(int n, double p) {
        return n*p;
    }

    public double dispersion(int n, double p) {
        return n*p*(1-p);
    }

    public double sigma(int n, double p) {
        return Math.sqrt(dispersion(n, p));
    }

    public double round (double d, byte round) {
        if (round<0 || round>18) try {
            throw new Exception("only numbers from 0 to 18");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        long l = (long) Math.pow(10, round), part0 = (long) d;
        double part1 = d - part0;
        return part0 + Math.round(part1 * l) / (double) l;
    }

    public static String fmt(double d, byte b) {
        String s = String.format("%."+b+"f", d);
        while (true) {
            if (s.indexOf('0', s.length()-1)>-1) s = s.substring(0, s.length()-1);
            else {
                if (s.indexOf('.', s.length()-1)>-1) s = s.substring(0, s.length()-1);
                break;
            }
        }
        return s;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("bool", active);
        if (active) {
            outState.putInt("n", n);
            outState.putByte("round", b);
            outState.putInt("k", k);
            outState.putDouble("p", p);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (active = savedInstanceState.getBoolean("bool")) {
            n = savedInstanceState.getInt("n");
            b = savedInstanceState.getByte("round");
            k = savedInstanceState.getInt("k");
            p = savedInstanceState.getDouble("p");
            allInformationJS(p, n, k, b);
        }
    }
}