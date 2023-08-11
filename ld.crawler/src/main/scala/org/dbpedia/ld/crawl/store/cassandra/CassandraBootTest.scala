
// import com.datastax.oss.driver.api.core.uuid.Uuids
// import org.apache.commons.lang3.time.StopWatch
// import org.springframework.boot.autoconfigure.SpringBootApplication
// import org.springframework.boot.{CommandLineRunner, SpringApplication}
// import org.springframework.stereotype.Component

// import java.util.UUID

// @SpringBootApplication
// class CassandraBootTest {

//   private val bytea = Array.fill[Byte](520000)(0)
//   private val byteaj = bytea

//   @Component
//   class clr(testRepository: TestRepository) extends CommandLineRunner {

//     override def run(args: String*): Unit = {

// //      val es = generateEntities()

//       println("start insert")
//       val sw = new StopWatch()
//       sw.start()
// //      es.foreach({
// //        e =>
// //          testRepository.save(e)
// //      })
//       (0 to 100000).foreach({
//         idx =>
//           val te = new TestEntity()
//           te.id = idx.toString
//           te.bytea = bytea
//           testRepository.save(te)
//           if((idx % 1000) == 0)
//             sw.split()
//             println(idx+ " "+ sw.formatSplitTime())
//       })

//       sw.stop()
//       println(sw.formatTime())
// //      testRepository.save(te)
// //      println("saved entity")
//     }

// //    def generateEntities(): List[TestEntity] = {
// //
// //    }
//   }
// }

// object CassandraBootTest {

//   def main(args: Array[String]): Unit = {
//     SpringApplication.run(classOf[CassandraBootTest], args: _*)
//   }
// }
