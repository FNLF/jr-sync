/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package no.bfsk.jrsync.db;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author glenn
 */

public abstract class AbstractDatabaseReader {
    private String filename;
    protected Properties props;

    public AbstractDatabaseReader(String filename, Properties p) {
        this.props = p;
        String tmpDir = props.getProperty("jrsync.tmpdir");
        if(tmpDir == null || tmpDir.trim().length() == 0) {
            tmpDir = System.getProperty("java.io.tmpdir");
        }
        this.filename = tmpDir.trim() + File.separator + filename;
    }

    public void select() {
        select(getQuery());
    }

    public void selectByDate(Date dateFrom, Date dateTo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(getQuery());
        if(dateFrom != null && dateTo != null) {
            queryBuilder.append(queryBuilder.indexOf(" where ") > 0 ? " and" : " where").append(" dtInsert >= '").append(sdf.format(dateFrom)).append("'");
            queryBuilder.append(" and dtInsert < '").append(sdf.format(dateTo)).append("'");
        } else if(dateFrom != null) {
            queryBuilder.append(queryBuilder.indexOf(" where ") > 0 ? " and" : " where").append(" dtInsert >= '").append(sdf.format(dateFrom)).append("'");
        } else if(dateTo != null) {
            queryBuilder.append(queryBuilder.indexOf(" where ") > 0 ? " and" : " where").append(" dtInsert < '").append(sdf.format(dateTo)).append("'");
        }

        select(queryBuilder.toString());
    }

    private void select(String query) {
        Connection conn = null;
        Statement s = null;
        ResultSet rs = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(filename, false);
            conn = getConnection();
            s = conn.createStatement();
            rs = s.executeQuery(query);
            List<String> columns = new ArrayList<String>(rs.getMetaData().getColumnCount());
            while(rs.next()) {
                columns.clear();
                for(int i=1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String col = rs.getString(i);
                    columns.add(col == null ? "" : col.trim());
                }
                String row = prepareRow(columns);
                if(row != null) {
                    fw.append(row).append("\n");
                }
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(rs != null) try { rs.close(); rs = null; }catch(Exception ex) { }
            if(s != null) try { s.close(); s = null; }catch(Exception ex) { }
            if(conn != null) try {conn.close(); conn = null; }catch(Exception ex) { }
            if(fw != null) try {fw.close(); fw = null; }catch(Exception ex) { }
        }
    }

    public String getFilename() {
        return filename;
    }

    protected String cleanString(String original) {
        if(original == null) return "";
        return original.replaceAll("[\"',;:)(]+", " ").replaceAll("\n", " ")
                .replaceAll("\r", " ");
    }

    protected abstract String prepareRow(List<String> columns);
    protected abstract String getQuery();

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(props.getProperty("db.url"));
        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
