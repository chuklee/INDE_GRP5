import java.sql.{Connection, DriverManager, ResultSet}

object FetchUsers {
  def main(args: Array[String]): Unit = {
    // Paramètres de connexion à la base de données
    val url = "jdbc:postgresql://172.28.85.10:5432/postgres"
    val user = "postgres"
    val password = "abc"

    // Établir la connexion
    val connection: Connection = DriverManager.getConnection(url, user, password)

    try {
      // Créer la requête SQL
      val statement = connection.createStatement()
      val resultSet: ResultSet = statement.executeQuery("SELECT * FROM alerte_utilisateur")

      // Parcourir les résultats et les afficher
      while (resultSet.next()) {
        println(s"ID: ${resultSet.getInt("id")}")
        println(s"User ID: ${resultSet.getInt("user_id")}")
        println(s"Prénom: ${resultSet.getString("firstname")}")
        println(s"Nom: ${resultSet.getString("lastname")}")
        println(s"Email: ${resultSet.getString("email")}")
        println(s"Travail: ${resultSet.getString("job")}")
        println(s"Localisation: ${resultSet.getString("location")}")
        println(s"Date: ${resultSet.getTimestamp("date")}")
        println(s"Âge: ${resultSet.getInt("age")}")
        println("--------------------")
      }
    } finally {
      connection.close()
    }
  }
}
