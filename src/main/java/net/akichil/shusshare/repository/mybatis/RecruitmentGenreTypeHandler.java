package net.akichil.shusshare.repository.mybatis;

import net.akichil.shusshare.entity.RecruitmentGenre;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecruitmentGenreTypeHandler extends BaseTypeHandler<RecruitmentGenre> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, RecruitmentGenre parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, String.valueOf(parameter.getValue()));
    }

    @Override
    public RecruitmentGenre getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return RecruitmentGenre.getRecruitmentGenre(rs.getInt(columnName));
    }

    @Override
    public RecruitmentGenre getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return RecruitmentGenre.getRecruitmentGenre(rs.getInt(columnIndex));
    }

    @Override
    public RecruitmentGenre getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return RecruitmentGenre.getRecruitmentGenre(cs.getInt(columnIndex));
    }

}
