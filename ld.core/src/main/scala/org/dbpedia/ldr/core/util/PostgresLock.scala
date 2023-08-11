//package org.dbpedia.ldr.core
//
//import java.sql.{Connection, DriverManager, SQLException};
//
//class PostgresLock {
//  val DB_URL = "jdbc:postgresql://localhost/test";
//  val DB_USER = "postgres";
//  val DB_PASSWORD = "password";
//
//  val LOCK_TABLE = "lock_table";
//  val LOCK_NAME = "my_lock";
//
//  val ACQUIRE_LOCK_QUERY = "INSERT INTO " + LOCK_TABLE + " (lock_name) VALUES (?) ON CONFLICT DO NOTHING";
//  val RELEASE_LOCK_QUERY = "DELETE FROM " + LOCK_TABLE + " WHERE lock_name = ?";
//
//  val connection: Connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//
////  public PostgresLock() {
////    try {
////
////    } catch (SQLException e) {
////      // Handle exception
////      e.printStackTrace();
////    }
////  }
//
//  def acquireLock(): Unit = {
//    try {
//      val stmt = connection.prepareStatement(ACQUIRE_LOCK_QUERY);
//      stmt.setString(1, LOCK_NAME);
//      stmt.executeUpdate();
//    } catch  {
//      case e : SQLException =>
//        // Handle exception
//        e.printStackTrace();
//    }
//  }
//
//  def releaseLock(): Unit = {
//    try {
//      val stmt = connection.prepareStatement(RELEASE_LOCK_QUERY);
//      stmt.setString(1, LOCK_NAME);
//      stmt.executeUpdate();
//    } catch  {
//      case e : SQLException =>
//      // Handle exception
//      e.printStackTrace();
//    }
//  }
//
//  def isLockAcquired(): Boolean = {
//    try {
//      val stmt = connection.prepareStatement("SELECT COUNT(*) FROM " + LOCK_TABLE + " WHERE lock_name = ?");
//      stmt.setString(1, LOCK_NAME);
//      val rs = stmt.executeQuery();
//      rs.next();
//      val count = rs.getInt(1);
//      return count > 0;
//    } catch  {
//      case e : SQLException =>
//        // Handle exception
//        e.printStackTrace();
//    }
//    false;
//  }
//
//  def close(): Unit = {
//    try {
//      connection.close();
//    } catch  {
//      case e : SQLException =>
//        // Handle exception
//        e.printStackTrace();
//    }
//  }
//}