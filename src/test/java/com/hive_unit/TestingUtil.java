package com.hive_unit;

import com.hive_unit.common.PropertyNames;

import java.util.Properties;

public class TestingUtil {

  public static Properties getDefaultProperties() {
    Properties properties = new Properties();
    properties.put(PropertyNames.HIVE_JAR.toString(), "hive-exec-0.11.0.jar");
    return properties;
  }
}
