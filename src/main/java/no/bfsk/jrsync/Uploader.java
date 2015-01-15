/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package no.bfsk.jrsync;

import java.io.File;
import java.util.Properties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author glenn
 */
public class Uploader {
    private Properties props = null;

    public Uploader(Properties p) {
        this.props = p;
    }

    public void upload() {
        String url = props.getProperty("upload.url");
        if(url == null || url.isEmpty()) return;

        String tmpDir = props.getProperty("jrsync.tmpdir");
        if(tmpDir == null || tmpDir.trim().length() == 0) {
            tmpDir = System.getProperty("java.io.tmpdir");
        }
        tmpDir = tmpDir.trim();

        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost post = new HttpPost(url);
            FileBody users = new FileBody(new File(tmpDir, props.getProperty("user.filename")), "text/plain");
            FileBody payments = new FileBody(new File(tmpDir, props.getProperty("payment.filename")), "text/plain");
            FileBody invoices = new FileBody(new File(tmpDir, props.getProperty("invoice.filename")), "text/plain");

            MultipartEntity multipart = new MultipartEntity();
            multipart.addPart("users", users);
            multipart.addPart("payments", payments);
            multipart.addPart("invoices", invoices);

            post.setEntity(multipart);
            HttpResponse response = httpClient.execute(post);

            HttpEntity resEntity = response.getEntity();
            EntityUtils.consume(resEntity);

        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            try { httpClient.getConnectionManager().shutdown(); }catch(Exception ex) { }
        }
    }

}
