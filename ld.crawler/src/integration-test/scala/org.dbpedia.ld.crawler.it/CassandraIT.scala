//package org.dbpedia.ld.crawler.it
//
////import org.junit.jupiter.api.Test
//import org.scalatest.funsuite.AnyFunSuite
//import org.testcontainers.containers.GenericContainer
//import org.testcontainers.utility.DockerImageName
//
//import java.net.http.{HttpClient, HttpRequest}
//
//class CassandraIT {
//
//  @Test
//  def test(): Unit = {
//    println("### INTEGRATION TEST ###")
//
//    // Define the Docker image for Nginx
//    val nginxImage: DockerImageName = DockerImageName.parse("nginx:latest")
//
//    // Create the container
//    val nginxContainer = new GenericContainer(nginxImage)
//    nginxContainer.addExposedPort(80)
//
//    // Start the container
//    nginxContainer.start()
//
//    // Fetch the mapped port for access from the host machine
//    val mappedPort: Integer = nginxContainer.getMappedPort(80)
//
//    println(s"Nginx server started and accessible via http://localhost:$mappedPort/")
//
//    // The container can be stopped using `nginxContainer.stop()`
//    // For this example, we'll keep it running indefinitely
//    Thread.sleep(Long.MaxValue)
//  }
//}
