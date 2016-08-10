package Database_connection;

import java.sql.*;
import java.util.ArrayList;

public class cardConnection {

    public String[] getCard(String cardName) {
        ResultSet resultSet = null;
        String[] result = new String[11];
        try {
            PreparedStatement preparedStatement = ConnectionManager.getInstance().getConnection().prepareStatement("SELECT Name, BaseType, Cost, Actions, Buy, Card, Coin, VP, Text, IsKingdom, CardType1, CardType2 FROM dominioncards WHERE Name = ?");
            preparedStatement.setObject(1, cardName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result[0] = resultSet.getString("BaseType");
                result[1] = resultSet.getString("Cost");
                result[2] = resultSet.getString("Actions");
                result[3] = resultSet.getString("Buy");
                result[4] = resultSet.getString("Card");
                result[5] = resultSet.getString("Coin");
                result[6] = resultSet.getString("VP");
                result[7] = resultSet.getString("Text");
                result[8] = resultSet.getString("IsKingdom");
                result[9] = resultSet.getString("Cardtype1");
                result[10] = resultSet.getString("Cardtype2");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> getKingdomCards(String[] expansions) {
        ResultSet resultSet = null;
        ArrayList<String> result = new ArrayList<>();
        try {
            String stmt = "SELECT * FROM dominioncards WHERE IsKingdom = true AND (";
            for (String e : expansions) {
                stmt += " Expansion = '" + e + "'";
            }
            stmt += ") order by Cost";
            PreparedStatement preparedStatement = ConnectionManager.getInstance().getConnection().prepareStatement(stmt);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
