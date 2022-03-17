package uk.ac.ed.yazzzam.database;

import uk.ac.ed.yazzzam.GlobalSettings;

public class Database {
    private static Sql2oModel model;

    private static void setupDatabase() {
        ConnectDB conn = new ConnectDB("jdbc:postgresql://localhost:5432/" + GlobalSettings.DB_NAME, "postgres", "ttds_YAZZZAM123");
        conn.connect();
        model = conn.getModel();
    }

    public static Sql2oModel getModel() {
        if (model == null) {
            setupDatabase();
        }
        return model;
    }
}
