package com.flipkart.adq.mapper;

import com.flipkart.adq.model.Cmp_Exc_Date;
import com.flipkart.adq.model.DspCmp_Exc_Date;
import com.flipkart.adq.model.PublisherDate;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by ravi.pratap on 14/02/16.
 */


public class PublisherDateMapper implements ResultSetMapper<DspCmp_Exc_Date> {

    @Override
    public DspCmp_Exc_Date map(int i, ResultSet r, StatementContext statementContext) throws SQLException {
        return new DspCmp_Exc_Date(r.getLong("CampaignId"), r.getLong("ExchangeId"),r.getLong("AdRequests"),r.getLong("MatchedReqs"),r.getLong("UUMatched") , r.getLong("QualifiedReqs"), r.getLong("UUQualified"),r.getLong("BidsPlaced"),r.getLong("BidsWon"),r.getLong("Impressions"),r.getLong("Clicks"), r.getLong("Conversions"));
    }
}
