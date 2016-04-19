package com.flipkart.adq.dao;

import com.flipkart.adq.mapper.Cmp_Exc_DateMapper;
import com.flipkart.adq.mapper.PublisherDateMapper;
import com.flipkart.adq.model.Cmp_Exc_Date;
import com.flipkart.adq.model.DspCmp_Exc_Date;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

/**
 * Created by ravi.pratap on 12/02/16.
 */

@RegisterMapper(PublisherDateMapper.class)
public interface PublisherDateDAO {
        @SqlQuery("select cmpFunData.exchange_id as 'ExchangeId', reqT.name as 'Exchange Name', reqT.reqs as 'AdRequests', cmpFunData.campaign_id as 'CampaignId', cmpFunData.matched_count as 'MatchedReqs', cmpFunData.uuMatched as 'UUs Matched', cmpFunData.filter_passed_count as 'QualifiedReqs', cmpFunData.uuFiltered as 'UUs Qualified', cmpFunData.bidsPlaced as 'BidsPlaced', cmpFunData.bidsWon as 'BidsWon', cmpFunData.Imps as 'Impressions', cmpFunData.Clks as 'Clicks', cmpFunData.cnvs as 'Conversions' from   (select cfunT.created_at as date, cfunT.campaign_id as campaign_id, cfunT.exchange_id as exchange_id, cfunT.matched_count as matched_count, ufunT.unique_users_matched as uuMatched, cfunT.filter_passed_count as filter_passed_count, ufunT.unique_users_filter_passed as uuFiltered, if(reqT is NULL, 0, reqT) as bidsPlaced, if(winT is NULL, 0, winT) as bidsWon, if(impT is NULL, 0, impT) as Imps, if(cliT is NULL, 0, cliT) as Clks, if(cnvT is NULL, 0, cnvT) as Cnvs    from adq_bi.campaign_funnel cfunT    left outer join adq_bi.user_campaign_funnel as ufunT on cfunT.created_at = ufunT.created_at and cfunT.campaign_id = ufunT.campaign_id and cfunT.exchange_id = ufunT.exchange_id    left outer join  (      select  rpT1.date, rpT1.pubid, rpT1.cmpid,sum(req) as reqT,sum(win) as winT, sum(bcn) as impT, sum(cli) as cliT, sum(cnv) as cnvT  from gjx_reports_m.gjx_rpt_pub_con_plat_cdv_win_stats_dsp as rpT1        where rpT1.date=Date_      group by rpT1.date, rpT1.pubid, rpT1.cmpid     ) rpT on (rpT.pubid=cfunT.exchange_id and rpT.cmpid = cfunT.campaign_id)      where cfunT.created_at=Date_ and cfunT.exchange_id = ExchangeId) cmpFunData left outer join (   select sum(req) as reqs, pubid, date, name  from gjx_reports_m.gjx_rpt_pub_con_dsp join gjx_core_db.ox_affiliates on pubid=affiliateid where date=Date_ group by pubid ) reqT on cmpFunData.date = reqT.date and cmpFunData.exchange_id = reqT.pubid;")
        Cmp_Exc_Date getcmp_exc_date(@Bind("affid") long affid ,@Bind("date") String date );

}
