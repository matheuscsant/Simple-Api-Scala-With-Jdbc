package handler

import akka.http.scaladsl.server
import dao.{ProductDao, ProductsList, Product}
// akka http
import akka.http.scaladsl.server.Directives.*
// spray
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import spray.json.DefaultJsonProtocol.*


case class ProductHandler(dao: ProductDao) {

  implicit val productMarshaller: spray.json.RootJsonFormat[Product] = jsonFormat2(Product.apply)
  implicit val productsMarshaller: spray.json.RootJsonFormat[ProductsList] = jsonFormat1(ProductsList.apply)

  def getProductsRoute: server.Route =
    path("products") {
      get {
        complete {
          dao.getProducts
        }
      }
    }

  def getProductRoute: server.Route =
    path("product") {
      get {
        complete {
          dao.getProduct
        }
      }
    }

}
