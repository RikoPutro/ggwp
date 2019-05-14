package com.example.stegocipher.steganogarficipher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private Button btnEnkripsi,btnDekripsi,btnAbout,btnHelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnEnkripsi=(Button) this.findViewById(R.id.btnEnkripsi);
        btnDekripsi=(Button) this.findViewById(R.id.btnDekripsi);
        btnAbout=(Button) this.findViewById(R.id.btnAbout);
        btnHelp=(Button) this.findViewById(R.id.btnHelp);

        btnEnkripsi.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EncodeActivity.class);
                startActivity(intent);
            }
        });

        btnDekripsi.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DecodeActivity.class);
                startActivity(intent);
            }
        });

        btnAbout.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ProfilActivity.class);
                startActivity(intent);
            }
        });

        btnHelp.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,HelpActivity.class);
                startActivity(intent);
            }
        });
    }
}
