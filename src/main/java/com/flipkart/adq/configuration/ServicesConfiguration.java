package com.flipkart.adq.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by jagmeet.singh on 02/01/15.
 */
@Getter
public class ServicesConfiguration extends Configuration {

    @NotEmpty
    private String healthCheckProperty;

    @NotEmpty
    private String redisHost;

    @NotEmpty
    private String redisPort;

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    private DataSourceFactory database2 = new DataSourceFactory();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database2")
    public DataSourceFactory getDataSourceFactory2() {
        return database2;
    }



}
