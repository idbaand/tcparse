package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.UnsupportedDataTypeException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReportJsonFetcher {

    static String path = "http://awsjenkins.ventyx.abb.com:8080/job/ji5.10-selenium-regression-managepeople/lastSuccessfulBuild/artifact/el_jsc/test/SeleniumTests/RegressionTests/report/data/reportdata.js";
    static String USERAGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36";
    StringBuilder result = new StringBuilder();

    ObjectMapper mapper;

    String parsed;

    Object source;

    public ReportJsonFetcher() {
        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        mapper.configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true);
    }

    ReportJsonFetcher from(Object source) throws Exception {
        this.source = source;
        if (String.class.isAssignableFrom(source.getClass())) {
            String stringSource = (String) source;
            if (stringSource.startsWith("http") || stringSource.startsWith("https")) {
                get(stringSource);
            } else {
                File file = Paths.get(stringSource).toFile();
                _readInputStream(new FileInputStream(file));
            }
        } else if (URL.class.isAssignableFrom(source.getClass())) {
            _readInputStream(((URL) source).openStream());
        } else if (File.class.isAssignableFrom(source.getClass())) {
        		_readInputStream(new FileInputStream((File) source));
        }else {
            throw new UnsupportedDataTypeException("Not supported source with type: " + source.getClass());
        }

        return this;
    }

    public static void main(String[] args) throws Exception {
        // System.out.println(new ReportJsonFetcher().get().read().toRaw());
    }

    private ReportJsonFetcher get(String source) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(source);

        // add request header
        request.addHeader("User-Agent", USERAGENT);
        HttpResponse response = client.execute(request);

        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

        _readInputStream(response.getEntity().getContent());
        return this;
    }

    private void _readInputStream(InputStream stream) throws Exception {
        BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
        result.setLength(0);
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
    }

    String toRaw() {
        read();
        return parsed;
    }

    private ReportJsonFetcher read() {
        String regex = "(?<=var reportData = )(.*)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = pattern.matcher(result.toString());
        if (m.find()) {
            // System.out.println("b:" + m.group(1));
            /*
             * File f = new File("x.text"); try(PrintWriter w = new PrintWriter(f)) { w.write(m.group(1)); }
             */
            parsed = m.group(1);
            return this;
        }

        throw new RuntimeException("Couldn't read reportdata");
    }

    JsonNode toJson() throws JsonParseException, JsonMappingException, IOException {
        read();
        return mapper.readValue(parsed, JsonNode.class);
    }
}
