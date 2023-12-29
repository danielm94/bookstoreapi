package com.github.danielm94.server.services.delete;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.val;

import java.sql.SQLException;
import java.util.UUID;

import static com.github.danielm94.database.repository.BookRepository.*;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static java.net.HttpURLConnection.*;


public class BookRemovalService implements DeleteBookService {

    @Override
    public void delete(@NonNull HttpExchange exchange, @NonNull UUID id) {
        int rowsAffected;
        try {
            rowsAffected = deleteBook(id);
        } catch (SQLException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while deleting the book from the database.");
            return;
        } catch (InterruptedException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while managing connections to the database.");
            return;
        }

        val bookWasDeleted = rowsAffected == 1;
        if (bookWasDeleted) {
            sendResponse(exchange, HTTP_OK, "The book was deleted.");
        } else {
            sendResponse(exchange, HTTP_NOT_FOUND, "The book was not deleted because there are no books with a UUID of " + id);
        }
    }
}
