package com.flipkart.adq.resources;

import ch.qos.logback.core.status.Status;
import com.flipkart.adq.dao.Cmp_Exc_DateDAO;
import com.flipkart.adq.dao.CommonDAO;
import com.flipkart.adq.dao.DspCmp_Exc_DateDAO;
import com.flipkart.adq.model.Cmp_Exc_Date;
import com.flipkart.adq.model.DspCmp_Exc_Date;
import com.mysql.jdbc.exceptions.MySQLDataException;
import org.json.simple.JSONObject;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.IntegerMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Created by rahul.sachan on 20/01/16.
 */
@Path("/services/funnel")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrandAdsFunnelResource {
    Handle handle;
    private final Cmp_Exc_DateDAO cmp_exc_dateDAO;
    private final DspCmp_Exc_DateDAO dspCmp_exc_dateDAO;
    private final CommonDAO commonDAO;

    public BrandAdsFunnelResource(Handle handle , Cmp_Exc_DateDAO cmp_exc_dateDAO, DspCmp_Exc_DateDAO dspCmp_exc_dateDAO, CommonDAO commonDAO){
        this.handle = handle;
        this.cmp_exc_dateDAO = cmp_exc_dateDAO;
        this.dspCmp_exc_dateDAO = dspCmp_exc_dateDAO;
        this.commonDAO = commonDAO;
    }

    @GET
    @Path("/cmp_exe_date/")
    public Response getCmp_Exe_Date(@QueryParam("cmpid") long cmpid, @QueryParam("affid") long affid , @QueryParam("date") String date){
        Cmp_Exc_Date cmp_data ;
        try {
            cmp_data = cmp_exc_dateDAO.getcmp_exc_date(cmpid, affid, date);
            if (cmp_data != null) {
                return Response.ok()
                        .entity(cmp_data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
            else{
                return Response.status(Response.Status.NO_CONTENT)
                        .entity(cmp_data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }catch (Exception e){
            return Response.status(Response.Status.NO_CONTENT)
                    .entity(e.getMessage())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .build();
        }
    }

    @GET
    @Path("/cmp_date/")
    public Response getCmp_Date(@QueryParam("cmpid") long cmpid , @QueryParam("date") String date){
        Cmp_Exc_Date cmp_data ;
        try {
            cmp_data = cmp_exc_dateDAO.getcmp_date(cmpid, date);
            if (cmp_data != null) {
                return Response.ok()
                        .entity(cmp_data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
            else{
                return Response.status(Response.Status.NO_CONTENT)
                        .entity(cmp_data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }catch (Exception e){
            return Response.status(Response.Status.NO_CONTENT)
                    .entity(e.getMessage())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .build();
        }
    }

    @GET
    @Path("/exe_date/")
    public Response getExe_Date(@QueryParam("affid") long affid , @QueryParam("date") String date){
        Cmp_Exc_Date cmp_data ;
        try {
            cmp_data = cmp_exc_dateDAO.getexc_date(affid, date);
            if (cmp_data != null) {
                return Response.ok()
                        .entity(cmp_data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
            else{
                return Response.status(Response.Status.NO_CONTENT)
                        .entity(cmp_data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }catch (Exception e){
            return Response.status(Response.Status.NO_CONTENT)
                    .entity(e.getMessage())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .build();
        }
    }

    @Path("/error")
    @GET
    public Response getErrorcodeNumber(@QueryParam("cmpid") long cmpid, @QueryParam("affid") long affid , @QueryParam("date") String date)
    {
        Map<String,Long> data = new HashMap<>();
        List<Map<String,Long>> ss = new LinkedList<Map<String, Long>>();
        try {
            Iterator<Map<String, Long>> itr = null;
            if(cmpid >0 && affid >0){
                itr = commonDAO.getErrorNumber(cmpid, affid, date);
            }else if(cmpid > 0){
                itr = commonDAO.getErrorNumberbyCmp(cmpid,date);
            }else {
                itr = commonDAO.getErrorNumberbyAff(affid, date);
            }
            while (itr.hasNext()){
            data.putAll(itr.next());
            }

            if (data != null) {
                return Response.ok()
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
            else {
                return Response.status(Response.Status.NO_CONTENT)
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }catch (Exception e){
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/dsp_cmp_exe_date")
    public Response getDspCmp_Exe_Date(@QueryParam("cmpid") long cmpid, @QueryParam("affid") long affid , @QueryParam("date") String date) throws MySQLDataException{
        try {
            DspCmp_Exc_Date data = dspCmp_exc_dateDAO.getdspcmp_exc_date(cmpid, affid, date);
            if (data != null) {
                return Response.ok()
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
            else {
                return Response.status(Response.Status.NO_CONTENT)
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }catch (Exception e){
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/dsp_cmp_date")
    public Response getDspCmp_Date(@QueryParam("cmpid") long cmpid, @QueryParam("date") String date) throws Exception{
        try {
            DspCmp_Exc_Date data = dspCmp_exc_dateDAO.getdspcmp_date(cmpid, date);
            System.out.println();
            if (data != null) {
                return Response.ok()
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
            else {
                return Response.status(Response.Status.NO_CONTENT)
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }catch (Exception e){
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage().toString()).build();
        }
    }

    @GET
    @Path("/dsp_exe_date")
    public Response getDspExe_Date(@QueryParam("affid") long affid , @QueryParam("date") String date) throws MySQLDataException{
        try {
            DspCmp_Exc_Date data = dspCmp_exc_dateDAO.getdspexc_date(affid, date);
            if (data != null) {
                return Response.ok()
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
            else {
                return Response.status(Response.Status.NO_CONTENT)
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }catch (Exception e){
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/aff_list")
    public Response getAffiliateList(){
        List<String> aff_list =new  ArrayList<String>();

        try {
            aff_list = commonDAO.getAffiliate();
            List<String> finalList = new ArrayList<>();
            Iterator itr = aff_list.iterator();
            while (itr.hasNext()) {
                List<String> nList = new ArrayList<>();
                nList = (List<String>) itr.next();
                Iterator nitr = nList.iterator();
                finalList.add(nitr.next().toString());
            }
            finalList.add("15403508-AppNexus RTB Exchange(DSP)");
            return Response.status(Response.Status.OK)
                    .entity(finalList)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .build();
        }catch (Exception e){
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @GET
    @Path("/dspflag")
    public Response getDspcheck(@QueryParam("cmpid") long cmpid, @QueryParam("affid") long affid ,@QueryParam("date") String date){
        Map<Integer,Integer> isDsp =new HashMap<Integer,Integer>();
        try {
            isDsp = commonDAO.getDspFlag(cmpid, affid);
            System.out.print(isDsp.values());
            System.out.print(isDsp.containsKey(1));
            if(isDsp.isEmpty()){
                return Response.status(Response.Status.NO_CONTENT)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }catch (Exception e){
            return Response.status(Response.Status.NO_CONTENT)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .build();
        }
        Integer key = (Integer) isDsp.keySet().toArray()[0];
        Integer val = isDsp.get(key);

        if(key.intValue() == 1 && val.intValue() == 1){
            DspCmp_Exc_Date data = dspCmp_exc_dateDAO.getdspcmp_exc_date(cmpid, affid, date);
            if (data != null) {
                return Response.ok()
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
            else {
                return Response.status(Response.Status.NO_CONTENT)
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }
        else if(key.intValue() == 0 && val.intValue() == 0){
            Cmp_Exc_Date cmp_data ;
            cmp_data = cmp_exc_dateDAO.getcmp_exc_date(cmpid, affid, date);
            System.out.print(cmp_data);
            if (cmp_data != null) {
                return Response.ok()
                        .entity(cmp_data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
            else{
                return Response.status(Response.Status.NO_CONTENT)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }
        else{
            System.out.print("message");
            JSONObject jobj = new JSONObject();
            jobj.put("msg","CampaignId and AffiliateId mismatch");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jobj.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .build();
        }


    }

    @GET
    @Path("/databycmpid")
    @Produces("application/json")
    public Response getdatabyCamp(@QueryParam("cmpid") long cmpid, @QueryParam("date") String date) throws Exception{
        try {
            int isDsp = handle.createQuery("select is_dsp from gjx_core_db.ox_campaigns where campaignid=:cmpid;").bind("cmpid", cmpid).map(IntegerMapper.FIRST).first();
            if(isDsp == 1){
                DspCmp_Exc_Date data = dspCmp_exc_dateDAO.getdspcmp_date(cmpid, date);
                return Response.ok()
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
            else {
                Cmp_Exc_Date cmp_data = cmp_exc_dateDAO.getcmp_date(cmpid, date);
                return Response.ok()
                        .entity(cmp_data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }
        catch (NullPointerException e){
            return Response.status(Response.Status.NO_CONTENT)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .build();
        }

    }

    @GET
    @Path("/databyaffid")
    @Produces("application/json")
    public Response getdatabyAffid(@QueryParam("affid") long affid ,@QueryParam("date") String date){
        try {
            int isDsp = handle.createQuery("select is_dsp from gjx_core_db.ox_publishers p left join gjx_core_db.ox_affiliates a on p.publisherid = a.publisherid where a.affiliateid= :affid").bind("affid", affid).map(IntegerMapper.FIRST).first();
            System.out.println("is_dsp"+ isDsp);
            if(isDsp == 1){
                DspCmp_Exc_Date data = dspCmp_exc_dateDAO.getdspexc_date(affid,date);
                return Response.ok()
                        .entity(data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
            else {
                Cmp_Exc_Date cmp_data = cmp_exc_dateDAO.getexc_date(affid,date);
                return Response.ok()
                        .entity(cmp_data)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET")
                        .build();
            }
        }
        catch (NullPointerException e){
            return Response.status(Response.Status.NO_CONTENT)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .build();
        }
    }

    @GET
    @Path("/validate")
    public Response validate(@QueryParam("cmpid") long cmpid, @QueryParam("affid") long affid){
        int affid1 = commonDAO.getaffCheck(affid);
        int cmpid1 = commonDAO.getcmpCheck(cmpid);
        JSONObject jobj = new JSONObject();

        jobj.put("cmpid",cmpid);
        jobj.put("affid", affid1);
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .entity(jobj.toString())
                .build();
    }
}
