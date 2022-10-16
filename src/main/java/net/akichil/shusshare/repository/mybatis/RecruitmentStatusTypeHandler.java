package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.RecruitmentStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecruitmentStatusTypeHandler extends BaseTypeHandler<RecruitmentStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, RecruitmentStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, String.valueOf(parameter.getValue()));
    }

    @Override
    public RecruitmentStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return RecruitmentStatus.getRecruitmentStatus(rs.getInt(columnName));
    }

    @Override
    public RecruitmentStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return  RecruitmentStatus.getRecruitmentStatus(rs.getInt(columnIndex));
    }

    @Override
    public RecruitmentStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return RecruitmentStatus.getRecruitmentStatus(cs.getInt(columnIndex));
    }
}
