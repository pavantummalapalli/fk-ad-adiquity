function getConstants(){
        var baseUrl="http://108.178.60.18:25916/services/ticker";
        return {
            appsumintervalrequests:baseUrl+"/appsumintervalrequests",
            appsumintervalfillrate:baseUrl+"/appsumintervalfillrate",
            appsumintervalsef:baseUrl+"/appsumintervalsef",
            appsumintervalimpressions:baseUrl+"/appsumintervalimpressions",
            appsumintervalviews:baseUrl+"/appsumintervalviews",
            appsumintervalviewrate:baseUrl+"/appsumintervalviewrate",
            appsumintervalactionrate:baseUrl+"/appsumintervalactionrate",
            appsumintervalengrate:baseUrl+"/appsumintervalengrate",
            appsumintervallivecampaigns:baseUrl+"/appsumintervallivecampaigns",
            apptotalviews:baseUrl+"/apptotalviews",
            appintervalviews:baseUrl+"/appintervalviews",
            apptotalviewrate:baseUrl+"/apptotalviewrate",
            appintervalviewrate:baseUrl+"/appintervalviewrate",
            apptotalengrate:baseUrl+"/apptotalengrate",
            appintervalengrate:baseUrl+"/appintervalengrate",
            apptotalactionrate:baseUrl+"/apptotalactionrate",
            appintervalactionrate:baseUrl+"/appintervalactionrate",
            desktoptotalactionrate:baseUrl+"/desktoptotalactionrate",
            desktopintervalactionrate:baseUrl+"/desktopintervalactionrate",
            apptotalfillrate:baseUrl+"/apptotalfillrate",
            appintervalfillrate:baseUrl+"/appintervalfillrate",
            desktoptotalfillrate:baseUrl+"/desktoptotalfillrate",
            desktopintervalfillrate:baseUrl+"/desktopintervalfillrate",
            apptotalsef:baseUrl+"/apptotalsef",
            appintervalsef:baseUrl+"/appintervalsef",
            desktoptotalsef:baseUrl+"/desktoptotalsef",
            desktopintervalsef:baseUrl+"/desktopintervalsef",
            apptotalrequests:baseUrl+"/apptotalrequests",
            appintervalrequests:baseUrl+"/appintervalrequests",
            desktoptotalrequests:baseUrl+"/desktoptotalrequests",
            desktopintervalrequests:baseUrl+"/desktopintervalrequests",
            apptotalimpressions:baseUrl+"/apptotalimpressions",
            appintervalimpressions:baseUrl+"/appintervalimpressions",
            desktoptotalimpressions:baseUrl+"/desktoptotalimpressions",
            desktopintervalimpressions:baseUrl+"/desktopintervalimpressions",
            appviewrate:baseUrl+"/appviewrate",
            appviews:baseUrl+"/apptotalviews",
            appengrate:baseUrl+"/appengrate",
            appactionrate:baseUrl+"/appactionrate",
            desktopactionrate:baseUrl+"/desktopactionrate",
            appfillrate:baseUrl+"/appfillrate",
            desktopfillrate:baseUrl+"/desktopfillrate",
            appsef:baseUrl+"/appsef",
            desktopsef:baseUrl+"/desktopsef",
            apprequests:baseUrl+"/apprequests",
            desktoprequests:baseUrl+"/desktoprequests",
            appimpressions:baseUrl+"/appimpressions",
            desktopimpressions:baseUrl+"/desktopimpressions",
            desktopsumintervalrequests:baseUrl+"/desktopsumintervalrequests",
            desktopsumintervalfillrate:baseUrl+"/desktopsumintervalfillrate",
            desktopsumintervalsef:baseUrl+"/desktopsumintervalsef",
            desktopsumintervalimpressions:baseUrl+"/desktopsumintervalimpressions",
            desktopsumintervalactionrate:baseUrl+"/desktopsumintervalactionrate",
            desktopsumintervallivecampaigns:baseUrl+"/desktopsumintervallivecampaigns"
        }
}

