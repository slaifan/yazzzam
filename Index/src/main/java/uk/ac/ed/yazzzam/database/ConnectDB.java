package uk.ac.ed.yazzzam.database;

import org.sql2o.Sql2o;


public class ConnectDB {

    private String jdbcURL;
    private String username;
    private String password;
    private Sql2o sql2o;
    public Sql2oModel model;

    public ConnectDB(String jdbcURL, String username, String password) {
        this.jdbcURL = jdbcURL;
        this.username = username;
        this.password = password;
    }

    // Connect to the database
    public void connect(){
        sql2o = new Sql2o(jdbcURL, username, password);
    }


    // Creates the model and returns it
    public Sql2oModel getModel(){
        model = new Sql2oModel(sql2o);
        return model;
    }

}
