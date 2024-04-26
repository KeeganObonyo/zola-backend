package reviews.zola.core.http

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, StatusCode }
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer

import reviews.zola._

import core.util.ZolaCCPrinter

case class ZolaHttpClientResponse(
  status: StatusCode,
  data: String
) extends ZolaCCPrinter

trait ZolaHttpClientT {

  implicit val system: ActorSystem
  final implicit lazy val materializer = Materializer(system)
  private lazy val http                = Http(system)

  def sendHttpRequest(req: HttpRequest): Future[ZolaHttpClientResponse] = for {
    response <- http.singleRequest(req)
    data     <- Unmarshal(response.entity).to[String]
  } yield ZolaHttpClientResponse(
    status = response.status,
    data   = data
  )
}
