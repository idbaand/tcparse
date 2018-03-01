package tools;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
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

	static final String build = "100";
	public static void main(String[] args) throws Exception {
		URL url = Main.class.getClassLoader().getResource("treedata.json");

		System.err.println("Absolute Path to json: " + url.toString());

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
		JsonNode root = mapper.readValue(url, JsonNode.class);

		String dburl = "jdbc:h2:./app_features";
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(dburl, true);
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);

		try {
			jdbc.execute("drop table steps;");
		} catch (DataAccessException e1) {
		}

		SimpleJdbcInsert sj = new SimpleJdbcInsert(jdbc);
		sj.setTableName("steps");
		
		for (JsonNode node : root) {
			if (node.get("name").asText().equals("Manage People")) {
				JsonNode managePeople = node.get("children");
				
				outer:
				for (JsonNode ts : managePeople) {
					for (JsonNode tc : ts.get("children")) {
						JsonNode steps = tc.get("children");
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
					for (JsonNode tc : ts.get("children")) {
						insert(sj, ts, tc);
					}
				}
			}
		}
		
		System.out.print("\n\nFirstRecord: ");
		
		
		jdbc.queryForMap("select * from steps limit 1").forEach((k , v) -> {
			System.out.println(k + " : " + v.toString());
		});
		
		System.out.print("\n\n");
		

		String[] count = jdbc.execute(new StatementCallback<String[]>() {

			@Override
			public String[] doInStatement(Statement stmt) throws SQLException, DataAccessException {
				
				String[] result = new String[3];
				String[] q = {
						"select count(*) from steps",
						"select count(*) from steps group by tsId",
						"select count(*) from steps group by tcId"
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
		sb.append("create table steps (").append("\n");
		sb.append("build varchar(20), \n");
		sb.append("tcId varchar(255), \n");
		sb.append("tcName varchar(255), \n");
		sb.append("tsId varchar(255), \n");
		sb.append("tsName varchar(255), \n");
		sb.append("tcStatus varchar(255), \n");
		sb.append(join(fields, n -> n + " VARCHAR(255)", ", \n"));
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

		for (JsonNode step : tc.get("children")) {
			//System.out.println("got tc step --> tc: " + tc.get("id").asText() + " --> " + step.get("id") + " action: "
			//		+ step.get("action"));
			ObjectNode stepEdit = (ObjectNode) step;
			stepEdit.put("tsId", ts.get("id").asText());
			stepEdit.put("tsName", ts.get("name").asText());
			stepEdit.put("tcId", tc.get("id").asText());
			stepEdit.put("tcName", tc.get("name").asText());
			stepEdit.put("tcStatus", tc.get("status").asText());

			Map<String, Object> params = toMap(stepEdit);
			params.put("build", build);
			insert.execute(params);
		}
	}
}
