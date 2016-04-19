package com.flipkart.adq.model;

/**
 * Created by rahul.sachan on 20/01/16.
 */
public class Cmp_Exc_Date {
    private String dspFlag;
    private long cmpid;
    private long affid;
    private long tReq;
    private long mReq;
    private long uuMatch;
    private long pReq;
    private long uuP;
    private long imp;
    private long cli;
    private long cnv;

    public Cmp_Exc_Date(long cmpid, long affid,long tReq, long mReq,long uuMatch, long pReq, long uuP, long imp, long cli, long cnv) {
        this.dspFlag = "false";
        this.cmpid = cmpid;
        this.affid = affid;
        this.tReq = tReq;
        this.mReq = mReq;
        this.uuMatch = uuMatch;
        this.pReq = pReq;
        this.uuP = uuP;
        this.imp = imp;
        this.cli = cli;
        this.cnv = cnv;
    }

    public long getCmpid() {
        return cmpid;
    }

    public void setCmpid(long cmpid) {
        this.cmpid = cmpid;
    }

    public long getAffid() {
        return affid;
    }

    public void setAffid(long affid) {
        this.affid = affid;
    }

    public long gettReq() {
        return tReq;
    }

    public void settReq(long tReq) {
        this.tReq = tReq;
    }

    public long getmReq() {
        return mReq;
    }

    public void setmReq(long mReq) {
        this.mReq = mReq;
    }

    public long getpReq() {
        return pReq;
    }

    public void setpReq(long pReq) {
        this.pReq = pReq;
    }

    public long getImp() {
        return imp;
    }

    public void setImp(long imp) {
        this.imp = imp;
    }

    public long getCli() {
        return cli;
    }

    public void setCli(long cli) {
        this.cli = cli;
    }

    public long getCnv() {
        return cnv;
    }

    public void setCnv(long cnv) {
        this.cnv = cnv;
    }

    public long getUuMatch() {
        return uuMatch;
    }

    public void setUuMatch(long uuMatch) {
        this.uuMatch = uuMatch;
    }

    public long getUuP() {
        return uuP;
    }

    public void setUuP(long uuP) {
        this.uuP = uuP;
    }

    public String getDspFlag() {
        return dspFlag;
    }

    public void setDspFlag(String dspFlag) {
        this.dspFlag = dspFlag;
    }
}
