package com.github.danielm94.database.resultset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.danielm94.server.domain.Book;
import com.github.danielm94.ConnectionPoolManager;
import com.github.danielm94.config.DefaultPoolConfiguration;
import com.github.danielm94.credentials.PropertyFileConnectionCredentials;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

class ResultSetParserTest {
    @Test
    void name() throws IOException, SQLException, InterruptedException {
        val propertyFile = new FileInputStream("src/main/resources/db.properties");
        val credentials = new PropertyFileConnectionCredentials(propertyFile);
        ConnectionPoolManager.initialize(new DefaultPoolConfiguration(), credentials);
        val connection = ConnectionPoolManager.getInstance().getConnection();
        val statement = connection.createStatement();
        val resultSet = statement.executeQuery("SELECT * FROM classicmodels.customers  where state = \"NY\";");
        val result = ResultSetParser.parseResultSetToListOfMaps(resultSet);
        ConnectionPoolManager.getInstance().returnConnection(connection);
        System.out.println();
        val mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(result));

    }

    @Test
    void oneBook() throws JsonProcessingException {
        val now = LocalDateTime.now();
        val book = Book.builder()
                       .id(UUID.randomUUID())
                       .bookName("A Song of Ice and Fire")
                       .author("George R.R. Martin")
                       .isbn("1234567890123")
                       .price(new BigDecimal("40.40"))
                       .dateAdded(now)
                       .dateUpdated(now)
                       .build();

        val mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        val jsonStr = mapper.writeValueAsString(book);
        System.out.println(jsonStr);
    }
    @Test
    void deSerializer() throws JsonProcessingException {
        val now = LocalDateTime.now();
        val book = Book.builder()
                       .id(UUID.randomUUID())
                       .bookName("A Song of Ice and Fire")
                       .author("George R.R. Martin")
                       .isbn("1234567890123")
                       .price(new BigDecimal("40.40"))
                       .dateAdded(now)
                       .dateUpdated(now)
                       .build();
        val list = new LinkedList<Book>();
        for (var i = 0; i < 20; i++) {
            list.add(book);
        }
        val mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        val jsonStr = mapper.writeValueAsString(list);
        System.out.println(jsonStr);
    }

    @Test
    void serialize() throws JsonProcessingException {
        val json = "{\"id\":\"00e49a78-0ddb-48d8-aba5-93d8c4a17adb\",\"bookName\":\"Game of Thrones\",\"author\":\"Game Of Thrones Guy\",\"isbn\":\"12350321950243\",\"price\":40.04,\"dateAdded\":[2023,12,23,2,29,32,463916400],\"dateUpdated\":[2023,12,23,2,29,32,463916400]}\n";
        val mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        val book = mapper.readValue(json, Book.class);
        System.out.println();
    }

    @Test
    void serializeList() throws JsonProcessingException {
        val json = "[{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]},{\"id\":\"c1f728d9-c4da-4ada-b03f-1bd88b2cf0bb\",\"bookName\":\"A Song of Ice and Fire\",\"author\":\"George R.R. Martin\",\"isbn\":\"1234567890123\",\"price\":40.40,\"dateAdded\":[2023,12,23,2,39,15,756173300],\"dateUpdated\":[2023,12,23,2,39,15,756173300]}]";
        val mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        val books = mapper.readValue(json, Book[].class);
        val bookList = new ArrayList<Book>(Arrays.asList(books));
        System.out.println();
    }
}