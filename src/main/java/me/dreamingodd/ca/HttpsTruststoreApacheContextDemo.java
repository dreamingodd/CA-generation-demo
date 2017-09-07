package me.dreamingodd.ca;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * #3
 * HTTPS 双向认证 - use truststore
 * Apache插件
 * @Author Ye_Wenda
 * @Date 7/11/2017
 */
public class HttpsTruststoreApacheContextDemo {
    // 客户端证书路径，用了本地绝对路径，需要修改
    private final static String CLIENT_CERT_FILE = "C:/Development/deployment/ssl/ca-demo/client.p12";
    // 客户端证书密码
    private final static String CLIENT_PWD = "demo";
    // 信任库路径
    private final static String TRUST_STRORE_FILE = "C:\\Development\\deployment\\ssl\\ca-demo\\demo.truststore";
    // 信任库密码
    private final static String TRUST_STORE_PWD = "demodemo";


    private static String readResponseBody(InputStream inputStream) throws IOException {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuffer sb = new StringBuffer();
            String buff = null;
            while((buff = br.readLine()) != null){
                sb.append(buff+"\n");
            }
            return sb.toString();
        }finally{
            inputStream.close();
        }
    }

    public static void httpsCall() throws Exception {
        // 初始化密钥库
        KeyManagerFactory keyManagerFactory = KeyManagerFactory
                .getInstance("SunX509");
        KeyStore keyStore = getKeyStore(CLIENT_CERT_FILE, CLIENT_PWD, "PKCS12");
        keyManagerFactory.init(keyStore, CLIENT_PWD.toCharArray());

        // 初始化信任库
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance("SunX509");
        KeyStore trustkeyStore = getKeyStore(TRUST_STRORE_FILE, TRUST_STORE_PWD,"JKS");
        trustManagerFactory.init(trustkeyStore);

//        SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, "123456".toCharArray())
//        	.loadTrustMaterial(new File(TRUST_STRORE_FILE),"012345".toCharArray()).setSecureRandom(new SecureRandom()).useProtocol("SSL").build();
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,new String[]{"TLSv1", "TLSv2", "TLSv3"},null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        CloseableHttpClient closeableHttpClient = HttpClients.custom().setSSLContext(sslContext).build();
        HttpGet getCall = new HttpGet();
        getCall.setURI(new URI("https://ssl.demo.com"));
        CloseableHttpResponse response = closeableHttpClient.execute(getCall);
        System.out.println(convertStreamToString(response.getEntity().getContent()));

    }

    public static String convertStreamToString(InputStream is) {
        /*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * 获得KeyStore
     *
     * @param keyStorePath
     * @param password
     * @return
     * @throws Exception
     */
    private static KeyStore getKeyStore(String keyStorePath, String password,String type)
            throws Exception {
        FileInputStream is = new FileInputStream(keyStorePath);
        KeyStore ks = KeyStore.getInstance(type);
        ks.load(is, password.toCharArray());
        is.close();
        return ks;
    }



    public static void main(String[] args) throws Exception {
        httpsCall();
    }
}
