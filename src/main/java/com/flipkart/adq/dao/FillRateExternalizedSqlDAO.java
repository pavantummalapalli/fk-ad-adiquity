package com.flipkart.adq.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

/**
 * Created by pavan.t on 14/04/16.
 */


@UseStringTemplate3StatementLocator
public interface FillRateExternalizedSqlDAO {
    @SqlQuery("select :sumOfImpressions/sum(`req`)*100 as metric from gjx_tmp_hrl_zone a where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') and zoneid in (<zoneIds>)")
    String getFillRate(@Bind("sumOfImpressions") double sumOfImpressions, @BindIn("zoneIds") List<String> zoneIds, @Bind("strTime")String strTime, @Bind("enTime")String enTime);

    @SqlQuery("select sum(`req`) as requests from gjx_tmp_hrl_zone a where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') and zoneid in (<zoneIds>)")
    String getRequests(@BindIn("zoneIds") List<String> zoneIds, @Bind("strTime")String strTime, @Bind("enTime")String enTime);

}
