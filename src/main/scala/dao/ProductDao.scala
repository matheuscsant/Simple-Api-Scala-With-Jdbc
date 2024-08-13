package dao

import java.sql.Connection

final case class Product(id: Long, name: String)

final case class ProductsList(productsList: List[Product])

class ProductDao(connection: Connection) {

  def getProduct: Product = {
    Product(1L, "Teste")
  }

  def getProducts: ProductsList = {
    ProductsList.apply(List(Product(1L, "Teste"), Product(2L, "OutroTeste")))
  }
}
