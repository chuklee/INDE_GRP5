import java.sql.{Connection, DriverManager, PreparedStatement}

object ManageForbiddenAreas {
  // Paramètres de connexion à la base de données
  val url = "jdbc:postgresql://172.28.85.10:5432/postgres"
  val user = "postgres"
  val password = "abc"

  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      println("Usage: scala ManageForbiddenAreas [add|remove] [area_name]")
      System.exit(1)
    }

    val action = args(0)
    val area = args(1)

    val connection: Connection = DriverManager.getConnection(url, user, password)

    try {
      action match {
        case "add" => addForbiddenArea(connection, area)
        case "remove" => removeForbiddenArea(connection, area)
        case _ => println("Action non reconnue. Utilisez 'add' ou 'remove'.")
      }
    } finally {
      connection.close()
    }
  }

  def addForbiddenArea(connection: Connection, area: String): Unit = {
    val sql = "INSERT INTO forbidden_areas (area) VALUES (?)"
    val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
    preparedStatement.setString(1, area)
    val rowsAffected = preparedStatement.executeUpdate()
    if (rowsAffected > 0) println(s"Zone interdite '$area' ajoutée avec succès.")
    else println("Échec de l'ajout de la zone interdite.")
  }

  def removeForbiddenArea(connection: Connection, area: String): Unit = {
    val sql = "DELETE FROM forbidden_areas WHERE area = ?"
    val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
    preparedStatement.setString(1, area)
    val rowsAffected = preparedStatement.executeUpdate()
    if (rowsAffected > 0) println(s"Zone interdite '$area' supprimée avec succès.")
    else println(s"Aucune zone interdite '$area' trouvée.")
  }
}
