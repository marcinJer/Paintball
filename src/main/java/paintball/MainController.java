package paintball;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
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
        loadDataFromClientDatabaseIntoCombobox(null);
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

    private void saveToDatabase(Object object) {
        Transaction transaction = session.beginTransaction();
        session.save(object);
        transaction.commit();
    }

    private void deleteFromDatabase(Object object) {
        Transaction transaction = session.beginTransaction();
        session.delete(object);
        transaction.commit();
    }

    private void updateObject(Object object) {
        Transaction transaction = session.beginTransaction();
        session.merge(object);
        transaction.commit();
    }

    private void labelVisibility(Label label) {
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                label.setVisible(false);
            }
        });
        visiblePause.play();
        label.setVisible(true);

    }



    //Weapons
    @FXML
    private void addWeapon(ActionEvent event) {
        if ((comboBoxModel.getSelectionModel().getSelectedItem() == null) || (comboBoxBrand.getSelectionModel().getSelectedItem() == null)) {
            labelInfoWeapon.setText("Proszę wypełnić wszystkie pola");
            return;
        }
        Weapon weapon = new Weapon(null, comboBoxBrand.getSelectionModel().getSelectedItem(), comboBoxModel.getSelectionModel().getSelectedItem());

        saveToDatabase(weapon);

        comboBoxBrand.getSelectionModel().clearSelection();
        comboBoxModel.getSelectionModel().clearSelection();


        labelInfoWeapon.setText("Dodano marker paintballowy");
        labelVisibility(labelInfoWeapon);

        refreshWeaponsTable();
        refreshWeaponComboBox();
    }

    @FXML
    private void loadWeaponDataFromDatabase(ActionEvent event) {
        Transaction transaction = session.beginTransaction();
        List allWeapons = session.createCriteria(Weapon.class).list();
        transaction.commit();

        columnWeaponId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnWeaponBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        columnWeaponModel.setCellValueFactory(new PropertyValueFactory<>("type"));


        WeaponsTable.setItems(null);
        WeaponsTable.setItems(ObservableListWeapons);

        ObservableList weaponsList = FXCollections.observableArrayList(allWeapons);
        WeaponsTable.setItems(weaponsList);
    }

    @FXML
    private void deleteWeapon(ActionEvent event) {

        Weapon weapon = WeaponsTable.getSelectionModel().getSelectedItem();

        deleteFromDatabase(weapon);

        comboBoxBrand.getSelectionModel().clearSelection();
        comboBoxModel.getSelectionModel().clearSelection();
        labelInfoWeapon.setText("Usunięto marker paintballowy!");

        labelVisibility(labelInfoWeapon);

        refreshWeaponsTable();
        refreshWeaponComboBox();

    }


    @FXML
    private void editWeapon(ActionEvent event) {


        if (((comboBoxModel.getSelectionModel().getSelectedItem() == null) || comboBoxModel.getSelectionModel().isEmpty()) || ((comboBoxBrand.getSelectionModel().getSelectedItem() == null) || comboBoxBrand.getSelectionModel().isEmpty())) {
            labelInfoWeapon.setText("Proszę wypełnić wszystkie pola");
            return;
        }

        int selectedId = WeaponsTable.getSelectionModel().getSelectedItem().getId();
        Weapon weaponEdit = new Weapon(selectedId, comboBoxBrand.getSelectionModel().getSelectedItem(), comboBoxModel.getSelectionModel().getSelectedItem());
        weaponEdit.setId(selectedId);

        updateObject(weaponEdit);

        comboBoxBrand.getSelectionModel().clearSelection();
        comboBoxModel.getSelectionModel().clearSelection();
        labelInfoWeapon.setText("Dane markeru paintballowego \n "
                + "zostały zmienione!");
        labelVisibility(labelInfoWeapon);
        refreshWeaponsTable();
        refreshWeaponComboBox();
    }

    @FXML
    private void refreshWeaponsTable() {

        Transaction transaction = session.beginTransaction();
        List allWeapons = session.createCriteria(Weapon.class).list();
        transaction.commit();

        ObservableList weaponsList = FXCollections.observableArrayList(allWeapons);
        WeaponsTable.setItems(weaponsList);
        WeaponsTable.refresh();
    }

    @FXML
    private void handleWeapon(MouseEvent event) {
        Weapon selected = WeaponsTable.getSelectionModel().getSelectedItem();

        if (selected != null) {


            refreshWeaponsTable();
        }
    }

    // ==================================================================================================================================
    //CLIENTS
    // ==================================================================================================================================

    @FXML
    private void addClient(ActionEvent event) {
        if (((textClientName.getText() == null) || textClientName.getText().isEmpty()) || ((textClientSurname.getText() == null) || textClientSurname.getText().isEmpty()) || ((textClientId.getText() == null) || (textClientId.getText().isEmpty()))) {
            labelInfoClient.setText("Proszę wypełnić wszystkie pola");
            labelVisibility(labelInfoClient);
            return;
        }

        String name = textClientName.getText();
        String surname = textClientSurname.getText();

        if (textClientId.getLength() > 11) {
            labelInfoClient.setText("PESEL musi składać się z wyłącznie z 11 cyfr!");
            labelVisibility(labelInfoClient);

        } else if (textClientId.getLength() < 11) {
            labelInfoClient.setText("PESEL musi składać się z wyłącznie z 11 cyfr!");
            labelVisibility(labelInfoClient);

        } else {
            long pesel = Long.parseLong(textClientId.getText());

            Client client = new Client(null, pesel, name, surname);

            saveToDatabase(client);

            textClientName.clear();
            textClientSurname.clear();
            textClientId.clear();
            labelInfoClient.setText("Dodano klienta!");

            labelVisibility(labelInfoClient);
            refreshClientTable();
            refreshClientComboBox();


        }
    }

    @FXML
    private void editionInstruction(ActionEvent event) {
        labelInfoClient.setText("1. Kliknij na wybranego klienta w tabeli \n"
                + "2. Wprowadź nowe dane w polach tekstowych \n"
                + "3. Kliknij 'edytuj'");
        labelVisibility(labelInfoClient);
    }

    @FXML
    private void deleteInstruction(ActionEvent event) {
        labelInfoClient.setText("1. Kliknij na wybranego klienta w tabeli \n"
                + "2. Kliknij usuń \n");
        labelVisibility(labelInfoClient);
    }

    @FXML
    private void editionInstruction1(ActionEvent event) {
        labelInfoWeapon.setText("1. Kliknij na wybranego markera w tabeli \n"
                + "2. Wprowadź nowe dane w comboboxach \n"
                + "3. Kliknij 'edytuj'");
        labelVisibility(labelInfoWeapon);
    }

    @FXML
    private void deleteInstruction1(ActionEvent event) {
        labelInfoWeapon.setText("1. Kliknij na wybranego markera w tabeli \n"
                + "2. Kliknij usuń \n");
        labelVisibility(labelInfoWeapon);
    }

    @FXML
    private void loadClientDataFromDatabase(ActionEvent event) {
        Transaction transaction = session.beginTransaction();
        List allClients = session.createCriteria(Client.class).list();
        transaction.commit();

        columnClientsName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnClientsSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        columnClientPesel.setCellValueFactory(new PropertyValueFactory<>("pesel"));
        columnClientId.setCellValueFactory(new PropertyValueFactory<>("id"));

        clientsTable.setItems(null);
        clientsTable.setItems(ObservableListClients);

        ObservableList clientsList = FXCollections.observableArrayList(allClients);
        clientsTable.setItems(clientsList);
    }

    @FXML
    private void deleteClient(ActionEvent event) {

        if (clientsTable.getSelectionModel().isEmpty()) {
            labelInfoClient.setText("Wybierz klienta!");
            labelVisibility(labelInfoClient);
            return;
        }
        Client client = clientsTable.getSelectionModel().getSelectedItem();
        try {

            deleteFromDatabase(client);

            textClientName.clear();
            textClientSurname.clear();
            textClientId.clear();
            labelInfoClient.setText("Usunięto klienta!");
            labelVisibility(labelInfoClient);
            loadOrderDataFromDatabase(null);
            refreshClientTable();
            refreshClientComboBox();
        } catch (RuntimeException e) {
            labelInfoClient.setText("Ten klient już złożył zamówienie. \n "
                    + "Nie można go usunąć!");
            labelVisibility(labelInfoClient);
        }
    }

    @FXML
    private void editClient(ActionEvent event) {

        String name = textClientName.getText();
        String surname = textClientSurname.getText();
        if (textClientId.getLength() > 11) {
            labelInfoClient.setText("PESEL musi składać się z wyłącznie z 11 cyfr!");
            labelVisibility(labelInfoClient);

        } else if (textClientId.getLength() < 11) {
            labelInfoClient.setText("PESEL musi składać się z wyłącznie z 11 cyfr!");
            labelVisibility(labelInfoClient);

        } else {
            long pesel = Long.parseLong(textClientId.getText());

            int selectedId = clientsTable.getSelectionModel().getSelectedItem().getId();
            Client clientEdit = new Client(selectedId, pesel, name, surname);
            clientEdit.setId(selectedId);

            updateObject(clientEdit);

            textClientName.clear();
            textClientSurname.clear();
            textClientId.clear();

            labelInfoClient.setText("Dane klienta zostały zmienione");
            labelVisibility(labelInfoClient);

            refreshClientTable();
            refreshClientComboBox();
            refreshOrdersTable();
        }
    }

    @FXML
    private void handleClient(MouseEvent event) {
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
    private void refreshClientTable() {

        Transaction transaction = session.beginTransaction();
        List allClients = session.createCriteria(Client.class).list();
        transaction.commit();

        ObservableList clientsList = FXCollections.observableArrayList(allClients);
        clientsTable.setItems(clientsList);
        clientsTable.refresh();
    }

    // ----------------------------------------------------------------------------------------------------------------------------
    // ============================================================================================================================
    @FXML
    private void loadDataFromWeaponDatabaseIntoCombobox(ActionEvent event) {

        Transaction transaction = session.beginTransaction();
        List allWeapons = session.createCriteria(Weapon.class).list();
        transaction.commit();

        ObservableList weaponsList = FXCollections.observableArrayList(allWeapons);
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
    private void refreshWeaponComboBox() {

        Transaction transaction = session.beginTransaction();
        List allWeapons = session.createCriteria(Weapon.class).list();
        transaction.commit();

        ObservableList weaponsList = FXCollections.observableArrayList(allWeapons);
        comboBoxWeapon.setItems(weaponsList);


    }

    @FXML
    private void loadDataFromClientDatabaseIntoCombobox(ActionEvent event) {

        Transaction transaction = session.beginTransaction();
        List allClients = session.createCriteria(Client.class).list();
        transaction.commit();

        ObservableList clientsList = FXCollections.observableArrayList(allClients);
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
    private void refreshClientComboBox() {

        Transaction transaction = session.beginTransaction();
        List allClients = session.createCriteria(Client.class).list();
        transaction.commit();

        ObservableList clientsList = FXCollections.observableArrayList(allClients);
        comboBoxClient.setItems(clientsList);


    }

    @FXML
    private void setOrderPrice() {

        if (comboBoxBullets.getItems() == null || comboBoxBullets.getItems().isEmpty() || comboBoxBullets.getSelectionModel().isEmpty()) {
            textFieldPrice.setText(null);
        } else {

            int price = comboBoxBullets.getSelectionModel().getSelectedItem();
            int price1 = (price / 100) * 20;
            String result = String.valueOf(price1);
            textFieldPrice.setText(result);
            textFieldPrice.setDisable(true);

        }
    }

    @FXML
    private void addOrder(ActionEvent event) {
        if (((comboBoxClient.getSelectionModel().getSelectedItem() == null) || (comboBoxClient.getSelectionModel().isEmpty())) || ((comboBoxWeapon.getSelectionModel().getSelectedItem() == null) || (comboBoxWeapon.getSelectionModel().isEmpty())) || ((comboBoxBullets.getSelectionModel().getSelectedItem() == null) || (comboBoxBullets.getSelectionModel().isEmpty()))) {
            labelInfoAdminOrders.setText("Proszę wypełnić wszystkie pola formularzu!");
            return;
        }
        Client client = comboBoxClient.getSelectionModel().getSelectedItem();
        Weapon weapon = comboBoxWeapon.getSelectionModel().getSelectedItem();
        Integer price = Integer.parseInt(textFieldPrice.getText());

        Order order = new Order(client, weapon, price, comboBoxBullets.getSelectionModel().getSelectedItem());

        saveToDatabase(order);

        comboBoxClient.getSelectionModel().clearSelection();
        comboBoxWeapon.getSelectionModel().clearSelection();
        comboBoxBullets.getSelectionModel().clearSelection();
        textFieldPrice.clear();
        labelInfoAdminOrders.setText("Dodano zamówienie!");
        labelVisibility(labelInfoAdminOrders);
        loadOrderDataFromDatabase(null);
    }


    @FXML
    private void loadOrderDataFromDatabase(ActionEvent event) {

        Transaction transaction = session.beginTransaction();
        List allOrders = session.createCriteria(Order.class).list();
        transaction.commit();


        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnBullets.setCellValueFactory(new PropertyValueFactory<>("bullets"));

        columnOrderClientName.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getClient().getName()));

        columnOrderClientSurname.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getClient().getSurname()));

        columnOrderClientId.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getClient().getPesel()));

        columnOrderBrand.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getWeapon().getBrand()));

        columnOrderType.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getWeapon().getType()));

        ordersTable.setItems(null);
        ordersTable.setItems(ObservableListOrders);

        ObservableList ordersList = FXCollections.observableArrayList(allOrders);
        ordersTable.setItems(ordersList);

    }

    @FXML
    private void deleteOrder(ActionEvent event) {
        Order order = ordersTable.getSelectionModel().getSelectedItem();
        deleteFromDatabase(order);

        labelInfoAdminOrders.setText("Usunięto zamówienie!");
        labelVisibility(labelInfoAdminOrders);
        loadOrderDataFromDatabase(null);
        refreshOrdersTable();
    }

    @FXML
    private void refreshOrdersTable() {

        Transaction transaction = session.beginTransaction();
        List allOrders = session.createCriteria(Order.class).list();
        transaction.commit();

        ObservableList ordersList = FXCollections.observableArrayList(allOrders);
        ordersTable.setItems(ordersList);
        ordersTable.refresh();
    }

}
