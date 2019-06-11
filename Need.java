public class Need implements StandardFunc {
    private String name;

    public Need(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean sameNeed(Character character1, Character character2) {
        if (character1.getNeed().equals(character2.getNeed())) {
            System.out.println(character1.getName() + " так же нуждается в " + name + " как и " + character2.getName());
            return true;
        }
        return false;
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
        Need need = (Need) object;
        return (need.hashCode() == hashCode());
    }

    @Override
    public String toString() {
        return (getClass() + "@" + name);
    }
}
