package com.albdgsldev.turismotenerife;

import android.content.Context;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AsyncTaskTableValues  extends AsyncTask<Void, Void, String> {

    public com.albdgsldev.turismotenerife.AsyncResponse delegate = null;

    private final Table table;
    private String url;
    private String user;
    private String pass;

    public AsyncTaskTableValues(Table table, Context context, com.albdgsldev.turismotenerife.AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        this.table = table;
        url = context.getResources().getString(R.string.url);
        user = context.getResources().getString(R.string.user);
        pass = context.getResources().getString(R.string.pass);
    }

    @Override
    protected String doInBackground(Void... params) {
        ArrayList<String> auxValues = new ArrayList<String>();
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pass);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from `" + table.getTableName() + "`");
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();

            for(int i = 1; i<=count; i++) {
                auxValues.add(rsMetaData.getColumnName(i));
            }
            table.setColumnNames(auxValues);

            auxValues = new ArrayList<String>();

            table.emptyTableValues();
            while (rs.next()) {
                for (int i = 1; i <= count; i++){
                    auxValues.add(rs.getString(i));
                }
                table.addTableRow(auxValues);
                auxValues = new ArrayList<String>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(con != null){
                try {
                    con.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return "Complete";
    }

    protected void onPostExecute(String result) {
        if (result.equals("Complete")) {
            delegate.processFinish(result);
        }
    }
}
