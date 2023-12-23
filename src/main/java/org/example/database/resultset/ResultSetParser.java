package org.example.database.resultset;

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

        log.atFine().log("Successfully parsed result set to a list of maps.");
        return resultTable;
    }
}
