package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.FriendStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendStatusTypeHandler extends BaseTypeHandler<FriendStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, FriendStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, String.valueOf(parameter.getValue()));
    }

    @Override
    public FriendStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return FriendStatus.getFriendStatus(rs.getInt(columnName));
    }

    @Override
    public FriendStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return FriendStatus.getFriendStatus(rs.getInt(columnIndex));
    }

    @Override
    public FriendStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return FriendStatus.getFriendStatus(cs.getInt(columnIndex));
    }

}
