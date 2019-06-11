import java.util.ArrayList;

public class Buffet implements Furniture{
    private ArrayList<Food> content = new ArrayList<>();
    private boolean isOpen = false;

    public void open() {
        System.out.println("Ура! *Персонажи открыли буфет*");
        isOpen = true;
    }

    public void close() {
        System.out.println("Эх, буфет закрыт");
        isOpen = false;
    }

    public void lookAt() {
        if (!isOpen) {
            System.out.println("Прежде чем смотреть на еду, нужно открыть буфет");
            return;
        }
        System.out.print("Давайте посмотрим, чем тут можно поживиться : ");
        for (int i = 0; i < content.size(); i++) {
            System.out.print(content.get(i).getTaste() + " " + content.get(i).getName());
            if (i != content.size() - 1) System.out.print(", ");
        }
        System.out.println(". Перерыто всё, что тут есть!");
    }


    public void addFood(Food food) {
        content.add(content.size(), food);
    }

    public boolean isEatable(Guest guest) {
        boolean result = false;
        for (int i = 0; i < content.size(); i++) {
            if (guest.isFavoriteFood(content.get(i))) {
                System.out.println(guest.getName() + " может сожрать " + content.get(i));
                result = true;
            }
        }
        if (!result) {
            System.out.println("Ничего из найденного в буфете " + guest.getName() + " есть не может :-(");
        }
        return result;
    }

    public void take(Food food){
        if (content.contains(food)) {
            content.remove(food);
            System.out.println("Взяли " + food.getName());
        } else {
            throw new ExistException("Кто-то хочет удалить из буфета " + food.getName() + ", а его в буфете нет");
        }
    }
}
