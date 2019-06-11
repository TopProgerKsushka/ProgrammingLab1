import java.io.Serializable;

public enum Request implements Serializable {
    REMOVE, INFO, REMOVE_LOWER, ADD, ADD_IF_MIN, ADD_IF_MAX, SHOW, IMPORT, LOAD, SAVE, REGISTER;
}
