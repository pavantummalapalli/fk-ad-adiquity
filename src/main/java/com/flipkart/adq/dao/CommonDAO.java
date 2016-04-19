package com.flipkart.adq.dao;

import com.flipkart.adq.mapper.CommonMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rahul.sachan on 02/02/16.
 */

@UseStringTemplate3StatementLocator
public interface CommonDAO {

    @Mapper(CommonMapper.ErrorcodeMapper.class)
    @SqlQuery("select resp_name as 'Reason',filtered_count as 'Filtered_Count' from adq_bi.filtered_rejected_campaigns join adq_bi.campaign_funnel_resp_code where reason=resp_code  and created_at = :date and campaign_id=:cmpid and exchange_id= :affid;")
    Iterator<Map<String,Long>> getErrorNumber(@Bind("cmpid") long cmpid, @Bind("affid") long affid, @Bind("date") String date);

    @Mapper(CommonMapper.ErrorcodeMapper.class)
    @SqlQuery("select resp_name as 'Reason',filtered_count as 'Filtered_Count' from adq_bi.filtered_rejected_campaigns join adq_bi.campaign_funnel_resp_code where reason=resp_code  and created_at = :date and exchange_id= :affid;")
    Iterator<Map<String,Long>> getErrorNumberbyAff(@Bind("affid") long affid, @Bind("date") String date);

    @Mapper(CommonMapper.ErrorcodeMapper.class)
    @SqlQuery("select resp_name as 'Reason',filtered_count as 'Filtered_Count' from adq_bi.filtered_rejected_campaigns join adq_bi.campaign_funnel_resp_code where reason=resp_code  and created_at = :date and campaign_id=:cmpid;")
    Iterator<Map<String,Long>> getErrorNumberbyCmp(@Bind("cmpid") long cmpid, @Bind("date") String date);

    @Mapper(CommonMapper.AffiliateMapper.class)
    @SqlQuery("select CONCAT(a.affiliateid ,\"-\",a.name) as name from gjx_core_db.ox_affiliates a left join gjx_core_db.ox_publishers p on a.publisherid=p.publisherid where p.publisher_type='FLIPKART' and p.status='ACTIVE'")
    List<String> getAffiliate();

    @Mapper(CommonMapper.Dspcheck.class)
    @SqlQuery("(select is_dsp,(select is_dsp from gjx_core_db.ox_publishers p left join gjx_core_db.ox_affiliates a on p.publisherid = a.publisherid where a.affiliateid= :affid)as aff_dsp from gjx_core_db.ox_campaigns where campaignid=:cmpid);")
    Map<Integer,Integer> getDspFlag(@Bind("cmpid") long cmpid, @Bind("affid") long affid);

    @Mapper(CommonMapper.cmpCheck.class)
    @SqlQuery("select campaignid from gjx_core_db.ox_campaigns where campaignid=:cmpid")
    int getcmpCheck(@Bind("cmpid") long cmpid);

    @Mapper(CommonMapper.affCheck.class)
    @SqlQuery("select affiliateid from gjx_core_db.ox_affiliates where affiliateid=:affid")
    int getaffCheck(@Bind("affid") long affid);


}
