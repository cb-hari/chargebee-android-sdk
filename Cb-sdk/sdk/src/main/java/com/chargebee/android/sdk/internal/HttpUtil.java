package com.chargebee.android.sdk.internal;

import android.util.Base64;

import com.chargebee.android.sdk.APIException;
import com.chargebee.android.sdk.Environment;
import com.chargebee.android.sdk.ListResult;
import com.chargebee.android.sdk.Result;
import com.chargebee.android.sdk.exceptions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.json.*;

import javax.net.ssl.HttpsURLConnection;

public class HttpUtil {

    public enum Method {
        GET, POST;
    }

    /**
     * To temporarily capture the http response
     */
    private static class Resp {
        int httpCode;
        JSONObject jsonContent;

        private Resp(int httpCode, JSONObject jsonContent) {
            this.httpCode = httpCode;
            this.jsonContent = jsonContent;
        }

        private Result toResult() {
            return new Result(httpCode, jsonContent);
        }

        private ListResult toListResult() {
            return new ListResult(httpCode, jsonContent);
        }
    }

    public static Result get(String url, Params params, Map<String,String> headers,Environment env)
            throws ProtocolException, IOException {
        if(params != null && !params.isEmpty()) {
            url = url + '?' + toQueryStr(params); // fixme: what about url size restrictions ??
        }
        HttpsURLConnection conn = createConnection(url, Method.GET, headers,env);
        Resp resp = sendRequest(conn);
        return resp.toResult();
    }

    public static ListResult getList(String url, Params params, Map<String,String> headers, Environment env) throws IOException {
        if(params != null && !params.isEmpty()) {
            url = url + '?' + toQueryStr(params, true); // fixme: what about url size restrictions ??
        }
        HttpsURLConnection conn = createConnection(url, Method.GET, headers,env);
        Resp resp = sendRequest(conn);
        return resp.toListResult();
    }

    public static Result post(String url, Params params, Map<String,String> headers, Environment env) throws IOException {
        return doFormSubmit(url,Method.POST, toQueryStr(params), headers,env);
    }


    public static String toQueryStr(Params map) {
        return toQueryStr(map, false);
    }
    
    public static String toQueryStr(Params map, boolean isListReq) {
        StringJoiner buf = new StringJoiner("&");
        for (Map.Entry<String, Object> entry : map.entries()) {
            Object value = entry.getValue();            
            if(value instanceof List){
               List<String> l = (List<String>)value;
               if(isListReq){
                   String keyValPair = enc(entry.getKey()) + "=" + enc(l.isEmpty()?"": l.toString());
                   buf.add(keyValPair);
                   continue;
               }
                for (int i = 0; i < l.size(); i++) {
                    String val = l.get(i);
                    String keyValPair = enc(entry.getKey() + "[" + i + "]") + "=" + enc(val != null?val:"");
                    buf.add(keyValPair);
                }
            }else{
               String keyValPair = enc(entry.getKey()) + "=" + enc((String)value);                
               buf.add(keyValPair);
            }
        }
        return buf.toString();
    }

    private static String enc(String val) {
        try {
            return URLEncoder.encode(val, Environment.CHARSET);
        } catch(Exception exp) {
            throw new RuntimeException(exp);
        }
    }

    private static Result doFormSubmit(String url, Method m, String queryStr, Map<String,String> headers,
                                       Environment env) throws IOException {
        HttpsURLConnection conn = createConnection(url, m, headers,env);
        writeContent(conn, queryStr);
        Resp resp = sendRequest(conn);
        return resp.toResult();
    }

    private static void writeContent(HttpsURLConnection conn, String queryStr) throws IOException {
        if (queryStr == null) {
            return;
        }
        OutputStream os = conn.getOutputStream();
        try {
            os.write(queryStr.getBytes(Environment.CHARSET));
        } finally {
            os.close();
        }
    }

