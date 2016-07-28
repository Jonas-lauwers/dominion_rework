package Database_connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PremadeSetsConnection {
	public ArrayList<String> getKingdomSet(String setName) {
		ResultSet resultSet = null;
		ArrayList<String> result = new ArrayList<>();
		try {
			String stmt = "SELECT ";
			for(int i = 1; i<=10; i++){
				stmt += "`card " + i + "` ";
				if(i!=10){
					stmt +=  ", ";
				}
			}
			stmt += "FROM kingdomsets WHERE setname = (?)";
			PreparedStatement ps = ConnectionManager.getInstance().getConnection().prepareStatement(stmt);
			ps.setObject(1, setName);
			resultSet = ps.executeQuery();
			while(resultSet.next()) {
				for(int i = 1; i<=10; i++) {
					result.add(resultSet.getString(i));
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void setKingdomSet(String setName, ArrayList<String> cards) throws Exception{
		if(cards.size() != 10){
			throw new IllegalArgumentException();
		}
		PreparedStatement ps=null;
        String sql=null;
        
        if(!getAllSetNames().contains(setName)){
        	sql ="insert into kingdomsets (";
        	for(int i = 1; i<=10; i++){
        		sql += "`card " + i + "`, ";
			}
        	sql += " setname) values(?,?,?,?,?,?,?,?,?,?,?)";
        }
        else{
        	sql="update kingdomsets set "; 
        	for(int i = 1; i<=10; i++){
        		sql += "`card " + i + "` = ? ";
        		if(i!=10){
					sql +=  ", ";
				}
			}
        	
        	sql += "where setname = ?";
        }
        
        ps=ConnectionManager.getInstance().getConnection().prepareStatement(sql);
        int i = 1;
        for(String c : cards){
        	ps.setObject(i++, c);
        }
        ps.setObject(11, setName);
        ps.executeUpdate();
	}
	
    public void deleteSet(String setName) throws Exception{
        PreparedStatement ps=null;
        String sql=null;
        
        if(!getAllSetNames().contains(setName)){
        	throw new Exception();
        }
        
        sql="delete from kingdomsets where setname=?";
        ps=ConnectionManager.getInstance().getConnection().prepareStatement(sql);
        ps.setObject(1, setName);
        ps.executeUpdate();
    }
	
	public ArrayList<String> getAllSetNames(){
    	ResultSet rs = null;
    	String sql=null;
        PreparedStatement ps=null;
		ArrayList<String> result = new ArrayList<>();
		
		try {
			sql = "SELECT setname FROM kingdomsets ORDER BY setname asc";
			ps = ConnectionManager.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getString(1));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
    }
}
