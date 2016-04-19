#!/usr/bin/env bash


# $1 =~ ^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}-[0-9]{1,2}$

HIVE_DATABASE="analytics_adq_data"
HIVE_SERVED_REQUESTS_TABLE="adq_direct_served_request"
HIVE_UNSERVED_REQUESTS_TABLE="adq_direct_unserved_request"


adqLocalReqDir="/adqiuity/requests"
adqNadiReqDir="/serving/requests.ptr"
aqdRemoteStoreDir="/serving/requests"

mysql_user="root"
mysql_password="root"
mysql_dbName="plareports"
mysql_tableName="gjx_hrly_zn_req"
mysql_host="127.0.0.1"


function getDateUnits(){
    curDateTimeFormat=(${1//-/ })
    year=${curDateTimeFormat[0]}
    month=${curDateTimeFormat[1]}
    day=${curDateTimeFormat[2]}
    hour=${curDateTimeFormat[3]}
    minutes0=0
    minutes30=30
}

if [ "$#" -eq 1 ]; then
   echo $1
   getDateUnits $1

elif [ "$#" -gt 1 ];then
    echo "wrong number of arguments"
    exit 1
else
    curDateTime=`date -u -d "1 hour ago"  +%Y-%m-%d-%H | tr -d '\n'`
    getDateUnits $curDateTime
fi

hive -e "select x.year,x.month, x.day, x.hour, x.minutes ,x.zone_id,COUNT( distinct x.request_id) as requests
from (select year, month, day, hour, minutes , zone_id , request_id from adq_direct_served_request  where  year=${year}
and month=${month} and day=${day}  and hour =${hour} and  (minutes=${minutes0} or minutes=${minutes30}) union all
select year, month, day, hour, minutes , zone_id , request_id from adq_direct_unserved_request  where  year=${year}
and month=${month} and day=${day}  and hour =${hour} and  (minutes=${minutes0} or minutes=${minutes30}) ) x group by
x.year,x.month, x.day, x.hour, x.minutes,x.zone_id;" > ${adqLocalReqDir}/requests_${year}_${month}_${day}_${hour}.csv

read -d '' metaJson << EOF
        {
   "meta": [
       {
           "src": "${adqNadiReqDir}/requests_${year}_${month}_${day}_${hour}.csv",
           "destination": "${aqdRemoteStoreDir}/requests_${year}_${month}_${day}_${hour}.csv",
           "action": "echo "LOAD DATA INFILE ${aqdRemoteStoreDir}/requests_${year}_${month}_${day}_${hour}.csv INTO TABLE ${mysql_tableName};" | mysql -h${mysql_host} -u ${mysql_user} -p ${mysql_password} ${mysql_dbName}"
       }
   ]
}
EOF

echo $metaJson;






