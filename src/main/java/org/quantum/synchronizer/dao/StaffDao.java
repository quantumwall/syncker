package org.quantum.synchronizer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.quantum.synchronizer.entity.Staff;
import org.quantum.synchronizer.entity.StaffKey;
import org.quantum.synchronizer.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaffDao {

    private static final Logger log = LoggerFactory.getLogger(StaffDao.class);

    private StaffDao() {

    }

    private static final String INSERT_OR_UPDATE = """
                                                   INSERT INTO staff (dep_code, dep_job, description)
                                                   VALUES 	(?, ?, ?)
                                                   ON CONFLICT ON CONSTRAINT dep_code_job_unq
                                                   DO UPDATE SET description = EXCLUDED.description;
                                                   """;
    private static final String FIND_ALL = """
                                           SELECT id, dep_code, dep_job, description
                                           FROM staff
                                           """;
    private static final String DELETE = "DELETE FROM staff";

    public void insert(Map<StaffKey, Staff> staffMap) {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);
            var statement = connection.prepareStatement(INSERT_OR_UPDATE);
            for (Staff staff : staffMap.values()) {
                log.info("insert or update staff: {}", staff);
                statement.setString(1, staff.getDepCode());
                statement.setString(2, staff.getDepJob());
                statement.setString(3, staff.getDescription());
                statement.addBatch();
            }
            statement.executeBatch();
            deleteIfAbsent(connection, staffMap.keySet());
            connection.commit();
        } catch (SQLException e) {
            log.error("error during insert or update staff: {}", e);
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                log.error("error during rollback transaction: {}", ex);
                throw new RuntimeException(ex);
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("error during close connection: {}", e);
                throw new RuntimeException(e);
            }
        }
    }

    public Set<Staff> findAll() {
        log.info("find all staff");
        var result = new HashSet<Staff>();
        try (var connection = ConnectionManager.getConnection(); var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(FIND_ALL);
            while (resultSet.next()) {
                result.add(new Staff(resultSet.getString("dep_code"), resultSet.getString("dep_job"),
                        resultSet.getString("description")));
            }
            log.info("finded: {}", result);
            return result;
        } catch (SQLException e) {
            log.error("error during find all staff: {}", e);
            throw new RuntimeException(e);
        }
    }

    private void deleteIfAbsent(Connection connection, Set<StaffKey> keys) throws SQLException {
        try (var findStatement = connection.createStatement();
             var deleteStatement = connection.prepareStatement(DELETE + " WHERE dep_code = ? AND dep_job = ?")) {
            var resultSet = findStatement.executeQuery(FIND_ALL);
            while (resultSet.next()) {
                var staffKey = mapToStaffKey(resultSet);
                if (staffKey != null && !keys.contains(staffKey)) {
                    log.info("delete {}", staffKey);
                    deleteStatement.setString(1, staffKey.getDepCode());
                    deleteStatement.setString(2, staffKey.getDepJob());
                    deleteStatement.addBatch();
                }
            }
            deleteStatement.executeBatch();
        }
    }

    private StaffKey mapToStaffKey(ResultSet rs) throws SQLException {
        return new StaffKey(rs.getString("dep_code"), rs.getString("dep_job"));
    }

    public static StaffDao getInstance() {
        return StaffDaoHolder.INSTANCE;
    }

    private static class StaffDaoHolder {
        public static final StaffDao INSTANCE = new StaffDao();

    }

}
