package com.github.danielm94.database.migration;

import com.github.danielm94.ConnectionPoolManager;
import com.github.danielm94.database.resultset.ResultSetParser;
import com.github.danielm94.database.schemas.DatabaseSchemas;
import com.github.danielm94.database.schemas.bookstoreapi.DatabaseTables;
import lombok.NonNull;
import lombok.val;

import java.sql.SQLException;

import static com.github.danielm94.database.schemas.bookstoreapi.books.BooksColumn.*;

public class DatabaseMigrationUtil {
    public static final String CREATE_SCHEMA_QUERY_FORMATABLE_STRING = "CREATE DATABASE IF NOT EXISTS %s;";
    public static final String CHECK_IF_SCHEMA_EXISTS_QUERY = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";

    public static final String CHECK_IF_TABLE_EXISTS_QUERY =
            "SELECT * FROM information_schema.tables " +
                    "WHERE table_schema = ? " +
                    "AND table_name = ?;";

    private DatabaseMigrationUtil() {
    }

    public static void createDatabaseSchema(@NonNull DatabaseSchemas schema) throws SQLException, InterruptedException {
        val connection = ConnectionPoolManager.getInstance().getConnection();
        val statement = connection.createStatement();
        val query = String.format(CREATE_SCHEMA_QUERY_FORMATABLE_STRING, schema.getSchemaName());
        statement.execute(query);
        ConnectionPoolManager.getInstance().returnConnection(connection);
    }

    public static boolean schemaExists(@NonNull DatabaseSchemas schema) throws SQLException, InterruptedException {
        val connection = ConnectionPoolManager.getInstance().getConnection();
        val statement = connection.prepareStatement(CHECK_IF_SCHEMA_EXISTS_QUERY);
        statement.setString(1, schema.getSchemaName());
        val result = statement.executeQuery();
        val resultMapList = ResultSetParser.parseResultSetToListOfMaps(result);
        ConnectionPoolManager.getInstance().returnConnection(connection);
        return !resultMapList.isEmpty() && !resultMapList.getFirst().isEmpty();
    }

    public static boolean tableExists(@NonNull DatabaseSchemas schema, @NonNull DatabaseTables table) throws SQLException, InterruptedException {
        val connection = ConnectionPoolManager.getInstance().getConnection();
        val statement = connection.prepareStatement(CHECK_IF_TABLE_EXISTS_QUERY);
        statement.setString(1, schema.getSchemaName());
        statement.setString(2, table.getTableName());
        val result = statement.executeQuery();
        val resultMapList = ResultSetParser.parseResultSetToListOfMaps(result);
        ConnectionPoolManager.getInstance().returnConnection(connection);
        return !resultMapList.isEmpty() && !resultMapList.getFirst().isEmpty();
    }

    public static void createBooksTable() throws SQLException, InterruptedException {
        val connection = ConnectionPoolManager.getInstance().getConnection();
        val statement = connection.createStatement();
        var query = new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS ").append(DatabaseSchemas.BOOKSTOREAPI.getSchemaName())
                .append(".").append(DatabaseTables.BOOKS.getTableName()).append("(")
                .append(ID.getColumnName()).append(" CHAR(36) NOT NULL, ")
                .append(BOOK_NAME.getColumnName()).append(" VARCHAR(255) NOT NULL, ")
                .append(AUTHOR.getColumnName()).append(" VARCHAR(255) NOT NULL, ")
                .append(ISBN.getColumnName()).append(" VARCHAR(20), ")
                .append(PRICE.getColumnName()).append(" DECIMAL(10, 2) NOT NULL, ")
                .append(DATE_ADDED.getColumnName()).append(" DATETIME NOT NULL, ")
                .append(DATE_UPDATED.getColumnName()).append(" DATETIME NOT NULL, ")
                .append("PRIMARY KEY (").append(ID.getColumnName()).append("));")
                .toString();
        statement.execute(query);
        ConnectionPoolManager.getInstance().returnConnection(connection);
    }
}
