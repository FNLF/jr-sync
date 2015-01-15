/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package no.bfsk.jrsync.db;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author glenn
 *
 * date(0);userId(1);amount(2);comment(3);description(4);type(5);item(6)
 */
public class InvoiceReader extends AbstractDatabaseReader {
    private enum InvoiceColumns {
        PROCESS_DATE("dtProcess"),
        ID("wId"),
        MANIFEST("nMani"),
        USERID("wCustId"),
        PRICE("-cPrice"),
        COMMENT("sComment"),
        PLANE_NAME("sPlaneName"),
        ITEM("sItem"),
        ITEM_ID("wItemId");

        InvoiceColumns(String value) {
            this.value = value;
        }

        static String toSQLString() {
            StringBuilder sb = new StringBuilder();
            for(InvoiceColumns c : InvoiceColumns.values()) {
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

    private static final String INVOICE_QUERY = "select " + InvoiceColumns.toSQLString() + " from dbo.viewInvoice";
    private static final String INVOICE_FILENAME = "jrinvoices.csv";

    private List<String> nonJumps = null;

    public InvoiceReader(Properties p) {
        super(INVOICE_FILENAME, p);
        String nj = props.getProperty("price_item.nojump");
        if(nj != null && nj.length() > 0) {
            nonJumps = Arrays.asList(nj.split(","));
        }
        p.put("invoice.filename", INVOICE_FILENAME);
    }

    @Override
    protected String getQuery() {
        String query = INVOICE_QUERY;
        String skipUsers = props.getProperty("user.skip");
        if(skipUsers != null && !skipUsers.isEmpty()) {
            query += " where wCustId not in(" + skipUsers + ")";
        }

        return query;
    }

    // date(0);userId(1);amount(2);comment(3);description(4);type(5);item(6)
    @Override
    protected String prepareRow(List<String> columns) {
        if(columns.size() != InvoiceColumns.values().length) {
            System.err.println("Invalid number of columns: " + columns.size());
            return null;
        }
        StringBuilder rowBuilder = new StringBuilder();
        rowBuilder.append(columns.get(InvoiceColumns.PROCESS_DATE.ordinal())).append(";");
        rowBuilder.append(columns.get(InvoiceColumns.USERID.ordinal())).append(";");
        rowBuilder.append(columns.get(InvoiceColumns.PRICE.ordinal())).append(";");
        rowBuilder.append(cleanString(columns.get(InvoiceColumns.COMMENT.ordinal()))).append(";");
        rowBuilder.append(cleanString(columns.get(InvoiceColumns.PLANE_NAME.ordinal())))
                  .append(" ").append(columns.get(InvoiceColumns.MANIFEST.ordinal())).append(";");

        String manifest = columns.get(InvoiceColumns.MANIFEST.ordinal());
        String itemId = columns.get(InvoiceColumns.ITEM_ID.ordinal());
        if(itemId != null && nonJumps.contains(itemId)) {
            rowBuilder.append("2;");
        } else {
            rowBuilder.append("0".equals(manifest) ? "2" : "1").append(";");
        }
        rowBuilder.append(cleanString(columns.get(InvoiceColumns.ITEM.ordinal())));

        return rowBuilder.toString();
    }
}
