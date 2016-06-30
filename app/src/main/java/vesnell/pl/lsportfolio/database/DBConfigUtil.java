package vesnell.pl.lsportfolio.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import vesnell.pl.lsportfolio.database.model.Image;
import vesnell.pl.lsportfolio.database.model.Project;
import vesnell.pl.lsportfolio.database.model.Store;

/**
 * Created by alek6 on 20.04.2016.
 */
public class DBConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[] {
            Project.class, Image.class, Store.class
    };

    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile(new File("app/src/main/res/raw/ormlite_config.txt"), classes);
    }

}
