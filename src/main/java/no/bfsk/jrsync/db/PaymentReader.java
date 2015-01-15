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
 *
 * date(0);userId(1);amount(2);comment(3);description(4);type(5);item(6)
 */
public class PaymentReader extends AbstractDatabaseReader {
    private enum PaymentColumns {
        PROCESS_DATE("dtProcess"),
        ID("wId"),
        USERID("wCustId"),
        COMMENT("sComment"),
        AMOUNT("-cAmount");

        PaymentColumns(String value) {
            this.value = value;
        }

        static String toSQLString() {
            StringBuilder sb = new StringBuilder();
            for(PaymentColumns c : PaymentColumns.values()) {
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
    
    private static final String PAYMENT_QUERY = "select " + PaymentColumns.toSQLString() + " from dbo.viewPayment";
    private static final String PAYMENT_FILENAME = "jrpayments.csv";

    public PaymentReader(Properties p) {
        super(PAYMENT_FILENAME, p);
        p.put("payment.filename", PAYMENT_FILENAME);
    }

    @Override
    protected String getQuery() {
        String query = PAYMENT_QUERY;
        String skipUsers = props.getProperty("user.skip");
        if(skipUsers != null && !skipUsers.isEmpty()) {
            query += " where wCustId not in(" + skipUsers + ")";
        }

        return query;
    }

    // date(0);userId(1);amount(2);comment(3);description(4);type(5);item(6)
    @Override
    protected String prepareRow(List<String> columns) {
        if(columns.size() != PaymentColumns.values().length) {
            System.err.println("Invalid number of columns: " + columns.size());
            return null;
        }
        StringBuilder rowBuilder = new StringBuilder();
        rowBuilder.append(columns.get(PaymentColumns.PROCESS_DATE.ordinal())).append(";");
        rowBuilder.append(columns.get(PaymentColumns.USERID.ordinal())).append(";");
        rowBuilder.append(columns.get(PaymentColumns.AMOUNT.ordinal())).append(";");
        rowBuilder.append(columns.get(PaymentColumns.COMMENT.ordinal())).append(";");
        rowBuilder.append("N/A;"); // description
        rowBuilder.append("2;"); // type
        double amount = 0.0;
        try {
            amount = Double.parseDouble(columns.get(PaymentColumns.AMOUNT.ordinal()));
        }catch(NumberFormatException ex) {
            System.err.println("Invalid number: " + columns.get(PaymentColumns.AMOUNT.ordinal()));
        }
        rowBuilder.append(amount > 0.0 ? "Innskudd" : "Uttak");

        return rowBuilder.toString();
    }
}
