package com.github.danielm94.server.domain.book.parsers;

import com.github.danielm94.server.domain.book.BookDTO;

import java.io.IOException;
import java.io.InputStream;

public interface BookDTOParser {

    BookDTO parseRequestBodyToBookDTO(InputStream stream) throws IOException;
}
