public class LogicalException extends Exception {
    private String trouble;
    LogicalException(String s) {
        super(s);
        trouble = "(!!!) " + s;
    }
    public String getTrouble() {
        return trouble;
    }
}
