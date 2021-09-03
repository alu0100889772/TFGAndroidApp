package com.albdgsldev.turismotenerife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import com.google.android.material.slider.RangeSlider;

public class ChartActivity extends AppCompatActivity {

    private Table table;

    private LineChart lineChart;
    private ArrayList<ILineDataSet> lineDataSets;
    private Button acceptButton;
    private Spinner columnsSpinner;
    private Spinner nationSpinner;
    private RangeSlider doubleSlider;

    private TextView meanResult;
    private TextView medianResult;
    private TextView modeResult;
    private TextView stdDevResult;

    /**
     * Constructor. Inicia los atributos privados.
     * Inicia la tarea de obtener los valores de la tabla recibida en los Extras.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCustomChart);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        lineDataSets = new ArrayList<>();
        lineChart = findViewById(R.id.lineChart);
        lineChart.getLegend().setWordWrapEnabled(true);
        meanResult = findViewById(R.id.MediaViewResult);
        medianResult = findViewById(R.id.MedianaViewResult);
        modeResult = findViewById(R.id.ModaViewResult);
        stdDevResult = findViewById(R.id.DesvEstViewResult);

        lineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("STATE", Arrays.toString(lineChart.getHighlighted()));

            }
        });

        lineChart.setDrawMarkers(true);
        lineChart.setMarker(markerView(this));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            table = (Table) extras.getSerializable("table");
        }

        TextView toolbarTextViewChart = findViewById(R.id.toolbarTextViewChart);
        toolbarTextViewChart.setText(table.getDisplayName());

        doubleSlider = findViewById(R.id.doubleSlider);


        AsyncTaskTableValues asyncTask = new AsyncTaskTableValues(table, this, new AsyncResponse() {

            @Override
            public void processFinish(String output) {
                //Here you will receive the result fired from async class
                //of onPostExecute(result) method.
                configureWidgets();
//                getEntries();
            }
        });

        asyncTask.execute();
    }


    /**
     * Configura los widgets de la Activity. Necesita esperar a que se termine la actividad asíncrona.
     */
    private void configureWidgets() {

        ////////////////////// SPINNER COLUMNAS //////////////////////////////

        columnsSpinner = findViewById(R.id.columnsSpinner);
        ArrayList<String> strings = new ArrayList<>();
        for (String columnName : table.getColumnNames()) {
            if (!columnName.matches("(año|mes|nacion|semestre)")) {
                strings.add(columnName);
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnsSpinner.setAdapter(arrayAdapter);

        ////////////////////// SPINNER NACIONES //////////////////////////////

        nationSpinner = findViewById(R.id.nationSpinner);
        strings = new ArrayList<>();
        if (table.getHasNations()) {
            int nationPos = 0;
            for (int pos = 0; pos < table.getColumnNames().size(); pos++) {
                if (table.getColumnNames().get(pos).equals("nacion")) {
                    nationPos = pos;
                }
            }

//            String[] countries = new String[table.getTableValues().size()];
//            int i = 0;
//            for (ArrayList<String> aux : table.getTableValues()) {
//                countries[i] = aux.get(nationPos);
//                i++;
//            }

            String[] unique = Arrays.stream(table.getArrayOfColumn(nationPos)).distinct().toArray(String[]::new);
            for (int i = 0; i < unique.length; i++) {
                strings.add(unique[i]);
            }
        } else {
            strings.add("Total");
            nationSpinner.setEnabled(false);
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nationSpinner.setAdapter(arrayAdapter);

        ////////////////////// DOUBLE SLIDER //////////////////////////////
        int minAño = Integer.parseInt(table.getTableValues().get(0).get(0));
        int maxAño = Integer.parseInt(table.getTableValues().get(table.getTableValues().size() - 1).get(0));
        ;

        if (minAño > maxAño) {
            int aux = minAño;
            minAño = maxAño;
            maxAño = aux;
        }

        doubleSlider.setValueFrom(minAño);
        doubleSlider.setValueTo(maxAño);

        ArrayList<Float> years = new ArrayList<Float>();
        years.add((float) minAño);
        years.add((float) maxAño);

        doubleSlider.setValues(years);

        doubleSlider.setStepSize(1);


        ////////////////////// ACCEPT BUTTON //////////////////////////////
        acceptButton = findViewById(R.id.accpetButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("STATE", "0: " + doubleSlider.getValues().get(0) + " 1: " + doubleSlider.getValues().get(1));
                getEntries(columnsSpinner.getSelectedItem().toString(), nationSpinner.getSelectedItem().toString(), doubleSlider.getValues().get(0), doubleSlider.getValues().get(1));
            }
        });


    }


    /**
     * Muestra el gráfico y los datos de interés.
     *
     * @param dataToShow
     * @param nation
     */
    private void getEntries(String dataToShow, String nation, Float minYear, Float maxYear) {

        ArrayList<Entry> lineEntries = new ArrayList<>();

        Iterator<String> columnIter = table.getColumnNames().iterator();
        String value = "";
        int añoPos = 0;
        int mesPos = 0;
        int semestrePos = 0;
        int datoPos = 0;
        int nationPos = 0;
        String val1 = "";
        String val2 = "";

        int pos = 0;
        while (columnIter.hasNext()) {
            value = columnIter.next();
            if (value.equals("año")) {
                añoPos = pos;
            }
            if (value.equals("mes")) {
                mesPos = pos;
            }
            if (value.equals("semestre")) {
                semestrePos = pos;
            }
            if (value.equals("nacion")) {
                nationPos = pos;
            }
            if (value.equals(dataToShow)) {
                datoPos = pos;
            }
            pos++;
        }

        Boolean isFloat = false;

        if (table.getTableValues().size() > 0) {

            ArrayList<String> aux = null;
            String half1 = "";
            String half2 = "";
            for (int i = 0; i < table.getTableValues().size(); i++) {
                if (!table.getInvertedOrder()) {
                    aux = table.getTableValues().get(i);
                } else {
                    aux = table.getTableValues().get(table.getTableValues().size() - 1 - i);
                }
                if ((Float.parseFloat(aux.get(añoPos)) >= minYear) && (Float.parseFloat(aux.get(añoPos)) <= maxYear)) {
                    if ((!table.getHasNations()) || ((table.getHasNations()) && (aux.get(nationPos).equals(nation)))) {
                        if (table.getHasMonths()) {
                            if (aux.get(mesPos).matches("^(13|total|TOTAL|Total)$")) {
                                val1 = aux.get(añoPos);
                                val2 = aux.get(datoPos);

                                if (val2.contains(".")) {
                                    isFloat = true;
                                } else {
                                    isFloat = false;
                                }

                                if (isFloat) {
                                    lineEntries.add(new Entry(Integer.parseInt(val1), Float.parseFloat(val2)));
                                } else {
                                    lineEntries.add(new Entry(Integer.parseInt(val1), Integer.parseInt(val2)));
                                }

                            }
                        } else if (table.getHasSemester()) {
                            val1 = aux.get(añoPos);
                            val2 = aux.get(datoPos);
                            if (aux.get(semestrePos).equals("0")) {
                                lineEntries.add(new Entry(Integer.parseInt(val1), Float.parseFloat(val2)));
                            } else {
                                lineEntries.add(new Entry((float) (Integer.parseInt(val1) + 0.5), Float.parseFloat(val2)));
                            }

                        } else if (!table.getHasMonths() && !table.getHasSemester()) {
                            val1 = aux.get(añoPos);
                            val2 = aux.get(datoPos);
                            lineEntries.add(new Entry(Integer.parseInt(val1), Float.parseFloat(val2)));
                        }
                    }
                }
            }
        }

        SimpleMath sm = new SimpleMath();

        double[] arrayOfDataToShow = Stream.of(table.getArrayOfColumn(datoPos, false, nation, minYear, maxYear)).mapToDouble(Double::parseDouble).toArray();

        Set<Double> desviacionEstandar = sm.getMode(arrayOfDataToShow);
        if (desviacionEstandar.size() > 3) {
            modeResult.setText("Tiende a distribución uniforme discreta");
        } else {
            modeResult.setText(desviacionEstandar.toString());
        }

        //modeesult.setText(().toString());
        meanResult.setText(String.format("%.3f", sm.getMean(arrayOfDataToShow)));
        medianResult.setText(String.format("%.3f", sm.getMedian(arrayOfDataToShow)));
        stdDevResult.setText(String.format("%.3f", sm.getStandardDeviation(arrayOfDataToShow)));

        LineDataSet lineDataSet = new LineDataSet(lineEntries, dataToShow + ", "  + nation + ", [" + minYear + ".." + maxYear + "]");

//        LineData lineData = new LineData(lineDataSet);


//        lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        int[] androidColors = getResources().getIntArray(R.array.androidcolors);
        int lineaChartColor = androidColors[lineDataSets.size()%androidColors.length];
        lineDataSet.setColors(lineaChartColor);
        lineDataSet.setDrawValues(false);
        lineDataSet.setCircleColors(lineaChartColor);

        lineDataSet.setValueTextColor(lineaChartColor);
        lineDataSet.setValueTextSize(18f);
        lineDataSets.add(lineDataSet);

        lineChart.setData(new LineData(lineDataSets));

        lineChart.invalidate();
    }

    public CustomMarkerView markerView(Context context)
    {
        CustomMarkerView mv = new CustomMarkerView(context, R.layout.custom_marker, 16, Color.WHITE);
        mv.setOffset(- mv.getWidth(), -mv.getHeight());
        return mv;
    }
}