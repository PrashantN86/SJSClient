package org.sjsclient.sjs

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._

/**
  * Created by prashant on 17/4/17.
  */

case class SJSResponse(duration : String,
                       classPath : String,
                       startTime : String,
                       context: String,
                       result : String,
                       status : String,
                       jobId : String) {
}

object SJSResponse {

  def apply(jsonString : String) : SJSResponse = {
    implicit val formats = DefaultFormats
    val json = parse(jsonString)
    json.extract[SJSResponse]
  }

}


