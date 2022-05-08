package sample;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Main extends Application {

    Stage window;
    Scene scene;
    VBox vbox;
    HBox hbox1, hbox2, hbox3;
    TableView<Product> productTable;
    TableView<CartItem> cartTable;
    Label quantityLabel, idLabel, productsLabel, cartLabel;
    Text userText, productsText, cartText;
    TextField quantityTF;
    Button addBtn, deleteBtn;

    Cart cart = new Cart(0);

    private static SessionFactory factory;

    @Override
    public void start(Stage primaryStage) throws Exception{

        try{
            factory = new Configuration().configure().buildSessionFactory();
        } catch(Throwable ex){
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

        listProducts();

        window = primaryStage;
        window.setTitle("Sklep zoologiczny");
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        idLabel = new Label("Koszyk o id: " + cart.id);
        idLabel.setPadding(new Insets(10,10,10,10));
        idLabel.setFont(new Font(15));
        productsLabel = new Label("Lista produktów:");
        productsLabel.setPadding(new Insets(0,235,0,0));
        productsLabel.setFont(new Font(20));
        cartLabel = new Label("Koszyk:");
        cartLabel.setFont(new Font(20));



        userText = new Text("Koszyk o id: " + cart.id);
        productsText = new Text("Lista produktów:");
        cartText = new Text("Koszyk:");

        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setMinWidth(50);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> nameCol = new TableColumn<>("Nazwa produktu");
        nameCol.setMinWidth(200);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Integer> availableCol = new TableColumn<>("Dostępna ilość");
        availableCol.setMinWidth(100);
        availableCol.setCellValueFactory(new PropertyValueFactory<>("available"));

        TableColumn<CartItem, String> cartNameCol = new TableColumn<>("Nazwa produktu");
        cartNameCol.setMinWidth(200);
        cartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<CartItem, Integer> cartQuantityCol = new TableColumn<>("Ilość");
        cartQuantityCol.setMinWidth(50);
        cartQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        productTable = new TableView<>();
        //productTable.setMinWidth(350);
        productTable.getColumns().addAll(idCol,nameCol,availableCol);
        //productTable.setEditable(true);
        loadProducts();

        cartTable = new TableView<>();
        cartTable.getColumns().addAll(cartNameCol,cartQuantityCol);
        cartTable.setEditable(true);
        loadCart();

        quantityLabel = new Label("Podaj ilość:");

        quantityTF = new TextField();
        quantityTF.setPromptText("ilość");
        quantityTF.setMinWidth(250);

        addBtn = new Button("Dodaj");
        addBtn.setOnAction(e->addBtnClicked());
        addBtn.setMinWidth(100);

        deleteBtn = new Button("Usuń");
        deleteBtn.setOnAction(e->deleteBtnClicked());
        deleteBtn.setMinWidth(100);

        hbox1 = new HBox(10);
        hbox1.setPadding(new Insets(10,10,10,10));
        hbox1.getChildren().addAll(productsLabel, cartLabel);

        hbox2 = new HBox(10);
        hbox2.setPadding(new Insets(10,10,10,10));
        hbox2.getChildren().addAll(productTable, cartTable);

        hbox3 = new HBox(10);
        hbox3.setPadding(new Insets(10,10,10,10));
        hbox3.getChildren().addAll(quantityLabel,quantityTF,addBtn,deleteBtn);

        vbox = new VBox(10);
        vbox.getChildren().addAll(idLabel,hbox1,hbox2,hbox3);

        scene = new Scene(vbox,1000,500);
        window.setScene(scene);
        window.show();

    }

    public void loadCart(){
        Cart deserializedCart = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("cart.bin"))) {
            deserializedCart = (Cart) inputStream.readObject();
            for(Map.Entry<String, Integer> entry : deserializedCart.cart.entrySet()) {
                CartItem cartItem = new CartItem(entry.getKey(),entry.getValue());
                cartTable.getItems().add(cartItem);
            }
        } catch(IOException i){
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c){
            System.out.println("Product class not found");
            c.printStackTrace();
            return;
        }
    }

    public void loadProducts(){
        Session session = factory.openSession();
        Transaction tx = null;

        try{
            tx = session.beginTransaction();
            List products = session.createQuery("FROM Product").list();
            for(Iterator iterator = products.iterator(); iterator.hasNext();){
                Product product = (Product) iterator.next();
                productTable.getItems().add(product);
            }
            tx.commit();
        } catch (HibernateException e){
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void listProducts(){
        Session session = factory.openSession();
        Transaction tx = null;

        try{
            tx = session.beginTransaction();
            List products = session.createQuery("FROM Product").list();
            for(Iterator iterator = products.iterator(); iterator.hasNext();){
                Product product = (Product) iterator.next();
                System.out.println("id: " + product.getId());
                System.out.println("name: " + product.getName());
                System.out.println("available: " + product.getAvailable());
            }
            tx.commit();
        } catch (HibernateException e){
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void decreaseProduct(Integer productID, int quantity){
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Product product = (Product)session.get(Product.class, productID);
            int available = product.getAvailable();
            available -= quantity;
            product.setAvailable(available);
            session.update(product);
            tx.commit();
        } catch (HibernateException e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void increaseProduct(Integer productID, int quantity){
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Product product = (Product)session.get(Product.class, productID);
            int available = product.getAvailable();
            available += quantity;
            product.setAvailable(available);
            session.update(product);
            tx.commit();
        } catch (HibernateException e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void addBtnClicked(){
        //try zabezpiecza przed niepoprawnym inputem
        try {
            int quantity = Integer.parseInt(quantityTF.getText());
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
            //jesli nie zostal wybrany produkt
            if(selectedProduct == null){
                AlertBox.display("Komunikat","Wybierz produkt");
            } //jesli produkt zostal wybrany - normalna procedura
            else {
                //jesli podano wiecej niz jest
                if(quantity > selectedProduct.available){
                    AlertBox.display("Komunikat", "Podana wartość przekracza dostępną ilość");
                } else {
                    CartItem cartItem = new CartItem(selectedProduct.getName(), quantity);
                    //sprawdz czy w koszyku jest juz produkt o takiej samej nazwie
                    if (cart.cart.containsKey(cartItem.name)) {
                        //zaktualizuj wartosc w obiekcie koszyka
                        cart.cart.put(cartItem.name, cart.cart.get(cartItem.name) + cartItem.quantity);
                        //zaktualizuj wartosc w tabeli
                        ObservableList<CartItem> allItems = cartTable.getItems();
                        allItems.forEach(i->{
                            if(i.name.equals(selectedProduct.name)){
                                System.out.println("eeeeee");
                                //aktualizacja w tabeli
                                i.quantity += quantity;
                            }
                        });
                    } else {
                        //dodanie do koszyka
                        cart.addToCart(cartItem);
                        //dodanie do tabeli
                        cartTable.getItems().add(cartItem);
                    }
                    //zaktualizowanie dostepnej ilosci w bazie danych
                    decreaseProduct(selectedProduct.id, quantity);
                    productTable.getItems().clear();
                    loadProducts();
                    cartTable.refresh();
                }
            }
        } catch (NumberFormatException e){
            AlertBox.display("Błędne dane", "Ilość musi być liczbą całkowitą");
        }
    }

    public void deleteBtnClicked(){
        //try zabezpiecza przed niepoprawnym inputem
        try {
            int quantity = Integer.parseInt(quantityTF.getText());
            CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
            ObservableList<CartItem> allItems = cartTable.getItems();
            ObservableList<Product> allProducts = productTable.getItems();
            //jesli nie zostal wybrany produkt
            if(selectedItem == null){
                AlertBox.display("Komunikat","Wybierz produkt");
            }
            else {
                //jesli wprowadzono ilosc do usuniecia wieksza badz rowna obecnej liczbie
                //to usun obiekt z koszyka i z tabeli
                if(quantity >= selectedItem.quantity){
                    //usuniecie z tabeli
                    allItems.remove(selectedItem);
                    //usuniecie z koszyka
                    cart.removeFromCart(selectedItem);

                    //ultimate znalezienie itemu w tabeli
                    allProducts.forEach(i->{
                        if(i.name.equals(selectedItem.name)){
                            System.out.println("eeeeee");
                            //aktualizacja w bazie danych
                            increaseProduct(i.id,selectedItem.quantity);
                        }
                    });
                }
                //w przeciwnym razie zmniejsz liczbe w koszyku i tabeli
                else {
                    //zmniejszenie w tabeli
                    selectedItem.quantity -= quantity;
                    //zmniejszenie w koszyku
                    cart.cart.put(selectedItem.name,cart.cart.get(selectedItem.name) - quantity);
                    //aktualizacja w bazie danych
                    allProducts.forEach(i->{
                        if(i.name.equals(selectedItem.name)){
                            System.out.println("aaaa");
                            //aktualizacja w bazie danych
                            increaseProduct(i.id,quantity);
                        }
                    });

                }
                productTable.getItems().clear();
                loadProducts();
                cartTable.refresh();
            }
        } catch (NumberFormatException e){
            AlertBox.display("Błędne dane", "Ilość musi być liczbą całkowitą");
        }
    }

    private void closeProgram(){
        Boolean answer = ConfirmBox.display("Komunikat", "Na pewno chcesz zakończyć program?");
        if(answer) {
            //serializacja
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("cart.bin"))) {
                outputStream.writeObject(cart);
                System.out.println("Zapisano stan koszyka.");
            } catch (IOException i){
                i.printStackTrace();
                return;
            }
            window.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
