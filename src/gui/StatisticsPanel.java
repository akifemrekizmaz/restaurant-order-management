package gui;

import manager.MenuManager;
import manager.OrderManager;
import model.Order;
import javax.swing.*;
import java.awt.*;
import java.util.*;
//istatistikler ve grafikler
public class StatisticsPanel extends JPanel {
    private OrderManager orderManager;
    private MenuManager menuManager;

    public StatisticsPanel(OrderManager orderManager, MenuManager menuManager) {
        this.orderManager = orderManager;
        this.menuManager = menuManager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        build();
    }

    private void build() {
        removeAll();

        int total   = orderManager.getAll().size();
        int pending = orderManager.getByStatus("BEKLIYOR").size();
        int active  = orderManager.getByStatus("HAZIRLANIYOR").size();
        int served  = orderManager.getByStatus("SERVIS_EDILDI").size();
        double revenue = orderManager.getTotalRevenue();

        JPanel cards = new JPanel(new GridLayout(1, 5, 10, 0));
        cards.setOpaque(false);
        cards.add(statCard("Toplam Siparis", String.valueOf(total), new Color(70, 130, 180)));
        cards.add(statCard("Bekliyor", String.valueOf(pending), new Color(230, 150, 0)));
        cards.add(statCard("Hazirlaniyor", String.valueOf(active), new Color(30, 140, 60)));
        cards.add(statCard("Servis Edildi", String.valueOf(served), new Color(100, 60, 180)));
        cards.add(statCard("Toplam Gelir", String.format("%.0f TL", revenue), new Color(180, 50, 50)));
        add(cards, BorderLayout.NORTH);

        JPanel charts = new JPanel(new GridLayout(1, 2, 12, 0));
        charts.setOpaque(false);
        charts.add(wrapInBorder(buildBarChart(), "En Cok Siparis Edilen Urunler (Top 5)"));
        charts.add(wrapInBorder(buildPieChart(), "Siparis Durum Dagilimi"));
        add(charts, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        JButton btn = new JButton("Istatistikleri Guncelle");
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.addActionListener(e -> { build(); revalidate(); repaint(); });
        bottom.add(btn);
        add(bottom, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel statCard(String title, String value, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
        p.setPreferredSize(new Dimension(0, 80));

        JLabel lTitle = new JLabel(title, SwingConstants.CENTER);
        lTitle.setForeground(new Color(230, 230, 230));
        lTitle.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel lValue = new JLabel(value, SwingConstants.CENTER);
        lValue.setForeground(Color.WHITE);
        lValue.setFont(new Font("Arial", Font.BOLD, 22));

        p.add(lTitle, BorderLayout.NORTH);
        p.add(lValue, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBarChart() {
        return new JPanel() {
            {
                setBackground(Color.WHITE);
                setOpaque(true);
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                HashMap<String, Integer> counts = orderManager.getItemOrderCounts();
                if (counts.isEmpty()) {
                    g2.setColor(Color.GRAY);
                    g2.setFont(new Font("Arial", Font.ITALIC, 14));
                    g2.drawString("Henuz siparis verisi yok", 20, getHeight() / 2);
                    return;
                }

                // En cok siparis edilen 5 urunu bul (basit secme siralamasi)
                ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
                for (int i = 0; i < entries.size() - 1; i++) {
                    for (int j = i + 1; j < entries.size(); j++) {
                        if (entries.get(j).getValue() > entries.get(i).getValue()) {
                            Map.Entry<String, Integer> tmp = entries.get(i);
                            entries.set(i, entries.get(j));
                            entries.set(j, tmp);
                        }
                    }
                }
                int topN = Math.min(5, entries.size());
                int maxVal = entries.get(0).getValue();
                if (maxVal == 0) return;

                int leftMargin = 130;
                int rightMargin = 60;
                int topMargin = 20;
                int barAreaW = getWidth() - leftMargin - rightMargin;
                int rowH = (getHeight() - topMargin) / (topN + 1);

                Color[] barColors = {
                    new Color(70,130,180), new Color(60,160,80),
                    new Color(200,110,50), new Color(150,60,180), new Color(200,180,40)
                };

                g2.setFont(new Font("Arial", Font.PLAIN, 12));

                for (int i = 0; i < topN; i++) {
                    Map.Entry<String, Integer> e = entries.get(i);
                    int barW = (int) ((double) e.getValue() / maxVal * barAreaW);
                    int y = topMargin + i * rowH + 5;
                    int barH = rowH - 10;

                    g2.setColor(barColors[i % barColors.length]);
                    g2.fillRoundRect(leftMargin, y, barW, barH, 6, 6);

                    g2.setColor(Color.DARK_GRAY);
                    String name = e.getKey().length() > 14 ? e.getKey().substring(0, 14) + "." : e.getKey();
                    g2.drawString(name, 4, y + barH / 2 + 5);

                    g2.drawString(e.getValue() + " adet", leftMargin + barW + 4, y + barH / 2 + 5);
                }
            }
        };
    }

    private JPanel buildPieChart() {
        return new JPanel() {
            {
                setBackground(Color.WHITE);
                setOpaque(true);
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                String[] statuses = {"BEKLIYOR", "HAZIRLANIYOR", "SERVIS_EDILDI", "ODENDI"};
                String[] labels   = {"Bekliyor", "Hazirlaniyor", "Servis Edildi", "Odendi"};
                Color[] colors    = {
                    new Color(255, 200, 60), new Color(70, 130, 200),
                    new Color(60, 180, 80), new Color(160, 160, 160)
                };

                int[] counts = new int[statuses.length];
                int total = 0;
                for (int i = 0; i < statuses.length; i++) {
                    counts[i] = orderManager.getByStatus(statuses[i]).size();
                    total += counts[i];
                }

                if (total == 0) {
                    g2.setColor(Color.GRAY);
                    g2.setFont(new Font("Arial", Font.ITALIC, 14));
                    g2.drawString("Henuz siparis yok", 30, getHeight() / 2);
                    return;
                }

                int size = Math.min(getWidth() - 20, getHeight() - 80);
                size = Math.max(size, 100);
                int x = (getWidth() - size) / 2;
                int y = 10;

                int startAngle = 0;
                for (int i = 0; i < statuses.length; i++) {
                    if (counts[i] == 0) continue;
                    int sweep = (int) Math.round((double) counts[i] / total * 360);
                    if (i == statuses.length - 1) {
                        sweep = 360 - startAngle; // Yuvarlamadan kalan kapat
                    }
                    g2.setColor(colors[i]);
                    g2.fillArc(x, y, size, size, startAngle, sweep);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawArc(x, y, size, size, startAngle, sweep);
                    startAngle += sweep;
                }

                int legendY = y + size + 12;
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.setStroke(new BasicStroke(1));
                for (int i = 0; i < statuses.length; i++) {
                    int col = i % 2;
                    int row = i / 2;
                    int lx = 10 + col * 150;
                    int ly = legendY + row * 22;
                    g2.setColor(colors[i]);
                    g2.fillRect(lx, ly, 14, 14);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawString(labels[i] + " (" + counts[i] + ")", lx + 18, ly + 12);
                }
            }
        };
    }

    private JPanel wrapInBorder(JPanel inner, String title) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder(title));
        wrapper.add(inner, BorderLayout.CENTER);
        return wrapper;
    }
}
