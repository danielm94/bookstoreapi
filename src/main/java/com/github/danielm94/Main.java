package com.github.danielm94;

import com.github.danielm94.config.DefaultPoolConfiguration;
import com.github.danielm94.credentials.PropertyFileConnectionCredentials;
import com.github.danielm94.database.migration.MissingSchemaException;
import com.github.danielm94.database.migration.MissingTableException;
import com.github.danielm94.database.schemas.DatabaseSchemas;
import com.github.danielm94.database.schemas.bookstoreapi.DatabaseTables;
import com.github.danielm94.server.BookServer;
import com.github.danielm94.server.executors.BookServerExecutor;
import com.github.danielm94.server.handlers.RequestHandler;
import com.github.danielm94.server.handlers.SimpleResponseHandler;
import com.github.danielm94.server.handlers.book.BookHandler;
import com.sun.net.httpserver.HttpHandler;
import lombok.val;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.github.danielm94.database.migration.DatabaseMigrationUtil.*;
import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;

public class Main {
    public static final int SERVER_PORT = 8000;
    public static final String DEBUG_ENDPOINT = "/";
    public static final String BOOK_STORE_API_ENDPOINT = "/api/books/";
    public static final String DATABASE_PROPERTIES_FILE_PATH = "src/main/resources/db.properties";
    public static final Pattern BOOKS_ENDPOINT_DYNAMIC_PATH_PATTERN = Pattern.compile(BOOK_STORE_API_ENDPOINT + "([^/]+)");

    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        initializeDatabase();
        val server = initializeAndConfigureServer();
        server.start();
    }

    private static BookServer initializeAndConfigureServer() {
        val server = new BookServer();
        val address = new InetSocketAddress(InetAddress.getLoopbackAddress(), SERVER_PORT);
        server.bind(address, 0);
        server.setExecutor(new BookServerExecutor());

        server.registerDynamicPath(BOOKS_ENDPOINT_DYNAMIC_PATH_PATTERN, params -> {
            val context = server.createContext(BOOK_STORE_API_ENDPOINT);
            val bookId = params.get("bookId");
            HttpHandler handler;
            try {
                UUID bookUUID = UUID.fromString(bookId);
                handler = new BookHandler();
                context.getAttributes().put(BOOK_ID.toString(), bookUUID);
            } catch (IllegalArgumentException e) {
                handler = new SimpleResponseHandler(HttpURLConnection.HTTP_BAD_REQUEST, bookId + " is not a valid UUID.");
            }
            context.setHandler(handler);
            return context;
        });

        val bookContext = server.createContext(BOOK_STORE_API_ENDPOINT);
        bookContext.setHandler(new BookHandler());

        val debugContext = server.createContext(DEBUG_ENDPOINT);
        debugContext.setHandler(new RequestHandler());

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