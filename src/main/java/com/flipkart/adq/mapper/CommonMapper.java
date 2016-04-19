package com.flipkart.adq.mapper;

import org.omg.PortableInterceptor.INACTIVE;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by rahul.sachan on 20/01/16.
 */
public class CommonMapper{

    public static class ErrorcodeMapper implements ResultSetMapper<Map<String, Long>> {
        @Override
        public Map<String, Long> map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            Map<String, Long> sessionUserMap = new HashMap<>();
           sessionUserMap.put(r.getString("Reason") , r.getLong("Filtered_Count"));
            return sessionUserMap;
        }
    }

    public static class AffiliateMapper implements ResultSetMapper<List<String>>{
        @Override
        public List<String> map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            List<String> sessionUserMap = new ArrayList<String>();
            sessionUserMap.add(r.getString("name"));
            return sessionUserMap;
        }
    }

    public static class Dspcheck implements ResultSetMapper<Map<Integer,Integer>>{
        @Override
        public Map<Integer,Integer> map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            Map<Integer,Integer> Dspflag = new HashMap<>();
            Dspflag.put(r.getInt("is_dsp"), r.getInt("aff_dsp"));
            return Dspflag;
        }
    }

    public static class cmpCheck implements ResultSetMapper<Integer>{
        @Override
        public Integer map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return r.getInt("campaignid");
        }
    }

    public static class affCheck implements ResultSetMapper<Integer>{
        @Override
        public Integer map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return r.getInt("affiliateid");
        }
    }
}
