package com.github.danielm94.database.resultset;

import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Flogger
public class ResultSetParser {

    public static final int FIRST_COLUMN_INDEX = 1;

    private ResultSetParser() {
    }

    public static List<Map<String, Object>> parseResultSetToListOfMaps(@NonNull ResultSet resultSet) throws SQLException {
        log.atFine().log("Parsing result set to a list of maps...");
        val meta = resultSet.getMetaData();

        val columnCount = meta.getColumnCount();
        val columnNames = new String[columnCount];
        for (var i = 0; i < columnCount; i++) {
            columnNames[i] = meta.getColumnName(i + 1);
        }

        val resultTable = new ArrayList<Map<String, Object>>();
        var rowMap = new HashMap<String, Object>();

        while (resultSet.next()) {
            for (var i = 0; i < columnCount; i++) {
                val columnName = columnNames[i];
                val columnValue = resultSet.getObject(i + 1);
                log.atFinest().log("Adding {%s:%s} to row map.", columnName, columnValue);
                rowMap.put(columnName, columnValue);
            }
            resultTable.add(rowMap);
            rowMap = new HashMap<>();
        }

        log.atFine().log("Successfully parsed result set to a list of maps. Number of rows: %d", resultTable.size());
        return resultTable;
    }

    public static Integer parseResultSetToInteger(@NonNull ResultSet resultSet) throws SQLException {
        log.atFine().log("Parsing result set to an integer");
        val integer = resultSet.getInt(FIRST_COLUMN_INDEX);
        log.atFine().log("Found integer value %s from result set", integer);
        return integer;
    }

    public static String parseResultToString(@NonNull ResultSet resultSet) throws SQLException {
        log.atFine().log("Parsing result set to an string");
        val string = resultSet.getString(FIRST_COLUMN_INDEX);
        log.atFine().log("Found string value %s from result set", string);
        return string;
    }
}
