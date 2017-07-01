package test

import com.typesafe.scalalogging.LazyLogging
import org.sjsclient.httpClient.OkHttpClient
import org.sjsclient.sjs.SJSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


/**
  * Created by prashant on 18/4/17.
  */
object test extends App  with LazyLogging{
  val client = new SJSClient("http://192.168.0.88:8090","etherjobs_prashant","ctx")

  //testSync(client)

  //testGet(client)

  testASync(client)


  def testSync(client: SJSClient) = {
    val jobArgs = Map("expression" -> """{"op":"leafGroup","id":"#14:773","name":"PROBLEMS","table":"PROBLEMS","jdbcDetails":{"url":"jdbc:mysql://192.168.0.17:3306/HACKER_EARTH?zeroDateTimeBehavior=convertToNull","user":"root","pass":"root","dbName":"HACKER_EARTH"},"columns":[{"op":"concept","name":"PROBLEM_ID_1","conceptId":"#14:776"},{"op":"concept","name":"PROBLEM_ID","conceptId":"#14:777"},{"op":"concept","name":"LEVEL","conceptId":"#14:778"},{"op":"concept","name":"ACCURACY","conceptId":"#14:779"},{"op":"concept","name":"SOLVED_COUNT","conceptId":"#14:780"},{"op":"concept","name":"ERROR_COUNT","conceptId":"#14:781"},{"op":"concept","name":"RATING","conceptId":"#14:782"},{"op":"concept","name":"TAG1","conceptId":"#14:783"},{"op":"concept","name":"TAG2","conceptId":"#14:784"},{"op":"concept","name":"TAG3","conceptId":"#14:785"},{"op":"concept","name":"TAG4","conceptId":"#14:786"},{"op":"concept","name":"TAG5","conceptId":"#14:787"},{"op":"scalarInt","name":"scalar_new","conceptId":"#14:816","value":12},{"op":"scalarInt","name":"scalar_test","conceptId":"#14:817","value":34},{"op":"scalarvarint","name":"scalr_test_variable","conceptId":"#14:818","value":21,"commDiff":2}]}""",
      "groupConceptId" -> "#14:773","etherServer" -> "http://192.168.0.14:9001")

    val responseHandler : String => String = {resp => resp}
    val response = client.syncSubmit[String]("com.sjsclient.jobs.Statistics.CorrelationMatrixJob",jobArgs,responseHandler)
    response match {
      case Success(resp) => print(resp)
      case Failure(e) => print(e.getMessage)
    }
  }


  def testGet(client: SJSClient) = {
    val jobId = "8ad0f5cb-96d2-45f7-a519-4cb768e64d50"
    val url = s"${client.jobServerUrl}/jobs/$jobId"
    val response = OkHttpClient.get(url)
    response match {
      case Success(resp) => print(resp)
      case Failure(e) => print(e.getMessage)
    }
  }

  def testASync(client: SJSClient) = {
    val jobArgs = Map("expression" -> """{"op":"leafGroup","id":"#14:773","name":"PROBLEMS","table":"PROBLEMS","jdbcDetails":{"url":"jdbc:mysql://192.168.0.17:3306/HACKER_EARTH?zeroDateTimeBehavior=convertToNull","user":"root","pass":"root","dbName":"HACKER_EARTH"},"columns":[{"op":"concept","name":"PROBLEM_ID_1","conceptId":"#14:776"},{"op":"concept","name":"PROBLEM_ID","conceptId":"#14:777"},{"op":"concept","name":"LEVEL","conceptId":"#14:778"},{"op":"concept","name":"ACCURACY","conceptId":"#14:779"},{"op":"concept","name":"SOLVED_COUNT","conceptId":"#14:780"},{"op":"concept","name":"ERROR_COUNT","conceptId":"#14:781"},{"op":"concept","name":"RATING","conceptId":"#14:782"},{"op":"concept","name":"TAG1","conceptId":"#14:783"},{"op":"concept","name":"TAG2","conceptId":"#14:784"},{"op":"concept","name":"TAG3","conceptId":"#14:785"},{"op":"concept","name":"TAG4","conceptId":"#14:786"},{"op":"concept","name":"TAG5","conceptId":"#14:787"},{"op":"scalarInt","name":"scalar_new","conceptId":"#14:816","value":12},{"op":"scalarInt","name":"scalar_test","conceptId":"#14:817","value":34},{"op":"scalarvarint","name":"scalr_test_variable","conceptId":"#14:818","value":21,"commDiff":2}]}""",
      "groupConceptId" -> "#14:773","etherServer" -> "http://192.168.0.14:9001")

    val response = client.asyncSubmit("com.sjsclient.jobs.Statistics.CorrelationMatrixJob",jobArgs)

    val responseHandler : String => String = { res => res}

    response match {
      case Success(jobId) => {
        logger.info(s"jobId :: $jobId")
        client.pollJobStatus[String](jobId,responseHandler) onComplete {
          case Success(resp) => logger.info(resp)
          case Failure(e) => logger.error(e.getMessage)
        }
      }
      case Failure(e) => logger.error(e.getMessage)
    }
  }
}