    private static HttpsURLConnection createConnection(String url, Method m, Map<String,String> headers, Environment config)
            throws ProtocolException, IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(m.name());
        setTimeouts(connection, config);
        addHeaders(connection, config);
        addCustomHeaders(connection,headers);
        setContentType(connection, m);
        connection.setDoOutput(m == Method.POST);
        connection.setUseCaches(false);
        return connection;

    }

    private static Resp sendRequest(HttpsURLConnection conn) throws IOException {
        int httpRespCode = conn.getResponseCode();
        if (httpRespCode == HttpsURLConnection.HTTP_NO_CONTENT) {
            throw new RuntimeException("Got https_no_content response");
        }
        boolean error = httpRespCode < 200 || httpRespCode > 299;
        String content = getContentAsString(conn, error);
        JSONObject jsonResp = getContentAsJSON(content);
        if(error) {
            try {
                jsonResp.getString("api_error_code");
                String type = jsonResp.optString("type");
                if ("payment".equals(type)) {
                   //needed ???
                    // throw new PaymentException(httpRespCode, jsonResp);
                } else if ("operation_failed".equals(type)) {
                    throw new OperationFailedException(httpRespCode, jsonResp);
                } else if ("invalid_request".equals(type)) {
                    throw new InvalidRequestException(httpRespCode, jsonResp);
                } else{
                    throw new APIException(httpRespCode, jsonResp);
                }
            }catch(APIException ex){
                throw ex;            
            } catch (Exception ex) {
                throw new RuntimeException("Error when parsing the error response. Probably not ChargeBee' error response. The content is \n " + content, ex);
            }
        }
        return new Resp(httpRespCode, jsonResp);
    }

    private static void setTimeouts(HttpsURLConnection conn, Environment config) {
        conn.setConnectTimeout(config.connectTimeout);
        conn.setReadTimeout(config.readTimeout);
    }

    private static void setContentType(HttpsURLConnection conn, Method m) {
        if (m == Method.POST) {
            addHeader(conn, "Content-Type", "application/x-www-form-urlencoded;charset=" + Environment.CHARSET);
        }
    }

    private static void addHeaders(HttpsURLConnection conn, Environment config) {
        addHeader(conn, "Accept-Charset", Environment.CHARSET);
        addHeader(conn, "User-Agent", String.format("Chargebee-Java-Client v%s", Environment.LIBRARY_VERSION));
        addHeader(conn, "Authorization", getAuthValue(config));
        addHeader(conn, "Accept", "application/json");
    }

    private static void addCustomHeaders(HttpsURLConnection conn, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            addHeader(conn, entry.getKey(), entry.getValue());
        }
    }

    private static void addHeader(HttpsURLConnection conn, String headerName, String value) {
        conn.setRequestProperty(headerName, value);
    }

    private static String getAuthValue(Environment config) {
        //TODO ... verify flag ???
        return "Basic " + Base64.encodeToString((config.apiKey + ":").getBytes(), Base64.DEFAULT)
                .replaceAll("\r?", "").replaceAll("\n?", "");
    }

    private static JSONObject getContentAsJSON(String content) throws IOException {
        JSONObject obj;
        try {
            obj = new JSONObject(content);
        } catch (JSONException exp) {
            throw new RuntimeException("Not in JSON format. Probably not a ChargeBee response. \n " + content,exp);
        }
        return obj;
    }

    private static String getContentAsString(HttpsURLConnection conn, boolean error) throws IOException {

        InputStream resp = (error) ? conn.getErrorStream() : conn.getInputStream();
        if (resp == null) {
            throw new RuntimeException("Got Empty Response ");
        }
        try {
            if ("gzip".equalsIgnoreCase(conn.getContentEncoding())) {
                resp = new GZIPInputStream(resp);
            }
            InputStreamReader inp = new InputStreamReader(resp, Environment.CHARSET);
            StringBuilder buf = new StringBuilder();
            char[] buffer = new char[1024];//Should use content length.
            int bytesRead;
            while ((bytesRead = inp.read(buffer, 0, buffer.length)) >= 0) {
                buf.append(buffer, 0, bytesRead);
            }
            String content = buf.toString();
            return content;
        } finally {
            resp.close();
        }
    }
    
    

}
