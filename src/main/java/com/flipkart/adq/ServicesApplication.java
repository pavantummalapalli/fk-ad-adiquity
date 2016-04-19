package com.flipkart.adq;

import com.flipkart.adq.configuration.ServicesConfiguration;
import com.flipkart.adq.dao.Cmp_Exc_DateDAO;
import com.flipkart.adq.dao.CommonDAO;
import com.flipkart.adq.dao.DspCmp_Exc_DateDAO;
import com.flipkart.adq.resources.BrandAdsFunnelResource;
import com.flipkart.adq.resources.BrandAdsResource;
import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by pavan.t on 12/01/16.
 */
public class ServicesApplication extends Application<ServicesConfiguration> {

    public static void main(String[] args) throws Exception {

        new ServicesApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<ServicesConfiguration> bootstrap) {

    }

    @Override
    public void run(ServicesConfiguration configuration, Environment environment) throws Exception {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
        final DBI jdbi2 = factory.build(environment, configuration.getDataSourceFactory2(), "mysql2");
        final Cmp_Exc_DateDAO dao = jdbi.onDemand(Cmp_Exc_DateDAO.class);
        final DspCmp_Exc_DateDAO dspDao = jdbi.onDemand(DspCmp_Exc_DateDAO.class);
        final CommonDAO commonDAO = jdbi.onDemand(CommonDAO.class);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        final JedisPool pool = new JedisPool(poolConfig,configuration.getRedisHost(), Integer.parseInt(configuration.getRedisPort()));
        BrandAdsResource brandAdsResource = new BrandAdsResource(jdbi.open(),jdbi2.open(),pool.getResource());

        environment.jersey().register(brandAdsResource);
        BrandAdsFunnelResource brandAdsFunnelResource = new BrandAdsFunnelResource(jdbi.open(), dao, dspDao,commonDAO);
        environment.jersey().register(brandAdsFunnelResource);

        final ServicesHealthCheck servicesHealthCheck = new ServicesHealthCheck(configuration.getHealthCheckProperty());
        environment.healthChecks().register("configurationCheck", servicesHealthCheck);

    }
}
