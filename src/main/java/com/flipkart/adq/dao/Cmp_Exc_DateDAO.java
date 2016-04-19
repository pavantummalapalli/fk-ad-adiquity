package com.flipkart.adq.dao;

import com.flipkart.adq.mapper.Cmp_Exc_DateMapper;
import com.flipkart.adq.mapper.CommonMapper;
import com.flipkart.adq.model.Cmp_Exc_Date;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rahul.sachan on 20/01/16.
 */
@RegisterMapper(Cmp_Exc_DateMapper.class)
public interface Cmp_Exc_DateDAO {

    @SqlQuery("select cmpFunData.campaign_id as 'CampaignId',cmpFunData.exchange_id as 'AffiliateId',reqT.total_requests as 'Requests',cmpFunData.matched_count as 'MatchedReqs', cmpFunData.uuMatched as 'UUMatched', cmpFunData.filter_passed_count as 'QualifiedReqs', cmpFunData.uuFiltered as 'UUQualified', cmpFunData.Imps as 'Impressions', cmpFunData.Clks as 'Clicks', cmpFunData.cnvs as 'Conversions' from (select cfunT.created_at as date, cfunT.campaign_id as campaign_id,  cfunT.exchange_id as exchange_id,  cfunT.matched_count as matched_count,  ufunT.unique_users_matched as uuMatched, cfunT.filter_passed_count as filter_passed_count, ufunT.unique_users_filter_passed as uuFiltered,  if(impT is NULL, 0, impT) as Imps,  if(cliT is NULL, 0, cliT) as Clks,  if(cnvT is NULL, 0, cnvT) as cnvs from adq_bi.campaign_funnel as cfunT left outer join adq_bi.user_campaign_funnel as ufunT on cfunT.created_at = ufunT.created_at and cfunT.campaign_id = ufunT.campaign_id and cfunT.exchange_id = ufunT.exchange_id left outer join  (select  rpT1.date as date, rpT1.pubid, rpT1.cmpid,sum(req) as reqT, sum(bcn) as impT, sum(cli) as cliT, sum(cnv) as cnvT from gjx_reports_m.gjx_tmp_cdv_mon_cm_cn as rpT1 where rpT1.date= :date group by rpT1.date, rpT1.pubid, rpT1.cmpid) rpT on (rpT.pubid=cfunT.exchange_id and rpT.cmpid = cfunT.campaign_id) where cfunT.created_at= :date and cfunT.campaign_id = :cmpid)  cmpFunData left outer join (select total_requests,created_at from adq_bi.campaign_funnel_pub_reqs where exchange_id= :affid and created_at=:date) reqT on cmpFunData.date = reqT.created_at and cmpFunData.exchange_id = :affid;")
    Cmp_Exc_Date getcmp_exc_date(@Bind("cmpid") long cmpid, @Bind("affid") long affid ,@Bind("date") String date );

