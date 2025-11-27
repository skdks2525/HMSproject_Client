package client.model;

import java.util.List;

public class Cart {
    private List<Menu> items;
    public Cart(List<Menu> items) {
        this.items = items;
    }
    public List<Menu> getItems() { return items; }
    public void setItems(List<Menu> items) { this.items = items; }
    public void addItem(Menu menu) { this.items.add(menu); }
    public void clear() { this.items.clear(); }
    public int getTotalPrice() {
        return items.stream().mapToInt(Menu::getPrice).sum();
    }
    public String getFoodNamesString() {
        return String.join("|", items.stream().map(Menu::getName).toArray(String[]::new));
    }
}
