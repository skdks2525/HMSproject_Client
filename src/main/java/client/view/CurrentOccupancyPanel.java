package client.view;

import client.net.NetworkService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;

public class CurrentOccupancyPanel extends JPanel {
    private final JButton refreshButton;
    private final JTable table;
    private final JLabel infoLabel;
    private final DefaultTableModel model;

    public CurrentOccupancyPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        Color navy = new Color(10, 48, 87);
        Font titleFont = new Font("맑은 고딕", Font.BOLD, 18);
        Font btnFont = new Font("맑은 고딕", Font.BOLD, 14);

        // 상단 제목/뒤로가기 패널 제거

        model = new DefaultTableModel(new String[]{"객실번호", "예약ID", "체크인", "체크아웃", "인원"}, 0);
        table = new JTable(model);
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 14));
        table.getTableHeader().setBackground(navy);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(28);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        add(tableScroll, BorderLayout.CENTER);

        infoLabel = new JLabel();
        refreshButton = new JButton("새로고침");
        refreshButton.setBackground(navy);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(btnFont);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(this::handleRefresh);
        fetchAndFillTable();
    }

    private void handleRefresh(ActionEvent ignored) {
        fetchAndFillTable();
    }

    private void fetchAndFillTable() {
        String req = "GET_CURRENT_OCCUPANCY";
        String resp = NetworkService.getInstance().sendRequest(req);
        model.setRowCount(0);
        if (resp == null || !resp.startsWith("CURRENT_OCCUPANCY:")) {
            infoLabel.setText("서버 오류 또는 데이터 없음");
            return;
        }
        String data = resp.substring("CURRENT_OCCUPANCY:".length());
        if (data.trim().isEmpty()) return;
        for (String entry : data.split(";")) {
            String[] cols = entry.split(",");
            if (cols.length == 5) {
                model.addRow(cols);
            }
        }
    }
}
