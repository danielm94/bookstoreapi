package com.github.danielm94.database.migration;

import java.sql.SQLException;

public class MissingTableException extends SQLException {

    public MissingTableException(String reason) {
        super(reason);
    }


    public MissingTableException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
