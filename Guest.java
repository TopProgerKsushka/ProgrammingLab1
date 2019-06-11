import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class Guest extends Character {
    static Gson gson = new Gson();
    public static class DBException extends Exception {
        public enum Cause {
            BADNAME("Guest must have exactly one name\n"),
            BADPLACE("Guest can't be in more than one place\n"),
            BADTIME("Guest can't be born twise\n"),
            BADSIZE("Guest can't have two sizes\n"),
            BADCREATED("Guest object can have one creation time\n");
            String msg;

            Cause(String s) {
                msg = s;
            }
        }

        Cause type;

        @Override
        public String getMessage() {
            return type.msg;
        }

        public DBException(Cause type) {
            this.type = type;
        }
    }

    private ArrayList<Food> favoriteFood = new ArrayList<>();
    OffsetDateTime created;

    public Guest(String name) {
        super(name);
        System.out.println("Новый гость " + name + " успешно добавлен" + " в сказку");
    }

    public int compareTo(Character guest) {
        int minLength = guest.getName().length();
        if (name.length() < minLength) {
            minLength = name.length();
        }
        for (int i = 0; i < minLength; i++) {
            if (guest.getName().charAt(i) == name.charAt(i)) continue;
            if (guest.getName().charAt(i) < name.charAt(i)) {
                return 1;
            } else return -1;
        }
        if (guest.getName().length() == name.length()) return 0;
        return ((minLength == name.length()) ? 1 : -1);
    }

    public void addFavoriteFood(Food food) {
        favoriteFood.add(food);
    }

    public boolean isFavoriteFood(Food food) {
        return favoriteFood.contains(food);
    }

    public void announceFavoriteFood() {
        System.out.print(getName() + ": Я люблю ");
        for (int i = 0; i < favoriteFood.size(); i++) {
            System.out.print(favoriteFood.get(i).getName());
            if (i != favoriteFood.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    public String getPlace() {
        return place;
    }

    public static Guest loadFromSQL(ResultSet set) throws SQLException {
        Guest guest = new Guest(set.getString("name"));
        guest.created = OffsetDateTime.ofInstant(set.getTimestamp("created").toInstant(), ZoneId.systemDefault());
        guest.place = set.getString("place");
        guest.size = set.getInt("size");
        guest.timeOfBirth = set.getInt("timeofbirth");
        return guest;
    }

    public static Guest loadFromXML(Element e) throws DBException
    {
        NodeList names = e.getElementsByTagName("name");
        if (names.getLength() != 1)
            throw new DBException(DBException.Cause.BADNAME);
        Guest guest = new Guest(names.item(0).getTextContent());
        NodeList places = e.getElementsByTagName("place");
        if (places.getLength() > 1)
            throw new DBException(DBException.Cause.BADPLACE);
        if (places.getLength() == 1)
            guest.place = places.item(0).getTextContent();
        NodeList sizes = e.getElementsByTagName("size");
        if (sizes.getLength() > 1)
            throw new DBException(DBException.Cause.BADSIZE);
        if (sizes.getLength() == 1)
            guest.size = Integer.valueOf(sizes.item(0).getTextContent());
        NodeList times = e.getElementsByTagName("time");
        if (times.getLength() > 1)
            throw new DBException(DBException.Cause.BADTIME);
        if (times.getLength() == 1)
            guest.timeOfBirth = Integer.valueOf(times.item(0).getTextContent());
        return guest;
    }

    public static Guest createFromJson(String json) throws JsonParseException, DBException {
        Guest guest = gson.fromJson(json, Guest.class);
        if (guest.name == null)
            throw new DBException(DBException.Cause.BADNAME);
        guest.created = OffsetDateTime.now();
        return guest;
    }
}
