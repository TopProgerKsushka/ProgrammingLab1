import java.util.*;

public class Location {
    private static String information;

    public static class Coordinate {
        private int x, y;
        private Character character;


        public Coordinate(int x, int y, Character character) {
            this.x = x;
            this.y = y;
            this.character = character;
        }

    }


    public static void change(Character character, int addX, int addY) {
        Comparator<Coordinate> comp = new Comparator<Coordinate>() {
            public int compare(Coordinate a, Coordinate b) {
                //System.out.print(this.getClass().isAnonymousClass());
                return a.x != b.x ? a.x - b.x : b.y - a.y;
            }
        };


        Collections.sort(shape, comp);
        for (int i = 0; i < shape.size(); i++) {
            if (shape.get(i).character.equals(character)) {
                Coordinate coordinate = new Coordinate(shape.get(i).x + addX, shape.get(i).y + addY, character);
                shape.set(i, coordinate);
            }
        }
    }

    private static ArrayList<Coordinate> shape = new ArrayList<>();

    public static void getInformation() {
        System.out.println(information);
    }

    public static void checkInformation() {
        int sumSquare = getSquare(shape.get(0), shape.get(1), shape.get(2));
        sumSquare += getSquare(shape.get(0), shape.get(2), shape.get(3));
        sumSquare += getSquare(shape.get(2), shape.get(1), shape.get(3));
        if (getSquare(shape.get(0), shape.get(1), shape.get(3)) == sumSquare) {
            information = "Расположение : все стоят вокруг персонажа " + shape.get(2).character.getName();
        } else {
            information = "Неправда, что все стоят вокруг персонажа " + shape.get(3).character.getName();
        }
    }

    public static int getSquare(Coordinate a, Coordinate b, Coordinate c) {
        return a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y);
    }

    public static void addPosition(int x, int y, Character character) {
        Coordinate coordinate = new Coordinate(x, y, character);
        shape.add(coordinate);
    }
}
