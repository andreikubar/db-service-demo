package model;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

public class DatabaseImpl implements Database {

    private JdbcTemplate jdbcTemplate;

    public DatabaseImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public int insert(String tableName, List<String> values) {
        String query = "insert into " + tableName + "(field_1, field_2)" + " values (?, ?)";
        KeyHolder generatedKey = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, new String[]{"id"});
            statement.setString(1, values.get(0));
            statement.setString(2, values.get(1));
            return statement;
        }, generatedKey);
        return generatedKey.getKey().intValue();
    }

    @Override
    @Transactional
    public boolean update(String tableName, List<String> values, int id) {
        StringJoiner valuesSqlJoiner = new StringJoiner(",");
        Iterator<String> valuesIterator = values.iterator();
        while (valuesIterator.hasNext()) {
            String fieldName = valuesIterator.next();
            String value = valuesIterator.next();
            valuesSqlJoiner.add(fieldName + " = '" + value + "'");
        }
        String query = "update " + tableName + " set " + valuesSqlJoiner.toString();
        return jdbcTemplate.update(query) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> select(String tableName, int id) {
        String query = "select field_1, field_2 from " + tableName + " where id = ?";
        return jdbcTemplate.query(query, new Object[]{id}, resultSet -> {
            List<String> resultList = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int cols = metaData.getColumnCount();
            if (!resultSet.next()){
                throw new NoDataFoundException();
            }
            for (int x = 1; x <= cols; x++) {
                resultList.add(resultSet.getString(x));
            }
            return resultList;
        });

    }
}
