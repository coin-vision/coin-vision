package net.coinshome.coinvision.de;

import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class CrawlerUtils {

    static {
        disableSslVerification();
    }

    public static void downloadImageFromCoinshomeNetSite(String imgId, File inImgFile) {
        String imgUrl = "https://st.coinshome.net/fs/600_300/" + imgId + ".jpg";
        byte[] img = downloadDataFromSite(imgUrl);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(inImgFile);
            fos.write(img);
            fos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }

    /**
     * @param url
     * @return
     */
    private static byte[] downloadDataFromSite(String url) {
        if (url.startsWith("//")) {
            url = "http:" + url;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            URL u = new URL(url);
            URLConnection uc = u.openConnection();
            InputStream is = uc.getInputStream();
            int bytesRead = 0;
            int bufferSize = 4000;
            byte[] byteBuffer = new byte[bufferSize];
            while ((bytesRead = is.read(byteBuffer)) != -1) {
                baos.write(byteBuffer, 0, bytesRead);
            }
            is.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return baos.toByteArray();
    }


    /**
     * disable ssl validation
     */
    private static void disableSslVerification() {

        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @SuppressWarnings("unused")
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
                    // TODO Auto-generated method stub

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
                    // TODO Auto-generated method stub

                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

}
