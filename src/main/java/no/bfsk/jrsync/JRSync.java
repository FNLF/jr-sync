/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package no.bfsk.jrsync;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import no.bfsk.jrsync.db.InvoiceReader;
import no.bfsk.jrsync.db.PaymentReader;
import no.bfsk.jrsync.db.UserReader;
import no.bfsk.jrsync.ui.JRSyncUI;

/**
 *
 * @author glenn
 *
 */
public class JRSync {
    private static final String PROPERTY_FILE_NAME = "jrsync.properties";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        JRSyncUI ui = new JRSyncUI();
        ui.setVisible(true);
        
        String propertyFileName = PROPERTY_FILE_NAME;
        if (args.length > 0) {
            propertyFileName = args[0];
        }
		
        Properties p = new Properties();
        try {
            p.load(new FileReader(propertyFileName));
        } catch(IOException ex) {
            ui.setMessage("Fila " + propertyFileName + " manglar!");
            try {
                Thread.sleep(5000);
            }catch(InterruptedException ie) { }
            
            System.exit(1);
        }

        ui.setMessage("Grev fram brukarane");
        new UserReader(p).select();
        ui.setMessage("Ser kva folk har betalt inn eller tatt ut");
        new PaymentReader(p).select();
        ui.setMessage("Hentar hopp og andre kjekke ting");
        new InvoiceReader(p).select();

        ui.setMessage("Sender greiene ut på verdsveven");
        new Uploader(p).upload();

        ui.setMessage("Ferdig!");

        try {
            Thread.sleep(1000);
        }catch(InterruptedException ie) { }

        System.exit(0);

    }
}
