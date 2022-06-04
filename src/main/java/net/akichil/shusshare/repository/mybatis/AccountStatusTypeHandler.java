package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.AccountStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountStatusTypeHandler extends BaseTypeHandler<AccountStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, AccountStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, String.valueOf(parameter.getValue()));
    }

    @Override
    public AccountStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return AccountStatus.getUserStatus(rs.getInt(columnName));
    }

    @Override
    public AccountStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return AccountStatus.getUserStatus(rs.getInt(columnIndex));
    }

    @Override
    public AccountStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return AccountStatus.getUserStatus(cs.getInt(columnIndex));
    }

}
