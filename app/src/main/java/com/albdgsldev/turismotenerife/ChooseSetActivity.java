package com.albdgsldev.turismotenerife;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class ChooseSetActivity extends AppCompatActivity {

    List<Button> contactButtonList = new ArrayList<>();
    public static ArrayList<Table> tableList;
    public static ConstraintLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCustom);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ll = findViewById(R.id.buttonlayout);
        tableList = new ArrayList<Table>();

        AsyncTaskTableNames asyncTask = new AsyncTaskTableNames(this, new AsyncResponse() {

            @Override
            public void processFinish(String output) {
                //Here you will receive the result fired from async class
                //of onPostExecute(result) method.
                addButtons();
            }
        });

        asyncTask.execute();

    }

    /**
     * Crea los botones para consultar cada tabla
     */
    public void addButtons() {

        ConstraintSet set = new ConstraintSet();

        Log.i("STATE", "intentando añadir botones");


        for (Table table : tableList){
            Boolean addDataSet = true;
            int i = 0;
            if(contactButtonList.size() > 0) {
                for (Button btn : contactButtonList) {
                    i ++;
                    Log.i("STATE", "Iter " + i + ": Data set del button " + btn.getText() + ", data set de table " + table.getDataSet());

                    if (btn.getText().equals(table.getDataSet())) {
                        addDataSet = false;
                    }
                }
            } else {
                addDataSet = true;
            }


            if(addDataSet) {
                //creates string to display on button
                String buttonText = table.getDataSet();
                //creates new button and sets ID based of size of list
                Button btn = new Button(this);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChooseSetActivity.this, ChooseTableActivity.class);
                        intent.putExtra("tableList", tableList); //second param is Serializable
                        intent.putExtra("dataSet", table.getDataSet());
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