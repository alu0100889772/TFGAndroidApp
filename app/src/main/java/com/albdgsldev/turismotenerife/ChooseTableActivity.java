package com.albdgsldev.turismotenerife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChooseTableActivity extends AppCompatActivity {

    List<Button> contactButtonList = new ArrayList<>();
    String dataSet;
    public static ArrayList<Table> tableList;
    public static ConstraintLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_table);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCustomChooseTable);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbarTextViewChart = findViewById(R.id.toolbarTextViewChooseTable);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        ll = findViewById(R.id.buttonlayoutTable);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tableList = (ArrayList<Table>) extras.getSerializable("tableList");
            dataSet = (String) extras.getSerializable("dataSet");
        }

        toolbarTextViewChart.setText(dataSet);

        addButtons();

    }

    public void addButtons() {

        ConstraintSet set = new ConstraintSet();

        for (Table table : tableList){


            if(dataSet.equals(table.getDataSet())) {
                //creates string to display on button
                String buttonText = table.getDisplayName();
                Log.i("STATE", buttonText);
                //creates new button and sets ID based of size of list
                Button btn = new Button(this);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChooseTableActivity.this, ChartActivity.class);
                        intent.putExtra("table", table); //second param is Serializable
                        startActivity(intent);

                    }
                });

                //sets layout params width as 0 so we can set to match constraint
                btn.setLayoutParams(new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT));

                btn.setId(View.generateViewId());
                //sets button text from array
                btn.setText(buttonText);


                //adds the button to the layout
                ll.addView(btn, contactButtonList.size());

                //sets the constraint set to match the current layout.... i think?, this needs to be done after adding the view
                set.clone(ll);

                //sets button width to match constraint
                set.constrainDefaultWidth(btn.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

                //connects button to left vert constraint
                set.connect(btn.getId(), ConstraintSet.LEFT, R.id.guideline19, ConstraintSet.RIGHT, 8);
                //connects button to right ver constraint
                set.connect(btn.getId(), ConstraintSet.RIGHT, R.id.guideline20, ConstraintSet.RIGHT, 8);

                //if first button attaches to horz constraint else attaches to last button
                if (contactButtonList.isEmpty()) {
                    set.connect(btn.getId(), ConstraintSet.TOP, R.id.guideline18, ConstraintSet.BOTTOM, 8);
                } else {
                    set.connect(btn.getId(), ConstraintSet.TOP, contactButtonList.get(contactButtonList.size() - 1).getId(), ConstraintSet.BOTTOM);
                }

                //adds button to list
                contactButtonList.add(btn);

                //apply set to layout
                set.applyTo(ll);
            }
        }

    }
}