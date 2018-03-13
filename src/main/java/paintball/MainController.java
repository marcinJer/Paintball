package paintball;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class MainController implements Initializable {

    //Weapons
    @FXML
    private Label labelInfoWeapon;
    @FXML
    private ComboBox<String> comboBoxBrand;
    @FXML
    private TableView<Weapon> WeaponsTable;
    @FXML
    private TableColumn<Weapon, Integer> columnWeaponId;
    @FXML
    private TableColumn<Weapon, String> columnWeaponBrand;
    @FXML
    private TableColumn<Weapon, String> columnWeaponModel;
    @FXML
    private ComboBox<String> comboBoxModel;
    
    //Clients
    @FXML
    private TextField textClientName;
    @FXML
    private TextField textClientSurname;
    @FXML
    private TextField textClientId;
    @FXML
    private TableView<Client> clientsTable;
    @FXML
    private TableColumn<Client, String> columnClientsName;
    @FXML
    private TableColumn<Client, String> columnClientsSurname;
    @FXML
    private TableColumn<Client, Long> columnClientPesel;
    @FXML
    private TableColumn<Client, Integer> columnClientId;
    private ObservableList<Client> ObservableListClients;
    private ObservableList<Weapon> ObservableListWeapons;
    private ObservableList<Order> ObservableListOrders;

    //Orders
    @FXML
    private ComboBox<Weapon> comboBoxWeapon;
    @FXML
    private ComboBox<Client> comboBoxClient;
    @FXML
    private ComboBox<Integer> comboBoxBullets;
    @FXML
    private TableView<Order> ordersTable; 
    @FXML
    private TextField textFieldPrice;
    @FXML
    private TableColumn<Order, Integer> columnPrice;
    @FXML
    private TableColumn<Order, Integer> columnBullets;
    @FXML
    private TableColumn<Order, String> columnOrderClientName;
    @FXML
    private TableColumn<Order, String> columnOrderClientSurname;
    @FXML
    private TableColumn<Order, Long> columnOrderClientId;
    @FXML
    private TableColumn<Order, String> columnOrderBrand;
    @FXML
    private TableColumn<Order, String> columnOrderType;
    @FXML
    private Label labelInfoClient;
    @FXML
    private Label labelInfoAdminOrders;
    private Session session;
    private SessionFactory sessionFactory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDatabase();

        loadWeaponDataFromDatabase(null);
        loadClientDataFromDatabase(null);
        loadDataFromWeaponDatabaseIntoCombobox(null);
        loadDataFromClietnDatabaseIntoCombobox(null);
        loadOrderDataFromDatabase(null);
        
        comboBoxBrand.getItems().addAll("Brawler", "Spyder Kingman");
        comboBoxModel.getItems().addAll("Pompka", "Pneumatyczny", "Elektro-pneumatyczny", "Elektroniczny");
        comboBoxBullets.getItems().addAll(
        100,
        200,
        300,
        400,
        500,
        600);
    }

    private void initDatabase() {

        Configuration configuration = new Configuration().configure("HibernateConf\\hibernate.cfg.xml");

        configuration.addAnnotatedClass(Weapon.class);
        configuration.addAnnotatedClass(Client.class);
        configuration.addAnnotatedClass(Order.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();

        this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        this.session = sessionFactory.openSession();
    }

    public void closeDbConnection() {
        this.session.close();
        this.sessionFactory.close();
    }

    //Weapons
    @FXML
    public void addWeapon(ActionEvent event) {
        if((comboBoxModel.getSelectionModel().getSelectedItem() == null) || (comboBoxBrand.getSelectionModel().getSelectedItem() == null) ){
            labelInfoWeapon.setText("Proszę wypełnić wszystkie pola");
            return;
        }
        Weapon weapon = new Weapon(null, comboBoxBrand.getSelectionModel().getSelectedItem(), comboBoxModel.getSelectionModel().getSelectedItem());

        Transaction transaction = session.beginTransaction();
        session.save(weapon);
        transaction.commit();

        comboBoxBrand.getSelectionModel().clearSelection();
        comboBoxModel.getSelectionModel().clearSelection();

        
        labelInfoWeapon.setText("Dodano marker paintballowy");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoWeapon.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoWeapon.setVisible(true);
        
        refreshWeaponsTable();
        refreshWeaponComboBox();
    }

    @FXML
    public void loadWeaponDataFromDatabase(ActionEvent event) {
        Transaction transaction = session.beginTransaction();
        List<Weapon> allWeapons = session.createCriteria(Weapon.class).list();
        transaction.commit();

        columnWeaponId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnWeaponBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        columnWeaponModel.setCellValueFactory(new PropertyValueFactory<>("type"));
       

        WeaponsTable.setItems(null);
        WeaponsTable.setItems(ObservableListWeapons);

        ObservableList<Weapon> weaponsList = FXCollections.observableArrayList(allWeapons);
        WeaponsTable.setItems(weaponsList);
    }

    @FXML
    public void deleteWeapon(ActionEvent event) {

        Weapon weapon = WeaponsTable.getSelectionModel().getSelectedItem();
        Transaction transaction = session.beginTransaction();
        session.delete(weapon);
        transaction.commit();

        comboBoxBrand.getSelectionModel().clearSelection();
        comboBoxModel.getSelectionModel().clearSelection();
        labelInfoWeapon.setText("Usunięto marker paintballowy!");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoWeapon.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoWeapon.setVisible(true);
        refreshWeaponsTable();
        refreshWeaponComboBox();
    
    }
    

    @FXML
    public void editWeapon(ActionEvent event) {

        
        if(((comboBoxModel.getSelectionModel().getSelectedItem() == null) || comboBoxModel.getSelectionModel().isEmpty()) || ((comboBoxBrand.getSelectionModel().getSelectedItem()== null) || comboBoxBrand.getSelectionModel().isEmpty()) ){
            labelInfoWeapon.setText("Proszę wypełnić wszystkie pola");
            return;
        }

        int selectedId = WeaponsTable.getSelectionModel().getSelectedItem().getId();
        Weapon weaponEdit = new Weapon(selectedId, comboBoxBrand.getSelectionModel().getSelectedItem(), comboBoxModel.getSelectionModel().getSelectedItem());
        weaponEdit.setId(selectedId);

        Transaction transaction = session.beginTransaction();
        session.merge(weaponEdit);
        transaction.commit();

        comboBoxBrand.getSelectionModel().clearSelection();
        comboBoxModel.getSelectionModel().clearSelection();
        labelInfoWeapon.setText("Dane markeru paintballowego \n "
                + "zostały zmienione!");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoWeapon.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoWeapon.setVisible(true);
        refreshWeaponsTable();
        refreshWeaponComboBox();
    }

    @FXML
    public void refreshWeaponsTable() {

        Transaction transaction = session.beginTransaction();
        List<Weapon> allWeapons = session.createCriteria(Weapon.class).list();
        transaction.commit();

        ObservableList<Weapon> weaponsList = FXCollections.observableArrayList(allWeapons);
        WeaponsTable.setItems(weaponsList);
        WeaponsTable.refresh();
    }

    @FXML
    public void handleWeapon(MouseEvent event) {
        Weapon selected = WeaponsTable.getSelectionModel().getSelectedItem();

        if (selected != null) {

            
            refreshWeaponsTable();
        }
    }

    // ==================================================================================================================================
    //CLIENTS
    // ==================================================================================================================================
    
    @FXML
    public void addClient(ActionEvent event) {
        if(((textClientName.getText() == null) || textClientName.getText().isEmpty()) || ((textClientSurname.getText() == null) || textClientSurname.getText().isEmpty()) || ((textClientId.getText() == null)|| (textClientId.getText().isEmpty())) ){
            labelInfoClient.setText("Proszę wypełnić wszystkie pola");
            return;
        }
        String name = textClientName.getText();
        String surname = textClientSurname.getText();
        
        if (textClientId.getLength() >= 11) {
            textClientId.setText(textClientId.getText().substring(0, 11));
        } else {
            labelInfoClient.setText("PESEL musi składać się z wyłącznie z 11 cyfr!");
            PauseTransition visiblePause = new PauseTransition(
                    Duration.seconds(3)
            );
            visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    labelInfoClient.setVisible(false);
                }
            });
            visiblePause.play();
            labelInfoClient.setVisible(true);
            return;
        }
        long pesel = Long.parseLong(textClientId.getText());

        Client client = new Client(null, pesel, name, surname);

        Transaction transaction = session.beginTransaction();
        session.save(client);
        transaction.commit();

        textClientName.clear();
        textClientSurname.clear();
        textClientId.clear();
        labelInfoClient.setText("Dodano klienta!");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoClient.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoClient.setVisible(true);
        refreshClientTable();
        refreshClientComboBox();

    }
    
    @FXML
    public void editionInstruction(ActionEvent event){
        labelInfoClient.setText("1. Kliknij na wybranego klienta w tabeli \n"
                + "2. Wprowadź nowe dane w polach tekstowych \n"
                + "3. Kliknij 'edytuj'");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(8)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoClient.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoClient.setVisible(true);
    }
    
    @FXML
    public void deleteInstruction(ActionEvent event){
        labelInfoClient.setText("1. Kliknij na wybranego klienta w tabeli \n"
                + "2. Kliknij usuń \n");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(8)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoClient.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoClient.setVisible(true);
    }
    
    @FXML
    public void editionInstruction1(ActionEvent event){
        labelInfoWeapon.setText("1. Kliknij na wybranego klienta w tabeli \n"
                + "2. Wprowadź nowe dane w polach tekstowych \n"
                + "3. Kliknij 'edytuj'");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(8)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoWeapon.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoWeapon.setVisible(true);
    }
    
    @FXML
    public void deleteInstruction1(ActionEvent event){
        labelInfoWeapon.setText("1. Kliknij na wybranego klienta w tabeli \n"
                + "2. Kliknij usuń \n");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(8)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoWeapon.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoWeapon.setVisible(true);
    }

    @FXML
    public void loadClientDataFromDatabase(ActionEvent event) {
        Transaction transaction = session.beginTransaction();
        List<Client> allClients = session.createCriteria(Client.class).list();
        transaction.commit();

        columnClientsName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnClientsSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        columnClientPesel.setCellValueFactory(new PropertyValueFactory<>("pesel"));
        columnClientId.setCellValueFactory(new PropertyValueFactory<>("id"));

        clientsTable.setItems(null);
        clientsTable.setItems(ObservableListClients);

        ObservableList<Client> clientsList = FXCollections.observableArrayList(allClients);
        clientsTable.setItems(clientsList);
    }

    @FXML
    public void deleteClient(ActionEvent event) {

        if(clientsTable.getSelectionModel().isEmpty()){
            labelInfoClient.setText("Wybierz klienta!");
            PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoClient.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoClient.setVisible(true);
            return;
            
        }
        Client client = clientsTable.getSelectionModel().getSelectedItem();
        try{
           
        Transaction transaction = session.beginTransaction();
        session.delete(client);
        transaction.commit();

        textClientName.clear();
        textClientSurname.clear();
        textClientId.clear();
        labelInfoClient.setText("Usunięto klienta!");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoClient.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoClient.setVisible(true);
        loadOrderDataFromDatabase(null);
        refreshClientTable();
        refreshClientComboBox();
        }catch(RuntimeException e){
            labelInfoClient.setText("Ten klient już złożył zamówienie. \n "
                    + "Nie można go usunąć!");
        }
    }

    @FXML
    public void editClient(ActionEvent event) {

        String name = textClientName.getText();
        String surname = textClientSurname.getText();
        if (textClientId.getLength() >= 11) {
            textClientId.setText(textClientId.getText().substring(0, 11));
        } else {
            labelInfoClient.setText("PESEL musi składać się z wyłącznie z 11 cyfr!");
            PauseTransition visiblePause = new PauseTransition(
                    Duration.seconds(3)
            );
            visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    labelInfoClient.setVisible(false);
                }
            });
            visiblePause.play();
            labelInfoClient.setVisible(true);
            return;

        }
        long pesel = Long.parseLong(textClientId.getText());

        int selectedId = clientsTable.getSelectionModel().getSelectedItem().getId();
        Client clientEdit = new Client(selectedId, pesel, name, surname);
        clientEdit.setId(selectedId);

        Transaction transaction = session.beginTransaction();
        session.merge(clientEdit);
        transaction.commit();

        textClientName.clear();
        textClientSurname.clear();
        textClientId.clear();
        
        labelInfoClient.setText("Dane klienta zostały zmienione");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoClient.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoClient.setVisible(true);
        
        refreshClientTable();
        refreshClientComboBox();
        refreshOrdersTable();
    }

    @FXML
    public void handleClient(MouseEvent event) {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();

        if (selected != null) {

            textClientName.setText(selected.getName());
            textClientSurname.setText(selected.getSurname());
            String s = String.valueOf(selected.getPesel());
            textClientId.setText(s);

            refreshClientTable();
        }
    }

    @FXML
    public void refreshClientTable() {

        Transaction transaction = session.beginTransaction();
        List<Client> allClients = session.createCriteria(Client.class).list();
        transaction.commit();

        ObservableList<Client> clientsList = FXCollections.observableArrayList(allClients);
        clientsTable.setItems(clientsList);
        clientsTable.refresh();
    }

    // ----------------------------------------------------------------------------------------------------------------------------
    // ============================================================================================================================
    @FXML
    public void loadDataFromWeaponDatabaseIntoCombobox(ActionEvent event) {

        Transaction transaction = session.beginTransaction();
        List<Weapon> allWeapons = session.createCriteria(Weapon.class).list();
        transaction.commit();

        ObservableList<Weapon> weaponsList = FXCollections.observableArrayList(allWeapons);
        comboBoxWeapon.setItems(weaponsList);

        StringConverter<Weapon> scConverter = new StringConverter<Weapon>() {

            @Override
            public String toString(Weapon weapons) {
                return weapons.getBrand() + " " + weapons.getType();
            }

            @Override
            public Weapon fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };
        comboBoxWeapon.setConverter(scConverter);
        
        refreshWeaponComboBox();

    }

    @FXML
    public void refreshWeaponComboBox() {

        Transaction transaction = session.beginTransaction();
        List<Weapon> allWeapons = session.createCriteria(Weapon.class).list();
        transaction.commit();

        ObservableList<Weapon> weaponsList = FXCollections.observableArrayList(allWeapons);
        comboBoxWeapon.setItems(weaponsList);
        

    }

    @FXML
    public void loadDataFromClietnDatabaseIntoCombobox(ActionEvent event) {

        Transaction transaction = session.beginTransaction();
        List<Client> allClients = session.createCriteria(Client.class).list();
        transaction.commit();

        ObservableList<Client> clientsList = FXCollections.observableArrayList(allClients);
        comboBoxClient.setItems(clientsList);

        StringConverter<Client> scConverter = new StringConverter<Client>() {

            @Override
            public String toString(Client clients) {
                return clients.getName() + " " + clients.getSurname();
            }

            @Override
            public Client fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };
        comboBoxClient.setConverter(scConverter);
        refreshClientComboBox();

    }

    @FXML
    public void refreshClientComboBox() {

        Transaction transaction = session.beginTransaction();
        List<Client> allClients = session.createCriteria(Client.class).list();
        transaction.commit();

        ObservableList<Client> clientsList = FXCollections.observableArrayList(allClients);
        comboBoxClient.setItems(clientsList);


    }

    @FXML
    public void setOrderPrice() {

        if (comboBoxBullets.getItems() == null || comboBoxBullets.getItems().isEmpty()   || comboBoxBullets.getSelectionModel().isEmpty()) {
            textFieldPrice.setText(null);
            return;
        } else {
            
            int price = comboBoxBullets.getSelectionModel().getSelectedItem();
            int price1 = (price/100) * 20;
            String result = String.valueOf(price1);
            textFieldPrice.setText(result);
            textFieldPrice.setDisable(true);

        }
    }

   @FXML
    public void addOrder(ActionEvent event) {
if(((comboBoxClient.getSelectionModel().getSelectedItem() == null) || (comboBoxClient.getSelectionModel().isEmpty())) || ((comboBoxWeapon.getSelectionModel().getSelectedItem() == null) || (comboBoxWeapon.getSelectionModel().isEmpty())) || ((comboBoxBullets.getSelectionModel().getSelectedItem() == null) || (comboBoxBullets.getSelectionModel().isEmpty()))){
    labelInfoAdminOrders.setText("Proszę wypełnić wszystkie pola formularzu!");
    return;
}
        Client client = comboBoxClient.getSelectionModel().getSelectedItem();
        Weapon weapon = comboBoxWeapon.getSelectionModel().getSelectedItem();
        Integer price = Integer.parseInt(textFieldPrice.getText());

        Order order = new Order(client, weapon, price, comboBoxBullets.getSelectionModel().getSelectedItem());
        Transaction transaction = session.beginTransaction();
        session.save(order);
        transaction.commit();

        comboBoxClient.getSelectionModel().clearSelection();
        comboBoxWeapon.getSelectionModel().clearSelection();
        comboBoxBullets.getSelectionModel().clearSelection();
        textFieldPrice.clear();
        labelInfoAdminOrders.setText("Dodano zamówienie!");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoAdminOrders.setVisible(false);
            }
        });
        visiblePause.play();
        comboBoxBullets.getSelectionModel().clearSelection();
        labelInfoAdminOrders.setVisible(true);
        loadOrderDataFromDatabase(null);
    }


    @FXML
    public void loadOrderDataFromDatabase(ActionEvent event) {

        Transaction transaction = session.beginTransaction();
        List<Order> allOrders = session.createCriteria(Order.class).list();
        transaction.commit();

        
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnBullets.setCellValueFactory(new PropertyValueFactory<>("loanPeriod"));

        columnOrderClientName.setCellValueFactory(new Callback<CellDataFeatures<Order, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Order, String> p) {
                return new SimpleStringProperty(p.getValue().getClient().getName());
            }
        });

        columnOrderClientSurname.setCellValueFactory(new Callback<CellDataFeatures<Order, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Order, String> p) {
                return new SimpleStringProperty(p.getValue().getClient().getSurname());
            }
        });

        columnOrderClientId.setCellValueFactory(new Callback<CellDataFeatures<Order, Long>, ObservableValue<Long>>() {
            @Override
            public ObservableValue<Long> call(CellDataFeatures<Order, Long> p) {
                return new SimpleObjectProperty<>(p.getValue().getClient().getPesel());
            }
        });

        columnOrderBrand.setCellValueFactory(new Callback<CellDataFeatures<Order, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Order, String> p) {
                return new SimpleStringProperty(p.getValue().getWeapon().getBrand());
            }
        });

        columnOrderType.setCellValueFactory(new Callback<CellDataFeatures<Order, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Order, String> p) {
                return new SimpleStringProperty(p.getValue().getWeapon().getType());
            }
        });

        ordersTable.setItems(null);
        ordersTable.setItems(ObservableListOrders);

        ObservableList<Order> ordersList = FXCollections.observableArrayList(allOrders);
        ordersTable.setItems(ordersList);

    }

    @FXML
    public void deleteOrder(ActionEvent event) {
        Order order = ordersTable.getSelectionModel().getSelectedItem();
        Transaction transaction = session.beginTransaction();
        session.delete(order);
        transaction.commit();
        labelInfoAdminOrders.setText("Usunięto zamówienie!");
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                labelInfoAdminOrders.setVisible(false);
            }
        });
        visiblePause.play();
        labelInfoAdminOrders.setVisible(true);
        loadOrderDataFromDatabase(null);
        refreshOrdersTable();
    }

    @FXML
    public void refreshOrdersTable() {

        Transaction transaction = session.beginTransaction();
        List<Order> allOrders = session.createCriteria(Order.class).list();
        transaction.commit();

        ObservableList<Order> ordersList = FXCollections.observableArrayList(allOrders);
        ordersTable.setItems(ordersList);
        ordersTable.refresh();
    }

}
