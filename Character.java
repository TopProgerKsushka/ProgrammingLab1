import java.io.Serializable;

public abstract class Character implements StandardFunc, Serializable, Comparable<Character> {
    protected String name;
    public int size;
    public String place;
    public int timeOfBirth;

    private Need need;
    private Type type;

    public Character(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public int getTimeOfBirth() {
        return timeOfBirth;
    }

    public String getName() {
        return name;
    }

    public void setNeed(Need need) {
        this.need = need;
    }

    public Need getNeed() {
        return need;
    }

    public abstract int compareTo(Character character);

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
        Character character = (Character) object;
        return (character.hashCode() == hashCode());
    }

    @Override
    public String toString() {
        return (getClass() + "@" + name);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void doPromise(String title) {
        class Promise {
            private String title;

            Promise(String title) {
                this.title = title;
            }

            public void doubt() {
                //System.out.print(this.getClass().isLocalClass());
                System.out.println("Может, не надо - сказал " + name);
            }

            public void challenge() {
                System.out.println("Все уговаривают персонажа с именем " + name + " " + title);
            }

            public void remind() {
                System.out.println("Ну-ну, " + type + " " + name + ", вспомни, что ты мне обещал");
            }

        }
        Promise promise = new Promise(title);
        promise.challenge();
        promise.doubt();
        promise.remind();
    }


    public void hug(Character character) throws LogicalException {
        try {
            if ((this.equals(character))) throw new LogicalException("Нельзя обнять самого себя!");
            System.out.println(name + " и " + character.getName() + " обнялись");
        } catch (LogicalException e) {
            System.out.println(e.getTrouble());
        }
    }

    public void tell(Character listener, String speech) {
        System.out.println(name + " сказал : " + ((listener.type != null) ? listener.type : "") +
                listener.getName() + ", " + (speech != null ? speech : ""));
    }

    public void greet(Guest guest) {
        System.out.println("Здравствуй, " + guest.getName() + " - сказала" + name);
    }

    public void newGreet(Guest guest) {
        System.out.print("Здравствуй, " + guest.getName() + " - сказал " + name);
        System.out.println(" дважды, потому что звучало это забавно :3");
        System.out.println(name + " : Кстати, я ещё никогда так не здоровался");
    }

    public void seem(Type type) {
        System.out.println(name + " кажется с виду : " + type);
    }
}
