package Database_connection;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import Engine.GameEngine; 


public class SaveGameConnection {

    public GameEngine gameEngine =null;
    static final long serialVersionUID = 1337;

    public GameEngine getGameEngine() {
        return gameEngine;
    }


    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }


    public  void saveGame(String saveName) throws Exception
    {
        PreparedStatement ps=null;
        String sql=null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(gameEngine);
        oos.flush();
        oos.close();
        bos.close();

        byte[] data = bos.toByteArray();

        if(!this.getAllSaveNames().contains(saveName)){
	        sql="insert into savedGames (gameState, saveName) values(?,?)";
        }
        else{
	        sql="update savedGames set gameState = ? where saveName = ?";

        }
        	ps=ConnectionManager.getInstance().getConnection().prepareStatement(sql);
	        ps.setObject(1, data);
	        ps.setObject(2, saveName);
	        ps.executeUpdate();

    }


    public GameEngine loadGame(String saveName) throws Exception
    {
        GameEngine loadGame=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        String sql=null;

        sql="select * from savedGames where saveName=?";
        ps=ConnectionManager.getInstance().getConnection().prepareStatement(sql);
        ps.setObject(1, saveName);
        rs=ps.executeQuery();

        if(rs.next())
        {
            ByteArrayInputStream bais;
            ObjectInputStream ins;
            
            bais = new ByteArrayInputStream(rs.getBytes("gameState"));
            ins = new ObjectInputStream(bais);

            loadGame =(GameEngine)ins.readObject();
            ins.close();
        }
        else{
        	throw new Exception();
        }

        return loadGame;
    }
    
    public void deleteSave(String saveName) throws Exception{
        PreparedStatement ps=null;
        String sql=null;
        
        if(!getAllSaveNames().contains(saveName)){
        	throw new Exception();
        }
        
        sql="delete from savedGames where saveName=?";
        ps=ConnectionManager.getInstance().getConnection().prepareStatement(sql);
        ps.setObject(1, saveName);
        ps.executeUpdate();
    }
    
    public ArrayList<String> getAllSaveNames(){
    	ResultSet rs = null;
    	String sql=null;
        PreparedStatement ps=null;
		ArrayList<String> result = new ArrayList<>();
		
		try {
			sql = "SELECT saveName FROM savedGames ORDER BY saveName asc";
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
