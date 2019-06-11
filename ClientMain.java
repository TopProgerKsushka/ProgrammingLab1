import com.google.gson.Gson;
import org.xml.sax.SAXException;
import java.security.NoSuchAlgorithmException;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import com.google.gson.JsonParseException;

import static java.lang.System.exit;

public class ClientMain {
    static Gson gson = new Gson();
    public static void main(String args[]) throws NoSuchAlgorithmException, IOException, Guest.DBException, ParserConfigurationException{
        System.out.println("Я Вера");
        if (args.length != 4) {
            System.out.println("4 arguments required : <server> <port> <username> <password>");
            return;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        String server = args[0];
        int port = Integer.valueOf(args[1]);
        String user = args[2];
        String pass = PasswordHasher.generateMD2(args[3]);
        System.out.println("If you aren't registred, type 'logon'");
        while (true) {
            String[] command = br.readLine().split(" ", 2);
            System.out.println (command[0]);
            Guest guest = null;
            String string = null;
            Document document = null;
            Request req = null;
            try{
                switch (command[0]) {
                    case "add" : {
                        req = Request.ADD;
                        guest = Guest.createFromJson(command[1]);
                        break;
                    }
                    case "remove" : {
                        req = Request.REMOVE;
                        guest = Guest.createFromJson(command[1]);
                        break;
                    }
                    case "add_if_max" : {
                        req = Request.ADD_IF_MAX;
                        guest = Guest.createFromJson(command[1]);
                        break;
                    }
                    case "add_if_min" : {
                        req = Request.ADD_IF_MIN;
                        guest = Guest.createFromJson(command[1]);
                        break;
                    }
                    case "remove_lower" : {
                        req = Request.REMOVE_LOWER;
                        guest = Guest.createFromJson(command[1]);
                        break; 
                    }
                    case "show" : {
                        req = Request.SHOW;
                        break;
                    }
                    case "logon" : {
                        req = Request.REGISTER;
                        break;
                    }
                    case "import" : {
                        req = Request.IMPORT;
                        document = db.parse(command[1]);
                        break;
                    }
                    case "info" : {
                        req = Request.INFO;
                        break;
                    }
                    case "save" : {
                        req = Request.SAVE;
                        string = command[1];
                        break;
                    }
                    case "load" : {
                        req = Request.LOAD;
                        string = command[1];
                        break;
                    }
                    default:
                        break;
                }
                if (req == null) {
                    System.out.println("Invalid command");
                    continue;
                }
                Socket socket = new Socket (server, port);
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.writeObject(req);
                output.writeObject(user);
                output.writeObject(pass);
                if (guest != null)
                    output.writeObject(guest);
                if (string != null)
                    output.writeObject(string);
                if (document != null)
                    output.writeObject(document);
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Response response = (Response) input.readObject();
                switch (response){
                    case SUCCESS:
                        System.out.println("done!");
                        break;
                    case MESSAGE:
                        System.out.println((String) input.readObject());
                        break;
                    case ARRAY:
                        ArrayList<Guest> cont = (ArrayList<Guest>) input.readObject();
                        for (Guest g : cont)
                            System.out.println(gson.toJson(g));
                        break;
                    default:
                        System.out.println("Bad response");
                        break;
                }
            }
            catch (JsonParseException | SAXException e) {
                System.out.println("Syntax error");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Bad response");
            }
        }
    }
}
