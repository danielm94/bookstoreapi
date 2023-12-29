package com.github.danielm94.server.services.delete;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

import java.sql.SQLException;
import java.util.UUID;

import static com.github.danielm94.database.repository.BookRepository.*;


public class BookRemovalService implements DeleteBookService {

    @Override
    public void delete(@NonNull HttpExchange exchange, @NonNull UUID id) {
        Integer rowsAffected;
        try {
            rowsAffected = deleteBook(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
