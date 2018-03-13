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

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Main {

    static final String TABLE = "step";
    static final Integer BUILD = 100;
    
    JdbcTemplate jdbc;
    ObjectMapper mapper;
    
    public Main() {
        String dburl = "jdbc:h2:./app_features";
        dburl = "jdbc:mysql://localhost/ellipseapps";
        
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource(dburl, "ellipse", "ellipse", true);
        jdbc = new JdbcTemplate(dataSource);
        
        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        mapper.configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true);
    }
    
    public Main createReportTable() {
        String createTable = "create table "+TABLE+" (\r\n" + 
                "build int, \r\n" + 
                "tcId varchar(255), \r\n" + 
                "tcName varchar(255), \r\n" + 
                "tsId varchar(255), \r\n" + 
                "tsName varchar(255), \r\n" + 
                "tcStatus varchar(255), \r\n" + 
                "tsStatus varchar(255), \r\n" + 
                "errorType varchar(255), \r\n" + 
                "error TEXT, \r\n" + 
                "stepId VARCHAR(255), \r\n" + 
                "text VARCHAR(255), \r\n" + 
                "action VARCHAR(255), \r\n" + 
                "label VARCHAR(255), \r\n" + 
                "value VARCHAR(255), \r\n" + 
                "actualvalue TEXT, \r\n" + 
                "status VARCHAR(255), \r\n" + 
                "screenshot TEXT, \r\n" + 
                "sourcecapture TEXT)";
        
        try {
            jdbc.execute(createTable);
        } catch (DataAccessException e1) {
            System.err.println("failed create table report: " + e1.getMessage());
        }
        
        return this;
    }
    
    public Main dropReportTable() {
        try {
            jdbc.execute("drop table "+TABLE);
        } catch (DataAccessException e1) {
            System.err.println("failed drop table "+TABLE+": " + e1.getMessage());
        }
        
        return this;
    }
    
    static String stripToJson(String content) {
        String regex = "(?<=var reportData = )(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(content);
        if (m.matches()) {
            System.out.println(m.group(1).substring(0, 100));
        }
        
        return null;
    }
    
    public Main doImport(String url) {
        return this;
    }
    
    public Main stats() {
        return this;
    }
    
	public static void main(String[] args) throws Exception {
		URL url = Main.class.getClassLoader().getResource("reportdata.js");

		System.err.println("Absolute Path to json: " + url.toString());

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
		JsonNode root = mapper.readValue(url, JsonNode.class);

		String dburl = "jdbc:h2:./app_features";
		dburl = "jdbc:mysql://localhost/ellipseapps";
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(dburl, "ellipse", "ellipse", true);
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);

		try {
			jdbc.execute("drop table " + TABLE);
		} catch (DataAccessException e1) {
		}

		SimpleJdbcInsert sj = new SimpleJdbcInsert(jdbc);
		sj.setTableName(TABLE);
		
		for (JsonNode node : root.get("package")) {
			if (node.get("text").asText().equals("Manage People")) {
				JsonNode managePeople = node.get("testsuite");
				
				outer:
				for (JsonNode ts : managePeople) {
					for (JsonNode tc : ts.get("testcase")) {
						JsonNode steps = tc.get("teststep");
						if (steps != null) {
							JsonNode step = steps.get(0);
							if (step != null) {
								createTable(jdbc, step);
								break outer;
							}
						}
					}
				}
				
				System.out.print("\n\n");
				
				for (JsonNode ts : managePeople) {
					for (JsonNode tc : ts.get("testcase")) {
						insert(sj, ts, tc);
					}
				}
			}
		}
		
		System.out.print("\n\nFirstRecord: ");
		
		
		jdbc.queryForMap("select * from "+TABLE+" limit 1").forEach((k , v) -> {
			System.out.println(k + " : " + String.valueOf(v));
		});
		
		System.out.print("\n\n");
		

		String[] count = jdbc.execute(new StatementCallback<String[]>() {

			@Override
			public String[] doInStatement(Statement stmt) throws SQLException, DataAccessException {
				
				String[] result = new String[3];
				String[] q = {
						"select count(*) from "+TABLE,
						"SELECT COUNT(*) FROM (SELECT DISTINCT tsid FROM "+TABLE+") a",
						"SELECT COUNT(*) FROM (SELECT DISTINCT tcid FROM "+TABLE+" a"
				};
				
				int i = 0;
				for(String s: q) {
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
	}

	static void createTable(JdbcTemplate jdbc, JsonNode step) {
		Iterator<String> fields = step.fieldNames();
		StringBuilder sb = new StringBuilder();
		sb.append("create table ").append(TABLE).append(" (\n");
		sb.append("build varchar(20), \n");
		sb.append("tcId varchar(255), \n");
		sb.append("tcName varchar(255), \n");
		sb.append("tsId varchar(255), \n");
		sb.append("tsName varchar(255), \n");
		sb.append("tcStatus varchar(255), \n");
		sb.append("tsStatus varchar(255), \n");
		sb.append("errorType varchar(255), \n");
		sb.append("error TEXT, \n");
		sb.append(join(fields, n -> {
            return n.matches("id|text|action|label|value|status") ? n + " VARCHAR(255)" : n + " TEXT";
         }, ", \n"));
        
		sb.append(")");
		System.out.println(sb.toString());
		jdbc.execute(sb.toString());

	}

	static Map<String, Object> toMap(JsonNode node) {
		Iterable<String> i = () -> node.fieldNames();
		Stream<String> stream = StreamSupport.stream(i.spliterator(), false);
		return stream.collect(Collectors.<String, String, Object>toMap(n -> n, n -> node.get(n).asText()));
	}

	static String join(Iterator<String> itrator, Function<String, String> f, String sep) {
		Iterable<String> i = () -> itrator;
		Stream<String> stream = StreamSupport.stream(i.spliterator(), false);
		return stream.map(f).collect(Collectors.joining(sep));
	}

	public static void insert(SimpleJdbcInsert insert, JsonNode ts, JsonNode tc) {

		for (JsonNode step : tc.get("teststep")) {
			//System.out.println("got tc step --> tc: " + tc.get("id").asText() + " --> " + step.get("id") + " action: "
			//		+ step.get("action"));
			ObjectNode stepEdit = (ObjectNode) step;
			stepEdit.put("tsId", ts.get("id").asText());
			stepEdit.put("tsName", ts.get("text").asText());
			stepEdit.put("tcId", tc.get("id").asText());
			stepEdit.put("tcName", tc.get("text").asText());
			stepEdit.put("tcStatus", tc.get("status").asText());
			stepEdit.put("tsStatus", ts.get("status").asText());
			stepEdit.put("stepId", step.get("id").asText());
			
			Map<String, Object> params = toMap(stepEdit);
			params.remove("id");
			JsonNode error = stepEdit.get("error");
			if (error!= null) {
			    JsonNode errorDesc = error.get("desc");
			    JsonNode errorType = error.get("type");
			    
			    if(errorDesc!=null) params.put("error", errorDesc.asText());
			    if(errorDesc!=null) params.put("errorType", errorType.asText());
			}
			
			params.put("build", BUILD);
			insert.execute(params);
		}
	}
}
