package com.github.danielm94.database.repository;

import com.github.danielm94.database.schemas.DatabaseSchemas;
import com.github.danielm94.database.schemas.bookstoreapi.DatabaseTables;
import com.github.danielm94.server.domain.book.Book;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static com.github.danielm94.ConnectionPoolManager.getInstance;
import static com.github.danielm94.database.schemas.DatabaseSchemas.*;
import static com.github.danielm94.database.schemas.bookstoreapi.DatabaseTables.*;
import static com.github.danielm94.database.schemas.bookstoreapi.books.BooksColumn.*;
import static java.lang.String.format;

@Flogger
public class BookRepository {
    public static final DatabaseSchemas SCHEMA = BOOKSTOREAPI;
    public static final DatabaseTables TABLE = BOOKS;
    public static final String SELECT_BOOK_BY_ID_FORMATTABLE_QUERY = "select * from %s.%s where %s = '%s';";
    public static final String CREATE_NEW_BOOK_FORMATTABLE_QUERY = "insert into %s.%s(%s,%s,%s,%s,%s,%s,%s) values('%s','%s','%s','%s','%s','%s','%s');";
    public static final String GET_ALL_BOOKS_FORMATTABLE_QUERY = "select * from %s.%s;";
    public static final String GET_NUMBER_OF_BOOKS_WITH_ID_FORMATTABLE_QUERY = "select count(*) from %s.%s where %s = '%s';";
    public static final String UPDATE_BOOK_FORMATTABLE_QEURY = "UPDATE %s.%s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?";

    private BookRepository() {
    }

    public static int createBook(@NonNull Book book) throws SQLException, InterruptedException {
        log.atFine().log("Creating book in %s.%s table for the book:\n%s", SCHEMA, TABLE, book);
        val connection = getInstance().getConnection();

        val statement = connection.createStatement();
        val query = format(CREATE_NEW_BOOK_FORMATTABLE_QUERY,
                SCHEMA, TABLE,
                ID, BOOK_NAME, AUTHOR, ISBN, PRICE, DATE_ADDED, DATE_UPDATED,
                book.getId(), book.getBookName(), book.getAuthor(), book.getIsbn(), book.getPrice(), book.getDateAdded(), book.getDateUpdated());

        log.atFine().log("Executing query: %s", query);
        val rowsAffected = statement.executeUpdate(query);
        log.atFine().log("Query has finished executing. Rows affected: %d", rowsAffected);

        getInstance().returnConnection(connection);
        return rowsAffected;
    }

    public static ResultSet getBook(@NonNull UUID uuid) throws SQLException, InterruptedException {
        log.atFine().log("Getting book by id: %s", uuid);
        val connection = getInstance().getConnection();

        val statement = connection.createStatement();
        val query = format(SELECT_BOOK_BY_ID_FORMATTABLE_QUERY, SCHEMA, TABLE, ID, uuid);

        log.atFine().log("Executing query: %s", query);
        val resultSet = statement.executeQuery(query);
        log.atFine().log("Query has been successfully executed.");

        getInstance().returnConnection(connection);
        return resultSet;
    }

    public static ResultSet getBooks() throws SQLException, InterruptedException {
        log.atFine().log("Getting all books...");
        val connection = getInstance().getConnection();

        val statement = connection.createStatement();
        val query = format(GET_ALL_BOOKS_FORMATTABLE_QUERY, SCHEMA, TABLE);

        log.atFine().log("Executing query: %s", query);
        val resultSet = statement.executeQuery(query);
        log.atFine().log("Query has been successfully executed.");

        getInstance().returnConnection(connection);
        return resultSet;
    }

    public static ResultSet getNumberOfBooksWithId(@NonNull UUID uuid) throws SQLException, InterruptedException {
        log.atFine().log("Getting the number of books");
        val connection = getInstance().getConnection();

        val statement = connection.createStatement();
        val query = format(GET_NUMBER_OF_BOOKS_WITH_ID_FORMATTABLE_QUERY, SCHEMA, TABLE, ID, uuid);

        log.atFine().log("Executing query: %s", query);
        val resultSet = statement.executeQuery(query);
        log.atFine().log("Query has been successfully executed.");

        getInstance().returnConnection(connection);
        return resultSet;
    }

    public static int updateBook(@NonNull Book book) throws MissingBookIDException, SQLException, InterruptedException {
        val id = book.getId();
        if (id == null) {
            val message = String.format("Expected the book to have an ID, but it was null. The book in question:\n%s", book);
            log.atWarning().log(message);
            throw new MissingBookIDException(message);
        }

        log.atFine().log("Updating book with ID of [%s] with new book:\n%s", id, book);
        val connection = getInstance().getConnection();

        val query = String.format(UPDATE_BOOK_FORMATTABLE_QEURY,
                SCHEMA,
                TABLE,
                BOOK_NAME,
                AUTHOR,
                ISBN,
                PRICE,
                DATE_ADDED,
                DATE_UPDATED,
                ID
        );

        val statement = connection.prepareStatement(query);
        statement.setString(1, book.getBookName());
        statement.setString(2, book.getAuthor());
        statement.setString(3, book.getIsbn());
        statement.setBigDecimal(4, book.getPrice());
        statement.setObject(5, book.getDateAdded());
        statement.setObject(6, book.getDateUpdated());
        statement.setString(7, id.toString());

        val affectedRows = statement.executeUpdate();
        log.atFine()
           .log("Successfully executed query to update book with an ID of [%s]. Number of rows affected: %d", id, affectedRows);
        return affectedRows;
    }
}
