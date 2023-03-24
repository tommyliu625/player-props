// package com.player.props.util;

// import org.apache.spark.sql.Dataset;
// import org.apache.spark.sql.Row;
// import org.apache.spark.sql.SaveMode;
// import org.apache.spark.sql.SparkSession;

// import lombok.Data;

// public class DatabaseUtils {

//   @Data
//   public static class DatabaseConnectionOptions {
//     private String url;
//     private String driver;
//     private String dbtable;
//     private String user;
//     private String password;

//     public DatabaseConnectionOptions(String url, String driver, String dbtable, String user, String password) {
//       this.url = url;
//       this.driver = driver;
//       this.dbtable = dbtable;
//       this.user = user;
//       this.password = password;
//     }
//   }

//   public static Dataset<Row> readTable(SparkSession spark, DatabaseConnectionOptions options) {
//     Dataset<Row> df = spark.read()
//         .format("jdbc")
//         .option("url", options.getUrl())
//         .option("driver", options.getDriver())
//         .option("dbtable", options.getDbtable())
//         .option("user", options.getUser())
//         .option("password", options.getPassword())
//         .load();
//     return df;
//   }

//   public static void writeTable(Dataset<Row> df, DatabaseConnectionOptions options) {
//     df.write()
//         .format("jdbc")
//         .option("url", options.getUrl())
//         .option("driver", options.getDriver())
//         .option("dbtable", options.getDbtable())
//         .option("user", options.getUser())
//         .option("password", options.getPassword())
//         .mode(SaveMode.Overwrite)
//         .save();
//   }
// }
