package com.flipkart.adq.resources;
import com.flipkart.adq.dao.FillRateExternalizedSqlDAO;
import com.flipkart.adq.representations.CombinedRequestsServedImpressionsInterval;
import com.flipkart.adq.representations.RequestsZoneIdInterval;
import com.flipkart.adq.representations.ServedImpressionsZoneIdInterval;
import com.flipkart.adq.representations.ZoneInterval;
import com.flipkart.adq.utils.SqlArray;
import com.flipkart.adq.utils.Tuple;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.skife.jdbi.v2.Folder2;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.stringtemplate.ExternalizedSqlViaStringTemplate3;
import org.skife.jdbi.v2.sqlobject.stringtemplate.StringTemplate3StatementLocator;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.unstable.BindIn;
import org.skife.jdbi.v2.util.IntegerMapper;
import org.skife.jdbi.v2.util.StringMapper;
import redis.clients.jedis.Jedis;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.awt.image.AreaAveragingScaleFilter;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  *  * Created by pavan.t on 12/01/16.
 *   *   */


/*
      /apptotalrequests
      /appintervalrequests
      /apptotalfillrate
      /appintervalfillrate
      /apptotalsef
      /appintervalsef
      /apptotalimpressions
      /appintervalimpressions
      /apptotalviews
      /appintervalviews
      /apptotalviewrate
      /appintervalviewrate
      /apptotalactionrate
      /appintervalactionrate
      /apptotalengrate
      /appintervalengrate
      /appintervallivecamapigns
      /appintervalviewspercamapign


      /appsumintervalrequests
      /appsumintervalfillrate
      /appsumintervalsef
      /appsumintervalimpressions
      /appsumintervalviews
      /appsumintervalviewrate
      /appsumintervalactionrate
      /appsumintervalengrate
      /appsumintervallivecampaigns

      /desktoptotalrequests
      /desktopintervalrequests
      /desktoptotalfillrate
      /desktopintervalfillrate
      /desktoptotalsef
      /desktopintervalsef
      /desktoptotalimpressions
      /desktopintervalimpressions
      /desktoptotalactionrate
      /desktopintervalactionrate
      /desktopintervallivecampaigns



      /desktopsumintervalrequests
      /desktopsumintervalfillrate
      /desktopsumintervalsef
      /desktopsumintervalimpressions
      /desktopsumintervalactionrate
      /desktopsumintervallivecampaigns

 */


@Path("/services/ticker")
public class BrandAdsResource  {
    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    private static final Joiner JOINER = Joiner.on(",");
    Handle handle;
    Handle handle2;
    private Jedis jedis;
    public BrandAdsResource(Handle handle, Handle handle2, Jedis jedis) {
        this.handle = handle;
        this.handle2=handle2;
        this.jedis = jedis;
    }


