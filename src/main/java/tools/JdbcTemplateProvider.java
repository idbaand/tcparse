package tools;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class JdbcTemplateProvider {

	private static JdbcTemplateProvider instance;

	String dbType = "h2";

	private JdbcTemplateProvider(String dbType) {
		this.dbType = dbType;
	}

	public void createTable() {
		try {
			String createTable = "create table if not exists report (\r\n" 
					+ "id int auto_increment primary key, \r\n"
					+ "build int, \r\n"
					+ "regression varchar(255), \r\n"
					+ "tcId varchar(255), \r\n" 
					+ "tcName varchar(255), \r\n" 
					+ "tsId varchar(255), \r\n"
					+ "tsName varchar(255), \r\n" 
					+ "tcStatus varchar(255), \r\n" 
					+ "tsStatus varchar(255), \r\n"
					+ "errorType varchar(255), \r\n" 
					+ "error TEXT, \r\n" 
					+ "stepId VARCHAR(255), \r\n"
					+ "text VARCHAR(255), \r\n" 
					+ "action VARCHAR(255), \r\n" 
					+ "label VARCHAR(255), \r\n"
					+ "value VARCHAR(255), \r\n" 
					+ "actualvalue TEXT, \r\n" 
					+ "status VARCHAR(255), \r\n"
					+ "screenshot TEXT, \r\n" 
					+ "sourcecapture TEXT, "
					+ "category VARCHAR(255))";
			JdbcTemplate jdbc = getTemplate();
			if ("h2".equals(this.dbType)) {
				createTable = createTable.replaceAll("TEXT", "CLOB");
			}

			jdbc.execute(createTable);

			jdbc.query("select count(*) from report limit 1", new RowMapper<Object>() {

				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					while (rs.next()) {
						System.out.print(rs.getInt(1));
					}
					return null;
				}

			});

		} catch (Exception ex) {
			// TODO: propagate error to UI
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}

	}

	public synchronized static JdbcTemplateProvider getInstance() {
		if (instance == null) {
			instance = new JdbcTemplateProvider(System.getProperty("dbtype", "h2"));
			instance.createTable();
		}
		return instance;
	}

	public JdbcTemplate getTemplate() {
		String user = "sa";
		String pass = "";
		String dburl = "jdbc:h2:./ellipseapps";

		switch (this.dbType) {
		case "mysql":
			dburl = "jdbc:mysql://localhost:3306/ellipseapps";
			user = "ellipse";
			pass = "ellipse";
			break;
		}

		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(dburl, user, pass, true);
		return new JdbcTemplate(dataSource);
	}
}
