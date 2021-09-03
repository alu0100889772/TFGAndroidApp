package com.albdgsldev.turismotenerife;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Table implements Serializable {
    private String tableName;
    private String displayName;
    private String dataSet;
    private ArrayList<String> columnNames;
    private ArrayList<ArrayList<String>> tableValues;
    private Boolean hasMonths;
    private Boolean hasSemester;
    private Boolean hasNations;
    private Boolean invertedOrder;

    public Table(String tableName, String displayName, String dataSet) {
        columnNames = new ArrayList<String>();
        tableValues = new ArrayList<ArrayList<String>>();
        setTableName(tableName);
        setDisplayName(displayName);
        setDataSet(dataSet);
        setHasMonths(false);
        setHasSemester(false);
        setHasNations(false);
        setInvertedOrder(null);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDataSet() {
        return dataSet;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public ArrayList<ArrayList<String>> getTableValues() {
        return tableValues;
    }

    public Boolean getHasMonths() {
        return hasMonths;
    }

    public void setHasMonths(Boolean hasMonths) {
        this.hasMonths = hasMonths;
    }

    public Boolean getHasSemester() {
        return hasSemester;
    }

    public void setHasSemester(Boolean hasSemester) {
        this.hasSemester = hasSemester;
    }

    public Boolean getHasNations() {
        return hasNations;
    }

    public void setHasNations(Boolean hasNations) {
        this.hasNations = hasNations;
    }

    public Boolean getInvertedOrder() {
        return invertedOrder;
    }

    public void setInvertedOrder(Boolean invertedOrder) {
        this.invertedOrder = invertedOrder;
    }

    public void addTableRow(ArrayList<String> row) {
        if (invertedOrder == null) {
            // Si la anterior fila ingresada es mayor, tiene el órden invertido, si no, el orden no es inverso
            if (tableValues.size() > 0) {
                if (Integer.parseInt(tableValues.get(tableValues.size() - 1).get(0)) > Integer.parseInt(row.get(0))) {
                    setInvertedOrder(true);
                } else if (Integer.parseInt(tableValues.get(tableValues.size() - 1).get(0)) < Integer.parseInt(row.get(0))) {
                    setInvertedOrder(false);
                }
            }
        }
        tableValues.add(row);
    }

    public ArrayList<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(ArrayList<String> columnNames) {
        this.columnNames = columnNames;
        for (String columnName : columnNames) {
            if (columnName.equals("mes")) {
                setHasMonths(true);
            } else if (columnName.equals("semestre")) {
                setHasSemester(true);
            } else if (columnName.equals("nacion")) {
                setHasNations(true);
            }
        }
    }

    public void emptyTableValues() {
        tableValues = new ArrayList<ArrayList<String>>();
    }

    public String[] getArrayOfColumn(int column) {
        return getArrayOfColumn(column, true, null, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public String[] getArrayOfColumn(int column, Boolean everyMonth, String nation, Float minYear, Float maxYear) {
        int nationPos = columnNames.indexOf("nacion");
        int mesPos = columnNames.indexOf("mes");
        int semestrePos = columnNames.indexOf("semestre");

        ArrayList<String> strings = new ArrayList<>();
        for (ArrayList<String> aux : getTableValues()) {
            if ((Float.parseFloat(aux.get(0)) >= minYear) && (Float.parseFloat(aux.get(0)) <= maxYear)) {
                if ((mesPos != -1) && ((everyMonth) || ((!everyMonth) && (aux.get(mesPos).matches("^(13|total|TOTAL|Total)$")))) || (semestrePos != -1) || ((mesPos == -1) && (semestrePos == -1))) {
                    if ((nation == null) || (nationPos == -1)) {
                        strings.add(aux.get(column));
                    } else {
                        if (aux.get(nationPos).equals(nation)) {
                            strings.add(aux.get(column));
                        }
                    }
                }
            }
        }

        return strings.toArray(new String[strings.size()]);

    }



}