    public Response getTotals(Handle handle,String query){

        String metric = handle.createQuery(query)
                .map(StringMapper.FIRST)
                .first();
        Map<String ,String> metricResultMap = new HashMap<>();

        try{
            if(metric == null){
                metricResultMap.put("success","false");
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .entity(metricResultMap).build();
            }

            metricResultMap.put("metric",metric);
            metricResultMap.put("success","true");
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .entity(metricResultMap)
                    .build();
        }
        catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    public Response getIntervals(Handle handle,String query,String startTime,String endTime){

        if(startTime==null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .build();
        }

        if(endTime==null){
            endTime=getCurrentTime();
        }

        String strTime = convertStringToDateTime(startTime);
        String enTime = convertStringToDateTime(endTime);



        LinkedHashMap<String,Double> intervalViewsMap = handle.createQuery(query).bind("strTime",strTime).bind("enTime",enTime).fold(new LinkedHashMap<String,Double>(), new Folder2<LinkedHashMap<String, Double>>() {
            @Override
            public LinkedHashMap<String, Double> fold(LinkedHashMap<String, Double> accumulator, ResultSet rs, StatementContext ctx) throws SQLException {
                accumulator.put(rs.getString("dateTime"),rs.getDouble("metric"));
                return accumulator;
            }
        });

        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .entity(intervalViewsMap).build();
    }


    public Response getSumIntervals(Handle handle,String query,String startTime , String endTime){
        if(startTime==null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .build();
        }

        if(endTime==null){
            endTime=getCurrentTime();
        }

        String strTime = convertStringToDateTime(startTime);
        String enTime = convertStringToDateTime(endTime);

        String metric = handle.createQuery(query)
                .bind("strTime",strTime)
                .bind("enTime",enTime)
                .map(StringMapper.FIRST)
                .first();
        Map<String ,String> metricResultMap = new HashMap<>();

        try{
            if(metric == null){
                metricResultMap.put("success","false");
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .entity(metricResultMap).build();
            }

            metricResultMap.put("metric",metric);
            metricResultMap.put("success","true");
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .entity(metricResultMap)
                    .build();
        }
        catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }



    @GET
    @Path("/apptotalviews")
    @Produces("application/json")
    public Response appTotalViews(){
        String query = "select sum(tav.view) from gjx_cdv_bnzn_action_atm_tz tav  join `gjx_core_db`.ox_banners ban on tav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD';";
        return getTotals(handle,query);
    }


    @GET
    @Path("/appintervalviews")
    @Produces("application/json")
    public Response appIntervalViews(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query = "select str_to_date(concat(date ,' ',concat(hour,':',bucket)),'%Y-%m-%d %H:%i') as `datetime` , sum(view) as metric from gjx_bi_hly_cdv_bnzn_action_tz hav join `gjx_core_db`.ox_banners ban on hav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(date ,' ',concat(hour,':',bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by str_to_date(concat(date ,' ',concat(hour,':',bucket)),'%Y-%m-%d %H:%i') order by datetime;";
        return getIntervals(handle,query,startTime,endTime);

    }

    @GET
    @Path("/apptotalviewrate")
    @Produces("application/json")
    public Response appTotalViewRate(){
        String query="select sum(hav.view)/sum(hab.bcn)*100 as metric from gjx_cdv_bnzn_action_atm_tz hav join `gjx_cdv_atm_znbn_tz` hab on hav.`bannerid`=hab.`bannerid` and hav.`zoneid`=hab.`zoneid` and hav.`date`=hab.date  join `gjx_core_db`.ox_banners ban on hav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD';";
        return getTotals(handle,query);
    }

    @GET
    @Path("/appintervalviewrate")
    @Produces("application/json")
    public Response appIntervalViewRate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') as `datetime` , sum(hav.view)/sum(hab.bcn)*100 as metric from gjx_bi_hly_cdv_bnzn_action_tz hav join `gjx_bi_hly_cdv_znbn_tz` hab on hav.`bannerid`=hab.`bannerid` and hav.`zoneid`=hab.`zoneid` and hav.`date`=hab.date and hav.`hour`=hab.`hour` and hav.`bucket`=hab.`bucket` join `gjx_core_db`.ox_banners ban on hav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') order by datetime;";
        return getIntervals(handle,query,startTime,endTime);
    }


    @GET
    @Path("/apptotalsef")
    @Produces("application/json")
    public Response appTotalSef(){
        String query="select sum(hav.bcn)/sum(hav.imp)*100 as metric from `gjx_cdv_atm_znbn_tz` hav join `gjx_core_db`.ox_banners ban on hav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD';";
        return getTotals(handle,query);
    }

    @GET
    @Path("/appintervalsef")
    @Produces("application/json")
    public Response appIntervalSef(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') as `datetime`,sum(hav.bcn)/sum(hav.imp)*100 as metric from `gjx_bi_hly_cdv_znbn_tz` hav join `gjx_core_db`.ox_banners ban on hav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') order by datetime;";
        return getIntervals(handle,query,startTime,endTime);
    }



    @GET
    @Path("/apptotalactionrate")
    @Produces("application/json")
    public Response appTotalActionRate(){
        String query="select  (sum(a.cli))/sum(b.view)*100 as metric from gjx_cdv_bnzn_action_atm_tz b  join `gjx_cdv_atm_znbn_tz` a on a.bannerid = b.bannerid and a.zoneid=b.zoneid and a.date=b.date join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD';";
        return getTotals(handle,query);
    }

    @GET
    @Path("/appintervalactionrate")
    @Produces("application/json")
    public Response appIntervalActionRate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') as `datetime`, (sum(a.cli))/sum(b.view)*100 as metric from gjx_bi_hly_cdv_bnzn_action_tz b  join `gjx_bi_hly_cdv_znbn_tz` a on a.bannerid = b.bannerid and a.zoneid=b.zoneid and a.date=b.date and a.hour=b.hour and a.`bucket`=b.`bucket` join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') order by datetime;";
        return getIntervals(handle,query,startTime,endTime);
    }



    @GET
    @Path("/apptotalengrate")
    @Produces("application/json")
    public Response appTotalEngRate(){
        String query="select (sum(b.action)-sum(a.cli))/sum(b.view)*100 as metric from gjx_cdv_bnzn_action_atm_tz b left join  `gjx_cdv_atm_znbn_tz` a on a.bannerid = b.bannerid and a.zoneid=b.zoneid and a.date=b.date join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD';";
        return getTotals(handle,query);
    }

    @GET
    @Path("/appintervalengrate")
    @Produces("application/json")
    public Response appIntervalEngRate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') as `datetime`, (sum(b.action)-sum(a.cli))/sum(b.view)*100 as metric from gjx_bi_hly_cdv_bnzn_action_tz b  left join `gjx_bi_hly_cdv_znbn_tz` a on a.bannerid = b.bannerid and a.zoneid=b.zoneid and a.date=b.date and a.hour=b.hour join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD'  where  str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') order by datetime;";
        return getIntervals(handle,query,startTime,endTime);
    }


    @GET
    @Path("/apptotalimpressions")
    @Produces("application/json")
    public Response appTotalImpressions(){
        String query="select sum(b.bcn) as metric from `gjx_cdv_atm_znbn_tz` b join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD';";
        return getTotals(handle,query);
    }

    @GET
    @Path("/appintervalimpressions")
    @Produces("application/json")
    public Response appIntervalImpressions(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query ="select str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') as `datetime`, sum(b.bcn) as metric from `gjx_bi_hly_cdv_znbn_tz` b join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') order by datetime;";
        return getIntervals(handle,query,startTime,endTime);
    }

    @GET
    @Path("/appintervalrequests")
    @Produces("application/json")
    public Response appIntervalRequests(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){

        String strTime = convertStringToDateTime(startTime);
        String enTime = convertStringToDateTime(endTime);


        String ZoneIntervalquery = "select date_add(a.date , interval a.hour hour) as datetime , zoneid from gjx_tmp_hly_cdv_zn_bn a join `gjx_core_db`.ox_banners ban on a.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by a.`zoneid`,date_add(a.date ,  interval a.hour hour) order by datetime;";

        String zoneRequestIntervalQuery ="select date_add(a.date , interval a.hour hour) as datetime ,zoneid,sum(`req`) requests from gjx_tmp_hrl_zone a where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by a.`zoneid`,date_add(a.date ,  interval a.hour hour) order by datetime;";

        List<ZoneInterval> ZoneIntervalIntervalMapList = handle.createQuery(ZoneIntervalquery).bind("strTime",strTime).bind("enTime",enTime).map(new ResultSetMapper<ZoneInterval>() {
            @Override
            public ZoneInterval map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                ZoneInterval zoneInterval = new ZoneInterval(r.getString("datetime"),r.getInt("zoneid"));
                return zoneInterval;
            }
        }).list();

        List<RequestsZoneIdInterval> requestsZoneIdIntervalMapList = handle2.createQuery(zoneRequestIntervalQuery).bind("strTime",strTime).bind("enTime",enTime).map(new ResultSetMapper<RequestsZoneIdInterval>() {
            @Override
            public RequestsZoneIdInterval map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                RequestsZoneIdInterval requestsZoneIdInterval = new RequestsZoneIdInterval(r.getString("datetime"),r.getInt("zoneid"),r.getInt("requests"));
                return requestsZoneIdInterval;
            }
        }).list();

        Map<String, List<Tuple<ZoneInterval, RequestsZoneIdInterval>>> collect = ZoneIntervalIntervalMapList.stream().flatMap(sI -> requestsZoneIdIntervalMapList.stream().filter(rI -> ((sI.getZoneId() == rI.getZoneId()) && (sI.getDatetime().equals(rI.getDatetime())))).map(rI -> new Tuple<>(sI, rI))).collect(Collectors.groupingBy(t -> t.getA().getDatetime(),LinkedHashMap::new,Collectors.toList()));

        Map<String,Long> fillRateIntervalMap = new LinkedHashMap<>();
        for(String dateTime:collect.keySet()){
            long sumRequests =0;
            for(Tuple<ZoneInterval,RequestsZoneIdInterval> tuple:collect.get(dateTime)){
                sumRequests+=tuple.getB().getRequests();
            }
            fillRateIntervalMap.put(dateTime,sumRequests);
        }

        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .entity(fillRateIntervalMap).build();
    }

    @GET
    @Path("/appintervalfillrate")
    @Produces("application/json")
    public Response appIntervalaFillrate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){

        String strTime = convertStringToDateTime(startTime);
        String enTime = convertStringToDateTime(endTime);

        String servedImpressionsIntervalquery = "select date_add(a.date , interval a.hour hour) as datetime , zoneid,sum(imp) as servedimpressions from gjx_tmp_hly_cdv_zn_bn a join `gjx_core_db`.ox_banners ban on a.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by a.`zoneid`,date_add(a.date ,  interval a.hour hour) order by datetime;";

        String zoneRequestIntervalQuery ="select date_add(a.date ,interval a.hour hour) as datetime ,zoneid,sum(`req`) requests from gjx_tmp_hrl_zone a where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by a.`zoneid`,date_add(a.date ,  interval a.hour hour) order by datetime;";

        List<ServedImpressionsZoneIdInterval> servedImpressionsIntervalMapList = handle.createQuery(servedImpressionsIntervalquery).bind("strTime",strTime).bind("enTime",enTime).map(new ResultSetMapper<ServedImpressionsZoneIdInterval>() {
            @Override
            public ServedImpressionsZoneIdInterval map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                ServedImpressionsZoneIdInterval servedImpressionsZoneIdInterval = new ServedImpressionsZoneIdInterval(r.getString("datetime"),r.getInt("zoneid"),r.getInt("servedimpressions"));
                return servedImpressionsZoneIdInterval;
            }
        }).list();

        List<RequestsZoneIdInterval> requestsZoneIdIntervalMapList = handle2.createQuery(zoneRequestIntervalQuery).bind("strTime",strTime).bind("enTime",enTime).map(new ResultSetMapper<RequestsZoneIdInterval>() {
            @Override
            public RequestsZoneIdInterval map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                RequestsZoneIdInterval requestsZoneIdInterval = new RequestsZoneIdInterval(r.getString("datetime"),r.getInt("zoneid"),r.getInt("requests"));
                return requestsZoneIdInterval;
            }
        }).list();

        Map<String, List<Tuple<ServedImpressionsZoneIdInterval, RequestsZoneIdInterval>>> collect = servedImpressionsIntervalMapList.stream().flatMap(sI -> requestsZoneIdIntervalMapList.stream().filter(rI -> ((sI.getZoneId() == rI.getZoneId()) && (sI.getDatetime().equals(rI.getDatetime())))).map(rI -> new Tuple<>(sI, rI))).collect(Collectors.groupingBy(t -> t.getA().getDatetime(),LinkedHashMap::new,Collectors.toList()));

        Map<String,Double> fillRateIntervalMap = new LinkedHashMap<>();
        for(String dateTime:collect.keySet()){
            double sumImpressions =0;
            double sumRequests =0;
            for(Tuple<ServedImpressionsZoneIdInterval,RequestsZoneIdInterval> tuple:collect.get(dateTime)){
                sumImpressions+=tuple.getA().getServedImpressions();
                sumRequests+=tuple.getB().getRequests();
            }
            double fillRate = (double)(sumImpressions/sumRequests)*100;
            fillRateIntervalMap.put(dateTime,fillRate);
        }

        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .entity(fillRateIntervalMap).build();
    }



    @GET
    @Path("/appsumintervalrequests")
    @Produces("application/json")
    public  Response appSumIntervalRequests(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){

        String strTime = convertStringToDateTime(startTime);
        String enTime = convertStringToDateTime(endTime);


        String ZoneIntervalquery = "select distinct zoneid from gjx_tmp_hly_cdv_zn_bn a join `gjx_core_db`.ox_banners ban on a.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i');";

        List<String> zoneIds = handle.createQuery(ZoneIntervalquery).bind("strTime",strTime).bind("enTime",enTime).map(StringMapper.FIRST).list();
        String metric  = handle2.attach(FillRateExternalizedSqlDAO.class).getRequests(zoneIds,strTime,enTime);

        Map<String ,String> metricResultMap = new HashMap<>();

        try{
            if(metric == null){
                metricResultMap.put("success","false");
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .entity(metricResultMap).build();
            }

            metricResultMap.put("metric",metric);
            metricResultMap.put("success","true");
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .entity(metricResultMap)
                    .build();
        }
        catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GET
    @Path("/appsumintervalfillrate")
    @Produces("application/json")
    public  Response appSumIntervalFillRate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){

        String strTime = convertStringToDateTime(startTime);
        String enTime = convertStringToDateTime(endTime);

        String servedImpressionsIntervalquery = "select zoneid,sum(imp) as impressions from gjx_tmp_hly_cdv_zn_bn a join `gjx_core_db`.ox_banners ban on a.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by zoneid;";

        LinkedHashMap<String,Double> sumImpressionsperZoneIdMap = handle.createQuery(servedImpressionsIntervalquery).bind("strTime",strTime).bind("enTime",enTime).fold(new LinkedHashMap<String,Double>(), new Folder2<LinkedHashMap<String, Double>>() {
            @Override
            public LinkedHashMap<String, Double> fold(LinkedHashMap<String, Double> accumulator, ResultSet rs, StatementContext ctx) throws SQLException {
                accumulator.put(rs.getString("zoneid"),rs.getDouble("impressions"));
                return accumulator;
            }
        });

        double sumofImpressions = sumImpressionsperZoneIdMap.entrySet().stream().mapToDouble
                (Map.Entry::getValue).sum();

        List<String> zoneIds = new ArrayList<>();
        zoneIds.addAll(sumImpressionsperZoneIdMap.keySet());

        String metric = handle2.attach(FillRateExternalizedSqlDAO.class).getFillRate(sumofImpressions,zoneIds,strTime,enTime);
        Map<String ,String> metricResultMap = new HashMap<>();

        try{
            if(metric == null){
                metricResultMap.put("success","false");
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .entity(metricResultMap).build();
            }

            metricResultMap.put("metric",metric);
            metricResultMap.put("success","true");
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .entity(metricResultMap)
                    .build();
        }
        catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GET
    @Path("/appsumintervalsef")
    @Produces("application/json")
    public  Response appSumIntervalSef(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select sum(hav.bcn)/sum(hav.imp)*100 as metric from `gjx_bi_hly_cdv_znbn_tz` hav join `gjx_core_db`.ox_banners ban on hav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i');";
        return getSumIntervals(handle,query,startTime,endTime);

    }

    @GET
    @Path("/appsumintervalimpressions")
    @Produces("application/json")
    public  Response appSumIntervalImpressions(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query ="select sum(b.bcn) as metric from `gjx_bi_hly_cdv_znbn_tz` b join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i')";
        return getSumIntervals(handle,query,startTime,endTime);
    }

    @GET
    @Path("/appsumintervalviews")
    @Produces("application/json")
    public  Response appSumIntervalViews(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query = "select sum(view) as metric from gjx_bi_hly_cdv_bnzn_action_tz hav join `gjx_core_db`.ox_banners ban on hav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(date ,' ',concat(hour,':',bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i')";
        return getSumIntervals(handle,query,startTime,endTime);
    }

    @GET
    @Path("/appsumintervalviewrate")
    @Produces("application/json")
    public  Response appSumIntervalViewRate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select sum(hav.view)/sum(hab.bcn)*100 as metric from gjx_bi_hly_cdv_bnzn_action_tz hav join `gjx_bi_hly_cdv_znbn_tz` hab on hav.`bannerid`=hab.`bannerid` and hav.`zoneid`=hab.`zoneid` and hav.`date`=hab.date and hav.`hour`=hab.`hour` and hav.`bucket`=hab.`bucket` join `gjx_core_db`.ox_banners ban on hav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') ";
        return getSumIntervals(handle,query,startTime,endTime);
    }

    @GET
    @Path("/appsumintervalactionrate")
    @Produces("application/json")
    public  Response appSumIntervalActionRate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select (sum(a.cli))/sum(b.view)*100 as metric from gjx_bi_hly_cdv_bnzn_action_tz b  join `gjx_bi_hly_cdv_znbn_tz` a on a.bannerid = b.bannerid and a.zoneid=b.zoneid and a.date=b.date and a.hour=b.hour and a.`bucket`=b.`bucket` join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i')";
        return getSumIntervals(handle,query,startTime,endTime);
    }

    @GET
    @Path("/appsumintervalengrate")
    @Produces("application/json")
    public  Response appSumIntervalEngRate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select (sum(b.action)-sum(a.cli))/sum(b.view)*100 as metric from gjx_bi_hly_cdv_bnzn_action_tz b  left join `gjx_bi_hly_cdv_znbn_tz` a on a.bannerid = b.bannerid and a.zoneid=b.zoneid and a.date=b.date and a.hour=b.hour join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD'  where  str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i')";
        return getSumIntervals(handle,query,startTime,endTime);
    }

    @GET
    @Path("/appsumintervallivecampaigns")
    @Produces("application/json")
    public  Response appSumIntervalLiveCampaigns(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query ="select sum(b.bcn) as metric from `gjx_bi_hly_cdv_znbn_tz` b join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='ANDROID_APPS' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i')";
        return getSumIntervals(handle,query,startTime,endTime);
    }


    @GET
    @Path("/desktoptotalsef")
    @Produces("application/json")
    public Response dekstopTotalSef(){
        String query="select sum(hav.bcn)/sum(hav.imp)*100 as metric from `gjx_cdv_atm_znbn_tz` hav join `gjx_core_db`.ox_banners ban on hav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD';";
        return getTotals(handle,query);
    }

    @GET
    @Path("/desktopintervalsef")
    @Produces("application/json")
    public Response desktopIntervalSef(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') as `datetime`,sum(hav.bcn)/sum(hav.imp)*100 as metric from `gjx_bi_hly_cdv_znbn_tz` hav join `gjx_core_db`.ox_banners ban on hav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') order by datetime;";
        return getIntervals(handle,query,startTime,endTime);
    }

    @GET
    @Path("/desktoptotalactionrate")
    @Produces("application/json")
    public Response desktopTotalActionRate(){
        String query="select  (sum(b.cli))/sum(b.bcn)*100 as metric from `gjx_cdv_atm_znbn_tz` b join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD';";
        return getTotals(handle,query);
    }

    @GET
    @Path("/desktopintervalactionrate")
    @Produces("application/json")
    public Response desktopIntervalActionRate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select str_to_date(concat(a.date ,' ',concat(a.hour,':',a.bucket)),'%Y-%m-%d %H:%i') as `datetime` ,(sum(a.cli))/sum(a.bcn)*100 as metric  from `gjx_bi_hly_cdv_znbn_tz` a  join `gjx_core_db`.ox_banners ban on a.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(a.date ,' ',concat(a.hour,':',a.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by str_to_date(concat(a.date ,' ',concat(a.hour,':',a.bucket)),'%Y-%m-%d %H:%i') order by datetime;";
        return getIntervals(handle,query,startTime,endTime);
    }


    @GET
    @Path("/desktopintervalfillrate")
    @Produces("application/json")
    public Response desktopIntervalFillrate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){

        String strTime = convertStringToDateTime(startTime);
        String enTime = convertStringToDateTime(endTime);

       String servedImpressionsIntervalquery = "select date_add(a.date , interval a.hour hour) as datetime , zoneid,sum(imp) as servedimpressions from gjx_tmp_hly_cdv_zn_bn a join `gjx_core_db`.ox_banners ban on a.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD' where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by a.`zoneid`,date_add(a.date ,  interval a.hour hour) order by datetime;";

       String zoneRequestIntervalQuery ="select date_add(a.date , interval a.hour hour) as datetime ,zoneid,sum(`req`) requests from gjx_tmp_hrl_zone a where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by a.`zoneid`,date_add(a.date ,  interval a.hour hour) order by datetime;";

        List<ServedImpressionsZoneIdInterval> servedImpressionsIntervalMapList = handle.createQuery(servedImpressionsIntervalquery).bind("strTime",strTime).bind("enTime",enTime).map(new ResultSetMapper<ServedImpressionsZoneIdInterval>() {
            @Override
            public ServedImpressionsZoneIdInterval map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                ServedImpressionsZoneIdInterval servedImpressionsZoneIdInterval = new ServedImpressionsZoneIdInterval(r.getString("datetime"),r.getInt("zoneid"),r.getInt("servedimpressions"));
                return servedImpressionsZoneIdInterval;
            }
        }).list();

        List<RequestsZoneIdInterval> requestsZoneIdIntervalMapList = handle2.createQuery(zoneRequestIntervalQuery).bind("strTime",strTime).bind("enTime",enTime).map(new ResultSetMapper<RequestsZoneIdInterval>() {
            @Override
            public RequestsZoneIdInterval map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                RequestsZoneIdInterval requestsZoneIdInterval = new RequestsZoneIdInterval(r.getString("datetime"),r.getInt("zoneid"),r.getInt("requests"));
                return requestsZoneIdInterval;
            }
        }).list();

        Map<String, List<Tuple<ServedImpressionsZoneIdInterval, RequestsZoneIdInterval>>> collect = servedImpressionsIntervalMapList.stream().flatMap(sI -> requestsZoneIdIntervalMapList.stream().filter(rI -> ((sI.getZoneId() == rI.getZoneId()) && (sI.getDatetime().equals(rI.getDatetime())))).map(rI -> new Tuple<>(sI, rI))).collect(Collectors.groupingBy(t -> t.getA().getDatetime(),LinkedHashMap::new,Collectors.toList()));

        Map<String,Double> fillRateIntervalMap = new LinkedHashMap<>();
        for(String dateTime:collect.keySet()){
            double sumImpressions =0;
            double sumRequests =0;
            for(Tuple<ServedImpressionsZoneIdInterval,RequestsZoneIdInterval> tuple:collect.get(dateTime)){
                sumImpressions+=tuple.getA().getServedImpressions();
                sumRequests+=tuple.getB().getRequests();
            }
            double fillRate = (double)(sumImpressions/sumRequests)*100;
            fillRateIntervalMap.put(dateTime,fillRate);
        }

        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .entity(fillRateIntervalMap).build();
    }

    @GET
    @Path("/desktopintervalrequests")
    @Produces("application/json")
    public Response desktopIntervalRequets(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){

        String strTime = convertStringToDateTime(startTime);
        String enTime = convertStringToDateTime(endTime);


        String ZoneIntervalquery = "select date_add(a.date , interval a.hour hour) as datetime , zoneid from gjx_tmp_hly_cdv_zn_bn a join `gjx_core_db`.ox_banners ban on a.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD' where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by a.`zoneid`,date_add(a.date ,  interval a.hour hour) order by datetime;";

        String zoneRequestIntervalQuery ="select date_add(a.date , interval a.hour hour) as datetime ,zoneid,sum(`req`) requests from gjx_tmp_hrl_zone a where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by a.`zoneid`,date_add(a.date ,  interval a.hour hour) order by datetime;";

        List<ZoneInterval> ZoneIntervalIntervalMapList = handle.createQuery(ZoneIntervalquery).bind("strTime",strTime).bind("enTime",enTime).map(new ResultSetMapper<ZoneInterval>() {
            @Override
            public ZoneInterval map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                ZoneInterval zoneInterval = new ZoneInterval(r.getString("datetime"),r.getInt("zoneid"));
                return zoneInterval;
            }
        }).list();

        List<RequestsZoneIdInterval> requestsZoneIdIntervalMapList = handle2.createQuery(zoneRequestIntervalQuery).bind("strTime",strTime).bind("enTime",enTime).map(new ResultSetMapper<RequestsZoneIdInterval>() {
            @Override
            public RequestsZoneIdInterval map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                RequestsZoneIdInterval requestsZoneIdInterval = new RequestsZoneIdInterval(r.getString("datetime"),r.getInt("zoneid"),r.getInt("requests"));
                return requestsZoneIdInterval;
            }
        }).list();

        Map<String, List<Tuple<ZoneInterval, RequestsZoneIdInterval>>> collect = ZoneIntervalIntervalMapList.stream().flatMap(sI -> requestsZoneIdIntervalMapList.stream().filter(rI -> ((sI.getZoneId() == rI.getZoneId()) && (sI.getDatetime().equals(rI.getDatetime())))).map(rI -> new Tuple<>(sI, rI))).collect(Collectors.groupingBy(t -> t.getA().getDatetime(),LinkedHashMap::new,Collectors.toList()));

        Map<String,Long> fillRateIntervalMap = new LinkedHashMap<>();
        for(String dateTime:collect.keySet()){
            long sumRequests =0;
            for(Tuple<ZoneInterval,RequestsZoneIdInterval> tuple:collect.get(dateTime)){
                sumRequests+=tuple.getB().getRequests();
            }
            fillRateIntervalMap.put(dateTime,sumRequests);
        }

        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .entity(fillRateIntervalMap).build();
    }

    @GET
    @Path("/desktoptotalimpressions")
    @Produces("application/json")
    public Response desktopTotalImpressions(){
        String query="select sum(b.bcn) as metric from `gjx_cdv_atm_znbn_tz` b join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD';";
        return getTotals(handle,query);
    }

    @GET
    @Path("/desktopintervalimpressions")
    @Produces("application/json")
    public Response desktopIntervalImpressions(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query ="select str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') as `datetime`, sum(b.bcn) as metric from `gjx_bi_hly_cdv_znbn_tz` b join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') order by datetime;";
        return getIntervals(handle,query,startTime,endTime);
    }

    @GET
    @Path("/desktopsumintervalrequests")
    @Produces("application/json")
    public Response desktopSumIntervalRequests(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String strTime = convertStringToDateTime(startTime);
        String enTime = convertStringToDateTime(endTime);


        String ZoneIntervalquery = "select distinct zoneid from gjx_tmp_hly_cdv_zn_bn a join `gjx_core_db`.ox_banners ban on a.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD' where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i');";

        List<String> zoneIds = handle.createQuery(ZoneIntervalquery).bind("strTime",strTime).bind("enTime",enTime).map(StringMapper.FIRST).list();
        String metric  = handle2.attach(FillRateExternalizedSqlDAO.class).getRequests(zoneIds,strTime,enTime);

        Map<String ,String> metricResultMap = new HashMap<>();

        try{
            if(metric == null){
                metricResultMap.put("success","false");
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .entity(metricResultMap).build();
            }

            metricResultMap.put("metric",metric);
            metricResultMap.put("success","true");
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .entity(metricResultMap)
                    .build();
        }
        catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GET
    @Path("/desktopsumintervalfillrate")
    @Produces("application/json")
    public Response desktopSumIntervalFillRate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String strTime = convertStringToDateTime(startTime);
        String enTime = convertStringToDateTime(endTime);

        String servedImpressionsIntervalquery = "select zoneid,sum(imp) as impressions from gjx_tmp_hly_cdv_zn_bn a join `gjx_core_db`.ox_banners ban on a.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD' where  date_add(a.date ,  interval a.hour hour) between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i') group by zoneid;";

        LinkedHashMap<String,Double> sumImpressionsperZoneIdMap = handle.createQuery(servedImpressionsIntervalquery).bind("strTime",strTime).bind("enTime",enTime).fold(new LinkedHashMap<String,Double>(), new Folder2<LinkedHashMap<String, Double>>() {
            @Override
            public LinkedHashMap<String, Double> fold(LinkedHashMap<String, Double> accumulator, ResultSet rs, StatementContext ctx) throws SQLException {
                accumulator.put(rs.getString("zoneid"),rs.getDouble("impressions"));
                return accumulator;
            }
        });

        double sumofImpressions = sumImpressionsperZoneIdMap.entrySet().stream().mapToDouble
                (Map.Entry::getValue).sum();

        List<String> zoneIds = new ArrayList<>();
        zoneIds.addAll(sumImpressionsperZoneIdMap.keySet());

        String metric = handle2.attach(FillRateExternalizedSqlDAO.class).getFillRate(sumofImpressions,zoneIds,strTime,enTime);
        Map<String ,String> metricResultMap = new HashMap<>();

        try{
            if(metric == null){
                metricResultMap.put("success","false");
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .entity(metricResultMap).build();
            }

            metricResultMap.put("metric",metric);
            metricResultMap.put("success","true");
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .entity(metricResultMap)
                    .build();
        }
        catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/desktopsumintervalsef")
    @Produces("application/json")
    public Response desktopSumIntervalSef(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select sum(hav.bcn)/sum(hav.imp)*100 as metric from `gjx_bi_hly_cdv_znbn_tz` hav join `gjx_core_db`.ox_banners ban on hav.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(hav.date ,' ',concat(hav.hour,':',hav.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i');";
        return getSumIntervals(handle,query,startTime,endTime);
    }

    @GET
    @Path("/desktopsumintervalimpressions")
    @Produces("application/json")
    public Response desktopSumIntervalimpressions(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query ="select  sum(b.bcn) as metric as metric from `gjx_bi_hly_cdv_znbn_tz` b join `gjx_core_db`.ox_banners ban on b.bannerid=ban.`bannerid`  join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(b.date ,' ',concat(b.hour,':',b.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i')";
        return getSumIntervals(handle,query,startTime,endTime);
    }

    @GET
    @Path("/desktopsumintervalactionrate")
    @Produces("application/json")
    public Response desktopSumIntervalActionRate(@QueryParam("starttime") String startTime,@QueryParam("endtime") String endTime){
        String query="select (sum(a.cli))/sum(a.bcn)*100 as metric  from `gjx_bi_hly_cdv_znbn_tz` a  join `gjx_core_db`.ox_banners ban on a.bannerid=ban.`bannerid` join `gjx_core_db`.ox_campaigns cam on ban.`campaignid`=cam.`campaignid` and cam.affiliate_type='WEB_SITES-DESKTOP' and cam.`campaign_type`='BRANDAD' where  str_to_date(concat(a.date ,' ',concat(a.hour,':',a.bucket)),'%Y-%m-%d %H:%i') between str_to_date(:strTime, '%Y-%m-%d %H:%i') and str_to_date(:enTime, '%Y-%m-%d %H:%i')";
        return getSumIntervals(handle,query,startTime,endTime);
    }

    @GET
    @Path("/desktopsumintervallivecampaigns")
    @Produces("application/json")
    public Response desktopSumIntervalLiveCampaigns(){
        return null;
    }

    @GET
    @Path("/livecamp")
    @Produces("application/json")
    public Response getLiveCampaign() {
        int  desktopCamp = handle.createQuery("select count(distinct c.campaignid) livedCamp from gjx_core_db.ox_campaigns c join gjx_core_db.ox_banners b on c.campaignid=b.campaignid join gjx_tmp_hly_cdv_zn_bn hr on b.bannerid=hr.bannerid where date= CURDATE() and hour=(select max(hour) from gjx_tmp_hly_cdv_zn_bn where date=CURDATE()) and c.campaign_type='BRANDAD' and affiliate_type='WEB_SITES-DESKTOP' and hr.imp > 0;").map(IntegerMapper.FIRST).first();
        int appCamp = handle.createQuery("select count(distinct c.campaignid) livedCamp from gjx_core_db.ox_campaigns c join gjx_core_db.ox_banners b on c.campaignid=b.campaignid join gjx_tmp_hly_cdv_zn_bn hr on b.bannerid=hr.bannerid where date= CURDATE() and hour=(select max(hour) from gjx_tmp_hly_cdv_zn_bn where date=CURDATE()) and c.campaign_type='BRANDAD' and affiliate_type='ANDROID_APPS' and hr.imp > 0;").map(IntegerMapper.FIRST).first();
        JSONObject jobj = new JSONObject();
        jobj.put("desktopCamp", desktopCamp);
        jobj.put("appCamp",appCamp);
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .entity(jobj.toString()).build();
    }


    public String[] getLast24hrDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd#HH");
        Calendar c = Calendar.getInstance();
        String curdate =sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, -1);
        String ydate=sdf.format(c.getTime());
        String dates[]={curdate,ydate};
        return dates;
    }

    private String getCurrentTime() {

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeZone zone = DateTimeZone.forID("Asia/Kolkata");
            DateTime dt = new DateTime(zone);
            String dateTimeString = formatter.print(dt);
            return dateTimeString;
    }

    public String convertStringToDateTime(String dateTime){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dt = formatter.parseDateTime(dateTime);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String dateTimeString = fmt.print(dt);
        return dateTimeString;
    }

}









