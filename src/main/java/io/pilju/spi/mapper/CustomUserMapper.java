package io.pilju.spi.mapper;

import io.pilju.spi.constants.DatabaseQueryConstants;
import io.pilju.spi.utils.DbUtil;
import io.pilju.spi.entity.CustomUserModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;

public class CustomUserMapper {

    private static final Logger logger = Logger.getLogger(CustomUserMapper.class);
    private final ComponentModel config;

    public CustomUserMapper(ComponentModel config) {
        this.config = config;
    }

    public List<CustomUserModel> getUsers(String username) throws SQLException {
        try (Connection c = DbUtil.getConnection(config)) {
            PreparedStatement st = c.prepareStatement(DatabaseQueryConstants.getUserListByUserName);
            st.setString(1, username);
            return selectAll(st.executeQuery());
        }
    }

    public String getUserPassword(String username) throws SQLException {
        try (Connection c = DbUtil.getConnection(config)) {
            PreparedStatement st = c.prepareStatement(DatabaseQueryConstants.getPasswordByUserName);
            st.setString(1, username);
            return selectOneResultString(st.executeQuery(), "password");
        }
    }

    public List<CustomUserModel> getUsers() throws SQLException {
        try (Connection c = DbUtil.getConnection(config)) {
            PreparedStatement st = c.prepareStatement(DatabaseQueryConstants.getUserList);
            return selectAll(st.executeQuery());
        }
    }

    private String selectOneResultString(ResultSet rs, String columnName) throws SQLException {
        List<String> columnList = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            columnList.add(metaData.getColumnLabel(i));
        }
        if (rs.next()) {
            return getString(columnList, columnName, rs);
        }
        return "";
    }

    private List<CustomUserModel> selectAll(ResultSet rs) throws SQLException {
        List<CustomUserModel> result = new ArrayList<>();
        List<String> columnList = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            columnList.add(metaData.getColumnLabel(i));
        }
        while (rs.next()) {
            CustomUserModel userModel = CustomUserModel.builder()
                .userId(getString(columnList, "username", rs))
                .emailAddress(getString(columnList, "email", rs))
                .userPw(getString(columnList, "password", rs))
                .companyCd(getString(columnList, "companyCd", rs))
                .deptCd(getString(columnList, "deptCd", rs))
                .build();
            result.add(userModel);
        }
        return result;
    }

    private String getString(List<String> columnList, String columnLabel, ResultSet rs) throws SQLException {
        if (columnList.contains(columnLabel)) {
            String result = rs.getString(columnLabel);
            return result;
        }
        return "";
    }

    private Date getDate(List<String> columnList, String columnLabel, ResultSet rs) throws SQLException {
        if (columnList.contains(columnLabel)) {
            return rs.getDate(columnLabel);
        }
        return null;
    }
}
