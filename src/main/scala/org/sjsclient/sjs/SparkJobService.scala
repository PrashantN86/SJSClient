package org.sjsclient.sjs

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by prashant on 17/4/17.
  */

trait SparkJobService {
  
    def syncSubmit[T](jobClassPath : String,
                      jobArgs : Map[String,Any],
                      responseHandler : JobResponse => T,
                      timeOut: Duration) : Try[T]

    def asyncSubmit(jobClassPath : String,
                  jobArgs : Map[String,Any],
                  pollInterval : Duration = 5 seconds) : Try[JobId]


    def pollJobStatus[T](jobId: JobId,
                       responseHandler: JobResponse => T,
                       pollInterval: Duration): Future[T]

    def getJobStatus(jobId : JobId) : Try[JobResponse]

    def killJob(jobId : JobId) : Unit

}
