package com.albdgsldev.turismotenerife;

import android.content.Context;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class AsyncTaskTableNames extends AsyncTask<Void, Void, String>{

    public com.albdgsldev.turismotenerife.AsyncResponse delegate = null;

    private Context context;
    private String url;
    private String user;
    private String pass;

    public AsyncTaskTableNames(Context context, com.albdgsldev.turismotenerife.AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        this.context =context;
        url = context.getResources().getString(R.string.url);
        user = context.getResources().getString(R.string.user);
        pass = context.getResources().getString(R.string.pass);
    }

    protected String doInBackground(Void... params) {

        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pass);

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from tablesInfo");


            while (rs.next()) {
                Table table = new Table(rs.getString("table_name"),rs.getString("display_name"), rs.getString("data_set"));
                ChooseSetActivity.tableList.add(table);

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
