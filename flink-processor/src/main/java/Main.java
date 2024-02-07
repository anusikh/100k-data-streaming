import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class Main {

    static final String BROKERS = "kafka:9092";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<Order> source = KafkaSource.<Order>builder()
                .setBootstrapServers(BROKERS)
                .setProperty("partition.discovery.interval.ms", "1000")
                .setTopics("datastream")
                .setGroupId("groupId-919292")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new OrderDeserializationSchema())
                .build();

        DataStreamSource<Order> kafka = env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka");

        DataStream<Tuple2<String, Double>> sumCostAggregatorStream = kafka.keyBy(myEvent -> myEvent.customer_name)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(300))) // 300 is 5 minutes, so the window size is 5 mins
                .aggregate(new CostAggregator());

        sumCostAggregatorStream.print();

        env.execute("Kafka-flink-postgres");
    }

    public static class CostAggregator implements AggregateFunction<Order, Tuple2<String, Double>, Tuple2<String, Double>> {

        @Override
        public Tuple2<String, Double> createAccumulator() {
            return new Tuple2<>("", 0.0);
        }

        @Override
        public Tuple2<String, Double> add(Order event, Tuple2<String, Double> accumulator) {
            return new Tuple2<>(event.customer_name, accumulator.f1 + event.cost);
        }

        @Override
        public Tuple2<String, Double> getResult(Tuple2<String, Double> accumulator) {
            return accumulator;
        }

        @Override
        public Tuple2<String, Double> merge(Tuple2<String, Double> a, Tuple2<String, Double> b) {
            return new Tuple2<>(a.f0, a.f1 + b.f1);
        }
    }
}
