package vesnell.pl.lsportfolio.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.database.model.Image;
import vesnell.pl.lsportfolio.database.model.Project;
import vesnell.pl.lsportfolio.database.model.Store;

public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "lsportfolio.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.setForeignKeyConstraintsEnabled(true);
            } else {
                db.execSQL("PRAGMA foreign_keys=ON");
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Project.class);
            TableUtils.createTable(connectionSource, Image.class);
            TableUtils.createTable(connectionSource, Store.class);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Store.class, true);
            TableUtils.dropTable(connectionSource, Image.class, true);
            TableUtils.dropTable(connectionSource, Project.class, true);
            onCreate(database, connectionSource);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
