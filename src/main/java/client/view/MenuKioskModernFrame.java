package client.view;
import client.net.NetworkService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class MenuKioskModernFrame extends JFrame {

    private JPanel menuListPanel;
    private JButton cartButton;

    public MenuKioskModernFrame() {
        setTitle("식음료 주문");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 20));

        add(createHeader(), BorderLayout.NORTH);
        add(createMenuScrollPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        loadMenuCards(); // 메뉴 카드 추가 실행

        setVisible(true);
    }

    // ===============================
    // 상단 제목
    // ===============================
    private JPanel createHeader() {
        JPanel header = new JPanel();
        JLabel title = new JLabel("식음료 주문");
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

        cartButton = new JButton("장바구니 보기 / 결제");
        cartButton.setBackground(Color.BLACK);
        cartButton.setForeground(Color.WHITE);
        cartButton.setPreferredSize(new Dimension(200, 40));

        bottom.add(cartButton);
        return bottom;
    }

    // ===============================
    // 메뉴 카드 생성
    // ===============================
    private void loadMenuCards() {   
    menuListPanel.removeAll(); // 혹시 이전 것 지우기
    // 1. 서버 요청
    String response = NetworkService.getInstance().sendRequest("GET_MENUS");

    if (response == null || !response.startsWith("MENU_LIST:")) {
        JOptionPane.showMessageDialog(this, "메뉴 로딩 실패!");
        return;
    }

    // 2. 필요한 데이터만 분리
    String data = response.substring("MENU_LIST:".length());

    if (data.isEmpty()) {
        JOptionPane.showMessageDialog(this, "메뉴가 없습니다.");
        return;
    }

    // 3. 메뉴 단위로 분리  (예: id,name,price,category/isAvailable)
    String[] menus = data.split("/");

    for (String m : menus) {
        String[] info = m.split(",");

        if (info.length < 5) continue;

        String menuid = info[0];
        String name = info[1];
        int price = Integer.parseInt(info[2]);
        String category = info[3];
        boolean isAvailable = Boolean.parseBoolean(info[4]);

        if (!isAvailable) continue;  // 판매 불가 메뉴는 표시 안 함

        // 4. 카드 UI 추가
        menuListPanel.add(createMenuCard(menuid, name, price, category));
        menuListPanel.add(Box.createVerticalStrut(15));
    }
    menuListPanel.revalidate();
    menuListPanel.repaint();
}

    // ===============================
    // 개별 메뉴 카드 UI 구성
    // ===============================
    private JPanel createMenuCard(String menuid, String name, int price, String category) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 0));
        card.setPreferredSize(new Dimension(820, 120));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // 왼쪽 이미지 박스
        JPanel imgBox = new JPanel();
        imgBox.setPreferredSize(new Dimension(120, 100));
        imgBox.setBackground(new Color(200, 200, 200));
        imgBox.add(new JLabel("IMG"));
        card.add(imgBox, BorderLayout.WEST);

        // 중앙 텍스트
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        JLabel priceLabel = new JLabel(price + "원");
        priceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        priceLabel.setForeground(new Color(252, 136, 3)); // 주황색

        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        categoryLabel.setForeground(Color.GRAY);

        center.add(Box.createVerticalStrut(10));
        center.add(nameLabel);
        center.add(priceLabel);
        center.add(categoryLabel);

        card.add(center, BorderLayout.CENTER);

        // 오른쪽 “담기” 버튼
        JButton addBtn = new JButton("담기");
        addBtn.setPreferredSize(new Dimension(80, 40));
        addBtn.setBackground(new Color(30, 144, 255));
        addBtn.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 40));
        right.setOpaque(false);
        right.add(addBtn);

        card.add(right, BorderLayout.EAST);

        return card;
    }
}
