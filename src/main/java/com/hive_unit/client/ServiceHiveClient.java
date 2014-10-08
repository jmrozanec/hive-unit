package com.hive_unit.client;

import com.hive_unit.ServiceHive;
import com.hive_unit.common.Response;
import org.apache.thrift.TException;

/**
 * HiveClient implementation for connecting to some running Hive.
 */
public class ServiceHiveClient implements HiveClient {
    private ServiceHive hive;

    public ServiceHiveClient(ServiceHive hive) {
        this.hive = hive;
    }

    @Override
    public Response execute(String command) {
        try {
            hive.client.execute(command);
            return new Response(hive.client.getStatus().getValue(), hive.client.fetchAll());
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        hive.transport.close();
    }
}
