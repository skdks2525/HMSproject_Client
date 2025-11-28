package client.view;
import client.net.NetworkService;

import javax.swing.*;
import java.awt.*;
// import java.util.List; // 불필요한 import 제거
import java.util.ArrayList;
import client.model.Menu;
import client.model.Cart;


/**
 * 고객용 룸서비스 키오스크 프레임
 * - 서버에서 판매중인 메뉴만 불러와 카드 형태로 표시
 * - 장바구니에 메뉴 추가, 결제, 결제 후 재고 반영 등 전체 주문 프로세스 담당
 * - 모든 서버 통신은 NetworkService를 통해 이루어짐
 */
public class MenuKioskModernFrame extends JFrame {

    // 메뉴 카드가 표시될 패널
    private JPanel menuListPanel;
    // 장바구니 버튼
    private JButton cartButton;
    // 장바구니 객체(메뉴 담기/비우기/총액 계산)
    private final Cart cart = new Cart(new ArrayList<>());

    /**
     * 프레임 생성자: UI 초기화 및 메뉴 카드 로드
     * - 서버에서 메뉴 목록을 받아와 화면에 표시
     * - 장바구니/결제 기능 연결
     */
    public MenuKioskModernFrame() {
        setTitle("룸서비스 주문");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 20));

        add(createHeader(), BorderLayout.NORTH);
        add(createMenuScrollPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        loadMenuCards(); // 메뉴 카드 동적 생성 및 표시

        setVisible(true);
    }

    // ===============================
    // 상단 제목
    // ===============================
    private JPanel createHeader() {
        JPanel header = new JPanel();
        JLabel title = new JLabel("룸서비스 주문");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        header.add(title);
        return header;
    }

    // ===============================
    // 스크롤 가능한 메뉴 패널
    // ===============================
    private JScrollPane createMenuScrollPanel() {
        menuListPanel = new JPanel();
        menuListPanel.setLayout(new BoxLayout(menuListPanel, BoxLayout.Y_AXIS));
        menuListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(menuListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return scrollPane;
    }

    // ===============================
    // 하단 버튼 영역
    // ===============================
    private JPanel createFooter() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        cartButton = new JButton("장바구니");
        cartButton.setBackground(Color.BLACK);
        cartButton.setForeground(Color.WHITE);
        cartButton.setPreferredSize(new Dimension(200, 40));
        cartButton.addActionListener(e -> showCartDialog());
        bottom.add(cartButton);
        return bottom;
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
            // 판매중이 아니거나 재고가 0 이하인 메뉴는 표시하지 않음
            if (!isAvailable || stock <= 0) continue;
            Menu menu = new Menu(menuid, name, price, category, isAvailable, stock);
            menuListPanel.add(createMenuCard(menu));
            menuListPanel.add(Box.createVerticalStrut(15));
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
        card.setLayout(new BorderLayout(15, 0));
        card.setPreferredSize(new Dimension(820, 120));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // 메뉴 이미지(샘플)
        JPanel imgBox = new JPanel();
        imgBox.setPreferredSize(new Dimension(120, 100));
        imgBox.setBackground(new Color(200, 200, 200));
        imgBox.add(new JLabel("IMG"));
        card.add(imgBox, BorderLayout.WEST);

        // 메뉴명, 가격, 재고 표시
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(menu.getName());
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        JLabel priceLabel = new JLabel(menu.getPrice() + "원");
        priceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        priceLabel.setForeground(new Color(252, 136, 3));

        JLabel stockLabel = new JLabel("재고: " + menu.getStock());
        stockLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        center.add(nameLabel);
        center.add(priceLabel);
        center.add(stockLabel);
        card.add(center, BorderLayout.CENTER);

        // 장바구니 담기 버튼 (재고 0/판매중지면 비활성화)
        JButton addButton = new JButton("담기");
        addButton.setEnabled(menu.getStock() > 0 && menu.getIsAvailable());
        addButton.addActionListener(e -> {
            cart.addItem(menu);
            JOptionPane.showMessageDialog(this, menu.getName() + "를 장바구니에 담았습니다.");
        });
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.add(addButton);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    // 장바구니/결제 화면
    /**
     * 장바구니/결제 다이얼로그 표시 및 결제 처리
     * - 장바구니 내역, 총액 표시
     * - 결제 버튼 클릭 시 카드번호 입력받고 서버에 ORDER_MENU 프로토콜로 결제 요청
     * - 결제 성공 시 장바구니 비우고, 메뉴 재고 갱신
     */
    private void showCartDialog() {
        JDialog dialog = new JDialog(this, "장바구니", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Menu m : cart.getItems()) {
            listModel.addElement(m.getName() + " - " + m.getPrice() + "원");
        }
        JList<String> list = new JList<>(listModel);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        JLabel totalLabel = new JLabel("총액: " + cart.getTotalPrice() + "원");
        panel.add(totalLabel, BorderLayout.NORTH);

        JButton payButton = new JButton("결제");
        payButton.addActionListener(e -> {
            // 카드번호 입력 없이 바로 결제 처리
            String req = String.format("ORDER_MENU:%s:%d:%s:%s", System.getProperty("user.name"), cart.getTotalPrice(), "Paid", cart.getFoodNamesString());
            NetworkService.getInstance().sendRequest(req);
            JOptionPane.showMessageDialog(dialog, "요금이 추가되었습니다.");
            cart.clear();
            dialog.dispose();
            loadMenuCards(); // 결제 후 재고 반영
        });
        panel.add(payButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

}
