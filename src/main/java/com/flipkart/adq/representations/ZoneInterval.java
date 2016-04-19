package com.flipkart.adq.representations;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by pavan.t on 14/03/16.
 */


@Data
@AllArgsConstructor
public class ZoneInterval {
    String datetime;
    int zoneId;
}
