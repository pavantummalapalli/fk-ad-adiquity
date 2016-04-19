package com.flipkart.adq.mapper;

import com.flipkart.adq.model.Cmp_Exc_Date;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by rahul.sachan on 20/01/16.
 */
public class Cmp_Exc_DateMapper implements ResultSetMapper<Cmp_Exc_Date> {
    @Override
    public Cmp_Exc_Date map(int i, ResultSet r, StatementContext statementContext) throws SQLException {
        return new Cmp_Exc_Date(r.getLong("CampaignId"), r.getLong("AffiliateId"),r.getLong("Requests"),r.getLong("MatchedReqs"),r.getLong("UUMatched"),r.getLong("QualifiedReqs"),r.getLong("UUQualified"), r.getLong("Impressions"),r.getLong("Clicks"),r.getLong("Conversions"));
    }
}
