package com.flipkart.adq.representations;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by pavan.t on 07/03/16.
 */

@Data
@AllArgsConstructor
public class RequestsZoneIdInterval {
    String datetime;
    int zoneId;
    int requests;
}
