package com.github.danielm94.server.domain.book.mappers;

import com.github.danielm94.server.domain.book.BookDTO;

import java.io.IOException;
import java.io.InputStream;

public interface BookDTOMapper {

    BookDTO parseRequestBodyToBookDTO(InputStream stream) throws IOException;
}
