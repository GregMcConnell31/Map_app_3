package com.varsitycollege.mappedone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class SetttingsActivity extends AppCompatActivity {
    private Spinner landmarkSpinner;
    private RadioButton btnMetric;
    private RadioButton btnImperial;
    private String selectedUnit;
    private Button btnSubmit;
    private SwitchCompat switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.Theme_Dark);
        }else{
            setTheme(R.style.Theme_Light);
        }
        super.onCreate(savedInstanceState);
        selectedUnit="M";
        setContentView(R.layout.activity_setttings);
        landmarkSpinner=findViewById(R.id.landmarkSpinner);
        btnMetric=findViewById(R.id.metricButton);
        btnImperial=findViewById(R.id.imperialButton);
        btnSubmit=findViewById(R.id.settingsSubmit);
        switchCompat=findViewById(R.id.bt_switch);
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchCompat.isChecked()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
        String[] collections={"Historical", "Modern", "Popular"};
        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(SetttingsActivity.this, android.R.layout.simple_spinner_item, collections);
        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        landmarkSpinner.setAdapter(areasAdapter);
        btnImperial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedUnit="Imperial";
                btnMetric.setSelected(false);
            }
        });
        btnMetric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnImperial.setSelected(false);
                selectedUnit="Metric";
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SetttingsActivity.this, "Preferred unit: "+selectedUnit+ "\n Preferred landmark "+landmarkSpinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}