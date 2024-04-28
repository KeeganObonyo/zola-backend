package reviews.zola.blogic.test

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer

import reviews.zola._

import core.http.ZolaHttpClientResponse

trait TestHttpStringEndpointT extends TestServiceT {

    final implicit val Mat: Materializer = Materializer(system)

  def getStringHttpResponse(req: HttpRequest) = {
    getStringHttpResponseImpl(
      uri  = req.uri,
      data = Await.result(
        Unmarshal(req.entity).to[String],
        1.second
      ))
  }

  def getStringHttpResponseImpl(
    data: String,
    uri: Uri
  ): ZolaHttpClientResponse 
}
