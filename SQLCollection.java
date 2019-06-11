import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class SQLCollection {
    public static class SQLCException extends Exception
    {
        public enum Cause
        {
            NO_USER("User not found\n"),
            HAS_USER("This user has already registred"),
            HAS_ELEM("There's already a guest with that name");
            public String msg;
            Cause(String s) {
                msg = s;
            }
        }

        Cause cause;

        public SQLCException (Cause c) {
            cause = c;
        }

        @Override
        public String getMessage() {
            return cause.msg;
        }
    }
    String URL, USER, PASS;
    private Connection connection;
    private PreparedStatement add, remove, removeLower, max, min, all, password, register, find, size;
    public SQLCollection(String URL, String USER, String PASS) throws SQLException{
        this.URL = URL;
        this.USER = USER;
        this.PASS = PASS;
        connection = DriverManager.getConnection(URL, USER, PASS);
        add = connection.prepareStatement("INSERT INTO collection(name, place, size, timeofbirth, created, owner) VALUES (?, ?, ?, ?, ?, ?);");
        remove = connection.prepareStatement( "DELETE FROM collection WHERE name = ? AND owner = ?;");
        removeLower = connection.prepareStatement("DELETE FROM collection WHERE name < ? AND owner = ?;");
        max = connection.prepareStatement("SELECT MAX(name) FROM collection;");
        min = connection.prepareStatement("SELECT MIN(name) FROM collection;");
        all = connection.prepareStatement("SELECT * FROM collection;");
        password = connection.prepareStatement("SELECT password FROM passwords WHERE username = ?;");
        register = connection.prepareStatement("INSERT INTO passwords (username, password) VALUES (?, ?);");
        find = connection.prepareStatement("SELECT name FROM collection WHERE name = ?;");
        size = connection.prepareStatement("SELECT COUNT (name) FROM collection AS size;");
    }

    public void createCollection() throws SQLException {
        connection.createStatement().execute("CREATE TABLE collection(name VARCHAR PRIMARY KEY, place VARCHAR, size INT, timeofbirth INT, created TIMESTAMP NOT NULL, owner VARCHAR, FOREIGN KEY (owner) REFERENCES passwords(username) );");
    }

    public void createUserbase() throws SQLException {
        connection.createStatement().execute("CREATE TABLE passwords(username VARCHAR PRIMARY KEY, password VARCHAR NOT NULL);");
    }

    public void dropCollection() throws SQLException {
        connection.createStatement().execute("DROP TABLE collection;");
    }

    public void dropUserbase() throws SQLException {
        connection.createStatement().execute("DROP TABLE passwords;");
    }

    private boolean findElem(String s) throws SQLException {
        find.clearParameters();
        find.setString(1, s);
        return find.executeQuery().next();
    }

    public  void addElem(Guest g, String owner) throws  SQLException, SQLCException{
        if (findElem(g.name))
            throw new SQLCException(SQLCException.Cause.HAS_ELEM);
        add.clearParameters();
        add.setString(1, g.name);
        add.setString(2, g.place);
        add.setInt(3, g.size);
        add.setInt(4, g.timeOfBirth);
        add.setTimestamp(5, Timestamp.valueOf(g.created.toLocalDateTime()));
        add.setString(6, owner);
        add.executeUpdate();
    }

    public void removeElem(Guest g, String owner) throws  SQLException {
        remove.clearParameters();
        remove.setString(1, g.name);
        remove.setString(2, owner);
        remove.executeUpdate();
    }


    public void removeLowerElem(Guest g, String owner) throws  SQLException {
        removeLower.clearParameters();
        removeLower.setString(1, g.name);
        removeLower.setString(2, owner);
        removeLower.executeUpdate();
    }

    public String maxElem() throws SQLException{
        ResultSet set =  max.executeQuery();
        if (!set.next())
            return null;
        return set.getString(1);
    }

    public String minElem() throws SQLException{
        ResultSet set =  min.executeQuery();
        if (!set.next())
            return null;
        return set.getString(1);
    }

    public void newUser(String user, String pass) throws SQLException, SQLCException {
        try {
            getPassword(user);
        } catch (SQLCException e) {
            register.clearParameters();
            register.setString(1, user);
            register.setString(2, pass);
            register.executeUpdate();
            return;
        }
        throw new SQLCException(SQLCException.Cause.HAS_USER);
    }

    public String getPassword(String user) throws SQLException, SQLCException {
        password.clearParameters();
        password.setString(1, user);
        ResultSet s = password.executeQuery();
        if (!s.next()) throw new SQLCException(SQLCException.Cause.NO_USER);
        return s.getString(1);
    }

    public ArrayList<Guest> toArrayList() throws SQLException{
        ArrayList<Guest> arrayList = new ArrayList<>();
        ResultSet set = all.executeQuery();
        while (set.next())
            arrayList.add(Guest.loadFromSQL(set));
        return arrayList;
    }

    public int getSize() throws SQLException{
        ResultSet sz = size.executeQuery();
        sz.next();
        return sz.getInt("size");
    }

    void loadFromDoc(Document doc, String user, ObjectOutputStream output) throws Guest.DBException, SQLException, IOException {

        NodeList guests = doc.getDocumentElement().getElementsByTagName("Guest");
        int ok = 0, bad = 0;
        for (int i = 0; i < guests.getLength(); i++) {
            try {
                addElem(Guest.loadFromXML((Element) guests.item(i)), user);
                ok++;
            } catch (SQLCException e) {
                bad++;
            }
        }
        output.writeObject(Response.MESSAGE);
        output.writeObject("Guests added : " + ok + " succeeded, " + bad + " failed.\n");
    }

    void saveToFile(String s) throws IOException, SQLException {
        ArrayList<Guest> arrayList = toArrayList();
        BufferedWriter bw = new BufferedWriter(new FileWriter(s));
        bw.write("<collection>");
        for (Guest g : arrayList) {
            bw.newLine();
            bw.write("<Guest>\n");
            bw.write("<name>" + g.getName() + "</name>\n");
            if (g.place != null)
                bw.write("<place>" + g.getPlace() + "</place>\n");
            bw.write("<size>" + g.getSize() + "</size>\n");
            bw.write("<time>" + g.getTimeOfBirth() + "</time>\n");
            bw.write("</Guest>");
        }
        bw.newLine();
        bw.write("</collection>");
    }
}
