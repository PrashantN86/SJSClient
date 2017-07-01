package org.sjsclient.sjs

import com.typesafe.scalalogging.LazyLogging
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._
import org.sjsclient.httpClient.{HttpClient, OkHttpClient}

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

/**
  * Created by prashant on 17/4/17.
  */
class SJSClient(val jobServerUrl:String,
             val appName : String,
             val context : String) extends SparkJobService with LazyLogging{


  implicit val formats = DefaultFormats
  private val httpClient : HttpClient = OkHttpClient

  override def syncSubmit[T](jobClassPath: String,
                             jobArgs: Map[String, Any],
                             responseHandler: JobResponse => T,
                             timeOut: Duration = 30 seconds): Try[T] = {

    val jobUrl = getJobUrl(Sync(timeOut),jobClassPath)
    val response = httpClient.postJson(jobUrl,write(jobArgs))
     response match {
      case Success(resp) => Success(responseHandler(resp))
      case Failure(e)  => Failure(e)
    }
  }

  override def asyncSubmit(jobClassPath : String,
                           jobArgs : Map[String,Any],
                           pollInterval : Duration = 5 seconds) : Try[JobId] = {

    val jobUrl = getJobUrl(Async,jobClassPath)
    val response: Try[String] = httpClient.postJson(jobUrl,write(jobArgs))

    response match {
      case Success(resp) => {
        val json = parse(resp)
        val jobId = json \\ "jobId"
        Success(jobId.extract[String])
      }
      case Failure(e) => Failure(e)
    }

  }

  private def getJobUrl(jobType: JobType, jobClassPath : String) : String = {
    val baseUrl = s"$jobServerUrl/jobs?appName=$appName&classPath=$jobClassPath&context=$context"
    jobType match {
      case Sync(timeout)  => baseUrl + s"&sync=true&timeout=${timeout.toSeconds}"
      case Async => baseUrl
    }
  }

  override def getJobStatus(jobId: JobId): Try[JobResponse] = {
    val url = s"$jobServerUrl/jobs/$jobId"
    httpClient.get(url)
  }

  override def killJob(jobId: JobId): Unit = ???


  override def pollJobStatus[T](jobId: JobId,
                                responseHandler: JobResponse => T,
                                pollInterval: Duration = 5 second): Future[T] = {


    //Can be taken as a input in form of higher order function
    def stopCriteria(jobResponse : JobResponse) : Boolean ={
      val json = parse(jobResponse)
      val status: String = (json \ "status").extract[String]
      JobStatus.withName(status) match {
        case JobStatus.RUNNING => false
        case _ => true
      }
    }

    @scala.annotation.tailrec
    def poll(condition : Boolean, jobResponse: JobResponse) : Try[JobResponse] = {
      if(condition) Success(jobResponse)
      else {
        Thread.sleep(pollInterval.toMillis)
        getJobStatus(jobId) match {
          case Success(res) => poll(stopCriteria(res),res)
          case Failure(e) => Failure(e)
        }
      }
    }

    val jobResponsePromise = Promise[T]
    getJobStatus(jobId) match {
      case Success(jobResponse) => poll(stopCriteria(jobResponse),jobResponse) match {
        case Success(resp) => jobResponsePromise.success(responseHandler(resp))
        case Failure(e) =>  jobResponsePromise.failure(e)
      }
      case Failure(e) => jobResponsePromise.failure(e)
    }
    jobResponsePromise.future
  }
}
