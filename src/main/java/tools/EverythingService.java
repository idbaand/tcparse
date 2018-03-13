package tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import tools.beans.StepErrorCount;

public class EverythingService {

	public static List<Integer> getLastNBuild(String regression, int n) {
		String sql = "select distinct build from report where regression='%s' order by build desc limit " + n;
		JdbcTemplate jdbc = JdbcTemplateProvider.getInstance().getTemplate();
		return jdbc.query(String.format(sql, regression), new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet arg0, int arg1) throws SQLException {

				return arg0.getInt("build");
			}

		});
	}
	
	public static List<String> getRegressions() {
	    return Arrays.asList("managepeople", "managematerials", "managefinancials", "manageworks");
	}

	public static Map<String, StepErrorCount> getStepErrorCount(String regression, Integer... build) {
		JdbcTemplate jdbc = JdbcTemplateProvider.getInstance().getTemplate();
		String sql = "select category, build, count(*) as error_count from report "
				+ "where regression='%s' and build in (%s) group by category, build order by error_count desc";

		if (build == null || build.length == 0)
			return null;

		Map<String, StepErrorCount> results = new HashMap<>();
		jdbc.query(String.format(sql, regression, Arrays.stream(build).map(String::valueOf).collect(Collectors.joining(","))),
				new RowMapper<StepErrorCount>() {

					@Override
					public StepErrorCount mapRow(ResultSet arg0, int arg1) throws SQLException {
						String cat = arg0.getString("category");
						StepErrorCount opt = Optional.ofNullable(results.get(cat)).orElse(new StepErrorCount());

						opt.setActivity(cat);
						opt.setErrorCount(arg0.getInt("build"), arg0.getInt("error_count"));
						results.put(cat, opt);

						return opt;
					}

				});

		return results;
	}

	public static void getBuildStep(String regression, int build, RowCallbackHandler callback) {
		JdbcTemplate jdbc = JdbcTemplateProvider.getInstance().getTemplate();
		String sql = "select id, category, actualvalue, text, tcname, tcid, error, text, value, label"
				+ " from report where regression = ? and build = ? and status = '0' order by actualvalue asc, tcid asc";

		jdbc.query(sql, new Object[] { regression, build }, callback);
	}
}
