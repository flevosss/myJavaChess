package view.Panels;

import controller.GameController;
import model.*;
import network.client.Client;
import view.Dialogs.ServerConnectionDialog;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;

public class MainPanel extends JPanel {

    public static final int WIDTH = 1200;
    public static final int HEIGHT = 700;
    public static final int TILE_SIZE = 88;

    private enum ViewState { MENU, GAME }

    private CardLayout leftLayout;
    private JPanel leftCards;
    private JPanel menuBoardHolder;
    private JPanel gameBoardHolder;

    private JPanel rightPanel;
    private JPanel rightTopPanel;
    private JPanel rightBottomPanel;

    private CardLayout topRightLayout;
    private JPanel topRightCards;

    private GraphicsBoard currentBoardView;
    private boolean flipped = false;

    public MainPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        setUpLeftCards();
        setUpRightSide();

        setView(ViewState.MENU);
    }

    private void setUpLeftCards() {
        leftLayout = new CardLayout();
        leftCards = new JPanel(leftLayout);
        leftCards.setPreferredSize(new Dimension(8 * TILE_SIZE, 8 * TILE_SIZE));
        leftCards.setBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK)
        );

        menuBoardHolder = new JPanel(new BorderLayout());
        menuBoardHolder.add(
                new GraphicsBoard(new Board(8, 8), TILE_SIZE),
                BorderLayout.CENTER
        );

        gameBoardHolder = new JPanel(new BorderLayout());

        leftCards.add(menuBoardHolder, ViewState.MENU.toString());
        leftCards.add(gameBoardHolder, ViewState.GAME.toString());

        add(leftCards, BorderLayout.WEST);
    }

    private void setUpRightSide() {
        rightPanel = new JPanel(new BorderLayout());
        add(rightPanel, BorderLayout.CENTER);

        rightTopPanel = new JPanel(new BorderLayout());
        rightBottomPanel = new JPanel(new BorderLayout());

        rightTopPanel.setPreferredSize(
                new Dimension(WIDTH - (8 * TILE_SIZE), HEIGHT / 2)
        );
        rightTopPanel.setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK)
        );

        rightPanel.add(rightTopPanel, BorderLayout.NORTH);
        rightPanel.add(rightBottomPanel, BorderLayout.CENTER);

        setUpTopRightCards();
    }

    private void setUpTopRightCards() {
        topRightLayout = new CardLayout();
        topRightCards = new JPanel(topRightLayout);

        topRightCards.add(buildMenuTopRight(), ViewState.MENU.name());
        topRightCards.add(buildGameTopRight(), ViewState.GAME.name());

        rightTopPanel.add(topRightCards, BorderLayout.CENTER);
    }

    private JPanel buildMenuTopRight() {
        JPanel centered = new JPanel(new GridBagLayout());
        centered.add(getButtonsPanel());
        return centered;
    }

    private JPanel buildGameTopRight() {
        JButton back = new JButton("Back");
        back.addActionListener(e -> setView(ViewState.MENU));

        JButton flipBtn = new JButton("Flip");
        flipBtn.addActionListener(e -> {
            flipped = !flipped;
            if (currentBoardView != null) {
                currentBoardView.setFlipped(flipped);
            }
        });

        JPanel topBar = new JPanel(new BorderLayout());
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.add(flipBtn);
        rightButtons.add(back);

        topBar.add(rightButtons, BorderLayout.EAST);

        JLabel title = new JLabel("Moves", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(title, BorderLayout.CENTER);

        return panel;
    }


    private JPanel getButtonsPanel() {
        JLabel title = new JLabel("Choose your game type", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JButton online = new JButton("Online");
        JButton offline = new JButton("Offline");

        online.addActionListener(e -> startOnlineGame());
        offline.addActionListener(e -> startOfflineGame());

        Dimension size = new Dimension(160, 45);
        online.setPreferredSize(size);
        offline.setPreferredSize(size);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 10));
        buttons.add(online);
        buttons.add(offline);

        JPanel buttonsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonsWrapper.add(buttons);

        JPanel wrapper = new JPanel(new BorderLayout(0, 15));
        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(buttonsWrapper, BorderLayout.CENTER);

        return wrapper;
    }

    private void setView(ViewState state) {
        if (state == ViewState.MENU) {
            flipped = false;
            currentBoardView = null;

            menuBoardHolder.removeAll();
            menuBoardHolder.add(
                    new GraphicsBoard(new Board(8, 8), TILE_SIZE),
                    BorderLayout.CENTER
            );

            gameBoardHolder.removeAll();
            rightBottomPanel.removeAll();
        }

        leftLayout.show(leftCards, state.name());
        topRightLayout.show(topRightCards, state.name());

        revalidate();
        repaint();
    }

    private void startOfflineGame() {
        Player white = new Player("White", PieceColour.WHITE);
        Player black = new Player("Black", PieceColour.BLACK);
        Game game = new Game(white, black);

        GameController controller = new GameController(game);

        GraphicsBoard view = controller.getView();
        currentBoardView = view;


        gameBoardHolder.removeAll();
        gameBoardHolder.add(controller.getView(), BorderLayout.CENTER);

        setView(ViewState.GAME);
    }

    private void startOnlineGame() {
        ConnectionInfo info = ServerConnectionDialog.showDialog(this, null);
        if (info == null) return;

        try {
            Client client = new Client();
            InetAddress address = InetAddress.getByName(info.getServerAddress());
            client.initializeClient(address, info.port, info.username);

            setView(ViewState.GAME);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Connection failed:\n" + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("MyChess");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            window.setContentPane(new MainPanel());
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}