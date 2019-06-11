//import com.google.gson.JsonParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerMain {


    public static void main(String args[]) throws IOException, ParserConfigurationException, SQLException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        if (args.length < 4)
        {
            System.out.println("Requiers at least 4 arguments : <Server port> <Database adress> <Username> <Password> [-dc] [-dp] [-p] [-c]");
            return;
        }
        SQLCollection collection = new SQLCollection(args[1], args[2], args[3]);
        ServerSocket serverSocket = new ServerSocket(Integer.valueOf(args[0]));
        for (int i = 4; i < args.length; i++) {
            switch (args[i]) {
                case "-dc":
                    collection.dropCollection();
                    break;
                case "-dp":
                    collection.dropUserbase();
                    break;
                case "-p":
                    collection.createUserbase();
                    break;
                case "-c":
                    collection.createCollection();
                    break;
                default:
                    System.out.println("Bad Argument");
                    break;
            }
        }
        Lock mutex = new ReentrantLock();
        System.out.println("connected\n");
        class QueryRunner extends Thread {
            Socket client;

            public QueryRunner(Socket client) {
                this.client = client;
                start();
            }

            public void run() {
                ObjectInputStream input;
                ObjectOutputStream output;
                try {
                    input = new ObjectInputStream(client.getInputStream());
                    output = new ObjectOutputStream(client.getOutputStream());
                } catch (IOException e) {
                    System.out.println("Connection issues:");
                    e.printStackTrace();
                    return;
                }
                try {
                    mutex.lock();
                    Request request = (Request) input.readObject();
                    String user = (String) input.readObject();
                    String pass = (String) input.readObject();
                    System.out.println(user + " : " + pass + " tries to connect");
                    try {
                        if (request.equals(Request.REGISTER)) 
                        {
                            collection.newUser(user, pass);
                            System.out.println("Done!");
                            output.writeObject(Response.SUCCESS);
                            mutex.unlock();
                            client.close();
                            return;
                        }
                        if (!collection.getPassword(user).equals(pass)) {
                            output.writeObject(Response.MESSAGE);
                            output.writeObject("Wrong password\n");
                            mutex.unlock();
                            client.close();
                        }
                        switch (request) {
                            case ADD: {
                                Guest g = (Guest) input.readObject();
                                collection.addElem(g, user);
                                output.writeObject(Response.SUCCESS);
                                mutex.unlock();
                                client.close();
                                break;
                            }
                            case ADD_IF_MIN: {
                                Guest g = (Guest) input.readObject();
                                if (g.name.compareTo(collection.minElem()) < 0)
                                    collection.addElem(g, user);
                                output.writeObject(Response.SUCCESS);
                                mutex.unlock();
                                client.close();
                                break;
                            }
                            case ADD_IF_MAX: {
                                Guest g = (Guest) input.readObject();
                                if (g.name.compareTo(collection.maxElem()) > 0)
                                    collection.addElem(g, user);
                                output.writeObject(Response.SUCCESS);
                                mutex.unlock();
                                client.close();
                                break;
                            }
                            case REMOVE: {
                                Guest g = (Guest) input.readObject();
                                collection.removeElem(g, user);
                                output.writeObject(Response.SUCCESS);
                                mutex.unlock();
                                client.close();
                                break;
                            }
                            case REMOVE_LOWER: {
                                Guest g = (Guest) input.readObject();
                                collection.removeLowerElem(g, user);
                                output.writeObject(Response.SUCCESS);
                                mutex.unlock();
                                client.close();
                                break;
                            }
                            case INFO: {
                                int size = collection.getSize();
                                output.writeObject(Response.MESSAGE);
                                output.writeObject("There are " + size + " Guests in collcetion");
                                mutex.unlock();
                                client.close();
                                break;
                            }
                            case SAVE: {
                                String file = (String) input.readObject();
                                collection.saveToFile(file);
                                output.writeObject(Response.SUCCESS);
                                mutex.unlock();
                                client.close();
                                break;
                            }
                            case LOAD: {
                                String file = (String) input.readObject();
                                collection.loadFromDoc(db.parse(file), user, output);
                                mutex.unlock();
                                client.close();
                                break;
                            }
                            case SHOW: {
                                ArrayList<Guest> arrayList = collection.toArrayList();
                                output.writeObject(Response.ARRAY);
                                output.writeObject(arrayList);
                                mutex.unlock();
                                client.close();
                                break;
                            }
                            case IMPORT: {
                                Document doc = (Document) input.readObject();
                                collection.loadFromDoc(doc, user, output);
                                mutex.unlock();
                                client.close();
                                break;
                            }
                            default:
                                mutex.unlock();
                                client.close();
                                break;
                        }
                    } catch (SQLCollection.SQLCException e) {
                        output.writeObject(Response.MESSAGE);
                        output.writeObject(e.getMessage());
                        mutex.unlock();
                        client.close();
                    } catch (SAXException e) {
                        output.writeObject(Response.MESSAGE);
                        output.writeObject("Syntax error: " + e.getMessage());
                        mutex.unlock();
                        client.close();
                    } catch (Guest.DBException e) {
                        output.writeObject(Response.MESSAGE);
                        output.writeObject("Invalid data:" + e.getMessage());
                        mutex.unlock();
                        client.close();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Bad query format:");
                    e.printStackTrace();
                } catch (SQLException e) {
                    System.out.println("SQL Error:");
                    e.printStackTrace();
                }
            }
        }
        while (true) {
            Socket client = serverSocket.accept();
            new QueryRunner(client);
        }
    }
}
