package com.hive_unit.client;

import com.hive_unit.EmbeddedHive;
import com.hive_unit.common.Response;
import org.apache.log4j.Logger;

/**
 * HiveClient implementation that allows to execute statements against an embedded Hive
 */
public class EmbeddedHiveClient implements HiveClient {
    private static final Logger log = Logger.getLogger(EmbeddedHiveClient.class);
    private EmbeddedHive hive;

    public EmbeddedHiveClient(EmbeddedHive hive) {
        this.hive = hive;
    }

    @Override
    public Response execute(String command) {
        log.info("Will execute command " + command);
        return hive.doHiveCommand(command);
    }

    @Override
    public void close() {
        hive.close();
    }
}
