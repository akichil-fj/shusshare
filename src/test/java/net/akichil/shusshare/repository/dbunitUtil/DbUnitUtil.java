package net.akichil.shusshare.repository.dbunitUtil;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.ext.h2.H2Connection;
import org.dbunit.operation.DatabaseOperation;

import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;

import static org.dbunit.Assertion.assertEqualsIgnoreCols;

public class DbUnitUtil {

    public static void assertMutateResults(DataSource dataSource, String tableName, String expectedResultPath, String... skipCols)
            throws DatabaseUnitException, SQLException {
        IDatabaseConnection connection = getConnection(dataSource);
        ITable expected = getCsvTable(expectedResultPath, tableName);
        ITable actual = getCurrentTable(connection, tableName);
        assertEqualsIgnoreCols(expected, actual, skipCols);
        connection.close();
    }

    private static ITable getCurrentTable(IDatabaseConnection connection, String tableName) throws SQLException, DataSetException {
        return connection.createDataSet().getTable(tableName);
    }

    private static ITable getCsvTable(String resourcePath, String tableName) throws DataSetException {
        IDataSet csvDataSet = new CsvDataSet(new File(resourcePath));
        return csvDataSet.getTable(tableName);
    }

    private static IDatabaseConnection getConnection(DataSource dataSource) throws SQLException, DatabaseUnitException {
        return new H2Connection(dataSource.getConnection(), "s_share");
    }

    public static void loadData(DataSource dataSource, String resourcePath) throws SQLException, DatabaseUnitException {
        IDatabaseConnection connection = getConnection(dataSource);
        CsvDataSet csvDataSet = new CsvDataSet(new File(resourcePath));
        DatabaseOperation.CLEAN_INSERT.execute(connection, csvDataSet);
        connection.close();
    }

}
