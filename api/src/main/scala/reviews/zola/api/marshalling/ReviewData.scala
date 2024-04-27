package reviews.zola.api
package marshalling

import reviews.zola._

import core.util.{ ZolaCCPrinter, ZolaUtil, ZolaEnum }
import ZolaEnum._

import blogic.UserReviewService._

private[api] case class AddReview(
    username: String,
    rating: Int,
    businessName: String,
    callback: Option[String],
    textInfo: Option[String],
    authorName: String
) extends ZolaCCPrinter {
  require(rating > 0, "Rating must be greater than zero")
  def getServiceRequest(userId: Int) = AddReviewRequest(
    userId       = userId,
    rating       = rating,
    businessName = businessName,
    callback     = callback,
    textInfo     = textInfo,
    authorName   = authorName
  )
}

private[api] case class AddResponse(
    description: String,
    status: ServiceStatus.Value
) extends ZolaCCPrinter

object AddResponse {
  def fromServiceResponse(response: AddReviewResponse) = AddResponse(
      description = response.description,
      status      = response.status
  )
}

private[api] case class ListReview(
  username: String,
  businessName: String
) extends ZolaCCPrinter {
  def getServiceRequest(userId: Int) = ListReviewRequest(
    userId       = userId,
    businessName = businessName
  )
}