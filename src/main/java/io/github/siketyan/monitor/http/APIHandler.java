package io.github.siketyan.monitor.http;

import io.github.siketyan.monitor.TempMonitor;
import io.github.siketyan.monitor.object.APIType;
import io.github.siketyan.monitor.util.SQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class APIHandler {
    private static final String RECORD_TABLE = "records";
    private static final String HOUR_TABLE= "hours";
    private static final String DAY_TABLE = "days";
    private static final String MONTH_TABLE = "months";
    private static final String YEAR_TABLE = "years";
    private static final String RECORD_SELECT = "SELECT GROUP_CONCAT('\"',q.hour,'\"'),GROUP_CONCAT('\"',q.minute,'\"')";
    private static final String HOUR_SELECT = "SELECT GROUP_CONCAT('\"',q.day,'\"'),GROUP_CONCAT('\"',q.hour,'\"')";
    private static final String DAY_SELECT = "SELECT GROUP_CONCAT('\"',q.month,'\"'),GROUP_CONCAT('\"',q.day,'\"')";
    private static final String MONTH_SELECT = "SELECT GROUP_CONCAT('\"',q.year,'\"'),GROUP_CONCAT('\"',q.month,'\"')";
    private static final String YEAR_SELECT = "SELECT GROUP_CONCAT('\"',q.year,'\"'),''";
    private static final String SUB_QUERY = ",GROUP_CONCAT(q.temp),GROUP_CONCAT(q.hum),GROUP_CONCAT(q.pres) "
        + "FROM (SELECT * FROM (SELECT * FROM {{TABLE}} ORDER BY id DESC limit 24) sq ORDER BY sq.id ASC) q;";
    
    private SQLManager sql;
    
    APIHandler() {
        sql = TempMonitor.getSQL();
    }
    
    String[] handle(APIType type) {
        try (Statement stmt = sql.getStatement();
             ResultSet rs = stmt.executeQuery(getSelectQuery(type))) {
            rs.next();
            return new String[]{
                "[" + rs.getString(1) + "]",
                "[" + rs.getString(2) + "]",
                "[" + rs.getString(3) + "]",
                "[" + rs.getString(4) + "]",
                "[" + rs.getString(5) + "]",
            };
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private String getSelectQuery(APIType type) {
        String select, table;
        switch (type) {
            case RECORD:
                select = RECORD_SELECT;
                table = RECORD_TABLE;
                break;
                
            default:
            case HOUR:
                select = HOUR_SELECT;
                table = HOUR_TABLE;
                break;
                
            case DAY:
                select = DAY_SELECT;
                table = DAY_TABLE;
                break;
    
            case MONTH:
                select = MONTH_SELECT;
                table = MONTH_TABLE;
                break;
    
            case YEAR:
                select = YEAR_SELECT;
                table = YEAR_TABLE;
                break;
        }
        
        return select + SUB_QUERY.replace("{{TABLE}}", table);
    }
}