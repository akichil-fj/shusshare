package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.ShusshaStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShusshaStatusTypeHandler extends BaseTypeHandler<ShusshaStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ShusshaStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, String.valueOf(parameter.getValue()));
    }

    @Override
    public ShusshaStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return ShusshaStatus.getShusshaStatus(rs.getInt(columnName));
    }

    @Override
    public ShusshaStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return ShusshaStatus.getShusshaStatus(rs.getInt(columnIndex));
    }

    @Override
    public ShusshaStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return ShusshaStatus.getShusshaStatus(cs.getInt(columnIndex));
    }

}
