public class Food implements StandardFunc {
    private String name;
    private Taste taste;
    private boolean exists = true;

    public Food(String name, Taste taste) {
        this.name = name;
        this.taste = taste;
    }


    public enum Taste {
        cool,
        delicious {
            public int calc(int x, int y) {return x - y;}
        },
        awful,
        usual,
        perfect
    }

    @Override
    public int hashCode() {
        int hash = 0;
        final int P = 239;
        final int M = 1000_000_009;
        for (int i = 0; i < name.length(); i++) {
            hash = (hash + name.charAt(i)) % M;
            hash *= P;
            hash %= M;
        }
        return (hash);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Food food = (Food) object;
        if (!food.exists || !exists) {
            return false;
        }
        return (food.hashCode() == hashCode());
    }

    public boolean getExists() {
        return exists;
    }

    @Override
    public String toString() {
        return (getClass() + "@" + name);
    }

    public String getName() {
        return name;
    }

    public Taste getTaste() {
        return taste;
    }

    public void setTaste(Taste taste) {
        this.taste = taste;
    }

    public void eat(Character character) {
        exists = false;
        System.out.println(taste + " " + name + " съеден персонажем " + character.getName());
    }

}
