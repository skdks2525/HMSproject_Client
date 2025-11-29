package client.view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import client.model.Cart;
import client.model.Menu;
import client.net.NetworkService;


/**
 * 고객용 룸서비스 키오스크 프레임
 * - 서버에서 판매중인 메뉴만 불러와 카드 형태로 표시
 * - 장바구니에 메뉴 추가, 결제, 결제 후 재고 반영 등 전체 주문 프로세스 담당
 * - 모든 서버 통신은 NetworkService를 통해 이루어짐
 */
public class MenuKioskModernFrame extends JFrame {
    private final String userId;

    // 메뉴 카드가 표시될 패널
    private JPanel menuListPanel;
    // 장바구니 패널
    private JPanel cartPanel;
    private DefaultListModel<String> cartListModel;
    private JLabel totalLabel;
    private JButton payButton;
    // 장바구니 객체(메뉴 담기/비우기/총액 계산)
    private final Cart cart = new Cart(new ArrayList<>());

    /**
     * 프레임 생성자: UI 초기화 및 메뉴 카드 로드
     * - 서버에서 메뉴 목록을 받아와 화면에 표시
     * - 장바구니/결제 기능 연결
     */
    public MenuKioskModernFrame(String userId) {
        this.userId = userId;
        setTitle("룸서비스 주문");
        setSize(1200, 700); // 가로폭 확장
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 20));

        add(createHeader(), BorderLayout.NORTH);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createMenuScrollPanel(), BorderLayout.CENTER);
        mainPanel.add(createCartPanel(), BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);

        loadMenuCards(); // 메뉴 카드 동적 생성 및 표시

        setVisible(true);
    }

    // ===============================
    // 상단 제목
    // ===============================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(18, 18, 8, 18));

        // 뒤로가기 버튼 제거

        // 중앙 제목
        JLabel title = new JLabel("룸서비스 주문");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        title.setForeground(new Color(10, 48, 87));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(title, BorderLayout.CENTER);

        header.add(Box.createHorizontalStrut(30), BorderLayout.WEST);
        return header;
    }

    // ===============================
    // 스크롤 가능한 메뉴 패널
    // ===============================
    private JScrollPane createMenuScrollPanel() {
        menuListPanel = new JPanel();
        menuListPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(menuListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Color.WHITE);
        return scrollPane;
    }

    // ===============================
    // 우측 장바구니 패널
    private JPanel createCartPanel() {
        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setPreferredSize(new Dimension(270, 0));
        cartPanel.setBackground(Color.WHITE);
        cartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel cartTitle = new JLabel("장바구니");
        cartTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        cartTitle.setForeground(new Color(10, 48, 87));
        cartTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartPanel.add(cartTitle);
        cartPanel.add(Box.createVerticalStrut(10));

        cartListModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartListModel);
        cartList.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        JScrollPane cartScroll = new JScrollPane(cartList);
        cartScroll.setPreferredSize(new Dimension(220, 220));
        cartPanel.add(cartScroll);

        totalLabel = new JLabel("총액: 0원");
        totalLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        totalLabel.setForeground(new Color(10, 48, 87));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartPanel.add(Box.createVerticalStrut(10));
        cartPanel.add(totalLabel);

        payButton = new JButton("결제");
        payButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        payButton.setMaximumSize(new Dimension(200, 40));
        payButton.setBackground(new Color(10, 48, 87));
        payButton.setForeground(Color.WHITE);
        payButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        payButton.setFocusPainted(false);
        payButton.addActionListener(e -> showPaymentOptions());
        cartPanel.add(Box.createVerticalStrut(10));
        cartPanel.add(payButton);

        return cartPanel;
    }

    // ===============================
    // ...existing code...
    /**
     * 서버에서 판매중인 메뉴 목록을 받아와 카드 형태로 동적으로 패널에 추가
     * - GET_MENUS 요청 후, 응답이 정상(MENU_LIST:)이 아니면 에러 메시지 출력
     * - 각 메뉴 info: [0]=ID, [1]=이름, [2]=가격, [3]=카테고리, [4]=판매여부, [5]=재고
     * - 판매중이 아니거나 재고가 0 이하인 메뉴는 표시하지 않음
     * - 각 메뉴는 createMenuCard로 카드 생성 후 패널에 추가
     */
    private void loadMenuCards() {
        menuListPanel.removeAll();
        String response = NetworkService.getInstance().sendRequest("GET_MENUS");
        if (response == null || !response.startsWith("MENU_LIST:")) {
            JOptionPane.showMessageDialog(this, "메뉴 로딩 실패!");
            return;
        }
        String data = response.substring("MENU_LIST:".length());
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "메뉴가 없습니다.");
            return;
        }
        String[] menus = data.split("/");
        java.util.List<Menu> menuObjs = new java.util.ArrayList<>();
        for (String m : menus) {
            String[] info = m.split(",");
            if (info.length < 6) continue;
            String menuid = info[0];
            String name = info[1];
            int price;
            int stock;
            try {
                price = Integer.parseInt(info[2]);
                stock = Integer.parseInt(info[5]);
            } catch (NumberFormatException e) {
                continue;
            }
            String category = info[3];
            boolean isAvailable = Boolean.parseBoolean(info[4]);
            if (!isAvailable || stock <= 0) continue;
            menuObjs.add(new Menu(menuid, name, price, category, isAvailable, stock));
        }
        // 3x3 고정 또는 3열 자동 스크롤
        int colCount = 3;
        if (menuObjs.size() <= 9) {
            menuListPanel.setLayout(new GridLayout(3, 3, 20, 20));
            for (Menu menu : menuObjs) {
                menuListPanel.add(createMenuCard(menu));
            }
            // 빈 칸 채우기
            int empty = 9 - menuObjs.size();
            for (int i = 0; i < empty; i++) {
                menuListPanel.add(Box.createGlue());
            }
        } else {
            int rowCount = (int)Math.ceil(menuObjs.size() / (double)colCount);
            menuListPanel.setLayout(new GridLayout(rowCount, colCount, 20, 20));
            for (Menu menu : menuObjs) {
                menuListPanel.add(createMenuCard(menu));
            }
            // 마지막 줄 빈 칸 채우기
            int empty = rowCount * colCount - menuObjs.size();
            for (int i = 0; i < empty; i++) {
                menuListPanel.add(Box.createGlue());
            }
        }
        menuListPanel.revalidate();
        menuListPanel.repaint();
    }

    // ===============================
        // ===============================
    // 메뉴 객체 기반 카드 생성
    /**
     * 단일 메뉴 정보를 카드 형태의 JPanel로 생성
     * - 메뉴명, 가격, 재고 표시
     * - '담기' 버튼 클릭 시 장바구니에 해당 메뉴 추가
     * - 재고 0이거나 판매중지 메뉴는 버튼 비활성화
     * @param menu 메뉴 정보 객체
     * @return 메뉴 카드 패널
     */
    private JPanel createMenuCard(Menu menu) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(180, 220));
        card.setMaximumSize(new Dimension(180, 220));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(10, 48, 87), 1, true),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setOpaque(true);

        JPanel imgBox = new JPanel();
        imgBox.setPreferredSize(new Dimension(80, 80));
        imgBox.setMaximumSize(new Dimension(80, 80));
        imgBox.setMinimumSize(new Dimension(80, 80));
        imgBox.setBackground(new Color(230, 236, 245));
        imgBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel imgLabel = new JLabel("IMG");
        imgLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        imgLabel.setForeground(new Color(120, 120, 120));
        imgBox.add(imgLabel);
        card.add(imgBox);

        card.add(Box.createVerticalStrut(10));

        JLabel nameLabel = new JLabel(menu.getName());
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(new Color(10, 48, 87));
        card.add(nameLabel);

        JLabel priceLabel = new JLabel(menu.getPrice() + "원");
        priceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        priceLabel.setForeground(new Color(10, 48, 87));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(priceLabel);

        card.add(Box.createVerticalStrut(5));

        JButton addButton = new JButton("담기");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setEnabled(menu.getStock() > 0 && menu.getIsAvailable());
        addButton.setBackground(new Color(10, 48, 87));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> {
            cart.addItem(menu);
            updateCartPanel();
            JOptionPane.showMessageDialog(this, menu.getName() + "를 장바구니에 담았습니다.");
        });
        card.add(addButton);

        card.add(Box.createVerticalStrut(5));

        JLabel stockLabel = new JLabel("재고: " + menu.getStock());
        stockLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        stockLabel.setForeground(new Color(120,120,120));
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(stockLabel);

        return card;
    }


    // 결제 옵션 선택 대화상자
    private void showPaymentOptions() {
        if (cart.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "장바구니가 비어있습니다.");
            return;
        }

        String[] options = {"지금 결제", "나중에 결제 (체크아웃 시)"};
        int choice = JOptionPane.showOptionDialog(this,
            "결제 방식을 선택하세요.",
            "결제 옵션",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

        if (choice == 0) {
            handleImmediatePay();
        } else if (choice == 1) {
            handleDeferredPay();
        }
    }

    // 지금 결제 (즉시 결제)
    private void handleImmediatePay() {
        if (cart.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "장바구니가 비어있습니다.");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "결제 하시겠습니까?", "결제 확인", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            String req = String.format("ORDER_MENU:%s:%d:%s:%s", userId, cart.getTotalPrice(), "Paid", cart.getFoodNamesString());
            NetworkService.getInstance().sendRequest(req);
            JOptionPane.showMessageDialog(this, "요금이 추가되었습니다.");
            cart.clear();
            updateCartPanel();
            loadMenuCards(); // 결제 후 재고 반영
        }
    }

    // 나중에 결제 (체크아웃 시)
    private void handleDeferredPay() {
        if (cart.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "장바구니가 비어있습니다.");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "체크아웃 시 결제하시겠습니까?", "나중 결제 확인", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            String req = String.format("ORDER_MENU:%s:%d:%s:%s", userId, cart.getTotalPrice(), "Unpaid", cart.getFoodNamesString());
            NetworkService.getInstance().sendRequest(req);
            JOptionPane.showMessageDialog(this, "주문이 저장되었습니다. 체크아웃 시 결제하세요.");
            cart.clear();
            updateCartPanel();
            loadMenuCards(); // 결제 후 재고 반영
        }
    }

    // 장바구니 패널 갱신
    private void updateCartPanel() {
        cartListModel.clear();
        for (Menu m : cart.getItems()) {
            cartListModel.addElement(m.getName() + " - " + m.getPrice() + "원");
        }
        totalLabel.setText("총액: " + cart.getTotalPrice() + "원");
    }

}