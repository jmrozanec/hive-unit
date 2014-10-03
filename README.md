## hive-unit: A simple way to test Hive scripts.

[![Build Status](https://travis-ci.org/jmrozanec/hive-unit.svg)](https://travis-ci.org/jmrozanec/hive-unit)

### Usage

hive-unit provides means to test Hive scripts. This can be done against an Hive instance 
including an embedded Derby database, and a local HiveThriftService; or against an existing Hive cluster. 
This allows us to create unit and integration tests very easily.

Hive scripts testing can be done in a similar way to [PigUnit](http://pig.apache.org/docs/r0.8.1/pigunit.html).
For more examples check [this class](https://github.com/jmrozanec/hive_test/blob/master/src/test/java/com/jointhegrid/hive_test/HiveTestTest.java)

        @Test
            public void testScriptListInput() {
                Map<String, List<String>> input = Maps.<String, List<String>>newHashMap();
                List<String> lines = Lists.newArrayList();
                lines.add("msmith,10");
                lines.add("mjohnson,2");
                lines.add("mwilliamson,7");
                lines.add("mjones,4");
                lines.add("mdavies,5");

                input.put("$INPUT1", lines);

                Response output =
                        HiveBuilder.create()
                                .hiveTestWithEmbeddedHive("src/test/resources/scripts/passing-scores.hql")
                                .outputForInput(input);

                List<String> expected = Lists.newArrayList();
                expected.add("msmith,10");
                expected.add("mwilliamson,7");

                assertEquals(ResponseStatus.SUCCESS, output.getResponseStatus());
                assertEquals(expected, output.getResult());
            }


### Soon will be available at Maven central!

### Contribute!

Contributions are welcome! You can contribute by
 * starring this repo!
 * adding new features
 * enhancing existing code
 * testing
 * enhancing documentation
 * bringing suggestions and reporting bugs
 * spreading the word / telling us how you use it!