    @SqlQuery("select cmpFunData.campaign_id as 'CampaignId', cmpFunData.exchange_id as 'AffiliateId',sum(reqT.total_requests) as 'Requests', sum(cmpFunData.matched_count) as 'MatchedReqs',sum(cmpFunData.uuMatched) as 'UUMatched', sum(cmpFunData.filter_passed_count) as 'QualifiedReqs', sum(cmpFunData.uuFiltered) as 'UUQualified',  sum(cmpFunData.Imps) as 'Impressions', sum(cmpFunData.Clks) as 'Clicks', sum(cmpFunData.cnvs) as 'Conversions' from   (select cfunT.created_at as date,cfunT.campaign_id as campaign_id,cfunT.exchange_id as exchange_id,cfunT.matched_count as matched_count,ufunT.unique_users_matched as uuMatched,cfunT.filter_passed_count as filter_passed_count,            ufunT.unique_users_filter_passed as uuFiltered,if(impT is NULL, 0, impT) as Imps, if(cliT is NULL, 0, cliT) as Clks, if(cnvT is NULL, 0, cnvT) as cnvs from adq_bi.campaign_funnel cfunT left outer join adq_bi.user_campaign_funnel as ufunT on cfunT.created_at = ufunT.created_at and cfunT.campaign_id = ufunT.campaign_id and cfunT.exchange_id = ufunT.exchange_id left outer join  (   select  rpT1.date as date, rpT1.pubid, rpT1.cmpid,sum(req) as reqT, sum(bcn) as impT, sum(cli) as cliT, sum(cnv) as cnvT  from gjx_reports_m.gjx_tmp_cdv_mon_cm_cn as rpT1 where rpT1.date= :date group by rpT1.date, rpT1.pubid, rpT1.cmpid ) rpT on (rpT.pubid=cfunT.exchange_id and rpT.cmpid = cfunT.campaign_id) where cfunT.created_at=:date  and cfunT.campaign_id = :cmpid)  cmpFunData  left outer join ( select exchange_id,total_requests,created_at,name from adq_bi.campaign_funnel_pub_reqs join gjx_core_db.ox_affiliates on exchange_id=affiliateid                           ) reqT on cmpFunData.date = reqT.created_at and cmpFunData.exchange_id = reqT.exchange_id;")
    Cmp_Exc_Date getcmp_date(@Bind("cmpid") long cmpid ,@Bind("date") String date);

    @SqlQuery("select cmpFunData.campaign_id as 'CampaignId',cmpFunData.exchange_id as 'AffiliateId', reqT.total_requests as 'Requests',  sum(cmpFunData.matched_count) as 'MatchedReqs', sum(cmpFunData.uuMatched) as 'UUMatched', sum(cmpFunData.filter_passed_count) as 'QualifiedReqs',  sum(cmpFunData.uuFiltered) as 'UUQualified', sum(cmpFunData.Imps) as 'Impressions',   sum(cmpFunData.Limps) as 'Loaded Imps',   sum(cmpFunData.Clks) as 'Clicks',  sum(cmpFunData.cnvs) as 'Conversions' from   (select cfunT.created_at as date, cfunT.campaign_id as campaign_id, cfunT.exchange_id as exchange_id, cfunT.matched_count as matched_count, ufunT.unique_users_matched as uuMatched, cfunT.filter_passed_count as filter_passed_count, ufunT.unique_users_filter_passed as uuFiltered, if(impT is NULL, 0, impT) as Imps,if(limpT is NULL, 0, limpT) as Limps, if(cliT is NULL, 0, cliT) as Clks,  if(cnvT is NULL, 0, cnvT) as cnvs from adq_bi.campaign_funnel cfunT  left outer join adq_bi.user_campaign_funnel as ufunT on cfunT.created_at = ufunT.created_at   and cfunT.campaign_id = ufunT.campaign_id and cfunT.exchange_id = ufunT.exchange_id left outer join  (   select  rpT1.date as date, rpT1.pubid, rpT1.cmpid,sum(req) as reqT, sum(imp) as impT, sum(bcn) as limpT, sum(cli) as cliT, sum(cnv) as cnvT   from gjx_reports_m.gjx_tmp_cdv_mon_cm_cn as rpT1    where rpT1.date=:date       group by rpT1.date, rpT1.pubid, rpT1.cmpid   ) rpT on (rpT.pubid=cfunT.exchange_id and rpT.cmpid = cfunT.campaign_id)                     where cfunT.created_at=:date and cfunT.exchange_id =:affid)  cmpFunData left outer join ( select exchange_id,total_requests,created_at,name from adq_bi.campaign_funnel_pub_reqs join gjx_core_db.ox_affiliates on exchange_id=affiliateid) reqT on cmpFunData.date = reqT.created_at and cmpFunData.exchange_id = reqT.exchange_id;")
    Cmp_Exc_Date getexc_date(@Bind("affid") long affid ,@Bind("date") String date);
}
