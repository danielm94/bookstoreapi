package com.github.danielm94;

import com.github.danielm94.config.DefaultPoolConfiguration;
import com.github.danielm94.credentials.PropertyFileConnectionCredentials;
import com.github.danielm94.database.migration.MissingTableException;
import com.github.danielm94.database.schemas.DatabaseSchemas;
import com.github.danielm94.database.migration.MissingSchemaException;
import com.github.danielm94.database.schemas.bookstoreapi.DatabaseTables;
import com.github.danielm94.server.BookServer;
import com.github.danielm94.server.executors.BookServerExecutor;
import com.github.danielm94.server.handlers.BookHandler;
import com.github.danielm94.server.handlers.RequestHandler;
import lombok.val;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import static com.github.danielm94.database.migration.DatabaseMigrationUtil.*;

public class Main {
    public static final int SERVER_PORT = 8000;
    public static final String DEBUG_ENDPOINT = "/";
    public static final String BOOK_STORE_API_ENDPOINT = "/api/books";
    public static final String DATABASE_PROPERTIES_FILE_PATH = "src/main/resources/db.properties";

    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        initializeDatabase();
        val server = initializeAndConfigureServer();
        server.start();
    }

    private static BookServer initializeAndConfigureServer() {
        var server = new BookServer();
        var address = new InetSocketAddress(InetAddress.getLoopbackAddress(), SERVER_PORT);
        server.bind(address, 0);
        server.setExecutor(new BookServerExecutor());

        var debugContext = server.createContext(DEBUG_ENDPOINT);
        debugContext.setHandler(new RequestHandler());

        var bookContext = server.createContext(BOOK_STORE_API_ENDPOINT);
        bookContext.setHandler(new BookHandler());
        return server;
    }

    private static void initializeDatabase() throws SQLException, IOException, InterruptedException {
        val schema = DatabaseSchemas.BOOKSTOREAPI;
        val table = DatabaseTables.BOOKS;
        initializeConnectionPoolManager();
        createDatabaseSchema(schema);
        if (!schemaExists(schema)) {
            val exceptionMessage = String.format("Required schema %s is missing from the database.", schema);
            throw new MissingSchemaException(exceptionMessage);
        }
        createBooksTable();

        if (!tableExists(schema, table)) {
            val exceptionMessage = String.format("Required table %s in schema %s is missing from the database", schema, table);
            throw new MissingTableException(exceptionMessage);
        }
    }

    private static void initializeConnectionPoolManager() throws IOException, SQLException {
        val propertyFile = new FileInputStream(DATABASE_PROPERTIES_FILE_PATH);
        val credentials = new PropertyFileConnectionCredentials(propertyFile);
        ConnectionPoolManager.initialize(new DefaultPoolConfiguration(), credentials);
    }
}