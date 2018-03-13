package tools;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tools.beans.ErrorCategory;

public class Importer {

    static final String TABLE = "report";

    JdbcTemplate jdbc;
    JdbcTemplateProvider jdbcProvider;
    ObjectMapper mapper;
    int buildNo;
    String regression;
    SimpleJdbcInsert inserter;

    private boolean deleteBuild;

    private boolean dropTable;

    public Importer() {
        jdbcProvider = JdbcTemplateProvider.getInstance(); 
        jdbc = jdbcProvider.getTemplate();

        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        mapper.configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true);

        inserter = new SimpleJdbcInsert(jdbc);
        inserter.setTableName(TABLE);

    }

    public Importer withBuild(int buildno) {
        this.buildNo = buildno;
        return this;
    }
    
    public Importer withRegression(String regres) {
        this.regression = regres;
        return this;
    }

    private Importer createReportTable() {
        
        try {
            //`jdbc.execute(createTable);
        	JdbcTemplateProvider.getInstance()
        	.createTable();
        } catch (DataAccessException e1) {
            System.err.println("failed create table report: " + e1.getMessage());
        }

        return this;
    }

    public Importer deleteBuild() {
        this.deleteBuild = true;
        return this;
    }

    public Importer dropTable() {
        dropTable = true;
        return this;
    }

    private void _dropTable() {
        try {
            jdbc.execute("drop table " + TABLE);
        } catch (DataAccessException e1) {
            System.out.println("failed drop table " + TABLE + ": " + e1.getMessage());
        }
    }

    public void _deleteBuild() {
        try {
            jdbc.execute("delete from " + TABLE + " where build=" + buildNo);
        } catch (DataAccessException e1) {
            System.out.println("failed delete build " + buildNo + " from " + TABLE + ": " + e1.getMessage());
        }
    }

    public Importer doImport(Object source) throws Exception {

        JsonNode root = new ReportJsonFetcher().from(source).toJson();

        for (JsonNode node : root.get("package")) {
            if (node.get("text").asText().equals("Manage People")) {
                JsonNode managePeople = node.get("testsuite");

                if (this.dropTable)
                    _dropTable();

                createReportTable();

                if (this.deleteBuild)
                    _deleteBuild();

                /*
                 * outer: for (JsonNode ts : managePeople) { for (JsonNode tc : ts.get("testcase")) { JsonNode steps = tc.get("teststep"); if (steps
                 * != null) { JsonNode step = steps.get(0); if (step != null) { createTable(jdbc, step); break outer; } } } }
                 */

                for (JsonNode ts : managePeople) {
                    for (JsonNode tc : ts.get("testcase")) {
                        insert(ts, tc);
                    }
                }
            }
        }

        return this;
    }

    Importer printRecord(int n) {
        System.out.println("================== record " + n + " ==========================");
        jdbc.queryForMap("select * from " + TABLE + " limit 1 offset " + n).forEach((k, v) -> {
            System.out.println(k + " : " + String.valueOf(v));
        });
        return this;
    }

    Importer printSummary() {
        System.out.println("=============== summary ===================");
        String[] count = jdbc.execute(new StatementCallback<String[]>() {

            @Override
            public String[] doInStatement(Statement stmt) throws SQLException, DataAccessException {

                String[] result = new String[3];
                String[] q = { "select count(*) from " + TABLE,
                        "SELECT COUNT(*) FROM (SELECT DISTINCT tsid FROM " + TABLE + ") a",
                        "SELECT COUNT(*) FROM (SELECT DISTINCT tcid FROM " + TABLE + " a" };

                int i = 0;
                for (String s : q) {
                    System.out.println("Sql:" + s);
                    stmt.execute(s);
                    ResultSet rs = stmt.getResultSet();
                    rs.next();
                    result[i++] = rs.getString(1);
                }
                return result;
            }

        });

        System.out.println("Successfuly inserted total  " + count[0] + " steps");
        System.out.println("Successfuly inserted total  " + count[1] + " test suites");
        System.out.println("Successfuly inserted total  " + count[2] + " test cases");

        System.out.println("=================== summary ====================");
        return this;
    }

    public static void main(String[] args) throws Exception {
        URL url = Importer.class.getClassLoader().getResource("reportdata.js");

        System.err.println("Absolute Path to json: " + url.toString());

        new Importer().withBuild(100)
                .dropTable().doImport(url)
                .printSummary().printRecord(0);
    }

    void createTable(JsonNode step) {
//        Iterator<String> fields = step.fieldNames();
//        StringBuilder sb = new StringBuilder();
//        sb.append("create table ").append(TABLE).append(" (\n");
//        sb.append("build varchar(20), \n");
//        sb.append("tcId varchar(255), \n");
//        sb.append("tcName varchar(255), \n");
//        sb.append("tsId varchar(255), \n");
//        sb.append("tsName varchar(255), \n");
//        sb.append("tcStatus varchar(255), \n");
//        sb.append("tsStatus varchar(255), \n");
//        sb.append("errorType varchar(255), \n");
//        sb.append("error TEXT, \n");
//        sb.append(join(fields, n -> {
//            return n.matches("id|text|action|label|value|status") ? n + " VARCHAR(255)" : n + " TEXT";
//        }, ", \n"));
//
//        sb.append(")");
//        System.out.println(sb.toString());
//        jdbc.execute(sb.toString());
    		this.jdbcProvider.createTable();

    }

    static Map<String, Object> toMap(JsonNode node) {
        Iterable<String> i = () -> node.fieldNames();
        Stream<String> stream = StreamSupport.stream(i.spliterator(), false);
        return stream.collect(Collectors.<String, String, Object> toMap(n -> n, n -> {
        		String val = node.get(n).asText().trim(); 
        		if ("actualvalue".equals(n))
        			return StringEscapeUtils.unescapeHtml4(val);
        		return val;
        }));
    }

    static String join(Iterator<String> itrator, Function<String, String> f, String sep) {
        Iterable<String> i = () -> itrator;
        Stream<String> stream = StreamSupport.stream(i.spliterator(), false);
        return stream.map(f).collect(Collectors.joining(sep));
    }

    void insert(JsonNode ts, JsonNode tc) {

        for (JsonNode step : tc.get("teststep")) {
            // System.out.println("got tc step --> tc: " + tc.get("id").asText() + " --> " +
            // step.get("id") + " action: "
            // + step.get("action"));
            ObjectNode stepEdit = (ObjectNode) step;
            stepEdit.put("tsId", ts.get("id").asText());
            stepEdit.put("tsName", ts.get("text").asText());
            stepEdit.put("tcId", tc.get("id").asText());
            stepEdit.put("tcName", tc.get("text").asText());
            stepEdit.put("tcStatus", tc.get("status").asText());
            stepEdit.put("tsStatus", ts.get("status").asText());
            stepEdit.put("stepId", step.get("id").asText());

            Map<String, Object> params = toMap(stepEdit);
            params.put("category", ErrorCategory.whichCategory(
            		params.get("actualvalue").toString()
            		));
            params.remove("id");
            JsonNode error = stepEdit.get("error");
            if (error != null) {
                JsonNode errorDesc = error.get("desc");
                JsonNode errorType = error.get("type");

                if (errorDesc != null)
                    params.put("error", errorDesc.asText());
                if (errorDesc != null)
                    params.put("errorType", errorType.asText());
            }

            params.put("build", this.buildNo);
            params.put("regression", this.regression);
            inserter.execute(params);
        }
    }

    
    
}
