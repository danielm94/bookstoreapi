package com.github.danielm94.server.domain.book.mappers;

import com.github.danielm94.ConnectionPoolManager;
import com.github.danielm94.config.ConnectionPoolConfiguration;
import com.github.danielm94.config.DefaultPoolConfiguration;
import com.github.danielm94.credentials.ConnectionCredentials;
import com.github.danielm94.credentials.RawConnectionCredentials;
import com.github.danielm94.server.domain.book.Book;
import com.github.danielm94.server.domain.book.BookDTO;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.github.danielm94.database.repository.BookRepository.*;
import static com.github.danielm94.server.domain.book.mappers.BookMapper.*;
import static com.github.danielm94.util.DateTimeFormat.H2_DATABASE_FORMAT;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SuppressWarnings("DataFlowIssue")
class BookMapperTest {
    private static final ConnectionCredentials H2_CREDENTIALS = new RawConnectionCredentials("", "sa", "jdbc:h2:mem:bookstoreapi;DB_CLOSE_DELAY=-1");
    private static final ConnectionPoolConfiguration DEFAULT_CONFIG = new DefaultPoolConfiguration();


    @Test
    void createNewBookFromDTOTest() {
        val dto = new BookDTO();
        dto.setBookName("Test Book");
        dto.setAuthor("Author Name");
        dto.setIsbn("1234567890");
        dto.setPrice(new BigDecimal("29.99"));

        val book = createNewBookFromDTO(dto);
        val now = LocalDateTime.of(2000, 1, 1, 1, 1, 1).truncatedTo(SECONDS);

        assertThat(book.getId())
                .as("Check book receives a random ID upon creation.")
                .isNotNull();
        assertThat(book.getBookName())
                .as("Check book name is correctly mapped from DTO")
                .isEqualTo(dto.getBookName());
        assertThat(book.getAuthor())
                .as("Check author is correctly mapped from DTO")
                .isEqualTo(dto.getAuthor());
        assertThat(book.getIsbn())
                .as("Check ISBN is correctly mapped from DTO")
                .isEqualTo(dto.getIsbn());
        assertThat(book.getPrice())
                .as("Check price is correctly mapped from DTO")
                .isEqualTo(dto.getPrice());
        assertThat(book.getDateAdded())
                .as("Check book receives a Date Added value set close to now.")
                .isBetween(now.minusSeconds(5), now);
        assertThat(book.getDateUpdated())
                .as("Check book receives a Date Updated value set to the Date Added time")
                .isEqualTo(book.getDateAdded());
    }

    @Test
    void exceptionIsThrownWhenPassingNullBookToCreateFromNewBookDTOTest() {
        assertThatThrownBy(() -> createNewBookFromDTO(null))
                .as("NullPointerException should be thrown when passing null book.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void mapFromResultSetTest() throws SQLException, InterruptedException {
        ConnectionPoolManager.initialize(DEFAULT_CONFIG, H2_CREDENTIALS);
        setUpH2Database();
        val localDateTime = LocalDateTime.of(2000, 1, 1, 1, 1, 1).truncatedTo(SECONDS);
        val expectedBook1 = Book.builder()
                                .id(randomUUID())
                                .bookName("Book 1")
                                .author("Author 1")
                                .isbn("1234567891")
                                .price(new BigDecimal("19.99"))
                                .dateAdded(localDateTime)
                                .dateUpdated(localDateTime)
                                .build();
        val expectedBook2 = Book.builder()
                                .id(randomUUID())
                                .bookName("Book 2")
                                .author("Author 2")
                                .isbn("1234567892")
                                .price(new BigDecimal("39.99"))
                                .dateAdded(localDateTime)
                                .dateUpdated(localDateTime)
                                .build();

        createBook(expectedBook1);
        createBook(expectedBook2);

        val resultSet = getBooks();

        val books = mapFromResultSet(resultSet, H2_DATABASE_FORMAT.getFormatter());

        assertThat(books)
                .as("Check if the list contains the expected books")
                .containsExactly(expectedBook1, expectedBook2);
    }

    @Test
    void exceptionIsThrownWhenPassingNullResultSetToMapFromResultSetTest() {
        assertThatThrownBy(() -> mapFromResultSet(null, DateTimeFormatter.ISO_DATE))
                .as("NullPointerException should be thrown when passing null book.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void exceptionIsThrownWhenPassingNullDateTimeFormatterToMapFromResultSetTest() {
        assertThatThrownBy(() -> mapFromResultSet(any(ResultSet.class), null))
                .as("NullPointerException should be thrown when passing null book.")
                .isInstanceOf(NullPointerException.class);
    }

    private void setUpH2Database() throws SQLException, InterruptedException {
        val connection = ConnectionPoolManager.getInstance().getConnection();
        val createSchemaQuery = "CREATE SCHEMA IF NOT EXISTS bookstoreapi;";
        val createTableQuery = "CREATE TABLE IF NOT EXISTS bookstoreapi.books (" +
                "    id CHAR(36) NOT NULL, " +
                "    bookName VARCHAR(255) NOT NULL, " +
                "    author VARCHAR(255) NOT NULL, " +
                "    isbn VARCHAR(20), " +
                "    price DECIMAL(10, 2) NOT NULL, " +
                "    dateAdded DATETIME NOT NULL, " +
                "    dateUpdated DATETIME NOT NULL, " +
                "    PRIMARY KEY (id)" +
                ");";

        val statement = connection.createStatement();
        statement.executeUpdate(createSchemaQuery);
        statement.executeUpdate(createTableQuery);
        ConnectionPoolManager.getInstance().returnConnection(connection);
    }
}
