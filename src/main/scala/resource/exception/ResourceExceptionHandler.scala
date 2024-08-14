package resource.exception

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.*
import akka.http.scaladsl.server.*
import akka.http.scaladsl.server.Directives.*
import spray.json.DefaultJsonProtocol.*
import spray.json.SerializationException

import java.sql.SQLException
import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId}

final case class ResourceNotFound(private val message: String = "",
                                  private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

final case class StandardResponse(message: String, result: String, moment: String)

object ResourceExceptionHandler {

  implicit val resultMarshaller: spray.json.RootJsonFormat[StandardResponse] = jsonFormat3(StandardResponse.apply)

  private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"))

  // https://doc.akka.io/docs/akka-http/current/routing-dsl/exception-handling.html
  val customExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: ResourceNotFound =>
      extractUri {
        uri =>
          println("Recurso não encontrado")
          complete(StatusCodes.NotFound -> StandardResponse(e.getMessage, "Recurso não encontrado", formatter.format(Instant.now())))
      }
    case e: SQLException =>
      extractUri {
        uri =>
          println("Falha no banco de dados")
          complete(StatusCodes.BadRequest -> StandardResponse(e.getMessage, "Falha de banco de dados", formatter.format(Instant.now())))
      }
    case e: SerializationException =>
      extractUri {
        uri =>
          println("Falha no banco de dados")
          complete(StatusCodes.BadRequest -> StandardResponse(e.getMessage, "Falha de banco de dados", formatter.format(Instant.now())))
      }
  }

  // https://doc.akka.io/docs/akka-http/current/routing-dsl/rejections.html?language=scala#customizing-rejection-handling
  val customRejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
        case MalformedRequestContentRejection(message, cause) =>
          complete(BadRequest -> StandardResponse(message, "Rejeição da API", formatter.format(Instant.now())))
      }
      .result()

}
