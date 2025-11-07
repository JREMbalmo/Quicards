package com.quiboysstudio.quicards.server;

//imports
import com.quiboysstudio.quicards.server.listener.HostListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ServerHostClient {
    //variables
    private static String username;
    private static String password;
    private static String url;
    private static String ip;
    private static String port;
    private static boolean hosting = false;
    
    //objects
    private static Connection connection;
    private static Statement statement;
    private static ResultSet result;
    private static HostListener listener;
    
    private static JFrame serverHostClientFrame;
    private static JScrollPane logsPane;
    private static JScrollPane usersPane;
    private static JPanel tablesPanel;
    private static JPanel commandPanel;
    private static JPanel usersPanel;
    private static JPanel exportPanel;
    private static JPanel logsPanel;
    private static JTable logsTable;
    private static JTable usersTable;
    private static JButton exportLogsButton;
    private static JButton runCommandButton;
    private static JTextField commandField;
    
    public static void setServer(String ip, String port, String username, String password) {
        if (hosting) {
            return;
        }
        
        url = String.format("jdbc:mysql://%s:%s/?zeroDateTimeBehavior=CONVERT_TO_NULL", ip, port);
            
        ServerHostClient.ip = ip;
        ServerHostClient.port = port;
        ServerHostClient.username = username;
        ServerHostClient.password = password;
    }
    
    public static boolean connectServer() {
        
        if (hosting) {
            JOptionPane.showMessageDialog(null, "Already hosting a server");
            return false;
        }
        
        try {
            //connect to server
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            
            //ensure Server database setup
            checkServer();
            connection.close();
            
            //connect to server using Server database
            url = String.format("jdbc:mysql://%s:%s/Server?zeroDateTimeBehavior=CONVERT_TO_NULL", ip, port);
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            
            //check if someone is already hosting the server
            if (checkHost()) {
                leaveServer();
                return false;
            }
            
            //check account creator user setup
            checkCreationUser();
            
            //start server
            initHostedServer();
            runHostedServer();
            
            hosting = true;
            System.out.println("Connected to server!");
            
            return true;
        } catch(Exception e) {
            System.out.println("Failed to connect to server: " + e);
            JOptionPane.showMessageDialog(null, "Can't connect to server");
            return false;
        }
    }
        
    public static void leaveServer() {
        //variables
        String query;
        
        //reset server host
        query = "update ServerDetails set CurrentHost = null;";
        
        try {
            statement.executeUpdate(query);
            connection.close();
        } catch (Exception e) {
            System.out.println("Failed to reset server host: " + e);
        }
        
        //reset saved host server details
        ip = null;
        port = null;
        url = null;
        username = null;
        password = null;
        result = null;
        statement = null;
        connection = null;
        hosting = false;
        
        //stop host thread if active
        if (listener.isRunning()) listener.stop();
        
        System.out.println("Stopped Hosting Server");
    }
    
    private static void initHostedServer() {
        //frame
        serverHostClientFrame = new JFrame("Admin Menu (" + ip + ")");
        serverHostClientFrame.setSize(1280, 720);
        serverHostClientFrame.setResizable(false);
        serverHostClientFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        serverHostClientFrame.setLayout(new BorderLayout());
        serverHostClientFrame.setLocationRelativeTo(null);
        serverHostClientFrame.setIconImage(new ImageIcon("resources//logos//game_logo_appicon.png").getImage());
        
        //custom close operation to ensure proper server disconnection
        serverHostClientFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JOptionPane.showMessageDialog(null, "Exiting server " + ip);
                serverHostClientFrame.dispose();
                leaveServer();
            }
        });
        
        //panels
        
        //tables panel
        tablesPanel = new JPanel();
        tablesPanel.setPreferredSize(new Dimension(1280,500));
        
        //command panel
        commandPanel = new JPanel();
        commandPanel.setPreferredSize(new Dimension(1280, 150));
        
        //logs panel
        logsPanel = new JPanel();
        logsPanel.setPreferredSize(new Dimension (1000, 480));
        
        //users panel
        usersPanel = new JPanel();
        usersPanel.setPreferredSize(new Dimension (200, 480));
        
        //export panel
        exportPanel = new JPanel();
        exportPanel.setPreferredSize(new Dimension(250,150));
        exportPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        
        //setup tables
        
        //logs table
        DefaultTableModel model = new DefaultTableModel(new Object[][] {},
                new String[] {"Action ID", "User", "Action", "Status"});
        logsTable = new JTable(model);
        logsTable.setPreferredSize(new Dimension(1000, 480));
        logsPane = new JScrollPane(logsTable);
        logsPane.setPreferredSize(new Dimension(1000, 480));
        
        //users table
        model = new DefaultTableModel(new Object[][] {}, new String[] {"Active Users"});
        usersTable = new JTable(model);
        usersTable.setPreferredSize(new Dimension(200, 480));
        usersPane = new JScrollPane(usersTable);
        usersPane.setPreferredSize(new Dimension(200, 480));
        
        //command text field
        commandField = new JTextField();
        commandField.setBackground(Color.white);
        commandField.setPreferredSize(new Dimension (1000, 18));
        
        //buttons
        
        //run command button
        runCommandButton = new JButton("Run");
        runCommandButton.setPreferredSize(new Dimension(100, 18));
        runCommandButton.addActionListener(e -> {
            runCommand(String.valueOf(commandField.getText()));
            commandField.setText(null);
        });
        
        //export logs button
        exportLogsButton = new JButton("Export Logs");
        exportLogsButton.setPreferredSize(new Dimension(200, 18));
        exportLogsButton.addActionListener(e -> {
            exportLogs();
        });
        
        //listener
        listener = new HostListener(result, statement);
        
        //add components
        exportPanel.add(exportLogsButton);
        
        commandPanel.add(commandField);
        commandPanel.add(runCommandButton);
        commandPanel.add(exportPanel);
        
        logsPanel.add(logsPane);
        usersPanel.add(usersPane);
        
        tablesPanel.add(logsPanel);
        tablesPanel.add(usersPanel);
        
        serverHostClientFrame.add(tablesPanel, BorderLayout.NORTH);
        serverHostClientFrame.add(commandPanel, BorderLayout.SOUTH);
    }
    
    private static void runHostedServer() {
        serverHostClientFrame.setVisible(true);
        listener.start();
    }
    
    private static void checkServer() {
        try {
            //check if Server database exists on server
            result = statement.executeQuery(
                    "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = 'Server'");
            
            if (!result.next()) {
                
                //create Server database if it doesn't exist on server
                statement.executeUpdate(
                        "CREATE DATABASE Server;"
                );
                
                statement.executeUpdate(
                        "USE Server;"
                );
                
                //create ServerDetails table
                statement.executeUpdate(
                        "CREATE TABLE ServerDetails (" +
                        "ID TINYINT(1) NOT NULL PRIMARY KEY DEFAULT 1," +
                        "ServerName VARCHAR(32)," +
                        "CurrentHost VARCHAR(128)," +
                        "CONSTRAINT ck_single_row CHECK (ID = 1)" +
                        ");"
                );
                
                //create Users table
                statement.executeUpdate(
                        "CREATE TABLE Users (" +
                        "UserID INT PRIMARY KEY AUTO_INCREMENT," +
                        "Username VARCHAR(16) UNIQUE NOT NULL," +
                        "Password TEXT NOT NULL," +
                        "Seed BIGINT NOT NULL," +
                        "Money INT NOT NULL DEFAULT 5000," +
                        "Rolls INT NOT NULL DEFAULT 0" +
                        ") AUTO_INCREMENT = 100000;"
                );
                
                //create Actions table
                statement.executeUpdate(
                        "CREATE TABLE Actions (" +
                        "ActionsID INT PRIMARY KEY AUTO_INCREMENT," +
                        "UserID INT NOT NULL," +
                        "Password TEXT NOT NULL," +
                        "Action VARCHAR(128) NOT NULL," +
                        "Processed TINYINT(1) NOT NULL DEFAULT 0," +
                        "Valid TINYINT(1) NOT NULL," +
                        "FOREIGN KEY (UserID) REFERENCES Users(UserID)" +
                        ") AUTO_INCREMENT = 1;"
                );
                
                //create AccountCreation table
                statement.executeUpdate(
                        "CREATE TABLE AccountCreation (" +
                        "CreationID INT PRIMARY KEY AUTO_INCREMENT," +
                        "Username VARCHAR(16) UNIQUE NOT NULL," +
                        "Password TEXT NOT NULL," +
                        "Processed TINYINT(1) NOT NULL DEFAULT 0" +
                        ") AUTO_INCREMENT = 1;"
                );
                
                //create RollResults table
                statement.executeUpdate(
                        """
                        CREATE TABLE RollResults (
                        RollID INT PRIMARY KEY AUTO_INCREMENT,
                        UserID INT NOT NULL,
                        Result INT NOT NULL,
                        FOREIGN KEY (UserID) REFERENCES Users(UserID)
                        ) AUTO_INCREMENT = 1;
                        """
                );
                
                //create strategy table
                statement.executeUpdate(
                        """
                        CREATE TABLE Strategies(
                        StrategyID INT PRIMARY KEY AUTO_INCREMENT,
                        Name TEXT NOT NULL
                        ) AUTO_INCREMENT = 1;
                        """
                );
                
                //create rarity table
                statement.executeUpdate(
                        """
                        CREATE TABLE Rarity(
                        RarityID INT PRIMARY KEY AUTO_INCREMENT,
                        Name TEXT NOT NULL
                        ) AUTO_INCREMENT = 1;
                        """
                );                
                
                //create cards table
                statement.executeUpdate(
                        """
                        CREATE TABLE Cards(
                        CardID INT PRIMARY KEY AUTO_INCREMENT,
                        Name TEXT NOT NULL
                        ) AUTO_INCREMENT = 1;
                        """
                );
                
                //create card rarity table
                statement.executeUpdate(
                        """
                        CREATE TABLE CardRarity(
                        CardID INT NOT NULL,
                        RarityID INT NOT NULL,
                        FOREIGN KEY (CardID) REFERENCES Cards(CardID),
                        FOREIGN KEY (RarityID) REFERENCES Rarity(RarityID)
                        );
                        """
                );                
                
                //create card stats table
                statement.executeUpdate(
                        """
                        CREATE TABLE CardStats(
                        CardID INT NOT NULL,
                        Attack INT NOT NULL,
                        Health INT NOT NULL,
                        StrategyID INT NOT NULL,
                        FOREIGN KEY (CardID) REFERENCES Cards(CardID),
                        FOREIGN KEY (StrategyID) REFERENCES Strategies(StrategyID)
                        );
                        """
                );
                
                //create owned cards table
                statement.executeUpdate(
                        """
                        CREATE TABLE OwnedCards(
                        OwnershipID INT PRIMARY KEY AUTO_INCREMENT,
                        UserID INT NOT NULL,
                        CardID INT NOT NULL,
                        FOREIGN KEY (UserID) REFERENCES Users(UserID),
                        FOREIGN KEY (CardID) REFERENCES Cards(CardID)
                        ) AUTO_INCREMENT = 1;
                        """
                );
                
                //create packs table
                statement.executeUpdate(
                        """
                        CREATE TABLE Packs(
                        PackID INT PRIMARY KEY AUTO_INCREMENT,
                        Name TEXT NOT NULL,
                        Price INT NOT NULL
                        ) AUTO_INCREMENT = 1;
                        """
                );
                
                //create pack contents table
                statement.executeUpdate(
                        """
                        CREATE TABLE PackContents(
                        PackID INT NOT NULL,
                        CardID INT NOT NULL,
                        FOREIGN KEY (CardID) REFERENCES Cards(CardID),
                        FOREIGN KEY (PackID) REFERENCES Packs(PackID)
                        );
                        """
                );
                
                //create decks table
                statement.executeUpdate(
                        """
                        CREATE TABLE Decks(
                        DeckID INT PRIMARY KEY AUTO_INCREMENT,
                        UserID INT NOT NULL,
                        Name TEXT NOT NULL,
                        FOREIGN KEY (UserID) REFERENCES Users(UserID)
                        ) AUTO_INCREMENT = 1;
                        """
                );
                
                //create deck contents table
                statement.executeUpdate(
                        """
                        CREATE TABLE DeckContents(
                        DeckID INT NOT NULL,
                        CardID INT NOT NULL,
                        FOREIGN KEY (DeckID) REFERENCES Decks(DeckID),
                        FOREIGN KEY (CardID) REFERENCES Cards(CardID)
                        );
                        """
                );
                
                //create rooms table
                statement.executeUpdate(
                        """
                        CREATE TABLE Rooms(
                        RoomID INT PRIMARY KEY AUTO_INCREMENT,
                        User1 INT NOT NULL,
                        Deck1 INT,
                        User2 INT NOT NULL,
                        Deck2 INT,
                        FOREIGN KEY (User1) REFERENCES Users(UserID),
                        FOREIGN KEY (User2) REFERENCES Users(UserID),
                        FOREIGN KEY (Deck1) REFERENCES Decks(DeckID),
                        FOREIGN KEY (Deck2) REFERENCES Decks(DeckID)
                        ) AUTO_INCREMENT = 1;
                        """
                );
                
                //create card state
                statement.executeUpdate(
                        """
                        CREATE TABLE CardStates(
                        StateID INT PRIMARY KEY,
                        RoomID INT NOT NULL,
                        OwnershipID INT NOT NULL,
                        CurrentAttack INT NOT NULL,
                        CurrentHealth INT NOT NULL,
                        FOREIGN KEY (RoomID) REFERENCES Rooms(RoomID),
                        FOREIGN KEY (OwnershipID) REFERENCES OwnedCards(OwnershipID)
                        );
                        """
                );
                
                //create board state table
                statement.executeUpdate(
                        """
                        CREATE TABLE BoardState(
                        RoomID INT PRIMARY KEY,
                        P1Health INT DEFAULT 100,
                        P1LeftCard INT,
                        P1MidCard INT,
                        P2Health INT DEFAULT 100,
                        P1RightCard INT,
                        P2LeftCard INT,
                        P2MidCard INT,
                        P2RightCard INT,
                        FOREIGN KEY (RoomID) REFERENCES Rooms(RoomID),
                        FOREIGN KEY (P1LeftCard) REFERENCES CardStates(StateID),
                        FOREIGN KEY (P1MidCard) REFERENCES CardStates(StateID),
                        FOREIGN KEY (P1RightCard) REFERENCES CardStates(StateID),
                        FOREIGN KEY (P2LeftCard) REFERENCES CardStates(StateID),
                        FOREIGN KEY (P2MidCard) REFERENCES CardStates(StateID),
                        FOREIGN KEY (P2RightCard) REFERENCES CardStates(StateID)
                        );
                        """
                );
                
                //insert strategies
                statement.executeUpdate(
                        """
                        INSERT INTO Strategies(Name) Values("BasicATK");
                        """
                );
                statement.executeUpdate(
                        """
                        INSERT INTO Strategies(Name) Values("BasicDEF");
                        """
                );
                statement.executeUpdate(
                        """
                        INSERT INTO Strategies(Name) Values("SplitATK");
                        """
                );
                statement.executeUpdate(
                        """
                        INSERT INTO Strategies(Name) Values("SplitDEF");
                        """
                );
                
                //insert rarities
                statement.executeUpdate(
                        """
                        INSERT INTO Rarity(Name) Values("Common");
                        """
                );
                statement.executeUpdate(
                        """
                        INSERT INTO Rarity(Name) Values("Rare");
                        """
                );
                statement.executeUpdate(
                        """
                        INSERT INTO Rarity(Name) Values("Epic");
                        """
                );
                statement.executeUpdate(
                        """
                        INSERT INTO Rarity(Name) Values("Legendary");
                        """
                );
                
                //add card packs
        try {

            File cardsDir = new File("resources/cards");
            File[] packFolders = cardsDir.listFiles(File::isDirectory);

            if (packFolders == null) {
                System.out.println("No pack folders found.");
                return;
            }

            for (File packFolder : packFolders) {

                String packName = packFolder.getName();
                System.out.println("Processing pack: " + packName);

                File infoFile = new File(packFolder, "info.txt");
                File priceFile = new File(packFolder, "price.txt");

                if (!infoFile.exists() || !priceFile.exists()) {
                    System.out.println("Missing info.txt or price.txt in " + packName);
                    continue;
                }

                // ✅ Step 1 — Extract Pack Price
                int packPrice = Integer.parseInt(Files.readString(priceFile.toPath()).trim());

                // ✅ Step 2 — Insert Pack into Packs table
                String insertPackSQL =
                        "INSERT INTO Packs (Name, Price) VALUES ('" + packName + "', " + packPrice + ")";
                statement.executeUpdate(insertPackSQL, Statement.RETURN_GENERATED_KEYS);

                ResultSet rsPackID = statement.getGeneratedKeys();
                int packID = (rsPackID.next()) ? rsPackID.getInt(1) : -1;

                if (packID == -1) {
                    System.out.println("Failed to insert pack: " + packName);
                    continue;
                }

                // Store IDs of cards from this pack to insert into PackContents later
                List<Integer> cardIDs = new ArrayList<>();

                // ✅ Step 3 — Read info.txt and insert all card details
                List<String> lines = Files.readAllLines(infoFile.toPath());

                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;

                    String[] data = line.split(",");
                    if (data.length != 5) {
                        System.out.println("Invalid card format in " + infoFile);
                        continue;
                    }

                    String name = data[0].trim();
                    String rarity = data[1].trim();
                    String strategy = data[2].trim();
                    int atk = Integer.parseInt(data[3].trim());
                    int hp = Integer.parseInt(data[4].trim());

                    // ✅ Insert Card Name
                    String insertCardSQL =
                            "INSERT INTO Cards (Name) VALUES ('" + name + "')";
                    statement.executeUpdate(insertCardSQL, Statement.RETURN_GENERATED_KEYS);

                    ResultSet rsCardID = statement.getGeneratedKeys();
                    int cardID = (rsCardID.next()) ? rsCardID.getInt(1) : -1;

                    if (cardID == -1) continue;

                    cardIDs.add(cardID);

                    // ✅ Insert Rarity
                    // Here we assume your Rarity table already contains the rarity names mapping to IDs  
                    String rarityIDSQL = "SELECT RarityID FROM Rarity WHERE Name = '" + rarity + "'";
                    ResultSet rsRarityID = statement.executeQuery(rarityIDSQL);

                    int rarityID = (rsRarityID.next()) ? rsRarityID.getInt("RarityID") : 1;

                    String insertCardRaritySQL =
                            "INSERT INTO CardRarity (CardID, RarityID) VALUES (" + cardID + ", " + rarityID + ")";
                    statement.executeUpdate(insertCardRaritySQL);

                    // ✅ Insert Strategy
                    String strategyIDSQL = "SELECT StrategyID FROM Strategies WHERE Name = '" + strategy + "'";
                    ResultSet rsStrategyID = statement.executeQuery(strategyIDSQL);

                    int strategyID = (rsStrategyID.next()) ? rsStrategyID.getInt("StrategyID") : 1;

                    // ✅ Insert Stats
                    String insertStatsSQL =
                            "INSERT INTO CardStats (CardID, Attack, Health, StrategyID) VALUES (" +
                                    cardID + ", " + atk + ", " + hp + ", " + strategyID + ")";
                    statement.executeUpdate(insertStatsSQL);
                }

                // ✅ Step 4 — Insert Pack Contents
                for (Integer cid : cardIDs) {
                    String insertContentsSQL =
                            "INSERT INTO PackContents (PackID, CardID) VALUES (" + packID + ", " + cid + ")";
                    statement.executeUpdate(insertContentsSQL);
                }

                System.out.println("Pack '" + packName + "' imported successfully.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//                Files.list(Paths.get("resources/cards")).filter(Files::isDirectory).forEach(packDir -> {
//                try {
//                // 1. Read pack name
//                String packName = packDir.getFileName().toString();
//
//                // 2. Read pack price
//                int price = Integer.parseInt(
//                Files.readString(packDir.resolve("price.txt")).trim()
//                );
//
//                // 3. Insert into Packs table
//                statement.executeUpdate("INSERT INTO Packs(Name, Price) VALUES ('" + packName + "', " + price + ")");
//
//                // 4. Retrieve generated PackID
//                ResultSet rsPack = statement.executeQuery("SELECT PackID FROM Packs ORDER BY PackID DESC LIMIT 1");
//                rsPack.next();
//                int packID = rsPack.getInt(1);
//
//                // 5. Read cards from info.txt
//                List<Integer> cardIds = new ArrayList<>();
//                List<String> lines = Files.readAllLines(packDir.resolve("info.txt"));
//
//                for (String line : lines) {
//                if (line.isBlank()) continue;
//                String[] data = line.split(",");
//                String name = data[0];
//                String rarity = data[1];
//                String strategy = data[2];
//                int atk = Integer.parseInt(data[3]);
//                int hp = Integer.parseInt(data[4]);
//
//                // Find strategy ID
//                ResultSet rsStrat = statement.executeQuery(
//                "SELECT StrategyID FROM Strategies WHERE Name='" + strategy + "' LIMIT 1"
//                );
//                rsStrat.next();
//                int stratID = rsStrat.getInt(1);
//
//                // Insert card
//                statement.executeUpdate(
//                "INSERT INTO Cards(Name, Rarity, Attack, Health, StrategyID) VALUES ('" + name + "', '" + rarity + "', " + atk + ", " + hp + ", " + stratID + ")"
//                );
//
//                // Retrieve CardID
//                ResultSet rsCard = statement.executeQuery("SELECT CardID FROM Cards ORDER BY CardID DESC LIMIT 1");
//                rsCard.next();
//                cardIds.add(rsCard.getInt(1));
//                }
//
//                // 6. Insert into PackContents
//                for (int cardID : cardIds) {
//                statement.executeUpdate(
//                "INSERT INTO PackContents(PackID, CardID) VALUES (" + packID + ", " + cardID + ")"
//                );
//                }
//
//                } catch (Exception e) {
//                e.printStackTrace();
//                }
//                });
                
            }
        } catch (Exception e) {
            System.out.println("Failed to check server: " + e);
        }
    }
    
    //wip
    private static void runCommand(String command) {
        JOptionPane.showMessageDialog(null, command);
    }
    
    //wip
    private static void exportLogs() {
        JOptionPane.showMessageDialog(null, "Exporting Logs");
    }
    
    private static void checkCreationUser() {
        String query;
            try {
                //check if user creator exists
                query = "SELECT COUNT(*) AS count " +
                    "FROM mysql.user WHERE user = 'QuiCardsCreator1'";

                result = statement.executeQuery(query);
                if (result.next() && result.getInt("count") == 0) {
                    //create user creator if it doesn't exist
                    query = "CREATE USER 'QuiCardsCreator1'@'%' IDENTIFIED BY 'QuiC4rds!';";
                    statement.executeUpdate(query);

                    //grant privileges
                    query = "GRANT INSERT ON Server.AccountCreation TO 'QuiCardsCreator1'@'%'";
                    statement.executeUpdate(query);
                    query = "GRANT SELECT ON Server.ServerDetails TO 'QuiCardsCreator1'@'%'";
                    statement.executeUpdate(query);
                    query = "GRANT PROCESS ON *.* TO 'QuiCardsCreator1'@'%'";
                    statement.executeUpdate(query);

                    // --- NEWLY ADDED ---
                    query = "GRANT SELECT ON Server.Rooms TO 'QuiCardsCreator1'@'%'";
                    statement.executeUpdate(query);
                    // --- END NEW ---

                    statement.executeUpdate("FLUSH PRIVILEGES");

                    System.out.println("QuiCardsCreator1 user created and granted privileges.");
                } else {
                    System.out.println("QuiCardsCreator1 already exists.");

                    // --- ADDED FOR EXISTING USER ---
                    // Grant select on Rooms just in case the user exists but lacks permission
                    query = "GRANT SELECT ON Server.Rooms TO 'QuiCardsCreator1'@'%'";
                    statement.executeUpdate(query);
                    statement.executeUpdate("FLUSH PRIVILEGES");
                    // --- END ---
                }
            } catch (Exception e) {
                System.out.println("Failed to check or create QuiCardsAccountCreator: " + e);
            }
        
    }
    
    private static boolean checkHost() {
        String query;
        String user;
        String ip;

        try {
            //check if there is already a stored host
            result = statement.executeQuery("SELECT CurrentHost FROM ServerDetails LIMIT 1");

            if (!result.next()) {
                //if no row then insert user@ip
                query = "SELECT USER();";
                result = statement.executeQuery(query);

                if (result.next()) {
                    String actualUser = result.getString(1);
                    query = String.format(
                        "INSERT INTO ServerDetails(CurrentHost) VALUES ('%s')",
                        actualUser
                    );
                    statement.executeUpdate(query);
                }
                return false;
            } else if (result.getString("CurrentHost") == null) {
                //if row exists but current host is null then update current host
                query = "SELECT USER();";
                result = statement.executeQuery(query);

                if (result.next()) {
                    String actualUser = result.getString(1);
                    query = String.format(
                        "UPDATE ServerDetails SET CurrentHost = '%s'",
                        actualUser
                    );
                    statement.executeUpdate(query);
                }
                return false;
            }

            String hostEntry = result.getString("CurrentHost");

            // get user@ip
            query = "SELECT USER();";
            result = statement.executeQuery(query);

            if (result.next()) {
                String actualUser = result.getString(1);
                if (hostEntry.equalsIgnoreCase(actualUser)) {
                    //if host matches current connection, refresh stored host
                    query = String.format(
                        "UPDATE ServerDetails SET CurrentHost = '%s'",
                        actualUser
                    );
                    statement.executeUpdate(query);
                    return false;
                }
            }

            String[] parts = hostEntry.split("@");
            user = parts[0].trim();
            ip = parts[1].trim();

            //check if stored host is currently connected
            query = String.format(
                "SELECT user, host FROM information_schema.PROCESSLIST " +
                "WHERE user = '%s' AND host LIKE '%s%%';",
                user, ip
            );

            result = statement.executeQuery(query);
            return result.next();

        } catch (Exception e) {
            System.out.println("Failed to check host: " + e);
        }

        return false; //default
    }
}