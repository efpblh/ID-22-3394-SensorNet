package com.example.testsensornet;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    protected  Connection con;
    protected  String uname, pass, ip, port, database;
    @SuppressLint("NewApi")

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUname(String uname){
        this.uname = uname;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public ConnectionHelper(String ip, String name, String pass, String database) {
        this.setIp(ip);
        this.setDatabase(database);
        //this.setPort(port);
        this.setUname(name);
        this.setPass(pass);
    }

    public Connection connectionclass()
    {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection connection = null;
        String ConnectionURL = null;

        try
        {
            Class.forName("com.mysql.jdbc.Driver");

            ConnectionURL= "jdbc:mysql://" + ip + "/" + database;
            connection = DriverManager.getConnection(ConnectionURL, uname, pass);
        }
        catch(Exception ex){
            Log.e("Server error ", ex.getMessage());
        }
        return connection;
    }
}
