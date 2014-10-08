/*
Copyright 2011 Edward Capriolo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.hive_unit;

import com.hive_unit.common.PropertyNames;
import com.hive_unit.common.Response;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.processors.CommandProcessor;
import org.apache.hadoop.hive.ql.processors.CommandProcessorFactory;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hadoop.hive.ql.session.SessionState;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmbeddedHive {
    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    private static Logger logger = Logger.getLogger(EmbeddedHive.class.getName());

    private SessionState ss;
    private HiveConf c;
    private File warehouseDir;

    /**
     * Create embedded Hive
     *
     * @param properties - Properties
     */
    public EmbeddedHive(Properties properties) {
        HiveConf conf = new HiveConf();
        if (properties.get(PropertyNames.HIVE_JAR.toString()) != null) {
            //this line may be required so that the embedded derby works well
            //refers to dependencies containing ExecDriver class
            conf.setVar(HiveConf.ConfVars.HIVEJAR, properties.get(PropertyNames.HIVE_JAR.toString()).toString());
        }
        //this property is required so that every test runs on a different warehouse location.
        // This way we avoid conflicting scripts or dirty reexecutions
        File tmpDir = new File(System.getProperty(JAVA_IO_TMPDIR));
        warehouseDir = new File(tmpDir, UUID.randomUUID().toString());
        warehouseDir.mkdir();
        conf.setVar(HiveConf.ConfVars.METASTOREWAREHOUSE, warehouseDir.getAbsolutePath());

        ss = new SessionState(new HiveConf(conf, EmbeddedHive.class));
        SessionState.start(ss);
        c = ss.getConf();
    }

    /**
     * Execute Hive command
     *
     * @param cmd - hive command
     * @return Response
     */
    public Response doHiveCommand(String cmd) {
        ArrayList<String> results = new ArrayList<String>();
        CommandProcessorResponse processorResponse = null;
        String cmd_trimmed = cmd.trim();
        String[] tokens = cmd_trimmed.split("\\s+");
        String cmd_1 = cmd_trimmed.substring(tokens[0].length()).trim();
        CommandProcessor proc = null;
        try {
            proc = CommandProcessorFactory.get(tokens, c);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (proc instanceof Driver) {
            try {
                processorResponse = proc.run(cmd);
            } catch (CommandNeedRetryException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                processorResponse = proc.run(cmd_1);
            } catch (CommandNeedRetryException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        try {
            if (proc instanceof org.apache.hadoop.hive.ql.Driver) {
                ((Driver) proc).getResults(results);
            } else {
                logger.info(String.format(
                        "Processor of class %s is currently not supported for retrieving results", proc.getClass()
                ));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
        } catch (CommandNeedRetryException e) {
            logger.log(Level.SEVERE, null, e);
        }
        return new Response((processorResponse != null) ? processorResponse.getResponseCode() : -40, results);
    }

    /**
     * Close connection and cleanup directory used for warehousing
     */
    public void close() {
        try {
            if (ss != null) {
                ss.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (warehouseDir != null) {
            warehouseDir.delete();
        }
    }
}