function getNameToUrlMappingDetails(brandAdsConstants){

        return {

                  views: [
                            {
                                type:"app",
                                format:"number",
                                totalURL:brandAdsConstants.appviews,
                                intervalURL:brandAdsConstants.appintervalviews,
                                sumIntervalURL:brandAdsConstants.appsumintervalviews

                            }
                         ],

                  viewRate:[
                                {
                                    type:"app",
                                    format:"percent",
                                    totalURL:brandAdsConstants.apptotalviewrate,
                                    intervalURL:brandAdsConstants.appintervalviewrate,
                                    sumIntervalURL:brandAdsConstants.appsumintervalviewrate
                                }

                            ],

                  engRate:[
                             {
                                 type:"app",
                                 format:"percent",
                                 totalURL:brandAdsConstants.apptotalengrate,
                                 intervalURL:brandAdsConstants.appintervalengrate,
                                 sumIntervalURL:brandAdsConstants.appsumintervalengrate
                             }
                          ],

                  actionRate:[
                                    {
                                            type:"app",
                                            format:"percent",
                                            totalURL:brandAdsConstants.apptotalactionrate,
                                            intervalURL:brandAdsConstants.appintervalactionrate,
                                            sumIntervalURL:brandAdsConstants.appsumintervalactionrate
                                    },
                                    {
                                            type:"desktop",
                                            format:"percent",
                                            totalURL:brandAdsConstants.desktoptotalactionrate,
                                            intervalURL:brandAdsConstants.desktopintervalactionrate,
                                            sumIntervalURL:brandAdsConstants.desktopsumintervalactionrate
                                    }
                             ],

                  fillRate:[
                                {
                                         type:"app",
                                         format:"percent",
                                         totalURL:brandAdsConstants.apptotalfillrate,
                                         intervalURL:brandAdsConstants.appintervalfillrate,
                                         sumIntervalURL:brandAdsConstants.appsumintervalfillrate
                                },
                                {
                                        type:"desktop",
                                        format:"percent",
                                        totalURL:brandAdsConstants.desktoptotalfillrate,
                                        intervalURL:brandAdsConstants.desktopintervalfillrate,
                                        sumIntervalURL:brandAdsConstants.desktopsumintervalfillrate
                                }
                            ],
                  requests:[
                                {
                                       type:"app",
                                       format:"number",
                                       totalURL:brandAdsConstants.apptotalrequests,
                                       intervalURL:brandAdsConstants.appintervalrequests,
                                       sumIntervalURL:brandAdsConstants.appsumintervalrequests
                                },
                                {
                                       type:"desktop",
                                       format:"number",
                                       totalURL:brandAdsConstants.desktoptotalrequests,
                                       intervalURL:brandAdsConstants.desktopintervalrequests,
                                       sumIntervalURL:brandAdsConstants.desktopsumintervalrequests
                                }
                          ],

                  sef:[
                        {
                             type:"app",
                             format:"percent",
                             totalURL:brandAdsConstants.apptotalsef,
                             intervalURL:brandAdsConstants.appintervalsef,
                             sumIntervalURL:brandAdsConstants.appsumintervalsef
                        },
                        {
                            type:"desktop",
                            format:"percent",
                            totalURL:brandAdsConstants.desktoptotalsef,
                            intervalURL:brandAdsConstants.desktopintervalsef,
                            sumIntervalURL:brandAdsConstants.desktopsumintervalsef
                        }
                  ],

                  impressions:[
                                     {
                                               type:"app",
                                               format:"number",
                                               totalURL:brandAdsConstants.apptotalimpressions,
                                               intervalURL:brandAdsConstants.appintervalimpressions,
                                               sumIntervalURL:brandAdsConstants.appsumintervalimpressions
                                     },
                                     {
                                              type:"desktop",
                                              format:"number",
                                              totalURL:brandAdsConstants.desktoptotalimpressions,
                                              intervalURL:brandAdsConstants.desktopintervalimpressions,
                                              sumIntervalURL:brandAdsConstants.desktopsumintervalimpressions
                                     }
                              ]
        }
}



function HttpResource($http){

    return {
                getHttp:function(url,starttime,endtime){
                                        return $http({url:url,
                                                     method: "GET",
                                                     params: {starttime:starttime,endtime:endtime}
                                                   });
                                                   }

          };

}



function HighGraphs($http,$q,brandAdsConstants,getNameToUrlMappingDetails){

    Highcharts.setOptions({
      global: {
        useUTC: false
      }
    });

    Highcharts.Chart.prototype.getHttp= function(url,starttime,endtime){
                return $http({url:url,
                             method: "GET",
                             params: {starttime:starttime,endtime:endtime}
                           });

    }
    return Highcharts.Chart;
}












