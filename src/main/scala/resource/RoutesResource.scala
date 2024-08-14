package resource

import akka.http.scaladsl.server.*
import akka.http.scaladsl.server.Directives.*
import resource.exception.ResourceExceptionHandler.{customExceptionHandler, customRejectionHandler}

object RoutesResource {

  import ProductResource.allRoutesProduct

  def allRoutesUnified(innerRoute: Route = allRoutesProduct): Route = {
    handleRejections(customRejectionHandler) {
      handleExceptions(customExceptionHandler) {
        innerRoute
      }
    }

  }
}
