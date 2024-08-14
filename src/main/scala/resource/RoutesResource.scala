package resource

import akka.http.scaladsl.server.*
import akka.http.scaladsl.server.Directives.*
import resource.exception.CustomExceptionHandler.customExceptionHandler

object RoutesResource {

  import ProductResource.allRoutesProduct

  def allRoutesUnified(innerRoute: Route = allRoutesProduct): Route = {
    handleExceptions(customExceptionHandler) {
      innerRoute
    }
  }
}
