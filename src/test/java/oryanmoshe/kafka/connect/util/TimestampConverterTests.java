package oryanmoshe.kafka.connect.util;

import org.apache.kafka.connect.data.SchemaBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.debezium.spi.converter.RelationalColumn;
import io.debezium.spi.converter.CustomConverter.Converter;
import io.debezium.spi.converter.CustomConverter.ConverterRegistration;

import java.util.OptionalInt;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimestampConverterTests {
    private class MockRegistration<S> implements ConverterRegistration<S> {
        public Converter _converter;
        public S _schema;

        @Override
        public void register(S fieldSchema, Converter converter) {
            _converter = converter;
            _schema = fieldSchema;
        }
    }

    @ParameterizedTest
    @CsvSource({ "YY-MM-dd, YY-MM-dd", "," + TimestampConverter.DEFAULT_DATE_FORMAT })
    void configureDateTest(final String inputFormat, final String expectedFormat) {
        final TimestampConverter tsConverter = new TimestampConverter();

        Properties props = new Properties();
        if (inputFormat != null)
            props.put("format.date", inputFormat);

        final String beforeConfig = tsConverter.strDateFormat;
        assertEquals(null, beforeConfig, beforeConfig + " before configuration, should equal " + null);
        System.out.println(beforeConfig);

        tsConverter.configure(props);

        final String actualResult = tsConverter.strDateFormat;
        assertEquals(expectedFormat, actualResult,
                actualResult + " after configuration, should equal " + expectedFormat);
        System.out.println(actualResult);
    }

    @ParameterizedTest
    @CsvSource({ "mm:ss.SSS, mm:ss.SSS", "," + TimestampConverter.DEFAULT_TIME_FORMAT })
    void configureTimeTest(final String inputFormat, final String expectedFormat) {
        final TimestampConverter tsConverter = new TimestampConverter();

        Properties props = new Properties();
        if (inputFormat != null)
            props.put("format.time", inputFormat);

        final String beforeConfig = tsConverter.strTimeFormat;
        assertEquals(null, beforeConfig, beforeConfig + " before configuration, should equal " + null);
        System.out.println(beforeConfig);

        tsConverter.configure(props);

        final String actualResult = tsConverter.strTimeFormat;
        assertEquals(expectedFormat, actualResult,
                actualResult + " after configuration, should equal " + expectedFormat);
        System.out.println(actualResult);
    }

    @ParameterizedTest
    @CsvSource({
            "date, YYYY-MM-dd, 18368, 2020-04-16",
            "date,, 18368, 2020-04-16",
            "date,, 2021-11-21Z, 2021-11-21",
            "date, YYYY-MM-dd, 2021-11-21Z, 2021-11-21",
            "time, mm:ss.SSS, 2230, 00:02.230",
            "time,, 2230, 00:00:02.230",
            "datetime, YYYY-MM-dd, 1587042000279, 2020-04-16",
            "datetime,, 1587042000279, 2020-04-16T13:00:00.279Z",
            "timestamp, YYYY-MM-dd, 1587042000279, 2020-04-16",
            "datetime2,, 1587042000279, 2020-04-16T13:00:00.279Z",
            "datetime2, YYYY-MM-dd, 1587042000279, 2020-04-16",
            "timestamp,, 1587042000279, 2020-04-16T13:00:00.279Z",
            "date, YYYY-MM-dd, 2019-04-19, 2019-04-19",
            "datetime,, 2019-04-19 15:13:20.345123, 2019-04-19T15:13:20.345Z",
            "time,, 15:13:20, 15:13:20.000",
            "time,HH:mm:ss, 15:13:20, 15:13:20",
            "timestamp,, 2019-04-19 15:13:20, 2019-04-19T15:13:20.000Z",
            "datetime,, 19-Apr-2019 15:13:20.345123, 2019-04-19T15:13:20.345Z",
            "datetime,, 19/04/2019 15:13:20.345123, 2019-04-19T15:13:20.345Z",
            "datetime,, 2019-4-19 15:13:20.345123, 2019-04-19T15:13:20.345Z",
            "datetime,, 2021-08-17T08:38:41Z, 2021-08-17T08:38:41.000Z",
            "datetime,, 2019-4-19 15:13:20.34512, 2019-04-19T15:13:20.345Z",
            "datetime,, 2021-08-17 09:38:23.3615532, 2021-08-17T09:38:23.361Z",
            "datetime,, 2019-4-19 15:13:00, 2019-04-19T15:13:00.000Z",
            "datetime,, 2019-4-19 15:13, 2019-04-19T15:13:00.000Z",
            "datetime,, 2021-8-16 12:48:00, 2021-08-16T12:48:00.000Z",
            "datetime,YYYY-MM-dd HH:mm:ss, 2021-8-16 12:48, 2021-08-16 12:48:00",
            "datetime,YYYY-MM-dd HH:mm:ss, 2021-8-16T12:48, 2021-08-16 12:48:00",
            "datetime,YYYY-MM-dd HH:mm:ss, 2021-08-16 14:14:10.333, 2021-08-16 14:14:10",
            "datetime,YYYY-MM-dd HH:mm:ss,  2016-10-06T00:51:11, 2016-10-06 00:51:11",
            "datetime,YYYY-MM-dd HH:mm:ss,  2016-10-06T00:51:11.0, 2016-10-06 00:51:11",
            "datetime,YYYY-MM-dd HH:mm:ss,  2016-10-06 00:51:11.0, 2016-10-06 00:51:11",
            "datetime,YYYY-MM-dd HH:mm:ss,  2021-08-16T15:42:58, 2021-08-16 15:42:58",
            "datetime2,, 2019-4-19 15:13:20.345123, 2019-04-19T15:13:20.345Z",
            "datetime,, 2019-4-19 3:1:0.345123, 2019-04-19T03:01:00.345Z",
            "datetime,YYYY-MM-dd,,",
            "timestamp,,,",
            "date,,,"
    })
    void converterTest(final String columnType, final String format, final String input, final String expectedResult) {
        final TimestampConverter tsConverter = new TimestampConverter();

        TestFormat(columnType, format, input, expectedResult, tsConverter);
    }

    // Testing unique (as "," formats)
    @Test
    void uniqueConverterTest() {
        final TimestampConverter tsConverter = new TimestampConverter();

        var columnType = "datetime";
        String format = null;
        var input="2021-08-17 08:50:11,240";
        var expectedResult = "2021-08-17T08:50:11.240Z";

        TestFormat(columnType, format, input, expectedResult, tsConverter);
    }

    private void TestFormat(String columnType, String format, String input, String expectedResult, TimestampConverter tsConverter) {
        Properties props = new Properties();
        if (format != null)
        {
            props.put(
                    String.format("format.%s",
                            columnType.equals("timestamp") || columnType.equals("datetime2") ? "datetime" : columnType),
                    format);
        }

        props.put("debug", "true");
        tsConverter.configure(props);

        RelationalColumn mockColumn = getMockColumn(columnType);
        MockRegistration<SchemaBuilder> mockRegistration = new MockRegistration<SchemaBuilder>();

        tsConverter.converterFor(mockColumn, mockRegistration);

        System.out.println(mockRegistration._schema.name());

        Object actualResult = mockRegistration._converter.convert(input);
        System.out.println(actualResult);
        if (actualResult != null) {
            assertEquals(expectedResult, actualResult,
                    String.format(
                            "columnType: %s, format: %s, input: %s, actualTimeFormat: %s, actualDateFormat: %s, props: %s",
                            columnType, format, input, tsConverter.strTimeFormat, tsConverter.strDateFormat, props));
        }
    }

    RelationalColumn getMockColumn(String type) {
        switch (type) {
            case "date":
                return getDateColumn();
            case "time":
                return getTimeColumn();
            case "datetime":
                return getDateTimeColumn();
            case "datetime2":
                return getDateTime2Column();
            case "timestamp":
                return getTimestampColumn();
            default:
                return null;
        }
    }

    RelationalColumn getDateColumn() {
        return new RelationalColumn() {

            @Override
            public String typeName() {
                return "date";
            }

            @Override
            public String name() {
                return "datecolumn";
            }

            @Override
            public String dataCollection() {
                return null;
            }

            @Override
            public String typeExpression() {
                return null;
            }

            @Override
            public OptionalInt scale() {
                return null;
            }

            @Override
            public int nativeType() {
                return 0;
            }

            @Override
            public OptionalInt length() {
                return null;
            }

            @Override
            public int jdbcType() {
                return 0;
            }

            @Override
            public boolean isOptional() {
                return false;
            }

            @Override
            public boolean hasDefaultValue() {
                return false;
            }

            @Override
            public Object defaultValue() {
                return null;
            }
        };
    }

    RelationalColumn getTimeColumn() {
        return new RelationalColumn() {

            @Override
            public String typeName() {
                return "time";
            }

            @Override
            public String name() {
                return "timecolumn";
            }

            @Override
            public String dataCollection() {
                return null;
            }

            @Override
            public String typeExpression() {
                return null;
            }

            @Override
            public OptionalInt scale() {
                return null;
            }

            @Override
            public int nativeType() {
                return 0;
            }

            @Override
            public OptionalInt length() {
                return null;
            }

            @Override
            public int jdbcType() {
                return 0;
            }

            @Override
            public boolean isOptional() {
                return false;
            }

            @Override
            public boolean hasDefaultValue() {
                return false;
            }

            @Override
            public Object defaultValue() {
                return null;
            }
        };
    }

    RelationalColumn getDateTimeColumn() {
        return new RelationalColumn() {

            @Override
            public String typeName() {
                return "datetime";
            }

            @Override
            public String name() {
                return "datetimecolumn";
            }

            @Override
            public String dataCollection() {
                return null;
            }

            @Override
            public String typeExpression() {
                return null;
            }

            @Override
            public OptionalInt scale() {
                return null;
            }

            @Override
            public int nativeType() {
                return 0;
            }

            @Override
            public OptionalInt length() {
                return null;
            }

            @Override
            public int jdbcType() {
                return 0;
            }

            @Override
            public boolean isOptional() {
                return false;
            }

            @Override
            public boolean hasDefaultValue() {
                return true;
            }

            @Override
            public Object defaultValue() {
                return null;
            }
        };
    }

    RelationalColumn getDateTime2Column() {
        return new RelationalColumn() {

            @Override
            public String typeName() {
                return "datetime2";
            }

            @Override
            public String name() {
                return "datetime2column";
            }

            @Override
            public String dataCollection() {
                return null;
            }

            @Override
            public String typeExpression() {
                return null;
            }

            @Override
            public OptionalInt scale() {
                return null;
            }

            @Override
            public int nativeType() {
                return 0;
            }

            @Override
            public OptionalInt length() {
                return null;
            }

            @Override
            public int jdbcType() {
                return 0;
            }

            @Override
            public boolean isOptional() {
                return false;
            }

            @Override
            public boolean hasDefaultValue() {
                return false;
            }

            @Override
            public Object defaultValue() {
                return null;
            }
        };
    }

    RelationalColumn getTimestampColumn() {
        return new RelationalColumn() {

            @Override
            public String typeName() {
                return "timestamp";
            }

            @Override
            public String name() {
                return "timestampcolumn";
            }

            @Override
            public String dataCollection() {
                return null;
            }

            @Override
            public String typeExpression() {
                return null;
            }

            @Override
            public OptionalInt scale() {
                return null;
            }

            @Override
            public int nativeType() {
                return 0;
            }

            @Override
            public OptionalInt length() {
                return null;
            }

            @Override
            public int jdbcType() {
                return 0;
            }

            @Override
            public boolean isOptional() {
                return false;
            }

            @Override
            public boolean hasDefaultValue() {
                return false;
            }

            @Override
            public Object defaultValue() {
                return null;
            }
        };
    }
}