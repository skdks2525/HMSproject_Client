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
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        refreshButton = new JButton("새로고침");
        topPanel.add(new JLabel("오늘 투숙 중인 객실"));
        topPanel.add(refreshButton);
        infoLabel = new JLabel();
        topPanel.add(infoLabel);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"객실번호", "예약ID", "체크인", "체크아웃", "인원"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

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
