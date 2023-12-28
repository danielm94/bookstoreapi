package com.github.danielm94.database.repository;

import com.github.danielm94.ConnectionPoolManager;
import com.github.danielm94.database.schemas.DatabaseSchemas;
import com.github.danielm94.database.schemas.bookstoreapi.DatabaseTables;
import com.github.danielm94.server.domain.book.Book;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.sql.SQLException;

import static com.github.danielm94.database.schemas.bookstoreapi.books.BooksColumn.*;

@Flogger
public class BookRepository {
    public static final DatabaseSchemas SCHEMA = DatabaseSchemas.BOOKSTOREAPI;
    public static final DatabaseTables TABLE = DatabaseTables.BOOKS;

    private BookRepository() {
    }

    public static boolean createBook(Book book) throws SQLException, InterruptedException {
        log.atFine().log("Creating book in %s.%s table for the book:\n%s", SCHEMA, TABLE, book);
        val connection = ConnectionPoolManager.getInstance().getConnection();

        val statement = connection.createStatement();
        val query = String.format("insert into %s.%s(%s,%s,%s,%s,%s,%s,%s) values('%s','%s','%s','%s','%s','%s','%s')",
                SCHEMA, TABLE,
                ID, BOOK_NAME, AUTHOR, ISBN, PRICE, DATE_ADDED, DATE_UPDATED,
                book.getId(), book.getBookName(), book.getAuthor(), book.getIsbn(), book.getPrice(), book.getDateAdded(), book.getDateUpdated());
        log.atFine().log("Executing query: %s", query);
        val rowsAffected = statement.executeUpdate(query);
        log.atFine().log("Query has finished executing. Rows affected: %d", rowsAffected);
        ConnectionPoolManager.getInstance().returnConnection(connection);
        return rowsAffected > 0;
    }
}
