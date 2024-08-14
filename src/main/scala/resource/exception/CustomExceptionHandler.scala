package resource.exception

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.ExceptionHandler
import spray.json.DefaultJsonProtocol.*

import java.sql.SQLException
import java.time.Instant

final case class ResourceNotFound(private val message: String = "",
                                  private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

final case class StandardResponse(message: String, result: String, occurency: String)

object CustomExceptionHandler {

  implicit val resultMarshaller: spray.json.RootJsonFormat[StandardResponse] = jsonFormat3(StandardResponse.apply)

  val customExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: ResourceNotFound =>
      extractUri {
        uri =>
          println("Recurso não encontrado")
          complete(StatusCodes.NotFound -> StandardResponse(e.getMessage, "Recurso não encontrado", Instant.now().toString))
      }
    case e: SQLException =>
      extractUri {
        uri =>
          println("Falha no banco de dados")
          complete(StatusCodes.BadRequest -> StandardResponse(e.getMessage, "Falha de banco de daods", Instant.now().toString))
      }
    case _: ArithmeticException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(InternalServerError, entity = "Bad numbers, bad result!!!"))
      }
  }

}
