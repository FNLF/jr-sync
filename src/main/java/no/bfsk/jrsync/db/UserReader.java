/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package no.bfsk.jrsync.db;

import java.util.List;
import java.util.Properties;

/**
 * 
 * @author glenn
 */
public class UserReader extends AbstractDatabaseReader {
    private enum UserColumns {
        NAME("sCust"),
        USERID("wCustId"),
        USPANO("sUSPANo"),
        USPA_EXPIRE("dtUSPAExpire"),
        BALANCE("-cTotBal"),
        RESERVE_PACKED("dtReservePacked"),
        LAST_JUMP("dtLastJump"),
        EMAIL("sEmail");
        
        UserColumns(String value) {
            this.value = value;
        }

        static String toSQLString() {
            StringBuilder sb = new StringBuilder();
            for(UserColumns c : UserColumns.values()) {
                if(sb.length() > 0) sb.append(",");
                sb.append(c.getValue());
            }
            return sb.toString();
        }

        public String getValue() {
            return value;
        }
        
        private String value;
    }
    
    private static final String USER_QUERY = "select " + UserColumns.toSQLString() + " from viewCust";
    private static final String USER_FILENAME = "jrusers.csv";

    public UserReader(Properties p) {
        super(USER_FILENAME, p);
        p.put("user.filename", USER_FILENAME);
    }

    @Override
    protected String getQuery() {
        String query = USER_QUERY;
        String skipUsers = props.getProperty("user.skip");
        if(skipUsers != null && !skipUsers.isEmpty()) {
            query += " where wCustId not in(" + skipUsers + ")";
        }

        return query;
    }

    @Override
    // userId | balance | nak   | name         | email
    protected String prepareRow(List<String> columns) {
        if(columns.size() != UserColumns.values().length) {
            System.err.println("Invalid number of columns: " + columns.size());
            return null;
        }
        StringBuilder rowBuilder = new StringBuilder();
        rowBuilder.append(columns.get(UserColumns.USERID.ordinal())).append(";");
        rowBuilder.append(columns.get(UserColumns.BALANCE.ordinal())).append(";");
        rowBuilder.append(cleanString(columns.get(UserColumns.USPANO.ordinal()))).append(";");
        rowBuilder.append(cleanString(columns.get(UserColumns.NAME.ordinal()))).append(";");
        rowBuilder.append(cleanString(columns.get(UserColumns.EMAIL.ordinal())));

        return rowBuilder.toString();
    }

}
