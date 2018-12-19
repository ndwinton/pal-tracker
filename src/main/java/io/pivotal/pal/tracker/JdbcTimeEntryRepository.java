package io.pivotal.pal.tracker;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private static final String PROJECT_ID = "project_id";
    private static final String ID = "id";
    private static final String USER_ID = "user_id";
    private static final String DATE = "date";
    private static final String HOURS = "hours";

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder holder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> { return prepareInsert(connection, timeEntry); }, holder);
        BigInteger id = (BigInteger) holder.getKey();
        return new TimeEntry(id.longValue(), timeEntry);
    }

    private static PreparedStatement prepareInsert(Connection connection, TimeEntry timeEntry) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "insert into time_entries (" +
                        PROJECT_ID + ", " +
                        USER_ID + ", " +
                        DATE + ", " +
                        HOURS + ") values (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, timeEntry.getProjectId());
        ps.setLong(2, timeEntry.getUserId());
        ps.setDate(3, java.sql.Date.valueOf(timeEntry.getDate()));
        ps.setInt(4, timeEntry.getHours());
        return ps;
    }

    @Override
    public TimeEntry find(long id) {
        List<TimeEntry> results = jdbcTemplate.query("select * from time_entries where id = ?",
                new Object[]{id},
                ((rs, rowNum) -> new TimeEntry(id,
                        rs.getLong(PROJECT_ID),
                        rs.getLong(USER_ID),
                        rs.getDate(DATE).toLocalDate(),
                        rs.getInt(HOURS))));
        return results.size() > 0 ? results.get(0) : null;
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("select * from time_entries order by id",
                ((rs, rowNum) -> new TimeEntry(
                        rs.getLong(ID),
                        rs.getLong(PROJECT_ID),
                        rs.getLong(USER_ID),
                        rs.getDate(DATE).toLocalDate(),
                        rs.getInt(HOURS))));
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        jdbcTemplate.update("update time_entries set " +
                        PROJECT_ID + " = ?, " +
                        USER_ID + " = ?, " +
                        DATE + " = ?, " +
                        HOURS + " = ? " +
                        "where " +
                        ID + " = ?",
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                java.sql.Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(),
                id);
        return new TimeEntry(id, timeEntry);
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("delete from time_entries where id = ?", id)
;
    }
}
