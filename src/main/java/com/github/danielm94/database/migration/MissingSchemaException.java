package com.github.danielm94.database.migration;

import java.sql.SQLException;

public class MissingSchemaException extends SQLException {

    public MissingSchemaException(String reason) {
        super(reason);
    }

}
